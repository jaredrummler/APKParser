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

package com.jaredrummler.apkparser.sample.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Helper {

  public static String[] getXmlFiles(String path) {
    List<String> xmlFiles = new ArrayList<>();
    ZipFile zipFile = null;
    try {
      zipFile = new ZipFile(path);
      Enumeration<? extends ZipEntry> entries = zipFile.entries();
      while (entries.hasMoreElements()) {
        ZipEntry entry = entries.nextElement();
        String name = entry.getName();
        if (name.endsWith(".xml") && !name.equals("AndroidManifest.xml")) {
          xmlFiles.add(name);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (zipFile != null) {
        try {
          zipFile.close();
        } catch (IOException ignored) {
        }
      }
    }
    Collections.sort(xmlFiles);
    return xmlFiles.toArray(new String[xmlFiles.size()]);
  }
}
