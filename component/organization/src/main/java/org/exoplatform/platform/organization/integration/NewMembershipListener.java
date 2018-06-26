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

import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.Membership;
import org.exoplatform.services.organization.MembershipEventListener;

/**
 * This Listener is invoked when a Mambership is updated/added. Its purpose
 * is to ensure that OrganizationServiceIntegration don't apply
 * Organization Model Data listeners twice.
 * 
 * @deprecated OrganizationIntegrationService is replaced by External Store API
 * @author Boubaker KHANFIR
 */
@Deprecated
public class NewMembershipListener extends MembershipEventListener {

  private static final Log LOG = ExoLogger.getLogger(NewMembershipListener.class);

  private RepositoryService repositoryService;

  private OrganizationIntegrationService organizationIntegrationService;

  public NewMembershipListener(RepositoryService repositoryService) throws Exception {
    this.repositoryService = repositoryService;
  }

  /**
   * {@inheritDoc}
   */
  public void postSave(Membership m, boolean isNew) throws Exception {
    if (!getOrganizationIntegrationService().isEnabled()) {
      return;
    }
    if (!isNew) {
      return;
    }
    Session session = null;
    try {
      session = repositoryService.getCurrentRepository().getSystemSession(Util.WORKSPACE);
      if (Util.hasUserFolder(session, m.getUserName())) {
        if (!Util.hasMembershipFolder(session, m)) {
          Util.createMembershipFolder(session, m);
        }
      } else {
        LOG.warn("Membership listeners can't be invoked on membership '" + m + "' because the user isn't synchronized yet.");
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
    if (!getOrganizationIntegrationService().isEnabled()) {
      return;
    }
    Session session = null;
    try {
      session = repositoryService.getCurrentRepository().getSystemSession(Util.WORKSPACE);
      if (Util.hasMembershipFolder(session, m)) {
        Util.deleteMembershipFolder(session, m);
      }
    } finally {
      if (session != null) {
        session.logout();
      }
    }
  }

  public OrganizationIntegrationService getOrganizationIntegrationService() {
    if (this.organizationIntegrationService == null) {
      organizationIntegrationService = CommonsUtils.getService(OrganizationIntegrationService.class);
    }
    return this.organizationIntegrationService;
  }
}