/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
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
