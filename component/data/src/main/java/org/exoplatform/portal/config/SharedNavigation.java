/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.config;

/**
 * @author Tuan Nguyen (tuan08@users.sourceforge.net)
 * @since Dec 4, 2004
 * @version $Id$
 * @hibernate.class  table="EXO_COMMUNITY_NAVIGATION"
 */
public class SharedNavigation {
  
  private String  groupId ;
  private String  membership ;
  private String  navigation ;
  private int     priority ;
  private String  description ;
//  private boolean useDefaultHomePage;

  /**
   * @hibernate.id  generator-class="assigned" unsaved-value="null"
   ***/
  public String getGroupId() { return groupId ; }
  public void   setGroupId(String s) { groupId = s ; }
  
  /**
   * @hibernate.property
   ***/
  public String getMembership() { return membership ; }
  public void   setMembership(String s) {
    membership = s ; 
  }
  
  /**
   * @hibernate.property
   ***/
  public String getNavigation() { return navigation ; }
  public void   setNavigation(String s) { navigation = s ; }
  
  /**
   * @hibernate.property
   ***/
  public int  getPriority() { return priority ; }
  public void setPriority(int i) { priority  = i ; }
  
  /* *
   * @hibernate.property
   ***/
  /*public boolean getUseDefaultHomePage() { return useDefaultHomePage ; }
  public void   setUseDefaultHomePage(boolean b) {  useDefaultHomePage = b ; }*/
  
  /**
   * @hibernate.property
   ***/
  public String getDescription()  { return description ; }
  public void   setDescription(String s)  { description = s ; }
  
}