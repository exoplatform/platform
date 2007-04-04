/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.component;

import java.lang.reflect.Method;
import java.util.List;

import org.exoplatform.util.ReflectionUtil;
import org.exoplatform.webui.config.annotation.ComponentConfig;
/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * May 7, 2006
 */
@ComponentConfig(template = "system:/groovy/webui/component/UIGrid.gtmpl")
public class UIGrid extends UIComponent {
  
  private UIPageIterator uiIterator_ ;
  
  private String beanIdField_ ;
  private String[] beanField_ ;
  private String[] action_ ;
  private String classname_;
  private String label_ ;
  private boolean useAjax = true;
  
  public UIGrid() throws Exception {
    uiIterator_ = createUIComponent(UIPageIterator.class, null, null);
  }
  
  public UIPageIterator  getUIPageIterator() {  return uiIterator_ ; }
  
  public UIGrid configure(String beanIdField, String[] beanField, String[] action) {
    this.beanIdField_ =  beanIdField ;
    this.beanField_ =  beanField ;
    this.action_ = action ;
    return this ;
  }
  
  public String getBeanIdField()  { return beanIdField_ ; }
  
  public String[]  getBeanFields() { return beanField_ ; }
  
  public String[]  getBeanActions() { return action_ ; }
  
  public List getBeans() throws Exception { return uiIterator_.getCurrentPageData() ; }
  
  public String getName() { return classname_ ; }
  
  public String getLabel() { return label_ ; }
  public void setLabel(String label) { label_ = label ; }
  
  public Object getFieldValue(Object bean, String field) throws Exception {
    Method method = ReflectionUtil.getGetBindingMethod(bean, field);
    return method.invoke(bean, ReflectionUtil.EMPTY_ARGS) ;
  }

  @SuppressWarnings("unchecked")
  public UIComponent  findComponentById(String lookupId) {
    if(uiIterator_.getId().equals(lookupId))  return uiIterator_  ;
    return super.findComponentById(lookupId);
  }

  public boolean isUseAjax() { return useAjax; }
  public void setUseAjax(boolean value) { useAjax = value; }
  
}