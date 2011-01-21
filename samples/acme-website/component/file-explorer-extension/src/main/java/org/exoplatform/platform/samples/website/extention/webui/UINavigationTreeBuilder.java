package org.exoplatform.platform.samples.website.extention.webui;

import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.jcr.Node;
import javax.jcr.NodeIterator;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PageNode;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.mop.Visibility;
import org.exoplatform.portal.webui.container.UIContainer;
import org.exoplatform.portal.webui.navigation.UINavigationNodeSelector.TreeNodeData;
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

  private PageNavigation edittedNavigation;

  private TreeNodeData   edittedTreeNodeData;

  /** The current node. */
  protected PageNode     currentNode; 

  /**
   * Instantiates a new uI navigation tree builder.
   * 
   * @throws Exception the exception
   */
  public UINavigationTreeBuilder() throws Exception {

    UITree uiTree = addChild(UINavigationTree.class, null, UINavigationTree.class.getSimpleName() + hashCode());
    uiTree.setIcon("DefaultPageIcon");
    uiTree.setSelectedIcon("DefaultPageIcon");
    uiTree.setBeanIdField("uri");
    uiTree.setBeanLabelField("encodedResolvedLabel");
    uiTree.setBeanIconField("icon");
    uiTree.setUIRightClickPopupMenu(null);

    DataStorage dataService = getApplicationComponent(DataStorage.class);
    PageNavigation edittedNavigation = dataService.getPageNavigation(PortalConfig.PORTAL_TYPE, getSiteName());
    setEdittedNavigation(edittedNavigation);
    initTreeData();
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
      edittedTreeNodeData = new TreeNodeData(edittedNavigation);
//      if (edittedTreeNodeData.getNode() != null) {
//         selectPageNodeByUri(edittedTreeNodeData.getNode().getUri());
//      }
    }

    UITree tree = getChild(UITree.class);
    tree.setSibbling(edittedNavigation.getNodes());
  }

  private void localizeNavigation(Locale locale) {
    LocaleConfig localeConfig = getApplicationComponent(LocaleConfigService.class).getLocaleConfig(locale.getLanguage());

    String ownerType = edittedNavigation.getOwnerType();
    if (!PortalConfig.USER_TYPE.equals(ownerType)) {
      String ownerId = edittedNavigation.getOwnerId();
      if (PortalConfig.GROUP_TYPE.equals(ownerType)) {
        // Remove the trailing '/' for a group
        ownerId = ownerId.substring(1);
      }
      ResourceBundle res = localeConfig.getNavigationResourceBundle(ownerType, ownerId);
      for (PageNode node : edittedNavigation.getNodes()) {
        resolveLabel(res, node);
      }
    }
  }

  private void resolveLabel(ResourceBundle res, PageNode node) {
    node.setResolvedLabel(res);
    if (node.getChildren() == null) {
      return;
    }
    for (PageNode childNode : node.getChildren()) {
      resolveLabel(res, childNode);
    }
  }

  public void selectPageNodeByUri(String uri) {
    if (edittedTreeNodeData == null) {
      return;
    }
    UITree tree = getChild(UITree.class);
    List<?> sibbling = tree.getSibbling();
    tree.setSibbling(null);
    tree.setParentSelected(null);
    edittedTreeNodeData.setNode(searchPageNodeByUri(edittedTreeNodeData.getPageNavigation(), uri));
    if (edittedTreeNodeData.getNode() != null) {
      tree.setSelected(edittedTreeNodeData.getNode());
      tree.setChildren(edittedTreeNodeData.getNode().getChildren());
      return;
    }
    tree.setSelected(null);
    tree.setChildren(null);
    tree.setSibbling(sibbling);
  }

  public PageNode searchPageNodeByUri(PageNavigation pageNav, String uri) {
    if (pageNav == null || uri == null) {
      return null;
    }
    List<PageNode> pageNodes = pageNav.getNodes();
    UITree uiTree = getChild(UITree.class);
    for (PageNode ele : pageNodes) {
      PageNode returnPageNode = searchPageNodeByUri(ele, uri, uiTree);
      if (returnPageNode == null) {
        continue;
      }
      if (uiTree.getSibbling() == null) {
        uiTree.setSibbling(pageNodes);
      }
      return returnPageNode;
    }
    return null;
  }

  private PageNode searchPageNodeByUri(PageNode pageNode, String uri, UITree tree) {
    if (pageNode.getUri().equals(uri)) {
      return pageNode;
    }
    List<PageNode> children = pageNode.getChildren();
    if (children == null) {
      return null;
    }
    for (PageNode ele : children) {
      PageNode returnPageNode = searchPageNodeByUri(ele, uri, tree);
      if (returnPageNode == null) {
        continue;
      }
      if (tree.getSibbling() == null) {
        tree.setSibbling(children);
      }
      if (tree.getParentSelected() == null) {
        tree.setParentSelected(pageNode);
      }
      edittedTreeNodeData.setParentNode(pageNode);
      return returnPageNode;
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
    PageNode selectedNode = getSelectedPageNode();
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

    currentNode = searchPageNodeByUri(edittedTreeNodeData.getPageNavigation(), uri);
    broadcastOnChange(currentNode, context);
  }

  /**
   * Broadcast on change.
   * 
   * @param navigationNode the node
   * @param requestContext the request context
   * @throws Exception the exception
   */
  public void broadcastOnChange(PageNode navigationNode, Object context) throws Exception {
    UINavigationSelector nodeTreeSelector = getAncestorOfType(UINavigationSelector.class);
    nodeTreeSelector.onChange(navigationNode, context);
  }

  public void setEdittedNavigation(PageNavigation _filteredEdittedNavigation) throws Exception {
    this.edittedNavigation = _filteredEdittedNavigation;
  }

  public PageNavigation getEdittedNavigation() {
    return this.edittedNavigation;
  }

  public TreeNodeData getSelectedNode() {
    return edittedTreeNodeData;
  }

  public PageNavigation getSelectedNavigation() {
    return edittedTreeNodeData == null ? null : edittedTreeNodeData.getPageNavigation();
  }

  public PageNode getSelectedPageNode() {
    return edittedTreeNodeData == null ? null : edittedTreeNodeData.getNode();
  }

  public String getUpLevelUri() {
    return edittedTreeNodeData.getParentNode().getUri();
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
      builder.selectPageNodeByUri(uri);
      builder.changeNode(uri, event.getRequestContext());
      UINavigationSelector nodeTreeSelector = builder.getAncestorOfType(UINavigationSelector.class);
      event.getRequestContext().addUIComponentToUpdateByAjax(nodeTreeSelector);
    }

  }

}
