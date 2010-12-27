package org.exoplatform.calendar;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.exoplatform.api.calendar.CalendarException;
import org.exoplatform.api.calendar.CalendarsService;
import org.exoplatform.api.calendar.Event;
import org.exoplatform.calendar.service.Calendar;
import org.exoplatform.calendar.service.CalendarEvent;
import org.exoplatform.calendar.service.EventQuery;

public class CalendarsServiceImpl implements CalendarsService {

	private org.exoplatform.calendar.service.CalendarService calendarService;

	public CalendarsServiceImpl(
			org.exoplatform.calendar.service.CalendarService calendarService) {
		this.calendarService = calendarService;
	}

	public Event addEvent(String username, Event event)
			throws CalendarException {
		try {
			String calendarId = getDefaultCalendarId(username);
			CalendarEvent evt = CalendarFactory.toCalendarEvent(event);
			calendarService.saveUserEvent(username, calendarId, evt, true);
			return CalendarFactory.toEvent(evt);
		} catch (Exception e) {
			throw new CalendarException(e);
		}
	}


	String getDefaultCalendarId(String username) throws Exception {
		List<Calendar> calendars = calendarService.getUserCalendars(username,
				true);
		return calendars.get(0).getId();
	}
	


	public List<Event> getNextEvents(String username, int limit)
			throws CalendarException {
		try {
			List<Event> result = null;
			String calendarId = getDefaultCalendarId(username);
			EventQuery query = nextEventsQuery(limit, calendarId);
			List<CalendarEvent> events = calendarService.getUserEvents(username, query);
			result = CalendarFactory.toEvents(events);
			return result;

		} catch (Exception e) {
			throw new CalendarException(e);
		}
	}


	EventQuery newEventQuery(String calendarId) {
		EventQuery eventQuery = new EventQuery();
		eventQuery.setCalendarId(new String[] { calendarId });
		//eventQuery.setEventType(CalendarEvent.TYPE_EVENT);
		return eventQuery;
	}

	private EventQuery nextEventsQuery(int limit, String calendarId) {
		EventQuery eventQuery =  newEventQuery(calendarId);
		eventQuery.setLimitedItems(limit);
		GregorianCalendar now = new GregorianCalendar();
		now.setTime(new Date());
		eventQuery.setFromDate(now);
		return eventQuery;
	}



}
