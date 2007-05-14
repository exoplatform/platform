/**
 * Copyright 2001-2003 The eXo platform SARL All rights reserved.
 * Please look at license.txt in info directory for more license detail. 
 */

package org.exoplatform.application.registry.jcr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;

import org.exoplatform.application.registry.Application;
import org.exoplatform.application.registry.ApplicationCategory;
import org.exoplatform.application.registry.ApplicationRegistryService;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.registry.ApplicationRegistry;
import org.exoplatform.registry.JCRRegistryService;
import org.exoplatform.services.organization.Membership;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.portletcontainer.monitor.PortletContainerMonitor;
import org.exoplatform.services.portletcontainer.monitor.PortletRuntimeData;
import org.exoplatform.web.WebAppController;

/**
 * Created y the eXo platform team
 * User: lebienthuy@gmail.com
 * Date: 3/7/2007
 */
public class ApplicationRegistryServiceImpl implements ApplicationRegistryService {
  
  private final static String APPLICATION_NAME = "ApplicationRegistry";
  
  private final static String APPLICATION_NODE_TYPE = "exo:application";
  private final static String CATEGORY_NODE_TYPE = "exo:applicationCategory";
  
  private DataMapper mapper = new DataMapper();
  private OrganizationService orgService_ ;
  private JCRRegistryService jcrRegService_;
  
  public ApplicationRegistryServiceImpl(JCRRegistryService jcrRegService, OrganizationService orgService) throws Exception {
    jcrRegService_ = jcrRegService ;
    jcrRegService_.createApplicationRegistry(new ApplicationRegistry(APPLICATION_NAME), false);
    this.orgService_ = orgService;
  }
  
  public List<ApplicationCategory> getApplicationCategories() throws Exception {
    Session session = jcrRegService_.getSession();
    List<ApplicationCategory> lists = new ArrayList<ApplicationCategory>();
    NodeIterator iterator = jcrRegService_.getApplicationRegistryNode(session, APPLICATION_NAME).getNodes();
    while(iterator.hasNext()) {
      Node node = iterator.nextNode();
      lists.add(mapper.nodeToApplicationCategory(node));
    }
    session.logout();
    return lists;
  }
  
  public void save(ApplicationCategory category) throws Exception {
    Session session = jcrRegService_.getSession();
    Node root = jcrRegService_.getApplicationRegistryNode(session, APPLICATION_NAME);
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
    Node root = jcrRegService_.getApplicationRegistryNode(session, APPLICATION_NAME);
    if(!root.hasNode(category.getName())) {
      session.logout();
      return ; 
    }
    Node node = root.getNode(category.getName()); 
    node.remove();
    root.save();
    session.save();
    session.logout();
  }

  public ApplicationCategory getApplicationCategory(String name) throws Exception {
    Session session = jcrRegService_.getSession();
    Node node = jcrRegService_.getApplicationRegistryNode(session, APPLICATION_NAME);
    if(node.hasNode(name)) {
      ApplicationCategory category = mapper.nodeToApplicationCategory(node.getNode(name)); 
      session.logout();
      return category; 
    }
    session.logout();
    return null;
  }

  public List<Application> getApplications(ApplicationCategory category) throws Exception {
    Session session = jcrRegService_.getSession();
    List<Application> list = new ArrayList<Application>();
    Node root = jcrRegService_.getApplicationRegistryNode(session, APPLICATION_NAME);
    if(!root.hasNode(category.getName())) {
      session.logout();
      return list; 
    }
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
    Session session = jcrRegService_.getSession();
    String [] components = id.split("/");
    if(components.length < 2) {
      session.logout();
      return null;
    }
    Node node = getApplicationNode(session, components[0], components[1]);
    Application app = null;
    if(node != null) app = mapper.nodeToApplication(node);
    session.logout();
    return app;
  }

  public void save(ApplicationCategory category, Application application) throws Exception {
    Session session = jcrRegService_.getSession();
    application.setId(category.getName() + "/" + application.getApplicationName().replace(' ', '_'));
    application.setCategoryName(category.getName());
    
    Node rootNode = jcrRegService_.getApplicationRegistryNode(session, APPLICATION_NAME);
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
    if(node == null) {
      session.logout();
      return ;
    }
    Node categoryNode = node.getParent();
    node.remove();
    categoryNode.save();
    session.save();
    session.logout();
  }

  public void update(Application application) throws Exception {
    Session session = jcrRegService_.getSession();
    Node node = getApplicationNode(session, application.getCategoryName(), application.getApplicationName());
    if(node == null) {
      session.logout();
      return ;
    }
    mapper.applicationToNode(application, node);
    node.save();
    session.save();
    session.logout();
  }
  
  @SuppressWarnings("unchecked")
  public List<ApplicationCategory> getApplicationCategories(String accessUser) throws Exception {
    List<ApplicationCategory> categories = getApplicationCategories();
    
    List<Membership> memberships = (List<Membership>) orgService_.getMembershipHandler().findMembershipsByUser(accessUser);
    String [] groups = new String[memberships.size()];
    for(int i = 0; i < memberships.size(); i++) {
      groups[i] = memberships.get(i).getGroupId();
    }
    
    
    Iterator<ApplicationCategory> iter = categories.iterator();
    while(iter.hasNext()) {
      ApplicationCategory category = iter.next();
      if(groups == null || computePermission(category, groups)) continue;
      iter.remove();
    }
    
    return categories;
  }
  
  private boolean computePermission(ApplicationCategory category, String [] groups) throws Exception {
    List<Application> apps = getApplications(category);
    Iterator<Application> iter = apps.iterator();
    boolean remove = true;
    while(iter.hasNext()) {
      Application app = iter.next();
      String [] accessGroups = app.getAccessGroup();
      if(accessGroups == null) continue;
      remove = true;      
      for(String accessGroup : accessGroups) {
        for(String group : groups) {
          if(accessGroup.equals(group)) {
            remove = false;
            break;            
          }
        }
        if(!remove) break;
      }
      if(remove) iter.remove();
    }
    return apps.size() > 0;
  }

  public void importJSR168Portlets() throws Exception {
    Session session = jcrRegService_.getSession();
    PortalContainer manager  = PortalContainer.getInstance();
    PortletContainerMonitor monitor =
      (PortletContainerMonitor) manager.getComponentInstanceOfType(PortletContainerMonitor.class) ;
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
        category.setDisplayName(categoryName);
        save(category);
      }

      Node portletNode = getApplicationNode(session, category.getName(), portletName);
      if(portletNode != null)  continue;
      Application app = new Application();
      app.setDisplayName(portletName) ;
      app.setApplicationName(portletName);
      app.setApplicationGroup(categoryName);
      app.setApplicationType("jsr168-portlet");
      app.setDescription("jsr168 portlet application");
      app.setDisplayName(portletName);
      save(category, app);
    }
    session.logout();
  }
  
  public void importExoApplications() throws Exception {
    PortalContainer container  = PortalContainer.getInstance() ;
    WebAppController appController = 
      (WebAppController)container.getComponentInstanceOfType(WebAppController.class) ;
    List<org.exoplatform.web.application.Application> applications = 
      appController.getApplicationByType(org.exoplatform.web.application.Application.EXO_APPLICATION_TYPE) ;
    // Save apps from list to category
    for (org.exoplatform.web.application.Application app : applications) {
      // Save category
      ApplicationCategory category = new ApplicationCategory() ;
      category.setName(app.getApplicationGroup()) ;
      category.setDisplayName(app.getApplicationGroup()) ;
      category.setDescription(app.getApplicationGroup()) ;
      save(category) ;

      // Save app
      save(category, convertApplication(app)) ;
    }
  }
  
  //TODO: Tung.Pham added
  private Application convertApplication(org.exoplatform.web.application.Application app) {
    Application returnApplication = new Application() ;
    
    returnApplication.setApplicationName(app.getApplicationName()) ;
    returnApplication.setId(app.getApplicationId()) ;
    returnApplication.setDisplayName(app.getApplicationName()) ;
    returnApplication.setApplicationGroup(app.getApplicationGroup()) ;
    returnApplication.setCategoryName(app.getApplicationGroup()) ;
    returnApplication.setApplicationType(app.getApplicationType()) ;
    returnApplication.setDescription(app.getDescription()) ;
    
    return returnApplication ;
  }
  
  private Node getApplicationNode(Session session, String category, String name) throws Exception {
    Node node = jcrRegService_.getApplicationRegistryNode(session, APPLICATION_NAME);
    if(!node.hasNode(category)) return null; 
    node = node.getNode(category);
    if(node.hasNode(name)) return node.getNode(name);
    return null;
  }

  //TODO: Tung.Pham modified
  public void clearAllRegistries() throws Exception {    
    Session session = jcrRegService_.getSession();
    Node homeNode = jcrRegService_.getApplicationRegistryNode(session, APPLICATION_NAME);
    Node parentNode = homeNode.getParent();
    homeNode.remove();
    parentNode.save();
    //parentNode.addNode(APPLICATION_NODE_TYPE);
    parentNode.addNode(APPLICATION_NAME);
    parentNode.save();
    session.save();
    session.logout();
  } 
}