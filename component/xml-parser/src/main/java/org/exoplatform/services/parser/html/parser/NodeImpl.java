/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.services.parser.html.parser;

import java.util.LinkedList;

import org.exoplatform.services.parser.common.TypeToken;
import org.exoplatform.services.parser.html.HTMLNode;
import org.exoplatform.services.parser.html.Name;
import org.exoplatform.services.parser.html.NodeConfig;
import org.exoplatform.services.parser.html.Tag;
import org.exoplatform.services.parser.text.SpecChar;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          thuan.nhu@exoplatform.com
 * Sep 14, 2006  
 */
public class NodeImpl extends HTMLNode {
  
  private transient boolean isOpen = false;
  
  private transient TypeToken type = TypeToken.CONTENT ;
  
  public NodeImpl(char[] value, NodeConfig config){ 
    super(value, config);
  }

  public NodeImpl(char[] value, NodeConfig config, TypeToken type){
    super(value, config);
    this.type = type;
    children = new LinkedList<HTMLNode>();
    if(config.end() != Tag.FORBIDDEN && type == TypeToken.TAG) isOpen = true;    
  }
  
  public boolean isOpen() { return isOpen; }
  
  public void setIsOpen(boolean open) { isOpen = open; }
    
  public TypeToken getType() { return type; }
  
  public StringBuilder buildValue(StringBuilder builder){
    if(builder.length() > 0) builder.append(SpecChar.n);
    if(config.name() != Name.CONTENT && config.name() != Name.COMMENT) builder.append('<');
    if(type == TypeToken.CLOSE) builder.append('/');
    builder.append(value);
    if(config.name() != Name.CONTENT && config.name() != Name.COMMENT) builder.append('>');
    if(type == TypeToken.CLOSE || config.hidden())  return builder;

    if(children == null ) return builder;
    for(HTMLNode ele : children){
      ele.buildValue(builder);
    }
    if(config.end() != Tag.FORBIDDEN){
      builder.append(SpecChar.n).append('<').append('/').append(getName()).append('>');
    }
    return builder;
  }
  
 
}
