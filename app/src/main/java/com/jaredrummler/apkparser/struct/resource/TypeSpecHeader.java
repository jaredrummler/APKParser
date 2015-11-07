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

public class TypeSpecHeader extends ChunkHeader {

  // The type identifier this chunk is holding.  Type IDs start at 1 (corresponding to the value
  // of the type bits in a resource identifier).  0 is invalid.
  // The id also specifies the name of the Resource type. It is the string at index id - 1 in the
  // typeStrings StringPool chunk in the containing Package chunk.
  // uint8_t
  private short id;

  // Must be 0. uint8_t
  private short res0;

  // Must be 0.uint16_t
  private int res1;

  // Number of uint32_t entry configuration masks that follow.
  private long entryCount;

  public TypeSpecHeader(int chunkType, int headerSize, long chunkSize) {
    super(chunkType, headerSize, chunkSize);
  }

  public short getId() {
    return id;
  }

  public void setId(short id) {
    this.id = id;
  }

  public short getRes0() {
    return res0;
  }

  public void setRes0(short res0) {
    this.res0 = res0;
  }

  public int getRes1() {
    return res1;
  }

  public void setRes1(int res1) {
    this.res1 = res1;
  }

  public long getEntryCount() {
    return entryCount;
  }

  public void setEntryCount(long entryCount) {
    this.entryCount = entryCount;
  }
}
