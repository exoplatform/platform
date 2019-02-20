/*
 * Copyright (C) 2019 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.platform.common.rest.services.CalendarPortlet;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.exoplatform.calendar.service.*;
import org.exoplatform.calendar.ws.CalendarRestApi;
import org.exoplatform.calendar.ws.bean.CalendarResource;
import org.exoplatform.calendar.ws.bean.EventResource;
import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.api.settings.SettingValue;
import org.exoplatform.commons.api.settings.data.Scope;
import org.exoplatform.platform.common.portlet.models.CalendarPortletUtils;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;
import org.exoplatform.social.rest.api.EntityBuilder;
import org.exoplatform.social.rest.api.RestUtils;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Calendar;
import java.util.stream.Collectors;

@Path("portlet/calendar")
@Produces(MediaType.APPLICATION_JSON)
public class CalendarPortletRestService implements ResourceContainer {

    private Comparator<CalendarEvent> eventsComparator = new Comparator<CalendarEvent>() {
        public int compare(CalendarEvent e1, CalendarEvent e2) {
            Long d1 = e1.getToDateTime().getTime() - e1.getFromDateTime().getTime();
            Long d2 = e2.getToDateTime().getTime() - e2.getFromDateTime().getTime();

            if ((d1 > CalendarPortletUtils.JOUR_MS) && (d2 > CalendarPortletUtils.JOUR_MS))
                return -(Math.round(e1.getFromDateTime().getTime() - e2.getToDateTime().getTime()));
            else if ((d1 > CalendarPortletUtils.JOUR_MS) && (d2 < CalendarPortletUtils.JOUR_MS)) return 1;
            else if ((d1 < CalendarPortletUtils.JOUR_MS) && (d2 > CalendarPortletUtils.JOUR_MS)) return -1;
            else if ((d1 < CalendarPortletUtils.JOUR_MS) && (d2 == CalendarPortletUtils.JOUR_MS)) return -1;
            else if ((d1 == CalendarPortletUtils.JOUR_MS) && (d2 < CalendarPortletUtils.JOUR_MS)) return 1;
            else if ((d1 > CalendarPortletUtils.JOUR_MS) && (d2 == CalendarPortletUtils.JOUR_MS)) return 1;
            else if ((d1 == CalendarPortletUtils.JOUR_MS) && (d2 > CalendarPortletUtils.JOUR_MS)) return -1;
            else if ((d1 == CalendarPortletUtils.JOUR_MS) && (d2 == CalendarPortletUtils.JOUR_MS)) return 0;
            else if ((d1 < CalendarPortletUtils.JOUR_MS) && (d2 < CalendarPortletUtils.JOUR_MS)) {
                if (e1.getFromDateTime().getTime() == e2.getFromDateTime().getTime()) {
                    if (d1 < d2) return 1;
                    if (d1 > d2) return -1;
                }
                ;
                return ((int) (e1.getFromDateTime().compareTo(e2.getFromDateTime())));

            }
            return 0;
        }
    };
    private Comparator<CalendarEvent> tasksComparator = new Comparator<CalendarEvent>() {
        public int compare(CalendarEvent e1, CalendarEvent e2) {
            if (((e2.getEventState().equals(CalendarEvent.NEEDS_ACTION)) && (e2.getToDateTime().compareTo(new Date()) < 0)) &&
                    ((e1.getEventState().equals(CalendarEvent.NEEDS_ACTION) && (e1.getToDateTime().compareTo(new Date()) >= 0)) || (!e1.getEventState().equals(CalendarEvent.NEEDS_ACTION)))) {
                return 1;
            } else if (((e1.getEventState().equals(CalendarEvent.NEEDS_ACTION)) && (e1.getToDateTime().compareTo(new Date()) < 0)) &&
                    ((e2.getEventState().equals(CalendarEvent.NEEDS_ACTION) && (e2.getToDateTime().compareTo(new Date()) >= 0)) || (!e2.getEventState().equals(CalendarEvent.NEEDS_ACTION)))) {
                return -1;
            } else if (((e1.getEventState().equals(CalendarEvent.NEEDS_ACTION)) && (e1.getToDateTime().compareTo(new Date()) < 0)) &&
                    (((e2.getEventState().equals(CalendarEvent.NEEDS_ACTION)) && (e2.getToDateTime().compareTo(new Date()) < 0)))) {
                return (int) (e2.getFromDateTime().getTime() - e1.getFromDateTime().getTime());
            } else return (int) (e2.getFromDateTime().getTime() - e1.getFromDateTime().getTime());
        }
    };

    private static final Log LOG = ExoLogger.getLogger(CalendarPortletRestService.class);

    private CalendarService calendarService;
    private SettingService settingService;
    private OrganizationService organizationService;
    private SpaceService spaceService;

    public CalendarPortletRestService(CalendarService calendarService, SettingService settingService,
                                      OrganizationService organizationService, SpaceService spaceService) {
        this.calendarService = calendarService;
        this.settingService = settingService;
        this.organizationService = organizationService;
        this.spaceService = spaceService;
    }

    /**
     * Get needed resources for calendar portlet
     *
     */
    @GET
    @Path("init")
    @RolesAllowed("users")
    @ApiOperation(value = "Gets all portlet objects",
            httpMethod = "GET",
            response = Response.class,
            notes = "This returns calendar portlet needed objects")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Request fulfilled")})
    public Response initCalendarObjects(@Context UriInfo uriInfo,
                                        @ApiParam(value = "Portal language, ex: en", required = false) @QueryParam("lang") String lang,
                                        @ApiParam(value = "Number of days to increment/decrement", required = false) @DefaultValue("0") @QueryParam("nbclick") String nbclick,
                                        @ApiParam(value = "Space id", required = false) @QueryParam("spaceId") String spaceId) throws Exception {
        List<CalendarEvent> eventsDisplayed = new ArrayList<CalendarEvent>();
        List<CalendarResource> displayedCalendar = new ArrayList<CalendarResource>();
        List<CalendarEvent> tasksDisplayed = new ArrayList<CalendarEvent>();
        Map<String, CalendarResource> displayedCalendarMap = new HashMap<String, CalendarResource>();
        String[] nonDisplayedCalendarList = null;
        String username = CalendarPortletUtils.getCurrentUser();
        Locale locale = new Locale("en");
        if (StringUtils.isNotBlank(lang) && !"en".equals(lang)) {
            locale = new Locale(lang);
        }
        String dp= formatDate(locale);
        DateFormat d = new SimpleDateFormat(dp);
        DateFormat dTimezone = DateFormat.getDateInstance(DateFormat.SHORT, locale);
        dTimezone.setCalendar(CalendarPortletUtils.getCurrentCalendar());
        Long date = new Date().getTime();
        int clickNumber = Integer.parseInt(nbclick);
        if (clickNumber != 0) date = incDecJour(date, clickNumber);
        Date currentTime = new Date(date);
        // get current date base on calendar setting
        CalendarSetting calSetting = CalendarPortletUtils.getCurrentUserCalendarSetting();
        String strTimeZone = calSetting.getTimeZone();
        dTimezone.setTimeZone(TimeZone.getTimeZone(strTimeZone));
        String date_act = dTimezone.format(currentTime);
        // Get Calendar object set to the date and time of the given Date object
        Calendar cal =CalendarPortletUtils.getCurrentCalendar();
        cal.setTime(currentTime);

        // Set time fields to zero
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        // Put it back in the Date object
        currentTime = cal.getTime();
        Date comp = currentTime;
        String defaultCalendarLabel = "Default";
        SettingValue settingNode = settingService.get(org.exoplatform.commons.api.settings.data.Context.USER, Scope.APPLICATION, CalendarPortletUtils.HOME_PAGE_CALENDAR_SETTINGS);
        if ((settingNode != null) && (settingNode.getValue().toString().split(":").length == 2)) {
            nonDisplayedCalendarList = settingNode.getValue().toString().split(":")[1].split(",");
        }
        String groupId = null;
        if (StringUtils.isNotBlank(spaceId)) {
            Space space = spaceService.getSpaceById(spaceId);
            if (space != null) {
                groupId = space.getGroupId();
            } else {
                return EntityBuilder.getResponse("", uriInfo, RestUtils.getJsonMediaType(), Response.Status.NOT_FOUND);
            }
        }
        List<CalendarEvent> userEvents = getEvents(username,cal, groupId);
        if ((userEvents != null) && (!userEvents.isEmpty())) {
            Iterator itr = userEvents.iterator();
            while (itr.hasNext()) {
                CalendarEvent event = (CalendarEvent) itr.next();
                Date from = d.parse(dTimezone.format(event.getFromDateTime()));
                Date to = d.parse(dTimezone.format(event.getToDateTime()));
                if ((event.getEventType().equals(CalendarEvent.TYPE_EVENT)) && (from.compareTo(d.parse(dTimezone.format(comp))) <= 0) && (to.compareTo(d.parse(dTimezone.format(comp))) >= 0)) {
                    if (groupId != null || !CalendarPortletUtils.contains(nonDisplayedCalendarList, event.getCalendarId())) {
                        org.exoplatform.calendar.service.Calendar calendar = calendarService.getUserCalendar(username, event.getCalendarId());
                        if (calendar == null) {
                            calendar = calendarService.getGroupCalendar(event.getCalendarId());
                        }
                        if(calendar.getGroups()==null) {
                            if (calendar.getId().equals(Utils.getDefaultCalendarId(username)) && calendar.getName().equals(calendarService.getDefaultCalendarName())) {
                                calendar.setName(defaultCalendarLabel);
                            }
                        }
                        eventsDisplayed.add(event);
                        if (!displayedCalendarMap.containsKey(calendar.getId())) {
                            CalendarResource calendarResource = new CalendarResource(calendar, getBasePath(uriInfo));
                            displayedCalendarMap.put(calendar.getId(), calendarResource);
                            displayedCalendar.add(calendarResource);
                        }
                    }
                } else if ((event.getEventType().equals(CalendarEvent.TYPE_TASK)) &&
                        (((from.compareTo(comp) <= 0) && (to.compareTo(comp) >= 0)) ||
                                ((event.getEventState().equals(CalendarEvent.NEEDS_ACTION)) && (to.compareTo(comp) < 0)))) {
                    tasksDisplayed.add(event);
                }
            }
            Collections.sort(eventsDisplayed, eventsComparator);
            Collections.sort(tasksDisplayed, tasksComparator);
        }
        Locale finalLocale = locale;
        List<EventResource> eventsDisplayedList = eventsDisplayed.stream()
                .map(i -> {
                    try {
                        return processFromToLabel(new EventResource(i, getBasePath(uriInfo)), finalLocale);
                    } catch (Exception e) {
                        return new EventResource();
                    }
                })
                .collect(Collectors.toList());
        List<EventResource> tasksDisplayedList = tasksDisplayed.stream()
                .map(i -> {
                    try {
                        return processFromToLabel(new EventResource(i, getBasePath(uriInfo)), finalLocale);
                    } catch (Exception e) {
                        return new EventResource();
                    }
                })
                .collect(Collectors.toList());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("displayedCalendars", displayedCalendar);
        jsonObject.put("calendarDisplayedMap", displayedCalendarMap);
        jsonObject.put("eventsDisplayedList", eventsDisplayedList);
        jsonObject.put("tasksDisplayedList", tasksDisplayedList);
        jsonObject.put("date_act", date_act);

        return EntityBuilder.getResponse(jsonObject.toString(), uriInfo, RestUtils.getJsonMediaType(), Response.Status.OK);
    }

    /**
     * Get calendar portlet settings
     *
     */
    @GET
    @Path("settings")
    @RolesAllowed("users")
    @ApiOperation(value = "Gets calendar portlet settings",
            httpMethod = "GET",
            response = Response.class,
            notes = "This returns calendar portlet settings")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Request fulfilled")})
    public Response getSettings(@Context UriInfo uriInfo) throws Exception {
        String username = CalendarPortletUtils.getCurrentUser();
        String defaultCalendarLabel = "Default";
        Iterator itr1 = getAllCal(username).iterator();
        String[] nonDisplayedCalendarList = getNonDisplayedCalendarIds();
        List<CalendarResource> calendarDisplayedList = new ArrayList<>();
        List<CalendarResource> calendarNonDisplayedList = new ArrayList<>();
        while (itr1.hasNext()) {
            org.exoplatform.calendar.service.Calendar c = (org.exoplatform.calendar.service.Calendar) itr1.next();
            if(c.getGroups()==null) {
                if (c.getId().equals(Utils.getDefaultCalendarId(username)) && c.getName().equals(calendarService.getDefaultCalendarName())) {
                    c.setName(defaultCalendarLabel);
                }
            }
            if (CalendarPortletUtils.contains(nonDisplayedCalendarList, c.getId())) {
                calendarNonDisplayedList.add(new CalendarResource(c, getBasePath(uriInfo)));
            } else {
                calendarDisplayedList.add(new CalendarResource(c, getBasePath(uriInfo)));
            }
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("allDisplayedCals", calendarDisplayedList);
        jsonObject.put("nonDisplayedCals", calendarNonDisplayedList);

        return EntityBuilder.getResponse(jsonObject.toString(), uriInfo, RestUtils.getJsonMediaType(), Response.Status.OK);
    }

    /**
     * Posts calendar portlet settings
     *
     */
    @POST
    @Path("settings")
    @RolesAllowed("users")
    @ApiOperation(value = "sets calendar portlet settings",
            httpMethod = "POST",
            response = Response.class,
            notes = "This sets calendar portlet settings")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Request fulfilled")})
    public Response saveSettings(@Context UriInfo uriInfo, String calIds) throws Exception {
        settingService.remove(org.exoplatform.commons.api.settings.data.Context.USER, Scope.APPLICATION, CalendarPortletUtils.HOME_PAGE_CALENDAR_SETTINGS);
        settingService.set(org.exoplatform.commons.api.settings.data.Context.USER, Scope.APPLICATION, CalendarPortletUtils.HOME_PAGE_CALENDAR_SETTINGS, SettingValue.create("NonDisplayedCalendar:" + calIds));

        return EntityBuilder.getResponse("", uriInfo, RestUtils.getJsonMediaType(), Response.Status.OK);
    }

    private List getAllCal(String username) throws Exception {
        List<org.exoplatform.calendar.service.Calendar> calList = calendarService.getUserCalendars(username, true);
        List<GroupCalendarData> lgcd = calendarService.getGroupCalendars(getUserGroups(username), true, username);
        List<String> calIds = new ArrayList<String>();
        for (GroupCalendarData g : lgcd) {
            for (org.exoplatform.calendar.service.Calendar c : g.getCalendars()) {
                if (!calIds.contains(c.getId())) {
                    calIds.add(c.getId());
                    calList.add(c);
                }
            }
        }
        return calList;
    }

    private String[] getNonDisplayedCalendarIds() {
        SettingValue settingNode = settingService.get(org.exoplatform.commons.api.settings.data.Context.USER, Scope.APPLICATION, CalendarPortletUtils.HOME_PAGE_CALENDAR_SETTINGS);
        if ((settingNode != null) && (settingNode.getValue().toString().split(":").length == 2)) {
            return settingNode.getValue().toString().split(":")[1].split(",");
        }
        return new String[]{};
    }

    private Long incDecJour(Long date, int days) {
        Calendar cal;
        cal = Calendar.getInstance();
        cal.setTimeInMillis(date);
        cal.add(Calendar.DAY_OF_MONTH, days);
        return cal.getTime().getTime();
    }

    private EventResource processFromToLabel(EventResource eventResource, Locale locale) throws Exception {
        CalendarEvent event = calendarService.getEventById(eventResource.getId());
        Date fromDateTime = event.getFromDateTime();
        Date toDateTime = event.getToDateTime();
        if (toDateTime.getTime() - fromDateTime.getTime() > 86399999) {
            DateFormat sdf1= DateFormat.getDateInstance(DateFormat.SHORT,locale);
            sdf1.setCalendar(CalendarPortletUtils.getInstanceOfCurrentCalendar());
            String from = sdf1.format(fromDateTime);
            SimpleDateFormat df = new SimpleDateFormat("yyyy");
            String year = df.format(fromDateTime);
            String[] dateSplit = from.split("/");
            if(dateSplit.length > 1) {
                from = dateSplit[0] + "/" + dateSplit[1] + "/" + year;
            }
            String to = sdf1.format(toDateTime);
            year = df.format(toDateTime);
            dateSplit = to.split("/");
            if(dateSplit.length > 1) {
                to = dateSplit[0] + "/" + dateSplit[1] + "/" + year;
            }
            eventResource.setFrom(from);
            eventResource.setTo(to);
        } else {
            DateFormat sdf2= DateFormat.getTimeInstance(DateFormat.SHORT,locale);
            sdf2.setCalendar(CalendarPortletUtils.getInstanceOfCurrentCalendar());
            String from = sdf2.format(fromDateTime);
            String to = sdf2.format(toDateTime);
            if(locale.getLanguage().equals("en")){
                if(from.indexOf("00")==2)   from=from.substring(0,1)+ from.substring(4);
                if(from.indexOf("00")==3)   from=from.substring(0,2)+ from.substring(5);
                if(to.indexOf("00")==2)   to=to.substring(0,1) + to.substring(4);
                if(to.indexOf("00")==3)   to=to.substring(0,2) + to.substring(5);
            }
            eventResource.setFrom(from);
            eventResource.setTo(to);
        }
        return eventResource;
    }

    private String getBasePath(UriInfo uriInfo) {
        StringBuilder path = new StringBuilder(uriInfo.getBaseUri().toString());
        path.append(CalendarRestApi.CAL_BASE_URI);
        return path.toString();
    }

    private List<CalendarEvent> getEvents(String username , Calendar cal, String groupId) {
        Map<String, Map<String, CalendarEvent>> recurrenceEventsMap   = new LinkedHashMap<String, Map<String, CalendarEvent>>();
        String[] calList = getCalendarsIdList(username, groupId);
        Calendar begin = CalendarPortletUtils.getBeginDay(cal);
        Calendar end =CalendarPortletUtils.getEndDay(cal) ;
        end.add(Calendar.MILLISECOND, -1);

        EventQuery eventQuery = new EventQuery();
        eventQuery.setFromDate(begin);
        eventQuery.setToDate(end);
        eventQuery.setOrderBy(new String[]{Utils.EXO_FROM_DATE_TIME});

        eventQuery.setCalendarId(calList);
        List<CalendarEvent> userEvents = null;
        try {
            userEvents = calendarService.getEvents(username, eventQuery, calList);
            String timezone = CalendarPortletUtils.getCurrentUserCalendarSetting().getTimeZone();
            List<CalendarEvent> originalRecurEvents = calendarService.getOriginalRecurrenceEvents(username, eventQuery.getFromDate(), eventQuery.getToDate(),calList);
            if (originalRecurEvents != null && originalRecurEvents.size() > 0) {
                Iterator<CalendarEvent> recurEventsIter = originalRecurEvents.iterator();
                while (recurEventsIter.hasNext()) {
                    CalendarEvent recurEvent = recurEventsIter.next();
                    // don't build virtual event when the original RecurrenceEvents occurs on that day
                    if (recurEvent.getFromDateTime().compareTo(begin.getTime()) >= 0) continue;
                    Map<String,CalendarEvent> tempMap = calendarService.getOccurrenceEvents(recurEvent, eventQuery.getFromDate(), eventQuery.getToDate(), timezone);
                    if (tempMap != null) {
                        recurrenceEventsMap.put(recurEvent.getId(), tempMap);
                        userEvents.addAll(tempMap.values());
                    }
                }
            }

        } catch (Exception e) {
            LOG.error("Error while checking User Events:" + e.getMessage(), e);
        }
        return userEvents;
    }

    private String[] getCalendarsIdList(String username, String groupId) {
        StringBuilder sb = new StringBuilder();
        List<GroupCalendarData> listgroupCalendar = null;
        List<org.exoplatform.calendar.service.Calendar> listUserCalendar = null;
        try {
            if (StringUtils.isNotBlank(groupId)) {
                listgroupCalendar = calendarService.getGroupCalendars(new String[]{groupId}, true, username);
            } else {
                listgroupCalendar = calendarService.getGroupCalendars(getUserGroups(username), true, username);
                listUserCalendar = calendarService.getUserCalendars(username, true);
            }
        } catch (Exception e) {
            LOG.error("Error while checking User Calendar :" + e.getMessage(), e);
        }
        for (GroupCalendarData g : listgroupCalendar) {
            for (org.exoplatform.calendar.service.Calendar c : g.getCalendars()) {
                sb.append(c.getId()).append(",");
            }
        }
        if (listUserCalendar != null) {
            for (org.exoplatform.calendar.service.Calendar c : listUserCalendar) {
                sb.append(c.getId()).append(",");
            }
        }
        String[] list = sb.toString().split(",");
        return list;
    }

    private String[] getUserGroups(String username) throws Exception {
        String [] groupsList;
        Object[] objs = organizationService.getGroupHandler().findGroupsOfUser(username).toArray();
        groupsList = new String[objs.length];
        for (int i = 0; i < objs.length; i++) {
            groupsList[i] = ((Group) objs[i]).getId();
        }
        return groupsList;
    }

    private String formatDate(Locale locale) {
        String datePattern = "";
        DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.SHORT, locale);
        // convert to unique pattern
        datePattern = ((SimpleDateFormat)dateFormat).toPattern();
        if (!datePattern.contains("yy")) {
            datePattern = datePattern.replaceAll("y", "yy");
        }
        if (!datePattern.contains("yyyy")) {
            datePattern = datePattern.replaceAll("yy", "yyyy");
        }
        if (!datePattern.contains("dd")) {
            datePattern = datePattern.replaceAll("d", "dd");
        }
        if (!datePattern.contains("MM")) {
            datePattern= datePattern.replaceAll("M", "MM");
        }
        return datePattern;
    }

}
