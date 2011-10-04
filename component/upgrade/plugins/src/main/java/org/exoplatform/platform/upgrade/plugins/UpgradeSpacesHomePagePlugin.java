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

import org.exoplatform.commons.upgrade.UpgradeProductPlugin;
import org.exoplatform.commons.utils.ExoProperties;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.platform.common.space.SpaceCustomizationService;
import org.exoplatform.services.deployment.DeploymentDescriptor;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.GroupHandler;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.storage.api.SpaceStorage;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain
 *         Defrance</a>
 * @version $Revision$
 */
public class UpgradeSpacesHomePagePlugin extends UpgradeProductPlugin {

  private final OrganizationService service;
  private final SpaceStorage spaceStorage;
  private final SpaceCustomizationService spaceCustomizationService;
  private final ExoProperties welcomeSCVCustomPreferences;
  private final DeploymentDescriptor deploymentDescriptor;

  private static final Log LOG = ExoLogger.getLogger(UpgradeSpacesHomePagePlugin.class);

  public UpgradeSpacesHomePagePlugin(SpaceCustomizationService spaceCustomizationService,
      OrganizationService organizationService, SpaceStorage spaceStorage, InitParams initParams) {
    super(initParams);
    this.service = organizationService;
    this.spaceStorage = spaceStorage;
    this.spaceCustomizationService = spaceCustomizationService;

    welcomeSCVCustomPreferences = new ExoProperties();
    welcomeSCVCustomPreferences.put("nodeIdentifier", "/Groups{spaceGroupId}/SharedData/welcome");

    deploymentDescriptor = new DeploymentDescriptor();
    deploymentDescriptor.setCleanupPublication(true);
    deploymentDescriptor.setSourcePath("war:/conf/office-extension/social/artifacts/SpaceWelcome.xml");
    DeploymentDescriptor.Target target = new DeploymentDescriptor.Target();
    target.setNodePath("/SharedData");
    target.setWorkspace("collaboration");
    deploymentDescriptor.setTarget(target);
  }

  @Override
  public void processUpgrade(String oldVersion, String newVersion) {
    try {
      RequestLifeCycle.begin(PortalContainer.getInstance());

      GroupHandler groupHandler = service.getGroupHandler();
      Group spaces = service.getGroupHandler().findGroupById("/spaces");

      @SuppressWarnings("unchecked")
      Collection<Group> groups = groupHandler.findGroups(spaces);

      SessionProvider sessionProvider = SessionProvider.createSystemProvider();
      for (Group group : groups) {
        Space space = spaceStorage.getSpaceByGroupId(group.getId());
        spaceCustomizationService.createSpaceHomePage(space.getPrettyName(), group.getId(), welcomeSCVCustomPreferences);
        spaceCustomizationService.deployContentToSpaceDrive(sessionProvider, group.getId(), deploymentDescriptor);
      }

    } catch (Exception e) {
      LOG.info("Error during template migration : " + e.getMessage(), e);
    } finally {
      RequestLifeCycle.end();
    }

  }

  @Override
  public boolean shouldProceedToUpgrade(String oldVersion, String newVersion) {
    return true;
  }

}
