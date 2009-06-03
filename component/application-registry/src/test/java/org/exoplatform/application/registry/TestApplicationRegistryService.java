/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.application.registry;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.MembershipType;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.UserProfile;
import org.exoplatform.test.BasicTestCase;

/**
 * Created by The eXo Platform SARL
 * Author : Tung Pham
 *          thanhtungty@gmail.com
 * Nov 27, 2007  
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

  protected ApplicationRegistryService service_ ;
  protected int initialCats;
  protected int initialApps;
  
  public TestApplicationRegistryService(String name) {
    super(name);
  }

  @Override
  protected void setUp() throws Exception {
  	super.setUp();
    PortalContainer portalContainer = PortalContainer.getInstance() ;
    service_ = (ApplicationRegistryService)portalContainer.getComponentInstanceOfType(ApplicationRegistryService.class) ;
    initialCats = service_.getApplicationCategories().size();
    initialApps = service_.getAllApplications().size();
  }
  
  public void testInitializeService() throws Exception {
    assertNotNull(service_) ;
    assertEquals(3, initialCats);
    assertEquals(8, initialApps);
  }
  
  public void testApplicationCategory() throws Exception {
  	//Add new ApplicationRegistry
    String categoryName = "Category1" ;
    String categoryDes = "Description for category 1" ;
    ApplicationCategory category1 = createAppCategory(categoryName, categoryDes) ;
    service_.save(category1) ;
    
    int numberOfCats = service_.getApplicationCategories().size() ;
    assertEquals(initialCats + 1, numberOfCats) ;
    
    ApplicationCategory returnedCategory1 = service_.getApplicationCategory(categoryName);
    assertNotNull(returnedCategory1) ;
    assertEquals(category1.getName(), returnedCategory1.getName()) ;
    assertEquals(categoryName, returnedCategory1.getName()) ;
    
    //Update the ApplicationRegistry
    String newDescription = "New description for category 1";
    category1.setDescription(newDescription);
    service_.save(category1);
    
    numberOfCats = service_.getApplicationCategories().size();
    assertEquals(initialCats + 1, numberOfCats);
    returnedCategory1 = service_.getApplicationCategory(categoryName);
    assertEquals(newDescription, returnedCategory1.getDescription());
    
    //Remove the ApplicationRegistry
    service_.remove(category1);
    numberOfCats = service_.getApplicationCategories().size();
    assertEquals(initialCats, numberOfCats);
    
    returnedCategory1 = service_.getApplicationCategory(categoryName);
    assertNull(returnedCategory1);
  }

  void assertAppCategoryGetByAccessUser(ApplicationRegistryService service) throws Exception {
    PortalContainer portalContainer = PortalContainer.getInstance() ;
    OrganizationService orgService = (OrganizationService) portalContainer.getComponentInstanceOfType(OrganizationService.class) ;
    assertNotNull(orgService) ;
    prepareOrganizationData(orgService) ;

    String officeCategoryName = "Office" ;
    ApplicationCategory officeCategory = createAppCategory(officeCategoryName, "None") ;
    service.save(officeCategory) ;
    String[] officeApps = {"MSOffice", "OpenOffice"} ;
    Application msApp = createApplication(officeApps[0], "TestType", officeCategoryName) ;
    ArrayList<String> pers = new ArrayList<String>();
    pers.add("member:/users");
    msApp.setAccessPermissions(pers) ;
    service.save(officeCategory, msApp) ;
    Application openApp = createApplication(officeApps[1], "TestType", officeCategoryName) ;
    service.save(officeCategory, openApp) ;
    
    String gameCategoryName = "Game" ;
    ApplicationCategory gameCategory = createAppCategory(gameCategoryName, "None") ;
    service.save(gameCategory) ;
    String[] gameApps = {"HaftLife", "Chess"} ;
    Application haftlifeApp = createApplication(gameApps[0], "TestType", gameCategoryName) ;
    pers = new ArrayList<String>(); 
    pers.add("member:/portal/admin");
    haftlifeApp.setAccessPermissions(pers) ;
    service.save(gameCategory, haftlifeApp) ;
    Application chessApp = createApplication(gameApps[1], "TestType", gameCategoryName) ;
    chessApp.setAccessPermissions(pers) ;
    service.save(gameCategory, chessApp) ;

    List<ApplicationCategory> returnCategorys =  service.getApplicationCategories(username1) ;
    for (ApplicationCategory cate : returnCategorys) {
      System.out.println("\n\n\ncateName: " + cate.getName());
      List<Application> apps = service.getApplications(cate) ;
      for (Application app : apps) {
        System.out.println("\nappName: "  + app.getApplicationName() + "---" + app.getAccessPermissions());
      }
    }
    assertEquals(1, returnCategorys.size()) ;
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
    String[] appNames = {"OpenOffice_org", "MS_Office"} ;
    
    ApplicationCategory appCategory = createAppCategory(categoryName, "None") ;
    service.save(appCategory) ;
    
    for(String appName : appNames) {
      Application app = createApplication(appName, appType, appGroup) ;
      app.setCategoryName(categoryName) ;
      service.save(appCategory, app) ;
    }
   
    List<Application> apps = service.getApplications(appCategory) ;
    assertEquals(2, apps.size()) ;

    for (String appName : appNames) {
      String appId = categoryName + "/" + appName ;
      
      Application app = service.getApplication(appId) ;
      assertEquals(appName, app.getApplicationName()) ;  
    }
//    service.clearAllRegistries() ;
  }
  
  void assertApplicationUpdate(ApplicationRegistryService service) throws Exception {
    String categoryName = "Office" ;
    String appType = "TypeOne" ;
    String appGroup = "GroupOne" ;
    String[] appNames = {"OpenOffice_org", "MS_Office"} ;
    
    ApplicationCategory appCategory = createAppCategory(categoryName, "None") ;
    service.save(appCategory) ;
    
    // Save apps with description
    for(String appName : appNames) {
      String oldDesciption = "This is: " + appName ;
      Application app = createApplication(appName, appType, appGroup) ;
      app.setCategoryName(categoryName) ;
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
    
//    service.clearAllRegistries() ;
  }
  
  void assertApplicationRemove(ApplicationRegistryService service) throws Exception {
    String categoryName = "Office" ;
    String appType = "TestType" ;
    String appGroup = "TestGroup" ;
    String[] appNames = {"OpenOffice_org", "MS_Office"} ;
    
    ApplicationCategory appCategory = createAppCategory(categoryName, "None") ;
    service.save(appCategory) ;
    
    for(String appName : appNames) {
      Application app = createApplication(appName, appType, appGroup) ;
      app.setCategoryName(categoryName) ;
      service.save(appCategory, app) ;
    }

    List<Application> apps = service.getApplications(appCategory) ;
    assertEquals(2, apps.size()) ;

    for (Application app : apps) {
      service.remove(app) ;
    }

    List<Application> apps2 = service.getApplications(appCategory) ;
    assertEquals(0, apps2.size()) ;  
//    service.clearAllRegistries() ;
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
    groupDefault = orgService.getGroupHandler().findGroupById("/platform/users") ;         
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

  public void testClearAllRegistry() throws Exception {
  	service_.clearAllRegistries() ;
  }
}
