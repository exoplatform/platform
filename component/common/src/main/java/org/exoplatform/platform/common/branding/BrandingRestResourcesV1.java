/*
 * Copyright (C) 2003-2019 eXo Platform SAS.
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
package org.exoplatform.platform.common.branding;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.file.model.FileItem;
import org.exoplatform.commons.file.services.FileService;
import org.exoplatform.commons.file.services.FileStorageException;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.social.service.rest.api.VersionResources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


@Path(VersionResources.VERSION_ONE + "/platform/branding")
@Api(tags = VersionResources.VERSION_ONE + "/platform/branding", value = VersionResources.VERSION_ONE + "/platform/branding", description = "Managing company branding")
public class BrandingRestResourcesV1 implements ResourceContainer {
  private static final Log LOG = ExoLogger.getLogger(BrandingRestResourcesV1.class);
  
  private BrandingService brandingService;
  private FileService         fileService;
  private SettingService        settingService;

  public BrandingRestResourcesV1(BrandingService brandingService, FileService fileService,SettingService settingService) {
    this.brandingService = brandingService;
    this.fileService = fileService;
    this.settingService = settingService;
  }
  
  
  /**
   * @return global settings of Branding Company Name
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("users")
  @ApiOperation(value = "Get Company Branding Informations",
  httpMethod = "GET",
  response = Response.class)
  @ApiResponses(value = {
      @ApiResponse (code = 200, message = "successful company name recovery"),
      @ApiResponse (code = 500, message = "Can not retrieve  company name") })
  public Response brandingInformations() {
    try {
      return Response.ok(brandingService.getBrandingInformation()).build();
    } catch (Exception e) {
      LOG.error("Error when retrieving global settings", e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }
  
  @POST
  @RolesAllowed("administrators")
  @Consumes(MediaType.APPLICATION_JSON)
  @ApiOperation(value = "Update informations", httpMethod = "POST", response = Response.class)
  @ApiResponses(value = { @ApiResponse(code = 200, message = "Update company branding done"),
      @ApiResponse(code = 404, message = "Resource not found"), @ApiResponse(code = 500, message = "Can not save ") })
  public Response updateBrandingInformation(Branding branding) {
    try {
      brandingService.updateBranding(branding);
    } catch (Exception e) {
      LOG.error("Error when update company branding informations", e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
    return Response.status(Response.Status.ACCEPTED).entity("Update company branding informations").build();
  }

  @GET
  @Path("/logo")
  @RolesAllowed("users")
  public Response getCompanyImage(@Context Request request) throws ParseException, FileStorageException, IOException {
    
    Long imageId = brandingService.getLogoId();
    FileItem fileItem = fileService.getFile(imageId);
    if (fileItem == null) {
      throw new WebApplicationException(Response.Status.NOT_FOUND);
    }
    //
    long lastUpdated = (new SimpleDateFormat("yyyy-MM-dd")).parse(fileItem.getFileInfo().getUpdatedDate().toString()).getTime();
    EntityTag eTag = new EntityTag(String.valueOf(lastUpdated));
    //
    Response.ResponseBuilder builder = request.evaluatePreconditions(eTag);
    if (builder == null) {
      fileItem = fileService.getFile(imageId);
      InputStream stream = fileItem.getAsStream();
      builder = Response.ok(stream, "image/png");
      builder.tag(eTag);
    }
    CacheControl cc = new CacheControl();
    cc.setMaxAge(86400);
    builder.cacheControl(cc);
    return builder.cacheControl(cc).build();
  }
}
