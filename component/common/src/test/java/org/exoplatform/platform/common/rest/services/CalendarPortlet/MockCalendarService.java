package org.exoplatform.platform.common.rest.services.CalendarPortlet;

import org.exoplatform.calendar.service.*;
import org.exoplatform.calendar.service.Calendar;
import org.exoplatform.calendar.service.impl.CalendarEventListener;
import org.exoplatform.commons.utils.ListAccess;
import org.exoplatform.services.scheduler.JobSchedulerService;
import org.exoplatform.services.scheduler.impl.JobSchedulerServiceImpl;
import org.quartz.JobDetail;

import javax.jcr.Node;
import java.util.*;

public class MockCalendarService implements CalendarService {

    @Override
    public List<EventCategory> getEventCategories(String s) throws Exception {
        return null;
    }

    @Override
    public CalendarCollection<EventCategory> getEventCategories(String s, int i, int i1) throws Exception {
        return null;
    }

    @Override
    public void saveEventCategory(String s, EventCategory eventCategory, boolean b) throws Exception {

    }

    @Override
    public void removeEventCategory(String s, String s1) throws Exception {

    }

    @Override
    public EventCategory getEventCategory(String s, String s1) throws Exception {
        return null;
    }

    @Override
    public EventCategory getEventCategoryByName(String s, String s1) throws Exception {
        return null;
    }

    @Override
    public void saveCalendarSetting(String s, CalendarSetting calendarSetting) throws Exception {

    }

    @Override
    public CalendarSetting getCalendarSetting(String s) throws Exception {
        if ("root".equals(s)) {
            CalendarSetting calendarSetting = new CalendarSetting();
            calendarSetting.setTimeZone("Europe/Brussels");
            return calendarSetting;
        }
        return null;
    }

    @Override
    public CalendarImportExport getCalendarImportExports(String s) {
        return null;
    }

    @Override
    public String[] getExportImportType() throws Exception {
        return new String[0];
    }

    @Override
    public int generateRss(String s, LinkedHashMap<String, Calendar> linkedHashMap, RssData rssData) throws Exception {
        return 0;
    }

    @Override
    public int generateRss(String s, List<String> list, RssData rssData) throws Exception {
        return 0;
    }

    @Override
    public List<FeedData> getFeeds(String s) throws Exception {
        return null;
    }

    @Override
    public Node getRssHome(String s) throws Exception {
        return null;
    }

    @Override
    public void confirmInvitation(String s, String s1, int i, String s2, String s3, int i1) {

    }

    @Override
    public void removeFeedData(String s, String s1) {

    }

    @Override
    public ResourceBundle getResourceBundle() throws Exception {
        return null;
    }

    @Override
    public Calendar importRemoteCalendar(RemoteCalendar remoteCalendar) throws Exception {
        return null;
    }

    @Override
    public Calendar refreshRemoteCalendar(String s, String s1) throws Exception {
        return null;
    }

    @Override
    public Calendar updateRemoteCalendarInfo(RemoteCalendar remoteCalendar) throws Exception {
        return null;
    }

    @Override
    public RemoteCalendar getRemoteCalendar(String s, String s1) throws Exception {
        return null;
    }

    @Override
    public RemoteCalendarService getRemoteCalendarService() throws Exception {
        return null;
    }

    @Override
    public Calendar getRemoteCalendar(String s, String s1, String s2) throws Exception {
        return null;
    }

    @Override
    public int getRemoteCalendarCount(String s) throws Exception {
        return 0;
    }

    @Override
    public String getCalDavResourceHref(String s, String s1, String s2) throws Exception {
        return null;
    }

    @Override
    public String getCalDavResourceEtag(String s, String s1, String s2) throws Exception {
        return null;
    }

    @Override
    public void loadSynchronizeRemoteCalendarJob(String s) throws Exception {

    }

    @Override
    public JobDetail findSynchronizeRemoteCalendarJob(JobSchedulerService jobSchedulerService, String s) throws Exception {
        return null;
    }

    @Override
    public void stopSynchronizeRemoteCalendarJob(String s) throws Exception {

    }

    @Override
    public void importRemoteCalendarByJob(RemoteCalendar remoteCalendar) throws Exception {

    }

    @Override
    public Attachment getAttachmentById(String s) {
        return null;
    }

    @Override
    public void removeAttachmentById(String s) {

    }

    @Override
    public void addListenerPlugin(CalendarUpdateEventListener calendarUpdateEventListener) throws Exception {

    }

    @Override
    public void addEventListenerPlugin(CalendarEventListener calendarEventListener) throws Exception {

    }

    @Override
    public void initNewUser(String s, CalendarSetting calendarSetting) throws Exception {

    }

    @Override
    public boolean isValidRemoteUrl(String s, String s1, String s2, String s3) throws Exception {
        return false;
    }

    @Override
    public Calendar getCalendarById(String s) throws Exception {
        return null;
    }

    @Override
    public CalendarCollection<Calendar> getAllCalendars(String s, int i, int i1, int i2) {
        return null;
    }

    @Override
    public Calendar getUserCalendar(String s, String s1) throws Exception {
        return null;
    }

    @Override
    public List<Calendar> getUserCalendars(String s, boolean b) throws Exception {
        Calendar calendar = new Calendar("idUser");
        calendar.setCalType(0);
        calendar.setName("Root Root");
        calendar.setCalendarColor("asparagus");
        calendar.setTimeZone("Europe/Brussels");
        return new ArrayList<Calendar>(){{add(calendar);}};
    }

    @Override
    public ListAccess<Calendar> getPublicCalendars() throws Exception {
        return null;
    }

    @Override
    public void saveUserCalendar(String s, Calendar calendar, boolean b) {

    }

    @Override
    public Calendar saveCalendar(String s, Calendar calendar, int i, boolean b) {
        return null;
    }

    @Override
    public void saveSharedCalendar(String s, Calendar calendar) throws Exception {

    }

    @Override
    public void shareCalendar(String s, String s1, List<String> list) throws Exception {

    }

    @Override
    public GroupCalendarData getSharedCalendars(String s, boolean b) throws Exception {
        return null;
    }

    @Override
    public Calendar removeUserCalendar(String s, String s1) throws Exception {
        return null;
    }

    @Override
    public Calendar getGroupCalendar(String s) throws Exception {
        return null;
    }

    @Override
    public List<GroupCalendarData> getGroupCalendars(String[] strings, boolean b, String s) throws Exception {
        Calendar calendar = new Calendar("idGroups");
        calendar.setCalType(2);
        calendar.setName("Users");
        calendar.setCalendarColor("asparagus");
        calendar.setTimeZone("Europe/Brussels");
        return new ArrayList<GroupCalendarData>(){{
            add(new GroupCalendarData("/platform/users", "/platform/users", new ArrayList<Calendar>(){{add(calendar);}}));
        }};
    }

    @Override
    public void savePublicCalendar(Calendar calendar, boolean b) {

    }

    @Override
    public Calendar removePublicCalendar(String s) throws Exception {
        return null;
    }

    @Override
    public boolean isRemoteCalendar(String s, String s1) throws Exception {
        return false;
    }

    @Override
    public void autoShareCalendar(List<String> list, String s) throws Exception {

    }

    @Override
    public void autoRemoveShareCalendar(String s, String s1) throws Exception {

    }

    @Override
    public void shareCalendarByRunJob(String s, String s1, List<String> list) throws Exception {

    }

    @Override
    public void removeSharedCalendarByJob(String s, List<String> list, String s1) throws Exception {

    }

    @Override
    public void removeSharedCalendar(String s, String s1) throws Exception {

    }

    @Override
    public void removeSharedCalendarFolder(String s) throws Exception {

    }

    @Override
    public boolean isGroupBeingShared(String s, JobSchedulerServiceImpl jobSchedulerService) throws Exception {
        return false;
    }

    @Override
    public int getTypeOfCalendar(String s, String s1) throws Exception {
        return 0;
    }

    @Override
    public CalendarEvent getEventById(String s) throws Exception {
        return null;
    }

    @Override
    public CalendarEvent getEvent(String s, String s1) throws Exception {
        return null;
    }

    @Override
    public List<CalendarEvent> getEvents(String s, EventQuery eventQuery, String[] strings) throws Exception {
        return null;
    }

    @Override
    public List<CalendarEvent> getUserEventByCalendar(String s, List<String> list) throws Exception {
        return null;
    }

    @Override
    public List<CalendarEvent> getUserEvents(String s, EventQuery eventQuery) throws Exception {
        return null;
    }

    @Override
    public void saveUserEvent(String s, String s1, CalendarEvent calendarEvent, boolean b) throws Exception {

    }

    @Override
    public CalendarEvent removeUserEvent(String s, String s1, String s2) throws Exception {
        return null;
    }

    @Override
    public CalendarEvent getGroupEvent(String s) throws Exception {
        return null;
    }

    @Override
    public CalendarEvent getGroupEvent(String s, String s1) throws Exception {
        return null;
    }

    @Override
    public List<CalendarEvent> getGroupEventByCalendar(List<String> list) throws Exception {
        return null;
    }

    @Override
    public List<CalendarEvent> getPublicEvents(EventQuery eventQuery) throws Exception {
        return null;
    }

    @Override
    public void savePublicEvent(String s, CalendarEvent calendarEvent, boolean b) throws Exception {

    }

    @Override
    public CalendarEvent removePublicEvent(String s, String s1) throws Exception {
        return null;
    }

    @Override
    public CalendarEvent getRepetitiveEvent(CalendarEvent calendarEvent) throws Exception {
        return null;
    }

    @Override
    public List<CalendarEvent> getSharedEventByCalendars(String s, List<String> list) throws Exception {
        return null;
    }

    @Override
    public CalendarEvent getSharedEvent(String s, String s1, String s2) throws Exception {
        return null;
    }

    @Override
    public void saveEventToSharedCalendar(String s, String s1, CalendarEvent calendarEvent, boolean b) throws Exception {

    }

    @Override
    public void removeSharedEvent(String s, String s1, String s2) throws Exception {

    }

    @Override
    public void assignGroupTask(String s, String s1, String s2) throws Exception {

    }

    @Override
    public void setGroupTaskStatus(String s, String s1, String s2) throws Exception {

    }

    @Override
    public EventPageList searchEvent(String s, EventQuery eventQuery, String[] strings) throws Exception {
        return null;
    }

    @Override
    public List<Map<Integer, String>> searchHightLightEventSQL(String s, EventQuery eventQuery, String[] strings, String[] strings1) throws Exception {
        return null;
    }

    @Override
    public List<CalendarEvent> getAllNoRepeatEventsSQL(String s, EventQuery eventQuery, String[] strings, String[] strings1, List<String> list) throws Exception {
        return null;
    }

    @Override
    public EventDAO getEventDAO() {
        return null;
    }

    @Override
    public Collection<CalendarEvent> getAllExcludedEvent(CalendarEvent calendarEvent, Date date, Date date1, String s) {
        return null;
    }

    @Override
    public Collection<CalendarEvent> buildSeries(CalendarEvent calendarEvent, Date date, Date date1, String s) {
        return null;
    }

    @Override
    public String buildRecurrenceId(Date date, String s) {
        return null;
    }

    @Override
    public Map<String, String> checkFreeBusy(EventQuery eventQuery) throws Exception {
        return null;
    }

    @Override
    public void moveEvent(String s, String s1, String s2, String s3, List<CalendarEvent> list, String s4) throws Exception {

    }

    @Override
    public void confirmInvitation(String s, String s1, String s2, int i, String s3, String s4, int i1) throws Exception {

    }

    @Override
    public Map<String, CalendarEvent> getOccurrenceEvents(CalendarEvent calendarEvent, java.util.Calendar calendar, java.util.Calendar calendar1, String s) throws Exception {
        return null;
    }

    @Override
    public List<CalendarEvent> getOriginalRecurrenceEvents(String s, java.util.Calendar calendar, java.util.Calendar calendar1, String[] strings) throws Exception {
        return null;
    }

    @Override
    public void updateOccurrenceEvent(String s, String s1, String s2, String s3, List<CalendarEvent> list, String s4) throws Exception {

    }

    @Override
    public void saveOneOccurrenceEvent(CalendarEvent calendarEvent, CalendarEvent calendarEvent1, String s) {

    }

    @Override
    public void saveAllSeriesEvents(CalendarEvent calendarEvent, String s) {

    }

    @Override
    public void saveFollowingSeriesEvents(CalendarEvent calendarEvent, CalendarEvent calendarEvent1, String s) {

    }

    @Override
    public void removeOneOccurrenceEvent(CalendarEvent calendarEvent, CalendarEvent calendarEvent1, String s) {

    }

    @Override
    public void removeAllSeriesEvents(CalendarEvent calendarEvent, String s) {

    }

    @Override
    public void removeFollowingSeriesEvents(CalendarEvent calendarEvent, CalendarEvent calendarEvent1, String s) {

    }

    @Override
    public List<CalendarEvent> getExceptionEventsFromDate(String s, CalendarEvent calendarEvent, Date date) throws Exception {
        return null;
    }

    @Override
    public List<CalendarEvent> getExceptionEvents(String s, CalendarEvent calendarEvent) throws Exception {
        return null;
    }

    @Override
    public Map<Integer, String> searchHightLightEvent(String s, EventQuery eventQuery, String[] strings) throws Exception {
        return null;
    }

    @Override
    public List<CalendarEvent> getAllNoRepeatEvents(String s, EventQuery eventQuery, String[] strings) throws Exception {
        return null;
    }

    @Override
    public void removeOccurrenceInstance(String s, CalendarEvent calendarEvent) throws Exception {

    }

    @Override
    public void removeRecurrenceSeries(String s, CalendarEvent calendarEvent) throws Exception {

    }

    @Override
    public void updateRecurrenceSeries(String s, String s1, String s2, String s3, CalendarEvent calendarEvent, String s4) throws Exception {

    }

    @Override
    public Map<Integer, String> searchHighlightRecurrenceEvent(String s, EventQuery eventQuery, String[] strings, String s1) throws Exception {
        return null;
    }

    @Override
    public List<Map<Integer, String>> searchHighlightRecurrenceEventSQL(String s, EventQuery eventQuery, String s1, String[] strings, String[] strings1) throws Exception {
        return null;
    }

    @Override
    public List<CalendarEvent> getHighLightOriginalRecurrenceEvents(String s, java.util.Calendar calendar, java.util.Calendar calendar1, String[] strings) throws Exception {
        return null;
    }

    @Override
    public List<CalendarEvent> getHighLightOriginalRecurrenceEventsSQL(String s, java.util.Calendar calendar, java.util.Calendar calendar1, EventQuery eventQuery, String[] strings, String[] strings1, List<String> list) throws Exception {
        return null;
    }
}