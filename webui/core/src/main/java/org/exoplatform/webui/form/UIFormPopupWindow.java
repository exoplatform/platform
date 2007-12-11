/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.webui.form;

import java.util.List;

import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIPopupWindow;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
import org.exoplatform.webui.form.validator.Validator;

/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Nov 20, 2006
 */
@ComponentConfig(  
    template =  "system:/groovy/webui/core/UIPopupWindow.gtmpl",
    events = @EventConfig(listeners = UIFormPopupWindow.CloseActionListener.class, name="CloseFormPopup", phase = Phase.DECODE)
)
public class UIFormPopupWindow extends UIPopupWindow implements UIFormInput<Object> {
  
  public UIFormPopupWindow() {
    closeEvent_ = "CloseFormPopup" ;
  }
  
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
    if(uiForm != null) return uiForm.event(name, getId(), (String)null);
    return super.event(name);
  }
  
  static  public class CloseActionListener extends EventListener<UIPopupWindow> {
    public void execute(Event<UIPopupWindow> event) throws Exception {
      UIPopupWindow uiPopupWindow = event.getSource() ;
      UIForm uiForm = uiPopupWindow.getAncestorOfType(UIForm.class);   
      uiPopupWindow.setShow(false) ;
      event.getRequestContext().addUIComponentToUpdateByAjax(uiForm.getParent());
    }
  }
  
  public String getLabel() { return getName(); }

  @SuppressWarnings("unused")
  public <E extends Validator> UIFormInput addValidator(Class<E> clazz, Object...params) throws Exception { 
    return this;
  }

  public String getBindingField() { return null; }
  
  public List getValidators() { return null; }

  public Object getValue() throws Exception { return null; }

  public void reset() {}
  
  public Class getTypeValue() { return null; }

  @SuppressWarnings("unused")
  public UIFormInput setValue(Object value) throws Exception { return null; }

}
