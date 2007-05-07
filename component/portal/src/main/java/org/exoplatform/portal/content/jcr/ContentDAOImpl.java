/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.content.jcr;

import java.util.Calendar;
import java.util.Date;

import javax.jcr.Node;
import javax.jcr.Session;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.portal.content.BaseContentService;
import org.exoplatform.portal.content.ContentDAO;
import org.exoplatform.portal.content.model.ContentData;
import org.exoplatform.portal.content.model.ContentNavigation;
import org.exoplatform.registry.ApplicationRegistry;
import org.exoplatform.registry.JCRRegistryService;
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
  final private static String OWNER = "ownerId" ;
  final private static String DATA_TYPE = "dataType" ;
  final private static String DATA = "data";
  final private static String CREATED_DATE = "createdDate";
  final private static String MODIFIED_DATE = "modifiedDate";
  
  final private static String SYSTEM_WS = "production".intern();
  final private static String DATA_NODE_TYPE = "exo:data";
  
  final private static String USERS = "users";
  final public static String APPLICATION_NAME = "ContentService";
  private  RepositoryService service_ ;
  private JCRRegistryService jcrRegService_;
  
  public ContentDAOImpl(RepositoryService service, CacheService cservice, JCRRegistryService jcrRegService) throws Exception {
    super(cservice);
    service_ = service ;
    jcrRegService_ = jcrRegService; 
  }
  
  public void save(ContentNavigation navigation) throws Exception {
    Session session = service_.getRepository().getSystemSession(SYSTEM_WS) ;
    Node portalNode = jcrRegService_.getServiceRegistryNode(navigation.getOwner(), APPLICATION_NAME); 
    ContentData data = new ContentData();
    data.setDataType(ContentNavigation.class.getName());    
    data.setId(navigation.getOwner()+"::"+ContentNavigation.class.getName());
    data.setOwner(navigation.getOwner());
    data.setData(toXML(navigation));
    saveData(session, portalNode, data);   
  }
  
  public ContentNavigation get(String owner) throws Exception {
    ContentData data = getDataByOwner(owner);
    if(data == null) return null;
    return (ContentNavigation)fromXML(data.getData(), ContentNavigation.class);
  }
  
  public void remove(String owner) throws Exception { removeData(owner, NODE_NAME); }
  
  public ContentData getData(String id) throws Exception {
    String owner = id.substring(0, id.indexOf(':'));
    return getDataByOwner(owner);
  }
  
  private ContentData getDataByOwner(String owner) throws Exception {
    Node parentNode = jcrRegService_.getServiceRegistryNode(owner, APPLICATION_NAME);
    if(parentNode.hasNode(NODE_NAME) == false) return null;    
    Node node = parentNode.getNode(NODE_NAME);
    return nodeToContentData(node);
  }
  
  public void removeData(String id) throws Exception {
    removeDataByOwner(id.substring(0, id.indexOf(':')));
  }
  
  @SuppressWarnings("unused")
  public void removeData(String owner, String type) throws Exception {
    removeDataByOwner(owner); 
  } 
  
  private void removeDataByOwner(String owner) throws Exception {
    Session session = service_.getRepository().getSystemSession(SYSTEM_WS) ;
    Node parentNode = jcrRegService_.getServiceRegistryNode(owner, APPLICATION_NAME);
    if(parentNode.hasNode(NODE_NAME) == false) return ;
    Node node = parentNode.getNode(NODE_NAME);
    node.remove();
    parentNode.save();
    session.save();
  }
  
  private void saveData(Session session, Node parentNode, ContentData data) throws Exception {
    Node node;
    Date time = Calendar.getInstance().getTime();
    data.setModifiedDate(time);
    if(data.getCreatedDate() == null) data.setCreatedDate(time);
    if(parentNode.hasNode(NODE_NAME)) {
      node = parentNode.getNode(NODE_NAME);
      contentDataToNode(data, node);
      node.save();
    } else {
      node = parentNode.addNode(NODE_NAME, DATA_NODE_TYPE);
      contentDataToNode(data, node);
      parentNode.save();
    }
    session.save();
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
    node.setProperty("ownerType", "user");
    node.setProperty(DATA_TYPE, data.getDataType());
    node.setProperty(DATA, data.getData());
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(data.getCreatedDate());
    node.setProperty(CREATED_DATE, calendar);
    calendar = Calendar.getInstance();
    calendar.setTime(data.getModifiedDate());
    node.setProperty(MODIFIED_DATE, calendar);    
  }
  
  private javax.jcr.Session getSession() throws Exception{
    RepositoryService repoService = (RepositoryService)PortalContainer.getComponent(RepositoryService.class) ;    
    javax.jcr.Session session =  repoService.getRepository().getSystemSession(SYSTEM_WS) ;  
    return session;
  }
  
}