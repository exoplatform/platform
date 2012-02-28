package org.exoplatform.platform.upgrade.plugins;

import java.util.List;

import org.exoplatform.application.gadget.Gadget;
import org.exoplatform.application.gadget.GadgetRegistryService;
import org.exoplatform.application.gadget.SourceStorage;
import org.exoplatform.application.gadget.impl.GadgetDefinition;
import org.exoplatform.application.gadget.impl.GadgetRegistryServiceImpl;
import org.exoplatform.commons.upgrade.UpgradeProductPlugin;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.container.configuration.ConfigurationManager;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

public class UpgradeLocalGadgetsPlugin extends UpgradeProductPlugin {
  private static final Log LOG = ExoLogger.getLogger(UpgradeLocalGadgetsPlugin.class);

  private List<GadgetUpgrade> gadgets;

  protected RepositoryService repositoryService;

  protected ConfigurationManager configurationManager;

  protected SourceStorage sourceStorage;

  protected GadgetRegistryServiceImpl gadgetRegistryService;

  public UpgradeLocalGadgetsPlugin(ConfigurationManager configurationManager, RepositoryService repositoryService,
      SourceStorage sourceStorage, GadgetRegistryService gadgetRegistryService, InitParams initParams) {
    super(initParams);
    this.gadgets = initParams.getObjectParamValues(GadgetUpgrade.class);
    this.repositoryService = repositoryService;
    this.configurationManager = configurationManager;
    this.sourceStorage = sourceStorage;
    this.gadgetRegistryService = (GadgetRegistryServiceImpl) gadgetRegistryService;
  }

  @Override
  public void processUpgrade(String oldVersion, String newVersion) {
    LOG.info("processing upgrading gadgets from version " + oldVersion + " to " + newVersion);
    RequestLifeCycle.begin(PortalContainer.getInstance());
    try {
      for (GadgetUpgrade gadgetUpgrade : gadgets) {
        try {
          Gadget gadget = gadgetRegistryService.getGadget(gadgetUpgrade.getName());
          if (gadget == null) {
            LOG.warn("Can't find gadget '" + gadgetUpgrade.getName() + "'.");
            continue;
          }
          LOG.info("Replacing gadget " + gadgetUpgrade.getName() + " with new content ...");

          try {
            gadgetRegistryService.removeGadget(gadgetUpgrade.getName());
          } catch (Exception noSuchGadgetException) {
            // if gadget doesn't exist
            if (LOG.isDebugEnabled()) {
              LOG.debug("gadget doesn't exist in the store: " + gadget.getName());
            }
          }

          try {
            LocalGadgetImporter gadgetImporter = new LocalGadgetImporter(gadgetUpgrade.getName(), gadgetRegistryService,
                gadgetUpgrade.getPath(), configurationManager, PortalContainer.getInstance());

            GadgetDefinition def = gadgetRegistryService.getRegistry().addGadget(gadget.getName());
            gadgetImporter.doImport(def);

            gadget = gadgetRegistryService.getGadget(gadgetUpgrade.getName());
            if (gadget != null) {
              LOG.info("gadget " + gadgetUpgrade.getName() + " upgraded successfully.");
            } else {
              LOG.info("Gadget " + gadgetUpgrade.getName()
                  + " wasn't imported. It will be imported automatically with GadgetDeployer Service.");
            }
          } catch (Exception exception) {
            LOG.info("Gadget " + gadgetUpgrade.getName()
                + " wasn't imported. It will be imported automatically with GadgetDeployer Service.");
          }
        } catch (Exception exception) {
          LOG.error("Error while proceeding '" + gadgetUpgrade.getName() + "' gadget upgrade.", exception);
        }
      }
    } catch (Exception e) {
      LOG.error("Could not upgrade local gadget", e);
    } finally {
      RequestLifeCycle.end();
    }
  }

  @Override
  public boolean shouldProceedToUpgrade(String previousVersion, String newVersion) {
    return true;
  }
}
