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

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple implementation of BlobCrypter.
 */
public class BasicBlobCrypter implements BlobCrypter {

  // Labels for key derivation
  private static final byte CIPHER_KEY_LABEL = 0;
  private static final byte HMAC_KEY_LABEL = 1;
  
  /** Key used for time stamp (in seconds) of data */
  public static final String TIMESTAMP_KEY = "t";
  
  /** minimum length of master key */
  public static final int MASTER_KEY_MIN_LEN = 16;
  
  /** allow three minutes for clock skew */
  private static final long CLOCK_SKEW_ALLOWANCE = 180;
  
  private static final String UTF8 = "UTF-8";
  
  public TimeSource timeSource = new TimeSource();  
  private byte[] cipherKey;
  private byte[] hmacKey;
  
  /**
   * Builds a BlobCrypter from the specified master key
   * 
   * @param masterKey
   */
  public BasicBlobCrypter(byte[] masterKey) {
    if (masterKey.length < MASTER_KEY_MIN_LEN) {
      throw new IllegalArgumentException("Master key needs at least " +
          MASTER_KEY_MIN_LEN + " bytes");
    }
    cipherKey = deriveKey(CIPHER_KEY_LABEL, masterKey, Crypto.CIPHER_KEY_LEN);
    hmacKey = deriveKey(HMAC_KEY_LABEL, masterKey, 0);
  }

  /**
   * Generates unique keys from a master key.
   * 
   * @param label type of key to derive
   * @param masterKey master key
   * @param len length of key needed, less than 20 bytes.  20 bytes are 
   * returned if len is 0.  
   * 
   * @return a derived key of the specified length
   */
  private byte[] deriveKey(byte label, byte[] masterKey, int len) {
    byte[] base = Crypto.concat(new byte[] { label }, masterKey);
    byte[] hash = DigestUtils.sha(base);
    if (len == 0) {
      return hash;
    }
    byte[] out = new byte[len];
    System.arraycopy(hash, 0, out, 0, out.length);
    return out;
  }
  
  /* (non-Javadoc)
   * @see org.apache.shindig.util.BlobCrypter#wrap(java.util.Map)
   */
  public String wrap(Map<String, String> in)
  throws BlobCrypterException {
    if (in.containsKey(TIMESTAMP_KEY)) {
      throw new IllegalArgumentException("No 't' keys allowed for BlobCrypter");
    }
    try {
      byte[] encoded = serializeAndTimestamp(in);
      byte[] cipherText = Crypto.aes128cbcEncrypt(cipherKey, encoded);
      byte[] hmac = Crypto.hmacSha1(hmacKey, cipherText);
      byte[] b64 = Base64.encodeBase64(Crypto.concat(cipherText, hmac));
      return new String(b64, UTF8);
    } catch (UnsupportedEncodingException e) {
      throw new BlobCrypterException(e);
    } catch (GeneralSecurityException e) {
      throw new BlobCrypterException(e);
    }
  }

  /**
   * Encode the input for transfer.  We use something a lot like HTML form
   * encodings.  The time stamp is in seconds since the epoch.
   */
  private byte[] serializeAndTimestamp(Map<String, String> in)
  throws UnsupportedEncodingException {
    StringBuilder sb = new StringBuilder();

    for (Map.Entry<String, String> stringStringEntry : in.entrySet()) {
      Map.Entry<String, String> val = stringStringEntry;
      sb.append(URLEncoder.encode(val.getKey(), UTF8));
      sb.append('=');
      sb.append(URLEncoder.encode(val.getValue(), UTF8));
      sb.append('&');
    }
    sb.append(TIMESTAMP_KEY);
    sb.append('=');
    sb.append(timeSource.currentTimeMillis()/1000);
    return sb.toString().getBytes(UTF8);
  }

  /* (non-Javadoc)
   * @see org.apache.shindig.util.BlobCrypter#unwrap(java.lang.String, int)
   */
  public Map<String, String> unwrap(String in, int maxAgeSec)
  throws BlobCrypterException {
    try {
      byte[] bin = Base64.decodeBase64(in.getBytes());
      byte[] hmac = new byte[Crypto.HMAC_SHA1_LEN];
      byte[] cipherText = new byte[bin.length-Crypto.HMAC_SHA1_LEN];
      System.arraycopy(bin, 0, cipherText, 0, cipherText.length);
      System.arraycopy(bin, cipherText.length, hmac, 0, hmac.length);
      Crypto.hmacSha1Verify(hmacKey, cipherText, hmac);
      byte[] plain = Crypto.aes128cbcDecrypt(cipherKey, cipherText);
      Map<String, String> out = deserialize(plain);
      checkTimestamp(out, maxAgeSec);
      return out;
    } catch (GeneralSecurityException e) {
      throw new BlobCrypterException("Invalid token signature", e);
    } catch (ArrayIndexOutOfBoundsException e) {
      throw new BlobCrypterException("Invalid token format", e);
    } catch (UnsupportedEncodingException e) {
      throw new BlobCrypterException(e);
    }

  }

  private Map<String, String> deserialize(byte[] plain)
  throws UnsupportedEncodingException {
    String base = new String(plain, UTF8);
    String[] items = base.split("[&=]");
    Map<String, String> map = new HashMap<String, String>();
    for (int i=0; i < items.length; ) {
      String key = URLDecoder.decode(items[i++], UTF8);
      String val = URLDecoder.decode(items[i++], UTF8);
      map.put(key, val);
    }
    return map;
  }
  
  /**
   * We allow a few minutes on either side of the validity window to account
   * for clock skew.
   */
  private void checkTimestamp(Map<String, String> out, int maxAge)
  throws BlobExpiredException {
    long origin = Long.parseLong(out.get(TIMESTAMP_KEY));
    long minTime = origin - CLOCK_SKEW_ALLOWANCE;
    long maxTime = origin + maxAge + CLOCK_SKEW_ALLOWANCE;
    long now = timeSource.currentTimeMillis()/1000;
    if (!(minTime < now && now < maxTime)) {
      throw new BlobExpiredException(minTime, now, maxTime);
    }    
  }

}
