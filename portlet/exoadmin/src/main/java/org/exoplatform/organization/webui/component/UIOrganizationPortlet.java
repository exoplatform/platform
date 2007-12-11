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
package org.exoplatform.organization.webui.component;

import org.exoplatform.portal.webui.portal.UIPortalComponentActionListener.ViewChildActionListener;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;
/**
 * Created by The eXo Platform SARL
 * Author : chungnv
 *          nguyenchung136@yahoo.com
 * Jun 23, 2006
 * 10:07:15 AM
 */

@ComponentConfig(
  lifecycle = UIApplicationLifecycle.class
)
public class UIOrganizationPortlet extends UIPortletApplication {
 
  public UIOrganizationPortlet() throws Exception {
    setMinWidth(730) ;
  	addChild(UIViewMode.class, null, UIPortletApplication.VIEW_MODE);
  }

  @ComponentConfig(
      template = "app:/groovy/organization/webui/component/UIViewMode.gtmpl",
      events = @EventConfig (listeners = ViewChildActionListener.class)
  )
  static public class UIViewMode extends UIContainer {
    public UIViewMode() throws Exception {
      addChild(UIUserManagement.class, null, null);
      addChild(UIGroupManagement.class, null, null).setRendered(false);
      addChild(UIMembershipManagement.class, null, null).setRendered(false);
    }
  } 
  
}