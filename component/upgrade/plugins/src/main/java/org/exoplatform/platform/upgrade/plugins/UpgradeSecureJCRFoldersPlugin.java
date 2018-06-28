package org.exoplatform.platform.upgrade.plugins;

import org.exoplatform.commons.upgrade.UpgradeProductPlugin;
import org.exoplatform.commons.version.util.VersionComparator;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.access.PermissionType;
import org.exoplatform.services.jcr.core.ExtendedNode;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.IdentityConstants;

import javax.jcr.Node;
import javax.jcr.Session;
import java.util.HashMap;
import java.util.Map;

public class UpgradeSecureJCRFoldersPlugin extends UpgradeProductPlugin {

  private static final Log LOG = ExoLogger.getLogger(UpgradeSecureJCRFoldersPlugin.class.getName());

  private RepositoryService repoService;

  public UpgradeSecureJCRFoldersPlugin(InitParams initParams, RepositoryService repoService) {
    super(initParams);
    this.repoService = repoService;
  }

  @Override
  public void processUpgrade(String oldVersion, String newVersion) {
    SessionProvider sessionProvider = SessionProvider.createSystemProvider();
    try {
      Session session = sessionProvider.getSession("collaboration",
          repoService.getCurrentRepository());
      Node rootNode = session.getRootNode();
      if (rootNode.hasNode("Application Data")) {
        LOG.info("SecureJCRFolder - Proceed securing JCR folders for branding logo");
        Node applicationDataNode = rootNode.getNode("Application Data");
        Map<String, String[]> map = new HashMap<String, String[]>();
        map.put("*:/platform/administrators", PermissionType.ALL);
        ((ExtendedNode) applicationDataNode).setPermissions(map);

        if (applicationDataNode.hasNode("logos/logo.png")) {
          Node fileNode = applicationDataNode.getNode("logos/logo.png");
          if (fileNode.canAddMixin("exo:privilegeable")) {
            fileNode.addMixin("exo:privilegeable");
          }
          ((ExtendedNode) fileNode).setPermission(IdentityConstants.ANY, PermissionType.DEFAULT_AC);
        }
        session.save();
      }
    } catch (Exception e) {
      LOG.error("SecureJCRFolder - Error while migrating security for branding logo: ", e.getMessage());
    } finally {
      sessionProvider.close();
    }
  }

  @Override
  public boolean shouldProceedToUpgrade(String newVersion, String previousVersion) {
      return VersionComparator.isAfter(newVersion,previousVersion);
  }

}
