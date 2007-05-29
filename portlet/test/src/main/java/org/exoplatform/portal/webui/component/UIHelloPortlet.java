/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.component;

import org.exoplatform.webui.component.UIContainer;
import org.exoplatform.webui.component.UIPopupWindow;
import org.exoplatform.webui.component.UIPortletApplication;
import org.exoplatform.webui.component.lifecycle.UIApplicationLifecycle;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SARL
 * Author : Philippe Aristote
 *          philippe.aristote@gmail.com
 * May 23, 2007
 */
@ComponentConfigs({
    @ComponentConfig(
      lifecycle = UIApplicationLifecycle.class,
      template = "app:/groovy/webui/component/UIHelloPortlet.gtmpl",
      events = {
        @EventConfig(listeners = UIHelloPortlet.OpenPopupActionListener.class)
      }
    ),
    @ComponentConfig(
        type = UIContainer.class,
        id = "UIHelloContent",
        template = "app:/groovy/webui/component/UIHelloContent.gtmpl"
    )
})
public class UIHelloPortlet extends UIPortletApplication {

  public UIHelloPortlet() throws Exception {
    UIContainer uiContainer = createUIComponent(UIContainer.class, "UIHelloContent", null); 
    uiContainer.addChild(UIHelloSelector.class, null, null);
    uiContainer.addChild(UIHelloWelcome.class, null, null);
    uiContainer.addChild(UIHelloForm.class, null, null).setRendered(false);
    addChild(uiContainer);
    UIPopupWindow popup = addChild(UIPopupWindow.class, null, null);
    popup.setWindowSize(400, 300);
    UIHelloForm form = createUIComponent(UIHelloForm.class, null, null);
    popup.setUIComponent(form);
    popup.setRendered(false);
  }
  
  static public class OpenPopupActionListener extends EventListener<UIHelloPortlet> {
    public void execute(Event<UIHelloPortlet> event) throws Exception {
      UIHelloPortlet portlet = event.getSource();
      UIPopupWindow popup = portlet.getChild(UIPopupWindow.class);
//      UIHelloForm form = portlet.getChild(UIContainer.class).getChild(UIHelloForm.class);
//      popup.setUIComponent(form);
//      form.setRendered(true);
      popup.setRendered(true);
      popup.setShow(true);
    }
  }
}