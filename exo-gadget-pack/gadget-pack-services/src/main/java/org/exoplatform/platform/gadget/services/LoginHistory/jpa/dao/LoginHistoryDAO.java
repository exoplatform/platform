package org.exoplatform.platform.gadget.services.LoginHistory.jpa.dao;

import org.exoplatform.commons.api.persistence.ExoTransactional;
import org.exoplatform.commons.persistence.impl.GenericDAOJPAImpl;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.platform.gadget.services.LoginHistory.LastLoginBean;
import org.exoplatform.platform.gadget.services.LoginHistory.LoginCounterBean;
import org.exoplatform.platform.gadget.services.LoginHistory.jpa.entity.LoginHistoryEntity;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.OrganizationService;

import java.sql.Timestamp;
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

    private String getUserFullName(String userId) {
        try {
            OrganizationService service = (OrganizationService) ExoContainerContext.getCurrentContainer()
                    .getComponentInstanceOfType(OrganizationService.class);
            return service.getUserHandler().findUserByName(userId).getFullName();
        } catch (Exception e) {
            return userId;
        }
    }

    public List<LoginCounterBean> getLoginCountPerDaysInRange(String userId, long fromDate, long toDate) throws Exception {
        /*Timestamp from = new Timestamp(fromDate);
        Timestamp to = new Timestamp(toDate);
        int today = from.getDate();
        today ++;
        ZonedDateTime time = ZonedDateTime.now();*/
        try {
            Calendar from = Calendar.getInstance();
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
                cal2.set(Calendar.HOUR_OF_DAY,23);
                Long endOfDay = cal2.getTimeInMillis();

                Timestamp fDay = new Timestamp(firstDay);
                Timestamp endDay = new Timestamp(endOfDay);

                Long count = (Long) getEntityManager().createNamedQuery("loginHistory.getLoginCountPerDay")
                        .setParameter("userId",userId)
                        .setParameter("from",fDay)
                        .setParameter("to",endDay).getSingleResult();

                loginCountPerDay.setLoginCount(count);
                loginCountPerDay.setLoginDate(firstDay);
                counterBeanList.add(loginCountPerDay);

                firstDay = nextDay;
            }
            return counterBeanList;
        } catch (Exception e) {
            LOG.debug("Error while getting login counts of user '" + userId + "': " + e.getMessage(), e);
            throw e;
        }
    }

    private int getLoginCount(String userId, long fromDate, long toDate) throws Exception {
        List<LoginCounterBean> loginCounts = getLoginCountPerDaysInRange(userId, fromDate, toDate);
        int sum = 0;
        Iterator<LoginCounterBean> iter = loginCounts.iterator();
        while (iter.hasNext()) {
            sum += iter.next().getLoginCount();
        }
        return sum;
    }

    public Long getLastLogin(String userId) throws Exception {
        try {
            long ID = (long) getEntityManager().createNamedQuery("loginHistory.getUserLastLoginID").setParameter("id",userId).getSingleResult();
            LoginHistoryEntity loginHistoryEntity = (LoginHistoryEntity) find(ID);
            return loginHistoryEntity.getLoginDate().getTime();
        } catch (Exception e) {
            LOG.debug("Error while retrieving " + userId + "'s last login: " + e.getMessage(), e);
            throw e;
        }
    }

    public List<LastLoginBean> getLastLogins(int numLogins, String userId) throws Exception {
        try {
            String userName = getUserFullName(userId);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY,0);
            calendar.set(Calendar.MINUTE,0);
            calendar.set(Calendar.SECOND,0);
            calendar.set(Calendar.MILLISECOND,0);
            Timestamp today = new Timestamp(calendar.getTimeInMillis());

            /*Date now = calendar.getTime();
            now.setHours(0);
            now.setMinutes(0);
            now.setSeconds(0);
            Long today = now.getTime();
            calendar.setTimeInMillis(today);*/

            List<LoginHistoryEntity> loginHistoryEntityList = getEntityManager().createNamedQuery("loginHistory.getLastLogins")
                    .setParameter("userId",userId)
                    .setParameter("today",today)
                    .setParameter("limit",numLogins).getResultList();
            List<LastLoginBean> lastLoginBeanList = new ArrayList<LastLoginBean>();

            LoginHistoryEntity loginHistoryEntity = new LoginHistoryEntity();
            for (int i=0; i<loginHistoryEntityList.size(); i++) {
                lastLoginBeanList.get(i).setUserId(loginHistoryEntityList.get(i).getUserID());
                lastLoginBeanList.get(i).setUserName(userName);
                lastLoginBeanList.get(i).setLastLogin(getLastLogin(userId));
                lastLoginBeanList.get(i).setBeforeLastLogin(getBeforeLastLogin(userId));
            }

            return lastLoginBeanList;
        } catch (Exception e) {
            LOG.debug("Error while getting last logins of user '" + userId + "': " + e.getMessage(), e);
            throw e;
        }
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
        Timestamp from = new Timestamp(fromTime);
        Timestamp to = new Timestamp(toTime);
        try {
            return getEntityManager().createNamedQuery("loginHistory.getUserLoginHistory")
                    .setParameter("userId",userId)
                    .setParameter("from",from)
                    .setParameter("to",to).getResultList();
        } catch (Exception e) {
            LOG.debug("Error while getting the login history of user '" + userId + "': " + e.getMessage(), e);
            throw e;
        }
    }

    public Set<String> getLastUsersLogin(long fromTime) throws Exception {
        Timestamp from = new Timestamp(fromTime);
        try {
            List<String> userIds = getEntityManager().createNamedQuery("loginHistory.getLastUsersLogin").setParameter("from",from).getResultList();
            Set<String> users = new LinkedHashSet<String>(userIds);
            return users;
        } catch (Exception e) {
            LOG.debug("Error while getting login history of users " + e.getMessage(), e);
        }
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

    public List<LoginCounterBean> getLoginCountPerDaysInWeek(String userId, long week) throws Exception {
        List<LoginCounterBean> list = new ArrayList<LoginCounterBean>();
        List<Long> days = new ArrayList<Long>();

        long now = System.currentTimeMillis();
        long nextWeek = nextMonday(week);

        long day = week;
        LoginCounterBean loginCountPerDay;

        do {
            loginCountPerDay = new LoginCounterBean();
            loginCountPerDay.setLoginDate(day);
            loginCountPerDay.setLoginCount(day > now ? -1 : 0);

            list.add(loginCountPerDay);
            days.add(day);

            day += DAY_IN_MILLISEC;
        } while (day < nextWeek);

        List<LoginCounterBean> counters = getLoginCountPerDaysInRange(userId, week, nextMonday(week) - DAY_IN_MILLISEC);

        Iterator<LoginCounterBean> iter = counters.iterator();
        while (iter.hasNext()) {
            loginCountPerDay = iter.next();
            list.set(days.indexOf(loginCountPerDay.getLoginDate()), loginCountPerDay);
        }

        return list;
    }

    public List<LoginCounterBean> getLoginCountPerWeeksInMonths(String userId, long fromMonth, int numOfMonths) throws Exception {
        Calendar cal = Calendar.getInstance();
        long now = cal.getTime().getTime();

        cal.setTimeInMillis(fromMonth);
        cal.add(Calendar.MONTH, numOfMonths);
        long toMonth = cal.getTimeInMillis();

        long fromDate, toDate = fromMonth;
        List<LoginCounterBean> list = new ArrayList<LoginCounterBean>();

        do {
            fromDate = toDate;
            toDate = nextMonday(toDate);
            if (toDate > toMonth) toDate = toMonth;

            LoginCounterBean loginCountPerWeek = new LoginCounterBean();
            loginCountPerWeek.setLoginDate(fromDate);
            loginCountPerWeek.setLoginCount(fromDate > now ? -1 : getLoginCount(userId, fromDate, toDate - DAY_IN_MILLISEC));

            list.add(loginCountPerWeek);
        } while (toDate < toMonth);

        return list;
    }

    public List<LoginCounterBean> getLoginCountPerMonthsInYear(String userId, long year) throws Exception {
        Calendar cal = Calendar.getInstance();
        long now = cal.getTime().getTime();

        cal.setTimeInMillis(year);
        cal.add(Calendar.YEAR, 1);
        long nextYear = cal.getTimeInMillis();

        long fromDate, toDate = year;
        List<LoginCounterBean> list = new ArrayList<LoginCounterBean>();

        do {
            fromDate = toDate;
            cal.setTimeInMillis(toDate);
            cal.add(Calendar.MONTH, 1);
            toDate = cal.getTimeInMillis();
            if (toDate > nextYear) toDate = nextYear;

            LoginCounterBean loginCountPerWeek = new LoginCounterBean();
            loginCountPerWeek.setLoginDate(fromDate);
            loginCountPerWeek.setLoginCount(fromDate > now ? -1 : getLoginCount(userId, fromDate, toDate - DAY_IN_MILLISEC));

            list.add(loginCountPerWeek);
        } while (toDate < nextYear);

        return list;
    }

    public long getBeforeLastLogin(String userId) throws Exception {
        Long lastLoginID = (Long) getEntityManager().createNamedQuery("loginHistory.getLastUserLoginID").setParameter("userId",userId).getSingleResult();
        Long beforeLastLoginID = (Long) getEntityManager().createNamedQuery("loginHistory.getBeforeLastLoginID").setParameter("userId",userId).setParameter("id",lastLoginID).getSingleResult();
        LoginHistoryEntity loginHistoryEntity = (LoginHistoryEntity) find(beforeLastLoginID);
        return loginHistoryEntity.getLoginDate().getTime();
    }

}
