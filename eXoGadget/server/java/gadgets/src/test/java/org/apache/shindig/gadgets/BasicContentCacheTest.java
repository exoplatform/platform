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

import junit.framework.TestCase;

import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tests for basic content cache
 */
public class BasicContentCacheTest extends TestCase {
  /**
   * Used to parse Expires: header.
   */
  private final static DateFormat dateFormat
      = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");

  private ContentCache cache;

  @Override
  public void setUp() throws Exception {
     cache = new BasicContentCache();
  }

  @Override
  protected void tearDown() throws Exception {
    cache = null;
  }

  private RemoteContentRequest createRequest(String method) {
    RemoteContentRequest req = new RemoteContentRequest(method,
        URI.create("http://www.here.com"), new HashMap<String, List<String>>(),
        new byte[0], new RemoteContentRequest.Options());
    return req;
  }

  private RemoteContent createResponse(int statusCode, String header,
      String headerValue) {
    Map<String, List<String>> headers = new HashMap<String, List<String>>();
    if (header != null) {
      headers.put(header, Arrays.asList(headerValue));
    }
    RemoteContent resp = new RemoteContent(statusCode, new byte[0], headers);
    return resp;
  }

  private RemoteContent createExpiresResponse(int statusCode, long expiration) {
    Date newExpiry = new Date(expiration);
    return createResponse(statusCode, "Expires", dateFormat.format(newExpiry));
  }

  private RemoteContent createMaxAgeResponse(int statusCode, long age) {
    return createResponse(statusCode, "Cache-Control", "max-age=" + age);
  }

  public void testEmptyCache() {
    assertNull(cache.getContent(createRequest("GET")));
  }
  
  public void testCacheable() {
    RemoteContentRequest req = createRequest("GET");
    RemoteContent resp = createResponse(200, null, null);
    cache.addContent(req, resp);
    assertEquals(cache.getContent(req), resp);
  }

  public void testNotCacheableForPost() {
    RemoteContentRequest req = createRequest("POST");
    RemoteContent resp = createResponse(200, null, null);
    cache.addContent(req, resp);
    assertNull(cache.getContent(req));
  }

  public void testNotCacheableForErr() {
    RemoteContentRequest req = createRequest("GET");
    RemoteContent resp = createResponse(500, null, null);
    cache.addContent(req, resp);
    assertNull(cache.getContent(req));
  }

  public void testCacheableForFutureExpires() {
    RemoteContentRequest req = createRequest("GET");
    RemoteContent resp = createExpiresResponse(200,
        System.currentTimeMillis() + 10000L);
    cache.addContent(req, resp);
    assertEquals(cache.getContent(req), resp);
  }

  public void testNotCacheableForPastExpires() {
    RemoteContentRequest req = createRequest("GET");
    RemoteContent resp = createExpiresResponse(200,
        System.currentTimeMillis() - 10000L);
    cache.addContent(req, resp);
    assertNull(cache.getContent(req));
  }

  public void testNotCacheableForFutureExpiresWithError() {
    RemoteContentRequest req = createRequest("GET");
    RemoteContent resp = createExpiresResponse(500,
        System.currentTimeMillis() - 10000L);
    cache.addContent(req, resp);
    assertNull(cache.getContent(req));
  }

  public void testCacheableForFutureMaxAge() {
    RemoteContentRequest req = createRequest("GET");
    RemoteContent resp = createMaxAgeResponse(200,
        10000L);
    cache.addContent(req, resp);
    assertEquals(cache.getContent(req), resp);
  }

  public void testNotCacheableForNoCache() {
    RemoteContentRequest req = createRequest("GET");
    RemoteContent resp = createResponse(200, "Cache-Control", "no-cache");
    cache.addContent(req, resp);
    assertNull(cache.getContent(req));
  }

  public void testCacheableForExpiresWithWait() {
    RemoteContentRequest req = createRequest("GET");
    RemoteContent resp = createExpiresResponse(200,
        System.currentTimeMillis() + 5000L);
    cache.addContent(req, resp);
    try {
      synchronized (cache) {
        cache.wait(500L);
      }
    } catch (InterruptedException ie) {
      fail("Failed to wait for cache");
    }
    assertEquals(cache.getContent(req), resp);
  }
                                                 

  public void testNotCacheableForExpiresWithWait() {
    RemoteContentRequest req = createRequest("GET");
    RemoteContent resp = createExpiresResponse(200,
        System.currentTimeMillis() + 1000L);
    cache.addContent(req, resp);
    try {
      synchronized (cache) {
        cache.wait(1001L);
      }
    } catch (InterruptedException ie) {
      fail("Failed to wait for cache");
    }
    assertNull(cache.getContent(req));
  }

}
