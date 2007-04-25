/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.config.model;

import java.util.ArrayList;

/**
 * May 13, 2004
 * @author: Tuan Nguyen
 * @email:   tuan08@users.sourceforge.net
 * @version: $Id: Page.java,v 1.9 2004/11/03 01:23:55 tuan08 Exp $
 **/
public class Page extends Container {
  
  private String   pageId;
  private String   factoryId;
  
  private String   name ;
  private String   ownerType;
  private String   ownerId;
  
  private String accessGroups;
  private transient String[] accessGroup ;
  private boolean  showMaxWindow = false ;
  
  private String   creator ;
  private String   modifier ;
  
  private transient boolean modifiable ;
  
  public Page() {
  }
  
  public String getName() { return name ; }
  public void   setName(String s) { name = s ; } 
  
  public String getOwnerId() { return ownerId; }
  public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

  public String getOwnerType() { return ownerType; }
  public void setOwnerType(String ownerType) { this.ownerType = ownerType; }
  
  public String[] getAccessGroup() { return accessGroup ; }
  public void   setAccessGroup(String[] s) { accessGroup = s ; } 
  
  public void setAccessGroups(String s){ this.accessGroups = s; }  
  public String getAccessGroups(){  return accessGroups; }
  
  public String getFactoryId() { return factoryId; }
  public void setFactoryId(String factoryId) { this.factoryId = factoryId; }
  
  public boolean isShowMaxWindow() { return showMaxWindow; }  
  public void setShowMaxWindow(Boolean showMaxWindow) {
    this.showMaxWindow = showMaxWindow.booleanValue(); 
  }
  
  public void setPageId(String id) { 
    this.pageId = id;
    // split id, assign value to ownerType, ownerId, name
  }
  //ownerType::ownerId::name
  public String getPageId() { return pageId; }
  
  public boolean isModifiable() { return modifiable ; }
  public void    setModifiable(boolean b) { modifiable = b ; }
  
  public String getCreator()  {  return creator ; }
  public void   setCreator(String s) { creator = s ; }
  
  public String getModifier() { return modifier ; }
  public void   setModifier(String s) { modifier = s ; }
  
  static public class PageSet {
    private ArrayList<Page> pages ;
    public PageSet() { pages = new ArrayList<Page>(); }
    public ArrayList<Page> getPages() { return pages ; }
    public void setPages(ArrayList<Page> list) { pages = list ; }
  }
  
}