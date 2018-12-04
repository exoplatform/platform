package org.exoplatform.platform.gadget.services.LoginHistory.jpa;

import org.exoplatform.commons.persistence.impl.GenericDAOJPAImpl;
import org.exoplatform.platform.gadget.services.LoginHistory.jpa.dao.LoginHistoryDAO;
import org.exoplatform.platform.gadget.services.LoginHistory.jpa.entity.LoginHistoryEntity;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LoginHistoryDAOImpl extends GenericDAOJPAImpl implements LoginHistoryDAO {
    private static final Log LOG = ExoLogger.getLogger(LoginHistoryDAOImpl.class);

    @Override
    public Long countAll() {
        return (Long) getEntityManager().createNamedQuery("loginHistory.count").getSingleResult();
    }

    @Override
    public LoginHistoryEntity getLastLogin(String userID) throws Exception {
        try {
            long ID = (long) getEntityManager().createNamedQuery("loginHistory.getUserLastLoginID").setParameter("id",userID).getSingleResult();
            return (LoginHistoryEntity) getEntityManager().createNamedQuery("loginHistory.getLoginByID").setParameter("id",ID).getSingleResult();
        } catch (Exception e) {
            LOG.debug("Error while getting last login of user '" + userID + "': " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
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
                    .setParameter("today",today)
                    .setParameter("limit",numLogins).getResultList();
        } catch (Exception e) {
            LOG.debug("Error while getting last logins of user '" + userId + "': " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public LoginHistoryEntity getBeforeLastLogin(String userId) throws Exception {
        Long lastLoginID = (Long) getEntityManager().createNamedQuery("loginHistory.getLastUserLoginID").setParameter("userId",userId).getSingleResult();
        Long beforeLastLoginID = (Long) getEntityManager().createNamedQuery("loginHistory.getBeforeLastLoginID").setParameter("userId",userId).setParameter("id",lastLoginID).getSingleResult();
        return (LoginHistoryEntity) getEntityManager().createNamedQuery("loginHistory.getLoginByID").setParameter("id",beforeLastLoginID).getResultList();
    }

    @Override
    public void addLoginHistoryEntry(String userId) throws  Exception {
        try {
            Calendar calendar = Calendar.getInstance();
            Long date = calendar.getTime().getTime();
            calendar.setTimeInMillis(date);
            getEntityManager().createNamedQuery("loginHistory.addLoginHistory").setParameter("userId",userId).setParameter("date",date);
        } catch (Exception e) {
            LOG.debug("Error while adding a login entry for user '" + userId + "': " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
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

    @Override
    public List<LoginHistoryEntity> getLoginCountPerDaysInWeek(String userId, long week) throws Exception {
        return null;
    }

    @Override
    public List<LoginHistoryEntity> getLoginCountPerWeeksInMonths(String userId, long fromMonth, int numOfMonths) throws Exception {
        return null;
    }

    @Override
    public List<LoginHistoryEntity> getLoginCountPerMonthsInYear(String userId, long year) throws Exception {
        return null;
    }

    @Override
    public List<LoginHistoryEntity> findAll() {
        return getEntityManager().createNamedQuery("loginHistory.findAll").getResultList();
    }

}
