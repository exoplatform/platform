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
