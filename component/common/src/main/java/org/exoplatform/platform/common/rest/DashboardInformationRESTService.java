package org.exoplatform.platform.common.rest;

import java.net.URI;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.exoplatform.application.gadget.Gadget;
import org.exoplatform.application.gadget.GadgetRegistryService;
import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.config.UserPortalConfig;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.Application;
import org.exoplatform.portal.config.model.ApplicationType;
import org.exoplatform.portal.config.model.Container;
import org.exoplatform.portal.config.model.Dashboard;
import org.exoplatform.portal.config.model.ModelObject;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.TransientApplicationState;
import org.exoplatform.portal.mop.SiteKey;
import org.exoplatform.portal.mop.navigation.Scope;
import org.exoplatform.portal.mop.user.UserNavigation;
import org.exoplatform.portal.mop.user.UserNode;
import org.exoplatform.portal.mop.user.UserPortal;
import org.exoplatform.portal.mop.user.UserPortalContext;
import org.exoplatform.portal.pom.spi.portlet.Portlet;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.services.security.ConversationState;

/**
 * WS REST which permit to access to all user's dashboards
 * <p>
 * And for each dashboard, you can access to all gadgets installed
 * <p>
 * <ul>
 * <li>/dashboards to access to all dashboards</li>
 * <li>dashboards/{userName}/{dashboardName} to access to all gadgets from a dashboard</li>
 * </ul>
 * 
 * @author Clement
 *
 */
@Path(DashboardInformationRESTService.WS_ROOT_PATH)
public class DashboardInformationRESTService implements ResourceContainer {

  private Log logger = ExoLogger.getLogger(this.getClass());

  protected final static String WS_ROOT_PATH = "/dashboards";
  protected final static String STANDALONE_ROOT_PATH = "/standalone";
  protected final static String PORTAL_ROOT_PATH = "/portal";

  private final UserPortalConfigService userPortalConfigService;
  private final DataStorage dataStorageService;
  private final GadgetRegistryService gadgetRegistryService;
  
  private List<JsonGadgetInfo> gadgetsInfo;
  
  public DashboardInformationRESTService(UserPortalConfigService userPortalConfigService, DataStorage dataStorageService, GadgetRegistryService gadgetRegistryService) {
    this.userPortalConfigService = userPortalConfigService;
    this.dataStorageService = dataStorageService;
    this.gadgetRegistryService = gadgetRegistryService;
  }


  /*=======================================================================
   * WS REST methods
   *======================================================================*/
  
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @SuppressWarnings("unchecked")
  public Response getDashboards(@Context UriInfo uriInfo) {
    
    CacheControl cacheControl = new CacheControl();
    cacheControl.setNoCache(true);
    cacheControl.setNoStore(true);
    try {
      
      // Try to get all user nodes which corresponds to dashboards
      String userId = ConversationState.getCurrent().getIdentity().getUserId();
      UserNavigation userNavigation = getUserNavigation(userId);
      UserPortal userPortal = getUserPortal(userId);
      UserNode rootNode = userPortal.getNode(userNavigation, Scope.ALL, null, null);
      Collection<UserNode> nodes = rootNode.getChildren();
      
      // Fetch all nodes to add dashboards to the final list
      LinkedList<JsonDashboardInfo> list = new LinkedList<JsonDashboardInfo>();
      String wsSubPath = "";
      String dashboardSubPath = "";
      URI wsURI = null;
      URI dashboardURI = null;
      for(UserNode node : nodes){
        Application<Portlet> appDashboard = (Application<Portlet>) extractDashboard(dataStorageService.getPage(node.getPageRef()));
        
        // Dashboard only into TransientApplication
        if(appDashboard.getState() instanceof TransientApplicationState) {
          
          JsonDashboardInfo info = new JsonDashboardInfo();
          info.setId(node.getId());
          info.setLabel(node.getEncodedResolvedLabel());
          
          wsSubPath = WS_ROOT_PATH + "/" + userId + "/" + getPageName(node.getPageRef());
          wsURI = uriInfo.getBaseUriBuilder().path(wsSubPath).build();
          
          dashboardSubPath = PORTAL_ROOT_PATH + "/u/" + userId + "/" + node.getName();
          dashboardURI = uriInfo.getBaseUriBuilder().replaceMatrix(dashboardSubPath).build();

          info.setLink(wsURI.toString());
          info.setHtml(dashboardURI.toString());
          list.add(info);
        }
      }
      
      if (logger.isDebugEnabled()) {
        logger.debug("Getting Dashboards Information");
      }

      // Response to client
      return Response.ok(list, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();
    } 
    catch (Exception e) {
      logger.error("An error occured while getting dashboards information.", e);
      return Response.status(HTTPStatus.INTERNAL_ERROR).cacheControl(cacheControl).build();
    }
  }
  
  @GET
  @Path("/{userName}/{dashboardName}")
  @Produces(MediaType.APPLICATION_JSON)
  @SuppressWarnings("unchecked")
  public Response getGadgetInformation(@PathParam("userName") String userName, 
                                       @PathParam("dashboardName") String dashboardName,
                                       @Context UriInfo uriInfo) {
    
    // Initialize gadgetInfo list
    gadgetsInfo = new LinkedList<JsonGadgetInfo>();
    
    RequestLifeCycle.begin(PortalContainer.getInstance());

    CacheControl cacheControl = new CacheControl();
    cacheControl.setNoCache(true);
    cacheControl.setNoStore(true);
    try {
      if (logger.isDebugEnabled()) {
        logger.debug("Getting Gadgets Information");
      }
      
      Page page = dataStorageService.getPage("user::" + userName + "::" + dashboardName);
      if(page != null && page.getChildren() != null && page.getChildren().size() > 0) {
        Application<Portlet> appDashboard = (Application<Portlet>) extractDashboard(page);
        
        if(appDashboard != null) { 
          // Get gadgets from dashboard and fill it to the list
          Dashboard dashboard = dataStorageService.loadDashboard(appDashboard.getStorageId());
          
          // Extract all gadgets from dashboard
          extractGadgets(dashboard);
          
          // For each url into jsonObjects, modify some informations
          int i = 0;
          String standaloneSubPath = "";
          URI standaloneURI = null;
          String iconSubPath = "";
          URI iconURI = null;
          for(JsonGadgetInfo info : gadgetsInfo) {
            standaloneSubPath = info.getGadgetUrl();
            standaloneURI = uriInfo.getBaseUriBuilder().replaceMatrix(standaloneSubPath).build();
            info.setGadgetUrl(standaloneURI.toString());

            // We change icon url only if storage url is not completed
            if(info.getGadgetIcon() != null && info.getGadgetIcon().length() > 0 && !info.getGadgetIcon().startsWith("http")) {
              iconSubPath = info.getGadgetIcon();
              iconURI = uriInfo.getBaseUriBuilder().replaceMatrix(iconSubPath).build();
              info.setGadgetIcon(iconURI.toString());
            }
            
            gadgetsInfo.set(i++, info);
          }
        }
      }
      
      // Response to client
      return Response.ok(gadgetsInfo, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();
    } 
    catch (Exception e) {
      logger.error("An error occured while getting dashboards information.", e);
      return Response.status(HTTPStatus.INTERNAL_ERROR).cacheControl(cacheControl).build();
    }
    finally {
      try {
        RequestLifeCycle.end();
      } catch (Exception e) {
        logger.warn("An exception has occurred while proceed RequestLifeCycle.end() : " + e.getMessage());
      }
    }
  }
  
  

  /*=======================================================================
   * Private methods and classes
   *======================================================================*/

  /**
   * This recursive method extract all gadgets information from a container Tree to DTO
   * @throws Exception 
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  private void extractGadgets(Container container) throws Exception {
    
    if(container != null) {
      List<ModelObject> children = container.getChildren();
      for (Object child : children) {
        if (child instanceof Application) {
          Application application = (Application)child;
          if(ApplicationType.GADGET == application.getType()) {
            String gadgetName = dataStorageService.getId(application.getState());
            Gadget gadget = gadgetRegistryService.getGadget(gadgetName);

            JsonGadgetInfo info = new JsonGadgetInfo();
            info.setGadgetName(gadget.getName());
            info.setGadgetUrl(PORTAL_ROOT_PATH + STANDALONE_ROOT_PATH + "/" + application.getStorageId());
            info.setGadgetIcon(gadget.getThumbnail());
            info.setGadgetDescription(gadget.getDescription());
            gadgetsInfo.add(info);
          }
        }
        else if(child instanceof Container) {
          Container childContainer = (Container) child;
          extractGadgets(childContainer);
        }
      }
    }
  }
  
  /**
   * This recursive method extract a dashboard from a container Tree
   * @throws Exception 
   */
  @SuppressWarnings("rawtypes")
  private ModelObject extractDashboard(Container container) throws Exception {
    
    if(container != null) {
      List<ModelObject> children = container.getChildren();
      for (Object child : children) {
        if (child instanceof Application) {
          Application application = (Application)child;
          if(ApplicationType.PORTLET == application.getType()) {
            return application;
          }
        }
        else if(child instanceof Container) {
          Container childContainer = (Container) child;
          return extractDashboard(childContainer);
        }
      }
    }
    return null;
  }
  
  /**
   * Retrieve UserNavigation with an userID
   * @param userId
   * @return
   * @throws Exception
   */
  private UserNavigation getUserNavigation(String userId) throws Exception {
    UserPortal userPortal = getUserPortal(userId);
    return userPortal.getNavigation(SiteKey.user(userId));
  }
  
  /**
   * Retrieve a portal with an userID
   * @param userId
   * @return
   * @throws Exception
   */
  private UserPortal getUserPortal(String userId) throws Exception {
    UserPortalConfig portalConfig = userPortalConfigService.getUserPortalConfig(userPortalConfigService.getDefaultPortal(),
        userId, NULL_CONTEXT);
    return portalConfig.getUserPortal();
  }

  /**
   * DTO Object used to create JSON response
   * <p>
   * Represents a Dashboard
   * 
   * @author Clement
   *
   */
  public static class JsonDashboardInfo {
    String id;
    String label;
    String html;
    String link;
    
    public String getId() {
      return id;
    }
    public void setId(String id) {
      this.id = id;
    }
    public String getLabel() {
      return label;
    }
    public void setLabel(String label) {
      this.label = label;
    }
    public String getHtml() {
      return html;
    }
    public void setHtml(String html) {
      this.html = html;
    }
    public String getLink() {
      return link;
    }
    public void setLink(String link) {
      this.link = link;
    }
  }

  /**
   * DTO Object used to create JSON response
   * <p>
   * Represents a Gadget
   * 
   * @author Clement
   *
   */
  public static class JsonGadgetInfo {
    String gadgetName;
    String gadgetUrl;
    String gadgetIcon;
    String gadgetDescription;
    
    public String getGadgetName() {
      return gadgetName;
    }
    public void setGadgetName(String name) {
      this.gadgetName = name;
    }
    public String getGadgetUrl() {
      return gadgetUrl;
    }
    public void setGadgetUrl(String gadgetUrl) {
      this.gadgetUrl = gadgetUrl;
    }
    public String getGadgetIcon() {
      return gadgetIcon;
    }
    public void setGadgetIcon(String gadgetIcon) {
      this.gadgetIcon = gadgetIcon;
    }
    public String getGadgetDescription() {
      return gadgetDescription;
    }
    public void setGadgetDescription(String gadgetDescription) {
      this.gadgetDescription = gadgetDescription;
    }
  }

  // Don't need a portal context because webui isn't used
  private static final UserPortalContext NULL_CONTEXT = new UserPortalContext() {
    public ResourceBundle getBundle(UserNavigation navigation) {
      return null;
    }

    public Locale getUserLocale() {
      return Locale.ENGLISH;
    }
  };
  
  /**
   * Simple utility method to extract a page name from a page ref
   * @param pageRef
   * @return
   */
  private String getPageName(String pageRef) {
    String pageName = "";
    
    String[] refs = pageRef.split("::");
    pageName = refs[refs.length-1];
    
    return pageName;
  }
}
