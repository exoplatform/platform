/***************************************************************************
 * Copyright 2004-2006 The  eXo Platform SARL All rights reserved.  *
 **************************************************************************/
package org.exoplatform.services.parser.html.refs;

/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 8, 2006
 */
class CharsSequence {
  
  private int index;
  
  private char [] values;
  
  CharsSequence(int max){
    values = new char[max];
    index = 0;
  }
  
  void append(char c){
    if(index >= values.length) return;
    values[index] = c;
    index++;
  }
  
  void append(String string){
    char [] cs = string.toCharArray();
    for(int i = 0; i < cs.length; i++){
      if(index >= values.length) return;
      values[index] = cs[i];
      index++;
    }
  }

  char[] getValues() {
    char[] newValues = new char[index];
    System.arraycopy(values, 0, newValues, 0, index);
    return newValues;
  }  
}
