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

import org.exoplatform.portal.webui.application.UIGadget;
import org.exoplatform.portal.webui.container.UIContainer;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.application.portlet.PortletRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
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
@ComponentConfig(
  lifecycle = UIApplicationLifecycle.class, 
  template = "app:/groovy/dashboard/webui/component/UIDashboardPortlet.gtmpl",
  events = {
    @EventConfig(listeners = UIDashboardPortlet.MinimizeGadgetActionListener.class),
    @EventConfig(listeners = UIDashboardPortlet.MaximizeGadgetActionListener.class)
  }
)
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
    addChild(UIDashboardMask.class, null, null).setRendered(false);
    addChild(UIDashboardEditForm.class, null, null);

    PortletPreferences pref = context.getRequest().getPreferences();
    String containerTemplate = pref.getValue("template", "three-columns") ;
    dashboard.setContainerTemplate(containerTemplate) ;
    
    String aggregatorId = pref.getValue("aggregatorId", "rssAggregator") ;
    dashboard.getChild(UIDashboardSelectContainer.class).setAggregatorId(aggregatorId) ;
    
    isPrivate = pref.getValue(ISPRIVATE, "0").equals(1);
    owner = pref.getValue(OWNER, null);
  }
  
  public int getNumberOfCols() {
    UIDashboardContainer dbCont = getChild(UIDashboard.class).getChild(UIDashboardContainer.class) ;
    return dbCont.getChild(UIContainer.class).getChildren().size() ;
  }

  public boolean canEdit() {
    PortletRequestContext context = (PortletRequestContext) WebuiRequestContext
	    .getCurrentInstance();
    String accessUser = context.getRemoteUser() ;
    if(accessUser == null || accessUser.equals("")) return false ;
    if ("__CURRENT_USER__".equals(owner)) {
      return true;
    }
    if (isPrivate) {
      if (accessUser.equals(owner)) return true;
    }
    return false;
  }

  public String getDashboardOwner() {
	if ("__CURRENT_USER__".equals(owner)) {
      PortletRequestContext context = (PortletRequestContext) WebuiRequestContext
	    .getCurrentInstance();
      return context.getRemoteUser();
    }
    return owner;
  }
  
  public static class MinimizeGadgetActionListener extends EventListener<UIDashboardPortlet> {
    public final void execute(final Event<UIDashboardPortlet> event) throws Exception {
      WebuiRequestContext context = event.getRequestContext() ;
      UIDashboardPortlet uiPortlet = event.getSource() ;
      String objectId = context.getRequestParameter(OBJECTID) ;
      String minimized = context.getRequestParameter("minimized") ;

      UIDashboard uiDashboard = uiPortlet.getChild(UIDashboard.class) ;
      UIGadget uiGadget = uiDashboard.getChild(UIDashboardContainer.class).getUIGadget(objectId) ;
      uiGadget.getProperties().setProperty("minimized", minimized) ;
      uiDashboard.getChild(UIDashboardContainer.class).save() ;
      context.addUIComponentToUpdateByAjax(uiGadget) ;
    }
  }
  
  public static class MaximizeGadgetActionListener extends EventListener<UIDashboardPortlet> {
    public final void execute(final Event<UIDashboardPortlet> event) throws Exception {
      WebuiRequestContext context = event.getRequestContext();
      UIDashboardPortlet uiPortlet = event.getSource();
      String objectId = context.getRequestParameter(OBJECTID);
      String maximize = context.getRequestParameter("maximize");
      UIDashboard uiDashboard = uiPortlet.getChild(UIDashboard.class);
      UIDashboardContainer uiDashboardContainer = uiDashboard.getChild(UIDashboardContainer.class);
      UIDashboardMask uiDashboardMask = uiPortlet.getChild(UIDashboardMask.class);
      UIGadget uiGadget = uiDashboardContainer.getUIGadget(objectId);
      if(maximize.equals("maximize")) {
        uiGadget.setView(UIGadget.CANVAS_VIEW);
        uiDashboardMask.setUIComponent(uiGadget);
        uiDashboardMask.setRendered(true);
        uiDashboard.setRendered(false);
      } else {
        uiGadget.setView(UIGadget.HOME_VIEW);
        uiDashboardMask.setUIComponent(null);
        uiDashboardMask.setRendered(false);
        uiDashboard.setRendered(true);
      }
      //context.addUIComponentToUpdateByAjax(uiPortlet) ;
    }
  }
}
