/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.services.html.parser;

import java.util.LinkedList;

import org.exoplatform.services.chars.SpecChar;
import org.exoplatform.services.html.HTMLNode;
import org.exoplatform.services.html.Name;
import org.exoplatform.services.html.NodeConfig;
import org.exoplatform.services.html.Tag;
import org.exoplatform.services.token.TypeToken;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          thuan.nhu@exoplatform.com
 * Sep 14, 2006  
 */
public class NodeImpl extends HTMLNode {
  
  private transient boolean isOpen = false;
  
  private transient int type = TypeToken.CONTENT ;
  
  public NodeImpl(char[] value, Name name){ 
    super(value, name);
  }

  public NodeImpl(char[] value, Name name, int type){
    super(value, name);
    this.type = type;
    children = new LinkedList<HTMLNode>();
    NodeConfig config = HTML.getConfig(name);
    if(config.end() != Tag.FORBIDDEN && type == TypeToken.TAG) isOpen = true;    
  }
  
  public boolean isOpen() { return isOpen; }
  
  public void setIsOpen(boolean open) { isOpen = open; }
    
  public int getType() { return type; }
  
  public StringBuilder buildValue(StringBuilder builder){
    if(builder.length() > 0) builder.append(SpecChar.n);
    if(name != Name.CONTENT && name != Name.COMMENT) builder.append('<');
    if(type == TypeToken.CLOSE) builder.append('/');
    builder.append(value);
    if(name != Name.CONTENT && name != Name.COMMENT) builder.append('>');
    if(type == TypeToken.CLOSE || getConfig().hidden())  return builder;

    if(children == null ) return builder;
    for(HTMLNode ele : children){
      ele.buildValue(builder);
    }
    if(getConfig().end() != Tag.FORBIDDEN){
      builder.append(SpecChar.n).append('<').append('/').append(getName()).append('>');
    }
    return builder;
  }
  
 
}
