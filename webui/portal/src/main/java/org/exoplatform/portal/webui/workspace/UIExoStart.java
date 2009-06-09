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
package org.exoplatform.portal.webui.workspace;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.portal.account.UIAccountSetting;
import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.webui.UIWelcomeComponent;
import org.exoplatform.portal.webui.UIManagement.ManagementMode;
import org.exoplatform.portal.webui.navigation.PageNavigationUtils;
import org.exoplatform.portal.webui.navigation.UIPageManagement;
import org.exoplatform.portal.webui.page.UIPageCreationWizard;
import org.exoplatform.portal.webui.page.UIPageEditWizard;
import org.exoplatform.portal.webui.page.UIWizardPageCreationBar;
import org.exoplatform.portal.webui.page.UIWizardPageSetInfo;
import org.exoplatform.portal.webui.portal.PageNodeEvent;
import org.exoplatform.portal.webui.portal.UILanguageSelector;
import org.exoplatform.portal.webui.portal.UIPortal;
import org.exoplatform.portal.webui.portal.UIPortalManagement;
import org.exoplatform.portal.webui.portal.UIPortalSelector;
import org.exoplatform.portal.webui.portal.UISkinSelector;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIControlWorkspace.UIControlWSWorkingArea;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.InitParams;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.config.annotation.ParamConfig;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIComponentDecorator;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

@ComponentConfig(template = "system:/groovy/portal/webui/workspace/UIExoStart.gtmpl", initParams = { @ParamConfig(name = "UIExoStartPersonnalizationMenu", value = "system:/WEB-INF/conf/uiconf/portal/webui/workspace/UIExoStartPersonnalizationMenu.groovy") }, events = {
    @EventConfig(listeners = UIExoStart.BasicCustomizationActionListener.class),
    @EventConfig(listeners = UIExoStart.MyPortalActionListener.class),
    @EventConfig(listeners = UIExoStart.PageCreationWizardActionListener.class),
    @EventConfig(listeners = UIExoStart.EditCurrentPageActionListener.class),
    @EventConfig(listeners = UIExoStart.EditPageActionListener.class),
    @EventConfig(listeners = UIExoStart.BrowsePageActionListener.class),
    @EventConfig(listeners = UIExoStart.EditPortalActionListener.class),
    @EventConfig(listeners = UIExoStart.BrowsePortalActionListener.class),
    @EventConfig(listeners = UIExoStart.ManagePortalsActionListener.class),
    @EventConfig(listeners = UIExoStart.ChangePageActionListener.class),
    @EventConfig(listeners = UIExoStart.LoginActionListener.class),
    @EventConfig(listeners = UIExoStart.LanguageSettingsActionListener.class),
    @EventConfig(listeners = UIExoStart.SkinSettingsActionListener.class),
    @EventConfig(listeners = UIExoStart.ChangePortalActionListener.class),
    @EventConfig(listeners = UIExoStart.AccountSettingsActionListener.class) })
public class UIExoStart extends UIComponent {
  private List<List<MenuItemContainer>> menus = new ArrayList<List<MenuItemContainer>>(4);

  public UIExoStart(InitParams initParams) throws Exception {
    PortalRequestContext context = WebuiRequestContext.getCurrentInstance();
    if (context.getRemoteUser() != null) {
      List<MenuItemContainer> menu = initParams.getParam("UIExoStartPersonnalizationMenu")
                                               .getMapGroovyObject(context);
      menus.add(menu);
    }
  }

  public List<List<MenuItemContainer>> getMenus() {
    return menus;
  }

  public List<PageNavigation> getNavigations() throws Exception {
    List<PageNavigation> allNav = Util.getUIPortal().getNavigations();
    String removeUser = Util.getPortalRequestContext().getRemoteUser();
    List<PageNavigation> result = new ArrayList<PageNavigation>();
    for (PageNavigation nav : allNav) {
      result.add(PageNavigationUtils.filter(nav, removeUser));
    }
    return result;
  }

  static public class MenuItem {
    private String name;

    private String icon;

    public MenuItem(String name, String icon) {
      this.name = name;
      this.icon = icon;
    }

    public String getName() {
      return name;
    }

    public String getIcon() {
      return icon;
    }
  }

  static public class MenuItemContainer extends MenuItem {
    private List<MenuItem> children = new ArrayList<MenuItem>(5);

    public MenuItemContainer(String name) {
      super(name, name + "Icon");
    }

    public MenuItemContainer(String name, String icon) {
      super(name, icon);
    }

    public MenuItemContainer add(MenuItem item) {
      children.add(item);
      return this;
    }

    public List<MenuItem> getChildren() {
      return children;
    }
  }

  static public class MenuItemAction extends MenuItem {
    private String  action;

    private boolean useAjax = true;

    public MenuItemAction(String name) {
      super(name, name + "Icon");
      this.action = name;
    }

    public MenuItemAction(String name, boolean useAjax) {
      super(name, name + "Icon");
      this.useAjax = useAjax;
      this.action = name;
    }

    public MenuItemAction(String name, String icon) {
      super(name, icon);
      this.action = name;
    }

    public MenuItemAction(String name, String icon, String action) {
      super(name, icon);
      this.action = action;
    }

    public MenuItemAction(String name, String icon, String action, boolean useAjax) {
      super(name, icon);
      this.action = action;
      this.useAjax = useAjax;
    }

    public String getAction() {
      return action;
    }

    public boolean useAjax() {
      return useAjax;
    }
  }

  public <T extends UIComponent> void setUIControlWSWorkingComponent(Class<T> clazz) throws Exception {
    UIControlWorkspace uiControl = getAncestorOfType(UIControlWorkspace.class);
    UIControlWSWorkingArea uiWorking = uiControl.getChildById(UIControlWorkspace.WORKING_AREA_ID);
    uiWorking.setUIComponent(uiWorking.createUIComponent(clazz, null, null));
  }

  @SuppressWarnings("unchecked")
  public <T extends UIComponent> T getUIControlWSWorkingComponent() throws Exception {
    UIControlWorkspace uiControl = getAncestorOfType(UIControlWorkspace.class);
    UIControlWSWorkingArea uiWorking = uiControl.getChildById(UIControlWorkspace.WORKING_AREA_ID);
    return (T) uiWorking.getUIComponent();
  }

  static public class BasicCustomizationActionListener extends EventListener<UIExoStart> {
    public void execute(Event<UIExoStart> event) throws Exception {
      System.out.println("Call BasicCustomizationActionListener");
    }
  }

  static public class EditPageActionListener extends EventListener<UIExoStart> {
    public void execute(Event<UIExoStart> event) throws Exception {
      UIExoStart uiComp = event.getSource();
      uiComp.setUIControlWSWorkingComponent(UIPageManagement.class);
      PortalRequestContext pcontext = (PortalRequestContext) event.getRequestContext();
      ((UIPortalApplication) pcontext.getUIApplication()).setEditting(true);
      pcontext.addUIComponentToUpdateByAjax(uiComp);

      UIPageManagement uiManagement = uiComp.getUIControlWSWorkingComponent();
      uiManagement.setMode(ManagementMode.EDIT, event);
    }
  }

  static public class BrowsePageActionListener extends EventListener<UIExoStart> {
    public void execute(Event<UIExoStart> event) throws Exception {
      UIExoStart uiComp = event.getSource();
      uiComp.setUIControlWSWorkingComponent(UIPageManagement.class);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiComp);

      UIPageManagement uiManagement = uiComp.getUIControlWSWorkingComponent();
      uiManagement.setMode(ManagementMode.BROWSE, event);
    }
  }

  static public class EditPortalActionListener extends EventListener<UIExoStart> {
    public void execute(Event<UIExoStart> event) throws Exception {
      UIExoStart uiComp = event.getSource();
      UIPortal uiPortal = Util.getUIPortal();
      if (!uiPortal.isModifiable()) {
        UIPortalApplication uiPortalApp = event.getSource()
                                               .getAncestorOfType(UIPortalApplication.class);
        uiPortalApp.addMessage(new ApplicationMessage("UIPortalManagement.msg.Invalid-editPermission",
                                                      new String[] { uiPortal.getName() }));
        ;
        return;
      }
      uiComp.setUIControlWSWorkingComponent(UIPortalManagement.class);
      PortalRequestContext pcontext = (PortalRequestContext) event.getRequestContext();
      ((UIPortalApplication) pcontext.getUIApplication()).setEditting(true);
      UIPortalManagement uiManagement = uiComp.getUIControlWSWorkingComponent();
      uiManagement.setMode(ManagementMode.EDIT, event);
    }
  }

  static public class BrowsePortalActionListener extends EventListener<UIExoStart> {
    public void execute(Event<UIExoStart> event) throws Exception {
      UIExoStart uiComp = event.getSource();
      uiComp.setUIControlWSWorkingComponent(UIPortalManagement.class);

      UIPortalManagement uiManagement = uiComp.getUIControlWSWorkingComponent();
      uiManagement.setMode(ManagementMode.BROWSE, event);
    }
  }

  static public class ManagePortalsActionListener extends EventListener<UIExoStart> {
    public void execute(Event<UIExoStart> event) throws Exception {
      UIExoStart uiComp = event.getSource();
      //uiComp.setUIControlWSWorkingComponent(UIPortalManagement2.class);

      //UIPortalManagement2 uiManagement = uiComp.getUIControlWSWorkingComponent();
//      uiManagement.setId("UIPortalManagement");
//      uiManagement.setMode(ManagementMode.BROWSE, event);
    }
  }

  static public class MyPortalActionListener extends EventListener<UIExoStart> {
    public void execute(Event<UIExoStart> event) throws Exception {
      UIExoStart uicomp = event.getSource();
      uicomp.setUIControlWSWorkingComponent(UIWelcomeComponent.class);
      event.getRequestContext().addUIComponentToUpdateByAjax(uicomp.getParent());
    }
  }

  static public class PageCreationWizardActionListener extends EventListener<UIExoStart> {
    public void execute(Event<UIExoStart> event) throws Exception {
      UIExoStart uiExoStart = event.getSource();
      uiExoStart.setUIControlWSWorkingComponent(UIWizardPageCreationBar.class);
      UIPortalApplication uiApp = uiExoStart.getAncestorOfType(UIPortalApplication.class);
      uiApp.setEditting(true);
      UIWorkingWorkspace uiWorkingWS = uiApp.getChildById(UIPortalApplication.UI_WORKING_WS_ID);
      uiWorkingWS.setRenderedChild(UIPortalToolPanel.class);
      UIPortalToolPanel uiToolPanel = uiWorkingWS.getChild(UIPortalToolPanel.class);
      uiToolPanel.setShowMaskLayer(false);
      uiToolPanel.setWorkingComponent(UIPageCreationWizard.class, null);
      UIPageCreationWizard uiWizard = (UIPageCreationWizard) uiToolPanel.getUIComponent();
      UIWizardPageSetInfo uiPageSetInfo = uiWizard.getChild(UIWizardPageSetInfo.class);
      uiPageSetInfo.setShowPublicationDate(false);
      uiExoStart.setUIControlWSWorkingComponent(UIWelcomeComponent.class);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiWorkingWS);
      UIContainer uiParent = uiExoStart.getParent();
      UIComponentDecorator uiWorkingControl = uiParent.getChildById(UIControlWorkspace.WORKING_AREA_ID);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiWorkingControl);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiExoStart);
    }
  }

  static public class EditCurrentPageActionListener extends EventListener<UIExoStart> {
    public void execute(Event<UIExoStart> event) throws Exception {
      UIExoStart uiExoStart = event.getSource();
      UIPortalApplication uiApp = uiExoStart.getAncestorOfType(UIPortalApplication.class);
      uiApp.setEditting(true);
      UIWorkingWorkspace uiWorkingWS = uiApp.getChildById(UIPortalApplication.UI_WORKING_WS_ID);
      uiWorkingWS.setRenderedChild(UIPortalToolPanel.class);
      UIPortalToolPanel uiToolPanel = uiWorkingWS.getChild(UIPortalToolPanel.class);
      uiToolPanel.setShowMaskLayer(false);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiWorkingWS);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiExoStart.getParent());

      uiToolPanel.setWorkingComponent(UIPageEditWizard.class, null);
      UIPageEditWizard uiWizard = (UIPageEditWizard) uiToolPanel.getUIComponent();
      uiWizard.setDescriptionWizard(1);
      UIWizardPageSetInfo uiPageSetInfo = uiWizard.getChild(UIWizardPageSetInfo.class);
      uiPageSetInfo.setEditMode();
      uiPageSetInfo.createEvent("ChangeNode", Event.Phase.DECODE, event.getRequestContext())
                   .broadcast();
    }
  }

  static public class SkinSettingsActionListener extends EventListener<UIExoStart> {
    public void execute(Event<UIExoStart> event) throws Exception {
      UIPortal uiPortal = Util.getUIPortal();
      UIPortalApplication uiApp = uiPortal.getAncestorOfType(UIPortalApplication.class);
      UIMaskWorkspace uiMaskWS = uiApp.getChildById(UIPortalApplication.UI_MASK_WS_ID);

      UISkinSelector uiChangeSkin = uiMaskWS.createUIComponent(UISkinSelector.class, null, null);
      uiMaskWS.setUIComponent(uiChangeSkin);
      uiMaskWS.setWindowSize(640, 400);
      uiMaskWS.setShow(true);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiMaskWS);
    }
  }

  static public class LanguageSettingsActionListener extends EventListener<UIExoStart> {
    public void execute(Event<UIExoStart> event) throws Exception {
      UIPortal uiPortal = Util.getUIPortal();
      UIPortalApplication uiApp = uiPortal.getAncestorOfType(UIPortalApplication.class);
      UIMaskWorkspace uiMaskWS = uiApp.getChildById(UIPortalApplication.UI_MASK_WS_ID);

      UILanguageSelector languageForm = uiMaskWS.createUIComponent(UILanguageSelector.class);
      uiMaskWS.setUIComponent(languageForm);
      uiMaskWS.setWindowSize(640, 400);
      uiMaskWS.setShow(true);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiMaskWS);
      // Util.updateUIApplication(event);
    }
  }

  static public class ChangePageActionListener extends EventListener<UIExoStart> {
    public void execute(Event<UIExoStart> event) throws Exception {
      String uri = event.getRequestContext().getRequestParameter(OBJECTID);
      UIPortal uiPortal = Util.getUIPortal();
      uiPortal.setMode(UIPortal.COMPONENT_VIEW_MODE);
      // UIPageBody uiPageBody =
      // uiPortal.findFirstComponentOfType(UIPageBody.class);
      // if(uiPageBody != null) {
      // if(uiPageBody.getMaximizedUIComponent() != null) {
      // UIPortlet currentPortlet = (UIPortlet)
      // uiPageBody.getMaximizedUIComponent();
      // currentPortlet.setCurrentWindowState(WindowState.NORMAL);
      // uiPageBody.setMaximizedUIComponent(null);
      // }
      // }
      PageNodeEvent<UIPortal> pnevent = new PageNodeEvent<UIPortal>(uiPortal,
                                                                    PageNodeEvent.CHANGE_PAGE_NODE,
                                                                    uri);
      uiPortal.broadcast(pnevent, Event.Phase.PROCESS);
    }
  }

  static public class LoginActionListener extends EventListener<UIExoStart> {
    public void execute(Event<UIExoStart> event) throws Exception {
      UIExoStart uicomp = event.getSource();
      uicomp.setUIControlWSWorkingComponent(UIWelcomeComponent.class);
    }
  }

  static public class ChangePortalActionListener extends EventListener<UIExoStart> {
    public void execute(Event<UIExoStart> event) throws Exception {
      UIPortal uiPortal = Util.getUIPortal();
      UIPortalApplication uiApp = uiPortal.getAncestorOfType(UIPortalApplication.class);
      UIMaskWorkspace uiMaskWS = uiApp.getChildById(UIPortalApplication.UI_MASK_WS_ID);

      UIPortalSelector uiPortalSelector = uiMaskWS.createUIComponent(UIPortalSelector.class,
                                                                     null,
                                                                     null);
      uiMaskWS.setUIComponent(uiPortalSelector);
      uiMaskWS.setShow(true);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiMaskWS);
    }
  }

  // todo: tungnd - 08/09/07
  static public class AccountSettingsActionListener extends EventListener<UIExoStart> {
    public void execute(Event<UIExoStart> event) throws Exception {
      UIPortal uiPortal = Util.getUIPortal();
      UIPortalApplication uiApp = uiPortal.getAncestorOfType(UIPortalApplication.class);
      UIMaskWorkspace uiMaskWS = uiApp.getChildById(UIPortalApplication.UI_MASK_WS_ID);

      UIAccountSetting uiAccountForm = uiMaskWS.createUIComponent(UIAccountSetting.class,
                                                                  null,
                                                                  null);
      uiMaskWS.setUIComponent(uiAccountForm);
      uiMaskWS.setShow(true);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiMaskWS);
    }
  }
}
