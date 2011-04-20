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

import java.io.InputStream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.exoplatform.container.configuration.ConfigurationManager;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.services.rest.resource.ResourceContainer;

@Path("/organizationIntegration")
public class OrganizationIntegrationREST implements ResourceContainer {

  private static final String INTEGRATION_FORM_FILE_PATH = "jar:/html/integrationForm.html";
  private OrganizationIntegartionService integartionService;
  private ConfigurationManager configurationManager;
  private UserACL userACL;
  private String htmlFormContent = null;

  public OrganizationIntegrationREST(OrganizationIntegartionService integartionService, UserACL userACL,
      ConfigurationManager configurationManager) throws Exception {
    this.integartionService = integartionService;
    this.configurationManager = configurationManager;
    this.userACL = userACL;
  }

  @GET
  @Produces(MediaType.TEXT_HTML)
  public Response generateUI() throws Exception {
    if (!checkPermission()) {
      return Response.status(Status.FORBIDDEN).build();
    }
    if (htmlFormContent == null) {
      InputStream inputStream = configurationManager.getInputStream(INTEGRATION_FORM_FILE_PATH);
      byte[] bytes = new byte[inputStream.available()];
      inputStream.read(bytes);
      htmlFormContent = new String(bytes);
    }
    return Response.ok().entity(htmlFormContent).build();
  }

  @GET
  @Path("/invokeAllListeners")
  public Response invokeAllListeners() throws Exception {
    if (!checkPermission()) {
      return Response.status(Status.FORBIDDEN).build();
    }
    integartionService.invokeAllListeners();
    return generateUI();
  }

  @GET
  @Path("/invokeAllGroupsListeners")
  public Response invokeAllGroupsListeners() throws Exception {
    if (!checkPermission()) {
      return Response.status(Status.FORBIDDEN).build();
    }
    integartionService.invokeAllGroupsListeners();
    return generateUI();
  }

  @GET
  @Path("/invokeUserListeners")
  public Response invokeUserListeners(@QueryParam("username") String username) throws Exception {
    if (!checkPermission()) {
      return Response.status(Status.FORBIDDEN).build();
    }
    integartionService.applyUserListeners(username);
    return generateUI();
  }

  @GET
  @Path("/invokeGroupListeners")
  public Response invokeGroupListeners(@QueryParam("group") String groupId) throws Exception {
    if (!checkPermission()) {
      return Response.status(Status.FORBIDDEN).build();
    }
    integartionService.applyGroupListeners(groupId);
    return generateUI();
  }

  private boolean checkPermission() {
    String groupsString = userACL.getAdminGroups();
    String[] groups = groupsString.split(",");
    boolean hasPermission = false;
    int i = 0;
    while (!hasPermission && i < groups.length) {
      String group = groups[i];
      hasPermission = userACL.isUserInGroup(group.trim());
      i++;
    }
    return hasPermission;
  }
}