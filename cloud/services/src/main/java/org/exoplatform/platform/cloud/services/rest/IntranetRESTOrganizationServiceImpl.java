/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.platform.cloud.services.rest;

import static org.exoplatform.cloudmanagement.rest.CloudServicesRoles.CLOUD_ADMIN;
import static org.exoplatform.cloudmanagement.status.TenantStatus.PROPERTY_ADMIN_NAME;
import static org.exoplatform.cloudmanagement.status.TenantStatus.PROPERTY_ADMIN_PASSWORD;

import javax.annotation.security.RolesAllowed;
import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.GroupHandler;
import org.exoplatform.services.organization.MembershipType;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.UserHandler;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.services.jcr.RepositoryService;

import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.core.Response;
import javax.ws.rs.FormParam;
import javax.ws.rs.QueryParam;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class IntranetRESTOrganizationServiceImpl.
 */
@Path("/organization")
public class IntranetRESTOrganizationServiceImpl
{

	private static final Logger LOG = LoggerFactory.getLogger(IntranetRESTOrganizationServiceImpl.class);
	
   private static final String ROOT_USER = "root";

   private final RepositoryService repositoryService;

   private final OrganizationService organizationService;

   public IntranetRESTOrganizationServiceImpl(RepositoryService repositoryService,
      OrganizationService organizationService)
   {
      this.repositoryService = repositoryService;
      this.organizationService = organizationService;
   }

   /**
    * Creates the user on given repository.
    *
    * @param tname the tname
    * @param baseURI the base uri
    * @param userName the user name
    * @param password the password
    * @param firstName the first name
    * @param lastName the last name
    * @param email the email
    * @return the response
    * @throws Exception the exception
    */
   @POST
   @Path("/adduser")
   @RolesAllowed("cloud-admin")
   public Response createUser(@FormParam("tname") String tname, @FormParam("URI") String baseURI,
      @FormParam("username") String userName, @FormParam("password") String password,
      @FormParam("first-name") String firstName, @FormParam("last-name") String lastName,
      @FormParam("email") String email) throws Exception
   {
      try
      {
         repositoryService.setCurrentRepositoryName(tname);
         UserHandler userHandler = organizationService.getUserHandler();
         User newUser = userHandler.createUserInstance(email);
         newUser.setPassword(password);
         newUser.setFirstName(firstName);
         newUser.setLastName(lastName);
         newUser.setEmail(email);
         userHandler.createUser(newUser, true);

         // register user in groups '/platform/developers' and '/platform/users'
         GroupHandler groupHandler = organizationService.getGroupHandler();
         MembershipType membership = organizationService.getMembershipTypeHandler().findMembershipType("member");

         Group usersGroup = groupHandler.findGroupById("/platform/users");
         organizationService.getMembershipHandler().linkMembership(newUser, usersGroup, membership, true);
         return Response.status(HTTPStatus.CREATED).entity("Created").build();
      }
      catch (Exception e)
      {
         LOG.trace("Unable to store user in tenant " + tname, e);
         return Response.status(HTTPStatus.INTERNAL_ERROR).build();
      }
   }

   /**
    * Creates the root user on given repository.
    *
    * @param tname the tname
    * @param password the password
    * @param firstName the first name
    * @param lastName the last name
    * @param email the email
    * @return the response
    * @throws Exception the exception
    */
   @POST
   @Path("/createroot")
   @RolesAllowed("cloud-admin")
   public Response createRoot(@FormParam("tname") String tname, @FormParam("password") String password,
      @FormParam("first-name") String firstName, @FormParam("last-name") String lastName,
      @FormParam("email") String email) throws Exception
   {
      try
      {
         repositoryService.setCurrentRepositoryName(tname);
         UserHandler userHandler = organizationService.getUserHandler();
         User rootUser = userHandler.findUserByName(ROOT_USER);
         rootUser.setPassword(password);
         rootUser.setFirstName(firstName);
         rootUser.setLastName(lastName);
         //rootUser.setEmail(email);
         userHandler.saveUser(rootUser, true);//createUser(newUser, true);
         return Response.status(HTTPStatus.CREATED).entity("Created").build();
      }
      catch (Exception e)
      {
    	  LOG.trace("Unable to store ROOT user in tenant " + tname, e);
         return Response.status(HTTPStatus.INTERNAL_ERROR).build();
      }
   }
}
