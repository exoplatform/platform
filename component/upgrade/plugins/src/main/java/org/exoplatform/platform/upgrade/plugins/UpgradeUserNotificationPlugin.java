package org.exoplatform.platform.upgrade.plugins;

import org.exoplatform.commons.api.notification.service.setting.UserSettingService;
import org.exoplatform.commons.upgrade.UpgradeProductPlugin;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.commons.utils.ListAccess;
import org.exoplatform.commons.version.util.VersionComparator;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;

public class UpgradeUserNotificationPlugin extends UpgradeProductPlugin {
  
  private static final Log LOG = ExoLogger.getLogger(UpgradeUserNotificationPlugin.class);
  
  private static final String MAX_LIMIT_USERS = "max.limit.users.loaded";
  
  protected int max_limit = 30;

  public UpgradeUserNotificationPlugin(InitParams initParams) {
    super(initParams);
    max_limit = getValueParam(initParams, MAX_LIMIT_USERS, max_limit);
  }

  @Override
  public void processUpgrade(String oldVersion, String newVersion) {
    OrganizationService organizationService = CommonsUtils.getService(OrganizationService.class);
    try {
      ListAccess<User> list = organizationService.getUserHandler().findAllUsers();
      int offset = 0, size = list.getSize();
      
      while (offset < size) {
        offset += upgradeInRange(offset, max_limit, list);
      }
    
    } catch (Exception e) {
      LOG.error("Upgrade old users to use notification default setting failed", e);
    }

  }

  @Override
  public boolean shouldProceedToUpgrade(String newVersion, String previousVersion) {
    return VersionComparator.isAfter(newVersion, previousVersion);
  }

  /**
   * Use lazyload to upgrade old users
   * 
   * @param offset
   * @param limit
   * @param list the list access of users
   * @return the size of identities after load
   */
  private int upgradeInRange(int offset, int limit, ListAccess<User> list) {
    UserSettingService userSettingService = CommonsUtils.getService(UserSettingService.class);
    int size = 0;
    try {
      User[] users = list.load(offset, limit);
      size = users.length;
      if (size == 0) return size;
      userSettingService.addMixin(users);
      
    } catch (Exception e) {
      LOG.error("Failed to upgrade user notification setting", e);
    }
    return size;
  }
  
  private int getValueParam(InitParams params, String key, int defaultValue) {
    try {
      return Integer.valueOf(params.getValueParam(key).getValue());
    } catch (Exception e) {
      return defaultValue;
    }
  }
}
