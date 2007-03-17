/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.component.model;

import org.exoplatform.webui.component.UIForm;
import org.exoplatform.webui.component.UIFormInputSet;

/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Oct 13, 2006
 */
public interface BeanDataMapping {
  
  public void mapField(UIForm uiForm, Object bean) throws Exception ;
  
  public void mapField(UIFormInputSet uiFormInputSet, Object bean) throws Exception ;
  
  public void mapBean(Object bean,  UIForm uiForm) throws Exception ;
  
  public void mapBean(Object bean,  UIFormInputSet uiFormInputSet) throws Exception ;

}
