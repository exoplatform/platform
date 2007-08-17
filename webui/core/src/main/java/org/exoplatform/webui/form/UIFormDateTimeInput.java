/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.form;

import java.io.Writer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.exoplatform.webui.application.WebuiRequestContext;

/**
 * Created by The eXo Platform SARL
 * Author : Tran The Trong
 *          trongtt@gmail.com
 * Jul 14, 2006  
 * 
 * A date picker element
 */
public class UIFormDateTimeInput extends UIFormInputBase<String> {
  /**
   * The DateFormat
   */
  private DateFormat dateFormat_ ;
  /**
   * Whether to display the full time (with hours, minutes and seconds), not only the date
   */
  private boolean isDisplayTime_ ;
  
  public UIFormDateTimeInput(String name, String bindField, Date date, boolean isDisplayTime) {
    super(name, bindField, String.class) ;
    setDisplayTime(isDisplayTime) ;
    if(date != null) value_ = dateFormat_.format(date) ;
  }
  
  public UIFormDateTimeInput(String name, String bindField, Date date) {
    this(name, bindField, date, true) ;
  }
  /**
   * By default, creates a date of format Month/Day/Year
   * If isDisplayTime is true, adds the time of format Hours:Minutes:Seconds
   * TODO : Display time depending on the locale of the client.
   * @param isDisplayTime
   */
  public void setDisplayTime(boolean isDisplayTime) {
    isDisplayTime_ = isDisplayTime;
    if(isDisplayTime_) dateFormat_ = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    else dateFormat_ = new SimpleDateFormat("MM/dd/yyyy");
  }
  
  public void setCalendar(Calendar date) { value_ = dateFormat_.format(date.getTime()) ; }
  public Calendar getCalendar() {
    try {
      Calendar calendar = new GregorianCalendar() ;
      calendar.setTime(dateFormat_.parse(value_ + " 0:0:0")) ;
      return calendar ;
    } catch (ParseException e) {
      return null;
    }
  }
  
  @SuppressWarnings("unused")
  public void decode(Object input, WebuiRequestContext context) throws Exception {
    if(input != null) value_ = ((String)input).trim();
  }

  public void processRender(WebuiRequestContext context) throws Exception {
    context.getJavascriptManager().importJavascript("eXo.webui.UICalendar") ;
    Writer w = context.getWriter();
    w.write("<input type='text' onfocus='eXo.webui.UICalendar.init(this,") ;
    w.write(String.valueOf(isDisplayTime_));
    w.write(");' onkeyup='eXo.webui.UICalendar.show();' name='") ;
    w.write(getName()) ; w.write('\'') ;
    if(value_ != null && value_.length() > 0) {      
      w.write(" value='"); w.write(value_.toString()); w.write('\'');
    }
    w.write(" onmousedown='event.cancelBubble = true' />") ;
  }
}
