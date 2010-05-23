package org.exoplatform.calendar;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.jcr.Node;

import org.exoplatform.api.calendar.CalendarsService;
import org.exoplatform.api.calendar.Event;
import org.exoplatform.calendar.service.CalendarEvent;
import org.exoplatform.calendar.service.CalendarService;
import org.exoplatform.calendar.service.CalendarSetting;
import org.exoplatform.calendar.service.EventQuery;
import org.exoplatform.commons.testing.jcr.AbstractJCRTestCase;
import org.exoplatform.component.test.ConfigurationUnit;
import org.exoplatform.component.test.ConfiguredBy;
import org.exoplatform.component.test.ContainerScope;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ObjectParameter;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.ext.hierarchy.NodeHierarchyCreator;
import org.exoplatform.services.jcr.ext.hierarchy.impl.AddPathPlugin;
import org.exoplatform.services.jcr.ext.hierarchy.impl.HierarchyConfig;
import org.exoplatform.services.jcr.ext.hierarchy.impl.HierarchyConfig.JcrPath;
import org.testng.annotations.Test;

import com.ibm.icu.util.Calendar;

@ConfiguredBy( {
		@ConfigurationUnit(scope = ContainerScope.PORTAL, path = "conf/jcr/jcr-configuration.xml"),
		@ConfigurationUnit(scope = ContainerScope.PORTAL, path = "api-test-configuration.xml"),
		@ConfigurationUnit(scope = ContainerScope.PORTAL, path = "calendar-configuration.xml") })
public class TestCalendarsServiceImpl extends AbstractJCRTestCase {

	public TestCalendarsServiceImpl() {

	}

	@Test
	public void testSetup() {
		CalendarService app = getComponent(CalendarService.class);
		assertNotNull(app);

		CalendarsService api = getComponent(CalendarsService.class);
		assertNotNull(api);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testAddNewEvent() throws Exception {
		CalendarService app = getComponent(CalendarService.class);
		String username = "foo";
		createUserAppData(username); // normally done by a listener
		app.initNewUser(username, new CalendarSetting());
		CalendarsServiceImpl apiImpl = (CalendarsServiceImpl) getComponent(CalendarsService.class);
		GregorianCalendar cal = new GregorianCalendar();
		cal.set(2010, 06, 20);
		Date start = cal.getTime();
		cal.add(Calendar.DAY_OF_YEAR, 1);
		Date end = cal.getTime();
		Event event = CalendarFactory.newEvent("event1", "", start, end, "loc");
		event = apiImpl.addEvent(username, event);
		String calId = apiImpl.getDefaultCalendarId(username);
		EventQuery eventQuery = apiImpl.newEventQuery(calId);
		List<CalendarEvent> events = app.getUserEvents(username, eventQuery);
		assertEquals(events.size(), 1);
		assertTrue(eventsEqual(event, events.get(0)));
	}

	@Test
	public void testGetNextEvents() throws Exception {
		CalendarService app = getComponent(CalendarService.class);
		String username = "bar";
		createUserAppData(username); // normally done by a listener
		app.initNewUser(username, new CalendarSetting());
		CalendarsServiceImpl apiImpl = (CalendarsServiceImpl) getComponent(CalendarsService.class);
		GregorianCalendar start = new GregorianCalendar();
		
		start.add(Calendar.DAY_OF_YEAR, -1);
		start.setTime(new Date());
		GregorianCalendar end = new GregorianCalendar();
		end.setTime(start.getTime());
		end.add(Calendar.DAY_OF_YEAR, 15);
		
		// yesterday
		apiImpl.addEvent(username, CalendarFactory.newEvent("event1", "", start.getTime(), end.getTime(), "loc"));
		
		//
		start.add(Calendar.DAY_OF_YEAR, 2);
		apiImpl.addEvent(username, CalendarFactory.newEvent("event2", "", start.getTime(), end.getTime(), "loc"));
		
		start.add(Calendar.DAY_OF_YEAR, 1);
		apiImpl.addEvent(username, CalendarFactory.newEvent("event3", "", start.getTime(), end.getTime(), "loc"));
		
		List<Event> events = apiImpl.getNextEvents(username, 3);
		assertEquals(events.size(), 2);
		
		start.add(Calendar.DAY_OF_YEAR, 1);
		apiImpl.addEvent(username, CalendarFactory.newEvent("event4", "", start.getTime(), end.getTime(), "loc"));
		
		events = apiImpl.getNextEvents(username, 3);
		assertEquals(events.size(), 3);
		
		start.add(Calendar.DAY_OF_YEAR, 1);
		apiImpl.addEvent(username, CalendarFactory.newEvent("event5", "", start.getTime(), end.getTime(), "loc"));
		
		events = apiImpl.getNextEvents(username, 3);
		assertEquals(events.size(), 3);		
		
		events = apiImpl.getNextEvents(username, 5);
		assertEquals(events.size(), 4);
		
	}

	private void createUserAppData(String username) throws Exception {
		NodeHierarchyCreator creator = getComponent(NodeHierarchyCreator.class);
		SessionProvider provider = null;
		try {
			provider = SessionProvider.createSystemProvider();
			Node node = creator.getUserNode(provider, username);
			node.addNode("ApplicationData", "nt:unstructured");
			HierarchyConfig config = new HierarchyConfig();
			config.setRepository("repository");
			config.setWorksapces(Arrays.asList("portal-test"));
			JcrPath jcrPath = new JcrPath();
			jcrPath.setAlias("userApplicationData");
			jcrPath.setPath("ApplicationData");
			config.setJcrPaths(Arrays.asList(jcrPath));
			InitParams params = new InitParams();
			ObjectParameter param = new ObjectParameter();
			param.setObject(config);
			params.addParameter(param);
			AddPathPlugin plugin = new AddPathPlugin(params);
			creator.addPlugin(plugin);

		} finally {
			provider.close();
		}
	}

	private boolean eventsEqual(Event event, CalendarEvent calendarEvent) {
		return (event.getDetails().equals(calendarEvent.getDescription())
				&& event.getEndsAt().equals(calendarEvent.getToDateTime())
				&& event.getLocation().equals(calendarEvent.getLocation())
				&& event.getStartAt().equals(calendarEvent.getFromDateTime()) && event
				.getSummary().equals(calendarEvent.getSummary()));
	}

}
