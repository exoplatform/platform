package org.exoplatform.platform.component.organization.test;

import org.exoplatform.commons.utils.ListAccess;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.ComponentRequestLifecycle;
import org.exoplatform.container.configuration.ConfigurationManager;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.platform.organization.integration.*;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.organization.*;
import org.exoplatform.services.organization.idm.*;
import org.exoplatform.services.organization.impl.MembershipImpl;
import org.exoplatform.test.BasicTestCase;
import org.junit.Test;
import org.mockito.Mockito;
import org.picketlink.idm.api.AttributesManager;
import org.picketlink.idm.api.IdentitySession;
import org.picketlink.idm.api.IdentitySessionFactory;
import org.picketlink.idm.api.PersistenceManager;
import org.picketlink.idm.impl.api.session.IdentitySessionImpl;
import org.picketlink.idm.impl.api.session.context.IdentitySessionContextImpl;
import org.picketlink.idm.impl.repository.RepositoryIdentityStoreSessionImpl;
import org.picketlink.idm.spi.store.IdentityStoreInvocationContext;
import org.picketlink.idm.spi.store.IdentityStoreSession;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.naming.ldap.LdapContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class TestOrganizationIntegration extends BasicTestCase {
  PortalContainer container = null;
  RepositoryService repositoryService = null;
  OrganizationService organizationService = null;

  @Override
  protected void setUp() throws Exception {
    container = PortalContainer.getInstance();
    repositoryService = (RepositoryService) container.getComponentInstanceOfType(RepositoryService.class);
    organizationService = (OrganizationService) container.getComponentInstanceOfType(OrganizationService.class);
  }


    public void testIntegrationService() throws Exception {
        OrganizationIntegrationService organizationIntegrationService = container.createComponent(OrganizationIntegrationService.class);
        container.registerComponentInstance(organizationIntegrationService);
        assertNotNull(organizationIntegrationService);
        NewUserListener userListener = container.createComponent(NewUserListener.class);
        assertNotNull(userListener);
        organizationIntegrationService.addListenerPlugin(userListener);
        NewProfileListener profileListener = container.createComponent(NewProfileListener.class);
        assertNotNull(profileListener);
        organizationIntegrationService.addListenerPlugin(profileListener);
        NewMembershipListener membershipListener = container.createComponent(NewMembershipListener.class);
        assertNotNull(membershipListener);
        organizationIntegrationService.addListenerPlugin(membershipListener);
        NewGroupListener groupListener = container.createComponent(NewGroupListener.class);
        assertNotNull(groupListener);
        organizationIntegrationService.addListenerPlugin(groupListener);

    }

    @Test
    public void testSyncDeletedUserException() throws Exception {
        PicketLinkIDMService picketLinkIDMService = Mockito.mock(PicketLinkIDMService.class);
        IdentitySessionImpl identitySession = Mockito.mock(IdentitySessionImpl.class);
        Mockito.when((IdentitySessionImpl) picketLinkIDMService.getIdentitySession()).thenReturn(identitySession);
        IdentitySessionContextImpl ctx = Mockito.mock (IdentitySessionContextImpl.class);
        Mockito.when(((IdentitySessionImpl) identitySession).getSessionContext()).thenReturn(ctx);
        IdentityStoreInvocationContext identityStoreInvocationContext = Mockito.mock (IdentityStoreInvocationContext.class);
        Mockito.when(ctx.resolveStoreInvocationContext()).thenReturn(identityStoreInvocationContext);
        RepositoryIdentityStoreSessionImpl repoSession = Mockito.mock(RepositoryIdentityStoreSessionImpl.class);
        Mockito.when((RepositoryIdentityStoreSessionImpl) identityStoreInvocationContext.getIdentityStoreSession()).thenReturn(repoSession);
        IdentityStoreSession identityStoreSession = Mockito.mock(IdentityStoreSession.class);
        Mockito.when(repoSession.getIdentityStoreSession("PortalLDAPStore")).thenReturn(identityStoreSession);
        LdapContext ldapContext = Mockito.mock(LdapContext.class);
        Mockito.when((LdapContext) identityStoreSession.getSessionContext()).thenReturn(ldapContext);
        boolean connected = false;
        if (ldapContext.getEnvironment() != null) {
            connected = true;
        } else {
            connected = false;
        }
        assertFalse(connected);
        OrganizationService organizationService = Mockito.mock(OrganizationService.class);
        RepositoryService repositoryService = Mockito.mock(RepositoryService.class);
        ConfigurationManager manager = Mockito.mock(ConfigurationManager.class);
        PortalContainer container = Mockito.mock(PortalContainer.class);
        InitParams initParams = Mockito.mock(InitParams.class);
        PicketLinkIDMCacheService picketLinkIDMCacheService = Mockito.mock(PicketLinkIDMCacheService.class);
        OrganizationIntegrationService organizationIntegrationService = Mockito.spy(new OrganizationIntegrationService(organizationService, repositoryService, manager, container, initParams, picketLinkIDMCacheService));
        PicketLinkIDMOrganizationServiceImpl picketLinkIDMOrganizationServiceImpl = Mockito.mock(PicketLinkIDMOrganizationServiceImpl.class);
        UserDAOImpl userDAO = new UserDAOImpl(picketLinkIDMOrganizationServiceImpl, picketLinkIDMService);
        IdentitySession session = Mockito.mock(IdentitySession.class);
        Mockito.when(picketLinkIDMService.getIdentitySession()).thenReturn(session);
        User user = userDAO.createUserInstance("root");
        user.setPassword("123456");
        user.setEmail("test@gmail.com");
        NewUserListener userListener = Mockito.mock(NewUserListener.class);
        organizationIntegrationService.addListenerPlugin(userListener);
        Mockito.verify(userListener, never()).preDelete(user);
        Mockito.verify(userListener, never()).postDelete(user);
        assertNotNull(user);
  }

    @Test
    public void testSyncDeletedUser() throws Exception {
        PicketLinkIDMService picketLinkIDMService = Mockito.mock(PicketLinkIDMService.class);
        OrganizationService organizationService = Mockito.mock(OrganizationService.class);
        RepositoryService repositoryService = Mockito.mock(RepositoryService.class);
        ManageableRepository repository = Mockito.mock(ManageableRepository.class);
        Mockito.when(repositoryService.getCurrentRepository()).thenReturn(repository);
        Session session = Mockito.mock(Session.class);
        Mockito.when(repository.getSystemSession("collaboration")).thenReturn(session);
        ConfigurationManager manager = Mockito.mock(ConfigurationManager.class);
        PortalContainer container = Mockito.mock(PortalContainer.class);
        InitParams initParams = Mockito.mock(InitParams.class);
        PicketLinkIDMCacheService picketLinkIDMCacheService = Mockito.mock(PicketLinkIDMCacheService.class);
        OrganizationIntegrationService organizationIntegrationService = Mockito.spy(new OrganizationIntegrationService(organizationService, repositoryService, manager, container, initParams, picketLinkIDMCacheService));
        PicketLinkIDMOrganizationServiceImpl picketLinkIDMOrganizationServiceImpl = Mockito.mock(PicketLinkIDMOrganizationServiceImpl.class);
        UserDAOImpl userDAO = new UserDAOImpl(picketLinkIDMOrganizationServiceImpl, picketLinkIDMService);
        IdentitySession identitySession = Mockito.mock(IdentitySession.class);
        Mockito.when(picketLinkIDMService.getIdentitySession()).thenReturn(identitySession);
        Config config = Mockito.mock(Config.class);
        Mockito.when(picketLinkIDMOrganizationServiceImpl.getConfiguration()).thenReturn(config);
        AttributesManager am = Mockito.mock(AttributesManager.class);
        Mockito.when(identitySession.getAttributesManager()).thenReturn(am);
        PersistenceManager persistenceManager = Mockito.mock(PersistenceManager.class);
        Mockito.when(identitySession.getPersistenceManager()).thenReturn(persistenceManager);
        User user = userDAO.createUserInstance("exo");
        user.setPassword("123456");
        user.setEmail("test@gmail.com");
        userDAO.saveUser(user, false);
        userDAO.persistUserInfo(user, identitySession, true);
        userDAO.populateUser(user, identitySession);
        NewUserListener userListener = Mockito.mock(NewUserListener.class);
        organizationIntegrationService.addListenerPlugin(userListener);
        MembershipHandler membershipHandler = Mockito.mock(MembershipHandler.class);
        Mockito.when(organizationService.getMembershipHandler()).thenReturn(membershipHandler);
        UserHandler userHandler = Mockito.mock(UserHandler.class);
        Mockito.when(organizationService.getUserHandler()).thenReturn(userHandler);
        Node folder = Mockito.mock(Node.class);
        Mockito.when(session.getItem("/")).thenReturn(folder);
        Node organizationInitializersHomePathNode = Mockito.mock(Node.class);
        Mockito.when(folder.getNode("OrganizationIntegrationService")).thenReturn(organizationInitializersHomePathNode);
        Node node = Mockito.mock(Node.class);
        Mockito.when(org.exoplatform.platform.organization.integration.Util.getUsersFolder(session)).thenReturn(node);
        Mockito.when(Util.hasUserFolder(session, user.getUserName())).thenReturn(true);
        IdentitySession picketlinkIdentitySession = Mockito.mock(IdentitySession.class);
        Mockito.when(picketLinkIDMService.getIdentitySession()).thenReturn(picketlinkIdentitySession);
        IdentitySessionFactory identitySessionFactory = Mockito.mock(IdentitySessionFactory.class);
        Mockito.when(picketLinkIDMService.getIdentitySessionFactory()).thenReturn(identitySessionFactory);
        userDAO.removeUser(user.getUserName(), false);
        organizationIntegrationService.syncUser("exo", EventType.DELETED.toString());
        Mockito.verify(userListener, times(1)).preDelete(any());
        Mockito.verify(userListener, times(1)).postDelete(any());
    }

   /**
  public void testIntegrationService() throws Exception {
        verifyFoldersCreation(false);

        OrganizationIntegrationService organizationIntegrationService = container
                .createComponent(OrganizationIntegrationService.class);
        container.registerComponentInstance(organizationIntegrationService);

        NewUserListener userListener = container.createComponent(NewUserListener.class);
        organizationIntegrationService.addListenerPlugin(userListener);
        NewProfileListener profileListener = container.createComponent(NewProfileListener.class);
        organizationIntegrationService.addListenerPlugin(profileListener);
        NewMembershipListener membershipListener = container.createComponent(NewMembershipListener.class);
        organizationIntegrationService.addListenerPlugin(membershipListener);
        NewGroupListener groupListener = container.createComponent(NewGroupListener.class);
        organizationIntegrationService.addListenerPlugin(groupListener);

        Session session = null;
        try {
            session = repositoryService.getCurrentRepository().getSystemSession(Util.WORKSPACE);
            organizationIntegrationService.syncGroup("/organization/management/executive-board", EventType.ADDED.toString());
            organizationIntegrationService.syncGroup("/organization/management/executive-board", EventType.DELETED.toString());

            assertTrue(Util.hasGroupFolder(session, "/organization"));
            assertTrue(Util.hasGroupFolder(session, "/organization/management"));
            assertTrue(Util.hasGroupFolder(session, "/organization/management/executive-board"));

            organizationIntegrationService.syncAllGroups(EventType.DELETED.toString());

            assertTrue(Util.hasGroupFolder(session, "/organization"));
            assertTrue(Util.hasGroupFolder(session, "/organization/management"));
            assertTrue(Util.hasGroupFolder(session, "/organization/management/executive-board"));

            organizationIntegrationService.syncUser("root", EventType.ADDED.toString());

            verifyUserFoldersCreation("root", true);

            organizationIntegrationService.syncUser("root", EventType.DELETED.toString());

            verifyUserFoldersCreation("root", true);

            deleteMembership("member:root:/organization/management/executive-board");
            verifyMembershipFoldersCreation("root", "/organization/management/executive-board", "member", true);
            organizationIntegrationService.syncMembership("root", "/organization/management/executive-board",
                    EventType.DELETED.toString());
            verifyMembershipFoldersCreation("root", "/organization/management/executive-board", "member", false);

            deleteUser("root");
            organizationIntegrationService.syncUser("root", EventType.DELETED.toString());

            verifyUserFoldersCreation("root", false);

            deleteGroup("/organization");
            organizationIntegrationService.syncGroup("/organization", EventType.DELETED.toString());

            assertFalse(Util.hasGroupFolder(session, "/organization"));
            assertFalse(Util.hasGroupFolder(session, "/organization/management"));
            assertFalse(Util.hasGroupFolder(session, "/organization/management/executive-board"));

            MembershipImpl membership = new MembershipImpl();
            {
                membership.setMembershipType("manager");
                membership.setUserName("john");
                membership.setGroupId("/organization/management/executive-board");
                membership.setId(Util.computeId(membership));

                assertFalse(Util.hasMembershipFolder(session, membership));
            }

            organizationIntegrationService.syncAll();
            verifyFoldersCreation(true);

            if (organizationService instanceof ComponentRequestLifecycle) {
                ((ComponentRequestLifecycle) organizationService).startRequest(container);
            }
            @SuppressWarnings("unchecked")
            List<Group> groups = new ArrayList<Group>(organizationService.getGroupHandler().getAllGroups());
            Collections.sort(groups, OrganizationIntegrationService.GROUP_COMPARATOR);
            Collections.reverse(groups);

            for (Group group : groups) {
                organizationService.getGroupHandler().removeGroup(group, true);
            }

            ListAccess<User> usersListAccess = organizationService.getUserHandler().findAllUsers();
            int i = 0;
            while (i <= usersListAccess.getSize()) {
                int length = i + 10 <= usersListAccess.getSize() ? 10 : usersListAccess.getSize() - i;
                User[] users = usersListAccess.load(i, length);
                for (User user : users) {
                    organizationService.getUserHandler().removeUser(user.getUserName(), true);
                }
                i += 10;
            }

            if (organizationService instanceof ComponentRequestLifecycle) {
                ((ComponentRequestLifecycle) organizationService).endRequest(container);
            }
            organizationIntegrationService.syncAll();

            assertFalse(Util.getGroupsFolder(session).hasNodes());
            assertFalse(Util.getMembershipsFolder(session).hasNodes());
            assertFalse(Util.getUsersFolder(session).hasNodes());
            assertFalse(Util.getProfilesFolder(session).hasNodes());
        } finally {
            if (session != null) {
                session.logout();
            }
        }
    }
    */

  private void deleteMembership(String membershipId) throws Exception {
    if (organizationService instanceof ComponentRequestLifecycle) {
      ((ComponentRequestLifecycle) organizationService).startRequest(container);
    }
    organizationService.getMembershipHandler().removeMembership(membershipId, true);
    if (organizationService instanceof ComponentRequestLifecycle) {
      ((ComponentRequestLifecycle) organizationService).endRequest(container);
    }
  }

  private void deleteUser(String username) throws Exception {
    if (organizationService instanceof ComponentRequestLifecycle) {
      ((ComponentRequestLifecycle) organizationService).startRequest(container);
    }
    organizationService.getUserHandler().removeUser(username, true);
    if (organizationService instanceof ComponentRequestLifecycle) {
      ((ComponentRequestLifecycle) organizationService).endRequest(container);
    }
  }

  private void deleteGroup(String groupId) throws Exception {
    if (organizationService instanceof ComponentRequestLifecycle) {
      ((ComponentRequestLifecycle) organizationService).startRequest(container);
    }
    organizationService.getGroupHandler().removeGroup(organizationService.getGroupHandler().findGroupById(groupId), true);
    if (organizationService instanceof ComponentRequestLifecycle) {
      ((ComponentRequestLifecycle) organizationService).endRequest(container);
    }
  }

  private void verifyUserFoldersCreation(String username, boolean creationAssertionValue) throws Exception {
    Session session = null;
    try {
      session = repositoryService.getCurrentRepository().getSystemSession(Util.WORKSPACE);
      if (organizationService instanceof ComponentRequestLifecycle) {
        ((ComponentRequestLifecycle) organizationService).startRequest(container);
      }
      assertEquals(creationAssertionValue, Util.hasUserFolder(session, username));
      assertEquals(creationAssertionValue, Util.hasProfileFolder(session, username));

      Collection<?> memberships = organizationService.getMembershipHandler().findMembershipsByUser(username);
      if (creationAssertionValue) {// Related groups has to be
                                   // integrated/added, but when deleting
                                   // user, the group could still exists

        for (Object objectMembership : memberships) {
          assertEquals(creationAssertionValue, Util.hasMembershipFolder(session, (Membership) objectMembership));
        }

        @SuppressWarnings("unchecked")
        List<Group> groups = new ArrayList<Group>(organizationService.getGroupHandler().findGroupsOfUser(username));
        Collections.sort(groups, OrganizationIntegrationService.GROUP_COMPARATOR);
        for (Group group : groups) {
          assertEquals(creationAssertionValue, Util.hasGroupFolder(session, group.getId()));
        }
      } else {
        assertTrue(memberships == null || memberships.isEmpty());
      }
      if (organizationService instanceof ComponentRequestLifecycle) {
        ((ComponentRequestLifecycle) organizationService).endRequest(container);
      }
    } finally {
      if (session != null) {
        session.logout();
      }
    }
  }

  private void verifyFoldersCreation(boolean creationAssertionValue) throws Exception {
    Session session = null;
    try {
      session = repositoryService.getCurrentRepository().getSystemSession(Util.WORKSPACE);
      if (organizationService instanceof ComponentRequestLifecycle) {
        ((ComponentRequestLifecycle) organizationService).startRequest(container);
      }
      ListAccess<User> usersListAccess = organizationService.getUserHandler().findAllUsers();
      int i = 0;
      while (i <= usersListAccess.getSize()) {
        int length = i + 10 <= usersListAccess.getSize() ? 10 : usersListAccess.getSize() - i;
        User[] users = usersListAccess.load(i, length);
        for (User user : users) {
          assertEquals(creationAssertionValue, Util.hasUserFolder(session, user.getUserName()));
          UserProfile profile = organizationService.getUserProfileHandler().findUserProfileByName(user.getUserName());
          assertEquals(creationAssertionValue, Util.hasProfileFolder(session, profile.getUserName()));
          Collection<?> memberships = organizationService.getMembershipHandler().findMembershipsByUser(user.getUserName());
          for (Object objectMembership : memberships) {
            assertEquals(creationAssertionValue, Util.hasMembershipFolder(session, (Membership) objectMembership));
          }
        }
        i += 10;
      }
      @SuppressWarnings("unchecked")
      List<Group> groups = new ArrayList<Group>(organizationService.getGroupHandler().getAllGroups());
      Collections.sort(groups, OrganizationIntegrationService.GROUP_COMPARATOR);
      for (Group group : groups) {
        assertEquals(creationAssertionValue, Util.hasGroupFolder(session, group.getId()));
      }
      if (organizationService instanceof ComponentRequestLifecycle) {
        ((ComponentRequestLifecycle) organizationService).endRequest(container);
      }
    } finally {
      if (session != null) {
        session.logout();
      }
    }
  }

  private void verifyMembershipFoldersCreation(String username, String groupId, String membershipType, boolean assertionCondition)
      throws Exception {
    Session session = null;
    try {
      session = repositoryService.getCurrentRepository().getSystemSession(Util.WORKSPACE);
      MembershipImpl membership = new MembershipImpl();
      {
        membership.setMembershipType(membershipType);
        membership.setUserName(username);
        membership.setGroupId(groupId);
        membership.setId(Util.computeId(membership));
      }
      assertEquals(assertionCondition, Util.hasMembershipFolder(session, membership));
      session.save();
    } finally {
      if (session != null) {
        session.logout();
      }
    }
  }
}
