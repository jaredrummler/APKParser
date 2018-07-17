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

package com.jaredrummler.apkparser.sample.adapters;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaredrummler.apkparser.sample.R;
import com.jaredrummler.apkparser.sample.util.AppNames;
import com.jaredrummler.apkparser.sample.util.Density;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.jaredrummler.apkparser.sample.picasso.AppIconRequestHandler.SCHEME_PNAME;

public class AppListAdapter extends BaseAdapter {

  private final List<PackageInfo> apps;
  private final LayoutInflater inflater;
  private final PackageManager pm;
  private final Picasso picasso;
  private final int size;

  public AppListAdapter(Context context, List<PackageInfo> installedApps) {
    apps = installedApps;
    inflater = LayoutInflater.from(context);
    picasso = Picasso.get();
    size = Density.toPx(context, 46);
    pm = context.getPackageManager();
  }

  @Override public int getCount() {
    return apps.size();
  }

  @Override public PackageInfo getItem(int position) {
    return apps.get(position);
  }

  @Override public long getItemId(int position) {
    return position;
  }

  @Override public View getView(int position, View convertView, ViewGroup parent) {
    if (convertView == null) {
      convertView = inflater.inflate(R.layout.list_item_app, parent, false);
    }
    ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
    TextView textView = (TextView) convertView.findViewById(R.id.textView);
    PackageInfo packageInfo = getItem(position);
    textView.setText(AppNames.getLabel(pm, packageInfo));
    picasso.load(Uri.parse(SCHEME_PNAME + ":" + packageInfo.packageName))
        .placeholder(android.R.drawable.sym_def_app_icon)
        .resize(size, size)
        .centerInside()
        .into(imageView);
    return convertView;
  }

}
