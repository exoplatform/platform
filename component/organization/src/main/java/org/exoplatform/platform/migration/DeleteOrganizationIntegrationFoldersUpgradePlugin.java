package org.exoplatform.platform.migration;

import org.exoplatform.commons.upgrade.DeleteJCRFolderUpgradePlugin;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.UserStatus;
import org.exoplatform.services.organization.externalstore.IDMExternalStoreService;
import org.exoplatform.services.transaction.TransactionService;

/**
 * This Upgrade Plugin will Delete OrganizationIntegrationService JCR folder
 * Structure if External Store API is enabled
 */
public class DeleteOrganizationIntegrationFoldersUpgradePlugin extends DeleteJCRFolderUpgradePlugin {
  private static final Log              LOG = ExoLogger.getLogger(DeleteOrganizationIntegrationFoldersUpgradePlugin.class);

  final private OrganizationService     organizationService;

  final private IDMExternalStoreService externalStoreService;

  public DeleteOrganizationIntegrationFoldersUpgradePlugin(RepositoryService repositoryService,
                                                           TransactionService transactionService,
                                                           OrganizationService organizationService,
                                                           IDMExternalStoreService externalStoreService,
                                                           InitParams initParams) {
    super(repositoryService, transactionService, initParams);
    this.externalStoreService = externalStoreService;
    this.organizationService = organizationService;
  }

  @Override
  public void processUpgrade(String oldVersion, String newVersion) {
    try {
      int totalFoldersCountApproximation = organizationService.getUserHandler().findAllUsers(UserStatus.ANY).getSize();
      // Number of users + Number of user profile + three memberships per user
      totalFoldersCountApproximation = totalFoldersCountApproximation * 5;

      LOG.info("Attempt to delete JCR folders of Organization Integration service (approximatively {} folders)",
               totalFoldersCountApproximation);

      super.processUpgrade(oldVersion, newVersion);
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("An error occurred while Deleting Organization Integration service folders");
    }
  }

  @Override
  public boolean isEnabled() {
    if (externalStoreService == null || !externalStoreService.isEnabled()) {
      LOG.info("ExternalStore is disabled, thus the OrganizationIntegrationService data will be kept");
      return false;
    }
    return super.isEnabled();
  }

}
