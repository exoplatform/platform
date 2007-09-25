/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.core;

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
 * 
 * A component that creates an empty popup window
 */
@ComponentConfig(  
  template =  "system:/groovy/webui/core/UIPopupWindow.gtmpl",
  events = @EventConfig(listeners = UIPopupWindow.CloseActionListener.class, name = "ClosePopup")  
)
public class UIPopupWindow extends UIComponentDecorator {  
  /**
   * The width of the window
   */
  private int width_  = -1 ;
  /**
   * The height of the window
   */
  private int height_ =  -1 ;
  /**
   * Whether to show the close button at the bottom of the window
   */
  private boolean showCloseButton = true;
  /**
   * The visibility status of the window
   */
  private boolean isShow = false ;
  /**
   * Whether this window is resizable
   */
  private boolean isResizable = false ;
  
  protected String closeEvent_ = "ClosePopup" ;
  
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

  public boolean isShowCloseButton() {
    return showCloseButton;
  }
  public void setShowCloseButton(boolean showCloseButton) {
    this.showCloseButton = showCloseButton;
  }
  public String getCloseEvent() {
    return closeEvent_;
  }
  public void setCloseEvent(String closeEvent) {
    this.closeEvent_ = closeEvent;
  }

}
