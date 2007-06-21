/***************************************************************************
 * Copyright 2003-2006 by eXoPlatform - All rights reserved.  *
 *    *
 **************************************************************************/
package org.exoplatform.services.xml.parser;

import java.util.List;

import org.exoplatform.services.token.TypeToken;
/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Aug 3, 2006
 */
class DOMParser { 

  static void parse(XMLToken tokens, XMLNode root) {    
    XMLNode temp = tokens.pop();
    XMLNode current = root; 
    while(tokens.hasNext()){
      if(temp.getType() == TypeToken.CLOSE){    
        current = closeNode(temp.getName(), current); 
      }else {
        current.addChild(temp);
        if(temp.getType() == TypeToken.TAG) current = temp;  
      }      
      temp = tokens.pop(); 
    }   
    closeAll(root);  
  }   
  
  private static XMLNode closeNode(String name, XMLNode n){   
    XMLNode node = n;   
    while(node.getParent() != null){     
      if(node.isOpen() && node.isNode(name)){        
        closeAll(node);
        return node.getParent(); 
      }
      node = node.getParent();  
    }      
    return n;
  }
  
  static private void closeAll(XMLNode node){    
    if(!node.isOpen()) return;  
    XMLNode ele;
    List<XMLNode> children = node.getChildren();
    for(int i=0; i<children.size(); i++) {
      ele = children.get(i);
      closeAll(ele);
    }    
    node.setIsOpen(false);    
  }
  
}
