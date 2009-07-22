package org.exoplatform.navigation.webui.component;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.commons.utils.LazyPageList;
import org.exoplatform.commons.utils.PageList;
import org.exoplatform.i18n.webui.component.UII18nPortlet;
import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.config.Query;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.bean.UIDataFeed;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.core.UIPopupWindow;
import org.exoplatform.webui.core.UIRepeater;
import org.exoplatform.webui.core.UIVirtualList;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.form.UIForm;

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

/*
 * Created by The eXo Platform SAS
 * Author : tam.nguyen
 *          tamndrok@gmail.com
 * May 28, 2009  
 */
@ComponentConfigs( {
    @ComponentConfig(template = "app:/groovy/navigation/webui/component/UIGroupNavigationManagement.gtmpl", events = {
        @EventConfig(listeners = UIGroupNavigationManagement.EditNavigationActionListener.class),
        @EventConfig(listeners = UIGroupNavigationManagement.AddNavigationActionListener.class),
        @EventConfig(listeners = UIGroupNavigationManagement.DeleteNavigationActionListener.class, confirm = "UIGroupNavigationManagement.Delete.Confirm") }),
    @ComponentConfig(id = "UIGroupNavigationGrid", type = UIRepeater.class, template = "app:/groovy/navigation/webui/component/UINavigationGrid.gtmpl") })
public class UIGroupNavigationManagement extends UIContainer {

  private List<PageNavigation> navigations;

  private PageNavigation       selectedNavigation;

  public UIGroupNavigationManagement() throws Exception {
    UIVirtualList virtualList = addChild(UIVirtualList.class, null, null);
    virtualList.setPageSize(3);
    UIRepeater repeater = createUIComponent(UIRepeater.class,
                                              "UIGroupNavigationGrid",
                                              virtualList.getGenerateId());
    virtualList.setUIComponent(repeater);
    UIPopupWindow editNavigation = addChild(UIPopupWindow.class, null, "EditGroupNavigation");
  }

  public void loadNavigations() throws Exception {
    navigations = new ArrayList<PageNavigation>();
    UserACL userACL = getApplicationComponent(UserACL.class);
    DataStorage dataStorage = getApplicationComponent(DataStorage.class);
    // load all navigation that user has edit permission
    Query<PageNavigation> query = new Query<PageNavigation>(PortalConfig.GROUP_TYPE,
                                                            null,
                                                            PageNavigation.class);
    LazyPageList<PageNavigation> navis = dataStorage.find(query);    
    /*
     * List<PageNavigation> removeNavis = new ArrayList<PageNavigation>(); for
     * (PageNavigation ele : navis.getAll()) { if
     * (!userACL.hasEditPermission(ele)) { removeNavis.add(ele); } } for
     * (PageNavigation pageNavigation : removeNavis) {
     * navis.getAll().remove(pageNavigation); }
     */
    UIVirtualList virtualList = getChild(UIVirtualList.class);
    virtualList.dataBind(navis);
  }

  public List<PageNavigation> getNavigations() {
    return navigations;
  }

  public void addPageNavigation(PageNavigation navigation) {
    if (navigations == null)
      navigations = new ArrayList<PageNavigation>();
    navigations.add(navigation);
  }

  public void deletePageNavigation(PageNavigation navigation) {
    if (navigations == null || navigations.size() < 1)
      return;
    navigations.remove(navigation);
  }

  public PageNavigation getPageNavigation(int id) {
    for (PageNavigation ele : getPageNavigations()) {
      if (ele.getId() == id)
        return ele;
    }
    return null;
  }

  public List<PageNavigation> getPageNavigations() {
    if (navigations == null)
      navigations = new ArrayList<PageNavigation>();
    return navigations;
  }

  public PageNavigation getNavigationById(Integer navId) {
    PageNavigation navigation = new PageNavigation();
    for (PageNavigation nav : navigations) {
      if (nav.getId() == navId) {
        navigation = nav;
        break;
      }
    }
    return navigation;
  }

  public PageNavigation getSelectedNavigation() {
    return selectedNavigation;
  }

  public void setSelectedNavigation(PageNavigation navigation) {
    selectedNavigation = navigation;
  }

  static public class EditNavigationActionListener extends
                                                  EventListener<UIGroupNavigationManagement> {
    public void execute(Event<UIGroupNavigationManagement> event) throws Exception {

      UIGroupNavigationManagement uicomp = event.getSource();

      // get navigation id
      String id = event.getRequestContext().getRequestParameter(OBJECTID);
      Integer navId = Integer.parseInt(id);
      // get PageNavigation by navigation id
      PageNavigation navigation = uicomp.getNavigationById(navId);
      uicomp.setSelectedNavigation(navigation);
      WebuiRequestContext context = event.getRequestContext();
      UIApplication uiApplication = context.getUIApplication();

      // check edit permission, ensure that user has edit permission on that
      // navigation
      UserACL userACL = uicomp.getApplicationComponent(UserACL.class);

      if (!userACL.hasEditPermission(navigation)) {
        uiApplication.addMessage(new ApplicationMessage("UIDashboard.msg.notUrl", null));
        return;
      }

      // ensure this navigation is exist
      DataStorage service = uicomp.getApplicationComponent(DataStorage.class);
      if (service.getPageNavigation(navigation.getOwnerType(), navigation.getOwnerId()) == null) {
        uiApplication.addMessage(new ApplicationMessage("UIDashboard.msg.notUrl", null));
        return;
      }

      UIPopupWindow popUp = uicomp.getChild(UIPopupWindow.class);

      UINavigationManagement pageManager = popUp.createUIComponent(UINavigationManagement.class,
                                                                   null,
                                                                   null,
                                                                   popUp);
      pageManager.setOwner(navigation.getOwnerId());
      UINavigationNodeSelector selector = pageManager.getChild(UINavigationNodeSelector.class);
      selector.loadNavigationByNavId(navId, uicomp.navigations);
      popUp.setUIComponent(pageManager);
      popUp.setWindowSize(400, 400);
      popUp.setShow(true);
      // prContext.addUIComponentToUpdateByAjax(workingWS);
    }
  }

  static public class DeleteNavigationActionListener extends
                                                    EventListener<UIGroupNavigationManagement> {
    public void execute(Event<UIGroupNavigationManagement> event) throws Exception {
      UIGroupNavigationManagement uicomp = event.getSource();

      WebuiRequestContext context = event.getRequestContext();
      UIApplication uiApplication = context.getUIApplication();

      // get navigation id
      String id = event.getRequestContext().getRequestParameter(OBJECTID);
      Integer navId = Integer.parseInt(id);

      // get PageNavigation by navigation id
      PageNavigation navigation = uicomp.getNavigationById(navId);

      // check edit permission, ensure that user has edit permission on that
      // navigation
      UserACL userACL = uicomp.getApplicationComponent(UserACL.class);

      if (!userACL.hasEditPermission(navigation)) {
        uiApplication.addMessage(new ApplicationMessage("UIDashboard.msg.notUrl", null));
        return;
      }

      // TODO ensure this navigation is exist
      DataStorage service = uicomp.getApplicationComponent(DataStorage.class);
      if (service.getPageNavigation(navigation.getOwnerType(), navigation.getOwnerId()) == null) {
        uiApplication.addMessage(new ApplicationMessage("UIDashboard.msg.notUrl", null));
        return;
      }

      // remove selected navigation
      if (uicomp.navigations == null || uicomp.navigations.size() < 1)
        return;
      uicomp.navigations.remove(navigation);

      UserPortalConfigService dataService = uicomp.getApplicationComponent(UserPortalConfigService.class);
      dataService.remove(navigation);
      event.getRequestContext().addUIComponentToUpdateByAjax(uicomp);
    }
  }

  static public class AddNavigationActionListener extends
                                                 EventListener<UIGroupNavigationManagement> {
    public void execute(Event<UIGroupNavigationManagement> event) throws Exception {
      UIGroupNavigationManagement uicomp = event.getSource();
      UIGroupNavigationPortlet uiPortlet = (UIGroupNavigationPortlet) uicomp.getParent();
      UIAddGroupNavigation uiAddGroupNav = uiPortlet.getChild(UIAddGroupNavigation.class);
      // when click Add Group button, hidden UI List Group,
      uicomp.setRendered(false);
      // when click Add Group button, enable UI Add Group
      uiAddGroupNav.setRendered(true);

    }
  }
}
