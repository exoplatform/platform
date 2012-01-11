package org.exoplatform.platform.gadget.services.Agenda;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.services.rest.resource.ResourceContainer;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.ext.RuntimeDelegate;
import org.exoplatform.services.rest.impl.RuntimeDelegateImpl;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;


@Path("/plf/gadgets")
public class AgendaRestServices implements ResourceContainer {

        private Log log = ExoLogger.getExoLogger("calendar.webservice");

        static CacheControl cc = new CacheControl();
        static {
                cc.setNoCache(true);
                cc.setNoStore(true);
        }
  
  @GET
  @Path("/getprofiles")
  public Response getRunningProfiles() throws Exception {
       String myprofile1 = PortalContainer.getProfiles().toString().trim();
       String myprofile = myprofile1.substring(1,myprofile1.length()-1);
    try {
      String profiles = "{\"profiles\":\"" + myprofile + "\"}";
            return Response.ok(profiles, MediaType.APPLICATION_JSON).cacheControl(cc).build();
    } catch (Exception e) {
      return Response.status(HTTPStatus.INTERNAL_ERROR).cacheControl(cc).build();
    }
  }
}
