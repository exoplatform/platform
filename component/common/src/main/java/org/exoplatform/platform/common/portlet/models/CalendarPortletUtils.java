package org.exoplatform.platform.common.portlet.models;

import org.exoplatform.calendar.service.CalendarService;
import org.exoplatform.calendar.service.CalendarSetting;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="fbradai@exoplatform.com">Fbradai</a>
 */
public class CalendarPortletUtils {

    private static final Log LOG = ExoLogger.getLogger(CalendarPortletUtils.class);
    public final static String HOME_PAGE_CALENDAR_SETTINGS = "IntranetHomePageCalendarSettings";
    public final static int JOUR_MS = 86399999;
    private static ConcurrentHashMap<String, CalendarSetting> calendarSettingsByUserName = new ConcurrentHashMap<String, CalendarSetting>();

    public static boolean contains(String[] s, String str) {
        int i = 0;
        if (s != null) {
            while (i < s.length) {
                if ((s[i] != null) && (s[i].equals(str))) {
                    return true;
                }
                i++;
            }
        }
        return false;
    }

    public static Calendar getInstanceOfCurrentCalendar() {
        try {
            String user = getCurrentUser();
            CalendarSetting setting =getCalendarService().getCalendarSetting(user);
            return getCalendarInstanceBySetting(setting);
        } catch (Exception e) {
            if (LOG.isWarnEnabled()) LOG.warn("Could not get calendar setting!", e);
            Calendar calendar = Calendar.getInstance();
            calendar.setLenient(false);
            return calendar;
        }
    }

    public static CalendarSetting getCurrentUserCalendarSetting() {

        try {
            String user = getCurrentUser();
            CalendarSetting setting = calendarSettingsByUserName.get(user);
            if (setting == null) {
                setting = getCalendarService().getCalendarSetting(user);
                calendarSettingsByUserName.put(user, setting);
            }
            return setting;
        } catch (Exception e) {
            LOG.warn("could not get calendar setting of user", e);
            return null;
        }

    }

    static public CalendarService getCalendarService() {
        return (CalendarService) PortalContainer.getInstance().getComponentInstance(CalendarService.class);
    }

    static public String getCurrentUser() {
        return ConversationState.getCurrent().getIdentity().getUserId();
    }

    public static Calendar getCalendarInstanceBySetting(final CalendarSetting calendarSetting) {
        Calendar calendar = Calendar.getInstance();
        calendar.setLenient(false);
        calendar.setTimeZone(TimeZone.getTimeZone(calendarSetting.getTimeZone()));
        calendar.setFirstDayOfWeek(Integer.parseInt(calendarSetting.getWeekStartOn()));
        calendar.setMinimalDaysInFirstWeek(4);
        return calendar;
    }
    public static Calendar getBeginDay(Calendar cal) {
       Calendar newCal = (Calendar) cal.clone();
       newCal.set(Calendar.HOUR_OF_DAY, 0) ;
       newCal.set(Calendar.MINUTE, 0) ;
       newCal.set(Calendar.SECOND, 0) ;
       newCal.set(Calendar.MILLISECOND, 0) ;
       return newCal ;

    }
    public static Calendar getEndDay(Calendar cal)  {
       Calendar newCal = (Calendar) cal.clone();
       newCal.set(Calendar.HOUR_OF_DAY, 0) ;
       newCal.set(Calendar.MINUTE, 0) ;
       newCal.set(Calendar.SECOND, 0) ;
       newCal.set(Calendar.MILLISECOND, 0) ;
       newCal.add(Calendar.HOUR_OF_DAY, 24) ;
       return newCal ;
    }

    public static Calendar getCurrentCalendar() {
      try {
           CalendarSetting setting = getCurrentUserCalendarSetting();
           return getCalendarInstanceBySetting(setting);
            } catch (Exception e) {
             if (LOG.isWarnEnabled()) LOG.warn("Could not get calendar setting!", e);
             Calendar calendar = Calendar.getInstance() ;
             calendar.setLenient(false);
             return calendar;
            }
        }
}
