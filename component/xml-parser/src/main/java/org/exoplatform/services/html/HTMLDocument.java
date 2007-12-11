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
package org.exoplatform.services.html;

import java.util.List;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Aug 5, 2006
 */
public class HTMLDocument {
  
  private HTMLNode root;  
  
  private HTMLNode doctype;
  
  public HTMLNode getDoctype() { return doctype; }
  
  public void setDoctype(HTMLNode doctype) { this.doctype = doctype;}
  
  public HTMLNode getRoot() { return root; }
  
  public void setRoot(HTMLNode root) { this.root = root; }
  
  public String getTextValue(){
    StringBuilder builder = new StringBuilder();
    List<HTMLNode> list = root.getChildren();
    for(HTMLNode ele : list){
      ele.buildValue(builder);
    }
    return builder.toString();
  }
  
  
}
