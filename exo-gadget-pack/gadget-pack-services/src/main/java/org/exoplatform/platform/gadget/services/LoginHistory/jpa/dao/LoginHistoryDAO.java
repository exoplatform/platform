package org.exoplatform.platform.gadget.services.LoginHistory.jpa.dao;

import org.exoplatform.commons.persistence.impl.GenericDAOJPAImpl;
import org.exoplatform.platform.gadget.services.LoginHistory.jpa.entity.LoginHistoryEntity;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.sql.Timestamp;
import java.util.*;

public class LoginHistoryDAO extends GenericDAOJPAImpl<LoginHistoryEntity, Long> {
    private static final Log LOG = ExoLogger.getLogger(LoginHistoryDAO.class);

    public List<String> getActiveUsersId(Long fromTime) {
        // returns a list of active users from a given time
        Timestamp from = new Timestamp(fromTime);
        return getEntityManager()
                .createNamedQuery("loginHistory.getActiveUsersId")
                .setParameter("from",from)
                .getResultList();
    }

    public Long getLoginCountPerDay(String userId, Long fromDay, Long toDay) {
        // returns the number of a user's login for a given period of time
        Timestamp from = new Timestamp(fromDay);
        Timestamp to = new Timestamp(toDay);
        if (userId.equals("AllUsers") || userId == null){
            return (Long) getEntityManager().createNamedQuery("loginHistory.getLoginsCountOfAllUserInDateRange").setParameter("from",from).setParameter("to",to).getSingleResult();
        } else {
            return (Long) getEntityManager().createNamedQuery("loginHistory.getLoginsCountOfUserInDateRange")
                    .setParameter("userId",userId)
                    .setParameter("from", from)
                    .setParameter("to",to).getSingleResult();
        }
    }

    public Long getLastLogin(String userId) {
        Long lastLogin;
        LoginHistoryEntity loginHistoryEntity = getEntityManager()
                    .createNamedQuery("loginHistory.getLastLoginsOfUser", LoginHistoryEntity.class)
                    .setParameter("userId", userId)
                    .setMaxResults(1)
                    .getSingleResult();
        lastLogin = loginHistoryEntity.getLoginDate().getTime();
        return loginHistoryEntity == null ? 0 : lastLogin;
    }

    public List<LoginHistoryEntity> getLastLoginsOfUser(int numLogins, String userId) {
        List<LoginHistoryEntity> loginHistoryEntityList = getEntityManager()
                .createNamedQuery("loginHistory.getLastLoginsOfUser", LoginHistoryEntity.class)
                .setParameter("userId", userId)
                .setMaxResults(numLogins)
                .getResultList();
        return loginHistoryEntityList;
    }

    public List<LoginHistoryEntity> getLastLogins(int numLogins) {
        List<LoginHistoryEntity> loginHistoryEntityList = getEntityManager()
                .createNamedQuery("loginHistory.getLastLogins", LoginHistoryEntity.class)
                .setMaxResults(numLogins)
                .getResultList();
        return loginHistoryEntityList;
    }

    public List<LoginHistoryEntity> getLoginHistory(String userId, long fromTime, long toTime) {
        Timestamp from = new Timestamp(fromTime);
        Timestamp to = new Timestamp(toTime);
        List<LoginHistoryEntity> loginHistoryEntityList = getEntityManager()
                .createNamedQuery("loginHistory.getLastLoginsOfUserInDateRange",LoginHistoryEntity.class)
                .setParameter("userId",userId)
                .setParameter("from",from)
                .setParameter("to",to).getResultList();
        return loginHistoryEntityList;
    }

    public List<LoginHistoryEntity> getAllLoginHistory(long fromTime, long toTime) {
        Timestamp from = new Timestamp(fromTime);
        Timestamp to = new Timestamp(toTime);
        List<LoginHistoryEntity> loginHistoryEntityList = getEntityManager()
                .createNamedQuery("loginHistory.getLastLoginsInDateRange",LoginHistoryEntity.class)
                .setParameter("from",from)
                .setParameter("to",to).getResultList();
        return loginHistoryEntityList;
    }

    public Set<String> getLastLoginsAfterDate(long fromTime) throws Exception {
        Timestamp from = new Timestamp(fromTime);
        try {
            List<String> userIds = getEntityManager()
                    .createNamedQuery("loginHistory.getLastLoginsAfterDate")
                    .setParameter("from",from)
                    .getResultList();
            Set<String> users = new LinkedHashSet<String>(userIds);
            return users;
        } catch (Exception e) {
            LOG.error("Error while getting login history of users " + e.getMessage(), e);
        }
        return null;
    }

    public long getBeforeLastLogin(String userId) throws Exception {
        try {
            LoginHistoryEntity lastLogin = getEntityManager()
                    .createNamedQuery("loginHistory.getLastLoginsOfUser", LoginHistoryEntity.class)
                    .setParameter("userId", userId)
                    .setMaxResults(1)
                    .getSingleResult();
            Long lastLoginID = lastLogin.getID();
            Long beforeLastLoginID = (Long) getEntityManager()
                    .createNamedQuery("loginHistory.getBeforeLastLoginID")
                    .setParameter("userId",userId)
                    .setParameter("id",lastLoginID)
                    .getSingleResult();
            LoginHistoryEntity loginHistoryEntity = find(beforeLastLoginID);
            return loginHistoryEntity.getLoginDate().getTime();
        } catch (Exception e) {
            LOG.debug("Error while retrieving " + userId + "'s last login: " + e.getMessage(), e);
            throw e;
        }
    }

}
