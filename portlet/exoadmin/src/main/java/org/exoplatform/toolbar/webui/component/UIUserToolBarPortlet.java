/*
 * Copyright (C) 2003-2009 eXo Platform SAS.
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
package org.exoplatform.toolbar.webui.component;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.webui.navigation.PageNavigationUtils;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;

/**
 * Created by The eXo Platform SAS
 * Author : Pham Thanh Tung
 *          thanhtungty@gmail.com
 * May 26, 2009  
 */
@ComponentConfig(
                 lifecycle = UIApplicationLifecycle.class,
                 template = "app:/groovy/admintoolbar/webui/component/UIUserToolBarPortlet.gtmpl"
)
public class UIUserToolBarPortlet extends UIPortletApplication {

  private List<PageNavigation> groupNavigations = null;
  private boolean hasGroupNavigations = false;
  
  public UIUserToolBarPortlet() throws Exception {
    buildNavigations();
  }
  
  public List<PageNavigation> getGroupNavigations() throws Exception {    
    return groupNavigations;
  }
  
  public boolean hasGroupNavigations() {
    return hasGroupNavigations;
  }

  private void buildNavigations() throws Exception {
    String remoteUser = Util.getPortalRequestContext().getRemoteUser();
    List<PageNavigation> allNavigations = Util.getUIPortal().getNavigations();
    groupNavigations = new ArrayList<PageNavigation>();
    for (PageNavigation navigation : allNavigations) {      
      if (navigation.getOwnerType().equals(PortalConfig.GROUP_TYPE)) {
        groupNavigations.add(PageNavigationUtils.filter(navigation, remoteUser));
      }
    }
    if(!groupNavigations.isEmpty()) {
      hasGroupNavigations = true;
    } else hasGroupNavigations = false;
  }

}
