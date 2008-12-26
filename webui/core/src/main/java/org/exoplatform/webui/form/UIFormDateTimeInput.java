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
package org.exoplatform.webui.form;

import java.io.Writer;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

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
  /**
   * The Date Pattern. Ex: dd/mm/yyyy
   */
  private String datePattern_ ;
  /**
   * The date
   */
  private Date date ;
  /**
   * List of month's name
   */
  private String[] months_ ;
  
  public UIFormDateTimeInput(String name, String bindField, Date date, boolean isDisplayTime) {
    super(name, bindField, String.class) ;
    this.date = date;
    setDisplayTime(isDisplayTime);
    
    WebuiRequestContext requestContext = WebuiRequestContext.getCurrentInstance();
    formatPattern(requestContext.getLocale()) ;
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
  }
  
  public void setCalendar(Calendar date) { 

    WebuiRequestContext requestContext = WebuiRequestContext.getCurrentInstance();
    formatPattern(requestContext.getLocale()) ;
    this.date = date.getTime();
	  if(date != null){
		  value_ = dateFormat_.format(date.getTime()) ;
	  } else {
		  value_ = null;
	  }
	   
  }
  public Calendar getCalendar() {
    
    try {
      Calendar calendar = new GregorianCalendar() ;
      calendar.setTime(dateFormat_.parse(value_ + " 0:0:0")) ;
      return calendar ;
    } catch (ParseException e) {
      return null;
    }
  }
  
  private void setDatePattern_(String datePattern_) {
    this.datePattern_ = datePattern_;
  }

  public String getDatePattern_() {
    return datePattern_;
  }

  private void formatPattern(Locale locale) {
    if(isDisplayTime_) {
      dateFormat_ = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM, locale);
    } else {
      dateFormat_ = SimpleDateFormat.getDateInstance(DateFormat.SHORT, locale);
    }
    // convert to unique pattern
    
    setDatePattern_(((SimpleDateFormat)dateFormat_).toPattern());
    
    if (!getDatePattern_().contains("yy")) {
      setDatePattern_(getDatePattern_().replaceAll("y", "yy"));
    }
    if (!getDatePattern_().contains("yyyy")) {
      setDatePattern_(getDatePattern_().replaceAll("yy", "yyyy"));
    }
    if (!getDatePattern_().contains("dd")) {
      setDatePattern_(getDatePattern_().replaceAll("d", "dd"));
    }
    if (!getDatePattern_().contains("MM")) {
      setDatePattern_(getDatePattern_().replaceAll("M", "MM"));
    }
    setDatePattern_(getDatePattern_().replaceAll("H", "h"));
    if (!getDatePattern_().contains("hh")) {
      setDatePattern_(getDatePattern_().replaceAll("h", "hh"));
    }
    if (getDatePattern_().contains("a")) {
      setDatePattern_(getDatePattern_().replaceAll("a", ""));
    }
    
    dateFormat_ = new SimpleDateFormat(getDatePattern_());

    DateFormatSymbols symbols = new DateFormatSymbols(locale); 
    months_ = symbols.getMonths(); 
  }
  
  @SuppressWarnings("unused")
  public void decode(Object input, WebuiRequestContext context) throws Exception {
    if(input != null) value_ = ((String)input).trim();
  }

  public void processRender(WebuiRequestContext context) throws Exception {
    
    WebuiRequestContext requestContext = WebuiRequestContext.getCurrentInstance();
    formatPattern(requestContext.getLocale()) ;
    String monthNames_ = ""; 
    for (String month : months_) {
      // remove empty element
      if (!month.equals("")) {
        monthNames_ += month + ",";
      }
    }
    
    if(date != null) {
      value_ = dateFormat_.format(date) ;
    } else {
      value_ = "";
    }
    
    context.getJavascriptManager().importJavascript("eXo.webui.UICalendar") ;
    Writer w = context.getWriter();
    
    w.write("<input type='text' onfocus='eXo.webui.UICalendar.init(this,") ;
    w.write(String.valueOf(isDisplayTime_));
    w.write(",\"");
    w.write( getDatePattern_() );
    w.write("\"");
    w.write(",\"");
    w.write( value_.toString() );
    w.write("\"");
    w.write(",\"");
    w.write( monthNames_ );
    w.write("\"");
    w.write(");' onkeyup='eXo.webui.UICalendar.show();' name='") ;
    w.write(getName()) ; w.write('\'') ;
    if(value_ != null && value_.length() > 0) {      
      w.write(" value='"); w.write(value_.toString()); w.write('\'');
    }
    w.write(" onmousedown='event.cancelBubble = true' />") ;
  }
}
