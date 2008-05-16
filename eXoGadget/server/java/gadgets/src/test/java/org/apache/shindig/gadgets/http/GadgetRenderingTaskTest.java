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

import org.apache.shindig.gadgets.ContainerConfig;
import org.apache.shindig.gadgets.Gadget;
import org.apache.shindig.gadgets.GadgetContext;
import org.apache.shindig.gadgets.RemoteContent;
import org.apache.shindig.gadgets.RemoteContentRequest;
import org.apache.shindig.gadgets.spec.GadgetSpec;
import org.easymock.EasyMock;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

public class GadgetRenderingTaskTest extends HttpTestFixture {

  final static Enumeration<String> EMPTY_PARAMS = new Enumeration<String>() {
    public boolean hasMoreElements() {
      return false;
    }
    public String nextElement() {
      return null;
    }
  };

  final ByteArrayOutputStream baos = new ByteArrayOutputStream();
  final PrintWriter writer = new PrintWriter(baos);

  final static URI SPEC_URL = URI.create("http://example.org/gadget.xml");
  final static RemoteContentRequest SPEC_REQUEST
      = new RemoteContentRequest(SPEC_URL);
  final static String CONTENT = "Hello, world!";
  final static String ALT_CONTENT = "Goodbye, city.";
  final static String SPEC_XML
      = "<Module>" +
        "<ModulePrefs title=\"hello\"/>" +
        "<Content type=\"html\" quirks=\"false\">" + CONTENT + "</Content>" +
        "<Content type=\"html\" view=\"quirks\" quirks=\"true\"/>" +
        "<Content type=\"html\" view=\"ALIAS\">" + ALT_CONTENT + "</Content>" +
        "</Module>";
  final static String LIBS = "dummy:blah";

  /**
   * Performs boilerplate operations to get basic gadgets rendered
   * @return Output of the rendering request
   * @throws Exception
   */
  private String parseBasicGadget(String view) throws Exception {
    expectParseRequestParams(view);
    expectFetchGadget();
    expectLockedDomainCheck();
    expectWriteResponse();
    replay();
    gadgetRenderer.process(request, response);
    verify();
    writer.close();
    return new String(baos.toByteArray(), "UTF-8");
  }
  
  private void expectParseRequestParams(String view) throws Exception {
    expect(request.getParameter("url")).andReturn(SPEC_URL.toString());
    expect(request.getParameter("view")).andReturn(view);
    expect(request.getParameterNames()).andReturn(EMPTY_PARAMS);
    expect(request.getParameter("container")).andReturn(null);
    expect(request.getHeader("Host")).andReturn("www.example.com");    
  }
  
  private void expectLockedDomainCheck() throws Exception {
    expect(lockedDomainService.gadgetCanRender(
        EasyMock.eq("www.example.com"),
        (Gadget)EasyMock.anyObject(),
        EasyMock.eq("default"))).andReturn(true);    
  }
  
  private void expectFetchGadget() throws Exception {
    expect(fetcher.fetch(SPEC_REQUEST)).andReturn(new RemoteContent(SPEC_XML));
  }
  
  private void expectWriteResponse() throws Exception {
    expect(request.getParameter("libs")).andReturn(LIBS);
    expect(response.getWriter()).andReturn(writer);    
  }
  
  public void testStandardsMode() throws Exception {
    String content = parseBasicGadget(GadgetSpec.DEFAULT_VIEW);
    assertTrue(-1 != content.indexOf(GadgetRenderingTask.STRICT_MODE_DOCTYPE));
  }

  public void testQuirksMode() throws Exception {
    String content = parseBasicGadget("quirks");
    assertTrue(-1 == content.indexOf(GadgetRenderingTask.STRICT_MODE_DOCTYPE));
  }


  public void testContentRendered() throws Exception {
    String content = parseBasicGadget(GadgetSpec.DEFAULT_VIEW);
    assertTrue(-1 != content.indexOf(CONTENT));
  }

  @SuppressWarnings("unchecked")
  public void testForcedLibsIncluded() throws Exception {
    String jsLibs = "http://example.org/js/foo:bar.js";
    List<String> libs = Arrays.asList(LIBS.split(":"));
    expect(urlGenerator.getBundledJsUrl(isA(Collection.class),
        isA(GadgetContext.class))).andReturn(jsLibs);
    String content = parseBasicGadget(GadgetSpec.DEFAULT_VIEW);
    assertTrue(-1 != content.indexOf("<script src=\"" + jsLibs + "\">"));
  }

  public void testViewAliases() throws Exception {
    JSONObject json = new JSONObject();
    json.put("gadgets.container",
             new JSONArray().put(ContainerConfig.DEFAULT_CONTAINER));
    JSONArray aliases = new JSONArray().put("ALIAS");
    JSONObject dummy = new JSONObject().put("aliases", aliases);
    JSONObject views = new JSONObject().put("dummy", dummy);
    JSONObject features = new JSONObject().put("views", views);
    json.put("gadgets.features", features);
    containerConfig.loadFromString(json.toString());

    String content = parseBasicGadget("dummy");

    assertTrue(-1 != content.indexOf(ALT_CONTENT));
  }
  
  public void testLockedDomainFailure() throws Exception {
    expectParseRequestParams(GadgetSpec.DEFAULT_VIEW);
    expectFetchGadget();
    expectLockedDomainFailure();
    expectSendRedirect();
    replay();
    gadgetRenderer.process(request, response);
    verify();
    writer.close();
  }

  private void expectLockedDomainFailure() {
    expect(lockedDomainService.gadgetCanRender(
        EasyMock.eq("www.example.com"),
        (Gadget)EasyMock.anyObject(),
        EasyMock.eq("default"))).andReturn(false);
    expect(request.getScheme()).andReturn("http");
    expect(request.getServletPath()).andReturn("/gadgets/ifr");
    expect(request.getQueryString()).andReturn("stuff=foo%20bar");
    expect(lockedDomainService.getLockedDomainForGadget(
        SPEC_URL.toString(), "default")).andReturn("locked.example.com");
  }
  
  private void expectSendRedirect() throws Exception {
    response.sendRedirect(
        "http://locked.example.com/gadgets/ifr?stuff=foo%20bar");
    EasyMock.expectLastCall().once();
  }

  // TODO: Lots of ugly tests on html content.
}
