/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.component;

import java.io.Writer;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.component.validator.Validator;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;

/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Sep 14, 2006
 */
@ComponentConfig(
  events = {
    @EventConfig(listeners = UIFormMultiValueInputSet.AddActionListener.class, phase = Phase.DECODE),
    @EventConfig(listeners = UIFormMultiValueInputSet.RemoveActionListener.class, phase = Phase.DECODE)
  }
)
public class UIFormMultiValueInputSet extends UIFormInputContainer<List<?>> {

  protected List<Validator>  validators ;

  private Class<? extends UIFormInput> clazz_;  
  private Constructor constructor_ = null;

  public UIFormMultiValueInputSet() throws Exception {
    super(null, null);
  }

  public UIFormMultiValueInputSet(String name, String bindingField) throws Exception {
    super(name, bindingField);    
    setComponentConfig(getClass(), null) ;  
  }
  
  public Class<List> getTypeValue(){return List.class; }

  public void setType(Class<? extends UIFormInput> clazz){
    this.clazz_ = clazz; 
    Constructor [] constructors = clazz_.getConstructors();
    if(constructors.length > 0) constructor_ = constructors[0];
  }  
  
  public Class<? extends UIFormInput> getUIFormInputBase() { return clazz_; }

  public List<?> getValue(){
    List<Object> values = new ArrayList<Object>();
    for (UIComponent child : getChildren()) {
      UIFormInputBase uiInput = (UIFormInputBase) child;
      if(uiInput.getValue() == null) continue;
      values.add(uiInput.getValue());
    }
    return values;
  }

  @SuppressWarnings("unchecked")
  public UIFormInput setValue(List<?> values) throws Exception {
    getChildren().clear();
    for(int i = 0; i < values.size(); i++){
      UIFormInputBase uiInput =  createUIFormInput(i);
      uiInput.setValue(values.get(i));
    }    
    return this;
  } 

  public void processDecode(WebuiRequestContext context) throws Exception {   
    super.processDecode(context);
    UIForm uiForm  = getAncestorOfType(UIForm.class);
    String action =  uiForm.getSubmitAction();
    Event<UIComponent> event = createEvent(action, Event.Phase.DECODE, context) ;    
    if(event == null)  return;
    event.broadcast() ;
  }

  public void processRender(WebuiRequestContext context) throws Exception {   
    if(getChildren() == null || getChildren().size() < 1) createUIFormInput(0);
    
    Writer writer = context.getWriter() ;    
    writer.append("<div class=\"LeftBlock\"></div>") ;

    UIForm uiForm = getAncestorOfType(UIForm.class) ;
    int size = getChildren().size() ;

    for(int i = 0; i < size; i++) {
      UIFormInputBase uiInput = getChild(i) ;
      writer.append("<div class=\"MultiValueContainer\">") ;
      uiInput.processRender(context) ;

      if(i == size - 1) {
        if(size >= 2){
          writer.append("<a class=\"UIFormMultiValueInputSet DustBin16x16Icon\" href=\"");
          writer.append(uiForm.event("Remove", getId()+String.valueOf(i))).append("\">");
          writer.append("<span></span></a>");
        }
        writer.append("<a class=\"AddNewNodeIcon\" href=\"");
        writer.append(uiForm.event("Add", getId())).append("\">");
        writer.append("<span></span></a>");
      }      
      writer.append("<div style=\"clear: both\"><span></span></div></div>") ;
    }    
  }

  public  UIFormInputBase createUIFormInput(int idx) throws Exception {
    Class [] classes = constructor_.getParameterTypes();    
    Object [] params = new Object[classes.length];
    params[0] = getId()+String.valueOf(idx);
    UIFormInputBase inputBase = (UIFormInputBase)constructor_.newInstance(params);
    addChild(inputBase);
    return inputBase;    
  }

  static  public class AddActionListener extends EventListener<UIFormMultiValueInputSet> {
    public void execute(Event<UIFormMultiValueInputSet> event) throws Exception {
      UIFormMultiValueInputSet uiSet = event.getSource();
      String id = event.getRequestContext().getRequestParameter(OBJECTID);  
      if(uiSet.getId().equals(id)){
        uiSet.createUIFormInput(uiSet.getChildren().size());
      }
    }
  }

  static  public class RemoveActionListener extends EventListener<UIFormMultiValueInputSet> {
    public void execute(Event<UIFormMultiValueInputSet> event) throws Exception {
      UIFormMultiValueInputSet uiSet = event.getSource();
      String id = event.getRequestContext().getRequestParameter(OBJECTID);  
      uiSet.removeChildById(id);
    }
  }

}
