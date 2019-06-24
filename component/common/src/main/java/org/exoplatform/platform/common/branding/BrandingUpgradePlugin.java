package org.exoplatform.platform.common.branding;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.Session;

import org.apache.commons.io.IOUtils;

import org.exoplatform.commons.upgrade.UpgradeProductPlugin;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

public class BrandingUpgradePlugin extends UpgradeProductPlugin {
  private static final Log    LOG                     = ExoLogger.getLogger(BrandingUpgradePlugin.class);

  private static final String path                    = "Application Data/logos/";

  private static final String logo_name               = "logo.png";

  private static final String WORKSPACE_COLLABORATION = "collaboration";

  private RepositoryService   repositoryService;

  private BrandingService brandingService;

  public BrandingUpgradePlugin(InitParams initParams,
                               RepositoryService repositoryService,
                               BrandingService brandingService)

  {
    super(initParams);
    this.repositoryService = repositoryService;
    this.brandingService = brandingService;
  }

  @Override
  public void processUpgrade(String newVersion, String previousVersion) {
    SessionProvider sProvider = SessionProvider.createSystemProvider();

    if (!hasDataToMigrate()) {
      LOG.info("== No Branding data to migrate from JCR");
    } else {
      LOG.info("== Start migration of Branding data from JCR");
      try {
        Session session = getSession(sProvider);
        Node rootNode = session.getRootNode();
        if (rootNode.hasNode(path)) {
          Node logosNode = rootNode.getNode(path);
          if (logosNode.hasNode(logo_name)) {
            Node logoNode = logosNode.getNode(logo_name);
            if (logoNode.hasNode("jcr:content")) {
              Node logoContent = logoNode.getNode("jcr:content");
              Property data = logoContent.getProperty("jcr:data");

              Logo logo = new Logo();
              byte[] logoData = IOUtils.toByteArray(data.getStream());
              logo.setData(logoData);
              logo.setSize(logoData.length);
              brandingService.uploadLogo(logo);
            }
            logoNode.remove();
            session.save();
          }

          removeLogoHome();
        }
      } catch (Exception e) {
        throw new RuntimeException("== Branding Data migration - Error when migrating logo from JCR", e);
      } finally {
        sProvider.close();
      }

      LOG.info("==    Branding Data migration - Branding Data migration successfully !");
      LOG.info("== Migration Branding Data done");
    }

  }

  private Session getSession(SessionProvider sessionProvider) throws Exception {
    ManageableRepository currentRepo = this.repositoryService.getCurrentRepository();
    return sessionProvider.getSession(WORKSPACE_COLLABORATION, currentRepo);
  }

  private Boolean hasDataToMigrate() {
    boolean hasDataToMigrate = false;
    SessionProvider sProvider = SessionProvider.createSystemProvider();
    try {
      Session session = getSession(sProvider);
      Node rootNode = session.getRootNode();

      if (rootNode.hasNode(path)) {
        Node logosNode = rootNode.getNode(path);
        if (logosNode.hasNode(logo_name)) {
          hasDataToMigrate = true;

        }
      }
    } catch (Exception e) {
      LOG.error("Error while checking the existence of branding data in JCR", e);
    } finally {
      sProvider.close();
    }
    return hasDataToMigrate;
  }

  private void removeLogoHome() throws Exception {
    SessionProvider sProvider = SessionProvider.createSystemProvider();
    try {
      Session session = getSession(sProvider);
      Node rootNode = session.getRootNode();

      if (rootNode.hasNode(path)) {
        rootNode.getNode(path).remove();
      }
    } finally {
      sProvider.close();
    }

  }

}
