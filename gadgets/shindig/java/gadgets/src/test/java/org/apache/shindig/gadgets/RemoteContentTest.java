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

import org.apache.shindig.util.InputStreamConsumer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RemoteContentTest extends TestCase {
  private Map<String, List<String>> headers;

  @Override
  public void setUp() {
    headers = new HashMap<String, List<String>>();
  }

  private void addHeader(String name, String value) {
    java.util.List<String> existing = headers.get(name);
    if (existing == null) {
      existing = new LinkedList<String>();
      headers.put(name, existing);
    }
    existing.add(value);
  }

  public void testGetEncoding() throws Exception {
    addHeader("Content-Type", "text/plain; charset=TEST-CHARACTER-SET");
    RemoteContent content = new RemoteContent(200, new byte[0], headers);
    assertEquals("TEST-CHARACTER-SET", content.getEncoding());
  }

  public void testEncodingDetectionUtf8WithBom() throws Exception {
    // Input is UTF-8 with BOM.
    byte[] data = new byte[] {
      (byte)0xEF, (byte)0xBB, (byte)0xBF, 'h', 'e', 'l', 'l', 'o'
    };
    addHeader("Content-Type", "text/plain; charset=UTF-8");
    RemoteContent content = new RemoteContent(200, data, headers);
    assertEquals("hello", content.getResponseAsString());
  }

  public void testEncodingDetectionLatin1() throws Exception {
    // Input is a basic latin-1 string with 1 non-UTF8 compatible char.
    byte[] data = new byte[] {
      'h', (byte)0xE9, 'l', 'l', 'o'
    };
    addHeader("Content-Type", "text/plain; charset=iso-8859-1");
    RemoteContent content = new RemoteContent(200, data, headers);
    assertEquals("h\u00E9llo", content.getResponseAsString());
  }

  public void testEncodingDetectionBig5() throws Exception {
    byte[] data = new byte[] {
      (byte)0xa7, (byte)0x41, (byte)0xa6, (byte)0x6e
    };
    addHeader("Content-Type", "text/plain; charset=BIG5");
    RemoteContent content = new RemoteContent(200, data, headers);
    String resp = content.getResponseAsString();
    assertEquals("\u4F60\u597D", content.getResponseAsString());
  }

  public void testPreserveBinaryData() throws Exception {
    byte[] data = new byte[] {
        (byte)0x00, (byte)0xDE, (byte)0xEA, (byte)0xDB, (byte)0xEE, (byte)0xF0
    };
    addHeader("Content-Type", "application/octet-stream");
    RemoteContent content = new RemoteContent(200, data, headers);
    byte[] out = InputStreamConsumer.readToByteArray(content.getResponse());
    assertTrue(Arrays.equals(data, out));
  }
}
