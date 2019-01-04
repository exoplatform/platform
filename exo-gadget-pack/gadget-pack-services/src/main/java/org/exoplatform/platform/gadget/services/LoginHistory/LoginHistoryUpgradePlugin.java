/*package org.exoplatform.platform.gadget.services.LoginHistory;

import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.upgrade.UpgradeProductPlugin;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.platform.gadget.services.LoginHistory.jpa.entity.LoginHistoryEntity;
import org.exoplatform.platform.gadget.services.LoginHistory.storage.JCRLoginHistoryStorageImpl;
import org.exoplatform.platform.gadget.services.LoginHistory.storage.JPALoginHistoryStorageImpl;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.common.SessionProvider;

import javax.jcr.Session;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import static org.eclipse.jetty.http.HttpParser.LOG;

public class LoginHistoryUpgradePlugin extends UpgradeProductPlugin {
    //service
    private JCRLoginHistoryStorageImpl jcrLoginHistoryStorage;
    private JPALoginHistoryStorageImpl jpaLoginHistoryStorage;
    private ExecutorService executorService;

    //List of migration error
    private Set<String> loginHistoryErrorsList = new HashSet<>();
    private RepositoryService repositoryService;



    public LoginHistoryUpgradePlugin(SettingService settingService, InitParams initParams) {
        super(settingService, initParams);
    }

    public LoginHistoryUpgradePlugin(InitParams initParams) {
        super(initParams);
    }

    @Override
    public void processUpgrade(String s, String s1) {
        //First check to see if the JCR still contains wiki data. If not, migration is skipped
        if (!hasDataToMigrate()) {
            LOG.info("No Login History data to migrate from JCR to RDBMS");
            return;
        }
        long startTime = System.currentTimeMillis();
        Long from = 946681200000L;


        try {
            Set<String> allUsers = jcrLoginHistoryStorage.getLastUsersLogin(from);
            List<String> allUsersList = allUsers.stream().collect(Collectors.toList());
            LoginHistoryEntity loginHistoryEntity = new LoginHistoryEntity();
            List<LoginHistoryBean> loginHistoryBeanList = new ArrayList<LoginHistoryBean>();
            List<LoginHistoryEntity> loginHistoryEntityList = new ArrayList<>();
            for (int i=0; i<allUsersList.size(); i++) {
                loginHistoryBeanList = jcrLoginHistoryStorage.getLoginHistory(allUsersList.get(i),from,startTime);
                for (int j=0;j<loginHistoryBeanList.size(); j++) {
                    Timestamp loginDate = new Timestamp(loginHistoryBeanList.get(j).getLoginTime());
                    loginHistoryEntity.setUserID(allUsersList.get(i));
                    loginHistoryEntity.setLoginDate(loginDate);
                    jpaLoginHistoryStorage.addLoginHistoryEntry(loginHistoryEntity.getUserID(),loginHistoryBeanList.get(j).getLoginTime());
                }
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
            hasDataToMigrate= getSession(sProvider).getRootNode().hasNode("exo:LoginHistoryHome");
            hasDataToMigrate = session.getRootNode().hasNode("exo:LoginHistoryHome");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hasDataToMigrate;
    }
}
*/