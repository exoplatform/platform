/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.component.validator;

import java.util.Calendar;
import java.util.GregorianCalendar;

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
  static private final String SPLIT_REGEX = "/|\\s+|:" ;
  static private final String DATETIME_REGEX = 
    "^(\\d{1,2}\\/\\d{1,2}\\/\\d{1,4})\\s*(\\s+\\d{1,2}:\\d{1,2}:\\d{1,2})?$" ;
  
  public void validate(UIFormInput uiInput) throws Exception {
    String s = (String)uiInput.getValue() ;
    if(s == null || s.trim().length() < 1 || (s.matches(DATETIME_REGEX) && isValidDateTime(s))) return ;
    Object[]  args = { uiInput.getName(), s } ;
    throw new MessageException(new ApplicationMessage("DateTimeValidator.msg.Invalid-input", args)) ;
  }
  
  private boolean isValidDateTime(String dateTime) {
    String[] arr = dateTime.split(SPLIT_REGEX, 7) ;
    int valid = Integer.parseInt(arr[0]) ;
    if(valid < 1 || valid > 12) return false;
    Calendar date = new GregorianCalendar(Integer.parseInt(arr[2]), valid - 1, 1) ;
    if(Integer.parseInt(arr[1]) > date.getActualMaximum(Calendar.DAY_OF_MONTH)) return false;
    if(arr.length > 3 && (Integer.parseInt(arr[3]) > 23 || Integer.parseInt(arr[4]) > 59 || Integer.parseInt(arr[5]) > 59)) return false; 
    return true;
  }
}
