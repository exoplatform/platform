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
  private String      navigationId;
  private String      ownerType;
  private String      ownerId;
  
  private String accessGroups;
  private transient String[]    accessGroup ;
  private String      description ;
  private transient boolean     modifiable ;
  
  private  String     creator ;
  private  String     modifier ;

  private ArrayList<PageNode>	pageNodes = new ArrayList<PageNode>();
  private int         priority = 0 ;
  
  public String getId() { return id; }
  public void setId(String id) { this.id = id; }

  public String getOwnerId() { return ownerId; }
  public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

  public String getOwnerType() { return ownerType; }
  public void setOwnerType(String ownerType) { this.ownerType = ownerType; }

  public void setAccessGroup(String[] s){ this.accessGroup = s; }  
  public String[] getAccessGroup(){  return accessGroup; }
  
  public void setAccessGroups(String s){ this.accessGroups = s; }  
  public String getAccessGroups(){  return accessGroups; }

  public boolean getModifiable(){  return modifiable; }
  public void    setModifiable(boolean b) { modifiable = b ; }

  public void setDescription(String des){  description = des; }  
  public String getDescription(){  return description; }

  public int  getPriority() { return priority ; }
  public void setPriority(int i) { priority  = i ; }
  
  public String getCreator()  {  return creator ; }
  public void   setCreator(String s) { creator = s ; }
  
  public String getModifier() { return modifier ; }
  public void   setModifier(String s) { modifier = s ; }
  
  public String getNavigationId() { return navigationId; }
  public void setNavigationId(String navigationId) { this.navigationId = navigationId; }
  
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
    newNav.setOwnerId(ownerId);
    newNav.setOwnerType(ownerType);
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