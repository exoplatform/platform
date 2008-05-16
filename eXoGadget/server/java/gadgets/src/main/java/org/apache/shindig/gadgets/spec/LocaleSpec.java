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

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Represents a Locale tag.
 * Generally compatible with java.util.Locale, but with some extra
 * localization data from the spec.
 * Named "LocaleSpec" so as to not conflict with java.util.Locale
 *
 * No localization.
 * No user pref substitution.
 */
public class LocaleSpec {

  /**
   * Locale@lang
   */
  private final String language;
  public String getLanguage() {
    return language;
  }

  /**
   * Locale@country
   */
  private final String country;
  public String getCountry() {
    return country;
  }

  /**
   * Locale@language_direction
   */
  private final String languageDirection;
  public String getLanguageDirection() {
    return languageDirection;
  }

  /**
   * Locale@messages
   */
  private final URI messages;
  public URI getMessages() {
    return messages;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder();
    buf.append("<Locale lang=\"")
       .append(language)
       .append("\" country=\"")
       .append(country)
       .append("\" language_direction=\"")
       .append(languageDirection)
       .append("\" messages=\"")
       .append(messages)
       .append("\"/>");
    return buf.toString();
  }

  /**
   * @param element
   * @param specUrl The url that the spec is loaded from. messages is assumed
   *     to be relative to this path.
   * @throws SpecParserException If language_direction is not valid
   */
  public LocaleSpec(Element element, URI specUrl) throws SpecParserException {
    language = XmlUtil.getAttribute(element, "lang", "all").toLowerCase();
    country = XmlUtil.getAttribute(element, "country", "ALL").toUpperCase();
    languageDirection
        = XmlUtil.getAttribute(element, "language_direction", "ltr");
    if (!("ltr".equals(languageDirection) ||
          "rtl".equals(languageDirection))) {
      throw new SpecParserException(
          "Locale@language_direction must be ltr or rtl");
    }
    String messages = XmlUtil.getAttribute(element, "messages");
    if (messages == null) {
      this.messages = URI.create("");
    } else {
      try {
        this.messages = new URL(specUrl.toURL(), messages).toURI();
      } catch (URISyntaxException e) {
        throw new SpecParserException("Locale@messages url is invalid.");
      } catch (MalformedURLException e) {
        throw new SpecParserException("Locale@messages url is invalid.");
      }
    }
  }
}
