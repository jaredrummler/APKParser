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

public class Permission {

  public static Builder newPermission() {
    return new Builder();
  }

  public final String name;
  public final String label;
  public final String icon;
  public final String description;
  public final String group;
  public final String protectionLevel;

  private Permission(Builder builder) {
    this.name = builder.name;
    this.label = builder.label;
    this.icon = builder.icon;
    this.description = builder.description;
    this.group = builder.group;
    this.protectionLevel = builder.protectionLevel;
  }

  public static final class Builder {

    private String name;
    private String label;
    private String icon;
    private String description;
    private String group;
    private String protectionLevel;

    private Builder() {
    }

    public Permission build() {
      return new Permission(this);
    }

    public Builder name(String name) {
      this.name = name;
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

    public Builder description(String description) {
      this.description = description;
      return this;
    }

    public Builder group(String group) {
      this.group = group;
      return this;
    }

    public Builder protectionLevel(String protectionLevel) {
      this.protectionLevel = protectionLevel;
      return this;
    }
  }
}
