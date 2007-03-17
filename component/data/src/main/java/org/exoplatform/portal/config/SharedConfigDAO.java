/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.config;

import java.util.List;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Mar 1, 2007  
 */
public interface SharedConfigDAO {
  
  static SharedPortal NO_COMMUNITY_PORTAL = new SharedPortal() ;
  
  static SharedNavigation NO_COMMUNITY_NAVIGATION = new SharedNavigation() ;

  public void saveSharedPortal(List<SharedPortal> list) throws Exception;
  
  public void addSharedPortal(SharedPortal sharedPortal) throws Exception ;
  
  public SharedPortal getSharedPortal(String groupId) throws Exception ;
  
  public List<SharedPortal> getSharedPortals() throws Exception;
  
  public void removeSharedPortal(SharedPortal cp) throws Exception ;
  
  
  public void saveSharedNavigation(List<SharedNavigation> list) throws Exception;
  
  public void addSharedNavigation(SharedNavigation sharedNav) throws Exception ;
  
  public SharedNavigation getSharedNavigation(String groupId) throws Exception ;
  
  public List<SharedNavigation> getSharedNavigations() throws Exception;
  
  public void removeSharedNavigation(SharedNavigation sharedNav) throws Exception ;
  
}
