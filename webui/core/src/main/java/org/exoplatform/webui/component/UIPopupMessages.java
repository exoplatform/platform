/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.component;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.webui.application.ApplicationMessage;
import org.exoplatform.webui.application.RequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SARL
 * Author : Tran The Trong
 *          trong.tran@exoplatform.com
 * @version: $Id$
 * Aug 14, 2006  
 */
@ComponentConfig(
    template = "system:groovy/webui/component/UIPopupMessages.gtmpl",
    events = @EventConfig(listeners = UIPopupMessages.CloseActionListener.class)  
)
public class UIPopupMessages extends UIPopupWindow {
  
  private List<ApplicationMessage> errors_ ;
  private List<ApplicationMessage> warnings_ ;
  private List<ApplicationMessage> infos_ ;
  
  public UIPopupMessages() {
    errors_ = new ArrayList<ApplicationMessage>() ;
    warnings_ = new ArrayList<ApplicationMessage>() ;
    infos_ = new ArrayList<ApplicationMessage>() ;
    setShow(true);
  }
  
  public List<ApplicationMessage> getErrors() { return errors_; }

  public void setErrors(List<ApplicationMessage> errors_) { this.errors_ = errors_; }

  public List<ApplicationMessage> getInfos() { return infos_; }

  public void setInfos(List<ApplicationMessage> infos_) { this.infos_ = infos_; }

  public List<ApplicationMessage> getWarnings() { return warnings_; }

  public void setWarnings(List<ApplicationMessage> warnings_) { this.warnings_ = warnings_; }
  
  public void processRender(RequestContext context) throws Exception {
    super.processRender(context);    
  }

  public void addMessage(ApplicationMessage msg) {
    switch(msg.getType()) {
      case ApplicationMessage.ERROR : errors_.add(msg) ; break ;
      case ApplicationMessage.WARNING : warnings_.add(msg) ; break ;
      default  : infos_.add(msg) ;
    }
  }
  
  public boolean hasMessage() {
    return (errors_.size() > 0  || warnings_.size() > 0 || infos_.size() > 0);  
  }
    
  public void clearMessages() {
    errors_.clear() ;
    warnings_.clear() ;
    infos_.clear() ;
  }
  
  static  public class CloseActionListener extends EventListener<UIPopupMessages> {
    public void execute(Event<UIPopupMessages> event) throws Exception {
      UIPopupMessages uiPopupMessage = event.getSource() ;
      uiPopupMessage.clearMessages();
      RequestContext context =  event.getRequestContext() ;
      if(uiPopupMessage.getParent() == null){
        context.addUIComponentToUpdateByAjax(uiPopupMessage) ;
        return;
      }
      if(!uiPopupMessage.isShow()) return;
      UIComponent uiParent = uiPopupMessage.getParent();
      context.addUIComponentToUpdateByAjax(uiParent) ;
      Event<UIComponent> pEvent = uiParent.createEvent("ClosePopup", event.getExecutionPhase(), event.getRequestContext());
      if(pEvent != null) pEvent.broadcast();
    }
  }
}
