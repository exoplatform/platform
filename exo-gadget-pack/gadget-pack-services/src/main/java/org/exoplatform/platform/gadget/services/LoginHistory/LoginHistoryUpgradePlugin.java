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
      LOG.info("No Login History data to migrate from JCR to RDBMS");
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
          throw new RuntimeException("Errors during the deleting of Login History Counters and Users Profiles");
        }

        try {
          jcrLoginHistoryStorage.removeLoginHistoryHomeNode();
        } catch (Exception e) {
          throw new RuntimeException("Error when deleting Login History home node");
        }
        LOG.info("==    Login History migration - Home Node deleted successfully !");
        LOG.info("== Login History migration done");
      } else {
        LOG.error("==    Login History migration aborted, {} errors encountered", migrationErrors);
        throw new RuntimeException("Login History migration aborted because of migration failures");
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
   * it to the JPAStorage and then removes it
   */
  private int migrateAndDeleteLoginHistory() {
    SessionProvider sProvider = SessionProvider.createSystemProvider();
    entityManagerService.startRequest(ExoContainerContext.getCurrentContainer());

    long offset = 0, migrated = 0, countLoginHistoryNodes, countLoginHistoryCountersNodes, countAllUsersLoginHistoryCountersNodes;
    int errors = 0;

    long count;
    try {
      countLoginHistoryNodes = jcrLoginHistoryStorage.countLoginHistoryNodes(sProvider);
      countAllUsersLoginHistoryCountersNodes = jcrLoginHistoryStorage.countAllUsersLoginHistoryCountersNodes(sProvider);
      countLoginHistoryCountersNodes = jcrLoginHistoryStorage.countLoginHistoryCountersNodes(sProvider);

      LOG.info("({}) Total Login History Nodes to migrate !", countLoginHistoryNodes);
      LOG.info("({}) Total All Users Login History Counters Nodes to delete !", countAllUsersLoginHistoryCountersNodes);
      LOG.info("({}) Total Login History Counters Nodes to delete !", countLoginHistoryCountersNodes);
      do {
        entityManagerService.endRequest(ExoContainerContext.getCurrentContainer());
        entityManagerService.startRequest(ExoContainerContext.getCurrentContainer());
        NodeIterator loginHistoryNodes = jcrLoginHistoryStorage.getLoginHistoryNodes(sProvider,
                                                                                     offset,
                                                                                     LOGIN_HISTORY_NODE_PAGE_SIZE);
        count = loginHistoryNodes.getSize();
        Node loginHistoryNode;
        String userId = null;
        long loginTime = 0;
        while (loginHistoryNodes.hasNext()) {
          try {
            loginHistoryNode = loginHistoryNodes.nextNode();
            userId = loginHistoryNode.getProperty("exo:LoginHisSvc_loginHistoryItem_userId").getString();
            loginTime = loginHistoryNode.getProperty("exo:LoginHisSvc_loginHistoryItem_loginTime").getLong();
            LOG.debug("==    Login History migration - Migrate entry of user {} at {}", userId, loginTime);
            jpaLoginHistoryStorage.addLoginHistoryEntry(userId, loginTime);
            LOG.info("Removing Login History Node :", loginHistoryNode.getPath());
            jcrLoginHistoryStorage.removeLoginHistoryNode(sProvider, loginHistoryNode);
            migrated++;
          } catch (Exception e) {
            LOG.error("==    Login History migration - Error while migrating login of " + userId + " at " + loginTime, e);
            errors++;
          }
        }
        offset += count;
        LOG.info("==    Login History migration - Progress : {} logins migrated ({} errors)", migrated, errors);
      } while (count == LOGIN_HISTORY_NODE_PAGE_SIZE);
    } finally {
      entityManagerService.endRequest(ExoContainerContext.getCurrentContainer());
      sProvider.close();
    }
    return errors;
  }

  /**
   * iterates on Login History Counters by a given page size each time and removes
   * them one by one and by the end deletes the All Users Profile Node
   */
  private int deleteAllUsersLoginHistoryCounters() {
    SessionProvider sProvider = SessionProvider.createSystemProvider();
    Session session = null;

    long offset = 0, removed = 0;
    int errors = 0;

    long count;
    try {
      do {
        try {
          session = this.getSession(sProvider);
        } catch (Exception e) {
          LOG.error("Error while getting JCR Session for delete All Users Login History Counters : ", e.getMessage(), e);
        }
        NodeIterator loginCountersNodes = jcrLoginHistoryStorage.getAllUsersLoginCountersNodes(sProvider,
                                                                                               offset,
                                                                                               LOGIN_HISTORY_NODE_PAGE_SIZE);
        count = loginCountersNodes.getSize();
        Node loginCounterNode;
        while (loginCountersNodes.hasNext()) {
          try {
            loginCounterNode = loginCountersNodes.nextNode();
            LOG.info("Removing Login Counter Node :", loginCounterNode.getPath());
            jcrLoginHistoryStorage.removeAllUsersLoginCounterNode(loginCounterNode);
            removed++;
            if (removed % 10 == 0) {
              session.save();
            }
          } catch (Exception e) {
            LOG.error("==    Login History migration - Error while removing All Users login counter : ", e);
            errors++;
          }
        }
        try {
          session.save();
        } catch (Exception e) {
          LOG.error("Error while saving JCR Session delete All Users Login History Counters : ", e.getMessage(), e);
        }
        offset += count;
        LOG.info("==    Login History migration - Process : {} All Users Login Counters removed ({} errors)", removed, errors);
        session.logout();
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
   * iterates on Login History Counters by a given page size each time and removes
   * them one by one
   */
  private int deleteLoginHistoryCounters() {
    SessionProvider sProvider = SessionProvider.createSystemProvider();
    Session session = null;

    long offset = 0, removed = 0;
    int errors = 0;

    long count;
    try {
      do {
        try {
          session = this.getSession(sProvider);
        } catch (Exception e) {
          LOG.error("Error while getting JCR Session delete Login History Counters : ", e.getMessage(), e);
        }
        NodeIterator loginCountersNodes = jcrLoginHistoryStorage.getLoginCountersNodes(sProvider,
                                                                                       offset,
                                                                                       LOGIN_HISTORY_NODE_PAGE_SIZE);
        count = loginCountersNodes.getSize();
        Node loginCounterNode;
        while (loginCountersNodes.hasNext()) {
          try {
            loginCounterNode = loginCountersNodes.nextNode();
            LOG.info("Removing Login Counter Node :", loginCounterNode.getPath());
            jcrLoginHistoryStorage.removeLoginCounterNode(loginCounterNode);
            removed++;
            if (removed % 10 == 0) {
              session.save();
            }
          } catch (Exception e) {
            LOG.error("==    Login History migration - Error while removing login counter : ", e);
            errors++;
          }
        }
        try {
          session.save();
        } catch (Exception e) {
          LOG.error("Error while saving JCR Session delete Login History Counters : ", e.getMessage(), e);
        }
        offset += count;
        LOG.info("==    Login History migration - Process : {} Each User Login Counters removed ({} errors)", removed, errors);
        session.logout();
      } while (count == LOGIN_HISTORY_NODE_PAGE_SIZE);
    } finally {
      sProvider.close();
    }
    return errors;
  }

  /**
   * iterates on All Login History Entries by a given page size each time and removes
   * them one by one
   */
  private int deleteAllLoginHistoryNodes() {
    SessionProvider sProvider = SessionProvider.createSystemProvider();
    Session session = null;

    long offset = 0, removed = 0;
    int errors = 0;

    long count;
    try {
      do {
        try {
          session = this.getSession(sProvider);
        } catch (Exception e) {
          LOG.error("Error while getting JCR Session delete All Login History : ", e.getMessage(), e);
        }
        NodeIterator allLoginHistoryNodes = jcrLoginHistoryStorage.getLoginHistoryNodes(sProvider,
                                                                                        offset,
                                                                                        LOGIN_HISTORY_NODE_PAGE_SIZE);
        count = allLoginHistoryNodes.getSize();
        Node allLoginHistoryNode;
        while (allLoginHistoryNodes.hasNext()) {
          try {
            allLoginHistoryNode = allLoginHistoryNodes.nextNode();
            LOG.info("Removing All Login History Node :", allLoginHistoryNode.getPath());
            jcrLoginHistoryStorage.removeAllLoginHistoryNode(allLoginHistoryNode);
            removed++;
            if (removed % 10 == 0) {
              session.save();
            }
          } catch (Exception e) {
            LOG.error("==    Login History migration - Error while removing login history node : ", e);
            errors++;
          }
        }
        try {
          session.save();
        } catch (Exception e) {
          LOG.error("Error while saving JCR Session delete All Login History Nodes : ", e.getMessage(), e);
        }
        offset += count;
        LOG.info("==    Login History migration - Process : {} Each User Login History removed ({} errors)", removed, errors);
        session.logout();
      } while (count == LOGIN_HISTORY_NODE_PAGE_SIZE);
    } finally {
      sProvider.close();
    }
    return errors;
  }

  /**
   * iterates on Login History Users Profiles right under the Login History Home
   * Node by a given page size each time and removes them one by one
   */
  private int deleteLoginHistoryProfiles() {
    SessionProvider sProvider = SessionProvider.createSystemProvider();
    Session session = null;

    long offset = 0, removed = 0;
    int errors = 0;
    String userId = null;

    long count;
    try {
      do {
        try {
          session = this.getSession(sProvider);
        } catch (Exception e) {
          LOG.error("Error while getting JCR Session delete Login History Profiles : ", e.getMessage(), e);
        }
        NodeIterator loginHistoryProfilesNodes =
                                               jcrLoginHistoryStorage.getLoginHistoryUsersProfilesNodes(sProvider,
                                                                                                        offset,
                                                                                                        LOGIN_HISTORY_NODE_PAGE_SIZE);
        count = loginHistoryProfilesNodes.getSize();
        Node loginHistoryProfileNode;
        while (loginHistoryProfilesNodes.hasNext()) {
          try {
            loginHistoryProfileNode = loginHistoryProfilesNodes.nextNode();
            userId = loginHistoryProfileNode.getProperty("exo:LoginHisSvc_userId").getString();
            jcrLoginHistoryStorage.removeLoginHistoryUserProfileChildNodes(session, userId);
            LOG.info("==    Login History migration - User Profile child Nodes for User {} removed successfully !", userId);
            jcrLoginHistoryStorage.removeLoginHistoryUserProfileNode(loginHistoryProfileNode);
            LOG.info("==    Login History migration - User Profile for user {} removed successfully !", userId);
            removed++;
            if (removed % 10 == 0) {
              session.save();
            }
          } catch (Exception e) {
            LOG.error("==    Login History migration - Error while removing login history profile for user {} : ", userId, e);
            errors++;
          }
        }
        try {
          session.save();
        } catch (Exception e) {
          LOG.error("Error while saving JCR Session delete Login History Profiles : ", e.getMessage(), e);
        }
        offset += count;
        LOG.info("==    Login History migration - Process : {} Login History Profiles removed ({} errors)", removed, errors);
        session.logout();
      } while (count == LOGIN_HISTORY_NODE_PAGE_SIZE);
    } finally {
      sProvider.close();
    }
    return errors;
  }

}
