/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.component.debug;

import org.exoplatform.webui.component.UIApplication;
import org.exoplatform.webui.component.UIComponent;
import org.exoplatform.webui.component.UIPopupWindow;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SARL
 * Author : Nguyen My Ngoc
 *          ngoc.nguyen@exoplatform.com
 * Aug 25, 2006
 */
@ComponentConfig(
    template = "war:/groovy/webui/component/debug/UIComponentInfo.gtmpl",
    events = {
        @EventConfig(listeners = UIComponentInfo.TestActionListener.class)
      }
  )
public class UIComponentInfo extends UIComponent{
  
  static  public class TestActionListener extends EventListener<UIComponentInfo> {
    public void execute(Event<UIComponentInfo> event) throws Exception {
      UIComponentInfo uicomp = event.getSource() ;
      String clickUIComponentId  = event.getRequestContext().getRequestParameter(OBJECTID) ;
      UIApplication uiApp = event.getRequestContext().getUIApplication() ;
      UIComponent selectedUIComponent = uiApp.findComponentById(clickUIComponentId) ;
      UIApplicationTree uiAppTree = uicomp.getParent() ;
      uiAppTree.setSelectedUIComponent(selectedUIComponent) ;
      UIPopupWindow uiWindow = uicomp.getAncestorOfType(UIPopupWindow.class) ;
      if(uiWindow != null) uiWindow.setShow(true) ;
    }
  }
}
  

