/*
 * Copyright 2004-2006 The  eXo Platform SARL        All rights reserved.
 *
 * Created on January 24, 2006, 7:49 PM
 */

package org.exoplatform.services.parser.text;

/**
 *
 * @author nhuthuan
 * Email: nhudinhthuan@yahoo.com
 */
public class SpecChar {
  
  public static char s = ' ';
  public static char t = '\t';
  public static char n = '\n';
  public static char b = '\b';
  public static char f = '\f';
  public static char r = '\r';
  
  public static char END_TAG = '/', OPEN_TAG = '<', CLOSE_TAG = '>' ; 
  
  public static char  HYPHEN = '-', QUESTION_MASK = '?',  PUNCTUATION_MASK='!';
    
  public static int findWhiteSpace(String value, int start){    
    for(int i=start; i<value.length(); i++){  
      if(Character.isWhitespace(value.charAt(i))) return i;      
    }       
    return value.length();
  }    
}
