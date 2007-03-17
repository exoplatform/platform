/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.component.model;

import java.lang.reflect.Method;
import java.util.List;

import org.exoplatform.webui.component.UIComponent;
import org.exoplatform.webui.component.UIForm;
import org.exoplatform.webui.component.UIFormInputBase;
import org.exoplatform.webui.component.UIFormInputSet;

/**
 * Author : Nhu Dinh Thuan nhudinhthuan@exoplatform.com Oct 13, 2006
 */
public class ReflectionDataMapping implements BeanDataMapping {

  public void mapBean(Object bean, UIForm uiForm) throws Exception {
    List<UIComponent> children = uiForm.getChildren() ;
    for(UIComponent uichild : children ) {
      if(uichild instanceof UIFormInputBase) {
        invokeSetBindingField(bean, (UIFormInputBase) uichild);
      }else if(uichild instanceof UIFormInputSet){
        mapBean(bean, (UIFormInputSet) uichild);
      }
    }
  }

  public void mapBean(Object bean, UIFormInputSet uiFormInputSet) throws Exception {
    List<UIComponent> children = uiFormInputSet.getChildren() ;
    for(UIComponent uichild : children ) {
      if(uichild instanceof UIFormInputBase) {
        invokeSetBindingField(bean, (UIFormInputBase) uichild);
      }
    }
  }

  public void mapField(UIForm uiForm, Object bean) throws Exception {
    List<UIComponent> children = uiForm.getChildren() ;
    for(UIComponent uichild : children ) {
      if(uichild instanceof UIFormInputBase) {
        invokeGetBindingField((UIFormInputBase) uichild, bean);
      }else if(uichild instanceof UIFormInputSet){
        mapField((UIFormInputSet) uichild, bean);
      }
    }
  }

  public void mapField(UIFormInputSet uiFormInputSet, Object bean) throws Exception {
    List<UIComponent> children = uiFormInputSet.getChildren() ;
    for(UIComponent uichild : children ) {
      if(uichild instanceof UIFormInputBase) {
        invokeGetBindingField((UIFormInputBase) uichild, bean);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private void invokeGetBindingField(UIFormInputBase uiFormInput, Object bean) throws Exception {
    String bindingField = uiFormInput.getBindingField();
    if(bindingField == null) return;
    Method method = ReflectionUtil.getGetBindingMethod(bean, bindingField);
    Object value = method.invoke(bean, ReflectionUtil.EMPTY_ARGS);
    if(value == null) return;
    uiFormInput.setValue(value);
  }
  
  private  void invokeSetBindingField(Object bean, UIFormInputBase uiFormInput) throws Exception {    
    String bindingField = uiFormInput.getBindingField();
    if(bindingField == null) return;
    Class  [] classes = new Class[]{uiFormInput.getTypeValue()};
    Method method = ReflectionUtil.getSetBindingMethod(bean, bindingField, classes);
    method.invoke(bean, new Object[]{ uiFormInput.getValue() } ) ;
  }

}
