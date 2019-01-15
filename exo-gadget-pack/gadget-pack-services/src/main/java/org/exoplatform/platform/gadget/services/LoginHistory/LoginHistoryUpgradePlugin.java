package org.exoplatform.platform.gadget.services.LoginHistory;

import org.exoplatform.commons.upgrade.UpgradeProductPlugin;
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
  private static final Log           LOG = ExoLogger.getLogger(LoginHistoryUpgradePlugin.class);

  private JCRLoginHistoryStorageImpl jcrLoginHistoryStorage;

  private LoginHistoryStorage        jpaLoginHistoryStorage;

  private RepositoryService          repositoryService;

  public LoginHistoryUpgradePlugin(InitParams initParams) {
    super(initParams);
  }

  public LoginHistoryUpgradePlugin(InitParams initParams,
                                   JCRLoginHistoryStorageImpl jcrLoginHistoryStorage,
                                   LoginHistoryStorage jpaLoginHistoryStorage,
                                   RepositoryService repositoryService) {
    super(initParams);
    this.jcrLoginHistoryStorage = jcrLoginHistoryStorage;
    this.jpaLoginHistoryStorage = jpaLoginHistoryStorage;
    this.repositoryService = repositoryService;
  }

  @Override
  public void processUpgrade(String s, String s1) {
    // First check to see if the JCR still contains Login History data. If not,
    // migration is
    // skipped
    long offset = 0, pageSize = 50, migrated = 0;
    if (!hasDataToMigrate()) {
      LOG.info("No Login History data to migrate from JCR to RDBMS");
    } else {
      do {
        try {
          migrated = migrateDeleteLoginHistory(offset, pageSize);
          offset += migrated;
        } catch (Exception e) {
          LOG.error("Error during the migration process: " + e.getMessage());
        }
      } while (migrated == pageSize);

      deleteLoginHistoryCounters(pageSize);
      LOG.info("Login History Entries and Counters JCR Data deleted successfully");

      try {
        // jcrLoginHistoryStorage.removeLoginHistoryHomeNode();
      } catch (Exception e) {
        LOG.error("Error while deleting Login History Home Node: " + e.getMessage());
      }
      LOG.info("Login History Home Node deleted successfully !");
    }
  }

  private Session getSession(SessionProvider sessionProvider) throws Exception {
    ManageableRepository currentRepo = this.repositoryService.getCurrentRepository();
    return sessionProvider.getSession(currentRepo.getConfiguration().getDefaultWorkspaceName(), currentRepo);
  }

  private Boolean hasDataToMigrate() {
    boolean hasDataToMigrate = true;
    SessionProvider sProvider = SessionProvider.createSystemProvider();
    try {
      Session session = getSession(sProvider);
      hasDataToMigrate = session.getRootNode().hasNode("exo:LoginHistoryHome");
    } catch (Exception e) {
      e.printStackTrace();
    }
    return hasDataToMigrate;
  }

  private long migrateDeleteLoginHistory(long offset, long size) throws Exception {
    NodeIterator loginHistoryNodes = jcrLoginHistoryStorage.getLoginHistoryNodes(offset, size);
    long count = loginHistoryNodes.getSize();
    Node loginHistoryNode;
    String userId;
    long loginTime;
    while (loginHistoryNodes.hasNext()) {
      loginHistoryNode = loginHistoryNodes.nextNode();
      userId = loginHistoryNode.getProperty("exo:LoginHisSvc_loginHistoryItem_userId").getString();
      loginTime = loginHistoryNode.getProperty("exo:LoginHisSvc_loginHistoryItem_loginTime").getLong();
      jpaLoginHistoryStorage.addLoginHistoryEntry(userId, loginTime);
      jcrLoginHistoryStorage.removeLoginHistoryNode(loginHistoryNode);
    }
    return count;
  }

  private void deleteLoginHistoryCounters(long pageSize) {
    long removed, offset = 0;
    do {
      try {
        removed = jcrLoginHistoryStorage.removeLoginCounter(offset, pageSize);
        offset += removed;
      } catch (Exception e) {
        LOG.error("Error while deleting Login Counter" + e.getMessage());
        break;
      }
    } while (removed > 0);
  }

}
