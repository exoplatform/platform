/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
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
