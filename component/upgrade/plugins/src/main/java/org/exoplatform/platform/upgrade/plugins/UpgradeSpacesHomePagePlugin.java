package org.exoplatform.platform.upgrade.plugins;

/*
 * Copyright (C) 2003-2011 eXo Platform SAS.
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

import java.util.Collection;
import java.util.List;

import org.exoplatform.commons.upgrade.UpgradeProductPlugin;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.config.Query;
import org.exoplatform.portal.config.model.Application;
import org.exoplatform.portal.config.model.ApplicationType;
import org.exoplatform.portal.config.model.Container;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.TransientApplicationState;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.GroupHandler;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.social.core.space.SpaceUtils;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.storage.api.SpaceStorage;
import org.gatein.pc.api.Portlet;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain
 *         Defrance</a>
 * @version $Revision$
 */
public class UpgradeSpacesHomePagePlugin extends UpgradeProductPlugin {

  private final OrganizationService service;
  private final SpaceStorage spaceStorage;

  private final String UI_SIMPLE_TEMPLATE = "system:/groovy/portal/webui/container/UIContainer.gtmpl";
  private final String UI_TABLE_COLUMN_TEMPLATE = "system:/groovy/portal/webui/container/UITableColumnContainer.gtmpl";

  private final String PORTLET_SUMMARY = "acme-intranet-portlet/SpaceSummaryInfoPortlet";
  private final String PORTLET_DETAILS = "presentation/SingleContentViewer";

  private static final Log LOG = ExoLogger.getLogger(UpgradeSpacesHomePagePlugin.class);

  public UpgradeSpacesHomePagePlugin(OrganizationService organizationService, SpaceStorage spaceStorage, InitParams initParams) {
    super(initParams);
    this.service = organizationService;
    this.spaceStorage = spaceStorage;
  }

  @Override
  public void processUpgrade(String oldVersion, String newVersion) {
    try {
      RequestLifeCycle.begin(PortalContainer.getInstance());

      GroupHandler groupHandler = service.getGroupHandler();
      Group spaces = groupHandler.findGroupById("/spaces");
      Collection<Group> groups = groupHandler.findGroups(spaces);

      for (Group group : groups) {
        Query query = new Query<Page>("group", group.getId(), null, null, Page.class);
        DataStorage dataStorage = SpaceUtils.getDataStorage();
        List<Page> pages = dataStorage.find(query).getAll();

        for (Page page : pages) {

          if ("SpaceActivityStreamPortlet".equals(page.getName())) {

            try {
              //
              Container root = (Container) page.getChildren().get(0);
              Container bottom = (Container) root.getChildren().get(1);
              Application application = (Application) bottom.getChildren().get(0);

              //
              bottom.setTemplate(UI_TABLE_COLUMN_TEMPLATE);

              //
              Container left = new Container();
              left.setTemplate(UI_SIMPLE_TEMPLATE);
              left.setAccessPermissions(root.getAccessPermissions());

              //
              Space space = spaceStorage.getSpaceByGroupId(group.getId());
              TransientApplicationState<Portlet> summaryState = new TransientApplicationState<Portlet>(PORTLET_SUMMARY);
              Application summaryApplication = new Application(ApplicationType.PORTLET);
              summaryApplication.getProperties().put("SPACE_URL", space.getUrl());
              summaryApplication.setAccessPermissions(root.getAccessPermissions());
              summaryApplication.setShowInfoBar(false);
              summaryApplication.setState(summaryState);
              summaryApplication.setTitle("Space Summary Info");

              //
              TransientApplicationState<Portlet> detailsState = new TransientApplicationState<Portlet>(PORTLET_DETAILS);
              Application detailsApplication = new Application(ApplicationType.PORTLET);
              detailsApplication.setAccessPermissions(root.getAccessPermissions());
              detailsApplication.getProperties().put("repository", "repository");
              detailsApplication.getProperties().put("workspace", "collaboration");
              detailsApplication.getProperties().put("nodeIdentifier", "");
              detailsApplication.getProperties().put("ShowQuickEdit", "true");
              detailsApplication.getProperties().put("ShowPrintAction", "false");
              detailsApplication.getProperties().put("ShowTitle", "false");
              detailsApplication.setShowApplicationState(false);
              detailsApplication.setShowApplicationMode(false);
              detailsApplication.setShowInfoBar(false);
              detailsApplication.setState(detailsState);
              detailsApplication.setTitle("Content Details");

              //
              left.getChildren().add(summaryApplication);
              left.getChildren().add(detailsApplication);

              //
              bottom.getChildren().clear();
              bottom.getChildren().add(left);
              bottom.getChildren().add(application);

              //
              dataStorage.save(page);
              LOG.info("Upgrade space home for : " + page.getTitle());

            } catch (RuntimeException exception) {
              // TODO Auto-generated catch block
              exception.printStackTrace();
            }
          }

        }
      }

    } catch (Exception e) {
      LOG.info("Error during template migration : " + e.getMessage(), e);
    } finally {
      RequestLifeCycle.end();
    }

  }

  @Override
  public boolean shouldProceedToUpgrade(String arg0, String arg1) {
    return true;
  }

}
