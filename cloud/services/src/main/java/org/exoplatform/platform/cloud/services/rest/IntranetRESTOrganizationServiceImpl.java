package org.exoplatform.platform.cloud.services.rest;

import static org.exoplatform.cloudmanagement.rest.CloudServicesRoles.CLOUD_ADMIN;

import javax.annotation.security.RolesAllowed;
import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.rest.RESTOrganizationServiceAbstractImpl;
import org.exoplatform.services.rest.resource.ResourceContainer;

import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.core.Response;
import javax.ws.rs.FormParam;
import javax.ws.rs.QueryParam;
import java.util.UUID;

@Path("/organization")
public class IntranetRESTOrganizationServiceImpl extends RESTOrganizationServiceAbstractImpl implements ResourceContainer {
	
   private static final String ROOT_USER = "root";
   
    public IntranetRESTOrganizationServiceImpl(OrganizationService organizationService){
		super(organizationService);
    }
    
	
  @POST
  @Path("/adduser")
  @RolesAllowed("cloud-admin")
  public Response createUser(@FormParam("URI") String baseURI,
		                     @FormParam("username") String userName,
		                     @FormParam("password") String password,
		                     @FormParam("first-name") String firstName,
		                     @FormParam("last-name") String lastName,
		                     @FormParam("email") String email){
	
    super.createUser(baseURI, userName, password, firstName, lastName, email);
    return Response.status(HTTPStatus.CREATED).entity("Created").build();
   }
  
  
  @POST
  @Path("/createroot")
  @RolesAllowed("cloud-admin")
  public Response createRoot(@FormParam("password") String password,
                           @FormParam("first-name") String firstName,
                           @FormParam("last-name") String lastName,
                           @FormParam("email") String email){
   
     
    super.deleteUser(ROOT_USER);
    super.createUser("/", ROOT_USER, password, firstName, lastName, email);
    return Response.status(HTTPStatus.CREATED).entity("Created").build();
   }
}
