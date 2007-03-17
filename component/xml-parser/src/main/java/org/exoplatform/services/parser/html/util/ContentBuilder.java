/***************************************************************************
 * Copyright 2003-2006 by  eXo Platform SARL - All rights reserved.  *
 *    *
 **************************************************************************/
package org.exoplatform.services.parser.html.util;

import java.util.Calendar;
import java.util.List;

import org.exoplatform.services.parser.html.Group;
import org.exoplatform.services.parser.html.HTMLNode;
import org.exoplatform.services.parser.html.Name;
import org.exoplatform.services.parser.html.refs.RefsDecoder;
import org.exoplatform.services.parser.text.CharsUtil;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Aug 24, 2006
 */
public class ContentBuilder {

  private Name [] ignores = {Name.HEAD}; 
  private Name [] styles = {Name.FONT, Name.SUB, Name.SUP};
  private char [] end  = {'.', ':', '?'};
  private String month, year;  
  
  public ContentBuilder(){
    Calendar calendar = Calendar.getInstance();
    month = String.valueOf(calendar.get(Calendar.MONTH)+1);
    year = String.valueOf(calendar.get(Calendar.YEAR));
  }

  public synchronized boolean build(HTMLNode node, List<char[]> values, RefsDecoder decoder){
    if(!validate(node)) return true;
    List<HTMLNode> children = node.getChildren();   
    if(children == null) return true;
    for(HTMLNode child : children){
      if(child.isNode(Name.CONTENT)){
        char [] chars = child.getValue();
        if(decoder != null){
          chars = decoder.decode(chars);
          chars = CharsUtil.cutAndTrim(chars, 0, chars.length);
          if(chars.length < 1) continue;
          child.setValue(chars);
        }
        HTMLNode parent = getAncestor(child, Name.A);
        if(parent != null) {
          if(values.size()  < 1) return false;
          continue;
        }
        add(node, values, child.getValue());
      }else if(!child.isNode(Name.SCRIPT) && !child.isNode(Name.STYLE)){
        if(!build(child, values, decoder)) return false;
      }
    }  
    return true;
  }   
  
  private HTMLNode getAncestor(HTMLNode node, Name name){
    HTMLNode current  = node.getParent();    
    while(current != null){
      if(current.isNode(name)) return current;
      current = current.getParent();
    }   
    return null;
  }
 
  private boolean validate(HTMLNode node){
    for(Name name : ignores){
      if(node.getName() == name) return false;
    }
    return true;
  }

  private boolean isStyle(HTMLNode node){
    if(node.getConfig().type() == Group.Fontstyle.class) return true;
    if(node.getConfig().type() == Group.Phrase.class) return true;
    for(Name name : styles){
      if(node.getName() == name) return false;
    }
    return false;
  }

  private void add(HTMLNode node, List<char[]> values, char [] chars){   
    int length = countWord(chars);
    if(values.size() > 0 
        && isEnd(values.get(values.size()-1)) && length < 5) return;
    if(!isStyle(node) && !isEnd(chars)){
      char [] nText = new char[chars.length+1];
      System.arraycopy(chars, 0, nText, 0, nText.length-1);
      nText[chars.length] = '.';
      chars = nText;
    }
    if(length < 11 
       && CharsUtil.indexOf(chars, month.toCharArray(), 0) > -1 
       &&  CharsUtil.indexOf(chars, year.toCharArray(), 0) > -1) return;
    values.add(chars);
  }

  private boolean isEnd(char[] text){
    char c = text[text.length - 1]; 
    for(char ele : end){
      if(c == ele) return true;
    }
    return false;
  }

  private int countWord(char[] chars){
    int count = 0;
    boolean preEmpty = false;
    for(int i = 0 ; i < chars.length; i++){
      if(!preEmpty && Character.isSpaceChar(chars[i])){
        count++;
        preEmpty = true;
      }else{
        preEmpty = false;
      }
    }
    return count;
  }  

  public void setIgnores(Name[] ignores) { 
    this.ignores = ignores; 
  }

}
