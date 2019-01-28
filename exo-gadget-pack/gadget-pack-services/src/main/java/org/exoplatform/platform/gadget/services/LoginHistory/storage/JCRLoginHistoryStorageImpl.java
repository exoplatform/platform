package org.exoplatform.platform.gadget.services.LoginHistory.storage;

import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.platform.gadget.services.LoginHistory.*;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.access.PermissionType;
import org.exoplatform.services.jcr.core.ExtendedNode;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.impl.core.query.QueryImpl;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.OrganizationService;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import java.util.*;

public class JCRLoginHistoryStorageImpl implements LoginHistoryStorage {
  private static final Log  LOG             = ExoLogger.getLogger(LoginHistoryServiceImpl.class);

  private static String     HOME            = "exo:LoginHistoryHome";

  private static String     LOGIN_HISTORY   = "loginHistory";

  private static String     LOGIN_COUNTER   = "loginCounter";

  private static long       DAY_IN_MILLISEC = 86400000;

  private RepositoryService repositoryService;

  public JCRLoginHistoryStorageImpl(RepositoryService repositoryService) {
    this.repositoryService = repositoryService;
  }

  /**
   * Create exo:LoginHistoryHome node.
   *
   * @throws RepositoryException
   */
  protected void createHomeNode() throws RepositoryException {
    SessionProvider sProvider = SessionProvider.createSystemProvider();
    try {
      ManageableRepository currentRepo = this.repositoryService.getCurrentRepository();
      Session session = sProvider.getSession(currentRepo.getConfiguration().getDefaultWorkspaceName(), currentRepo);
      Node rootNode = session.getRootNode();
      if (!rootNode.hasNode(HOME)) {
        Node homeNode = rootNode.addNode(HOME, "exo:LoginHisSvc_loginHistoryService");
        homeNode.addMixin("exo:privilegeable");
        Map<String, String[]> permissions = new HashMap<String, String[]>();
        permissions.put("*:/platform/administrators", PermissionType.ALL);
        permissions.put("*:/platform/users", new String[] { PermissionType.READ });
        ((ExtendedNode) homeNode).setPermissions(permissions);

        homeNode.addMixin("exo:owneable");
        rootNode.save();
        // --- PLF-2493 : Umbrella for usability issues
        if (homeNode.canAddMixin("exo:hiddenable")) {
          homeNode.addMixin("exo:hiddenable");
        }
        Node globalLoginCounterNode = homeNode.addNode(ALL_USERS, "exo:LoginHisSvc_globalLoginCounter");
        globalLoginCounterNode.setProperty("exo:LoginHisSvc_globalLoginCounter_lastIndex", 0);
        homeNode.save();
        LOG.info("Login history storage initialized.");
      }
    } finally {
      sProvider.close();
    }
  }

  /**
   * Utility function to get JCR session in current repository
   *
   * @param sessionProvider
   * @return JCR session
   * @throws Exception
   */
  private Session getSession(SessionProvider sessionProvider) throws Exception {
    ManageableRepository currentRepo = this.repositoryService.getCurrentRepository();
    return sessionProvider.getSession(currentRepo.getConfiguration().getDefaultWorkspaceName(), currentRepo);
  }

  private String getUserFullName(String userId) {
    try {
      OrganizationService service =
                                  (OrganizationService) ExoContainerContext.getCurrentContainer()
                                                                           .getComponentInstanceOfType(OrganizationService.class);
      return service.getUserHandler().findUserByName(userId).getFullName();
    } catch (Exception e) {
      return userId;
    }
  }

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

  public List<LoginCounterBean> getLoginCountPerDaysInRange(String userId, long fromDate, long toDate) throws Exception {
    SessionProvider sProvider = SessionProvider.createSystemProvider();
    try {
      Session session = this.getSession(sProvider);

      QueryManager queryManager = session.getWorkspace().getQueryManager();
      String sqlStatement = "SELECT * FROM exo:LoginHisSvc_loginCounterItem "
          + "WHERE exo:LoginHisSvc_loginCounterItem_userId = '" + userId + "' "
          + "AND (exo:LoginHisSvc_loginCounterItem_loginDate BETWEEN " + Long.toString(fromDate) + " AND " + Long.toString(toDate)
          + ") " + "ORDER BY exo:LoginHisSvc_loginCounterItem_loginDate ASC";
      QueryImpl query = (QueryImpl) queryManager.createQuery(sqlStatement, Query.SQL);
      QueryResult result = query.execute();
      NodeIterator nodeIterator = result.getNodes();
      List<LoginCounterBean> list = new ArrayList<LoginCounterBean>();
      Node node;
      while (nodeIterator.hasNext()) {
        node = nodeIterator.nextNode();
        LoginCounterBean loginCountPerDay = new LoginCounterBean();
        loginCountPerDay.setLoginDate(node.getProperty("exo:LoginHisSvc_loginCounterItem_loginDate").getLong());
        loginCountPerDay.setLoginCount(node.getProperty("exo:LoginHisSvc_loginCounterItem_loginCount").getLong());
        list.add(loginCountPerDay);
      }
      return list;
    } catch (Exception e) {
      LOG.debug("Error while getting login counts of user '" + userId + "': " + e.getMessage(), e);
      throw e;
    } finally {
      sProvider.close();
    }
  }

  /**
   * Get user login count
   *
   * @return: The total number of logins since {fromDate} to {toDate} of user
   *          {userId}
   */
  private int getLoginCount(String userId, long fromDate, long toDate) throws Exception {
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
    SessionProvider sProvider = SessionProvider.createSystemProvider();

    if (!getSession(sProvider).getRootNode().hasNode(HOME)) {
      createHomeNode();
    }

    try {
      Session session = this.getSession(sProvider);
      Node homeNode = session.getRootNode().getNode(HOME);
      return !homeNode.hasNode(userId) ? 0 : homeNode.getNode(userId).getProperty("exo:LoginHisSvc_lastLogin").getLong();
    } catch (Exception e) {
      LOG.debug("Error while retrieving " + userId + "'s last login: " + e.getMessage(), e);
      throw e;
    } finally {
      sProvider.close();
    }
  }

  @Override
  public List<LastLoginBean> getLastLogins(int numLogins, String userIdFilter) throws Exception {
    SessionProvider sProvider = SessionProvider.createSystemProvider();

    if (!getSession(sProvider).getRootNode().hasNode(HOME)) {
      createHomeNode();
    }

    try {
      Session session = this.getSession(sProvider);
      QueryManager queryManager = session.getWorkspace().getQueryManager();

      String sqlStatement = "SELECT * FROM exo:LoginHisSvc_userProfile " + "WHERE (UPPER(exo:LoginHisSvc_userId) LIKE '%"
          + userIdFilter.toUpperCase() + "%') OR (UPPER(exo:LoginHisSvc_userName) LIKE '%" + userIdFilter.toUpperCase() + "%') "
          + "ORDER BY exo:LoginHisSvc_lastLogin DESC";

      QueryImpl query = (QueryImpl) queryManager.createQuery(sqlStatement, Query.SQL);
      query.setLimit(numLogins);

      QueryResult result = query.execute();

      NodeIterator nodeIterator = result.getNodes();
      List<LastLoginBean> lastLogins = new ArrayList<LastLoginBean>();
      Node node;
      String userId, userName;
      while (nodeIterator.hasNext()) {
        node = nodeIterator.nextNode();
        LastLoginBean lastLoginBean = new LastLoginBean();
        userId = node.getName();
        userName = node.getProperty("exo:LoginHisSvc_userName").getString();
        lastLoginBean.setUserId(userId);
        lastLoginBean.setUserName(userName.isEmpty() ? userId : userName);
        lastLoginBean.setLastLogin(node.getProperty("exo:LoginHisSvc_lastLogin").getLong());
        lastLoginBean.setBeforeLastLogin(node.getProperty("exo:LoginHisSvc_beforeLastLogin").getLong());

        lastLogins.add(lastLoginBean);
      }
      return lastLogins;
    } catch (Exception e) {
      LOG.debug("Error while retrieving last logins: " + e.getMessage(), e);
      throw e;
    } finally {
      sProvider.close();
    }
  }

  @Override
  public void addLoginHistoryEntry(String userId, long loginTime) throws Exception {
    SessionProvider sProvider = SessionProvider.createSystemProvider();

    if (!getSession(sProvider).getRootNode().hasNode(HOME)) {
      createHomeNode();
    }

    try {
      Session session = this.getSession(sProvider);
      Node homeNode = session.getRootNode().getNode(HOME);

      Node userNode, loginHistoryNode, loginCounterNode, globalLoginCounterNode;
      String userName = getUserFullName(userId);
      if (!homeNode.hasNode(userId)) {
        userNode = homeNode.addNode(userId, "exo:LoginHisSvc_userProfile");
        userNode.setProperty("exo:LoginHisSvc_userId", userId);
        userNode.setProperty("exo:LoginHisSvc_userName", userName);
        userNode.setProperty("exo:LoginHisSvc_lastLogin", 0);
        userNode.setProperty("exo:LoginHisSvc_beforeLastLogin", 0);
        homeNode.save();

        loginHistoryNode = userNode.addNode(LOGIN_HISTORY, "exo:LoginHisSvc_loginHistory");
        loginHistoryNode.setProperty("exo:LoginHisSvc_loginHistory_lastIndex", 0);

        loginCounterNode = userNode.addNode(LOGIN_COUNTER, "exo:LoginHisSvc_loginCounter");
        loginCounterNode.setProperty("exo:LoginHisSvc_loginCounter_lastIndex", 0);

        userNode.save();
      } else {
        userNode = homeNode.getNode(userId);
      }
      userNode.setProperty("exo:LoginHisSvc_userName", userName);
      userNode.setProperty("exo:LoginHisSvc_beforeLastLogin", userNode.getProperty("exo:LoginHisSvc_lastLogin").getLong());
      userNode.setProperty("exo:LoginHisSvc_lastLogin", loginTime);

      // Update login history
      loginHistoryNode = userNode.getNode("loginHistory");
      long loginHistory_lastIndex = loginHistoryNode.getProperty("exo:LoginHisSvc_loginHistory_lastIndex").getLong();
      loginHistory_lastIndex++;
      Node loginHistory_loginTimeNode = loginHistoryNode.addNode(Long.toString(loginHistory_lastIndex),
                                                                 "exo:LoginHisSvc_loginHistoryItem");
      loginHistory_loginTimeNode.setProperty("exo:LoginHisSvc_loginHistoryItem_id", loginHistory_lastIndex);
      loginHistory_loginTimeNode.setProperty("exo:LoginHisSvc_loginHistoryItem_userId", userId);
      loginHistory_loginTimeNode.setProperty("exo:LoginHisSvc_loginHistoryItem_loginTime", loginTime);
      loginHistoryNode.setProperty("exo:LoginHisSvc_loginHistory_lastIndex", loginHistory_lastIndex);

      Calendar cal = Calendar.getInstance();
      cal.setTimeInMillis(loginTime);
      cal.set(Calendar.HOUR_OF_DAY, 0);
      cal.set(Calendar.MINUTE, 0);
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MILLISECOND, 0);

      String loginDate = Long.toString(cal.getTimeInMillis());

      // Update login counter
      loginCounterNode = userNode.getNode("loginCounter");
      long loginCounter_lastIndex = loginCounterNode.getProperty("exo:LoginHisSvc_loginCounter_lastIndex").getLong();
      Node loginCounter_loginDateNode;
      if (!loginCounterNode.hasNode(loginDate)) {
        loginCounter_loginDateNode = loginCounterNode.addNode(loginDate, "exo:LoginHisSvc_loginCounterItem");
        loginCounter_lastIndex++;
        loginCounter_loginDateNode.setProperty("exo:LoginHisSvc_loginCounterItem_id", loginCounter_lastIndex);
        loginCounter_loginDateNode.setProperty("exo:LoginHisSvc_loginCounterItem_userId", userId);
        loginCounter_loginDateNode.setProperty("exo:LoginHisSvc_loginCounterItem_loginDate", Long.parseLong(loginDate));
        loginCounter_loginDateNode.setProperty("exo:LoginHisSvc_loginCounterItem_loginCount", 1);
      } else {
        loginCounter_loginDateNode = loginCounterNode.getNode(loginDate);
        long loginCount = loginCounter_loginDateNode.getProperty("exo:LoginHisSvc_loginCounterItem_loginCount").getLong();
        loginCounter_loginDateNode.setProperty("exo:LoginHisSvc_loginCounterItem_loginCount", loginCount + 1);
      }
      loginCounterNode.setProperty("exo:LoginHisSvc_loginCounter_lastIndex", loginCounter_lastIndex);

      // Update global login counter
      globalLoginCounterNode = session.getRootNode().getNode(HOME).getNode(ALL_USERS);
      long globalLoginCounter_lastIndex = globalLoginCounterNode.getProperty("exo:LoginHisSvc_globalLoginCounter_lastIndex")
                                                                .getLong();
      Node globalLoginCounter_loginDateNode;
      if (!globalLoginCounterNode.hasNode(loginDate)) {
        globalLoginCounter_loginDateNode = globalLoginCounterNode.addNode(loginDate, "exo:LoginHisSvc_loginCounterItem");
        globalLoginCounter_lastIndex++;
        globalLoginCounter_loginDateNode.setProperty("exo:LoginHisSvc_loginCounterItem_id", globalLoginCounter_lastIndex);
        globalLoginCounter_loginDateNode.setProperty("exo:LoginHisSvc_loginCounterItem_userId", ALL_USERS);
        globalLoginCounter_loginDateNode.setProperty("exo:LoginHisSvc_loginCounterItem_loginDate", Long.parseLong(loginDate));
        globalLoginCounter_loginDateNode.setProperty("exo:LoginHisSvc_loginCounterItem_loginCount", 1);
      } else {
        globalLoginCounter_loginDateNode = globalLoginCounterNode.getNode(loginDate);
        long loginCount = globalLoginCounter_loginDateNode.getProperty("exo:LoginHisSvc_loginCounterItem_loginCount").getLong();
        globalLoginCounter_loginDateNode.setProperty("exo:LoginHisSvc_loginCounterItem_loginCount", loginCount + 1);
      }
      globalLoginCounterNode.setProperty("exo:LoginHisSvc_globalLoginCounter_lastIndex", globalLoginCounter_lastIndex);

      userNode.save();
      globalLoginCounterNode.save();
    } catch (Exception e) {
      LOG.debug("Error while adding login history entry for user '" + userId + "': " + e.getMessage(), e);
      throw e;
    } finally {
      sProvider.close();
    }
  }

  @Override
  public List<LoginHistoryBean> getLoginHistory(String userId, long fromTime, long toTime) throws Exception {
    SessionProvider sProvider = SessionProvider.createSystemProvider();

    if (!getSession(sProvider).getRootNode().hasNode(HOME)) {
      createHomeNode();
    }

    try {
      Session session = this.getSession(sProvider);

      QueryManager queryManager = session.getWorkspace().getQueryManager();
      String sqlStatement = "SELECT * FROM exo:LoginHisSvc_loginHistoryItem " + "WHERE "
          + (userId.equals(LoginHistoryService.ALL_USERS) ? ""
                                                          : "exo:LoginHisSvc_loginHistoryItem_userId = '" + userId + "' AND ")
          + "(exo:LoginHisSvc_loginHistoryItem_loginTime BETWEEN " + Long.toString(fromTime) + " AND " + Long.toString(toTime)
          + ") " + "ORDER BY exo:LoginHisSvc_loginHistoryItem_loginTime DESC";
      QueryImpl query = (QueryImpl) queryManager.createQuery(sqlStatement, Query.SQL);
      QueryResult result = query.execute();
      NodeIterator nodeIterator = result.getNodes();
      List<LoginHistoryBean> list = new ArrayList<LoginHistoryBean>();
      Node node;
      String uId, uName;
      while (nodeIterator.hasNext()) {
        node = nodeIterator.nextNode();
        LoginHistoryBean loginHistory = new LoginHistoryBean();
        uId = node.getProperty("exo:LoginHisSvc_loginHistoryItem_userId").getString();
        uName = getUserFullName(uId);
        loginHistory.setUserId(uId);
        loginHistory.setUserName(uName.isEmpty() ? uId : uName);
        loginHistory.setLoginTime(node.getProperty("exo:LoginHisSvc_loginHistoryItem_loginTime").getLong());
        list.add(loginHistory);
      }
      return list;
    } catch (Exception e) {
      LOG.debug("Error while getting login history of user '" + userId + "': " + e.getMessage(), e);
      throw e;
    } finally {
      sProvider.close();
    }
  }

  @Override
  public Set<String> getLastUsersLogin(long fromTime) throws Exception {
    Set<String> users = new LinkedHashSet<String>();
    SessionProvider sProvider = SessionProvider.createSystemProvider();
    try {
      Session session = this.getSession(sProvider);
      QueryManager queryManager = session.getWorkspace().getQueryManager();
      StringBuilder sb = new StringBuilder();
      sb.append("SELECT * FROM exo:LoginHisSvc_userProfile WHERE")
        .append(" exo:LoginHisSvc_lastLogin >= " + Long.toString(fromTime))
        .append(" ORDER BY exo:LoginHisSvc_lastLogin DESC");
      QueryImpl query = (QueryImpl) queryManager.createQuery(sb.toString(), Query.SQL);
      QueryResult result = query.execute();
      NodeIterator nodeIterator = result.getNodes();
      while (nodeIterator.hasNext()) {
        Node node = nodeIterator.nextNode();
        String userId = node.getProperty("exo:LoginHisSvc_userId").getString();
        users.add(userId);
      }
      return users;
    } catch (Exception e) {
      LOG.debug("Error while getting login history of users " + e.getMessage(), e);
    } finally {
      sProvider.close();
    }
    return null;
  }

  @Override
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

  @Override
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
      if (toDate > toMonth)
        toDate = toMonth;

      LoginCounterBean loginCountPerWeek = new LoginCounterBean();
      loginCountPerWeek.setLoginDate(fromDate);
      loginCountPerWeek.setLoginCount(fromDate > now ? -1 : getLoginCount(userId, fromDate, toDate - DAY_IN_MILLISEC));

      list.add(loginCountPerWeek);
    } while (toDate < toMonth);

    return list;
  }

  @Override
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
      if (toDate > nextYear)
        toDate = nextYear;

      LoginCounterBean loginCountPerWeek = new LoginCounterBean();
      loginCountPerWeek.setLoginDate(fromDate);
      loginCountPerWeek.setLoginCount(fromDate > now ? -1 : getLoginCount(userId, fromDate, toDate - DAY_IN_MILLISEC));

      list.add(loginCountPerWeek);
    } while (toDate < nextYear);

    return list;
  }

  @Override
  public long getBeforeLastLogin(String userId) throws Exception {
    SessionProvider sProvider = SessionProvider.createSystemProvider();
    try {
      Session session = this.getSession(sProvider);
      Node homeNode = session.getRootNode().getNode(HOME);
      return !homeNode.hasNode(userId) ? 0 : homeNode.getNode(userId).getProperty("exo:LoginHisSvc_beforeLastLogin").getLong();
    } catch (Exception e) {
      LOG.debug("Error while retrieving " + userId + "'s last login: " + e.getMessage(), e);
      throw e;
    } finally {
      sProvider.close();
    }
  }

  /**
   * returns the node iterator which contains a given number of Login History
   * nodes after a given offset
   *
   * @param sProvider {@link SessionProvider}
   * @param offset long
   * @param size long
   * @return Node Iterator
   */
  public NodeIterator getLoginHistoryNodes(SessionProvider sProvider, long offset, long size) {

    NodeIterator nodeIterator = null;

    try {
      Session session = this.getSession(sProvider);

      QueryManager queryManager = session.getWorkspace().getQueryManager();
      String sqlStatement = "SELECT * FROM exo:LoginHisSvc_loginHistoryItem WHERE jcr:path LIKE '/exo:LoginHistoryHome/%' "
          + "ORDER BY exo:LoginHisSvc_loginHistoryItem_loginTime ASC";
      QueryImpl query = (QueryImpl) queryManager.createQuery(sqlStatement, Query.SQL);
      query.setLimit(size);
      query.setOffset(offset);
      QueryResult result = query.execute();
      nodeIterator = result.getNodes();
    } catch (Exception e) {
      LOG.error("Error while getting the Login History Nodes NodeIterator: " + e.getMessage(), e);
    }
    return nodeIterator;
  }

  /**
   * returns the node iterator which contains a given number of All Users Login
   * Counter nodes after a given offset
   *
   * @param sProvider {@link SessionProvider}
   * @param offset long
   * @param size long
   * @return Node Iterator
   */
  public NodeIterator getAllUsersLoginCountersNodes(SessionProvider sProvider, long offset, long size) {

    NodeIterator nodeIterator = null;

    try {
      Session session = this.getSession(sProvider);

      QueryManager queryManager = session.getWorkspace().getQueryManager();
      String sqlStatement = "SELECT * FROM exo:LoginHisSvc_loginCounterItem"
          + " WHERE jcr:path LIKE '/exo:LoginHistoryHome/AllUsers/%' "
          + "ORDER BY exo:LoginHisSvc_loginCounterItem_loginDate ASC";
      QueryImpl query = (QueryImpl) queryManager.createQuery(sqlStatement, Query.SQL);
      query.setLimit(size);
      query.setOffset(offset);
      QueryResult result = query.execute();
      nodeIterator = result.getNodes();
    } catch (Exception e) {
      LOG.error("Error while getting the Login Counters Nodes NodeIterator: " + e.getMessage(), e);
    }
    return nodeIterator;
  }

  /**
   * returns the node iterator which contains a given number of Login Counter
   * nodes after a given offset
   *
   * @param sProvider {@link SessionProvider}
   * @param offset long
   * @param size long
   * @return Node Iterator
   */
  public NodeIterator getLoginCountersNodes(SessionProvider sProvider, long offset, long size) {

    NodeIterator nodeIterator = null;

    try {
      Session session = this.getSession(sProvider);

      QueryManager queryManager = session.getWorkspace().getQueryManager();
      String sqlStatement = "SELECT * FROM exo:LoginHisSvc_loginCounterItem WHERE jcr:path LIKE '/exo:LoginHistoryHome/%' "
          + "ORDER BY exo:LoginHisSvc_loginCounterItem_loginDate ASC";
      QueryImpl query = (QueryImpl) queryManager.createQuery(sqlStatement, Query.SQL);
      query.setLimit(size);
      query.setOffset(offset);
      QueryResult result = query.execute();
      nodeIterator = result.getNodes();
    } catch (Exception e) {
      LOG.error("Error while getting the Login Counters Nodes NodeIterator: " + e.getMessage(), e);
    }
    return nodeIterator;
  }

  /**
   * returns the node iterator which contains a given number of Login History
   * Users Profiles nodes after a given offset
   *
   * @param sProvider {@link SessionProvider}
   * @param offset long
   * @param size long
   * @return Node Iterator
   */
  public NodeIterator getLoginHistoryUsersProfilesNodes(SessionProvider sProvider, long offset, long size) {

    NodeIterator nodeIterator = null;

    try {
      Session session = this.getSession(sProvider);

      QueryManager queryManager = session.getWorkspace().getQueryManager();
      String sqlStatement = "SELECT * FROM exo:LoginHisSvc_userProfile WHERE jcr:path LIKE '/exo:LoginHistoryHome/%'";
      QueryImpl query = (QueryImpl) queryManager.createQuery(sqlStatement, Query.SQL);
      query.setLimit(size);
      query.setOffset(offset);
      QueryResult result = query.execute();
      nodeIterator = result.getNodes();
    } catch (Exception e) {
      LOG.error("Error while getting the Login History Users Profiles Nodes NodeIterator: " + e.getMessage(), e);
    }
    return nodeIterator;
  }

  /**
   * removes the given Login History node
   *
   * @param sProvider {@link SessionProvider}
   * @param loginHistoryNode {@link Node}
   */
  public void removeLoginHistoryNode(SessionProvider sProvider, Node loginHistoryNode) {
    try {
      Session session = this.getSession(sProvider);
      loginHistoryNode.remove();
      session.save();
    } catch (Exception e) {
      LOG.error("Error while deleting Login History Node {} : ", loginHistoryNode, e.getMessage(), e);
    }
  }

  /**
   * removes the given Login Counter node
   *
   * @param sProvider {@link SessionProvider}
   * @param loginCounterNode {@link Node}
   */
  public void removeAllUsersLoginCounterNode(SessionProvider sProvider, Node loginCounterNode) {
    try {
      Session session = this.getSession(sProvider);
      loginCounterNode.remove();
      session.save();
    } catch (Exception e) {
      LOG.error("Error while deleting Login Counter Node {} : ", loginCounterNode, e.getMessage(), e);
    }
  }

  /**
   * removes All Users Profile Node
   *
   * @param sProvider {@link SessionProvider}
   */
  public void removeAllUsersProfileNode(SessionProvider sProvider) {
    try {
      Session session = this.getSession(sProvider);
      Node loginHistoryAllUsersProfileNode = session.getRootNode().getNode(HOME + "/AllUsers");
      loginHistoryAllUsersProfileNode.remove();
      session.save();
    } catch (Exception e) {
      LOG.error("Error while removing All Users Profile Node : ", e.getMessage(), e);
    }
  }

  /**
   * removes the given Login Counter node
   *
   * @param sProvider {@link SessionProvider}
   * @param loginCounterNode {@link Node}
   */
  public void removeLoginCounterNode(SessionProvider sProvider, Node loginCounterNode) {
    try {
      Session session = this.getSession(sProvider);
      loginCounterNode.remove();
      session.save();
    } catch (Exception e) {
      LOG.error("Error while deleting Login Counter Node {} : ", loginCounterNode, e.getMessage(), e);
    }
  }

  /**
   * removes the given Login History User Profile node
   *
   * @param sProvider {@link SessionProvider}
   * @param loginHistoryUserProfileNode {@link Node}
   */
  public void removeLoginHistoryUserProfileNode(SessionProvider sProvider, Node loginHistoryUserProfileNode) {
    try {
      Session session = this.getSession(sProvider);
      loginHistoryUserProfileNode.remove();
      session.save();
    } catch (Exception e) {
      LOG.error("Error while deleting Login History User Profile Node {} : ", loginHistoryUserProfileNode, e.getMessage(), e);
    }
  }

  /**
   * used at the end of the migration process, it removes the JCR root node for
   * Login History service
   * 
   * @throws Exception
   */
  public void removeLoginHistoryHomeNode() throws Exception {
    SessionProvider sProvider = SessionProvider.createSystemProvider();
    if (!getSession(sProvider).getRootNode().hasNode(HOME)) {
      return;
    }

    Session session = this.getSession(sProvider);
    Node homeNode = session.getRootNode().getNode(HOME);
    homeNode.remove();
    session.save();
    sProvider.close();
  }

}
