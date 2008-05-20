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
import org.apache.shindig.gadgets.Substitutions;
import org.apache.shindig.util.XmlUtil;

import org.w3c.dom.Element;

/**
 * Represents a ModuleSpec.Icon tag.
 *
 * TODO: Support substitution
 */
public class Icon {
  /**
   * Icon@mode
   * Probably better labeled "encoding"; currently only base64 is supported.
   * If mode is not set, content must be a url. Otherwise, content is
   * a mode-encoded image with a mime type equal to type.
   */
  private final String mode;
  public String getMode() {
    return mode;
  }

  /**
   * Icon@type
   * Mime type of the icon
   */
  private final String type;
  public String getType() {
    return type;
  }

  /**
   * Icon#CDATA
   *
   * Message Bundles
   */
  private String content;
  public String getContent() {
    return content;
  }

  /**
   * Substitutes the icon fields according to the spec.
   *
   * @param substituter
   * @return The substituted icon
   */
  public Icon substitute(Substitutions substituter) {
    Icon icon = new Icon(this);
    icon.content
        = substituter.substituteString(Substitutions.Type.MESSAGE, content);
    return icon;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder();
    buf.append("<Icon type=\"")
       .append(type)
       .append("\" mode=\"")
       .append(mode)
       .append("\">")
       .append(content)
       .append("</Icon>");
    return buf.toString();
  }

  /**
   * Currently does not validate icon data.
   * @param element
   */
  public Icon(Element element) throws SpecParserException {
    mode = XmlUtil.getAttribute(element, "mode");
    if (mode != null && !mode.equals("base64")) {
      throw new SpecParserException(
          "The only valid value for Icon@mode is \"base64\"");
    }
    type = XmlUtil.getAttribute(element, "type", "");
    content = element.getTextContent();
  }

  /**
   * Creates an icon for substitute()
   *
   * @param icon
   */
  private Icon(Icon icon) {
    mode = icon.mode;
    type = icon.type;
  }
}