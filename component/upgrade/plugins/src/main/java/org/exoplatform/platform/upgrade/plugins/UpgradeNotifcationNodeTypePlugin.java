/*
 * Copyright (C) 2003-2014 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU Affero General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.platform.upgrade.plugins;

import java.io.InputStream;

import org.exoplatform.commons.upgrade.UpgradeProductPlugin;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.commons.version.util.VersionComparator;
import org.exoplatform.container.configuration.ConfigurationManager;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.nodetype.ExtendedNodeTypeManager;
import org.exoplatform.services.jcr.core.nodetype.NodeTypeDataManager;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;


public class UpgradeNotifcationNodeTypePlugin extends UpgradeProductPlugin {
  private static Log LOG      = ExoLogger.getLogger(UpgradeNotifcationNodeTypePlugin.class);
  
  public UpgradeNotifcationNodeTypePlugin(InitParams initParams) {
    super(initParams);
  }

  @Override
  public void processUpgrade(String oldVersion, String newVersion) {
    try {
      registerNodeTypes("jar:/conf/notification-nodetypes.xml", ExtendedNodeTypeManager.IGNORE_IF_EXISTS);
      //
      LOG.info(String.format("Successfully to migrate notification system from %s to %s", oldVersion, newVersion));
    } catch (Exception e) {
      LOG.warn(String.format("Failed to migrate notification system from %s to %s", oldVersion, newVersion), e);
    }
  }

  @Override
  public boolean shouldProceedToUpgrade(String newVersion, String previousVersion) {
    return VersionComparator.isAfter(newVersion, previousVersion);
  }
  
  private static void registerNodeTypes(String nodeTypeFilesName, int alreadyExistsBehaviour) throws Exception {
    ConfigurationManager configurationService = CommonsUtils.getService(ConfigurationManager.class);
    InputStream isXml = configurationService.getInputStream(nodeTypeFilesName);
    RepositoryService repositoryService = CommonsUtils.getService(RepositoryService.class);
    ExtendedNodeTypeManager ntManager = repositoryService.getCurrentRepository().getNodeTypeManager();
    ntManager.registerNodeTypes(isXml, alreadyExistsBehaviour, NodeTypeDataManager.TEXT_XML);
  }

}
