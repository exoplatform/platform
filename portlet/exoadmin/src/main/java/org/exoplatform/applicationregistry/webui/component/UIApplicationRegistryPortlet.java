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
package org.exoplatform.applicationregistry.webui.component;

import org.exoplatform.portal.webui.portal.UIPortalComponentActionListener.ViewChildActionListener;
import org.exoplatform.webui.application.WebuiApplication;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;

@ComponentConfig(
    lifecycle = UIApplicationLifecycle.class,
    template = "app:/groovy/applicationregistry/webui/component/UIApplicationRegistryPortlet.gtmpl",
    events = {
      @EventConfig(listeners = ViewChildActionListener.class)
    }
)

public class UIApplicationRegistryPortlet extends UIPortletApplication {
  
  public UIApplicationRegistryPortlet() throws Exception{
    addChild(UIApplicationOrganizer.class, null, null).setRendered(true) ; 
    addChild(UIPortletManagement.class, null, null).setRendered(false) ;
    addChild(UIGadgetManagement.class, null, null).setRendered(false) ;
  }
  
  @Override
public void processRender(WebuiApplication app, WebuiRequestContext context) throws Exception {
	// TODO Auto-generated method stub
	super.processRender(app, context);
}
}