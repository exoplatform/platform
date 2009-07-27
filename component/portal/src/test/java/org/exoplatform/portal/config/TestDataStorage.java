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

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.portal.application.PortletPreferences;
import org.exoplatform.portal.application.Preference;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.services.portletcontainer.pci.ExoWindowID;
import org.exoplatform.test.BasicTestCase;

/**
 * Created by The eXo Platform SARL Author : Tung Pham thanhtungty@gmail.com Nov
 * 13, 2007
 */
public class TestDataStorage extends BasicTestCase {

	private final String testPortal = "testPortal";
	private final String testPage = "portal::classic::testPage";
	private final String testPortletPreferences = "portal#classic:/web/BannerPortlet/testPortletPreferences";
	
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
  	PageNavigation pageNavi = storage_.getPageNavigation("portal", testPortal);
  	assertNotNull(pageNavi);
 
  	String newModifier = "trong.tran";
  	pageNavi.setModifier(newModifier);
  	storage_.save(pageNavi);
  	
  	PageNavigation newPageNavi = storage_.getPageNavigation(pageNavi.getOwnerType(), pageNavi.getOwnerId());
  	assertEquals(newModifier, newPageNavi.getModifier());
  }

  public void testNavigationRemove() throws Exception {
  	PageNavigation pageNavi = storage_.getPageNavigation("portal", testPortal);
  	assertNotNull(pageNavi);
  	
  	storage_.remove(pageNavi);
  	
  	pageNavi = storage_.getPageNavigation("portal", testPortal);
  	assertNull(pageNavi);
  }

  public void testPortletPreferencesCreate() throws Exception {
  	ArrayList<Preference> prefs = new ArrayList<Preference>();
  	for(int i = 0; i < 5; i++) {
  		Preference pref = new Preference();
  		pref.setName("name" + i);
  		pref.addValue("value" + i);
  		prefs.add(pref);
  	}
  	
  	PortletPreferences portletPreferences = new PortletPreferences();
  	portletPreferences.setWindowId(testPortletPreferences);
  	portletPreferences.setOwnerId("classic");
  	portletPreferences.setOwnerType("portal");
  	portletPreferences.setPreferences(prefs);
  	
  	storage_.save(portletPreferences);
  	
  	PortletPreferences portletPref = storage_.getPortletPreferences(new ExoWindowID(testPortletPreferences));
  	assertEquals(portletPref.getWindowId(), testPortletPreferences);
  }

  public void testPortletPreferencesSave() throws Exception {
  	PortletPreferences portletPref = storage_.getPortletPreferences(new ExoWindowID(testPortletPreferences));
  	assertNotNull(portletPref);
  	
  	List<Preference> prefs = portletPref.getPreferences();
  	assertNotNull(prefs);
  	assertEquals(5, prefs.size());
  }

  public void testPortletPreferencesRemove() throws Exception {
  	PortletPreferences portletPref = storage_.getPortletPreferences(new ExoWindowID(testPortletPreferences));
  	assertNotNull(portletPref);
  	
  	storage_.remove(portletPref);

  	portletPref = storage_.getPortletPreferences(new ExoWindowID(testPortletPreferences));
  	assertNull(portletPref);
  }

  @SuppressWarnings("unchecked")
  public void testFind() throws Exception {
    Query<Page> query = new Query<Page>(null, null, Page.class);
    List<Page> findedPages = storage_.find(query).getAll();

    findedPages = storage_.find(query).getAll();
    assertTrue(findedPages.size() > 0);
  }
}
