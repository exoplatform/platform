/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.form;
/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Jul 9, 2007  
 */
import java.util.List;

import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIPageIterator;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
import org.exoplatform.webui.form.validator.Validator;

@ComponentConfig(
    template = "system:/groovy/webui/core/UIPageIterator.gtmpl",
    events = @EventConfig(listeners = UIFormPageIterator.ShowPageActionListener.class, phase = Phase.DECODE)    
)
public class UIFormPageIterator extends UIPageIterator implements UIFormInput<Object> {
  
  public void processDecode(WebuiRequestContext context) throws Exception {   
    UIForm uiForm  = getAncestorOfType(UIForm.class);
    String action = uiForm.getSubmitAction(); 
    if(action == null) return;    
    Event<UIComponent> event = createEvent(action, Event.Phase.DECODE, context) ;
    if(event != null) event.broadcast() ;
  }  
  
  public String event(String name, String beanId) throws Exception {
    UIForm uiForm = getAncestorOfType(UIForm.class) ;
    return uiForm.event(name, getId(), beanId);
  }
  
  @SuppressWarnings("unused")
  static  public class ShowPageActionListener extends EventListener<UIFormPageIterator> {
    public void execute(Event<UIFormPageIterator> event) throws Exception {
      UIFormPageIterator uiPageIterator = event.getSource() ;
      int page = Integer.parseInt(event.getRequestContext().getRequestParameter(OBJECTID)) ;
      uiPageIterator.setCurrentPage(page) ;
      UIForm uiForm = uiPageIterator.getAncestorOfType(UIForm.class);   
      event.getRequestContext().addUIComponentToUpdateByAjax(uiForm.getParent());
    }
  }
  
  @SuppressWarnings("unused")
  public <E extends Validator> UIFormInput addValidator(Class<E> clazz, Object...params) throws Exception { 
    return this;
  }

  public String getBindingField() { return null; }
  
  public List getValidators() { return null; }

  public Object getValue() throws Exception { return null; }

  public void reset() {}
  
  public String getLabel() { return getName(); }
  
  public Class getTypeValue() { return null; }

  @SuppressWarnings("unused")
  public UIFormInput setValue(Object value) throws Exception { return null; }
  
}
