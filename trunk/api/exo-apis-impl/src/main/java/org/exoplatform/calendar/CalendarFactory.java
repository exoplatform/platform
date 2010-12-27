package org.exoplatform.calendar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.exoplatform.api.calendar.Event;
import org.exoplatform.calendar.service.CalendarEvent;

/**
 * Factory for Calendar objects
 * @author patricelamarque
 *
 */
public class CalendarFactory {

	public static Event toEvent(CalendarEvent evt) {
		EventImpl event = newEvent();
		event.details = evt.getDescription();
		event.endsAt = evt.getToDateTime();
		event.id = evt.getId();
		event.location = evt.getLocation();
		event.startsAt = evt.getFromDateTime();
		event.summary = evt.getSummary();
		return event;
	}

	public static CalendarEvent toCalendarEvent(Event event) {
		CalendarEvent evt = new CalendarEvent();
		evt.setCalendarId(event.getId());
		evt.setSummary(event.getSummary());
		evt.setDescription(event.getDetails());
		evt.setLocation(event.getLocation());
		evt.setFromDateTime(event.getStartAt());
		evt.setToDateTime(event.getEndsAt());
		return evt;
	}

	public static List<Event> toEvents(List<CalendarEvent> events) {
		List<Event> results = new ArrayList<Event>();
		for (CalendarEvent calendarEvent : events) {
			results.add(CalendarFactory.toEvent(calendarEvent));
		}
		return results;
	}

	public static EventImpl newEvent() {
		return new EventImpl();
	}
	
	public static EventImpl newEvent(String summary, String details, Date startsAt, Date endsAt, String location) {
		EventImpl event = new EventImpl();
		event.summary = summary;
		event.details = details;
		event.startsAt = startsAt;
		event.endsAt = endsAt;
		event.location = location;
		return event;
	}

	public static class EventImpl implements Event {

		protected String id;
		protected String details;
		protected Date endsAt;
		protected Date startsAt;
		protected String location;
		protected String summary;

		public EventImpl() {

		}

		public String getId() {
			return id;
		}

		public String getDetails() {
			return details;
		}

		public Date getEndsAt() {
			return endsAt;
		}

		public Date getStartAt() {
			return startsAt;
		}

		public String getLocation() {
			return location;
		}

		public String getSummary() {
			return summary;
		}
	}
}
