/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.component;

import java.io.Writer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.exoplatform.webui.application.WebuiRequestContext;

/**
 * Created by The eXo Platform SARL
 * Author : Tran The Trong
 *          trongtt@gmail.com
 * Jul 14, 2006  
 */

// TODO : TrongTT

public class UIFormDateTimeInput extends UIFormInputBase<String> {
  private DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
  
  public UIFormDateTimeInput(String name, String bindField, Date date) {
    super(name, bindField, String.class) ;
    if(date != null) value_ = formatter.format(date) ;
  }

  public void setDateValue(Date date) { value_ = formatter.format(date) ; }
  public Date getDateValue() throws ParseException {
    if(value_ != null) return formatter.parse(value_ + " 00:00:00") ;
    return null;
  }
  public Calendar getCalendar() throws ParseException {
    Calendar cal = Calendar.getInstance();
    cal.setTime(getDateValue()) ;
    return cal;
  }
  
  @SuppressWarnings("unused")
  public void decode(Object input, WebuiRequestContext context) throws Exception {    
    value_ = (String) input;
    if(value_ != null) value_ = value_.trim() ;
    System.out.println("\n\n" + getDateValue() + "\n\n");
  }

  public void processRender(WebuiRequestContext context) throws Exception {
    context.getJavascriptManager().importJavascript("eXo.webui.UICalendar") ;
    Writer w = context.getWriter();
    w.write("<input type='text' onclick='eXo.webui.UICalendar.show(this);' onkeyup='eXo.webui.UICalendar.show(this);' name='") ;
    w.write(getName()) ; w.write('\'') ;
    if(value_ != null && value_.length() > 0) {      
      w.write(" value='"); w.write(value_.toString()); w.write('\'');
    }
    w.write("/> ( mm/dd/yyyy [hh:mm:ss] )") ;
  }
}
