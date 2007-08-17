/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.form.validator;

import javax.faces.validator.LengthValidator;

import org.codehaus.groovy.runtime.NewInstanceMetaMethod;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.exception.MessageException;
import org.exoplatform.webui.form.UIFormInput;

/**
 * Created by The eXo Platform SARL
 * Author : Dang Van Minh
 *          minhdv81@yahoo.com
 * Jun 7, 2006
 * 
 * Validates whether this value has a length between min and max
 */
public class StringLengthValidator implements Validator {
  /**
   * The minimum number of characters in this String
   */
  private Integer min_ = 0;
  /**
   * The maximum number of characters in this String
   */
  private Integer max_ = 0;
  
  public StringLengthValidator(Integer max) {
    max_ = max;
  }
  public StringLengthValidator(Integer min, Integer max){
   min_ = min;
   max_ = max;
  }
  
  public void validate(UIFormInput uiInput) throws Exception {
    if((uiInput.getValue() != null) ){
      int length = ((String)uiInput.getValue()).trim().length();
      if(min_ <= length && max_ >= length) return ;
    }    
    String label = uiInput.getLabel();
    if(label == null) label = uiInput.getName();
    label = label.trim();
    if(label.charAt(label.length() - 1) == ':') label = label.substring(0, label.length() - 1);
    Object[]  args = {label,min_.toString(), max_.toString()} ;
    throw new MessageException(new ApplicationMessage("StringLengthValidator.msg.length-invalid", args, 
                                                      ApplicationMessage.WARNING)) ;
  }
}
