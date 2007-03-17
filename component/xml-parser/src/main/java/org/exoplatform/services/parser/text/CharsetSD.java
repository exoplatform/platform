/***************************************************************************
 * Copyright 2003-2006 by  eXo Platform SARL - All rights reserved.  *
 *    *
 **************************************************************************/
package org.exoplatform.services.parser.text;

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
