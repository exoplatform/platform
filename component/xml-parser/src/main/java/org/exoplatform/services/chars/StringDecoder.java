/***************************************************************************
 * Copyright 2003-2006 by eXoPlatform - All rights reserved.  *
 *    *
 **************************************************************************/
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

