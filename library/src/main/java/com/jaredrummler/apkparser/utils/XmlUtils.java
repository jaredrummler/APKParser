/*
 * Copyright (C) 2015. Jared Rummler <jared.rummler@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.jaredrummler.apkparser.utils;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class XmlUtils {

  public static Long getLongAttribute(NamedNodeMap namedNodeMap, String name) {
    String value = getAttribute(namedNodeMap, name);
    if (value == null) {
      return null;
    } else {
      return Long.valueOf(value);
    }
  }

  public static Integer getIntAttribute(NamedNodeMap namedNodeMap, String name) {
    String value = getAttribute(namedNodeMap, name);
    if (value == null) {
      return null;
    } else {
      return Integer.valueOf(value);
    }
  }

  public static boolean getBoolAttribute(NamedNodeMap namedNodeMap, String name,
                                         boolean defaultValue) {
    Boolean value = getBoolAttribute(namedNodeMap, name);
    return value == null ? defaultValue : value;
  }

  public static Boolean getBoolAttribute(NamedNodeMap namedNodeMap, String name) {
    String value = getAttribute(namedNodeMap, name);
    if (value == null) {
      return null;
    } else {
      return Boolean.valueOf(value);
    }
  }

  public static String getAttribute(NamedNodeMap namedNodeMap, String name) {
    Node node = namedNodeMap.getNamedItem(name);
    if (node == null) {
      if (name.startsWith("android:")) {
        name = name.substring("android:".length());
      }
      node = namedNodeMap.getNamedItem(name);
      if (node == null) {
        return null;
      }
    }
    return node.getNodeValue();
  }
}
