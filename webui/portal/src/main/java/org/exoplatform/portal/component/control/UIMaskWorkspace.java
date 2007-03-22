/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.control;

import javax.faces.component.UIOutput;

import org.exoplatform.webui.application.RequestContext;
import org.exoplatform.webui.component.UIComponent;
import org.exoplatform.webui.component.UIComponentDecorator;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Mar 13, 2007  
 */
@ComponentConfig(  
    id = "UIMaskWorkspace",
    template =  "app:/groovy/portal/webui/component/control/UIMaskWorkspace.gtmpl",
    events = @EventConfig(listeners = UIMaskWorkspace.CloseActionListener.class)  
)
public class UIMaskWorkspace extends UIComponentDecorator {  
  
  private int width_  = -1 ;
  private int height_ =  -1 ;
  private boolean isShow = false ;
  
  public int getWindowWidth() { return width_ ; }
  
  public int getWindowHeight() { return height_ ; }
  
  public void setWindowSize(int w, int h) {
    width_ = w ;
    height_ = h ;
  }
  
  public void processRender(RequestContext context) throws Exception {
    super.processRender(context);
    context.addJavascript("eXo.portal.UIMaskWorkspace.init('"+getId()+"', "+isShow+");");
  }

  public boolean isShow() { return isShow;}
  
  public void setShow(boolean isShow) { 
    this.isShow = isShow;
  }
  
  public void setUIComponent(UIComponent uicomponent) { 
    super.setUIComponent(uicomponent);
    this.isShow = (uicomponent != null);
  }
  
  static  public class CloseActionListener extends EventListener<UIComponent> {
    public void execute(Event<UIComponent> event) throws Exception {
      UIComponent uiSource = event.getSource();
      UIMaskWorkspace uiMaskWorkspace = uiSource.getAncestorOfType(UIMaskWorkspace.class);
      uiMaskWorkspace.setShow(false);
      uiMaskWorkspace.setUIComponent(null);
      RequestContext context =  event.getRequestContext() ;
      context.addUIComponentToUpdateByAjax(uiMaskWorkspace) ;
    }
  }
}
