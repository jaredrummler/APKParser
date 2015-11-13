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

import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.google.common.html.HtmlEscapers;
import com.jaredrummler.apkparser.ApkParser;
import com.jaredrummler.apkparser.sample.R;
import com.jaredrummler.apkparser.sample.util.AppNames;

import java.io.IOException;
import java.io.InputStream;

/**
 * Simple example that parses the AndroidManifest.xml.
 *
 * More examples to come.
 */
public class AndroidManifestActivity extends AppCompatActivity {

  private ApkParser apkParser;
  private WebView webView;
  private String sourceCodeText;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.source_viewer);

    PackageInfo packageInfo = getIntent().getParcelableExtra("app");

    String title = AppNames.getLabel(getPackageManager(), packageInfo);
    getSupportActionBar().setTitle(title);

    apkParser = ApkParser.create(packageInfo.applicationInfo);

    webView = (WebView) findViewById(R.id.source_view);
    webView.getSettings().setJavaScriptEnabled(true);
    webView.getSettings().setDefaultTextEncodingName("UTF-8");
    webView.setWebViewClient(new WebViewClient() {

      @Override public void onPageFinished(WebView view, String url) {
        ProgressBar progress = (ProgressBar) findViewById(R.id.progress);
        progress.setVisibility(View.GONE);
      }

      @Override public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        InputStream stream = inputStreamForAndroidResource(url);
        if (stream != null) {
          return new WebResourceResponse("text/javascript", "UTF-8", stream);
        }
        return super.shouldInterceptRequest(view, url);
      }

      private InputStream inputStreamForAndroidResource(String url) {
        final String ANDROID_ASSET = "file:///android_asset/";

        if (url.contains(ANDROID_ASSET)) {
          url = url.replaceFirst(ANDROID_ASSET, "");
          try {
            AssetManager assets = getAssets();
            Uri uri = Uri.parse(url);
            return assets.open(uri.getPath(), AssetManager.ACCESS_STREAMING);
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
        return null;
      }
    });

    new Thread(new Runnable() {

      @Override public void run() {
        try {
          sourceCodeText = HtmlEscapers.htmlEscaper().escape(apkParser.getManifestXml());
        } catch (IOException e) {
          e.printStackTrace();
        }

        runOnUiThread(new Runnable() {

          @Override public void run() {
            webView.loadDataWithBaseURL("file:///android_asset/",
                "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"><html xmlns=\"http://www.w3.org/1999/xhtml\"><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" /><script src=\"run_prettify.js?skin=sons-of-obsidian\"></script></head><body bgcolor=\"#000000\"><pre class=\"prettyprint linenums\">" +
                    sourceCodeText + "</pre></body></html>", "text/html", "UTF-8", null);
          }
        });
      }
    }).start();

  }

}
