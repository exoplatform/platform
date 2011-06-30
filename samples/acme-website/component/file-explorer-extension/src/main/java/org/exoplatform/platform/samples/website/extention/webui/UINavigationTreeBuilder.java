package org.exoplatform.platform.samples.website.extention.webui;

import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.jcr.Node;
import javax.jcr.NodeIterator;

import org.exoplatform.platform.webui.navigation.TreeNode;
import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.mop.SiteKey;
import org.exoplatform.portal.mop.SiteType;
import org.exoplatform.portal.mop.navigation.Scope;
import org.exoplatform.portal.mop.user.UserNavigation;
import org.exoplatform.portal.mop.user.UserNode;
import org.exoplatform.portal.mop.user.UserPortal;
import org.exoplatform.portal.webui.container.UIContainer;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.services.resources.LocaleConfig;
import org.exoplatform.services.resources.LocaleConfigService;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UITree;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

@ComponentConfig(events = @EventConfig(listeners = UINavigationTreeBuilder.ChangeNodeActionListener.class))
public class UINavigationTreeBuilder extends UIContainer {

  private UserNavigation edittedNavigation;

  private TreeNode   edittedTreeNodeData;

  /** The current node. */
  protected UserNode     currentNode; 

  /**
   * Instantiates a new uI navigation tree builder.
   * 
   * @throws Exception the exception
   */
  public UINavigationTreeBuilder() throws Exception {

    UITree uiTree = addChild(UINavigationTree.class, null, UINavigationTree.class.getSimpleName() + hashCode());
    uiTree.setIcon("DefaultPageIcon");
    uiTree.setSelectedIcon("DefaultPageIcon");
    uiTree.setBeanIdField("URI");
    uiTree.setBeanLabelField("encodedResolvedLabel");
    uiTree.setBeanIconField("icon");
    uiTree.setUIRightClickPopupMenu(null);

    UserNavigation edittedNavigation = getUserPortal().getNavigation(SiteKey.portal(getSiteName()));
    setEdittedNavigation(edittedNavigation);
    initTreeData();
  }

  private UserPortal getUserPortal() {
    UserPortal userPortal = Util.getUIPortalApplication().getUserPortalConfig().getUserPortal();
    return userPortal;
  }
  
  public String getSiteName() throws UnsupportedEncodingException{
    PortalRequestContext prContext = Util.getPortalRequestContext();
    String portalName = URLEncoder.encode(prContext.getPortalOwner(), "UTF-8");
    return portalName;
  }

  /**
   * Initializes the UITree wrapped in UINavigationSelector and localize the label
   * 
   * @throws Exception
   */
  public void initTreeData() throws Exception {
    WebuiRequestContext requestContext = WebuiRequestContext.getCurrentInstance();
    localizeNavigation(requestContext.getLocale());

    initEdittedTreeNodeData();
  }

  /**
   * Initializes the edited node as well as its parent, navigation
   * 
   * @throws Exception
   */
  @SuppressWarnings("deprecation")
  private void initEdittedTreeNodeData() throws Exception {
    if (edittedNavigation == null) {
      return;
    }
    if (edittedTreeNodeData == null) {
      edittedTreeNodeData = new TreeNode(edittedNavigation, Util.getUIPortal().getSelectedUserNode());
    }

    UITree tree = getChild(UITree.class);
    tree.setSibbling(getNodes(edittedNavigation));
  }

  private Collection<UserNode> getNodes(UserNavigation navigation) {
    return getUserPortal().getNode(navigation, Scope.ALL, null, null).getChildren();
  }

  private void localizeNavigation(Locale locale) {
    LocaleConfig localeConfig = getApplicationComponent(LocaleConfigService.class).getLocaleConfig(locale.getLanguage());

    SiteType ownerType = edittedNavigation.getKey().getType();
    if (!SiteType.USER.equals(ownerType)) {
      String ownerId = edittedNavigation.getKey().getName();
      if (SiteType.GROUP.equals(ownerType)) {
        // Remove the trailing '/' for a group
        ownerId = ownerId.substring(1);
      }
      ResourceBundle res = localeConfig.getNavigationResourceBundle(ownerType.getName(), ownerId);
      for (UserNode node : getNodes(edittedNavigation)) {
        resolveLabel(res, node);
      }
    }
  }

  private void resolveLabel(ResourceBundle res, UserNode node) {
//    node.setResolvedLabel(res);
    if (node.getChildren() == null) {
      return;
    }
    for (UserNode childNode : node.getChildren()) {
      resolveLabel(res, childNode);
    }
  }

  public void selectUserNodeByUri(String uri) {
    if (edittedTreeNodeData == null) {
      return;
    }
    UITree tree = getChild(UITree.class);
    Collection<?> sibbling = tree.getSibbling();
    tree.setSibbling(null);
    tree.setParentSelected(null);
    edittedTreeNodeData.setPageRef(searchUserNodeByUri(edittedTreeNodeData.getPageNavigation(), uri).getPageRef());
    if (edittedTreeNodeData.getNode() != null) {
      tree.setSelected(edittedTreeNodeData.getNode());
      tree.setChildren(edittedTreeNodeData.getNode().getChildren());
      return;
    }
    tree.setSelected(null);
    tree.setChildren(null);
    tree.setSibbling(sibbling);
  }

  public UserNode searchUserNodeByUri(UserNavigation pageNav, String uri) {
    if (pageNav == null || uri == null) {
      return null;
    }
    Collection<UserNode> UserNodes = getNodes(pageNav);
    UITree uiTree = getChild(UITree.class);
    for (UserNode ele : UserNodes) {
      UserNode returnUserNode = searchUserNodeByUri(ele, uri, uiTree);
      if (returnUserNode == null) {
        continue;
      }
      if (uiTree.getSibbling() == null) {
        uiTree.setSibbling(UserNodes);
      }
      return returnUserNode;
    }
    return null;
  }

  private UserNode searchUserNodeByUri(UserNode userNode, String uri, UITree tree) {
    if (userNode.getURI().equals(uri)) {
      return userNode;
    }
    Collection<UserNode> children = userNode.getChildren();
    if (children == null) {
      return null;
    }
    for (UserNode ele : children) {
      UserNode returnUserNode = searchUserNodeByUri(ele, uri, tree);
      if (returnUserNode == null) {
        continue;
      }
      if (tree.getSibbling() == null) {
        tree.setSibbling(children);
      }
      if (tree.getParentSelected() == null) {
        tree.setParentSelected(userNode);
      }
      edittedTreeNodeData.getParent().setPageRef(userNode.getPageRef());
      return returnUserNode;
    }
    return null;
  }

  /**
   * Builds the tree.
   * 
   * @throws Exception the exception
   */

  public void buildTree() throws Exception {
    NodeIterator sibbling = null;
    UINavigationTree tree = getChild(UINavigationTree.class);
    UserNode selectedNode = getSelectedUserNode();
    tree.setSelected(selectedNode);
 
    if (sibbling != null) {
      tree.setSibbling(filfer(sibbling));
    }
  }

  private List<Node> filfer(final NodeIterator iterator) throws Exception {
    List<Node> list = new ArrayList<Node>();

    for (; iterator.hasNext();) {
      Node sibbling = iterator.nextNode();
      if (sibbling.isNodeType("exo:hiddenable")) continue;
      list.add(sibbling);
    }
    
    return list;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.exoplatform.webui.core.UIComponent#processRender(org.exoplatform.webui.application.WebuiRequestContext)
   */
  public void processRender(WebuiRequestContext context) throws Exception {
    Writer writer = context.getWriter();
    writer.write("<div class=\"Explorer\">");
    writer.write("<div class=\"ExplorerTree\">");
    writer.write("<div class=\"InnerExplorerTree\">");

    buildTree();
    super.renderChildren();

    writer.write("</div>");
    writer.write("</div>");
    writer.write("</div>");
  }

  /**
   * When a node is change in tree. This method will render the children &
   * sibbling nodes of current node and broadcast change node event to other
   * uicomponent
   * 
   * @param uri the path
   * @param requestContext the request context
   * @throws Exception the exception
   */
  public void changeNode(String uri, Object context) throws Exception {

    currentNode = searchUserNodeByUri(edittedTreeNodeData.getPageNavigation(), uri);
    broadcastOnChange(currentNode, context);
  }

  /**
   * Broadcast on change.
   * 
   * @param navigationNode the node
   * @param requestContext the request context
   * @throws Exception the exception
   */
  public void broadcastOnChange(UserNode navigationNode, Object context) throws Exception {
    UINavigationSelector nodeTreeSelector = getAncestorOfType(UINavigationSelector.class);
    nodeTreeSelector.onChange(navigationNode, context);
  }

  public void setEdittedNavigation(UserNavigation _filteredEdittedNavigation) throws Exception {
    this.edittedNavigation = _filteredEdittedNavigation;
  }

  public UserNavigation getEdittedNavigation() {
    return this.edittedNavigation;
  }

  public TreeNode getSelectedNode() {
    return edittedTreeNodeData;
  }

  public UserNavigation getSelectedNavigation() {
    return edittedTreeNodeData == null ? null : edittedTreeNodeData.getPageNavigation();
  }

  public UserNode getSelectedUserNode() {
    return edittedTreeNodeData == null ? null : edittedTreeNodeData.getNode();
  }

  public String getUpLevelUri() {
    return edittedTreeNodeData.getParent().getURI();
  }
  
  /**
   * The listener interface for receiving changeNodeAction events. The class
   * that is interested in processing a changeNodeAction event implements this
   * interface, and the object created with that class is registered with a
   * component using the component's
   * <code>addChangeNodeActionListener<code> method. When
   * the changeNodeAction event occurs, that object's appropriate
   * method is invoked.
   * 
   * @see ChangeNodeActionEvent
   */
  static public class ChangeNodeActionListener extends EventListener<UITree> {

    /*
     * (non-Javadoc)
     * 
     * @see org.exoplatform.webui.event.EventListener#execute(org.exoplatform.webui.event.Event)
     */
    public void execute(Event<UITree> event) throws Exception {
      UINavigationTreeBuilder builder = event.getSource().getParent();
      String uri = event.getRequestContext().getRequestParameter(OBJECTID);
      builder.selectUserNodeByUri(uri);
      builder.changeNode(uri, event.getRequestContext());
      UINavigationSelector nodeTreeSelector = builder.getAncestorOfType(UINavigationSelector.class);
      event.getRequestContext().addUIComponentToUpdateByAjax(nodeTreeSelector);
    }

  }

}
