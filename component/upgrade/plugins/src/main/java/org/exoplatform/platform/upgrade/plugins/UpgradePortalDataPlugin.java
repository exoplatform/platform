/**
 * Copyright (C) 2019 eXo Platform SAS.
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
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.mop.importer.Imported;
import org.exoplatform.portal.mop.importer.Imported.Status;
import org.exoplatform.portal.pom.config.POMSession;
import org.exoplatform.portal.pom.config.POMSessionManager;
import org.gatein.mop.api.workspace.Workspace;

import java.util.Date;

public class UpgradePortalDataPlugin extends UpgradeProductPlugin {

  private UserPortalConfigService portalConfigService;

  private final POMSessionManager pomMgr;

  public UpgradePortalDataPlugin(UserPortalConfigService portalConfigService, POMSessionManager pomMgr, InitParams initParams) {
    super(initParams);
    this.portalConfigService = portalConfigService;
    this.pomMgr = pomMgr;
  }

  @Override
  public void processUpgrade(String oldVersion, String newVersion) {
    RequestLifeCycle.begin(PortalContainer.getInstance());
    try {
      POMSession session = pomMgr.getSession();
      Workspace workspace = session.getWorkspace();
      Imported imported = workspace.adapt(Imported.class);
      imported.setLastModificationDate(new Date());
      imported.setStatus(Status.WANT_REIMPORT.status());
      session.save();
    } finally {
      RequestLifeCycle.end();
    }
    portalConfigService.start();
  }
}
