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
import org.apache.shindig.util.HashUtil;
import org.apache.shindig.util.XmlException;
import org.apache.shindig.util.XmlUtil;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Represents a gadget specification root element (Module).
 * @see <a href="http://code.google.com/apis/gadgets/docs/gadgets-extended-xsd.html">gadgets spec</a>
 */
public class GadgetSpec {
  public static final String DEFAULT_VIEW = "default";
  public static final Locale DEFAULT_LOCALE = new Locale("all", "ALL");

  /**
   * The url for this gadget spec.
   */
  private final URI url;
  public URI getUrl() {
    return url;
  }

  /**
   * A checksum of the gadget's content.
   */
  private final String checksum;
  public String getChecksum() {
    return checksum;
  }

  /**
   * ModulePrefs
   */
  private ModulePrefs modulePrefs;
  public ModulePrefs getModulePrefs() {
    return modulePrefs;
  }

  /**
   * UserPref
   */
  private List<UserPref> userPrefs;
  public List<UserPref> getUserPrefs() {
    return userPrefs;
  }

  /**
   * Content
   * Mapping is view -> Content section.
   */
  private Map<String, View> views;
  public Map<String, View> getViews() {
    return views;
  }

  /**
   * Retrieves a single view by name.
   *
   * @param name The name of the view you want to see
   * @return The view object, if it exists, or null.
   */
  public View getView(String name) {
    return views.get(name);
  }

  /**
   * Performs substitutions on the spec. See individual elements for
   * details on what gets substituted.
   *
   * @param substituter
   * @return The substituted spec.
   */
  public GadgetSpec substitute(Substitutions substituter) {
    GadgetSpec spec = new GadgetSpec(this);
    spec.modulePrefs = modulePrefs.substitute(substituter);
    if (userPrefs.size() == 0) {
      spec.userPrefs = Collections.emptyList();
    } else {
      List<UserPref> prefs = new ArrayList<UserPref>(userPrefs.size());
      for (UserPref pref : userPrefs) {
        prefs.add(pref.substitute(substituter));
      }
      spec.userPrefs = Collections.unmodifiableList(prefs);
    }
    Map<String, View> viewMap = new HashMap<String, View>(views.size());
    for (View view : views.values()) {
     viewMap.put(view.getName(), view.substitute(substituter));
    }
    spec.views = Collections.unmodifiableMap(viewMap);

    return spec;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder();
    buf.append("<Module>\n")
       .append(modulePrefs).append('\n');
    for (UserPref pref : userPrefs) {
      buf.append(pref).append('\n');
    }
    for (Map.Entry<String, View> view : views.entrySet()) {
      buf.append(view.getValue()).append('\n');
    }
    buf.append("</Module>");
    return buf.toString();
  }

  /**
   * Creates a new Module from the given xml input.
   *
   * @param url
   * @param xml
   * @throws SpecParserException
   */
  public GadgetSpec(URI url, String xml) throws SpecParserException {
    Element doc;
    try {
      doc = XmlUtil.parse(xml);
    } catch (XmlException e) {
      throw new SpecParserException("Malformed XML in file " + url.toString()
          + ": " + e.getMessage());
    }
    this.url = url;

    // This might not be good enough; should we take message bundle changes
    // into account?
    this.checksum = HashUtil.checksum(xml.getBytes());

    NodeList children = doc.getChildNodes();

    ModulePrefs modulePrefs = null;
    List<UserPref> userPrefs = new LinkedList<UserPref>();
    Map<String, List<Element>> views = new HashMap<String, List<Element>>();
    for (int i = 0, j = children.getLength(); i < j; ++i) {
      Node child = children.item(i);
      if (!(child instanceof Element)) {
        continue;
      }
      Element element = (Element)child;
      String name = element.getTagName();
      if ("ModulePrefs".equals(name)) {
        if (modulePrefs == null) {
          modulePrefs = new ModulePrefs(element, url);
        } else {
          throw new SpecParserException(
              "Only 1 ModulePrefs is allowed.");
        }
      }
      if ("UserPref".equals(name)) {
        UserPref pref = new UserPref(element);
        userPrefs.add(pref);
      }
      if ("Content".equals(name)) {
        String viewNames = XmlUtil.getAttribute(element, "view", "default");
        for (String view : viewNames.split(",")) {
          view = view.trim();
          List<Element> viewElements = views.get(view);
          if (viewElements == null) {
            viewElements = new LinkedList<Element>();
            views.put(view, viewElements);
          }
          viewElements.add(element);
        }
      }
    }

    if (modulePrefs == null) {
      throw new SpecParserException(
          "At least 1 ModulePrefs is required.");
    } else {
      this.modulePrefs = modulePrefs;
    }

    if (views.size() == 0) {
      throw new SpecParserException("At least 1 Content is required.");
    } else {
      Map<String, View> tmpViews = new HashMap<String, View>();
      for (Map.Entry<String, List<Element>> view : views.entrySet()) {
        View v = new View(view.getKey(), view.getValue());
        tmpViews.put(v.getName(), v);
      }
      this.views = Collections.unmodifiableMap(tmpViews);
    }

    if (userPrefs.size() > 0) {
      this.userPrefs = Collections.unmodifiableList(userPrefs);
    } else {
      this.userPrefs = Collections.emptyList();
    }
  }

  /**
   * Constructs a GadgetSpec for substitute calls.
   * @param spec
   */
  private GadgetSpec(GadgetSpec spec) {
    url = spec.url;
    checksum = spec.checksum;
  }
}