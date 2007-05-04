/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.component.validator;

import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.component.UIComponent;
import org.exoplatform.webui.component.UIFormInputBase;
import org.exoplatform.webui.exception.MessageException;

/**
 * Created by The eXo Platform SARL
 * Author : Dang Van Minh
 *          minhdv81@yahoo.com
 * Jun 7, 2006
 */
public class EmptyFieldValidator implements Validator {
  
  public void validate(UIComponent uicomponent) throws Exception {
    UIFormInputBase uiInput = (UIFormInputBase) uicomponent ;
    if((uiInput.getValue() != null) && ((String)uiInput.getValue()).trim().length() > 0) {
      return ;
    }
    Object[]  args = { uiInput.getName(), uiInput.getBindingField() } ;
//  System.out.println("====>>Name: " + uiInput.getName() + "_++++>> BindingField " + uiInput.getBindingField());
    throw new MessageException(new ApplicationMessage("EmptyFieldValidator.msg.empty-input", args)) ;
  }
}
