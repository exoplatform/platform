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
 */
// TODO : TrongTT
public class UIFormDateTimeInput extends UIFormInputBase<String> {
  
  private DateFormat dateFormat_ ;
  private boolean displayTime_ = true;
  
  public UIFormDateTimeInput(String name, String bindField, Date date, DateFormat dateFormat) {
    super(name, bindField, String.class) ;
    dateFormat_ = dateFormat;
    if(!displayTime_) dateFormat_ = new SimpleDateFormat("MM/dd/yyyy");
    if(date != null) value_ = dateFormat_.format(date) ;
  }
  
  public UIFormDateTimeInput(String name, String bindField, Date date) {
    super(name, bindField, String.class) ;
    dateFormat_ = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    if(!displayTime_) dateFormat_ = new SimpleDateFormat("MM/dd/yyyy");
    if(date != null) value_ = dateFormat_.format(date) ;
  }
  
  public void setDisplayTime(boolean displayTime) { displayTime_ = displayTime; }
  public boolean isDisplayTime() { return displayTime_; }
  
  public void setCalendar(Calendar date) { dateFormat_.format(date.getTime()) ; }

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
    w.write("<input type='text' onfocus='eXo.webui.UICalendar.init(this);' onkeyup='eXo.webui.UICalendar.show();' name='") ;
    w.write(getName()) ; w.write('\'') ;
    if(value_ != null && value_.length() > 0) {      
      w.write(" value='"); w.write(value_.toString()); w.write('\'');
    }
    w.write(" onmousedown='event.cancelBubble = true' />") ;
  }

  public void setDateFormat(DateFormat formatter) { this.dateFormat_ = formatter; }
}
