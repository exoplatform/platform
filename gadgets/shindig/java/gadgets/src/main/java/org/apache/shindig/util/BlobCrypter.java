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

import java.util.Map;

/**
 * Utility interface for managing signed, encrypted, and time stamped blobs.
 * Blobs are made up of name/value pairs.  Time stamps are automatically
 * included and checked.
 * 
 * Thread safe.
 */
public interface BlobCrypter {

  /**
   * Time stamps, encrypts, and signs a blob.
   * 
   * @param in name/value pairs to encrypt
   * @return a base64 encoded blob
   * 
   * @throws BlobCrypterException
   */
  public String wrap(Map<String, String> in) throws BlobCrypterException;

  /**
   * Unwraps a blob.
   * 
   * @param in blob
   * @param maxAgeSec maximum age for the blob
   * @return the name/value pairs, including the origin timestamp.
   * 
   * @throws BlobExpiredException if the blob is too old to be accepted.
   * @throws BlobCrypterException if the blob can't be decoded.
   */
  public Map<String, String> unwrap(String in, int maxAgeSec)
      throws BlobCrypterException;

}
