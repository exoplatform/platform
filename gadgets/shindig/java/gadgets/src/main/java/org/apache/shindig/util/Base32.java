/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.shindig.util;

import org.apache.commons.codec.BinaryDecoder;
import org.apache.commons.codec.BinaryEncoder;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;

/**
 * Implements Base32 encoding.
 */
public class Base32 implements BinaryDecoder, BinaryEncoder {

  private static final StringEncoding ENCODER =
      new StringEncoding("0123456789abcdefghijklmnopqrstuv".toCharArray());

  public static byte[] encodeBase32(byte[] arg0) {
    return ENCODER.encode(arg0).getBytes(); 
  }
  
  public static byte[] decodeBase32(byte[] arg0) {
    return ENCODER.decode(new String(arg0)); 
  }
  
  @SuppressWarnings("unused")
  public byte[] decode(byte[] arg0) throws DecoderException {
    return decodeBase32(arg0);
  }

  @SuppressWarnings("unused")
  public byte[] encode(byte[] arg0) throws EncoderException {
    return encodeBase32(arg0);
  }

  public Object decode(Object object) throws DecoderException {
    if (!(object instanceof byte[])) {
      throw new DecoderException(
          "Parameter supplied to Base32 decode is not a byte[]");
    }
    return decodeBase32((byte[]) object);
  }

  public Object encode(Object object) throws EncoderException {
    if (!(object instanceof byte[])) {
      throw new EncoderException(
          "Parameter supplied to Base64 encode is not a byte[]");
    }
    return encodeBase32((byte[]) object);
  }
}
