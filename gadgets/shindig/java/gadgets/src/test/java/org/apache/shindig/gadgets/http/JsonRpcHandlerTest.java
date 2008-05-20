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

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;

import org.apache.shindig.gadgets.Gadget;
import org.apache.shindig.gadgets.RemoteContent;
import org.apache.shindig.gadgets.RemoteContentRequest;
import org.apache.shindig.gadgets.spec.GadgetSpec;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

public class JsonRpcHandlerTest extends HttpTestFixture {
  private static final URI SPEC_URL = URI.create("http://example.org/g.xml");
  private static final RemoteContentRequest SPEC_REQUEST
      = new RemoteContentRequest(SPEC_URL);
  private static final URI SPEC_URL2 = URI.create("http://example.org/g2.xml");
  private static final RemoteContentRequest SPEC_REQUEST2
      = new RemoteContentRequest(SPEC_URL2);
  private static final String SPEC_TITLE = "JSON-TEST";
  private static final String SPEC_TITLE2 = "JSON-TEST2";
  private static final String SPEC_XML
      = "<Module>" +
        "<ModulePrefs title=\"" + SPEC_TITLE + "\"/>" +
        "<Content type=\"html\">Hello, world</Content>" +
        "</Module>";
  private static final String SPEC_XML2
      = "<Module>" +
        "<ModulePrefs title=\"" + SPEC_TITLE2 + "\"/>" +
        "<Content type=\"html\">Hello, world</Content>" +
        "</Module>";

  private JSONObject createContext(String lang, String country)
      throws JSONException {
    return new JSONObject().put("language", lang).put("country", country);
  }

  private JSONObject createGadget(String url, int moduleId,
      Map<String, String> prefs) throws JSONException {
    return new JSONObject()
        .put("url", url)
        .put("moduleId", moduleId)
        .put("prefs", prefs == null ? Collections.emptySet() : prefs);
  }

  public void testSimpleRequest() throws Exception {
    JSONArray gadgets = new JSONArray()
      .put(createGadget(SPEC_URL.toString(), 0, null));
    JSONObject input = new JSONObject()
        .put("context", createContext("en", "US"))
        .put("gadgets", gadgets);

    GadgetSpec spec = new GadgetSpec(SPEC_URL, SPEC_XML);

    expect(fetcher.fetch(SPEC_REQUEST)).andReturn(new RemoteContent(SPEC_XML));
    expect(urlGenerator.getIframeUrl(isA(Gadget.class)))
        .andReturn(SPEC_URL.toString());

    replay();
    JSONObject response = jsonRpcHandler.process(input);
    verify();

    JSONArray outGadgets = response.getJSONArray("gadgets");
    JSONObject gadget = outGadgets.getJSONObject(0);
    assertEquals(SPEC_URL.toString(), gadget.getString("iframeUrl"));
    assertEquals(SPEC_TITLE, gadget.getString("title"));
    assertEquals(0, gadget.getInt("moduleId"));
  }

  public void testMultipleGadgets() throws Exception {
    JSONArray gadgets = new JSONArray()
     .put(createGadget(SPEC_URL.toString(), 0, null))
     .put(createGadget(SPEC_URL2.toString(), 1, null));
    JSONObject input = new JSONObject()
        .put("context", createContext("en", "US"))
        .put("gadgets", gadgets);

    GadgetSpec spec = new GadgetSpec(SPEC_URL, SPEC_XML);
    GadgetSpec spec2 = new GadgetSpec(SPEC_URL2, SPEC_XML2);

    expect(fetcher.fetch(SPEC_REQUEST))
        .andReturn(new RemoteContent(SPEC_XML));
    expect(fetcher.fetch(SPEC_REQUEST2))
        .andReturn(new RemoteContent(SPEC_XML2));

    replay();
    JSONObject response = jsonRpcHandler.process(input);
    verify();

    JSONArray outGadgets = response.getJSONArray("gadgets");
    JSONObject gadget = outGadgets.getJSONObject(0);
    if (gadget.getString("url").equals(SPEC_URL.toString())) {
      assertEquals(SPEC_TITLE, gadget.getString("title"));
      assertEquals(0, gadget.getInt("moduleId"));
    } else {
      assertEquals(SPEC_TITLE2, gadget.getString("title"));
      assertEquals(1, gadget.getInt("moduleId"));
    }
  }
}
