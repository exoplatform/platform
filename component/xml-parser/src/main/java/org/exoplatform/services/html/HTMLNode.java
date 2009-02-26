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
package org.exoplatform.services.html;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.exoplatform.services.html.parser.HTML;
import org.exoplatform.services.token.Node;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Aug 3, 2006
 */
public abstract class HTMLNode implements Node<Name> {

  protected char [] value ;
  protected Name name;

  protected HTMLNode parent = null;  
  protected List<HTMLNode> children ;

  protected HTMLNode(char[] value, Name name){
    this.value = value;
    this.name = name;
  }

  public char[] getValue() { return value;  }
  public void setValue(char[] value) { this.value = value; }

  public NodeConfig getConfig() { 
    return HTML.getConfig(name); 
  }

  public boolean isNode(String nodeName){
    return name.toString().equalsIgnoreCase(nodeName);
  }
  public boolean isNode(Name n){ return name == n; } 
  public Name getName(){  return name ; }
  public void setName(Name name){  this.name = name; }

  public HTMLNode getParent() { return parent;  }  
  public void setParent(HTMLNode parent) { this.parent = parent; }

  public void addChild(HTMLNode ele){
    NodeConfig config = getConfig();
    if(config.end() == Tag.FORBIDDEN) return;
    if(children == null) children = new ArrayList<HTMLNode>(5) ;
    children.add(ele);
  }

  public List<HTMLNode> getChildren() { return children; }

  public List<HTMLNode> getChildrenNode(){
    List<HTMLNode> list = new LinkedList<HTMLNode>();
    if(children  == null) return list;
    for(HTMLNode ele : children){
      if(name == Name.CONTENT || name == Name.COMMENT) continue;
      list.add(ele);
    }
    return list;
  } 

  public String getTextValue(){
    StringBuilder builder = new StringBuilder();
    buildValue(builder);
    return builder.toString();
  }  
  
  abstract public StringBuilder buildValue(StringBuilder builder);
}
