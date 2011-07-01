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
package org.exoplatform.platform.gadgets.services;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.UUID;

import org.exoplatform.application.gadget.Gadget;
import org.exoplatform.application.gadget.GadgetRegistryService;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.platform.gadgets.listeners.InitNewUserDashboardListener;
import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.config.UserPortalConfig;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.Application;
import org.exoplatform.portal.config.model.Container;
import org.exoplatform.portal.config.model.Dashboard;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.TransientApplicationState;
import org.exoplatform.portal.mop.SiteKey;
import org.exoplatform.portal.mop.SiteType;
import org.exoplatform.portal.mop.Visibility;
import org.exoplatform.portal.mop.navigation.NavigationContext;
import org.exoplatform.portal.mop.navigation.NavigationState;
import org.exoplatform.portal.mop.navigation.Scope;
import org.exoplatform.portal.mop.user.UserNavigation;
import org.exoplatform.portal.mop.user.UserNode;
import org.exoplatform.portal.mop.user.UserNodeFilterConfig;
import org.exoplatform.portal.mop.user.UserPortal;
import org.exoplatform.portal.mop.user.UserPortalContext;
import org.exoplatform.portal.pom.spi.portlet.Portlet;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

/**
 * @author <a href="mailto:anouar.chattouna@exoplatform.com">Anouar
 *         Chattouna</a>
 * @version $Revision$
 */
public class UserDashboardConfigurationService {

  public static String DEFAULT_TAB_NAME;
  public static String DEFAULT_TAB_LABEL;
  private static String DASHBOARD_PAGE_TEMPLATE;
  private static String INVOLVED_USERS;
  private static final String SEPARATE_INVOLVED_USERS = "separate-users";
  private static final String ALL_INVOLVED_USERS = "all-users";
  private final UserNodeFilterConfig filterConfig;
  private DataStorage dataStorageService = null;
  private UserPortalConfigService userPortalConfigService = null;
  private GadgetRegistryService gadgetRegistryService = null;
  private List<UserDashboardConfiguration> separateUsersconfig;
  private List<Gadget> allUsersConfig;
  private static Log logger = ExoLogger.getExoLogger(InitNewUserDashboardListener.class);

  public UserDashboardConfigurationService(DataStorage dataStorageService, UserPortalConfigService userPortalConfigService,
      GadgetRegistryService gadgetRegistryService, InitParams initParams) {
    DEFAULT_TAB_NAME = initParams.getValueParam("dashboardTabName").getValue();
    DEFAULT_TAB_LABEL = initParams.getValueParam("dashboardTabLabel").getValue();
    DASHBOARD_PAGE_TEMPLATE = initParams.getValueParam("dashboardPageTemplate").getValue();
    INVOLVED_USERS = initParams.getValueParam("involvedUsers").getValue();
    if (INVOLVED_USERS.equals(SEPARATE_INVOLVED_USERS)) {
      separateUsersconfig = initParams.getObjectParamValues(UserDashboardConfiguration.class);
      // separateUsersconfig should not be null
      if (separateUsersconfig == null) {
        throw new IllegalStateException(INVOLVED_USERS + " is used for " + initParams.getValueParam("involvedUsers").getName()
            + " init param..\nObject param values can not be null..\nPlease check your configuration");
      }
    } else if (INVOLVED_USERS.equals(ALL_INVOLVED_USERS)) {
      allUsersConfig = initParams.getObjectParamValues(Gadget.class);
      // allUsersConfig should not be null
      if (allUsersConfig == null) {
        throw new IllegalStateException(INVOLVED_USERS + " is used for " + initParams.getValueParam("involvedUsers").getName()
            + " init param..\nObject param values can not be null..\nPlease check your configuration");
      }
    } else {
      // error in configuration
      throw new IllegalStateException(initParams.getValueParam("involvedUsers").getName()
          + " init param is missing.. Please check your configuration");
    }
    this.dataStorageService = dataStorageService;
    this.userPortalConfigService = userPortalConfigService;
    this.gadgetRegistryService = gadgetRegistryService;

    UserNodeFilterConfig.Builder scopeBuilder = UserNodeFilterConfig.builder();
    scopeBuilder.withAuthorizationCheck().withVisibility(Visibility.DISPLAYED, Visibility.TEMPORAL);
    scopeBuilder.withTemporalCheck();
    filterConfig = scopeBuilder.build();
  }

  /**
   * Gets or creates the user's dashboard page, and configures its
   * dashboard.
   * 
   * @param userId
   *          The user name.
   * @throws Exception
   */
  public void prepaopulateUserDashboard(String userId) throws Exception {
    if (INVOLVED_USERS.equals(SEPARATE_INVOLVED_USERS)) {
      // if separate users, check if userId exist in the list, then
      // prepopulate its dashboard
      for (UserDashboardConfiguration userDashboardConfig : separateUsersconfig) {
        if (userId.equals(userDashboardConfig.getUserId())) {
          Page dashboardPage = getUserDashboardPage(userId);
          if (dashboardPage == null) {
            createUserDashboard(userId);
            dashboardPage = getUserDashboardPage(userId);
            configureUserDashboard(dashboardPage, userDashboardConfig.getGadgets());
          }
        }
      }
    } else {
      // if all users, prepopulate all users dashboard
      Page dashboardPage = getUserDashboardPage(userId);
      if (dashboardPage == null) {
        createUserDashboard(userId);
        dashboardPage = getUserDashboardPage(userId);
        configureUserDashboard(dashboardPage, allUsersConfig);
      }
    }

  }

  private UserNavigation getUserNavigation(String userId) throws Exception {
    UserPortal userPortal = getUserPortal(userId);
    UserNavigation userNavigation = userPortal.getNavigation(SiteKey.user(userId));
    if (userNavigation == null) {
      NavigationContext navigationContextt = new NavigationContext(SiteKey.user(userId), new NavigationState(1));
      userPortalConfigService.getNavigationService().saveNavigation(navigationContextt);
      userPortal = getUserPortal(userId);
      userNavigation = userPortal.getNavigation(SiteKey.user(userId));
    }
    return userNavigation;
  }

  private UserPortal getUserPortal(String userId) throws Exception {
    UserPortalConfig portalConfig = userPortalConfigService.getUserPortalConfig(userPortalConfigService.getDefaultPortal(),
        userId, NULL_CONTEXT);
    return portalConfig.getUserPortal();
  }

  /**
   * Return the {@link Page} that have a dashbord of user that name is
   * provided <br/>
   * 
   * @param userId
   *          The user name.
   * @throws Exception
   */
  private Page getUserDashboardPage(String userId) throws Exception {
    return dataStorageService.getPage(SiteType.USER.getName() + "::" + userId + "::" + DEFAULT_TAB_NAME);
  }

  /**
   * Creates a dashboard page for a given user
   * 
   * @param userId
   *          The user's ID.
   */
  private void createUserDashboard(String userId) {
    try {

      UserPortal userPortal = getUserPortal(userId);
      UserNavigation userNav = getUserNavigation(userId);
      if (userNav == null) {
        logger.warn("User navigation for '" + userId + "' cannot be found. Cannot prePopulate gadgets in user's dashboard.");
        return;
      }
      SiteKey siteKey = userNav.getKey();
      Page page = userPortalConfigService.createPageTemplate(DASHBOARD_PAGE_TEMPLATE, siteKey.getTypeName(), siteKey.getName());
      page.setTitle(DEFAULT_TAB_NAME);
      page.setName(DEFAULT_TAB_NAME);
      dataStorageService.create(page);

      UserNode rootNode = userPortal.getNode(userNav, Scope.CHILDREN, filterConfig, null);
      UserNode tabNode = rootNode.addChild(DEFAULT_TAB_NAME);
      tabNode.setLabel(DEFAULT_TAB_NAME);
      tabNode.setPageRef(page.getPageId());

      userPortal.saveNode(rootNode, null);

    } catch (Exception e) {
      if (logger.isDebugEnabled()) {
        logger.debug("Error while creating the user dashboard page for: " + userId, e);
      }
    }
  }

  /**
   * Adds provided gadgets to user's dashboard
   * 
   * @param userDashboardPage
   *          The user's dashboard page.
   * @param gadgets
   *          The list of gadgets to populate in the user dashboard.
   */
  @SuppressWarnings("unchecked")
  private void configureUserDashboard(Page userDashboardPage, List<Gadget> gadgets) {
    try {
      // gets the dashboard portlet
      Application<Portlet> dashboardPortlet = (Application<Portlet>) userDashboardPage.getChildren().get(0);
      String dashboardId = dashboardPortlet.getStorageId();
      // loads the dashboard from dashboard portlet
      Dashboard dashboard = dataStorageService.loadDashboard(dashboardId);
      int colIndex = 0;
      for (Gadget gadget : gadgets) {
        // creates a gadget application from the gadget
        Application<org.exoplatform.portal.pom.spi.gadget.Gadget> gadgetApplication = Application.createGadgetApplication();
        gadgetApplication.setStorageName(UUID.randomUUID().toString());
        gadgetApplication.setState(new TransientApplicationState<org.exoplatform.portal.pom.spi.gadget.Gadget>(gadget.getName()));
        // check if the gadget was saved elsewhere
        if (gadgetRegistryService.getGadget(gadget.getName()) == null) {
          gadgetRegistryService.saveGadget(gadget);
        }
        Container column = (Container) dashboard.getChildren().get(colIndex);
        column.getChildren().add(gadgetApplication);
        colIndex = colIndex + 1 == dashboard.getChildren().size() ? 0 : colIndex + 1;
      }
      dataStorageService.saveDashboard(dashboard);
    } catch (Exception e) {
      if (logger.isDebugEnabled()) {
        logger.debug("Error while configuring the user dashboard for: " + userDashboardPage.getOwnerId(), e);
      }
    }
  }

  public static class UserDashboardConfiguration {
    private String userId;

    private List<Gadget> gadgets;

    public String getUserId() {
      return userId;
    }

    public void setUserId(String userId) {
      this.userId = userId;
    }

    public List<Gadget> getGadgets() {
      return gadgets;
    }

    public void setGadgets(List<Gadget> gadgets) {
      this.gadgets = gadgets;
    }
  }

  // Don't need a portal context because webui isn't used
  private static final UserPortalContext NULL_CONTEXT = new UserPortalContext() {
    public ResourceBundle getBundle(UserNavigation navigation) {
      return null;
    }

    public Locale getUserLocale() {
      return Locale.ENGLISH;
    }
  };
}
