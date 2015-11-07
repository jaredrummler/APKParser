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
import com.jaredrummler.apkparser.struct.StringPool;
import com.jaredrummler.apkparser.struct.StringPoolHeader;
import com.jaredrummler.apkparser.struct.resource.*;
import com.jaredrummler.apkparser.utils.Buffers;
import com.jaredrummler.apkparser.utils.Pair;
import com.jaredrummler.apkparser.utils.ParseUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class ResourceTableParser {

  private StringPool stringPool;
  private ByteBuffer buffer;
  private ResourceTable resourceTable;
  private Set<Locale> locales;

  public ResourceTableParser(ByteBuffer buffer) {
    this.buffer = buffer.duplicate();
    this.buffer.order(ByteOrder.LITTLE_ENDIAN);
    this.locales = new HashSet<>();
  }

  public void parse() {
    ResourceTableHeader resourceTableHeader = (ResourceTableHeader) readChunkHeader();
    stringPool = ParseUtils.readStringPool(buffer, (StringPoolHeader) readChunkHeader());
    resourceTable = new ResourceTable();
    resourceTable.setStringPool(stringPool);
    PackageHeader packageHeader = (PackageHeader) readChunkHeader();
    for (int i = 0; i < resourceTableHeader.getPackageCount(); i++) {
      Pair<ResourcePackage, PackageHeader> pair = readPackage(packageHeader);
      resourceTable.addPackage(pair.getLeft());
      packageHeader = pair.getRight();
    }
  }

  private Pair<ResourcePackage, PackageHeader> readPackage(PackageHeader packageHeader) {
    Pair<ResourcePackage, PackageHeader> pair = new Pair<>();
    ResourcePackage resourcePackage = new ResourcePackage(packageHeader);
    pair.setLeft(resourcePackage);

    long beginPos = buffer.position();
    if (packageHeader.getTypeStrings() > 0) {
      buffer.position(
          (int) (beginPos + packageHeader.getTypeStrings() - packageHeader.getHeaderSize()));
      resourcePackage.setTypeStringPool(
          ParseUtils.readStringPool(buffer, (StringPoolHeader) readChunkHeader()));
    }

    if (packageHeader.getKeyStrings() > 0) {
      buffer.position(
          (int) (beginPos + packageHeader.getKeyStrings() - packageHeader.getHeaderSize()));
      resourcePackage.setKeyStringPool(
          ParseUtils.readStringPool(buffer, (StringPoolHeader) readChunkHeader()));
    }

    outer:
    while (buffer.hasRemaining()) {
      ChunkHeader chunkHeader = readChunkHeader();
      switch (chunkHeader.getChunkType()) {
        case ChunkType.TABLE_TYPE_SPEC:
          long typeSpecChunkBegin = buffer.position();
          TypeSpecHeader typeSpecHeader = (TypeSpecHeader) chunkHeader;
          long[] entryFlags = new long[(int) typeSpecHeader.getEntryCount()];
          for (int i = 0; i < typeSpecHeader.getEntryCount(); i++) {
            entryFlags[i] = Buffers.readUInt(buffer);
          }

          TypeSpec typeSpec = new TypeSpec(typeSpecHeader);

          typeSpec.setEntryFlags(entryFlags);
          typeSpec.setName(resourcePackage.getTypeStringPool().get(
              typeSpecHeader.getId() - 1));

          resourcePackage.addTypeSpec(typeSpec);
          buffer.position((int) (typeSpecChunkBegin + typeSpecHeader.getBodySize()));
          break;
        case ChunkType.TABLE_TYPE:
          long typeChunkBegin = buffer.position();
          TypeHeader typeHeader = (TypeHeader) chunkHeader;
          // read offsets table
          long[] offsets = new long[(int) typeHeader.getEntryCount()];
          for (int i = 0; i < typeHeader.getEntryCount(); i++) {
            offsets[i] = Buffers.readUInt(buffer);
          }

          Type type = new Type(typeHeader);
          type.setName(resourcePackage.getTypeStringPool().get(typeHeader.getId() - 1));
          long entryPos =
              typeChunkBegin + typeHeader.getEntriesStart() - typeHeader.getHeaderSize();
          buffer.position((int) entryPos);
          ByteBuffer b = buffer.slice();
          b.order(ByteOrder.LITTLE_ENDIAN);
          type.setBuffer(b);
          type.setKeyStringPool(resourcePackage.getKeyStringPool());
          type.setOffsets(offsets);
          type.setStringPool(stringPool);
          resourcePackage.addType(type);
          locales.add(type.getLocale());
          buffer.position((int) (typeChunkBegin + typeHeader.getBodySize()));
          break;
        case ChunkType.TABLE_PACKAGE:
          pair.setRight((PackageHeader) chunkHeader);
          break outer;
        default:
          throw new ParserException("unexpected chunk type:" + chunkHeader.getChunkType());
      }
    }

    return pair;
  }

  private ChunkHeader readChunkHeader() {
    long begin = buffer.position();

    int chunkType = Buffers.readUShort(buffer);
    int headerSize = Buffers.readUShort(buffer);
    long chunkSize = Buffers.readUInt(buffer);

    switch (chunkType) {
      case ChunkType.TABLE:
        ResourceTableHeader resourceTableHeader =
            new ResourceTableHeader(chunkType, headerSize, chunkSize);
        resourceTableHeader.setPackageCount(Buffers.readUInt(buffer));
        buffer.position((int) (begin + headerSize));
        return resourceTableHeader;
      case ChunkType.STRING_POOL:
        StringPoolHeader stringPoolHeader = new StringPoolHeader(chunkType, headerSize, chunkSize);
        stringPoolHeader.setStringCount(Buffers.readUInt(buffer));
        stringPoolHeader.setStyleCount(Buffers.readUInt(buffer));
        stringPoolHeader.setFlags(Buffers.readUInt(buffer));
        stringPoolHeader.setStringsStart(Buffers.readUInt(buffer));
        stringPoolHeader.setStylesStart(Buffers.readUInt(buffer));
        buffer.position((int) (begin + headerSize));
        return stringPoolHeader;
      case ChunkType.TABLE_PACKAGE:
        PackageHeader packageHeader = new PackageHeader(chunkType, headerSize, chunkSize);
        packageHeader.setId(Buffers.readUInt(buffer));
        packageHeader.setName(ParseUtils.readStringUTF16(buffer, 128));
        packageHeader.setTypeStrings(Buffers.readUInt(buffer));
        packageHeader.setLastPublicType(Buffers.readUInt(buffer));
        packageHeader.setKeyStrings(Buffers.readUInt(buffer));
        packageHeader.setLastPublicKey(Buffers.readUInt(buffer));
        buffer.position((int) (begin + headerSize));
        return packageHeader;
      case ChunkType.TABLE_TYPE_SPEC:
        TypeSpecHeader typeSpecHeader = new TypeSpecHeader(chunkType, headerSize, chunkSize);
        typeSpecHeader.setId(Buffers.readUByte(buffer));
        typeSpecHeader.setRes0(Buffers.readUByte(buffer));
        typeSpecHeader.setRes1(Buffers.readUShort(buffer));
        typeSpecHeader.setEntryCount(Buffers.readUInt(buffer));
        buffer.position((int) (begin + headerSize));
        return typeSpecHeader;
      case ChunkType.TABLE_TYPE:
        TypeHeader typeHeader = new TypeHeader(chunkType, headerSize, chunkSize);
        typeHeader.setId(Buffers.readUByte(buffer));
        typeHeader.setRes0(Buffers.readUByte(buffer));
        typeHeader.setRes1(Buffers.readUShort(buffer));
        typeHeader.setEntryCount(Buffers.readUInt(buffer));
        typeHeader.setEntriesStart(Buffers.readUInt(buffer));
        typeHeader.setConfig(readResTableConfig());
        buffer.position((int) (begin + headerSize));
        return typeHeader;
      case ChunkType.NULL:
        // buffer.skip((int) (chunkSize - headerSize));
      default:
        throw new ParserException("Unexpected chunk Type:" + Integer.toHexString(chunkType));
    }
  }

  private ResTableConfig readResTableConfig() {
    long beginPos = buffer.position();
    ResTableConfig config = new ResTableConfig();
    long size = Buffers.readUInt(buffer);
    Buffers.skip(buffer, 4);
    // read locale
    config.setLanguage(new String(Buffers.readBytes(buffer, 2)).replace("\0", ""));
    config.setCountry(new String(Buffers.readBytes(buffer, 2)).replace("\0", ""));

    long endPos = buffer.position();
    Buffers.skip(buffer, (int) (size - (endPos - beginPos)));
    return config;
  }

  public ResourceTable getResourceTable() {
    return resourceTable;
  }

  public Set<Locale> getLocales() {
    return this.locales;
  }
}
