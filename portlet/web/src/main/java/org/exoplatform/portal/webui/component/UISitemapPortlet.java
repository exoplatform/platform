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
package org.exoplatform.portal.webui.component;

import org.exoplatform.portal.webui.navigation.UIPortalNavigation;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;

/**
 * Created by The eXo Platform SARL
 * Author : Dang Van Minh
 *          minhdv81@yahoo.com
 * Jul 3, 2006  
 */
@ComponentConfigs({
  @ComponentConfig(
    lifecycle = UIApplicationLifecycle.class
  ),
  @ComponentConfig(
    type = UIPortalNavigation.class,
    id = "UISiteMap",
    template = "system:/groovy/webui/core/UISitemap.gtmpl" ,
    events = @EventConfig(listeners = UIPortalNavigation.SelectNodeActionListener.class)
  )
})
public class UISitemapPortlet extends UIPortletApplication {
  
  public UISitemapPortlet() throws Exception {
    addChild(UIPortalNavigation.class, "UISiteMap", null) ;
  }

}
