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
package org.exoplatform.platform.migration.plf.rest;

import java.io.ByteArrayInputStream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.exoplatform.platform.migration.common.component.MarshallConfigurationService;
import org.exoplatform.platform.migration.common.handler.ComponentHandler.Entry;
import org.exoplatform.platform.migration.common.handler.ComponentHandler.EntryType;
import org.exoplatform.services.rest.resource.ResourceContainer;

@Path(MarshallConfigurationService.CLASS_URI_TEMPLE)
public class MarshallConfigurationREST implements ResourceContainer {

  private MarshallConfigurationService marshallConfigurationService;

  public MarshallConfigurationREST(MarshallConfigurationService marshallConfigurationService) {
    this.marshallConfigurationService = marshallConfigurationService;
  }

  @GET
  @Produces(MediaType.TEXT_HTML)
  public Response containersList() throws Exception {
    String htmlContainersLink = marshallConfigurationService.generateHTMLContainersList();
    return Response.ok().entity(htmlContainersLink).build();
  }

  @GET
  @Path(MarshallConfigurationService.GET_CONTAINERS_METHOD_URI_TEMPLE)
  @Produces(MediaType.TEXT_HTML)
  public Response componentsList(@QueryParam(MarshallConfigurationService.CONTAINER_ID_PARAM_NAME) String containerId) throws Exception {
    String htmlComponentsLink = marshallConfigurationService.generateHTMLComponentsList(containerId);
    return Response.ok().entity(htmlComponentsLink).build();
  }

  @GET
  @Produces(MediaType.APPLICATION_OCTET_STREAM)
  @Path(MarshallConfigurationService.GET_COMPONENT_METHOD_URI_TEMPLE)
  public Response getComponentConfiguration(@QueryParam(MarshallConfigurationService.CONTAINER_ID_PARAM_NAME) String containerId, @QueryParam(MarshallConfigurationService.COMONENT_KEY_PARAM_NAME) String componentKey) throws Exception {
    Entry configurationEntry = marshallConfigurationService.getComponentConfiguration(containerId, componentKey);
    ResponseBuilder builder = Response.ok();
    if (configurationEntry.getType().equals(EntryType.ZIP)) {
      builder.header("Content-disposition", "attachment; filename=" + configurationEntry.getComponentName() + configurationEntry.getType());
    }
    return builder.entity(new ByteArrayInputStream(configurationEntry.getContent())).build();
  }

  @GET
  @Produces(MediaType.APPLICATION_OCTET_STREAM)
  @Path(MarshallConfigurationService.GET_CONTAINER_CONFIGURATION_URI_TEMPLE)
  public Response getAllComponentsConfiguration(@QueryParam(MarshallConfigurationService.CONTAINER_ID_PARAM_NAME) String containerId) throws Exception {
    Entry configurationEntry = marshallConfigurationService.getAllComponentsConfiguration(containerId);
    ResponseBuilder builder = Response.ok();
    builder.header("Content-disposition", "attachment; filename=" + configurationEntry.getComponentName() + configurationEntry.getType());
    return builder.entity(new ByteArrayInputStream(configurationEntry.getContent())).build();
  }
}
