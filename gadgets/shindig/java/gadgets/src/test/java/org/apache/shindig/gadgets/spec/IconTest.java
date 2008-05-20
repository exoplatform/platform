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

public class IconTest extends TestCase {
  public void testBasicIcon() throws Exception {
    String xml = "<Icon type=\"foo\" mode=\"base64\">helloWorld</Icon>";
    Icon icon = new Icon(XmlUtil.parse(xml));
    assertEquals("foo", icon.getType());
    assertEquals("base64", icon.getMode());
    assertEquals("helloWorld", icon.getContent());
  }

  public void testInvalidMode() throws Exception {
    String xml = "<Icon type=\"foo\" mode=\"broken\"/>";
    try {
      Icon icon = new Icon(XmlUtil.parse(xml));
      fail("No exception thrown when an invalid mode attribute is passed.");
    } catch (SpecParserException e) {
      // OK
    }
  }

  public void testSubstitutions() throws Exception {
    String xml = "<Icon>http://__MSG_domain__/icon.png</Icon>";
    Substitutions substituter = new Substitutions();
    substituter.addSubstitution(Substitutions.Type.MESSAGE, "domain",
        "example.org");
    Icon icon = new Icon(XmlUtil.parse(xml)).substitute(substituter);
    assertEquals("http://example.org/icon.png", icon.getContent());
  }
}
