package org.exoplatform.platform.upgrade.plugins;

import org.exoplatform.container.configuration.ConfigurationManager;
import org.exoplatform.commons.upgrade.UpgradeProductPlugin;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.impl.core.ExtendedNamespaceRegistry;


public class UpgradeNamespaceWebcontentPlugin extends UpgradeProductPlugin {

  private RepositoryService repositoryService;
  private ConfigurationManager configurationManager;

  public UpgradeNamespaceWebcontentPlugin(InitParams initParams, ConfigurationManager configurationManager, 
      RepositoryService repositoryService) throws Exception {

    super(initParams);
    this.configurationManager = configurationManager;
    this.repositoryService = repositoryService;
  }

  @Override
  public void processUpgrade(String oldVersion, String newVersion) {
    try {
      ExtendedNamespaceRegistry namespaceRegistry = (ExtendedNamespaceRegistry) repositoryService.getCurrentRepository().getNamespaceRegistry();
      String oldNamespaceURL = namespaceRegistry.getNamespaceURIByPrefix("cia");
      if(oldNamespaceURL.equalsIgnoreCase("http://www.bull.com/fr/aladinng/cia/1.0") == true)
      {
        // Unregister the namespace
        namespaceRegistry.unregisterNamespace("cia");
      }
    } catch (Exception e)
    {
      // Nothing to do
    }
  }

  @Override
  public boolean shouldProceedToUpgrade(String newVersion, String previousVersion) {
    // Only activate this plugin if target version is 3.5.5
    if(newVersion.contains("3.5.5")){
      return true;
    }
    return false;
  }
}
