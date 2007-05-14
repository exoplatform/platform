/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.config.jcr;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.exoplatform.commons.utils.ObjectPageList;
import org.exoplatform.commons.utils.PageList;
import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.portlet.PortletPreferences;
import org.exoplatform.registry.ApplicationRegistry;
import org.exoplatform.registry.JCRRegistryService;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan.nguyen@exoplatform.com
 * Apr 20, 2007  
 */
public class DataStorageImpl implements DataStorage {
  
  final public static String PORTLET_TPREFERENCES = "portletPreferences";

  final private static String NT_FOLDER_TYPE = "nt:folder" ;
  final private static String EXO_DATA_TYPE = "exo:data" ;

  final private static String PORTAL = "portal" ;

  final private static String PORTAL_DATA = "MainPortalData" ;
  final private static String USER_DATA = "UserPortalData";
  final private static String GROUP_DATA = "SharedPortalData";

  final private static String PORTAL_CONFIG_FILE_NAME = "config.xml" ;
  final private static String NAVIGATION_CONFIG_FILE_NAME = "navigation.xml" ;
  final private static String PAGE_SET_NODE = "pages" ;

  private DataMapper mapper_ = new DataMapper();
  private JCRRegistryService jcrRegService_;
  
  public DataStorageImpl(JCRRegistryService jcrRegService) throws Exception{   
    jcrRegService_ = jcrRegService;
    jcrRegService_.createApplicationRegistry(new ApplicationRegistry(PORTAL_DATA), false);
  }
  
  public void create(PortalConfig config) throws Exception {
    Session session = jcrRegService_.getSession();
    Node appNode = jcrRegService_.getApplicationRegistryNode(session, PORTAL_DATA);
    Node portalNode = create(appNode, config.getName());
    Node configNode = portalNode.addNode(PORTAL_CONFIG_FILE_NAME, EXO_DATA_TYPE) ;
    portalNode.save();
    mapper_.map(configNode, config) ;    
    configNode.save() ;
    session.save() ;
    session.logout();
  }

  public void save(PortalConfig config) throws Exception {
    Session session = jcrRegService_.getSession();
    Node appNode = jcrRegService_.getApplicationRegistryNode(session, PORTAL_DATA);
    Node portalNode = create(appNode, config.getName());
    Node configNode = portalNode.getNode(PORTAL_CONFIG_FILE_NAME) ;
    mapper_.map(configNode, config) ;    
    configNode.save() ;
    session.save() ;
    session.logout();
  }

  public PortalConfig getPortalConfig(String portalName) throws Exception {
    Session session = jcrRegService_.getSession();
    Node appNode = jcrRegService_.getApplicationRegistryNode(session, PORTAL_DATA);
    if(!appNode.hasNode(portalName)) {
      session.logout();
      return null;
    }
    Node portalNode = appNode.getNode(portalName) ;
    if(!portalNode.hasNode(PORTAL_CONFIG_FILE_NAME)) {
      session.logout();
      return null;
    }
    Node configNode = portalNode.getNode(PORTAL_CONFIG_FILE_NAME) ;
    PortalConfig portalConfig = mapper_.toPortalConfig(configNode) ;
    session.logout();
    return portalConfig;
  }
  
  public List<PortalConfig> getAllPortalConfig() throws Exception{
    Session session = jcrRegService_.getSession();
    Node appNode = jcrRegService_.getApplicationRegistryNode(session, PORTAL_DATA);
    List<PortalConfig> configs = new ArrayList<PortalConfig>();
    NodeIterator iterator = appNode.getNodes();
    while(iterator.hasNext()){
      Node portalNode = iterator.nextNode();
      if(!portalNode.hasNode(PORTAL_CONFIG_FILE_NAME)) {
        continue;
      }
      Node configNode = portalNode.getNode(PORTAL_CONFIG_FILE_NAME) ;
      PortalConfig portalConfig = mapper_.toPortalConfig(configNode) ;
      configs.add(portalConfig);
    }
    session.logout();
    return configs;
  }

  public void remove(PortalConfig config) throws Exception {
    Session session = jcrRegService_.getSession();
    Node appNode = jcrRegService_.getApplicationRegistryNode(session, PORTAL_DATA);
    Node portalNode = appNode.getNode(config.getName()) ;
    Node portalConfigNode = portalNode.getNode(PORTAL_CONFIG_FILE_NAME) ;
    portalConfigNode.remove() ;
    portalNode.save() ;
    session.save() ;
    session.logout();
  }

//------------------------------------------------- Page--------------------------------------------

  public void create(Page page) throws Exception {
    Session session = jcrRegService_.getSession();
    Node pageSetNode = createPageSetNode(session, page.getOwnerType(), page.getOwnerId());
    Node pageNode = pageSetNode.addNode(page.getName(), EXO_DATA_TYPE) ;
    pageSetNode.save() ;
    mapper_.map(pageNode, page) ;
    pageNode.save() ;
    session.save() ;
    session.logout();
  }

  public void save(Page page) throws Exception {  
    Session session = jcrRegService_.getSession();
    Node pageSetNode = createPageSetNode(session, page.getOwnerType(), page.getOwnerId()); 
    Node pageNode = pageSetNode.getNode(page.getName()) ;
    mapper_.map(pageNode, page) ;
    pageNode.save() ;
    session.save() ;
    session.logout();
  }

  public void remove(Page page) throws Exception {
    Session session = jcrRegService_.getSession();
    Node pageSetNode = createPageSetNode(session, page.getOwnerType(), page.getOwnerId());  
    if(pageSetNode == null || !pageSetNode.hasNode(page.getName())) {
      session.logout();
      return;
    }
    Node pageNode = pageSetNode.getNode(page.getName()) ;
    pageNode.remove() ;    
    pageSetNode.save() ;
    session.save() ;
    session.logout();
  }

  public Page getPage(String pageId) throws Exception {
    if(pageId == null) return null;
    String [] components = pageId.split("::");
    if(components.length < 3) throw new Exception ("Invalid pageId :"+pageId);        
    Session session = jcrRegService_.getSession();
    Node pageSetNode = getPageSetNode(session, components[0], components[1]); 
    if(pageSetNode == null || !pageSetNode.hasNode(components[2])) {
      session.logout();
      return null;
    }
    Node pageNode = pageSetNode.getNode(components[2]) ;
    Page page = mapper_.toPage(pageNode) ;
    session.logout();
    return page;
  }
  
  private Node getPageSetNode(Session session, String ownerType, String ownerId) throws Exception {
    Node portalNode = getDataNode(session, ownerType, ownerId);
    if(portalNode == null || !portalNode.hasNode(PAGE_SET_NODE)) return  null;
    return portalNode.getNode(PAGE_SET_NODE) ;    
  }

  private Node createPageSetNode(Session session, String ownerType, String ownerId) throws Exception {
    Node portalNode = createDataNode(session, ownerType, ownerId);
    if (portalNode.hasNode(PAGE_SET_NODE)) return portalNode.getNode(PAGE_SET_NODE) ;
    Node pageSetNode = portalNode.addNode(PAGE_SET_NODE, NT_FOLDER_TYPE) ;
    portalNode.save() ;
    return pageSetNode;
  }

//------------------------------------------------- Page Navigation --------------------------------
  
  public void create(PageNavigation navigation) throws Exception {
    Session session = jcrRegService_.getSession();
    Node groupNode = createDataNode(session, navigation.getOwnerType(), navigation.getOwnerId());
    Node navigationNode = groupNode.addNode(NAVIGATION_CONFIG_FILE_NAME, EXO_DATA_TYPE) ;
    groupNode.save() ;
    mapper_.map(navigationNode, navigation) ;
    navigationNode.save();
    session.save() ;
    session.logout();
  }

  public void save(PageNavigation navigation) throws Exception {
    Session session = jcrRegService_.getSession();
    Node portalNode = createDataNode(session, navigation.getOwnerType(), navigation.getOwnerId());
    Node navigationNode = portalNode.getNode(NAVIGATION_CONFIG_FILE_NAME) ;
    mapper_.map(navigationNode, navigation) ;
    navigationNode.save();
    session.save() ;
    session.logout();
  }

  public void remove(PageNavigation navigation) throws Exception {
    Session session = jcrRegService_.getSession();
    Node portalNode = getDataNode(session, navigation.getOwnerType(), navigation.getOwnerId());
    if(portalNode == null || !portalNode.hasNode(NAVIGATION_CONFIG_FILE_NAME)) {
      session.logout();
      return;
    }
    Node navigationNode = portalNode.getNode(NAVIGATION_CONFIG_FILE_NAME) ;
    navigationNode.remove() ;    
    portalNode.save() ;       
    session.save() ;
    session.logout();
  }

  public PageNavigation getPageNavigation(String id) throws Exception {
    if(id == null) return null;
    Session session = jcrRegService_.getSession();
    String [] components = id.split("::");
    if(components.length < 2) throw new Exception ("Invalid navigationId :"+id);

    Node portalNode = getDataNode(session, components[0], components[1]);
    if(portalNode == null || !portalNode.hasNode(NAVIGATION_CONFIG_FILE_NAME)) {
      session.logout();
      return null;
    }
    Node navigationNode  = portalNode.getNode(NAVIGATION_CONFIG_FILE_NAME) ;
    PageNavigation nav = mapper_.toPageNavigation(navigationNode) ;
    session.logout();
    return nav;
  }
  
//------------------------------------------------- Portlet Preferences ----------------------------
  
  public void savePortletPreferencesConfig(PortletPreferences portletPreferences) throws Exception {
    Session session = jcrRegService_.getSession();
    Node appNode = jcrRegService_.getApplicationRegistryNode(session, PORTAL_DATA);
    Node rootNode = create(appNode, portletPreferences.getOwner());
    Node portletPreNode = null;
    if(rootNode.hasNode(PORTLET_TPREFERENCES)) {
      portletPreNode = rootNode.getNode(PORTLET_TPREFERENCES) ;
    } else {
      portletPreNode = rootNode.addNode(PORTLET_TPREFERENCES, NT_FOLDER_TYPE) ;
      rootNode.save();
    }
    String name  = portletPreferences.getWindowId().replace('/', '_').replace(':', '_');
    Node node = portletPreNode.addNode(name, EXO_DATA_TYPE);
    portletPreNode.save();
    mapper_.map(node, portletPreferences) ;    
    node.save() ;
    session.save() ;
    session.logout();
  }
  
  @SuppressWarnings("unchecked")
  public  PageList find(org.exoplatform.portal.config.Query cq) throws Exception {
    StringBuilder  builder = new StringBuilder("select * from "+NT_FOLDER_TYPE);
    generateScript(builder, "dataType", cq.getClassType().getSimpleName());
    generateScript(builder, "name", cq.getName());
    generateScript(builder, "ownerType", cq.getOwnerType());
    generateScript(builder, "ownerId", cq.getOwnerId());
    
    Session session = jcrRegService_.getSession();
    QueryManager queryManager = session.getWorkspace().getQueryManager() ;
    Query query = queryManager.createQuery(builder.toString(), "sql") ;
    QueryResult queryResult = query.execute() ;
    ArrayList<Object> list = new ArrayList<Object>();
    NodeIterator iterator = queryResult.getNodes();
    while(iterator.hasNext()){
      Node node = iterator.nextNode();
      String  xml = node.getProperty("data").getValue().getString() ;
      list.add(mapper_.fromXML(xml, cq.getClassType())) ;
    }
    return new ObjectPageList(list, 20);
  }
  
  private void generateScript(StringBuilder sql, String name, String value){
    if(value == null || value.length() < 1) return ;
    if(sql.indexOf(" where") < 0) sql.append(" where "); else sql.append(" and "); 
    value = value.replace('*', '%') ;
    sql.append(name).append(" like '").append(value).append("'");
  }
  
//------------------------------------------------- Util method-------- ----------------------------
  
  private Node getDataNode(Session session, String ownerType, String ownerId) throws Exception {
    if(ownerType.equals(PortalConfig.PORTAL_TYPE)) {
      Node appNode = jcrRegService_.getApplicationRegistryNode(session, PORTAL_DATA);
      if(appNode.hasNode(ownerId)) return appNode.getNode(ownerId);
      return null;
    }
    
    if(ownerType.equals(PortalConfig.USER_TYPE)){
      Node node = jcrRegService_.getApplicationRegistryNode(session, ownerId, USER_DATA);
      if(node == null || !node.hasNode(ownerId)) return null;
      node = node.getNode(ownerId);
      if(!node.hasNode(EXO_DATA_TYPE)) return null;
      node = node.getNode(EXO_DATA_TYPE);
      if(node.hasNode(PORTAL)) return node.getNode(PORTAL);
      return null;
    } 
    
    if(ownerType.equals(PortalConfig.GROUP_TYPE)){
      Node node = jcrRegService_.getApplicationRegistryNode(session, GROUP_DATA);
      String [] groups = ownerId.split("/");
      for(String group : groups) {
        if(group.trim().length() < 1) continue;
        if(!node.hasNode(group)) return null;
        node = node.getNode(group);
      }
      if(!node.hasNode(EXO_DATA_TYPE)) return null;
      node = node.getNode(EXO_DATA_TYPE);
      if(node.hasNode(PORTAL)) return node.getNode(PORTAL);
    }
    
    return null;
  }

  private Node createDataNode(Session session, String ownerType, String ownerId) throws Exception {
    if(ownerType.equals(PortalConfig.PORTAL_TYPE)) {
      Node appNode = jcrRegService_.getApplicationRegistryNode(session, PORTAL_DATA);
      return create(appNode, ownerId);
    } 
    
    if(ownerType.equals(PortalConfig.USER_TYPE)){
      jcrRegService_.createUserHome(ownerId, false);
      jcrRegService_.createApplicationRegistry(ownerId, new ApplicationRegistry(USER_DATA), false);
      Node appNode = jcrRegService_.getApplicationRegistryNode(session, ownerId, USER_DATA);
      Node portalNode = create(appNode, ownerId);
      return create(create(portalNode, EXO_DATA_TYPE), PORTAL);
    }
    
    if(ownerType.equals(PortalConfig.GROUP_TYPE)){
      jcrRegService_.createApplicationRegistry(new ApplicationRegistry(GROUP_DATA), false);
      Node appNode = jcrRegService_.getApplicationRegistryNode(session, GROUP_DATA);
      String [] groups = ownerId.split("/");
      for(String group : groups) {
        if(group.trim().length() < 1) continue;
        appNode = create(appNode, group);
      }
      return create(create(appNode, EXO_DATA_TYPE), PORTAL);
    }
    
    return null;
  }

  private Node create(Node parent, String name) throws Exception {
    if(parent.hasNode(name)) return parent.getNode(name);    
    Node node = parent.addNode(name, NT_FOLDER_TYPE);
    parent.save();
    return node;    
  }
  
}
