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
package org.exoplatform.portal.config;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.portal.application.PortletPreferences;
import org.exoplatform.portal.application.PortletPreferences.PortletPreferencesSet;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.config.model.Page.PageSet;
import org.exoplatform.services.portletcontainer.pci.ExoWindowID;
import org.exoplatform.test.BasicTestCase;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;

/**
 * Created by The eXo Platform SARL
 * Author : Tung Pham
 *          thanhtungty@gmail.com
 * Nov 13, 2007  
 */
public class TestDataStorage extends BasicTestCase {
  
  DataStorage storage_ ;
  
  public TestDataStorage(String name) {
    super(name) ;
  }
  
  public void setUp() throws Exception {
    super.setUp() ;
    //if(storage_ != null) return ; 
    //PortalContainer container = PortalContainer.getInstance() ;
    //storage_ = (DataStorage) container.getComponentInstanceOfType(DataStorage.class) ;
  }
  
  public void tearDown() throws Exception {
    super.tearDown() ;
  }
    
  public void testPortalConfigCreate() throws Exception {
//    String portalName = "portalone" ;
//    PortalConfig config = loadPortalConfig(portalName) ;
//    assertEquals(portalName, config.getName()) ;
//    storage_.create(config) ;
//    PortalConfig returnConfig = storage_.getPortalConfig(portalName) ;
//    assertNotNull(returnConfig) ;
//    assertEquals(portalName, returnConfig.getName()) ;
//    assertEquals(config.getCreator(), returnConfig.getCreator()) ;
//
//    storage_.remove(config) ;
  }
//  
//  public void testPortalConfigSave() throws Exception {
//    String portalName = "portalone" ;
//    PortalConfig config = loadPortalConfig(portalName) ;
//    assertEquals(portalName, config.getName()) ;
//    storage_.create(config) ;
//    
//    String newLocale = "vietnam" ;
//    config.setLocale(newLocale) ;
//    assertEquals(newLocale, config.getLocale()) ;
//    storage_.save(config) ;
//    PortalConfig returnConfig = storage_.getPortalConfig(portalName) ;
//    assertNotNull(returnConfig) ;
//    assertEquals(newLocale, returnConfig.getLocale()) ;
//    
//    storage_.remove(config) ;
//  }
//  
//  public void testPortalConfigRemove() throws Exception {
//    String portalName = "portalone" ;
//    PortalConfig config = loadPortalConfig(portalName) ;
//    assertEquals(portalName, config.getName()) ;
//    storage_.create(config) ;
//    
//    PortalConfig returnConfig = storage_.getPortalConfig(portalName) ;
//    assertNotNull(returnConfig) ;
//    
//    storage_.remove(config) ;    
//    assertNull(storage_.getPortalConfig(portalName)) ;
//    storage_.create(config) ;
//    storage_.remove(config) ;
//  }
//  
//  public void testPageConfigCreate() throws Exception {
//    createPageConfig(PortalConfig.PORTAL_TYPE, "portalone") ;
//    createPageConfig(PortalConfig.USER_TYPE, "exoadmin") ;
//    createPageConfig(PortalConfig.GROUP_TYPE, "portal/admin") ;
//  }
//  
//  private void createPageConfig(String ownerType, String ownerId) throws Exception {    
//    List<Page> pages = loadPages(ownerType, ownerId) ;
//    for(Page page : pages) {
//      storage_.create(page) ;
//    }
//    
//    List<Page> returnPages = new ArrayList<Page>() ;
//    for(Page page : pages) {
//      Page returnPage = storage_.getPage(page.getPageId()) ;
//      assertEquals(page.getPageId(), returnPage.getPageId()) ;
//      assertEquals(page.getOwnerType(), returnPage.getOwnerType()) ;
//      assertEquals(page.getOwnerId(), returnPage.getOwnerId()) ;
//      assertEquals(page.getChildren().size(), returnPage.getChildren().size()) ;
//      returnPages.add(returnPage) ;
//    }
//    assertEquals(pages.size(), returnPages.size()) ;
//    
//    for(Page page : pages) {
//      storage_.remove(page) ;
//    }
//  }
//  
//  public void testPageConfigSave() throws Exception {
//    savePageConfig(PortalConfig.PORTAL_TYPE, "portalone") ;
//    savePageConfig(PortalConfig.USER_TYPE, "exoadmin") ;
//    savePageConfig(PortalConfig.GROUP_TYPE, "portal/admin") ;
//  }
//  
//  private void savePageConfig(String ownerType, String ownerId) throws Exception {
//
//    List<Page> pages = loadPages(ownerType, ownerId) ;
//    for(Page page : pages) {
//      storage_.create(page) ;
//    }
//    
//    for(int i = 0; i < pages.size(); i++) {
//      Page page = pages.get(i) ; 
//      page.setTitle("Page title " + i) ;
//      page.setOwnerId("customers") ;
//      storage_.save(page) ;
//    }
//    List<Page> returnPages = new ArrayList<Page>() ;
//    for(int i = 0; i < pages.size(); i++) {
//      Page page = pages.get(i) ; 
//      Page returnPage = storage_.getPage(page.getPageId()) ;
//      assertEquals("Page title " + i, returnPage.getTitle()) ;
//      assertEquals(page.getPageId(), returnPage.getPageId()) ;
//      assertEquals(page.getOwnerType(), returnPage.getOwnerType()) ;
//      assertEquals(page.getOwnerId(), returnPage.getOwnerId()) ;
//      assertEquals(page.getChildren().size(), returnPage.getChildren().size()) ;
//      returnPages.add(returnPage) ;
//    }
//    assertEquals(pages.size(), returnPages.size()) ;
//    
//    for(Page page : pages) {
//      storage_.remove(page) ;
//    }
//    
//  }
//  
//  public void testPageConfigRemove() throws Exception {
//    removePageConfig(PortalConfig.PORTAL_TYPE, "portalone") ;
//    removePageConfig(PortalConfig.USER_TYPE, "exoadmin") ;
//    removePageConfig(PortalConfig.GROUP_TYPE, "portal/admin") ;
//  }
//  
//  private void removePageConfig(String ownerType, String ownerId) throws Exception {
//    List<Page> pages = loadPages(ownerType, ownerId) ;
//    for(Page page : pages) {
//      storage_.create(page) ;
//    }
//    
//    for(Page page : pages) {
//      storage_.remove(page) ;
//    }    
//
//    for(Page page : pages) {
//      storage_.create(page) ;
//    }
//    
//    for(Page page : pages) {
//      storage_.remove(page) ;
//    }    
//  }
//  
//  public void testNavigationCreate() throws Exception {
//    createNavigation(PortalConfig.PORTAL_TYPE, "portalone") ;
//    createNavigation(PortalConfig.USER_TYPE, "exoadmin") ;
//    createNavigation(PortalConfig.GROUP_TYPE, "portal/admin") ;
//  }
//  
//  private void createNavigation(String ownerType, String ownerId) throws Exception {
//    PageNavigation navi = loadNavigation(ownerType, ownerId) ;
//    assertNotNull(navi) ;
//    assertEquals(ownerType, navi.getOwnerType()) ;
//    assertEquals(ownerId, navi.getOwnerId()) ;
//    
//    storage_.create(navi) ;
//    PageNavigation returnedNavi = storage_.getPageNavigation(navi.getOwnerType(), navi.getOwnerId()) ;
//    assertNotNull(navi) ;
//    assertEquals(navi.getOwnerType(), returnedNavi.getOwnerType()) ;
//    assertEquals(navi.getOwnerId(), returnedNavi.getOwnerId()) ;
//    assertEquals(navi.getNodes().size(), returnedNavi.getNodes().size()) ;
//    
//    storage_.remove(navi) ;
//  }
//  
//  public void testNavigationSave() throws Exception {
//    saveNavigation(PortalConfig.PORTAL_TYPE, "portalone") ;
//    saveNavigation(PortalConfig.USER_TYPE, "exoadmin") ;
//    saveNavigation(PortalConfig.GROUP_TYPE, "portal/admin") ;
//  }
//  
//  private void saveNavigation(String ownerType, String ownerId) throws Exception {
//    PageNavigation navi = loadNavigation(ownerType, ownerId) ;
//    String modifier = "exoadmin" ;
//    navi.setModifier(modifier) ;
//    assertEquals(modifier, navi.getModifier()) ;
//    storage_.create(navi) ;
//
//    String newModifier = "Tung.Pham" ;
//    navi.setModifier(newModifier) ;
//    storage_.save(navi) ;    
//    PageNavigation afterSaveNavi = storage_.getPageNavigation(navi.getOwnerType(), navi.getOwnerId()) ;
//    assertEquals(newModifier, afterSaveNavi.getModifier()) ;
//    assertEquals(navi.getModifier(), afterSaveNavi.getModifier()) ;
//    assertEquals(navi.getNodes().size(), afterSaveNavi.getNodes().size()) ;
//    
//    storage_.remove(navi) ;
//  }
//  
//  public void testNavigationRemove() throws Exception {
//    removeNavigation(PortalConfig.PORTAL_TYPE, "portalone") ;
//    removeNavigation(PortalConfig.USER_TYPE, "exoadmin") ;
//    removeNavigation(PortalConfig.GROUP_TYPE, "portal/admin") ;
//  }
//  
//  private void removeNavigation(String ownerType, String ownerId) throws Exception {
//    PageNavigation navi = loadNavigation(ownerType, ownerId) ;
//    storage_.create(navi) ;
//    storage_.remove(navi) ;
//    storage_.create(navi) ;
//    storage_.remove(navi) ;
//  }
//  public void testPortletPreferencesCreate() throws Exception {
//    List<PortletPreferences> prefList = loadPortletPreferences(PortalConfig.PORTAL_TYPE, "portalone") ;
//    for(PortletPreferences ele : prefList) {
//      storage_.save(ele) ;      
//    }
//    
//    for(PortletPreferences ele : prefList) {
//      storage_.save(ele) ;      
//    }
//    
//    for(PortletPreferences ele : prefList) {
//      ExoWindowID exoWindowID = new ExoWindowID(ele.getWindowId()) ;
//      PortletPreferences pref = storage_.getPortletPreferences(exoWindowID);
//      assertNotNull(pref) ;
//      assertEquals(ele.getOwnerId(), pref.getOwnerId()) ;
//      assertEquals(ele.getOwnerType(), pref.getOwnerType()) ;
//      assertEquals(ele.getOwnerId(), pref.getOwnerId()) ;
//    }
//    
//    for(PortletPreferences ele : prefList) {
//      storage_.remove(ele) ;      
//    }
//  }
//  
//  public void testPortletPreferencesSave() throws Exception {
//    List<PortletPreferences> prefList = loadPortletPreferences(PortalConfig.PORTAL_TYPE, "portalone") ;
//    for(PortletPreferences ele : prefList) {
//      storage_.save(ele) ;      
//    }
//    
//    String newValidator = "NewValidator" ;
//    for(int i = 0; i < prefList.size(); i++) {
//      PortletPreferences ele = prefList.get(i) ;
//      ele.setPreferencesValidator(newValidator+i) ;
//      storage_.save(ele) ;
//    }
//    
//    for(int i = 0; i < prefList.size(); i++) {
//      PortletPreferences ele = prefList.get(i) ;
//      ExoWindowID exoWindowID = new ExoWindowID(ele.getWindowId()) ;
//      PortletPreferences afterSavePref = storage_.getPortletPreferences(exoWindowID) ;
//      assertEquals(newValidator + i, afterSavePref.getPreferencesValidator()) ;
//    }
//    
//    for(PortletPreferences ele : prefList) {
//      storage_.remove(ele) ;      
//    }
//  }
//  
//  public void testPortletPreferencesRemove() throws Exception {
//    List<PortletPreferences> prefList = loadPortletPreferences(PortalConfig.PORTAL_TYPE, "portalone") ;
//    for(PortletPreferences ele : prefList) {
//      storage_.save(ele) ;      
//    }
//    
//    for(PortletPreferences ele : prefList) {
//      ExoWindowID exoWindowID = new ExoWindowID(ele.getWindowId()) ;
//      PortletPreferences pref = storage_.getPortletPreferences(exoWindowID);
//      assertNotNull(pref) ;
//    }
//    
//    for(PortletPreferences ele : prefList) {
//      storage_.remove(ele) ;      
//    }
//
//    for(PortletPreferences ele : prefList) {
//      ExoWindowID exoWindowID = new ExoWindowID(ele.getWindowId()) ;
//       assertNull(storage_.getPortletPreferences(exoWindowID)) ;
//    }
//  }
//  
//  @SuppressWarnings("unchecked")
//  public void testFind() throws Exception {
//    List<Page> pages = loadPages(PortalConfig.USER_TYPE, "exoadmin") ;
//    for(Page page : pages) {
//      storage_.create(page) ;
//    }
//    
//    Query<Page> query = new Query<Page>(null, null, Page.class) ;
//    List<Page> findedPages = storage_.find(query).getAll() ;
//    assertEquals(pages.size(), findedPages.size()) ;
//    
//    for(int i = 0; i < findedPages.size(); i++) {
//      Page existingPage = pages.get(i) ;
//      Page findedPage = findedPages.get(i) ;
//      assertEquals(existingPage.getName(), findedPage.getName()) ;
//      assertEquals(existingPage.getPageId(), findedPage.getPageId()) ;
//      assertEquals(existingPage.getOwnerType(), findedPage.getOwnerType()) ;
//      assertEquals(existingPage.getOwnerId(), findedPage.getOwnerId()) ;
//      assertEquals(existingPage.getChildren().size(), findedPage.getChildren().size()) ;
//      storage_.remove(findedPage) ;
//    }    
//  }
//  
//  //------------------------------------------------------------------------------------------//
//  
//  private PortalConfig loadPortalConfig(String portalName) throws Exception {
//    String configFile = "PortalApp/" + portalName + "/portal.xml" ;
//    
//    PortalConfig config = loadObject(PortalConfig.class, configFile) ;
//    
//    return config ;
//  }
//
//  private PageNavigation loadNavigation(String ownerType, String ownerId) throws Exception {
//    String navigationFile = "" ;
//    if(PortalConfig.PORTAL_TYPE.equals(ownerType)) {
//      navigationFile = "PortalApp/" + ownerId + "/navigation.xml" ; 
//    } else if(PortalConfig.USER_TYPE.equals(ownerType)) {
//      navigationFile = "user/" + ownerId + "/navigation.xml" ;
//    } else if(PortalConfig.GROUP_TYPE.equals(ownerType)) {
//      navigationFile = "group/" + ownerId + "/navigation.xml" ;
//    }
//    PageNavigation navigation = loadObject(PageNavigation.class, navigationFile) ;
//    
//    return navigation ;
//  }
//  
//  private List<Page> loadPages(String ownerType, String ownerId) throws Exception {
//    String pageSetFile = "" ;
//    if(PortalConfig.PORTAL_TYPE.equals(ownerType)) {
//      pageSetFile = "PortalApp/" + ownerId + "/pages.xml" ; 
//    } else if(PortalConfig.USER_TYPE.equals(ownerType)) {
//      pageSetFile = "user/" + ownerId + "/pages.xml" ;
//    } else if(PortalConfig.GROUP_TYPE.equals(ownerType)) {
//      pageSetFile = "group/" + ownerId + "/pages.xml" ;
//    }     
//    PageSet pageSet = loadObject(PageSet.class, pageSetFile) ;
// 
//    return pageSet.getPages() ;
//  }
//  
//  private List<PortletPreferences> loadPortletPreferences(String ownerType, String ownerId) throws Exception {
//    String filePath = "" ;
//    if(PortalConfig.PORTAL_TYPE.equals(ownerType)) {
//      filePath = "PortalApp/" + ownerId + "/portlet-preferences.xml" ;
//    } else if(PortalConfig.GROUP_TYPE.equals(ownerType)) {
//      filePath = "group/" + ownerId + "/portlet-preferences.xml" ;
//    }
//    PortletPreferencesSet set = loadObject(PortletPreferencesSet.class, filePath) ;
//    return set.getPortlets() ;
//  }
//
//  @SuppressWarnings("unchecked")
//  private <T> T loadObject(Class<T> clazz, String file) throws Exception{
//    IBindingFactory bfact = BindingDirectory.getFactory(clazz) ;
//    IUnmarshallingContext uctx = bfact.createUnmarshallingContext() ;
//    FileInputStream is = new FileInputStream("src/test/resources/" + file) ;
//    
//    return  (T) uctx.unmarshalDocument(is, null) ;
//  }
  
}
