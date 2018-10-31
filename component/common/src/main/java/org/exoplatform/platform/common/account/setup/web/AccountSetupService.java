package org.exoplatform.platform.common.account.setup.web;

import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.api.settings.SettingValue;
import org.exoplatform.commons.api.settings.data.Context;
import org.exoplatform.commons.api.settings.data.Scope;
import org.exoplatform.commons.utils.PropertyManager;
import org.exoplatform.container.component.ComponentRequestLifecycle;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.*;

public class AccountSetupService {

  private static final Log    LOG                             = ExoLogger.getLogger(AccountSetup.class);

  public final static String  ACCOUNT_SETUP_NODE              = "accountSetup";

  public final static String  ACCOUNT_SETUP_SKIP_PROPERTY     = "accountsetup.skip";

  private final static String ADMIN_FIRST_NAME                = "root";

  private final static String PLATFORM_USERS_GROUP            = "/platform/administrators";

  private final static String PLATFORM_WEB_CONTRIBUTORS_GROUP = "/platform/web-contributors";

  private final static String PLATFORM_DEVELOPERS_GROUP       = "/developers";

  private final static String PLATFORM_PLATFORM_USERS_GROUP   = "/platform/users";

  private final static String MEMBERSHIP_TYPE_MANAGER         = "*";

  private SettingService      settingService;

  private OrganizationService organizationService;

  private Boolean             skipSetup                       = null;

  public AccountSetupService(SettingService settingService, OrganizationService organizationService) {
    this.settingService = settingService;
    this.organizationService = organizationService;
  }

  public void setSkipSetup(boolean skipSetup) {
    this.skipSetup = skipSetup;
    settingService.set(Context.GLOBAL, Scope.GLOBAL, ACCOUNT_SETUP_NODE, SettingValue.create("true"));
  }

  public boolean mustSkipAccountSetup() {
    if (skipSetup == null) {
      SettingValue accountSetupNode = settingService.get(Context.GLOBAL, Scope.GLOBAL, ACCOUNT_SETUP_NODE);

      String propertySetupSkip = PropertyManager.getProperty(ACCOUNT_SETUP_SKIP_PROPERTY);
      if (propertySetupSkip == null) {
        LOG.debug("Property accountsetup.skip not found in configuration.properties");
        propertySetupSkip = "false";
      }

      skipSetup = accountSetupNode != null || propertySetupSkip.equals("true") || PropertyManager.isDevelopping();
    }

    return skipSetup;
  }

  public void createAccount(String userNameAccount,
                            String firstNameAccount,
                            String lastNameAccount,
                            String emailAccount,
                            String userPasswordAccount,
                            String adminPassword) {
    MembershipType membershipType;
    try {
      RequestLifeCycle.begin((ComponentRequestLifecycle) organizationService);

      MembershipTypeHandler membershipTypeHandler = organizationService.getMembershipTypeHandler();

      // Create user account
      UserHandler userHandler = organizationService.getUserHandler();
      User user = userHandler.createUserInstance(userNameAccount);
      user.setPassword(userPasswordAccount);
      user.setFirstName(firstNameAccount);
      user.setLastName(lastNameAccount);
      user.setEmail(emailAccount);

      try {
        userHandler.createUser(user, true);
      } catch (Exception e) {
        LOG.error("Can not create User", e);
      }

      // Assign the membership "*:/platform/administrators" to the created user
      try {
        Group group = organizationService.getGroupHandler().findGroupById(PLATFORM_USERS_GROUP);
        membershipType = membershipTypeHandler.findMembershipType(MEMBERSHIP_TYPE_MANAGER);
        organizationService.getMembershipHandler().linkMembership(user, group, membershipType, true);
      } catch (Exception e) {
        LOG.error("Can not assign *:/platform/administrators membership to the created user", e);
      }

      // Assign the membership "*:/platform/web-contributors" to the created user
      try {
        Group group = organizationService.getGroupHandler().findGroupById(PLATFORM_WEB_CONTRIBUTORS_GROUP);
        membershipType = membershipTypeHandler.findMembershipType(MEMBERSHIP_TYPE_MANAGER);
        organizationService.getMembershipHandler().linkMembership(user, group, membershipType, true);
      } catch (Exception e) {
        LOG.error("Can not assign *:/platform/web-contributors membership to the created user", e);
      }

      // Assign the membership "member:/developer" to the created user
      try {
        Group group = organizationService.getGroupHandler().findGroupById(PLATFORM_DEVELOPERS_GROUP);
        membershipType = membershipTypeHandler.findMembershipType(MEMBERSHIP_TYPE_MANAGER);
        organizationService.getMembershipHandler().linkMembership(user, group, membershipType, true);
      } catch (Exception e) {
        LOG.error("Can not assign *:/developers membership to the created user", e);
      }

      // Assign the membership "*:/platform/users" to the created user
      try {
        Group group = organizationService.getGroupHandler().findGroupById(PLATFORM_PLATFORM_USERS_GROUP);
        membershipType = membershipTypeHandler.findMembershipType(MEMBERSHIP_TYPE_MANAGER);
        organizationService.getMembershipHandler().linkMembership(user, group, membershipType, true);
      } catch (Exception e) {
        LOG.error("Can not assign *:/platform/users membership to the created user", e);
      }

      // Set password for admin user
      try {
        User adminUser = userHandler.findUserByName(ADMIN_FIRST_NAME);
        adminUser.setPassword(adminPassword);
        organizationService.getUserHandler().saveUser(adminUser, false);
      } catch (Exception e) {
        LOG.error("Can not set password to the created user", e);
      }

      setSkipSetup(true);
    } finally {
      RequestLifeCycle.end();
    }
  }
}
