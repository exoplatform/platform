import javax.ws.rs.Path
import javax.ws.rs.GET
import javax.ws.rs.PathParam

import org.exoplatform.social.webui.Utils;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.relationship.model.Relationship;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.web.application.ApplicationMessage;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.manager.RelationshipManager;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;


@Path("/addFriend")
public class AddFriend {
  private RelationshipManager relationshipManager;

  /** Stores IdentityManager instance. */
  private IdentityManager identityManager = null;
  
  @GET
  @Path("/add/{id}")
  public Response add(@Context SecurityContext sc, @PathParam("id") String id) throws Exception {
      String name = sc.getUserPrincipal().getName();
      
      RelationshipManager relm = getRelationshipManager();
      IdentityManager im = getIdentityManager();
      
       Identity currentIdentity = im.getOrCreateIdentity(OrganizationIdentityProvider.NAME, name); 
      Identity requestedIdentity = im.getOrCreateIdentity(OrganizationIdentityProvider.NAME, id);
     
      relm.invite(currentIdentity, requestedIdentity);
     
      return Response.ok(requestedIdentity.getProfile().getFullName(), new MediaType("application", "json")).build();
  }
  
  private RelationshipManager getRelationshipManager() {
    if (relationshipManager == null) {
      ExoContainer container = ExoContainerContext.getCurrentContainer();
      relationshipManager = (RelationshipManager) container.getComponentInstanceOfType(RelationshipManager.class);
    }
    return relationshipManager;
  }
  
  private IdentityManager getIdentityManager() {
    if (identityManager == null) {
      ExoContainer container = ExoContainerContext.getCurrentContainer();
      identityManager = (IdentityManager) container.getComponentInstanceOfType(IdentityManager.class);
    }
    return identityManager;
  }
  
}

