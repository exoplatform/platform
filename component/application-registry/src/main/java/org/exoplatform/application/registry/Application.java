/**
 * Copyright 2001-2003 The eXo platform SARL All rights reserved.
 * Please look at license.txt in info directory for more license detail. 
 */

package org.exoplatform.application.registry;

import java.util.Date;

/**
 * Created by the eXo platform team
 * User: Benjamin Mestrallet
 * Date: 15 juin 2004
 *
 */
public class Application {

  private String id;
  
  private String categoryName ;
  private String displayName;
  private String description;
  private Date createdDate;
  private String owner;
  private Date modifiedDate;
  private String accessPermission ;
  private transient String[] accessPermissions ;
  private String editPermission ;
  private String applicationGroup ;
  private String applicationName ;
  private String applicationType ;
  
  private int minWidthResolution ;
  
  public String getId() { return id; }  
  public void setId(String id) { this.id = id; }

  public String getDisplayName() { return displayName; }  
  public void setDisplayName(String displayName) { this.displayName = displayName; }
  
  public String getDescription() {
    if(description == null ) return "" ;
    return description;
  }  
  public void setDescription(String s) { description = s; }

  public Date getCreatedDate() { return createdDate; }  
  public void setCreatedDate(Date d) { createdDate = d; }

  public Date getModifiedDate() { return modifiedDate; }  
  public void setModifiedDate(Date d) { modifiedDate = d; }
  
  public String getCategoryName() {   return categoryName; }  
  public void setCategoryName(String s) { this.categoryName = s; }
  
  public String getOwner() {   
    if(owner == null || owner.length() < 1) return "Unknown" ; 
    return owner;
  }  
  
  public void setOwner(String s) { this.owner = s; }
  
  public void setAccessPermissions(String[] permiss) { accessPermissions = permiss ; }  
  public String[] getAccessPermissions() { return accessPermissions ; }
  
  public String getAccessPermission() {
    if (accessPermissions == null || accessPermissions.length < 1) return null ;
    StringBuilder builder = new StringBuilder() ;
    for (int i= 0; i < accessPermissions.length; i ++) {
      builder.append(accessPermissions[i]) ;
      if (i < (accessPermissions.length - 1)) builder.append(',') ;
    }
    
    return builder.toString() ;
  }
  public void setAccessPermission(String permiss) {
    accessPermission = permiss ;
    if (accessPermission == null) return ;
    accessPermissions = accessPermission.split(",") ;
    for (int i = 0; i < accessPermissions.length ; i++) {
      accessPermissions[i] = accessPermissions[i].trim() ;
    }
  } 
  
  public String getEditPermission() { return editPermission ; }
  public void setEditPermission(String editPermiss) { editPermission = editPermiss; }
  
  public String getApplicationGroup() { return applicationGroup; }
  public void setApplicationGroup(String applicationGroup) {
    this.applicationGroup = applicationGroup;
  }
  
  public String getApplicationName() { return applicationName; }
  public void setApplicationName(String applicationName) { this.applicationName = applicationName; }
  
  public String getApplicationType() { return applicationType; }
  public void setApplicationType(String applicationType) { this.applicationType = applicationType; }
  
  public int getMinWidthResolution() { return minWidthResolution; }
  public void setMinWidthResolution(int minWidthResolution) { 
    this.minWidthResolution = minWidthResolution; 
  }
}