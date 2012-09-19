/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
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
package org.exoplatform.platform.organization.integration;

import javax.jcr.Session;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.ComponentRequestLifecycle;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.ext.distribution.DataDistributionManager;
import org.exoplatform.services.organization.Membership;
import org.exoplatform.services.organization.MembershipEventListener;
import org.exoplatform.services.organization.OrganizationService;

/**
 * This Listener is invoked when a Mambership is updated/added. Its purpose
 * is to ensure that OrganizationServiceIntegration don't apply
 * Organization Model Data listeners twice.
 * 
 * @author Boubaker KHANFIR
 */
public class NewMembershipListener extends MembershipEventListener {

  private RepositoryService repositoryService;
  private DataDistributionManager dataDistributionManager;
  private OrganizationIntegrationService organizationIntegrationService;
  private OrganizationService organizationService;

  public NewMembershipListener(DataDistributionManager dataDistributionManager, RepositoryService repositoryService) throws Exception {
    this.repositoryService = repositoryService;
    this.dataDistributionManager = dataDistributionManager;
  }

  /**
   * {@inheritDoc}
   */
  public void postSave(Membership m, boolean isNew) throws Exception {
    if (!isNew) {
      return;
    }
    Session session = null;
    try {
      session = repositoryService.getCurrentRepository().getSystemSession(Util.WORKSPACE);
      if (!Util.hasMembershipFolder(dataDistributionManager, session, m)) {
          if (!Util.hasGroupFolder(dataDistributionManager, session, m.getGroupId())) {
              getOrganizationIntegrationService().syncGroup(m.getGroupId(), EventType.ADDED.toString());
              if (getOrganizationService() instanceof ComponentRequestLifecycle) {
                  ((ComponentRequestLifecycle) organizationService).startRequest(PortalContainer.getInstance());
              }
          }
          Util.createMembershipFolder(dataDistributionManager, session, m);
      }
    } finally {
      if (session != null) {
        session.logout();
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  public void postDelete(Membership m) throws Exception {
    Session session = null;
    try {
      session = repositoryService.getCurrentRepository().getSystemSession(Util.WORKSPACE);
      if (Util.hasMembershipFolder(dataDistributionManager, session, m)) {
        Util.deleteMembershipFolder(dataDistributionManager, session, m);
      }
    } finally {
      if (session != null) {
        session.logout();
      }
    }
  }

  private OrganizationIntegrationService getOrganizationIntegrationService() {

    if(organizationIntegrationService == null) {

        organizationIntegrationService = (OrganizationIntegrationService)PortalContainer.getInstance().getComponentInstanceOfType(OrganizationIntegrationService.class);

    }

    return organizationIntegrationService;

  }

  private OrganizationService getOrganizationService() {

    if(organizationService == null) {

        organizationService = (OrganizationService)PortalContainer.getInstance().getComponentInstanceOfType(OrganizationService.class);

    }

    return organizationService;

    }
}