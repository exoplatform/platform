/**
 * Copyright (C) 2012 eXo Platform SAS.
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

import org.exoplatform.commons.upgrade.UpgradeProductPlugin;
import org.exoplatform.commons.version.util.VersionComparator;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.nodetype.ExtendedNodeTypeManager;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;


public class UpgradeNodeTypeSpaceAccessPlugin extends UpgradeProductPlugin {

    private static final Log LOG = ExoLogger.getLogger(UpgradeNodeTypeSpaceAccessPlugin.class);
    private RepositoryService repositoryService;

    public UpgradeNodeTypeSpaceAccessPlugin(InitParams initParams, RepositoryService repositoryService) throws Exception {

        super(initParams);
        this.repositoryService = repositoryService;
    }

    @Override
    public void processUpgrade(String oldVersion, String newVersion) {

        LOG.info("Start UpgradeNodeTypeSpaceAccessPlugin ...");

        try {

           ExtendedNodeTypeManager nodeTypeManager = (ExtendedNodeTypeManager) repositoryService.getCurrentRepository().getNodeTypeManager();

            try {

                nodeTypeManager.unregisterNodeType("plf:spaceaccess");

            }  catch (Exception e) {

                LOG.info("plf:spaceaccess does not exist...");

            }

            LOG.info("Finish UpgradeNodeTypeSpaceAccessPlugin ...");


        }  catch (Exception e) {

            LOG.error("UpgradeNodeTypeSpaceAccessPlugin: Upgrade Space nodeTypes failure", e);

        }
    }

    @Override
    public boolean shouldProceedToUpgrade(String newVersion, String previousVersion) {
        // --- return true anly for the first version of platform
        return VersionComparator.isAfter(newVersion,previousVersion);
    }

}
