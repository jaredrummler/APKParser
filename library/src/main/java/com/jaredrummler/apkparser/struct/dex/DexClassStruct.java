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

public class DexClassStruct {

  public static final int ACC_PUBLIC = 0x1;
  public static final int ACC_PRIVATE = 0x2;
  public static final int ACC_PROTECTED = 0x4;
  public static final int ACC_STATIC = 0x8;
  public static final int ACC_FINAL = 0x10;
  public static final int ACC_SYNCHRONIZED = 0x20;
  public static final int ACC_VOLATILE = 0x40;
  public static final int ACC_BRIDGE = 0x40;
  public static final int ACC_TRANSIENT = 0x80;
  public static final int ACC_VARARGS = 0x80;
  public static final int ACC_NATIVE = 0x100;
  public static final int ACC_INTERFACE = 0x200;
  public static final int ACC_ABSTRACT = 0x400;
  public static final int ACC_STRICT = 0x800;
  public static final int ACC_SYNTHETIC = 0x1000;
  public static final int ACC_ANNOTATION = 0x2000;
  public static final int ACC_ENUM = 0x4000;
  public static final int ACC_CONSTRUCTOR = 0x10000;
  public static final int ACC_DECLARED_SYNCHRONIZED = 0x20000;

  public static Builder newDexClassStruct() {
    return new Builder();
  }

  public final int classIdx;
  public final int accessFlags;
  public final int superclassIdx;
  public final long interfacesOff;
  public final int sourceFileIdx;
  public final long annotationsOff;
  public final long classDataOff;
  public final long staticValuesOff;

  private DexClassStruct(Builder builder) {
    this.classIdx = builder.classIdx;
    this.accessFlags = builder.accessFlags;
    this.superclassIdx = builder.superclassIdx;
    this.interfacesOff = builder.interfacesOff;
    this.sourceFileIdx = builder.sourceFileIdx;
    this.annotationsOff = builder.annotationsOff;
    this.classDataOff = builder.classDataOff;
    this.staticValuesOff = builder.staticValuesOff;
  }

  public static final class Builder {

    private int classIdx;
    private int accessFlags;
    private int superclassIdx;
    private long interfacesOff;
    private int sourceFileIdx;
    private long annotationsOff;
    private long classDataOff;
    private long staticValuesOff;

    private Builder() {
    }

    public DexClassStruct build() {
      return new DexClassStruct(this);
    }

    public Builder classIdx(int classIdx) {
      this.classIdx = classIdx;
      return this;
    }

    public Builder accessFlags(int accessFlags) {
      this.accessFlags = accessFlags;
      return this;
    }

    public Builder superclassIdx(int superclassIdx) {
      this.superclassIdx = superclassIdx;
      return this;
    }

    public Builder interfacesOff(long interfacesOff) {
      this.interfacesOff = interfacesOff;
      return this;
    }

    public Builder sourceFileIdx(int sourceFileIdx) {
      this.sourceFileIdx = sourceFileIdx;
      return this;
    }

    public Builder annotationsOff(long annotationsOff) {
      this.annotationsOff = annotationsOff;
      return this;
    }

    public Builder classDataOff(long classDataOff) {
      this.classDataOff = classDataOff;
      return this;
    }

    public Builder staticValuesOff(long staticValuesOff) {
      this.staticValuesOff = staticValuesOff;
      return this;
    }
  }
}
