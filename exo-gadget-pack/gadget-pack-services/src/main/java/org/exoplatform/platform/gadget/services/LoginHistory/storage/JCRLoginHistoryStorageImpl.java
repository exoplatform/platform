package org.exoplatform.platform.gadget.services.LoginHistory.storage;

import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
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
    private static final Log LOG = ExoLogger.getLogger(LoginHistoryServiceImpl.class);
    private static String HOME = "exo:LoginHistoryHome";
    private static String LOGIN_HISTORY = "loginHistory";
    private static String LOGIN_COUNTER = "loginCounter";
    private static String BEFORE_LAST_LOGIN = "exo:LoginHisSvc_beforeLastLogin";
    private static String LAST_LOGIN_TIME = "exo:LoginHisSvc_lastLogin";
    private static int MAX_NUM_OF_LOGIN_HISTORY_ENTRIES = 0;
    private static int DAYS_FOR_KEEPING_USER_STATISTIC = 0;
    private static int DAYS_FOR_KEEPING_GLOBAL_STATISTIC = 0;
    private static long DAY_IN_MILLISEC = 86400000;
    private RepositoryService repositoryService;


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
                permissions.put("*:/platform/users", new String[]{PermissionType.READ});
                ((ExtendedNode)homeNode).setPermissions(permissions);

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
     * Apply configurations from service's xml
     */
    public void addConfiguration(LoginHistoryPlugin plugin) {
        InitParams initParams = plugin.getInitParams();
        if (initParams != null) {
            ValueParam MaxNumOfUserLoginHistoryEntries = initParams.getValueParam("MaxNumOfUserLoginHistoryEntries");
            if (MaxNumOfUserLoginHistoryEntries != null)
                MAX_NUM_OF_LOGIN_HISTORY_ENTRIES = Integer.parseInt(MaxNumOfUserLoginHistoryEntries.getValue());

            ValueParam DaysForKeepingUserStatistic = initParams.getValueParam("DaysForKeepingUserStatistic");
            if (DaysForKeepingUserStatistic != null)
                DAYS_FOR_KEEPING_USER_STATISTIC = Integer.parseInt(DaysForKeepingUserStatistic.getValue());

            ValueParam DaysForKeepingGlobalStatistic = initParams.getValueParam("DaysForKeepingGlobalStatistic");
            if (DaysForKeepingGlobalStatistic != null)
                DAYS_FOR_KEEPING_GLOBAL_STATISTIC = Integer.parseInt(DaysForKeepingGlobalStatistic.getValue());
        }
    }


    public JCRLoginHistoryStorageImpl(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
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
            OrganizationService service = (OrganizationService) ExoContainerContext.getCurrentContainer()
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
            String sqlStatement = "SELECT * FROM exo:LoginHisSvc_loginCounterItem " +
                    "WHERE exo:LoginHisSvc_loginCounterItem_userId = '" + userId + "' " +
                    "AND (exo:LoginHisSvc_loginCounterItem_loginDate BETWEEN " + Long.toString(fromDate) + " AND " + Long.toString(toDate) + ") " +
                    "ORDER BY exo:LoginHisSvc_loginCounterItem_loginDate ASC";
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
     * @return: The total number of logins since {fromDate} to {toDate} of user {userId}
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
        try {
            Session session = this.getSession(sProvider);
            QueryManager queryManager = session.getWorkspace().getQueryManager();

            String sqlStatement = "SELECT * FROM exo:LoginHisSvc_userProfile " +
                    "WHERE (UPPER(exo:LoginHisSvc_userId) LIKE '%" + userIdFilter.toUpperCase() +
                    "%') OR (UPPER(exo:LoginHisSvc_userName) LIKE '%" + userIdFilter.toUpperCase() + "%') " +
                    "ORDER BY exo:LoginHisSvc_lastLogin DESC";

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
            Node loginHistory_loginTimeNode = loginHistoryNode.addNode(Long.toString(loginHistory_lastIndex), "exo:LoginHisSvc_loginHistoryItem");
            loginHistory_loginTimeNode.setProperty("exo:LoginHisSvc_loginHistoryItem_id", loginHistory_lastIndex);
            loginHistory_loginTimeNode.setProperty("exo:LoginHisSvc_loginHistoryItem_userId", userId);
            loginHistory_loginTimeNode.setProperty("exo:LoginHisSvc_loginHistoryItem_loginTime", loginTime);
            // Keep only up to MAX_NUM_OF_LOGIN_HISTORY_ENTRIES last items
            if (MAX_NUM_OF_LOGIN_HISTORY_ENTRIES > 0 && loginHistory_lastIndex > MAX_NUM_OF_LOGIN_HISTORY_ENTRIES) {
                QueryManager queryManager = session.getWorkspace().getQueryManager();
                String sqlStatement = "SELECT * FROM exo:LoginHisSvc_loginHistoryItem " +
                        "WHERE exo:LoginHisSvc_loginHistoryItem_userId = '" + userId + "' " +
                        "AND exo:LoginHisSvc_loginHistoryItem_id <= " + Long.toString(loginHistory_lastIndex - MAX_NUM_OF_LOGIN_HISTORY_ENTRIES);
                QueryImpl query = (QueryImpl) queryManager.createQuery(sqlStatement, Query.SQL);
                QueryResult result = query.execute();
                NodeIterator nodeIterator = result.getNodes();
                while (nodeIterator.hasNext()) {
                    Node node = nodeIterator.nextNode();
                    node.remove();
                }
            }
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
            // Keep only up to DAYS_FOR_KEEPING_USER_STATISTIC last items
            if (DAYS_FOR_KEEPING_USER_STATISTIC > 0 && loginCounter_lastIndex > DAYS_FOR_KEEPING_USER_STATISTIC) {
                QueryManager queryManager = session.getWorkspace().getQueryManager();
                String sqlStatement = "SELECT * FROM exo:LoginHisSvc_loginCounterItem " +
                        "WHERE exo:LoginHisSvc_loginCounterItem_userId = '" + userId + "' " +
                        "AND exo:LoginHisSvc_loginCounterItem_id <= " + Long.toString(loginCounter_lastIndex - DAYS_FOR_KEEPING_USER_STATISTIC);
                QueryImpl query = (QueryImpl) queryManager.createQuery(sqlStatement, Query.SQL);
                QueryResult result = query.execute();
                NodeIterator nodeIterator = result.getNodes();
                Node node;
                while (nodeIterator.hasNext()) {
                    node = nodeIterator.nextNode();
                    node.remove();
                }
            }
            loginCounterNode.setProperty("exo:LoginHisSvc_loginCounter_lastIndex", loginCounter_lastIndex);

            // Update global login counter
            globalLoginCounterNode = session.getRootNode().getNode(HOME).getNode(ALL_USERS);
            long globalLoginCounter_lastIndex = globalLoginCounterNode.getProperty("exo:LoginHisSvc_globalLoginCounter_lastIndex").getLong();
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
            // Keep only up to DAYS_FOR_KEEPING_GLOBAL_STATISTIC last items
            if (DAYS_FOR_KEEPING_GLOBAL_STATISTIC > 0 && globalLoginCounter_lastIndex > DAYS_FOR_KEEPING_GLOBAL_STATISTIC) {
                QueryManager queryManager = session.getWorkspace().getQueryManager();
                String sqlStatement = "SELECT * FROM exo:LoginHisSvc_loginCounterItem " +
                        "WHERE exo:LoginHisSvc_loginCounterItem_userId = '" + ALL_USERS + "' " +
                        "AND exo:LoginHisSvc_loginCounterItem_id <= " + Long.toString(globalLoginCounter_lastIndex - DAYS_FOR_KEEPING_GLOBAL_STATISTIC);
                QueryImpl query = (QueryImpl) queryManager.createQuery(sqlStatement, Query.SQL);
                QueryResult result = query.execute();
                NodeIterator nodeIterator = result.getNodes();
                Node node;
                while (nodeIterator.hasNext()) {
                    node = nodeIterator.nextNode();
                    node.remove();
                }
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
        try {
            Session session = this.getSession(sProvider);

            QueryManager queryManager = session.getWorkspace().getQueryManager();
            String sqlStatement = "SELECT * FROM exo:LoginHisSvc_loginHistoryItem " +
                    "WHERE " +
                    (userId.equals(LoginHistoryService.ALL_USERS) ? "" : "exo:LoginHisSvc_loginHistoryItem_userId = '" + userId + "' AND ") +
                    "(exo:LoginHisSvc_loginHistoryItem_loginTime BETWEEN " + Long.toString(fromTime) + " AND " + Long.toString(toTime) + ") " +
                    "ORDER BY exo:LoginHisSvc_loginHistoryItem_loginTime DESC";
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
    public boolean isActiveUser(String userId, int days) {
        SessionProvider sProvider = SessionProvider.createSystemProvider();
        try {
            Session session = this.getSession(sProvider);
            Node homeNode = session.getRootNode().getNode(HOME);
            if (! homeNode.hasNode(userId)) {
                return false;
            }
            Node userNode = homeNode.getNode(userId);
            long beforeLastLogin = userNode.getProperty(BEFORE_LAST_LOGIN).getLong();
            // return true if it's the first login of user
            if (beforeLastLogin == 0) return true;
            //
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - days);
            long limitTime = cal.getTimeInMillis();
            return beforeLastLogin >= limitTime;
        } catch (Exception e) {
            LOG.error("Error while get the last login of current user", e);
        }
        return false;
    }

    @Override
    public Map<String, Integer> getActiveUsers(long fromTime) {
        Map<String, Integer> users = new LinkedHashMap<String, Integer>();
        SessionProvider sProvider = SessionProvider.createSystemProvider();
        try {
            Session session = this.getSession(sProvider);
            QueryManager queryManager = session.getWorkspace().getQueryManager();
            StringBuilder sb = new StringBuilder();
            sb.append("SELECT * FROM exo:LoginHisSvc_userProfile WHERE")
                    .append(" exo:LoginHisSvc_lastLogin >= " + Long.toString(fromTime))
                    .append(" AND exo:LoginHisSvc_beforeLastLogin > 0")
                    .append(" ORDER BY exo:LoginHisSvc_lastLogin DESC");
            QueryImpl query = (QueryImpl) queryManager.createQuery(sb.toString(), Query.SQL);
            QueryResult result = query.execute();
            NodeIterator nodeIterator = result.getNodes();
            while (nodeIterator.hasNext()) {
                Node node = nodeIterator.nextNode();
                String userId = node.getProperty("exo:LoginHisSvc_userId").getString();
                long numberOfLogin = node.getNode(LOGIN_HISTORY).getProperty("exo:LoginHisSvc_loginHistory_lastIndex").getLong();
                users.put(userId, (int) numberOfLogin);
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
            if (toDate > toMonth) toDate = toMonth;

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
            if (toDate > nextYear) toDate = nextYear;

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

}
