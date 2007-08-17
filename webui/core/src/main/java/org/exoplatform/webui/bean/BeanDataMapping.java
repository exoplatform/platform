/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.bean;

import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormInputSet;

/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Oct 13, 2006
 * 
 * An interface to define mappings between a bean and some data
 */
public interface BeanDataMapping {

  public void mapField(UIForm uiForm, Object bean) throws Exception ;

  public void mapField(UIFormInputSet uiFormInputSet, Object bean) throws Exception ;
  
  public void mapBean(Object bean,  UIForm uiForm) throws Exception ;
  
  public void mapBean(Object bean,  UIFormInputSet uiFormInputSet) throws Exception ;

}
