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
package org.apache.shindig.gadgets;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.URI;
import java.util.List;

public class JsFeatureLoaderTest extends GadgetTestFixture {
  JsFeatureLoader loader;

  private static final String FEATURE_NAME = "test";
  private static final String DEF_JS_CONTENT = "var hello = 'world';";
  private static final String ALT_JS_CONTENT = "function test(){while(true);}";
  private static final String CONT_A = "test";
  private static final String CONT_B = "wuwowowaefdf";
  private static final URI JS_URL = URI.create("http://example.org/feature.js");

  @Override
  public void setUp() throws Exception {
    loader = new JsFeatureLoader(fetcher);
  }

  public void testBasicLoading() throws Exception {
    String xml = "<feature>" +
                 "  <name>" + FEATURE_NAME + "</name>" +
                 "  <gadget>" +
                 "    <script>" + DEF_JS_CONTENT + "</script>" +
                 "  </gadget>" +
                 "</feature>";
    GadgetFeatureRegistry.Entry entry = loader.loadFeature(registry, xml);

    assertEquals(FEATURE_NAME, entry.getName());
    GadgetFeature feature = entry.getFeature().create();
    List<JsLibrary> libs = feature.getJsLibraries(new GadgetContext());
    assertEquals(1, libs.size());
    assertEquals(JsLibrary.Type.INLINE, libs.get(0).getType());
    assertEquals(DEF_JS_CONTENT, libs.get(0).getContent());
  }

  public void testMultiContainers() throws Exception {
    String xml = "<feature>" +
                 "  <name>" + FEATURE_NAME + "</name>" +
                 "  <gadget container=\"" + CONT_A + "\">" +
                 "    <script>" + DEF_JS_CONTENT + "</script>" +
                 "  </gadget>" +
                 "  <gadget container=\"" + CONT_B + "\">" +
                 "    <script>" + ALT_JS_CONTENT + "</script>" +
                 "  </gadget>" +
                 "</feature>";
    GadgetFeatureRegistry.Entry entry = loader.loadFeature(registry, xml);
    GadgetFeature feature = entry.getFeature().create();
    List<JsLibrary> libs;
    libs = feature.getJsLibraries(new ContainerContext(CONT_A));
    assertEquals(DEF_JS_CONTENT, libs.get(0).getContent());
    libs = feature.getJsLibraries(new ContainerContext(CONT_B));
    assertEquals(ALT_JS_CONTENT, libs.get(0).getContent());
  }

  public void testFileReferences() throws Exception {
    File temp = File.createTempFile(getName(), ".js-noopt");
    BufferedWriter out = new BufferedWriter(new FileWriter(temp));
    out.write(DEF_JS_CONTENT);
    out.close();
    String xml = "<feature>" +
                 "  <name>" + FEATURE_NAME + "</name>" +
                 "  <gadget>" +
                 "    <script src=\"" + temp.getPath() + "\"/>" +
                 "  </gadget>" +
                 "</feature>";
    GadgetFeatureRegistry.Entry entry = loader.loadFeature(registry, xml);
    GadgetFeature feature = entry.getFeature().create();
    List<JsLibrary> libs = feature.getJsLibraries(new GadgetContext());
    assertEquals(1, libs.size());
    assertEquals(DEF_JS_CONTENT, libs.get(0).getContent());
    assertEquals(FEATURE_NAME, libs.get(0).getFeature());
  }

  public void testUrlReferences() throws Exception {
    String xml = "<feature>" +
                 "  <name>" + FEATURE_NAME + "</name>" +
                 "  <gadget>" +
                 "    <script src=\"" + JS_URL + "\"/>" +
                 "  </gadget>" +
                 "</feature>";
    RemoteContentRequest request = new RemoteContentRequest(JS_URL);
    RemoteContent response
        = new RemoteContent(200, ALT_JS_CONTENT.getBytes(), null);
    expect(fetcher.fetch(eq(request))).andReturn(response);
    replay();
    GadgetFeatureRegistry.Entry entry = loader.loadFeature(registry, xml);
    verify();
    GadgetFeature feature = entry.getFeature().create();
    List<JsLibrary> libs = feature.getJsLibraries(new GadgetContext());
    assertEquals(1, libs.size());
    assertEquals(ALT_JS_CONTENT, libs.get(0).getContent());
    assertEquals(FEATURE_NAME, libs.get(0).getFeature());
  }
}

class ContainerContext extends GadgetContext {
  private final String container;
  @Override
  public String getContainer() {
    return container;
  }
  public ContainerContext(String container) {
    this.container = container;
  }
}
