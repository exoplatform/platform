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

import org.apache.shindig.gadgets.Substitutions;
import org.apache.shindig.util.XmlUtil;

import junit.framework.TestCase;

import java.net.URI;
import java.util.Locale;

public class ModulePrefsTest extends TestCase {
  private static final URI SPEC_URL = URI.create("http://example.org/g.xml");
  public void testBasic() throws Exception {
    String xml = "<ModulePrefs" +
                 " title=\"title\"" +
                 " title_url=\"title_url\"" +
                 " description=\"description\"" +
                 " author=\"author\"" +
                 " author_email=\"author_email\"" +
                 " screenshot=\"screenshot\"" +
                 " thumbnail=\"thumbnail\"" +
                 " directory_title=\"directory_title\"" +
                 " width=\"1\"" +
                 " height=\"2\"" +
                 " category=\"category\"" +
                 " category2=\"category2\">" +
                 "  <Require feature=\"require\"/>" +
                 "  <Optional feature=\"optional\"/>" +
                 "  <Preload href=\"http://example.org\" authz=\"signed\"/>" +
                 "  <Icon/>" +
                 "  <Locale/>" +
                 "</ModulePrefs>";
    ModulePrefs prefs = new ModulePrefs(XmlUtil.parse(xml), SPEC_URL);
    assertEquals("title", prefs.getTitle());
    assertEquals("title_url", prefs.getTitleUrl().toString());
    assertEquals("description", prefs.getDescription());
    assertEquals("author", prefs.getAuthor());
    assertEquals("author_email", prefs.getAuthorEmail());
    assertEquals("screenshot", prefs.getScreenshot().toString());
    assertEquals("thumbnail", prefs.getThumbnail().toString());
    assertEquals("directory_title", prefs.getDirectoryTitle());
    assertEquals(1, prefs.getWidth());
    assertEquals(2, prefs.getHeight());
    assertEquals("category", prefs.getCategories().get(0));
    assertEquals("category2", prefs.getCategories().get(1));
    assertEquals(true, prefs.getFeatures().get("require").getRequired());
    assertEquals(false, prefs.getFeatures().get("optional").getRequired());
    assertEquals("http://example.org",
        prefs.getPreloads().get(0).getHref().toString());
    assertEquals(1, prefs.getIcons().size());
    assertEquals(1, prefs.getLocales().size());
  }

  public void testGetLocale() throws Exception {
    String xml = "<ModulePrefs title=\"locales\">" +
                 "  <Locale lang=\"en\" messages=\"en.xml\"/>" +
                 "  <Locale lang=\"foo\" language_direction=\"rtl\"/>" +
                 "</ModulePrefs>";
    ModulePrefs prefs = new ModulePrefs(XmlUtil.parse(xml), SPEC_URL);
    LocaleSpec spec = prefs.getLocale(new Locale("en", "uk"));
    assertEquals("http://example.org/en.xml", spec.getMessages().toString());

    spec = prefs.getLocale(new Locale("foo", "bar"));
    assertEquals("rtl", spec.getLanguageDirection());

  }

  public void testSubstitutions() {
    Substitutions substitutions = new Substitutions();
    // TODO
  }

  public void testTitleRequired() throws Exception {
    String xml = "<ModulePrefs/>";
    try {
      ModulePrefs prefs = new ModulePrefs(XmlUtil.parse(xml), SPEC_URL);
      fail("No exception thrown when ModulePrefs@title is missing.");
    } catch (SpecParserException e) {
      // OK
    }
  }
}
