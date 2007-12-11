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
