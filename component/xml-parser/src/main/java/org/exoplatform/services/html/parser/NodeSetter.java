/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.exoplatform.services.html.parser;

import java.util.List;

import org.exoplatform.services.html.HTMLNode;
import org.exoplatform.services.html.Name;
import org.exoplatform.services.html.Tag;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Aug 13, 2006
 */
final class NodeSetter {
  
  void add(NodeImpl node){
    if(node.getConfig().only()){
      set(node);
      return;
    }

    HTMLNode parent = ParserService.getNodeCreator().getOpenParent(node.getConfig(), true);    
    if(parent  != null && 
        parent.getConfig().end() == Tag.OPTIONAL  && HTML.isEndType(node, parent.getConfig()) ){
      ParserService.getNodeCloser().close((NodeImpl)parent);     
      parent = ParserService.getNodeCreator().getOpenParent(node.getConfig(), true);   
    }  
    
    //close all older children in parent #Bug 28/11 
    List<HTMLNode> children = parent.getChildren();
    if(children.size() > 0) {
      ParserService.getNodeCloser().close((NodeImpl)children.get(children.size() - 1));
    }

    add(parent, node);    
    if(node.getConfig().end() != Tag.FORBIDDEN){    
      if(node.isOpen()) ParserService.getNodeCreator().getOpens().add(node);      
    }    

  }

  HTMLNode add(HTMLNode node, HTMLNode ele){      
    ele.setParent(node);
    node.addChild(ele);
    if(ele.getConfig().end() != Tag.FORBIDDEN) return ele;
    return node;
  }

  NodeImpl set(NodeImpl node){ 
    if(node.getName() == Name.HTML) return ParserService.getRootNode();
    List<HTMLNode> children = ParserService.getRootNode().getChildren(); 

    for(HTMLNode ele : children){
      if(ele.getConfig().name() != node.getConfig().name()) continue;
      ele.setValue(node.getValue());
      return (NodeImpl)ele;
    }

    if(node.getName() == Name.BODY){
      add(ParserService.getRootNode(), node);
      ParserService.getNodeCreator().getOpens().add(1, node);  
      return node;
    }

    children.add(0, node);    
    node.setParent(ParserService.getRootNode());
    node.setIsOpen(false);
    return node;
  } 
}
