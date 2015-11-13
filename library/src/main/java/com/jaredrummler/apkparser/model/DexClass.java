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

package com.jaredrummler.apkparser.model;

import com.jaredrummler.apkparser.struct.dex.DexClassStruct;

public class DexClass {

  public static Builder newDexClass() {
    return new Builder();
  }

  public final String classType;
  public final String superClass;
  public final int accessFlags;

  private DexClass(Builder builder) {
    this.classType = builder.classType;
    this.superClass = builder.superClass;
    this.accessFlags = builder.accessFlags;
  }

  public String getPackageName() {
    String packageName = classType;
    if (packageName.length() > 0) {
      if (packageName.charAt(0) == 'L') {
        packageName = packageName.substring(1);
      }
    }
    if (packageName.length() > 0) {
      int idx = classType.lastIndexOf('/');
      if (idx > 0) {
        packageName = packageName.substring(0, classType.lastIndexOf('/') - 1);
      } else if (packageName.charAt(packageName.length() - 1) == ';') {
        packageName = packageName.substring(0, packageName.length() - 1);
      }
    }
    return packageName.replace('/', '.');
  }

  public boolean isInterface() {
    return (accessFlags & DexClassStruct.ACC_INTERFACE) != 0;
  }

  public boolean isEnum() {
    return (accessFlags & DexClassStruct.ACC_ENUM) != 0;
  }

  public boolean isAnnotation() {
    return (accessFlags & DexClassStruct.ACC_ANNOTATION) != 0;
  }

  public boolean isPublic() {
    return (accessFlags & DexClassStruct.ACC_PUBLIC) != 0;
  }

  public boolean isProtected() {
    return (accessFlags & DexClassStruct.ACC_PROTECTED) != 0;
  }

  public boolean isStatic() {
    return (accessFlags & DexClassStruct.ACC_STATIC) != 0;
  }

  @Override public String toString() {
    return classType;
  }

  public static final class Builder {

    private String classType;
    private String superClass;
    private int accessFlags;

    private Builder() {
    }

    public DexClass build() {
      return new DexClass(this);
    }

    public Builder classType(String classType) {
      this.classType = classType;
      return this;
    }

    public Builder superClass(String superClass) {
      this.superClass = superClass;
      return this;
    }

    public Builder accessFlags(int accessFlags) {
      this.accessFlags = accessFlags;
      return this;
    }
  }
}
