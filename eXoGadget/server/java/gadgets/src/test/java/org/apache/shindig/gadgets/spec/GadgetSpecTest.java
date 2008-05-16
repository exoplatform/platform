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

import org.apache.shindig.gadgets.GadgetException;
import org.apache.shindig.gadgets.Substitutions;
import org.apache.shindig.gadgets.Substitutions.Type;

import junit.framework.TestCase;

import java.net.URI;

public class GadgetSpecTest extends TestCase {
  private static final URI SPEC_URL = URI.create("http://example.org/g.xml");
  public void testBasic() throws Exception {
    String xml = "<Module>" +
                 "<ModulePrefs title=\"title\"/>" +
                 "<UserPref name=\"foo\" datatype=\"string\"/>" +
                 "<Content type=\"html\">Hello!</Content>" +
                 "</Module>";
    GadgetSpec spec = new GadgetSpec(SPEC_URL, xml);
    assertEquals("title", spec.getModulePrefs().getTitle());
    assertEquals(UserPref.DataType.STRING,
        spec.getUserPrefs().get(0).getDataType());
    assertEquals("Hello!", spec.getView(GadgetSpec.DEFAULT_VIEW).getContent());
  }

  public void testMultipleContentSections() throws Exception {
    String xml = "<Module>" +
                 "<ModulePrefs title=\"title\"/>" +
                 "<Content type=\"html\" view=\"hello\">hello </Content>" +
                 "<Content type=\"html\" view=\"world\">world</Content>" +
                 "<Content type=\"html\" view=\"hello, test\">test</Content>" +
                 "</Module>";
    GadgetSpec spec = new GadgetSpec(SPEC_URL, xml);
    assertEquals("hello test", spec.getView("hello").getContent());
    assertEquals("world", spec.getView("world").getContent());
    assertEquals("test", spec.getView("test").getContent());
  }

  public void testMissingModulePrefs() throws Exception {
    String xml = "<Module>" +
                 "<Content type=\"html\"/>" +
                 "</Module>";
    try {
      GadgetSpec spec = new GadgetSpec(SPEC_URL, xml);
      fail("No exception thrown when ModulePrefs is missing.");
    } catch (SpecParserException e) {
      // OK
    }
  }

  public void testEnforceOneModulePrefs() throws Exception {
    String xml = "<Module>" +
                 "<ModulePrefs title=\"hello\"/>" +
                 "<ModulePrefs title=\"world\"/>" +
                 "<Content type=\"html\"/>" +
                 "</Module>";
    try {
      GadgetSpec spec = new GadgetSpec(SPEC_URL, xml);
      fail("No exception thrown when more than 1 ModulePrefs is specified.");
    } catch (SpecParserException e) {
      // OK
    }
  }

  public void testMalformedXml() throws Exception {
    String xml = "<Module><ModulePrefs/>";
    try {
      GadgetSpec spec = new GadgetSpec(SPEC_URL, xml);
      fail("No exception thrown on malformed XML.");
    } catch (SpecParserException e) {
      // OK
      assertEquals(GadgetException.Code.MALFORMED_XML_DOCUMENT, e.getCode());
      assertTrue(e.getMessage().contains(SPEC_URL.toString()));
    }
  }

  public void testSubstitutions() throws Exception {
    Substitutions substituter = new Substitutions();
    String title = "Hello, World!";
    String content = "Goodbye, world :(";
    String xml = "<Module>" +
                 "<ModulePrefs title=\"__UP_title__\"/>" +
                 "<Content type=\"html\">__MSG_content__</Content>" +
                 "</Module>";
    substituter.addSubstitution(Type.USER_PREF, "title", title);
    substituter.addSubstitution(Type.MESSAGE, "content", content);

    GadgetSpec spec = new GadgetSpec(SPEC_URL, xml).substitute(substituter);
    assertEquals(title, spec.getModulePrefs().getTitle());
    assertEquals(content, spec.getView(GadgetSpec.DEFAULT_VIEW).getContent());
  }
}
