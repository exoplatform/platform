package org.exoplatform.navigation.webui.component;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.exoplatform.commons.utils.ObjectPageList;
import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.config.Query;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.webui.navigation.UIPageNavigationForm;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.core.UIPopupWindow;
import org.exoplatform.webui.core.UIRepeater;
import org.exoplatform.webui.core.UIVirtualList;
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
@ComponentConfigs( {
    @ComponentConfig(template = "app:/groovy/navigation/webui/component/UIAddGroupNavigation.gtmpl", events = {
        @EventConfig(listeners = UIAddGroupNavigation.CancelActionListener.class),
        @EventConfig(listeners = UIAddGroupNavigation.AddNavigationActionListener.class) }),
    @ComponentConfig(id = "UIAddGroupNavigationGrid", type = UIRepeater.class, template = "app:/groovy/navigation/webui/component/UIGroupGrid.gtmpl") })
public class UIAddGroupNavigation extends UIContainer {

  public UIAddGroupNavigation() throws Exception {
    UIVirtualList virtualList = addChild(UIVirtualList.class, null, "virtualGroupList");
    virtualList.setPageSize(6);
    UIRepeater repeater = createUIComponent(UIRepeater.class,
                                            "UIAddGroupNavigationGrid",
                                            virtualList.getGenerateId());
    virtualList.setUIComponent(repeater);
    UIPopupWindow editGroup = addChild(UIPopupWindow.class, null, "EditGroup");
  }

  public void loadGroups() throws Exception {

    PortalRequestContext pContext = Util.getPortalRequestContext();
    UserPortalConfigService dataService = getApplicationComponent(UserPortalConfigService.class);
    // get all group that user has permission
    List<String> listGroup = dataService.getMakableNavigations(pContext.getRemoteUser());

    List<PageNavigation> navigations = new ArrayList<PageNavigation>();
    UserACL userACL = getApplicationComponent(UserACL.class);
    DataStorage dataStorage = getApplicationComponent(DataStorage.class);
    // get all group navigation that user have edit permission
    Query<PageNavigation> query = new Query<PageNavigation>(PortalConfig.GROUP_TYPE,
                                                            null,
                                                            PageNavigation.class);
    // filter, only get group don't have navigation
    List<PageNavigation> navis = dataStorage.find(query).getAll();
    for (PageNavigation ele : navis) {
      if (listGroup.contains(ele.getOwnerId())) {
        listGroup.remove(ele.getOwnerId());
      }
    }

    if (listGroup == null)
      listGroup = new ArrayList<String>();
    UIVirtualList virtualList = getChild(UIVirtualList.class);
    virtualList.dataBind(new ObjectPageList<String>(listGroup, listGroup.size()));
  }

  static public class AddNavigationActionListener extends EventListener<UIAddGroupNavigation> {
    public void execute(Event<UIAddGroupNavigation> event) throws Exception {
      UIAddGroupNavigation uicomp = event.getSource();

      // get navigation id
      String ownerId = event.getRequestContext().getRequestParameter(OBJECTID);
      ownerId = URLDecoder.decode(ownerId);

      // open a add navigation popup
      UIPopupWindow popUp = uicomp.getChild(UIPopupWindow.class);
      UIPageNavigationForm pageNavigation = popUp.createUIComponent(UIPageNavigationForm.class,
                                                                    null,
                                                                    null,
                                                                    popUp);
      pageNavigation.setOwnerId(ownerId);
      pageNavigation.setOwnerType(PortalConfig.GROUP_TYPE);
      pageNavigation.addFormInput();
      popUp.setUIComponent(pageNavigation);
      popUp.setWindowSize(600, 400);
      popUp.setShow(true);

    }
  }

  static public class CancelActionListener extends EventListener<UIAddGroupNavigation> {
    public void execute(Event<UIAddGroupNavigation> event) throws Exception {
      UIAddGroupNavigation uicomp = event.getSource();
      UIGroupNavigationPortlet uiPortlet = (UIGroupNavigationPortlet) uicomp.getParent();
      UIGroupNavigationManagement uiGroupNav = uiPortlet.getChild(UIGroupNavigationManagement.class);

      // when click Cancel button, disable UI Add Group
      uicomp.setRendered(false);

      // when click Cancel button, enable UI List Group,
      uiGroupNav.setRendered(true);

    }
  }
}
