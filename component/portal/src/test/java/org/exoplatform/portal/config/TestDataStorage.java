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
 * Created by The eXo Platform SARL Author : Tung Pham thanhtungty@gmail.com Nov
 * 13, 2007
 */
public class TestDataStorage extends BasicTestCase {

	private final String testPortal = "testPortal";
	private final String testPage = "portal::classic::testPage";
  DataStorage storage_;

  public TestDataStorage(String name) {
    super(name);
  }

  public void setUp() throws Exception {
    super.setUp();
    if (storage_ != null) return;
    PortalContainer container = PortalContainer.getInstance();
    storage_ = (DataStorage) container.getComponentInstanceOfType(DataStorage.class);
  }

  public void testPortalConfigCreate() throws Exception {
  	PortalConfig portalConfig = new PortalConfig();
  	portalConfig.setName(testPortal);
  	portalConfig.setLocale("en");
  	portalConfig.setAccessPermissions(new String[]{UserACL.EVERYONE});
  	
    PortalConfig returnConfig = storage_.getPortalConfig(portalConfig.getName());
    if (returnConfig != null)
      storage_.remove(returnConfig);
    
    storage_.create(portalConfig);
    returnConfig = storage_.getPortalConfig(portalConfig.getName());
    assertNotNull(returnConfig);
    assertEquals(portalConfig.getName(), returnConfig.getName());
    assertEquals(portalConfig.getCreator(), returnConfig.getCreator());
  }

  public void testPortalConfigSave() throws Exception {
    PortalConfig portalConfig = storage_.getPortalConfig(testPortal);
    
    assertNotNull(portalConfig);
    
    String newLocale = "vietnam";
    portalConfig.setLocale(newLocale);
    storage_.save(portalConfig);
    portalConfig = storage_.getPortalConfig(testPortal);
    assertNotNull(portalConfig);
    assertEquals(newLocale, portalConfig.getLocale());
  }

  public void testPortalConfigRemove() throws Exception {
    PortalConfig portalConfig = storage_.getPortalConfig(testPortal);
    assertNotNull(portalConfig);
    
    storage_.remove(portalConfig);
    assertNull(storage_.getPortalConfig(testPortal));
  }

  public void testPageConfigCreate() throws Exception {
    createPageConfig(PortalConfig.PORTAL_TYPE, "portalone");
    createPageConfig(PortalConfig.USER_TYPE, "exoadmin");
    createPageConfig(PortalConfig.GROUP_TYPE, "portal/admin");
  }

  private void createPageConfig(String ownerType, String ownerId) throws Exception {
  	Page page = new Page();
  	page.setName("testPage");
  	page.setOwnerId("classic");
  	page.setOwnerType("portal");

  	try {
  		storage_.create(page);
  	} catch (Exception e) {
  		page = storage_.getPage(page.getPageId());
  	}

  	Page returnPage = storage_.getPage(page.getPageId());
  	assertNotNull(returnPage);
  	assertEquals(page.getPageId(), returnPage.getPageId());
  	assertEquals(page.getOwnerType(), returnPage.getOwnerType());
  	assertEquals(page.getOwnerId(), returnPage.getOwnerId());
  	assertEquals(page.getChildren().size(), returnPage.getChildren().size());
  }

  public void testPageConfigSave() throws Exception {
  	Page page = storage_.getPage(testPage);
  	assertNotNull(page);
  	
  	page.setTitle("New Page Title");
  	page.setOwnerId("customers");
  	storage_.save(page);

  	Page returnPage = storage_.getPage(page.getPageId());
  	assertEquals("New Page Title", returnPage.getTitle());
  	assertEquals(page.getPageId(), returnPage.getPageId());
  	assertEquals(page.getOwnerType(), returnPage.getOwnerType());
  	assertEquals(page.getOwnerId(), returnPage.getOwnerId());
  	assertEquals(page.getChildren().size(), returnPage.getChildren().size());
  }

  public void testPageConfigRemove() throws Exception {
  	Page page = storage_.getPage(testPage);
  	assertNotNull(page);
  	
  	storage_.remove(page);
  	
  	page = storage_.getPage(testPage);
  	assertNull(page);
  }

  public void testNavigationCreate() throws Exception {
  	PageNavigation pageNavi = new PageNavigation();
  	pageNavi.setOwnerId(testPortal);
  	pageNavi.setOwnerType("portal");
  	storage_.create(pageNavi);
  }

  public void testNavigationSave() throws Exception {
    saveNavigation(PortalConfig.PORTAL_TYPE, "portalone");
    saveNavigation(PortalConfig.USER_TYPE, "exoadmin");
    saveNavigation(PortalConfig.GROUP_TYPE, "portal/admin");
  }

  private void saveNavigation(String ownerType, String ownerId) throws Exception {
    PageNavigation navi = loadNavigation(ownerType, ownerId);
    String modifier = "exoadmin";
    navi.setModifier(modifier);
    assertEquals(modifier, navi.getModifier());
    storage_.create(navi);

    String newModifier = "Tung.Pham";
    navi.setModifier(newModifier);
    storage_.save(navi);
    PageNavigation afterSaveNavi = storage_.getPageNavigation(navi.getOwnerType(),
                                                              navi.getOwnerId());
    assertEquals(newModifier, afterSaveNavi.getModifier());
    assertEquals(navi.getModifier(), afterSaveNavi.getModifier());
    assertEquals(navi.getNodes().size(), afterSaveNavi.getNodes().size());

    storage_.remove(navi);
  }

  public void testNavigationRemove() throws Exception {
    removeNavigation(PortalConfig.PORTAL_TYPE, "portalone");
    removeNavigation(PortalConfig.USER_TYPE, "exoadmin");
    removeNavigation(PortalConfig.GROUP_TYPE, "portal/admin");
  }

  private void removeNavigation(String ownerType, String ownerId) throws Exception {
    PageNavigation navi = loadNavigation(ownerType, ownerId);
    storage_.create(navi);
    storage_.remove(navi);
    storage_.create(navi);
    storage_.remove(navi);
  }

  public void testPortletPreferencesCreate() throws Exception {
    List<PortletPreferences> prefList = loadPortletPreferences(PortalConfig.PORTAL_TYPE,
                                                               "portalone");
    for (PortletPreferences ele : prefList) {
      storage_.save(ele);
    }

    for (PortletPreferences ele : prefList) {
      storage_.save(ele);
    }

    for (PortletPreferences ele : prefList) {
      ExoWindowID exoWindowID = new ExoWindowID(ele.getWindowId());
      PortletPreferences pref = storage_.getPortletPreferences(exoWindowID);
      assertNotNull(pref);
      assertEquals(ele.getOwnerId(), pref.getOwnerId());
      assertEquals(ele.getOwnerType(), pref.getOwnerType());
      assertEquals(ele.getOwnerId(), pref.getOwnerId());
    }

    for (PortletPreferences ele : prefList) {
      storage_.remove(ele);
    }
  }

  public void testPortletPreferencesSave() throws Exception {
    List<PortletPreferences> prefList = loadPortletPreferences(PortalConfig.PORTAL_TYPE,
                                                               "portalone");
    for (PortletPreferences ele : prefList) {
      storage_.save(ele);
    }

    String newValidator = "NewValidator";
    for (int i = 0; i < prefList.size(); i++) {
      PortletPreferences ele = prefList.get(i);
      ele.setPreferencesValidator(newValidator + i);
      storage_.save(ele);
    }

    for (int i = 0; i < prefList.size(); i++) {
      PortletPreferences ele = prefList.get(i);
      ExoWindowID exoWindowID = new ExoWindowID(ele.getWindowId());
      PortletPreferences afterSavePref = storage_.getPortletPreferences(exoWindowID);
      assertEquals(newValidator + i, afterSavePref.getPreferencesValidator());
    }

    for (PortletPreferences ele : prefList) {
      storage_.remove(ele);
    }
  }

  public void testPortletPreferencesRemove() throws Exception {
    List<PortletPreferences> prefList = loadPortletPreferences(PortalConfig.PORTAL_TYPE,
                                                               "portalone");
    for (PortletPreferences ele : prefList) {
      storage_.save(ele);
    }

    for (PortletPreferences ele : prefList) {
      ExoWindowID exoWindowID = new ExoWindowID(ele.getWindowId());
      PortletPreferences pref = storage_.getPortletPreferences(exoWindowID);
      assertNotNull(pref);
    }

    for (PortletPreferences ele : prefList) {
      storage_.remove(ele);
    }

    for (PortletPreferences ele : prefList) {
      ExoWindowID exoWindowID = new ExoWindowID(ele.getWindowId());
      assertNull(storage_.getPortletPreferences(exoWindowID));
    }
  }

  @SuppressWarnings("unchecked")
  public void testFind() throws Exception {
    Query<Page> query = new Query<Page>(null, null, Page.class);
    List<Page> findedPages = storage_.find(query).getAll();
    for (Page findedPage : findedPages) {
      storage_.remove(findedPage);
    }
    
    List<Page> pages = loadPages(PortalConfig.USER_TYPE, "exoadmin");
    for (Page page : pages) {
      storage_.create(page);
    }
    
    findedPages = storage_.find(query).getAll();
    assertEquals(pages.size(), findedPages.size());

    for (int i = 0; i < findedPages.size(); i++) {
      Page existingPage = pages.get(i);
      Page findedPage = findedPages.get(i);
      assertEquals(existingPage.getName(), findedPage.getName());
      assertEquals(existingPage.getPageId(), findedPage.getPageId());
      assertEquals(existingPage.getOwnerType(), findedPage.getOwnerType());
      assertEquals(existingPage.getOwnerId(), findedPage.getOwnerId());
      assertEquals(existingPage.getChildren().size(), findedPage.getChildren().size());
      storage_.remove(findedPage);
    }
  }

  //----------------------------------------------------------------------------
  // --------------//

  private PageNavigation loadNavigation(String ownerType, String ownerId) throws Exception {
    String navigationFile = "";
    if (PortalConfig.PORTAL_TYPE.equals(ownerType)) {
      navigationFile = "PortalApp/" + ownerId + "/navigation.xml";
    } else if (PortalConfig.USER_TYPE.equals(ownerType)) {
      navigationFile = "user/" + ownerId + "/navigation.xml";
    } else if (PortalConfig.GROUP_TYPE.equals(ownerType)) {
      navigationFile = "group/" + ownerId + "/navigation.xml";
    }
    PageNavigation navigation = loadObject(PageNavigation.class, navigationFile);

    return navigation;
  }

  private List<Page> loadPages(String ownerType, String ownerId) throws Exception {
    String pageSetFile = "";
    if (PortalConfig.PORTAL_TYPE.equals(ownerType)) {
      pageSetFile = "portal/portal/" + ownerId + "/pages.xml";
    } else if (PortalConfig.USER_TYPE.equals(ownerType)) {
      pageSetFile = "portal/user/" + ownerId + "/pages.xml";
    } else if (PortalConfig.GROUP_TYPE.equals(ownerType)) {
      pageSetFile = "portal/group/" + ownerId + "/pages.xml";
    }
    PageSet pageSet = loadObject(PageSet.class, pageSetFile);

    return pageSet.getPages();
  }

  private List<PortletPreferences> loadPortletPreferences(String ownerType, String ownerId) throws Exception {
    String filePath = "";
    if (PortalConfig.PORTAL_TYPE.equals(ownerType)) {
      filePath = "PortalApp/" + ownerId + "/portlet-preferences.xml";
    } else if (PortalConfig.GROUP_TYPE.equals(ownerType)) {
      filePath = "group/" + ownerId + "/portlet-preferences.xml";
    }
    PortletPreferencesSet set = loadObject(PortletPreferencesSet.class, filePath);
    return set.getPortlets();
  }

  @SuppressWarnings("unchecked")
  private <T> T loadObject(Class<T> clazz, String file) throws Exception {
    IBindingFactory bfact = BindingDirectory.getFactory(clazz);
    IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
    FileInputStream is = new FileInputStream("src/test/resources/" + file);

    return (T) uctx.unmarshalDocument(is, null);
  }

}
