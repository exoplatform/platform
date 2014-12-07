package org.exoplatform.platform.common.rest;

import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.exoplatform.application.gadget.Gadget;
import org.exoplatform.application.gadget.GadgetRegistryService;
import org.exoplatform.application.registry.Application;
import org.exoplatform.application.registry.ApplicationRegistryService;
import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.portal.config.model.ApplicationType;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;

/**
 * WS REST which permit to access to all applications
 * 
 * @author Clement
 *
 */
@Path(ApplicationRESTService.WS_ROOT_PATH)
public class ApplicationRESTService implements ResourceContainer {

  private static final Log LOG = ExoLogger.getLogger(ApplicationRESTService.class);

  protected final static String WS_ROOT_PATH = "/allapps";
  protected final static String STANDALONE_ROOT_PATH = "/standalone";

  private final ApplicationRegistryService appRegistryService;
  private final GadgetRegistryService gadgetRegistryService;
  
  public ApplicationRESTService(GadgetRegistryService gadgetRegistryService, ApplicationRegistryService appRegistryService) {
    this.gadgetRegistryService = gadgetRegistryService;
    this.appRegistryService = appRegistryService;
  }


  /*=======================================================================
   * WS REST methods
   *======================================================================*/
  
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response getDashboards(@Context UriInfo uriInfo) {
    
    CacheControl cacheControl = new CacheControl();
    cacheControl.setNoCache(true);
    cacheControl.setNoStore(true);
    try {

      LinkedList<JsonAppInfo> list = new LinkedList<JsonAppInfo>();
      
      List<Application> apps = this.appRegistryService.getAllApplications();
      if(apps != null) {
        JsonAppInfo info;
        for(Application app : apps) {
          info = new JsonAppInfo();
          if(ApplicationType.GADGET == app.getType()) {
            Gadget gadget = gadgetRegistryService.getGadget(app.getContentId());
            if(gadget != null) {
              info.setName(gadget.getName());
              info.setUrl((new StringBuilder()).append(PortalContainer.getCurrentPortalContainerName()).append("/standalone").append("/").append(app.getStorageId()).toString());
              info.setIcon(gadget.getThumbnail());
              info.setDescription(gadget.getDescription());
            } 
            else {
              LOG.warn((new StringBuilder()).append("Gadget with name ").append(app.getApplicationName()).append(" is no longer registered in Gadget Registry").toString());
            }
          }
          else {
            info.setName(app.getApplicationName());
            info.setDescription(app.getDescription());
            info.setIcon(app.getIconURL());
          }
          info.setType(app.getType().getName());
          list.add(info);
        }
      }
      
      if (LOG.isDebugEnabled()) {
        LOG.debug("Getting Apps Information");
      }

      // Response to client
      return Response.ok(list, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();
    }
    catch (Exception e) {
      LOG.error("An error occured while getting apps information.", e);
      return Response.status(HTTPStatus.INTERNAL_ERROR).cacheControl(cacheControl).build();
    }
  }

  /**
   * DTO Object used to create JSON response
   * <p>
   * Represents an app
   * 
   * @author Clement
   *
   */
  public static class JsonAppInfo {
    String name;
    String url;
    String icon;
    String description;
    String type;
    
    public String getName() {
      return name;
    }
    public void setName(String name) {
      this.name = name;
    }
    public String getUrl() {
      return url;
    }
    public void setUrl(String url) {
      this.url = url;
    }
    public String getIcon() {
      return icon;
    }
    public void setIcon(String icon) {
      this.icon = icon;
    }
    public String getDescription() {
      return description;
    }
    public void setDescription(String description) {
      this.description = description;
    }
    public String getType() {
      return type;
    }
    public void setType(String type) {
      this.type = type;
    }
  }
}
