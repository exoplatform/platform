package org.exoplatform.navigation.webui.component;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.config.Query;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.core.UIPopupWindow;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

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

@ComponentConfig(template = "app:/groovy/navigation/webui/component/NewUIGroupManagement.gtmpl", events = {
    @EventConfig(listeners = NewUIGroupManagement.EditNavigationActionListener.class),
    @EventConfig(listeners = NewUIGroupManagement.DeleteNavigationActionListener.class, confirm = "them sau") })
public class NewUIGroupManagement extends UIContainer {

  private List<PageNavigation> navigations;

  public NewUIGroupManagement() throws Exception {
    UIPopupWindow editNavigation = addChild(UIPopupWindow.class, null, "EditGroupNavigation");
    editNavigation.setWindowSize(400, 400);
    loadNavigations();
  }

  public void loadNavigations() throws Exception {
    navigations = new ArrayList<PageNavigation>();
    UserACL userACL = getApplicationComponent(UserACL.class);
    DataStorage dataStorage = getApplicationComponent(DataStorage.class);
    Query<PageNavigation> query = new Query<PageNavigation>(PortalConfig.GROUP_TYPE,
                                                            null,
                                                            PageNavigation.class);
    List<PageNavigation> navis = dataStorage.find(query).getAll();
    for (PageNavigation ele : navis) {
      if (userACL.hasEditPermission(ele)) {
        navigations.add(ele);
      }
    }
  }

  public List<PageNavigation> getNavigations() {
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

  static public class EditNavigationActionListener extends EventListener<NewUIGroupManagement> {
    public void execute(Event<NewUIGroupManagement> event) throws Exception {

      NewUIGroupManagement uicomp = event.getSource();

      // get navigation id
      String id = event.getRequestContext().getRequestParameter(OBJECTID);
      Integer navId = Integer.parseInt(id);
      // get PageNavigation by navigation id
      PageNavigation navigation = uicomp.getNavigationById(navId);
      WebuiRequestContext context = event.getRequestContext();
      UIApplication uiApplication = context.getUIApplication();

      // check edit permission, ensure that user has edit permission on that navigation
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

      //UIWorkingWorkspace workingWS = uiPortalApp.getChildById(UIPortalApplication.UI_WORKING_WS_ID);
      //UIPopupWindow popUp = workingWS.getChild(UIPopupWindow.class);
      
      UIPopupWindow popUp = uicomp.getChild(UIPopupWindow.class);
      //popUp = workingWS.addChild(UIPopupWindow.class, null, null);

      UINavigationManagement pageManager = popUp.createUIComponent(UINavigationManagement.class,
                                                                   null,
                                                                   null,
                                                                   popUp);
      pageManager.setOwner(navigation.getOwnerId());
      UINavigationNodeSelector selector = pageManager.getChild(UINavigationNodeSelector.class);
      selector.loadNavigationByNavId(navId, uicomp.navigations);
      popUp.setUIComponent(pageManager);
      popUp.setShow(true);
      //prContext.addUIComponentToUpdateByAjax(workingWS);
    }
  }

  static public class DeleteNavigationActionListener extends EventListener<NewUIGroupManagement> {
    public void execute(Event<NewUIGroupManagement> event) throws Exception {
      NewUIGroupManagement uicomp = event.getSource();
      
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
}
