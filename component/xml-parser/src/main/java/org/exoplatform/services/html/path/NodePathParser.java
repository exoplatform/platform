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
package org.exoplatform.services.html.path;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.services.html.HTMLNode;
import org.exoplatform.services.html.Name;
import org.exoplatform.services.html.path.NodePath.Index;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Aug 15, 2006
 */
public class NodePathParser {
  
  public synchronized static NodePath toPath(String text) throws Exception {
    return new NodePath(toIndexs(text));
  }
  
  public synchronized static Index [] toIndexs(String text) throws Exception {
    String[] split  = text.split("\\.");
    Index [] indexs = new Index[split.length];
    for(int i = 0;i < split.length; i++ ){      
     indexs[i] = toIndex(split[i]); 
    }
    return indexs;
  }
  
  public synchronized static NodePath toPath(HTMLNode element) {
    return new NodePath(toIndexs(element));
  }
  
  public synchronized static Index [] toIndexs(HTMLNode element) {
    HTMLNode parent = element.getParent();
    List<Index> list = new ArrayList<Index>();
    while(parent != null){
      list.add(toIndex(parent, element));
      element = parent;
      parent = element.getParent();
    }
    Index [] indexs = new Index[list.size()];
    for(int i = list.size() - 1; i > -1; i--){
      indexs[list.size() - i -1] = list.get(i);
    }
    return indexs;
  }
  
  private static Index toIndex(HTMLNode parent, HTMLNode element){
    List<HTMLNode> children  = parent.getChildren();
    int count = -1;
    for(HTMLNode ele : children){
      if(ele.getName() == element.getName()){
        count++;
        if(ele == element) break;
      }
    }
    return new Index(element.getName(), count);
  }
  
  static Index toIndex(String name) throws Exception {
    int idx = 0;   
    int squareBracketStart = name.indexOf('[');
    int squareBracketEnd  = name.indexOf(']');
    if(squareBracketStart < 0 && squareBracketEnd > -1)  throw new Exception (name + " is invalid ");
    if(squareBracketStart > -1 && squareBracketEnd < 0)  throw new Exception (name + " is invalid ");
    if(squareBracketStart < 0 && squareBracketEnd < 0)   {
      return new Index(Name.valueOf(name.toUpperCase()), 0);    
    }
    String sIDX = name.substring(squareBracketStart+1, squareBracketEnd).trim();
    name  = name.substring(0, squareBracketStart);
    idx = Integer.parseInt(sIDX);
    if(idx < 0) throw new IndexOutOfBoundsException();
    return new Index(Name.valueOf(name.toUpperCase()), idx);
  }
  
}
