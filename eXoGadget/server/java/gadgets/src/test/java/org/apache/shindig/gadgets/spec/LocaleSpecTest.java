/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.shindig.gadgets.spec;

import org.apache.shindig.util.XmlUtil;

import junit.framework.TestCase;

import java.net.URI;

public class LocaleSpecTest extends TestCase {
  private static final URI SPEC_URL = URI.create("http://example.org/foo.xml");

  public void testNormalLocale() throws Exception {
    String xml = "<Locale" +
                 " lang=\"en\"" +
                 " country=\"US\"" +
                 " language_direction=\"rtl\"" +
                 " messages=\"http://example.org/msgs.xml\"/>";

    LocaleSpec locale = new LocaleSpec(XmlUtil.parse(xml), SPEC_URL);
    assertEquals("en", locale.getLanguage());
    assertEquals("US", locale.getCountry());
    assertEquals("rtl", locale.getLanguageDirection());
    assertEquals("http://example.org/msgs.xml",
        locale.getMessages().toString());
  }

  public void testRelativeLocale() throws Exception {
    String xml = "<Locale messages=\"/test/msgs.xml\"/>";
    LocaleSpec locale = new LocaleSpec(XmlUtil.parse(xml), SPEC_URL);
    assertEquals("http://example.org/test/msgs.xml",
        locale.getMessages().toString());
  }

  public void testDefaultLanguageAndCountry() throws Exception {
    String xml = "<Locale/>";
    LocaleSpec locale = new LocaleSpec(XmlUtil.parse(xml), SPEC_URL);
    assertEquals("all", locale.getLanguage());
    assertEquals("ALL", locale.getCountry());
  }

  public void testInvalidLanguageDirection() throws Exception {
    String xml = "<Locale language_direction=\"invalid\"/>";
    try {
      LocaleSpec locale = new LocaleSpec(XmlUtil.parse(xml), SPEC_URL);
      fail("No exception thrown when invalid language_direction is specified.");
    } catch (SpecParserException e) {
      // OK.
    }
  }

  public void testInvalidMessagesUrl() throws Exception {
    String xml = "<Locale messages=\"fobad@$%!fdf\"/>";
    try {
      LocaleSpec locale = new LocaleSpec(XmlUtil.parse(xml), SPEC_URL);
      fail("No exception thrown when invalid messages url is specified.");
    } catch (SpecParserException e) {
      // OK.
    }
  }
}
