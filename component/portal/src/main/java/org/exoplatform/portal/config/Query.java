/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.config;
/**
 * Created by The eXo Platform SARL        .
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * Date: Jun 14, 2003
 * Time: 1:12:22 PM
 */
public class Query <T> {
  
  private String ownerType_ ;
  private String ownerId_ ;
  private String name_;
  private Class<T> classType_;

  public Query(String ownerType, String ownerId, Class<T> clazz) {
    ownerType_ = ownerType ;
    ownerId_ = ownerId;
    classType_ = clazz;
  }
  
  public Query(String ownerType, String ownerId, String name, Class<T> clazz) {
    ownerType_ = ownerType ;
    ownerId_ = ownerId;
    classType_ = clazz;
    try {
      name_ = name;
    }catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  
  public String getOwnerType() { return ownerType_ ; }
  public void   setOwnerType(String s) { ownerType_ = s ; }
  
  public String getOwnerId() { return ownerId_ ; }
  public void   setOwnerId(String s) { ownerId_ = s ; }
  
  public Class<T> getClassType() {  return classType_ ;  }
  public void   setClassType(Class<T> clazz) { classType_ = clazz;  }

  public String getName() { return name_; }
  public void setName(String name_) { this.name_ = name_; }
  
}
