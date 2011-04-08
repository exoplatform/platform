package org.exoplatform.platform.upgrade.plugins;

import java.util.List;

import org.exoplatform.application.gadget.Gadget;
import org.exoplatform.application.gadget.GadgetRegistryService;
import org.exoplatform.application.gadget.SourceStorage;
import org.exoplatform.application.gadget.impl.GadgetRegistryServiceImpl;
import org.exoplatform.commons.chromattic.ChromatticLifeCycle;
import org.exoplatform.commons.upgrade.UpgradeProductPlugin;
import org.exoplatform.container.configuration.ConfigurationManager;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

public class UpgradeLocalGadgetsPlugin extends UpgradeProductPlugin {
  private static final Log log = ExoLogger.getLogger(UpgradeLocalGadgetsPlugin.class);

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
    log.info("processing upgrading gadgets from version " + oldVersion + " to " + newVersion);
    ChromatticLifeCycle lifeCycle = gadgetRegistryService.getChromatticLifeCycle();
    try {
      lifeCycle.openContext();

      for (GadgetUpgrade gadgetUpgrade : gadgets) {
        Gadget gadget = gadgetRegistryService.getGadget(gadgetUpgrade.getName());
        if (gadget == null) {
          log.warn("Can't upgrade gadget " + gadgetUpgrade.getName() + ". It doesn't exit.");
          continue;
        }
        log.info("Replacing gadget " + gadgetUpgrade.getName() + " with new content ...");

        gadgetRegistryService.getRegistry().removeGadget(gadgetUpgrade.getName());
        LocalGadgetImporter gadgetImporter = new LocalGadgetImporter(gadgetUpgrade.getName(),
            gadgetRegistryService.getRegistry(), gadgetUpgrade.getPath(), configurationManager);
        gadgetImporter.doImport();

        log.info("gadget " + gadgetUpgrade.getName() + " upgraded successfully.");
      }
    } catch (Exception exception) {
      log.error("Error while proceeding gadgets upgrade.", exception);
    } finally {
      if (lifeCycle != null) {
        lifeCycle.closeContext(true);
      }
    }
  }

  @Override
  public boolean shouldProceedToUpgrade(String previousVersion, String newVersion) {
    return true;
  }
}
