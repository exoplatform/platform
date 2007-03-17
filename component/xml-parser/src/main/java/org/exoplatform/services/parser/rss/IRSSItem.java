/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.services.parser.rss;

import org.exoplatform.services.parser.xml.XMLNode;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Jul 21, 2006  
 */
public interface IRSSItem {  
  
  public void setTitle(String title);  
  public String getTitle() ;
  
  public void setDesc(String desc);  
  public String getDesc();
  
  public void setImage(String image);  
  public String getImage();
  
  public void setTime(String time);  
  public String getTime();
  
  public void setLink(String link);  
  public String getLink();
  
  public void setNode(XMLNode node);  
  public XMLNode getNode();
  
  public XMLNode getItem(String name);  
  public String getValueItem(String name);
}
