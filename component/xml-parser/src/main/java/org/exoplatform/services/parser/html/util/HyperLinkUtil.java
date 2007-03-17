/***************************************************************************
 * Copyright 2004-2006 The  eXo Platform SARL All rights reserved.  *
 **************************************************************************/
package org.exoplatform.services.parser.html.util;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.exoplatform.services.parser.attribute.Attribute;
import org.exoplatform.services.parser.attribute.AttributeParser;
import org.exoplatform.services.parser.attribute.AttributeUtil;
import org.exoplatform.services.parser.attribute.Attributes;
import org.exoplatform.services.parser.html.HTMLNode;
import org.exoplatform.services.parser.text.TextVerifier;
import org.exoplatform.services.parser.text.ValueVerifier;

/** 
 * Author : Thuannd
 *         nhudinhthuan@yahoo.com
 * Apr 21, 2006
 */
public class HyperLinkUtil extends AttributeUtil {   
  //-----------------------------   
  public synchronized List<String> getSiteLink(HTMLNode node) {
    Map<String, String> map = new HashMap<String, String>(4); 
    map.put("a", "href");
    map.put("iframe", "src");
    map.put("frame", "src");
    map.put("meta", "url");
    return getAttributes(node, null, map, new SiteLinkVerifier());
  }
  //-----------------------------
  public  synchronized String getSingleImageLink(HTMLNode node) { 
    Map<String, String> map = new HashMap<String, String>(1); 
    map.put("img", "src");    
    return getAttribute(node, map, new ImageLinkVerifier());
  }   
  //-----------------------------   
  public  synchronized void createFullNormalLink(HTMLNode node, URL home) {  
    createFullNormalLink(node, home, new URLCreator());
  }
  
  public  synchronized void createFullNormalLink(HTMLNode node, URL home, URLCreator creator) {   
    Map<String, String> map = new HashMap<String, String>(5); 
    map.put("a", "href");
    map.put("iframe", "src");
    map.put("frame", "src");
    map.put("meta", "url");
    map.put("link", "href");
    createFullLink(node, map, home, creator, new NormalLinkVerifier());  
  } 
  //-----------------------------
  public synchronized void createFullImageLink(HTMLNode node,URL home) {   
    createFullImageLink(node, home, new URLCreator());  
  } 
  
  public  synchronized void createFullImageLink(HTMLNode node,URL home, URLCreator creator) {   
    Map<String, String> map = new HashMap<String, String>(1); 
    map.put("img", "src");
    createFullLink(node, map, home, creator, new ImageLinkVerifier());  
  } 
  //-------------------------------skip testing the below methods.------------------------------
  public  synchronized void createFullLink(HTMLNode node, Map<String, String> map, 
                                           URL home, URLCreator creator, ValueVerifier verifier) {   
    createFullSingleLink(node, map, home, creator, verifier);    
    List<HTMLNode> children = node.getChildrenNode();
    for(HTMLNode ele : children)
      createFullLink(ele, map, home, creator, verifier);
  } 
  //------------------------------
  private void createFullSingleLink(HTMLNode node, Map<String,String> map,
                                    URL home, URLCreator creator,  ValueVerifier verifier)   {
    Attribute attr = null;
    Set<String> keys = map.keySet();
    Iterator<String> iter = keys.iterator();
    while(iter.hasNext()){
      String key = iter.next();
      if(!node.isNode(key)) continue;
      Attributes attrs = AttributeParser.getAttributes(node); 
      int idx = attrs.indexOf(map.get(key));
      if(idx < 0)  continue;
      attr = attrs.get(idx);
      String value = attr.getValue();
      if(verifier != null && !verifier.verify(value)) return;
      value  = creator.createURL(home, value);      
      attr.setValue(value);      
      attrs.set(attr);
      return;
    }  
  }
  
  //--------------------------------CLASS---------------------------------------------------------
  
  private class SiteLinkVerifier extends TextVerifier implements ValueVerifier{
    public boolean verify(String link){
      link = link.toLowerCase();    
      String start[]={"mailto","javascript","window","history"};
      String end[]={"css","js","jpg","gif","jpeg","bmp","dat","exe","txt",
                    "java","pdf","doc", "rm","ram","wma","wmv","mp3","swf" ,"zip", "jar","rar"};
      String exist[] ={"javascript","img(\"","image","#"} ;
      return !startOrEndOrExist(link, start, end, exist); 
    }
  }
  
  private class ImageLinkVerifier extends TextVerifier implements ValueVerifier{
    public boolean verify(String link){
      link = link.toLowerCase();    
      String exist[] = {"img","image"};
      String end[]={"jpg","gif","jpeg","bmp","dib"};
      return existIn(link, exist) || endIn(link, end);
    }
  }
  
  private class NormalLinkVerifier extends TextVerifier implements ValueVerifier{
    public boolean verify(String link){
      link = link.toLowerCase();    
      String start[]={"mailto","javascript","window","history"};    
      String exist[] ={"javascript","#"} ;
      String end[]={};
      return !startOrEndOrExist(link, start, end, exist); 
    }
  }
  
}

