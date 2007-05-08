/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.config.jcr;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.portlet.PortletPreferences;
import org.exoplatform.registry.ApplicationRegistry;
import org.exoplatform.registry.JCRRegistryService;
import org.exoplatform.registry.ServiceRegistry;
import org.exoplatform.services.jcr.RepositoryService;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan.nguyen@exoplatform.com
 * Apr 20, 2007  
 */
public class DataStorageImpl2 implements DataStorage {
  
  final public static String PORTLET_TPREFERENCES = "portletPreferences";

  final private static String NT_FOLDER_TYPE = "nt:folder" ;
  final private static String EXO_DATA_TYPE = "exo:data" ;

  final private static String PORTAL = "portal" ;

  final private static String WORKSPACE = "production" ;
  final private static String PORTAL_APP = "PortalApp" ;

  final private static String HOME = "home";
  final private static String USER_DATA = "user";
  final private static String GROUP_DATA = "group";

  final private static String PORTAL_CONFIG_FILE_NAME = "config.xml" ;
  final private static String NAVIGATION_CONFIG_FILE_NAME = "navigation.xml" ;
  final private static String PAGE_SET_NODE = "pages" ;

  private  RepositoryService service_ ;
  private DataMapper mapper_ = new DataMapper();
  private JCRRegistryService jcrRegService_;
  public DataStorageImpl2(RepositoryService service, JCRRegistryService jcrRegService) throws Exception{
    service_ = service ;
    Session session = service_.getRepository().getSystemSession(WORKSPACE) ;
    Node rootNode = session.getRootNode() ;
    create(rootNode, PORTAL_APP);
    create(create(rootNode, USER_DATA), HOME);
    create(create(rootNode, GROUP_DATA), HOME);
    session.save();
    jcrRegService_ = jcrRegService;
    jcrRegService_.createApplicationRegistry(new ApplicationRegistry(PORTAL_APP), false);
  }
  
  private Session getSession() throws Exception{
   return jcrRegService_.getSession();
  }
  
  private Node getPortalAppNode() throws Exception{
    return jcrRegService_.getApplicationRegistryNode(getSession(), PORTAL_APP); 
  }
  
  private Node getUsersNode() throws Exception{
    return getSession().getRootNode().getNode(USER_DATA);
  }
  
  private Node getGroupsNode()throws Exception{
    return getSession().getRootNode().getNode(GROUP_DATA);
  }
  
  public void create(PortalConfig config) throws Exception {
    Session session = getSession();
    Node node = create(getPortalAppNode(), config.getName());
    Node portalNode = node.addNode(PORTAL_CONFIG_FILE_NAME, EXO_DATA_TYPE) ;
    node.save();
    mapper_.map(portalNode, config) ;    
    portalNode.save() ;
    session.save() ;
  }

  public void save(PortalConfig config) throws Exception {
    Session session = getSession() ;
    Node node = create(getPortalAppNode(), config.getName());
    Node portalNode = node.getNode(PORTAL_CONFIG_FILE_NAME) ;
    mapper_.map(portalNode, config) ;    
    portalNode.save() ;
    session.save() ;
  }

  public PortalConfig getPortalConfig(String portalName) throws Exception {
    Node appNode = getPortalAppNode() ;
    if(!appNode.hasNode(portalName)) return null;
    Node portalNode = appNode.getNode(portalName) ;
    if(!portalNode.hasNode(PORTAL_CONFIG_FILE_NAME)) return null;
    Node portalConfigNode = portalNode.getNode(PORTAL_CONFIG_FILE_NAME) ;
    PortalConfig portalConfig = mapper_.toPortalConfig(portalConfigNode) ;
    return portalConfig;
  }

  public void remove(PortalConfig config) throws Exception {
    Session session = getSession();
    Node portalAppNode = getPortalAppNode() ;
    Node portalNode = portalAppNode.getNode(config.getName()) ;
    Node portalConfigNode = portalNode.getNode(PORTAL_CONFIG_FILE_NAME) ;
    portalConfigNode.remove() ;
    portalNode.save() ;
    session.save() ;
  }

//------------------------------------------------- Page--------------------------------------------

  public void create(Page page) throws Exception {
    Session session = service_.getRepository().getSystemSession(WORKSPACE) ;
    Node pageSetNode = createPageSetNode(session, page.getOwnerType(), page.getOwnerId());
    Node pageNode = pageSetNode.addNode(page.getName(), EXO_DATA_TYPE) ;
    pageSetNode.save() ;
    mapper_.map(pageNode, page) ;
    pageNode.save() ;
    session.save() ;
  }

  public void save(Page page) throws Exception {  
    Session session = service_.getRepository().getSystemSession(WORKSPACE) ;
    Node pageSetNode = createPageSetNode(session, page.getOwnerType(), page.getOwnerId()); 
    Node pageNode = pageSetNode.getNode(page.getName()) ;
    mapper_.map(pageNode, page) ;
    pageNode.save() ;
    session.save() ;
  }

  public void remove(Page page) throws Exception {
    Session session = getSession() ;
    Node pageSetNode = createPageSetNode(session, page.getOwnerType(), page.getOwnerId());  
    if(pageSetNode == null || !pageSetNode.hasNode(page.getName())) return;
    Node pageNode = pageSetNode.getNode(page.getName()) ;
    pageNode.remove() ;    
    pageSetNode.save() ;
    session.save() ;
  }

  public Page getPage(String pageId) throws Exception {
    String [] components = pageId.split("::");
    if(components.length < 3) throw new Exception ("Invalid pageId :"+pageId);        
    Session session = getSession() ;
    Node pageSetNode = getPageSetNode(session, components[0], components[1]); 
    if(pageSetNode == null || !pageSetNode.hasNode(components[2]))  return null;
    Node pageNode = pageSetNode.getNode(components[2]) ;
    return mapper_.toPage(pageNode) ;
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
    Session session = getSession() ;
    Node groupNode = createDataNode(session, navigation.getOwnerType(), navigation.getOwnerId());
    Node navigationNode = groupNode.addNode(NAVIGATION_CONFIG_FILE_NAME, EXO_DATA_TYPE) ;
    groupNode.save() ;
    mapper_.map(navigationNode, navigation) ;
    navigationNode.save();
    session.save() ;
  }

  public void save(PageNavigation navigation) throws Exception {
    Session session = service_.getRepository().getSystemSession(WORKSPACE) ;
    Node portalNode = createDataNode(session, navigation.getOwnerType(), navigation.getOwnerId());
    Node navigationNode = portalNode.getNode(NAVIGATION_CONFIG_FILE_NAME) ;
    mapper_.map(navigationNode, navigation) ;
    navigationNode.save();
    session.save() ;
  }

  public void remove(PageNavigation navigation) throws Exception {
    Session session = service_.getRepository().getSystemSession(WORKSPACE) ;
    Node portalNode = getDataNode(session, navigation.getOwnerType(), navigation.getOwnerId());
    if(portalNode == null || !portalNode.hasNode(NAVIGATION_CONFIG_FILE_NAME)) return;
    Node navigationNode = portalNode.getNode(NAVIGATION_CONFIG_FILE_NAME) ;
    navigationNode.remove() ;    
    portalNode.save() ;       
    session.save() ;
  }

  public PageNavigation getPageNavigation(String id) throws Exception {
    Session session = service_.getRepository().getSystemSession(WORKSPACE) ;
    String [] components = id.split("::");
    if(components.length < 2) throw new Exception ("Invalid navigationId :"+id);

    Node portalNode = getDataNode(session, components[0], components[1]);
    if(portalNode == null || !portalNode.hasNode(NAVIGATION_CONFIG_FILE_NAME)) return null;
    Node navigationNode  = portalNode.getNode(NAVIGATION_CONFIG_FILE_NAME) ;
    return mapper_.toPageNavigation(navigationNode) ;
  }
  
//------------------------------------------------- Portlet Preferences ----------------------------
  
  public void savePortletPreferencesConfig(PortletPreferences portletPreferences) throws Exception {
    Session session = service_.getRepository().getSystemSession(WORKSPACE) ;
    Node rootNode = create(getPortalAppNode(), portletPreferences.getOwner());
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
  }
  
//------------------------------------------------- Util method-------- ----------------------------
  
  private Node getDataNode(Session session, String ownerType, String ownerId) throws Exception {
    if(ownerType.equals(PORTAL_TYPE)) {
      Node node = session.getRootNode().getNode(PORTAL_APP);
      if(node.hasNode(ownerId)) return node.getNode(ownerId);
      return null;
    }
    
    if(ownerType.equals(USER_TYPE)){
      Node node = session.getRootNode().getNode(USER_DATA).getNode(HOME);
      if(!node.hasNode(ownerId)) return null;
      node = node.getNode(ownerId);
      if(!node.hasNode(EXO_DATA_TYPE)) return null;
      node = node.getNode(EXO_DATA_TYPE);
      if(node.hasNode(PORTAL)) return node.getNode(PORTAL);
      return null;
    } 
    
    if(ownerType.equals(GROUP_TYPE)){
      String [] groups = ownerId.split("/");
      Node node = session.getRootNode().getNode(GROUP_DATA).getNode(HOME);
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
    if(ownerType.equals(PORTAL_TYPE)) {
      return create(getPortalAppNode(), ownerId);
    } 
    
    if(ownerType.equals(USER_TYPE)){
      Node portalNode = create(getUsersNode(), ownerId);
      return create(create(portalNode, EXO_DATA_TYPE), PORTAL);
    }
    
    if(ownerType.equals(GROUP_TYPE)){
      String [] groups = ownerId.split("/");
      Node portalNode = getGroupsNode();
      for(String group : groups) {
        if(group.trim().length() < 1) continue;
        portalNode = create(portalNode, group);
      }
      return create(create(portalNode, EXO_DATA_TYPE), PORTAL);
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
