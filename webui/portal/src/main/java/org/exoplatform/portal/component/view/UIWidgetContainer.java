/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.view;

import java.util.Iterator;
import java.util.List;

import org.exoplatform.portal.component.widget.UIWelcomeComponent;
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
  template = "system:/groovy/portal/webui/component/view/UIWidgetContainer.gtmpl",
  events = {
      @EventConfig(listeners = UIWidgetContainer.DeleteWidgetActionListener.class),
      @EventConfig(listeners = UIWidgetContainer.AddApplicationActionListener.class)
  }
)
public class UIWidgetContainer extends UIContainer {
  
  public UIWidgetContainer() throws Exception {
    
  }
  
  static public class DeleteWidgetActionListener extends EventListener<UIWidgetContainer> {
    public void execute(Event<UIWidgetContainer> event) throws Exception {
      String id  = event.getRequestContext().getRequestParameter(OBJECTID);
      UIWidgetContainer uiWidgetContainer = event.getSource();
      
      List<UIComponent> children = uiWidgetContainer.getChildren();
      Iterator<UIComponent> iter = children.iterator();
      while(iter.hasNext()) {
        UIWidget uiWidget = (UIWidget) iter.next();
        if(uiWidget.getApplicationId().equals(id)) {
          iter.remove();
          break;
        }
      }
      
      UIWelcomeComponent uiWelcomeComponent = uiWidgetContainer.getAncestorOfType(UIWelcomeComponent.class);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiWelcomeComponent);
    }
  }
  
  static public class AddApplicationActionListener  extends EventListener<UIWidgetContainer> {
    public void execute(Event<UIWidgetContainer> event) throws Exception {
      System.out.println("\n\n\n == > da call vao day \n\n\n");
    }
  }
}
