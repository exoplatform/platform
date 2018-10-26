package org.exoplatform.platform.upgrade.plugins;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.config.RepositoryEntry;
import org.exoplatform.services.jcr.core.ExtendedNode;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.ext.hierarchy.NodeHierarchyCreator;
import org.exoplatform.services.security.IdentityConstants;
import org.junit.Test;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import static org.mockito.Mockito.*;

public class SecureJCRFoldersUpgradePluginTest {

  @Test
  public void testSecureJCRFoldersMigration() throws RepositoryException {
    // Given
    NodeHierarchyCreator nodeHierarchyCreator = mock(NodeHierarchyCreator.class);
    ManageableRepository repo = mock(ManageableRepository.class);
    RepositoryEntry entry = mock(RepositoryEntry.class);
    when(repo.getConfiguration()).thenReturn(entry);

    RepositoryService repositoryService = mock(RepositoryService.class);
    when(repositoryService.getCurrentRepository()).thenReturn(repo);

    SessionProvider sessionProvider = mock(SessionProvider.class);
    Session session = mock(Session.class);
    when(sessionProvider.getSession(anyString(), any())).thenReturn(session);

    ExtendedNode rootNode = mock(ExtendedNode.class);
    when(session.getRootNode()).thenReturn(rootNode);

    ExtendedNode loginHistoryNode = mock(ExtendedNode.class);
    when(rootNode.hasNode("exo:LoginHistoryHome")).thenReturn(true);
    when(rootNode.getNode("exo:LoginHistoryHome")).thenReturn(loginHistoryNode);

    ExtendedNode usersNode = mock(ExtendedNode.class);
    when(nodeHierarchyCreator.getJcrPath("usersPath")).thenReturn("/Users");
    when(session.getItem("/Users")).thenReturn(usersNode);

    ExtendedNode gadgetsNode = mock(ExtendedNode.class);
    when(session.getItem("/production/app:gadgets")).thenReturn(gadgetsNode);

    SecureJCRFoldersUpgradePlugin plugin = new SecureJCRFoldersUpgradePlugin(repositoryService, nodeHierarchyCreator, new InitParams());
    plugin.setSessionProvider(sessionProvider);

    // When
    plugin.processUpgrade("5.1.0", "5.2.0");

    // Then
    verify(rootNode, times(1)).removePermission(IdentityConstants.ANY);
    verify(gadgetsNode, times(1)).removePermission(IdentityConstants.ANY);
    verify(usersNode, times(1)).removePermission("*:/platform/users");
    verify(loginHistoryNode, times(1)).removePermission("*:/platform/users");
  }
}
