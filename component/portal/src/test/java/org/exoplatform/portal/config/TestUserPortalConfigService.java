/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.config;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PageNode;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.config.model.Page.PageSet;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;

/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 23, 2006
 */
public class TestUserPortalConfigService extends UserPortalServiceTestBase {
  
  private UserPortalConfigService service_; 
  
  @SuppressWarnings("unused")
  
  public TestUserPortalConfigService(String name){
    super(name);
  }
  
  public void setUp() throws Exception {
    if(service_ != null) return;
    PortalContainer manager  = PortalContainer.getInstance();      
    service_ = (UserPortalConfigService) manager.getComponentInstanceOfType(UserPortalConfigService.class) ;      
  }
  
  public void tearDown() throws Exception { 
  }

  public void testUserPortalConfigService() throws Exception { 
    assertUserPortalConfigOperator() ;
    assertPortalConfigOperator() ;
    assertNavigationOperator() ;
    assertPageOperator() ;
  }
  
  void assertUserPortalConfigOperator() throws Exception {
    //assertCreateUserPortalConfig() ;
    assertGetUserPortalConfig() ;
    assertRemoveUserPortalConfig() ;
  }
  
  void assertGetUserPortalConfig() throws Exception {
    UserPortalConfig userPortalConfig = service_.getUserPortalConfig("site" ,"exo");
    PortalConfig portalConfig = userPortalConfig.getPortalConfig();
    
    assertTrue(portalConfig != null);
    assertEquals(2, portalConfig.getAccessPermissions().length);
    assertEquals("*:/guest", portalConfig.getAccessPermissions()[0]) ;
    assertEquals(1, userPortalConfig.getNavigations().size());
    
    userPortalConfig = service_.getUserPortalConfig("site" ,"exoadmin");
    assertEquals(2, userPortalConfig.getNavigations().size());
    
  }
  
  void assertCreateUserPortalConfig() throws Exception {
    String siteName = "site" ;
    String newName = "newportal" ;
    UserPortalConfig sitePortal = service_.getUserPortalConfig(siteName, "none") ;
    UserPortalConfig newportal = service_.createUserPortalConfig(newName, siteName) ;
    
    PortalConfig sitePortalConfig = sitePortal.getPortalConfig() ;
    PortalConfig newPortalConfig = newportal.getPortalConfig() ;
    assertEquals(newPortalConfig.getAccessPermission(), sitePortalConfig.getAccessPermission()) ;
    assertTrue(!newPortalConfig.getName().equals(sitePortalConfig.getName())) ;
    
    List<PageNavigation> siteNavigations = sitePortal.getNavigations() ;
    List<PageNavigation> newNavigations = newportal.getNavigations() ;
    assertEquals(newNavigations.size(), siteNavigations.size()) ;
    
    String[] pageNames = {"content", "register"} ;
    for (String name : pageNames) {
      String sitePageId = "portal" + "::" + siteName + "::" + name ;
      String newPageId = "portal" + "::" + newName + "::" + name ;
      Page sitePage = service_.getPage(sitePageId, "none") ;
      Page newPage = service_.getPage(newPageId, "none") ;
      assertEquals(name, sitePage.getName()) ;
      assertEquals(name, newPage.getName()) ;
      assertEquals(newPage.getOwnerType(), sitePage.getOwnerType()) ;
      assertEquals(newPage.getTitle(), sitePage.getTitle()) ;
      assertEquals(newPage.getOwnerType(), sitePage.getOwnerType()) ;
      assertEquals(newPage.getAccessPermission(), sitePage.getAccessPermission()) ;
      assertTrue(!newPage.getOwnerId().equals(sitePage.getOwnerId())) ;
    }
    
  }

  //TODO: Remove action was implemented, Tung must be write test code
  void assertRemoveUserPortalConfig() throws Exception {
    
  } 
  
  void assertPortalConfigOperator() throws Exception {
    assertPortalConfigUpdate() ;
  }
  
  void assertPortalConfigUpdate() throws Exception {
    UserPortalConfig oldUserPortalConfig = service_.getUserPortalConfig("site", "exo") ;
    PortalConfig oldPortalConfig = oldUserPortalConfig.getPortalConfig() ;
    assertEquals("en", oldPortalConfig.getLocale()) ;
    assertEquals(2, oldPortalConfig.getAccessPermissions().length);
    assertEquals("*:/guest", oldPortalConfig.getAccessPermissions()[0]) ;
    assertEquals(1, oldUserPortalConfig.getNavigations().size());

    
    String newLocate = "vi" ;
    oldPortalConfig.setLocale(newLocate) ;
    service_.update(oldPortalConfig) ;
    
    UserPortalConfig newUserPortalConfig = service_.getUserPortalConfig("site", "exo") ;
    PortalConfig newPortalConfig = newUserPortalConfig.getPortalConfig() ;
    assertEquals(newLocate, newPortalConfig.getLocale()) ;
    assertEquals(2, newPortalConfig.getAccessPermissions().length);
    assertEquals("*:/guest", newPortalConfig.getAccessPermissions()[0]) ;
    assertEquals(1, newUserPortalConfig.getNavigations().size());

  }
  
  void assertNavigationOperator() throws Exception {
    assertNavigationCreate() ;
    assertNavigationUpdate() ;
    assertNavigationRemove() ;
  }
  
  void assertNavigationCreate() throws Exception {
    
  }
  
  void assertNavigationUpdate() throws Exception {
    String portalName = "site" ;
    String accessUser = "exoadmin" ;
    
    UserPortalConfig oldUserPortalConfig = service_.getUserPortalConfig(portalName, accessUser) ;
    List<PageNavigation> oldNavigations = oldUserPortalConfig.getNavigations() ;
    assertEquals(2, oldNavigations.size()) ;

    // Change description
    String newDescription = "This is new description.";
    for (PageNavigation navi : oldNavigations) {
      navi.setDescription(newDescription) ;
      service_.update(navi) ;
    }
    
    UserPortalConfig newUserPortalConfig = service_.getUserPortalConfig(portalName, accessUser) ;
    List<PageNavigation> newNavigations = newUserPortalConfig.getNavigations() ;
    assertEquals(2, newNavigations.size()) ;
    
    PageNavigation portalNavigation = newNavigations.get(0) ;
    assertEquals(newDescription, portalNavigation.getDescription()) ;
    
    PageNavigation userNavigation = newNavigations.get(1) ;
    assertEquals(newDescription, userNavigation.getDescription()) ;
    assertEquals(1, userNavigation.getNodes().size()) ;
    
    // Add new node
    PageNode pn = new PageNode() ;
    pn.setUri("myaccount") ;
    pn.setName("myaccount") ;
    pn.setPageReference("group::company::myaccount") ;
    userNavigation.addNode(pn) ;
    
    service_.update(userNavigation) ;    
    newUserPortalConfig = service_.getUserPortalConfig(portalName, accessUser) ;
    newNavigations = newUserPortalConfig.getNavigations() ;
    assertEquals(2, newNavigations.size()) ;
    userNavigation = newNavigations.get(1) ;
    assertEquals(2, userNavigation.getNodes().size()) ;    
  }
  
  void assertNavigationRemove() throws Exception {
    String portalName = "site" ;
    String accessUser = "exoadmin" ;
    
    UserPortalConfig oldUserPortalConfig = service_.getUserPortalConfig(portalName, accessUser) ;
    List<PageNavigation> oldNavigations = oldUserPortalConfig.getNavigations() ;
    assertEquals(2, oldNavigations.size()) ;

    // Remove navigation of the portal
    PageNavigation portalNavigation = oldNavigations.get(0) ;
    service_.remove(portalNavigation) ;
    
    UserPortalConfig newUserPortalConfig = service_.getUserPortalConfig(portalName, accessUser) ;
    List<PageNavigation> newNavigations = newUserPortalConfig.getNavigations() ;
    assertEquals(1, newNavigations.size()) ;
    PageNavigation userNavigation = newNavigations.get(0) ;
    assertEquals(accessUser, userNavigation.getOwnerId()) ;
    assertEquals(1, userNavigation.getAccessPermissions().length) ;
    
    // Remove remain navigation
    service_.remove(userNavigation) ;
    newUserPortalConfig = service_.getUserPortalConfig(portalName, accessUser) ;
    assertEquals(0, newUserPortalConfig.getNavigations().size()) ;
  }
  
  void assertPageOperator() throws Exception {
    assertPageCreate() ;
    assertPageGet() ;
    assertPageUpdate() ;
    assertPageRemove() ;
  }
  
  void assertPageCreate() throws Exception {
    String accessUser = "exoadmin" ; 
    String[] sitePortalPageNames = {"content", "register", "test"} ;
    
    List<Page> pages = new ArrayList<Page>() ;
    for (String pageName : sitePortalPageNames) {
      String sitePortalPageId = "portal::site::" + pageName ;
      Page page = service_.getPage(sitePortalPageId, accessUser) ;
      if (page != null) pages.add(page) ;
    }
    assertEquals(2, pages.size()) ;

    
    // Add new page to Site portal
    String pageSetFile = "testpages.xml" ;
    PageSet pageSet = loadObject(PageSet.class, pageSetFile) ;
    List<Page> addPages = pageSet.getPages() ;
    for (Page p : addPages) {
      service_.create(p) ;
    }
    int totalPage = pages.size() + addPages.size() ;
    
    pages = new ArrayList<Page>() ;
    for (String pageName : sitePortalPageNames) {
      String sitePortalPageId = "portal::site::" + pageName ;
      Page page = service_.getPage(sitePortalPageId, accessUser) ;
      if (page != null) pages.add(page) ;
    }
    
    assertEquals(totalPage, pages.size()) ;
  }
  
  void assertPageGet() throws Exception {
    String accessUser = "exoadmin" ;
    String pageId = "group::/portal/admin::web" ;
    
    Page page = service_.getPage(pageId, accessUser) ;
    assertEquals(pageId, page.getPageId()) ;
  }
  
  void assertPageUpdate() throws Exception {
    String accessUser = "exoadmin" ; 
    String[] sitePortalPageNames = {"content", "register"} ;
    
    for (String pageName : sitePortalPageNames) {
      String sitePortalPageId = "portal::site::" + pageName ;
      Page page = service_.getPage(sitePortalPageId, accessUser) ;
      String newTitle = "New title of " + pageName ;
      page.setTitle(newTitle) ;
      service_.update(page) ;
      
      Page returnPage = service_.getPage(sitePortalPageId, accessUser) ;
      assertEquals(sitePortalPageId, returnPage.getPageId()) ;
    }
  }

  void assertPageRemove() throws Exception {
    String accessUser = "exoadmin" ; 
    String[] sitePortalPageNames = {"content", "register"} ;
    
    List<Page> pages = new ArrayList<Page>() ;
    for (String pageName : sitePortalPageNames) {
      String sitePortalPageId = "portal::site::" + pageName ;
      Page page = service_.getPage(sitePortalPageId, accessUser) ;
      if (page != null) pages.add(page) ;
    }
    assertEquals(2, pages.size()) ;
    
    Page deletePage = service_.getPage("portal::site::content", accessUser) ;
    service_.remove(deletePage) ;
    pages = new ArrayList<Page>() ;
    for (String pageName : sitePortalPageNames) {
      String sitePortalPageId = "portal::site::" + pageName ;
      Page page = service_.getPage(sitePortalPageId, accessUser) ;
      if (page != null) pages.add(page) ;
    }
    assertEquals(1, pages.size()) ;
    assertEquals(sitePortalPageNames[1], pages.get(0).getName()) ;
  }
  
  private <T> T loadObject(Class<T> clazz, String file) throws Exception{
    IBindingFactory bfact = BindingDirectory.getFactory(clazz) ;
    IUnmarshallingContext uctx = bfact.createUnmarshallingContext() ;
    FileInputStream is = new FileInputStream("src/test/resources/" + file) ;
    
    return  (T) uctx.unmarshalDocument(is, null) ;
  }
  
}
