/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.portal.config;

import java.util.Comparator;

import org.exoplatform.commons.utils.LazyPageList;
import org.exoplatform.portal.application.PortletPreferences;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.services.portletcontainer.pci.WindowID;

/**
 * Created by The eXo Platform SAS
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
  public PageNavigation getPageNavigation(String fullId) throws Exception ;
  
  /**
   * This method  should load the PageNavigation object from the database according to the ownerType
   * and id
   * @param ownerType
   * @param id
   * @return
   * @throws Exception
   */
  public PageNavigation getPageNavigation(String ownerType, String id) throws Exception ;
  
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
  
  public void save(PortletPreferences portletPreferences)  throws Exception;
  
  public PortletPreferences getPortletPreferences(WindowID windowID)  throws Exception;
  
  public void remove(PortletPreferences portletPreferences) throws Exception ;
  
  public  LazyPageList find(Query<?> q) throws Exception ;
  
  public  LazyPageList find(Query<?> q, Comparator<?> sortComparator) throws Exception ;  
}