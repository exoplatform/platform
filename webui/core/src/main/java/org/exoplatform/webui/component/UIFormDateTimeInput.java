/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.component;

import java.io.Writer;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.exoplatform.commons.utils.ISO8601;
import org.exoplatform.webui.application.WebuiRequestContext;

/**
 * Created by The eXo Platform SARL
 * Author : Dang Van Minh
 *          minhdv81@yahoo.com
 * Jul 14, 2006  
 */
public class UIFormDateTimeInput extends UIFormInputBase<String> {

  public static final String DAY_EXTENSION = "_dd";
  public static final String MONTH_EXTENSION = "_mm";
  public static final String YEAR_EXTENSION = "_yyyy";  
  public static final String HOUR_EXTENSION = "_hh";
  public static final String MINUTE_EXTENSION = "_min";

  protected GregorianCalendar calendar_ ;
  private boolean updateOnChangeAction_;
  private int minYearRange_ = GregorianCalendar.getInstance().get(GregorianCalendar.YEAR) - 10 ;
  private int maxYearRange_ = GregorianCalendar.getInstance().get(GregorianCalendar.YEAR) + 10 ;

  public UIFormDateTimeInput(String name, String bindField, Date date) {
    super(name, bindField, String.class) ;
    calendar_ = new GregorianCalendar();
    if(date == null) date = Calendar.getInstance().getTime(); 
    calendar_.setTime(date) ;
  }

  public UIFormDateTimeInput setUpdateOnChangeAction(boolean b) {
    updateOnChangeAction_ = b ;
    return this ;
  }

  public boolean getUpdateOnChangeAction() {
    return updateOnChangeAction_ ;
  }

  public Date getDateValue() { return  calendar_.getTime() ; }
  public void   setDateValue(Date input) {
    if(input == null) input = Calendar.getInstance().getTime(); 
    calendar_.setTime(input) ;
  }

  public String getValue() { return ISO8601.format(calendar_) ; }  
  public UIFormDateTimeInput  setValue(String value){
    calendar_ = new GregorianCalendar();
    calendar_.setGregorianChange(ISO8601.parse(value).getTime());
    return this;
  }  
  public Class<String> getTypeValue(){return String.class; }

  public Calendar getCalendar() { return calendar_ ; }

  public int getMinYearRange() { return minYearRange_ ; }
  public void setMinYearRange(int year) {
    if (year > maxYearRange_) return;
    if (calendar_.get(GregorianCalendar.YEAR) < year) return;
    minYearRange_ = year ; 
  }

  public int getMaxYearRange() { return maxYearRange_ ; }  
  public void setMaxYearRange(int year) { 
    if (year < minYearRange_) return;
    if (calendar_.get(GregorianCalendar.YEAR) > year) return;
    maxYearRange_ = year ;
  }

  public UIFormDateTimeInput addTime(Date date) {
    calendar_.setTime(date);
    if (calendar_.get(GregorianCalendar.YEAR) < minYearRange_ || 
        calendar_.get(GregorianCalendar.YEAR) > maxYearRange_) {
      minYearRange_ = GregorianCalendar.getInstance().get(GregorianCalendar.YEAR) - 10 ;
      maxYearRange_ = GregorianCalendar.getInstance().get(GregorianCalendar.YEAR) + 10 ;
    }
    return this ;
  }

  @SuppressWarnings("unused")
  public void decode(Object input, WebuiRequestContext context) throws Exception {    
    String day = context.getRequestParameter(name+DAY_EXTENSION) ;
    String month = context.getRequestParameter(name+MONTH_EXTENSION) ;
    String year = context.getRequestParameter(name+YEAR_EXTENSION) ;
    String hour = context.getRequestParameter(name + HOUR_EXTENSION);
    String minute = context.getRequestParameter(name + MINUTE_EXTENSION);
    if( day == null || month == null || year == null) return;
    int dayValue = Integer.parseInt(day);
    int monthValue = Integer.parseInt(month);
    int yearValue = Integer.parseInt(year);
    int hourValue = Integer.parseInt(hour);
    int minuteValue = Integer.parseInt(minute);
    GregorianCalendar gCal = new GregorianCalendar(yearValue, monthValue, dayValue, hourValue, minuteValue);
    addTime(gCal.getTime());   
  }

  public void processRender(WebuiRequestContext context) throws Exception{
    Writer w = context.getWriter();
    int day = calendar_.get(Calendar.DAY_OF_MONTH);
    int month = calendar_.get(Calendar.MONTH);
    int year = calendar_.get(Calendar.YEAR);    
    renderCalendarField(w, DAY_EXTENSION, Calendar.DAY_OF_MONTH, day);
    renderCalendarField(w, MONTH_EXTENSION, Calendar.MONTH, month);
    renderCalendarField(w, YEAR_EXTENSION, Calendar.YEAR, year);

    int hour = calendar_.get(Calendar.HOUR);
    int minute = calendar_.get(Calendar.MINUTE);
//    w.append("<div style='float:left; width: 20px'>&nbsp;</div>");      what is the affection of this DIV ?
    renderCalendarField(w, HOUR_EXTENSION, Calendar.HOUR, hour);
    renderCalendarField(w, MINUTE_EXTENSION, Calendar.MINUTE, minute);
  }

  private void renderCalendarField(Writer w, String ext, int field, int value) throws Exception {
    w.append("<select style=\"width: auto;\" name='"); 
    w.append(name).append(ext).append("'");
    if(!enable_ ) w.append(" disabled ");
    w.append(">\n") ;

    int min = field == Calendar.YEAR ?  minYearRange_ : calendar_.getMinimum(field);
    int max = field == Calendar.YEAR ?  maxYearRange_ : calendar_.getMaximum(field); 

    DecimalFormat df = new DecimalFormat("00");
    for(int i = min; i <= max; i++){       
      w.append("<option value=\"").append(String.valueOf(i)).append('\"');
      if(i == value) w.append(" selected ");
      w.append('>'); 
      if(field == Calendar.MONTH){
        w.append(df.format(i+1)).append("</option>\n");
      }else{
        w.append(df.format(i)).append("</option>\n");
      }     
    }   
    w.append("</select>\n") ;
  }

}
