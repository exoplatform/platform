package org.exoplatform.platform.common.rest.services.CalendarPortlet;

import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.api.settings.SettingValue;
import org.exoplatform.commons.api.settings.data.Context;
import org.exoplatform.commons.api.settings.data.Scope;
import org.exoplatform.platform.common.portlet.models.CalendarPortletUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class MockSettingService implements SettingService {
    @Override
    public void set(Context context, Scope scope, String s, SettingValue<?> settingValue) {

    }

    @Override
    public void remove(Context context, Scope scope, String s) {

    }

    @Override
    public void remove(Context context, Scope scope) {

    }

    @Override
    public void remove(Context context) {

    }

    @Override
    public SettingValue<?> get(Context context, Scope scope, String s) {
        if (Context.USER.getName().equals(context.getName())
            && Scope.APPLICATION.getName().equals(scope.getName())
            && CalendarPortletUtils.HOME_PAGE_CALENDAR_SETTINGS.equals(s)) {
            return SettingValue.create("NonDisplayedCalendar:idUser");
        }
        return null;
    }

    @Override
    public Map<Scope, Map<String, SettingValue<String>>> getSettingsByContext(Context context) {
        return null;
    }

    @Override
    public List<Context> getContextsByTypeAndScopeAndSettingName(String s, String s1, String s2, String s3, int i, int i1) {
        return null;
    }

    @Override
    public Set<String> getEmptyContextsByTypeAndScopeAndSettingName(String s, String s1, String s2, String s3, int i, int i1) {
        return null;
    }

    @Override
    public void save(Context context) {

    }

    @Override
    public Map<String, SettingValue> getSettingsByContextAndScope(String s, String s1, String s2, String s3) {
        return null;
    }
}
