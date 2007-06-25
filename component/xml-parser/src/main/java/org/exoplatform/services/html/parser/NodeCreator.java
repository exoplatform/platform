/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.exoplatform.services.html.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.exoplatform.services.html.HTMLNode;
import org.exoplatform.services.html.Name;
import org.exoplatform.services.html.NodeConfig;
import org.exoplatform.services.token.TypeToken;
/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Aug 13, 2006
 */
final class NodeCreator { 

  private List<NodeImpl> opens = new ArrayList<NodeImpl>();    

  List<NodeImpl> getOpens() { return opens;  }

  NodeImpl getLast(){ return opens.get(opens.size() - 1); }

  NodeImpl getOpenParent(NodeConfig config, boolean create){ 
    List<Name[]> list = new ArrayList<Name[]>();    
    while(config.parent().length > 0){
      list.add(0, config.parent());
      config = HTML.getConfig(config.parent()[0]);
    }    
    if(opens.size() < 1) return null;
    if(list.size() < 1) return opens.get(opens.size()-1);   
    NodeImpl parent = opens.get(opens.size()-1);
    NodeImpl impl = null;
    Iterator<Name[]> iter = list.iterator();
    boolean start = false;    
    while(iter.hasNext()){
      Name [] names = iter.next();
      if(start){
        List<HTMLNode> children = parent.getChildrenNode();       
        for(int i = children.size() - 1; i > -1; i--){
          NodeImpl child = (NodeImpl)children.get(i); 
          if(!child.isOpen()) break;
          for(Name name : names){
            if(child.getName() != name) continue;
            impl = child;
            break;
          }
        }
      }else{
        impl  = getOpenNode(names);
      }
      if(impl == null){
        if(create) return createNode(list, parent);
        return null;
      }
      parent = impl;
      impl = null;
      iter.remove();
      start = true;
    }    
    return parent;
  }

  private NodeImpl createNode(List<Name[]> list, NodeImpl parent){
    NodeImpl child = null;
    for(Name[] names : list){
      Name name = names[0];
      child = new NodeImpl(name.toString().toCharArray(), name, TypeToken.TAG);      
      if(child.getConfig().only()){
        parent = ParserService.getNodeSetter().set(child);
      }else{
        parent.addChild(child);
        child.setParent(parent);
        opens.add(child);
        parent = child;
      }
    }
    return parent;
  }  

  private NodeImpl getOpenNode(Name[] names){    
    for(int i = opens.size() - 1; i > -1; i--){
      for(Name name : names){
        if(opens.get(i).getConfig().name() == name)  {
          return opens.get(i);
        }
      }
      if(opens.get(i).getConfig().block()) break;
    }
    return null;
  }
}
