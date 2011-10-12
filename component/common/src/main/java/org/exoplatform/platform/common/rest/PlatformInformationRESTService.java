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
package org.exoplatform.platform.common.rest;

import java.lang.reflect.Method;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.commons.info.ProductInformations;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;

/**
 * @author <a href="mailto:anouar.chattouna@exoplatform.com">Anouar
 *         Chattouna</a>
 * @version $Revision$
 */
@Path("/platform")
public class PlatformInformationRESTService implements ResourceContainer {

  private Log logger = ExoLogger.getLogger(this.getClass());
  private ProductInformations platformInformations;

  public PlatformInformationRESTService(ProductInformations productInformations) {
    this.platformInformations = productInformations;
  }

  /**
   * This method return a JSON Object with the platform required
   * informations.
   */
  @GET
  @Path("/info")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getPlatformInformation() {
    CacheControl cacheControl = new CacheControl();
    cacheControl.setNoCache(true);
    cacheControl.setNoStore(true);
    try {
      JsonPlatformInfo jsonPlatformInfo = new JsonPlatformInfo();
      jsonPlatformInfo.setPlatformVersion(platformInformations.getVersion());
      jsonPlatformInfo.setPlatformBuildNumber(platformInformations.getBuildNumber());
      jsonPlatformInfo.setPlatformRevision(platformInformations.getRevision());
      jsonPlatformInfo.setPlatformEdition(getPlatformEdition());
      jsonPlatformInfo.setIsMobileCompliant(isMobileCompliant().toString());
      if (logger.isDebugEnabled()) {
        logger.debug("Getting Platform Informations: eXo Platform (v" + platformInformations.getVersion() + " - build "
            + platformInformations.getBuildNumber() + " - rev. " + platformInformations.getRevision());
      }
      return Response.ok(jsonPlatformInfo, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();
    } catch (Exception e) {
      logger.error("An error occured while getting platform version information.", e);
      return Response.status(HTTPStatus.INTERNAL_ERROR).cacheControl(cacheControl).build();
    }
  }

  private Boolean isMobileCompliant() {
    String platformEdition = getPlatformEdition();
    return (platformEdition != null && (platformEdition.equals("enterprise")));
  }

  private String getPlatformEdition() {
    try {
      Class<?> c = Class.forName("org.exoplatform.platform.edition.PlatformEdition");
      Method getEditionMethod = c.getMethod("getEdition");
      String platformEdition = (String) getEditionMethod.invoke(null);
      return platformEdition;
    } catch (Exception e) {
      logger.error("An error occured while getting the platform edition information.", e);
    }
    return null;
  }

  public static class JsonPlatformInfo {

    private String platformVersion;
    private String platformBuildNumber;
    private String platformRevision;
    private String platformEdition;
    private String isMobileCompliant;

    public JsonPlatformInfo() {}

    public String getPlatformVersion() {
      return platformVersion;
    }

    public void setPlatformVersion(String platformVersion) {
      this.platformVersion = platformVersion;
    }

    public String getIsMobileCompliant() {
      return this.isMobileCompliant;
    }

    public void setIsMobileCompliant(String isMobileCompliant) {
      this.isMobileCompliant = isMobileCompliant;
    }

    public String getPlatformBuildNumber() {
      return platformBuildNumber;
    }

    public void setPlatformBuildNumber(String platformBuildNumber) {
      this.platformBuildNumber = platformBuildNumber;
    }

    public String getPlatformRevision() {
      return platformRevision;
    }

    public void setPlatformRevision(String platformRevision) {
      this.platformRevision = platformRevision;
    }

    public String getPlatformEdition() {
      return this.platformEdition;
    }

    public void setPlatformEdition(String platformEdition) {
      this.platformEdition = platformEdition;
    }

  }

}
