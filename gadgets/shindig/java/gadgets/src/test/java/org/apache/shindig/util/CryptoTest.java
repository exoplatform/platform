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

import java.security.GeneralSecurityException;

public class CryptoTest {
  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(CryptoTest.class);
  }

  private BasicBlobCrypter crypter;
  
  public CryptoTest() {
    crypter = new BasicBlobCrypter("0123456789abcdef".getBytes());
    crypter.timeSource = new FakeTimeSource();
  }
  
  @Test
  public void testHmacSha1() throws Exception { 
    String key = "abcd1234";
    String val = "your mother is a hedgehog";
    byte[] expected = new byte[] {
        -21, 2, 47, -101, 9, -40, 18, 43, 76, 117,
        -51, 115, -122, -91, 39, 26, -18, 122, 30, 90,     
    };
    byte[] hmac = Crypto.hmacSha1(key.getBytes(), val.getBytes());
    assertArrayEquals(expected, hmac);
  }
  
  @Test
  public void testHmacSha1Verify() throws Exception { 
    String key = "abcd1234";
    String val = "your mother is a hedgehog";
    byte[] expected = new byte[] {
        -21, 2, 47, -101, 9, -40, 18, 43, 76, 117,
        -51, 115, -122, -91, 39, 26, -18, 122, 30, 90,     
    };
    Crypto.hmacSha1Verify(key.getBytes(), val.getBytes(), expected);
  }
  
  
  @Test
  public void testHmacSha1VerifyTampered() throws Exception { 
    String key = "abcd1234";
    String val = "your mother is a hedgehog";
    byte[] expected = new byte[] {
        -21, 2, 47, -101, 9, -40, 18, 43, 76, 117,
        -51, 115, -122, -91, 39, 0, -18, 122, 30, 90,     
    };
    try {
      Crypto.hmacSha1Verify(key.getBytes(), val.getBytes(), expected);
      fail();
    } catch (GeneralSecurityException e) {
      // OK
    }
  }
  
  @Test
  public void testAes128Cbc() throws Exception { 
    byte[] key = Crypto.getRandomBytes(Crypto.CIPHER_KEY_LEN);
    for (byte i=0; i < 50; i++) {
      byte[] orig = new byte[i];
      for (byte j=0; j < i; j++) {
        orig[j] = j;
      }
      byte[] cipherText = Crypto.aes128cbcEncrypt(key, orig);
      byte[] plainText = Crypto.aes128cbcDecrypt(key, cipherText);
      assertArrayEquals("Array of length " + i, orig, plainText);
    }
  }
}
