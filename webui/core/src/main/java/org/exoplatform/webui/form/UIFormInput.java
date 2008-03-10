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
package org.exoplatform.webui.form;

import java.util.List;

import org.exoplatform.webui.form.validator.Validator;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * Jun 6, 2006
 * 
 * The base interface to create form elements.
 * This interface is implemented by UIFormInputBase, extend it instead of implementing this interface.
 * @see UIFormInputBase
 */
public interface UIFormInput<E> {
  
  public String getName()  ;
  public String getBindingField()  ;
  
  public String getLabel();

  public <E extends Validator> UIFormInput addValidator(Class<E> clazz, Object...params) throws Exception ;
  public List<Validator>  getValidators()  ;
  
  public E getValue() throws Exception ;
  public UIFormInput setValue(E value) throws Exception;
  
  public Class<E> getTypeValue() ;

  public void reset();
  
}