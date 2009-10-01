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
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SARL
 * Author : Philippe Aristote
 *          philippe.aristote@gmail.com
 * May 25, 2007  
 */
@ComponentConfig(
    template = "app:/groovy/webui/component/UIHelloSelector.gtmpl",
    events = {
        @EventConfig(listeners = UIHelloSelector.OpenFormActionListener.class),
        @EventConfig(listeners = UIHelloSelector.OpenWelcomeActionListener.class)
    }
)
public class UIHelloSelector extends UIComponent {

  public UIHelloSelector() throws Exception {
  }
  
  static public class OpenFormActionListener extends EventListener<UIHelloSelector> {
    public void execute(Event<UIHelloSelector> event) throws Exception {
      UIHelloSelector selector = event.getSource();
      UIHelloPortlet portlet = selector.getAncestorOfType(UIHelloPortlet.class);
      UIHelloForm form = portlet.getChild(UIContainer.class).getChild(UIHelloForm.class);
      form.setRendered(true);
      UIHelloWelcome welcome = portlet.getChild(UIContainer.class).getChild(UIHelloWelcome.class);
      welcome.setRendered(false);
    }
  }
  
  static public class OpenWelcomeActionListener extends EventListener<UIHelloSelector> {
    public void execute(Event<UIHelloSelector> event) throws Exception {
      UIHelloSelector selector = event.getSource();
      UIHelloPortlet portlet = selector.getAncestorOfType(UIHelloPortlet.class);
      UIHelloWelcome welcome = portlet.getChild(UIContainer.class).getChild(UIHelloWelcome.class);
      welcome.setRendered(true);
      UIHelloForm form = portlet.getChild(UIContainer.class).getChild(UIHelloForm.class);
      form.setRendered(false);
    }
  }
  
}
