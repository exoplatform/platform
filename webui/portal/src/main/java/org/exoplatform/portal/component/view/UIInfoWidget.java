/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.view;

import org.exoplatform.webui.component.UIComponent;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SARL
 * Author : Pham Dung Ha
 *          ha.pham@exoplatform.com
 * May 16, 2007  
 */

@ComponentConfig(
  template = "system:/groovy/portal/webui/component/view/UIInfoWidget.gtmpl",
  events = {
      @EventConfig(listeners = UIInfoWidget.EditActionListener.class )
  }
)

public class UIInfoWidget extends UIComponent {
  
  static  public class EditActionListener extends EventListener {
    public void execute(Event event) throws Exception {
      System.out.println("\n\n\n\n==============================Edit UIInfoWidget!!!");
    }
  }
}
