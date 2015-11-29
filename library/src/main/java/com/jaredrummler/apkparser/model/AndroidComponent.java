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
import java.util.Collection;
import java.util.List;

public class AndroidComponent {

  public static final int TYPE_ACTIVITY = 1;
  public static final int TYPE_SERVICE = 2;
  public static final int TYPE_CONTENT_PROVIDER = 3;
  public static final int TYPE_BROADCAST_RECEIVER = 4;

  public static Builder newAndroidComponent(int type) {
    return new Builder(type);
  }

  public final String name;
  public final boolean exported;
  public final String process;
  public final List<IntentFilter> intentFilters;
  public final int type;

  private AndroidComponent(Builder builder) {
    this.name = builder.name;
    this.exported = builder.exported;
    this.process = builder.process;
    this.intentFilters = builder.intentFilters;
    this.type = builder.type;
  }

  public static final class Builder {

    private String name;
    private boolean exported;
    private String process;
    private final List<IntentFilter> intentFilters = new ArrayList<>();
    private final int type;

    private Builder(int type) {
      this.type = type;
    }

    public AndroidComponent build() {
      return new AndroidComponent(this);
    }

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder exported(boolean exported) {
      this.exported = exported;
      return this;
    }

    public Builder process(String process) {
      this.process = process;
      return this;
    }

    public Builder addIntentFilter(IntentFilter intentFilter) {
      this.intentFilters.add(intentFilter);
      return this;
    }

    public Builder addIntentFilters(Collection<IntentFilter> intentFilters) {
      this.intentFilters.addAll(intentFilters);
      return this;
    }
  }
}
