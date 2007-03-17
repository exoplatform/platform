/***************************************************************************
 * Copyright 2001-2003 The  eXo Platform SARL        All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.services.parser.xml;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.services.parser.common.Node;
import org.exoplatform.services.parser.common.TypeToken;
import org.exoplatform.services.parser.text.SpecChar;

/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 13, 2006
 */
public class XMLNode implements Node {

  protected XMLNode parent;   
  protected char [] value ;
  protected String name;  

  protected List<XMLNode> children;  
  private transient boolean isOpen = false;

  private transient TypeToken type = TypeToken.CONTENT ;

  public XMLNode(char [] value, String name, TypeToken type){  
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
  
  TypeToken getType() { return type; }
  
  public String getTextValue(){
    StringBuilder builder = new StringBuilder();
    buildValue(builder, 0);
    return builder.toString();
  }
  
  public XMLNode getChild(int i){ return children.get( i); }
  
  public String getNodeValue(){   return new String(value);  }
  
  public void buildValue(StringBuilder builder){
    buildValue(builder, 0);
  }

  private void buildValue(StringBuilder builder, int tab){    
    if(tab > 0) builder.append(SpecChar.n);
    for(int  i = 0 ; i < tab ; i++) builder.append(SpecChar.s);
    if(type != TypeToken.CONTENT && type != TypeToken.COMMENT) builder.append('<');
    builder.append(value);
    if(type != TypeToken.CONTENT && type != TypeToken.COMMENT) builder.append('>');
   
    if(children == null ) return ;
    if(children.size() == 1  
        && children.get(0).getType() != TypeToken.TAG 
        && children.get(0).value.length < 20) tab = -3;
    for(XMLNode ele : children){
      ele.buildValue(builder, tab+2);
    }
    if(type == TypeToken.TAG){
      if(tab > -1) builder.append(SpecChar.n);
      for(int  i = 0 ; i < tab ; i++) builder.append(SpecChar.s);
      builder.append('<').append('/').append(getName()).append('>');
    }
    return ;
  }


}
