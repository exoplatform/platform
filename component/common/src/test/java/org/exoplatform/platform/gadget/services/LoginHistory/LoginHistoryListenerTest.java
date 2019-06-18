package org.exoplatform.platform.gadget.services.LoginHistory;

import org.exoplatform.commons.persistence.impl.EntityManagerService;
import org.exoplatform.commons.testing.BaseExoTestCase;
import org.exoplatform.component.test.ConfigurationUnit;
import org.exoplatform.component.test.ConfiguredBy;
import org.exoplatform.component.test.ContainerScope;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.platform.gadget.services.LoginHistory.storage.LoginHistoryStorage;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.naming.InitialContextInitializer;
import org.exoplatform.services.security.ConversationRegistry;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;

import java.util.Set;

@ConfiguredBy({
        @ConfigurationUnit(scope = ContainerScope.PORTAL, path = "conf/standalone/exo.platform.test-configuration.xml")
})
public class LoginHistoryListenerTest extends BaseExoTestCase {

  private static final String USER = "userLogin1";
  private EntityManagerService entityManagerService;

  private LoginHistoryStorage loginHistoryStorage;

  public void setUp() {
    ExoContainer container = getContainer();
    container.getComponentInstance(InitialContextInitializer.class);
    entityManagerService = container.getComponentInstanceOfType(EntityManagerService.class);
    loginHistoryStorage = container.getComponentInstanceOfType(LoginHistoryStorage.class);
  }

  public void testShouldAddLoginEntryWhenUserListenerIsCalledOnNullInitParams() throws Exception {
    // Given
    LoginHistoryService loginHistoryService = new LoginHistoryServiceImpl(loginHistoryStorage, null);
    LoginHistoryListener loginHistoryListener = new LoginHistoryListener(loginHistoryService);
    long beforeLoginTime = System.currentTimeMillis();
    Event<ConversationRegistry, ConversationState> event = LoginWithUser(USER);

    // When
    loginHistoryListener.onEvent(event);

    // Then
    entityManagerService.startRequest(PortalContainer.getInstance());
    try {
      Set<String> lastUsersLogins = loginHistoryService.getLastUsersLogin(beforeLoginTime);
      assertNotNull(lastUsersLogins);
      assertTrue(loginHistoryService.isEnabled());
      assertEquals(1, lastUsersLogins.size());
      assertEquals(USER, lastUsersLogins.iterator().next());
    } finally {
      entityManagerService.endRequest(PortalContainer.getInstance());
    }
  }

  public void testShouldAddLoginEntryWhenUserListenerIsCalled() throws Exception {
    // Given
    LoginHistoryService loginHistoryService = new LoginHistoryServiceImpl(loginHistoryStorage, createParams(true));
    LoginHistoryListener loginHistoryListener = new LoginHistoryListener(loginHistoryService);
    long beforeLoginTime = System.currentTimeMillis();
    Event<ConversationRegistry, ConversationState> event = LoginWithUser(USER);

    // When
    loginHistoryListener.onEvent(event);

    // Then
    entityManagerService.startRequest(PortalContainer.getInstance());
    try {
      Set<String> lastUsersLogins = loginHistoryService.getLastUsersLogin(beforeLoginTime);
      assertNotNull(lastUsersLogins);
      assertTrue(loginHistoryService.isEnabled());
      assertEquals(1, lastUsersLogins.size());
      assertEquals(USER, lastUsersLogins.iterator().next());
    } finally {
      entityManagerService.endRequest(PortalContainer.getInstance());
    }
  }

  public void testShouldNotAddLoginEntryWhenItisDisabled() throws Exception {
    // Given
    LoginHistoryService loginHistoryService = new LoginHistoryServiceImpl(loginHistoryStorage, createParams(false));
    LoginHistoryListener loginHistoryListener = new LoginHistoryListener(loginHistoryService);
    long beforeLoginTime = System.currentTimeMillis();
    Event<ConversationRegistry, ConversationState> event = LoginWithUser(USER);

    // When
    loginHistoryListener.onEvent(event);

    // Then
    entityManagerService.startRequest(PortalContainer.getInstance());
    try {
      Set<String> lastUsersLogins = loginHistoryService.getLastUsersLogin(beforeLoginTime);
      assertNotNull(lastUsersLogins);
      assertFalse(loginHistoryService.isEnabled());
      assertEquals(0, lastUsersLogins.size());
    } finally {
      entityManagerService.endRequest(PortalContainer.getInstance());
    }
  }

  private Event<ConversationRegistry, ConversationState> LoginWithUser(String user) {
    ConversationState conversationState = new ConversationState(new Identity(user));
    return (Event<ConversationRegistry, ConversationState>) new Event("login", new Object(), conversationState);
  }

  private InitParams createParams(boolean isEnabled) {
    ValueParam valueParam = new ValueParam();
    valueParam.setName(LoginHistoryService.EXO_AUDIT_LOGIN_ENABLED);
    valueParam.setValue(String.valueOf(isEnabled));
    InitParams initParams = new InitParams();
    initParams.addParam(valueParam);
    return initParams;
  }
}
