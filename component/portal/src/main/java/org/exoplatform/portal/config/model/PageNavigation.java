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
  
  private transient String id ;
  
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
  
  public String getOwnerId() { return ownerId; }
  public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

  public String getOwnerType() { return ownerType; }
  public void setOwnerType(String ownerType) { this.ownerType = ownerType; }

  public String[] getAccessGroup(){  return accessGroup; }
  public void     setAccessGroup(String[] s) { accessGroup = s ; }
  
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
  
  public String getId() {
    if(id == null) id = ownerType +"::"+ownerId;
    return id; 
  }
  
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
  
  public String getAccessGroups(){
    if(accessGroup == null)  return "";
    StringBuilder builder = new StringBuilder();
    for(String ele : accessGroup) {
      builder.append(ele).append(' ');
    }
    return builder.toString();
  }
  public void setAccessGroups(String s){ 
    this.accessGroups = s;
    if(accessGroups == null) return ;
    accessGroup = accessGroups.split(",");
    for(int i = 0; i < accessGroup.length; i++) {
      accessGroup[i] = accessGroup[i].trim(); 
    }
  }
  
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