package org.exoplatform.platform.gadget.services.LoginHistory.jpa.dao;

import org.exoplatform.commons.persistence.impl.GenericDAOJPAImpl;
import org.exoplatform.platform.gadget.services.LoginHistory.jpa.entity.LoginHistoryEntity;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import javax.persistence.NoResultException;
import java.sql.Timestamp;
import java.util.*;

public class LoginHistoryDAO extends GenericDAOJPAImpl<LoginHistoryEntity, Long> {
    private static final Log LOG = ExoLogger.getLogger(LoginHistoryDAO.class);

    public List<String> getActiveUsersId(Long fromTime) {
        // returns a list of active users from a given time
        Timestamp from = new Timestamp(fromTime);
        return getEntityManager().createNamedQuery("loginHistory.getActiveUsersId").setParameter("from",from).getResultList();
    }

    public Long getLoginCountPerDay(String userId, Long fromDay, Long toDay) {
        // returns the number of a user's login for a given period of time
        Timestamp from = new Timestamp(fromDay);
        Timestamp to = new Timestamp(toDay);
        try {
            return (Long) getEntityManager().createNamedQuery("loginHistory.getLoginCountPerDay")
                                            .setParameter("userId",userId)
                                            .setParameter("from", from)
                                            .setParameter("to",to).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Long getLastLogin(String userId) {
        Long lastLogin;
        LoginHistoryEntity loginHistoryEntity = getEntityManager().createNamedQuery("loginHistory.getLastLoginHistory",LoginHistoryEntity.class).setParameter("userId",userId).getSingleResult();
        lastLogin = loginHistoryEntity.getLoginDate().getTime();
        try {
            return loginHistoryEntity == null ? 0 : lastLogin;
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<LoginHistoryEntity> getLastLogins(int numLogins, String userId) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        Timestamp today = new Timestamp(calendar.getTimeInMillis());

        List<LoginHistoryEntity> loginHistoryEntityList = getEntityManager().createNamedQuery("loginHistory.getLastLogins",LoginHistoryEntity.class)
                    .setParameter("userId",userId)
                    .setParameter("today",today)
                    .setMaxResults(numLogins)
                    .getResultList();
        return loginHistoryEntityList;
    }

    public List<LoginHistoryEntity> getLoginHistory(String userId, long fromTime, long toTime) {
        Timestamp from = new Timestamp(fromTime);
        Timestamp to = new Timestamp(toTime);
        List<LoginHistoryEntity> loginHistoryEntityList = getEntityManager().createNamedQuery("loginHistory.getUserLoginHistory",LoginHistoryEntity.class)
                .setParameter("userId",userId)
                .setParameter("from",from)
                .setParameter("to",to).getResultList();
        return loginHistoryEntityList;
    }

    public Set<String> getLastUsersLogin(long fromTime) throws Exception {
        Timestamp from = new Timestamp(fromTime);
        try {
            List<String> userIds = getEntityManager().createNamedQuery("loginHistory.getLastUsersLogin").setParameter("from",from).getResultList();
            Set<String> users = new LinkedHashSet<String>(userIds);
            return users;
        } catch (Exception e) {
            LOG.error("Error while getting login history of users " + e.getMessage(), e);
        }
        return null;
    }

    public long getBeforeLastLogin(String userId) {
        Long lastLoginID = (Long) getEntityManager().createNamedQuery("loginHistory.getUserLastLoginID").setParameter("userId",userId).getSingleResult();
        Long beforeLastLoginID = (Long) getEntityManager().createNamedQuery("loginHistory.getBeforeLastLoginID").setParameter("userId",userId).setParameter("id",lastLoginID).getSingleResult();
        LoginHistoryEntity loginHistoryEntity = find(beforeLastLoginID);
        return loginHistoryEntity.getLoginDate().getTime();
    }

}
