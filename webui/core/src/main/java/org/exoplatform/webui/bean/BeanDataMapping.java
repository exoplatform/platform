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
