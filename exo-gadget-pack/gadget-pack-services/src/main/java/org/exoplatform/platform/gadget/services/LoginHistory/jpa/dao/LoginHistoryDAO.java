package org.exoplatform.platform.gadget.services.LoginHistory.jpa.dao;

import org.exoplatform.commons.api.persistence.ExoTransactional;
import org.exoplatform.commons.persistence.impl.GenericDAOJPAImpl;
import org.exoplatform.platform.gadget.services.LoginHistory.LoginCounterBean;
import org.exoplatform.platform.gadget.services.LoginHistory.jpa.entity.LoginHistoryEntity;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.util.*;

public class LoginHistoryDAO extends GenericDAOJPAImpl {
    private static final Log LOG = ExoLogger.getLogger(LoginHistoryDAO.class);
    private static long DAY_IN_MILLISEC = 86400000;

    private static long nextMonday(long date) {
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(date);

        int weekday = now.get(Calendar.DAY_OF_WEEK);
        // calculate how much to add
        // the 2 is the difference between Saturday and Monday
        int days = weekday == Calendar.SUNDAY ? 1 : Calendar.SATURDAY - weekday + 2;
        now.add(Calendar.DAY_OF_YEAR, days);
        return now.getTimeInMillis();
    }

    public List<LoginCounterBean> getLoginCountPerDaysInRange(String userId, long fromDate, long toDate) {
        /*Timestamp from = new Timestamp(fromDate);
        Timestamp to = new Timestamp(toDate);
        int today = from.getDate();
        today ++;
        ZonedDateTime time = ZonedDateTime.now();*/
        Calendar from = Calendar.getInstance(); from.set(Calendar.DAY_OF_YEAR, from.get(Calendar.DAY_OF_YEAR)+1);
        Calendar to = Calendar.getInstance();
        from.setTimeInMillis(fromDate);
        to.setTimeInMillis(toDate);
        from.set(Calendar.MILLISECOND,0);
        from.set(Calendar.SECOND,0);
        from.set(Calendar.MINUTE,0);
        from.set(Calendar.HOUR,0);

        to.set(Calendar.MILLISECOND,999);
        to.set(Calendar.SECOND,59);
        to.set(Calendar.MINUTE,59);
        to.set(Calendar.HOUR,23);

        long firstDay = from.getTimeInMillis();
        Long nextDay = 0L;
        Long lastDay = to.getTimeInMillis();

        List<LoginCounterBean> counterBeanList = new ArrayList<LoginCounterBean>();
        LoginCounterBean loginCountPerDay = new LoginCounterBean();

        while (firstDay <= lastDay) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(firstDay);
            cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR) +1);
            nextDay = cal.getTimeInMillis();

            Calendar cal2 = Calendar.getInstance();
            cal2.setTimeInMillis(firstDay);
            cal2.set(Calendar.MILLISECOND,999);
            cal2.set(Calendar.SECOND,59);
            cal2.set(Calendar.MINUTE,59);
            cal2.set(Calendar.HOUR,23);
            Long endOfDay = cal2.getTimeInMillis();

            Long count = (Long) getEntityManager().createNamedQuery("loginHistory.getLoginCountPerDay")
                                                                                    .setParameter("userId",userId)
                                                                                    .setParameter("from",firstDay)
                                                                                    .setParameter("to",endOfDay).getSingleResult();

            loginCountPerDay.setLoginCount(count);
            loginCountPerDay.setLoginDate(firstDay);
            counterBeanList.add(loginCountPerDay);

            firstDay = nextDay;
        }
        return counterBeanList;
    }

    private int getLoginCount(String userId, long fromDate, long toDate) throws Exception {
        List<LoginHistoryEntity> list = getLoginHistory(userId,fromDate,toDate);
        int sum = 0;
        Iterator<LoginHistoryEntity> iterator = list.iterator();
        while (iterator.hasNext()) {
            sum+=iterator.next().getLoginDate().getTime();
        }
        return sum;
    }

    public Long getLastLogin(String userID) throws Exception {
        try {
            long ID = (long) getEntityManager().createNamedQuery("loginHistory.getUserLastLoginID").setParameter("id",userID).getSingleResult();
            LoginHistoryEntity loginHistoryEntity = (LoginHistoryEntity) find(ID);
            return loginHistoryEntity.getLoginDate().getTime();
        } catch (Exception e) {
            LOG.debug("Error while getting last login of user '" + userID + "': " + e.getMessage(), e);
            throw e;
        }
    }

    public List<LoginHistoryEntity> getLastLogins(int numLogins, String userId) throws Exception {
        try {
            Calendar calendar = Calendar.getInstance();
            Date now = calendar.getTime();
            now.setHours(0);
            now.setMinutes(0);
            now.setSeconds(0);
            Long today = now.getTime();
            calendar.setTimeInMillis(today);
            return getEntityManager().createNamedQuery("loginHistory.getLastLogins")
                    .setParameter("userId",userId)
                    .setParameter("today",calendar.getTimeInMillis())
                    .setParameter("limit",numLogins).getResultList();
        } catch (Exception e) {
            LOG.debug("Error while getting last logins of user '" + userId + "': " + e.getMessage(), e);
            throw e;
        }
    }

    public long getBeforeLastLogin(String userId) throws Exception {
        Long lastLoginID = (Long) getEntityManager().createNamedQuery("loginHistory.getLastUserLoginID").setParameter("userId",userId).getSingleResult();
        Long beforeLastLoginID = (Long) getEntityManager().createNamedQuery("loginHistory.getBeforeLastLoginID").setParameter("userId",userId).setParameter("id",lastLoginID).getSingleResult();
        LoginHistoryEntity loginHistoryEntity = (LoginHistoryEntity) find(beforeLastLoginID);
        return loginHistoryEntity.getLoginDate().getTime();
    }

    @ExoTransactional
    public void addLoginHistoryEntry(String userId) throws  Exception {
        try {
            LoginHistoryEntity loginHistoryEntity = new LoginHistoryEntity(userId);
            create(loginHistoryEntity); //the create method will return the entity which we'll ignore.
        } catch (Exception e) {
            LOG.debug("Error while adding a login entry for user '" + userId + "': " + e.getMessage(), e);
            throw e;
        }
    }

    public List<LoginHistoryEntity> getLoginHistory(String userId, long fromTime, long toTime) throws Exception {
        try {
            return getEntityManager().createNamedQuery("loginHistory.getUserLoginHistory")
                    .setParameter("userId",userId)
                    .setParameter("from",fromTime)
                    .setParameter("to",toTime).getResultList();
        } catch (Exception e) {
            LOG.debug("Error while getting the login history of user '" + userId + "': " + e.getMessage(), e);
            throw e;
        }
    }

    public Set<String> getLastUsersLogin(long fromTime) throws Exception {
        return null;
    }

    public boolean isActiveUser(String userId, int days) {
        LoginHistoryEntity loginHistoryEntity = (LoginHistoryEntity) find(userId);
        Long beforeLastLogin = loginHistoryEntity.getLoginDate().getTime();
        // return true if it's the first login of user
        if (beforeLastLogin == 0) return true;
        //
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH,calendar.get(Calendar.DAY_OF_MONTH) - days);
        long limitTime = calendar.getTimeInMillis();
        return beforeLastLogin >= limitTime;
    }

    public Map<String, Integer> getActiveUsers(long fromTime) {
        return null;
    }

    public List<LoginHistoryEntity> getLoginCountPerDaysInWeek(String userId, long week) throws Exception {
        List<LoginHistoryEntity> list = new ArrayList<LoginHistoryEntity>();
        List<Long> days = new ArrayList<Long>();

        long now = System.currentTimeMillis();
        long nextWeek = nextMonday(week);

        long day = week;
        LoginHistoryEntity loginHistoryEntity;

        return null;
    }

    public List<LoginHistoryEntity> getLoginCountPerWeeksInMonths(String userId, long fromMonth, int numOfMonths) throws Exception {
        return null;
    }

    public List<LoginHistoryEntity> getLoginCountPerMonthsInYear(String userId, long year) throws Exception {
        return null;
    }

}
