/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.component.validator;

import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.component.UIFormInput;
import org.exoplatform.webui.exception.MessageException;

/**
 * Created by The eXo Platform SARL
 * Author : Dang Van Minh
 *          minhdv81@yahoo.com
 * Jun 7, 2006
 */
public class NumberFormatValidator implements Validator {
    
  public void validate(UIFormInput uiInput) throws Exception {
    String s = (String)uiInput.getValue();    
    if(s == null || s.trim().length() < 1) return ;
    for(int i = 0; i < s.length(); i ++){
      char c = s.charAt(i);
      if (Character.isDigit(c)){
        continue;
      }
      Object[] args = { uiInput.getName(), uiInput.getBindingField() };
      throw new MessageException(new ApplicationMessage("NumberFormatValidator.msg.Invalid-number", args)) ;
    }
  }
  
}
