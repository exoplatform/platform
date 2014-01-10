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
import org.exoplatform.commons.version.util.VersionComparator;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.access.PermissionType;
import org.exoplatform.services.jcr.core.ExtendedNode;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Jan 10, 2014  
 */
public class UpgradeExoLoginHistoryHome extends UpgradeProductPlugin {
  
  private static final String HOME = "exo:LoginHistoryHome";
  
  private static final String EXO_PRIVILEGEABLE = "exo:privilegeable";
  
  private static final String EXO_OWNABLE = "exo:owneable";
  
  private Log LOG = ExoLogger.getLogger(this.getClass().getName());
  
  private RepositoryService repoService_;

  public UpgradeExoLoginHistoryHome(RepositoryService repoService, InitParams initParams) {
    super(initParams);
    repoService_ = repoService;
  }

  @Override
  public void processUpgrade(String oldVersion, String newVersion) {
    if (LOG.isInfoEnabled()) {
      LOG.info("Starting " + this.getClass().getName() + " ............");
    }
    SessionProvider sProvider = null;
    try {
      sProvider = SessionProvider.createSystemProvider();
      Session session = sProvider.getSession(repoService_.getCurrentRepository().getConfiguration().getDefaultWorkspaceName(),
                                             repoService_.getCurrentRepository());
      Node rootNode = session.getRootNode();
      if (rootNode.hasNode(HOME)) {
        Node home = rootNode.getNode(HOME);
        if (home.canAddMixin(EXO_PRIVILEGEABLE)) {
          home.addMixin("exo:privilegeable");
          Map<String, String[]> permissions = new HashMap<String, String[]>();
          permissions.put("*:/platform/administrators", PermissionType.ALL);
          permissions.put("*:/platform/users", new String[]{PermissionType.READ});
          ((ExtendedNode)home).setPermissions(permissions);
          rootNode.save();
        }
        if (home.canAddMixin(EXO_OWNABLE)) {
          home.addMixin(EXO_OWNABLE);
          rootNode.save();
        }
      }
      if (LOG.isInfoEnabled()) {
        LOG.info("Upgrade node /exo:LoginHistoryHome succeeded");
      }
    } catch (Exception e) {
      if (LOG.isErrorEnabled()) {
        LOG.error("Upgrade node /exo:LoginHistoryHome failed!", e);
      }
    } finally {
      sProvider.close();
    }
  }

  @Override
  public boolean shouldProceedToUpgrade(String newVersion, String previousVersion) {
    return VersionComparator.isAfter(newVersion,previousVersion);
  }

}
