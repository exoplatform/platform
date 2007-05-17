/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.customization;

import org.exoplatform.webui.component.UIToolbar;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SARL
 * Author : LeBienThuy  
 *          lebienthuy@gmail.com
 * Mar 16, 2007  
 */
@ComponentConfig(
    template = "system:/groovy/webui/component/UIToolbar.gtmpl",
    events = {   
        @EventConfig(listeners = UIPageBrowseControlBar.BackActionListener.class)
    }
)

public class UIPageBrowseControlBar extends UIToolbar {

  public UIPageBrowseControlBar() throws Exception {
    setToolbarStyle("ControlToolbar") ;
    setJavascript("Preview","onClick='eXo.portal.UIPortal.switchMode(this);'") ;
  }

  static public class BackActionListener extends EventListener<UIPageBrowseControlBar> {
    public void execute(Event<UIPageBrowseControlBar> event) throws Exception {
      UIPageBrowseControlBar uiPageNav = event.getSource();
      UIPageManagement uiManagement = uiPageNav.getParent();
      System.out.println("\n\n\n--------------Back to page Browse");
//      UIPageEditBar uiPageEditBar = uiManagement.getChild(UIPageEditBar.class);
//      Class [] childrenToRender = null;
//      if(uiPageEditBar.isRendered()) {
//        childrenToRender = new Class[]{UIPageEditBar.class, UIPageNodeSelector.class, UIPageBrowseControlBar.class};
//      } else {
//        childrenToRender = new Class[]{UIPageNodeSelector.class, UIPageBrowseControlBar.class};
//      }
//      uiManagement.setRenderedChildrenOfTypes(childrenToRender);
//      event.getRequestContext().addUIComponentToUpdateByAjax(uiManagement);
    }
  }

}
