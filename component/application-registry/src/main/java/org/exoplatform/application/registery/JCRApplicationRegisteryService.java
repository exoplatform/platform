/**
 * Copyright 2001-2003 The eXo platform SARL All rights reserved.
 * Please look at license.txt in info directory for more license detail. 
 */

package org.exoplatform.application.registery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.portletcontainer.monitor.PortletContainerMonitor;
import org.exoplatform.services.portletcontainer.monitor.PortletRuntimeData;

/**
 * Created y the eXo platform team
 * User: lebienthuy@gmail.com
 * Date: 3/7/2007
 */
public class JCRApplicationRegisteryService implements ApplicationRegisteryService {
  
  private final String systemWS = "production".intern();
  private final String registry = "registry";
  private final String jcrSystem = "jcr:system";
  private final String applicationData = "eXo:AppRegistry:2.0";

  private final String applications = "applications";
  
  private final String applicationNodeType = "exo:application";
  private final String appCategoryNodeType = "exo:applicationCategory";
  
  private final static String ID = "id";
  private final static String NAME = "name";
  
  private DataMapper mapper = new DataMapper();

  public JCRApplicationRegisteryService() {
  }
  
  public List<ApplicationCategory> getApplicationCategories() throws Exception {
    List<ApplicationCategory> lists = new ArrayList<ApplicationCategory>();
    Node portletCategoryNode = getApplicationRegistryNode(true);
    NodeIterator iterator = portletCategoryNode.getNodes();
    while(iterator.hasNext()) {
      Node node = iterator.nextNode();
      String name = node.getPrimaryNodeType().getName();
      if(appCategoryNodeType.equals(name)) lists.add(mapper.nodeToApplicationCategory(node));
    }
    return lists;
  }
  
  public List<ApplicationCategory> getApplicationCategories(String accessUser) throws Exception {
    return null;
  }

  public void save(ApplicationCategory appCategory) throws Exception {
    Node rootNode = getApplicationRegistryNode(true);
    appCategory.setName(appCategory.getName().replace(' ', '_'));
    Node node = null;
    if(rootNode.hasNode(appCategory.getName())){
      node = rootNode.getNode( appCategory.getName());
    } else {
      node = rootNode.addNode(appCategory.getName(), appCategoryNodeType);
    }
    mapper.applicationCategoryToNode(appCategory, node);
    rootNode.save();
    getSession().save();
  }
  
  public void remove(ApplicationCategory category) throws Exception {
    Node node = getNode(getApplicationRegistryNode(false), NAME, category.getName());
    if(node == null) return ;
    Node parentNode  = node.getParent();
    node.remove();
    parentNode.save();
    getSession().save();
  }

  public ApplicationCategory getApplicationCategory(String name) throws Exception {
    Node node = getNode(getApplicationRegistryNode(false), NAME, name);
    if(node != null)  return  mapper.nodeToApplicationCategory(node);
    return null;
  }

  public List<Application> getApplications(ApplicationCategory category) throws Exception {
    List<Application> list = new ArrayList<Application>();
    Node categoryNode = getNode(getApplicationRegistryNode(false), NAME, category.getName());
    NodeIterator iterator  = categoryNode.getNodes();
    while(iterator.hasNext()){
      Node portletNode = iterator.nextNode();
      list.add(mapper.nodeToApplication(portletNode ));
    }
    return list;
  }

  public Application getApplication(String id) throws Exception {
    Node node = getNode(getApplicationRegistryNode(false), ID, id);
    if(node != null) return mapper.nodeToApplication(node);
    return null;
  }

  public void save(ApplicationCategory category, Application application) throws Exception {
    application.setId(category.getName() + "/" + application.getApplicationName().replace(' ', '_'));
    application.setCategoryName(category.getName());
    
    Node rootNode = getApplicationRegistryNode(true);
    Node categoryNode ;
    if(rootNode.hasNode(category.getName())) { 
      categoryNode = rootNode.getNode(category.getName());
    } else {
      categoryNode = rootNode.addNode(category.getName(), appCategoryNodeType);
      mapper.applicationCategoryToNode(category, categoryNode);
      rootNode.save();
      getSession().save();
    }

    Node portletNode ;
    if(categoryNode.hasNode(application.getApplicationName())) { 
      portletNode = categoryNode.getNode(application.getApplicationName());
      return ;
    }
    
    portletNode = categoryNode.addNode(application.getApplicationName(), applicationNodeType);
    mapper.applicationToNode(application, portletNode);
    categoryNode.save();
    getSession().save();
  }

  public void remove(Application application) throws Exception {
    Node node = getApplicationRegistryNode(false);
    Node applicationNode = getNode(getApplicationRegistryNode(false), ID, application.getId());
    if(applicationNode == null) return;
    applicationNode.remove();
    node.save();
    getSession().save();
  }

  public void update(Application application) throws Exception {
    Node node = getNode(getApplicationRegistryNode(false), ID, application.getId());
    if(node == null) return ;
    mapper.applicationToNode(application, node);
    node.save();
    getSession().save();
  }
  
  public void importJSR168Portlets() throws Exception {
    PortalContainer manager  = PortalContainer.getInstance();
    PortletContainerMonitor monitor =(PortletContainerMonitor) manager.getComponentInstanceOfType(PortletContainerMonitor.class) ;
    Collection portletDatas = monitor.getPortletRuntimeDataMap().values();  
    
    Iterator iterator = portletDatas.iterator();
    while(iterator.hasNext()) {
      PortletRuntimeData portletRuntimeData = (PortletRuntimeData) iterator.next();
      String categoryName = portletRuntimeData.getPortletAppName();
      String portletName = portletRuntimeData.getPortletName();
      ApplicationCategory category = null;

      category = getApplicationCategory(categoryName);
      if(category == null) {
        category = new ApplicationCategory();
        category.setName(categoryName);
        save(category);
      }

      Application portlet = findPortletByDisplayName(category.getName(), portletName);
      if(portlet != null)  continue;
      portlet = new Application();
      portlet.setDisplayName(portletName);
      portlet.setAliasName(portletName);
      portlet.setCategoryName(categoryName);
      save(category, portlet);

     /* for(Object defaultPortletPermission : defaultPortletPermissions_) {
        PortletPermission defaultPermission = (PortletPermission) defaultPortletPermission;
        PortletPermission newPermission = createPortletPermissionInstance();
        newPermission.setMembership(defaultPermission.getMembership()) ;
        newPermission.setGroupId(defaultPermission.getGroupId()) ;
        newPermission.setDescription(defaultPermission.getDescription()) ;
        addPortletPermission(portlet, newPermission);
      }*/
      
    }
  }
    
  private Application findPortletByDisplayName(String categoryName, String displayName) throws Exception {
    Node node = getNode(getApplicationRegistryNode(false), ID, categoryName);
    if(node == null) return null;
    Node portletNode = getNode(node, "displayName", displayName);
    if(portletNode == null) return null;
    return mapper.nodeToApplication(portletNode);
  }

  public void clearAllRegistries() throws Exception {    
    Node homeNode = getApplicationRegistryNode(false);
    Node parentNode = homeNode.getParent();
    homeNode.remove();
    parentNode.save();
    getSession().save();
    parentNode.addNode(applicationNodeType);
    parentNode.save();
    getSession().save();
  }
  
  
  private javax.jcr.Session getSession() throws Exception{
    RepositoryService repoService = (RepositoryService)PortalContainer.getComponent(RepositoryService.class) ;    
    Session session = repoService.getRepository().getSystemSession(systemWS) ;  
    return session;
  }
  
  private Node getNode(Node parentNode, String nodeName, boolean autoCreate) throws Exception {
    if(parentNode.hasNode(nodeName)) return parentNode.getNode(nodeName);
    if(!autoCreate) return null;
    Node node  = parentNode.addNode(nodeName);
    parentNode.save();
    return node;
  }
  
  private Node getApplicationRegistryNode(boolean autoCreate) throws Exception {
    Node node = getNode(getSession().getRootNode(), jcrSystem, autoCreate);
    if((node = getNode(node, applicationData, autoCreate)) == null && !autoCreate) return null;
    if((node = getNode(node, registry, autoCreate)) == null && !autoCreate) return null;
    if((node = getNode(node, applications, autoCreate)) == null && !autoCreate) return null;
    return node;
  }

  private Node getNode(Node node, String property, String value) throws Exception {
    if(node.hasProperty(property) && value.equals(node.getProperty(property).getString())) return node;
    NodeIterator iterator = node.getNodes();
    while(iterator.hasNext()){
      Node returnNode = getNode(iterator.nextNode(), property, value);
      if(returnNode != null) return returnNode;
    }
    return null;
  }
  
}