package org.exoplatform.platform.upgrade.plugins;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.config.RepositoryEntry;
import org.exoplatform.services.jcr.core.ExtendedNode;
import org.exoplatform.services.jcr.core.ExtendedSession;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.hierarchy.NodeHierarchyCreator;
import org.exoplatform.services.security.IdentityConstants;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class PLFSecureJCRFoldersUpgradePluginTest {

  @Test
  public void testSecureJCRFoldersMigration() throws Exception {
    // Given
    NodeHierarchyCreator nodeHierarchyCreator = mock(NodeHierarchyCreator.class);
    RepositoryEntry entry = mock(RepositoryEntry.class);
    when(entry.getDefaultWorkspaceName()).thenReturn("collaboration");

    ExtendedSession session = mock(ExtendedSession.class);
    ManageableRepository repo = mock(ManageableRepository.class);
    when(repo.getConfiguration()).thenReturn(entry);
    when(repo.getSystemSession(any())).thenReturn(session);

    RepositoryService repositoryService = mock(RepositoryService.class);
    when(repositoryService.getCurrentRepository()).thenReturn(repo);


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

    // When
    PLFSecureJCRFoldersUpgradePlugin plugin = new PLFSecureJCRFoldersUpgradePlugin(repositoryService, nodeHierarchyCreator, new InitParams());
    plugin.processUpgrade("5.1.0", "5.2.0");

    // Then
    verify(rootNode, times(1)).removePermission(IdentityConstants.ANY);
    verify(gadgetsNode, times(1)).removePermission(IdentityConstants.ANY);
    verify(usersNode, times(1)).removePermission("*:/platform/users");
    verify(loginHistoryNode, times(1)).removePermission("*:/platform/users");
  }
}
