/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
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