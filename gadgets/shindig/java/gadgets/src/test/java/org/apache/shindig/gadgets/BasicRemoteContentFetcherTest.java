/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.shindig.gadgets;

import junit.framework.TestCase;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.URI;

public class BasicRemoteContentFetcherTest extends TestCase {
  private ContentCache cache = new BasicContentCache();
  private ContentFetcher fetcher
      = new BasicRemoteContentFetcher(cache, Integer.MAX_VALUE);

  public void testFetch() throws Exception {
    String content = "Hello, world!";
    File temp = File.createTempFile(this.getName(), ".txt");
    temp.deleteOnExit();
    BufferedWriter out = new BufferedWriter(new FileWriter(temp));
    out.write(content);
    out.close();
    RemoteContentRequest request = new RemoteContentRequest(temp.toURI());
    RemoteContent response = fetcher.fetch(request);
    assertEquals(RemoteContent.SC_OK, response.getHttpStatusCode());
    assertEquals(content, response.getResponseAsString());
  }

  public void testNotExists() throws Exception {
    RemoteContentRequest request
        = new RemoteContentRequest(new URI("file:///does/not/exist"));
    RemoteContent response = fetcher.fetch(request);
    assertEquals(RemoteContent.SC_NOT_FOUND, response.getHttpStatusCode());
  }

  // TODO simulate fake POST requests, headers, options, etc.
}