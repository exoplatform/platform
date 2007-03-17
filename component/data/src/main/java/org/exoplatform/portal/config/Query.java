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
public class Query {
  
  private String owner_ ;
  private String type_ ;
  private String viewPermission_ ;
  private String editPermission_ ;
  private Class classType_;

  public Query(String owner, String vp , String ep, String type) {
    owner_ = owner ;
    viewPermission_ = vp ;
    editPermission_ = ep ;
    type_ = type;
  }
  
  public Query(String owner, String vp , String ep, Class clazz) {
    owner_ = owner ;
    viewPermission_ = vp ;
    editPermission_ = ep ;
    type_ = clazz.getName();
    classType_ = clazz;
  }
  
  public String getOwner() { return owner_ ; }
  public void   setOwner(String s) { owner_ = s ; }
  
  public String getType() { return type_ ; }
  public void   setType(String s) { type_ = s ; }
  
  public Class getClassType() { 
    return classType_ ; 
  }
  public void   setClassType(Class clazz) {
    classType_ = clazz;
    type_ = clazz.getName() ; 
  }
  
  public String getViewPermission() { return viewPermission_ ; }
  public void   setViewPermission(String s) { viewPermission_ = s ; }
  
  public String getEditPermission() { return editPermission_ ; }
  public void   setEditPermission(String s) { editPermission_ = s ; }
  
}
