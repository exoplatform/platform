/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.config;

import java.util.List;

import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PortalConfig;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan.nguyen@exoplatform.com
 * Apr 19, 2007
 * 
 * This service is used to load the PortalConfig, Page config  and  Navigation config for a given 
 * user.
 */
public class UserPortalConfigService {
  private DataStorage  storage_ ;
  
  /**
   *The constructor should create the DataStorage object and broadcast "the UserPortalConfigService.onInit"
   *event
   */
  public UserPortalConfigService() {
    
  }
  /**
   * This  method should load the PortalConfig object according to the portalName,  set the view and edit
   * permission according to the accessUser, load Naviagtion of the portal according to the portalName, 
   * find all the navigation of the groups that the user is belong to
   * @param userName
   * @return a UserPortalConfig object that contain the PortalConfig  and a list of the PageNavigation objects
   */
  public UserPortalConfig  getUserPortalConfig(String portalName, String accessUser) throws Exception {
    return null ;
  }
  
  /**
   * This method  should create a  the portal  config, pages and navigation according to the template 
   * name
   * @param portalName
   * @param template
   * @return
   * @throws Exception
   */
  public UserPortalConfig  createUserPortalConfig(String portalName, String template) throws Exception {
    return null ;
  }
  
  /**
   * This method should remove the PortalConfig, Page and PageNavigation  that  belong to the portal 
   * in the database. The method should broadcast the event UserPortalConfigService.portal.onRemove
   * @param config
   * @throws Exception
   */
  public void  removeUserPortalConfig(String portalName) throws Exception {
  }
  
  /**
   * This method should create or update the PortalConfig  object
   * @param config
   * @throws Exception
   */
  public void  save(PortalConfig config) throws Exception {
    
  }
  
  /**
   * This method  should load the page according to the pageId,  set view and edit  permission for the
   * Page object  according to the accessUser.
   * @param accessUser
   * @return
   * @throws Exception
   */
  public Page getPage(String pageId, String accessUser) throws Exception {
    return null ; 
  }
  
  /**
   * This method should remove the page object in the database and  broadcast the event 
   * UserPortalConfigService.page.onRemove
   * @param config
   * @throws Exception
   */
  public void remove(Page page) throws Exception {
    
  }
  
  /**
   * This method should create  or  udate the given page object
   * @param page
   * @throws Exception
   */
  public void save(Page page) throws Exception {
    
  }
  
  /**
   * This method should return the PageNavigation of the given portal, and the PageNavigation of the
   * groups that the accessUser belong to
   * @param portalName
   * @param accessUser
   * @return
   * @throws Exception
   */
  public List<PageNavigation> getPageNavigations(String portalName, String accessUser) throws Exception {
    return  null;
  }
  
  /**
   * This method should create or update the navigation object in the database
   * @param navigation
   * @throws Exception
   */
  public void save(PageNavigation navigation) throws Exception {
    
  }
  
  /**
   * This method should  remove the navigation object from the database
   * @param navigation
   * @throws Exception
   */
  public void remove(PageNavigation navigation) throws Exception {
    
  }
}