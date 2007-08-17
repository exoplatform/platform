/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.form;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.form.validator.Validator;
/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * Jun 6, 2006
 * 
 * The base class to create form elements.
 * Extend it to create your own elements.
 */
abstract public class UIFormInputBase<T> extends UIContainer implements UIFormInput<T> {
  
  final static public boolean  ENABLE = true, DISABLE = false;
  /**
   * The HTML 'name' attribute of this element
   */
  protected String name ;
  /**
   * The HTML 'label' field of this element
   */
  private String label;
  /**
   * 
   */
  protected String bindingField ;
  /**
   * The list of validators for this form element
   */
  protected List<Validator>  validators ;
  /**
   * A default value for this field
   */
  protected T defaultValue_;
  /**
   * The current value of this field
   */
  protected T value_;
  /**
   * The type of value that is expected
   */
  protected Class<T> typeValue_ ;
  /**
   * Whether this field is enabled
   */
  protected boolean enable_ = true;
  /**
   * Whether this field is in read only mode
   */
  protected boolean readonly_ = false;
  
  public UIFormInputBase(String name, String bindingField,Class<T> typeValue) {
    this.name = name ;
    this.bindingField =  bindingField ;
    this.typeValue_ = typeValue;
    setId(name);
  }
  
  public String getName()  { return name ; }
  public void   setName(String name) { this.name = name ; }
  
  public String getBindingField() { return bindingField ; }
  public void   setBindingField(String s) {  this.bindingField = s ; }
  
  public T getDefaultValue() { return defaultValue_; }
  public void setDefaultValue(T defaultValue) { defaultValue_ = defaultValue; }
  
  public T getValue(){ return value_; }    
  public UIFormInput setValue(T value){
    this.value_ = value;
    return this;
  }  
  
  public Class<T> getTypeValue() { return typeValue_; }
  
  public void reset(){ value_ = defaultValue_; }
    
  public boolean isEnable() { return enable_; }  
  public UIFormInputBase<T> setEnable(boolean enable) {
    enable_ = enable;
    return this;
  }
  
  public boolean isEditable() { return !readonly_; }
  public UIFormInputBase<T> setEditable(boolean editable) { 
    readonly_ = !editable;
    return this;
  }
  
  public <E extends Validator> UIFormInputBase<T> addValidator(Class<E> clazz, Object...params) throws Exception {
    if(validators == null)  validators = new ArrayList<Validator>(3) ;
    if(params.length > 0) {
      Class<?> [] classes = new Class[params.length];
      for(int i=0; i<params.length; i++) {
        classes[i] = params[i].getClass();
      }
      Constructor<E> constructor = clazz.getConstructor(classes);
      validators.add(constructor.newInstance(params)) ;
      return this;
    } 
    validators.add(clazz.newInstance()) ;
    return this ;
  } 
  
  public List<Validator>  getValidators() { return validators ; }
  
  final public void processDecode(WebuiRequestContext context) throws Exception {
    UIForm uiForm  = getAncestorOfType(UIForm.class);
    String action =  uiForm.getSubmitAction(); //context.getRequestParameter(UIForm.ACTION) ;
    Event<UIComponent> event = createEvent(action, Event.Phase.DECODE, context) ;
    if(event != null) event.broadcast() ;   
  }
  
  abstract public  void decode(Object input,  WebuiRequestContext context) throws Exception ;
  
  @SuppressWarnings("unused")
  public void decodeFromMultipartFields(WebuiRequestContext context, UIComponent component, List items) {}

  public String getLabel() { return label; }

  public void setLabel(String label) { this.label = label; }
 
}