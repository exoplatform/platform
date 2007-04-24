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
  
  private String owner ;
  private String name ;
  private String accessGroup ;
  private boolean showMaxWindow = false ;
  
  public Page() {
  	setId("page") ;
  }
  
  public String getOwner() { return owner ; }
  public void   setOwner(String s) { owner = s ; } 
  
  public String getName() { return name ; }
  public void   setName(String s) { name = s ; } 
  
  public String getAccessGroup() { return accessGroup ; }
  public void   setAccessGroup(String s) { accessGroup = s ; } 
  
  public boolean isShowMaxWindow() { return showMaxWindow; }  
  public void setShowMaxWindow(Boolean showMaxWindow) {
    this.showMaxWindow = showMaxWindow.booleanValue(); 
  }
  
  public String getPageId() {	return owner + ":/" + name ; }
  
  static public class PageSet {
    private ArrayList<Page> pages ;
    public PageSet() { pages = new ArrayList<Page>(); }
    public ArrayList<Page> getPages() { return pages ; }
    public void setPages(ArrayList<Page> list) { pages = list ; }
  }
  
}