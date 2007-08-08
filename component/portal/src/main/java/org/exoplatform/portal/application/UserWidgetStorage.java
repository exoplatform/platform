/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.application;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Aug 8, 2007  
 */
public interface UserWidgetStorage {

  public void save(String userName, String widgetType, String instantId, Object data) throws Exception ;
  
  public Object get(String userName, String widgetType, String instantId) throws Exception ;
  
  public void delete(String userName, String widgetType, String instantId) throws Exception ;
  
}
