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

package com.jaredrummler.apkparser.model;

import java.util.ArrayList;
import java.util.List;

public class IntentFilter {

  public final List<String> actions = new ArrayList<>();
  public final List<String> categories = new ArrayList<>();
  public final List<IntentData> dataList = new ArrayList<>();

  public static class IntentData {

    public final String scheme;
    public final String mimeType;
    public final String host;
    public final String pathPrefix;
    public final String type;

    public IntentData(String scheme, String mimeType, String host, String pathPrefix,
                      String type) {
      this.scheme = scheme;
      this.mimeType = mimeType;
      this.host = host;
      this.pathPrefix = pathPrefix;
      this.type = type;
    }

    @Override public String toString() {
      return "IntentData{" +
          "scheme='" + scheme + '\'' +
          ", mimeType='" + mimeType + '\'' +
          ", host='" + host + '\'' +
          ", pathPrefix='" + pathPrefix + '\'' +
          ", type='" + type + '\'' +
          '}';
    }
  }
}
