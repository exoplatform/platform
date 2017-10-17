package org.exoplatform.platform.component;

import java.util.*;

import org.exoplatform.commons.utils.ExpressionUtil;
import org.exoplatform.platform.common.service.MenuConfiguratorService;
import org.exoplatform.portal.config.UserPortalConfig;
import org.exoplatform.portal.config.model.PageNode;
import org.exoplatform.portal.mop.user.UserNavigation;
import org.exoplatform.portal.mop.user.UserNode;
import org.exoplatform.portal.mop.user.UserPortal;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.application.portlet.PortletRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;
import org.gatein.common.text.EntityEncoder;

/**
 * @author <a href="rtouzi@exoplatform.com">rtouzi</a>
 */

@ComponentConfig(
  lifecycle = UIApplicationLifecycle.class,
  template = "app:/groovy/platformNavigation/portlet/UIBreadCrumbsNavigationPortlet/UIBreadCrumbsNavigationPortlet.gtmpl"
)
public class UIBreadCrumbsNavigationPortlet extends UIPortletApplication {

  private static Map<Locale, Map<String, String>> resolvedEncodeLabels = Collections
          .synchronizedMap(new HashMap<Locale, Map<String, String>>());
  private MenuConfiguratorService menuConfiguratorService;

  public UIBreadCrumbsNavigationPortlet() throws Exception {
    menuConfiguratorService = getApplicationComponent(MenuConfiguratorService.class);
  }

  public List<String> getBreadcumbs() throws Exception {
    List<String> breadcumbs = new ArrayList<>();

    String pageRef = getSelectedPage();
    List<PageNode> nodes = buildBreadcumbs(pageRef, getPageNodes());
    for (PageNode node : nodes) {
      String label = getEncodedResolvedLabel(node);
      breadcumbs.add(label);
    }
    return breadcumbs;
  }

  private String getEncodedResolvedLabel(PageNode pageNode) {
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

  private List<PageNode> buildBreadcumbs(String selectedPage, List<PageNode> nodes) {
    List<PageNode> breadcumbs = new ArrayList<>();
    //
    if (selectedPage != null) {
      for (PageNode node : nodes) {
        boolean added = false;
        if (node.getPageReference().equals(selectedPage)) {
          breadcumbs.add(node);
          added = true;
        }
        if (node.getChildren() != null) {
          List<PageNode> tmp = buildBreadcumbs(selectedPage, node.getChildren());
          if (!tmp.isEmpty()) {
            if (!added) {
              breadcumbs.add(node);
            }
            breadcumbs.addAll(tmp);
          }
        }
      }
    }
    return breadcumbs;
  }

  private String getSelectedPage() throws Exception {
    UserNode node = Util.getUIPortal().getSelectedUserNode();
    UserPortal userPortal = getUserPortal();
    UserNavigation nav = userPortal.getNavigation(node.getNavigation().getKey());
    UserNode targetNode = userPortal.resolvePath(nav, null, node.getURI());
    if (targetNode != null && targetNode.getPageRef() != null) {
      return targetNode.getPageRef().format();
    } else {
      return null;
    }
  }

  private List<PageNode> getPageNodes() throws Exception {
    return menuConfiguratorService.getSetupMenuOriginalPageNodes();
  }

  private static UserPortal getUserPortal() {
    UserPortalConfig portalConfig = Util.getPortalRequestContext().getUserPortalConfig();
    return portalConfig.getUserPortal();
  }
}