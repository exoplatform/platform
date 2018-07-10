package org.exoplatform.platform.component.organization.test;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.configuration.ConfigurationManager;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.platform.organization.integration.*;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.organization.MembershipHandler;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.UserHandler;
import org.exoplatform.services.organization.externalstore.IDMExternalStoreService;
import org.exoplatform.services.organization.idm.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.picketlink.idm.api.AttributesManager;
import org.picketlink.idm.api.IdentitySession;
import org.picketlink.idm.api.PersistenceManager;
import org.picketlink.idm.impl.api.session.IdentitySessionImpl;
import org.picketlink.idm.impl.api.session.context.IdentitySessionContextImpl;
import org.picketlink.idm.impl.repository.RepositoryIdentityStoreSessionImpl;
import org.picketlink.idm.spi.store.IdentityStoreInvocationContext;
import org.picketlink.idm.spi.store.IdentityStoreSession;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.naming.ldap.LdapContext;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class TestOrganizationIntegration {
  PortalContainer     container           = null;

  RepositoryService   repositoryService   = null;

  OrganizationService organizationService = null;

  @Before
  public void setUp() throws Exception {
    container = PortalContainer.getInstance();
    repositoryService = container.getComponentInstanceOfType(RepositoryService.class);
    organizationService = container.getComponentInstanceOfType(OrganizationService.class);
  }

  @Test
  public void testIntegrationService() throws Exception {
    IDMExternalStoreService externalStoreService = Mockito.mock(IDMExternalStoreService.class);
    Mockito.when(externalStoreService.isEnabled()).thenReturn(false);
    container.registerComponentInstance(org.exoplatform.services.organization.externalstore.IDMExternalStoreService.class,
                                        externalStoreService);

    OrganizationIntegrationService organizationIntegrationService =
                                                                  container.createComponent(OrganizationIntegrationService.class);
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
    IdentitySessionContextImpl ctx = Mockito.mock(IdentitySessionContextImpl.class);
    Mockito.when(((IdentitySessionImpl) identitySession).getSessionContext()).thenReturn(ctx);
    IdentityStoreInvocationContext identityStoreInvocationContext = Mockito.mock(IdentityStoreInvocationContext.class);
    Mockito.when(ctx.resolveStoreInvocationContext()).thenReturn(identityStoreInvocationContext);
    RepositoryIdentityStoreSessionImpl repoSession = Mockito.mock(RepositoryIdentityStoreSessionImpl.class);
    Mockito.when((RepositoryIdentityStoreSessionImpl) identityStoreInvocationContext.getIdentityStoreSession())
           .thenReturn(repoSession);
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
    IDMExternalStoreService externalStoreService = Mockito.mock(IDMExternalStoreService.class);
    Mockito.when(externalStoreService.isEnabled()).thenReturn(false);
    OrganizationIntegrationService organizationIntegrationService =
                                                                  Mockito.spy(new OrganizationIntegrationService(organizationService,
                                                                                                                 externalStoreService,
                                                                                                                 repositoryService,
                                                                                                                 manager,
                                                                                                                 container,
                                                                                                                 initParams,
                                                                                                                 picketLinkIDMCacheService));
    PicketLinkIDMOrganizationServiceImpl picketLinkIDMOrganizationServiceImpl =
                                                                              Mockito.mock(PicketLinkIDMOrganizationServiceImpl.class);
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
    IDMExternalStoreService externalStoreService = Mockito.mock(IDMExternalStoreService.class);
    Mockito.when(externalStoreService.isEnabled()).thenReturn(false);
    OrganizationIntegrationService organizationIntegrationService =
                                                                  Mockito.spy(new OrganizationIntegrationService(organizationService,
                                                                                                                 externalStoreService,
                                                                                                                 repositoryService,
                                                                                                                 manager,
                                                                                                                 container,
                                                                                                                 initParams,
                                                                                                                 picketLinkIDMCacheService));
    PicketLinkIDMOrganizationServiceImpl picketLinkIDMOrganizationServiceImpl =
                                                                              Mockito.mock(PicketLinkIDMOrganizationServiceImpl.class);
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
    userDAO.removeUser(user.getUserName(), false);
    organizationIntegrationService.syncUser("exo", EventType.DELETED.toString());
    Mockito.verify(userListener, times(1)).preDelete(any());
    Mockito.verify(userListener, times(1)).postDelete(any());
  }

}
