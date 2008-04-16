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

import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;
/**
 * Author : lxchiati  
 *          lebienthuy@gmail.com
 * May 30, 2006
 */
@ComponentConfig(
  lifecycle = UIApplicationLifecycle.class,
  events = {
    @EventConfig(listeners = UIProcessEventPortlet.ProcessEventActionListener.class)
  }

)

public class UITestFormPortlet extends UIPortletApplication {
  
  public UITestFormPortlet() throws Exception {
//    addChild(UITestTemplate.class, null, null) ;
    addChild(UITestForm.class, null, null) ;
    //addChild(UITree.class, null,"UITree") ;
  }
}
