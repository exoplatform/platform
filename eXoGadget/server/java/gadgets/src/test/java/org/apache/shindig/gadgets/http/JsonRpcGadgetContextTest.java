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

package org.apache.shindig.gadgets.http;

import org.json.JSONObject;

import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class JsonRpcGadgetContextTest extends TestCase {
  final static String SPEC_URL = "http://example.org/gadget.xml";
  final static int SPEC_ID = 1234;
  final static String[] PREF_KEYS = new String[] {"hello", "foo"};
  final static String[] PREF_VALUES = new String[] {"world", "bar"};
  final static Map<String, String> prefs = new HashMap<String, String>();
  static {
    for (int i = 0, j = PREF_KEYS.length; i < j; ++i) {
      prefs.put(PREF_KEYS[i], PREF_VALUES[i]);
    }
  }

  public void testCorrectExtraction() throws Exception {
    JSONObject gadget = new JSONObject()
        .put("url", SPEC_URL)
        .put("moduleId", SPEC_ID)
        .put("prefs", prefs);

    JSONObject context = new JSONObject()
        .put("language", Locale.US.getLanguage())
        .put("country", Locale.US.getCountry().toUpperCase());

    JsonRpcGadgetContext jsonContext
        = new JsonRpcGadgetContext(context, gadget);
    assertEquals(SPEC_URL, jsonContext.getUrl().toString());
    assertEquals(SPEC_ID, jsonContext.getModuleId());
    assertEquals(Locale.US.getLanguage(),
                 jsonContext.getLocale().getLanguage());
    assertEquals(Locale.US.getCountry(), jsonContext.getLocale().getCountry());

    for (String key : PREF_KEYS) {
      String value = jsonContext.getUserPrefs().getPref(key);
      assertEquals(prefs.get(key), value);
    }
  }
}
