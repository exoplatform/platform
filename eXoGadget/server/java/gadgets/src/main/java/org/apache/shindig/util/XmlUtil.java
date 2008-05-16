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

package org.apache.shindig.util;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class XmlUtil {

  /**
   * Extracts an attribute from a node.
   *
   * @param node
   * @param attr
   * @param def
   * @return The value of the attribute, or def
   */
  public static String getAttribute(Node node, String attr, String def) {
    NamedNodeMap attrs = node.getAttributes();
    Node val = attrs.getNamedItem(attr);
    if (val != null) {
      return val.getNodeValue();
    }
    return def;
  }

  /**
   * @param node
   * @param attr
   * @return The value of the given attribute, or null if not present.
   */
  public static String getAttribute(Node node, String attr) {
    return getAttribute(node, attr, null);
  }

  /**
   * Retrieves an attribute as a URI.
   * @param node
   * @param attr
   * @return The parsed uri, or def if the attribute doesn't exist or can not
   *     be parsed as a URI.
   */
  public static URI getUriAttribute(Node node, String attr, URI def) {
    String uri = getAttribute(node, attr);
    if (uri != null) {
      try {
        return new URI(uri);
      } catch (URISyntaxException e) {
        return def;
      }
    }
    return def;
  }

  /**
   * Retrieves an attribute as a URI.
   * @param node
   * @param attr
   * @return The parsed uri, or null.
   */
  public static URI getUriAttribute(Node node, String attr) {
    return getUriAttribute(node, attr, null);
  }

  /**
   * Retrieves an attribute as a boolean.
   *
   * @param node
   * @param attr
   * @param def
   * @return True if the attribute exists and is not equal to "false"
   *    false if equal to "false", and def if not present.
   */
  public static boolean getBoolAttribute(Node node, String attr, boolean def) {
    String value = getAttribute(node, attr);
    if (value == null) {
      return def;
    }
    return !"false".equals(value);
  }

  /**
   * @param node
   * @param attr
   * @return True if the attribute exists and is not equal to "false"
   *    false otherwise.
   */
  public static boolean getBoolAttribute(Node node, String attr) {
    return getBoolAttribute(node, attr, false);
  }

  /**
   * Attempts to parse the input xml into a single element.
   * @param xml
   * @return The document object
   * @throws XmlException if a parse error occured.
   */
  public static Element parse(String xml) throws XmlException {
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      InputSource is = new InputSource(new StringReader(xml.trim()));
      return factory.newDocumentBuilder().parse(is).getDocumentElement();
    } catch (SAXParseException e) {
      throw new XmlException(e.getMessage()+" At: ("+e.getLineNumber()+","+e.getColumnNumber()+")", e);
    } catch (SAXException e) {
      throw new XmlException(e);
    } catch (ParserConfigurationException e) {
      throw new XmlException(e);
    } catch (IOException e) {
      throw new XmlException(e);
    }
  }
}
