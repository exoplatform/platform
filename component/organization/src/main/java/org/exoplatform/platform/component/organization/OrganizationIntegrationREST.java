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

/**
 * This is a RESTfull Service that exopses the
 * OrganizationIntegrationService functionnalities
 * 
 * @author Boubaker KHANFIR
 */

@Path("/organizationIntegration")
public class OrganizationIntegrationREST implements ResourceContainer {

  private static final String INTEGRATION_FORM_FILE_PATH = "jar:/html/integrationForm.html";
  private OrganizationIntegrationService organizationIntegrationService;
  private ConfigurationManager configurationManager;
  private UserACL userACL;
  private String htmlFormContent = null;

  public OrganizationIntegrationREST(OrganizationIntegrationService organizationIntegrationService, UserACL userACL,
      ConfigurationManager configurationManager) {
    this.organizationIntegrationService = organizationIntegrationService;
    this.configurationManager = configurationManager;
    this.userACL = userACL;
  }

  /**
   * @return an HTML interface to invoke this RESTfull Service methods.
   */
  @GET
  @Produces(MediaType.TEXT_HTML)
  public Response generateUI() {
    if (!checkPermission()) {
      return Response.status(Status.FORBIDDEN).build();
    }
    if (htmlFormContent == null) {
      try {
        InputStream inputStream = configurationManager.getInputStream(INTEGRATION_FORM_FILE_PATH);
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes);
        htmlFormContent = new String(bytes);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    return Response.ok().entity(htmlFormContent).build();
  }

  /**
   * @see OrganizationIntegrationService#invokeAllListeners()
   * @return an HTML interface to invoke this RESTfull Service methods.
   */
  @GET
  @Path("/invokeAllListeners")
  public Response invokeAllListeners() {
    if (!checkPermission()) {
      return Response.status(Status.FORBIDDEN).build();
    }
    organizationIntegrationService.invokeAllListeners();
    return generateUI();
  }

  /**
   * @see OrganizationIntegrationService#invokeAllGroupsListeners()
   * @return an HTML interface to invoke this RESTfull Service methods.
   */
  @GET
  @Path("/invokeAllGroupsListeners")
  public Response invokeAllGroupsListeners() {
    if (!checkPermission()) {
      return Response.status(Status.FORBIDDEN).build();
    }
    organizationIntegrationService.invokeAllGroupsListeners();
    return generateUI();
  }

  /**
   * @see OrganizationIntegrationService#applyUserListeners(String)
   * @param username
   *          The user name
   * @return an HTML interface to invoke this RESTfull Service methods.
   */
  @GET
  @Path("/invokeUserListeners")
  public Response invokeUserListeners(@QueryParam("username") String username) {
    if (!checkPermission()) {
      return Response.status(Status.FORBIDDEN).build();
    }
    organizationIntegrationService.applyUserListeners(username);
    return generateUI();
  }

  /**
   * @see OrganizationIntegrationService#applyGroupListeners(String)
   * @param groupId
   *          The group Identifier
   * @return an HTML interface to invoke this RESTfull Service methods.
   */
  @GET
  @Path("/invokeGroupListeners")
  public Response invokeGroupListeners(@QueryParam("group") String groupId) {
    if (!checkPermission()) {
      return Response.status(Status.FORBIDDEN).build();
    }
    organizationIntegrationService.applyGroupListeners(groupId);
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