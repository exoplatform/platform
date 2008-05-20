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

import org.apache.commons.codec.binary.Hex;

import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Cryptographic utility functions.
 */
public class Crypto {
  
  /** 
   * Use this random number generator instead of creating your own.  This is
   * thread-safe.
   */
  public static SecureRandom rand = new SecureRandom();
  
  /**
   * HMAC algorithm to use
   */
  private final static String HMAC_TYPE = "HMACSHA1";
  
  /** 
   * minimum safe length for hmac keys (this is good practice, but not 
   * actually a requirement of the algorithm
   */
  private final static int MIN_HMAC_KEY_LEN = 8;
  
  /**
   * Encryption algorithm to use
   */
  private final static String CIPHER_TYPE = "AES/CBC/PKCS5Padding";
  
  private final static String CIPHER_KEY_TYPE = "AES";
  
  /**
   * Use keys of this length for encryption operations
   */
  public final static int CIPHER_KEY_LEN = 16;
  
  private static int CIPHER_BLOCK_SIZE = 16;
  
  /**
   * Length of HMAC SHA1 output
   */
  public final static int HMAC_SHA1_LEN = 20;
  
  // everything is static, no instantiating this class
  private Crypto() { 
  }

  /**
   * Gets a hex encoded random string.
   * 
   * @param numBytes number of bytes of randomness.
   */
  public static String getRandomString(int numBytes) {
    return new String(Hex.encodeHex(getRandomBytes(numBytes)));
  }
  
  /**
   * Returns strong random bytes.
   * 
   * @param numBytes number of bytes of randomness
   */
  public static byte[] getRandomBytes(int numBytes) {
    byte[] out = new byte[numBytes];
    rand.nextBytes(out);
    return out;
  }
  
  /**
   * HMAC sha1
   * 
   * @param key the key must be at least 8 bytes in length.
   * @param in byte array to HMAC.
   * @return the hash
   * 
   * @throws GeneralSecurityException
   */
  public static byte[] hmacSha1(byte[] key, byte[] in) throws GeneralSecurityException {
    if (key.length < MIN_HMAC_KEY_LEN) {
      throw new GeneralSecurityException("HMAC key should be at least "
          + MIN_HMAC_KEY_LEN + " bytes.");
    }
    Mac hmac = Mac.getInstance(HMAC_TYPE);
    Key hmacKey = new SecretKeySpec(key, HMAC_TYPE);
    hmac.init(hmacKey);
    hmac.update(in);
    return hmac.doFinal();
  }
  
  /**
   * Verifies an HMAC SHA1 hash.  Throws if the verification fails.
   * 
   * @param key
   * @param in
   * @param expected
   * @throws GeneralSecurityException
   */
  public static void hmacSha1Verify(byte[] key, byte[] in, byte[] expected)
  throws GeneralSecurityException {
    Mac hmac = Mac.getInstance(HMAC_TYPE);
    Key hmacKey = new SecretKeySpec(key, HMAC_TYPE);
    hmac.init(hmacKey);
    hmac.update(in);
    byte actual[] = hmac.doFinal();
    if (actual.length != expected.length) {
      throw new GeneralSecurityException("HMAC verification failure");
    }
    for (int i=0; i < actual.length; i++) {
      if (actual[i] != expected[i]) {
        throw new GeneralSecurityException("HMAC verification failure");        
      }
    }
  }
  
  /**
   * AES-128-CBC encryption.  The IV is returned as the first 16 bytes
   * of the cipher text.
   * 
   * @param key
   * @param plain
   * 
   * @return the IV and cipher text
   * 
   * @throws GeneralSecurityException
   */
  public static byte[] aes128cbcEncrypt(byte[] key, byte[] plain)
  throws GeneralSecurityException {
    Cipher cipher = Cipher.getInstance(CIPHER_TYPE);
    Key cipherKey = new SecretKeySpec(key, CIPHER_KEY_TYPE);
    byte iv[] = getRandomBytes(cipher.getBlockSize());
    IvParameterSpec ivSpec = new IvParameterSpec(iv);
    cipher.init(Cipher.ENCRYPT_MODE, cipherKey, ivSpec);
    byte[] cipherText = cipher.doFinal(plain);
    return concat(iv, cipherText);
  }

  /**
   * AES-128-CBC decryption.  The IV is assumed to be the first 16 bytes
   * of the cipher text.
   * 
   * @param key
   * @param cipherText
   * 
   * @return the plain text
   * 
   * @throws GeneralSecurityException
   */
  public static byte[] aes128cbcDecrypt(byte[] key, byte[] cipherText)
  throws GeneralSecurityException {
    byte iv[] = new byte[CIPHER_BLOCK_SIZE];
    System.arraycopy(cipherText, 0, iv, 0, iv.length);
    return aes128cbcDecryptWithIv(key, iv, cipherText, iv.length);
  }
  
  /**
   * AES-128-CBC decryption with a particular IV.
   * 
   * @param key decryption key
   * @param iv initial vector for decryption
   * @param cipherText cipher text to decrypt
   * @param offset offset into cipher text to begin decryption
   * 
   * @return the plain text
   * 
   * @throws GeneralSecurityException
   */
  public static byte[] aes128cbcDecryptWithIv(byte[] key, byte[] iv,
      byte[] cipherText, int offset) throws GeneralSecurityException {
    Cipher cipher = Cipher.getInstance(CIPHER_TYPE);
    Key cipherKey = new SecretKeySpec(key, CIPHER_KEY_TYPE);
    IvParameterSpec ivSpec = new IvParameterSpec(iv);
    cipher.init(Cipher.DECRYPT_MODE, cipherKey, ivSpec);
    return cipher.doFinal(cipherText, offset, cipherText.length-offset);
  }

  /**
   * Concatenate two byte arrays.
   */
  public static byte[] concat(byte[] a, byte[] b) {
    byte[] out = new byte[a.length + b.length];
    int cursor = 0;
    System.arraycopy(a, 0, out, cursor, a.length);
    cursor += a.length;
    System.arraycopy(b, 0, out, cursor, b.length);
    return out;
  }
}
