/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.component;

import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SARL
 * Author : Tran The Trong
 *          trong.tran@exoplatform.com
 * @version: $Id$
 * Aug 11, 2006  
 */
@ComponentConfig(  
  template =  "system:/groovy/webui/component/UIPopupWindow.gtmpl",
  events = @EventConfig(listeners = UIPopupWindow.CloseActionListener.class)  
)
public class UIPopupWindow extends UIComponentDecorator {  
  
  private int width_  = -1 ;
  private int height_ =  -1 ;
  private boolean isShow = false ;
  private boolean isResizable = false ;
  
  public int getWindowWidth() { return width_ ; }
  public int getWindowHeight() { return height_ ; }
  
  public void setWindowSize(int w, int h) {
    width_ = w ;
    height_ = h ;
  }
  
  public boolean isResizable() { return isResizable ; }
  public void setResizable(boolean isResizable) { this.isResizable = isResizable ; }

  public boolean isShow() { return isShow;}
  public void setShow(boolean isShow) { this.isShow = isShow; }
  
  static  public class CloseActionListener extends EventListener<UIPopupWindow> {
    public void execute(Event<UIPopupWindow> event) throws Exception {
      UIPopupWindow uiPopupWindow = event.getSource() ;
      if(!uiPopupWindow.isShow()) return;
      uiPopupWindow.setShow(false);
      WebuiRequestContext context =  event.getRequestContext() ;
      context.addUIComponentToUpdateByAjax(uiPopupWindow) ;
    }
  }
}
