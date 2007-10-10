/*
 * Copyright 2001-2003 The eXo platform SARL All rights reserved.
 * Please look at license.txt in info directory for more license detail. 
 */

package org.exoplatform.application.registry;

import java.util.Date;
import java.util.List;

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
  
  private List<Application> applications;
  
  public String getName() { return name; }
  public void   setName(String id) { this.name = id; }

  public String getDisplayName() { return displayName; }
  public void setDisplayName(String s) { displayName = s; }

  public String getDescription() { return description; }
  public void setDescription(String s) { description = s; }

  public Date getCreatedDate() { return createdDate; }
  public void setCreatedDate(Date d) { createdDate = d; }

  public Date getModifiedDate() { return modifiedDate; }
  public void setModifiedDate(Date d) { modifiedDate = d; }
  
  public List<Application> getApplications() { return applications; }
  public void setApplications(List<Application> applications) { this.applications = applications; }
  
}