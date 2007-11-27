/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.form.validator;

import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.exception.MessageException;
import org.exoplatform.webui.form.UIFormInput;

/**
 * Created by The eXo Platform SARL
 * Author : Nguyen Ba Phu
 *          phului@gmail.com
 * Nov 27, 2007  
 */
public class PageNodeNameValidator implements Validator {
  
  public void validate(UIFormInput uiInput) throws Exception {
    String s = (String)uiInput.getValue();
    if(Character.isDigit(s.charAt(0))) {
      Object[] args = { uiInput.getName(), uiInput.getBindingField() };
      throw new MessageException(new ApplicationMessage("PageNodeNameValidator.msg.invalid-digit", args, ApplicationMessage.WARNING)) ;
    }    
  }

}
