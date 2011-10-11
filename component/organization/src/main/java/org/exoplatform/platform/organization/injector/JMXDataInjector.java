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
package org.exoplatform.platform.organization.injector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.zip.ZipOutputStream;

import org.exoplatform.management.annotations.Impact;
import org.exoplatform.management.annotations.ImpactType;
import org.exoplatform.management.annotations.Managed;
import org.exoplatform.management.annotations.ManagedDescription;
import org.exoplatform.management.annotations.ManagedName;
import org.exoplatform.management.jmx.annotations.NameTemplate;
import org.exoplatform.management.jmx.annotations.Property;
import org.exoplatform.management.rest.annotations.RESTEndpoint;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.picocontainer.Startable;

@Managed
@ManagedDescription("Platform Organization Data Injector Service")
@NameTemplate({ @Property(key = "name", value = "OrganizationDataInjectorService"),
    @Property(key = "service", value = "extensions"), @Property(key = "type", value = "platform") })
@RESTEndpoint(path = "orgsync")
public class JMXDataInjector implements Startable {
  private static final Log logger_ = ExoLogger.getLogger(JMXDataInjector.class);

  private DataInjectorService dataInjectorService;

  public JMXDataInjector(DataInjectorService dataInjectorService) {
    this.dataInjectorService = dataInjectorService;
  }

  /**
   * extract Organization Data to a zip file
   * 
   * @param filePath
   *          exported file path
   * @throws Exception
   */
  @Managed
  @ManagedDescription("extract Organization Data to a zip file")
  @Impact(ImpactType.READ)
  public void extractData(@ManagedDescription("exported file path") @ManagedName("filePath") String filePath) throws Exception {
    if (filePath == null || filePath.isEmpty() || !filePath.endsWith(".zip")) {
      throw new IllegalArgumentException(filePath + " have to point into a zip file.");
    }

    File targetFile = new File(filePath);
    if (targetFile.exists()) {
      throw new IllegalArgumentException(filePath + " already exists.");
    }

    logger_.info("Extracting Organization model data to : " + filePath);

    OutputStream out = new FileOutputStream(filePath);
    ZipOutputStream zos = new ZipOutputStream(out);

    dataInjectorService.writeProfiles(zos);
    dataInjectorService.writeUsers(zos);
    dataInjectorService.writeOrganizationModelData(zos);

    zos.close();
    logger_.info("Organization model data successfully expoted.");
  }

  @Managed
  @ManagedDescription("inject Organization Data from a zip file")
  @Impact(ImpactType.READ)
  public void injectData(@ManagedDescription("path to zip file") @ManagedName("filePath") String filePath) {
    if (filePath == null || filePath.isEmpty() || !filePath.endsWith(".zip")) {
      throw new IllegalArgumentException(filePath + " have to point into a zip file.");
    }

    try {
      dataInjectorService.readDataPlugins(filePath);
      dataInjectorService.readUsersData(filePath);
      dataInjectorService.readUserProfilesData(filePath);
      dataInjectorService.doImport(true);
    } catch (Exception e) {
      logger_.error("Can not import users profile .. ", e);
    }
  }

  @Override
  public void start() {}

  @Override
  public void stop() {}

}
