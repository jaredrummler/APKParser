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
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.jaredrummler.apkparser.ApkParser;
import com.jaredrummler.apkparser.sample.R;
import com.jaredrummler.apkparser.sample.util.AppNames;

import java.io.IOException;
import java.io.InputStream;

/**
 * Simple example that parses the AndroidManifest.xml and displays the source in a WebView
 */
public class XmlSourceViewerActivity extends AppCompatActivity {

  private WebView webView;
  private PackageInfo app;
  private String sourceCodeText;
  private String xml;

  private final WebViewClient webViewClient = new WebViewClient() {

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

  };

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.source_viewer);

    app = getIntent().getParcelableExtra("app");
    xml = getIntent().getStringExtra("xml");

    getSupportActionBar().setTitle(AppNames.getLabel(getPackageManager(), app));
    getSupportActionBar().setSubtitle(xml);

    webView = (WebView) findViewById(R.id.source_view);
    webView.getSettings().setJavaScriptEnabled(true);
    webView.getSettings().setDefaultTextEncodingName("UTF-8");
    webView.setWebViewClient(webViewClient);

    if (savedInstanceState != null && savedInstanceState.containsKey("source")) {
      sourceCodeText = savedInstanceState.getString("source");
    }

    if (sourceCodeText == null) {
      new AndroidXmlLoader().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, app);
    } else {
      loadSourceCode(sourceCodeText);
    }
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putString("source", sourceCodeText);
  }

  private void loadSourceCode(String html) {
    String data = String.format(
        "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3" +
            ".org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"><html xmlns=\"http://www.w3" +
            ".org/1999/xhtml\"><head><meta http-equiv=\"Content-Type\" content=\"text/html; " +
            "charset=utf-8\" /><script src=\"run_prettify.js?skin=github\"></script></head><body " +
            "bgcolor=\"white\"><pre class=\"prettyprint linenums\">%s</pre></body></html>",
        html);
    webView.loadDataWithBaseURL("file:///android_asset/", data, "text/html", "UTF-8", null);
  }

  private final class AndroidXmlLoader extends AsyncTask<PackageInfo, Void, String> {

    @Override protected String doInBackground(PackageInfo... params) {
      ApkParser apkParser = ApkParser.create(params[0].applicationInfo);
      try {
        final String source;
        if (xml.equals("AndroidManifest.xml")) {
          source = apkParser.getManifestXml();
        } else {
          source = apkParser.transBinaryXml(xml);
        }
        return Html.escapeHtml(source);
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        apkParser.close();
      }
      return null;
    }

    @Override protected void onPostExecute(String escapedHtml) {
      sourceCodeText = escapedHtml;
      loadSourceCode(escapedHtml);
    }
  }

}
