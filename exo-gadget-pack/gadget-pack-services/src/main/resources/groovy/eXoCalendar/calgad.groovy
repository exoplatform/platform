
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.Response.Status;

import org.exoplatform.calendar.service.Calendar;
import org.exoplatform.calendar.service.CalendarEvent;
import org.exoplatform.calendar.service.CalendarService;
import org.exoplatform.calendar.service.EventQuery;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.manager.IdentityManager;

@Path("/calgad")
class CalGadService {
  private static final CacheControl cc;
  
  private String[] fake = { "xxxxxxxxxxxx" };
  
  static {
    cc = new CacheControl();
    cc.setNoCache(true);
    cc.setNoStore(true);
  }

  private CalendarService           calendarService;

  private IdentityManager           identityManager;

  public CalGadService(CalendarService calendarService, IdentityManager identityManager) {
    this.calendarService = calendarService;
    this.identityManager = identityManager;
  }

  private String getUsername(SecurityContext sc, String viewer) {
    String username = null;
    Principal p = sc.getUserPrincipal();
    if (p == null) {
      if (viewer != null) {
        Identity identity = this.identityManager.getIdentity(viewer);
        username = identity.getRemoteId();
      }
    } else {
      username = p.getName();
    }

    return username;
  }

  @GET
  @Path("calendars/personal")
  public Response getPersonalCalendars(@QueryParam("opensocial_viewer_id") String viewer, @Context SecurityContext sc) {
    String username = getUsername(sc, viewer);
    if (username == null) {
      return Response.status(Status.UNAUTHORIZED).build(); // unauthorized
    }

    JsonData<Calendar> calendarsData = new JsonData<Calendar>();
    try {
      List<Calendar> calendars = calendarService.getUserCalendars(username, false);
      
      List<Calendar> returnList = new ArrayList<Calendar>();
      for (Calendar c : calendars) {
        Calendar cal = new Calendar();
        cal.setId(c.getId());
        cal.setName(c.getName());
        cal.setCalendarColor(c.getCalendarColor());
        returnList.add(cal);
      }
      
      calendarsData.setList(returnList);
    } catch (Exception e) {
      e.printStackTrace();
      return Response.serverError().build();
    }

    return Response.ok(calendarsData, MediaType.APPLICATION_JSON).cacheControl(cc).build();
  }

  @GET
  @Path("events/{from: \\d+}/{to: \\d+}/{calids: .*}/")
  public Response getPersonalEvents(@QueryParam("opensocial_viewer_id") String viewer,
                                    @PathParam("from") long from,
                                    @PathParam("to") long to,
                                    @PathParam("calids") String cals,
                                    @Context SecurityContext sc) {
    String username = getUsername(sc, viewer);
    if (username == null) {
      return Response.status(Status.UNAUTHORIZED).build(); // unauthorized
    }
    EventQuery eventQuery = new EventQuery();
    eventQuery.setEventType(CalendarEvent.TYPE_EVENT);
    if (cals.length() > 0) {
      String[] pieces = cals.split("/");
      List<String> l = new ArrayList<String>();
      for (String s : pieces) {
        if (s.length() > 0) l.add(s);
      }
      eventQuery.setCalendarId(l.toArray(pieces));
    } else {
      eventQuery.setCalendarId(fake);
    }
    
    java.util.Calendar c = java.util.Calendar.getInstance();
    if (from > 0) {
      c.setTimeInMillis(from);
      eventQuery.setFromDate(c);
    }
    c = java.util.Calendar.getInstance();
    if (to > 0) {
      c.setTimeInMillis(to);
      eventQuery.setToDate(c);
    }

    String[] orderBy = new String[1];
    orderBy[0] = "exo:fromDateTime";
    eventQuery.setOrderBy(orderBy);
    
    JsonData<CalendarEvent> jsonData = new JsonData<CalendarEvent>();
    try {
      List<CalendarEvent> eventList = calendarService.getUserEvents(username, eventQuery);
      List<CalendarEvent> returnList = new ArrayList<CalendarEvent>();
      for (CalendarEvent ce : eventList) {
        CalendarEvent event = new CalendarEvent();
        event.setId(ce.getId());
        event.setSummary(ce.getSummary());
        event.setDescription(ce.getDescription());
        event.setCalendarId(ce.getCalendarId());
        event.setFromDateTime(ce.getFromDateTime());
        event.setToDateTime(ce.getToDateTime());
        returnList.add(event);
      }
      jsonData.setList(returnList);
    } catch (Exception e) {
      e.printStackTrace();
      return Response.serverError().build();
    }

    return Response.ok(jsonData, MediaType.APPLICATION_JSON).cacheControl(cc).build();
  }

  @GET
  @Path("hdays/{from: \\d+}/{to: \\d+}/{calids: .*}/")
  public Response getHighlightDays(@QueryParam("opensocial_viewer_id") String viewer,
                                   @PathParam("from") long from,
                                   @PathParam("to") long to,
                                   @PathParam("calids") String cals,
                                   @Context SecurityContext sc) {
    String username = getUsername(sc, viewer);
    if (username == null) {
      return Response.status(Status.UNAUTHORIZED).build(); // unauthorized
    }
    EventQuery eventQuery = new EventQuery();
    eventQuery.setEventType(CalendarEvent.TYPE_EVENT);
    java.util.Calendar c = java.util.Calendar.getInstance();
    if (from > 0) {
      c.setTimeInMillis(from);
      eventQuery.setFromDate(c);
    }
    c = java.util.Calendar.getInstance();
    if (to > 0) {
      c.setTimeInMillis(to);
      eventQuery.setToDate(c);
    }
    if (cals.length() > 0) {
      String[] pieces = cals.split("/");
      List<String> l = new ArrayList<String>();
      for (String s : pieces) {
        if (s.length() > 0) l.add(s);
      }
      eventQuery.setCalendarId(l.toArray(pieces));
    } else {
      eventQuery.setCalendarId(fake);
    }
    
    JsonData<Integer> jsonData = new JsonData<Integer>();
    try {
      Map<Integer, String> days = calendarService.searchHightLightEvent(username, eventQuery, fake);
      jsonData.setList(new ArrayList<Integer>(days.keySet()));
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return Response.serverError().build();
    }
    return Response.ok(jsonData, MediaType.APPLICATION_JSON).cacheControl(cc).build();
  }

  @POST
  @Path("addevent")
  public Response addEvent(@QueryParam("opensocial_viewer_id") String viewer,
                           @FormParam("calId") String calId,
                           @FormParam("eventTitle") String eTitle, 
                           @FormParam("from") long from,
                           @FormParam("to") long to, 
                           @Context SecurityContext sc) {
    String username = getUsername(sc, viewer);
    if (username == null) {
      return Response.status(Status.UNAUTHORIZED).build(); // unauthorized
    }
    
    if (from >= to || from < 0) {
      return Response.status(Status.BAD_REQUEST).build(); // bad request
    }
    
    CalendarEvent event = new CalendarEvent();
    event.setCalendarId(calId);
    event.setSummary(eTitle);
    event.setEventType(CalendarEvent.TYPE_EVENT);
    java.util.Calendar calendar = java.util.Calendar.getInstance();
    calendar.setTimeInMillis(from);
    event.setFromDateTime(calendar.getTime());
    calendar.setTimeInMillis(to);
    event.setToDateTime(calendar.getTime());
    try {
      calendarService.saveUserEvent(username, calId, event, true);
    } catch (Exception e) {
      e.printStackTrace();
      return Response.serverError().build();
    }
    return Response.ok().cacheControl(cc).build();
  }

}

class JsonData<T> {
  private Collection<T> list;

  public Collection<T> getList() {
    return list;
  }

  public void setList(Collection<T> list) {
    this.list = list;
  }

}

