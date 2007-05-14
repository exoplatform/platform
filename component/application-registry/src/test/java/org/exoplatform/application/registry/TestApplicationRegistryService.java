/**
 * Copyright 2001-2003 The eXo platform SARL All rights reserved.
 * Please look at license.txt in info directory for more license detail. 
 */
package org.exoplatform.application.registry;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.exoplatform.application.registry.Application;
import org.exoplatform.application.registry.ApplicationCategory;
import org.exoplatform.application.registry.ApplicationRegistryService;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.test.BasicTestCase;
import org.exoplatform.web.WebAppController;

/**
 * Created y the eXo platform team
 * User: Benjamin Mestrallet
 * Date: 16 juin 2004
 */
public class TestApplicationRegistryService extends BasicTestCase {
  public TestApplicationRegistryService(String name) {
    super(name);
  }

  public void testService() throws Exception {
    PortalContainer portalContainer = PortalContainer.getInstance() ;
    ApplicationRegistryService service = (ApplicationRegistryService)portalContainer.getComponentInstanceOfType(ApplicationRegistryService.class) ;
    
    assertNotNull(service) ;
    assertAppCategoryOperation(service) ;
    assertApplicationOperation(service) ;
    assertImportExoApplication(service) ;

    service.clearAllRegistries() ;
    System.out.println("\n\n\n\n");
  }
  
  void assertAppCategoryOperation(ApplicationRegistryService service) throws Exception {
    assertAppCategorySave(service) ;
    assertAppCategoryGet(service) ; 
    assertCategoryUpdate(service) ;
    assertCategoryRemove(service) ;
  }
  
  void assertAppCategorySave(ApplicationRegistryService service) throws Exception {
    String categoryName = "Office" ;
    String categoryDes = "Tools for officer." ;
    ApplicationCategory category = createAppCategory(categoryName, categoryDes) ;

    // Before save category
    int numberOfCategories = service.getApplicationCategories().size() ;
    assertEquals(0, numberOfCategories) ;
    
    // Save category
    service.save(category) ;
    
    numberOfCategories = service.getApplicationCategories().size() ;
    assertEquals(1, numberOfCategories) ;
    
    ApplicationCategory returnCategory1 = service.getApplicationCategories().get(0) ;
    assertNotNull(returnCategory1) ;
    assertEquals(category.getName(), returnCategory1.getName()) ;
    assertEquals(categoryName, returnCategory1.getName()) ;
    

    ApplicationCategory returnCategory2 = service.getApplicationCategory(categoryName);
    assertNotNull(returnCategory2) ;
    assertEquals(category.getName(), returnCategory2.getName()) ;
    assertEquals(categoryName, returnCategory2.getName()) ;
    service.clearAllRegistries() ;
  }
  
  void assertAppCategoryGet(ApplicationRegistryService service) throws Exception {
    String[] categoryNames = {"Office", "Game"} ;
    
    for (String name : categoryNames) {
      ApplicationCategory category = createAppCategory(name, "None") ;
      service.save(category) ;
    }
    
    for (String  name : categoryNames) {
      ApplicationCategory returnCategory = service.getApplicationCategory(name) ;
      assertEquals(name, returnCategory.getName()) ;
    }
    
    service.clearAllRegistries() ;
  }
  
  void assertCategoryUpdate(ApplicationRegistryService service) throws Exception {
    String categoryName = "Office" ;
    String categoryDes = "Tools for officer." ;

    ApplicationCategory category = createAppCategory(categoryName, categoryDes) ;
    service.save(category) ;
    
    int numberOfCategories = service.getApplicationCategories().size() ;
    assertEquals(1, numberOfCategories) ;
    
    ApplicationCategory returnCategory1 = service.getApplicationCategory(categoryName);
    assertEquals(categoryDes, returnCategory1.getDescription()) ;

    // Use save() method to update category
    String newDescription = "New description for office category." ;
    category.setDescription(newDescription) ;
    service.save(category) ;
    
    List<ApplicationCategory> categories = service.getApplicationCategories() ;
    assertEquals(1, categories.size()) ;
    
    ApplicationCategory returnCategory = categories.get(0) ;
    assertEquals(newDescription, returnCategory.getDescription()) ;
    
    service.clearAllRegistries() ;
  }
  
  void assertCategoryRemove(ApplicationRegistryService service) throws Exception {
    String[] categoryNames = {"Office", "Game"} ;
    
    for (String name : categoryNames) {
      ApplicationCategory category = createAppCategory(name, "None") ;
      service.save(category) ;
    }
    
    for (String  name : categoryNames) {
      ApplicationCategory returnCategory = service.getApplicationCategory(name) ;      
      service.remove(returnCategory) ;
      
      ApplicationCategory returnCategory2 = service.getApplicationCategory(name) ;
      assertNull(returnCategory2);
    }
    
    int numberOfCategories = service.getApplicationCategories().size() ;
    assertEquals(0, numberOfCategories) ;
  }
    
  void assertApplicationOperation(ApplicationRegistryService service) throws Exception {
    assertApplicationSave(service) ;
    assertApplicationUpdate(service) ;
    assertApplicationRemove(service) ;
  }
  
  void assertApplicationSave(ApplicationRegistryService service) throws Exception {
    String categoryName = "Office" ;
    String appType = "TypeOne" ;
    String appGroup = "GroupOne" ;
    String[] appNames = {"OpenOffice.org", "MS Office"} ;
    
    ApplicationCategory appCategory = createAppCategory(categoryName, "None") ;
    service.save(appCategory) ;
    
    for(String appName : appNames) {
      Application app = creatApplication(appName, appType, appGroup) ;
      service.save(appCategory, app) ;
    }
   
    List<Application> apps = service.getApplications(appCategory) ;
    assertEquals(2, apps.size()) ;

    for (String appName : appNames) {
      String appId = categoryName + "/" + appName ;
      
      Application app = service.getApplication(appId) ;
      assertEquals(appName, app.getApplicationName()) ;  
    }       
  }
  
  void assertApplicationUpdate(ApplicationRegistryService service) throws Exception {
    String categoryName = "Office" ;
    String appType = "TypeOne" ;
    String appGroup = "GroupOne" ;
    String[] appNames = {"OpenOffice.org", "MS Office"} ;
    
    ApplicationCategory appCategory = createAppCategory(categoryName, "None") ;
    service.save(appCategory) ;
    
    // Save apps with description
    for(String appName : appNames) {
      String oldDesciption = "This is: " + appName ;
      Application app = creatApplication(appName, appType, appGroup) ;
      app.setDescription(oldDesciption) ;
      service.save(appCategory, app) ;
    }

    for (String appName : appNames) {
      String appId = categoryName + "/" + appName ;
      String oldDesciption = "This is: " + appName ;
      
      Application app = service.getApplication(appId) ;
      assertEquals(oldDesciption, app.getDescription()) ;  
    }
    
    // Update apps with new description: use save() method
    List<Application> apps = service.getApplications(appCategory) ;
    for (Application app : apps) {
      String newDesciption = "This is: " + app.getApplicationName() + " suite.";
      app.setDescription(newDesciption) ;
      service.save(appCategory, app) ;
      
    }
    
    for (String appName : appNames) {
      String appId = categoryName + "/" + appName ;
      
      Application app = service.getApplication(appId) ;      
      String newDesciption = "This is: " + app.getApplicationName() + " suite.";
      assertEquals(newDesciption, app.getDescription()) ;  
    }
    
    // Update apps with new description: use update() method
    for(String appName : appNames) {
      String appId = categoryName + "/" + appName ;
      String newDesciption = "This is new : " + appName + " suite.";
      
      Application app = service.getApplication(appId) ;
      app.setDescription(newDesciption) ;
      service.update(app) ;
    }
    
    for (String appName : appNames) {
      String appId = categoryName + "/" + appName ;
      String newDesciption = "This is new : " + appName + " suite.";      
      Application app = service.getApplication(appId) ;      
      assertEquals(newDesciption, app.getDescription()) ;  
    }
    
    List<Application> apps2 = service.getApplications(appCategory) ;
    assertEquals(2, apps2.size()) ;
    
    service.clearAllRegistries() ;
  }
  
  void assertApplicationRemove(ApplicationRegistryService service) throws Exception {
    String categoryName = "Office" ;
    String appType = "TestType" ;
    String appGroup = "TestGroup" ;
    String[] appNames = {"OpenOffice.org", "MS Office"} ;
    
    ApplicationCategory appCategory = createAppCategory(categoryName, "None") ;
    service.save(appCategory) ;
    
    for(String appName : appNames) {
      Application app = creatApplication(appName, appType, appGroup) ;
      service.save(appCategory, app) ;
    }

    List<Application> apps = service.getApplications(appCategory) ;
    assertEquals(2, apps.size()) ;

    for (Application app : apps) {
      service.remove(app) ;
    }

    List<Application> apps2 = service.getApplications(appCategory) ;
    assertEquals(0, apps2.size()) ;    
  }
  
  void assertImportExoApplication(ApplicationRegistryService service) throws Exception {
    PortalContainer container = PortalContainer.getInstance() ;
    WebAppController controller = 
      (WebAppController)container.getComponentInstanceOfType(WebAppController.class) ;
    
    String group = "exo.app.web" ;
    String[] appNames = {"eXoBrowser", "eXoConsole"} ;
    for (String name : appNames) {
      controller.addApplication(new MockExoApplication(group, name, name)) ;
    }
    service.importExoApplications() ;
    
    ApplicationCategory category = createAppCategory(group, "None") ;
    assertEquals(appNames.length, service.getApplications(category).size()) ;
    
    for (String name : appNames) {
      String appId = group + "/" + name ;
      Application app = service.getApplication(appId) ;
      assertEquals(name, app.getApplicationName()) ;
      assertEquals(group, app.getCategoryName()) ;
      assertEquals(group, app.getApplicationGroup()) ;
    }
    
    service.clearAllRegistries() ;
  }
  
  static public class MockExoApplication  extends org.exoplatform.web.application.Application {
    private String name_ ;
    private String group_ ;
    private String id_ ;
    
    public MockExoApplication(String group, String name, String id) {
      name_ =  name ;  group_ = group ; id_ = id ;
    }
    
    public String getApplicationType() { return EXO_APPLICATION_TYPE; }
    public String getApplicationGroup() { return group_; }

    public String getApplicationId() { return id_; }

    public String getApplicationName() { return name_ ;}

    public ResourceBundle getOwnerResourceBundle(String arg0, Locale arg1) throws Exception { return null; }
    public ResourceBundle getResourceBundle(Locale arg0) throws Exception {  return null; }    
  }

  ApplicationCategory createAppCategory(String categoryName, String categoryDes) {
    ApplicationCategory category = new ApplicationCategory () ;
    category.setName(categoryName) ;
    category.setDisplayName(categoryName);
    category.setDescription(categoryDes) ;
    return category ;
  }
  
  Application creatApplication(String appName, String appType, String appGroup) {
    Application app = new Application() ;
    app.setApplicationName(appName) ;
    app.setDisplayName(appName);
    app.setApplicationType(appType) ;
    app.setApplicationGroup(appGroup) ;
    return app ;
  }
  
}