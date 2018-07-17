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

package com.jaredrummler.apkparser.parser;

import com.jaredrummler.apkparser.model.ApkMeta;
import com.jaredrummler.apkparser.model.GlEsVersion;
import com.jaredrummler.apkparser.model.Permission;
import com.jaredrummler.apkparser.model.UseFeature;
import com.jaredrummler.apkparser.struct.xml.*;

public class ApkMetaTranslator implements XmlStreamer {

  private final ApkMeta.Builder builder = ApkMeta.newApkMeta();

  @Override public void onStartTag(XmlNodeStartTag xmlNodeStartTag) {
    Attributes attributes = xmlNodeStartTag.getAttributes();
    switch (xmlNodeStartTag.getName()) {
      case "application":
        builder.label(attributes.get("label"));
        builder.icon(attributes.get("icon"));
        break;
      case "manifest":
        builder.packageName(attributes.get("package"));
        builder.versionName(attributes.get("versionName"));
        builder.versionCode(attributes.getLong("versionCode"));
        String installLocation = attributes.get("installLocation");
        if (installLocation != null) {
          builder.installLocation(installLocation);
        }
        break;
      case "uses-sdk":
        builder.minSdkVersion(attributes.get("minSdkVersion"));
        builder.targetSdkVersion(attributes.get("targetSdkVersion"));
        builder.maxSdkVersion(attributes.get("maxSdkVersion"));
        break;
      case "supports-screens":
        builder.anyDensity(attributes.getBoolean("anyDensity", false));
        builder.smallScreens(attributes.getBoolean("smallScreens", false));
        builder.normalScreens(attributes.getBoolean("normalScreens", false));
        builder.largeScreens(attributes.getBoolean("largeScreens", false));
        break;
      case "uses-feature":
        String name = attributes.get("name");
        boolean required = attributes.getBoolean("required", false);
        if (name != null) {
          builder.addUseFeatures(new UseFeature(name, required));
        } else {
          Integer gl = attributes.getInt("glEsVersion");
          if (gl != null) {
            builder.glEsVersion(new GlEsVersion(gl >> 16, gl & 0xffff, required));
          }
        }
        break;
      case "uses-permission":
        builder.addUsesPermission(attributes.get("name"));
        break;
      case "permission":
        builder.addPermission(Permission.newPermission()
            .name(attributes.get("name"))
            .label(attributes.get("label"))
            .icon(attributes.get("icon"))
            .group(attributes.get("group"))
            .description(attributes.get("description"))
            .protectionLevel(attributes.get("android:protectionLevel"))
            .build());
        break;
      case "meta-data":
        builder.addMetaData(attributes.get("name"), attributes.get("value"));
        break;
    }
  }

  @Override public void onEndTag(XmlNodeEndTag xmlNodeEndTag) {
  }

  @Override public void onCData(XmlCData xmlCData) {
  }

  @Override public void onNamespaceStart(XmlNamespaceStartTag tag) {
  }

  @Override public void onNamespaceEnd(XmlNamespaceEndTag tag) {
  }

  public ApkMeta getApkMeta() {
    return builder.build();
  }

}
