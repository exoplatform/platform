/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.config.model;

import java.util.ArrayList;

/**
 * Jul 18, 2004 
 * @author: Tuan Nguyen
 * @email:   tuan08@users.sourceforge.net
 * @version: $Id: NodeNavigation.java,v 1.1 2004/07/20 12:41:07 tuan08 Exp $
 */
public class PageNavigation {
  
  private String      id ;
  private String			portalName;
  private String      accessGroup ;
  private String      description ;
  private boolean     modifiable ;

  private ArrayList<PageNode>	pageNodes = new ArrayList<PageNode>();
  private int         priority = 0 ;
  
  public String getId() { return id; }
  public void setId(String id) { this.id = id; }

  public String getPortalName() { return portalName;}
  public void setPortalName(String owner) { this.portalName = owner; }

  public void setAccessGroup(String accessPermission){
    this.accessGroup = accessPermission;
  }  
  public String getAccessGroup(){  return accessGroup; }

  public boolean getModifiable(){  return modifiable; }
  public void    setModifiable(boolean b) { modifiable = b ; }

  public void setDescription(String des){  description = des; }  
  public String getDescription(){  return description; }

  public int  getPriority() { return priority ; }
  public void setPriority(int i) { priority  = i ; }
  
  public PageNode getNode(int idx) {  return pageNodes.get(idx); }

  public PageNode removeNode(int idx) {  
    PageNode node = pageNodes.get(idx);
    pageNodes.remove(idx);
    return node;
  }  

  public void removeNode(String uri) {
    for(PageNode pageNode: pageNodes){
      if(pageNode.getUri().equalsIgnoreCase(uri)) {
        pageNodes.remove(pageNode);
        break;
      }
    }
  }

  public void removeNode(PageNode page) { pageNodes.remove(page); }

  public void addNode(PageNode node) {
    if(pageNodes == null) pageNodes = new ArrayList<PageNode>();
    pageNodes.add(node); 
  }

  public ArrayList<PageNode> getNodes(){ return pageNodes; }
  public void setNodes(ArrayList<PageNode> nodes) { pageNodes = nodes; }
  
  public PageNavigation clone() {
    PageNavigation newNav = new PageNavigation();
    newNav.setPortalName(portalName);
    newNav.setPriority(priority);
    newNav.setAccessGroup(accessGroup);
    newNav.setModifiable(modifiable);
    newNav.setDescription(description);

    if(pageNodes == null || pageNodes.size() < 1) return newNav;
    for(PageNode ele : pageNodes) {
      newNav.getNodes().add(ele.clone());
    }
    return newNav;
  }

}