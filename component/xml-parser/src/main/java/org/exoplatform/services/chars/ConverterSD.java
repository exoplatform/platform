/***************************************************************************
 * Copyright 2003-2006 by eXoPlatform - All rights reserved.  *
 *    *
 **************************************************************************/
package org.exoplatform.services.chars;

import java.io.CharConversionException;

import sun.io.ByteToCharConverter;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Sep 16, 2006
 */
@SuppressWarnings("deprecation")
class ConverterSD extends StringDecoder {
  
  private ByteToCharConverter btc;

  ConverterSD(ByteToCharConverter btc, String rcn) {
   super(rcn);
    this.btc = btc;
  }

  String charsetName() {
    return btc.getCharacterEncoding();
  }

  char[] decode(byte[] ba, int off, int len) {
    int en = scale(len, btc.getMaxCharsPerByte());
    char[] ca = new char[en];
    if (len == 0) return ca;
    btc.reset();
    int n = 0;
    try {
      n = btc.convert(ba, off, off + len, ca, 0, en);
      n += btc.flush(ca, btc.nextCharIndex(), en);
    } catch (CharConversionException x) {      
      n = btc.nextCharIndex();
    }
    return trim(ca, n);
  }
}
