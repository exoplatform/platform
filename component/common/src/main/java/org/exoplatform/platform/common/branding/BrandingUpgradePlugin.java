package org.exoplatform.platform.common.branding;

import org.exoplatform.commons.file.model.FileItem;
import org.exoplatform.commons.file.services.FileService;
import org.exoplatform.commons.persistence.impl.EntityManagerService;
import org.exoplatform.commons.upgrade.UpgradeProductPlugin;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.Session;
import java.io.InputStream;
import java.util.Date;

public class BrandingUpgradePlugin extends UpgradeProductPlugin {
  private static final Log    LOG                     = ExoLogger.getLogger(BrandingUpgradePlugin.class);

  private static final String path                    = "Application Data/logos/";

  private static final String logo_name               = "logo.png";

  private final String        WORKSPACE_COLLABORATION = "collaboration";

  private static final String FILE_API_NAME_SPACE     = "CompanyBranding";

  private RepositoryService   repositoryService;

  private FileService         fileService;

  EntityManagerService        entityManagerService;

  public BrandingUpgradePlugin(InitParams initParams,
                               RepositoryService repositoryService,
                               EntityManagerService entityManagerService,

                               FileService fileService)

  {
    super(initParams);
    this.repositoryService = repositoryService;
    this.fileService = fileService;
    this.entityManagerService = entityManagerService;
  }

  @Override
  public void processUpgrade(String newVersion, String previousVersion) {
    SessionProvider sProvider = SessionProvider.createSystemProvider();

    if (!hasDataToMigrate()) {
      LOG.info("== No Branding data to migrate from JCR to File Storage");
    } else {
      LOG.info("== Start migration of Branding data from JCR to File Storage");
      try {
        Session session = getSession(sProvider);
        Node rootNode = session.getRootNode();
        String logoCreator = "";
        long fileSize = 0;
        if (rootNode.hasNode(path)) {
          Node logosNode = rootNode.getNode(path);
          if (logosNode.hasNode(logo_name)) {
            Node logoNode = logosNode.getNode(logo_name);
            // && logoNode.isNodeType("nt:file")
            if (logoNode.hasNode("jcr:content")) {
              Node logoContent = logoNode.getNode("jcr:content");
              Property data = logoContent.getProperty("jcr:data");
              if (logoNode.hasProperty("exo:lastModifier")) {
                logoCreator = logoNode.getProperty("exo:lastModifier").getString();
              }
              // should use branding service instead!
              entityManagerService.startRequest(ExoContainerContext.getCurrentContainer());
              InputStream inputStream = data.getStream();
              fileSize = data.getLength();
              FileItem fileItem = new FileItem(null,
                                               logo_name,
                                               "image/png",
                                               FILE_API_NAME_SPACE,
                                               fileSize,
                                               new Date(),
                                               logoCreator,
                                               false,
                                               inputStream);
              fileItem = fileService.writeFile(fileItem);

            }
            logoNode.remove();
            session.save();
          }

        }
      } catch (Exception e) {
        throw new RuntimeException("== Branding Data migration - Error when migrate logo to File Storage", e);
      } finally {
        entityManagerService.endRequest(ExoContainerContext.getCurrentContainer());
        sProvider.close();
      }
      if (!removeLogoHome()) {
        throw new RuntimeException("== Branding Data migration - Error when remove logo from JCR");
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
      LOG.error("Error while checking the existence of branding data", e);
    } finally {
      sProvider.close();
    }
    return hasDataToMigrate;
  }

  private boolean removeLogoHome() {
    SessionProvider sProvider = SessionProvider.createSystemProvider();
    boolean status = true;
    try {
      Session session = getSession(sProvider);
      Node rootNode = session.getRootNode();

      if (rootNode.hasNode(path)) {
        rootNode.getNode(path).remove();
      }
    } catch (Exception e) {
      LOG.error("Error while removing the logo home node", e);
      status = false;
    } finally {
      sProvider.close();
      return status;
    }

  }

}
