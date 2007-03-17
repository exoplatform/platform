/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.config;

import java.util.Date;

/**
 * Created by The eXo Platform SARL        .
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * Date: Jun 14, 2003
 * Time: 1:12:22 PM
 *
 * @hibernate.class  table="EXO_DATA"
 */
public class DataDescription   {
  
  protected String id_ ;
  protected String type_ ;
  protected String owner_ ;
  protected String viewPermission_ ;
  protected String editPermission_ ;
  protected Date createdDate_ ;
  protected Date modifiedDate_ ;

  /**
   * @hibernate.id  generator-class="assigned" unsaved-value="null"
   ***/
  public String   getId() { return id_ ; }
  public void     setId(String id) { id_ = id ; }
  
  /**
   * @hibernate.property
   ***/
  public String   getDataType() { return type_ ; }
  public void     setDataType(String name) { type_ = name ; }

  /**
   * @hibernate.property
   ***/
  public String   getOwner() { return owner_ ; }
  public void     setOwner(String owner) { owner_ = owner ; }
  
  /**
   * @hibernate.property
   ***/
  public String getViewPermission() { return viewPermission_ ; }
  public void   setViewPermission(String s) { viewPermission_ = s ; }

  /**
   * @hibernate.property
   ***/
  public String getEditPermission() { return editPermission_ ; }
  public void   setEditPermission(String s) { editPermission_ = s ; }
  
  /**
   * @hibernate.property
   ***/
  public Date getCreatedDate() { return createdDate_ ; }
  public void   setCreatedDate(Date date) { createdDate_ = date ; }
  
  /**
   * @hibernate.property
   ***/
  public Date getModifiedDate() { return modifiedDate_ ; }
  public void   setModifiedDate(Date date) { modifiedDate_ = date ; }
  
}