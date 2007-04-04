/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.component;

import java.lang.reflect.Method;
import java.util.List;

import org.exoplatform.util.ReflectionUtil;
import org.exoplatform.webui.config.annotation.ComponentConfig;

/**
 * Created by The eXo Platform SARL
 * Author : Nguyen Viet Chung
 *          nguyenchung136@yahoo.com
 * Jul 20, 2006  
 */
@ComponentConfig( template = "system:/groovy/webui/component/UITableTemplate.gtmpl" )
//TODO:  Look like  this class doing the same  job  as  UIGrid
public class UITableTemplate extends UIComponent {
  
  private String beanIdField_ ;
  private String[] beanField_ ;
  private String[] action_ ;
  private String label_ ;
  private List data_ ;
  private String icon_ ;
  public UITableTemplate() throws Exception {
    
  }
  
  public UITableTemplate configure(String beanIdField, String[] beanField, String[] action) {
    this.beanIdField_ =  beanIdField ;
    this.beanField_ =  beanField ;
    this.action_ = action ;
    return this ;
  }
  
  public String[]  getBeanFields() { return beanField_ ; }
  public String[]  getBeanActions() { return action_ ; }
  public String getBeanIdField()  { return beanIdField_ ; }
  
  public String getLabel() { return label_ ; }
  public void setLabel(String label) { label_ = label ; }
  
  public void setIcon(String icon) { icon_ = icon ; }
  public String getIcon() { return icon_ ; }
  
  public void setData(List data) { data_ = data ; }
  public List getData() { return data_ ; }
  
  public Object getFieldValue(Object bean, String field) throws Exception {
    Method method = ReflectionUtil.getGetBindingMethod(bean, field);
    return method.invoke(bean, ReflectionUtil.EMPTY_ARGS) ;
  }  

}
