/**************************************************************************
 *    Copyright 2003-2006 by  eXo Platform SARL - All rights reserved.    *
 *                                                                        *
 **************************************************************************/
package org.exoplatform.services.parser.html;

import java.util.LinkedList;
import java.util.List;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Aug 3, 2006
 */

public abstract class HTMLNode implements org.exoplatform.services.parser.common.Node<Name> {

  protected NodeConfig config;
  protected char [] value ;

  protected HTMLNode parent = null;  
  protected List<HTMLNode> children ;
  
  public abstract StringBuilder buildValue(StringBuilder builder);
  
  protected HTMLNode(char[] value, NodeConfig config){
    this.value = value;
    this.config = config;
  }

  public char[] getValue() { return value;  }
  public void setValue(char[] value) { this.value = value; }

  public NodeConfig getConfig() { return config; }

  public boolean isNode(String nodeName){
    return config.name().toString().equalsIgnoreCase(nodeName);
  }
  public boolean isNode(Name n){ return config.name() == n; } 
  public Name getName(){  return config.name() ; }

  public HTMLNode getParent() { return parent;  }  
  public void setParent(HTMLNode parent) { this.parent = parent; }

  public void addChild(HTMLNode ele){
    if(config.end() == Tag.FORBIDDEN) return;    
    children.add(ele);
  }

  public List<HTMLNode> getChildren() {
    return children;
  }

  public List<HTMLNode> getChildrenNode(){
    List<HTMLNode> list = new LinkedList<HTMLNode>();
    if(children  == null) return list;
    for(HTMLNode ele : children){
      if(config.name() == Name.CONTENT || config.name() == Name.COMMENT) continue;
      list.add(ele);
    }
    return list;
  } 

  public String getTextValue(){
    StringBuilder builder = new StringBuilder();
    buildValue(builder);
    return builder.toString();
  } 
}
