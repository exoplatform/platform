/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.exoplatform.services.html.parser;

import java.util.Iterator;
import java.util.List;

import org.exoplatform.services.html.HTMLNode;
import org.exoplatform.services.html.Name;
import org.exoplatform.services.html.NodeConfig;
import org.exoplatform.services.token.TypeToken;
/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Aug 3, 2006
 */
final class DOMParser { 

  final void parse(CharsToken tokens) {    
    if(!tokens.hasNext()) return;
    NodeImpl temp = tokens.pop();    
    
    NodeCreator creator = ParserService.getNodeCreator();
    NodeSetter setter = ParserService.getNodeSetter();
    NodeCloser closer = ParserService.getNodeCloser();
    
    while(tokens.hasNext()){    
      NodeConfig config = temp.getConfig();
      
      if(config.hidden()) setter.add(creator.getLast(), temp);
        
      else if(temp.getType() == TypeToken.CLOSE) closer.close(config);
      
      else if(temp.getType() == TypeToken.TAG) setter.add(temp);
      
      else setter.add(creator.getLast(), temp);
      
      temp = tokens.pop(); 
    }   
    
    move(ParserService.getRootNode());
    closer.close(ParserService.getRootNode());   
  } 

  private void move(HTMLNode root){
    List<HTMLNode> children = root.getChildren();
    if(children == null || children.size() < 1) return;
    HTMLNode head = null ;
    HTMLNode body = null;
    for(HTMLNode child : children){
      if(child.isNode(Name.HEAD)) head = child;
      if(child.isNode(Name.BODY)) body = child;
    }
    if(head == null) head = ParserService.createHeader();      
    if(body == null) body = ParserService.createBody();
    
    Iterator<HTMLNode> iter = children.iterator();
    while(iter.hasNext()){
      HTMLNode ele = iter.next();
      if(ele.isNode(Name.HEAD) || ele.isNode(Name.BODY)) continue;
      if(ele.isNode(Name.SCRIPT)){
        head.addChild(ele);
        ele.setParent(head);
      }else{
        body.addChild(ele);
        ele.setParent(body);
      }
      iter.remove();
    }
  }
  
}
