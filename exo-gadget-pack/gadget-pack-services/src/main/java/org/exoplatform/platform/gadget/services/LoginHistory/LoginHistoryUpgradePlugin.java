package org.exoplatform.platform.gadget.services.LoginHistory;

import org.exoplatform.commons.persistence.impl.EntityManagerService;
import org.exoplatform.commons.upgrade.UpgradeProductPlugin;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.platform.gadget.services.LoginHistory.storage.JCRLoginHistoryStorageImpl;
import org.exoplatform.platform.gadget.services.LoginHistory.storage.LoginHistoryStorage;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import java.time.Instant;

public class LoginHistoryUpgradePlugin extends UpgradeProductPlugin {
  private static final Log           LOG                          = ExoLogger.getLogger(LoginHistoryUpgradePlugin.class);

  private static final int           LOGIN_HISTORY_NODE_PAGE_SIZE = 50;

  private JCRLoginHistoryStorageImpl jcrLoginHistoryStorage;

  private LoginHistoryStorage        jpaLoginHistoryStorage;

  private RepositoryService          repositoryService;

  private EntityManagerService       entityManagerService;

  public LoginHistoryUpgradePlugin(InitParams initParams,
                                   JCRLoginHistoryStorageImpl jcrLoginHistoryStorage,
                                   LoginHistoryStorage jpaLoginHistoryStorage,
                                   RepositoryService repositoryService,
                                   EntityManagerService entityManagerService) {
    super(initParams);
    this.jcrLoginHistoryStorage = jcrLoginHistoryStorage;
    this.jpaLoginHistoryStorage = jpaLoginHistoryStorage;
    this.repositoryService = repositoryService;
    this.entityManagerService = entityManagerService;
  }

  @Override
  public void processUpgrade(String newVersion, String previousVersion) {
    // First check to see if the JCR still contains Login History data. If not,
    // migration is skipped

    int migrationErrors, allUsersCountersDeletionErrors, countersDeletionErrors, usersProfilesDeletionErrors,
        deleteAllLoginHistoryNodesErrors;

    if (!hasDataToMigrate()) {
      LOG.info("== No Login History data to migrate from JCR to RDBMS");
    } else {
      LOG.info("== Start migration of Login History data from JCR to RDBMS");

      migrationErrors = migrateAndDeleteLoginHistory();

      if (migrationErrors == 0) {
        allUsersCountersDeletionErrors = deleteAllUsersLoginHistoryCounters();
        if (allUsersCountersDeletionErrors == 0) {
          LOG.info("==    Login History migration - Login History All Users Counters JCR Data deleted successfully");
        } else {
          LOG.warn("==    Login History migration - {} Errors during Login History All Users Counters JCR Data deletion",
                   allUsersCountersDeletionErrors);
        }

        countersDeletionErrors = deleteLoginHistoryCounters();
        if (countersDeletionErrors == 0) {
          LOG.info("==    Login History migration - Login History Counters JCR Data deleted successfully");
        } else {
          LOG.warn("==    Login History migration - {} Errors during Login History Counters JCR Data deletion",
                   countersDeletionErrors);
        }

        deleteAllLoginHistoryNodesErrors = deleteAllLoginHistoryNodes();
        if (deleteAllLoginHistoryNodesErrors == 0) {
          LOG.info("==    Login History migration - All Login History Entries JCR Data deleted successfully");
        } else {
          LOG.warn("==    Login History migration - {} Errors during All Login History Entries JCR Data deletion",
                   deleteAllLoginHistoryNodesErrors);
        }

        usersProfilesDeletionErrors = deleteLoginHistoryProfiles();
        if (usersProfilesDeletionErrors == 0) {
          LOG.info("==    Login History migration - Login History Users Profiles deleted successfully");
        } else {
          LOG.warn("==    Login History migration - {} Errors during Login History Users Profiles JCR Data deletion",
                   usersProfilesDeletionErrors);
        }

        if (countersDeletionErrors > 0 || usersProfilesDeletionErrors > 0 || allUsersCountersDeletionErrors > 0
            || deleteAllLoginHistoryNodesErrors > 0) {
          throw new RuntimeException("== Login History migration aborted due to errors during the deletion of Login History Counters and Users Profiles");
        }

        try {
          jcrLoginHistoryStorage.removeLoginHistoryHomeNode();
        } catch (Exception e) {
          throw new RuntimeException("== Login History migration - Error when deleting Login History home node");
        }
        LOG.info("==    Login History migration - Home Node deleted successfully !");
        LOG.info("== Login History migration done");
      } else {
        LOG.error("==    Login History migration aborted, {} errors encountered", migrationErrors);
        throw new RuntimeException("== Login History migration aborted because of migration failures");
      }
    }
  }

  private Session getSession(SessionProvider sessionProvider) throws Exception {
    ManageableRepository currentRepo = this.repositoryService.getCurrentRepository();
    return sessionProvider.getSession(currentRepo.getConfiguration().getDefaultWorkspaceName(), currentRepo);
  }

  private Boolean hasDataToMigrate() {
    boolean hasDataToMigrate;
    SessionProvider sProvider = SessionProvider.createSystemProvider();
    try {
      Session session = getSession(sProvider);
      hasDataToMigrate = session.getRootNode().hasNode("exo:LoginHistoryHome");
    } catch (Exception e) {
      LOG.error("Error while checking the existence of login history home node", e);
      hasDataToMigrate = false;
    } finally {
      sProvider.close();
    }
    return hasDataToMigrate;
  }

  /**
   * iterates on all present Login History nodes by a given page size, for each
   * returned page of Login History nodes it iterates on each node in order to add
   * it to the JPAStorage and then removes it and by the end returns the number of
   * errors occurred during the process
   */
  private int migrateAndDeleteLoginHistory() {
    SessionProvider sProvider = SessionProvider.createSystemProvider();
    entityManagerService.startRequest(ExoContainerContext.getCurrentContainer());

    NodeIterator loginHistoryNodes;
    Node loginHistoryNode;

    String path;
    long countLoginHistoryNodes, countAllUsersLoginHistoryCountersNodes, countLoginHistoryCountersNodes, countUsersProfilesNodes,
        count, offset = 0, migrated = 0;
    int errors = 0;
    try {
      countLoginHistoryNodes = jcrLoginHistoryStorage.countLoginHistoryNodes(sProvider);
      countAllUsersLoginHistoryCountersNodes = jcrLoginHistoryStorage.countGlobalLoginHistoryCountersNodes(sProvider);
      countLoginHistoryCountersNodes = jcrLoginHistoryStorage.countLoginHistoryCountersNodes(sProvider)
          - countAllUsersLoginHistoryCountersNodes;
      countUsersProfilesNodes = jcrLoginHistoryStorage.getAllUsersProfilesNodes(sProvider).getSize();

      LOG.info("==    Login History migration - ({}) Total Login History Nodes to migrate !", countLoginHistoryNodes);
      LOG.info("==    Login History migration - ({}) Total All Users Login History Counters Nodes to delete !",
               countAllUsersLoginHistoryCountersNodes);
      LOG.info("==    Login History migration - ({}) Total Login History Counters Nodes to delete !",
               countLoginHistoryCountersNodes);
      LOG.info("==    Login History migration - ({}) Total Login History Users Profiles to delete !", countUsersProfilesNodes);
      do {
        entityManagerService.endRequest(ExoContainerContext.getCurrentContainer());
        entityManagerService.startRequest(ExoContainerContext.getCurrentContainer());
        loginHistoryNodes = jcrLoginHistoryStorage.getLoginHistoryNodes(sProvider, offset, LOGIN_HISTORY_NODE_PAGE_SIZE);
        count = loginHistoryNodes.getSize();
        String userId = null;
        long loginTime;
        Instant instantLoginTime = null;
        while (loginHistoryNodes.hasNext()) {
          try {
            loginHistoryNode = loginHistoryNodes.nextNode();
            userId = loginHistoryNode.getProperty("exo:LoginHisSvc_loginHistoryItem_userId").getString();
            loginTime = loginHistoryNode.getProperty("exo:LoginHisSvc_loginHistoryItem_loginTime").getLong();
            instantLoginTime = Instant.ofEpochMilli(loginTime);
            path = loginHistoryNode.getPath();
            LOG.debug("==    Login History migration - Migrate Login History Entry of user {} at {}", userId, instantLoginTime);
            jpaLoginHistoryStorage.addLoginHistoryEntry(userId, loginTime);
            LOG.debug("Removing Login History Node : " + path);
            jcrLoginHistoryStorage.removeLoginHistoryNode(sProvider, loginHistoryNode);
            migrated++;
          } catch (Exception e) {
            LOG.error("==    Login History migration - Error while migrating Login History Entry of user {} at {} ",
                      userId,
                      instantLoginTime,
                      e);
            errors++;
          }
        }
        offset += count;
        LOG.info("==    Login History migration - Progress : {} Login History Entries migrated ({} errors)", migrated, errors);
      } while (count == LOGIN_HISTORY_NODE_PAGE_SIZE);
    } finally {
      entityManagerService.endRequest(ExoContainerContext.getCurrentContainer());
      sProvider.close();
    }
    return errors;
  }

  /**
   * iterates on Login History Counters by a given page size each time and removes
   * them one by one and by the end deletes the All Users Profile Node and returns
   * the number of errors occurred during the process
   */
  private int deleteAllUsersLoginHistoryCounters() {
    SessionProvider sProvider = SessionProvider.createSystemProvider();
    Session session = null;

    NodeIterator loginCountersNodes;
    Node loginCounterNode;

    String path = null;
    long count, offset = 0, removed = 0;
    int errors = 0;
    try {
      do {
        try {
          session = this.getSession(sProvider);
        } catch (Exception e) {
          LOG.error("==    Login History migration - Error while getting JCR Session for delete All Users Login History Counters : ",
                    e.getMessage(),
                    e);
        }
        loginCountersNodes =
                           jcrLoginHistoryStorage.getAllUsersLoginCountersNodes(sProvider, offset, LOGIN_HISTORY_NODE_PAGE_SIZE);
        count = loginCountersNodes.getSize();
        while (loginCountersNodes.hasNext()) {
          try {
            loginCounterNode = loginCountersNodes.nextNode();
            path = loginCounterNode.getPath();
            loginCounterNode.remove();
            removed++;
            if (removed % 10 == 0) {
              session.save();
            }
          } catch (Exception e) {
            LOG.error("==    Login History migration - Error while deleting All Users Login Counter Node {} : " + path,
                      e.getMessage(),
                      e);
            errors++;
          }
        }
        try {
          session.save();
        } catch (Exception e) {
          LOG.error("==    Login History migration - Error while saving JCR Session delete All Users Login History Counters : ",
                    e.getMessage(),
                    e);
        }
        offset += count;
        LOG.info("==    Login History migration - Progress : {} All Users Login Counters removed ({} errors)", removed, errors);
      } while (count == LOGIN_HISTORY_NODE_PAGE_SIZE);
      LOG.info("==    Login History migration - Removing All Users Profile Node");
      try {
        jcrLoginHistoryStorage.removeAllUsersProfileNode(sProvider);
      } catch (Exception e) {
        LOG.error("==    Login History migration - Error while removing All Users Profile Node : ", e);
        errors++;
      }
    } finally {
      sProvider.close();
    }
    return errors;
  }

  /**
   * iterates on Login History Users Profiles by a given page size each time and
   * for each user gets all its Login Counters and removes them one by one and
   * returns the number of errors occurred during the process
   */
  private int deleteLoginHistoryCounters() {
    SessionProvider sProvider = SessionProvider.createSystemProvider();
    Session session = null;

    NodeIterator userLoginCountersNodes, usersProfilesNodes;
    Node userProfileNode, userLoginCounterNode;

    String userId = null, day = null;
    long count, countLoginCountersNodes, offset = 0, userLoginCountersNodesRemoved, removed = 0;
    int errors = 0;
    try {
      do {
        try {
          session = this.getSession(sProvider);
        } catch (Exception e) {
          LOG.error("==    Login History migration - Error while getting JCR Session for delete Login History Counters : ",
                    e.getMessage(),
                    e);
        }
        usersProfilesNodes = jcrLoginHistoryStorage.getUsersProfilesNodes(sProvider, offset, LOGIN_HISTORY_NODE_PAGE_SIZE);
        count = usersProfilesNodes.getSize();
        while (usersProfilesNodes.hasNext()) {
          userProfileNode = usersProfilesNodes.nextNode();
          userLoginCountersNodes = jcrLoginHistoryStorage.getAllUserLoginCountersNodes(userProfileNode);
          countLoginCountersNodes = userLoginCountersNodes.getSize();
          userLoginCountersNodesRemoved = 0;
          try {
            userId = userProfileNode.getProperty("exo:LoginHisSvc_userId").getString();
          } catch (Exception e) {
            LOG.error("==    Login History migration - Error while retrieving the UserId for : ", userProfileNode);
          }

          LOG.debug("==    Login History migration - ({}) Login Counters Nodes to delete for user : {}",
                    countLoginCountersNodes,
                    userId);
          while (userLoginCountersNodes.hasNext()) {
            try {
              userLoginCounterNode = userLoginCountersNodes.nextNode();
              day = userLoginCounterNode.getProperty("exo:LoginHisSvc_loginCounterItem_loginDate").getString();
              userLoginCounterNode.remove();
              userLoginCountersNodesRemoved++;
              removed++;
              if (removed % 10 == 0) {
                session.save();
              }
            } catch (Exception e) {
              LOG.error("==    Login History migration - Error while deleting Login Counter Node for {} of user {} : ",
                        day,
                        userId,
                        e.getMessage(),
                        e);
              errors++;
            }
          }
          try {
            session.save();
          } catch (Exception e) {
            LOG.error("==    Login History migration - Error while saving JCR Session for delete Login History Counters : ",
                      e.getMessage(),
                      e);
          }
          if (userLoginCountersNodesRemoved != 0) {
            LOG.debug("==    Login History migration - ({}) Total Login Counters Nodes deleted for user : {}",
                      userLoginCountersNodesRemoved,
                      userId);
          }
        }
        offset += count;
        LOG.info("==    Login History migration - Progress : {} Total Users Login Counters removed ({} errors)", removed, errors);
      } while (count == LOGIN_HISTORY_NODE_PAGE_SIZE);
    } finally {
      sProvider.close();
    }
    return errors;
  }

  /**
   * iterates on Login History Users Profiles by a given page size each time and
   * for each user gets all its Login History Entries and removes them one by one
   * and returns the number of errors occurred during the process
   */
  private int deleteAllLoginHistoryNodes() {
    SessionProvider sProvider = SessionProvider.createSystemProvider();
    Session session = null;

    NodeIterator usersProfilesNodes, userLoginHistoryNodes;
    Node userProfileNode, userLoginHistoryNode;

    String userId = null;
    long count, countLoginHistoryNodes, offset = 0, userLoginHistoryNodesRemoved, removed = 0;
    int errors = 0;
    try {
      do {
        try {
          session = this.getSession(sProvider);
        } catch (Exception e) {
          LOG.error("==    Login History migration - Error while getting JCR Session for delete All Login History Nodes : ",
                    e.getMessage(),
                    e);
        }
        usersProfilesNodes = jcrLoginHistoryStorage.getUsersProfilesNodes(sProvider, offset, LOGIN_HISTORY_NODE_PAGE_SIZE);
        count = usersProfilesNodes.getSize();
        while (usersProfilesNodes.hasNext()) {
          userProfileNode = usersProfilesNodes.nextNode();
          userLoginHistoryNodes = jcrLoginHistoryStorage.getAllUserLoginHistoryNodes(userProfileNode);
          countLoginHistoryNodes = userLoginHistoryNodes.getSize();
          userLoginHistoryNodesRemoved = 0;
          try {
            userId = userProfileNode.getProperty("exo:LoginHisSvc_userId").getString();
          } catch (Exception e) {
            LOG.error("==    Login History migration - Error while retrieving the UserId for : ", userProfileNode);
          }

          LOG.debug("==    Login History migration - ({}) Login History Nodes to delete for user : {}",
                    countLoginHistoryNodes,
                    userId);
          while (userLoginHistoryNodes.hasNext()) {
            try {
              userLoginHistoryNode = userLoginHistoryNodes.nextNode();
              userLoginHistoryNode.remove();
              userLoginHistoryNodesRemoved++;
              removed++;
              if (removed % 10 == 0) {
                session.save();
              }
            } catch (Exception e) {
              LOG.error("==    Login History migration - Error while deleting All Login History Nodes for user {} : ",
                        userId,
                        e.getMessage(),
                        e);
              errors++;
            }
          }
          if (userLoginHistoryNodesRemoved != 0) {
            LOG.debug("==    Login History migration - ({}) Total Login History Nodes deleted for user : {}",
                      userLoginHistoryNodesRemoved,
                      userId);
          }
        }
        try {
          session.save();
        } catch (Exception e) {
          LOG.error("==    Login History migration - Error while saving JCR Session for delete All Login History Nodes : ",
                    e.getMessage(),
                    e);
        }
        offset += count;
        LOG.info("==    Login History migration - Progress : {} All Login History Nodes removed ({} errors)", removed, errors);
      } while (count == LOGIN_HISTORY_NODE_PAGE_SIZE);
    } finally {
      sProvider.close();
    }
    return errors;
  }

  /**
   * iterates on Login History Users Profiles right under the Login History Home
   * Node by a given page size each time and removes them one by one and returns
   * the number of errors occurred during the process
   */
  private int deleteLoginHistoryProfiles() {
    SessionProvider sProvider = SessionProvider.createSystemProvider();
    Session session = null;

    NodeIterator loginHistoryProfilesNodes;
    Node loginHistoryProfileNode;

    String userId = null;
    long removed = 0;
    int errors = 0;
    try {
      try {
        session = this.getSession(sProvider);
      } catch (Exception e) {
        LOG.error("==    Login History migration - Error while getting JCR Session for delete Login History Profiles : ",
                  e.getMessage(),
                  e);
      }
      loginHistoryProfilesNodes = jcrLoginHistoryStorage.getAllUsersProfilesNodes(sProvider);
      while (loginHistoryProfilesNodes.hasNext()) {
        try {
          loginHistoryProfileNode = loginHistoryProfilesNodes.nextNode();
          userId = loginHistoryProfileNode.getProperty("exo:LoginHisSvc_userId").getString();
          jcrLoginHistoryStorage.removeLoginHistoryUserProfileChildNodes(session, userId);
          LOG.debug("==    Login History migration - User Profile child Nodes for User {} removed successfully !", userId);
          loginHistoryProfileNode.remove();
          LOG.debug("==    Login History migration - User Profile for user {} removed successfully !", userId);
          removed++;
          if (removed % 10 == 0) {
            session.save();
          }
        } catch (Exception e) {
          LOG.error("==    Login History migration - Error while removing Login History User Profile for user {} : ", userId, e);
          errors++;
        }
      }
      try {
        session.save();
      } catch (Exception e) {
        LOG.error("==    Login History migration - Error while saving JCR Session for delete Login History Profiles : ",
                  e.getMessage(),
                  e);
      }
      LOG.info("==    Login History migration - Progress : {} Login History Profiles removed ({} errors)", removed, errors);
    } finally {
      sProvider.close();
    }
    return errors;
  }

}
