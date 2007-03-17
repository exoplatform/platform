/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.content.model;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by The eXo Platform SARL
 * Author : Dang Van Minh
 *          minhdv81@yahoo.com
 * Jun 30, 2006
 */
public class ContentNode  {
  
  protected String id ;
  protected String url;
  protected String label ;
  protected String icon ;
  protected String description ;
  protected String type ;
  
  protected ArrayList<ContentNode> children ;
  
  public ContentNode() {  }
  
  public String getId() { return id ; }
  public void   setId(String s) { id = s ; }

  public String getLabel() { return label ; }
  public void   setLabel(String s) { label = s ; }
  
  public String getIcon() { return icon ; }
  public void   setIcon(String s) { icon = s ; }
  
  public String getDescription() { return description ; }
  public void   setDescription(String s) { description = s ; }
  
  public String getType() { return type; }
  public void   setType(String s) { type = s ; }
  
  public String getUrl() { return url; }
  public void setUrl(String url) { this.url = url; }
  
  public List<ContentNode> getChildren() { return children ;  }
  public void setChildren(ArrayList<ContentNode> list) { children = list ; }
  public void addChild(ContentNode node){
    if(children == null) children = new ArrayList<ContentNode>();
    children.add(node);
  }

  
}