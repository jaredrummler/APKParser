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

import java.util.Arrays;
import java.util.Locale;

public class ResourceMapEntry extends ResourceEntry {

  // Resource identifier of the parent mapping, or 0 if there is none.
  //ResTable_ref specifies the parent Resource, if any, of this Resource.
  // struct ResTable_ref { uint32_t ident; };
  private long parent;

  // Number of name/value pairs that follow for FLAG_COMPLEX. uint32_t
  private long count;

  private ResourceTableMap[] resourceTableMaps;

  public ResourceMapEntry(ResourceEntry resourceEntry) {
    this.setSize(resourceEntry.getSize());
    this.setFlags(resourceEntry.getFlags());
    this.setKey(resourceEntry.getKey());
  }

  public long getParent() {
    return parent;
  }

  public void setParent(long parent) {
    this.parent = parent;
  }

  public long getCount() {
    return count;
  }

  public void setCount(long count) {
    this.count = count;
  }

  public ResourceTableMap[] getResourceTableMaps() {
    return resourceTableMaps;
  }

  public void setResourceTableMaps(ResourceTableMap[] resourceTableMaps) {
    this.resourceTableMaps = resourceTableMaps;
  }

  public String toStringValue(ResourceTable resourceTable, Locale locale) {
    if (resourceTableMaps.length > 0) {
      return resourceTableMaps[0].toString();
    }
    return null;
  }

  @Override public String toString() {
    return "ResourceMapEntry{" +
        "parent=" + parent +
        ", count=" + count +
        ", resourceTableMaps=" + Arrays.toString(resourceTableMaps) +
        '}';
  }

}
