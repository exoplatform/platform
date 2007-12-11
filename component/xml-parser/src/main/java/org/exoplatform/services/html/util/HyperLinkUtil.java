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
package org.exoplatform.services.html.util;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.exoplatform.services.chars.TextVerifier;
import org.exoplatform.services.chars.ValueVerifier;
import org.exoplatform.services.common.ServiceConfig;
import org.exoplatform.services.common.ServiceConfig.ServiceType;
import org.exoplatform.services.html.HTMLNode;
import org.exoplatform.services.token.attribute.Attribute;
import org.exoplatform.services.token.attribute.AttributeParser;
import org.exoplatform.services.token.attribute.AttributeUtil;
import org.exoplatform.services.token.attribute.Attributes;

/** 
 * Author : Thuannd
 *         nhudinhthuan@yahoo.com
 * Apr 21, 2006
 */
@ServiceConfig(type = ServiceType.SOFT_REFERENCE)
public class HyperLinkUtil extends AttributeUtil {   
     
  public synchronized List<String> getSiteLink(HTMLNode node) {
    Map<String, String> map = new HashMap<String, String>(4); 
    map.put("a", "href");
    map.put("iframe", "src");
    map.put("frame", "src");
    map.put("meta", "url");
    return getAttributes(node, null, map, new SiteLinkVerifier());
  }
  
  public synchronized List<String> getImageLink(HTMLNode node) {
    Map<String, String> map = new HashMap<String, String>(1); 
    map.put("img", "src");
    return getAttributes(node, null, map, null);
  }
  
  public  synchronized String getSingleImageLink(HTMLNode node) { 
    Map<String, String> map = new HashMap<String, String>(1); 
    map.put("img", "src");    
    return getAttribute(node, map, new ImageLinkVerifier());
  }   
     
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
  
  public synchronized void createFullImageLink(HTMLNode node,URL home) {   
    createFullImageLink(node, home, new URLCreator());  
  } 
  
  public  synchronized void createFullImageLink(HTMLNode node,URL home, URLCreator creator) {   
    Map<String, String> map = new HashMap<String, String>(1); 
    map.put("img", "src");
    createFullLink(node, map, home, creator, new ImageLinkVerifier());  
  } 
  
  public  synchronized void createFullLink(HTMLNode node, Map<String, String> map, 
                                           URL home, URLCreator creator, ValueVerifier verifier) {   
    createFullSingleLink(node, map, home, creator, verifier);    
    List<HTMLNode> children = node.getChildrenNode();
    for(HTMLNode ele : children)
      createFullLink(ele, map, home, creator, verifier);
  } 
  
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
  
  public static class ImageLinkVerifier extends TextVerifier implements ValueVerifier{
    public boolean verify(String link){
      link = link.toLowerCase();    
      String exist[] = {"img","image"};
      String end[]={"jpg","gif","jpeg","bmp","dib"};
      return existIn(link, exist) || endIn(link, end);
    }
  }
  
  public static class NormalLinkVerifier extends TextVerifier implements ValueVerifier{
    public boolean verify(String link){
      link = link.toLowerCase();    
      String start[]={"mailto","javascript","window","history"};    
      String exist[] ={"javascript","#"} ;
      String end[]={};
      return !startOrEndOrExist(link, start, end, exist); 
    }
  }
  
}

