/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.services.resources;
/**
 * Created by The eXo Platform SARL        .
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * Date: Jun 14, 2003
 * Time: 1:12:22 PM
 */
public class Query {
  
  private String name_ ;
  private String languages_ ;
  private int maxSize_ ;

  public Query(String name, String language) {
    name_ = name ;
    languages_ = language ;
    maxSize_ = 100 ;
  }
  
  public String getName() { return name_ ; }
  public void   setName(String s) { name_ = s ; }
  
  public String getLanguage() { return languages_ ; }
  public void   setLanguage(String s) { languages_ = s ; }
  
  public int  getMaxSize() { return maxSize_ ; }
  public void setMaxSize(int s) { maxSize_ = s ; }
}