package org.exoplatform.platform.cloud.services.rest;

import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.core.Response;
import javax.ws.rs.FormParam;
import javax.ws.rs.QueryParam;
import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.rest.RESTOrganizationServiceAbstractImpl;
import org.exoplatform.services.rest.resource.ResourceContainer;

@Path("/organization")
public class IntranetRESTOrganizationServiceImpl extends RESTOrganizationServiceAbstractImpl implements ResourceContainer {
	
    public IntranetRESTOrganizationServiceImpl(OrganizationService organizationService){
		super(organizationService);
    }
    
	
  @POST
  @Path("/adduser")
  public Response createUser(@FormParam("URI") String baseURI,
		                     @FormParam("username") String userName,
		                     @FormParam("password") String password,
		                     @FormParam("first-name") String firstName,
		                     @FormParam("last-name") String lastName,
		                     @FormParam("email") String email){
	
    super.createUser(baseURI, userName, password, firstName, lastName, email);
    return Response.status(HTTPStatus.CREATED).entity("Created").build();
   }
  
   @GET
   @Path("/test")
   public Response test(){
	   assert(super.getUsersCount() != null);
	   return Response.ok().build();
   }
}
