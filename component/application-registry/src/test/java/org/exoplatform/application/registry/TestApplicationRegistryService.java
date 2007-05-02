/**
 * Copyright 2001-2003 The eXo platform SARL All rights reserved.
 * Please look at license.txt in info directory for more license detail. 
 */
package org.exoplatform.application.registry;

import java.util.List;

import org.exoplatform.application.registery.Application;
import org.exoplatform.application.registery.ApplicationCategory;
import org.exoplatform.application.registery.ApplicationRegisteryService;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.test.BasicTestCase;

/**
 * Created y the eXo platform team
 * User: Benjamin Mestrallet
 * Date: 16 juin 2004
 */
public class TestApplicationRegistryService extends BasicTestCase {

  static protected ApplicationRegisteryService service_ ;

  public TestApplicationRegistryService(String name) {
    super(name);
  }

  public void testService() throws Exception {
    PortalContainer portalContainer = PortalContainer.getInstance() ;
    service_ = (ApplicationRegisteryService)portalContainer.getComponentInstanceOfType(ApplicationRegisteryService.class) ;
    
    assertNotNull(service_) ;
    assertAppCategoryOperator() ;
    assertApplicationOperator() ;
  }
  
  void assertAppCategoryOperator() throws Exception {
    assertAppCategorySave() ;
    assertAppCategoryGet() ;    
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
    
    // Remove category
    service_.remove(category) ;
    
    numberOfCategories = service_.getApplicationCategories().size() ;
    assertEquals(0, numberOfCategories) ;
    
    ApplicationCategory returnCategory3 = service_.getApplicationCategory(categoryName);
    assertNull(returnCategory3) ;
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
      
      service_.remove(returnCategory) ;
      ApplicationCategory returnCategory2 = service_.getApplicationCategory(name) ;
      assertNull(returnCategory2);
    }
    
    int numberOfCategories = service_.getApplicationCategories().size() ;
    assertEquals(0, numberOfCategories) ;
  }
  
  void assertApplicationOperator() throws Exception {
    assertApplicationSave() ;
    assertApplicationUpdate() ;
  }
  
  void assertApplicationSave() throws Exception {
    String categoryName = "Office" ;
    String[] appNames = {"OpenOffice.org", "MS Office"} ;
    ApplicationCategory appCategory = createAppCategory(categoryName, "None") ;
    service_.save(appCategory) ;
    
    for(String appName : appNames) {
      Application app = creatApplication(appName) ;
      service_.save(appCategory, app) ;
    }
    
    for(String appName : appNames) {
      Application app = creatApplication(appName) ;
      service_.save(appCategory, app) ;
    }

    List<Application> apps = service_.getApplications(appCategory) ;
    assertEquals(2, apps.size()) ;
    assertEquals(appNames[0], apps.get(0).getApplicationName()) ;
    assertEquals(appNames[1], apps.get(1).getApplicationName()) ;

    //TODO: check  [ApplicationRegisteryService.getApplication(String id)] method
    for (String appName : appNames) {
      String appId = categoryName + "/" + appName ;
      Application app = service_.getApplication(appId) ;
      assertEquals(appName, app.getApplicationName()) ;
    }

    for (Application app : apps) {
      service_.remove(app) ;
    }
    
    List<Application> apps2 = service_.getApplications(appCategory) ;
    assertEquals(0, apps2.size()) ;
  }

  //write test code for update application and application category 
  void assertApplicationUpdate() throws Exception {
  }
   
  ApplicationCategory createAppCategory(String categoryName, String categoryDes) {
    ApplicationCategory category = new ApplicationCategory () ;
    category.setName(categoryName) ;
    category.setDisplayName(categoryName);
    category.setDescription(categoryDes) ;
    return category ;
  }
  
  Application creatApplication(String appName) {
    Application app = new Application() ;
    app.setApplicationName(appName) ;
    app.setDisplayName(appName);
    return app ;
  }
  
}