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

import java.sql.Timestamp;
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
   * @param userId
   * @return
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
   * @param userId
   * @param fromDate
   * @param toDate
   * @return
   * @throws Exception
   */
  public List<LoginCounterBean> getLoginCountPerDaysInRange(String userId, long fromDate, long toDate) throws Exception {
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

      List<LoginCounterBean> counterBeanList = new ArrayList<>();

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
      return counterBeanList;
    } catch (Exception e) {
      throw e;
    }
  }

  private int getLoginCountInDateRange(String userId, long fromDate, long toDate) throws Exception {
    List<LoginCounterBean> loginCounts = getLoginCountPerDaysInRange(userId, fromDate, toDate);

    int sum = 0;
    Iterator<LoginCounterBean> iter = loginCounts.iterator();
    while (iter.hasNext()) {
      sum += iter.next().getLoginCount();
    }
    return sum;
  }

  @Override
  public long getLastLogin(String userId) throws Exception {
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
   * @param numLogins
   * @param userIdFilter
   * @return
   * @throws Exception
   */
  @Override
  public List<LastLoginBean> getLastLogins(int numLogins, String userIdFilter) throws Exception {
    String userId = userIdFilter;
    List<LoginHistoryEntity> loginHistoryEntityList = new LinkedList<>();
    List<LastLoginBean> lastLoginBeanList = new LinkedList<>();
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
  public void addLoginHistoryEntry(String userId, long loginTime) throws Exception {
    try {
      Date loginDate = new Date(loginTime);
      LoginHistoryEntity loginHistoryEntity = new LoginHistoryEntity(userId, loginDate);
      loginHistoryDAO.create(loginHistoryEntity); // the create method will return the entity which we'll ignore.
    } catch (Exception e) {
      throw e;
    }
  }

  /**
   * if the user id is set to "AllUsers" it returns a list of login history beans
   * between two given dates that contains for each user: the user id, the user's
   * name and the login date. else it returns the list of login history beans for
   * a given user between the two dates.
   *
   * @param userId
   * @param fromTime
   * @param toTime
   * @return
   * @throws Exception
   */
  @Override
  public List<LoginHistoryBean> getLoginHistory(String userId, long fromTime, long toTime) throws Exception {
    List<LoginHistoryBean> loginHistoryBeanList;
    try {
      if (userId.equals(ALL_USERS) || userId == null) {
        List<LoginHistoryEntity> loginHistoryEntityList1 = loginHistoryDAO.getAllLoginHistory(fromTime, toTime);
        loginHistoryBeanList = convertToLoginHistoryBeanList(loginHistoryEntityList1);

      } else {
        List<LoginHistoryEntity> loginHistoryEntityList = loginHistoryDAO.getLoginHistory(userId, fromTime, toTime);
        loginHistoryBeanList = convertToLoginHistoryBeanList(loginHistoryEntityList);

      }
      return loginHistoryBeanList;

    } catch (Exception e) {
      throw e;
    }
  }

  @Override
  public Set<String> getLastUsersLogin(long fromTime) throws Exception {
    return loginHistoryDAO.getLastLoginsAfterDate(fromTime);
  }

  /**
   * returns if a given user is still logged in or not from a given date.
   *
   * @param userId
   * @param days
   * @return
   * @throws Exception
   */
  public boolean isActiveUser(String userId, int days) throws Exception {
    Long beforeLastLogin = getBeforeLastLogin(userId);
    // return true [if it's the first login of user]
    if (beforeLastLogin == 0) {
      return true;
    }
    //
    ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(Instant.now(), ZONE_ID);
    ZonedDateTime limit = zonedDateTime.minusDays(days);
    long limitTime = limit.toInstant().toEpochMilli();
    return beforeLastLogin >= limitTime;
  }

  @Override
  public Map<String, Integer> getActiveUsers(long fromTime) {
    Map<String, Integer> activeUsers = new LinkedHashMap<>();
    List<String> list = loginHistoryDAO.getActiveUsersId(fromTime);
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    Long toTime = timestamp.getTime();
    for (String userId : list) {
      Long numberOfLogin = loginHistoryDAO.getLoginCountPerDay(userId, fromTime, toTime);
      activeUsers.put(userId, Math.toIntExact(numberOfLogin));
    }
    return activeUsers;
  }

  @Override
  public List<LoginCounterBean> getLoginCountPerDaysInWeek(String userId, long week) throws Exception {
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
  public List<LoginCounterBean> getLoginCountPerWeeksInMonths(String userId, long fromMonth, int numOfMonths) throws Exception {
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
  public List<LoginCounterBean> getLoginCountPerMonthsInYear(String userId, long year) throws Exception {
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
   * @param loginHistoryEntity
   * @return
   */
  public LoginHistoryBean convertToLoginHistoryBean(LoginHistoryEntity loginHistoryEntity) {
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
   * @param loginHistoryEntity
   * @return
   * @throws Exception
   */
  public LastLoginBean convertToLastLoginBean(LoginHistoryEntity loginHistoryEntity) throws Exception {
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
   * @param loginHistoryEntityList
   * @return
   */
  public List<LoginHistoryBean> convertToLoginHistoryBeanList(List<LoginHistoryEntity> loginHistoryEntityList) {
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
   * @param loginHistoryEntityList
   * @return
   * @throws Exception
   */
  public List<LastLoginBean> convertToLastLoginBeanList(List<LoginHistoryEntity> loginHistoryEntityList) throws Exception {
    List<LastLoginBean> lastLoginBeanList = new ArrayList<>();
    LastLoginBean lastLoginBean;

    for (LoginHistoryEntity loginHistoryEntity : loginHistoryEntityList) {
      lastLoginBean = convertToLastLoginBean(loginHistoryEntity);
      lastLoginBeanList.add(lastLoginBean);
    }
    return lastLoginBeanList;
  }
}
