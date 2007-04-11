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
 */
public class PortletCategory {

  private String id;
  private String portletCategoryName;
  private String description;
  private Date createdDate;
  private Date modifiedDate;
  
  public String getId() { return id; }
  public void setId(String id) { this.id = id; }

  public String getPortletCategoryName() { return portletCategoryName; }
  public void setPortletCategoryName(String s) { portletCategoryName = s; }

  public String getDescription() { return description; }
  public void setDescription(String s) { description = s; }

  public Date getCreatedDate() { return createdDate; }
  public void setCreatedDate(Date d) { createdDate = d; }

  public Date getModifiedDate() { return modifiedDate; }
  public void setModifiedDate(Date d) { modifiedDate = d; }
  
}