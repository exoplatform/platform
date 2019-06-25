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
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.social.rest.api.ErrorResource;
import org.exoplatform.social.service.rest.api.VersionResources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


@Path(VersionResources.VERSION_ONE + "/platform/branding")
@Api(tags = VersionResources.VERSION_ONE + "/platform/branding", value = VersionResources.VERSION_ONE + "/platform/branding", description = "Managing branding information")
public class BrandingRestResourcesV1 implements ResourceContainer {
  private static final Log LOG = ExoLogger.getLogger(BrandingRestResourcesV1.class);
  
  private BrandingService brandingService;

  public BrandingRestResourcesV1(BrandingService brandingService) {
    this.brandingService = brandingService;
  }
  
  
  /**
   * @return global settings of Branding Company Name
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("users")
  @ApiOperation(value = "Get Branding information",
  httpMethod = "GET",
  response = Response.class)
  @ApiResponses(value = {
      @ApiResponse (code = 200, message = "Request fulfilled"),
      @ApiResponse (code = 500, message = "Server error when retrieving branding information") })
  public Response getBrandingInformation() {
    try {
      return Response.ok(brandingService.getBrandingInformation()).build();
    } catch (Exception e) {
      LOG.error("Error when retrieving branding information", e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
              .entity(new ErrorResource("Error when retrieving branding information", e.getMessage())).build();
    }
  }
  
  @PUT
  @RolesAllowed("administrators")
  @Consumes(MediaType.APPLICATION_JSON)
  @ApiOperation(value = "Update Branding information", httpMethod = "POST", response = Response.class)
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Branding information updated"),
      @ApiResponse(code = 500, message = "Server error when updating branding information") })
  public Response updateBrandingInformation(Branding branding) {
    try {
      brandingService.updateBrandingInformation(branding);
    } catch (Exception e) {
      LOG.error("Error when updating branding information", e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
              .entity(new ErrorResource("Error when updating branding information", e.getMessage())).build();
    }
    return Response.status(Response.Status.OK).build();
  }

  @GET
  @Path("/logo")
  @RolesAllowed("users")
  @ApiOperation(value = "Get Branding logo", httpMethod = "GET", response = Response.class)
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Branding logo retrieved"),
          @ApiResponse(code = 404, message = "Branding logo not found"),
          @ApiResponse(code = 500, message = "Server error when retrieving branding logo") })
  public Response getBrandingLogo(@Context Request request) {
    
    Logo logo = brandingService.getLogo();
    if (logo == null) {
      throw new WebApplicationException(Response.Status.NOT_FOUND);
    }
    //
    long lastUpdated = logo.getUpdatedDate().getTime();
    EntityTag eTag = new EntityTag(String.valueOf(lastUpdated));
    //
    Response.ResponseBuilder builder = request.evaluatePreconditions(eTag);
    if (builder == null) {
      InputStream stream = new ByteArrayInputStream(logo.getData());
      builder = Response.ok(stream, "image/png");
      builder.tag(eTag);
    }
    CacheControl cc = new CacheControl();
    cc.setMaxAge(86400);
    builder.cacheControl(cc);
    return builder.cacheControl(cc).build();
  }
}
