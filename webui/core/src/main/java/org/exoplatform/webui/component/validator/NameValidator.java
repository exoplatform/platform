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
public class NameValidator implements Validator {
    
  public void validate(UIComponent uicomponent) throws Exception {
    UIFormInputBase uiInput = (UIFormInputBase) uicomponent ;
    String s = (String)uiInput.getValue();
    if(s == null || s.length() == 0) {
      Object[] args = { uiInput.getName(), uiInput.getBindingField() };
      throw new MessageException(new ApplicationMessage("NameValidator.msg.empty-input", args)) ;
    }
    for(int i = 0; i < s.length(); i ++){
      char c = s.charAt(i);
      if (Character.isLetter(c) || Character.isDigit(c) || c=='_' || c=='-' || c=='.' || c=='*' ){
        continue;
      }
      Object[] args = { uiInput.getName(), uiInput.getBindingField() };
      throw new MessageException(new ApplicationMessage("NameValidator.msg.Invalid-char", args)) ;
    }
  }
  
}
