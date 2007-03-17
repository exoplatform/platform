/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.services.parser.text;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          thuan.nhu@exoplatform.com
 * Sep 13, 2006  
 */
public final class CharsUtil {
  
  public static int indexOf(char[] value, char [] c, int start){
    boolean is = false;
    for(int i = start; i < value.length; i++){
      is = true;
      for(int j = 0; j< c.length; j++){
        if(i+j < value.length && c[j] ==  value[i+j]) continue;
        is = false;
        break;
      }      
      if(is) return i;
    }
    return -1;
  }
  
  public static char [] cutAndTrim(char [] data, int start, int end){
    int s = start;
    int e = end-1;
    while(s < end){
      if(!Character.isWhitespace(data[s])) break;
      s++;
    }
    while(e > start){
      if(!Character.isWhitespace(data[e])) break;
      e--;
    }
    e++;
    if(e <= s) return new char[0];
    char [] newChar = new char[e - s];
    System.arraycopy(data, s, newChar, 0, newChar.length);
    return newChar;
  }
  
  public static char [] cutBySpace(char [] data, int start){
    int e = start;
    while(e < data.length){
      if(Character.isWhitespace(data[e])) break;
      e++;
    }   
    if(e <= start) return new char[0];
    char [] newChar = new char[e-start];   
    System.arraycopy(data, start, newChar, 0, newChar.length);
    return newChar;
  }
  
  public static int indexOfIgnoreCase(char[] value, char [] c, int start){
    boolean is = false;
    for(int i = start; i < value.length; i++){
      is = true;
      for(int j = 0; j< c.length; j++){        
        if(Character.toLowerCase(c[j]) == Character.toLowerCase(value[i+j])) continue;
        is = false;
        break;
      }      
      if(is) return i;
    }
    return -1;
  }
  
  public static int indexOf(char[] value, char c, int start){
    for(int i = start; i < value.length; i++){
      if(c == value[i]) return i;
    }
    return -1;
  }
  
 /* public static void main(String args[]){
    String yahoo =" nhu dinh thuan nhu      dinh  ";
    String pattern = "dinh";
    System.out.println(indexOf(yahoo.toCharArray(), pattern.toCharArray(), 7));
    System.out.println(indexOf(yahoo.toCharArray(), 'd', 0));
    pattern = "DiNh";
    System.out.println(indexOf(yahoo.toCharArray(), pattern.toCharArray(), 7));
    System.out.println(indexOfIgnoreCase(yahoo.toCharArray(), pattern.toCharArray(), 7));
    
    char [] data = cutAndTrim(yahoo.toCharArray(), 19, 31);
    System.out.println("|"+new String(data)+"|");
    data = cutBySpace(yahoo.toCharArray(), 1);
    System.out.println("|"+new String(data)+"|");
  }*/
}
