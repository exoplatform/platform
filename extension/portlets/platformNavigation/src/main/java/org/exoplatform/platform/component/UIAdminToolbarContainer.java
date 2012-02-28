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

import java.util.List;

import org.exoplatform.platform.webui.NavigationURLUtils;
import org.exoplatform.platform.webui.navigation.TreeNode;
import org.exoplatform.platform.webui.navigation.UINavigationManagement;
import org.exoplatform.platform.webui.navigation.UINavigationNodeSelector;
import org.exoplatform.platform.webui.navigation.UIPageNodeForm;
import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.portal.config.UserPortalConfig;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.mop.SiteKey;
import org.exoplatform.portal.mop.SiteType;
import org.exoplatform.portal.mop.user.UserNavigation;
import org.exoplatform.portal.mop.user.UserPortal;
import org.exoplatform.portal.webui.page.UIPage;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;
import org.exoplatform.wcm.webui.Utils;
import org.exoplatform.wcm.webui.seo.UISEOToolbarForm;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webos.webui.page.UIDesktopPage;
import org.exoplatform.webui.application.WebuiApplication;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.application.portlet.PortletRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIPopupContainer;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.Event.Phase;
import org.exoplatform.webui.event.EventListener;

@ComponentConfigs({
    @ComponentConfig(template = "app:/groovy/platformNavigation/portlet/UIAdminToolbarPortlet/UIAdminToolbarContainer.gtmpl", events = {
        @EventConfig(listeners = UIAdminToolbarContainer.ChangeEditingActionListener.class),
        @EventConfig(listeners = UIAdminToolbarContainer.EditNavigationActionListener.class) }),
    @ComponentConfig(type = UIPageNodeForm.class, lifecycle = UIFormLifecycle.class, template = "system:/groovy/webui/form/UIFormTabPane.gtmpl", events = {
        @EventConfig(listeners = UIPageNodeForm.SaveActionListener.class),
        @EventConfig(listeners = UIAdminToolbarContainer.BackActionListener.class, phase = Phase.DECODE),
        @EventConfig(listeners = UIPageNodeForm.SwitchPublicationDateActionListener.class, phase = Phase.DECODE),
        @EventConfig(listeners = UIPageNodeForm.SwitchLabelModeActionListener.class, phase = Phase.DECODE),
        @EventConfig(listeners = UIPageNodeForm.ChangeLanguageActionListener.class, phase = Phase.DECODE),
        @EventConfig(listeners = UIPageNodeForm.SwitchVisibleActionListener.class, phase = Phase.DECODE),
        @EventConfig(listeners = UIPageNodeForm.ClearPageActionListener.class, phase = Phase.DECODE),
        @EventConfig(listeners = UIPageNodeForm.CreatePageActionListener.class, phase = Phase.DECODE) }) })
public class UIAdminToolbarContainer extends UIPortletApplication {

  private static final String SEO_TOOLBAR_FORM_POPUP_CONTAINER_ID = "UISEOToolbarFormPopupContainer";
  private static final String EDIT_NAVIGATION_POPUP_CONTAINER_ID = "UIPopupWindow-UIEditNavigationPopupContainer";
  private static final String PAGE_MANAGEMENT_URI = "administration/pageManagement";
  private String pageManagementLink = null;
  private SpaceService spaceService = null;

  private String userId = null;
  protected UINavigationManagement naviManager;

  public UIAdminToolbarContainer() throws Exception {
    PortalRequestContext context = Util.getPortalRequestContext();
    Boolean quickEdit = (Boolean) context.getRequest().getSession().getAttribute(Utils.TURN_ON_QUICK_EDIT);
    if (quickEdit == null) {
      context.getRequest().getSession().setAttribute(Utils.TURN_ON_QUICK_EDIT, false);
    }
    addChild(UIPopupContainer.class, null, SEO_TOOLBAR_FORM_POPUP_CONTAINER_ID);
    addChild(UISEOToolbarForm.class, null, null);
    spaceService = getApplicationComponent(SpaceService.class);
  }

  public String getPageManagementLink() {
    if (pageManagementLink == null) {
      UserACL userACL = getApplicationComponent(UserACL.class);
      String[] adminGroups = userACL.getAdminGroups().split(";");
      pageManagementLink = NavigationURLUtils.getURL(SiteKey.group(adminGroups[0]), PAGE_MANAGEMENT_URI);
    }
    return pageManagementLink;

  }

  public boolean hasEditPermissionOnPortal() throws Exception {
    return Utils.hasEditPermissionOnPortal();
  }

  public boolean isGroupNavigation() throws Exception {
    return SiteType.GROUP.equals(Utils.getSelectedNavigation().getKey().getType());
  }

  public boolean isPortaNavigation() throws Exception {
    return SiteType.PORTAL.equals(Utils.getSelectedNavigation().getKey().getType());
  }

  public boolean isUserNavigation() throws Exception {
    return SiteType.USER.equals(Utils.getSelectedNavigation().getKey().getType());
  }

  public boolean hasEditPermissionOnNavigation() throws Exception {
    return Utils.hasEditPermissionOnNavigation();
  }

  public boolean hasEditPermissionOnPage() throws Exception {
    return Utils.hasEditPermissionOnPage();
  }

  public boolean isSpaceManager() throws Exception {
    if (spaceService != null) {
      UserNavigation currentUserNavigation = Util.getUIPortal().getSelectedUserNode().getNavigation();
      if (SiteType.GROUP.equals((currentUserNavigation.getKey().getType()))
          && currentUserNavigation.getKey().getName().contains("/spaces")) {
        String remoteUser = getUserId();
        String spaceId = currentUserNavigation.getKey().getName();
        Space space = spaceService.getSpaceByGroupId(spaceId);
        return (space != null && spaceService.hasSettingPermission(space, remoteUser));
      }
    }
    return true;
  }

  public void processRender(WebuiApplication app, WebuiRequestContext context) throws Exception {
    // A user could view the toolbar portlet if he has edit permission
    // either on 'active' page, 'active' portal or 'active' navigation
    boolean canAccessMenu = canAcceedMenu();
    if (canAccessMenu) {
      super.processRender(app, context);
    }
  }

  @Override
  public void processRender(WebuiRequestContext context) throws Exception {
    boolean canAccessMenu = canAcceedMenu();
    if (canAccessMenu) {
      super.processRender(context);
    }
  }

  public static UserPortal getUserPortal() {
    UserPortalConfig portalConfig = Util.getPortalRequestContext().getUserPortalConfig();
    return portalConfig.getUserPortal();
  }

  @Override
  public void renderChildren() throws Exception {
    List<UIComponent> list = getChildren();
    for (UIComponent child : list) {
      if (!(child instanceof UISEOToolbarForm) && child.isRendered()) {
        renderChild(child);
      }
    }
  }

  public String getUserId() {
    if (userId == null) {
      userId = Util.getPortalRequestContext().getRemoteUser();
    }
    return userId;
  }

  public String getUIPageId() {
    UIPortalApplication portalApp = Util.getUIPortalApplication();
    UIPage uiPage = portalApp.findFirstComponentOfType(UIPage.class);
    return uiPage.getId();
  }

  private boolean canAcceedMenu() throws Exception {
    UIPortalApplication portalApp = Util.getUIPortalApplication();
    UIPage uiPage = portalApp.findFirstComponentOfType(UIPage.class);
    boolean canAccessMenu = (!(uiPage instanceof UIDesktopPage))
        && (hasEditPermissionOnNavigation() || hasEditPermissionOnPage() || hasEditPermissionOnPortal());
    return canAccessMenu;
  }

  public static class ChangeEditingActionListener extends EventListener<UIAdminToolbarContainer> {

    /*
     * (non-Javadoc)
     * @see
     * org.exoplatform.webui.event.EventListener#execute(org.exoplatform
     * .webui.event.Event)
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
      UserNavigation edittedNavigation = Utils.getSelectedNavigation();

      WebuiRequestContext context = event.getRequestContext();
      UIApplication uiApplication = context.getUIApplication();

      if (edittedNavigation == null) {
        uiApplication.addMessage(new ApplicationMessage("UISiteManagement.msg.Invalid-editPermission", null));
        return;
      }

      UserACL userACL = uicomp.getApplicationComponent(UserACL.class);
      if (edittedNavigation.getKey().getType().equals(SiteType.PORTAL)) {
        String portalName = Util.getPortalRequestContext().getPortalOwner();
        UserPortalConfigService configService = uicomp.getApplicationComponent(UserPortalConfigService.class);
        UserPortalConfig userPortalConfig = configService.getUserPortalConfig(portalName, context.getRemoteUser(),
            PortalRequestContext.USER_PORTAL_CONTEXT);
        if (userPortalConfig == null) {
          uiApplication.addMessage(new ApplicationMessage("UISiteManagement.msg.portal-not-exist", new String[] { portalName }));
          return;
        }
        if (!userACL.hasEditPermission(userPortalConfig.getPortalConfig())) {
          uiApplication.addMessage(new ApplicationMessage("UISiteManagement.msg.Invalid-editPermission", null));
          return;
        }
      } else if (edittedNavigation.getKey().getType().equals(PortalConfig.GROUP_TYPE)) {
        if (!userACL.hasEditPermissionOnNavigation(SiteKey.group(edittedNavigation.getKey().getTypeName()))) {
          uiApplication.addMessage(new ApplicationMessage("UISiteManagement.msg.Invalid-editPermission", null));
          return;
        }
      }

      if (uicomp.naviManager == null) {
        uicomp.naviManager = uicomp.createUIComponent(UINavigationManagement.class, null, null);
      }
      Utils.createPopupWindow(uicomp, uicomp.naviManager, EDIT_NAVIGATION_POPUP_CONTAINER_ID, 400, -1, -1);

      uicomp.naviManager.setSiteKey(edittedNavigation.getKey());
      UserPortal userPortal = getUserPortal();
      UINavigationNodeSelector selector = uicomp.naviManager.getChild(UINavigationNodeSelector.class);
      selector.setEdittedNavigation(edittedNavigation);
      selector.setUserPortal(userPortal);
      selector.initTreeData();

      context.addUIComponentToUpdateByAjax(uicomp);
    }
  }

  static public class BackActionListener extends EventListener<UIPageNodeForm> {

    public void execute(Event<UIPageNodeForm> event) throws Exception {
      UIPageNodeForm uiPageNodeForm = event.getSource();
      UIAdminToolbarContainer uicomp = uiPageNodeForm.getAncestorOfType(UIAdminToolbarContainer.class);

      UINavigationNodeSelector selector = uicomp.naviManager.getChild(UINavigationNodeSelector.class);
      TreeNode selectedParent = (TreeNode) uiPageNodeForm.getSelectedParent();
      selector.selectNode(selectedParent);

      WebuiRequestContext context = event.getRequestContext();
      Utils.createPopupWindow(uicomp, uicomp.naviManager, EDIT_NAVIGATION_POPUP_CONTAINER_ID, 400, -1, -1);
      context.addUIComponentToUpdateByAjax(uicomp);

      TreeNode pageNode = uiPageNodeForm.getPageNode();
      if (pageNode != null) {
        selector.getUserNodeLabels().put(pageNode.getId(), pageNode.getI18nizedLabels());
      }
      selector.createEvent("NodeModified", Phase.PROCESS, context).broadcast();
    }

  }

}
