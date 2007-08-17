/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.core;

import java.util.List;

/**
 * Created by The eXo Platform SARL
 * Author : Dang Van Minh
 *          minhdv@exoplatform.com
 * Aug 31, 2006  
 * 
 * A component that represents a toolbar
 */

public abstract class UIToolbar extends UIComponent {
  /**
   * The css style
   */
  private String toolbarStyle_ = "LightToolbar" ;
  /**
   * A javascript expression
   */
  private String strJavascript_ = "" ;
  /**
   * A javascript event name
   */
  private String eventName_ = "" ;
  
  public UIToolbar() throws Exception {
  }
  
  public String getToolbarStyle() { return toolbarStyle_ ; }
  public void setToolbarStyle(String toolbarStyle) { toolbarStyle_ = toolbarStyle ;  }
  
  public List getEvents() { return getComponentConfig().getEvents() ; }
  
  public String getJavascript() { return strJavascript_ ; }
  public String getEventName() { return eventName_ ; }
  public void setJavascript(String eventName, String strJavascript) { 
    strJavascript_ = strJavascript ; 
    eventName_ = eventName ;
  }

}
