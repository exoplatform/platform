/*
 * Copyright (C) 2003-2011 eXo Platform SAS.
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
package org.exoplatform.calendar.client;

import java.util.Date;

import org.exoplatform.calendar.client.JSO.CalendarEvent;

import com.google.gwt.view.client.ProvidesKey;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * May 13, 2011  
 */
public class CalendarEventInfo implements Comparable<CalendarEventInfo> {
  
  String id;
  String summary;
  String description;
  String calendarId;
  Date fromDate;
  Date toDate;
  
  public CalendarEventInfo(String id, String summary, String description, String calendarId, Date fromDate, Date toDate) {
    this.id = id;
    this.summary = summary;
    this.description = description;
    this.calendarId = calendarId;
    this.fromDate = fromDate;
    this.toDate = toDate;
  }
  
  public CalendarEventInfo(CalendarEvent jsoEvent) {
    this.id = jsoEvent.getId();
    this.summary = jsoEvent.getSummary();
    this.description = jsoEvent.getDescription();
    this.calendarId = jsoEvent.getCalendarId();
    double longValue = Double.valueOf(jsoEvent.getFromDate());
    this.fromDate = new Date();
    this.fromDate.setTime((long) longValue);
    longValue = Double.valueOf(jsoEvent.getToDate());
    this.toDate = new Date();
    this.toDate.setTime((long) longValue);
  }
  
  public static final ProvidesKey<CalendarEventInfo> KEY_PROVIDER = new ProvidesKey<CalendarEventInfo>() {
    public Object getKey(CalendarEventInfo ce) {
      return ce == null ? null : ce.getId();
    }
  };
  
  public int compareTo(CalendarEventInfo ce) {
    return this.fromDate.compareTo(ce.getFromDate());
  }
  
  /**
   * @return the id
   */
  public String getId() {
    return id;
  }
  /**
   * @param id the id to set
   */
  public void setId(String id) {
    this.id = id;
  }
  /**
   * @return the summary
   */
  public String getSummary() {
    return summary;
  }
  /**
   * @param summary the summary to set
   */
  public void setSummary(String summary) {
    this.summary = summary;
  }
  /**
   * @return the description
   */
  public String getDescription() {
    return description;
  }
  /**
   * @param description the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }
  /**
   * @return the calendarId
   */
  public String getCalendarId() {
    return calendarId;
  }
  /**
   * @param calendarId the calendarId to set
   */
  public void setCalendarId(String calendarId) {
    this.calendarId = calendarId;
  }
  /**
   * @return the fromDate
   */
  public Date getFromDate() {
    return fromDate;
  }
  /**
   * @param fromDate the fromDate to set
   */
  public void setFromDate(Date fromDate) {
    this.fromDate = fromDate;
  }
  /**
   * @return the toDate
   */
  public Date getToDate() {
    return toDate;
  }
  /**
   * @param toDate the toDate to set
   */
  public void setToDate(Date toDate) {
    this.toDate = toDate;
  }
  
}
