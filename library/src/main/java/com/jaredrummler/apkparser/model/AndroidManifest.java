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
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static com.jaredrummler.apkparser.model.AndroidComponent.TYPE_ACTIVITY;
import static com.jaredrummler.apkparser.model.AndroidComponent.TYPE_BROADCAST_RECEIVER;
import static com.jaredrummler.apkparser.model.AndroidComponent.TYPE_CONTENT_PROVIDER;
import static com.jaredrummler.apkparser.model.AndroidComponent.TYPE_SERVICE;

public class AndroidManifest {

  private static AndroidComponent getAndroidComponent(Node node, int type) {
    AndroidComponent.Builder builder = AndroidComponent.newAndroidComponent(type);
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

  private static IntentFilter getIntentFilter(Node intentNode) {
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

  public final List<AndroidComponent> activities = new ArrayList<>();
  public final List<AndroidComponent> receivers = new ArrayList<>();
  public final List<AndroidComponent> services = new ArrayList<>();
  public final List<AndroidComponent> providers = new ArrayList<>();
  public final ApkMeta apkMeta;
  public final String xml;

  public AndroidManifest(ApkMeta apkMeta, String xml) throws ParserException {
    this.apkMeta = apkMeta;
    this.xml = xml;
    parse();
  }

  /**
   * @return a list of all components
   */
  public List<AndroidComponent> getComponents() {
    List<AndroidComponent> components = new ArrayList<>();
    components.addAll(activities);
    components.addAll(services);
    components.addAll(receivers);
    components.addAll(providers);
    return components;
  }

  private void parse() throws ParserException {
    DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
    Document document;
    try {
      DocumentBuilder builder = builderFactory.newDocumentBuilder();
      document = builder.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));
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
            switch (childName) {
              case "service":
                services.add(getAndroidComponent(child, TYPE_SERVICE));
                break;
              case "activity":
                activities.add(getAndroidComponent(child, TYPE_ACTIVITY));
                break;
              case "receiver":
                receivers.add(getAndroidComponent(child, TYPE_BROADCAST_RECEIVER));
                break;
              case "provider":
                providers.add(getAndroidComponent(child, TYPE_CONTENT_PROVIDER));
                break;
            }
          }
        }
      }
    } catch (Exception e) {
      throw new ParserException("Error parsing AndroidManifest.xml", e);
    }
  }

}
