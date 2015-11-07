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

import com.jaredrummler.apkparser.struct.ChunkHeader;

public class PackageHeader extends ChunkHeader {

  // If this is a base package, its ID.  ResourcePackage IDs start at 1 (corresponding to the value of
  // the package bits in a resource identifier).  0 means this is not a base package.
  // uint32_t
  private long id;

  // Actual name of this package, -terminated.
  // char16_t name[128]
  private String name;

  // Offset to a ResStringPool_header defining the resource type symbol table.
  // If zero, this package is inheriting from another base package (overriding specific values in it).
  // uinit 32
  private long typeStrings;

  // Last index into typeStrings that is for public use by others.
  // uint32_t
  private long lastPublicType;

  // Offset to a ResStringPool_header defining the resource
  // key symbol table.  If zero, this package is inheriting from
  // another base package (overriding specific values in it).
  // uint32_t
  private long keyStrings;

  // Last index into keyStrings that is for public use by others.
  // uint32_t
  private long lastPublicKey;

  public PackageHeader(int chunkType, int headerSize, long chunkSize) {
    super(chunkType, headerSize, chunkSize);
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public long getTypeStrings() {
    return typeStrings;
  }

  public void setTypeStrings(long typeStrings) {
    this.typeStrings = typeStrings;
  }

  public long getLastPublicType() {
    return lastPublicType;
  }

  public void setLastPublicType(long lastPublicType) {
    this.lastPublicType = lastPublicType;
  }

  public long getKeyStrings() {
    return keyStrings;
  }

  public void setKeyStrings(long keyStrings) {
    this.keyStrings = keyStrings;
  }

  public long getLastPublicKey() {
    return lastPublicKey;
  }

  public void setLastPublicKey(long lastPublicKey) {
    this.lastPublicKey = lastPublicKey;
  }
}
