/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.site.webui.component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Sep 27, 2006  
 */
public class Node {
  private List<Node> children_ ;
  private String name_ ;
  private String path_ ;
  
  public Node(String path, String name) {
    name_ = name ;
    path_ =  path ;
  }
  
  public String getName() { return name_ ; }
  
  public String getPath() { return path_ ;}
  
  public List<Node> getChildren() { return children_ ; }
  
  public void addChild(Node node) {
    if(children_ == null) children_ = new ArrayList<Node>(5) ;
    children_.add(node) ;
  }
}
