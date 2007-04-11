/*
 * Copyright 2001-2003 The eXo platform SARL All rights reserved.
 * Please look at license.txt in info directory for more license detail. 
 */

package org.exoplatform.services.portletregistery;

import java.util.Date;

/**
 * Created y the eXo platform team
 * User: Benjamin Mestrallet
 * Date: 15 juin 2004
 *
 */
public class Portlet {

  private String id;
  private String portletCategoryId ;
  private String portletApplicationName;
  private String portletName;
  private String description;
  private Date createdDate;
  private Date modifiedDate;
  private String displayName;
  private String viewPermission ;

  public String getId() { return id; }  
  public void setId(String id) { this.id = id; }

  public String getDisplayName() { return displayName; }  
  public void setDisplayName(String displayName) { this.displayName = displayName; }
  
  public String getPortletApplicationName() { return portletApplicationName; }  
  public void setPortletApplicationName(String portletApplicationName) {
    this.portletApplicationName = portletApplicationName;
  }

  public String getPortletName() { return portletName; }  
  public void setPortletName(String portletName) { this.portletName = portletName; }

  public String getDescription() {
    if(description == null ) return "" ;
    return description;
  }  
  public void setDescription(String s) { description = s; }

  public Date getCreatedDate() { return createdDate; }  
  public void setCreatedDate(Date d) { createdDate = d; }

  public Date getModifiedDate() { return modifiedDate; }  
  public void setModifiedDate(Date d) { modifiedDate = d; }
  
  public String getPortletCategoryId() {   return portletCategoryId; }  
  public void setPortletCategoryId(String portletCategoryId) {
    this.portletCategoryId = portletCategoryId;
  }
  
  public String getViewPermission() { return viewPermission ; }
  public void   setViewPermission(String s) { viewPermission = s ; }  
  
}