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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApkMeta {

  public static Builder newApkMeta() {
    return new Builder();
  }

  public final String packageName;
  public final String label;
  public final String icon;
  public final String versionName;
  public final Long versionCode;
  public final String installLocation;
  public final String minSdkVersion;
  public final String targetSdkVersion;
  public final String maxSdkVersion;
  public final GlEsVersion glEsVersion;
  public final boolean anyDensity;
  public final boolean smallScreens;
  public final boolean normalScreens;
  public final boolean largeScreens;

  public final List<String> usesPermissions;
  public final List<UseFeature> usesFeatures;
  public final List<Permission> permissions;
  public final Map<String, String> metaData;

  private ApkMeta(Builder builder) {
    this.packageName = builder.packageName;
    this.label = builder.label;
    this.icon = builder.icon;
    this.versionName = builder.versionName;
    this.versionCode = builder.versionCode;
    this.installLocation = builder.installLocation;
    this.minSdkVersion = builder.minSdkVersion;
    this.targetSdkVersion = builder.targetSdkVersion;
    this.maxSdkVersion = builder.maxSdkVersion;
    this.glEsVersion = builder.glEsVersion;
    this.anyDensity = builder.anyDensity;
    this.smallScreens = builder.smallScreens;
    this.normalScreens = builder.normalScreens;
    this.largeScreens = builder.largeScreens;
    this.usesPermissions = builder.usesPermissions;
    this.usesFeatures = builder.usesFeatures;
    this.permissions = builder.permissions;
    this.metaData = builder.metaData;
  }

  public static final class Builder {

    private String packageName;
    private String label;
    private String icon;
    private String versionName;
    private Long versionCode;
    private String installLocation;
    private String minSdkVersion;
    private String targetSdkVersion;
    private String maxSdkVersion;
    private GlEsVersion glEsVersion;
    private boolean anyDensity;
    private boolean smallScreens;
    private boolean normalScreens;
    private boolean largeScreens;
    private List<String> usesPermissions = new ArrayList<>();
    private List<UseFeature> usesFeatures = new ArrayList<>();
    private List<Permission> permissions = new ArrayList<>();
    private Map<String, String> metaData = new HashMap<>();

    private Builder() {
    }

    public ApkMeta build() {
      return new ApkMeta(this);
    }

    public Builder packageName(String packageName) {
      this.packageName = packageName;
      return this;
    }

    public Builder label(String label) {
      this.label = label;
      return this;
    }

    public Builder icon(String icon) {
      this.icon = icon;
      return this;
    }

    public Builder versionName(String versionName) {
      this.versionName = versionName;
      return this;
    }

    public Builder versionCode(Long versionCode) {
      this.versionCode = versionCode;
      return this;
    }

    public Builder installLocation(String installLocation) {
      this.installLocation = installLocation;
      return this;
    }

    public Builder minSdkVersion(String minSdkVersion) {
      this.minSdkVersion = minSdkVersion;
      return this;
    }

    public Builder targetSdkVersion(String targetSdkVersion) {
      this.targetSdkVersion = targetSdkVersion;
      return this;
    }

    public Builder maxSdkVersion(String maxSdkVersion) {
      this.maxSdkVersion = maxSdkVersion;
      return this;
    }

    public Builder glEsVersion(GlEsVersion glEsVersion) {
      this.glEsVersion = glEsVersion;
      return this;
    }

    public Builder anyDensity(boolean anyDensity) {
      this.anyDensity = anyDensity;
      return this;
    }

    public Builder smallScreens(boolean smallScreens) {
      this.smallScreens = smallScreens;
      return this;
    }

    public Builder normalScreens(boolean normalScreens) {
      this.normalScreens = normalScreens;
      return this;
    }

    public Builder largeScreens(boolean largeScreens) {
      this.largeScreens = largeScreens;
      return this;
    }

    public Builder addUsesPermission(String permission) {
      this.usesPermissions.add(permission);
      return this;
    }

    public Builder addUseFeatures(UseFeature feature) {
      this.usesFeatures.add(feature);
      return this;
    }

    public Builder addPermission(Permission permissions) {
      this.permissions.add(permissions);
      return this;
    }

    public Builder addMetaData(String key, String value) {
      if (key != null) {
          this.metaData.put(key, value);
      }
      return this;
    }
  }
}
