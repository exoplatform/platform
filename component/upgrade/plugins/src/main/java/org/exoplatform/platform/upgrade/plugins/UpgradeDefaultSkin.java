package org.exoplatform.platform.upgrade.plugins;

import org.apache.commons.lang.StringUtils;
import org.exoplatform.commons.upgrade.UpgradeProductPlugin;
import org.exoplatform.commons.version.util.VersionComparator;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.util.List;

/**
 *  Upgrade plugin to set the sites skin correctly from PLF EE 5.0 (which comes with Default and Enterprise skins)
 *  - if it is a fresh install (no data), do not do anything, Enterprise skin will be enabled by default
 *  - if it is not a fresh install and previous version is pre-5.0, for each site, if the skin is set, keep it, if not, set it to "Default".
 *  It ensures that not fresh installs will keep the same skin after the upgrade.
 */
public class UpgradeDefaultSkin extends UpgradeProductPlugin {

  private static final Log LOG = ExoLogger.getLogger(UpgradeDefaultSkin.class);

  private static final String UPGRADE_FROM_VERSION = "5.0";

  private UserPortalConfigService userPortalConfigService;

  public UpgradeDefaultSkin(InitParams initParams, UserPortalConfigService userPortalConfigService) {
    super(initParams);
    this.userPortalConfigService = userPortalConfigService;
  }

  @Override
  public void processUpgrade(String oldVersion, String newVersion) {

    RequestLifeCycle.begin(ExoContainerContext.getCurrentContainer());
    try {
      List<String> allPortalNames = userPortalConfigService.getDataStorage().getAllPortalNames();
      for(String portalName : allPortalNames) {
        try {
          PortalConfig portalConfig = userPortalConfigService.getDataStorage().getPortalConfig(portalName);
          if(StringUtils.isEmpty(portalConfig.getSkin())) {
            portalConfig.setSkin("Default");
            userPortalConfigService.getDataStorage().save(portalConfig);
          }
        } catch (Exception e) {
          LOG.error("Cannot update default skin of portal " + portalName, e);
        }
      }
    } catch(Exception e) {
      LOG.error("Cannot get list of portal names", e);
    } finally {
      RequestLifeCycle.end();
    }
  }

  @Override
  public boolean shouldProceedToUpgrade(String newVersion, String previousVersion) {
    return VersionComparator.isBefore(previousVersion, UPGRADE_FROM_VERSION);
  }
}
