/**
 * Copyright ( C ) 2012 eXo Platform SAS.
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

package org.exoplatform.platform.portlet.juzu.calendar;

import juzu.*;
import juzu.impl.common.Tools;
import juzu.template.Template;
import org.apache.commons.lang.ArrayUtils;
import org.exoplatform.calendar.service.*;
import org.exoplatform.calendar.service.impl.NewUserListener;
import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.api.settings.SettingValue;
import org.exoplatform.commons.api.settings.data.Context;
import org.exoplatform.commons.api.settings.data.Scope;
import org.exoplatform.commons.juzu.ajax.Ajax;
import org.exoplatform.platform.portlet.juzu.calendar.models.CalendarPortletUtils;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.web.application.RequestContext;
import org.gatein.common.text.EntityEncoder;

import javax.inject.Inject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Calendar;

/**
 * @author <a href="fbradai@exoplatform.com">Fbradai</a>
 * @date 13/12/12
 */

@SessionScoped
public class CalendarPortletController {

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
    List<org.exoplatform.calendar.service.Calendar> calendarDisplayedList = new ArrayList<org.exoplatform.calendar.service.Calendar>();
    List<org.exoplatform.calendar.service.Calendar> calendarNonDisplayedList = new ArrayList<org.exoplatform.calendar.service.Calendar>();
    List<CalendarEvent> eventsDisplayedList = new ArrayList<CalendarEvent>();
    List<org.exoplatform.calendar.service.Calendar> displayedCalendar = new ArrayList<org.exoplatform.calendar.service.Calendar>();
    Map<String, org.exoplatform.calendar.service.Calendar> displayedCalendarMap = new HashMap<String, org.exoplatform.calendar.service.Calendar>();
    List<CalendarEvent> tasksDisplayedList = new ArrayList<CalendarEvent>();
    List<org.exoplatform.calendar.service.Calendar> searchResult = new ArrayList<org.exoplatform.calendar.service.Calendar>();
    String[] nonDisplayedCalendarList = null;
    String nbclick = "0";

    @Inject
    CalendarService calendarService_;
    @Inject
    OrganizationService organization_;
    @Inject
    SettingService settingService_;

    private static final Log LOG = ExoLogger.getLogger(CalendarPortletController.class);

    @Inject
    @Path("calendar.gtmpl")
    Template calendar;

    @Inject
    @Path("settings.gtmpl")
    Template setting;

    @Inject
    @Path("search.gtmpl")
    Template search;

    @Inject
    @Path("calendarPortletContainer.gtmpl")
    org.exoplatform.platform.portlet.juzu.calendar.templates.calendarPortletContainer container;

    @View
    public Response.Content index() {
        return container.ok();
    }

        // Format the Date pattern
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

    @Ajax
    @Resource
    public Response.Content calendarHome() throws Exception {

        displayedCalendar.clear();
        displayedCalendarMap.clear();
        tasksDisplayedList.clear();
        eventsDisplayedList.clear();
        String date_act = null;
        String username = CalendarPortletUtils.getCurrentUser();
        Locale locale =  Util.getPortalRequestContext().getLocale();
        String dp= formatDate(locale);
        DateFormat d = new SimpleDateFormat(dp);
        DateFormat dTimezone = DateFormat.getDateInstance(DateFormat.SHORT, locale);
        dTimezone.setCalendar(CalendarPortletUtils.getCurrentCalendar());
        Long date = new Date().getTime();
        int clickNumber = Integer.parseInt(nbclick);
        if (clickNumber != 0) date = incDecJour(date, clickNumber);
        Date currentTime = new Date(date);
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
        date_act = d.format(currentTime);
        Date comp = currentTime;
        String defaultCalendarLabel = "Default";
        String dateLabel = "";
        try {
            ResourceBundle rs = ResourceBundle.getBundle("locale/portlet/calendar/calendar", locale);
            defaultCalendarLabel = EntityEncoder.FULL.encode(rs.getString("UICalendars.label.defaultCalendarId"));
            if (clickNumber == 0) dateLabel = rs.getString("today.label") + ": ";
            else if (clickNumber == -1) dateLabel = rs.getString("yesterday.label") + ": ";
            else if (clickNumber == 1) dateLabel = rs.getString("tomorrow.label") + ": ";
            else dateLabel = "";
        } catch (MissingResourceException ex) {
            if (clickNumber == 0) dateLabel = "today.label" + ": ";
            else if (clickNumber == -1) dateLabel = "yesterday.label" + ": ";
            else if (clickNumber == 1) dateLabel = "tomorrow.label" + ": ";
            else dateLabel = "";
        }

        EntityEncoder.FULL.encode(dateLabel);
        dateLabel = new StringBuffer(dateLabel).append(date_act).toString();
        if (nonDisplayedCalendarList == null) {
            SettingValue settingNode = settingService_.get(Context.USER, Scope.APPLICATION, CalendarPortletUtils.HOME_PAGE_CALENDAR_SETTINGS);
            if ((settingNode != null) && (settingNode.getValue().toString().split(":").length == 2)) {
                nonDisplayedCalendarList = settingNode.getValue().toString().split(":")[1].split(",");
            }
        }
        List<CalendarEvent> userEvents = getEvents(username,cal);
        if ((userEvents != null) && (!userEvents.isEmpty())) {
            Iterator itr = userEvents.iterator();
            while (itr.hasNext()) {
                CalendarEvent event = (CalendarEvent) itr.next();
                Date from = d.parse(dTimezone.format(event.getFromDateTime()));
                Date to = d.parse(dTimezone.format(event.getToDateTime()));
                if ((event.getEventType().equals(CalendarEvent.TYPE_EVENT)) && (from.compareTo(d.parse(dTimezone.format(comp))) <= 0) && (to.compareTo(d.parse(dTimezone.format(comp))) >= 0)) {
                    if (!CalendarPortletUtils.contains(nonDisplayedCalendarList, event.getCalendarId())) {
                        org.exoplatform.calendar.service.Calendar calendar = calendarService_.getUserCalendar(username, event.getCalendarId());
                        if (calendar == null) {
                            calendar = calendarService_.getGroupCalendar(event.getCalendarId());
                        }
                        if(calendar.getGroups()==null) {
                            if (calendar.getId().equals(Utils.getDefaultCalendarId(username)) && calendar.getName().equals(NewUserListener.defaultCalendarName)) {
                                calendar.setName(defaultCalendarLabel);
                            }
                        }
                        eventsDisplayedList.add(event);
                        if (!displayedCalendarMap.containsKey(calendar.getId())) {
                            displayedCalendarMap.put(calendar.getId(), calendar);
                            displayedCalendar.add(calendar);
                        }
                    }
                } else if ((event.getEventType().equals(CalendarEvent.TYPE_TASK)) &&
                        (((from.compareTo(comp) <= 0) && (to.compareTo(comp) >= 0)) ||
                                ((event.getEventState().equals(CalendarEvent.NEEDS_ACTION)) && (to.compareTo(comp) < 0)))) {
                    tasksDisplayedList.add(event);
                }
            }
            Collections.sort(eventsDisplayedList, eventsComparator);
            Collections.sort(tasksDisplayedList, tasksComparator);
        }
        return calendar.with().
                set("displayedCalendar", displayedCalendar).
                set("calendarDisplayedMap", displayedCalendarMap).
                set("eventsDisplayedList", eventsDisplayedList).
                set("tasksDisplayedList", tasksDisplayedList).
                set("date_act", dateLabel).ok().withCharset(Tools.UTF_8);
    }

    @Ajax
    @Resource
    public Response.Content setting() throws Exception {
        calendarDisplayedList.clear();
        calendarNonDisplayedList.clear();
        String username = RequestContext.getCurrentInstance().getRemoteUser();
        String defaultCalendarLabel = "Default";
        Iterator itr1 = getAllCal(username).iterator();
        while (itr1.hasNext()) {
            org.exoplatform.calendar.service.Calendar c = (org.exoplatform.calendar.service.Calendar) itr1.next();
            if(c.getGroups()==null) {
                if (c.getId().equals(Utils.getDefaultCalendarId(username)) && c.getName().equals(NewUserListener.defaultCalendarName)) {
                    c.setName(defaultCalendarLabel);
                }
            }
            if (CalendarPortletUtils.contains(nonDisplayedCalendarList, c.getId())) {
                calendarNonDisplayedList.add(c);
            } else {
                calendarDisplayedList.add(c);
            }
        }
        return setting.with().set("displayedCalendar", calendarDisplayedList).
                set("nonDisplayedCalendar", calendarNonDisplayedList).ok().withCharset(Tools.UTF_8);
    }

    @Ajax
    @Resource
    public Response.Content addCalendar(String calendarId) throws Exception {

            StringBuilder cals = new StringBuilder();
            int i = 0;
            nonDisplayedCalendarList = (String[]) ArrayUtils.removeElement(nonDisplayedCalendarList, calendarId);
            while (i < nonDisplayedCalendarList.length) {
                if (!nonDisplayedCalendarList[i].equals(calendarId))
                    cals.append(nonDisplayedCalendarList[i]).append(",");
                i++;
            }
            settingService_.remove(Context.USER, Scope.APPLICATION, CalendarPortletUtils.HOME_PAGE_CALENDAR_SETTINGS);
            settingService_.set(Context.USER, Scope.APPLICATION, CalendarPortletUtils.HOME_PAGE_CALENDAR_SETTINGS, SettingValue.create("NonDisplayedCalendar:" + cals.toString()));
        return setting();
    }

    @Ajax
    @Resource
    public Response.Content deleteCalendar(String calendarId) throws Exception {

        nonDisplayedCalendarList = (String[]) ArrayUtils.add(nonDisplayedCalendarList, calendarId);
        StringBuffer cal = new StringBuffer();
        int i = 0;
        while (i < nonDisplayedCalendarList.length) {
                cal.append(nonDisplayedCalendarList[i]).append(",");
            i++;
        }
        settingService_.remove(Context.USER, Scope.APPLICATION, CalendarPortletUtils.HOME_PAGE_CALENDAR_SETTINGS);
        settingService_.set(Context.USER, Scope.APPLICATION, CalendarPortletUtils.HOME_PAGE_CALENDAR_SETTINGS, SettingValue.create("NonDisplayedCalendar:" + cal.toString()));
        return setting();
    }


    @Ajax
    @Resource
    public Response.Content incDate(String nbClick) throws Exception {
        int clickNumber = Integer.parseInt(nbclick);
        clickNumber++;
        nbclick = new Integer(clickNumber).toString();
        return calendarHome();
    }

    @Ajax
    @Resource
    public Response.Content decDate(String nbClick) throws Exception {
        int clickNumber = Integer.parseInt(nbclick);
        clickNumber--;
        nbclick = new Integer(clickNumber).toString();
        return calendarHome();
    }

    @Ajax
    @Resource
    public Response.Content getSearchResult(String key) {

        Iterator itr = null;
        if (calendarNonDisplayedList != null) itr = calendarNonDisplayedList.iterator();
        searchResult.clear();
        while (itr.hasNext()) {
            org.exoplatform.calendar.service.Calendar c = (org.exoplatform.calendar.service.Calendar) itr.next();
            if (c.getName().toLowerCase().contains(key.toLowerCase())) searchResult.add(c);
        }
       // String label = "Default Personal Calendar";

        return search.with().set("searchResultList", searchResult).ok().withCharset(Tools.UTF_8);
    }


    public static Long incDecJour(Long date, int nbJour) {
        Calendar cal;
        cal = Calendar.getInstance();
        cal.setTimeInMillis(date);
        cal.add(Calendar.DAY_OF_MONTH, nbJour);
        return cal.getTime().getTime();
    }


    public String[] getUserGroups(String username) throws Exception {
        Object[] objs = organization_.getGroupHandler().findGroupsOfUser(username).toArray();
        String[] groups = new String[objs.length];
        for (int i = 0; i < objs.length; i++) {
            groups[i] = ((Group) objs[i]).getId();
        }
        return groups;

    }

    public List getAllCal(String username) throws Exception {
        List<org.exoplatform.calendar.service.Calendar> calList = calendarService_.getUserCalendars(username, true);
        List<GroupCalendarData> lgcd = calendarService_.getGroupCalendars(getUserGroups(username), true, username);
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


    String[] getCalendarsIdList(String username) {


        StringBuilder sb = new StringBuilder();
        List<GroupCalendarData> listgroupCalendar = null;
        List<org.exoplatform.calendar.service.Calendar> listUserCalendar = null;
        try {
            listgroupCalendar = calendarService_.getGroupCalendars(getUserGroups(username), true, username);
            listUserCalendar = calendarService_.getUserCalendars(username, true);
        } catch (Exception e) {
            LOG.error("Error while checking User Calendar :" + e.getMessage(), e);
        }
        for (GroupCalendarData g : listgroupCalendar) {
            for (org.exoplatform.calendar.service.Calendar c : g.getCalendars()) {
                sb.append(c.getId()).append(",");
            }
        }
        for (org.exoplatform.calendar.service.Calendar c : listUserCalendar) {
            sb.append(c.getId()).append(",");
        }
        String[] list = sb.toString().split(",");
        return list;
    }

    List<CalendarEvent> getEvents(String username , Calendar cal) {

        Map<String, Map<String, CalendarEvent>> recurrenceEventsMap   = new LinkedHashMap<String, Map<String, CalendarEvent>>();
        String[] calList = getCalendarsIdList(username);
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
            userEvents = calendarService_.getEvents(username, eventQuery, calList);
            String timezone = CalendarPortletUtils.getCurrentUserCalendarSetting().getTimeZone();
            List<CalendarEvent> originalRecurEvents = calendarService_.getOriginalRecurrenceEvents(username, eventQuery.getFromDate(), eventQuery.getToDate(),calList);
                if (originalRecurEvents != null && originalRecurEvents.size() > 0) {
                      Iterator<CalendarEvent> recurEventsIter = originalRecurEvents.iterator();
                          while (recurEventsIter.hasNext()) {
                             CalendarEvent recurEvent = recurEventsIter.next();
                             Map<String,CalendarEvent> tempMap = calendarService_.getOccurrenceEvents(recurEvent, eventQuery.getFromDate(), eventQuery.getToDate(), timezone);
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
    private String getDateDelimiter(String date) {
       String[] availableDelimiter = {"/","-","."};
       for (String delim : availableDelimiter) {
            if (date.indexOf(delim) > 0) {
                        return delim;
                       }
               }
          return null;
        }
}


