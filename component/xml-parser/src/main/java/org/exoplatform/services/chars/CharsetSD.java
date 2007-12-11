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

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;

import sun.nio.cs.HistoricallyNamedCharset;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Sep 15, 2006
 */
class CharsetSD extends StringDecoder {
  
  private final Charset cs;
  private final CharsetDecoder cd;
  
  CharsetSD(Charset cs, String rcn) {
    super(rcn);
    this.cs = cs;
    this.cd = cs.newDecoder()
      .onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
  } 

  String charsetName() {
    if (cs instanceof HistoricallyNamedCharset)
      return ((HistoricallyNamedCharset)cs).historicalName();
    return cs.name();
  }

  char[] decode(byte[] ba, int off, int len) {
    int en = scale(len, cd.maxCharsPerByte());
    char[] ca = new char[en];
    if (len == 0) return ca;
    cd.reset();
    ByteBuffer bb = ByteBuffer.wrap(ba, off, len);
    CharBuffer cb = CharBuffer.wrap(ca);
    try {
      CoderResult cr = cd.decode(bb, cb, true);
      if (!cr.isUnderflow()) cr.throwException();
      cr = cd.flush(cb);
      if (!cr.isUnderflow()) cr.throwException();
    } catch (CharacterCodingException x) {       
      throw new Error(x);
    }
    return trim(ca, cb.position());
  }
  
  
}
