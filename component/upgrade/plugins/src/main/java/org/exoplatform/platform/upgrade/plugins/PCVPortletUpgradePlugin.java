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
import java.util.Iterator;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.query.Query;

import org.exoplatform.commons.upgrade.UpgradeProductPlugin;
import org.exoplatform.commons.version.util.VersionComparator;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.config.model.Application;
import org.exoplatform.portal.config.model.ApplicationType;
import org.exoplatform.portal.config.model.Container;
import org.exoplatform.portal.config.model.ModelObject;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.TransientApplicationState;
import org.exoplatform.portal.config.serialize.PortletApplication;
import org.exoplatform.portal.mop.page.PageContext;
import org.exoplatform.portal.mop.page.PageKey;
import org.exoplatform.portal.mop.page.PageService;
import org.exoplatform.portal.pom.spi.portlet.Portlet;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Aug 28, 2014
 * This upgrade plugin aims to change the name of ParameterizedContentViewerPortlet
 * to SingleContentViewer  
 */
public class PCVPortletUpgradePlugin extends UpgradeProductPlugin {
  
  private static final Log LOG = ExoLogger.getLogger(PCVPortletUpgradePlugin.class.getName());
  
  private RepositoryService repoService;
  
  public PCVPortletUpgradePlugin(RepositoryService repoService, InitParams initParams) {
    super(initParams);
    this.repoService = repoService;
  }

  @Override
  public void processUpgrade(String oldVersion, String newVersion) {
    if (LOG.isInfoEnabled()) {
      LOG.info("Start " + this.getClass().getName() + ".............");
    }
    SessionProvider sessionProvider = null;
    try {
      RequestLifeCycle.begin(ExoContainerContext.getCurrentContainer());
      sessionProvider = SessionProvider.createSystemProvider();
      Session session = sessionProvider.getSession("portal-system", repoService.getCurrentRepository());
      NodeIterator iter = session.getWorkspace().getQueryManager().
          createQuery("Select * from mop:workspaceclone where mop:contentid='presentation/ParameterizedContentViewerPortlet' ", Query.SQL).execute().getNodes();
      while (iter.hasNext()) {
        try {
          Node node = iter.nextNode();
          if (node.hasProperty("mop:contentid")) {
            node.setProperty("mop:contentid", "presentation/SingleContentViewer");
            if (node.hasNode("mop:state")) {
              Node mopState = node.getNode("mop:state");
              if (!mopState.hasNode("ContextEnable")) {
                Node contextEnable = mopState.addNode("ContextEnable", "mop:portletpreference");
                contextEnable.setProperty("mop:readonly", false);
                Value[] value = new Value[1];
                value[0] = session.getValueFactory().createValue("true");
                contextEnable.setProperty("mop:value", value);
              }
              if (!mopState.hasNode("mop:ParameterName")) {
                Node contextEnable = mopState.addNode("mop:ParameterName", "mop:portletpreference");
                contextEnable.setProperty("mop:readonly", false);
                Value[] value = new Value[1];
                value[0] = session.getValueFactory().createValue("content-id");
                contextEnable.setProperty("mop:value", value);
              }
              node.save();
              if (LOG.isInfoEnabled()) {
                LOG.info("Migrate node: " + node.getPath());
              }
            }
          }
        } catch (Exception e) {
          if (LOG.isErrorEnabled()) {
            LOG.error("An unexpected error occurs when modifying PCV portlets: ", e);        
          }
        }
      }
      if (LOG.isInfoEnabled()) {
        LOG.info(this.getClass().getName() + " finished successfully!");
      }
    } catch (Exception e) {
      if (LOG.isErrorEnabled()) {
        LOG.error("An unexpected error occurs when modifying PCV portlets: ", e);        
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
