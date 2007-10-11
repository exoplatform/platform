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

import org.exoplatform.application.registry.ApplicationCategoriesPlugins;
import org.exoplatform.application.registry.Application;
import org.exoplatform.application.registry.ApplicationCategory;
import org.exoplatform.application.registry.ApplicationRegistryService;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.ComponentPlugin;
import org.exoplatform.registry.ApplicationRegistry;
import org.exoplatform.registry.JCRRegistryService;
import org.exoplatform.services.organization.MembershipHandler;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.portletcontainer.monitor.PortletContainerMonitor;
import org.exoplatform.services.portletcontainer.monitor.PortletRuntimeData;
import org.exoplatform.web.WebAppController;
import org.picocontainer.Startable;

/**
 * Created by the eXo platform team
 * User: lebienthuy@gmail.com
 * Date: 3/7/2007
 */
public class ApplicationRegistryServiceImpl implements ApplicationRegistryService, Startable {
  
  private final static String APPLICATION_NAME = "ApplicationRegistry";
  
  private final static String APPLICATION_NODE_TYPE = "exo:application";
  private final static String CATEGORY_NODE_TYPE = "exo:applicationCategory";
  
  private DataMapper mapper = new DataMapper();
  private JCRRegistryService jcrRegService_;

  private List<ApplicationCategoriesPlugins> plugins;
  
  public ApplicationRegistryServiceImpl(JCRRegistryService jcrRegService) throws Exception {
    jcrRegService_ = jcrRegService ;
    
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

  public List<Application> getApplications(ApplicationCategory category, String...appTypes) throws Exception {
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
      Node appNode = iterator.nextNode();
      Application application =  mapper.nodeToApplication(appNode);
      if(isApplicationType(application, appTypes)) list.add(application);
    }
    session.logout();
    return list;
  }
  
  public List<Application> getAllApplications() throws Exception {
    List<Application> list = new ArrayList<Application>();
    List<ApplicationCategory> listCategorys = getApplicationCategories();
    for(ApplicationCategory cate: listCategorys) {
      list.addAll(getApplications(cate));
    }    
    return list;
  }
  
  private boolean isApplicationType(Application app, String...appTypes){
    if(appTypes == null || appTypes.length < 1) return true;
    for(String appType : appTypes) {
      if(appType.equals(app.getApplicationType()))  return true;
    }
    return false;
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
//    application.setId(category.getName() + "/" + application.getApplicationName().replace(' ', '_'));
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
  
  public Application getApplication(String category, String name) throws Exception {
    Session session = jcrRegService_.getSession();
    Node node = getApplicationNode(session, category, name);
    if(node == null) {
      session.logout();
      return  null;
    }
    return mapper.nodeToApplication(node);
  } 
 
  @SuppressWarnings("unchecked")
  public List<ApplicationCategory> getApplicationCategories(String accessUser, String...appTypes) throws Exception {
    List<ApplicationCategory> categories = getApplicationCategories();
    Iterator<ApplicationCategory> iterCategory = categories.iterator();
    
    PortalContainer manager  = PortalContainer.getInstance();
    OrganizationService orgService = (OrganizationService)manager.getComponentInstanceOfType(OrganizationService.class);
    while(iterCategory.hasNext()) {
      ApplicationCategory category = iterCategory.next();
      List<Application> apps = getApplications(category, appTypes);
      Iterator<Application> iterApp = apps.iterator();
      while(iterApp.hasNext()) {
        Application app = iterApp.next();
        if(!hasViewPermission(orgService, accessUser, app.getAccessPermissions())) iterApp.remove();
      }
      category.setApplications(apps);      
      if(apps.size() < 1) iterCategory.remove();
    }
    return categories;
  }
  
  private boolean hasViewPermission(OrganizationService orgService, String remoteUser, ArrayList<String> expPerms) throws Exception {
    if(expPerms == null || expPerms.size() < 1) expPerms = new ArrayList<String>(); 
    expPerms.add("*:/user");
    for(String ele : expPerms) {
      if(hasViewPermission(orgService, remoteUser, ele)) return true;
    }
    return false;
  }
  
  private boolean hasViewPermission(OrganizationService orgService, String remoteUser, String expPerm) throws Exception {
    if(expPerm == null) return true;
    String[] temp = expPerm.split(":") ;
    if(temp.length < 2) return false;
    String membership = temp[0].trim() ;
    String groupId= temp[1].trim();
    if("/guest".equals(groupId)) return true ;
    MembershipHandler handler = orgService.getMembershipHandler();
    if(membership == null || "*".equals(membership)) {
      Collection<?> c = handler.findMembershipsByUserAndGroup(remoteUser, groupId) ;
      if(c == null) return false ;
      return c.size() > 0 ;
    } 
    return handler.findMembershipByUserGroupAndType(remoteUser, groupId, membership) != null;
  }
  
  public void importJSR168Portlets() throws Exception {
    Session session = jcrRegService_.getSession();
    PortalContainer manager  = PortalContainer.getInstance();
    PortletContainerMonitor monitor =
      (PortletContainerMonitor) manager.getComponentInstanceOfType(PortletContainerMonitor.class) ;
    Collection<?> portletDatas = monitor.getPortletRuntimeDataMap().values();  
    
    Iterator<?> iterator = portletDatas.iterator();
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
      app.setCategoryName(categoryName);
      app.setApplicationType(org.exoplatform.web.application.Application.EXO_PORTLET_TYPE);
      app.setDescription("A portlet application");
      app.setDisplayName(portletName);
      app.setId(categoryName + "/" + portletName);
      save(category, app);
    }
    session.logout();
  }
  
//  public void importExoApplications() throws Exception {
//    Session session = jcrRegService_.getSession();
//    PortalContainer container  = PortalContainer.getInstance() ;
//    WebAppController appController = 
//      (WebAppController)container.getComponentInstanceOfType(WebAppController.class) ;
//    List<org.exoplatform.web.application.Application> eXoApplications = 
//      appController.getApplicationByType(org.exoplatform.web.application.Application.EXO_APPLICATION_TYPE) ;
//    List<org.exoplatform.web.application.Application> eXoWidgets = 
//      appController.getApplicationByType(org.exoplatform.web.application.Application.EXO_WIDGET_TYPE) ;
//    eXoApplications.addAll(eXoWidgets);
//    for (org.exoplatform.web.application.Application app : eXoApplications) {
//      ApplicationCategory category = getApplicationCategory(app.getApplicationGroup()) ;
//      if (category == null) {
//        category = new ApplicationCategory() ;
//        category.setName(app.getApplicationGroup()) ;
//        category.setDisplayName(app.getApplicationGroup()) ;
//        category.setDescription(app.getApplicationGroup()) ;
//        save(category) ;
//      }
//      Node appNode = getApplicationNode(session, category.getName(), app.getApplicationName()) ;
//      if (appNode == null) save(category, convertApplication(app)) ;
//    }
//    
//    session.logout() ;
//  }
  
  public void importExoWidgets() throws Exception {
    Session session = jcrRegService_.getSession();
    PortalContainer container = PortalContainer.getInstance();
    WebAppController appController = (WebAppController) container
        .getComponentInstanceOfType(WebAppController.class);
    List<org.exoplatform.web.application.Application> eXoWidgets = appController
        .getApplicationByType(org.exoplatform.web.application.Application.EXO_WIDGET_TYPE);
    if(eXoWidgets == null || eXoWidgets.size() < 1) {
      session.logout();
      return ;
    }
    org.exoplatform.web.application.Application sampleApp = eXoWidgets.get(0) ;
    ApplicationCategory category = getApplicationCategory(sampleApp.getApplicationGroup());
    if (category == null) {
      category = new ApplicationCategory();
      category.setName(sampleApp.getApplicationGroup());
      category.setDisplayName(sampleApp.getApplicationGroup());
      category.setDescription(sampleApp.getApplicationGroup());
      save(category);
    }

    for (org.exoplatform.web.application.Application app : eXoWidgets) {
      Node appNode = getApplicationNode(session, category.getName(), app.getApplicationName());
      if (appNode == null)
        save(category, convertApplication(app));
    }

    session.logout();
  }
  
  private Application convertApplication(org.exoplatform.web.application.Application app) {
    Application returnApplication = new Application() ;
    returnApplication.setApplicationGroup(app.getApplicationGroup()) ;
    returnApplication.setApplicationType(app.getApplicationType()) ;
    returnApplication.setApplicationName(app.getApplicationName()) ;
    returnApplication.setId(app.getApplicationId()) ;
    
    
    returnApplication.setCategoryName(app.getApplicationGroup()) ;
    returnApplication.setDisplayName(app.getApplicationName()) ;
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

  public void initListener(ComponentPlugin com) throws Exception {
    if(com instanceof ApplicationCategoriesPlugins){
      if(plugins == null ) plugins = new ArrayList<ApplicationCategoriesPlugins>();
      plugins.add((ApplicationCategoriesPlugins) com);
    }
  }

  public void start() {
    try{
      jcrRegService_.createApplicationRegistry(new ApplicationRegistry(APPLICATION_NAME), false);
      if(getApplicationCategories().size() > 0) return ;
      if(plugins == null ) return;
      for(ApplicationCategoriesPlugins plugin: plugins)
        plugin.run();
    } catch (Exception e) {
      e.printStackTrace();  // TODO: user LogService
    }
  }

  public void stop() {}

 
}