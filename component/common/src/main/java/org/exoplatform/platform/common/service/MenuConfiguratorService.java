package org.exoplatform.platform.common.service;

import org.exoplatform.container.configuration.ConfigurationManager;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.platform.common.service.plugin.MenuConfiguratorAddNodePlugin;
import org.exoplatform.platform.common.service.plugin.MenuConfiguratorRemoveNodePlugin;
import org.exoplatform.portal.config.model.*;
import org.exoplatform.portal.mop.SiteKey;
import org.exoplatform.portal.mop.Visibility;
import org.exoplatform.portal.mop.navigation.Scope;
import org.exoplatform.portal.mop.user.UserNavigation;
import org.exoplatform.portal.mop.user.UserNode;
import org.exoplatform.portal.mop.user.UserNodeFilterConfig;
import org.exoplatform.portal.mop.user.UserPortal;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.picocontainer.Startable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class MenuConfiguratorService implements Startable {

  private static final Log LOG = ExoLogger.getLogger(MenuConfiguratorService.class);
  private ConfigurationManager configurationManager;
  private String setupNavigationFilePath;
  private List<PageNode> setupPageNodes = new LinkedList<PageNode>();
  private List<MenuConfiguratorAddNodePlugin> menuConfiguratorAddNodePlugins = new ArrayList<MenuConfiguratorAddNodePlugin>();
  private List<MenuConfiguratorRemoveNodePlugin> menuConfiguratorRemoveNodePlugins = new ArrayList<MenuConfiguratorRemoveNodePlugin>();
  private UserNodeFilterConfig myGroupsFilterConfig;

  public MenuConfiguratorService(InitParams initParams, ConfigurationManager configurationManager) {
    this.configurationManager = configurationManager;
    if (initParams.containsKey("setup.navigation.file")) {
      setupNavigationFilePath = initParams.getValueParam("setup.navigation.file").getValue();
    } else {
      throw new IllegalStateException("Init param 'setup.navigation.file' have to be set.");
    }
  }

  public List<PageNode> getSetupMenuOriginalPageNodes() {
    return setupPageNodes;
  }

  public List<String> getSetupMenuPageReferences() {
    List<String> pageReferences = new ArrayList<String>();
    getPageReferences(pageReferences, setupPageNodes);
    return pageReferences;
  }

  public List<UserNode> getSetupMenuItems(UserPortal userPortal) throws Exception {
    List<UserNode> userNodes = new ArrayList<UserNode>();
    getSetupMenuItems(userPortal, userNodes, setupPageNodes);
    return userNodes;
  }

  public UserNodeFilterConfig getMyGroupsFilterConfig() {
    return this.myGroupsFilterConfig;
  }

    /**
     * Allows to add new configuration paths
     */
    public void addNavigation(MenuConfiguratorAddNodePlugin plugin)
    {
        menuConfiguratorAddNodePlugins.add(plugin);
    }

    /**
     * Allows to remove a target navigation
     */
    public void removeNavigation(MenuConfiguratorRemoveNodePlugin plugin)
    {
        menuConfiguratorRemoveNodePlugins.add(plugin);
    }

  @Override
  public void start() {
    try {
      UserNodeFilterConfig.Builder builder = UserNodeFilterConfig.builder();
      builder.withReadWriteCheck().withVisibility(Visibility.DISPLAYED, Visibility.TEMPORAL);
      builder.withTemporalCheck();
      myGroupsFilterConfig = builder.build();

      LOG.info("Loading setup menu configuration from: " + setupNavigationFilePath);
      UnmarshalledObject<PageNavigation> obj = ModelUnmarshaller.unmarshall(PageNavigation.class,
          configurationManager.getInputStream(setupNavigationFilePath));
      PageNavigation pageNavigation = obj.getObject();
      NavigationFragment fragment = pageNavigation.getFragment();
      setupPageNodes = fragment.getNodes();

      for (MenuConfiguratorAddNodePlugin menuConfiguratorAddNodePlugin : menuConfiguratorAddNodePlugins) {
          menuConfiguratorAddNodePlugin.execute();
      }

      for (MenuConfiguratorRemoveNodePlugin menuConfiguratorRemoveNodePlugin : menuConfiguratorRemoveNodePlugins) {
          menuConfiguratorRemoveNodePlugin.execute();
      }

      for (PageNode pageNode : setupPageNodes) {
        fixOwnerName(pageNode);
      }

    } catch (Exception e) {
      throw new IllegalStateException("Unknown error occurred when setting Setup menu items.", e);
    }
  }



  @Override
  public void stop() {}

  private void getPageReferences(List<String> pageReferences, List<PageNode> pageNodes) {
    for (PageNode pageNode : pageNodes) {
      String pageReference = pageNode.getPageReference();
      if (pageReference != null && !pageReference.isEmpty()) {
        pageReferences.add(pageReference);
      }
      if (pageNode.getChildren() != null && !pageNode.getChildren().isEmpty()) {
        getPageReferences(pageReferences, pageNode.getChildren());
      }
    }
  }

  private void getSetupMenuItems(UserPortal userPortal, List<UserNode> userNodes, List<PageNode> setupPageNodes) {
    for (PageNode pageNode : setupPageNodes) {
      String pageReference = pageNode.getPageReference();
      UserNavigation userNavigation = userPortal.getNavigation(new SiteKey(getOwnerType(pageReference),
          getOwnerName(pageReference)));
      UserNode userNode = searchUserNodeByPageReference(userPortal, userNavigation, pageReference);
      if (userNode != null) {
        userNodes.add(userNode);
      } else {
        if (LOG.isDebugEnabled()) {
          LOG.debug("Can't find a navigation with pageReference: " + pageReference);
        }
      }
      if (pageNode.getChildren() != null && !pageNode.getChildren().isEmpty()) {
        getSetupMenuItems(userPortal, userNodes, pageNode.getChildren());
      }
    }
  }

  private UserNode searchUserNodeByPageReference(UserPortal userPortal, UserNavigation nav, String pageReference) {
    if (nav != null) {
      try {
        UserNode rootNode = userPortal.getNode(nav, Scope.ALL, myGroupsFilterConfig, null);
        if (rootNode.getPageRef() != null && pageReference.equals(rootNode.getPageRef())) {
          return rootNode;
        }

        if (rootNode.getChildren() != null && !rootNode.getChildren().isEmpty()) {
          return searchUserNodeByPageReference(rootNode.getChildren(), pageReference);
        }
      } catch (Exception exp) {
        LOG.warn(nav.getKey().getName() + " has been deleted");
      }
    }
    return null;
  }

  private UserNode searchUserNodeByPageReference(Collection<UserNode> userNodes, String pageReference) {
    if (userNodes == null || userNodes.isEmpty()) {
      return null;
    }
    for (UserNode userNode : userNodes) {
      if (userNode.getPageRef() != null && userNode.getPageRef().format().equals(pageReference)) {
        return userNode;
      } else if (userNode.getChildren() != null && !userNode.getChildren().isEmpty()) {
        UserNode childNode = searchUserNodeByPageReference(userNode.getChildren(), pageReference);
        if (childNode != null) {
          return childNode;
        }
      }
    }
    return null;
  }

  private String getOwnerType(String pageReference) {
    String[] pageIds = pageReference.split("::");
    return pageIds[0];
  }

  private String getOwnerName(String pageReference) {
    String[] pageIds = pageReference.split("::");
    return pageIds[1];
  }

  private static void fixOwnerName(PageNode pageNode) {
    if (pageNode.getPageReference() != null) {
      String pageRef = pageNode.getPageReference();
      int pos1 = pageRef.indexOf("::");
      int pos2 = pageRef.indexOf("::", pos1 + 2);
      String type = pageRef.substring(0, pos1);
      String owner = pageRef.substring(pos1 + 2, pos2);
      String name = pageRef.substring(pos2 + 2);
      owner = fixOwnerName(type, owner);
      pageRef = type + "::" + owner + "::" + name;
      pageNode.setPageReference(pageRef);
    }
    if (pageNode.getNodes() != null) {
      for (PageNode childPageNode : pageNode.getNodes()) {
        fixOwnerName(childPageNode);
      }
    }
  }

  private static String fixOwnerName(String type, String owner) {
    if (type.equals(PortalConfig.GROUP_TYPE) && !owner.startsWith("/")) {
      return "/" + owner;
    } else {
      return owner;
    }
  }
}
