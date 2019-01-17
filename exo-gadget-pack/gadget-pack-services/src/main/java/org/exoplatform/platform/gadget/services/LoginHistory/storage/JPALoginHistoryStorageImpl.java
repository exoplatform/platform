package org.exoplatform.platform.gadget.services.LoginHistory.storage;

import org.exoplatform.commons.api.persistence.ExoTransactional;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.platform.gadget.services.LoginHistory.LastLoginBean;
import org.exoplatform.platform.gadget.services.LoginHistory.LoginCounterBean;
import org.exoplatform.platform.gadget.services.LoginHistory.LoginHistoryBean;
import org.exoplatform.platform.gadget.services.LoginHistory.jpa.dao.LoginHistoryDAO;
import org.exoplatform.platform.gadget.services.LoginHistory.jpa.entity.LoginHistoryEntity;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.OrganizationService;
import org.joda.time.DateTimeConstants;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

public class JPALoginHistoryStorageImpl implements LoginHistoryStorage {
  private static final Log LOG       = ExoLogger.getLogger(JPALoginHistoryStorageImpl.class);

  private String           ALL_USERS = "AllUsers";

  private LoginHistoryDAO  loginHistoryDAO;

  private ZoneId           ZONE_ID   = ZoneId.systemDefault();

  public JPALoginHistoryStorageImpl(LoginHistoryDAO loginHistoryDAO) {
    this.loginHistoryDAO = loginHistoryDAO;
  }

  /**
   * returns the full name of a given user ID.
   *
   * @param userId {@link String}
   * @return String
   */
  private String getUserFullName(String userId) {
    try {
      OrganizationService service =
                                  ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(OrganizationService.class);
      return service.getUserHandler().findUserByName(userId).getFullName();
    } catch (Exception e) {
      return userId;
    }
  }

  private static long nextMonday(long date) {
    Instant instant = Instant.ofEpochMilli(date);
    ZoneId zoneId = ZoneId.systemDefault();
    ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, zoneId);
    ZonedDateTime nextMonday = zonedDateTime.with(TemporalAdjusters.next(DayOfWeek.MONDAY));

    return nextMonday.toInstant().toEpochMilli();
  }

  /**
   * returns a list of login counter bean that contains for each day the number of
   * logins between two given dates for a given user.
   *
   * @param userId {@link String}
   * @param fromDate long
   * @param toDate long
   * @return LoginCounterBean list
   */
  public List<LoginCounterBean> getLoginCountPerDaysInRange(String userId, long fromDate, long toDate) {
    List<LoginCounterBean> counterBeanList = new ArrayList<>();
    try {
      // set the fromDate to 00:00
      Instant instant1 = Instant.ofEpochMilli(fromDate);
      ZonedDateTime zonedDateTime1 = ZonedDateTime.ofInstant(instant1, ZONE_ID);
      ZonedDateTime from = zonedDateTime1.with(LocalTime.MIDNIGHT);

      // set the toDate to 23:59
      Instant instant2 = Instant.ofEpochMilli(toDate);
      ZonedDateTime zonedDateTime2 = ZonedDateTime.ofInstant(instant2, ZONE_ID);
      ZonedDateTime to = zonedDateTime2.with(LocalTime.MAX);

      // instantiate first day, next day and lastDay variables
      long firstDay = from.toInstant().toEpochMilli();
      Long nextDay;
      Long lastDay = to.toInstant().toEpochMilli();

      // returns the user's login count for each day and add it to a list:
      // loginCount/day
      while (firstDay <= lastDay) {
        LoginCounterBean loginCountPerDay = new LoginCounterBean();

        // set next day
        Instant instant3 = Instant.ofEpochMilli(firstDay);
        ZonedDateTime zonedDateTime3 = ZonedDateTime.ofInstant(instant3, ZONE_ID);
        ZonedDateTime next = zonedDateTime3.plusDays(1);
        nextDay = next.toInstant().toEpochMilli();

        // set end of day
        Instant instant4 = Instant.ofEpochMilli(firstDay);
        ZonedDateTime zonedDateTime4 = ZonedDateTime.ofInstant(instant4, ZONE_ID);
        ZonedDateTime end = zonedDateTime4.with(LocalTime.MAX);
        Long endOfDay = end.toInstant().toEpochMilli();

        Long count;
        if (userId.equals(ALL_USERS) || userId == null) {
          count = loginHistoryDAO.getLoginsCountInDateRange(firstDay, endOfDay);
        } else {
          count = loginHistoryDAO.getLoginCountPerDay(userId, firstDay, endOfDay);
        }
        loginCountPerDay.setLoginCount(count);
        loginCountPerDay.setLoginDate(firstDay);
        counterBeanList.add(loginCountPerDay);

        firstDay = nextDay;
      }
    } catch (Exception e) {
      LOG.error("Error while returning the Login Count Per Days In Range of " + userId + ":" + e.getMessage());
      counterBeanList = null;
    }
    return counterBeanList;
  }

  private int getLoginCountInDateRange(String userId, long fromDate, long toDate) {
    List<LoginCounterBean> loginCounts = getLoginCountPerDaysInRange(userId, fromDate, toDate);
    int sum = (int) loginCounts.stream().mapToLong(LoginCounterBean::getLoginCount).sum();
    return sum;
  }

  @Override
  public long getLastLogin(String userId) {
    Long lastLogin = loginHistoryDAO.getLastLogin(userId);
    if (lastLogin != null) {
      return lastLogin;
    } else {
      return 0;
    }
  }

  /**
   * if the userIdFilter is set to all users (%), returns a list of n last login
   * beans that contains for each user: the user id, the user's name, user's last
   * and before last login. if set to a user's id it returns its last n login
   * beans where n is the given limit number numLogins. but if the limit number
   * isn't set it returns just the last login bean of the given user
   *
   * @param numLogins int
   * @param userIdFilter {@link String}
   * @return LastLoginBean list
   */
  @Override
  public List<LastLoginBean> getLastLogins(int numLogins, String userIdFilter) {
    String userId = userIdFilter;
    List<LoginHistoryEntity> loginHistoryEntityList = new LinkedList<>();
    List<LastLoginBean> lastLoginBeanList;
    try {
      if (numLogins != 0 && (userId == null || userId.equals("%"))) {
        List<String> users = loginHistoryDAO.getLastLoggedUsers(numLogins);
        for (String user : users) {
          LoginHistoryEntity loginHistoryEntity = loginHistoryDAO.getLastLoginOfUser(user);
          loginHistoryEntityList.add(loginHistoryEntity);
        }
      } else if (userId != null && !userId.equals("%")) {
        if (numLogins == 0) {
          loginHistoryEntityList = loginHistoryDAO.getLastLoginsOfUser(1, userId);
        } else {
          loginHistoryEntityList = loginHistoryDAO.getLastLoginsOfUser(numLogins, userId);
        }
      } else {
        loginHistoryEntityList.add(loginHistoryDAO.getLastLoginHistory());
      }

      lastLoginBeanList = convertToLastLoginBeanList(loginHistoryEntityList);
    } catch (Exception e) {
      LOG.debug("Error while retrieving last logins: " + e.getMessage(), e);
      lastLoginBeanList = null;
    }
    return lastLoginBeanList;
  }

  @ExoTransactional
  public void addLoginHistoryEntry(String userId, long loginTime) {
    try {
      Date loginDate = new Date(loginTime);
      LoginHistoryEntity loginHistoryEntity = new LoginHistoryEntity(userId, loginDate);
      loginHistoryDAO.create(loginHistoryEntity); // the create method will return the entity which we'll ignore.
    } catch (Exception e) {
      LOG.error("Error while adding user " + userId + ":" + e.getMessage());
    }
  }

  /**
   * if the user id is set to "AllUsers" it returns a list of login history beans
   * between two given dates that contains for each user: the user id, the user's
   * name and the login date. else it returns the list of login history beans for
   * a given user between the two dates.
   *
   * @param userId {@link String}
   * @param fromTime long
   * @param toTime long
   * @return LoginHistoryBean list
   */
  @Override
  public List<LoginHistoryBean> getLoginHistory(String userId, long fromTime, long toTime) {
    List<LoginHistoryBean> loginHistoryBeanList;
    try {
      if (userId.equals(ALL_USERS) || userId == null) {
        List<LoginHistoryEntity> loginHistoryEntityList1 = loginHistoryDAO.getAllLoginHistory(fromTime, toTime);
        loginHistoryBeanList = convertToLoginHistoryBeanList(loginHistoryEntityList1);

      } else {
        List<LoginHistoryEntity> loginHistoryEntityList = loginHistoryDAO.getLoginHistory(userId, fromTime, toTime);
        loginHistoryBeanList = convertToLoginHistoryBeanList(loginHistoryEntityList);

      }
    } catch (Exception e) {
      LOG.error("Error while returning Login History of " + userId + ":" + e.getMessage());
      loginHistoryBeanList = null;
    }
    return loginHistoryBeanList;
  }

  @Override
  public Set<String> getLastUsersLogin(long fromTime) throws Exception {
    return loginHistoryDAO.getLastLoginsAfterDate(fromTime);
  }

  @Override
  public List<LoginCounterBean> getLoginCountPerDaysInWeek(String userId, long week) {
    List<LoginCounterBean> list = new ArrayList<>();
    List<Long> days = new ArrayList<>();

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

      day += DateTimeConstants.MILLIS_PER_DAY;
    } while (day < nextWeek);

    long leftDays = 0;
    for (LoginCounterBean counterBean : list) {
      leftDays += counterBean.getLoginCount();
    }

    List<LoginCounterBean> counters = getLoginCountPerDaysInRange(userId,
                                                                  week,
                                                                  nextMonday(week) - DateTimeConstants.MILLIS_PER_DAY
                                                                      + (leftDays * DateTimeConstants.MILLIS_PER_DAY));

    Iterator<LoginCounterBean> iter = counters.iterator();
    while (iter.hasNext()) {
      loginCountPerDay = iter.next();
      list.set(days.indexOf(loginCountPerDay.getLoginDate()), loginCountPerDay);
    }

    return list;
  }

  @Override
  public List<LoginCounterBean> getLoginCountPerWeeksInMonths(String userId, long fromMonth, int numOfMonths) {
    Instant instant = Instant.now();
    ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, ZONE_ID);

    long now = zonedDateTime.toInstant().toEpochMilli();

    Instant instant1 = Instant.ofEpochMilli(fromMonth);
    ZonedDateTime zonedDateTime1 = ZonedDateTime.ofInstant(instant1, ZONE_ID);

    ZonedDateTime zonedDateTime2 = zonedDateTime1.withMonth(numOfMonths);
    long toMonth = zonedDateTime2.toInstant().toEpochMilli();

    long fromDate, toDate = fromMonth;
    List<LoginCounterBean> list = new ArrayList<>();

    do {
      fromDate = toDate;
      toDate = nextMonday(toDate);
      if (toDate > toMonth)
        toDate = toMonth;

      LoginCounterBean loginCountPerWeek = new LoginCounterBean();
      loginCountPerWeek.setLoginDate(fromDate);
      loginCountPerWeek.setLoginCount(fromDate > now ? -1
                                                     : getLoginCountInDateRange(userId,
                                                                                fromDate,
                                                                                toDate - DateTimeConstants.MILLIS_PER_DAY));

      list.add(loginCountPerWeek);
    } while (toDate < toMonth);

    return list;
  }

  @Override
  public List<LoginCounterBean> getLoginCountPerMonthsInYear(String userId, long year) {
    Instant instant = Instant.now();
    ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, ZONE_ID);
    long now = zonedDateTime.toInstant().toEpochMilli();

    Instant instant1 = Instant.ofEpochMilli(year);
    ZonedDateTime zonedDateTime1 = ZonedDateTime.ofInstant(instant1, ZONE_ID);
    ZonedDateTime zonedDateTime2 = zonedDateTime1.withYear(1);
    long nextYear = zonedDateTime2.toInstant().toEpochMilli();

    long fromDate, toDate = year;
    List<LoginCounterBean> list = new ArrayList<>();

    do {
      fromDate = toDate;
      Instant instant2 = Instant.ofEpochMilli(toDate);
      ZonedDateTime zonedDateTime3 = ZonedDateTime.ofInstant(instant2, ZONE_ID);
      ZonedDateTime zonedDateTime4 = zonedDateTime3.withMonth(1);

      toDate = zonedDateTime4.toInstant().toEpochMilli();
      if (toDate > nextYear)
        toDate = nextYear;

      LoginCounterBean loginCountPerWeek = new LoginCounterBean();
      loginCountPerWeek.setLoginDate(fromDate);
      loginCountPerWeek.setLoginCount(fromDate > now ? -1
                                                     : getLoginCountInDateRange(userId,
                                                                                fromDate,
                                                                                toDate - DateTimeConstants.MILLIS_PER_DAY));

      list.add(loginCountPerWeek);
    } while (toDate < nextYear);

    return list;
  }

  @Override
  public long getBeforeLastLogin(String userId) throws Exception {
    return loginHistoryDAO.getBeforeLastLogin(userId);
  }

  /**
   * returns a converted LoginHistoryBean from a given LoginHistoryEntity.
   *
   * @param loginHistoryEntity {@link LoginHistoryEntity}
   * @return Login History Bean
   */
  private LoginHistoryBean convertToLoginHistoryBean(LoginHistoryEntity loginHistoryEntity) {
    LoginHistoryBean loginHistoryBean = new LoginHistoryBean();
    String userID = loginHistoryEntity.getUserID();
    String userName = getUserFullName(loginHistoryEntity.getUserID());
    long LoginTime = loginHistoryEntity.getLoginDate().getTime();

    loginHistoryBean.setUserId(userID);
    loginHistoryBean.setUserName(userName);
    loginHistoryBean.setLoginTime(LoginTime);
    return loginHistoryBean;
  }

  /**
   * returns a converted LastLoginBean from a given LoginHistoryEntity.
   * 
   * @param loginHistoryEntity {@link LoginHistoryEntity}
   * @return Last Login Bean
   * @throws Exception
   */
  private LastLoginBean convertToLastLoginBean(LoginHistoryEntity loginHistoryEntity) throws Exception {
    LastLoginBean lastLoginBean = new LastLoginBean();
    String userID = loginHistoryEntity.getUserID();
    String userName = getUserFullName(loginHistoryEntity.getUserID());
    long lastLogin = loginHistoryEntity.getLoginDate().getTime();
    long beforeLastLogin = getBeforeLastLogin(loginHistoryEntity.getUserID());

    lastLoginBean.setUserId(userID);
    lastLoginBean.setUserName(userName);
    lastLoginBean.setLastLogin(lastLogin);
    lastLoginBean.setBeforeLastLogin(beforeLastLogin);
    return lastLoginBean;
  }

  /**
   * returns a converted list of LoginHistoryBeans from a given list of
   * LoginHistoryEntities.
   *
   * @param loginHistoryEntityList List<{@link LoginHistoryEntity}>
   * @return LoginHistoryBean list
   */
  private List<LoginHistoryBean> convertToLoginHistoryBeanList(List<LoginHistoryEntity> loginHistoryEntityList) {
    List<LoginHistoryBean> loginHistoryBeanList = new ArrayList<>();
    LoginHistoryBean loginHistoryBean;

    for (LoginHistoryEntity loginHistoryEntity : loginHistoryEntityList) {
      loginHistoryBean = convertToLoginHistoryBean(loginHistoryEntity);
      loginHistoryBeanList.add(loginHistoryBean);
    }
    return loginHistoryBeanList;
  }

  /**
   * returns a converted list of LastLoginBeans from a given list of
   * LoginHistoryEntities.
   * 
   * @param loginHistoryEntityList List<{@link LoginHistoryEntity}>>
   * @return LastLoginBean list
   * @throws Exception
   */
  private List<LastLoginBean> convertToLastLoginBeanList(List<LoginHistoryEntity> loginHistoryEntityList) throws Exception {
    List<LastLoginBean> lastLoginBeanList = new ArrayList<>();
    LastLoginBean lastLoginBean;

    for (LoginHistoryEntity loginHistoryEntity : loginHistoryEntityList) {
      lastLoginBean = convertToLastLoginBean(loginHistoryEntity);
      lastLoginBeanList.add(lastLoginBean);
    }
    return lastLoginBeanList;
  }
}
