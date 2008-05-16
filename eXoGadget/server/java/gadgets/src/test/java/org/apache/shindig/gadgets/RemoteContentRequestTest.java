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

package org.apache.shindig.gadgets;

import org.apache.shindig.util.InputStreamConsumer;

import junit.framework.TestCase;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RemoteContentRequestTest extends TestCase {
  private static final String POST_BODY = "Hello, world!";
  private static final String CONTENT_TYPE = "text/plain";
  private static final String TEST_HEADER_KEY = "X-Test-Header";
  private static final String TEST_HEADER_VALUE = "Hello!";
  private static final URI DEFAULT_URI = URI.create("http://example.org/");

  public void testPostBodyCopied() throws Exception {
    RemoteContentRequest request
        = new RemoteContentRequest(DEFAULT_URI, POST_BODY.getBytes());
    assertEquals(POST_BODY.length(), request.getPostBodyLength());
    assertEquals(POST_BODY,
        InputStreamConsumer.readToString(request.getPostBody()));
  }

  public void testContentTypeExtraction() throws Exception {
    Map<String, List<String>> headers = new HashMap<String, List<String>>();
    headers.put("Content-Type", Arrays.asList(CONTENT_TYPE));
    RemoteContentRequest request
        = new RemoteContentRequest(DEFAULT_URI, headers);
    assertEquals(CONTENT_TYPE, request.getContentType());
  }

  public void testGetHeader() throws Exception {
    Map<String, List<String>> headers = new HashMap<String, List<String>>();
    headers.put(TEST_HEADER_KEY, Arrays.asList(TEST_HEADER_VALUE));
    RemoteContentRequest request
        = new RemoteContentRequest(DEFAULT_URI, headers);
    assertEquals(TEST_HEADER_VALUE, request.getHeader(TEST_HEADER_KEY));
  }
}
