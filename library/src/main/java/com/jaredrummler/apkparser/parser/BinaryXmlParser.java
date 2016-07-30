/*
 * Copyright (c) 2015, Jared Rummler
 * Copyright (c) 2015, Liu Dong
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jaredrummler.apkparser.parser;

import com.jaredrummler.apkparser.exception.ParserException;
import com.jaredrummler.apkparser.struct.ChunkHeader;
import com.jaredrummler.apkparser.struct.ChunkType;
import com.jaredrummler.apkparser.struct.ResourceEntity;
import com.jaredrummler.apkparser.struct.StringPool;
import com.jaredrummler.apkparser.struct.StringPoolHeader;
import com.jaredrummler.apkparser.struct.resource.ResourceTable;
import com.jaredrummler.apkparser.struct.xml.Attribute;
import com.jaredrummler.apkparser.struct.xml.Attributes;
import com.jaredrummler.apkparser.struct.xml.XmlCData;
import com.jaredrummler.apkparser.struct.xml.XmlHeader;
import com.jaredrummler.apkparser.struct.xml.XmlNamespaceEndTag;
import com.jaredrummler.apkparser.struct.xml.XmlNamespaceStartTag;
import com.jaredrummler.apkparser.struct.xml.XmlNodeEndTag;
import com.jaredrummler.apkparser.struct.xml.XmlNodeHeader;
import com.jaredrummler.apkparser.struct.xml.XmlNodeStartTag;
import com.jaredrummler.apkparser.struct.xml.XmlResourceMapHeader;
import com.jaredrummler.apkparser.utils.AttributeValues;
import com.jaredrummler.apkparser.utils.Buffers;
import com.jaredrummler.apkparser.utils.Locales;
import com.jaredrummler.apkparser.utils.ParseUtils;
import com.jaredrummler.apkparser.utils.Utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class BinaryXmlParser {

    private static final Set<String> INT_ATTRIBUTES = new HashSet<>(
            Arrays.asList("screenOrientation", "configChanges", "windowSoftInputMode", "launchMode",
                    "installLocation", "protectionLevel"));

    private StringPool stringPool;
    private String[] resourceMap;
    private ByteBuffer buffer;
    private XmlStreamer xmlStreamer;
    private final ResourceTable resourceTable;
    private Locale locale = Locales.ANY_LOCALE;

    public BinaryXmlParser(ByteBuffer buffer, ResourceTable resourceTable) {
        this.buffer = buffer.duplicate();
        this.buffer.order(ByteOrder.LITTLE_ENDIAN);
        this.resourceTable = resourceTable;
    }

    public void parse() throws ParserException {
        ChunkHeader chunkHeader = readChunkHeader();
        if (chunkHeader == null) {
            return;
        }
        if (chunkHeader.getChunkType() != ChunkType.XML) {
            return; // may be a plain xml file.
        }

        // read string pool chunk
        chunkHeader = readChunkHeader();
        if (chunkHeader == null) {
            return;
        }
        ParseUtils.checkChunkType(ChunkType.STRING_POOL, chunkHeader.getChunkType());
        stringPool = ParseUtils.readStringPool(buffer, (StringPoolHeader) chunkHeader);

        // read on chunk, check if it was an optional XMLResourceMap chunk
        chunkHeader = readChunkHeader();
        if (chunkHeader == null) {
            return;
        }
        if (chunkHeader.getChunkType() == ChunkType.XML_RESOURCE_MAP) {
            long[] resourceIds = readXmlResourceMap((XmlResourceMapHeader) chunkHeader);
            resourceMap = new String[resourceIds.length];
            for (int i = 0; i < resourceIds.length; i++) {
                resourceMap[i] = Attribute.AttrIds.getString(resourceIds[i]);
            }
            chunkHeader = readChunkHeader();
        }

        while (chunkHeader != null) {
            long beginPos = buffer.position();
            switch (chunkHeader.getChunkType()) {
                case ChunkType.XML_END_NAMESPACE:
                    XmlNamespaceEndTag xmlNamespaceEndTag = readXmlNamespaceEndTag();
                    xmlStreamer.onNamespaceEnd(xmlNamespaceEndTag);
                    break;
                case ChunkType.XML_START_NAMESPACE:
                    XmlNamespaceStartTag namespaceStartTag = readXmlNamespaceStartTag();
                    xmlStreamer.onNamespaceStart(namespaceStartTag);
                    break;
                case ChunkType.XML_START_ELEMENT:
                    readXmlNodeStartTag();
                    break;
                case ChunkType.XML_END_ELEMENT:
                    readXmlNodeEndTag();
                    break;
                case ChunkType.XML_CDATA:
                    readXmlCData();
                    break;
                default:
                    if (chunkHeader.getChunkType() >= ChunkType.XML_FIRST_CHUNK &&
                            chunkHeader.getChunkType() <= ChunkType.XML_LAST_CHUNK) {
                        Buffers.skip(buffer, chunkHeader.getBodySize());
                    } else {
                        throw new ParserException("Unexpected chunk type:" + chunkHeader.getChunkType());
                    }
            }
            buffer.position((int) (beginPos + chunkHeader.getBodySize()));
            chunkHeader = readChunkHeader();
        }
    }

    private XmlCData readXmlCData() {
        XmlCData xmlCData = new XmlCData();
        int dataRef = buffer.getInt();
        if (dataRef > 0) {
            xmlCData.setData(stringPool.get(dataRef));
        }
        xmlCData.setTypedData(ParseUtils.readResValue(buffer, stringPool));
        if (xmlStreamer != null) {
            // TODO: to know more about cdata. some cdata appears buffer xml tags
            //String value = xmlCData.toStringValue(resourceTable, locale);
            //xmlCData.setValue(value);
            //xmlStreamer.onCData(xmlCData);
        }
        return xmlCData;
    }

    private XmlNodeEndTag readXmlNodeEndTag() {
        XmlNodeEndTag xmlNodeEndTag = new XmlNodeEndTag();
        int nsRef = buffer.getInt();
        int nameRef = buffer.getInt();
        if (nsRef > 0) {
            xmlNodeEndTag.setNamespace(stringPool.get(nsRef));
        }
        xmlNodeEndTag.setName(stringPool.get(nameRef));
        if (xmlStreamer != null) {
            xmlStreamer.onEndTag(xmlNodeEndTag);
        }
        return xmlNodeEndTag;
    }

    private XmlNodeStartTag readXmlNodeStartTag() {
        int nsRef = buffer.getInt();
        int nameRef = buffer.getInt();
        XmlNodeStartTag xmlNodeStartTag = new XmlNodeStartTag();
        if (nsRef > 0) {
            xmlNodeStartTag.setNamespace(stringPool.get(nsRef));
        }
        xmlNodeStartTag.setName(stringPool.get(nameRef));

        // read attributes.
        // attributeStart and attributeSize are always 20 (0x14)
        Buffers.readUShort(buffer);
        Buffers.readUShort(buffer);
        int attributeCount = Buffers.readUShort(buffer);
        Buffers.readUShort(buffer);
        Buffers.readUShort(buffer);
        Buffers.readUShort(buffer);

        // read attributes
        Attributes attributes = new Attributes(attributeCount);
        for (int count = 0; count < attributeCount; count++) {
            Attribute attribute = readAttribute();
            if (xmlStreamer != null) {
                String value = attribute.toStringValue(resourceTable, locale);
                if (INT_ATTRIBUTES.contains(attribute.getName()) && Utils.isNumeric(value)) {
                    try {
                        value = getFinalValueAsString(attribute.getName(), value);
                    } catch (Exception ignore) {
                    }
                }
                attribute.setValue(value);
                attributes.set(count, attribute);
            }
        }
        xmlNodeStartTag.setAttributes(attributes);

        if (xmlStreamer != null) {
            xmlStreamer.onStartTag(xmlNodeStartTag);
        }

        return xmlNodeStartTag;
    }

    //trans int attr value to string
    private String getFinalValueAsString(String attributeName, String str) {
        int value = Integer.parseInt(str);
        switch (attributeName) {
            case "screenOrientation":
                return AttributeValues.getScreenOrientation(value);
            case "configChanges":
                return AttributeValues.getConfigChanges(value);
            case "windowSoftInputMode":
                return AttributeValues.getWindowSoftInputMode(value);
            case "launchMode":
                return AttributeValues.getLaunchMode(value);
            case "installLocation":
                return AttributeValues.getInstallLocation(value);
            case "protectionLevel":
                return AttributeValues.getProtectionLevel(value);
            default:
                return str;
        }
    }

    private Attribute readAttribute() {
        int nsRef = buffer.getInt();
        int nameRef = buffer.getInt();
        Attribute attribute = new Attribute();
        if (nsRef > 0) {
            attribute.setNamespace(stringPool.get(nsRef));
        }

        attribute.setName(stringPool.get(nameRef));
        if (attribute.getName().isEmpty() && resourceMap != null && nameRef < resourceMap.length) {
            // some processed apk file make the string pool value empty, if it is a xmlmap attr.
            attribute.setName(resourceMap[nameRef]);
            //TODO: how to get the namespace of attribute
        }

        int rawValueRef = buffer.getInt();
        if (rawValueRef > 0) {
            attribute.setRawValue(stringPool.get(rawValueRef));
        }
        ResourceEntity resValue = ParseUtils.readResValue(buffer, stringPool);
        attribute.setTypedValue(resValue);

        return attribute;
    }

    private XmlNamespaceStartTag readXmlNamespaceStartTag() {
        int prefixRef = buffer.getInt();
        int uriRef = buffer.getInt();
        XmlNamespaceStartTag nameSpace = new XmlNamespaceStartTag();
        if (prefixRef > 0) {
            nameSpace.setPrefix(stringPool.get(prefixRef));
        }
        if (uriRef > 0) {
            nameSpace.setUri(stringPool.get(uriRef));
        }
        return nameSpace;
    }

    private XmlNamespaceEndTag readXmlNamespaceEndTag() {
        int prefixRef = buffer.getInt();
        int uriRef = buffer.getInt();
        XmlNamespaceEndTag nameSpace = new XmlNamespaceEndTag();
        if (prefixRef > 0) {
            nameSpace.setPrefix(stringPool.get(prefixRef));
        }
        if (uriRef > 0) {
            nameSpace.setUri(stringPool.get(uriRef));
        }
        return nameSpace;
    }

    private long[] readXmlResourceMap(XmlResourceMapHeader chunkHeader) {
        int count = chunkHeader.getBodySize() / 4;
        long[] resourceIds = new long[count];
        for (int i = 0; i < count; i++) {
            resourceIds[i] = Buffers.readUInt(buffer);
        }
        return resourceIds;
    }

    private ChunkHeader readChunkHeader() throws ParserException {
        // finished
        if (!buffer.hasRemaining()) {
            return null;
        }

        long begin = buffer.position();
        int chunkType = Buffers.readUShort(buffer);
        int headerSize = Buffers.readUShort(buffer);
        long chunkSize = Buffers.readUInt(buffer);

        switch (chunkType) {
            case ChunkType.XML:
                return new XmlHeader(chunkType, headerSize, chunkSize);
            case ChunkType.STRING_POOL:
                StringPoolHeader stringPoolHeader = new StringPoolHeader(chunkType, headerSize, chunkSize);
                stringPoolHeader.setStringCount(Buffers.readUInt(buffer));
                stringPoolHeader.setStyleCount(Buffers.readUInt(buffer));
                stringPoolHeader.setFlags(Buffers.readUInt(buffer));
                stringPoolHeader.setStringsStart(Buffers.readUInt(buffer));
                stringPoolHeader.setStylesStart(Buffers.readUInt(buffer));
                buffer.position((int) (begin + headerSize));
                return stringPoolHeader;
            case ChunkType.XML_RESOURCE_MAP:
                buffer.position((int) (begin + headerSize));
                return new XmlResourceMapHeader(chunkType, headerSize, chunkSize);
            case ChunkType.XML_START_NAMESPACE:
            case ChunkType.XML_END_NAMESPACE:
            case ChunkType.XML_START_ELEMENT:
            case ChunkType.XML_END_ELEMENT:
            case ChunkType.XML_CDATA:
                XmlNodeHeader header = new XmlNodeHeader(chunkType, headerSize, chunkSize);
                header.setLineNum((int) Buffers.readUInt(buffer));
                header.setCommentRef((int) Buffers.readUInt(buffer));
                buffer.position((int) (begin + headerSize));
                return header;
            case ChunkType.NULL:
                //buffer.advanceTo(begin + headerSize);
                //buffer.skip((int) (chunkSize - headerSize));
            default:
                throw new ParserException("Unexpected chunk type:" + chunkType);
        }
    }

    public void setLocale(Locale locale) {
        if (locale != null) {
            this.locale = locale;
        }
    }

    public Locale getLocale() {
        return locale;
    }

    public XmlStreamer getXmlStreamer() {
        return xmlStreamer;
    }

    public void setXmlStreamer(XmlStreamer xmlStreamer) {
        this.xmlStreamer = xmlStreamer;
    }
}
