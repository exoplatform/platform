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

import org.exoplatform.application.registry.Application;
import org.exoplatform.application.registry.ApplicationRegistryService;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.webui.application.UIGadget;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.application.portlet.PortletRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * set the event listeners.
 */
/**
 * @author exo
 */
@ComponentConfigs({ 
  @ComponentConfig(
    lifecycle = UIApplicationLifecycle.class, 
    template = "app:/groovy/dashboard/webui/component/UIDashboardPortlet.gtmpl", 
    events = {
    @EventConfig(listeners = UIDashboardPortlet.MoveGadgetActionListener.class),
    @EventConfig(listeners = UIDashboardPortlet.AddNewGadgetActionListener.class),
    @EventConfig(listeners = UIDashboardPortlet.SetShowSelectFormActionListener.class),
    @EventConfig(listeners = UIDashboardPortlet.DeleteGadgetActionListener.class)
   }
)
})
/**
 * Dashboard portlet that display google gadgets
 */
public class UIDashboardPortlet extends UIPortletApplication {

  public static final String COLINDEX = "colIndex";

  public static final String ROWINDEX = "rowIndex";

  public static final String OBJECTID = "objectId";

  public UIDashboardPortlet() throws Exception {
    PortletRequestContext context = (PortletRequestContext) WebuiRequestContext
        .getCurrentInstance();
    PortletPreferences pref = context.getRequest().getPreferences();
    addChild(UIDashboardSelectForm.class, null, null);
    addChild(UIDashboardEditForm.class, null, null);
    addChild(UIDashboardContainer.class, null, null).
        setColumns(Integer.parseInt(pref.getValue(UIDashboardEditForm.TOTAL_COLUMNS, "3")));
  }

  public static class SetShowSelectFormActionListener extends EventListener<UIDashboardPortlet> {
    public final void execute(final Event<UIDashboardPortlet> event) throws Exception {
      UIDashboardPortlet uiPortlet = event.getSource();
      UIDashboardSelectForm uiForm = uiPortlet.getChild(UIDashboardSelectForm.class);
      PortletRequestContext pcontext = (PortletRequestContext) event.getRequestContext();
      boolean isShow = Boolean.parseBoolean(pcontext.getRequestParameter("isShow"));

      uiForm.setShowSelectForm(isShow);
    }
  }

  public static class AddNewGadgetActionListener extends EventListener<UIDashboardPortlet> {
    public final void execute(final Event<UIDashboardPortlet> event) throws Exception {
      WebuiRequestContext context = event.getRequestContext();
      UIDashboardPortlet uiPortlet = event.getSource();
      int col = Integer.parseInt(context.getRequestParameter(COLINDEX));
      int row = Integer.parseInt(context.getRequestParameter(ROWINDEX));
      String objectId = context.getRequestParameter(UIComponent.OBJECTID);

      ApplicationRegistryService service = uiPortlet
          .getApplicationComponent(ApplicationRegistryService.class);
      Application application = service.getApplication(objectId);
      if (application == null) {
        return;
      }
      StringBuilder windowId = new StringBuilder(PortalConfig.USER_TYPE);
      windowId.append("#").append(context.getRemoteUser());
      windowId.append(":/").append(
          application.getApplicationGroup() + "/" + application.getApplicationName()).append('/');
      UIGadget uiGadget = event.getSource().createUIComponent(context, UIGadget.class, null, null);
      uiGadget.setId(Integer.toString(uiGadget.hashCode()+1));
      windowId.append(uiGadget.hashCode());
      uiGadget.setApplicationInstanceId(windowId.toString());
      UIDashboardContainer uiDashboardContainer = uiPortlet.getChild(UIDashboardContainer.class); 
      uiDashboardContainer.addUIGadget(uiGadget, col, row);
      uiDashboardContainer.save();
      
    }
  }

  public static class MoveGadgetActionListener extends EventListener<UIDashboardPortlet> {
    public final void execute(final Event<UIDashboardPortlet> event) throws Exception {
      WebuiRequestContext context = event.getRequestContext();
      UIDashboardPortlet uiPortlet = event.getSource();
      UIDashboardContainer uiDashboardContainer = uiPortlet.getChild(UIDashboardContainer.class);
      int col = Integer.parseInt(context.getRequestParameter(COLINDEX));
      int row = Integer.parseInt(context.getRequestParameter(ROWINDEX));
      String objectId = context.getRequestParameter(OBJECTID);

      uiDashboardContainer.moveUIGadget(objectId, col, row);
      uiDashboardContainer.save();
    }
  }

  public static class DeleteGadgetActionListener extends EventListener<UIDashboardPortlet> {
    public final void execute(final Event<UIDashboardPortlet> event) throws Exception {
      WebuiRequestContext context = event.getRequestContext();
      UIDashboardPortlet uiPortlet = event.getSource();
      String objectId = context.getRequestParameter(OBJECTID);
      
      UIDashboardContainer uiDashboardContainer = uiPortlet.getChild(UIDashboardContainer.class);
      uiDashboardContainer.removeUIGadget(objectId);
      uiDashboardContainer.save();
    }
  }
}
