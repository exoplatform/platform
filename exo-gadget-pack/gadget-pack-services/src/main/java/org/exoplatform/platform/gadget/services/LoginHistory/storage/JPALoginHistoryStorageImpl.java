package org.exoplatform.platform.gadget.services.LoginHistory.storage;

import org.exoplatform.commons.api.persistence.ExoTransactional;
import org.exoplatform.platform.gadget.services.LoginHistory.LastLoginBean;
import org.exoplatform.platform.gadget.services.LoginHistory.LoginCounterBean;
import org.exoplatform.platform.gadget.services.LoginHistory.LoginHistoryBean;
import org.exoplatform.platform.gadget.services.LoginHistory.jpa.dao.LoginHistoryDAO;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class JPALoginHistoryStorageImpl implements LoginHistoryStorage {
    private LoginHistoryDAO loginHistoryDAO;

    public JPALoginHistoryStorageImpl(LoginHistoryDAO loginHistoryDAO) {
        this.loginHistoryDAO = loginHistoryDAO;
    }

    @Override
    public long getLastLogin(String userId) throws Exception {
        return loginHistoryDAO.getLastLogin(userId);
    }

    @Override
    public List<LastLoginBean> getLastLogins(int numLogins, String userIdFilter) throws Exception {
        return null;
    }

    @Override
    @ExoTransactional
    public void addLoginHistoryEntry(String userId, long loginTime) throws Exception {
        loginHistoryDAO.addLoginHistoryEntry(userId,loginTime);
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
        return loginHistoryDAO.getBeforeLastLogin(userId);
    }
}
