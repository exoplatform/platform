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

import java.util.ArrayList;

import org.exoplatform.commons.upgrade.UpgradeProductPlugin;
import org.exoplatform.commons.version.util.VersionComparator;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.config.model.Container;
import org.exoplatform.portal.config.model.ModelObject;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.mop.SiteKey;
import org.exoplatform.portal.mop.SiteType;
import org.exoplatform.portal.mop.page.PageKey;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Aug 15, 2014  
 * This plugin will remove redundant navigation bar in Answers page and FAQ page
 */
public class AnswerPageUpgradePlugin extends UpgradeProductPlugin {

  private static final Log LOG = ExoLogger.getLogger(AnswerPageUpgradePlugin.class.getName());
  private static final String INTRANET = "intranet";
  private static final String NAVIGATION = "UIUserNavigationPortlet";
  private DataStorage dataStorage;

  public AnswerPageUpgradePlugin(DataStorage dataStorage, InitParams initParams) {
    super(initParams);
    this.dataStorage = dataStorage;
  }

  @Override
  public void processUpgrade(String oldVersion, String newVersion) {
    if (LOG.isInfoEnabled()) {
      LOG.info("Start " + this.getClass().getName() + ".............");
    }
    try {
      RequestLifeCycle.begin(ExoContainerContext.getCurrentContainer());
      for (String pageName : new String[] {"answers", "faq"}) {
        SiteKey siteKey = new SiteKey(SiteType.PORTAL, INTRANET);
        PageKey pageKey = new PageKey(siteKey, pageName);
        Page page = dataStorage.getPage(pageKey.format());
        if (page == null) continue;
        ArrayList<ModelObject> children = page.getChildren();
        for(int i = 0; i < children.size(); i++) {
          if(NAVIGATION.equals(((Container)children.get(i)).getId())){
            page.getChildren().remove(i);
            if (LOG.isInfoEnabled()) {
              LOG.info(pageName + " removed!");
            }
            break;
          }
        }      
        dataStorage.save(page);
      }
      if (LOG.isInfoEnabled()) {
        LOG.info(this.getClass().getName() + " finished successfully!");
      }
    } catch (Exception e) {
      if (LOG.isErrorEnabled()) {
        LOG.error("An unexpected error occurs when migrating pages:", e);        
      }
    } finally {
      RequestLifeCycle.end();
    }
  }

  @Override
  public boolean shouldProceedToUpgrade(String newVersion, String previousVersion) {
    return VersionComparator.isAfter(newVersion,previousVersion);
  }
}
