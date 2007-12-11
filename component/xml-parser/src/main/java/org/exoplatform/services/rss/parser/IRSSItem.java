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

import org.exoplatform.services.xml.parser.XMLNode;

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
