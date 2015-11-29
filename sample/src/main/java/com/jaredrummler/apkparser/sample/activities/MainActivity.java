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

package com.jaredrummler.apkparser.sample.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jaredrummler.apkparser.sample.dialogs.XmlListDialog;
import com.jaredrummler.apkparser.sample.fragments.AppListFragment;
import com.jaredrummler.apkparser.sample.interfaces.ApkParserSample;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class MainActivity extends AppCompatActivity implements ApkParserSample {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (savedInstanceState == null) {
      getFragmentManager()
          .beginTransaction()
          .add(android.R.id.content, new AppListFragment())
          .commit();
    }
  }

  @Override public void openXmlFile(PackageInfo app, String xml) {
    Intent intent = new Intent(this, XmlSourceViewerActivity.class);
    intent.putExtra("app", app);
    intent.putExtra("xml", xml);
    startActivity(intent);
  }

  @Override public void listXmlFiles(final PackageInfo app) {
    final ProgressDialog pd = new ProgressDialog(this);
    pd.setMessage("Please wait...");
    pd.show();

    new AsyncTask<Void, Void, String[]>() {

      @Override protected String[] doInBackground(Void... params) {
        List<String> xmlFiles = new ArrayList<>();
        ZipFile zipFile = null;
        try {
          zipFile = new ZipFile(app.applicationInfo.sourceDir);
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

      @Override protected void onPostExecute(String[] items) {
        pd.dismiss();
        if (!isFinishing()) {
          XmlListDialog.show(MainActivity.this, app, items);
        }
      }
    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
  }
}
