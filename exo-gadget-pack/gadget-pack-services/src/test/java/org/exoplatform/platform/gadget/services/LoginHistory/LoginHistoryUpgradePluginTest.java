package org.exoplatform.platform.gadget.services.LoginHistory;

import java.util.Date;
import java.util.List;

import org.exoplatform.commons.persistence.impl.EntityManagerService;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.platform.gadget.services.LoginHistory.LoginHistoryService;
import org.exoplatform.platform.gadget.services.LoginHistory.storage.JCRLoginHistoryStorageImpl;
import org.exoplatform.platform.gadget.services.LoginHistory.storage.LoginHistoryStorage;
import org.exoplatform.platform.gadget.services.test.GadgetServiceTestcase;
import org.exoplatform.services.jcr.RepositoryService;

public class LoginHistoryUpgradePluginTest extends GadgetServiceTestcase {

  private JCRLoginHistoryStorageImpl jcrLoginHistoryStorage;

  private LoginHistoryStorage jpaLoginHistoryStorage;

  private RepositoryService repositoryService;

  private EntityManagerService entityManagerService;

  private LoginHistoryService loginHistoryService;

  public void setUp() {
    super.setUp();

    jcrLoginHistoryStorage = getService(JCRLoginHistoryStorageImpl.class);
    jpaLoginHistoryStorage = getService(LoginHistoryStorage.class);
    repositoryService = getService(RepositoryService.class);
    entityManagerService = getService(EntityManagerService.class);
    loginHistoryService = getService(LoginHistoryService.class);
  }

  public void testShouldMigrateLoginHistory() throws Exception {
    // Given
    LoginHistoryUpgradePlugin loginHistoryUpgradePlugin = new LoginHistoryUpgradePlugin(new InitParams(),
        jcrLoginHistoryStorage, jpaLoginHistoryStorage, repositoryService, entityManagerService);

    jcrLoginHistoryStorage.addLoginHistoryEntry("upgradeUser1", new Date("Jul 27 2011 13:52:57").getTime());
    jcrLoginHistoryStorage.addLoginHistoryEntry("upgradeUser1", new Date("Aug 10 2011 08:42:39").getTime());
    jcrLoginHistoryStorage.addLoginHistoryEntry("upgradeUser1", new Date("Aug 18 2011 11:23:45").getTime());
    jcrLoginHistoryStorage.addLoginHistoryEntry("upgradeUser1", new Date("Aug 19 2011 07:27:34").getTime());
    jcrLoginHistoryStorage.addLoginHistoryEntry("upgradeUser1", new Date("Aug 20 2011 09:56:12").getTime());
    jcrLoginHistoryStorage.addLoginHistoryEntry("upgradeUser2", new Date("Jul 21 2011 14:07:25").getTime());
    jcrLoginHistoryStorage.addLoginHistoryEntry("upgradeUser2", new Date("Aug 24 2011 17:45:15").getTime());

    // When
    loginHistoryUpgradePlugin.processUpgrade("5.1.0", "5.2.0");

    // Then
    entityManagerService.startRequest(PortalContainer.getInstance());
    try {
      List<LastLoginBean> upgradeUser1LastLogins = loginHistoryService.getLastLogins(10, "upgradeUser1");
      assertNotNull(upgradeUser1LastLogins);
      assertEquals(5, upgradeUser1LastLogins.size());

      long upgradeUser1LastLogin = loginHistoryService.getLastLogin("upgradeUser1");
      assertEquals(new Date("Aug 20 2011 09:56:12").getTime(), upgradeUser1LastLogin);
    } finally {
      entityManagerService.endRequest(PortalContainer.getInstance());
    }
  }
}
