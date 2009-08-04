package org.exoplatform.navigation.webui.component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.exoplatform.commons.utils.ObjectPageList;
import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.config.Query;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.webui.navigation.UINavigationManagement;
import org.exoplatform.portal.webui.navigation.UINavigationNodeSelector;
import org.exoplatform.portal.webui.navigation.UIPageNavigationForm;
import org.exoplatform.portal.webui.page.UIPageNodeForm2;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.core.UIPopupWindow;
import org.exoplatform.webui.core.UIRepeater;
import org.exoplatform.webui.core.UIVirtualList;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;

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
        @EventConfig(listeners = UIGroupNavigationManagement.EditPropertiesActionListener.class),
        @EventConfig(listeners = UIGroupNavigationManagement.AddNavigationActionListener.class),
        @EventConfig(listeners = UIGroupNavigationManagement.DeleteNavigationActionListener.class, confirm = "UIGroupNavigationManagement.Delete.Confirm") }),
    @ComponentConfig(id = "UIGroupNavigationGrid", type = UIRepeater.class, template = "app:/groovy/navigation/webui/component/UINavigationGrid.gtmpl"),
    @ComponentConfig(  
                     type = UIPageNodeForm2.class,
                     lifecycle = UIFormLifecycle.class,
                     template = "system:/groovy/webui/form/UIFormTabPane.gtmpl" ,    
                     events = {
                       @EventConfig(listeners = UIPageNodeForm2.SaveActionListener.class ),
                       @EventConfig(listeners = UIGroupNavigationManagement.BackActionListener.class, phase = Phase.DECODE),
                       @EventConfig(listeners = UIPageNodeForm2.SwitchPublicationDateActionListener.class, phase = Phase.DECODE ),
                       @EventConfig(listeners = UIPageNodeForm2.ClearPageActionListener.class, phase = Phase.DECODE)
                     }
    )    
})
public class UIGroupNavigationManagement extends UIContainer {

  private List<PageNavigation> navigations;

  private PageNavigation       selectedNavigation;

  public UIGroupNavigationManagement() throws Exception {
    UIVirtualList virtualList = addChild(UIVirtualList.class, null, "virtualNavigationList");
    virtualList.setPageSize(4);
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
    List<PageNavigation> navis = dataStorage.find(query, new Comparator<PageNavigation>(){
      public int compare(PageNavigation pconfig1, PageNavigation pconfig2) {
        return pconfig1.getOwnerId().compareTo(pconfig2.getOwnerId());
      }
    }).getAll();
    for (PageNavigation ele : navis) {
      if (userACL.hasEditPermission(ele)) {
        navigations.add(ele);
      }
    }

    UIVirtualList virtualList = getChild(UIVirtualList.class);
    virtualList.dataBind(new ObjectPageList<PageNavigation>(navigations, navigations.size()));
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

  static public class EditPropertiesActionListener extends
                                                  EventListener<UIGroupNavigationManagement> {
    public void execute(Event<UIGroupNavigationManagement> event) throws Exception {

      UIGroupNavigationManagement uicomp = event.getSource();

      // get navigation id
      String id = event.getRequestContext().getRequestParameter(OBJECTID);
      Integer navId = Integer.parseInt(id);

      // get PageNavigation by navigation id
      PageNavigation navigation = uicomp.getNavigationById(navId);

      // open a add navigation popup
      UIPopupWindow popUp = uicomp.getChild(UIPopupWindow.class);
      UIPageNavigationForm pageNavigation = popUp.createUIComponent(UIPageNavigationForm.class,
                                                                    null,
                                                                    null,
                                                                    popUp);
      pageNavigation.setOwnerId(navigation.getOwnerId());
      pageNavigation.setOwnerType(navigation.getOwnerType());
      pageNavigation.setDescription(navigation.getDescription());
      pageNavigation.setPriority(String.valueOf(navigation.getPriority()));
      pageNavigation.addFormInput();
      pageNavigation.setPageNav(navigation);
      popUp.setUIComponent(pageNavigation);
      popUp.setWindowSize(600, 400);
      popUp.setShow(true);
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
  
  static public class BackActionListener extends EventListener<UIPageNodeForm2> {

    public void execute(Event<UIPageNodeForm2> event) throws Exception {
      UIPageNodeForm2 uiPageNodeForm = event.getSource();
      UIGroupNavigationManagement uiGroupNavigation = 
        uiPageNodeForm.getAncestorOfType(UIGroupNavigationManagement.class);
      PageNavigation selectedNavigation = uiGroupNavigation.getSelectedNavigation();
      UIPopupWindow uiNavigationPopup = uiGroupNavigation.getChild(UIPopupWindow.class);
      UINavigationManagement pageManager =
        uiPageNodeForm.createUIComponent(UINavigationManagement.class, null, null);
      pageManager.setOwner(selectedNavigation.getOwnerId());
      UINavigationNodeSelector selector = pageManager.getChild(UINavigationNodeSelector.class);
      ArrayList<PageNavigation> navis = new ArrayList<PageNavigation>();
      navis.add(selectedNavigation);
      selector.initNavigations(navis);
      uiNavigationPopup.setUIComponent(pageManager);
      uiNavigationPopup.setWindowSize(400, 400);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiNavigationPopup);
    }
    
  }
}
