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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Routines for producing hashes.
 */
public class HashUtil {
  /**
   * Produces a checksum for the given input data.
   *
   * @param data
   * @return The checksum.
   */
  public static String checksum(byte[] data) {
    MessageDigest md;
    try {
      md = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException noMD5) {
      try {
        md = MessageDigest.getInstance("SHA");
      } catch (NoSuchAlgorithmException noSha) {
        throw new RuntimeException("No suitable MessageDigest found!");
      }
    }
    byte[] hash = md.digest(data);
    // Convert to hex. possibly change to base64 in the future for smaller
    // signatures.
    StringBuilder hexString = new StringBuilder(hash.length * 2 + 2);
    for (byte b : hash) {
      hexString.append(Integer.toHexString(0xFF & b));
    }
    return hexString.toString();
  }
}
