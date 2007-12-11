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

import org.exoplatform.services.html.HTMLDocument;
import org.exoplatform.services.html.HTMLNode;
import org.exoplatform.services.html.parser.HTMLParser;
import org.exoplatform.services.html.path.NodePath.Index;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Aug 15, 2006
 */
public class NodePathUtil {
  
  public synchronized static NodePath [] toNodePath(String[] paths) throws Exception {
    NodePath [] values = new NodePath[paths.length];
    for(int i = 0; i < paths.length; i++){
      values[i] = NodePathParser.toPath(paths[i]);
    }
    return values;
  }
    
  public synchronized static void remove(HTMLNode root, NodePath[] paths){
    if(paths == null) return;
    List<HTMLNode> list = new ArrayList<HTMLNode>();
    for(NodePath path : paths){
      HTMLNode element = lookFor(root, path);
      if (element != null) list.add(element);      
    }
    for(HTMLNode element : list){
      HTMLNode parent  = element.getParent();
      parent.getChildren().remove(element);
    }
  } 
  
  public synchronized static HTMLDocument create(HTMLNode root, NodePath[] paths){
    HTMLNode html = HTMLParser.clone(root);
    for(NodePath path : paths){
      HTMLNode element = lookFor(root, path);
      if (element != null)  html.addChild(element);
    }
    HTMLDocument document  = new HTMLDocument();
    document.setRoot(html);
    return document;
  }
  
  public synchronized static HTMLNode lookFor(HTMLNode root, NodePath path) {
    if(path == null) return null;
    Index [] indexs = path.getIndexs();
    List<HTMLNode> children = root.getChildren();
    if(children == null) return null;
    int count;
    for(int i = 0; i<indexs.length - 1; i++){
      count = 0;
      for(int j = 0; j < children.size(); j++){
        if(children.get(j).isNode(indexs[i].getName())){
          if(indexs[i].getIdx() == count){          
            root = children.get(j); 
            children = root.getChildren();
            break;
          }
          count++;
        }
      }      
    }
    count = 0;
    for(int j = 0; j < children.size(); j++){
      if(children.get(j).isNode(indexs[indexs.length - 1].getName())){
        if(indexs[indexs.length - 1].getIdx() == count) return children.get(j);         
        count++;
      }
    }    
    return  null ;
  }
  
}
