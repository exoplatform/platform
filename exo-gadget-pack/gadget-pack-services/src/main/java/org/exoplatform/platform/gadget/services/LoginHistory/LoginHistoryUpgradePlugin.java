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

import javax.jcr.Session;
import java.util.*;

public class LoginHistoryUpgradePlugin extends UpgradeProductPlugin {
  private static final Log           LOG                    = ExoLogger.getLogger(LoginHistoryUpgradePlugin.class);

  private JCRLoginHistoryStorageImpl jcrLoginHistoryStorage;

  private LoginHistoryStorage        jpaLoginHistoryStorage;

  // List of migration error
  private RepositoryService          repositoryService;

  private Set<String>                loginHistoryErrorsList = new HashSet<>();

  private String                     ALL_USERS              = "AllUsers";

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
    // First check to see if the JCR still contains wiki data. If not, migration is
    // skipped
    long pageSize = 50;
    long offset = 0;
    int count = 0;
    List<LoginHistoryBean> loginHistoryBeanList;
    if (!hasDataToMigrate()) {
      LOG.info("No Login History data to migrate from JCR to RDBMS");
      return;
    } else {
      do {
        try {
          loginHistoryBeanList = jcrLoginHistoryStorage.getLoginHistoryByNumber(pageSize, offset);
          migrateLoginHistory(loginHistoryBeanList);
          count = loginHistoryBeanList.size();
          offset += pageSize;
        } catch (Exception e) {
          e.printStackTrace();
        }
      } while (count != 0);

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

  private void migrateLoginHistory(List<LoginHistoryBean> historyBeans) throws Exception {
    if (historyBeans != null) {
      for (LoginHistoryBean loginHistoryBean : historyBeans) {
        String userId = loginHistoryBean.getUserId();
        long loginTime = loginHistoryBean.getLoginTime();
        jpaLoginHistoryStorage.addLoginHistoryEntry(userId, loginTime);
      }
    }
  }

}
