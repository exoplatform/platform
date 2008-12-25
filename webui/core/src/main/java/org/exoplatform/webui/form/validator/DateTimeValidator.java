/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.webui.form.validator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.exception.MessageException;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormDateTimeInput;
import org.exoplatform.webui.form.UIFormInput;

/**
 * Created by The eXo Platform SARL
 * Author : Tran The Trong
 *          trongtt@gmail.com
 * May 15, 2007
 * 
 * Validates whether a date is in a correct format
 */

public class DateTimeValidator implements Validator {
  static private final String SPLIT_REGEX = "/|\\s+|:" ;
  static private final String DATETIME_REGEX = 
    "^(\\d{1,2}\\/\\d{1,2}\\/\\d{1,4})\\s*(\\s+\\d{1,2}:\\d{1,2}:\\d{1,2})?$" ;
  
  public void validate(UIFormInput uiInput) throws Exception {
	  if (uiInput.getValue()==null || ((String)uiInput.getValue()).trim().length()==0) return;
    String s = (String)uiInput.getValue() ;
    DateFormat stFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    UIFormDateTimeInput uiDateInput = (UIFormDateTimeInput)uiInput;
    SimpleDateFormat sdf = new SimpleDateFormat(uiDateInput.getDatePattern_().trim());
    
    UIForm uiForm = ((UIComponent) uiInput).getAncestorOfType(UIForm.class);
    String label;
    try{
      label = uiForm.getLabel(uiInput.getName());
    } catch(Exception e) {
      label = uiInput.getName();
    }
    Object[]  args = { label, s } ;
    
    try {
      Date stDate = sdf.parse(s);
      s = stFormat.format(stDate);
    } catch (Exception e) {
      throw new MessageException(new ApplicationMessage("DateTimeValidator.msg.Invalid-input", args)) ;
    }
    if(s.matches(DATETIME_REGEX) && isValidDateTime(s)) return ;
    
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
