/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.component;

import org.exoplatform.portal.webui.portal.PageNodeEvent;
import org.exoplatform.portal.webui.portal.UIPortal;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SARL
 * Author : Nguyen Ba Phu
 *          phului@gmail.com
 * Nov 7, 2007  
 */

@ComponentConfig (
    lifecycle = UIApplicationLifecycle.class, 
    template = "app:/groovy/portal/webui/component/UIHomePagePortlet.gtmpl",
    events = {
        @EventConfig(listeners = UIHomePagePortlet.LinkActionListener.class)        
      }
)

public class UIHomePagePortlet extends UIPortletApplication {

  public UIHomePagePortlet() throws Exception {

  }  
  
  static public class LinkActionListener extends EventListener<UIHomePagePortlet> {
    public void execute(Event<UIHomePagePortlet> event) throws Exception {      
      UIPortal uiPortal = Util.getUIPortal();
      String uri = "group::portal/admin::ecm";
      PageNodeEvent<UIPortal> pnevent ;
      pnevent = new PageNodeEvent<UIPortal>(uiPortal, PageNodeEvent.CHANGE_PAGE_NODE, null, uri) ;      
      uiPortal.broadcast(pnevent, Event.Phase.PROCESS) ;
    }
  }
}
