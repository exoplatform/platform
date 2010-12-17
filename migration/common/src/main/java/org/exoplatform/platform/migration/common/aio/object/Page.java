/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.platform.migration.common.aio.object;

import java.util.ArrayList;

/**
 * May 13, 2004
 **/
public class Page extends Container {
  
  final static public String DESKTOP_PAGE = "Desktop";
  final static public String DEFAULT_PAGE = "Default";
  
  private String   pageId;
  
  private String   ownerType;
  private String   ownerId;
  
  private String[] accessPermissions ;
  
  private String editPermission;
  
  private boolean  showMaxWindow = false ;
  
  private String   creator ;
  private String   modifier ;
  
  private transient boolean modifiable ;
  
  public Page() {
  }
  
  public String getOwnerId() { return ownerId; }
  public void setOwnerId(String ownerId) {
    this.ownerId = ownerId; 
  }

  public String getOwnerType() { return ownerType; }
  public void setOwnerType(String ownerType) { 
    this.ownerType = ownerType;
  }
  
  public String[] getAccessPermissions() { return accessPermissions ; }
  public void     setAccessPermissions(String[] s) { accessPermissions = s; }
  
  public String getEditPermission() { return editPermission; }
  public void setEditPermission(String editPermission) { this.editPermission = editPermission; }
  
  public boolean isShowMaxWindow() { return showMaxWindow; }  
  public void setShowMaxWindow(boolean showMaxWindow) {
    this.showMaxWindow = showMaxWindow; 
  }
  
  public String getPageId() {
    if(pageId == null) pageId = ownerType +"::"+ownerId+"::"+name;
    return pageId; 
  }
  
  public void setPageId(String pageId) {
    this.pageId = pageId;
  }

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