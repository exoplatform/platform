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
 * Author : Vu Duy Tu
 *          duytucntt@gmail.com
 * Jun 22, 2007
 */

public class PositiveNumberFormatValidator implements Validator {
    
  public void validate(UIFormInput uiInput) throws Exception {
    String s = (String)uiInput.getValue();    
    if(s == null || s.trim().length() < 1) return ;
    boolean t = false;
    for(int i = 0; i < s.length(); i ++){
      char c = s.charAt(i);
      if (Character.isDigit(c) || (s.charAt(0) == '-' && i == 0)){
      	t = true;
        continue;  
      }
      t = false;
      Object[] args = { uiInput.getName(), uiInput.getBindingField() };
      throw new MessageException(new ApplicationMessage("NumberFormatValidator.msg.Invalid-number", args)) ;
    }
    if(t == true && s.charAt(0) == '-') {
      Object[] args = { uiInput.getName(), uiInput.getBindingField() };
      throw new MessageException(new ApplicationMessage("PositiveNumberFormatValidator.msg.Invalid-number", args)) ;
    }
  }  
}