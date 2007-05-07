/**
 * Copyright 2001-2003 The eXo platform SARL All rights reserved.
 * Please look at license.txt in info directory for more license detail. 
 */

package org.exoplatform.application.registery.jcr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;

import org.exoplatform.application.registery.Application;
import org.exoplatform.application.registery.ApplicationCategory;
import org.exoplatform.application.registery.ApplicationRegisteryService;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.registry.ApplicationRegistry;
import org.exoplatform.registry.JCRRegistryService;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.portletcontainer.monitor.PortletContainerMonitor;
import org.exoplatform.services.portletcontainer.monitor.PortletRuntimeData;

/**
 * Created y the eXo platform team
 * User: lebienthuy@gmail.com
 * Date: 3/7/2007
 */
public class ApplicationRegisteryServiceImpl implements ApplicationRegisteryService {
  
  private final static String SYSTEM_WS = "production".intern();

  private final static String APPLICATION_NODE_TYPE = "exo:application";
  private final static String CATEGORY_NODE_TYPE = "exo:applicationCategory";
  final private static String APPLLICATION_NAME = "ApplicationRegistryService";
  private DataMapper mapper = new DataMapper();
  private JCRRegistryService jcrRegService_;
  public ApplicationRegisteryServiceImpl(JCRRegistryService jcrRegService) throws Exception {
    jcrRegService_ = jcrRegService;
    jcrRegService_.createApplicationRegistry(new ApplicationRegistry(APPLLICATION_NAME), false);
  }
  
  public List<ApplicationCategory> getApplicationCategories() throws Exception {
    List<ApplicationCategory> lists = new ArrayList<ApplicationCategory>();
    Session session = jcrRegService_.getSession();
    NodeIterator iterator = jcrRegService_.getApplicationRegistryNode(session, APPLLICATION_NAME).getNodes();
    while(iterator.hasNext()) {
      Node node = iterator.nextNode();
      lists.add(mapper.nodeToApplicationCategory(node));
    }
    session.logout();
    return lists;
  }
  
  public void save(ApplicationCategory category) throws Exception {
    Session session = jcrRegService_.getSession();
    Node root = jcrRegService_.getApplicationRegistryNode(session, APPLLICATION_NAME);
    category.setName(category.getName().replace(' ', '_'));
    Node node = null;
    if(root.hasNode(category.getName())){
      node = root.getNode(category.getName());
    } else {
      node = root.addNode(category.getName(), CATEGORY_NODE_TYPE);
      root.save();
    }
    mapper.applicationCategoryToNode(category, node);
    node.save();
    session.save();
    session.logout();
  }
  
  public void remove(ApplicationCategory category) throws Exception {
    Session session = jcrRegService_.getSession();
    Node root = jcrRegService_.getApplicationRegistryNode(session, APPLLICATION_NAME);
    if(!root.hasNode(category.getName()))  return ; 
    Node node = root.getNode(category.getName()); 
    node.remove();
    root.save();
    session.save();
    session.logout();   
  }

  public ApplicationCategory getApplicationCategory(String name) throws Exception {
    Session session = jcrRegService_.getSession();
    Node node = jcrRegService_.getApplicationRegistryNode(session, APPLLICATION_NAME);
    ApplicationCategory  category = null;
    if(node.hasNode(name)) category = mapper.nodeToApplicationCategory(node.getNode(name));
    session.logout();
    return category;
  }

  public List<Application> getApplications(ApplicationCategory category) throws Exception {
    List<Application> list = new ArrayList<Application>();
    Session session = jcrRegService_.getSession();
    Node root = jcrRegService_.getApplicationRegistryNode(session, APPLLICATION_NAME);
    if(!root.hasNode(category.getName())) return list; 
    Node categoryNode = root.getNode(category.getName());
    NodeIterator iterator  = categoryNode.getNodes();
    while(iterator.hasNext()){
      Node portletNode = iterator.nextNode();
      list.add(mapper.nodeToApplication(portletNode));
    }
    session.logout();
    return list;
  }

  public Application getApplication(String id) throws Exception {
    String [] components = id.split("/");
    if(components.length < 2) return null;
    Session session = jcrRegService_.getSession();
    Node node = getApplicationNode(session, components[0], components[1]);
    Application application = null;
    if(node != null) application = mapper.nodeToApplication(node);
    return application;
  }
  
  public void save(ApplicationCategory category, Application application) throws Exception {
    Session session = jcrRegService_.getSession();
    save(session, category, application);
  }

  private void save(Session session, ApplicationCategory category, Application application) throws Exception {
    application.setId(category.getName() + "/" + application.getApplicationName().replace(' ', '_'));
    application.setCategoryName(category.getName());
    
    Node rootNode = jcrRegService_.getApplicationRegistryNode(session, APPLLICATION_NAME);
    Node categoryNode ;
    if(rootNode.hasNode(category.getName())) { 
      categoryNode = rootNode.getNode(category.getName());
    } else {
      categoryNode = rootNode.addNode(category.getName(), CATEGORY_NODE_TYPE);
      mapper.applicationCategoryToNode(category, categoryNode);
      rootNode.save();
      session.save();
    }
    
    if(categoryNode.hasNode(application.getApplicationName())) {
      update(application);
      session.logout();
      return;
    }
    Node portletNode = categoryNode.addNode(application.getApplicationName(), APPLICATION_NODE_TYPE);
    mapper.applicationToNode(application, portletNode);
    categoryNode.save();
    session.save();
    session.logout();
  }

  public void remove(Application application) throws Exception {
    Session session = jcrRegService_.getSession();
    Node node = getApplicationNode(session, application.getCategoryName(), application.getApplicationName());
    if(node == null) return ;
    Node categoryNode = node.getParent();
    node.remove();
    categoryNode.save();
    session.save();
  }

  public void update(Application application) throws Exception {
    Session session = jcrRegService_.getSession();
    Node node = getApplicationNode(session, application.getCategoryName(), application.getApplicationName());
    if(node == null) return ;
    mapper.applicationToNode(application, node);
    node.save();
    session.save();
  }
  
  public List<ApplicationCategory> getApplicationCategories(String accessUser) throws Exception {
    return null;
  }
  
  public void importJSR168Portlets() throws Exception {
    PortalContainer manager  = PortalContainer.getInstance();
    PortletContainerMonitor monitor =
      (PortletContainerMonitor) manager.getComponentInstanceOfType(PortletContainerMonitor.class) ;
    Collection portletDatas = monitor.getPortletRuntimeDataMap().values();  
    
    Session session = jcrRegService_.getSession();
    
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
        category.setDisplayName(categoryName);
        save(category);
      }

      Node portletNode = getApplicationNode(session, category.getName(), portletName);
      if(portletNode != null)  continue;
      Application portlet = new Application();
      portlet.setApplicationName(portletName);
      portlet.setApplicationGroup(categoryName);
      portlet.setApplicationType("jsr168-portlet");
      portlet.setDescription("jsr168 portlet application");
      portlet.setDisplayName(portletName);
      save(session, category, portlet);
    }
  }
    
  private Node getApplicationNode(Session session, String category, String name) throws Exception {
    Node node = jcrRegService_.getApplicationRegistryNode(session, APPLLICATION_NAME);
    if(!node.hasNode(category))  return null; 
    node = node.getNode(category);
    if(node.hasNode(name)) return node.getNode(name);
    return null;
  }

  public void clearAllRegistries() throws Exception {    
    Session session = jcrRegService_.getSession();
    Node homeNode = jcrRegService_.getApplicationRegistryNode(session, APPLLICATION_NAME);
    Node parentNode = homeNode.getParent();
    homeNode.remove();
    parentNode.save();
    parentNode.addNode(APPLICATION_NODE_TYPE);
    parentNode.save();
    session.save();
  }
  
}