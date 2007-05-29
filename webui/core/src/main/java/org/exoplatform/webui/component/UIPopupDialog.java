/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.component;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;

/**
 * Created by The eXo Platform SARL
 * Author : Le Bien Thuy
 *          thuy.le@exoplatform.com
 * @version: $Id$
 * Aug 14, 2006  
 */

@ComponentConfig(
    template = "system:groovy/webui/component/UIPopupDialog.gtmpl",
    
     events = {
      @EventConfig(listeners = UIPopupWindow.CloseActionListener.class)
    }
)
public class UIPopupDialog extends UIPopupWindow {
  
  public static final String OK_BUTTON = "ok";
  public static final String CANCEL_BUTTON = "cancel";
  public static final String YES_BUTTON = "yes";
  public static final String NO_BUTTON = "no";
  public static final String APPLY_BUTTON = "apply";
  public static final String CLOSE_BUTTON = "close";
  
  private List<String> actionList_;
  private boolean isForm_ = false;
  private String handerEvent_;
  private String message_;
  private String messageType_;
  
  public UIPopupDialog() { this(null); }
    
  public UIPopupDialog(List<String> btnList) {
    if(btnList != null && btnList.size() > 0){
      actionList_ = btnList;
    } else {
      actionList_ = new ArrayList<String>();
      actionList_.add(CLOSE_BUTTON);
    }
  }
  
  public void setButtonList(List<String> btnList) { actionList_ = btnList; }
  public List<String> getButtonList(){ return actionList_; }
  
  public void setHanderEvent(String action) {   handerEvent_ = action;}
  public String getHanderEvent() {return  handerEvent_; }
  
  public void setMessage(String action) {   message_ = action;}
  public String getMessage() {  return message_ ;}
  
  public void setMessageType(String action) {   messageType_ = action;}
  public String getMessageType() {  return messageType_;  }
  
  public void setIsForm(boolean action) {   isForm_ = action;}
  public boolean isForm() { return isForm_;  }

}
