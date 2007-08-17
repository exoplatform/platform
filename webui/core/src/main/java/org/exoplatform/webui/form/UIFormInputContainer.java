/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.form;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.form.validator.Validator;
/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * Jun 6, 2006
 * 
 * Represents an abstract container of elements, inside a form
 */
abstract public class UIFormInputContainer<T> extends UIContainer implements UIFormInput<T> {

  public String name ;
  public String bindingField ;
  private List<Validator>  validators ;
  protected T value_;
  private String label;
  
  protected UIFormInputContainer() {
  }

  public UIFormInputContainer(String name, String bindingField) {
    this.name = name ;
    this.bindingField =  bindingField ;
    setId(name);
  }
  
  public T getValue() throws Exception { return value_; }    
  public UIFormInput setValue(T value) throws Exception {
    this.value_ = value;
    return this;
  }
  
  public String getName()  { return name ; }
  public void   setName(String name) { this.name = name ; }

  public String getBindingField() { return bindingField ; }
  public void setBindingField(String s) {  this.bindingField = s ; }

  public <E extends Validator> UIFormInput addValidator(Class<E> clazz, Object...params) throws Exception { 
    if(validators == null)  validators = new ArrayList<Validator>(3) ;
    validators.add(clazz.newInstance()) ;
    return this ;
  }
  
  /*public void processDecode(WebuiRequestContext context) throws Exception {   
    super.processDecode(context);
    List<UIComponent>  children = getChildren() ;
    for(UIComponent uiChild :  children) {
      uiChild.processDecode(context);
    }
    
    UIForm uiForm  = getAncestorOfType(UIForm.class);
    String action =  uiForm.getSubmitAction();
    if(action == null) return;    
    Event<UIComponent> event = createEvent(action, Event.Phase.DECODE, context) ;
    if(event != null) event.broadcast() ;
    
  }
*/
  public List<Validator>  getValidators() { return validators ; }
  
  public void reset() {}
  
  public String getLabel() { return label; }

  public void setLabel(String label) { this.label = label; }
  
}