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
  //TODO: do not store the service,  pass it as the method parameter instead
  static protected ApplicationRegistryService service_ ;

  public TestApplicationRegistryService(String name) {
    super(name);
  }

  public void testService() throws Exception {
    PortalContainer portalContainer = PortalContainer.getInstance() ;
    service_ = (ApplicationRegistryService)portalContainer.getComponentInstanceOfType(ApplicationRegistryService.class) ;
    
    assertNotNull(service_) ;
    assertAppCategoryOperation() ;
    assertApplicationOperation() ;
    
    service_.clearAllRegistries() ;
    System.out.println("\n\n\n\n");
  }
  
  void assertAppCategoryOperation() throws Exception {
    assertAppCategorySave() ;
    assertAppCategoryGet() ; 
    assertCategoryUpdate() ;
    assertCategoryRemove() ;
  }
  
  void assertAppCategorySave() throws Exception {
    String categoryName = "Office" ;
    String categoryDes = "Tools for officer." ;
    ApplicationCategory category = createAppCategory(categoryName, categoryDes) ;

    // Before save category
    int numberOfCategories = service_.getApplicationCategories().size() ;
    assertEquals(0, numberOfCategories) ;
    
    // Save category
    service_.save(category) ;
    
    numberOfCategories = service_.getApplicationCategories().size() ;
    assertEquals(1, numberOfCategories) ;
    
    ApplicationCategory returnCategory1 = service_.getApplicationCategories().get(0) ;
    assertNotNull(returnCategory1) ;
    assertEquals(category.getName(), returnCategory1.getName()) ;
    assertEquals(categoryName, returnCategory1.getName()) ;
    

    ApplicationCategory returnCategory2 = service_.getApplicationCategory(categoryName);
    assertNotNull(returnCategory2) ;
    assertEquals(category.getName(), returnCategory2.getName()) ;
    assertEquals(categoryName, returnCategory2.getName()) ;
    
    service_.clearAllRegistries() ;    
  }
  
  void assertAppCategoryGet() throws Exception {
    String[] categoryNames = {"Office", "Game"} ;
    
    for (String name : categoryNames) {
      ApplicationCategory category = createAppCategory(name, "None") ;
      service_.save(category) ;
    }
    
    for (String  name : categoryNames) {
      ApplicationCategory returnCategory = service_.getApplicationCategory(name) ;
      assertEquals(name, returnCategory.getName()) ;
    }
    
    service_.clearAllRegistries() ;
  }
  
  void assertCategoryUpdate() throws Exception {
    String categoryName = "Office" ;
    String categoryDes = "Tools for officer." ;

    ApplicationCategory category = createAppCategory(categoryName, categoryDes) ;
    service_.save(category) ;
    
    int numberOfCategories = service_.getApplicationCategories().size() ;
    assertEquals(1, numberOfCategories) ;
    
    ApplicationCategory returnCategory1 = service_.getApplicationCategory(categoryName);
    assertEquals(categoryDes, returnCategory1.getDescription()) ;

    // Use save() method to update category
    String newDescription = "New description for office category." ;
    category.setDescription(newDescription) ;
    service_.save(category) ;
    
    List<ApplicationCategory> categories = service_.getApplicationCategories() ;
    assertEquals(1, categories.size()) ;
    
    ApplicationCategory returnCategory = categories.get(0) ;
    assertEquals(newDescription, returnCategory.getDescription()) ;
    
    service_.clearAllRegistries() ;
  }
  
  void assertCategoryRemove() throws Exception {
    String[] categoryNames = {"Office", "Game"} ;
    
    for (String name : categoryNames) {
      ApplicationCategory category = createAppCategory(name, "None") ;
      service_.save(category) ;
    }
    
    for (String  name : categoryNames) {
      ApplicationCategory returnCategory = service_.getApplicationCategory(name) ;      
      service_.remove(returnCategory) ;
      
      ApplicationCategory returnCategory2 = service_.getApplicationCategory(name) ;
      assertNull(returnCategory2);
    }
    
    int numberOfCategories = service_.getApplicationCategories().size() ;
    assertEquals(0, numberOfCategories) ;
  }
    
  void assertApplicationOperation() throws Exception {
    assertApplicationSave() ;
    assertApplicationUpdate() ;
    assertApplicationRemove() ;
  }
  
  void assertApplicationSave() throws Exception {
    String categoryName = "Office" ;
    String appType = "TypeOne" ;
    String appGroup = "GroupOne" ;
    String[] appNames = {"OpenOffice.org", "MS Office"} ;
    
    ApplicationCategory appCategory = createAppCategory(categoryName, "None") ;
    service_.save(appCategory) ;
    
    for(String appName : appNames) {
      Application app = creatApplication(appName, appType, appGroup) ;
      service_.save(appCategory, app) ;
    }
   
    List<Application> apps = service_.getApplications(appCategory) ;
    assertEquals(2, apps.size()) ;

    for (String appName : appNames) {
      String appId = categoryName + "/" + appName ;
      
      Application app = service_.getApplication(appId) ;
      assertEquals(appName, app.getApplicationName()) ;  
    }       
  }
  
  void assertApplicationUpdate() throws Exception {
    String categoryName = "Office" ;
    String appType = "TypeOne" ;
    String appGroup = "GroupOne" ;
    String[] appNames = {"OpenOffice.org", "MS Office"} ;
    
    ApplicationCategory appCategory = createAppCategory(categoryName, "None") ;
    service_.save(appCategory) ;
    
    // Save apps with description
    for(String appName : appNames) {
      String oldDesciption = "This is: " + appName ;
      Application app = creatApplication(appName, appType, appGroup) ;
      app.setDescription(oldDesciption) ;
      service_.save(appCategory, app) ;
    }

    for (String appName : appNames) {
      String appId = categoryName + "/" + appName ;
      String oldDesciption = "This is: " + appName ;
      
      Application app = service_.getApplication(appId) ;
      assertEquals(oldDesciption, app.getDescription()) ;  
    }
    
    // Update apps with new description: use save() method
    List<Application> apps = service_.getApplications(appCategory) ;
    for (Application app : apps) {
      String newDesciption = "This is: " + app.getApplicationName() + " suite.";
      app.setDescription(newDesciption) ;
      service_.save(appCategory, app) ;
      
    }
    
    for (String appName : appNames) {
      String appId = categoryName + "/" + appName ;
      
      Application app = service_.getApplication(appId) ;      
      String newDesciption = "This is: " + app.getApplicationName() + " suite.";
      assertEquals(newDesciption, app.getDescription()) ;  
    }
    
    // Update apps with new description: use update() method
    for(String appName : appNames) {
      String appId = categoryName + "/" + appName ;
      String newDesciption = "This is new : " + appName + " suite.";
      
      Application app = service_.getApplication(appId) ;
      app.setDescription(newDesciption) ;
      service_.update(app) ;
    }
    
    for (String appName : appNames) {
      String appId = categoryName + "/" + appName ;
      String newDesciption = "This is new : " + appName + " suite.";      
      Application app = service_.getApplication(appId) ;      
      assertEquals(newDesciption, app.getDescription()) ;  
    }
    
    List<Application> apps2 = service_.getApplications(appCategory) ;
    assertEquals(2, apps2.size()) ;
    
    service_.clearAllRegistries() ;
  }
  
  void assertApplicationRemove() throws Exception {
    String categoryName = "Office" ;
    String appType = "TestType" ;
    String appGroup = "TestGroup" ;
    String[] appNames = {"OpenOffice.org", "MS Office"} ;
    
    ApplicationCategory appCategory = createAppCategory(categoryName, "None") ;
    service_.save(appCategory) ;
    
    for(String appName : appNames) {
      Application app = creatApplication(appName, appType, appGroup) ;
      service_.save(appCategory, app) ;
    }

    List<Application> apps = service_.getApplications(appCategory) ;
    assertEquals(2, apps.size()) ;

    for (Application app : apps) {
      service_.remove(app) ;
    }

    List<Application> apps2 = service_.getApplications(appCategory) ;
    assertEquals(0, apps2.size()) ;    
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
  
  void assertImportExoApplication(ApplicationRegistryService service) throws Exception {
    PortalContainer container = PortalContainer.getInstance() ;
    WebAppController controller = 
      (WebAppController)container.getComponentInstanceOfType(WebAppController.class) ;
    controller.addApplication(new ExoApplication("exo.app.web", "eXoBrowser", "eXoBrowser")) ;
    controller.addApplication(new ExoApplication("exo.app.web", "eXoBrowser", "eXoConsole")) ;
    service.importExoApplications() ;
    //TODO: Verify that eXoBrowser and eXoConsole is registered with the service
  }
  
  static public class ExoApplication  extends org.exoplatform.web.application.Application {
    private String name_ ;
    private String group_ ;
    private String id_ ;
    
    public ExoApplication(String group, String name, String id) {
      name_ =  name ;  group_ = group ; id_ = id ;
    }
    
    public String getApplicationType() { return EXO_APPLICATION_TYPE; }
    public String getApplicationGroup() { return group_; }

    public String getApplicationId() { return id_; }

    public String getApplicationName() { return name_ ;}

    public ResourceBundle getOwnerResourceBundle(String arg0, Locale arg1) throws Exception { return null; }
    public ResourceBundle getResourceBundle(Locale arg0) throws Exception {  return null; }    
  }
}