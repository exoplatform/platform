/**
 * Copyright 2001-2003 The eXo platform SARL All rights reserved.
 * Please look at license.txt in info directory for more license detail. 
 */

package org.exoplatform.application.registery;

import java.util.Date;

/**
 * Created y the eXo platform team
 * User: Benjamin Mestrallet
 * Date: 15 juin 2004
 *
 */
public class Application {

  private String id;
  
  private String categoryName ;
  private String aliasName;
  private String displayName;
  private String description;
  private Date createdDate;
  private Date modifiedDate;
  private String[] accessGroup ;

  private String applicationGroup ;
  private String applicationName ;
  private String applicationType ;
  
  private int minWidthResolution ;
  
  public String getId() { return id; }  
  public void setId(String id) { this.id = id; }

  public String getAliasName() { return aliasName; }  
  public void setAliasName(String name) { this.aliasName = name; }
  
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
  
  public String[] getAccessGroup() { return accessGroup ; }
  public void setAccessGroup(String[] group) { accessGroup =  group ; }
}