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
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PageNode;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.services.organization.OrganizationService;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;

/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 23, 2006
 */
public class TestUserPortalConfigService extends UserPortalServiceTestBase {

	private UserPortalConfigService userPortalConfigSer_;

	public TestUserPortalConfigService(String name) {
		super(name);
	}

	public void setUp() throws Exception {
		if (userPortalConfigSer_ != null)
			return;
		PortalContainer manager = PortalContainer.getInstance();
		userPortalConfigSer_ = (UserPortalConfigService) manager
				.getComponentInstanceOfType(UserPortalConfigService.class);
		orgService_ = (OrganizationService) manager
				.getComponentInstanceOfType(OrganizationService.class);
	}

	public void tearDown() throws Exception {
		String newName = "newportal";
		UserPortalConfig portal = userPortalConfigSer_.getUserPortalConfig(newName, "root");
		if (portal != null) {
			userPortalConfigSer_.removeUserPortalConfig(newName);
			assertNull(userPortalConfigSer_.getUserPortalConfig(newName, "none"));
		}
	}

	public void testCreateUserPortalConfig() throws Exception {
		String portalTemplate = "classic";
		String newName = "newportal";

		userPortalConfigSer_.createUserPortalConfig(newName, portalTemplate);
		UserPortalConfig newportal = userPortalConfigSer_.getUserPortalConfig(
				newName, "root");

		PortalConfig newPortalConfig = newportal.getPortalConfig();
		assertEquals(newName, newPortalConfig.getName());
		//		service_.removeUserPortalConfig(newName) ;
	}

	public void testGetUserPortalConfig() throws Exception {
		UserPortalConfig userPortalConfig = userPortalConfigSer_
				.getUserPortalConfig("classic", "root");
		PortalConfig portalConfig = userPortalConfig.getPortalConfig();

		assertTrue(portalConfig != null);
	}

	public void testPortalConfigUpdate() throws Exception {
		String portalName = "newportal";
		userPortalConfigSer_.createUserPortalConfig(portalName, "classic");
		UserPortalConfig oldUserPortalConfig = userPortalConfigSer_
				.getUserPortalConfig(portalName, "root");
		PortalConfig oldPortalConfig = oldUserPortalConfig.getPortalConfig();
		assertEquals(portalName, oldPortalConfig.getName());
		assertEquals("en", oldPortalConfig.getLocale());
		assertEquals(1, oldPortalConfig.getAccessPermissions().length);
		assertEquals("Everyone", oldPortalConfig.getAccessPermissions()[0]);
		assertEquals(2, oldUserPortalConfig.getNavigations().size());

		String newLocate = "vi";
		oldPortalConfig.setLocale(newLocate);
		userPortalConfigSer_.update(oldPortalConfig);

		UserPortalConfig newUserPortalConfig = userPortalConfigSer_
				.getUserPortalConfig(portalName, "root");
		PortalConfig newPortalConfig = newUserPortalConfig.getPortalConfig();
		assertEquals(portalName, oldPortalConfig.getName());
		assertEquals(newLocate, newPortalConfig.getLocale());
		assertEquals(1, newPortalConfig.getAccessPermissions().length);
		assertEquals("Everyone", newPortalConfig.getAccessPermissions()[0]);
		assertEquals(2, newUserPortalConfig.getNavigations().size());
	}

	public void testNavigationUpdate() throws Exception {
		String portalName = "classic";
		String accessUser = "root";

		UserPortalConfig oldUserPortalConfig = userPortalConfigSer_
				.getUserPortalConfig(portalName, accessUser);
		List<PageNavigation> oldNavigations = oldUserPortalConfig.getNavigations();
		assertTrue(oldNavigations.size() > 0);

		// Change description
		String newDescription = "This is new description.";
		for (PageNavigation navi : oldNavigations) {
			navi.setDescription(newDescription);
			userPortalConfigSer_.update(navi);
		}

		UserPortalConfig newUserPortalConfig = userPortalConfigSer_
				.getUserPortalConfig(portalName, accessUser);
		List<PageNavigation> newNavigations = newUserPortalConfig.getNavigations();
		assertTrue(newNavigations.size() > 0);

		PageNavigation portalNavigation = newNavigations.get(0);
		assertEquals(newDescription, portalNavigation.getDescription());
	}

	public void testNavigationRemove() throws Exception {
		String portalName = "classic";
		String accessUser = "exoadmin";

		UserPortalConfig oldUserPortalConfig = userPortalConfigSer_
				.getUserPortalConfig(portalName, accessUser);
		List<PageNavigation> oldNavigations = oldUserPortalConfig.getNavigations();
		assertEquals(1, oldNavigations.size());

		// Remove navigation of the portal
		PageNavigation portalNavigation = oldNavigations.get(0);
		userPortalConfigSer_.remove(portalNavigation);

		UserPortalConfig newUserPortalConfig = userPortalConfigSer_
				.getUserPortalConfig(portalName, accessUser);
		List<PageNavigation> newNavigations = newUserPortalConfig.getNavigations();
		assertEquals(0, newNavigations.size());
		//		PageNavigation userNavigation = newNavigations.get(0) ;
		//		assertEquals(accessUser, userNavigation.getOwnerId()) ;

		//		// Remove remain navigation
		//		service_.remove(userNavigation) ;
		//		newUserPortalConfig = service_.getUserPortalConfig(portalName, accessUser) ;
		//		assertEquals(0, newUserPortalConfig.getNavigations().size()) ;
	}

	//	public void testPageCreate() throws Exception {
	//	String accessUser = "exoadmin" ; 
	//	String[] sitePortalPageNames = {"homepage", "register", "sitemap", "test"} ;

	//	List<Page> pages = new ArrayList<Page>() ;
	//	for (String pageName : sitePortalPageNames) {
	//	String sitePortalPageId = "portal::classic::" + pageName ;
	//	Page page = service_.getPage(sitePortalPageId, accessUser) ;
	//	if (page != null) pages.add(page) ;
	//	}
	//	assertEquals(1, pages.size()) ;

	//	// Add new page to Site portal
	//	String pageSetFile = "testpages.xml" ;
	//	PageSet pageSet = loadObject(PageSet.class, pageSetFile) ;
	//	List<Page> addPages = pageSet.getPages() ;
	//	assertTrue(addPages.size() == 1);
	//	for (Page p : addPages) {
	//	service_.create(p) ;
	//	}
	//	int totalPage = pages.size() + addPages.size() ;

	//	pages = new ArrayList<Page>() ;
	//	for (String pageName : sitePortalPageNames) {
	//	String sitePortalPageId = "portal::classic::" + pageName ;
	//	Page page = service_.getPage(sitePortalPageId, accessUser) ;
	//	if (page != null) pages.add(page) ;
	//	}

	//	assertEquals(totalPage, pages.size()) ;
	//	}

	public void testPageGet() throws Exception {
		this.prepareOrganizationData();
		String pageId = "group::platform/administrators::newAccount";
		Page page = userPortalConfigSer_.getPage(pageId);
		assertNotNull(page);
		assertEquals(pageId, page.getPageId());

		//Test if getPage() returns null when 'pageId' is Null
		page = userPortalConfigSer_.getPage(null);
		assertNull(page);
	}

	public void testPageUpdate() throws Exception {
		String[] sitePortalPageNames = { "homepage", "webexplorer" };

		for (String pageName : sitePortalPageNames) {
			String sitePortalPageId = "portal::classic::" + pageName;
			// TODO
			//Page page = service_.getPage(sitePortalPageId, accessUser) ;
			Page page = userPortalConfigSer_.getPage(sitePortalPageId);
			String newTitle = "New title of " + pageName;
			page.setTitle(newTitle);
			userPortalConfigSer_.update(page);

			//Page returnPage = service_.getPage(sitePortalPageId, accessUser) ;
			Page returnPage = userPortalConfigSer_.getPage(sitePortalPageId);
			assertEquals(sitePortalPageId, returnPage.getPageId());
		}
	}

	public void testPageRemove() throws Exception {
		String accessUser = "exoadmin";
		String[] sitePortalPageNames = { "homepage", "webexplorer" };

		List<Page> pages = new ArrayList<Page>();
		for (String pageName : sitePortalPageNames) {
			String sitePortalPageId = "portal::classic::" + pageName;
			//Page page = service_.getPage(sitePortalPageId, accessUser) ;
			Page page = userPortalConfigSer_.getPage(sitePortalPageId, accessUser);
			if (page != null)
				pages.add(page);
		}
		assertEquals(1, pages.size());

		//Page deletePage = service_.getPage("portal::classic::homepage", accessUser) ;
		Page deletePage = userPortalConfigSer_.getPage("portal::classic::homepage");
		userPortalConfigSer_.remove(deletePage);
		pages = new ArrayList<Page>();
		for (String pageName : sitePortalPageNames) {
			String sitePortalPageId = "portal::classic::" + pageName;
			Page page = userPortalConfigSer_.getPage(sitePortalPageId, accessUser);
			if (page != null)
				pages.add(page);
		}
		assertEquals(0, pages.size());
		//assertEquals(sitePortalPageNames[1], pages.get(0).getName()) ;
	}

	private <T> T loadObject(Class<T> clazz, String file) throws Exception {
		IBindingFactory bfact = BindingDirectory.getFactory(clazz);
		IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
		FileInputStream is = new FileInputStream("src/test/resources/" + file);

		return clazz.cast(uctx.unmarshalDocument(is, null));
	}

}
