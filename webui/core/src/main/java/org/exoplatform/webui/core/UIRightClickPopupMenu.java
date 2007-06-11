/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.core;

import java.util.List;

import org.exoplatform.webui.config.Event;
import org.exoplatform.webui.config.annotation.ComponentConfig;
/**
 * Created by The eXo Platform SARL
 * Author : Tran The Trong
 *          trongtt@gmail.com
 * January 18, 2007
 */
@ComponentConfig()
public class UIRightClickPopupMenu extends UIComponent {
  
  private String[] actions_ ;
  public UIRightClickPopupMenu() throws Exception {}

  public String[] getActions() {
    if(actions_ != null) return actions_;
    List<Event> events = config.getEvents();
    actions_ = new String[events.size()];    
    for(int i = 0; i < actions_.length; i++){
      actions_[i] = events.get(i).getName();
    }
    return actions_ ;
  }
  
  public void setActions(String[] action) { this.actions_ = action ; }
  
  public CharSequence getJSOnclickShowPopup(String objId, String actions) {
    StringBuilder jsOnclick = new StringBuilder("onmousedown=\"eXo.webui.UIRightClickPopupMenu");
    jsOnclick.append(".clickRightMouse(event, this, '").append(getId()).append('\'') ;
    if(objId != null) {
      objId = objId.replaceAll("'", "\\'") ;
      objId = objId.replaceAll("\"", "\\\"") ;
      jsOnclick.append(",'").append(objId).append("'") ;
    }
    if(actions != null) jsOnclick.append(",'").append(actions).append("'") ;
    jsOnclick.append(");\"") ;
    return jsOnclick;
  }
}