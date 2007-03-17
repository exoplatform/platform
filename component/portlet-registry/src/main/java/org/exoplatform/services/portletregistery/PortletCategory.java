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
 * @hibernate.class  table="PORTLET_CATEGORY"
 */
public class PortletCategory {

  private String id;
  private String portletCategoryName;
  private String description;
  private Date createdDate;
  private Date modifiedDate;
  //private Set portlets;

  /**
   * @hibernate.id  generator-class="assigned" unsaved-value="null"
   **/
  public String getId() { return id; }
  public void setId(String id) { this.id = id; }

  /**
   * @hibernate.property
   **/
  public String getPortletCategoryName() { return portletCategoryName; }
  public void setPortletCategoryName(String s) { portletCategoryName = s; }

  /**
   * @hibernate.property
   **/
  public String getDescription() { return description; }
  public void setDescription(String s) { description = s; }

  /**
   * @hibernate.property
   **/
  public Date getCreatedDate() { return createdDate; }
  public void setCreatedDate(Date d) { createdDate = d; }

  /**
   * @hibernate.property
   **/
  public Date getModifiedDate() { return modifiedDate; }
  public void setModifiedDate(Date d) { modifiedDate = d; }

  /**
   * @hibernate.set cascade="delete" lazy="true"
   * @hibernate.collection-key  column="portletCategoryId"
   * @hibernate.collection-one-to-many  class="org.exoplatform.services.portletregistery.Portlet"
   **/
  /*
  public Set   getPortlets() { return portlets ; }
  public void  setPortlets(Set list) { portlets = list ; }
  */
}