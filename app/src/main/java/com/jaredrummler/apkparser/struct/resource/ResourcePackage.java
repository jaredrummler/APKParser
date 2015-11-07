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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourcePackage {

  // the packageName
  private String name;
  private short id;
  // contains the names of the types of the Resources defined in the ResourcePackage
  private StringPool typeStringPool;
  //  contains the names (keys) of the Resources defined in the ResourcePackage.
  private StringPool keyStringPool;

  public ResourcePackage(PackageHeader header) {
    this.name = header.getName();
    this.id = (short) header.getId();
  }

  private Map<Short, TypeSpec> typeSpecMap = new HashMap<>();

  private Map<Short, List<Type>> typesMap = new HashMap<>();

  public void addTypeSpec(TypeSpec typeSpec) {
    this.typeSpecMap.put(typeSpec.getId(), typeSpec);
  }

  public TypeSpec getTypeSpec(Short id) {
    return this.typeSpecMap.get(id);
  }

  public void addType(Type type) {
    List<Type> types = this.typesMap.get(type.getId());
    if (types == null) {
      types = new ArrayList<>();
      this.typesMap.put(type.getId(), types);
    }
    types.add(type);
  }

  public List<Type> getTypes(Short id) {
    return this.typesMap.get(id);
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

  public StringPool getTypeStringPool() {
    return typeStringPool;
  }

  public void setTypeStringPool(StringPool typeStringPool) {
    this.typeStringPool = typeStringPool;
  }

  public StringPool getKeyStringPool() {
    return keyStringPool;
  }

  public void setKeyStringPool(StringPool keyStringPool) {
    this.keyStringPool = keyStringPool;
  }

  public Map<Short, TypeSpec> getTypeSpecMap() {
    return typeSpecMap;
  }

  public void setTypeSpecMap(Map<Short, TypeSpec> typeSpecMap) {
    this.typeSpecMap = typeSpecMap;
  }

  public Map<Short, List<Type>> getTypesMap() {
    return typesMap;
  }

  public void setTypesMap(Map<Short, List<Type>> typesMap) {
    this.typesMap = typesMap;
  }
}
