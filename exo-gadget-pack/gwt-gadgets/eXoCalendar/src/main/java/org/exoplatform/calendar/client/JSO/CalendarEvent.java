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
package org.exoplatform.calendar.client.JSO;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * May 13, 2011  
 */
public class CalendarEvent extends JavaScriptObject {

  protected CalendarEvent() {};
  
  public final native String getId() /*-{
    return this.id;
  }-*/;
  
  public final native String getSummary() /*-{
    return this.summary;
  }-*/;
  
  public final native String getDescription() /*-{
    return this.description;
  }-*/;
  
  public final native String getCalendarId() /*-{
    return this.calendarId;
  }-*/;
  
  public final native String getFromDate() /*-{
    return this.fromDateTime.time;
  }-*/;
  
  public final native String getToDate() /*-{
    return this.toDateTime.time;
  }-*/;
}
