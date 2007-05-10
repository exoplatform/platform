/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.component;

import java.util.List;

import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;

/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Nov 20, 2006
 */
@ComponentConfig(  
    template =  "system:/groovy/webui/component/UIPopupWindow.gtmpl",
    events = @EventConfig(listeners = UIFormPopupWindow.CloseActionListener.class, name="ClosePopup", phase = Phase.DECODE)
)
public class UIFormPopupWindow extends UIPopupWindow implements UIFormInput {
  
  public void processDecode(WebuiRequestContext context) throws Exception {   
    UIForm uiForm  = getAncestorOfType(UIForm.class);
    String action = uiForm.getSubmitAction(); 
    if(action == null) return;    
    Event<UIComponent> event = createEvent(action, Event.Phase.DECODE, context) ;
    if(event != null) event.broadcast() ;
    getUIComponent().processDecode(context);
    if(getUIComponent() == null) return;
  }  
  
  
  public String event(String name) throws Exception {
    UIForm uiForm = getAncestorOfType(UIForm.class) ;
    if(uiForm != null) return uiForm.event(name);
    return super.event(name);
  }
  
  static  public class CloseActionListener extends EventListener<UIPopupWindow> {
    public void execute(Event<UIPopupWindow> event) throws Exception {
      UIPopupWindow uiPopupWindow = event.getSource() ;
      UIForm uiForm = uiPopupWindow.getAncestorOfType(UIForm.class);   
      if(!uiPopupWindow.isShow()) return;
      uiPopupWindow.setShow(false) ;
      event.getRequestContext().addUIComponentToUpdateByAjax(uiForm.getParent());
    }
  }

  @SuppressWarnings("unused")
  public UIFormInput addValidator(Class clazz) throws Exception {
    return null;
  }

  public String getBindingField() { return null; }
  
  public List getValidators() { return null; }

  public Object getValue() throws Exception { return null; }

  public void reset() {
  }

  @SuppressWarnings("unused")
  public UIFormInput setValue(Object value) throws Exception { return null; }

}
