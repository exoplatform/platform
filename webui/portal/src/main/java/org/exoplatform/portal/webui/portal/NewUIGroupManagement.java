
package org.exoplatform.portal.webui.portal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIContainer;
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

@ComponentConfig(template = "app:/groovy/portal/webui/portal/NewUIGroupManagement.gtmpl", events = {
    @EventConfig(listeners = NewUIGroupManagement.EditNavigationActionListener.class),
    @EventConfig(listeners = NewUIGroupManagement.DeleteNavigationActionListener.class) })
public class NewUIGroupManagement extends UIContainer {

  private Collection           sibblingsGroup_;

  private List<PageNavigation> navigations;

  public NewUIGroupManagement() throws Exception {

    String username = org.exoplatform.portal.webui.util.Util.getPortalRequestContext().getRemoteUser();

    OrganizationService service = getApplicationComponent(OrganizationService.class);
    sibblingsGroup_ = service.getGroupHandler().findGroupsOfUser(username);

    // get all navigation
    navigations = new ArrayList<PageNavigation>();
    List<PageNavigation> pnavigations = getExistedNavigation(Util.getUIPortal().getNavigations());
    // loop throught all navigation
    for (PageNavigation nav : pnavigations) {
      // select only navigations that user has edit permission and type is group
      if (nav.isModifiable() && (nav.getOwnerType().equals(PortalConfig.GROUP_TYPE))) {
        navigations.add(nav);
      }
    }

  }

  public Collection getSibblingsGroup_() {
    return sibblingsGroup_;
  }

  public List<PageNavigation> getNavigations() {
    return navigations;
  }

  private List<PageNavigation> getExistedNavigation(List<PageNavigation> navis) throws Exception {
    Iterator<PageNavigation> itr = navis.iterator();
    UserPortalConfigService configService = getApplicationComponent(UserPortalConfigService.class);
    while (itr.hasNext()) {
      PageNavigation nav = itr.next();
      if (configService.getPageNavigation(nav.getOwnerType(), nav.getOwnerId()) == null)
        itr.remove();
    }
    return navis;
  }

  

  static public class EditNavigationActionListener extends EventListener<NewUIGroupManagement> {
    public void execute(Event<NewUIGroupManagement> event) throws Exception {

    }
  }

  static public class DeleteNavigationActionListener extends EventListener<NewUIGroupManagement> {
    public void execute(Event<NewUIGroupManagement> event) throws Exception {
      NewUIGroupManagement uiGroupManagement = event.getSource();
      // get navigation id
      String id = event.getRequestContext().getRequestParameter(OBJECTID);
      Integer navId = Integer.parseInt(id);
      // get PageNavigation by navigation id
      PageNavigation navigation = new PageNavigation();
      for (PageNavigation nav : uiGroupManagement.navigations) {
        if (nav.getId() == navId) {
          navigation = nav;
          break;
        }
      }

      // remove selected navigation
      if (uiGroupManagement.navigations == null || uiGroupManagement.navigations.size() < 1)
        return;
      uiGroupManagement.navigations.remove(navigation);

      UserPortalConfigService dataService = uiGroupManagement.getApplicationComponent(UserPortalConfigService.class);
      dataService.remove(navigation);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiGroupManagement);
    }
  }
}
