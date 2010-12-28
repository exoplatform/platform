/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
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
package org.exoplatform.platform.migration.aio.rest;

import java.io.File;

import org.apache.commons.logging.Log;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.platform.migration.aio.backup.AIOBackupMigrationService;
import org.exoplatform.platform.migration.common.component.ContainerParamExtractor;
import org.exoplatform.services.jcr.ext.backup.BackupConfig;
import org.exoplatform.services.jcr.ext.backup.BackupManager;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.rest.HTTPMethod;
import org.exoplatform.services.rest.OutputTransformer;
import org.exoplatform.services.rest.QueryParam;
import org.exoplatform.services.rest.Response;
import org.exoplatform.services.rest.URITemplate;
import org.exoplatform.services.rest.container.ResourceContainer;
import org.exoplatform.services.rest.transformer.StringOutputTransformer;

@URITemplate("/migration/backup/")
public class AIOBackupMigrationREST implements ResourceContainer {
  private Log log = ExoLogger.getLogger(this.getClass());

  private AIOBackupMigrationService backupService;

  private ContainerParamExtractor containerParamExtractor_ = null;

  public AIOBackupMigrationREST(AIOBackupMigrationService backupService, ContainerParamExtractor containerParamExtractor) {
    this.backupService = backupService;
    this.containerParamExtractor_ = containerParamExtractor;
  }

  @HTTPMethod("GET")
  @URITemplate()
  @OutputTransformer(StringOutputTransformer.class)
  public Response generateBackupForm() throws Exception {
    StringBuffer htmlContainersLink = new StringBuffer();
    htmlContainersLink.append("<html xmlns='http://www.w3.org/1999/xhtml'><body xmlns='http://www.w3.org/1999/xhtml'>");

    htmlContainersLink.append("<fieldset>");
    htmlContainersLink.append("<legend>Generates Collaboration&System Workspace backup files</legend>");

    htmlContainersLink.append("<form action='/" + containerParamExtractor_.getContainerId(PortalContainer.getInstance()) + "/" + containerParamExtractor_.getContainerRestContext(PortalContainer.getInstance()) + "/migration/backup/start' method='get'>");
    htmlContainersLink.append("Backup Location : <input type='text' name='location'/><BR/>");
    htmlContainersLink.append("<input type='submit'/>");
    htmlContainersLink.append("</form>");

    htmlContainersLink.append("</fieldset>");
    return Response.Builder.ok().entity(htmlContainersLink.toString(), "text/html").build();
  }

  @HTTPMethod("GET")
  @URITemplate("/start/")
  @OutputTransformer(StringOutputTransformer.class)
  public Response startBackup(@QueryParam("location") String backupLocation) throws Exception {
    try {
      {// Backup collaboration workspace
        BackupConfig config = new BackupConfig();
        config.setBuckupType(BackupManager.FULL_BACKUP_ONLY);
        config.setRepository("repository");
        config.setWorkspace("collaboration");
        config.setBackupDir(new File(backupLocation));
        backupService.startBackupWorkspace(config);
      }
      {// Backup system workspace
        BackupConfig config = new BackupConfig();
        config.setBuckupType(BackupManager.FULL_BACKUP_ONLY);
        config.setRepository("repository");
        config.setWorkspace("system");
        config.setBackupDir(new File(backupLocation));
        backupService.startBackupWorkspace(config);
      }
    } catch (Exception e) {
      log.error("Can't start backup", e);
    }
    return Response.Builder.ok().entity("The Convertor Job has been executed, please wait until it ends!", "text/html").build();
  }
}