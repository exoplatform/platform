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

import static org.eclipse.jetty.http.HttpParser.LOG;

public class LoginHistoryUpgradePlugin extends UpgradeProductPlugin {
  private JCRLoginHistoryStorageImpl jcrLoginHistoryStorage;

  private JPALoginHistoryStorageImpl jpaLoginHistoryStorage;

  // List of migration error
  private Set<String>                loginHistoryErrorsList = new HashSet<>();

  private RepositoryService          repositoryService;

  private String                     ALL_USERS              = "AllUsers";

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
    int pageSize = 50;
    int offset = 0;
    List<LoginHistoryBean> loginHistoryBeanList = new LinkedList<>();
    if (!hasDataToMigrate()) {
      LOG.info("No Login History data to migrate from JCR to RDBMS");
      return;
    } else {
      do {
        try {
          loginHistoryBeanList = jcrLoginHistoryStorage.getLoginHistoryByNumber(pageSize, offset);
          migrateLoginHistory(loginHistoryBeanList);
          offset += pageSize;
        } catch (Exception e) {
          e.printStackTrace();
        }
      } while (loginHistoryBeanList != null);

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
      hasDataToMigrate = getSession(sProvider).getRootNode().hasNode("exo:LoginHistoryHome");
      hasDataToMigrate = session.getRootNode().hasNode("exo:LoginHistoryHome");
    } catch (Exception e) {
      e.printStackTrace();
    }
    return hasDataToMigrate;
  }

  private void migrateLoginHistory(List<LoginHistoryBean> historyBeans) throws Exception {
    if (historyBeans == null) {
      return;
    } else {
      for (LoginHistoryBean loginHistoryBean : historyBeans) {
        String userId = loginHistoryBean.getUserId();
        long loginTime = loginHistoryBean.getLoginTime();
        jpaLoginHistoryStorage.addLoginHistoryEntry(userId, loginTime);
      }
    }
  }
}
