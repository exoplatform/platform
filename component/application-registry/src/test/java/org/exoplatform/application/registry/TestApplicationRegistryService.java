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
import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.MembershipType;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.UserProfile;
import org.exoplatform.test.BasicTestCase;
import org.exoplatform.web.WebAppController;

/**
 * Created by the eXo platform team
 * User: Benjamin Mestrallet
 * Date: 16 juin 2004
 */
public class TestApplicationRegistryService extends BasicTestCase {
  protected static String demo  = "demo" ;
  protected static String Group1 = "Group1" ;
  protected static String Group2 = "Group2" ;
  protected static String username1 = "userName_1";
  protected static String username2 = "userName_2" ;
  protected static String memtype1 = "MembershipType_1" ;
  protected static String memtype2 = "MembershipType_2" ;   
  
  protected Group group1, group2, groupDefault;  
  protected MembershipType mType1,mType2, mTypeDefault ;
  protected User user1, user2 ,userDefault;     

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
    assertAppCategoryGetByAccessUser(service) ;
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
  
  void assertAppCategoryGetByAccessUser(ApplicationRegistryService service) throws Exception {
    PortalContainer portalContainer = PortalContainer.getInstance() ;
    OrganizationService orgService = (OrganizationService) portalContainer.getComponentInstanceOfType(OrganizationService.class) ;
    prepareOrganizationData(orgService) ;

    String officeCategoryName = "Office" ;
    ApplicationCategory officeCategory = createAppCategory(officeCategoryName, "None") ;
    service.save(officeCategory) ;
    String[] officeApps = {"MS Office", "OpenOffice"} ;
    Application msApp = createApplication(officeApps[0], "TestType", officeCategoryName) ;
    msApp.setAccessPermission("member:/users") ;
    service.save(officeCategory, msApp) ;
    Application openApp = createApplication(officeApps[1], "TestType", officeCategoryName) ;
    service.save(officeCategory, openApp) ;
    
    String gameCategoryName = "Game" ;
    ApplicationCategory gameCategory = createAppCategory(gameCategoryName, "None") ;
    service.save(gameCategory) ;
    String[] gameApps = {"HaftLife", "Chess"} ;
    Application haftlifeApp = createApplication(gameApps[0], "TestType", gameCategoryName) ;
    haftlifeApp.setAccessPermission("member:/portal/admin") ;
    service.save(gameCategory, haftlifeApp) ;
    Application chessApp = createApplication(gameApps[1], "TestType", gameCategoryName) ;
    chessApp.setAccessPermission("member:/portal/admin") ;
    service.save(gameCategory, chessApp) ;

    List<ApplicationCategory> returnCategorys =  service.getApplicationCategories(username1) ;
    for (ApplicationCategory cate : returnCategorys) {
      System.out.println("\n\n\ncateName: " + cate.getName());
      List<Application> apps = service.getApplications(cate) ;
      for (Application app : apps) {
        System.out.println("\nappName: "  + app.getApplicationName() + "---" + app.getAccessPermission());
      }
    }
    assertEquals(1, returnCategorys.size()) ;

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
      Application app = createApplication(appName, appType, appGroup) ;
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
      Application app = createApplication(appName, appType, appGroup) ;
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
      Application app = createApplication(appName, appType, appGroup) ;
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

  private ApplicationCategory createAppCategory(String categoryName, String categoryDes) {
    ApplicationCategory category = new ApplicationCategory () ;
    category.setName(categoryName) ;
    category.setDisplayName(categoryName);
    category.setDescription(categoryDes) ;
    return category ;
  }
  
  private Application createApplication(String appName, String appType, String appGroup) {
    Application app = new Application() ;
    app.setApplicationName(appName) ;
    app.setDisplayName(appName);
    app.setApplicationType(appType) ;
    app.setApplicationGroup(appGroup) ;
    return app ;
  }
  
  private void prepareOrganizationData(OrganizationService orgService) throws Exception{
    groupDefault = orgService.getGroupHandler().findGroupById("/users") ;         
    if(group1 ==null) { group1 = createGroup(orgService, Group1); }    
    if(group2 ==null) { group2 = createGroup(orgService, Group2) ; }
    
    mTypeDefault = orgService.getMembershipTypeHandler().findMembershipType("member") ;
    if(mType1 ==null) { mType1 = createMembershipType(orgService,memtype1); }    
    if(mType2 ==null) {mType2 = createMembershipType(orgService, memtype2); 
    }
    
    if(user1 ==null) {
      user1 =  createUser(orgService, username1);
      createDataUser(orgService, user1);            
    }    
    if(user2 ==null) {
      user2= createUser(orgService, username2) ;    
      createDataUser(orgService, user2) ;            
    }
    
    userDefault = orgService.getUserHandler().findUserByName(demo) ;
  }            
  
  private Group createGroup(OrganizationService orgService, String groupName) throws Exception {   
    Group savedGroup = orgService.getGroupHandler().findGroupById("/"+groupName);
    if(savedGroup != null) return savedGroup;
    Group groupParent = orgService.getGroupHandler().createGroupInstance() ;
    groupParent.setGroupName( groupName);
    groupParent.setDescription("This is description");    
    orgService.getGroupHandler().addChild(null, groupParent, true);   
    return groupParent;
  }
  
  private MembershipType createMembershipType(OrganizationService orgService, String name) throws Exception {
    MembershipType savedMt = orgService.getMembershipTypeHandler().findMembershipType(name);
    if(savedMt != null) return savedMt;
    MembershipType mt = orgService.getMembershipTypeHandler().createMembershipTypeInstance();
    mt.setName( name) ;
    mt.setDescription("This is a test") ;
    mt.setOwner("exo") ;     
    orgService.getMembershipTypeHandler().createMembershipType(mt, true);
    return mt;
  }
  
  @SuppressWarnings("deprecation")
  private User createUser(OrganizationService orgService, String userName) throws Exception {   
    User savedUser = orgService.getUserHandler().findUserByName(userName);
    if(savedUser != null) return savedUser;
    User user = orgService.getUserHandler().createUserInstance(userName) ;
    user.setPassword("default") ;
    user.setFirstName("default") ;
    user.setLastName("default") ;
    user.setEmail("exo@exoportal.org") ;
    orgService.getUserHandler().createUser(user, true);
    return user ;
  }
  
  private User createDataUser(OrganizationService orgService, User u) throws Exception {
    UserProfile up = orgService.getUserProfileHandler().findUserProfileByName(u.getUserName());
    up.getUserInfoMap().put("user.gender", "male");
    orgService.getUserProfileHandler().saveUserProfile(up, true);    
    return u;
  }
  
}