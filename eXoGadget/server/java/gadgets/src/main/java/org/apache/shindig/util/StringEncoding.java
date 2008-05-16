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

import java.util.Arrays;
import java.util.TreeSet;

/**
 * Utility class for encoding strings to and from byte arrays.
 */
public class StringEncoding {
  private final char[] DIGITS;
  private final int SHIFT;
  private final int MASK;

  /** Creates a new encoding based on the supplied set of digits. */
  public StringEncoding(final char[] userDigits) {
    TreeSet<Character> t = new TreeSet<Character>();
    for (char c : userDigits) {
      t.add(c);
    }
    char[] digits = new char[t.size()];
    int i = 0;
    for (char c : t) {
      digits[i++] = c;
    }
    this.DIGITS = digits;
    this.MASK = digits.length - 1;
    this.SHIFT = Integer.numberOfTrailingZeros(MASK+1);
    if ((MASK+1) != (1<<SHIFT) || digits.length >= 256) {
      throw new AssertionError(Arrays.toString(digits));
    }
  }
  
  /** Returns the given bytes in their encoded form. */
  public String encode(byte[] data) {
    if (data.length == 0) {
      return "";
    }
    StringBuilder result =
      new StringBuilder(1 + data.length * 8 / DIGITS.length);
    int buffer = data[0];
    int next = 1;
    int bitsLeft = 8;
    while (bitsLeft > 0 || next < data.length) {
      if (bitsLeft < SHIFT) {
        if (next < data.length) {
          buffer <<= 8;
          buffer |= (data[next++] & 0xff);
          bitsLeft += 8;
        } else {
          int pad = SHIFT - bitsLeft;
          buffer <<= pad;
          bitsLeft += pad;
        }
      }
      int index = MASK & (buffer >> (bitsLeft - SHIFT));
      bitsLeft -= SHIFT;
      result.append(DIGITS[index]);
    }
    return result.toString();
  }
  
  /** Decodes the given encoded string and returns the original raw bytes. */
  public byte[] decode(String encoded) {
    if (encoded.length() == 0) {
      return new byte[0];
    }
    int encodedLength = encoded.length();
    int outLength = encodedLength * SHIFT / 8;
    byte[] result = new byte[outLength];
    int buffer = 0;
    int next = 0;
    int bitsLeft = 0;
    for (char c : encoded.toCharArray()) {
      buffer <<= SHIFT;
      buffer |= Arrays.binarySearch(DIGITS, c) & MASK;
      bitsLeft += SHIFT;
      if (bitsLeft >= 8) {
        result[next++] = (byte) (buffer >> (bitsLeft - 8));
        bitsLeft -= 8;
      }
    }
    assert next == outLength && bitsLeft < SHIFT;
    return result;
  }  
}
