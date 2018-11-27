package org.exoplatform.platform.gadget.services.LoginHistory.storage;

import org.exoplatform.platform.gadget.services.LoginHistory.LastLoginBean;
import org.exoplatform.platform.gadget.services.LoginHistory.LoginCounterBean;
import org.exoplatform.platform.gadget.services.LoginHistory.LoginHistoryBean;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class JPALoginHistoryStorageImpl implements LoginHistoryStorage {
    @Override
    public long getLastLogin(String userId) throws Exception {
        return 0;
    }

    @Override
    public List<LastLoginBean> getLastLogins(int numLogins, String userIdFilter) throws Exception {
        return null;
    }

    @Override
    public void addLoginHistoryEntry(String userId, long loginTime) throws Exception {

    }

    @Override
    public List<LoginHistoryBean> getLoginHistory(String userId, long fromTime, long toTime) throws Exception {
        return null;
    }

    @Override
    public Set<String> getLastUsersLogin(long fromTime) throws Exception {
        return null;
    }

    @Override
    public boolean isActiveUser(String userId, int days) {
        return false;
    }

    @Override
    public Map<String, Integer> getActiveUsers(long fromTime) {
        return null;
    }

    @Override
    public List<LoginCounterBean> getLoginCountPerDaysInWeek(String userId, long week) throws Exception {
        return null;
    }

    @Override
    public List<LoginCounterBean> getLoginCountPerWeeksInMonths(String userId, long fromMonth, int numOfMonths) throws Exception {
        return null;
    }

    @Override
    public List<LoginCounterBean> getLoginCountPerMonthsInYear(String userId, long year) throws Exception {
        return null;
    }

    @Override
    public long getBeforeLastLogin(String userId) throws Exception {
        return 0;
    }
}
