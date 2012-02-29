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
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.exoplatform.ecm.utils.comparator.PropertyValueComparator;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.jcr.ext.hierarchy.NodeHierarchyCreator;
import org.exoplatform.services.jcr.ext.app.SessionProviderService;
import org.exoplatform.services.cms.link.LinkManager;
import org.json.JSONObject;

/**
 * @author lamphan AUG 01, 2010
 */

@Path("/plf/favorite/")
public class FavoriteRESTService implements ResourceContainer {

  //private ManageDriveService   manageDriveService;

  private static final String DATE_MODIFIED   = "exo:dateModified";

  private static final String TITLE   = "exo:title";

  private static final int    NO_PER_PAGE     = 10;

  /** The Constant LAST_MODIFIED_PROPERTY. */
  private static final String LAST_MODIFIED_PROPERTY = "Last-Modified";

  /** The Constant IF_MODIFIED_SINCE_DATE_FORMAT. */
  private static final String IF_MODIFIED_SINCE_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss z";
  
  private static final Log LOG = ExoLogger.getLogger(FavoriteRESTService.class.getName());
  
  final static public String[] SPECIFIC_FOLDERS = { "exo:musicFolder", "exo:videoFolder", "exo:pictureFolder", "exo:documentFolder", "exo:searchFolder" };

 /* public FavoriteRESTService(FavoriteService favoriteService, ManageDriveService manageDriveService) {
    this.favoriteService = favoriteService;
    this.manageDriveService = manageDriveService;
  }*/
  
  @GET
  @Path("/get-documents/{userName}")
  public Response getFavoriteByUser(@PathParam("userName") String userName, @QueryParam("showItems") String showItems) throws Exception {
    List<FavoriteNode> listFavorites = new ArrayList<FavoriteNode>();
    List<Node> listNodes = new ArrayList<Node>();
    
    if (showItems == null || showItems.trim().length() == 0) showItems = String.valueOf(NO_PER_PAGE);
    try {
      NodeHierarchyCreator nodeHierarchyCreator = (NodeHierarchyCreator)ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(NodeHierarchyCreator.class);
      SessionProviderService sessionProviderService = (SessionProviderService)ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(SessionProviderService.class);
        
      Node userNode = nodeHierarchyCreator.getUserNode(sessionProviderService.getSystemSessionProvider(null), userName);
      Node privateDrive = userNode.getNode("Private");
      Node publicDrive = userNode.getNode("Public");
      getListDocument(privateDrive, listNodes);
      getListDocument(publicDrive, listNodes);
      
      Collections.sort(listNodes, new PropertyValueComparator(DATE_MODIFIED, PropertyValueComparator.DESCENDING_ORDER));
      FavoriteNode favoriteNode;
      
      for (Node favorite : listNodes) {
        favoriteNode = new FavoriteNode();
        favoriteNode.setName(favorite.getName());
        favoriteNode.setTitle(getTitle(favorite));
        favoriteNode.setDateAddFavorite(getDateFormat(favorite.getProperty(DATE_MODIFIED).getDate()));
        favoriteNode.setNodePath(favorite.getPath());
        String linkImage = "Icon16x16 default16x16Icon" + getNodeTypeIcon(favorite, "16x16Icon");
        favoriteNode.setLinkImage(linkImage);

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
    return Response.ok(listResultNode, new MediaType("application", "json")).header(LAST_MODIFIED_PROPERTY, dateFormat.format(new Date())).build();
  }

  @GET
  @Path("/all/{repoName}/{workspaceName}/{userName}")
  public Response getFavoriteByUser(@PathParam("repoName") String repoName,
      @PathParam("workspaceName") String wsName,
      @PathParam("userName") String userName, @QueryParam("showItems") String showItems) throws Exception {
    List<FavoriteNode> listFavorites = new ArrayList<FavoriteNode>();
    
    if (showItems == null || showItems.trim().length() == 0) showItems = String.valueOf(NO_PER_PAGE);
    try {
      NodeHierarchyCreator nodeHierarchyCreator = (NodeHierarchyCreator)ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(NodeHierarchyCreator.class);
      SessionProviderService sessionProviderService = (SessionProviderService)ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(SessionProviderService.class);
        
      Node userNode = nodeHierarchyCreator.getUserNode(sessionProviderService.getSystemSessionProvider(null), userName);
      
      List<Node> listNodes = getAllFavoritesByUser(wsName, repoName, userNode);
      Collections.sort(listNodes, new PropertyValueComparator(DATE_MODIFIED, PropertyValueComparator.DESCENDING_ORDER));
      FavoriteNode favoriteNode;
      
      for (Node favorite : listNodes) {
        favoriteNode = new FavoriteNode();
        favoriteNode.setName(favorite.getName());
        favoriteNode.setTitle(getTitle(favorite));
        favoriteNode.setDateAddFavorite(getDateFormat(favorite.getProperty(DATE_MODIFIED).getDate()));
        favoriteNode.setNodePath(favorite.getPath());
        String linkImage = "Icon16x16 default16x16Icon" + getNodeTypeIcon(favorite, "16x16Icon");
        favoriteNode.setLinkImage(linkImage);
        
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
    return Response.ok(listResultNode, new MediaType("application", "json")).header(LAST_MODIFIED_PROPERTY, dateFormat.format(new Date())).build();
  }
  
  
  @GET
  @Path("/favorite-folder/{userName}")
  
  public Response getFavoriteNode(@PathParam("userName") String userName) throws Exception {
    
    try {
      NodeHierarchyCreator nodeHierarchyCreator = (NodeHierarchyCreator)ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(NodeHierarchyCreator.class);
      SessionProviderService sessionProviderService = (SessionProviderService)ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(SessionProviderService.class);
        
      Node userNode = nodeHierarchyCreator.getUserNode(sessionProviderService.getSystemSessionProvider(null), userName);
      Node favoriteNode = userNode.getNode("Private/Favorites");
      
      JSONObject json = new JSONObject();                    
      json.put("name", "favoriteNode");
      json.put("value", favoriteNode.getPath());
      
      DateFormat dateFormat = new SimpleDateFormat(IF_MODIFIED_SINCE_DATE_FORMAT);
      return Response.ok(json.toString(), MediaType.APPLICATION_JSON).header(LAST_MODIFIED_PROPERTY, dateFormat.format(new Date())).build();
      
    } catch (Exception e) {
        LOG.error(e);
        return Response.serverError().build();
      }
  }
  
  public static String getNodeTypeIcon(Node node, String appended) throws RepositoryException {
	    StringBuilder str = new StringBuilder();
	    
	    if (node == null) return "";
	    
	    String nodeType = node.getPrimaryNodeType().getName();
	    
	    if (node.isNodeType("exo:symlink")) {
		    LinkManager linkManager = (LinkManager)ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(LinkManager.class);
	      try {
	        nodeType = node.getProperty("exo:primaryType").getString();
	        node = linkManager.getTarget(node);
	        if (node == null)
	          return "";
	      } catch (Exception e) {
	        return "";
	      }
	    }
	    
	    nodeType = nodeType.replace(':', '_') + appended;
	    str.append(nodeType);
	    str.append(" ");
	    str.append("default16x16Icon");
	    if (node.isNodeType("nt:file")) {
	      if (node.hasNode("jcr:content")) {
	        Node jcrContentNode = node.getNode("jcr:content");
	        str.append(' ').append(
	            jcrContentNode.getProperty("jcr:mimeType").getString().replaceAll(
	                "/|\\.", "_")).append(appended);
	      }
	    }
	    return str.toString();
	  }
  
  public void getListDocument(Node node, List<Node> listNodes) throws RepositoryException {
	    LinkManager linkManager = (LinkManager)ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(LinkManager.class);
	    String primaryType = node.getProperty("jcr:primaryType").getString();
	    if (primaryType.contains("nt:file") || linkManager.isLink(node)) {
	    		listNodes.add(node);
	        return;
	        }
	    else
	    {
	    	if (node.hasNodes()){
	    	        NodeIterator childNodes = node.getNodes();
	                while (childNodes.hasNext()) {
	                        Node childNode = childNodes.nextNode();
	                        getListDocument(childNode, listNodes);            
	                }
	    	}
	    }
	}
  
  public List<Node> getAllFavoritesByUser(String workspace, String repository, Node userNode) throws Exception {
	    List<Node> ret = new ArrayList<Node>();
	    LinkManager linkManager = (LinkManager)ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(LinkManager.class);
	    NodeHierarchyCreator nodeHierarchyCreator = (NodeHierarchyCreator)ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(NodeHierarchyCreator.class);
	    String favoritePath = nodeHierarchyCreator.getJcrPath("userPrivateFavorites");
	    Node favoriteFolder= userNode.getNode(favoritePath);
	      
	    NodeIterator nodeIter = favoriteFolder.getNodes();
	    while (nodeIter.hasNext()) {
	      Node childNode = nodeIter.nextNode();
	      String primaryType = childNode.getProperty("jcr:primaryType").getString();
	      if (linkManager.isLink(childNode) || primaryType.contains("nt:file")) {
	        ret.add(childNode);
	      }
	    }
	    return ret;
	  }
  
 /* public String createFullLink(Node userNode, Node document) throws Exception{
	  
    	String userPath = userNode.getPath();
      	String favoritePath = document.getPath();
      	String NodeUrl = favoritePath.substring(userPath.length()+1);
	    return NodeUrl;
  }*/


  private String getTitle(Node node) throws Exception {
    if (node.hasProperty(TITLE))
      return node.getProperty(TITLE).getString();
    return node.getName();
  }

  private String getDateFormat(Calendar date) {
    return String.valueOf(date.getTimeInMillis());
  }

/*
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
  }*/

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
    private String title;
    private String linkImage;
    
    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public void setNodePath(String nodePath) {
      this.nodePath = nodePath;
    }

    public String getNodePath() {
      return nodePath;
    }

    public void setDateAddFavorite(String dateAddFavorite) {
      this.dateAddFavorite = dateAddFavorite;
    }

    public String getDateAddFavorite() {
      return dateAddFavorite;
    }
/*
    public void setDriveName(String driveName) {
      this.driveName = driveName;
    }

    public String getDriveName() {
      return driveName;
    }*/

    public void setTitle(String title) {
      this.title = title;
    }

    public String getTitle() {
      return title;
    }
    
    public void setLinkImage(String linkImage) {
      this.linkImage = linkImage;
    }

    public String getLinkImage() {
      return linkImage;
    }
  }
  }
