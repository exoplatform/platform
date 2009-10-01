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
package org.exoplatform.samples.lazytabpane;

import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UILazyTabPane;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;

/**
 * Created by The eXo Platform SAS Author : tam.nguyen tamndrok@gmail.com Aug 2, 2009
 */
@ComponentConfig(lifecycle = UIApplicationLifecycle.class)
public class UILazyTabPanePortlet extends UIPortletApplication {

  public UILazyTabPanePortlet() throws Exception {
    UILazyTabPane uiLazyTabPane = addChild(UILazyTabPane.class, null, null);
    uiLazyTabPane.addChild(UILazyTabPaneInputSet1.class, null, null).setRendered(true);
    uiLazyTabPane.addChild(UILazyTabPaneInputSet2.class, null, null);
    uiLazyTabPane.setSelectedTab(1);
  }
}
