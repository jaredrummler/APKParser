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

package com.jaredrummler.apkparser.struct.dex;

public class DexHeader {

  public static final int K_SHA_1_DIGEST_LEN = 20;

  public static Builder newDexHeader() {
    return new Builder();
  }

  public final int version;
  public final byte signature[];
  public final long fileSize;
  public final long headerSize;
  public final long linkSize;
  public final long linkOff;
  public final long mapOff;
  public final int stringIdsSize;
  public final long stringIdsOff;
  public final int typeIdsSize;
  public final long typeIdsOff;
  public final int protoIdsSize;
  public final long protoIdsOff;
  public final int fieldIdsSize;
  public final long fieldIdsOff;
  public final int methodIdsSize;
  public final long methodIdsOff;
  public final int classDefsSize;
  public final long classDefsOff;
  public final int dataSize;
  public final long dataOff;

  private DexHeader(Builder builder) {
    this.version = builder.version;
    this.signature = builder.signature;
    this.fileSize = builder.fileSize;
    this.headerSize = builder.headerSize;
    this.linkSize = builder.linkSize;
    this.linkOff = builder.linkOff;
    this.mapOff = builder.mapOff;
    this.stringIdsSize = builder.stringIdsSize;
    this.stringIdsOff = builder.stringIdsOff;
    this.typeIdsSize = builder.typeIdsSize;
    this.typeIdsOff = builder.typeIdsOff;
    this.protoIdsSize = builder.protoIdsSize;
    this.protoIdsOff = builder.protoIdsOff;
    this.fieldIdsSize = builder.fieldIdsSize;
    this.fieldIdsOff = builder.fieldIdsOff;
    this.methodIdsSize = builder.methodIdsSize;
    this.methodIdsOff = builder.methodIdsOff;
    this.classDefsSize = builder.classDefsSize;
    this.classDefsOff = builder.classDefsOff;
    this.dataSize = builder.dataSize;
    this.dataOff = builder.dataOff;
  }

  public static final class Builder {

    private int version;
    private byte[] signature;
    private long fileSize;
    private long headerSize;
    private long linkSize;
    private long linkOff;
    private long mapOff;
    private int stringIdsSize;
    private long stringIdsOff;
    private int typeIdsSize;
    private long typeIdsOff;
    private int protoIdsSize;
    private long protoIdsOff;
    private int fieldIdsSize;
    private long fieldIdsOff;
    private int methodIdsSize;
    private long methodIdsOff;
    private int classDefsSize;
    private long classDefsOff;
    private int dataSize;
    private long dataOff;

    private Builder() {
    }

    public DexHeader build() {
      return new DexHeader(this);
    }

    public Builder version(int version) {
      this.version = version;
      return this;
    }

    public Builder signature(byte[] signature) {
      this.signature = signature;
      return this;
    }

    public Builder fileSize(long fileSize) {
      this.fileSize = fileSize;
      return this;
    }

    public Builder headerSize(long headerSize) {
      this.headerSize = headerSize;
      return this;
    }

    public Builder linkSize(long linkSize) {
      this.linkSize = linkSize;
      return this;
    }

    public Builder linkOff(long linkOff) {
      this.linkOff = linkOff;
      return this;
    }

    public Builder mapOff(long mapOff) {
      this.mapOff = mapOff;
      return this;
    }

    public Builder stringIdsSize(int stringIdsSize) {
      this.stringIdsSize = stringIdsSize;
      return this;
    }

    public Builder stringIdsOff(long stringIdsOff) {
      this.stringIdsOff = stringIdsOff;
      return this;
    }

    public Builder typeIdsSize(int typeIdsSize) {
      this.typeIdsSize = typeIdsSize;
      return this;
    }

    public Builder typeIdsOff(long typeIdsOff) {
      this.typeIdsOff = typeIdsOff;
      return this;
    }

    public Builder protoIdsSize(int protoIdsSize) {
      this.protoIdsSize = protoIdsSize;
      return this;
    }

    public Builder protoIdsOff(long protoIdsOff) {
      this.protoIdsOff = protoIdsOff;
      return this;
    }

    public Builder fieldIdsSize(int fieldIdsSize) {
      this.fieldIdsSize = fieldIdsSize;
      return this;
    }

    public Builder fieldIdsOff(long fieldIdsOff) {
      this.fieldIdsOff = fieldIdsOff;
      return this;
    }

    public Builder methodIdsSize(int methodIdsSize) {
      this.methodIdsSize = methodIdsSize;
      return this;
    }

    public Builder methodIdsOff(long methodIdsOff) {
      this.methodIdsOff = methodIdsOff;
      return this;
    }

    public Builder classDefsSize(int classDefsSize) {
      this.classDefsSize = classDefsSize;
      return this;
    }

    public Builder classDefsOff(long classDefsOff) {
      this.classDefsOff = classDefsOff;
      return this;
    }

    public Builder dataSize(int dataSize) {
      this.dataSize = dataSize;
      return this;
    }

    public Builder dataOff(long dataOff) {
      this.dataOff = dataOff;
      return this;
    }
  }
}
