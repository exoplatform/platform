/**
 * Copyright (C) 2009 eXo Platform SAS.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.platform.component;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.EventRequest;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PageNode;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.webui.portal.UIPortal;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.webos.webui.page.UIDesktopPage;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * @author <a href="mailto:anouar.chattouna@exoplatform.com">Anouar Chattouna</a>
 */

@ComponentConfig(lifecycle = UIApplicationLifecycle.class, template = "app:/groovy/platformNavigation/portlet/UIUserPlatformToolbarDesktopPortlet/UIUserPlatformToolbarDesktopPortlet.gtmpl", events = {
    @EventConfig(name = "AddDefaultDashboard", listeners = UIUserPlatformToolbarDesktopPortlet.AddDashboardActionListener.class),
    @EventConfig(listeners = UIUserPlatformToolbarDesktopPortlet.CreateWebOSActionListener.class),
    @EventConfig(listeners = UIUserPlatformToolbarDesktopPortlet.NavigationChangeActionListener.class) })
public class UIUserPlatformToolbarDesktopPortlet extends UIPortletApplication {
  public static String DEFAULT_TAB_NAME = "Tab_Default";

  public UIUserPlatformToolbarDesktopPortlet() throws Exception {}

  public PageNavigation getCurrentUserNavigation() throws Exception {
    String remoteUser = Util.getPortalRequestContext().getRemoteUser();
    return getPageNavigation(PortalConfig.USER_TYPE + "::" + remoteUser);
  }

  private PageNavigation getPageNavigation(String owner) throws Exception {
    List<PageNavigation> allNavigations = Util.getUIPortalApplication().getNavigations();
    for (PageNavigation nav : allNavigations) {
      if (nav.getOwner().equals(owner))
        return nav;
    }
    return null;
  }

  public PageNode getSelectedPageNode() throws Exception {
    return Util.getUIPortal().getSelectedNode();
  }

  private boolean isWebOSNode(PageNode pageNode) throws Exception {
    if (pageNode == null) {
      return false;
    }
    String pageRef = pageNode.getPageReference();
    if (pageRef == null) {
      return false;
    }
    DataStorage ds = getApplicationComponent(DataStorage.class);
    Page page = ds.getPage(pageRef);
    return page == null || UIDesktopPage.DESKTOP_FACTORY_ID.equals(page.getFactoryId());
  }

  private boolean isWebOSCreated() throws Exception {
    WebuiRequestContext context = WebuiRequestContext.getCurrentInstance();
    DataStorage storage = getApplicationComponent(DataStorage.class);
    Page page = storage.getPage(PortalConfig.USER_TYPE + "::" + context.getRemoteUser() + "::" + UIDesktopPage.PAGE_ID);
    return page != null;
  }

  private PageNode getFirstNonWebOSNode(ArrayList<PageNode> nodes) throws Exception {
    for (PageNode node : nodes) {
      if (!isWebOSNode(node)) {
        return node;
      }
    }
    return null;
  }
  
  private boolean isWebOsProfileActivated() {
    return (ExoContainer.getProfiles().contains("webos") || ExoContainer.getProfiles().contains("all"));
  }

  static public class NavigationChangeActionListener extends EventListener<UIUserPlatformToolbarDesktopPortlet> {
    private Log log = ExoLogger.getExoLogger(NavigationChangeActionListener.class);
    @Override
    public void execute(Event<UIUserPlatformToolbarDesktopPortlet> event) throws Exception {
//      log.debug("PageNode : " + ((EventRequest) event.getRequestContext().getRequest()).getEvent().getValue() + " is deleted");
      if(log.isDebugEnabled()){
        log.debug("Navigation Change, PageNode : " + ((EventRequest) event.getRequestContext().getRequest()).getEvent().getValue() + " is added/deleted");
      }
    }
  }

  /**
   * Create user dashboard pagenode or redirect to the first node already created which isn't webos node
   */
  static public class AddDashboardActionListener extends EventListener<UIUserPlatformToolbarDesktopPortlet> {

    private final static String PAGE_TEMPLATE = "dashboard";

    private static Log logger = ExoLogger.getExoLogger(AddDashboardActionListener.class);

    public void execute(Event<UIUserPlatformToolbarDesktopPortlet> event) throws Exception {
      UIUserPlatformToolbarDesktopPortlet toolBarPortlet = event.getSource();
      String nodeName = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID);

      PageNavigation cachedNavigation = toolBarPortlet.getCurrentUserNavigation();

      // Update navigation for prevent create first node which already existed
      DataStorage dataStorage = toolBarPortlet.getApplicationComponent(DataStorage.class);
      PageNavigation userNavigation = dataStorage.getPageNavigation(cachedNavigation.getOwnerType(), cachedNavigation
          .getOwnerId());
      cachedNavigation.merge(userNavigation);

      UserPortalConfigService configService = toolBarPortlet.getApplicationComponent(UserPortalConfigService.class);
      if (configService != null && cachedNavigation.getNodes().size() < 1 || cachedNavigation.getNodes().size() == 1
          && toolBarPortlet.isWebOSNode(cachedNavigation.getNodes().get(0))) {
        createDashboard(nodeName, cachedNavigation, toolBarPortlet);
      } else {
        PortalRequestContext prContext = Util.getPortalRequestContext();
        prContext.getResponse().sendRedirect(
            prContext.getPortalURI() + toolBarPortlet.getFirstNonWebOSNode(cachedNavigation.getNodes()).getName());
      }
    }

    private static void createDashboard(String _nodeName, PageNavigation _pageNavigation,
        UIUserPlatformToolbarDesktopPortlet toolbarPortlet) {
      UserPortalConfigService _configService = toolbarPortlet.getApplicationComponent(UserPortalConfigService.class);
      try {
        PortalRequestContext prContext = Util.getPortalRequestContext();
        if (_nodeName == null) {
          logger.debug("Parsed nodeName is null, hence use " + DEFAULT_TAB_NAME + " as default name");
          _nodeName = DEFAULT_TAB_NAME;
        }
        Page page = _configService
            .createPageTemplate(PAGE_TEMPLATE, _pageNavigation.getOwnerType(), _pageNavigation.getOwnerId());
        page.setTitle(_nodeName);
        page.setName(_nodeName);

        PageNode pageNode = new PageNode();
        pageNode.setName(_nodeName);
        pageNode.setLabel(_nodeName);
        pageNode.setUri(_nodeName);
        pageNode.setPageReference(page.getPageId());

        _pageNavigation.addNode(pageNode);
        DataStorage ds = toolbarPortlet.getApplicationComponent(DataStorage.class);
        ds.create(page);
        ds.save(_pageNavigation);

        prContext.getResponse().sendRedirect(prContext.getPortalURI() + _nodeName);
      } catch (Exception ex) {
        logger.info("Could not create default dashboard page", ex);
      }
    }
  }

  /**
   * Create user page navigation, page and node for Desktop if they haven't been created already.
   */
  static public class CreateWebOSActionListener extends EventListener<UIUserPlatformToolbarDesktopPortlet> {
    @Override
    public void execute(Event<UIUserPlatformToolbarDesktopPortlet> event) throws Exception {
      WebuiRequestContext context = event.getRequestContext();
      String userName = context.getRemoteUser();

      if (userName != null) {
        DataStorage storage = event.getSource().getApplicationComponent(DataStorage.class);

        Page page = createPage(userName, storage);
        PageNavigation pageNavigation = createNavigation(userName, page.getPageId(), storage);
        updateUI(pageNavigation);
      }
    }

    private PageNavigation createNavigation(String userName, String pageId, DataStorage storage) throws Exception {
      PageNavigation pageNavigation = storage.getPageNavigation(PortalConfig.USER_TYPE, userName);
      PageNode pageNode = null;
      if (pageNavigation == null) {
        pageNavigation = new PageNavigation();
        pageNavigation.setOwnerType(PortalConfig.USER_TYPE);
        pageNavigation.setOwnerId(userName);
        storage.create(pageNavigation);
      } else {
        pageNode = pageNavigation.getNode(UIDesktopPage.NODE_NAME);
      }

      if (pageNode == null) {
        pageNode = new PageNode();
        pageNode.setName(UIDesktopPage.NODE_NAME);
        pageNode.setUri(UIDesktopPage.NODE_NAME);
        pageNode.setLabel(UIDesktopPage.NODE_LABEL);
        pageNode.setPageReference(pageId);

        pageNavigation.addNode(pageNode);
        storage.save(pageNavigation);
      }

      return pageNavigation;
    }

    private void updateUI(PageNavigation pageNavigation) throws Exception {
      UIPortalApplication uiApp = Util.getUIPortalApplication();
      List<PageNavigation> all_navigations = uiApp.getNavigations();

      for (PageNavigation nav : all_navigations) {
        if (nav.getOwnerType().equals(PortalConfig.USER_TYPE) && nav.getNode(UIDesktopPage.NODE_NAME) == null) {
          nav.addNode(pageNavigation.getNode(UIDesktopPage.NODE_NAME));
          break;
        }
      }

      UIPortal uiPortal = Util.getUIPortal();
      if (uiPortal != null && uiPortal.findFirstComponentOfType(UIDesktopPage.class) == null) {
        uiPortal.refreshUIPage();
      }

      PortalRequestContext prContext = Util.getPortalRequestContext();
      prContext.getResponse().sendRedirect(prContext.getPortalURI() + UIDesktopPage.NODE_NAME);
    }

    private Page createPage(String userName, DataStorage storage) throws Exception {
      Page page = storage.getPage(PortalConfig.USER_TYPE + "::" + userName + "::" + UIDesktopPage.PAGE_ID);
      if (page == null) {
        page = new Page();
        page.setName(UIDesktopPage.PAGE_ID);
        page.setTitle(UIDesktopPage.PAGE_TITLE);
        page.setFactoryId(UIDesktopPage.DESKTOP_FACTORY_ID);
        page.setShowMaxWindow(true);
        page.setOwnerType(PortalConfig.USER_TYPE);
        page.setOwnerId(userName);
        storage.create(page);
      }
      return page;
    }
  }

}
