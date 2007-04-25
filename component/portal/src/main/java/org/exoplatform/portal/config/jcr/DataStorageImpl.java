/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.config.jcr;

import javax.jcr.Node;
import javax.jcr.Session;

import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.services.jcr.RepositoryService;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan.nguyen@exoplatform.com
 * Apr 20, 2007  
 */
public class DataStorageImpl  implements DataStorage {

  final public static String NT_FOLDER_TYPE = "nt:folder" ;
  final public static String EXO_DATA_TYPE = "exo:data" ;

  final public static String WORKSPACE = "production" ;
  final public static String PORTAL_APP = "PortalApp" ;

  final public static String PORTAL_CONFIG_FILE_NAME = "config.xml" ;
  final public static String NAVIGATION_CONFIG_FILE_NAME = "navigation.xml" ;
  final public static String PAGE_SET_NODE = "pages" ;

  private  RepositoryService service_ ;
  private DataMapper mapper_ = new DataMapper();

  public DataStorageImpl(RepositoryService service) throws Exception{
    service_ = service ;
    Session session = service_.getRepository().getSystemSession(WORKSPACE) ;
    Node rootNode = session.getRootNode() ;
    if (!rootNode.hasNode(PORTAL_APP)) {
      rootNode.addNode(PORTAL_APP, NT_FOLDER_TYPE);
      rootNode.save();
      session.save();
    } 
  }

  public PortalConfig getPortalConfig(String portalName) throws Exception {
    Session session = service_.getRepository().getSystemSession(WORKSPACE) ;
    Node portalAppNode = session.getRootNode().getNode(PORTAL_APP) ;

    if (portalAppNode.hasNode(portalName)) {
      Node portalNode = portalAppNode.getNode(portalName) ;

      if (portalNode.hasNode(PORTAL_CONFIG_FILE_NAME)) {
        Node portalConfigNode = portalNode.getNode(PORTAL_CONFIG_FILE_NAME) ;
        PortalConfig config = mapper_.toPortalConfig(portalConfigNode) ;

        return config ;
      }
    }

    return null ;
  }

  public void save(PortalConfig config) throws Exception {
    Session session = service_.getRepository().getSystemSession(WORKSPACE) ;
    Node portalAppNode = session.getRootNode().getNode(PORTAL_APP) ;

    Node portalNode = null ;
    String portalName = config.getOwner() ;
    if (portalAppNode.hasNode(portalName)) {
      portalNode = portalAppNode.getNode(portalName) ;
    } else {
      portalNode = portalAppNode.addNode(portalName, NT_FOLDER_TYPE) ;
      portalAppNode.save() ;
    }

    Node portalConfigNode = null ;
    if (portalNode.hasNode(PORTAL_CONFIG_FILE_NAME)) {
      portalConfigNode = portalNode.getNode(PORTAL_CONFIG_FILE_NAME) ;
    } else {
      portalConfigNode = portalNode.addNode(PORTAL_CONFIG_FILE_NAME, EXO_DATA_TYPE) ;
    }
    mapper_.map(portalConfigNode, config) ;
    portalNode.save() ;
    session.save() ;
  }

  public void remove(PortalConfig config) throws Exception {
    Session session = service_.getRepository().getSystemSession(WORKSPACE) ;

    Node portalAppNode = session.getRootNode().getNode(PORTAL_APP) ;
    String portalName = config.getOwner() ;
    Node  portalNode = portalAppNode.getNode(portalName) ;
    Node portalConfigNode = portalNode.getNode(PORTAL_CONFIG_FILE_NAME) ;
    portalConfigNode.remove() ;
  
    portalNode.save() ;
    session.save() ;
  }

  public void create(Page page) throws Exception {
  }

  public void save(Page page) throws Exception {
    Session session = service_.getRepository().getSystemSession(WORKSPACE) ;
    Node portalAppNode = session.getRootNode().getNode(PORTAL_APP) ;

    String portalName = page.getOwner() ;
    Node portalNode = null ;
    if (portalAppNode.hasNode(portalName)) {
      portalNode = portalAppNode.getNode(portalName) ;
    } else {
      portalNode = portalAppNode.addNode(portalName, NT_FOLDER_TYPE) ;
      portalAppNode.save() ;
    }

    Node pageSetNode = null ;
    if (portalNode.hasNode(PAGE_SET_NODE)) {
      pageSetNode = portalNode.getNode(PAGE_SET_NODE) ;
    } else {
      pageSetNode = portalNode.addNode(PAGE_SET_NODE, NT_FOLDER_TYPE) ;
      portalNode.save() ;
    }

    String pageName = page.getName() ;
    Node pageNode = null ;
    if (pageSetNode.hasNode(pageName)) {
      pageNode = pageSetNode.getNode(pageName) ;
    } else {
      pageNode = pageSetNode.addNode(pageName, EXO_DATA_TYPE) ;
      pageSetNode.save() ;
    }

    mapper_.map(pageNode, page) ;
    session.save() ;
  }

  public void remove(Page page) throws Exception {
    Session session = service_.getRepository().getSystemSession(WORKSPACE) ;
    Node portalAppNode = session.getRootNode().getNode(PORTAL_APP) ;
    
    String portalName = page.getOwner() ;
    String pageName = page.getName() ;
    Node portalNode = portalAppNode.getNode(portalName) ;
    Node pageSetNode = portalNode.getNode(PAGE_SET_NODE) ;
    Node pageNode = pageSetNode.getNode(pageName) ;
    pageNode.remove() ;
    
    pageSetNode.save() ;
    session.save() ;
  }

  public Page getPage(String pageId) throws Exception {
    String[] names = pageId.split(":/") ;
    String portalName = names[0] ;
    String pageName = names[1] ;

    Session session = service_.getRepository().getSystemSession(WORKSPACE) ;
    Node portalAppNode = session.getRootNode().getNode(PORTAL_APP) ;

    if (portalAppNode.hasNode(portalName)) {
      Node portalNode = portalAppNode.getNode(portalName) ;

      if (portalNode.hasNode(PAGE_SET_NODE)) {
        Node pageSetNode = portalNode.getNode(PAGE_SET_NODE) ;

        if(pageSetNode.hasNode(pageName)) {
          Node pageNode = pageSetNode.getNode(pageName) ;
          Page page = mapper_.toPage(pageNode) ;

          return page ;
        }
      }

    }

    return null;
  }

  public void save(PageNavigation navigation) throws Exception {
    Session session = service_.getRepository().getSystemSession(WORKSPACE) ;
    Node portalAppNode = session.getRootNode().getNode(PORTAL_APP) ;

    String portalName = navigation.getOwner() ;
    Node portalNode = null ;
    if (portalAppNode.hasNode(portalName)) {
      portalNode = portalAppNode.getNode(portalName) ;
    } else {
      portalNode = portalAppNode.addNode(portalName, NT_FOLDER_TYPE) ;
      portalAppNode.save() ;
    }

    Node navigationNode = null ;
    if (portalNode.hasNode(NAVIGATION_CONFIG_FILE_NAME)) {
      navigationNode = portalNode.getNode(NAVIGATION_CONFIG_FILE_NAME) ;
    } else {
      navigationNode = portalNode.addNode(NAVIGATION_CONFIG_FILE_NAME, EXO_DATA_TYPE) ;
      portalNode.save() ;
    }

    mapper_.map(navigationNode, navigation) ;
    session.save() ;
  }

  public void remove(PageNavigation navigation) throws Exception {
    Session session = service_.getRepository().getSystemSession(WORKSPACE) ;
    
    Node portalAppNode = session.getRootNode().getNode(PORTAL_APP) ;    
    String portalName = navigation.getOwner() ;        
    Node portalNode = portalAppNode.getNode(portalName) ;
    Node navigationNode = portalNode.getNode(NAVIGATION_CONFIG_FILE_NAME) ;
    navigationNode.remove() ;
    
    portalNode.save() ;       
    session.save() ;
  }

  public PageNavigation getPageNavigation(String id) throws Exception {
    Session session = service_.getRepository().getSystemSession(WORKSPACE) ;
    Node portalAppNode = session.getRootNode().getNode(PORTAL_APP) ;

    if (portalAppNode.hasNode(id)) {
      Node portalNode = portalAppNode.getNode(id) ;

      if (portalNode.hasNode(NAVIGATION_CONFIG_FILE_NAME)) {
        Node navigationNode  = portalNode.getNode(NAVIGATION_CONFIG_FILE_NAME) ;
        PageNavigation navigation = mapper_.toPageNavigation(navigationNode) ;

        return navigation ;   
      }
    }

    return null;
  }  

}
