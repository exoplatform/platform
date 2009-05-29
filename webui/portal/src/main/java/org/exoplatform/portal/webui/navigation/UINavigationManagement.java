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
package org.exoplatform.portal.webui.navigation;

import org.exoplatform.commons.utils.PageList;
import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.config.Query;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.core.UITree;
import org.exoplatform.webui.event.Event;

@ComponentConfig(
  template = "app:/groovy/portal/webui/navigation/UINavigationManagement.gtmpl"
)
public class UINavigationManagement extends UIContainer {
  
  @SuppressWarnings("unused")
  public UINavigationManagement() throws Exception {    
    addChild(UINavigationNodeSelector.class, null, null);
    addChild(UINavigationControlBar.class, null, null);    
  }
  
  public void loadNavigation(Query<PageNavigation> query) throws Exception {
    DataStorage service = getApplicationComponent(DataStorage.class);
    PageList navis = service.find(query);
    UINavigationNodeSelector nodeSelector = getChild(UINavigationNodeSelector.class);
    nodeSelector.initNavigations(navis.currentPage());
  }
  
  public <T extends UIComponent> T setRendered(boolean b) {
    return super.<T> setRendered(b);
  }

  public void loadView(Event<? extends UIComponent> event) throws Exception {
    UINavigationNodeSelector uiNodeSelector = getChild(UINavigationNodeSelector.class);
    UITree uiTree = uiNodeSelector.getChild(UITree.class);
    uiTree.createEvent("ChangeNode", event.getExecutionPhase(), event.getRequestContext()).broadcast();
  }
}