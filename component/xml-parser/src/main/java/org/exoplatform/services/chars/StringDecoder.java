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

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Sep 16, 2006
 */
abstract class StringDecoder {

  private final String requestedCharsetName;
  
  protected StringDecoder(String requestedCharsetName) {
    this.requestedCharsetName = requestedCharsetName;
  }
  
  final String requestedCharsetName() {
    return requestedCharsetName;
  }
  
  abstract String charsetName();
  abstract char[] decode(byte[] ba, int off, int len);
  
  int scale(int len, float expansionFactor) {    
    return (int)(len * (double)expansionFactor);
  }
  
  char[] trim(char[] ca, int len) {
    if (len == ca.length) return ca;
    char[] tca = new char[len];
    System.arraycopy(ca, 0, tca, 0, len);
    return tca;
  }
}

