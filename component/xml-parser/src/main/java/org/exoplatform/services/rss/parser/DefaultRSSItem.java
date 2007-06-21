/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.services.rss.parser;

import java.util.List;

import org.exoplatform.services.xml.parser.XMLNode;

/**
 * Created by The eXo Platform SARL        .
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 13, 2006
 */
public class DefaultRSSItem implements IRSSItem {  
  
  private String title = "", desc =  "", image = "",  time = "", link = ""; 
  private XMLNode node;
  
  public DefaultRSSItem() {} 
   
  public void setTitle(String title){ this.title = title; }  
  public String getTitle(){ return title; }
  
  public void setDesc(String desc){ this.desc = desc; }  
  public String getDesc(){ return desc; }
  
  public void setImage(String image){ this.image = image; }  
  public String getImage(){ return image; }  
  
  public void setTime(String time){ this.time = time; }  
  public String getTime(){ return time;  }
  
  public void setLink(String link){ this.link = link; }  
  public String getLink(){ return link; }
  
  public void setNode(XMLNode node){ this.node = node; }  
  public XMLNode getNode(){ return node; }
  
  public XMLNode getItem(String name){
    List<XMLNode> children = node.getChildren();
    for(XMLNode ele : children){
      if(ele.isNode(name)) return ele;
    }
    return null;
  }
  
  public String getValueItem(String name){
    XMLNode n = getItem(name);
    if(n == null || n.getTotalChildren() < 1) return "";
    return n.getChild(0).getNodeValue();
  }

}
