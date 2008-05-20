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
package org.apache.shindig.util;

import static org.junit.Assert.*;

import junit.framework.JUnit4TestAdapter;

import org.junit.Test;

public class StringEncodingTest {
  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(StringEncodingTest.class);
  }
 
  @Test
  public void testBase32() throws Exception {
    StringEncoding encoder = new StringEncoding(
        "0123456789abcdefghijklmnopqrstuv".toCharArray()); 
    testEncoding(encoder, new byte[] { 0 }, "00");
    testEncoding(encoder, new byte[] { 0, 0 }, "0000");
    testEncoding(encoder, new byte[] { 10, 0 }, "1800");
    testRoundTrip(encoder, Crypto.getRandomBytes(1));
    testRoundTrip(encoder, Crypto.getRandomBytes(2));
    testRoundTrip(encoder, Crypto.getRandomBytes(3));
    testRoundTrip(encoder, Crypto.getRandomBytes(20));
    testRoundTrip(encoder, Crypto.getRandomBytes(30));
  }

  private void testRoundTrip(StringEncoding encoder, byte[] bytes) {
    String encoded = encoder.encode(bytes);
    byte[] decoded = encoder.decode(encoded);
    assertArrayEquals(bytes, decoded);
  }

  private void testEncoding(StringEncoding encoder, byte[] b, String s) {
    String encoded = encoder.encode(b);
    assertEquals(s, encoded);
    byte[] decoded = encoder.decode(encoded);
    assertArrayEquals(b, decoded);
  }
}
