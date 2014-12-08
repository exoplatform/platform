package org.exoplatform.platform.common.rest;

import org.exoplatform.application.gadget.Gadget;
import org.exoplatform.application.gadget.GadgetRegistryService;
import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.platform.common.navigation.NavigationUtils;
import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.config.model.Application;
import org.exoplatform.portal.config.model.*;
import org.exoplatform.portal.mop.description.DescriptionService;
import org.exoplatform.portal.mop.navigation.NavigationService;
import org.exoplatform.portal.pom.spi.portlet.Portlet;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.services.security.ConversationState;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;

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

  private static final Log LOG = ExoLogger.getLogger(DashboardInformationRESTService.class);

  protected final static String WS_ROOT_PATH = "/dashboards";
  protected final static String STANDALONE_ROOT_PATH = "/standalone";

  private final DataStorage dataStorageService;
  private final GadgetRegistryService gadgetRegistryService;
  
  private List<JsonGadgetInfo> gadgetsInfo;
    private NavigationService navigationService_;
    private DescriptionService descriptionService_;
    private PageNavigation navigation;

    public DashboardInformationRESTService(DataStorage dataStorageService, GadgetRegistryService gadgetRegistryService,DescriptionService descriptionService,NavigationService navigationService) {
   this.dataStorageService = dataStorageService;
    this.gadgetRegistryService = gadgetRegistryService;
      this.navigationService_= navigationService;
      this.descriptionService_= descriptionService;
  }


  /*=======================================================================
   * WS REST methods
   *======================================================================*/
  
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("users")
  @SuppressWarnings("unchecked")
  public Response getDashboards(@Context UriInfo uriInfo) {

      CacheControl cacheControl = new CacheControl();
      cacheControl.setNoCache(true);
      cacheControl.setNoStore(true);
      try {
          LinkedList<JsonDashboardInfo> list = new LinkedList<JsonDashboardInfo>();
          // Try to get all user nodes which corresponds to dashboards
          String userId = ConversationState.getCurrent().getIdentity().getUserId();
          //Loading User Navigation only
          navigation = NavigationUtils.loadPageNavigation(userId,navigationService_, descriptionService_);

      // --- There is at least one user navigation
      if (navigation != null) {
          //Get navigations
          List<NavigationFragment> fragments = navigation.getFragments() ;
          //Get dashboard tabs
          for (NavigationFragment frag:fragments) {
              List<PageNode> pagesNode=frag.getNodes();
              // Fetch all nodes to add dashboards to the final list
              String wsSubPath = "";
              String dashboardSubPath = "";
              URI wsURI = null;
              URI dashboardURI = null;
              for (PageNode pageNode:pagesNode){
                  Application<Portlet> appDashboard = (Application<Portlet>) extractDashboard(dataStorageService.getPage(pageNode.getPageReference()));
                  if(appDashboard == null) {
                    continue;
                  }
                  // Dashboard only into TransientApplication
                  if(appDashboard.getState() instanceof TransientApplicationState) {
                      JsonDashboardInfo info = new JsonDashboardInfo();
                      info.setId(pageNode.getName());
                      info.setLabel(pageNode.getLabel());
                      // Create URI to WS REST
                      wsSubPath = PortalContainer.getCurrentRestContextName() + "/private" + WS_ROOT_PATH + "/" + userId + "/" + getPageName(pageNode.getPageReference());
                      wsURI = uriInfo.getBaseUriBuilder().replaceMatrix(wsSubPath).build();
                      // Create URI to dashboard into portal
                      dashboardSubPath = PortalContainer.getCurrentPortalContainerName() + "/u/" + userId + "/" + pageNode.getName();
                      dashboardURI = uriInfo.getBaseUriBuilder().replaceMatrix(dashboardSubPath).build();

                      info.setLink(wsURI.toString());
                      info.setHtml(dashboardURI.toString());
                      list.add(info);
                  }
              }
          }
          if (LOG.isDebugEnabled()) {
              LOG.debug("Getting Dashboards Information");
          }
      }
      // Response to client
      return Response.ok(list, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();
    } catch (Exception e) {
        LOG.error("An error occured while getting dashboards information.", e);
        return Response.status(HTTPStatus.INTERNAL_ERROR).cacheControl(cacheControl).build();
    }
  }
  
  @GET
  @Path("/{userName}/{dashboardName}")
  @Produces(MediaType.APPLICATION_JSON)
  @SuppressWarnings("unchecked")
  @RolesAllowed("users")
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
      if (LOG.isDebugEnabled()) {
        LOG.debug("Getting Gadgets Information");
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
      LOG.error("An error occured while getting dashboards information.", e);
      return Response.status(HTTPStatus.INTERNAL_ERROR).cacheControl(cacheControl).build();
    }
    finally {
      try {
        RequestLifeCycle.end();
      } catch (Exception e) {
        LOG.warn("An exception has occurred while proceed RequestLifeCycle.end() : " + e.getMessage());
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
      if(children != null) {
        for (Object child : children) {
          if (child instanceof Application) {
            Application application = (Application)child;
            if(ApplicationType.GADGET == application.getType()) {
              String gadgetName = dataStorageService.getId(application.getState());
              Gadget gadget = gadgetRegistryService.getGadget(gadgetName);
  
              if(gadget != null) {
                JsonGadgetInfo info = new JsonGadgetInfo();
                info.setGadgetName(gadget.getTitle());
                info.setGadgetUrl(PortalContainer.getCurrentPortalContainerName() + STANDALONE_ROOT_PATH + "/" + application.getStorageId());
                info.setGadgetIcon(gadget.getThumbnail());
                info.setGadgetDescription(gadget.getDescription());
                gadgetsInfo.add(info);
              }
              else {
                LOG.warn("Gadget with name " + gadgetName + " is no longer registered in Gadget Registry");
              }
            }
          }
          else if(child instanceof Container) {
            Container childContainer = (Container) child;
            extractGadgets(childContainer);
          }
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
