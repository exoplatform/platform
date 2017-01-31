/**
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.platform.upgrade.plugins;

import java.util.List;

import org.exoplatform.application.gadget.Gadget;
import org.exoplatform.application.gadget.GadgetImporter;
import org.exoplatform.application.gadget.GadgetRegistryService;
import org.exoplatform.application.gadget.ServletLocalImporter;
import org.exoplatform.application.gadget.SourceStorage;
import org.exoplatform.application.gadget.impl.GadgetDefinition;
import org.exoplatform.application.gadget.impl.GadgetRegistryServiceImpl;
import org.exoplatform.application.registry.impl.ApplicationRegistryChromatticLifeCycle;
import org.exoplatform.commons.chromattic.ChromatticLifeCycle;
import org.exoplatform.commons.chromattic.ChromatticManager;
import org.exoplatform.commons.upgrade.UpgradeProductPlugin;
import org.exoplatform.commons.version.util.VersionComparator;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.PortalContainer;
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
    
    private ChromatticLifeCycle chromatticLifeCycle;

    public UpgradeLocalGadgetsPlugin(ConfigurationManager configurationManager, RepositoryService repositoryService,
                                     SourceStorage sourceStorage, GadgetRegistryService gadgetRegistryService, InitParams initParams, ChromatticManager chromatticManager) {
        super(initParams);
        this.gadgets = initParams.getObjectParamValues(GadgetUpgrade.class);
        this.repositoryService = repositoryService;
        this.configurationManager = configurationManager;
        this.sourceStorage = sourceStorage;
        this.gadgetRegistryService = (GadgetRegistryServiceImpl) gadgetRegistryService;
        
        ApplicationRegistryChromatticLifeCycle lifeCycle = (ApplicationRegistryChromatticLifeCycle) chromatticManager
            .getLifeCycle("app");
        this.chromatticLifeCycle = lifeCycle;
    }

    @Override
    public void processUpgrade(String oldVersion, String newVersion) {
        LOG.info("processing upgrading gadgets from version " + oldVersion + " to " + newVersion);
        try {
            for (GadgetUpgrade gadgetUpgrade : gadgets) {
                boolean done = true;
                chromatticLifeCycle.openContext();
                try {
                    Gadget gadget = gadgetRegistryService.getGadget(gadgetUpgrade.getName());
                    if (gadget == null) {
                        LOG.warn("Can't find gadget '" + gadgetUpgrade.getName() + "'.");
                    }
                    else{
                      LOG.info("Start upgrading gadget " + gadgetUpgrade.getName() + " ...");
                      try {
                          gadgetRegistryService.removeGadget(gadgetUpgrade.getName());
                          LOG.info("Upgrade of Gadget " + gadgetUpgrade.getName() + " is delegated to GadgetDeployer");
                      } catch (Exception noSuchGadgetException) {
                          // if gadget doesn't exist
                          if (LOG.isDebugEnabled()) {
                              LOG.debug("gadget doesn't exist in the store: " + gadget.getName());
                          }
                      }
                    }

                } catch (Exception exception) {
                    done = false;
                    LOG.error("Error while proceeding '" + gadgetUpgrade.getName() + "' gadget upgrade.", exception);
                }
                finally{
                    chromatticLifeCycle.closeContext(done);
                }

                //--- GadgetRegistry sanitization
                if (!done) {
                    //--- invoke the sanitization process only when the import is not done as expected
                    sanitizeGadgetRegistry(gadgetUpgrade.getName());

                }

            }
        } catch (Exception e) {
            LOG.error("Could not upgrade local gadget", e);
        }
    }

    @Override
    public boolean shouldProceedToUpgrade(String newVersion, String previousVersion) {
        // --- return true only for the first version of platform
        return VersionComparator.isAfter(newVersion, previousVersion);
    }

    /**
     * When gadget is not imported correctly by the upgrade plugin
     * then remove it from GadgetRegistry and delegate the import process to GadgetDeployer
     * @param gadgetName
     */
    private void sanitizeGadgetRegistry (String gadgetName) throws IllegalArgumentException {

        chromatticLifeCycle.openContext();

        try {
            Gadget gadget = gadgetRegistryService.getGadget(gadgetName);
            if (gadget == null) {
                LOG.debug("The gadget '" + gadgetName + "' doesn't exist in GadgetRegistry store.");
            }   else{
                LOG.info("Sanitize the  gadget '" + gadgetName + "' in the GadgetRegistry store.");
                try {
                    gadgetRegistryService.removeGadget(gadgetName);
                } catch (Exception noSuchGadgetException) {
                    // if gadget doesn't exist
                    if (LOG.isDebugEnabled()) {
                        LOG.error("Exception occurs during the sanitization of the Gadgetregistry [TARGET GADGET : " + gadget.getName()+"]");
                    }
                }
            }

        } finally {
            chromatticLifeCycle.closeContext(true);
        }

    }

}