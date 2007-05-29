/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.exoplatform.commons.utils.PageList;
import org.exoplatform.container.component.ComponentPlugin;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PageNode;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.config.model.Widgets;
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
  public UserPortalConfigService(UserACL userACL,
                                 DataStorage storage,
                                 CacheService cacheService,
                                 OrganizationService  orgService) throws Exception {
    storage_ = storage ;
    orgService_ = orgService;

    userACL_ = userACL;

    portalConfigCache_   = cacheService.getCacheInstance(PortalConfig.class.getName()) ;
    pageConfigCache_     = cacheService.getCacheInstance(Page.class.getName()) ;
    pageNavigationCache_ = cacheService.getCacheInstance(PageNavigation.class.getName()) ;
  }

  /**
   * This  method should load the PortalConfig object according to the portalName,  set the view and edit
   * permission according to the accessUser, load Naviagtion of the portal according to the portalName, 
   * find all the navigation of the groups that the user is belong to
   * @param userName
   * @return a UserPortalConfig object that contain the PortalConfig  and a list of the PageNavigation objects
   */
  public UserPortalConfig  getUserPortalConfig(String portalName, String accessUser) throws Exception {
    PortalConfig portal = storage_.getPortalConfig(portalName) ;
    if(portal == null || !userACL_.hasPermission(portal, accessUser)) return null ;

    List<PageNavigation> navigations = new ArrayList<PageNavigation>();
    PageNavigation navigation = storage_.getPageNavigation(PortalConfig.PORTAL_TYPE+"::"+portalName) ;
    if (navigation != null) navigations.add(navigation);    
    
    navigation = storage_.getPageNavigation(PortalConfig.USER_TYPE+"::"+accessUser) ;
    if (navigation != null) navigations.add(navigation) ;

    Collection memberships = orgService_.getMembershipHandler().findMembershipsByUser(accessUser);
    Iterator mitr = memberships.iterator() ;
    
    boolean newNav = true;
    while(mitr.hasNext()) {
      Membership m = (Membership) mitr.next() ;   
      String navId = PortalConfig.GROUP_TYPE+"::"+m.getGroupId();
      newNav = true;
      for(PageNavigation nav : navigations) {
        if(nav.getId().equals(navId)){
          newNav = false;
          break;
        }
      }
      if(newNav) {
        navigation = storage_.getPageNavigation(navId) ;
        if (navigation != null) navigations.add(navigation) ;
      }
    }   
    userACL_.computeNavigation(navigations, accessUser);
    
    ArrayList<Widgets> widgets = new ArrayList<Widgets>();
    Widgets widgetsItem = storage_.getWidgets(PortalConfig.PORTAL_TYPE+"::"+portalName) ;
    if(widgetsItem != null) widgets.add(widgetsItem);
    
    widgetsItem = storage_.getWidgets(PortalConfig.USER_TYPE+"::"+accessUser) ;
    if(widgetsItem != null) widgets.add(widgetsItem);
    
    return new UserPortalConfig(portal, navigations, widgets) ;
  }

  /**
   * This method  should create a  the portal  config, pages and navigation according to the template 
   * name
   * @param portalName
   * @param template
   * @return
   * @throws Exception
   */
  public UserPortalConfig createUserPortalConfig(String portalName, String template) throws Exception {
    PortalConfig pconfig = storage_.getPortalConfig(template) ;
    pconfig.setName(portalName) ;
    storage_.create(pconfig) ;
    
    List<PageNavigation> navigations = new ArrayList<PageNavigation>();
    PageNavigation navigation = storage_.getPageNavigation(PortalConfig.PORTAL_TYPE + "::" + template) ;
    if (navigation != null) {
      navigation.setOwnerId(portalName);      
      navigations.add(navigation);
    }
    
    Query<Page> query = new Query<Page>(PortalConfig.PORTAL_TYPE, template, null, Page.class) ;
    PageList pagelist = storage_.find(query) ;
    pagelist.setPageSize(10);
    int i = 1;
    while(i <= pagelist.getAvailablePage()) {
      List<?>  list = pagelist.getPage(i);
      for(Object ele : list) {
        Page page  = (Page)ele;
        String oldID = page.getPageId();
        page.setOwnerId(portalName);
        storage_.create(page);
        if(navigation != null) replacePageReference(navigation.getNodes(), oldID, page.getPageId());
      }
      i++;
    }
    
    if(navigation != null) storage_.create(navigation);
    //TODO implement widgets
    return new UserPortalConfig(pconfig, navigations, new ArrayList<Widgets>()); 
  }
  
  private void replacePageReference(List<PageNode> nodes, String oldID, String newID) {
    if(nodes == null) return;
    for(PageNode node : nodes) {
      if(oldID.equals(node.getPageReference())) node.setPageReference(newID);      
      replacePageReference(node.getChildren(), oldID, newID);
    }
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
    
    Query<Page> query = new Query<Page>(null, null, null, Page.class) ;
    query.setOwnerType(PortalConfig.PORTAL_TYPE) ;
    query.setOwnerId(portalName) ;
    PageList pagelist = storage_.find(query) ;
    pagelist.setPageSize(10);
    int i = 1;
    while(i < pagelist.getAvailablePage()) {
      List<?>  list = pagelist.getPage(i);
      for(Object ele : list) {
        storage_.remove((Page)ele);
      }
      i++;
    }
    
    PageNavigation navigation = storage_.getPageNavigation(PortalConfig.PORTAL_TYPE+"::"+portalName) ;
    if (navigation != null) remove(navigation);
  }
  
  /**
   * This method should create the PortalConfig  object
   * @param config
   * @throws Exception
   */
  public void create(PortalConfig config) throws Exception {    
    storage_.create(config) ;    
  }

  /**
   * This method should update the PortalConfig  object
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
    if(page == null) page  = storage_.getPage(pageId) ;
    if(page == null || !userACL_.hasPermission(page, accessUser)) return null;
    pageConfigCache_.put(pageId, page);
    return page ; 
  }
  
  public Page getPage(String pageId) throws Exception {
    Page page = (Page) pageConfigCache_.get(pageId) ;    
    if(page == null) page  = storage_.getPage(pageId) ;
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
    pageConfigCache_.remove(page.getPageId());
  }
  /**
   * This method should create the given page object
   * @param page
   * @throws Exception
   */
  public void create(Page page) throws Exception {
    storage_.create(page) ;
    pageConfigCache_.put(page.getPageId(), page);
  }

  /**
   * This method should update the given page object
   * @param page
   * @throws Exception
   */
  public void update(Page page) throws Exception {
    storage_.save(page) ;    
    pageConfigCache_.select(new ExpireKeyStartWithSelector(page.getPageId())) ;
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
  
  public PageNavigation getPageNavigation(String id, String accessUser) throws Exception {
    PageNavigation navigation = (PageNavigation) pageNavigationCache_.get(id) ;
    if(navigation == null) navigation  = storage_.getPageNavigation(id) ;
    if(navigation == null || !userACL_.hasPermission(navigation, accessUser)) return null;
    pageNavigationCache_.put(id, navigation);
    return navigation ;   
  }
  
  public void computeModifiable(PageNavigation navigation, String accessUser) throws Exception {
    userACL_.hasEditPermission(navigation.getCreator(), accessUser, navigation.getEditPermission());
  }
  
  @SuppressWarnings("unused")
  public void initListener(ComponentPlugin listener) { }
}