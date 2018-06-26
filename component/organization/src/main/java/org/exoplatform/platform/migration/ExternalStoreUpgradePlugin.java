package org.exoplatform.platform.migration;

import org.exoplatform.commons.upgrade.UpgradeProductPlugin;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.externalstore.IDMExternalStoreImportService;
import org.exoplatform.services.organization.externalstore.IDMExternalStoreService;

/**
 * This Upgrade Plugin will execute the whole first additional information about
 * originating store (external or internal) of users and groups.
 */
public class ExternalStoreUpgradePlugin extends UpgradeProductPlugin {
  private static final Log              LOG = ExoLogger.getLogger(ExternalStoreUpgradePlugin.class);

  private IDMExternalStoreImportService externalStoreImportService;

  private IDMExternalStoreService       externalStoreService;

  public ExternalStoreUpgradePlugin(IDMExternalStoreImportService externalStoreImportService,
                                    IDMExternalStoreService externalStoreService,
                                    InitParams initParams) {
    super(initParams);
    this.externalStoreImportService = externalStoreImportService;
    this.externalStoreService = externalStoreService;
  }

  @Override
  public void processUpgrade(String oldVersion, String newVersion) {
    try {
      externalStoreImportService.importAllModifiedEntitiesToQueue();
    } catch (Exception e) {
      throw new RuntimeException("An error occurred while migrating IDM entities", e);
    }
  }

  @Override
  public boolean isEnabled() {
    if (!externalStoreService.isEnabled()) {
      LOG.info("ExternalStoreService is disabled, no migration is required.");
      return false;
    }
    return super.isEnabled();
  }
}
