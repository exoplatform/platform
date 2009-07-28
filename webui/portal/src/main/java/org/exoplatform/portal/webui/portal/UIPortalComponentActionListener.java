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
package org.exoplatform.portal.webui.portal;

import java.util.Date;
import java.util.List;

import org.exoplatform.commons.utils.PageList;
import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.config.model.Container;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.webui.application.UIApplicationList;
import org.exoplatform.portal.webui.application.UIPortlet;
import org.exoplatform.portal.webui.container.UIContainerList;
import org.exoplatform.portal.webui.login.UILogin;
import org.exoplatform.portal.webui.login.UIResetPassword;
import org.exoplatform.portal.webui.page.UIPage;
import org.exoplatform.portal.webui.page.UIPageBody;
import org.exoplatform.portal.webui.util.PortalDataMapper;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIMaskWorkspace;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.portal.webui.workspace.UIPortalToolPanel;
import org.exoplatform.portal.webui.workspace.UIWorkingWorkspace;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.Query;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.portletcontainer.PCConstants;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.core.UITabPane;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.exception.MessageException;

/**
 * Author : Nhu Dinh Thuan nhudinhthuan@yahoo.com Jun 14, 2006
 */
public class UIPortalComponentActionListener {

  static public class ViewChildActionListener extends EventListener<UIContainer> {
    public void execute(Event<UIContainer> event) throws Exception {
      UIContainer uiContainer = event.getSource();
      String id = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID);
      uiContainer.setRenderedChild(id);
    }
  }

  static public class ShowLoginFormActionListener extends EventListener<UIPortalComponent> {
    public void execute(Event<UIPortalComponent> event) throws Exception {
      UIPortal uiPortal = Util.getUIPortal();
      UIPortalApplication uiApp = uiPortal.getAncestorOfType(UIPortalApplication.class);
      UIMaskWorkspace uiMaskWS = uiApp.getChildById(UIPortalApplication.UI_MASK_WS_ID);
      UILogin uiLogin = uiMaskWS.createUIComponent(UILogin.class, null, null);
      uiMaskWS.setUIComponent(uiLogin);
      uiMaskWS.setWindowSize(630, -1);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiMaskWS);
    }
  }

  //  
  // static public class RemoveJSApplicationToDesktopActionListener extends
  // EventListener<UIPortalComponent> {
  // public void execute(Event<UIPortalComponent> event) throws Exception {
  // UIPortal uiPortal = Util.getUIPortal();
  // UIPortalApplication uiApp =
  // uiPortal.getAncestorOfType(UIPortalApplication.class);
  // UIPage uiPage = uiApp.findFirstComponentOfType(UIPage.class);
  // String id = event.getRequestContext().getRequestParameter("jsInstanceId");
  // uiPage.removeChildById(id);
  //     
  // Page page = PortalDataMapper.toPageModel(uiPage);
  // UserPortalConfigService configService =
  // uiPortal.getApplicationComponent(UserPortalConfigService.class);
  // if(page.getChildren() == null) page.setChildren(new ArrayList<Object>());
  // configService.update(page);
  // }
  // }

  static public class DeleteComponentActionListener extends EventListener<UIComponent> {
    public void execute(Event<UIComponent> event) throws Exception {
      String id = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID);
      UIComponent uiComponent = event.getSource();
      UIPortalComponent uiParent = (UIPortalComponent) uiComponent.getParent();
      UIComponent uiRemoveComponent = uiParent.findComponentById(id);
      if (uiRemoveComponent.findFirstComponentOfType(UIPageBody.class) != null) {
        Util.getUIPortalApplication()
            .addMessage(new ApplicationMessage("UIPortalApplication.msg.deletePageBody",
                                               new Object[] {},
                                               ApplicationMessage.WARNING));
        event.getRequestContext().addUIComponentToUpdateByAjax(Util.getUIPortalApplication()
                                                                   .getUIPopupMessages());
        return;
      }

      uiParent.removeChildById(id);
      UIPage uiPage = uiParent.getAncestorOfType(UIPage.class);
      if (uiPage != null && uiPage.getMaximizedUIPortlet() != null) {
        if (id.equals(uiPage.getMaximizedUIPortlet().getId())) {
          uiPage.setMaximizedUIPortlet(null);
        }
      } else {
        UIPortal uiPortal = Util.getUIPortal();
        if (uiPortal != null && uiPortal.getMaximizedUIComponent() != null) {
          if (id.equals(uiPortal.getMaximizedUIComponent().getId())) {
            uiPortal.setMaximizedUIComponent(null);
          }
        } else {
          UIPageBody uiPageBody = uiPortal.findFirstComponentOfType(UIPageBody.class);
          if (uiPageBody != null && uiPageBody.getMaximizedUIComponent() != null) {
            if (id.equals(uiPageBody.getMaximizedUIComponent().getId())) {
              uiPageBody.setMaximizedUIComponent(null);
            }
          }
        }
      }
      Util.showComponentLayoutMode(uiRemoveComponent.getClass());

      PortalRequestContext pcontext = (PortalRequestContext) event.getRequestContext();
      // UIPortalApplication uiPortalApp =
      // uiParent.getAncestorOfType(UIPortalApplication.class);
      // UIWorkingWorkspace uiWorkingWS =
      // uiPortalApp.getChildById(UIPortalApplication.UI_WORKING_WS_ID);
      // pcontext.addUIComponentToUpdateByAjax(uiWorkingWS);
      // pcontext.setFullRender(true);
      pcontext.setFullRender(false);
      pcontext.getWriter().write("OK");
      pcontext.setResponseComplete(true);
    }
  }

  static public class MoveChildActionListener extends EventListener<UIContainer> {
    public void execute(Event<UIContainer> event) throws Exception {
      PortalRequestContext pcontext = (PortalRequestContext) event.getRequestContext();
      String insertPosition = pcontext.getRequestParameter("insertPosition");
      int position = -1;
      try {
        position = Integer.parseInt(insertPosition);
      } catch (Exception exp) {
        position = -1;
      }

      boolean newComponent = false;
      String paramNewComponent = pcontext.getRequestParameter("newComponent");

      if(paramNewComponent != null) newComponent = Boolean.valueOf(paramNewComponent).booleanValue();
      
      UIPortalApplication uiApp = event.getSource().getAncestorOfType(UIPortalApplication.class);
      UIPortalComposer portalComposer = uiApp.findFirstComponentOfType(UIPortalComposer.class);

      if(newComponent){
        portalComposer.updateWorkspaceComponent();
        pcontext.setFullRender(true);
      }

      UIComponent uiWorking = uiApp.findFirstComponentOfType(UIPortal.class);
      if (!uiWorking.isRendered())
        uiWorking = uiApp.findFirstComponentOfType(UIPortalToolPanel.class);

      String sourceId = pcontext.getRequestParameter("srcID");
      UIComponent uiSource = uiWorking.findComponentById(sourceId);

      UIContainer uiTarget = uiWorking.findComponentById(pcontext.getRequestParameter("targetID"));
      if (position < 0 && uiTarget.getChildren().size() > 0) {
        position = uiTarget.getChildren().size();
      } else if (position < 0) {
        position = 0;
      }

      if (uiSource == null) {
        UITabPane subTabPane = portalComposer.getChild(UITabPane.class);
        UIContainerList uiContainerConfig = subTabPane.getChild(UIContainerList.class);
        if (uiContainerConfig != null
            && subTabPane.getSelectedTabId().equals(uiContainerConfig.getId())) {
          org.exoplatform.portal.webui.container.UIContainer uiContainer = uiTarget.createUIComponent(org.exoplatform.portal.webui.container.UIContainer.class,
                                                                                                      null,
                                                                                                      null);
          Container container = uiContainerConfig.getContainer(sourceId);
          container.setId(String.valueOf(container.hashCode()));
          PortalDataMapper.toUIContainer(uiContainer, container);
          uiSource = uiContainer;
        } else {
          // UIPortletOptions uiPortletOptions =
          // uiApp.findFirstComponentOfType(UIPortletOptions.class);
          org.exoplatform.application.registry.Application portlet = null;
          // if(uiPortletOptions != null) { portlet =
          // uiPortletOptions.getPortlet(sourceId);}
          // else {
          portlet = uiApp.findFirstComponentOfType(UIApplicationList.class).getPortlet(sourceId);
          // }
          UIPortlet uiPortlet = uiTarget.createUIComponent(UIPortlet.class, null, null);
          if (portlet.getDisplayName() != null) {
            uiPortlet.setTitle(portlet.getDisplayName());
          } else if (portlet.getApplicationName() != null) {
            uiPortlet.setTitle(portlet.getApplicationName());
          }
          uiPortlet.setDescription(portlet.getDescription());
          String[] accessPermissions = portlet.getAccessPermissions()
                                              .toArray(new String[portlet.getAccessPermissions()
                                                                         .size()]);
          uiPortlet.setAccessPermissions(accessPermissions);
          StringBuilder windowId = new StringBuilder();
          UIPage uiPage = uiTarget.getAncestorOfType(UIPage.class);
          if (uiPage != null)
            windowId.append(uiPage.getOwnerType());
          else
            windowId.append(PortalConfig.PORTAL_TYPE);
          windowId.append('#').append(Util.getUIPortal().getOwner()).append(":/");
          uiPortlet.setPortletInPortal(uiTarget instanceof UIPortal);
          windowId.append(portlet.getApplicationGroup() + "/" + portlet.getApplicationName())
                  .append('/');
          windowId.append(uiPortlet.hashCode());
          uiPortlet.setWindowId(windowId.toString());
          uiPortlet.setShowEditControl(true);
          uiSource = uiPortlet;
        }
        List<UIComponent> children = uiTarget.getChildren();
        uiSource.setParent(uiTarget);
        children.add(position, uiSource);
        // if(uiApp.isBlockEditMode())
        // Util.showComponentLayoutMode(uiSource.getClass());
        // else Util.showComponentEditInViewMode(uiSource.getClass());
        return;
      }

      UIContainer uiParent = uiSource.getParent();
      if (uiParent == uiTarget) {
        int currentIdx = uiTarget.getChildren().indexOf(uiSource);
        if (position <= currentIdx) {
          uiTarget.getChildren().add(position, uiSource);
          currentIdx++;
          uiTarget.getChildren().remove(currentIdx);
          return;
        }
        uiTarget.getChildren().remove(currentIdx);
        if (position >= uiTarget.getChildren().size()) {
          position = uiTarget.getChildren().size();
        }
        uiTarget.getChildren().add(position, uiSource);
        return;
      }
      uiParent.getChildren().remove(uiSource);
      uiTarget.getChildren().add(position, uiSource);
      uiSource.setParent(uiTarget);
      // if(uiApp.isBlockEditMode())
      // Util.showComponentLayoutMode(uiSource.getClass());
      // else Util.showComponentEditInViewMode(uiSource.getClass());
    }

  }

  public static class ChangeLanguageActionListener extends EventListener<UIPortal> {

    @Override
    public void execute(Event<UIPortal> event) throws Exception {
      UIPortal uiPortal = event.getSource();
      UIPortalApplication uiPortalApp = uiPortal.getAncestorOfType(UIPortalApplication.class);
      UIMaskWorkspace uiMaskWorkspace = uiPortalApp.getChildById(UIPortalApplication.UI_MASK_WS_ID);
      uiMaskWorkspace.createUIComponent(UILanguageSelector.class, null, null);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiMaskWorkspace);
    }

  }

  public static class RecoveryPasswordAndUsernameActionListener extends EventListener<UIPortal> {
    @Override
    public void execute(Event<UIPortal> event) throws Exception {
      UIPortal uiPortal = event.getSource();
      UIPortalApplication uiApp = uiPortal.getAncestorOfType(UIPortalApplication.class);
      UIMaskWorkspace uiMaskWS = uiApp.getChildById(UIPortalApplication.UI_MASK_WS_ID);
      String date = event.getRequestContext().getRequestParameter("datesend");
      String email = event.getRequestContext().getRequestParameter("email");
      OrganizationService orgSrc = uiPortal.getApplicationComponent(OrganizationService.class);
      // get user
      PageList userPageList = orgSrc.getUserHandler().findUsers(new Query());
      List userList = userPageList.currentPage();
      User user = null;
      for (int i = 0; i < userList.size(); i++) {
        User tmpUser = (User) userList.get(i);
        if (tmpUser.getEmail().equals(email)) {
          user = tmpUser;
          break;
        }
      }
      if (user == null) {
        throw new MessageException(new ApplicationMessage("UIForgetPassword.msg.user-delete", null));
      }
      // delete link active by one day
      long now = new Date().getTime();
      if (now - Long.parseLong(date) > 86400000) {
        user.setPassword(Long.toString(now));
        orgSrc.getUserHandler().saveUser(user, true);
        throw new MessageException(new ApplicationMessage("UIForgetPassword.msg.expration", null));
      }
      UIResetPassword uiReset = uiMaskWS.createUIComponent(UIResetPassword.class, null, null);
      uiReset.setData(user);
      uiMaskWS.setUIComponent(uiReset);
      uiMaskWS.setWindowSize(630, -1);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiMaskWS);
    }
  }

  static public class ChangeSkinActionListener extends EventListener<UIPortal> {
    public void execute(Event<UIPortal> event) throws Exception {
      UIPortal uiPortal = event.getSource();
      UIPortalApplication uiApp = uiPortal.getAncestorOfType(UIPortalApplication.class);
      UIMaskWorkspace uiMaskWS = uiApp.getChildById(UIPortalApplication.UI_MASK_WS_ID);

      UISkinSelector uiChangeSkin = uiMaskWS.createUIComponent(UISkinSelector.class, null, null);
      uiMaskWS.setUIComponent(uiChangeSkin);
      uiMaskWS.setWindowSize(640, 400);
      uiMaskWS.setShow(true);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiMaskWS);
    }
  }

}
