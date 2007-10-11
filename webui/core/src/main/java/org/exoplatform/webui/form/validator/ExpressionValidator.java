/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.form.validator;

import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.exception.MessageException;
import org.exoplatform.webui.form.UIFormInput;

/**
 * Created by The eXo Platform SARL
 * Author : Le Bien Thuy
 *          lebienthuy@gmail.com
 * Oct 10, 2007
 * 
 * Validates whether this value matches one regula expression.
 */
public class ExpressionValidator implements Validator {
  private String expression_;
  private String key_;
  
  public ExpressionValidator(String expression) {
    expression_ = expression;
    key_ = "ExpressionValidator.msg.value-invalid";
  }
  
  public ExpressionValidator(String exp, String key){
    expression_ = exp; 
    key_ = key;
  }
  
  public void validate(UIFormInput uiInput) throws Exception {
    if((uiInput.getValue() != null) ){
      String value = ((String)uiInput.getValue()).trim();
      if(value.matches(expression_)) return ;
    }    
    String label = uiInput.getLabel();
    if(label == null) label = uiInput.getName();
    label = label.trim();
    if(label.charAt(label.length() - 1) == ':') label = label.substring(0, label.length() - 1);
    Object[]  args = {label,} ;
    throw new MessageException(new ApplicationMessage(key_, args, ApplicationMessage.WARNING)) ;
  }
}
