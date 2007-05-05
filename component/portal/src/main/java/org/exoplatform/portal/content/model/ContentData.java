/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.content.model;

import java.util.Date;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Jul 3, 2006  
 * 
 * @hibernate.class  table="EXO_CONTENT_DATA"
 *                   polymorphism="explicit"
 */
public class ContentData {
  
  private String data ;
  
  protected String id_ ;
  protected String type_ ;
  protected String owner_ ;
  protected Date createdDate_ ;
  protected Date modifiedDate_ ;
  
  public ContentData() {}
  /**
   * @hibernate.property length="65535" type="org.exoplatform.services.database.impl.TextClobType"
   **/
  public String getData() throws Exception {  return data ; }
  public void  setData(String s) throws Exception {  data = s ; }
  
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
  public Date getCreatedDate() { return createdDate_ ; }
  public void   setCreatedDate(Date date) { createdDate_ = date ; }
  
  /**
   * @hibernate.property
   ***/
  public Date getModifiedDate() { return modifiedDate_ ; }
  public void   setModifiedDate(Date date) { modifiedDate_ = date ; }
  
}
