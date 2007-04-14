/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.services.portletregistery;

import java.util.Calendar;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.services.jcr.RepositoryService;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Mar 9, 2007  
 */
abstract class BaseJCRService {
  
  final static String SYSTEM_WS = "production".intern();
  final static String REGISTRY = "registry";
  final static String JCR_SYSTEM = "jcr:system";
  final static String APPLICATION_DATA = "AppData";
  final static String ID = "id" ;
  
  final static String CREATED_DATE = "createdDate";
  final static String MODIFIED_DATE = "modifiedDate";
  
  final static String VIEW_PERMISSION = "viewPermission" ;
  
  final static String PORTLETS = "portlets";
  final static String PORTLET_PERMISSION = "permission";
  
  final static String PORTLET_TYPE = "exo:portlet";
  final static String PORTLET_CATEGORY_TYPE = "exo:portletCategory";
  final static String PORTLET_PERMISSION_TYPE = "exo:portletPermission";
  
  final static String NAME = "name";
  final static String DESCRIPTION = "description";
  
  final static String PORTLET_CATEGORY_ID = "categoryId";
  final static String PORTLET_APPLICATION_NAME = "applicationName";
  final static String DISPLAY_NAME = "displayName";
  
  final static String PORTLET_ID = "portletId";
  final static String MEMBERSHIP = "membership";
  final static String GROUP_ID = "groupId";
  
  Node getNode(Node parentNode, String name, boolean autoCreate) throws Exception {
    if(parentNode.hasNode(name)) return parentNode.getNode(name);
    if(!autoCreate) return null;
    Node node  = parentNode.addNode(name);
    parentNode.save();
    return node;
  }
  
  javax.jcr.Session getSession() throws Exception{
    RepositoryService repoService = (RepositoryService)PortalContainer.getComponent(RepositoryService.class) ;    
    Session session = repoService.getRepository().getSystemSession(SYSTEM_WS) ;  
    return session;
  }
  
  Node getPortletRegistryNode(boolean autoCreate) throws Exception {
    Node node = getNode(getSession().getRootNode(), JCR_SYSTEM, autoCreate);
    if((node = getNode(node, APPLICATION_DATA, autoCreate)) == null && !autoCreate) return null;
    if((node = getNode(node, REGISTRY, autoCreate)) == null && !autoCreate) return null;
    if((node = getNode(node, PORTLETS, autoCreate)) == null && !autoCreate) return null;
    return node;
  }

  Node getNode(Node node, String property, String value) throws Exception {
    if(node.hasProperty(property) && value.equals(node.getProperty(property).getString())) return node;
    NodeIterator iterator = node.getNodes();
    while(iterator.hasNext()){
      Node returnNode = getNode(iterator.nextNode(), property, value);
      if(returnNode != null) return returnNode;
    }
    return null;
  }

  Node getPortletNode(Portlet portlet) throws Exception {
    Node rootNode = getPortletRegistryNode(true);
    Node categoryNode = getNode(rootNode, ID, portlet.getPortletCategoryId());
    if(categoryNode == null) return null;
    if(!categoryNode.hasNode(portlet.getPortletName().trim())) return null;
    return categoryNode.getNode(portlet.getPortletName());
  }

  PortletCategory nodeToPortletCategory(Node node) throws Exception {
    PortletCategory data = new PortletCategory();
    if(!node.hasProperty(ID)) return null;
    data.setId(node.getProperty(ID).getString().trim()); 
    if(node.hasProperty(NAME)) { 
      data.setPortletCategoryName(node.getProperty(NAME).getString().trim());
    }
    if(node.hasProperty(DESCRIPTION)) { 
      data.setDescription(node.getProperty(DESCRIPTION).getString());
    }
    data.setCreatedDate(node.getProperty(CREATED_DATE).getDate().getTime());
    data.setModifiedDate(node.getProperty(MODIFIED_DATE).getDate().getTime());
    return data;
  }

  void portletCategoryToNode(PortletCategory data, Node node) throws Exception {
    node.setProperty(ID, data.getId());
    node.setProperty(NAME, data.getPortletCategoryName());
    node.setProperty(DESCRIPTION, data.getDescription());
    
    Calendar calendar = Calendar.getInstance();
    if(data.getCreatedDate() != null) calendar.setTime(data.getCreatedDate());
    node.setProperty(CREATED_DATE, calendar);
    
    calendar = Calendar.getInstance();
    if(data.getModifiedDate() != null) calendar.setTime(data.getModifiedDate());
    node.setProperty(MODIFIED_DATE, calendar);
  }

  Portlet nodeToPortlet(Node node) throws Exception {
    Portlet portlet = new Portlet();
    if(!node.hasProperty(ID)) return null;

    portlet.setId(node.getProperty(ID).getString().trim()); 
    if(node.hasProperty(NAME)) { 
      portlet.setPortletName(node.getProperty(NAME).getString().trim());
    }
    if(node.hasProperty(DESCRIPTION)) {
      portlet.setDescription(node.getProperty(DESCRIPTION).getString());
    }
    if(node.hasProperty(DISPLAY_NAME)) {
      portlet.setDisplayName(node.getProperty(DISPLAY_NAME).getString());
    }
    if(node.hasProperty(PORTLET_CATEGORY_ID)) {
      portlet.setPortletCategoryId(node.getProperty(PORTLET_CATEGORY_ID).getString());
    }
    if(node.hasProperty(PORTLET_APPLICATION_NAME)) {
      portlet.setPortletApplicationName(node.getProperty(PORTLET_APPLICATION_NAME).getString());
    }   
    if(node.hasProperty(VIEW_PERMISSION)) {
      portlet.setViewPermission(node.getProperty(VIEW_PERMISSION).getString());
    }
    portlet.setCreatedDate(node.getProperty(CREATED_DATE).getDate().getTime());
    portlet.setModifiedDate(node.getProperty(MODIFIED_DATE).getDate().getTime());
    return portlet;
  }

  void portletToNode(Portlet portlet, Node node) throws Exception {
    node.setProperty(ID, portlet.getId());
    node.setProperty(NAME, portlet.getPortletName());
    node.setProperty(DESCRIPTION, portlet.getDescription());
    node.setProperty(DISPLAY_NAME, portlet.getDisplayName());
    
    node.setProperty(PORTLET_CATEGORY_ID, portlet.getPortletCategoryId());
    node.setProperty(PORTLET_APPLICATION_NAME, portlet.getPortletApplicationName());
    
    node.setProperty(VIEW_PERMISSION, portlet.getViewPermission());
    
    Calendar calendar = Calendar.getInstance();
    if(portlet.getCreatedDate() != null) calendar.setTime(portlet.getCreatedDate());
    node.setProperty(CREATED_DATE, calendar);

    calendar = Calendar.getInstance();
    if(portlet.getModifiedDate() != null) calendar.setTime(portlet.getModifiedDate());
    node.setProperty(MODIFIED_DATE, calendar);   
  }

  PortletPermission nodeToPortletPermission(Node node) throws Exception {
    PortletPermission permission = new PortletPermission();
    if(!node.hasProperty(ID)) return null;
    permission.setId(node.getProperty(ID).getString().trim()); 
    if(node.hasProperty(PORTLET_ID)) {
      permission.setPortletId(node.getProperty(PORTLET_ID).getString().trim());
    }
    if(node.hasProperty(DESCRIPTION)) {
      permission.setDescription(node.getProperty(DESCRIPTION).getString());
    }
    if(node.hasProperty(MEMBERSHIP)) {
      permission.setMembership(node.getProperty(MEMBERSHIP).getString());
    }
    if(node.hasProperty(GROUP_ID)) {
      permission.setGroupId(node.getProperty(GROUP_ID).getString());
    }
    return permission;
  }


  void portletPermissionToNode(PortletPermission data, Node node) throws Exception {
    node.setProperty(ID, data.getId());
    node.setProperty(PORTLET_ID, data.getPortletId());
    node.setProperty(DESCRIPTION, data.getDescription());
    node.setProperty(MEMBERSHIP, data.getMembership());
    node.setProperty(GROUP_ID, data.getGroupId());
  }

}
