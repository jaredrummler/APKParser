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

package com.jaredrummler.apkparser.utils;

import java.nio.ByteBuffer;

public class Buffers {

  /**
   * Get one unsigned byte as short type
   */
  public static short readUByte(ByteBuffer buffer) {
    byte b = buffer.get();
    return (short) (b & 0xff);
  }

  /**
   * Get one unsigned short as int type
   */
  public static int readUShort(ByteBuffer buffer) {
    short s = buffer.getShort();
    return s & 0xffff;
  }

  /**
   * Get one unsigned int as long type
   */
  public static long readUInt(ByteBuffer buffer) {
    int i = buffer.getInt();
    return i & 0xffffffffL;
  }

  /**
   * Get bytes
   */
  public static byte[] readBytes(ByteBuffer buffer, int size) {
    byte[] bytes = new byte[size];
    buffer.get(bytes);
    return bytes;
  }

  /**
   * Read utf16 strings, use strLen, not ending 0 char.
   *
   * @param strLen
   */
  public static String readString(ByteBuffer buffer, int strLen) {
    StringBuilder sb = new StringBuilder(strLen);
    for (int i = 0; i < strLen; i++) {
      sb.append(buffer.getChar());
    }
    return sb.toString();
  }

  /**
   * Skip count bytes
   */
  public static void skip(ByteBuffer buffer, int count) {
    buffer.position(buffer.position() + count);
  }
}
