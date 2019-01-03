package org.exoplatform.platform.gadget.services.LoginHistory;

import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.upgrade.UpgradeProductPlugin;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.platform.gadget.services.LoginHistory.storage.JCRLoginHistoryStorageImpl;
import org.exoplatform.platform.gadget.services.LoginHistory.storage.JPALoginHistoryStorageImpl;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.common.SessionProvider;

import javax.jcr.Session;
import java.util.*;
import java.util.concurrent.ExecutorService;

import static org.eclipse.jetty.http.HttpParser.LOG;

public class LoginHistoryUpgradePlugin extends UpgradeProductPlugin {
  private JCRLoginHistoryStorageImpl jcrLoginHistoryStorage;

  private JPALoginHistoryStorageImpl jpaLoginHistoryStorage;

  private ExecutorService            executorService;

  // List of migration error
  private Set<String>                loginHistoryErrorsList = new HashSet<>();

  private RepositoryService          repositoryService;

  private String ALL_USERS = "AllUsers";

  public LoginHistoryUpgradePlugin(SettingService settingService, InitParams initParams) {
    super(settingService, initParams);
  }

  public LoginHistoryUpgradePlugin(InitParams initParams) {
    super(initParams);
  }

  @Override
  public void processUpgrade(String s, String s1) {
    // First check to see if the JCR still contains wiki data. If not, migration is
    // skipped
    if (!hasDataToMigrate()) {
      LOG.info("No Login History data to migrate from JCR to RDBMS");
      return;
    }
    long startTime = System.currentTimeMillis();
    Long from = 946681200000L;

    try {
      List<LoginHistoryBean> loginHistoryBeanList = jcrLoginHistoryStorage.getLoginHistory(ALL_USERS,from,startTime);

      for (LoginHistoryBean loginHistoryBean : loginHistoryBeanList) {
        long loginDate = loginHistoryBean.getLoginTime();
        String userId = loginHistoryBean.getUserId();
        jpaLoginHistoryStorage.addLoginHistoryEntry(userId, loginDate);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private Session getSession(SessionProvider sessionProvider) throws Exception {
    ManageableRepository currentRepo = this.repositoryService.getCurrentRepository();
    return sessionProvider.getSession(currentRepo.getConfiguration().getDefaultWorkspaceName(), currentRepo);
  }

  private Boolean hasDataToMigrate() {
    boolean hasDataToMigrate = true;
    JCRLoginHistoryStorageImpl jcrStorage = new JCRLoginHistoryStorageImpl(repositoryService);
    SessionProvider sProvider = SessionProvider.createSystemProvider();
    try {
      Session session = getSession(sProvider);
      hasDataToMigrate = getSession(sProvider).getRootNode().hasNode("exo:LoginHistoryHome");
      hasDataToMigrate = session.getRootNode().hasNode("exo:LoginHistoryHome");
    } catch (Exception e) {
      e.printStackTrace();
    }
    return hasDataToMigrate;
  }
}
