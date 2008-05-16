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

import org.exoplatform.portal.webui.application.UIGadget;
import org.exoplatform.portal.webui.application.UIGadgetLifecycle;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;
/**
 * Created by The eXo Platform SARL
 * Author : dang.tung
 *          tungcnw@gmail.com
 * April 05, 2008          
 */

@ComponentConfigs({
    @ComponentConfig(
        lifecycle = UIApplicationLifecycle.class
    ),
    @ComponentConfig(
        type = UIGadget.class,
        id="UIGadget",
        lifecycle = UIGadgetLifecycle.class,
        template = "system:/groovy/portal/webui/application/UIGadget.gtmpl"
    )
  }
)
public class UITestGadgetPortlet extends UIPortletApplication {

  public UITestGadgetPortlet() throws Exception {
    UIGadget uiGadget1 = addChild(UIGadget.class, "UIGadget", "UIGadget1") ;
    uiGadget1.setUrl("http://www.labpixies.com/campaigns/todo/todo.xml") ;
    UIGadget uiGadget2 = addChild(UIGadget.class, "UIGadget", "UIGadget2") ;
    uiGadget2.setUrl("http://www.google.com/ig/modules/horoscope.xml") ;
  }
}
