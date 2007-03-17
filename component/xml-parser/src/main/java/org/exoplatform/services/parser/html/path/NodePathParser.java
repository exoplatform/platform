/***************************************************************************
 * Copyright 2003-2006 by  eXo Platform SARL - All rights reserved.  *
 *    *
 **************************************************************************/
package org.exoplatform.services.parser.html.path;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.services.parser.html.HTMLNode;
import org.exoplatform.services.parser.html.Name;
import org.exoplatform.services.parser.html.path.NodePath.Index;

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
