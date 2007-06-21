/***************************************************************************
 * Copyright 2003-2006 by eXoPlatform - All rights reserved.  *
 *    *
 **************************************************************************/
package org.exoplatform.services.html.parser;

import java.util.Iterator;
import java.util.List;

import org.exoplatform.services.html.HTMLNode;
import org.exoplatform.services.html.MoveType;
import org.exoplatform.services.html.Name;
import org.exoplatform.services.html.NodeConfig;
/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Aug 13, 2006
 */
final class NodeCloser {

  void close(NodeConfig config){    
    if(config.only()) return;

    if(config.parent().length > 0){
      NodeImpl parent = Services.OPEN_NODE.getRef().getOpenParent(config, false); 
      if(parent == null) return; 
      List<HTMLNode> children = parent.getChildrenNode();
      for(int j = children.size()-1; j > -1; j--){        
        if(((NodeImpl)children.get(j)).isOpen()){
          close((NodeImpl)children.get(j));         
          break;
        }
      }
      return;
    }

    List<NodeImpl> opens = Services.OPEN_NODE.getRef().getOpens();
    for(int i = opens.size() - 1; i > -1; i--){
      if(opens.get(i).getConfig().name() != config.name()){
        if(opens.get(i).getConfig().block()) break;
        continue;
      }
      close(opens.get(i));
      break;
    }
  }

  void close(NodeImpl node){
    if(!node.isOpen()) return;
    node.setIsOpen(false);
    Services.OPEN_NODE.getRef().getOpens().remove(node);

    List<HTMLNode> children = node.getChildrenNode();    
    for(HTMLNode ele : children) close((NodeImpl)ele);

    NodeConfig config = node.getConfig(); 
    if(config.children().length > 0 || config.children_types().length > 0){
      Iterator<HTMLNode> iter = node.getChildren().iterator();
      while(iter.hasNext()){
        HTMLNode child = iter.next();        
        if(HTML.isChild(node, child.getConfig())) continue;        
        iter.remove();
        if(config.move() == MoveType.INSERT) insert(node, child);
//        if(config.move() == MoveType.ADD) node.getParent().addChild(child);
      }
    }    

    if(config.move() != MoveType.HEADER) return; 
    
    HTMLNode header = null;
    if(Services.ROOT.getChildren().size() > 0){
      header = Services.ROOT.getChildren().get(0);
    }
    if(header  == null || !header.isNode(Name.HEAD)){
      header = Services.createHeader();
    }
    node.getParent().getChildren().remove(node);
    header.addChild(node);
    node.setParent(header);
  }

  private void insert(HTMLNode node, HTMLNode element){  
    HTMLNode parent  = node.getParent();    
    List<HTMLNode> children = parent.getChildren();
    int i = children.indexOf(node);
    if(i < 0) return;
    children.add(i, element);
    element.setParent(parent);          
  } 

}
