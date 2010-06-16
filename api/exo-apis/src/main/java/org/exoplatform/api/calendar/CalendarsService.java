/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
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
package org.exoplatform.api.calendar;

import java.util.List;

/**
 * API for eXo Calendar. Allows to manipulate calendars, tasks and events
 * @author patricelamarque
 *
 */
public interface CalendarsService {
  
  /**
   * Adds a new event to someone's default calendar
   * @param username owner of the calendar where the event will be added
   * @param event the event to add
   * @return the event created in the calendar
   */
  Event addEvent(String username, Event event);
  
  /**
   * Get the next events starting from now to someone's default calendar
   * @param username owner of the calendar
   * @param limit maximum number of events to fetch
   * @return a list of events scheduled to default calendar in chronological order.
   */
  List<Event> getNextEvents(String username, int limit);

}
