/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.config;

import java.util.List;

import org.exoplatform.commons.utils.PageList;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.portlet.PortletPreferences;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan.nguyen@exoplatform.com
 * Apr 19, 2007
 * 
 * This interface is used to load the PortalConfig, Page config  and  Navigation config from the 
 * database
 */
public interface DataStorage {
  
  /**
   * This method should create the PortalConfig  object
   * @param config
   * @throws Exception
   */
  public void  create(PortalConfig config) throws Exception;
  
  /**
   * This method should update the PortalConfig  object
   * @param config
   * @throws Exception
   */
  public void  save(PortalConfig config) throws Exception;

  /**
   * This method should load the PortalConfig object from db according to the portalName 
   * @param portalName
   * @return
   * @throws Exception
   */
  public PortalConfig getPortalConfig(String portalName) throws Exception ;

  /**
   * This method should remove the PortalConfig ,  all the Page that belong to the portal and the 
   * PageNavigation of the  portal from the database
   * @param config
   * @throws Exception
   */
  public void  remove(PortalConfig config) throws Exception;
  
  /**
   * This method  should load the Page object from the database according to the pageId
   * @param accessUser
   * @return
   * @throws Exception
   */
  public Page getPage(String pageId) throws Exception ;
  
  /**
   * This method should remove the page object  from the database
   * @param config
   * @throws Exception
   */
  public void remove(Page page) throws Exception ;

  /**
   * This method should create  or  udate the given page object
   * @param page
   * @throws Exception
   */
  public void create(Page page) throws Exception ;
  
  /**
   * This method should create  or  udate the given page object
   * @param page
   * @throws Exception
   */
  public void save(Page page) throws Exception ;
  
  /**
   * This method  should load the PageNavigation object from the database according to the pageId
   * @param id
   * @return
   * @throws Exception
   */
  public PageNavigation getPageNavigation(String id) throws Exception ;
  
  /**
   * This method should update the navigation object in the database
   * @param navigation
   * @throws Exception
   */
  public void save(PageNavigation navigation) throws Exception ;
  
  /**
   * This method should create the navigation object in the database
   * @param navigation
   * @throws Exception
   */
  public void create(PageNavigation navigation) throws Exception ;
  
  /**
   * This method should  remove the navigation object from the database
   * @param navigation
   * @throws Exception
   */
  public void remove(PageNavigation navigation) throws Exception ;
  
  public void savePortletPreferencesConfig(PortletPreferences portletPreferences)  throws Exception;
  
  public  PageList find(Query q) throws Exception ;
  
  public List<PortalConfig> getAllPortalConfig() throws Exception;
}