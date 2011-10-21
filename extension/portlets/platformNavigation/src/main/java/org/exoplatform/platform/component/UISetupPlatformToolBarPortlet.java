package org.exoplatform.platform.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.exoplatform.commons.utils.ExpressionUtil;
import org.exoplatform.platform.common.service.MenuConfiguratorService;
import org.exoplatform.platform.webui.NavigationURLUtils;
import org.exoplatform.portal.config.UserPortalConfig;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNode;
import org.exoplatform.portal.mop.user.UserNode;
import org.exoplatform.portal.mop.user.UserPortal;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.application.portlet.PortletRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;
import org.gatein.common.text.EntityEncoder;

@ComponentConfig(lifecycle = UIApplicationLifecycle.class, template = "app:/groovy/platformNavigation/portlet/UISetupPlatformToolBarPortlet/UISetupPlatformToolBarPortlet.gtmpl")
public class UISetupPlatformToolBarPortlet extends UIPortletApplication {

  private static Map<Locale, Map<String, String>> resolvedEncodeLabels = Collections
      .synchronizedMap(new HashMap<Locale, Map<String, String>>());
  private MenuConfiguratorService menuConfiguratorService;
  private UserPortalConfigService portalConfigService;
  private List<UserNode> setupMenuUserNodes = null;
  private List<PageNode> setupMenuPageNodes = null;
  private Map<String, Boolean> pagePermissionsMap = new HashMap<String, Boolean>();

  public UISetupPlatformToolBarPortlet() throws Exception {
    menuConfiguratorService = getApplicationComponent(MenuConfiguratorService.class);
    portalConfigService = getApplicationComponent(UserPortalConfigService.class);
    setupMenuUserNodes = menuConfiguratorService.getSetupMenuItems(getUserPortal());
    pagePermissionsMap.clear();
  }

  public static UserPortal getUserPortal() {
    UserPortalConfig portalConfig = Util.getPortalRequestContext().getUserPortalConfig();
    return portalConfig.getUserPortal();
  }

  public boolean hasPermissionOnPageNode(PageNode pageNode) throws Exception {
    Boolean hasPermission = null;
    // If page reference isn't null, verify if user can access it
    if (pageNode.getPageReference() != null) {
      hasPermission = pagePermissionsMap.get(pageNode.getPageReference());
      // hasPermission information isn't cached yet
      if (hasPermission == null) {
        UserNode userNode = getOriginalUserNode(pageNode.getPageReference());
        if (userNode != null) {
          Page page = portalConfigService.getPage(userNode.getPageRef(), Util.getPortalRequestContext().getRemoteUser());
          if (page != null) {
            hasPermission = true;
          }
        }
      }
    }
    // If hasPermission isn't yet assigned to true, verify if user can access one of children
    if (hasPermission == null && pageNode.getChildren() != null && !pageNode.getChildren().isEmpty()) {
      // Keep this in a for iteration, in order to compute and cache permissions on all children
      for (PageNode childPageNode : pageNode.getChildren()) {
        if (hasPermissionOnPageNode(childPageNode)) {
          hasPermission = true;
        }
      }
    }
    if (hasPermission == null) {
      hasPermission = false;
    }
    pagePermissionsMap.put(pageNode.getPageReference(), hasPermission);
    return hasPermission;
  }

  public String getHREF(PageNode pageNode) {
    String pageReference = pageNode.getPageReference();
    UserNode userNode = getOriginalUserNode(pageReference);
    if (userNode != null) {
      return NavigationURLUtils.getURL(userNode);
    }
    return null;
  }

  public String getEncodedResolvedLabel(PageNode pageNode) {
    if (pageNode.getLabel() != null && !pageNode.getLabel().isEmpty()) {
      Locale locale = Util.getPortalRequestContext().getLocale();
      Map<String, String> i18nizedLabels = resolvedEncodeLabels.get(locale);
      if (i18nizedLabels == null) {
        i18nizedLabels = Collections.synchronizedMap(new HashMap<String, String>());
        resolvedEncodeLabels.put(locale, i18nizedLabels);
      }
      String resolvedLabel = i18nizedLabels.get(pageNode.getLabel());
      if (resolvedLabel == null) {
        PortletRequestContext pcontext = (PortletRequestContext) WebuiRequestContext.getCurrentInstance();
        ResourceBundle bundle = pcontext.getApplicationResourceBundle();
        resolvedLabel = ExpressionUtil.getExpressionValue(bundle, pageNode.getLabel());
        resolvedLabel = EntityEncoder.FULL.encode(resolvedLabel);
        i18nizedLabels.put(pageNode.getLabel(), resolvedLabel);
      }
      return resolvedLabel;
    }
    return pageNode.getLabel();
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

  public List<PageNode> getPageNodes() throws Exception {
    if (setupMenuPageNodes == null) {
      setupMenuPageNodes = new ArrayList<PageNode>();
      List<PageNode> originalPageNodes = menuConfiguratorService.getSetupMenuOriginalPageNodes();
      if (setupMenuPageNodes != null) {
        for (PageNode pageNode : originalPageNodes) {
          if (hasPermissionOnPageNode(pageNode)) {
            setupMenuPageNodes.add(pageNode);
          }
        }
      }
    }
    return setupMenuPageNodes;
  }

  private UserNode getOriginalUserNode(String pageReference) {
    if (pageReference == null || pageReference.isEmpty()) {
      return null;
    }
    for (UserNode userNode : setupMenuUserNodes) {
      if (userNode.getPageRef().equals(pageReference)) {
        return userNode;
      }
    }
    return null;
  }

}
