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

@Path("/organization")
public class IntranetRESTOrganizationServiceImpl  {
	
   private static final String ROOT_USER = "root";
   
   private final RepositoryService repositoryService;
   private final OrganizationService organizationService;
   
   
   
    public IntranetRESTOrganizationServiceImpl(RepositoryService repositoryService, OrganizationService organizationService){
		this.repositoryService = repositoryService;
		this.organizationService = organizationService;
    }
    
	
  @POST
  @Path("/adduser")
  @RolesAllowed("cloud-admin")
  public Response createUser(@FormParam("tname") String tname,
                           @FormParam("URI") String baseURI,
		                     @FormParam("username") String userName,
		                     @FormParam("password") String password,
		                     @FormParam("first-name") String firstName,
		                     @FormParam("last-name") String lastName,
		                     @FormParam("email") String email) throws Exception {
    repositoryService.setCurrentRepositoryName(tname);
    UserHandler userHandler = organizationService.getUserHandler();
    User newUser = userHandler.createUserInstance(userName);
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
  
  
  @POST
  @Path("/createroot")
  @RolesAllowed("cloud-admin")
  public Response createRoot(@FormParam("tname") String tname,
                           @FormParam("password") String password,
                           @FormParam("first-name") String firstName,
                           @FormParam("last-name") String lastName,
                           @FormParam("email") String email) throws Exception {
    repositoryService.setCurrentRepositoryName(tname); 
    UserHandler userHandler = organizationService.getUserHandler();
    User rootUser = userHandler.findUserByName(ROOT_USER);
    rootUser.setPassword(password);
    rootUser.setFirstName(firstName);
    rootUser.setLastName(lastName);
    rootUser.setEmail(email);
    userHandler.saveUser(rootUser, true);//createUser(newUser, true);
    return Response.status(HTTPStatus.CREATED).entity("Created").build();
  }
}
