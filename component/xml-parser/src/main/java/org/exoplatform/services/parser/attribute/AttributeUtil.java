/***************************************************************************
 * Copyright 2001-2003 The  eXo Platform SARL        All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.services.parser.attribute;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.exoplatform.services.parser.html.HTMLNode;
import org.exoplatform.services.parser.text.ValueVerifier;
/**
 * Created by  eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 8, 2006
 */
public class AttributeUtil {
  
  public synchronized List<String> getAttributes(HTMLNode node, Map<String, String> map) {
    return getAttributes(node, null, map, null);   
  }
  
  public synchronized List<String> getAttributes(HTMLNode node, Map<String, String> map, ValueVerifier verifier) {
    return getAttributes(node, null, map, verifier);   
  }
  
  public synchronized String getAttribute(HTMLNode node, Map<String, String> map, ValueVerifier verifier) {   
    Attribute attr = getAttribute(node, map);    
    if(attr != null){
      if(verifier == null || verifier.verify(attr.getValue())) 
        return  attr.getValue();
    }    
    List<HTMLNode> children = node.getChildrenNode();
    for(HTMLNode ele : children){
      String link = getAttribute(ele, map, verifier);
      if(link != null) return link;
    }
    return null;
  }  
  
  public synchronized List<String> getAttributes(HTMLNode node, List<String> list, 
                                    Map<String, String> map, ValueVerifier verifier){   
    if(list == null) list  = new ArrayList<String>();
    Attribute attr = getAttribute(node, map);    
    if(attr != null){
      if(verifier == null || verifier.verify(attr.getValue()))
        list.add(attr.getValue());
    }
    
    List<HTMLNode> children = node.getChildrenNode();
    for(HTMLNode ele : children) getAttributes(ele, list, map, verifier);       
    return list;
  } 
  
  public synchronized Attribute getAttribute(HTMLNode node, Map<String, String> map){
    Set<String> keys = map.keySet();
    Iterator<String> iter = keys.iterator();
    while(iter.hasNext()){
      String key = iter.next();
      if(node.isNode(key)){
        Attributes attrs = AttributeParser.getAttributes(node);  
        int idx = attrs.indexOf(map.get(key));
        if(idx > -1) return attrs.get(idx);
      }
    }
    return null;
  }
  
}
