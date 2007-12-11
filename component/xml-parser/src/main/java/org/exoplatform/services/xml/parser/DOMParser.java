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
