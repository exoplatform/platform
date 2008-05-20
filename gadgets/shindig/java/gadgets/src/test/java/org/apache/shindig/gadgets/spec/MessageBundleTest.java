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

import junit.framework.TestCase;

import org.apache.shindig.gadgets.GadgetException;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class MessageBundleTest extends TestCase {
  private static final URI BUNDLE_URL = URI.create("http://example.org/m.xml");
  public void testNormalMessageBundle() throws Exception {
    Map<String, String> messages = new HashMap<String, String>();
    messages.put("hello", "world");
    messages.put("foo", "bar");

    String xml = "<messagebundle>";
    for (Map.Entry<String, String> entry : messages.entrySet()) {
      xml += "<msg name=\"" + entry.getKey() + "\">" + entry.getValue() +
          "</msg>";
    }
    xml += "</messagebundle>";
    MessageBundle bundle = new MessageBundle(BUNDLE_URL, xml);
    assertEquals(messages, bundle.getMessages());
  }

  public void testMissingNames() {
    String xml = "<messagebundle><msg>foo</msg></messagebundle>";
    try {
      MessageBundle bundle = new MessageBundle(BUNDLE_URL, xml);
      fail("No exception thrown when a msg has no name.");
    } catch (SpecParserException e) {
      // OK.
    }
  }

  public void testMalformedXml() {
    String xml = "</messagebundle>";
    try {
      MessageBundle bundle = new MessageBundle(BUNDLE_URL, xml);
      fail("No exception thrown on malformed XML.");
    } catch (SpecParserException e) {
      // OK
      assertEquals(GadgetException.Code.MALFORMED_XML_DOCUMENT, e.getCode());
      assertTrue(e.getMessage().contains(BUNDLE_URL.toString()));
    }
  }
}
