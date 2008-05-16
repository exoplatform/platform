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
import org.apache.shindig.util.XmlUtil;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a Require or Optional tag.
 * No substitutions on any fields.
 */
public class Feature {
  /**
   * Require@feature
   * Optional@feature
   */
  private final String name;
  public String getName() {
    return name;
  }

  /**
   * Require.Param
   * Optional.Param
   *
   * Flattened into a map where Param@name is the key and Param content is
   * the value.
   */
  private final Map<String, String> params;
  public Map<String, String> getParams() {
    return params;
  }

  /**
   * Whether this is a Require or an Optional feature.
   */
  private final boolean required;
  public boolean getRequired() {
    return required;
  }

  /**
   * Produces an xml representation of the feature.
   */
  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder();
    buf.append(required ? "<Require" : "<Optional")
       .append(" feature=\"")
       .append(name)
       .append("\">");
    for (Map.Entry<String, String> entry : params.entrySet()) {
      buf.append("\n<Param name=\"")
         .append(entry.getKey())
         .append("\">")
         .append(entry.getValue())
         .append("</Param>");
    }
    buf.append(required ? "</Require>" : "</Optional>");
    return buf.toString();
  }

  /**
   * Creates a new Feature from an xml node.
   *
   * @param feature The feature to create
   * @throws SpecParserException When the Require or Optional tag is not valid
   */
  public Feature(Element feature) throws SpecParserException {
    this.required = feature.getNodeName().equals("Require");
    String name = XmlUtil.getAttribute(feature, "feature");
    if (name == null) {
      throw new SpecParserException(
          (required ? "Require" : "Optional") +"@feature is required.");
    }
    this.name = name;
    NodeList children = feature.getElementsByTagName("Param");
    if (children.getLength() > 0) {
      Map<String, String> params = new HashMap<String, String>();
      for (int i = 0, j = children.getLength(); i < j; ++i) {
        Element param = (Element)children.item(i);
        String paramName = XmlUtil.getAttribute(param, "name");
        if (paramName == null) {
          throw new SpecParserException("Param@name is required");
        }
        params.put(paramName, param.getTextContent());
      }
      this.params = Collections.unmodifiableMap(params);
    } else {
      this.params = Collections.emptyMap();
    }
  }
}
