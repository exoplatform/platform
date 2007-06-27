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
  protected String name;
  
  //TODO Le Bien Thuy add
//  protected PageNavigation navigation;
//  protected PageNode parent;
  //---------------------------------------
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
  
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  
  public List<PageNode> getChildren() { return children ;  }
  public void setChildren(ArrayList<PageNode> list) { children = list ; }
  
  public void addChild(PageNode node){
    if(children == null) children = new ArrayList<PageNode>();
//    node.setUri(uri + "/" + node.name);
    children.add(node);
//    node.setParent(this);
//    node.setNavigation(navigation);
  }
  
  public boolean isModifiable() { return modifiable ; }
  public void    setModifiable(boolean b) { modifiable = b ; }  
  
  public PageNode clone() {
    PageNode newNode = new PageNode() ;
    newNode.setUri(uri);
    newNode.setLabel(label);
    newNode.setIcon(icon);
    newNode.setName(name);
    newNode.setPageReference(pageReference);
    newNode.setModifiable(modifiable);
    if(children == null || children.size() < 1) return newNode;
    for(PageNode ele : children) {
      newNode.getChildren().add(ele.clone());
    }
    return newNode;
  }

  public void removeNode(String str) {
    for(PageNode pageNode: children){
      if(pageNode.getUri().equalsIgnoreCase(str)) {
        children.remove(pageNode);
        return;
      }
    }
  }
  
  public PageNode findPageNodeByUri(String childUri){
    if(uri.equalsIgnoreCase(childUri) ) return this;
    if(childUri  == null ) return null;
    if( children == null ) return null;
    for(PageNode page: children) {
      PageNode resuilt = findPageNodeByUri(page, childUri);
      if(resuilt!= null) return resuilt;
    }
    return null;
  }
  
  private PageNode findPageNodeByUri(PageNode page, String childUri) {
    if(page.getUri().equals(childUri)) return page;
    List<PageNode> list = page.getChildren();
    if(list == null || list.size() < 1 ) return null;
    for(PageNode child: list) {
      PageNode resuilt = findPageNodeByUri(child, childUri);
      if(resuilt!= null) return resuilt;
    }
    return null;
  }

  public boolean hasNode(PageNode p){
    if(p == null) return false;
    return (findPageNodeByUri(p.getUri()) != null);
  }
  
  public boolean hasNode(String str){
    return (findPageNodeByUri(str) != null);
  }

  public void removeNode(PageNode page) { children.remove(page); }
  
  public PageNode getChildNodeByName(String nodeName){
    String childUri = getUri()+ "/" + nodeName;
    for(PageNode ele: children){
      if( ele.getUri().equals(childUri) ) return ele; 
    }
    return null;
  }
  
  public boolean hasChildNode(String nodeName){
    return getChildNodeByName(nodeName)!= null;
  }

//  public PageNavigation getNavigation() {
//    return navigation;
//  }
//
//  public void setNavigation(PageNavigation navigation) {
//    this.navigation = navigation;
//  }

//  public PageNode getParent() {
//    return parent;
//  }
//
//  public void setParent(PageNode parent) {
//    this.parent = parent;
//  }

  
}