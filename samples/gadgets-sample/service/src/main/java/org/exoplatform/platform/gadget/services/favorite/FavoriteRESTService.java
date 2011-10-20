package org.exoplatform.platform.gadget.services.favorite;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.exoplatform.ecm.utils.comparator.PropertyValueComparator;
import org.exoplatform.services.cms.documents.FavoriteService;
import org.exoplatform.services.cms.drives.DriveData;
import org.exoplatform.services.cms.drives.ManageDriveService;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.ecm.webui.utils.Utils;

/**
 * @author lamphan AUG 01, 2010
 */

@Path("/plf/favorite/")
public class FavoriteRESTService implements ResourceContainer {
  private final FavoriteService favoriteService;

  private ManageDriveService   manageDriveService;

  private static final String DATE_MODIFIED   = "exo:dateModified";

  private static final String TITLE   = "exo:title";

  private static final int    NO_PER_PAGE     = 10;

  /** The Constant LAST_MODIFIED_PROPERTY. */
  private static final String LAST_MODIFIED_PROPERTY = "Last-Modified";

  /** The Constant IF_MODIFIED_SINCE_DATE_FORMAT. */
  private static final String IF_MODIFIED_SINCE_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss z";
  
  private static final Log LOG = ExoLogger.getLogger(FavoriteRESTService.class
      .getName());

  public FavoriteRESTService(FavoriteService favoriteService, ManageDriveService manageDriveService) {
    this.favoriteService = favoriteService;
    this.manageDriveService = manageDriveService;
  }

  @GET
  @Path("/all/{repoName}/{workspaceName}/{userName}")
  public Response getFavoriteByUser(@PathParam("repoName") String repoName,
      @PathParam("workspaceName") String wsName,
      @PathParam("userName") String userName, @QueryParam("showItems") String showItems) throws Exception {
    List<FavoriteNode> listFavorites = new ArrayList<FavoriteNode>();
    List<DriveData> listDrive = manageDriveService.getAllDrives();
    
    if (showItems == null || showItems.trim().length() == 0) showItems = String.valueOf(NO_PER_PAGE);
    try {
      List<Node> listNodes = favoriteService.getAllFavoriteNodesByUser(wsName,
          repoName, userName);
      Collections.sort(listNodes, new PropertyValueComparator(DATE_MODIFIED, PropertyValueComparator.DESCENDING_ORDER));
      FavoriteNode favoriteNode;
      
      for (Node favorite : listNodes) {
        favoriteNode = new FavoriteNode();
        favoriteNode.setName(favorite.getName());
        favoriteNode.setTitle(getTitle(favorite));
        favoriteNode.setDateAddFavorite(getDateFormat(favorite.getProperty(DATE_MODIFIED).getDate()));
        favoriteNode.setDriveName(getDriveName(listDrive, favorite));
        favoriteNode.setPath(favorite.getPath());
        favoriteNode.setLinkImage("Icon16x16 default16x16Icon" + Utils.getNodeTypeIcon(favorite, "16x16Icon"));
        favoriteNode.setFullLink(createFullLink(favorite));

        if (favoriteNode != null) {
          if (listFavorites.size() < Integer.valueOf(showItems))
            listFavorites.add(favoriteNode);
        }
      }
      
    } catch (ItemNotFoundException e) {
      LOG.error(e);
    } catch (RepositoryException e) {
      LOG.error(e);
    } catch (Exception e) {
      LOG.error(e);
      return Response.serverError().build();
    }
    ListResultNode listResultNode = new ListResultNode();
    listResultNode.setListFavorite(listFavorites);

    DateFormat dateFormat = new SimpleDateFormat(IF_MODIFIED_SINCE_DATE_FORMAT);
    return Response.ok(listResultNode, new MediaType("application", "json"))
                   .header(LAST_MODIFIED_PROPERTY, dateFormat.format(new Date()))
                   .build();
  }
  
  
  public String createFullLink(Node node) throws Exception{
	  
	    String drivename = node.getSession().getWorkspace().getName();
	    ExoContainer container = ExoContainerContext.getCurrentContainer();
	    RepositoryService repoService = (RepositoryService)container.getComponentInstanceOfType(RepositoryService.class);
	    String repository = repoService.getCurrentRepository().getConfiguration().getName();
	    String nodePath = node.getPath();
	    String NodeUrl = new StringBuilder().append(repository)
	    					.append("/")
	                                        .append(drivename)
	                                        .append(nodePath)
	                                        .toString();
	    return NodeUrl;
  }


  private String getTitle(Node node) throws Exception {
    if (node.hasProperty(TITLE))
      return node.getProperty(TITLE).getString();
    return node.getName();
  }

  private String getDateFormat(Calendar date) {
    return String.valueOf(date.getTimeInMillis());
  }


  private String getDriveName(List<DriveData> listDrive, Node node) throws RepositoryException{
    String driveName = "";
    for (DriveData drive : listDrive) {
      if (node.getSession().getWorkspace().getName().equals(drive.getWorkspace())
          && node.getPath().contains(drive.getHomePath()) && drive.getHomePath().equals("/")) {
        driveName = drive.getName();
        break;
      }
    }
    return driveName;
  }

  public class ListResultNode {

    private List<? extends FavoriteNode> listFavorite;

    public List<? extends FavoriteNode> getListFavorite() {
      return listFavorite;
    }

    public void setListFavorite(List<? extends FavoriteNode> listFavorite) {
      this.listFavorite = listFavorite;
    }
  }
  
  public class FavoriteNode {

    private String name;
    private String nodePath;
    private String dateAddFavorite;
    private String driveName;
    private String title;
    private String linkImage;
    private String fullLink;
    
    
    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public void setPath(String path) {
      this.nodePath = path;
    }

    public String getPath() {
      return nodePath;
    }

    public void setDateAddFavorite(String dateAddFavorite) {
      this.dateAddFavorite = dateAddFavorite;
    }

    public String getDateAddFavorite() {
      return dateAddFavorite;
    }

    public void setDriveName(String driveName) {
      this.driveName = driveName;
    }

    public String getDriveName() {
      return driveName;
    }

    public void setTitle(String title) {
      this.title = title;
    }

    public String getTitle() {
      return title;
    }

    public void setFullLink(String fullLink) {
      this.fullLink = fullLink;
    }

    public String getFullLink() {
      return fullLink;
    }
    
    public void setLinkImage(String linkImage) {
      this.linkImage = linkImage;
    }

    public String getLinkImage() {
      return linkImage;
    }
  }
  }

