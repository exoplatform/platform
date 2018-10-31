package org.exoplatform.platform.common.account.setup.web;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.api.settings.SettingValue;
import org.exoplatform.commons.api.settings.data.Context;
import org.exoplatform.commons.api.settings.data.Scope;
import org.exoplatform.commons.utils.PropertyManager;
import org.exoplatform.services.organization.OrganizationService;

@RunWith(MockitoJUnitRunner.class)
public class AccountSetupServiceTest {

  @Mock
  private SettingService      settingService;

  @Mock
  private OrganizationService organizationService;

  @Test
  public void shouldNotSkipAccountSetupWhenNothingSet() {
    // Given
    PropertyManager.setProperty(AccountSetupService.ACCOUNT_SETUP_SKIP_PROPERTY, "");
    PropertyManager.setProperty(PropertyManager.DEVELOPING, "");
    when(settingService.get(any(Context.class), any(Scope.class), eq(AccountSetupService.ACCOUNT_SETUP_NODE))).thenReturn(null);

    AccountSetupService accountSetupService = new AccountSetupService(settingService, organizationService);

    // When
    boolean mustSkip = accountSetupService.mustSkipAccountSetup();

    // Then
    assertFalse(mustSkip);
  }

  @Test
  public void shouldSkipAccountSetupWhenPropertyIsSet() {
    // Given
    PropertyManager.setProperty(AccountSetupService.ACCOUNT_SETUP_SKIP_PROPERTY, "true");

    AccountSetupService accountSetupService = new AccountSetupService(settingService, organizationService);

    // When
    boolean mustSkip = accountSetupService.mustSkipAccountSetup();

    // Then
    assertTrue(mustSkip);

    PropertyManager.setProperty(AccountSetupService.ACCOUNT_SETUP_SKIP_PROPERTY, "");
  }

  @Test
  public void shouldSkipAccountSetupWhenAlreadyDoneOrSkipped() {
    // Given
    when(settingService.get(any(Context.class),
                            any(Scope.class),
                            eq(AccountSetupService.ACCOUNT_SETUP_NODE))).thenReturn(new SettingValue("true"));

    AccountSetupService accountSetupService = new AccountSetupService(settingService, organizationService);

    // When
    boolean mustSkip = accountSetupService.mustSkipAccountSetup();

    // Then
    assertTrue(mustSkip);
  }

  @Test
  public void shouldSkipAccountSetupWhenDevelopingModeIsSet() {
    // Given
    PropertyManager.setProperty(PropertyManager.DEVELOPING, "true");

    AccountSetupService accountSetupService = new AccountSetupService(settingService, organizationService);

    // When
    boolean mustSkip = accountSetupService.mustSkipAccountSetup();

    // Then
    assertTrue(mustSkip);

    PropertyManager.setProperty(PropertyManager.DEVELOPING, "");
  }

  @Test
  public void shouldSkipAccountSetupWhenSkipSetup() {
    // Given
    AccountSetupService accountSetupService = new AccountSetupService(settingService, organizationService);

    // When
    boolean mustSkipBefore = accountSetupService.mustSkipAccountSetup();
    accountSetupService.setSkipSetup(true);
    boolean mustSkipAfter = accountSetupService.mustSkipAccountSetup();

    // Then
    assertFalse(mustSkipBefore);
    assertTrue(mustSkipAfter);
  }
}
