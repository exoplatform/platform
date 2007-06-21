/***************************************************************************
 * Copyright 2003-2006 by eXoPlatform - All rights reserved.  *
 *    *
 **************************************************************************/
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
