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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.exoplatform.commons.utils.PageList;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.ComponentPlugin;
import org.exoplatform.portal.application.PortletPreferences;
import org.exoplatform.portal.config.model.Application;
import org.exoplatform.portal.config.model.Container;
import org.exoplatform.portal.config.model.Gadgets;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PageNode;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.config.model.Widgets;
import org.exoplatform.services.cache.CacheService;
import org.exoplatform.services.cache.ExoCache;
import org.exoplatform.services.cache.ExpireKeyStartWithSelector;
import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.portletcontainer.PCConstants;
import org.exoplatform.services.portletcontainer.PortletContainerService;
import org.exoplatform.services.portletcontainer.helper.PortletWindowInternal;
import org.exoplatform.services.portletcontainer.pci.ExoWindowID;
import org.exoplatform.services.portletcontainer.pci.Input;
import org.exoplatform.services.portletcontainer.pci.model.ExoPortletPreferences;
import org.exoplatform.services.portletcontainer.pci.model.Portlet;
import org.exoplatform.services.portletcontainer.plugins.pc.PortletApplicationsHolder;
import org.exoplatform.services.portletcontainer.plugins.pc.portletAPIImp.PortletPreferencesImp;
import org.exoplatform.services.portletcontainer.plugins.pc.portletAPIImp.persistenceImp.PersistenceManager;
/**
 * Created by The eXo Platform SAS
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
  protected ExoCache widgetsCache_ ;
  protected ExoCache gadgetsCache_ ;
  
  private NewPortalConfigListener newPortalConfigListener_ ;
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
    widgetsCache_ = cacheService.getCacheInstance(Widgets.class.getName()) ;
    gadgetsCache_ = cacheService.getCacheInstance(Gadgets.class.getName()) ;
  }

  /**
   * This  method should load the PortalConfig object according to the portalName,  set the view and edit
   * permission according to the accessUser, load Naviagtion of the portal according to the portalName, 
   * find all the navigation of the groups that the user is belong to
   * @param userName
   * @return a UserPortalConfig object that contain the PortalConfig  and a list of the PageNavigation objects
   */
  public UserPortalConfig  getUserPortalConfig(String portalName, String accessUser) throws Exception {
    PortalConfig portal = (PortalConfig) portalConfigCache_.get(portalName) ;
    if(portal == null) {
      portal = storage_.getPortalConfig(portalName) ;
      if(portal != null) portalConfigCache_.put(portalName, portal);
    }
    if(portal == null || !userACL_.hasPermission(portal, accessUser)) return null ;
    
    List<PageNavigation> navigations = new ArrayList<PageNavigation>();
    PageNavigation navigation = getPageNavigation(PortalConfig.PORTAL_TYPE+"::"+portalName) ;
    if (navigation != null) { 
      navigation.setModifiable(userACL_.hasEditPermission(portal, accessUser));
      navigations.add(navigation) ;
    }
    
    if(accessUser == null) {
      navigation = getPageNavigation(PortalConfig.GROUP_TYPE+"::"+userACL_.getGuestsGroup()) ;
      if(navigation != null) navigations.add(navigation) ;
    } else {
      navigation = getPageNavigation(PortalConfig.USER_TYPE+"::"+accessUser) ;
      if (navigation != null) {
        navigation.setModifiable(true);
        navigations.add(navigation);  
      }
      
      Collection<?> groups = null;
      if(userACL_.getSuperUser().equals(accessUser)) groups = orgService_.getGroupHandler().getAllGroups();
      else groups = orgService_.getGroupHandler().findGroupsOfUser(accessUser);
      Iterator<?> iterator = groups.iterator() ;
      while(iterator.hasNext()) {
        Group m = (Group) iterator.next() ;   
        String groupId = m.getId().trim();
        navigation = getPageNavigation(PortalConfig.GROUP_TYPE+"::"+groupId) ;
        if(navigation == null) continue;
        navigation.setModifiable(userACL_.hasEditPermission(navigation, accessUser));
        navigations.add(navigation) ;
      }
    }
    
    Widgets userWidgets = getWidgets(PortalConfig.USER_TYPE+"::"+accessUser) ;
    Collections.sort(navigations, new Comparator<PageNavigation>() {
      public int compare(PageNavigation nav1, PageNavigation nav2) {
        return nav1.getPriority()- nav2.getPriority() ;
      }
    });
    //TODO; dang.tung - add new gadgets
    Gadgets userGadgets = getGadgets(PortalConfig.USER_TYPE+"::"+accessUser) ;
    Collections.sort(navigations, new Comparator<PageNavigation>() {
      public int compare(PageNavigation nav1, PageNavigation nav2) {
        return nav1.getPriority()- nav2.getPriority() ;
      }
    });
    
    
    return new UserPortalConfig(portal, navigations, userWidgets, userGadgets) ;
  }
 
  public List<String> getMakableNavigations(String remoteUser)throws Exception {
    List<String> list = new ArrayList<String>();
    Collection<?> groups = null;
    if(remoteUser.equals(userACL_.getSuperUser()))
      groups = orgService_.getGroupHandler().getAllGroups();
    else   groups = orgService_.getGroupHandler().findGroupByMembership(remoteUser, userACL_.getMakableMT());
    if(groups != null) {
      Iterator<?> iterator = groups.iterator() ;
      while(iterator.hasNext()) {
        Group m = (Group)  iterator.next() ;   
        String groupId = m.getId().trim();
        if(groupId.charAt(0) == '/') groupId = groupId.substring(1);
        list.add(groupId);
      }
    }
    return list;
  }
  
  /**
   * This method  should create a  the portal  config, pages and navigation according to the template 
   * name
   * @param portalName
   * @param template
   * @return
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  public void createUserPortalConfig(String portalName, String template) throws Exception {
    NewPortalConfig portalConfig = newPortalConfigListener_.getPortalConfig(PortalConfig.PORTAL_TYPE);
    portalConfig.setTemplateOwner(template);
    portalConfig.getPredefinedOwner().clear();
    portalConfig.getPredefinedOwner().add(portalName);
    newPortalConfigListener_.initPortalTypeDB(portalConfig);
  }
  
  /**
   * This method should remove the PortalConfig, Page and PageNavigation  that  belong to the portal 
   * in the database. The method should broadcast the event UserPortalConfigService.portal.onRemove
   * @param config
   * @throws Exception
   */
  public void  removeUserPortalConfig(String portalName) throws Exception {
    Query<Page> query = new Query<Page>(null, null, null, Page.class) ;
    query.setOwnerType(PortalConfig.PORTAL_TYPE) ;
    query.setOwnerId(portalName) ;
    PageList pageList = storage_.find(query) ;
    pageList.setPageSize(10) ;
    int i = 1 ;
    while(i <= pageList.getAvailablePage()) {
      List<?> list = pageList.getPage(i) ;
      Iterator<?> itr = list.iterator() ;
      while(itr.hasNext()) {
        Page page = (Page) itr.next() ;
        remove(page) ;
      }
      i++;
    }
    
    String id = PortalConfig.PORTAL_TYPE + "::" + portalName ;
    PageNavigation navigation = storage_.getPageNavigation(id) ;
    if (navigation != null) remove(navigation) ;
    
    Query<PortletPreferences> portletPrefQuery = new Query<PortletPreferences>(null, null, null, PortletPreferences.class) ;
    portletPrefQuery.setOwnerType(PortalConfig.PORTAL_TYPE) ;
    portletPrefQuery.setOwnerId(portalName) ;
    pageList = storage_.find(portletPrefQuery) ;
    pageList.setPageSize(10) ;
    i = 1 ;
    while(i <= pageList.getAvailablePage()) {
      List<?> list = pageList.getPage(i) ;
      Iterator<?> itr = list.iterator() ;
      while(itr.hasNext()) {
        PortletPreferences portletPreferences = (PortletPreferences) itr.next() ;
        storage_.remove(portletPreferences) ;
      }
      i++ ;
    }
    
    PortalConfig config = storage_.getPortalConfig(portalName) ;
    portalConfigCache_.remove(config.getName()) ;
    if (config != null) storage_.remove(config) ;
  }
  
  /**
   * This method should update the PortalConfig  object
   * @param portal
   * @throws Exception
   */
  public void update(PortalConfig portal) throws Exception { 
    storage_.save(portal) ; 
    portalConfigCache_.select(new ExpireKeyStartWithSelector(portal.getName())) ;
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
  
  public PageNavigation getPageNavigation(String id) throws Exception{
    PageNavigation navigation = (PageNavigation) pageNavigationCache_.get(id);
    if(navigation == null) navigation  = storage_.getPageNavigation(id) ;
    return navigation;
  }

  //TODO: dang.tung
  //--------------------------------------------------------------------------
  public void create(Gadgets gadgets) throws Exception {
    storage_.create(gadgets) ;
    gadgetsCache_.put(gadgets.getId(), gadgets) ;
  }
  
  public void update(Gadgets gadgets) throws Exception {
    storage_.save(gadgets) ;
    gadgetsCache_.select(new ExpireKeyStartWithSelector(gadgets.getId())) ;
  }
  
  public void remove(Gadgets gadgets) throws Exception {
    storage_.remove(gadgets) ;
    gadgetsCache_.remove(gadgets.getId()) ;
  }
  
  public Gadgets getGadgets(String id) throws Exception {
    Gadgets gadgets = (Gadgets) pageConfigCache_.get(id) ;
    if(gadgets != null) return gadgets;
    gadgets = storage_.getGadgets(id) ;
    gadgetsCache_.put(id, gadgets) ;
    return gadgets ;
  }
  //--------------------------------------------------------------------------
  
  /**
   * This method should create the widgets object in the database
   * @param widgets
   * @throws Exception
   */
  public void create(Widgets widgets) throws Exception {
    storage_.create(widgets) ;
    widgetsCache_.put(widgets.getId(), widgets) ;
  }
  
  /**
   * This method should update the widgets object in the database
   * @param widgets
   * @throws Exception
   */
  public void update(Widgets widgets) throws Exception {
    storage_.save(widgets) ;
    widgetsCache_.select(new ExpireKeyStartWithSelector(widgets.getId())) ;
  }
  
  /**
   * This method should remove the widgets object from the database
   * @param widgets
   * @throws Exception
   */
  public void remove(Widgets widgets) throws Exception {
    storage_.remove(widgets) ;
    widgetsCache_.remove(widgets.getId()) ;
  }
  
  /**
   * This method load the widgets according to the id
   * @param id
   * @return Widgets
   * @throws Exception
   */
  public Widgets getWidgets(String id) throws Exception {
    Widgets widgets = (Widgets) pageConfigCache_.get(id) ;
    if(widgets != null) return widgets;
    widgets = storage_.getWidgets(id) ;
    widgetsCache_.put(id, widgets) ;
    return widgets ;
  }
  
  /**
   * This method creates new page from an existing page and links new page to a PageNode
   * @return PageNode
   * @throws Exception
   */
  public PageNode createNodeFromPageTemplate(String nodeName, String nodeLabel,
      String pageId, Map<String, String[]> portletPreferences, String accessUser) throws Exception {
    
    Page page = storage_.getPage(pageId) ;
    page = createPageFromPageTemplate(page, nodeName, portletPreferences, accessUser) ;
    create(page) ;
    PageNode pageNode = new PageNode() ;
    if(nodeLabel == null || nodeLabel.trim().length() < 1) nodeLabel = nodeName  ;
    pageNode.setName(nodeName) ;
    pageNode.setLabel(nodeLabel) ;
    pageNode.setPageReference(page.getPageId()) ;
    return  pageNode ;
  }
  
  private Page createPageFromPageTemplate(Page page, String pageName, Map<String, String[]> portletPreferences, String accessUser) throws Exception {
    
    String newId = PortalConfig.USER_TYPE + "::" + accessUser + "::" + pageName ;
    page.setName(pageName) ;
    page.setPageId(newId) ;
    page.setOwnerType(PortalConfig.USER_TYPE) ;
    page.setOwnerId(accessUser) ;
    page.setAccessPermissions(null) ;
    page.setEditPermission(null) ;
    List<Application> apps = new ArrayList<Application>(5) ; 
    getApplications(apps, page) ;
    Map<String, String[]> mergedPreferences ;
    for(Application ele : apps) {
      String appType = ele.getApplicationType() ;
      if(appType == null || org.exoplatform.web.application.Application.EXO_PORTLET_TYPE.equals(appType)) {
        mergedPreferences = new HashMap<String, String[]>(getPreferencesMap(ele)) ;
        mergedPreferences.putAll(portletPreferences) ;
        renewInstanceId(ele, accessUser) ;
        setPreferences(ele, mergedPreferences) ;
      } else {
        renewInstanceId(ele, accessUser) ;
      }
    }
    return page ;
  }
  
  private void getApplications(List<Application> apps, Object component) {
    if(component instanceof Application) {
      apps.add((Application) component) ;
    } else if(component instanceof Container) {
      Container container = (Container) component ;
      List<Object> children = container.getChildren() ;
      if(children != null) for(Object ele : children) getApplications(apps, ele) ;
    }    
  }

  private Map<String, String[]> getPreferencesMap(Application app) throws Exception {
   
    ExoWindowID windowID = new ExoWindowID(app.getInstanceId()) ;
    Input input = new Input() ;
    input.setInternalWindowID(windowID) ;
    PortalContainer container = PortalContainer.getInstance() ;
    PortletContainerService pcServ = (PortletContainerService) container.getComponentInstanceOfType(PortletContainerService.class) ;
    return pcServ.getPortletPreference(input) ;
  }
  
  private void renewInstanceId(Application app, String ownerId) {
    ExoWindowID newExoWindowID = new ExoWindowID(app.getInstanceId()) ;
    newExoWindowID.setOwner(PortalConfig.USER_TYPE + "#" + ownerId) ;
    newExoWindowID.setUniqueID(String.valueOf(newExoWindowID.hashCode())) ;
    app.setInstanceId(newExoWindowID.generatePersistenceId()) ;
  }
  
  private void setPreferences(Application portlet, Map<String, String[]> portletPreferences) throws Exception {
    if(portletPreferences == null || portletPreferences.size() < 1) return ;
    ExoWindowID windowID = new ExoWindowID(portlet.getInstanceId()) ;
    Input input = new Input() ;
    input.setInternalWindowID(windowID) ;
    PortalContainer container = PortalContainer.getInstance() ;
    PortletContainerService pcServ = (PortletContainerService) container.getComponentInstanceOfType(PortletContainerService.class) ;
    pcServ.setPortletPreference2(input, portletPreferences) ;
  }
  
  public Page createPageTemplate(String temp, String ownerType, String ownerId) throws Exception {
    Page page = newPortalConfigListener_.createPageFromTemplate(temp);
    page.setOwnerType(ownerType) ;
    page.setOwnerId(ownerId) ;
    List<Application> apps = new ArrayList<Application>(3) ; 
    getApplications(apps, page) ;
    if(!apps.isEmpty()) {
      for(Application ele : apps) {
        makeInstanceId(ele, ownerType, ownerId) ;
      }
      createPortletPreferences(apps, temp) ;      
    }
    return page ;
  }
  
  private void makeInstanceId(Application app, String ownerType, String ownerId) {
    StringBuilder builder = new StringBuilder(20) ;
    builder.append(ownerType + "#" + ownerId + ":").append(app.getInstanceId()).append("/" + builder.hashCode()) ;
    app.setInstanceId(builder.toString()) ;
  }
  
  private void createPortletPreferences(List<Application> apps, String temp) throws Exception {
    List<PortletPreferences> preferencesSet = newPortalConfigListener_.createPortletPreferencesFromTemplate(temp).getPortlets() ;
    if(preferencesSet == null || preferencesSet.size() < 1) return ;
    for(Application ele : apps) {
      String appType = ele.getApplicationType() ;
      if(appType == null || org.exoplatform.web.application.Application.EXO_PORTLET_TYPE.equals(appType)) {
        savePortletPreferences(ele, preferencesSet) ;
      }
    }
  }
  
  private void savePortletPreferences(Application app, List<PortletPreferences> preferencesSet) throws Exception {
    ExoWindowID windowID = new ExoWindowID(app.getInstanceId())  ;
    String tmp = "/" + windowID.getPortletApplicationName() + "/" + windowID.getPortletName() ;
    for(PortletPreferences preferences : preferencesSet) {
      if(tmp.equals(preferences.getWindowId())) {
        String[] fragments = windowID.getOwner().split("#") ;
        preferences.setOwnerType(fragments[0]) ;
        preferences.setOwnerId(fragments[1]) ;
        preferences.setWindowId(windowID.getPersistenceId()) ;
        storage_.save(preferences) ;
        break ;
      }
    }
  }
    
  @SuppressWarnings("unused")
  public void initListener(ComponentPlugin listener) { 
    if(listener instanceof  NewPortalConfigListener) {
      newPortalConfigListener_ = (NewPortalConfigListener)listener;
    }
  }
  
  public String getDefaultPortal() { return newPortalConfigListener_.getDefaultPortal(); }
}