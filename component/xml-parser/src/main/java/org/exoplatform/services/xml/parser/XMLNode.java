/***************************************************************************
 * Copyright 2001-2003 The eXoPlatform Studio        All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.services.xml.parser;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.services.chars.SpecChar;
import org.exoplatform.services.token.Node;
import org.exoplatform.services.token.TypeToken;

/**
 * Created by eXoPlatform Studio
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 13, 2006
 */
public class XMLNode implements Node<String> {

  protected XMLNode parent;   
  protected char [] value ;
  protected String name;  

  protected List<XMLNode> children;  
  private transient boolean isOpen = false;

  private transient int type = TypeToken.CONTENT ;
  
  public XMLNode(char [] value, String name, int type){  
    this.value = value;
    this.name = name;
    this.type = type;
    children = new ArrayList<XMLNode>(); 
    isOpen = type == TypeToken.TAG;
  }  

  public void setParent(XMLNode p){   this.parent = p; }

  public XMLNode getParent(){ return this.parent; }  

  public void addChild(XMLNode ele){
    children.add(ele);
    ele.setParent(this);
  }
  public List<XMLNode> getChildren() { return children; }
  
  public int getTotalChildren(){ return children.size(); }


  public char[] getValue() { return value; }

  public void setValue(char[] value) { this.value = value; }

  public String getName() { return name; }

  public void setName(String name) { this.name = name; }
  
  public boolean isNode(String n){
    if(name == null) return false;
    return name.equalsIgnoreCase(n);
  }

  boolean isOpen() { return isOpen; }

  void setIsOpen(boolean open) { isOpen = open; }
  
  int getType() { return type; }
  
  public String getTextValue(){
    return getTextValue(false);
  }
  
  public String getTextValue(boolean encode){
    StringBuilder builder = new StringBuilder();
    buildValue(builder, 0, encode);
    return builder.toString();
  }
  
  public XMLNode getChild(int i){ return children.get( i); }
  
  public String getNodeValue(){   return new String(value);  }
  
  public void buildValue(StringBuilder builder, boolean encode){
    buildValue(builder, 0, encode);
  }

  private void buildValue(StringBuilder builder, int tab, boolean encode){    
    if(tab > 0) builder.append(SpecChar.n);
    for(int  i = 0 ; i < tab ; i++) builder.append(SpecChar.s);
    if(type != TypeToken.CONTENT && type != TypeToken.COMMENT) builder.append('<');
    if(encode  && (type == TypeToken.CONTENT || type == TypeToken.COMMENT)) {
      value = Services.ENCODER.getRef().encode(value);
    }
    builder.append(value);
    
    if(type != TypeToken.CONTENT && type != TypeToken.COMMENT) builder.append('>');
   
    if(children == null ) return ;
    if(children.size() == 1  
        && children.get(0).getType() != TypeToken.TAG 
        && children.get(0).value.length < 20) tab = -3;
    for(XMLNode ele : children){
      ele.buildValue(builder, tab+2, encode);
    }
    if(type == TypeToken.TAG){
      if(tab > -1) builder.append(SpecChar.n);
      for(int  i = 0 ; i < tab ; i++) builder.append(SpecChar.s);
      builder.append('<').append('/').append(getName()).append('>');
    }
    return ;
  }


}
