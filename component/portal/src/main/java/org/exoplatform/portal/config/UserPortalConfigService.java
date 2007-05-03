/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.exoplatform.container.component.ComponentPlugin;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.services.cache.CacheService;
import org.exoplatform.services.cache.ExoCache;
import org.exoplatform.services.cache.ExpireKeyStartWithSelector;
import org.exoplatform.services.organization.Membership;
import org.exoplatform.services.organization.OrganizationService;

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
  private UserACL userACL_;
  private OrganizationService orgService_;
  
  protected ExoCache portalConfigCache_ ;
  protected ExoCache pageConfigCache_ ;
  protected ExoCache pageNavigationCache_ ;
  
  /**
   *The constructor should create the DataStorage object and broadcast "the UserPortalConfigService.onInit"
   *event
   */
  public UserPortalConfigService(InitParams params, 
                                 DataStorage storage,
                                 CacheService cacheService,
                                 OrganizationService  orgService) throws Exception {
    storage_ = storage ;
    orgService_ = orgService;
    
    userACL_ = new UserACL(params, orgService);
    
    portalConfigCache_   = cacheService.getCacheInstance(PortalConfig.class.getName()) ;
    pageConfigCache_     = cacheService.getCacheInstance(Page.class.getName()) ;
    pageNavigationCache_ = cacheService.getCacheInstance(PageNavigation.class.getName()) ;
  }
  
  
  /**
   * @return This method should the membership type that the user can access the portal, pages
   * and the navigation.
   */
  public String getViewMembershipType()  { return userACL_.getViewMembershipType() ; }
  
  /**
   * @return This method should return the membership type that the user can edit the portal, pages
   * and the navigation
   */
  public String getEditMembershipType()  { return userACL_.getEditMembershipType() ; }
  
  /**
   * This  method should load the PortalConfig object according to the portalName,  set the view and edit
   * permission according to the accessUser, load Naviagtion of the portal according to the portalName, 
   * find all the navigation of the groups that the user is belong to
   * @param userName
   * @return a UserPortalConfig object that contain the PortalConfig  and a list of the PageNavigation objects
   */
  public UserPortalConfig  getUserPortalConfig(String portalName, String accessUser) throws Exception {
    PortalConfig portalConfig = storage_.getPortalConfig(portalName) ;
    System.out.println("\n\n\n === > ta day ta co "+portalConfig+" : "+userACL_.getViewMembershipType() +"\n\n");
    if(portalConfig == null || 
       !userACL_.hasPermission(portalConfig, accessUser, userACL_.getViewMembershipType())) return null ;
    List<PageNavigation> navigations = new ArrayList<PageNavigation>();
    
    PageNavigation navigation = getPageNavigation(DataStorage.PORTAL_TYPE+"::"+portalName) ;
    if (navigation != null) navigations.add(navigation) ;
    navigation = getPageNavigation(DataStorage.PORTAL_TYPE+"::"+accessUser) ;
    if (navigation != null) navigations.add(navigation) ;
    
    Collection memberships = orgService_.getMembershipHandler().findMembershipsByUser(accessUser);
    Iterator mitr = memberships.iterator() ;
    while(mitr.hasNext()) {
      Membership m = (Membership) mitr.next() ;    
      navigation = getPageNavigation(DataStorage.GROUP_TYPE+"::"+m.getGroupId()) ;
      if (navigation != null) navigations.add(navigation) ;
    }   
    userACL_.computeNavigation(navigations, accessUser);
    System.out.println("\n\n\n === > ta day ta co "+navigations.size() +"\n\n");
    if (navigations.size() < 1) return null ;

    return new UserPortalConfig(portalConfig, navigations) ;    
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
    UserPortalConfig userPortalConfig  = new UserPortalConfig();
    return userPortalConfig;   
  }
  
  /**
   * This method should remove the PortalConfig, Page and PageNavigation  that  belong to the portal 
   * in the database. The method should broadcast the event UserPortalConfigService.portal.onRemove
   * @param config
   * @throws Exception
   */
  public void  removeUserPortalConfig(String portalName) throws Exception {
    PortalConfig portalConfig = storage_.getPortalConfig(portalName) ;
    if(portalConfig != null) storage_.remove(portalConfig);
    
    PageNavigation navigation = getPageNavigation(DataStorage.PORTAL_TYPE+"::"+portalName) ;
    if (navigation != null) remove(navigation);
  }
  
  /**
   * This method should create or update the PortalConfig  object
   * @param config
   * @throws Exception
   */
  public void update(PortalConfig config) throws Exception {    
    storage_.save(config) ;    
  }
  
//**************************************************************************************************
    
  /**
   * This method  should load the page according to the pageId,  set view and edit  permission for the
   * Page object  according to the accessUser.
   * @param accessUser
   * @return
   * @throws Exception
   */
  public Page getPage(String pageId, String accessUser) throws Exception {
    Page page = (Page) pageConfigCache_.get(pageId) ;
    if(page != null) return page ;
    page  = storage_.getPage(pageId) ;
    if(page == null || userACL_.hasPermission(page, accessUser, userACL_.getViewMembershipType())) return null;
    page.setModifiable(userACL_.hasPermission(page, accessUser, userACL_.getEditMembershipType()));
    pageConfigCache_.put(pageId, page);
    return page ; 
  }
  
  /**
   * This method should remove the page object in the database and  broadcast the event 
   * UserPortalConfigService.page.onRemove
   * @param config
   * @throws Exception
   */
  public void remove(Page page) throws Exception {    
    storage_.remove(page) ;
    pageConfigCache_.remove(page.getId());
  }
  /**
   * This method should create the given page object
   * @param page
   * @throws Exception
   */
  public void create(Page page) throws Exception {
    storage_.create(page) ;
    pageConfigCache_.put(page.getId(), page);
  }
  
  /**
   * This method should update the given page object
   * @param page
   * @throws Exception
   */
  public void update(Page page) throws Exception {
    storage_.save(page) ;
    pageConfigCache_.select(new ExpireKeyStartWithSelector(page.getId())) ;
  }

//**************************************************************************************************
  
  public void create(PageNavigation navigation) throws Exception {
    storage_.create(navigation);
    pageNavigationCache_.put(navigation.getId(), navigation);
  }
  
  /**
   * This method should create or update the navigation object in the database
   * @param navigation
   * @throws Exception
   */
  public void update(PageNavigation navigation) throws Exception {
    storage_.save(navigation) ;
    pageNavigationCache_.select(new ExpireKeyStartWithSelector(navigation.getId())) ;
  }
  
  /**
   * This method should  remove the navigation object from the database
   * @param navigation
   * @throws Exception
   */
  public void remove(PageNavigation navigation) throws Exception {
    storage_.remove(navigation) ;
    pageNavigationCache_.remove(navigation.getId());
  }
  
  PageNavigation getPageNavigation(String id) throws Exception {
    PageNavigation navigation = (PageNavigation) pageNavigationCache_.get(id) ;
    if(navigation != null) return navigation ;
    navigation  = storage_.getPageNavigation(id) ;
    pageNavigationCache_.put(id, navigation);
    return navigation ; 
  }
  
  @SuppressWarnings("unused")
  public void initListener(ComponentPlugin listener) { }

}