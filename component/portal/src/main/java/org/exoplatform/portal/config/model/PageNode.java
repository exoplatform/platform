/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.config.model;

import java.util.ArrayList;
import java.util.List;
/**
 * Thu, Apr 01, 2004 @ 11:02 
 * @author: Tuan Nguyen
 * @email:   tuan08@users.sourceforge.net
 * @version: $Id: PageNode.java,v 1.12 2004/10/27 03:11:17 tuan08 Exp $
 */
public class PageNode  {
  
  protected ArrayList<PageNode> children = new ArrayList<PageNode>(5) ;
  protected String uri ;
  protected String label ;
  protected String icon ;
  
  private String pageReference ;
  
  private transient boolean modifiable ;
  
  public PageNode() {  }
  
  public String getUri() { return uri ; }
  public void   setUri(String s) { uri = s ; }

  public String getLabel() { return label ; }
  public void   setLabel(String s) { label = s ; }
  
  public String getIcon() { return icon ; }
  public void   setIcon(String s) { icon = s ; }

  public String getPageReference() { return pageReference ;}  
  public void   setPageReference(String s) { pageReference = s ;}
  
  public List<PageNode> getChildren() { return children ;  }
  public void setChildren(ArrayList<PageNode> list) { children = list ; }
  public void addChild(PageNode node){
    if(children == null) children = new ArrayList<PageNode>();
    children.add(node);
  }
  
  public boolean isModifiable() { return modifiable ; }
  public void    setModifiable(boolean b) { modifiable = b ; }  
  
  public PageNode clone() {
    PageNode newNode = new PageNode() ;
    newNode.setUri(uri);
    newNode.setLabel(label);
    newNode.setIcon(icon);
    newNode.setPageReference(pageReference);
    newNode.setModifiable(modifiable);
    if(children == null || children.size() < 1) return newNode;
    for(PageNode ele : children) {
      newNode.getChildren().add(ele.clone());
    }
    return newNode;
  }

}