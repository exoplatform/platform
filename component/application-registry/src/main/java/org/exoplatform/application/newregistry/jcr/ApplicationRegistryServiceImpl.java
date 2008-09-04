/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.application.newregistry.jcr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.apache.commons.logging.Log;
import org.exoplatform.application.gadget.Gadget;
import org.exoplatform.application.gadget.GadgetRegistryService;
import org.exoplatform.application.newregistry.Application;
import org.exoplatform.application.newregistry.ApplicationCategoriesPlugins;
import org.exoplatform.application.newregistry.ApplicationCategory;
import org.exoplatform.application.newregistry.ApplicationRegistryService;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.component.ComponentPlugin;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.ext.registry.RegistryEntry;
import org.exoplatform.services.jcr.ext.registry.RegistryService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.organization.MembershipHandler;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.portletcontainer.PortletContainerService;
import org.exoplatform.services.portletcontainer.pci.PortletData;
import org.exoplatform.web.WebAppController;
import org.exoplatform.web.application.gadget.GadgetApplication;
import org.picocontainer.Startable;

/**
 * Created by The eXo Platform SARL
 * Author : Tung Pham
 *          thanhtungty@gmail.com
 * Nov 23, 2007  
 */
public class ApplicationRegistryServiceImpl implements ApplicationRegistryService, Startable {
  
  static final private String APPLICATION_REGISTRY = "ApplicationRegistry" ;
  static final private String CATEGORY_DATA = "CategoryData" ;
  static final private String APPLICATIONS = "applications" ;
  
  private Log log = ExoLogger.getLogger("ApplicationRegistryService");
  
  RegistryService regService_ ;
  DataMapper mapper_ = new DataMapper() ;
  private List<ApplicationCategoriesPlugins> plugins;

  public ApplicationRegistryServiceImpl(RegistryService service) throws Exception {
    regService_ = service ;
  }
  
  public List<ApplicationCategory> getApplicationCategories(String accessUser, String... appTypes)
  throws Exception {
    List<ApplicationCategory> categories = getApplicationCategories() ;
    Iterator<ApplicationCategory> cateItr = categories.iterator() ;
    ExoContainer container = ExoContainerContext.getCurrentContainer() ;
    OrganizationService orgService = (OrganizationService) container.getComponentInstanceOfType(OrganizationService.class) ;
    UserACL acl = (UserACL) container.getComponentInstanceOfType(UserACL.class) ;
    while(cateItr.hasNext()) {
      ApplicationCategory cate = cateItr.next() ;
      //TODO: dang.tung: filer category application
      if(!hasAccessPermission(orgService, acl, accessUser, cate)){
        cateItr.remove() ;
        continue ;
      }
      List<Application> applications = getApplications(cate, appTypes) ;
      Iterator<Application> appIterator = applications.iterator() ;
      while(appIterator.hasNext()) {
        Application app = appIterator.next() ;
        if(!hasAccessPermission(orgService, acl, accessUser, app)) appIterator.remove() ;
      }
      cate.setApplications(applications) ;
    }
    return categories ;
  }
  
  public List<ApplicationCategory> getApplicationCategories() throws Exception {
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    Node regNode = regService_.getRegistry(sessionProvider).getNode() ;
    Session session = regNode.getSession() ;
    StringBuilder builder = new StringBuilder("select * from " + DataMapper.EXO_REGISTRYENTRY_NT) ;
    generateScript(builder, "jcr:path", (regNode.getPath() + "/" + RegistryService.EXO_APPLICATIONS + "/" + APPLICATION_REGISTRY + "/%")) ;
    generateScript(builder, DataMapper.TYPE, ApplicationCategory.class.getSimpleName()) ;
    QueryManager queryManager = session.getWorkspace().getQueryManager() ;
    Query query = queryManager.createQuery(builder.toString(), "sql") ;
    QueryResult result = query.execute() ;
    NodeIterator itr = result.getNodes() ;
    List<ApplicationCategory> categories = new ArrayList<ApplicationCategory>() ;
    while(itr.hasNext()) {
      Node cateNode = itr.nextNode() ;
      String entryPath = cateNode.getPath().substring(regNode.getPath().length() + 1) ;
      RegistryEntry entry = regService_.getEntry(sessionProvider, entryPath) ;
      ApplicationCategory cate = mapper_.toApplicationCategory(entry.getDocument()) ;
      categories.add(cate) ;
    }
    sessionProvider.close() ;
    return categories;
  }
  
  private void generateScript(StringBuilder sql, String name, String value){
    if(value == null || value.length() < 1) return ;
    if(sql.indexOf(" where") < 0) sql.append(" where "); else sql.append(" and "); 
    value = value.replace('*', '%') ;
    sql.append(name).append(" like '").append(value).append("'");
  }
  
  public ApplicationCategory getApplicationCategory(String name) throws Exception {
    String categoryDataPath = getCategoryPath(name) + "/" + CATEGORY_DATA ;
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    RegistryEntry entry ;
    try {
      entry = regService_.getEntry(sessionProvider, categoryDataPath) ;
    } catch (PathNotFoundException ie) {
      sessionProvider.close() ;
      return null ;
    }
    ApplicationCategory category = mapper_.toApplicationCategory(entry.getDocument()) ;
    sessionProvider.close() ;
    return category ;
  }
  
  public void save(ApplicationCategory category) throws Exception {
    String categoryPath = getCategoryPath(category.getName()) ;
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    RegistryEntry entry ;
    try {
      entry = regService_.getEntry(sessionProvider, categoryPath + "/" + CATEGORY_DATA) ;
    } catch (PathNotFoundException ie) {
      entry = new RegistryEntry(CATEGORY_DATA) ;
      regService_.createEntry(sessionProvider, categoryPath, entry) ;
    }
    mapper_.map(entry.getDocument(), category) ;
    regService_.recreateEntry(sessionProvider, categoryPath, entry) ;
    sessionProvider.close() ;
  }
  
  public void remove(ApplicationCategory category) throws Exception {
    String categoryDataPath = getCategoryPath(category.getName()) + "/" + CATEGORY_DATA ;
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    //remove all applications of this group
    for(Application app : getApplications(category)) {
      remove(app) ;
    }
    //remove category data
    regService_.removeEntry(sessionProvider, categoryDataPath) ;
    sessionProvider.close() ;
  }
  
  public List<Application> getAllApplications() throws Exception {
    List<Application> applications = new ArrayList<Application>() ;
    List<ApplicationCategory> categories = getApplicationCategories() ;
    for(ApplicationCategory cate : categories) {
      applications.addAll(getApplications(cate)) ;
    }
    return applications ;
  }

  public Application getApplication(String id) throws Exception {
    String[] fragments = id.split("/") ;
    if(fragments.length < 2) {
      throw new Exception("Invalid Application Id: [" + id + "]") ;
    }
    String applicationPath = getCategoryPath(fragments[0])
                             + "/" + APPLICATIONS + "/" + fragments[1] ;
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    RegistryEntry entry ;
    try {
      entry = regService_.getEntry(sessionProvider, applicationPath) ;
    } catch (PathNotFoundException ie) {
      sessionProvider.close() ;
      return null ;
    }
    Application application = mapper_.toApplication(entry.getDocument()) ;
    sessionProvider.close() ;
    return application ;
  }

  public Application getApplication(String category, String name) throws Exception {
    String applicationPath = getCategoryPath(category)
                             + "/" + APPLICATIONS + "/" + name ;
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    RegistryEntry entry ;
    try {
      entry = regService_.getEntry(sessionProvider, applicationPath) ;
    } catch (PathNotFoundException ie) {
      sessionProvider.close() ;
      return null ;
    }
    Application application = mapper_.toApplication(entry.getDocument()) ;
    sessionProvider.close() ;
    return application ;
  }

  public List<Application> getApplications(ApplicationCategory category, String... appTypes)
      throws Exception {
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    Node regNode = regService_.getRegistry(sessionProvider).getNode() ;
    Session session = regNode.getSession() ;
    StringBuilder builder = new StringBuilder("select * from " + DataMapper.EXO_REGISTRYENTRY_NT) ;
    generateScript(builder, "jcr:path", (regNode.getPath() + "/" + RegistryService.EXO_APPLICATIONS + "/" + APPLICATION_REGISTRY + "/%")) ;
    generateScript(builder, DataMapper.TYPE, Application.class.getSimpleName()) ;
    generateScript(builder, DataMapper.APPLICATION_CATEGORY_NAME, category.getName()) ;
    QueryManager queryManager = session.getWorkspace().getQueryManager() ;
    Query query = queryManager.createQuery(builder.toString(), "sql") ;
    QueryResult result = query.execute() ;
    NodeIterator itr = result.getNodes() ;
    List<Application> applications = new ArrayList<Application>() ;
    while(itr.hasNext()) {
      Node appNode = itr.nextNode() ;
      String entryPath = appNode.getPath().substring(regNode.getPath().length() + 1) ;
      RegistryEntry entry = regService_.getEntry(sessionProvider, entryPath) ;
      Application app = mapper_.toApplication(entry.getDocument()) ;
      if(isApplicationType(app, appTypes)) applications.add(app) ;
    }
    sessionProvider.close() ;
    return applications ;
  }

  public void importExoWidgets() throws Exception {
    ExoContainer container = ExoContainerContext.getCurrentContainer();
    WebAppController appController = (WebAppController) container
        .getComponentInstanceOfType(WebAppController.class);
    List<org.exoplatform.web.application.Application> eXoWidgets = appController
        .getApplicationByType(org.exoplatform.web.application.Application.EXO_WIDGET_TYPE);
    if(eXoWidgets == null || eXoWidgets.size() < 1) {
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

    for (org.exoplatform.web.application.Application ele : eXoWidgets) {
      Application app = getApplication(category.getName() + "/" + ele.getApplicationName()) ;
      if (app == null) save(category, convertApplication(ele));
    }
  }
  
  //TODO: dang.tung
  public void importExoGadgets() throws Exception {
    ExoContainer container = ExoContainerContext.getCurrentContainer();
    GadgetRegistryService gadgetService = (GadgetRegistryService) container.getComponentInstanceOfType(GadgetRegistryService.class) ;
    List<Gadget> eXoGadgets = gadgetService.getAllGadgets() ;
    if(eXoGadgets == null || eXoGadgets.size() < 1) {
      return ;
    }
    String categoryName = GadgetApplication.EXO_GADGET_GROUP ;
    ApplicationCategory category = getApplicationCategory(categoryName);
    if (category == null) {
      category = new ApplicationCategory();
      category.setName(categoryName);
      category.setDisplayName(categoryName);
      category.setDescription(categoryName);
      save(category);
    }

    for (Gadget ele : eXoGadgets) {
      Application app = getApplication(category.getName() + "/" + ele.getName()) ;
      if (app == null) save(category, convertApplication(ele));
    }
  }

  private Application convertApplication(org.exoplatform.web.application.Application app) {
    Application returnApplication = new Application() ;
    returnApplication.setApplicationGroup(app.getApplicationGroup()) ;
    returnApplication.setApplicationType(app.getApplicationType()) ;
    returnApplication.setApplicationName(app.getApplicationName()) ;
    returnApplication.setCategoryName(app.getApplicationGroup()) ;
    returnApplication.setDisplayName(app.getApplicationName()) ;
    returnApplication.setDescription(app.getDescription()) ;
    
    return returnApplication ;
  }
  
  private Application convertApplication(Gadget gadget) {
    Application returnApplication = new Application() ;
    returnApplication.setApplicationGroup(GadgetApplication.EXO_GADGET_GROUP) ;
    returnApplication.setApplicationType(org.exoplatform.web.application.Application.EXO_GAGGET_TYPE) ;
    returnApplication.setApplicationName(gadget.getName()) ;
    returnApplication.setCategoryName(GadgetApplication.EXO_GADGET_GROUP) ;
    returnApplication.setDisplayName(gadget.getTitle()) ;
    returnApplication.setDescription(gadget.getDescription()) ;
    
    return returnApplication ;
  }

  public void importAllPortlets() throws Exception {
    ExoContainer manager  = ExoContainerContext.getCurrentContainer();
    PortletContainerService pcService =
      (PortletContainerService) manager.getComponentInstanceOfType(PortletContainerService.class) ;
    Map<String, PortletData> allPortletMetaData = pcService.getAllPortletMetaData();
    Iterator<String> iterator = allPortletMetaData.keySet().iterator();
    
    while(iterator.hasNext()) {
      String portletHandle = iterator.next();
      String categoryName = portletHandle.split("/")[0];
      String portletName = portletHandle.split("/")[1];
      
      ApplicationCategory category = null;

      category = getApplicationCategory(categoryName);
      if(category == null) {
        category = new ApplicationCategory();
        category.setName(categoryName);
        category.setDisplayName(categoryName);
        save(category);
      }

      Application app = getApplication(categoryName + "/" + portletName) ;
      if(app != null) continue ; 
      app = new Application();
      app.setDisplayName(portletName) ;
      app.setApplicationName(portletName);
      app.setApplicationGroup(categoryName);
      app.setCategoryName(categoryName);
      app.setApplicationType(org.exoplatform.web.application.Application.EXO_PORTLET_TYPE);
      app.setDescription("A portlet application");
      app.setDisplayName(portletName);
      save(category, app);
    }
  }

  public void remove(Application app) throws Exception {
    String applicationPath = getCategoryPath(app.getCategoryName())
                             + "/" + APPLICATIONS + "/" + app.getApplicationName() ;
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    regService_.removeEntry(sessionProvider, applicationPath) ;
    sessionProvider.close() ;
  }

  public void save(ApplicationCategory category, Application application) throws Exception {
    //prepare category
    String cateName = category.getName() ;
    String categoryPath = getCategoryPath(cateName) ;
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    RegistryEntry entry ;
    try {
      entry = regService_.getEntry(sessionProvider, categoryPath + "/" + CATEGORY_DATA) ;
    } catch (PathNotFoundException ie) {
      entry = new RegistryEntry(CATEGORY_DATA) ;
      mapper_.map(entry.getDocument(), category) ;
      regService_.createEntry(sessionProvider, categoryPath, entry) ;
    }
    
    //save application
    application.setCategoryName(cateName) ;
    String applicationSetPath = getCategoryPath(cateName) + "/" + APPLICATIONS ;
    String appName = application.getApplicationName() ;
    try {
      entry = regService_.getEntry(sessionProvider, applicationSetPath + "/" + appName) ;
    } catch (PathNotFoundException ie) {
      entry = new RegistryEntry(appName) ;
      regService_.createEntry(sessionProvider, applicationSetPath, entry) ;
    }
    mapper_.map(entry.getDocument(), application) ;
    regService_.recreateEntry(sessionProvider, applicationSetPath, entry) ;
    sessionProvider.close() ;
  }

  public void update(Application application) throws Exception {
    String applicationSetPath = getCategoryPath(application.getCategoryName()) + "/" + APPLICATIONS ;
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    RegistryEntry entry = regService_.getEntry(sessionProvider, applicationSetPath + "/" + application.getApplicationName()) ;
    mapper_.map(entry.getDocument(), application) ;
    regService_.recreateEntry(sessionProvider, applicationSetPath, entry) ;
    sessionProvider.close() ;
  }

  public void clearAllRegistries() throws Exception {
    for(ApplicationCategory cate : getApplicationCategories()) {
      remove(cate) ;
    }
  }
  
  public void initListener(ComponentPlugin com) throws Exception {
    if(com instanceof ApplicationCategoriesPlugins){
      if(plugins == null ) plugins = new ArrayList<ApplicationCategoriesPlugins>();
      plugins.add((ApplicationCategoriesPlugins) com);
    }    
  }
  
  public void start() {
    try{
      if(plugins == null ) return;
      for(ApplicationCategoriesPlugins plugin: plugins) plugin.run();
    } catch (Exception e) {
      log.error(e);
    }    
  }

  public void stop() {}
  
  //-------------------------------------Util function-------------------------------/
  private boolean hasAccessPermission(OrganizationService orgService, UserACL acl, String remoteUser, Application app) throws Exception {
    if(acl.getSuperUser().equals(remoteUser)) return true ;
    List<String> permissions = app.getAccessPermissions() ; 
    if(permissions == null) return false ;
    for(String ele : permissions) {
      if(hasViewPermission(orgService, acl, remoteUser, ele)) return true;
    }
    return false;
  }
  //TODO: dang.tung: check ApplicationCategory permission
  //-----------------------------------------------------
  private boolean hasAccessPermission(OrganizationService orgService, UserACL acl, String remoteUser, ApplicationCategory app) throws Exception {
    if(acl.getSuperUser().equals(remoteUser)) return true ;
    List<String> permissions = app.getAccessPermissions() ; 
    if(permissions == null) return false ;
    for(String ele : permissions) {
      if(hasViewPermission(orgService, acl, remoteUser, ele)) return true;
    }
    return false;
  }
  //-----------------------------------------------------
  private boolean hasViewPermission(OrganizationService orgService, UserACL acl, String remoteUser, String expPerm) throws Exception {
    if(UserACL.EVERYONE.equals(expPerm)) return true ;
    String[] temp = expPerm.split(":") ;
    if(temp.length < 2) return false;
    String membership = temp[0].trim() ;
    String groupId= temp[1].trim();
    MembershipHandler handler = orgService.getMembershipHandler();
    if(membership == null || "*".equals(membership)) {
      Collection<?> c = handler.findMembershipsByUserAndGroup(remoteUser, groupId) ;
      if(c == null) return false ;
      return c.size() > 0 ;
    } 
    return handler.findMembershipByUserGroupAndType(remoteUser, groupId, membership) != null;
  }
  
  private String getCategoryPath(String categoryName) {
    return RegistryService.EXO_APPLICATIONS + "/" + APPLICATION_REGISTRY + "/" + categoryName ;
  }
  
  private boolean isApplicationType(Application app, String...appTypes){
    if(appTypes == null || appTypes.length < 1) return true;
    for(String appType : appTypes) {
      if(appType.equals(app.getApplicationType()))  return true;
    }
    return false;
  }

}

