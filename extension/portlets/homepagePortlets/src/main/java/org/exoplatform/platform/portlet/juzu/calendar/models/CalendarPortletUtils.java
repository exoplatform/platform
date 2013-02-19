package org.exoplatform.platform.portlet.juzu.calendar.models;

/**
 * @author <a href="fbradai@exoplatform.com">Fbradai</a>
 * @date 1/10/13
 */
public class CalendarPortletUtils {
    public final static String HOME_PAGE_CALENDAR_SETTINGS = "IntranetHomePageCalendarSettings";
    public final static int JOUR_MS = 86399999;

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
}
