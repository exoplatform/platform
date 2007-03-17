/***************************************************************************
 * Copyright 2003-2006 by  eXo Platform SARL - All rights reserved.  *
 *    *
 **************************************************************************/
package org.exoplatform.services.parser.html.parser;

import java.util.Iterator;
import java.util.List;

import org.exoplatform.services.parser.common.TypeToken;
import org.exoplatform.services.parser.html.HTMLNode;
import org.exoplatform.services.parser.html.Name;
import org.exoplatform.services.parser.html.NodeConfig;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Aug 3, 2006
 */
public final class DOMParser { 

  final void parse(CharsToken tokens) {    
    if(!tokens.hasNext()) return;
    NodeImpl temp = tokens.pop();    
    while(tokens.hasNext()){    
      NodeConfig config = temp.getConfig();
      if(config.hidden()){
        Services.ADD_NODE.getRef().add(Services.OPEN_NODE.getRef().getLast(), temp);
      }else if(temp.getType() == TypeToken.CLOSE){    
        Services.CLOSE_NODE.getRef().close(config);
      }else if(temp.getType() == TypeToken.TAG){
        Services.ADD_NODE.getRef().add(temp);
      }else{
        Services.ADD_NODE.getRef().add(Services.OPEN_NODE.getRef().getLast(), temp);
      }
      temp = tokens.pop(); 
    }   
    move(Services.ROOT);
    Services.CLOSE_NODE.getRef().close(Services.ROOT);   
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
    if(head == null) head = Services.createHeader();      
    if(body == null) body = Services.createBody();
    
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
