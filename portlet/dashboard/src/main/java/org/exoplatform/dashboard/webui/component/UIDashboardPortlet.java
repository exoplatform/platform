/*
 * Copyright (C) 2003-2008 eXo Platform SAS.
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
package org.exoplatform.dashboard.webui.component;

import javax.portlet.PortletPreferences;

import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.application.portlet.PortletRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;

/**
 * set the event listeners.
 */
/**
 * @author exo
 */
@ComponentConfigs({ 
  @ComponentConfig(
    lifecycle = UIApplicationLifecycle.class, 
    template = "app:/groovy/dashboard/webui/component/UIDashboardPortlet.gtmpl"
)
})
/**
 * Dashboard portlet that display google gadgets
 */
public class UIDashboardPortlet extends UIPortletApplication implements DashboardParent {
  private boolean isPrivate;
  private String owner;


  public UIDashboardPortlet() throws Exception {
    PortletRequestContext context = (PortletRequestContext) WebuiRequestContext
        .getCurrentInstance();

    UIDashboard dashboard = addChild(UIDashboard.class, null, null);
    addChild(UIDashboardEditForm.class, null, null);

    PortletPreferences pref = context.getRequest().getPreferences();
    dashboard.setColumns(Integer.parseInt(pref.getValue(UIDashboardEditForm.TOTAL_COLUMNS, "3")));
    isPrivate = pref.getValue(ISPRIVATE, "0").equals(1);
    owner = pref.getValue(OWNER, null);
  }

  public boolean canEdit() {

    if ("__CURRENT_USER__".equals(owner)) {
      return true;
    }

    PortletRequestContext context = (PortletRequestContext) WebuiRequestContext
	    .getCurrentInstance();
    context.getRemoteUser();
    if (isPrivate) {
      if (context.getRemoteUser().equals(owner))
        return true;
      return false;
    }
    return true;
  }

  public String getDashboardOwner() {
	if ("__CURRENT_USER__".equals(owner)) {
      PortletRequestContext context = (PortletRequestContext) WebuiRequestContext
	    .getCurrentInstance();
      return context.getRemoteUser();
    }
    return owner;
  }

}
