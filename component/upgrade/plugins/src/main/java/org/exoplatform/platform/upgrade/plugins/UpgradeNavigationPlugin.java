package org.exoplatform.platform.upgrade.plugins;

import java.util.Date;

import org.exoplatform.commons.upgrade.UpgradeProductPlugin;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.mop.importer.Imported;
import org.exoplatform.portal.mop.importer.Imported.Status;
import org.exoplatform.portal.pom.config.POMSession;
import org.exoplatform.portal.pom.config.POMSessionManager;
import org.gatein.mop.api.workspace.Workspace;

public class UpgradeNavigationPlugin extends UpgradeProductPlugin {
  private UserPortalConfigService portalConfigService;
  private final POMSessionManager pomMgr;

  public UpgradeNavigationPlugin(UserPortalConfigService portalConfigService, POMSessionManager pomMgr, InitParams initParams) {
    super(initParams);
    this.portalConfigService = portalConfigService;
    this.pomMgr = pomMgr;
  }

  @Override
  public void processUpgrade(String oldVersion, String newVersion) {
    RequestLifeCycle.begin(PortalContainer.getInstance());
    try {
      POMSession session = pomMgr.getSession();
      Workspace workspace = session.getWorkspace();
      Imported imported = workspace.adapt(Imported.class);
      imported.setLastModificationDate(new Date());
      imported.setStatus(Status.WANT_REIMPORT.status());
      session.save();
    } finally {
      RequestLifeCycle.end();
    }
    portalConfigService.start();
  }

  @Override
  public boolean shouldProceedToUpgrade(String previousVersion, String newVersion) {
    return true;
  }

}
