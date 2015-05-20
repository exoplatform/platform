/*
 * Copyright (C) 2003-2015 eXo Platform SAS.
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

import java.util.Date;

import org.exoplatform.commons.upgrade.UpgradeProductPlugin;
import org.exoplatform.commons.version.util.VersionComparator;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.mop.SiteKey;
import org.exoplatform.portal.mop.SiteType;
import org.exoplatform.portal.mop.importer.Imported;
import org.exoplatform.portal.mop.importer.Imported.Status;
import org.exoplatform.portal.mop.page.PageKey;
import org.exoplatform.portal.mop.page.PageService;
import org.exoplatform.portal.pom.config.POMSession;
import org.exoplatform.portal.pom.config.POMSessionManager;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.gatein.mop.api.workspace.Workspace;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * May 15, 2015  
 */
public class ProfilePageUpgradePlugin extends UpgradeProductPlugin {

  private static final Log LOG = ExoLogger.getLogger(ProfilePageUpgradePlugin.class.getName());
  private static final String INTRANET = "intranet";
  private static final String PROFILE_PAGE = "profile";
  private DataStorage dataStorage;
  private UserPortalConfigService portalConfigService;
  private final POMSessionManager pomMgr;
  private PageService pageService;

  public ProfilePageUpgradePlugin(UserPortalConfigService portalConfigService, POMSessionManager pomMgr, DataStorage dataStorage, PageService pageService, InitParams initParams) {
    super(initParams);
    this.dataStorage = dataStorage;
    this.portalConfigService = portalConfigService;
    this.pomMgr = pomMgr;
    this.pageService = pageService;
  }

  @Override
  public void processUpgrade(String oldVersion, String newVersion) {
    if (LOG.isInfoEnabled()) {
      LOG.info("Start " + this.getClass().getName() + ".............");
    }
    try {
      RequestLifeCycle.begin(ExoContainerContext.getCurrentContainer());
        SiteKey siteKey = new SiteKey(SiteType.PORTAL, INTRANET);
        PageKey pageKey = new PageKey(siteKey, PROFILE_PAGE);
        Page page = dataStorage.getPage(pageKey.format());
        if (page == null) return;
        pageService.destroyPage(pageKey);
        
        if (LOG.isInfoEnabled()) {
          LOG.info(PROFILE_PAGE + " page has been removed!");
        }
        
        POMSession session = pomMgr.getSession();
        Workspace workspace = session.getWorkspace();
        Imported imported = workspace.adapt(Imported.class);
        imported.setLastModificationDate(new Date());
        imported.setStatus(Status.WANT_REIMPORT.status());
        session.save();
        LOG.info("Import status updated successfully!");
    } catch (Exception e) {
      if (LOG.isErrorEnabled()) {
        LOG.error("An unexpected error occurs when migrating pages:", e);        
      }
    } finally {
      RequestLifeCycle.end();
    }
    
    try {
      LOG.info("Starts to reimport portal configuration....");
      
      portalConfigService.start();
      if (LOG.isInfoEnabled()) {
        LOG.info(this.getClass().getName() + " finished successfully!");
      }

    } catch (Exception e) {
      if (LOG.isErrorEnabled()) {
        LOG.error("An unexpected error occurs when migrating pages:", e);
      }
    }
  }

  @Override
  public boolean shouldProceedToUpgrade(String newVersion, String previousVersion) {
    return VersionComparator.isAfter(newVersion,previousVersion);
  }
}