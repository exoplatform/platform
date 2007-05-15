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
 * Author : Tran The Trong
 *          trongtt@gmail.com
 * May 15, 2007
 */

public class DateTimeValidator implements Validator {
  static private final String DATETIME_REGEX = 
    "^(\\d{1,2}\\/\\d{1,2}\\/\\d{1,4})\\s*(\\s+\\d{1,2}:\\d{1,2}:\\d{1,2})?$";
  
  public void validate(UIFormInput uiInput) throws Exception {
    String s = (String)uiInput.getValue();
    if(s == null || s.trim().length() < 1 || (s).matches(DATETIME_REGEX)) return;
    Object[]  args = { uiInput.getName(), uiInput.getBindingField() } ;
    throw new MessageException(new ApplicationMessage("DateTimeValidator.msg.Invalid-input", args)) ;
  }
}
