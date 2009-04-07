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
package org.exoplatform.portal.config.jcr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Session;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.exoplatform.commons.utils.ObjectPageList;
import org.exoplatform.commons.utils.PageList;
import org.exoplatform.portal.application.PortletPreferences;
import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.config.Query;
import org.exoplatform.portal.config.model.Gadgets;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.ext.registry.RegistryEntry;
import org.exoplatform.services.jcr.ext.registry.RegistryService;
import org.exoplatform.services.listener.ListenerService;
import org.exoplatform.services.portletcontainer.pci.ExoWindowID;
import org.exoplatform.services.portletcontainer.pci.WindowID;
import org.picocontainer.Startable;

/**
 * Created by The eXo Platform SARL
 * Author : Tung Pham
 *          thanhtungty@gmail.com
 * Nov 14, 2007  
 */
public class DataStorageImpl implements DataStorage, Startable {
  
  public final static String CREATE_PORTAL_EVENT = "UserPortalConfigService.portal.event.createPortal".intern();
  public final static String REMOVE_PORTAL_EVENT = "UserPortalConfigService.portal.event.removePortal".intern();  
  public final static String UPDATE_PORTAL_EVENT = "UserPortalConfigService.portal.event.updatePortal".intern();
  
  final private static String PORTAL_DATA = "MainPortalData" ;
  final private static String USER_DATA = "UserPortalData";
  final private static String GROUP_DATA = "SharedPortalData";
  
  final private static String PORTAL_CONFIG_FILE_NAME = "portal-xml" ;
  final private static String NAVIGATION_CONFIG_FILE_NAME = "navigation-xml" ;
  final private static String GADGETS_CONFIG_FILE_NAME = "gadgets-xml" ; //TODO: dang.tung
  final private static String PAGE_SET_NODE = "pages" ;
  final private static String PORTLET_PREFERENCES_SET_NODE = "portletPreferences" ;

  private RegistryService regService_ ;     
  private DataMapper mapper_ = new DataMapper() ;
  private ListenerService listenerService;
  
  public DataStorageImpl(RegistryService service,ListenerService listenerService) throws Exception {
    regService_ = service ;
    this.listenerService = listenerService; 
  }

  public PortalConfig getPortalConfig(String portalName) throws Exception {
    String portalPath = getApplicationRegistryPath(PortalConfig.PORTAL_TYPE, portalName)
                        + "/"  + PORTAL_CONFIG_FILE_NAME;
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    RegistryEntry portalEntry ;
    try {
      portalEntry = regService_.getEntry(sessionProvider, portalPath) ;
    } catch (PathNotFoundException ie) {
      return null ;
    } finally {
      sessionProvider.close() ;
    }
    PortalConfig config = mapper_.toPortalConfig(portalEntry.getDocument()) ;
    return config ;
  }
  
  public void create(PortalConfig config) throws Exception {
    String portalAppPath = getApplicationRegistryPath(PortalConfig.PORTAL_TYPE, config.getName()) ;
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    try {
      RegistryEntry portalEntry = new RegistryEntry(PORTAL_CONFIG_FILE_NAME) ;
      mapper_.map(portalEntry.getDocument(), config) ;
      regService_.createEntry(sessionProvider, portalAppPath, portalEntry) ;
      //Broadcase event should be on UserPortalConfigService
      /**
     * Broadcast event should be on UserPortalConfigService
       * but in current implement, portal use 2 component to create new portal:
       * UserPortalConfigservice create/update/remove new portal from web ui
       * NewPortalConfigListener create new portal from config to create some predefined portal from
       * xml configuration.
       * this implement prevent us broadcast the event in UserPortalConfigService level.
       *
       * */
      listenerService.broadcast(CREATE_PORTAL_EVENT,this,config);
    }
    finally {
      sessionProvider.close() ;
    }
  }
  
  public void save(PortalConfig config) throws Exception {
    String portalAppPath = getApplicationRegistryPath(PortalConfig.PORTAL_TYPE, config.getName()) ;
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    try {
      RegistryEntry portalEntry = regService_.getEntry(sessionProvider, portalAppPath + "/" + PORTAL_CONFIG_FILE_NAME) ;
      mapper_.map(portalEntry.getDocument(), config) ;
      regService_.recreateEntry(sessionProvider, portalAppPath, portalEntry) ;
      listenerService.broadcast(UPDATE_PORTAL_EVENT,this,config);
    }
    finally {
      sessionProvider.close() ;
    }
  }

  public void remove(PortalConfig config) throws Exception {
    String portalPath = getApplicationRegistryPath(PortalConfig.PORTAL_TYPE, config.getName())
                        + "/"  + PORTAL_CONFIG_FILE_NAME;
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    try {
      regService_.removeEntry(sessionProvider, portalPath) ;
      listenerService.broadcast(REMOVE_PORTAL_EVENT,this,config);
    }
    finally {
      sessionProvider.close() ;
    }
  }

  public Page getPage(String pageId) throws Exception {
    String[] fragments = pageId.split("::") ;
    if(fragments.length < 3) {
      throw new Exception("Invalid PageId: " + "[" + pageId + "]") ;
    }
    String pagePath = getApplicationRegistryPath(fragments[0], fragments[1])
                      + "/" + PAGE_SET_NODE + "/" + fragments[2] ;
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    RegistryEntry pageEntry ;
    try {
      pageEntry = regService_.getEntry(sessionProvider, pagePath) ;      
    } catch (PathNotFoundException ie) {
      return null ;
    } finally {
      sessionProvider.close() ;
    }
    Page page = mapper_.toPageConfig(pageEntry.getDocument()) ;
    return page ;
  }
  
  public void create(Page page) throws Exception {
    String[] fragments = page.getPageId().split("::") ;
    String pageSetPath = getApplicationRegistryPath(fragments[0], fragments[1])
                         + "/" + PAGE_SET_NODE ;
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    try {
      RegistryEntry pageEntry = new RegistryEntry(page.getName()) ;
      mapper_.map(pageEntry.getDocument(), page) ;
      regService_.createEntry(sessionProvider, pageSetPath, pageEntry) ;
    }
    finally {
      sessionProvider.close() ;
    }
  }
  

  public void save(Page page) throws Exception {
    String[] fragments = page.getPageId().split("::") ;
    String pageSetPath = getApplicationRegistryPath(fragments[0], fragments[1]) + "/" + PAGE_SET_NODE ;
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    try {
      RegistryEntry pageEntry = regService_.getEntry(sessionProvider, pageSetPath + "/" + page.getName()) ;
      mapper_.map(pageEntry.getDocument(), page) ;
      regService_.recreateEntry(sessionProvider, pageSetPath, pageEntry) ;
    }
    finally {
      sessionProvider.close() ;
    }
  }
  
  public void remove(Page page) throws Exception {
    String[] fragments = page.getPageId().split("::") ;
    String pagePath = getApplicationRegistryPath(fragments[0], fragments[1])
                      + "/" + PAGE_SET_NODE + "/" + page.getName() ;
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    try {
      regService_.removeEntry(sessionProvider, pagePath) ;
    }
    finally {
      sessionProvider.close() ;
    }
  }
  
  public PageNavigation getPageNavigation(String fullId) throws Exception {
    String[] fragments = fullId.split("::") ;
    if(fragments.length < 2) {
      throw new Exception("Invalid PageNavigation Id: " + "[" + fullId + "]") ;
    }
    return getPageNavigation(fragments[0], fragments[1]) ;
  }
  
  public PageNavigation getPageNavigation(String ownerType, String id) throws Exception {
    String navigationPath = getApplicationRegistryPath(ownerType, id)
                            + "/" + NAVIGATION_CONFIG_FILE_NAME ;
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    RegistryEntry navigationEntry ;
    try {      
     navigationEntry = regService_.getEntry(sessionProvider, navigationPath) ;
    } catch (PathNotFoundException ie) {
      return null ;
    } finally {
      sessionProvider.close() ;
    }
    PageNavigation navigation = mapper_.toPageNavigation(navigationEntry.getDocument()) ;
    return navigation ;
  }
  
  public void create(PageNavigation navigation) throws Exception {
    String appRegPath = getApplicationRegistryPath(navigation.getOwnerType(), navigation.getOwnerId()) ; 
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    try {
      RegistryEntry NavigationEntry = new RegistryEntry(NAVIGATION_CONFIG_FILE_NAME) ;
      mapper_.map(NavigationEntry.getDocument(), navigation) ;
      regService_.createEntry(sessionProvider, appRegPath, NavigationEntry) ;
    }
    finally {
      sessionProvider.close() ;
    }
  }

  public void save(PageNavigation navigation) throws Exception {
    String appRegPath = getApplicationRegistryPath(navigation.getOwnerType(), navigation.getOwnerId()) ;
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    RegistryEntry navigationEntry = regService_.getEntry(sessionProvider, appRegPath + "/" + NAVIGATION_CONFIG_FILE_NAME ) ;
    mapper_.map(navigationEntry.getDocument(), navigation) ;
    regService_.recreateEntry(sessionProvider, appRegPath, navigationEntry) ;
    sessionProvider.close() ;
  }
  
  public void remove(PageNavigation navigation) throws Exception {
    String navigationPath = getApplicationRegistryPath(navigation.getOwnerType(), navigation.getOwnerId())
                            + "/" + NAVIGATION_CONFIG_FILE_NAME ;
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    try {
      regService_.removeEntry(sessionProvider, navigationPath) ;
    }
    finally {
      sessionProvider.close() ;;
    }
  }
  
  public Gadgets getGadgets(String id) throws Exception {
    String[] fragments = id.split("::") ;
    if(fragments.length < 2) {
      throw new Exception("Invalid Gadgets Id: " + "[" + id + "]") ;
    }
    String gadgetsPath = getApplicationRegistryPath(fragments[0], fragments[1])
    + "/" + GADGETS_CONFIG_FILE_NAME ;
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    RegistryEntry gadgetsEntry ;
    try {      
      gadgetsEntry = regService_.getEntry(sessionProvider, gadgetsPath) ;
    } catch (PathNotFoundException ie) {
      return null ;
    } finally {
      sessionProvider.close() ;
    }
    Gadgets gadgets = mapper_.toGadgets(gadgetsEntry.getDocument()) ;
    return gadgets ;
  }
  
  public void create(Gadgets gadgets) throws Exception {
    String appRegPath = getApplicationRegistryPath(gadgets.getOwnerType(), gadgets.getOwnerId()) ;
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    try {
      RegistryEntry gadgetsEntry = new RegistryEntry(GADGETS_CONFIG_FILE_NAME) ;
      mapper_.map(gadgetsEntry.getDocument(), gadgets) ;
      regService_.createEntry(sessionProvider, appRegPath, gadgetsEntry) ;
    }
    finally {
      sessionProvider.close() ;
    }
  }

  public void save(Gadgets gadgets) throws Exception {
    String appRegPath = getApplicationRegistryPath(gadgets.getOwnerType(), gadgets.getOwnerId()) ;
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    try {
      RegistryEntry gadgetsEntry = regService_.getEntry(sessionProvider, appRegPath + "/" + GADGETS_CONFIG_FILE_NAME) ;
      mapper_.map(gadgetsEntry.getDocument(), gadgets) ;
      regService_.recreateEntry(sessionProvider, appRegPath, gadgetsEntry) ;
    }
    finally {
      sessionProvider.close() ;
    }
  }
  
  public void remove(Gadgets gadgets) throws Exception {
    String gadgetsPath = getApplicationRegistryPath(gadgets.getOwnerType(), gadgets.getOwnerId())
                         + "/" + GADGETS_CONFIG_FILE_NAME ;
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    try {
      regService_.removeEntry(sessionProvider, gadgetsPath) ;
    }
    finally {
      sessionProvider.close() ;
    }
  }
  
  public PortletPreferences getPortletPreferences(WindowID windowID) throws Exception {
    String[] fragments = windowID.getOwner().split("#") ;
    if(fragments.length < 2) {
      throw new Exception("Invalid WindowID: " + "[" + windowID + "]");
    }
    ExoWindowID exoWindowID = (ExoWindowID) windowID ;
    String name = exoWindowID.getPersistenceId().replace('/', '_').replace(':', '_').replace('#', '_') ;
    String portletPreferencesPath = getApplicationRegistryPath(fragments[0], fragments[1])
                                    + "/" + PORTLET_PREFERENCES_SET_NODE
                                    + "/" + name ;
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    RegistryEntry portletPreferencesEntry ;
    try {
       portletPreferencesEntry = regService_.getEntry(sessionProvider, portletPreferencesPath) ;      
    } catch (PathNotFoundException ie) {
      return null ;
    } finally {
      sessionProvider.close() ;
    }
    PortletPreferences portletPreferences = mapper_.toPortletPreferences(portletPreferencesEntry.getDocument()) ;
    return portletPreferences ;
  }
  
  public void save(PortletPreferences portletPreferences) throws Exception {
    String name = portletPreferences.getWindowId().replace('/', '_').replace(':', '_').replace('#', '_') ;
    String portletPreferencesSet = getApplicationRegistryPath(portletPreferences.getOwnerType(), portletPreferences.getOwnerId())
                                    + "/" + PORTLET_PREFERENCES_SET_NODE ;
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    RegistryEntry entry ;
    try {
      entry = regService_.getEntry(sessionProvider, portletPreferencesSet + "/" + name) ;
      mapper_.map(entry.getDocument(), portletPreferences) ;
      regService_.recreateEntry(sessionProvider, portletPreferencesSet, entry) ;
    } catch (PathNotFoundException ie) {
      entry = new RegistryEntry(name) ;
      mapper_.map(entry.getDocument(), portletPreferences) ;
      regService_.createEntry(sessionProvider, portletPreferencesSet, entry) ;
    } finally {
      sessionProvider.close() ;      
    }
  }
  
  public void remove(PortletPreferences portletPreferences) throws Exception {
    String name = portletPreferences.getWindowId().replace('/', '_').replace(':', '_').replace('#', '_') ;
    String portletPreferencesPath = getApplicationRegistryPath(portletPreferences.getOwnerType(), portletPreferences.getOwnerId())
                                    + "/" + PORTLET_PREFERENCES_SET_NODE
                                    + "/" + name ;
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    try {
      regService_.removeEntry(sessionProvider, portletPreferencesPath) ;
    }
    finally {
      sessionProvider.close() ;
    }
  }
  
  @SuppressWarnings("unchecked")
  public PageList find(Query q) throws Exception {
    return find(q, null);
  }

  @SuppressWarnings("unchecked")
  public PageList find(Query q, Comparator sortComparator) throws Exception {
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    StringBuilder builder = new StringBuilder("select * from " + DataMapper.EXO_REGISTRYENTRY_NT) ;
    String registryNodePath = regService_.getRegistry(sessionProvider).getNode().getPath() ;
    generateLikeScript(builder, "jcr:path", registryNodePath + "/%") ;
    generateLikeScript(builder, DataMapper.EXO_DATA_TYPE, q.getClassType().getSimpleName()) ;
    generateContainScript(builder, DataMapper.EXO_OWNER_TYPE, q.getOwnerType()) ;
    generateContainScript(builder, DataMapper.EXO_OWNER_ID, q.getOwnerId()) ;
    generateContainScript(builder, DataMapper.EXO_NAME, q.getName()) ;
    generateContainScript(builder, DataMapper.EXO_TITLE, q.getTitle());
    Session session = regService_.getRegistry(sessionProvider).getNode().getSession() ;
    try {
      QueryManager queryManager = session.getWorkspace().getQueryManager() ;
      javax.jcr.query.Query query = queryManager.createQuery(builder.toString(), "sql") ;
      QueryResult result = query.execute() ;
      ArrayList<Object> list = new ArrayList<Object>() ;
      NodeIterator itr = result.getNodes() ;
      while(itr.hasNext()) {
        Node node = itr.nextNode() ;
        String entryPath = node.getPath().substring(registryNodePath.length() + 1) ;
        RegistryEntry entry = regService_.getEntry(sessionProvider, entryPath) ;
        list.add(mapper_.fromDocument(entry.getDocument(), q.getClassType())) ;
      }
      if(sortComparator != null) Collections.sort(list, sortComparator) ;
      return new ObjectPageList(list, 10);
    }
    finally {
      sessionProvider.close() ;
    }
  }
  
  public void start() {}

  public void stop() {}

  private void generateLikeScript(StringBuilder sql, String name, String value){
    if(value == null || value.length() < 1) return ;
    if(sql.indexOf(" where") < 0) sql.append(" where "); else sql.append(" and ");
    value = value.replace('*', '%') ;
    value = value.replace('?', '_');
    sql.append(name).append(" like '").append(value).append("'");
  }
  
  private void generateContainScript(StringBuilder sql, String name, String value){
    
    if(value == null || value.length() < 1) return ;
    
    if(value.indexOf("*")<0){
      if(value.charAt(0)!='*') value = "*"+value ;
      if(value.charAt(value.length()-1)!='*') value += "*" ;
    }
    value = value.replace('?', '_') ;
    
    if(sql.indexOf(" where") < 0) sql.append(" where "); else sql.append(" and ");
    sql.append("contains(").append(name).append(", '").append(value).append("')");
  }

  private String getApplicationRegistryPath(String ownerType, String ownerId) {
    String path = "" ;
    if(PortalConfig.PORTAL_TYPE.equals(ownerType)) {
      path = RegistryService.EXO_APPLICATIONS + "/" + PORTAL_DATA + "/" + ownerId;
    } else if(PortalConfig.USER_TYPE.equals(ownerType)) {
      path = RegistryService.EXO_USERS + "/" + ownerId + "/" + USER_DATA;
    } else if(PortalConfig.GROUP_TYPE.equals(ownerType)) {
      if(ownerId.charAt(0) != '/') ownerId = "/" + ownerId ;
      path = RegistryService.EXO_GROUPS + ownerId + "/" + GROUP_DATA ;
    }
    
    return path ;
  }
  
}
