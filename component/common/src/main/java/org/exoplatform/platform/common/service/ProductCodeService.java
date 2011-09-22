/**
 * Copyright (C) 2009 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.platform.common.service;

import java.io.BufferedReader;
import java.io.FileReader;

import org.exoplatform.commons.utils.PropertyManager;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.picocontainer.Startable;

public class ProductCodeService implements Startable {

  // Don't use a logger that references this class
  private Log logger = ExoLogger.getLogger("Product Code Service");
  private String productCode = null;
  private String productEdition = null;

  public String getProductEdition() {
    return productEdition;
  }

  public String getProductCode() {
    return productCode;
  }

  @Override
  public void start() {
    String platformEditionFilePath = PropertyManager.getProperty("platform.edition.file");
    try {
      BufferedReader editionFileReader = new BufferedReader(new FileReader(platformEditionFilePath));
      // Read an empty line
      editionFileReader.readLine();
      // Read platform edition
      productEdition = editionFileReader.readLine().trim().toLowerCase();
      // Read an empty line
      editionFileReader.readLine();
      // Read product code
      String productCodeLineTmp = editionFileReader.readLine();
      while (productCodeLineTmp != null) {
        productCode += productCodeLineTmp.trim();
        productCodeLineTmp = editionFileReader.readLine();
      }
    } catch (Exception exception) {
      logger.error("Please make sure you have set your product key file correctly.");
    }
  }

  @Override
  public void stop() {}

}
