/***************************************************************************
 * Copyright 2003-2006 by eXoPlatform - All rights reserved.  *
 *    *
 **************************************************************************/
package org.exoplatform.services.chars;

import java.util.ArrayList;
import java.util.List;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Aug 17, 2006
 */
public class StringTokenizer {
  
  private char[] regexes, addRegexes;
 
  public StringTokenizer(char[] reg, char [] addReg){
    this.regexes = reg;
    this.addRegexes = addReg;  
  }
  
  public List<String> split(String text){
    List<String> list  = new ArrayList<String>();
    int start = 0;
    int idx = 0;
    TypeRegex check;
    char c;
    while(idx < text.length()){
      c = text.charAt(idx);
      check = isSplitChar(c);
      if(check == TypeRegex.ADD_REGEX){
        if(start < idx) add(text.substring(start, idx), list);
        add(String.valueOf(c), list);        
        start = idx +1;
      }else if(check == TypeRegex.REGEX){  
        add(text.substring(start, idx), list);
        start = idx +1;              
      }
      idx++;
    }    
    if(start < text.length()){
      add(text.substring(start, text.length()), list);
    }
    return list;
  }
  
  private void add(String value, List<String> list){
    value  = value.trim();
    if(value.length() < 1) return;
    list.add(value);
  }
  
  private TypeRegex isSplitChar(char c){    
    for(char e : regexes) 
      if(e == c) return TypeRegex.ADD_REGEX;
    for(char e : addRegexes)      
      if(e == c) return TypeRegex.REGEX;
    return TypeRegex.NOT_REGEX;
  }
  
  public enum TypeRegex {  
    ADD_REGEX, NOT_REGEX, REGEX;
  }

  /*public static void main(String[] args) {
    SplitString split = new SplitString(
        new char[]{'\"', '\'', }, new char[]{' ','\t', '\b', '\f', '\r'});
    String text = " input name=\"hu  query\" size=15 maxlength=\"30\" style=\"width: 80px;\" type=\"text\' ";
    List<String> values = split.split(text);
    
    System.out.println("Mang la : ");
    
    if(values.size() > 0)  System.out.print("{ \"");
    for(int i = 0; i<values.size()-1 ; i++)
      System.out.print(values.get(i) +"\", \"");
    
    if(values.size() > 1) System.out.print(values.get(values.size()-1) +"\" }");
  }
  */

}