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

import java.lang.ref.SoftReference;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Sep 15, 2006
 */
public class CharsDecoder {

  private static ThreadLocal<SoftReference<StringDecoder>> decoder = 
                                    new ThreadLocal<SoftReference<StringDecoder>>();

  private static StringDecoder deref(ThreadLocal<SoftReference<StringDecoder>> tl) {
    SoftReference<StringDecoder> sr = tl.get();
    if (sr == null) return null;
    return sr.get();
  }

  private static void set(ThreadLocal<SoftReference<StringDecoder>> tl, StringDecoder ob) {
    tl.set(new SoftReference<StringDecoder>(ob));
  }

  @SuppressWarnings("deprecation")
  public static char[] decode(String charsetName, byte[] ba, int off, int len) throws Exception {
    StringDecoder sd = deref(decoder);
    String csn = (charsetName == null) ? "UTF-8" : charsetName;

    if ((sd == null) || !(csn.equals(sd.requestedCharsetName()) || csn.equals(sd.charsetName()))) {
      sd = null;
      try {
        Charset cs = lookupCharset(csn);
        if (cs != null) sd = new CharsetSD(cs, csn);
        else sd = null;
      } catch (IllegalCharsetNameException x) {
      }     
      if (sd == null)
        sd = new ConverterSD(sun.io.ByteToCharConverter.getConverter(csn), csn);
      set(decoder, sd);
    }
    return sd.decode(ba, off, len);
  }  

  private static Charset lookupCharset(String csn) {
    if (Charset.isSupported(csn)) {
      try {
        return Charset.forName(csn);
      } catch (Exception x) {
        throw new Error(x);
      }
    }
    return null;
  }

}
