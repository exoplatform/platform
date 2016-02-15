/*
 * Copyright (C) 2003-2014 eXo Platform SAS.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.exoplatform.platform.upgrade.plugins;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Session;

import org.exoplatform.commons.upgrade.UpgradeProductPlugin;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.access.PermissionType;
import org.exoplatform.services.jcr.core.ExtendedNode;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.ext.hierarchy.NodeHierarchyCreator;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.IdentityConstants;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * May 22, 2014
 * 
 *   This plugin remove any:read permission from node <br>
 *   /Application Data/ProductInformationsService/productVersionDeclarationNode <br>
 *   and allow only __system and administrators to access this node
 */
public class UpgradeProductInfoNodePlugin extends UpgradeProductPlugin {
  
  private static final Log LOG = ExoLogger.getLogger(UpgradeProductInfoNodePlugin.class.getName());
  
  /**
   * Constant that will be used in nodeHierarchyCreator.getJcrPath: it
   * represents the Application data root node Alias
   */
  private static final String EXO_APPLICATIONS_DATA_NODE_ALIAS = "exoApplicationDataNode";
  
  /**
   * Service application data node name
   */
  private static final String UPGRADE_PRODUCT_SERVICE_NODE_NAME = "ProductInformationsService";

  /**
   * node name where the Product version declaration is
   */
  private static final String PRODUCT_VERSION_DECLARATION_NODE_NAME = "productVersionDeclarationNode";
  
  private RepositoryService repoService;
  private NodeHierarchyCreator nodeHierarchyCreator;
  private UserACL userAcl;
  
  public UpgradeProductInfoNodePlugin(InitParams initParams, RepositoryService repoService,
                                      NodeHierarchyCreator nodeHierarchyCreator, UserACL userAcl) {
    super(initParams);
    this.repoService = repoService;
    this.nodeHierarchyCreator = nodeHierarchyCreator;
    this.userAcl = userAcl;
  }

  @Override
  public void processUpgrade(String oldVersion, String newVersion) {
    SessionProvider sessionProvider = null;
    LOG.info("processing upgrading product info node...");
    try {
      String applicationDataRootNodePath = nodeHierarchyCreator.getJcrPath(EXO_APPLICATIONS_DATA_NODE_ALIAS);
      
      sessionProvider = SessionProvider.createSystemProvider();
      String workspace = repoService.getCurrentRepository().getConfiguration().getDefaultWorkspaceName();
      Session session = sessionProvider.getSession(workspace, repoService.getCurrentRepository());
      Node applicationDataNode = (Node)session.getItem(applicationDataRootNodePath);
      Node productVersionDeclarationNode = applicationDataNode.getNode(UPGRADE_PRODUCT_SERVICE_NODE_NAME + "/"
          + PRODUCT_VERSION_DECLARATION_NODE_NAME);
      ExtendedNode extendedNode = (ExtendedNode)productVersionDeclarationNode;
      if (extendedNode.canAddMixin("exo:privilegeable")) {
        extendedNode.addMixin("exo:privilegeable");
      }
      Map<String, String[]> perms = new HashMap<String, String[]>();
      perms.put(IdentityConstants.SYSTEM, PermissionType.ALL);
      perms.put("*:" + userAcl.getAdminGroups(), PermissionType.ALL);
      extendedNode.setPermissions(perms);
      extendedNode.save();
      LOG.info("Product info node upgraded successfully!");
    } catch (Exception e){
      if (LOG.isWarnEnabled()) {
        LOG.warn("Can not upgrade product info node", e);
      }
    } finally {
      if (sessionProvider != null) {
        sessionProvider.close();
      }
    }
  }

  @Override
  public boolean shouldProceedToUpgrade(String newVersion, String previousVersion) {
      // --- return true only for the first version of platform
      //return VersionComparator.isAfter(newVersion,previousVersion);
      return true;
  }

}
