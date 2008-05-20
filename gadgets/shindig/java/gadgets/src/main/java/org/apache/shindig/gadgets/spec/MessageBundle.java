/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.shindig.gadgets.spec;

import org.apache.shindig.util.XmlException;
import org.apache.shindig.util.XmlUtil;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a messagebundle structure.
 */
public class MessageBundle {

  public static final MessageBundle EMPTY = new MessageBundle();

  private final Map<String, String> messages;
  /**
   * @return A read-only view of the message bundle.
   */
  public Map<String, String> getMessages() {
    return messages;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder();
    buf.append("<messagebundle>\n");
    for (Map.Entry<String, String> entry : messages.entrySet()) {
      buf.append("<msg name=\"").append(entry.getKey()).append("\">")
         .append(entry.getValue())
         .append("</msg>\n");
    }
    buf.append("</messagebundle>");
    return buf.toString();
  }

  /**
   * Constructs a message bundle from input xml
   * @param xml
   * @throws SpecParserException
   */
  public MessageBundle(URI url, String xml) throws SpecParserException {
    Element doc;
    try {
      doc = XmlUtil.parse(xml);
    } catch (XmlException e) {
      throw new SpecParserException("Malformed XML in file " + url.toString()
          + ": " + e.getMessage());
    }

    NodeList nodes = doc.getElementsByTagName("msg");
    Map<String, String> messages
        = new HashMap<String, String>(nodes.getLength(), 1);

    for (int i = 0, j = nodes.getLength(); i < j; ++i) {
      Element msg = (Element)nodes.item(i);
      String name = XmlUtil.getAttribute(msg, "name");
      if (name == null) {
        throw new SpecParserException(
            "All message bundle entries must have a name attribute.");
      }
      messages.put(name, msg.getTextContent().trim());
    }
    this.messages = Collections.unmodifiableMap(messages);
  }

  private MessageBundle() {
    this.messages = Collections.emptyMap();
  }
}
