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

import com.jaredrummler.apkparser.exception.ParserException;
import com.jaredrummler.apkparser.utils.XmlUtils;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class AndroidManifest {

  public final List<AndroidComponent> activities = new ArrayList<>();
  public final List<AndroidComponent> receivers = new ArrayList<>();
  public final List<AndroidComponent> services = new ArrayList<>();
  public final ApkMeta apkMeta;
  public final String xml;

  public AndroidManifest(ApkMeta apkMeta, String xml) throws ParseException {
    this.apkMeta = apkMeta;
    this.xml = xml;

    DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
    Document document;
    try {
      //DOM parser instance
      DocumentBuilder builder = builderFactory.newDocumentBuilder();
      //parse an XML file into a DOM tree
      document = builder.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));
    } catch (Exception e) {
      throw new ParserException("Parse manifest xml failed", e);
    }

    Node manifestNode = document.getElementsByTagName("manifest").item(0);
    NodeList nodes = manifestNode.getChildNodes();
    for (int i = 0; i < nodes.getLength(); i++) {
      Node node = nodes.item(i);
      String nodeName = node.getNodeName();
      if (nodeName.equals("application")) {
        NodeList children = node.getChildNodes();
        for (int j = 0; j < children.getLength(); j++) {
          Node child = children.item(j);
          String childName = child.getNodeName();
          if (childName.equals("service")) {
            services.add(getAndroidComponent(child));
          } else if (childName.equals("activity")) {
            activities.add(getAndroidComponent(child));
          } else if (childName.equals("receiver")) {
            receivers.add(getAndroidComponent(child));
          }
        }
      }
    }
  }

  private AndroidComponent getAndroidComponent(Node node) {
    AndroidComponent.Builder builder = AndroidComponent.newAndroidComponent();
    NamedNodeMap attributes = node.getAttributes();
    builder.name(XmlUtils.getAttribute(attributes, "android:name"));
    builder.exported(XmlUtils.getBoolAttribute(attributes, "android:exported", false));
    builder.process(XmlUtils.getAttribute(attributes, "android:process"));
    NodeList children = node.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      Node child = children.item(i);
      String childName = child.getNodeName();
      if (childName.equals("intent-filter")) {
        IntentFilter intentFilter = getIntentFilter(child);
        builder.addIntentFilter(intentFilter);
      }
    }
    return builder.build();
  }

  private IntentFilter getIntentFilter(Node intentNode) {
    NodeList intentChildren = intentNode.getChildNodes();
    IntentFilter intentFilter = new IntentFilter();
    for (int j = 0; j < intentChildren.getLength(); j++) {
      Node intentChild = intentChildren.item(j);
      String intentChildName = intentChild.getNodeName();
      NamedNodeMap intentChildAttributes = intentChild.getAttributes();
      if (intentChildName.equals("action")) {
        intentFilter.actions.add(XmlUtils.getAttribute(intentChildAttributes, "android:name"));
      } else if (intentChildName.equals("category")) {
        intentFilter.categories.add(XmlUtils.getAttribute(intentChildAttributes, "android:name"));
      } else if (intentChildName.equals("data")) {
        String scheme = XmlUtils.getAttribute(intentChildAttributes, "android:scheme");
        String host = XmlUtils.getAttribute(intentChildAttributes, "android:host");
        String pathPrefix = XmlUtils.getAttribute(intentChildAttributes, "android:pathPrefix");
        String mimeType = XmlUtils.getAttribute(intentChildAttributes, "android:mimeType");
        String type = XmlUtils.getAttribute(intentChildAttributes, "android:type");
        intentFilter.dataList.add(new IntentFilter.IntentData(
            scheme, mimeType, host, pathPrefix, type));
      }
    }
    return intentFilter;
  }

}
