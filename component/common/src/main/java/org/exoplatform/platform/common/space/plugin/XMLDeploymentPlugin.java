/*
 * Copyright (C) 2003-2008 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.platform.common.space.plugin;

import java.util.Iterator;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ObjectParameter;
import org.exoplatform.platform.common.space.SpaceCustomizationService;
import org.exoplatform.services.deployment.DeploymentDescriptor;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.ext.hierarchy.NodeHierarchyCreator;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.space.SpaceListenerPlugin;
import org.exoplatform.social.core.space.spi.SpaceLifeCycleEvent;

public class XMLDeploymentPlugin extends SpaceListenerPlugin {

  /** The init params. */
  private InitParams initParams;

  private SpaceCustomizationService spaceCustomizationService = null;

  /** The log. */
  private Log log = ExoLogger.getLogger(this.getClass());

  /**
   * Instantiates a new XML deployment plugin.
   * 
   * @param initParams
   *          the init params
   * @param configurationManager
   *          the configuration manager
   * @param repositoryService
   *          the repository service
   * @param nodeHierarchyCreator
   *          the nodeHierarchyCreator service
   */
  public XMLDeploymentPlugin(InitParams initParams, SpaceCustomizationService spaceCustomizationService_,
      NodeHierarchyCreator nodeHierarchyCreator) {
    this.spaceCustomizationService = spaceCustomizationService_;
    this.initParams = initParams;
  }

  @Override
  public void spaceCreated(SpaceLifeCycleEvent lifeCycleEvent) {
    SessionProvider sessionProvider = SessionProvider.createSystemProvider();
    try {
      Iterator<?> iterator = initParams.getObjectParamIterator();
      while (iterator.hasNext()) {
        ObjectParameter objectParameter = (ObjectParameter) iterator.next();
        DeploymentDescriptor deploymentDescriptor = (DeploymentDescriptor) objectParameter.getObject();
        spaceCustomizationService.deployContentToSpaceDrive(sessionProvider, lifeCycleEvent.getSpace().getGroupId(),
            deploymentDescriptor);
      }
    } catch (Exception e) {
      log.error("An unexpected problem occurs while deploying contents", e);
    } finally {
      sessionProvider.close();
    }
  }

  @Override
  public void applicationActivated(SpaceLifeCycleEvent arg0) {}

  @Override
  public void applicationAdded(SpaceLifeCycleEvent arg0) {}

  @Override
  public void applicationDeactivated(SpaceLifeCycleEvent arg0) {}

  @Override
  public void applicationRemoved(SpaceLifeCycleEvent arg0) {}

  @Override
  public void grantedLead(SpaceLifeCycleEvent arg0) {}

  @Override
  public void joined(SpaceLifeCycleEvent arg0) {}

  @Override
  public void left(SpaceLifeCycleEvent arg0) {}

  @Override
  public void revokedLead(SpaceLifeCycleEvent arg0) {}

  @Override
  public void spaceRemoved(SpaceLifeCycleEvent arg0) {}
}
