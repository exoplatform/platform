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
package org.exoplatform.webui.core;

import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SARL
 * Author : tung.dang
 *          tungcnw@gmail.com
 * Feb, 12, 2008
 */  
@ComponentConfig (
    template =  "system:/groovy/webui/core/UITabPane_New.gtmpl",
    events = {@EventConfig(listeners = UITabPane.SelectTabActionListener.class)}
)
public class UITabPane extends UIContainer {
  private static String selectedTabId = "";
  
  public String getSelectedTabId() { return selectedTabId; }
  public void setSelectedTab(String renderTabId) { selectedTabId = renderTabId; }
  public void setSelectedTab(int index) { selectedTabId = ((UIComponent)getChild(index-1)).getId();}
		
  static  public class SelectTabActionListener extends EventListener<UITabPane> {
	  public void execute(Event<UITabPane> event) throws Exception {
	      WebuiRequestContext context = event.getRequestContext();
	      String renderTab = context.getRequestParameter(UIComponent.OBJECTID) ;
	      if(renderTab == null) return;
	      selectedTabId = renderTab ;
        context.setResponseComplete(true);
	    }
	  }
}