/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.content.jcr;

import java.util.Calendar;

import javax.jcr.Node;
import javax.jcr.Session;

import org.exoplatform.portal.content.BaseContentService;
import org.exoplatform.portal.content.ContentDAO;
import org.exoplatform.portal.content.model.ContentData;
import org.exoplatform.portal.content.model.ContentNavigation;
import org.exoplatform.services.cache.CacheService;
import org.exoplatform.services.jcr.RepositoryService;

/**
 * Created by The eXo Platform SARL        .
 * Author : Le Bien Thuy  
 *          lebienthuy@gmail.com
 * Date: 3/5/2007
 * Time: 1:12:22 PM
 */
public class ContentDAOImpl extends BaseContentService implements ContentDAO {
  
  final public static String USER_TYPE = "user";
  final public static String GROUP_TYPE = "group";
  final public static String PORTAL_TYPE = "portal";
  
  final private static String NODE_NAME = "contentNavigation.xml";
  
  final private static String ID = "id" ;
  final private static String OWNER = "owner" ;
  final private static String DATA_TYPE = "dataType" ;
  final private static String DATA = "data";
  final private static String CREATED_DATE = "createdDate";
  final private static String MODIFIED_DATE = "modifiedDate";
  
  final private static String SYSTEM_WS = "production".intern();
  final private static String DATA_NODE_TYPE = "exo:data";
  
  final private static String USERS = "users";
  final private static String APP_DATA = "AppData";
  
  final private static String NT_FOLDER_TYPE = "nt:folder" ;
  final private static String EXO_DATA_TYPE = "exo:data" ;

  final private static String PORTAL = "portal" ;

  final private static String WORKSPACE = "production" ;
  final private static String PORTAL_APP = "PortalApp" ;

  final private static String HOME = "home";
  final private static String USER_DATA = "user";
  final private static String GROUP_DATA = "group";
  
  private  RepositoryService service_ ;
  
  public ContentDAOImpl(RepositoryService service, CacheService cservice) throws Exception {
    super(cservice);
    service_ = service ;
  }
  
  public void save(ContentNavigation navigation) throws Exception {
//    Node portalNode = getPortalServiceNode(navigation.getOwner(), true); 
//    ContentData data = new ContentData();
//    data.setDataType(ContentNavigation.class.getName());    
//    data.setId(navigation.getOwner()+":/"+ContentNavigation.class.getName());
//    data.setOwner(navigation.getOwner());
//    data.setData(toXML(navigation));
//    saveData(portalNode, data);  
  }
  
  public ContentNavigation get(String owner) throws Exception {
    ContentData data = getDataByOwner(owner);
    if(data == null) return null;
    return (ContentNavigation)fromXML(data.getData(), ContentNavigation.class);
  }
  
  public void remove(String owner) throws Exception {
    removeData(owner, NODE_NAME);
  }
  
  public ContentData getData(String id) throws Exception {
    String owner = id.substring(0, id.indexOf(':'));
    return getDataByOwner(owner);
  }
  
  private ContentData getDataByOwner(String owner) throws Exception {
//    Node parentNode = getPortalServiceNode(owner, false);
//    if(parentNode.hasNode(NODE_NAME) == false) return null;    
//    Node node = parentNode.getNode(NODE_NAME);
//    return nodeToContentData(node);
    return null;
  }
  
  public void removeData(String id) throws Exception {
    removeDataByOwner(id.substring(0, id.indexOf(':')));
  }
  
  @SuppressWarnings("unused")
  public void removeData(String owner, String type) throws Exception {
    removeDataByOwner(owner); 
  } 
  
  private void removeDataByOwner(String owner) throws Exception {
//    Node parentNode = getPortalServiceNode(owner, false);
//    if(parentNode.hasNode(NODE_NAME) == false) return ;
//    Node node = parentNode.getNode(NODE_NAME);
//    node.remove();
//    parentNode.save();
//    getSession().save();
  }
  
  private void saveData(Node parentNode, ContentData data) throws Exception {
//    Session session = service_.getRepository().getSystemSession(WORKSPACE) ;
//    Node node;
//    Date time = Calendar.getInstance().getTime();
//    data.setModifiedDate(time);
//    if(data.getCreatedDate() == null) data.setCreatedDate(time);
//    if(parentNode.hasNode(NODE_NAME)) {
//      node = parentNode.getNode(NODE_NAME);
//      contentDataToNode(data, node);
//      node.save();
//    } else {
//      node = parentNode.addNode(NODE_NAME, DATA_NODE_TYPE);
//      contentDataToNode(data, node);
//      parentNode.save();
//    }
//    getSession().save();
  }
  
  private Node getPortalDataNode(Session session, String ownerType, String ownerId) throws Exception {
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
        if(!node.hasNode(group)) return null;
        node = node.getNode(group);
      }
      if(!node.hasNode(EXO_DATA_TYPE)) return null;
      node = node.getNode(EXO_DATA_TYPE);
      if(node.hasNode(PORTAL)) return node.getNode(PORTAL);
    }
    
    return null;
  }

  private Node createPortalDataNode(Session session, String ownerType, String ownerId) throws Exception {
    if(ownerType.equals(PORTAL_TYPE)) {
      return create(session.getRootNode().getNode(PORTAL_APP), ownerId);
    } 
    
    if(ownerType.equals(USER_TYPE)){
      Node portalNode = create(session.getRootNode().getNode(USER_DATA).getNode(HOME), ownerId);
      return create(create(portalNode, EXO_DATA_TYPE), PORTAL);
    }
    
    if(ownerType.equals(GROUP_TYPE)){
      String [] groups = ownerId.split("/");
      Node portalNode = session.getRootNode().getNode(GROUP_DATA).getNode(HOME);
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
    
  private ContentData nodeToContentData(Node node) throws Exception {
    ContentData data = new ContentData();
    if(!node.hasProperty(ID)) return null;
    data.setId(node.getProperty(ID).getString()); 
    if(!node.hasProperty(OWNER)) return null; 
    data.setOwner(node.getProperty(OWNER).getString()); 
    data.setDataType(node.getProperty(DATA_TYPE).getString());
    data.setData(node.getProperty(DATA).getString());
    data.setCreatedDate(node.getProperty(CREATED_DATE).getDate().getTime());
    data.setModifiedDate(node.getProperty(MODIFIED_DATE).getDate().getTime());
    return data;
  }
  
  private void contentDataToNode(ContentData data, Node node) throws  Exception {
    node.setProperty(ID, data.getId());
    node.setProperty(OWNER, data.getOwner());
    node.setProperty(DATA_TYPE, data.getDataType());
    node.setProperty(DATA, data.getData());
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(data.getCreatedDate());
    node.setProperty(CREATED_DATE, calendar);
    calendar = Calendar.getInstance();
    calendar.setTime(data.getModifiedDate());
    node.setProperty(MODIFIED_DATE, calendar);    
  }
 
}