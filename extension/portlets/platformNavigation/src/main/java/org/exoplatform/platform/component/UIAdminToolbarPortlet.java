/**
 * Copyright (C) 2009 eXo Platform SAS.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.exoplatform.platform.component;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.wcm.webui.Utils;
import org.exoplatform.webui.application.WebuiApplication;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.application.portlet.PortletRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

@ComponentConfig(
    lifecycle = UIApplicationLifecycle.class,
    template = "app:/groovy/platformNavigation/portlet/UIAdminToolbarPortlet/UIAdminToolbarPortlet.gtmpl",
    events = {
      @EventConfig(listeners = UIAdminToolbarPortlet.ChangeEditingActionListener.class)
    }
)
public class UIAdminToolbarPortlet extends UIPortletApplication {
  public UIAdminToolbarPortlet() throws Exception {
    PortalRequestContext context = Util.getPortalRequestContext();
    Boolean quickEdit = (Boolean)context.getRequest().getSession().getAttribute(Utils.TURN_ON_QUICK_EDIT);
    if(quickEdit == null) {
      context.getRequest().getSession().setAttribute(Utils.TURN_ON_QUICK_EDIT, false);
    }
  }

  public PageNavigation getSelectedNavigation() throws Exception {
    return Utils.getSelectedNavigation();
  }

  public boolean hasEditPermissionOnPortal() throws Exception {
    return Utils.hasEditPermissionOnPortal();
  }

  public boolean hasEditPermissionOnNavigation() throws Exception {
    return Utils.hasEditPermissionOnNavigation();
  }

  public boolean hasEditPermissionOnPage() throws Exception {
    return Utils.hasEditPermissionOnPage();
  }

  public void processRender(WebuiApplication app, WebuiRequestContext context) throws Exception {
    // A user could view the toolbar portlet if he/she has edit permission
    // either on
    // 'active' page, 'active' portal or 'active' navigation
    if (hasEditPermissionOnNavigation() || hasEditPermissionOnPage() || hasEditPermissionOnPortal()) {
      super.processRender(app, context);
    }
  }

  public static class ChangeEditingActionListener extends EventListener<UIAdminToolbarPortlet> {
    
    /* (non-Javadoc)
     * @see org.exoplatform.webui.event.EventListener#execute(org.exoplatform.webui.event.Event)
     */
    public void execute(Event<UIAdminToolbarPortlet> event) throws Exception {
      PortalRequestContext context = Util.getPortalRequestContext();
      Boolean quickEdit = (Boolean)context.getRequest().getSession().getAttribute(Utils.TURN_ON_QUICK_EDIT);
      if(quickEdit == null || !quickEdit) {
        context.getRequest().getSession().setAttribute(Utils.TURN_ON_QUICK_EDIT, true);
        Utils.updatePortal((PortletRequestContext) event.getRequestContext());
      } else {
        context.getRequest().getSession().setAttribute(Utils.TURN_ON_QUICK_EDIT, false);
        Utils.updatePortal((PortletRequestContext) event.getRequestContext());
      }
    }
  }
}
