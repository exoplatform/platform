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

import java.util.List;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * May 13, 2011  
 */
public class EventDatabase {
  
  private ListDataProvider<CalendarEventInfo> dataProvider;
  
  private static EventDatabase instance;
  
  private EventDatabase() {
    dataProvider = new ListDataProvider<CalendarEventInfo>();
  }
  
  public static EventDatabase get() {
    if (instance == null) {
      instance = new EventDatabase();
    }
    return instance;
  }
  
  public void addEvent(CalendarEventInfo event) {
    List<CalendarEventInfo> list = dataProvider.getList();
    list.remove(event);
    list.add(event);
  }
  
  public void addDataDisplay(HasData<CalendarEventInfo> display) {
    dataProvider.addDataDisplay(display);
  }
  
  public ListDataProvider<CalendarEventInfo> getDataProvider() {
    return dataProvider;
  }
  
  /**
   * Refresh all displays.
   */
  public void refreshDisplays() {
    dataProvider.refresh();
  }
  
  public void cleanUp() {
    dataProvider.getList().clear();
  }
}

