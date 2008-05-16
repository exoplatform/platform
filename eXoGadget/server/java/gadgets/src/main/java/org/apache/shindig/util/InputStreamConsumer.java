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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * Used to consume entire input streams and transform them into data buffers.
 * These are all blocking routines and should never be called from a thread
 * that will cause deadlock.
 */
public class InputStreamConsumer {

  /**
   * Consumes the entire contents of the stream. Only safe to use if you are
   * sure that you're consuming a fixed-size buffer.
   * @param is
   * @return The contents of the stream.
   * @throws IOException on stream reading error.
   */
  public static byte[] readToByteArray(InputStream is) throws IOException {
    return readToByteArray(is, Integer.MAX_VALUE);
  }

  /**
   * Reads at most maxBytes bytes from the stream.
   * @param is
   * @param maxBytes
   * @return The bytes that were read
   * @throws IOException
   */
  public static byte[] readToByteArray(InputStream is, int maxBytes)
      throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    byte[] chunk = new byte[8192];
    int chunkSize;
    while (out.size() < maxBytes && (chunkSize = is.read(chunk)) != -1) {
      out.write(chunk, 0, chunkSize);
    }
    return out.toByteArray();
  }

  /**
   * Loads content from the given input stream as a UTF-8-encoded string.
   * Use only when you're sure of the finite length of the input stream.
   * If you're not sure, use {@code readToString(InputStream, maxBytes)}.
   *
   * @param is
   * @return The contents of the stream.
   * @throws IOException on stream reading error.
   */
  public static String readToString(InputStream is) throws IOException {
    return readToString(is, Integer.MAX_VALUE);
  }

  /**
   * Loads content from the given input stream as a UTF-8-encoded string.
   *
   * @param is
   * @return The contents of the stream.
   * @throws IOException on stream reading error.
   */
  public static String readToString(InputStream is, int maxBytes)
      throws IOException {
    try {
      return new String(readToByteArray(is, maxBytes), "UTF-8");
    } catch (UnsupportedEncodingException e) {
      // UTF-8 is required by the Java spec.
      throw new RuntimeException("UTF-8 not supported!", e);
    }
  }

  /**
   * Consumes all of is and sends it to os. This is not the same as
   * Piped Input / Output streams because it reads the entire input first.
   * This means that you won't get deadlock, but it also means that this is
   * not necessarily suitable for normal piping tasks. Use a piped stream for
   * that sort of work.
   *
   * @param is
   * @param os
   * @throws IOException
   */
  public static void pipe(InputStream is, OutputStream os) throws IOException {
    os.write(readToByteArray(is));
  }
}
