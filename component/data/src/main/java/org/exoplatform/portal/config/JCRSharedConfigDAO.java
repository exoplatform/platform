/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.config;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;

import org.exoplatform.services.cache.CacheService;
import org.exoplatform.services.cache.ExoCache;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Mar 1, 2007  
 */
public class JCRSharedConfigDAO extends JCRDataService implements SharedConfigDAO {

  final private static String NAVIGATION = "navigation";
  final private static String SHARED_PORTAL_TYPE = "exo:sharedPortal";
  final private static String SHARED_NAVIGATION_TYPE = "exo:sharedNavigation";

  final private static String  GROUP_ID = "groupId";
  final private static String  MEMBERSHIP = "membership" ;
  final private static String  PRIORITY = "priority" ;
  final private static String  DESCRIPTION = "description";

  private ExoCache sharedPortalCache_ ;  
  private ExoCache sharedNavCache_ ;

  public JCRSharedConfigDAO(CacheService cservice) throws Exception {
    super(cservice);
    sharedPortalCache_ = cservice.getCacheInstance(SharedPortal.class.getName()) ;
    sharedNavCache_ = cservice.getCacheInstance(SharedNavigation.class.getName()) ;
  }

  //-------------------------- Shared Portal ------------------------------- 

  public void saveSharedPortal(List<SharedPortal> list) throws Exception {
    if(list == null) return ;
    for(SharedPortal sharedPortal : list) {
      addSharedPortal(sharedPortal) ;
    }
  }

  public void addSharedPortal(SharedPortal sharedPortal) throws Exception {
    Node sharedNode = getSharedDataNode(PORTAL, true);
    Node node;
    String name = String.valueOf(sharedPortal.getGroupId().hashCode());
    if(sharedNode.hasNode(name)) {
      node = sharedNode.getNode(name);
      sharedPortalToNode(sharedPortal, node);
      node.save();
    } else {
      node = sharedNode.addNode(name, SHARED_PORTAL_TYPE);
      sharedPortalToNode(sharedPortal, node);
      sharedNode.save();
    }
    getSession().save();
    sharedPortalCache_.remove(sharedPortal.getGroupId()) ;
  }
  
  public SharedPortal getSharedPortal(String groupId) throws Exception {
    SharedPortal cportal = (SharedPortal) sharedPortalCache_.get(groupId) ;
    if(cportal == null) {
      cportal = getSharedPortalFromNode(groupId);
        //(SharedPortal)hservice_.findOne(SharedPortal.class, groupId) ;
      if(cportal == null) sharedPortalCache_.put(groupId, SharedConfigDAO.NO_COMMUNITY_PORTAL) ;
      else sharedPortalCache_.put(groupId, cportal) ;
    }
    if(cportal == SharedConfigDAO.NO_COMMUNITY_PORTAL)  return null ;
    return cportal ;
  }  

  private SharedPortal getSharedPortalFromNode(String groupId) throws Exception {
    Node sharedNode = getSharedDataNode(PORTAL, true);
    String name = String.valueOf(groupId.hashCode());
    if(!sharedNode.hasNode(name))  return null;
    Node node = sharedNode.getNode(name);
    return nodeToSharedPortal(node);
  }

  public List<SharedPortal> getSharedPortals() throws Exception {
    List<SharedPortal> list = new ArrayList<SharedPortal>();
    Node sharedNode = getSharedDataNode(PORTAL, true);
    NodeIterator iterator = sharedNode.getNodes();
    while(iterator.hasNext()){
      list.add(nodeToSharedPortal(iterator.nextNode()));
    }
    return list;    
  }

  public void removeSharedPortal(SharedPortal sharedPortal) throws Exception {
    Node sharedNode = getSharedDataNode(PORTAL, true);
    String name = String.valueOf(sharedPortal.getGroupId().hashCode());
    if(!sharedNode.hasNode(name))  return ;
    Node node = sharedNode.getNode(name);
    node.remove();
    sharedNode.save();
    getSession().save();
    sharedPortalCache_.remove(sharedPortal.getGroupId()) ;
  }

//-------------------------- Shared Navigation -------------------------------  

  public void saveSharedNavigation(List<SharedNavigation> list) throws Exception {
    if(list == null) return ;
    for(SharedNavigation sharedNav : list) {
      addSharedNavigation(sharedNav) ;
    }
  }

  public void addSharedNavigation(SharedNavigation sharedNav) throws Exception {
    Node sharedNode = getSharedDataNode(NAVIGATION, true);
    Node node;
    String name = String.valueOf(sharedNav.getGroupId().hashCode());
    if(sharedNode.hasNode(name)) {
      node = sharedNode.getNode(name);
      sharedNavigationToNode(sharedNav, node);
      node.save();
    } else {
      node = sharedNode.addNode(name, SHARED_NAVIGATION_TYPE);
      sharedNavigationToNode(sharedNav, node);
      sharedNode.save();
    }
    getSession().save();
    sharedNavCache_.remove(sharedNav.getGroupId()) ;
  }
  
  public SharedNavigation getSharedNavigation(String groupId) throws Exception {
    SharedNavigation cnav = (SharedNavigation) sharedNavCache_.get(groupId) ;
    if(cnav == null) {
      cnav = getSharedNavigationFromNode(groupId) ;
      if(cnav == null) cnav =  SharedConfigDAO.NO_COMMUNITY_NAVIGATION  ;
      sharedNavCache_.put(groupId, cnav) ;
    }
    if(cnav == SharedConfigDAO.NO_COMMUNITY_NAVIGATION)  return null ;
    return cnav ;
  }

  private SharedNavigation getSharedNavigationFromNode(String groupId) throws Exception {
    Node sharedNode = getSharedDataNode(NAVIGATION, true);
    String name = String.valueOf(groupId.hashCode());
    if(!sharedNode.hasNode(name))  return null;
    Node node = sharedNode.getNode(name);
    return nodeToSharedNavigation(node);
  }

  public List<SharedNavigation> getSharedNavigations() throws Exception {
    List<SharedNavigation> list = new ArrayList<SharedNavigation>();
    Node sharedNode = getSharedDataNode(NAVIGATION, true);
    NodeIterator iterator = sharedNode.getNodes();
    while(iterator.hasNext()){
      list.add(nodeToSharedNavigation(iterator.nextNode()));
    }
    return list;    
  }

  public void removeSharedNavigation(SharedNavigation sharedNav) throws Exception {
    Node sharedNode = getSharedDataNode(NAVIGATION, true);
    String name = String.valueOf(sharedNav.getGroupId().hashCode());
    if(!sharedNode.hasNode(name))  return ;
    Node node = sharedNode.getNode(name);
    node.remove();
    sharedNode.save();
    getSession().save();
    sharedNavCache_.remove(sharedNav.getGroupId()) ;
  }
  

  protected Node getSharedDataNode(String name, boolean autoCreate) throws Exception {
    Node node = getNode(getSession().getRootNode(), HOME, autoCreate);
    if((node = getNode(node, SHARED, autoCreate)) == null && !autoCreate) return null;
    if((node = getNode(node, APP_DATA, autoCreate)) == null && !autoCreate) return null;
    if((node = getNode(node, name, autoCreate)) == null && !autoCreate) return null;
    return node;
  }

  protected void sharedPortalToNode(SharedPortal data, Node node) throws Exception {
    node.setProperty(GROUP_ID, data.getGroupId());
    node.setProperty(MEMBERSHIP, data.getMembership());
    node.setProperty(PORTAL, data.getPortal());
    node.setProperty(PRIORITY, data.getPriority());
    node.setProperty(DESCRIPTION, data.getDescription());
  }

  protected SharedPortal nodeToSharedPortal(Node node) throws Exception {
    SharedPortal data  = new SharedPortal();
    data.setGroupId(node.getProperty(GROUP_ID).getString());
    data.setMembership(node.getProperty(MEMBERSHIP).getString());
    data.setPortal(node.getProperty(PORTAL).getString());
    data.setPriority((int)node.getProperty(PRIORITY).getLong());
    return data;
  }

  protected void sharedNavigationToNode(SharedNavigation data, Node node) throws Exception {
    node.setProperty(GROUP_ID, data.getGroupId());
    node.setProperty(MEMBERSHIP, data.getMembership());
    node.setProperty(NAVIGATION, data.getNavigation());
    node.setProperty(PRIORITY, data.getPriority());
    node.setProperty(DESCRIPTION, data.getDescription());
  }

  protected SharedNavigation nodeToSharedNavigation(Node node) throws Exception {
    SharedNavigation data  = new SharedNavigation();
    data.setGroupId(node.getProperty(GROUP_ID).getString());
    data.setMembership(node.getProperty(MEMBERSHIP).getString());
    data.setNavigation(node.getProperty(NAVIGATION).getString());
    data.setPriority((int)node.getProperty(PRIORITY).getLong());
    return data;
  }

}
