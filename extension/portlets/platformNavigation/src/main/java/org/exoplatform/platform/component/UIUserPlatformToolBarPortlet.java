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
package org.exoplatform.platform.component;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;

/**
 * Portlet manages profile.<br> 
 *
 */
@ComponentConfig(
    lifecycle = UIApplicationLifecycle.class, 
   
    template = "app:/groovy/platformNavigation/portlet/UIUserPlatformToolBarPortlet/UIUserPlatformToolBarPortlet.gtmpl"	
)
public class UIUserPlatformToolBarPortlet extends UIPortletApplication {

  public UIUserPlatformToolBarPortlet() throws Exception {
  }
  
  public User getUser() throws Exception {
	  OrganizationService service = getApplicationComponent(OrganizationService.class);
      String userName = Util.getPortalRequestContext().getRemoteUser();
      User user = service.getUserHandler().findUserByName(userName);
      return user;
  }

  /**
   * gets navigation page list
   * @return navigation page list
   * @throws Exception
   */
  public List<PageNavigation> getNavigations() throws Exception {
    List<PageNavigation> result = new ArrayList<PageNavigation>();
    UIPortalApplication uiPortalApp = Util.getUIPortalApplication();
    List<PageNavigation> navigations = uiPortalApp.getNavigations();
    
	for (PageNavigation pageNavigation : navigations) {
		if(pageNavigation.getOwnerType().equals("portal"))
			result.add(pageNavigation);
	  }
    return result;
  }
}