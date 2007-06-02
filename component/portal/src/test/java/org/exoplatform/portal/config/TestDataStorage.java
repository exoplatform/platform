/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.config;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.config.model.Widgets;
import org.exoplatform.portal.config.model.Page.PageSet;
import org.exoplatform.test.BasicTestCase;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;

/**
 * Created by The eXo Platform SARL
 * Author : Pham Thanh Tung
 *          thanhtungty@gmail.com
 * May 2, 2007  
 */
public class TestDataStorage extends BasicTestCase { 
  DataStorage storage_ ;
  public TestDataStorage(String name) {
    super(name) ;
  }
  
  public void testAll()  throws Exception {
    PortalContainer portalContainer = PortalContainer.getInstance() ;
    storage_ = (DataStorage)portalContainer.getComponentInstanceOfType(DataStorage.class) ;
    
    assertPortalConfigOperator() ;
    assertNavigationOperator() ;
    assertPageOperator() ;
    assertWidgetsOperator() ;
  }
  
  void assertPortalConfigOperator()  throws Exception {
    String portalName = "portalone" ;
    
    assertPortalConfigCreate(portalName) ;
    assertPortalConfigSave(portalName) ;
    assertPortalConfigRemove(portalName) ;
  }
  
  void assertPortalConfigCreate(String portalName) throws Exception {
    PortalConfig config = createPortalConfig(portalName) ;
    assertEquals(portalName, config.getName()) ;
    storage_.create(config) ;
    
    PortalConfig returnConfig = storage_.getPortalConfig(portalName) ;
    assertEquals(config.getName(), returnConfig.getName()) ;
    storage_.remove(config) ;
  }
  
  void assertPortalConfigSave(String portalName) throws Exception {
    PortalConfig config = createPortalConfig(portalName) ;
    assertEquals(portalName, config.getName()) ;
    storage_.create(config) ;
    PortalConfig returnConfig = storage_.getPortalConfig(portalName) ;
    assertEquals(portalName, returnConfig.getName()) ;
    
    String newLocate = "new locate" ;
    config.setLocale(newLocate) ;
    storage_.save(config) ;
    PortalConfig returnConfig2 = storage_.getPortalConfig(portalName) ;
    assertEquals(newLocate, returnConfig2.getLocale()) ;
    
    storage_.remove(config) ;
  }
  
  void assertPortalConfigRemove(String portalName) throws Exception {
    PortalConfig config = createPortalConfig(portalName) ;
    assertEquals(portalName, config.getName()) ;
    storage_.create(config) ;
    PortalConfig returnConfig = storage_.getPortalConfig(portalName) ;
    assertNotNull(returnConfig) ;
    
    storage_.remove(config) ;
    PortalConfig returnConfig2 = storage_.getPortalConfig(portalName) ;
    assertNull(returnConfig2) ;
  }
  
  void assertNavigationOperator() throws Exception {
    String ownerId = "portalone" ;
    
    assertNavigationCreate(ownerId) ;
    assertNavigationSave(ownerId) ;
    assertNavigationRemove() ;
  }
  
  void assertNavigationCreate(String ownerId) throws Exception {
    PageNavigation navigation = createNavigation(ownerId) ;
    assertEquals(ownerId, navigation.getOwnerId()) ;
    
    storage_.create(navigation) ;
    PageNavigation returnNavigation = storage_.getPageNavigation(navigation.getId()) ;
    assertEquals(ownerId, returnNavigation.getOwnerId()) ;
    
    storage_.remove(navigation) ;
  }
  
  void assertNavigationSave(String ownerId) throws Exception {
    PageNavigation navigation = createNavigation(ownerId) ;
    assertEquals(ownerId, navigation.getOwnerId()) ;
    String navigationId = navigation.getId() ;
    
    String oldDescription = "Old Description." ;
    navigation.setDescription(oldDescription) ;
    storage_.create(navigation) ;
    PageNavigation returnNavigation = storage_.getPageNavigation(navigationId) ;
    assertEquals(oldDescription, returnNavigation.getDescription()) ;
    
    String newDescription = "New Description." ;
    navigation.setDescription(newDescription) ;
    storage_.save(navigation) ;
    PageNavigation returnNavigation2 = storage_.getPageNavigation(navigationId) ;
    assertEquals(newDescription, returnNavigation2.getDescription()) ;
    
    storage_.remove(navigation) ;
  }
  
  void assertNavigationRemove() throws Exception {
    String ownerId1 = "portalone" ;
    PageNavigation navigation1 = createNavigation(ownerId1) ;
    assertEquals(ownerId1, navigation1.getOwnerId()) ;
    String navigationId1 = navigation1.getId() ;
    
    storage_.create(navigation1) ;
    PageNavigation returnNavigation1 = storage_.getPageNavigation(navigationId1) ;
    assertNotNull(returnNavigation1) ;
    
    String ownerId2 = "portaltwo" ;
    PageNavigation navigation2 = createNavigation(ownerId2) ;
    assertEquals(ownerId2, navigation2.getOwnerId()) ;
    String navigationId2 = navigation2.getId() ;
    
    storage_.create(navigation2) ;
    PageNavigation returnNavigation2 = storage_.getPageNavigation(navigationId2) ;
    assertNotNull(returnNavigation2) ;
    
    storage_.remove(navigation1) ;
    
    PageNavigation _returnNavigation1 = storage_.getPageNavigation(navigationId1) ;
    assertNull(_returnNavigation1) ;
    
    PageNavigation _returnNavigation2 = storage_.getPageNavigation(navigationId2) ;
    assertNotNull(_returnNavigation2) ;
    
    storage_.remove(navigation2) ;

  }
  
  void assertPageOperator() throws Exception {
    String ownerId = "portalone" ;
    
    assertPageCreate(ownerId) ;
    assertPageSave(ownerId) ;
    assertPageRemove(ownerId) ;
  }

  void assertPageCreate(String ownerId) throws Exception {
   List<Page> pages = createPages(ownerId) ;
   assertEquals(2, pages.size()) ;
   
   for (Page p : pages) {
     storage_.create(p) ;
   }
   
   List<Page> returnPages = new ArrayList<Page>() ;
   for (Page p : pages) {
     String pageId = p.getPageId() ;

     Page aPage = storage_.getPage(pageId) ;
     assertEquals(pageId, aPage.getPageId()) ;
     returnPages.add(aPage) ;
   }
   assertEquals(2, returnPages.size()) ;
   
   for (Page p : pages) {
     storage_.remove(p) ;
   }
   
  }
  
  void assertPageSave(String ownerId) throws Exception {
    List<Page> pages = createPages(ownerId) ;
    assertEquals(2, pages.size()) ;
    
    String oldPermisstion = "*:/guest" ;
    for (Page p : pages) {
      p.setAccessPermissions(new String[]{oldPermisstion}) ;
      storage_.create(p) ;
    }
    
    for (Page p : pages) {
      String pageId = p.getPageId() ;
      
      Page aPage = storage_.getPage(pageId) ;
      assertEquals(oldPermisstion, aPage.getAccessPermissions()[0]) ;
    }
    
    String newPermission = "*:/admin" ;
    for (Page p : pages) {
      p.setAccessPermissions(new String[]{newPermission}) ;
      storage_.save(p) ;
    }

    List<Page> returnPages = new ArrayList<Page>() ;
    for (Page p : pages) {
      String pageId = p.getPageId() ;

      Page aPage = storage_.getPage(pageId) ;
      assertEquals(newPermission, aPage.getAccessPermissions()[0]) ;
      returnPages.add(aPage) ;
    }
    assertEquals(2, returnPages.size()) ;
    
    for (Page p : pages) {
      storage_.remove(p) ;
    }
    
  }
  
  void assertPageRemove(String ownerId) throws Exception {
    List<Page> pages = createPages(ownerId) ;
    assertEquals(2, pages.size()) ;
    
    // Create 2 pages
    for (Page p : pages) {
      storage_.create(p) ;
    }
    
    // Before remove 2 pages
    List<Page> returnPages = new ArrayList<Page>() ;
    for (Page p : pages) {
      String pageId = p.getPageId() ;

      Page aPage = storage_.getPage(pageId) ;
      returnPages.add(aPage) ;
    }
    assertEquals(2, returnPages.size()) ;
    
    // Remove 2 pages
    for (Page p : pages) {
      storage_.remove(p) ;
    }
    
    // After remove 2 pages
    List<Page> returnPages2 = new ArrayList<Page>() ;
    for (Page p : pages) {
      String pageId = p.getPageId() ;

      Page aPage = storage_.getPage(pageId) ;
      if (aPage != null) returnPages2.add(aPage) ;
    }
    assertEquals(0, returnPages2.size()) ;
  }

  void assertWidgetsOperator() throws Exception {
   String testPortal = "portalone" ;
    assertWidgetsCreate(testPortal) ;
    assertWidgetsSave(testPortal) ;
    assertWidgetsRemove(testPortal) ;
  }
  
  void assertWidgetsCreate(String ownerId) throws Exception {
   Widgets widgets = createWidgets(ownerId) ;
   assertNotNull(widgets) ;
   assertEquals(ownerId, widgets.getOwnerId()) ;
   
   Widgets returnWidgets = storage_.getWidgets(widgets.getId()) ;
   assertNull(returnWidgets) ;
   
   storage_.create(widgets) ;
   returnWidgets = storage_.getWidgets(widgets.getId()) ;
   assertNotNull(returnWidgets) ;
   assertEquals(widgets.getOwnerId(), returnWidgets.getOwnerId()) ;
   
   storage_.remove(widgets) ;
  }

  void assertWidgetsSave(String ownerId) throws Exception {
    Widgets widgets = createWidgets(ownerId) ;
    assertEquals("*:/guest,*:/user", widgets.getAccessPermissions()) ;
    storage_.create(widgets) ;
    
    String newAccessPermission = "/tester" ;
    widgets.setAccessPermissions(new String[]{newAccessPermission}) ;
    storage_.save(widgets) ;
    Widgets returnWidgets = storage_.getWidgets(widgets.getId()) ;
    assertEquals(newAccessPermission, returnWidgets.getAccessPermissions()) ;
    
    storage_.remove(widgets) ;
  }
  void assertWidgetsRemove(String ownerId) throws Exception {
    Widgets widgets = createWidgets(ownerId) ;
    storage_.create(widgets) ;
    Widgets returnWidgets = storage_.getWidgets(widgets.getId()) ;
    assertNotNull(returnWidgets) ;
    
    storage_.remove(widgets) ;
    returnWidgets = storage_.getWidgets(widgets.getId()) ;
    assertNull(returnWidgets) ;
  }

//----------------------------------------------------------------------------------------------------------
  private PortalConfig createPortalConfig(String portalName) throws Exception {
    String configFile = portalName + "/portal.xml" ;
    
    PortalConfig config = loadObject(PortalConfig.class, configFile) ;
    
    return config ;
  }
  
  private PageNavigation createNavigation(String ownerId) throws Exception {
    String navigationFile = ownerId + "/navigation.xml" ;

    PageNavigation navigation = loadObject(PageNavigation.class, navigationFile) ;
    
    return navigation ;
  }
  
  private List<Page> createPages(String ownerId) throws Exception {
    String pageSetFile = ownerId + "/pages.xml" ;
    PageSet pageSet = loadObject(PageSet.class, pageSetFile) ;
 
    return pageSet.getPages() ;
  }
  
  private Widgets createWidgets(String ownerId) throws Exception {
    String widgetFile = ownerId + "/widgets.xml" ;
    Widgets widgets = loadObject(Widgets.class, widgetFile) ;
    
    return widgets ;
  }
  
  private <T> T loadObject(Class<T> clazz, String file) throws Exception{
    IBindingFactory bfact = BindingDirectory.getFactory(clazz) ;
    IUnmarshallingContext uctx = bfact.createUnmarshallingContext() ;
    FileInputStream is = new FileInputStream("src/test/resources/PortalApp/" + file) ;
    
    return  (T) uctx.unmarshalDocument(is, null) ;
  }

}
