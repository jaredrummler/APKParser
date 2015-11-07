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

package com.jaredrummler.apkparser.struct.resource;

import com.jaredrummler.apkparser.struct.StringPool;
import com.jaredrummler.apkparser.utils.Buffers;
import com.jaredrummler.apkparser.utils.ParseUtils;

import java.nio.ByteBuffer;
import java.util.Locale;

public class Type {

  private String name;
  private short id;

  private Locale locale;

  private StringPool keyStringPool;
  private ByteBuffer buffer;
  private long[] offsets;
  private StringPool stringPool;

  public Type(TypeHeader header) {
    this.id = header.getId();
    this.locale = new Locale(header.getConfig().getLanguage(), header.getConfig().getCountry());
  }

  public ResourceEntry getResourceEntry(int id) {
    if (id >= offsets.length) {
      return null;
    }

    if (offsets[id] == TypeHeader.NO_ENTRY) {
      return null;
    }

    // read Resource Entries
    buffer.position((int) offsets[id]);
    return readResourceEntry();
  }

  private ResourceEntry readResourceEntry() {
    long beginPos = buffer.position();
    ResourceEntry resourceEntry = new ResourceEntry();
    // size is always 8(simple), or 16(complex)
    resourceEntry.setSize(Buffers.readUShort(buffer));
    resourceEntry.setFlags(Buffers.readUShort(buffer));
    long keyRef = buffer.getInt();
    resourceEntry.setKey(keyStringPool.get((int) keyRef));

    if ((resourceEntry.getFlags() & ResourceEntry.FLAG_COMPLEX) != 0) {
      ResourceMapEntry resourceMapEntry = new ResourceMapEntry(resourceEntry);

      // Resource identifier of the parent mapping, or 0 if there is none.
      resourceMapEntry.setParent(Buffers.readUInt(buffer));
      resourceMapEntry.setCount(Buffers.readUInt(buffer));

      buffer.position((int) (beginPos + resourceEntry.getSize()));

      //An individual complex Resource entry comprises an entry immediately followed by one or more fields.
      ResourceTableMap[] resourceTableMaps =
          new ResourceTableMap[(int) resourceMapEntry.getCount()];
      for (int i = 0; i < resourceMapEntry.getCount(); i++) {
        resourceTableMaps[i] = readResourceTableMap();
      }

      resourceMapEntry.setResourceTableMaps(resourceTableMaps);
      return resourceMapEntry;
    } else {
      buffer.position((int) (beginPos + resourceEntry.getSize()));
      resourceEntry.setValue(ParseUtils.readResValue(buffer, stringPool));
      return resourceEntry;
    }
  }

  private ResourceTableMap readResourceTableMap() {
    ResourceTableMap resourceTableMap = new ResourceTableMap();
    resourceTableMap.setNameRef(Buffers.readUInt(buffer));
    resourceTableMap.setResValue(ParseUtils.readResValue(buffer, stringPool));

    if ((resourceTableMap.getNameRef() & 0x02000000) != 0) {
      //read arrays
    } else if ((resourceTableMap.getNameRef() & 0x01000000) != 0) {
      // read attrs
    } else {
    }

    return resourceTableMap;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public short getId() {
    return id;
  }

  public void setId(short id) {
    this.id = id;
  }

  public Locale getLocale() {
    return locale;
  }

  public void setLocale(Locale locale) {
    this.locale = locale;
  }

  public StringPool getKeyStringPool() {
    return keyStringPool;
  }

  public void setKeyStringPool(StringPool keyStringPool) {
    this.keyStringPool = keyStringPool;
  }

  public ByteBuffer getBuffer() {
    return buffer;
  }

  public void setBuffer(ByteBuffer buffer) {
    this.buffer = buffer;
  }

  public long[] getOffsets() {
    return offsets;
  }

  public void setOffsets(long[] offsets) {
    this.offsets = offsets;
  }

  public StringPool getStringPool() {
    return stringPool;
  }

  public void setStringPool(StringPool stringPool) {
    this.stringPool = stringPool;
  }

}
