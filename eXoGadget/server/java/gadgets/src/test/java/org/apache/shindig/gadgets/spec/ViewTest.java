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
import org.apache.shindig.gadgets.Substitutions.Type;
import org.apache.shindig.util.XmlUtil;

import junit.framework.TestCase;

import java.util.Arrays;

public class ViewTest extends TestCase {

  public void testSimpleView() throws Exception {
    String viewName = "VIEW NAME";
    String content = "This is the content";

    String xml = "<Content" +
                 " type=\"html\"" +
                 " view=\"" + viewName + '\"' +
                 " quirks=\"false\"><![CDATA[" +
                    content +
                 "]]></Content>";

    View view = new View(viewName, Arrays.asList(XmlUtil.parse(xml)));

    assertEquals(viewName, view.getName());
    assertEquals(false, view.getQuirks());
    assertEquals(View.ContentType.HTML, view.getType());
    assertEquals(content, view.getContent());
  }

  public void testConcatenation() throws Exception {
   String body1 = "Hello, ";
   String body2 = "World!";
   String content1 = "<Content type=\"html\">" + body1 + "</Content>";
   String content2 = "<Content type=\"html\">" + body2 + "</Content>";
   View view = new View("test", Arrays.asList(XmlUtil.parse(content1),
                                              XmlUtil.parse(content2)));
   assertEquals(body1 + body2, view.getContent());
  }

  public void testContentTypeConflict() throws Exception {
    String content1 = "<Content type=\"html\"/>";
    String content2
        = "<Content type=\"url\" href=\"http://example.org/\"/>";

    try {
      View view = new View("test", Arrays.asList(XmlUtil.parse(content1),
                                                 XmlUtil.parse(content2)));
      fail("No exception thrown with conflicting type attributes.");
    } catch (SpecParserException e) {
      // this is what was supposed to happen.
    }
  }

  public void testHrefOnTypeUrl() throws Exception {
    String xml = "<Content type=\"url\"/>";
    try {
      View view = new View("dummy", Arrays.asList(XmlUtil.parse(xml)));
      fail("No exception thrown when href attribute is missing for type=url.");
    } catch (SpecParserException e) {
      // Ok
    }
  }

  public void testHrefMalformed() throws Exception {
    // Unfortunately, this actually does URI validation rather than URL, so
    // most anything will pass. urn:isbn:0321146530 is valid here.
    String xml = "<Content type=\"url\" href=\"fobad@$%!fdf\"/>";
    try {
      View view = new View("dummy", Arrays.asList(XmlUtil.parse(xml)));
      fail("No exception thrown when href attribute is not a valid uri.");
    } catch (SpecParserException e) {
      // Ok
    }
  }

  public void testQuirksCascade() throws Exception {
    String content1 = "<Content type=\"html\" quirks=\"true\"/>";
    String content2 = "<Content type=\"html\" quirks=\"false\"/>";
    View view = new View("test", Arrays.asList(XmlUtil.parse(content1),
                                               XmlUtil.parse(content2)));
    assertEquals(false, view.getQuirks());
  }

  public void testQuirksCascadeReverse() throws Exception {
    String content1 = "<Content type=\"html\" quirks=\"false\"/>";
    String content2 = "<Content type=\"html\" quirks=\"true\"/>";
    View view = new View("test", Arrays.asList(XmlUtil.parse(content1),
                                               XmlUtil.parse(content2)));
    assertEquals(true, view.getQuirks());
  }

  public void testContentSubstitution() throws Exception {
    String xml
        = "<Content type=\"html\">Hello, __MSG_world__ __MODULE_ID__</Content>";

    Substitutions substituter = new Substitutions();
    substituter.addSubstitution(Type.MESSAGE, "world",
        "foo __UP_planet____BIDI_START_EDGE__");
    substituter.addSubstitution(Type.USER_PREF, "planet", "Earth");
    substituter.addSubstitution(Type.BIDI, "START_EDGE", "right");
    substituter.addSubstitution(Type.MODULE, "ID", "3");

    View view = new View("test",
        Arrays.asList(XmlUtil.parse(xml))).substitute(substituter);
    assertEquals("Hello, foo Earthright 3", view.getContent());
  }

  public void testHrefSubstitution() throws Exception {
    String href = "http://__MSG_domain__/__MODULE_ID__?dir=__BIDI_DIR__";
    String xml = "<Content type=\"url\" href=\"" + href + "\"/>";

    Substitutions substituter = new Substitutions();
    substituter.addSubstitution(Type.MESSAGE, "domain",
        "__UP_subDomain__.example.org");
    substituter.addSubstitution(Type.USER_PREF, "subDomain", "up");
    substituter.addSubstitution(Type.BIDI, "DIR", "rtl");
    substituter.addSubstitution(Type.MODULE, "ID", "123");

    View view = new View("test",
        Arrays.asList(XmlUtil.parse(xml))).substitute(substituter);
    assertEquals("http://up.example.org/123?dir=rtl",
                 view.getHref().toString());
  }
}
