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
  protected String name ;
  protected String label ;
  protected String icon ;
  protected String accessPermission ;
  protected String pageReference ;
  protected String description ;
  protected String type;
  
  protected  String creator ;
  protected  String modifier ;
  
  private transient boolean modifiable ;
  
  public PageNode() {  }
  
  public PageNode(PageNode pageNode){
    copyPageNode(pageNode) ;
    List<PageNode> list = pageNode.getChildren();
    if (list == null)  return;
    for (PageNode child : list) {      
      children.add(new PageNode(child)) ;
    }    
  }
  
  public String getUri() { return uri ; }
  public void   setUri(String s) { uri = s ; }

  public String getName() { return name ; }
  public void   setName(String s) { name = s ; }

  public String getLabel() { return label ; }
  public void   setLabel(String s) { label = s ; }
  
  public String getIcon() { return icon ; }
  public void   setIcon(String s) { icon = s ; }

  public String getAccessPermission() { return accessPermission ; }
  public void   setAccessPermission(String s) { accessPermission = s ; } 
  
  public String getDescription() { return description ; }
  public void   setDescription(String s) { description = s ; }
  
  public String getType() { return type; }
  public void   setType(String s) { type = s ; }

  public String getCreator()  {  return creator ; }
  public void   setCreator(String s) { creator = s ; }
  
  public String getModifier() { return modifier ; }
  public void   setModifier(String s) { modifier = s ; }
  
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
  
  private void copyPageNode(PageNode node){
    this.uri = node.getUri();
    this.name =  node.getName();
    this.label =  node.getLabel();
    this.accessPermission = node.getAccessPermission();
    this.icon =  node.getIcon();
    this.pageReference =  node.getPageReference() ;
    this.description =  node.getDescription();
    this.type = node.getType();
    this.creator = node.getCreator() ;
    this.modifier = node.getModifier() ;
  }
  
}