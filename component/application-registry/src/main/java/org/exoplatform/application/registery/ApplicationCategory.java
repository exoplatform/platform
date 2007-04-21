/*
 * Copyright 2001-2003 The eXo platform SARL All rights reserved.
 * Please look at license.txt in info directory for more license detail. 
 */

package org.exoplatform.application.registery;

import java.util.Date;

/**
 * Created y the eXo platform team
 * User: Benjamin Mestrallet
 * Date: 15 juin 2004
 */
public class ApplicationCategory {

  private String name;
  private String displayName;
  
  private String description;
  private Date createdDate;
  private Date modifiedDate;
  
  public String getName() { return name; }
  public void   setName(String id) { this.name = id; }

  public String getCategoryName() { return displayName; }
  public void setCategoryName(String s) { displayName = s; }

  public String getDescription() { return description; }
  public void setDescription(String s) { description = s; }

  public Date getCreatedDate() { return createdDate; }
  public void setCreatedDate(Date d) { createdDate = d; }

  public Date getModifiedDate() { return modifiedDate; }
  public void setModifiedDate(Date d) { modifiedDate = d; }
  
}