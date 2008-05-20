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

import org.apache.shindig.gadgets.FakeGadgetToken;
import org.apache.shindig.gadgets.GadgetException;
import org.apache.shindig.gadgets.GadgetToken;
import org.apache.shindig.gadgets.RemoteContent;
import org.apache.shindig.gadgets.RemoteContentRequest;
import org.apache.shindig.gadgets.spec.Auth;
import org.apache.shindig.gadgets.spec.Preload;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.util.Enumeration;

import javax.servlet.ServletOutputStream;

public class ProxyHandlerTest extends HttpTestFixture {

  private final static String URL_ONE = "http://www.example.com/test.html";
  private final static String DATA_ONE = "hello world";

  private static final GadgetToken DUMMY_TOKEN = new FakeGadgetToken();

  final ByteArrayOutputStream baos = new ByteArrayOutputStream();
  final PrintWriter writer = new PrintWriter(baos);
  
  final ServletOutputStream responseStream = new ServletOutputStream() {
    @Override
    public void write(int b) throws IOException {
      baos.write(b); 
    }
  };
  
  final static Enumeration<String> EMPTY_LIST = new Enumeration<String>() {
    public boolean hasMoreElements() {
      return false;
    }
    public String nextElement() {
      return null;
    }
  };

  private void expectGetAndReturnData(String url, byte[] data)
      throws Exception {
    RemoteContentRequest req = new RemoteContentRequest(
        "GET", new URI(url), null, null, new RemoteContentRequest.Options());
    RemoteContent resp = new RemoteContent(200, data, null);
    expect(contentFetcherFactory.get()).andReturn(fetcher);
    expect(fetcher.fetch(req)).andReturn(resp);
  }

  private void expectPostAndReturnData(String url, byte[] body, byte[] data)
      throws Exception {
    RemoteContentRequest req = new RemoteContentRequest(
        "POST", new URI(url), null, body, new RemoteContentRequest.Options());
    RemoteContent resp = new RemoteContent(200, data, null);
    expect(contentFetcherFactory.get()).andReturn(fetcher);
    expect(fetcher.fetch(req)).andReturn(resp);
  }

  private void setupPostRequestMock(String url, String body) throws Exception {
    setupGenericRequestMock("POST", url);
    expect(request.getParameter("postData")).andReturn(body).atLeastOnce();
  }

  private void setupGetRequestMock(String url) throws Exception {
    setupGenericRequestMock("GET", url);
  }

  private void setupGenericRequestMock(String method, String url)
      throws Exception {
    expect(request.getMethod()).andReturn("POST").atLeastOnce();
    expect(request.getParameter("httpMethod")).andReturn(method).atLeastOnce();
    expect(request.getParameter("url")).andReturn(url).atLeastOnce();
    expect(response.getWriter()).andReturn(writer).atLeastOnce();
  }
  
  private void setupProxyRequestMock(String host, String url) throws Exception {
    expect(request.getMethod()).andReturn("GET").atLeastOnce();
    expect(request.getHeader("Host")).andReturn(host);
    expect(request.getParameter("url")).andReturn(url).atLeastOnce();
    expect(request.getHeaderNames()).andReturn(EMPTY_LIST);
    expect(response.getOutputStream()).andReturn(responseStream).atLeastOnce();
  }
  
  private void setupFailedProxyRequestMock(String host, String url)
      throws Exception {
    expect(request.getHeader("Host")).andReturn(host);    
  }

  private JSONObject readJSONResponse(String body) throws Exception {
    String json
        = body.substring("throw 1; < don't be evil' >".length(), body.length());
    return new JSONObject(json);
  }

  public void testFetchJson() throws Exception {
    setupGetRequestMock(URL_ONE);
    expectGetAndReturnData(URL_ONE, DATA_ONE.getBytes());
    replay();
    proxyHandler.fetchJson(request, response);
    verify();
    writer.close();
    JSONObject json = readJSONResponse(baos.toString());
    JSONObject info = json.getJSONObject(URL_ONE);
    assertEquals(200, info.getInt("rc"));
    assertEquals(DATA_ONE, info.get("body"));
  }
  
  public void testLockedDomainEmbed() throws Exception {
    setupProxyRequestMock("www.example.com", URL_ONE);
    expect(lockedDomainService.embedCanRender("www.example.com"))
        .andReturn(true);
    expectGetAndReturnData(URL_ONE, DATA_ONE.getBytes());
    replay();
    proxyHandler.fetch(request, response);
    verify();
    responseStream.close();
    assertEquals(DATA_ONE, new String(baos.toByteArray()));
  }
  
  public void testLockedDomainFailedEmbed() throws Exception {
    setupFailedProxyRequestMock("www.example.com", URL_ONE);
    expect(lockedDomainService.embedCanRender("www.example.com"))
        .andReturn(false);
    replay();
    try {
      proxyHandler.fetch(request, response);
      fail("should have thrown");
    } catch (GadgetException e) {
      assertTrue(
          e.getMessage().indexOf("made to wrong domain www.example.com") != -1);
    }
    verify();
  }

  public void testFetchDecodedUrl() throws Exception {
    String origUrl = "http://www.example.com";
    String cleanedUrl = "http://www.example.com/";
    setupGetRequestMock(origUrl);
    expectGetAndReturnData(cleanedUrl, DATA_ONE.getBytes());
    replay();
    proxyHandler.fetchJson(request, response);
    verify();
    writer.close();
    JSONObject json = readJSONResponse(baos.toString());
    JSONObject info = json.getJSONObject(origUrl);
    assertEquals(200, info.getInt("rc"));
    assertEquals(DATA_ONE, info.get("body"));
  }

  public void testEmptyDocument() throws Exception {
    setupGetRequestMock(URL_ONE);
    expectGetAndReturnData(URL_ONE, "".getBytes());
    replay();
    proxyHandler.fetchJson(request, response);
    verify();
    writer.close();
    JSONObject json = readJSONResponse(baos.toString());
    JSONObject info = json.getJSONObject(URL_ONE);
    assertEquals(200, info.getInt("rc"));
    assertEquals("", info.get("body"));
  }

  public void testPostRequest() throws Exception {
    String body = "abc";
    setupPostRequestMock(URL_ONE, body);
    expectPostAndReturnData(URL_ONE, body.getBytes(), DATA_ONE.getBytes());
    replay();
    proxyHandler.fetchJson(request, response);
    verify();
    writer.close();
    JSONObject json = readJSONResponse(baos.toString());
    JSONObject info = json.getJSONObject(URL_ONE);
    assertEquals(200, info.getInt("rc"));
    assertEquals(DATA_ONE, info.get("body"));
  }

  public void testSignedGetRequest() throws Exception {
    // Doesn't actually sign since it returns the standard fetcher.
    // Signing tests are in SigningFetcherTest
    setupGetRequestMock(URL_ONE);
    expect(gadgetTokenDecoder.createToken("fake-token")).andReturn(DUMMY_TOKEN);
    expect(request.getParameter(ProxyHandler.SECURITY_TOKEN_PARAM))
        .andReturn("fake-token").atLeastOnce();
    expect(request.getParameter(Preload.AUTHZ_ATTR))
        .andReturn(Auth.SIGNED.toString()).atLeastOnce();
    RemoteContent resp = new RemoteContent(200, DATA_ONE.getBytes(), null);
    expect(contentFetcherFactory.getSigningFetcher(eq(DUMMY_TOKEN)))
        .andReturn(fetcher);
    expect(fetcher.fetch(isA(RemoteContentRequest.class))).andReturn(resp);
    replay();
    proxyHandler.fetchJson(request, response);
    verify();
    writer.close();
  }

  public void testSignedPostRequest() throws Exception {
    // Doesn't actually sign since it returns the standard fetcher.
    // Signing tests are in SigningFetcherTest
    String postBody = "foo=bar%20baz";
    setupPostRequestMock(URL_ONE, postBody);
    expect(gadgetTokenDecoder.createToken("fake-token")).andReturn(DUMMY_TOKEN);
    expect(request.getParameter(ProxyHandler.SECURITY_TOKEN_PARAM))
        .andReturn("fake-token").atLeastOnce();
    expect(request.getParameter(Preload.AUTHZ_ATTR))
        .andReturn(Auth.SIGNED.toString()).atLeastOnce();
    RemoteContent resp = new RemoteContent(200, DATA_ONE.getBytes(), null);
    expect(contentFetcherFactory.getSigningFetcher(eq(DUMMY_TOKEN)))
        .andReturn(fetcher);
    expect(fetcher.fetch(isA(RemoteContentRequest.class))).andReturn(resp);
    replay();
    proxyHandler.fetchJson(request, response);
    verify();
    writer.close();
  }

  public void testInvalidSigningType() throws Exception {
    setupGetRequestMock(URL_ONE);
    expect(request.getParameter(ProxyHandler.SECURITY_TOKEN_PARAM))
        .andReturn("fake-token").atLeastOnce();
    expect(request.getParameter(Preload.AUTHZ_ATTR))
        .andReturn("garbage").atLeastOnce();
    replay();
    try {
      proxyHandler.fetchJson(request, response);
      fail("proxyHandler accepted invalid authz type");
    } catch (GadgetException e) {
      assertEquals(GadgetException.Code.UNSUPPORTED_FEATURE, e.getCode());
    }
  }

  public void testValidateUrlNoPath() throws Exception {
    URI url = proxyHandler.validateUrl("http://www.example.com");
    assertEquals("http", url.getScheme());
    assertEquals("www.example.com", url.getHost());
    assertEquals(-1, url.getPort());
    assertEquals("/", url.getPath());
    assertNull(url.getQuery());
    assertNull(url.getFragment());
  }

  public void testValidateUrlWithPath() throws Exception {
    URI url = proxyHandler.validateUrl("http://www.example.com/foo");
    assertEquals("http", url.getScheme());
    assertEquals("www.example.com", url.getHost());
    assertEquals(-1, url.getPort());
    assertEquals("/foo", url.getPath());
    assertNull(url.getQuery());
    assertNull(url.getFragment());
  }

  public void testValidateUrlWithPort() throws Exception {
    URI url = proxyHandler.validateUrl("http://www.example.com:8080/foo");
    assertEquals("http", url.getScheme());
    assertEquals("www.example.com", url.getHost());
    assertEquals(8080, url.getPort());
    assertEquals("/foo", url.getPath());
    assertNull(url.getQuery());
    assertNull(url.getFragment());
  }

  public void testValidateUrlWithEncodedPath() throws Exception {
    URI url
        = proxyHandler.validateUrl("http://www.example.com:8080/foo%20bar");
    assertEquals("http", url.getScheme());
    assertEquals("www.example.com", url.getHost());
    assertEquals(8080, url.getPort());
    assertEquals("/foo%20bar", url.getRawPath());
    assertEquals("/foo bar", url.getPath());
    assertNull(url.getQuery());
    assertNull(url.getFragment());
  }

  public void testValidateUrlWithEncodedQuery() throws Exception {
    URI url= proxyHandler.validateUrl(
        "http://www.example.com:8080/foo?q=with%20space");
    assertEquals("http", url.getScheme());
    assertEquals("www.example.com", url.getHost());
    assertEquals(8080, url.getPort());
    assertEquals("/foo", url.getPath());
    assertEquals("q=with%20space", url.getRawQuery());
    assertEquals("q=with space", url.getQuery());
    assertNull(url.getFragment());
  }

  public void testValidateUrlWithNoPathAndEncodedQuery() throws Exception {
    URI url
        = proxyHandler.validateUrl("http://www.example.com?q=with%20space");
    assertEquals("http", url.getScheme());
    assertEquals("www.example.com", url.getHost());
    assertEquals(-1, url.getPort());
    assertEquals("/", url.getPath());
    assertEquals("q=with%20space", url.getRawQuery());
    assertEquals("q=with space", url.getQuery());
    assertNull(url.getFragment());
  }
}