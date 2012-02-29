package org.exoplatform.platform.samples.website.extention.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.RootContainer;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.groovyscript.text.TemplateService;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.access.PermissionType;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.impl.core.NodeImpl;
import org.exoplatform.services.jcr.impl.core.SessionImpl;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.IdentityConstants;
import org.exoplatform.services.wcm.core.NodeLocation;
import org.exoplatform.services.wcm.publication.WCMComposer;
import org.exoplatform.services.wcm.utils.WCMCoreUtils;

@Path("/content-navigation")
public class ContentNavigationService implements ResourceContainer {

  private static final Log LOG = ExoLogger.getExoLogger(ContentNavigationService.class);

  // private String repository = null;
  private String workspace = null;
  private RepositoryService repositoryService_;
  private ManageableRepository manageableRepository_;

  public ContentNavigationService(RepositoryService rService, TemplateService templateService, InitParams params) {
    this.repositoryService_ = rService;

    // repository = params.getValueParam("repository").getValue();
    workspace = params.getValueParam("workspace").getValue();
  }

  /**
   * REST resource to retrieve navigation by content
   * 
   * @return
   */
  @GET
  @Path("/navigation/")
  @Produces(MediaType.APPLICATION_JSON)
  public JsonMenu getNavigation() {
    JsonMenu jsonMenu_ = new JsonMenu();
    List<JsonMenuNode> jsonMenuNodes = new ArrayList<JsonMenuNode>();

    try {
      manageableRepository_ = repositoryService_.getCurrentRepository();

      // open jcr session
      Session session = SessionProvider.createSystemProvider().getSession(workspace, manageableRepository_);

      // retrieve all exo:navigable nodes
      QueryManager queryManager = session.getWorkspace().getQueryManager();
      String strQuery = "SELECT * FROM exo:navigable";
      Query query = queryManager.createQuery(strQuery, Query.SQL);
      QueryResult queryResult = query.execute();
      NodeIterator iterRawNodes = queryResult.getNodes();

      ExoContainer exoContanier = RootContainer.getInstance().getPortalContainer("portal");
      WCMComposer wcmComposer = (WCMComposer) exoContanier.getComponentInstanceOfType(WCMComposer.class);
      // WCMComposer wcmComposer =
      // uicomponent.getApplicationComponent(WCMComposer.class);
      HashMap<String, String> filters = new HashMap<String, String>();
      filters.put(WCMComposer.FILTER_MODE, WCMComposer.MODE_LIVE);
      /*
       * filters.put(WCMComposer.FILTER_PRIMARY_TYPE, "exo:navigable");
       * filters.put(WCMComposer.FILTER_ORDER_BY, "exo:dateModified");
       * //filters.put(WCMComposer.FILTER_MODE, Utils.getCurrentMode());
       * filters.put(WCMComposer.FILTER_MODE, WCMComposer.MODE_LIVE);
       * filters.put(WCMComposer.FILTER_RECURSIVE, "true");
       * //filters.put(WCMComposer.FILTER_LANGUAGE,
       * Util.getPortalRequestContext().getLocale().getLanguage());
       * List<Node> rawNodes = wcmComposer.getContents(repository,
       * workspace, "", filters, WCMCoreUtils.getUserSessionProvider());
       */

      // convert raw Nodes to NavigableNode
      List<NavigableNode> allNodes = new ArrayList<NavigableNode>();
      while (iterRawNodes.hasNext()) {
        Node node = (Node) iterRawNodes.next();
        // check if this content is published
        // if(wcmComposer.getContent(repository, workspace, node.getPath(),
        // filters, WCMCoreUtils.getUserSessionProvider()) != null) {
        NavigableNode navigableNode = new NavigableNode(node);
        navigableNode.setNavigationNode(node.hasProperty("exo:navigationNode") ? node.getProperty("exo:navigationNode")
            .getValue().getString() : "");
        navigableNode.setClickable(node.hasProperty("exo:clickable") ? node.getProperty("exo:clickable").getValue().getBoolean()
            : false);
        navigableNode.setListUri(node.hasProperty("exo:page") ? node.getProperty("exo:page").getValue().getString() : "");
        navigableNode.setListParam(node.hasProperty("exo:pageParamId") ? node.getProperty("exo:pageParamId").getValue()
            .getString() : "");
        navigableNode.setDetailUri(node.hasProperty("exo:childrenPage") ? node.getProperty("exo:childrenPage").getValue()
            .getString() : "");
        navigableNode.setDetailParam(node.hasProperty("exo:childrenPageParamId") ? node.getProperty("exo:childrenPageParamId")
            .getValue().getString() : "");
        if (wcmComposer.getContent(workspace, node.getPath(), filters, WCMCoreUtils.getUserSessionProvider()) == null) {
          navigableNode.setViewableNode(false);
        } else {
          navigableNode.setViewableNode(true);
        }
        allNodes.add(navigableNode);
      }
      // }

      // loop on all exo:navigable nodes
      Iterator<NavigableNode> itNavigablenode = allNodes.iterator();
      while (itNavigablenode.hasNext()) {
        NavigableNode navigableNode = itNavigablenode.next();

        // is the node linked to a navigation node ?
        if (!StringUtils.isEmpty(navigableNode.getNavigationNode())) {

          JsonMenuNode jsonMenuNode = new JsonMenuNode();
          jsonMenuNode.setLabel(navigableNode.getNode().getName());
          jsonMenuNode.setUri(navigableNode.isClickable() ? navigableNode.getNavigationNode() : "#");
          jsonMenuNode.setViewable(navigableNode.isViewableNode());

          jsonMenuNode.setNavigationNode(navigableNode.getNavigationNode());

          // add child nodes
          jsonMenuNode.setNodes(getChildNodes(navigableNode, allNodes));

          jsonMenuNodes.add(jsonMenuNode);
        }
      }

      jsonMenu_.setMenu(jsonMenuNodes);

      // jcr session logout
      session.logout();
    } catch (Exception e) {
      LOG.error("Error while retrieving navigable nodes", e);
    }

    return jsonMenu_;
  }

  /**
   * Recursive method to get children nodes of a node
   * 
   * @param parentNode
   * @param nodes
   * @return List of children nodes
   * @throws RepositoryException
   */
  private List<JsonMenuNode> getChildNodes(NavigableNode parentNavigableNode, List<NavigableNode> allNodes)
      throws RepositoryException {

    List<JsonMenuNode> childNodes = new ArrayList<JsonMenuNode>();

    String parentNodePath = parentNavigableNode.getNode().getPath();

    ConversationState userState = ConversationState.getCurrent();
    Identity userIdentity = null;

    SessionImpl session = (SessionImpl) WCMCoreUtils.getUserSessionProvider().getSession(this.workspace,
        this.manageableRepository_);
    if (userState != null) {
      userIdentity = userState.getIdentity();
    } else {
      userIdentity = new Identity(IdentityConstants.ANONIM);
    }

    // loop on all nodes...
    Iterator<NavigableNode> itAllNodes = allNodes.iterator();
    while (itAllNodes.hasNext()) {
      NavigableNode navigableNode = itAllNodes.next();
      // check permissions
      if (session.getAccessManager().hasPermission(((NodeImpl) navigableNode.getNode()).getACL(), PermissionType.READ,
          userIdentity)) {
        String nodePath = navigableNode.getNode().getPath();

        // is the current node a subnode of 'parentNode' ?
        if (nodePath.length() > parentNodePath.length() && nodePath.indexOf(parentNodePath) == 0) {
          String subPath = nodePath.substring(parentNodePath.length());
          // is the current node a direct child of 'parentNode' ?
          if (subPath.substring(1).split("/").length == 1) {

            if (StringUtils.isEmpty(navigableNode.getListUri())) {
              navigableNode.setListUri(parentNavigableNode.getListUri());
            }
            if (StringUtils.isEmpty(navigableNode.getListParam())) {
              navigableNode.setListParam(parentNavigableNode.getListParam());
            }
            if (StringUtils.isEmpty(navigableNode.getDetailUri())) {
              navigableNode.setDetailUri(parentNavigableNode.getDetailUri());
            }
            if (StringUtils.isEmpty(navigableNode.getDetailParam())) {
              navigableNode.setDetailParam(parentNavigableNode.getDetailParam());
            }

            JsonMenuNode jsonSubMenuNode = buildNode(navigableNode);

            childNodes.add(jsonSubMenuNode);

            jsonSubMenuNode.setNodes(getChildNodes(navigableNode, allNodes));
          }
        } else if (navigableNode.getNode().hasProperty("jcr:uuid")) {
          // we can have symlink to this node located under the current
          // node
          // get symlinks of this node
          QueryManager queryManager = session.getWorkspace().getQueryManager();
          String strQuery = "SELECT * FROM exo:symlink WHERE exo:uuid = '" + navigableNode.getNode().getUUID() + "'";
          Query query = queryManager.createQuery(strQuery, Query.SQL);
          QueryResult queryResult = query.execute();
          NodeIterator iter = queryResult.getNodes();

          while (iter.hasNext()) {
            Node symlinkNode = (Node) iter.next();

            String symlinkNodePath = symlinkNode.getPath();
            if (symlinkNodePath.length() > parentNodePath.length() && symlinkNodePath.indexOf(parentNodePath) == 0) {
              String symlinkSubPath = symlinkNodePath.substring(parentNodePath.length());
              // is the current node a direct symlink child of 'parentNode'
              // ?
              if (symlinkSubPath.substring(1).split("/").length == 1) {
                if (StringUtils.isEmpty(navigableNode.getListUri())) {
                  navigableNode.setListUri(parentNavigableNode.getListUri());
                }
                if (StringUtils.isEmpty(navigableNode.getListParam())) {
                  navigableNode.setListParam(parentNavigableNode.getListParam());
                }
                if (StringUtils.isEmpty(navigableNode.getDetailUri())) {
                  navigableNode.setDetailUri(parentNavigableNode.getDetailUri());
                }
                if (StringUtils.isEmpty(navigableNode.getDetailParam())) {
                  navigableNode.setDetailParam(parentNavigableNode.getDetailParam());
                }

                JsonMenuNode jsonSubMenuNode = buildNode(navigableNode);

                childNodes.add(jsonSubMenuNode);

                jsonSubMenuNode.setNodes(getChildNodes(navigableNode, allNodes));
              }
            }
          }
        }
      }
    }

    Collections.sort(childNodes);

    return childNodes;
  }

  /**
   * Build and populate a JSON Menu node
   * 
   * @param node
   * @param parentNode
   * @return
   * @throws RepositoryException
   */
  private JsonMenuNode buildNode(NavigableNode navigableNode) throws RepositoryException {
    JsonMenuNode jsonSubMenuNode = new JsonMenuNode();

    Node node = navigableNode.getNode();

    if (node.hasProperty("exo:title")) {
      jsonSubMenuNode.setLabel(node.getProperty("exo:title").getString());
    } else {
      jsonSubMenuNode.setLabel(node.getName());
    }

    // if this node is clickable...
    if (navigableNode.isClickable()) {

      // get the node location
      NodeLocation nodeLocation = NodeLocation.getNodeLocationByNode(node);

      // path pattern is different if we display content dynamically in SCV
      // or CLV...
      if (node.isNodeType("nt:folder") || node.isNodeType("exo:taxonomy")) {
        if (navigableNode.isClickable()) {
          jsonSubMenuNode.setUri(navigableNode.getListUri());
          jsonSubMenuNode.setPageParamId(navigableNode.getListParam());
        }
        jsonSubMenuNode.setContentPath(nodeLocation.getRepository() + ":" + nodeLocation.getWorkspace() + ":" + node.getPath());
      } else {
        if (navigableNode.isClickable()) {
          jsonSubMenuNode.setUri(navigableNode.getDetailUri());
          jsonSubMenuNode.setPageParamId(navigableNode.getDetailParam());
        }
        jsonSubMenuNode.setContentPath("/" + nodeLocation.getRepository() + "/" + nodeLocation.getWorkspace() + node.getPath());
      }
    }

    if (node.hasProperty("exo:index")) {
      jsonSubMenuNode.setIndex(node.getProperty("exo:index").getLong());
    } else {
      jsonSubMenuNode.setIndex(Integer.MAX_VALUE);
    }
    /**/
    try {
      jsonSubMenuNode.setViewable(navigableNode.isViewableNode());
    } catch (Exception e) {
      LOG.error("Can not get isViewableNode property for navigableNode " + navigableNode.getNode().getPath(), e);
    }
    /**/

    return jsonSubMenuNode;
  }

  /**
   * Navigation by Content Menu
   * 
   * @author Thomas
   */
  public class JsonMenu {

    private List<JsonMenuNode> menu;

    public JsonMenu() {}

    public List<JsonMenuNode> getMenu() {
      return menu;
    }

    public void setMenu(List<JsonMenuNode> menu) {
      this.menu = menu;
    }

    /*
     * @Override public String toString() { StringBuffer sb = new
     * StringBuffer(); sb.append("menu: ["); boolean first = true; for
     * (JsonMenuNode node : nodes) { if (!first) { sb.append(","); }
     * sb.append(node.toString()); } sb.append("]"); //
     * sb.append("menu:{").
     * append("label: ").append(label).append(" ").append
     * ("uri: ").append(uri).append(" "); // sb.append(" ").append("} ");
     * return sb.toString(); }
     */
  }

  /**
   * Navigation by Content Node
   * 
   * @author Thomas
   */
  public class JsonMenuNode implements Comparable<JsonMenuNode> {
    private String label;
    private String uri;
    private String navigationNode;
    private long index;
    private String pageParamId;
    private String contentPath;
    /** is the menu node viewable or not */
    private boolean isViewable;

    private List<JsonMenuNode> nodes;

    public JsonMenuNode() {}

    public JsonMenuNode(String label, String uri, String navigationNode) {
      super();
      this.label = label;
      this.uri = uri;
      this.navigationNode = navigationNode;
    }

    /*
     * @Override public String toString() { StringBuffer sb = new
     * StringBuffer();
     * sb.append("label: ").append(label).append(" ").append
     * ("uri: ").append(uri).append(" "); return sb.toString(); }
     */

    public String getLabel() {
      return label;
    }

    public void setLabel(String label) {
      this.label = label;
    }

    public String getUri() {
      return uri;
    }

    public void setUri(String uri) {
      this.uri = uri;
    }

    public List<JsonMenuNode> getNodes() {
      return nodes;
    }

    public void setNodes(List<JsonMenuNode> nodes) {
      this.nodes = nodes;
    }

    public String getNavigationNode() {
      return navigationNode;
    }

    public void setNavigationNode(String navigationNode) {
      this.navigationNode = navigationNode;
    }

    public void setIndex(long index) {
      this.index = index;
    }

    public String getPageParamId() {
      return pageParamId;
    }

    public void setPageParamId(String pageParamId) {
      this.pageParamId = pageParamId;
    }

    public String getContentPath() {
      return contentPath;
    }

    public void setContentPath(String contentPath) {
      this.contentPath = contentPath;
    }

    public int compareTo(JsonMenuNode o) {
      return (int) (this.index - o.index);
    }

    public boolean isViewable() {
      return isViewable;
    }

    public void setViewable(boolean isViewable) {
      this.isViewable = isViewable;
    }

  }
}