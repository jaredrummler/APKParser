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

package com.jaredrummler.apkparser.struct.xml;

import com.jaredrummler.apkparser.struct.ResourceEntity;
import com.jaredrummler.apkparser.struct.resource.ResourceTable;
import com.jaredrummler.apkparser.utils.ResourceLoader;

import java.util.Locale;
import java.util.Map;

public class Attribute {

  private String namespace;
  private String name;
  // The original raw string value of this
  private String rawValue;
  // Processed typed value of this
  private ResourceEntity typedValue;
  // the final value as string
  private String value;

  public String toStringValue(ResourceTable resourceTable, Locale locale) {
    if (rawValue != null) {
      return rawValue;
    } else if (typedValue != null) {
      return typedValue.toStringValue(resourceTable, locale);
    } else {
      // something happen;
      return "";
    }
  }

  /**
   * These are attribute resource constants for the platform; as found in android.R.attr
   */
  public static class AttrIds {

    private static final Map<Integer, String> IDS = ResourceLoader.loadSystemAttrIds();

    public static String getString(long id) {
      String value = IDS.get((int) id);
      if (value == null) {
        value = "AttrId:0x" + Long.toHexString(id);
      }
      return value;
    }

  }

  public String getNamespace() {
    return namespace;
  }

  public void setNamespace(String namespace) {
    this.namespace = namespace;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getRawValue() {
    return rawValue;
  }

  public void setRawValue(String rawValue) {
    this.rawValue = rawValue;
  }

  public ResourceEntity getTypedValue() {
    return typedValue;
  }

  public void setTypedValue(ResourceEntity typedValue) {
    this.typedValue = typedValue;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

}
