/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.config;

import java.util.Calendar;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.services.cache.CacheService;
import org.exoplatform.services.jcr.RepositoryService;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Feb 28, 2007  
 */
public abstract class JCRDataService extends BaseDataService {
  
  public JCRDataService(){}
  
  public JCRDataService(CacheService cservice) throws Exception {
    super(cservice);
  }

  final public static String SYSTEM_WS = "production".intern();
  final public static String DATA_NODE_TYPE = "exo:data";
  
  final public static String HOME = "home";
  final public static String USERS = "users";
  final public static String APP_DATA = "AppData";
  final public static String PORTAL = "portal";
  final public static String SHARED = "shared";
  
  final public static String PORTLE_TPREFERENCES = "portletPreferences";
  
  final public static String ID = "id" ;
  
  final public static String DATA = "data";
  final public static String CREATED_DATE = "createdDate";
  final public static String MODIFIED_DATE = "modifiedDate";
  
  final public static Node getPortalServiceNode(String owner, boolean autoCreate) throws Exception {
    Node node = getNode(getSession().getRootNode(), HOME, autoCreate);
    if((node = getNode(node, USERS, autoCreate)) == null && !autoCreate) return null;
    if((node = getNode(node, owner, autoCreate)) == null && !autoCreate) return null;
    if((node = getNode(node, APP_DATA, autoCreate)) == null && !autoCreate) return null;
    if((node = getNode(node, PORTAL, autoCreate)) == null && !autoCreate) return null;
    return node;
  }
  
  final public static Node getNode(Node parentNode, String name, boolean autoCreate) throws Exception {
    if(parentNode.hasNode(name)) return parentNode.getNode(name);
    if(!autoCreate) return null;
    Node node  = parentNode.addNode(name);
    parentNode.save();
    return node;
  }
  
  final public static javax.jcr.Session getSession() throws Exception{
    RepositoryService repoService = (RepositoryService)PortalContainer.getComponent(RepositoryService.class) ;    
    Session session = repoService.getRepository().getSystemSession(SYSTEM_WS) ;  
    return session;
  }
  
  final public static Node getNode(Node node, String id) throws Exception {
    if(node.hasProperty(ID) && id.equals(node.getProperty(ID).getString())) return node;
    NodeIterator iterator  = node.getNodes();
    while(iterator.hasNext()){
      Node value = getNode(iterator.nextNode(), id);
      if(value != null) return value;
    }
    return null;
  }
  
  final public Data getData(String owner, String id) throws Exception {  
    if(owner == null) return null;
    Node portalNode = getPortalServiceNode(owner, true);
    Node node = getNode(portalNode, id);
    if(node != null) return nodeToData(node);
    return null;
  }
  
  final public Data nodeToData(Node node) throws Exception {
    Data data  = new Data();
    if(node.hasProperty(ID)) data.setId(node.getProperty(ID).getString()); else return null;
    if(node.hasProperty(OWNER)) data.setOwner(node.getProperty(OWNER).getString()); else return null;
    data.setDataType(node.getProperty(DATA_TYPE).getString());
    if(node.hasProperty(VIEW_PERMISSION)){
      data.setViewPermission(node.getProperty(VIEW_PERMISSION).getString());
    }
    if(node.hasProperty(EDIT_PERMISSION)){
      data.setEditPermission(node.getProperty(EDIT_PERMISSION).getString());
    }
    data.setData(node.getProperty(DATA).getString());
    data.setCreatedDate(node.getProperty(CREATED_DATE).getDate().getTime());
    data.setModifiedDate(node.getProperty(MODIFIED_DATE).getDate().getTime());
    return data;
  }
  
  
  protected Node getDataServiceNode(String owner, String name, boolean autoCreate) throws Exception {
    Node node  = getPortalServiceNode(owner, autoCreate);
    if((node = getNode(node, name, autoCreate)) == null && !autoCreate) return null;
    return node;
  }
  
  protected void dataToNode(Data data, Node node) throws Exception {
    node.setProperty(ID, data.getId());
    node.setProperty(OWNER, data.getOwner());
    node.setProperty(DATA_TYPE, data.getDataType());
    node.setProperty(VIEW_PERMISSION, data.getViewPermission());
    node.setProperty(EDIT_PERMISSION, data.getEditPermission());
    node.setProperty(DATA, data.getData());
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(data.getCreatedDate());
    node.setProperty(CREATED_DATE, calendar);
    calendar = Calendar.getInstance();
    calendar.setTime(data.getModifiedDate());
    node.setProperty(MODIFIED_DATE, calendar);
  }
  
}
