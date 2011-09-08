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
package org.exoplatform.platform.component.organization;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.scheduler.BaseJob;
import org.exoplatform.services.scheduler.JobContext;

/**
 * This is a scheduled job that invoke all OrganizationService listeners on
 * Users & Groups.
 * 
 * @author Boubaker KHANFIR
 */
public class OrganizationIntegrationJob extends BaseJob {
  private static final Log LOG = ExoLogger.getLogger(OrganizationIntegrationJob.class);
  private OrganizationIntegrationService organizationIntegrationService;

  public OrganizationIntegrationJob() {}

  /**
   * {@inheritDoc}
   */
  public void execute(JobContext context) throws Exception {
    LOG.info("Start all Organizational model synchronization.");
    getOrganizationIntegrationService().syncAll();
    LOG.info("Organizational model synchronization finished successfully.");
  }

  public OrganizationIntegrationService getOrganizationIntegrationService() {
    if (this.organizationIntegrationService == null) {
      ExoContainer exoContainer = ExoContainerContext.getCurrentContainer();
      this.organizationIntegrationService = (OrganizationIntegrationService) exoContainer
          .getComponentInstanceOfType(OrganizationIntegrationService.class);
      if (this.organizationIntegrationService == null) {
        throw new IllegalStateException(
            "Could not retrieve an instance of service 'OrganizationIntegrationService' from the selected container: "
                + exoContainer);
      }
    }
    return this.organizationIntegrationService;
  }
}
