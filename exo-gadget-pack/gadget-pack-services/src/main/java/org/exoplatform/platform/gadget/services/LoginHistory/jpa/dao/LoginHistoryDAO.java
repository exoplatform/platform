package org.exoplatform.platform.gadget.services.LoginHistory.jpa.dao;

import org.exoplatform.commons.persistence.impl.GenericDAOJPAImpl;
import org.exoplatform.platform.gadget.services.LoginHistory.jpa.entity.LoginHistoryEntity;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import javax.persistence.NoResultException;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

public class LoginHistoryDAO extends GenericDAOJPAImpl<LoginHistoryEntity, Long> {
  private static final Log LOG = ExoLogger.getLogger(LoginHistoryDAO.class);

  /**
   * returns a list of users IDs that logged in from a given date.
   * 
   * @param fromTime
   * @return
   */
  public List<String> getActiveUsersId(Long fromTime) {
    Timestamp from = new Timestamp(fromTime);
    List<String> activeUsersId;
    try {
      activeUsersId = getEntityManager().createNamedQuery("loginHistory.getActiveUsersId")
                                        .setParameter("from", from)
                                        .getResultList();
    } catch (NoResultException e) {
      LOG.error("No active users found.");
      activeUsersId = null;
    }
    return activeUsersId;
  }

  /**
   * returns the count of logins per day for a given user.
   *
   * @param userId
   * @param fromDay
   * @param toDay
   * @return
   */
  public Long getLoginCountPerDay(String userId, Long fromDay, Long toDay) {
    Timestamp from = new Timestamp(fromDay);
    Timestamp to = new Timestamp(toDay);
    Long count;
    try {
      count = (Long) getEntityManager().createNamedQuery("loginHistory.getLoginsCountOfUserInDateRange")
                                       .setParameter("userId", userId)
                                       .setParameter("from", from)
                                       .setParameter("to", to)
                                       .getSingleResult();
    } catch (NoResultException e) {
      LOG.error("No login found for " + userId + ".");
      count = null;
    }
    return count;
  }

  /**
   * returns the count per day of all users logins.
   * 
   * @param fromDay
   * @param toDay
   * @return
   */
  public Long getLoginsCountInDateRange(Long fromDay, Long toDay) {
    Timestamp from = new Timestamp(fromDay);
    Timestamp to = new Timestamp(toDay);
    Long count;
    try {
      count = (Long) getEntityManager().createNamedQuery("loginHistory.getLoginsCountInDateRange")
                                       .setParameter("from", from)
                                       .setParameter("to", to)
                                       .getSingleResult();
    } catch (NoResultException e) {
      LOG.error("No Login found.");
      count = null;
    }
    return count;
  }

  /**
   * returns the last login entry date of a given user.
   *
   * @param userId
   * @return
   */
  public Long getLastLogin(String userId) {
    Long lastLogin;
    try {
      LoginHistoryEntity loginHistoryEntity = getEntityManager()
                                                                .createNamedQuery("loginHistory.getLastLoginsOfUser",
                                                                                  LoginHistoryEntity.class)
                                                                .setParameter("userId", userId)
                                                                .setMaxResults(1)
                                                                .getSingleResult();
      lastLogin = loginHistoryEntity.getLoginDate().getTime();
    } catch (NoResultException e) {
      LOG.info("No last login found for " + userId + ".");
      lastLogin = null;
    }
    return lastLogin;
  }

  /**
   * returns the last n history logins of a user set by a given limit number
   * numLogins.
   *
   * @param numLogins
   * @param userId
   * @return
   */
  public List<LoginHistoryEntity> getLastLoginsOfUser(int numLogins, String userId) {
    List<LoginHistoryEntity> loginHistoryEntityList;
    try {
      loginHistoryEntityList = getEntityManager().createNamedQuery("loginHistory.getLastLoginsOfUser", LoginHistoryEntity.class)
                                                 .setParameter("userId", userId)
                                                 .setMaxResults(numLogins)
                                                 .getResultList();
    } catch (NoResultException e) {
      LOG.error("No login found for " + userId + ".");
      loginHistoryEntityList = null;
    }
    return loginHistoryEntityList;
  }

  /**
   * returns the last n user IDs set by a given limit number numLogins.
   *
   * @param numLogins
   * @return
   */
  public List<String> getLastLoggedUsers(int numLogins) {
    List<String> lastLoggedUsers = new LinkedList<>();
    try {
      List<String> resultList = getEntityManager().createNamedQuery("loginHistory.getAllLoggedUsers").getResultList();
      if (resultList == null) {
        lastLoggedUsers = null;
      } else {
        List<String> lastLoggedUsersList = resultList.stream().distinct().collect(Collectors.toList());
        for (int i = 0; i < numLogins && i < lastLoggedUsersList.size(); i++) {
          lastLoggedUsers.add(lastLoggedUsersList.get(i));
        }
      }
    } catch (Exception e) {
      LOG.error("No logged Users found" + e.getMessage(), e);
      lastLoggedUsers = null;
    }
    return lastLoggedUsers;
  }

  /**
   * returns the last history login entry of a given user.
   *
   * @param userId
   * @return
   */
  public LoginHistoryEntity getLastLoginOfUser(String userId) {
    LoginHistoryEntity lastLogin;
    try {
      lastLogin = getEntityManager().createNamedQuery("loginHistory.getLastLoginsOfUser", LoginHistoryEntity.class)
                                    .setParameter("userId", userId)
                                    .setMaxResults(1)
                                    .getSingleResult();
    } catch (NoResultException e) {
      LOG.info("No last login found for " + userId + ".");
      lastLogin = null;
    }
    return lastLogin;
  }

  /**
   * returns the last login history entry.
   * 
   * @return
   */
  public LoginHistoryEntity getLastLoginHistory() {
    LoginHistoryEntity lastHistoryEntity;
    try {
      lastHistoryEntity = getEntityManager().createNamedQuery("loginHistory.getLastLoginHistory", LoginHistoryEntity.class)
                                            .setMaxResults(1)
                                            .getSingleResult();
    } catch (NoResultException e) {
      LOG.error("No last login found.");
      lastHistoryEntity = null;
    }
    return lastHistoryEntity;
  }

  /**
   * returns a list of login history entries of a given user between a given two
   * dates, fromTime and toTime.
   *
   * @param userId
   * @param fromTime
   * @param toTime
   * @return
   */
  public List<LoginHistoryEntity> getLoginHistory(String userId, long fromTime, long toTime) {
    Timestamp from = new Timestamp(fromTime);
    Timestamp to = new Timestamp(toTime);
    List<LoginHistoryEntity> loginHistoryEntityList;
    try {
      loginHistoryEntityList = getEntityManager()
                                                 .createNamedQuery("loginHistory.getLastLoginsOfUserInDateRange",
                                                                   LoginHistoryEntity.class)
                                                 .setParameter("userId", userId)
                                                 .setParameter("from", from)
                                                 .setParameter("to", to)
                                                 .getResultList();
    } catch (NoResultException e) {
      LOG.error("No login found for " + userId + ".");
      loginHistoryEntityList = null;
    }
    return loginHistoryEntityList;
  }

  /**
   * returns a list of login history entries a given two dates, fromTime and
   * toTime.
   *
   * @param fromTime
   * @param toTime
   * @return
   */
  public List<LoginHistoryEntity> getAllLoginHistory(long fromTime, long toTime) {
    Timestamp from = new Timestamp(fromTime);
    Timestamp to = new Timestamp(toTime);
    List<LoginHistoryEntity> loginHistoryEntityList;
    try {
      loginHistoryEntityList = getEntityManager()
                                                 .createNamedQuery("loginHistory.getLastLoginsInDateRange",
                                                                   LoginHistoryEntity.class)
                                                 .setParameter("from", from)
                                                 .setParameter("to", to)
                                                 .getResultList();
    } catch (NoResultException e) {
      LOG.error("No logins found.");
      loginHistoryEntityList = null;
    }
    return loginHistoryEntityList;
  }

  /**
   * returns a list of user IDs that did log in from a given date till now.
   *
   * @param fromTime
   * @return
   * @throws Exception
   */
  public Set<String> getLastLoginsAfterDate(long fromTime) throws Exception {
    Timestamp from = new Timestamp(fromTime);
    Set<String> users;
    try {
      List<String> userIds = getEntityManager().createNamedQuery("loginHistory.getLastLoginsAfterDate")
                                               .setParameter("from", from)
                                               .getResultList();
      users = new LinkedHashSet<>(userIds);
    } catch (NoResultException e) {
      LOG.error("No login found since " + from + ".");
      users = null;
    }
    return users;
  }

  /**
   * returns the just before last login entry date of a given user.
   *
   * @param userId
   * @return
   * @throws Exception
   */
  public long getBeforeLastLogin(String userId) throws Exception {
    long beforeLastLogin;
    try {
      LoginHistoryEntity lastLogin = getLastLoginOfUser(userId);
      if (lastLogin == null) {
        beforeLastLogin = 0;
      } else {
        Long lastLoginId = lastLogin.getID();
        Long beforeLastLoginId = (Long) getEntityManager().createNamedQuery("loginHistory.getBeforeLastLoginID")
                                                          .setParameter("userId", userId)
                                                          .setParameter("id", lastLoginId)
                                                          .getSingleResult();
        if (beforeLastLoginId == null) {
          beforeLastLogin = 0;
        } else {
          LoginHistoryEntity loginHistoryEntity = find(beforeLastLoginId);
          beforeLastLogin = loginHistoryEntity.getLoginDate().getTime();
        }
      }
    } catch (Exception e) {
      LOG.error("Error while retrieving " + userId + "'s before last login: " + e.getMessage(), e);
      beforeLastLogin = 0;
    }
    return beforeLastLogin;
  }

}
