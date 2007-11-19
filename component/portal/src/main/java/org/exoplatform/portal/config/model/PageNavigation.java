/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.config.model;

import java.util.ArrayList;
import java.util.ResourceBundle;

public class PageNavigation {
  private String      ownerType;
  private String      ownerId;
  private String      description ;
  private transient boolean     modifiable ;
  private  String     creator ;
  private  String     modifier ;
  private ArrayList<PageNode>	pageNodes = new ArrayList<PageNode>();
  private int         priority = 1 ;
  
  public String getOwnerId() { return ownerId; }
  public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

  public String getOwnerType() { return ownerType; }
  public void setOwnerType(String ownerType) { this.ownerType = ownerType; }

  public boolean isModifiable(){  return modifiable; }
  public void    setModifiable(boolean b) { modifiable = b ; }

  public void setDescription(String des){  description = des; }  
  public String getDescription(){  return description; }

  public int  getPriority() { return priority ; }
  public void setPriority(int i) { priority  = i ; }
  
  public String getCreator()  {  return creator ; }
  public void   setCreator(String s) { creator = s ; }
  
  public String getModifier() { return modifier ; }
  public void   setModifier(String s) { modifier = s ; }
  
  public String getId() { return ownerType +"::"+ownerId; }
  
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
//    newNav.setAccessPermissions(accessPermissions);
//    newNav.setEditPermission(editPermission);
    newNav.setModifiable(modifiable);
    newNav.setDescription(description);
    newNav.setCreator(creator);
    newNav.setModifier(modifier);

    if(pageNodes == null || pageNodes.size() < 1) return newNav;
    for(PageNode ele : pageNodes) {
      newNav.getNodes().add(ele.clone());
    }
    return newNav;
  }
 
}