/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.shindig.util;

import junit.framework.JUnit4TestAdapter;

import static org.junit.Assert.*;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class BlobCrypterTest {
  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(BlobCrypterTest.class);
  }

  private BasicBlobCrypter crypter;
  private FakeTimeSource timeSource;
  
  public BlobCrypterTest() {
    crypter = new BasicBlobCrypter("0123456789abcdef".getBytes());
    timeSource = new FakeTimeSource();
    crypter.timeSource = timeSource;
  }
  
  @Test
  public void testEncryptAndDecrypt() throws Exception {
    checkString("");
    checkString("a");
    checkString("ab");
    checkString("dfkljdasklsdfklasdjfklajsdfkljasdklfjasdkljfaskldjf");
    checkString(Crypto.getRandomString(500));
    checkString("foo bar baz");
    checkString("foo\nbar\nbaz");
  }

  private void checkString(String string) throws Exception {
    Map<String, String> in = new HashMap<String, String>();
    if (string != null) {
      in.put("a", string);
    }
    String blob = crypter.wrap(in);
    Map<String, String> out = crypter.unwrap(blob, 0);
    assertEquals(string, out.get("a"));
  }
  
  @Test
  public void testManyEntries() throws Exception {
    Map<String, String> in = new HashMap<String, String>();
    for (int i=0; i < 1000; i++) {
      in.put(Integer.toString(i), Integer.toString(i));
    }
    String blob = crypter.wrap(in);
    Map<String, String> out = crypter.unwrap(blob, 0);
    for (int i=0; i < 1000; i++) {
      assertEquals(out.get(Integer.toString(i)), Integer.toString(i));
    }
  }
  
  @Test
  public void testTimeStamping() throws Exception {
    long start = 1201917724000L;
    long skew = 180000;
    int maxAge = 300; // 5 minutes
    int realAge = 600; // 10 minutes
    try {
      
      timeSource.setCurrentTimeMillis(start);
      Map<String, String> in = new HashMap<String, String>();
      in.put("a", "b");
      String blob = crypter.wrap(in);
      timeSource.incrementSeconds(realAge);
      crypter.unwrap(blob, maxAge);
      fail("Blob should have expired");
    } catch (BlobExpiredException e) {
      assertEquals(start-skew, e.minDate.getTime());
      assertEquals(start+realAge*1000, e.used.getTime());
      assertEquals(start+skew+maxAge*1000, e.maxDate.getTime());
    }
  }
  
  @Test
  public void testTamperIV() throws Exception {
    try {
      Map<String, String> in = new HashMap<String, String>();
      in.put("a", "b");
      String blob = crypter.wrap(in);
      byte[] blobBytes = Base64.decodeBase64(blob.getBytes());
      blobBytes[0] ^= 0x01;
      String tampered = new String(Base64.encodeBase64(blobBytes));
      crypter.unwrap(tampered, 30);
      fail("Signature verification should have failed.");
    } catch (BlobCrypterException e) {
      // Good
    }
  }
  
  @Test
  public void testTamperData() throws Exception {
    try {
      Map<String, String> in = new HashMap<String, String>();
      in.put("a", "b");
      String blob = crypter.wrap(in);
      byte[] blobBytes = Base64.decodeBase64(blob.getBytes());
      blobBytes[30] ^= 0x01;
      String tampered = new String(Base64.encodeBase64(blobBytes));
      crypter.unwrap(tampered, 30);
      fail("Signature verification should have failed.");
    } catch (BlobCrypterException e) {
      // Good
    }
  }
  
  @Test
  public void testTamperMac() throws Exception {
    try {
      Map<String, String> in = new HashMap<String, String>();
      in.put("a", "b");
      String blob = crypter.wrap(in);
      byte[] blobBytes = Base64.decodeBase64(blob.getBytes());
      blobBytes[blobBytes.length-1] ^= 0x01;
      String tampered = new String(Base64.encodeBase64(blobBytes));
      crypter.unwrap(tampered, 30);
      fail("Signature verification should have failed.");
    } catch (BlobCrypterException e) {
      // Good
    }
  }
  
  @Test
  public void testFixedKey() throws Exception {
    BlobCrypter alt = new BasicBlobCrypter("0123456789abcdef".getBytes());
    Map<String, String> in = new HashMap<String, String>();
    in.put("a", "b");
    String blob = crypter.wrap(in);
    Map<String, String> out = alt.unwrap(blob, 30);
    assertEquals("b", out.get("a"));
  }
  
  @Test
  public void testBadKey() throws Exception {
    BlobCrypter alt = new BasicBlobCrypter("1123456789abcdef".getBytes());
    Map<String, String> in = new HashMap<String, String>();
    in.put("a", "b");
    String blob = crypter.wrap(in);
    try {
      alt.unwrap(blob, 30);
      fail("Decryption should have failed");
    } catch (BlobCrypterException e) {
      // Good.
    }
  }
  
  @Test
  public void testShortKeyFails() throws Exception {
    try {
      new BasicBlobCrypter("0123456789abcde".getBytes());
      fail("Short key should fail");
    } catch (IllegalArgumentException e) {
      // good.
    }
  }
}
