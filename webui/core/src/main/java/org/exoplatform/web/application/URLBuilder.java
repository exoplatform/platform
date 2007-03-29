/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.web.application;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan.nguyen@exoplatform.com
 * Mar 29, 2007  
 */
abstract public class URLBuilder {
  abstract public String getBaseURL() ;
  
  public String createURL(String action) {
    return createURL(action, (Parameter[])null) ;
  }
  
  abstract public String createURL(String action, Parameter[] params) ;
  
  public String createURL(String action, String objectId) {
    return createURL(action, objectId, (Parameter[]) null) ;
  }
  abstract public String createURL(String action, String objectId, Parameter[] params) ;
  
  public <T> String createURL(T targetComponent, String action, String objectId) {
    return createURL(targetComponent, action, objectId, (Parameter[])null) ;
  }
  
  abstract public <T> String createURL(T targetComponent, String action, String objectId, Parameter[] param) ;
}
