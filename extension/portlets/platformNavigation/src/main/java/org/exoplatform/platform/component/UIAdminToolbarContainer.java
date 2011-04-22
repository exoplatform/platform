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

import java.util.UUID;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.portal.config.UserPortalConfig;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PageNode;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.webui.navigation.UINavigationManagement;
import org.exoplatform.portal.webui.navigation.UINavigationNodeSelector;
import org.exoplatform.portal.webui.page.UIPageNodeForm;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.wcm.webui.Utils;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.application.WebuiApplication;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.application.portlet.PortletRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.UIPopupWindow;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.Event.Phase;
import org.exoplatform.webui.event.EventListener;


@ComponentConfigs({
  @ComponentConfig(
      template = "app:/groovy/platformNavigation/portlet/UIAdminToolbarPortlet/UIAdminToolbarContainer.gtmpl",
      events = {
        @EventConfig(listeners = UIAdminToolbarContainer.ChangeEditingActionListener.class),
        @EventConfig(listeners = UIAdminToolbarContainer.EditNavigationActionListener.class)
      }
  ),
  @ComponentConfig(type = UIPageNodeForm.class, lifecycle = UIFormLifecycle.class,
      template = "system:/groovy/webui/form/UIFormTabPane.gtmpl",
      events = {
        @EventConfig(listeners = UIPageNodeForm.SaveActionListener.class),
        @EventConfig(listeners = UIAdminToolbarContainer.BackActionListener.class, phase = Phase.DECODE),
        @EventConfig(listeners = UIPageNodeForm.SwitchPublicationDateActionListener.class, phase = Phase.DECODE),
        @EventConfig(listeners = UIPageNodeForm.SwitchVisibleActionListener.class, phase = Phase.DECODE),
        @EventConfig(listeners = UIPageNodeForm.ClearPageActionListener.class, phase = Phase.DECODE),
        @EventConfig(listeners = UIPageNodeForm.CreatePageActionListener.class, phase = Phase.DECODE)
      }
  )
})
public class UIAdminToolbarContainer extends UIPortletApplication {

  public UIAdminToolbarContainer() throws Exception {
    PortalRequestContext context = Util.getPortalRequestContext();
    Boolean quickEdit = (Boolean) context.getRequest().getSession().getAttribute(Utils.TURN_ON_QUICK_EDIT);
    if (quickEdit == null) {
      context.getRequest().getSession().setAttribute(Utils.TURN_ON_QUICK_EDIT, false);
    }
    UIPopupWindow editNavigation = addChild(UIPopupWindow.class, null, null);
    editNavigation.setWindowSize(400, 400);
    editNavigation.setId(editNavigation.getId() + "-" + UUID.randomUUID().toString().replaceAll("-", ""));
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

  public static class ChangeEditingActionListener extends EventListener<UIAdminToolbarContainer> {

    /*
     * (non-Javadoc)
     * @see org.exoplatform.webui.event.EventListener#execute(org.exoplatform.webui.event.Event)
     */
    public void execute(Event<UIAdminToolbarContainer> event) throws Exception {
      PortalRequestContext context = Util.getPortalRequestContext();
      Boolean quickEdit = (Boolean) context.getRequest().getSession().getAttribute(Utils.TURN_ON_QUICK_EDIT);
      if (quickEdit == null || !quickEdit) {
        context.getRequest().getSession().setAttribute(Utils.TURN_ON_QUICK_EDIT, true);
        Utils.updatePortal((PortletRequestContext) event.getRequestContext());
      } else {
        context.getRequest().getSession().setAttribute(Utils.TURN_ON_QUICK_EDIT, false);
        Utils.updatePortal((PortletRequestContext) event.getRequestContext());
      }
    }
  }

  static public class EditNavigationActionListener extends EventListener<UIAdminToolbarContainer> {
    public void execute(Event<UIAdminToolbarContainer> event) throws Exception {
      UIAdminToolbarContainer uicomp = event.getSource();
      PageNavigation edittedNavigation = Utils.getSelectedNavigation();

      WebuiRequestContext context = event.getRequestContext();
      UIApplication uiApplication = context.getUIApplication();

      if (edittedNavigation == null) {
        uiApplication.addMessage(new ApplicationMessage("UISiteManagement.msg.Invalid-editPermission", null));
        return;
      }

      UserACL userACL = uicomp.getApplicationComponent(UserACL.class);
      if (edittedNavigation.getOwnerType().equals(PortalConfig.PORTAL_TYPE)) {
        String portalName = Util.getPortalRequestContext().getPortalOwner();
        UserPortalConfigService configService = uicomp.getApplicationComponent(UserPortalConfigService.class);
        UserPortalConfig userPortalConfig = configService.getUserPortalConfig(portalName, context.getRemoteUser());
        if (userPortalConfig == null) {
          uiApplication.addMessage(new ApplicationMessage("UISiteManagement.msg.portal-not-exist", new String[] { portalName }));
          return;
        }
        if (!userACL.hasEditPermission(userPortalConfig.getPortalConfig())) {
          uiApplication.addMessage(new ApplicationMessage("UISiteManagement.msg.Invalid-editPermission", null));
          return;
        }
      } else if (edittedNavigation.getOwnerType().equals(PortalConfig.GROUP_TYPE)) {
        if (!userACL.hasEditPermission(edittedNavigation)) {
          uiApplication.addMessage(new ApplicationMessage("UISiteManagement.msg.Invalid-editPermission", null));
          return;
        }
      }

      UIPopupWindow popUp = uicomp.getChild(UIPopupWindow.class);
      UINavigationManagement naviManager = popUp.createUIComponent(UINavigationManagement.class, null, null, popUp);
      naviManager.setOwner(edittedNavigation.getOwnerId());
      naviManager.setOwnerType(edittedNavigation.getOwnerType());

      UINavigationNodeSelector selector = naviManager.getChild(UINavigationNodeSelector.class);
      selector.setEdittedNavigation(edittedNavigation);
      selector.initTreeData();

      popUp.setUIComponent(naviManager);
      popUp.setShowMask(true);
      popUp.setShow(true);
      context.addUIComponentToUpdateByAjax(uicomp);
    }
  }

  static public class BackActionListener extends EventListener<UIPageNodeForm>
  {

     public void execute(Event<UIPageNodeForm> event) throws Exception
     {
        UIPageNodeForm uiPageNodeForm = event.getSource();
        PageNavigation contextNavigation = uiPageNodeForm.getContextPageNavigation();
        UIAdminToolbarContainer uiAdminToolbarContainer = uiPageNodeForm.getAncestorOfType(UIAdminToolbarContainer.class);
        UIPopupWindow uiNavigationPopup = uiAdminToolbarContainer.getChild(UIPopupWindow.class);
        UINavigationManagement navigationManager = uiPageNodeForm.createUIComponent(UINavigationManagement.class, null, null);
        navigationManager.setOwner(contextNavigation.getOwnerId());
        navigationManager.setOwnerType(contextNavigation.getOwnerType());
        UINavigationNodeSelector selector = navigationManager.getChild(UINavigationNodeSelector.class);
        
        selector.setEdittedNavigation(uiPageNodeForm.getContextPageNavigation());
        selector.initTreeData();
        
        if (uiPageNodeForm.getSelectedParent() instanceof PageNode)
        {
           PageNode selectedParent = (PageNode)uiPageNodeForm.getSelectedParent();
           selector.selectPageNodeByUri(selectedParent.getUri());
        }
        
        uiNavigationPopup.setUIComponent(navigationManager);
        uiNavigationPopup.setWindowSize(400, 400);
        event.getRequestContext().addUIComponentToUpdateByAjax(uiNavigationPopup.getParent());
     }

  }
}
