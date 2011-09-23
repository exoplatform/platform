package org.exoplatform.platform.component;

import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.exoplatform.commons.utils.PropertyManager;
import org.exoplatform.platform.common.service.MenuConfiguratorService;
import org.exoplatform.platform.webui.NavigationURLUtils;
import org.exoplatform.portal.config.UserPortalConfig;
import org.exoplatform.portal.config.model.PageNode;
import org.exoplatform.portal.mop.user.UserNode;
import org.exoplatform.portal.mop.user.UserPortal;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.application.portlet.PortletRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;

@ComponentConfig(lifecycle = UIApplicationLifecycle.class, template = "app:/groovy/platformNavigation/portlet/UISetupPlatformToolBarPortlet/UISetupPlatformToolBarPortlet.gtmpl")
public class UISetupPlatformToolBarPortlet extends UIPortletApplication {

  private MenuConfiguratorService menuConfiguratorService;
  private List<UserNode> setupMenuUserNodes = null;

  public UISetupPlatformToolBarPortlet() throws Exception {
    menuConfiguratorService = getApplicationComponent(MenuConfiguratorService.class);
    setupMenuUserNodes = menuConfiguratorService.getSetupMenuItems(getUserPortal());
  }

  public static UserPortal getUserPortal() {
    UserPortalConfig portalConfig = Util.getPortalRequestContext().getUserPortalConfig();
    return portalConfig.getUserPortal();
  }

  public String getHREF(PageNode pageNode) {
    String pageReference = pageNode.getPageReference();
    UserNode userNode = getOriginalUserNode(pageReference);
    if (userNode != null) {
      return NavigationURLUtils.getURL(userNode);
    }
    return null;
  }

  public String getLabel(PageNode pageNode) {
    String mesgKey = pageNode.getLabel();
    PortletRequestContext pcontext = (PortletRequestContext) WebuiRequestContext.getCurrentInstance();
    String value = "";
    try {
      ResourceBundle res = pcontext.getApplicationResourceBundle();
      value = res.getString(mesgKey);
    } catch (MissingResourceException ex) {
      if (PropertyManager.isDevelopping()) {
        log.warn("Can not find resource bundle for key : " + mesgKey);
      }
      if (mesgKey != null) {
        value = mesgKey;
      }
    }
    return value;
  }

  public boolean hasChild(PageNode pageNode) {
    return pageNode != null && pageNode.getChildren() != null && !pageNode.getChildren().isEmpty();
  }

  public List<PageNode> getChildren(PageNode pageNode) {
    if (pageNode == null) {
      throw new IllegalArgumentException("getChildren method don't expect a null pageNode object.");
    }
    return pageNode.getChildren();
  }

  public List<PageNode> getPageNodes() {
    return menuConfiguratorService.getSetupMenuOriginalPageNodes();
  }

  private UserNode getOriginalUserNode(String pageReference) {
    for (UserNode userNode : setupMenuUserNodes) {
      if (userNode.getPageRef().equals(pageReference)) {
        return userNode;
      }
    }
    return null;
  }

}
