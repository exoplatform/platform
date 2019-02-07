package org.exoplatform.platform.gadget.services.LoginHistory;

import java.util.Set;

import org.exoplatform.commons.persistence.impl.EntityManagerService;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.platform.gadget.services.LoginHistory.LoginHistoryService;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.security.ConversationRegistry;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;

import junit.framework.TestCase;

public class LoginHistoryListenerTest extends TestCase {

  private EntityManagerService entityManagerService;

  private LoginHistoryService loginHistoryService;

  public void setUp() {
    PortalContainer container = PortalContainer.getInstance();
    entityManagerService = container.getComponentInstanceOfType(EntityManagerService.class);
    loginHistoryService = container.getComponentInstanceOfType(LoginHistoryService.class);
  }

  public void testShouldAddLoginEntryWhenUserListenerIsCalled() throws Exception {
    // Given
    LoginHistoryListener loginHistoryListener = new LoginHistoryListener(loginHistoryService);
    long beforeLoginTime = System.currentTimeMillis();
    ConversationState conversationState = new ConversationState(new Identity("userLogin1"));
    Event<ConversationRegistry, ConversationState> event = new Event("login", new Object(), conversationState);

    // When
    loginHistoryListener.onEvent(event);

    // Then
    entityManagerService.startRequest(PortalContainer.getInstance());
    try {
      Set<String> lastUsersLogins = loginHistoryService.getLastUsersLogin(beforeLoginTime);
      assertNotNull(lastUsersLogins);
      assertEquals(1, lastUsersLogins.size());
      assertEquals("userLogin1", lastUsersLogins.iterator().next());
    } finally {
      entityManagerService.endRequest(PortalContainer.getInstance());
    }
  }
}
