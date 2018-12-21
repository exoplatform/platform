package org.exoplatform.platform.upgrade.plugins;

import org.exoplatform.commons.upgrade.UpgradeProductPlugin;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.ExtendedNode;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.ext.hierarchy.NodeHierarchyCreator;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.IdentityConstants;

import javax.jcr.Node;
import javax.jcr.Session;

public class SecureJCRFoldersUpgradePlugin extends UpgradeProductPlugin {

  private static final Log LOG = ExoLogger.getLogger(SecureJCRFoldersUpgradePlugin.class.getName());
  private static final String HOME = "exo:LoginHistoryHome";

  private RepositoryService repoService;
  private NodeHierarchyCreator nodeHierarchyCreator;
  private SessionProvider sessionProvider;

  public SecureJCRFoldersUpgradePlugin(RepositoryService repoService, NodeHierarchyCreator nodeHierarchyCreator, InitParams initParams) {
    super(initParams);
    this.repoService = repoService;
    this.nodeHierarchyCreator = nodeHierarchyCreator;
  }

  @Override
  public void processUpgrade(String oldVersion, String newVersion) {
    SessionProvider sessionProvider = SessionProvider.createSystemProvider();

    migrateCollaboration(sessionProvider);

    migrateGadgets(sessionProvider);

    migrateUsers(sessionProvider);

    migrateLoginHistory(sessionProvider);

    sessionProvider.close();
  }

  // Remove public access permission from root node of collaboration workspace
  private void migrateCollaboration(SessionProvider sessionProvider) {
    try {
      String ws = repoService.getCurrentRepository().getConfiguration().getDefaultWorkspaceName();
      Session session = sessionProvider.getSession(ws, repoService.getCurrentRepository());

      Node node = session.getRootNode();
      ((ExtendedNode) node).removePermission(IdentityConstants.ANY);
      session.save();
    } catch (Exception e) {
      if (LOG.isErrorEnabled()) {
        LOG.error("An unexpected error occurs when migrate Collaboration workspace", e);
      }
    }
  }

  // Remove public access permission from gadgets node
  private void migrateGadgets(SessionProvider sessionProvider) {
    try {
      Session session = sessionProvider.getSession("portal-system", repoService.getCurrentRepository());

      Node node = (Node)session.getItem("/production/app:gadgets");
      ((ExtendedNode) node).removePermission(IdentityConstants.ANY);
      session.save();
    } catch (Exception e) {
      if (LOG.isErrorEnabled()) {
        LOG.error("An unexpected error occurs when migrate /production/app:gadgets", e);
      }
    }
  }

  // Remove normal users access permission from /Users node
  private void migrateUsers(SessionProvider sessionProvider) {
    try {
      String ws = repoService.getCurrentRepository().getConfiguration().getDefaultWorkspaceName();
      Session session = sessionProvider.getSession(ws, repoService.getCurrentRepository());

      Node groups = (Node)session.getItem(nodeHierarchyCreator.getJcrPath("usersPath"));
      ((ExtendedNode) groups).removePermission("*:/platform/users");
      session.save();
    } catch (Exception e) {
      if (LOG.isErrorEnabled()) {
        LOG.error("An unexpected error occurs when migrate /Users", e);
      }
    }
  }

  // Remove normal users access permission from login history home node
  private void migrateLoginHistory(SessionProvider sessionProvider) {
    try {
      String ws = repoService.getCurrentRepository().getConfiguration().getDefaultWorkspaceName();
      Session session = sessionProvider.getSession(ws, repoService.getCurrentRepository());

      Node rootNode = session.getRootNode();
      if (rootNode.hasNode(HOME)) {
        Node node = rootNode.getNode(HOME);
        ((ExtendedNode) node).removePermission("*:/platform/users");
      }
      session.save();
    } catch (Exception e) {
      if (LOG.isErrorEnabled()) {
        LOG.error("An unexpected error occurs when migrate /exo:LoginHistoryHome", e);
      }
    }
  }
}
