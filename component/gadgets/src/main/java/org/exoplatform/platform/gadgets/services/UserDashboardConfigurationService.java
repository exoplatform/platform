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
import java.util.UUID;

import org.exoplatform.application.gadget.Gadget;
import org.exoplatform.application.gadget.GadgetRegistryService;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.platform.gadgets.listeners.InitNewUserDashboardListener;
import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.Application;
import org.exoplatform.portal.config.model.Container;
import org.exoplatform.portal.config.model.Dashboard;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PageNode;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.config.model.TransientApplicationState;
import org.exoplatform.portal.pom.spi.portlet.Portlet;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

/**
 * @author <a href="mailto:anouar.chattouna@exoplatform.com">Anouar Chattouna</a>
 * @version $Revision$
 */
public class UserDashboardConfigurationService {

  public static String DEFAULT_TAB_NAME;
  public static String DEFAULT_TAB_LABEL;
  private static String DASHBOARD_PAGE_TEMPLATE;
  private static String INVOLVED_USERS;
  private static final String SEPARATE_INVOLVED_USERS = "separate-users";
  private static final String ALL_INVOLVED_USERS = "all-users";
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
  }

  /**
   * Gets or creates the user's dashboard page, and configures its dashboard.
   * 
   * @param userId
   *          The user name.
   * @throws Exception
   */
  public void prepaopulateUserDashboard(String userId) throws Exception {
    if (INVOLVED_USERS.equals(SEPARATE_INVOLVED_USERS)) {
      // if separate users, check if userId exist in the list, then prepopulate its dashboard
      for (UserDashboardConfiguration userDashboardConfig : separateUsersconfig) {
        if (userId.equals(userDashboardConfig.getUserId())) {
          Page dashboardPage = getUserDashboardPage(userId);
          if (dashboardPage == null) {
            PageNavigation pageNavigation = getUserPageNavigation(userId);
            if (pageNavigation.getNodes().size() < 1) {
              createUserDashboard(pageNavigation);
            }
            dashboardPage = getUserDashboardPage(userId);
            configureUserDashboard(dashboardPage, userDashboardConfig.getGadgets());
          }
        }
      }
    } else {
      // if all users, prepopulate all users dashboard
      Page dashboardPage = getUserDashboardPage(userId);
      if (dashboardPage == null) {
        PageNavigation pageNavigation = getUserPageNavigation(userId);
        if (pageNavigation.getNodes().size() < 1) {
          createUserDashboard(pageNavigation);
        }
        dashboardPage = getUserDashboardPage(userId);
        configureUserDashboard(dashboardPage, allUsersConfig);
      }
    }

  }

  /**
   * Return the {@link PageNavigation} of user that name is provided <br/>
   * 
   * @param userId
   *          The user name.
   * @throws Exception
   */
  private PageNavigation getUserPageNavigation(String userId) throws Exception {
    return dataStorageService.getPageNavigation(PortalConfig.USER_TYPE + "::" + userId);
  }

  /**
   * Return the {@link Page} that have a dashbord of user that name is provided <br/>
   * 
   * @param userId
   *          The user name.
   * @throws Exception
   */
  private Page getUserDashboardPage(String userId) throws Exception {
    return dataStorageService.getPage(PortalConfig.USER_TYPE + "::" + userId + "::" + DEFAULT_TAB_NAME);
  }

  /**
   * Creates a dashboard page for user that name will be retrieved from provided PageNavigation object param
   * 
   * @param userPageNavigation
   *          The user's page navigation.
   */
  private void createUserDashboard(PageNavigation userPageNavigation) {
    try {
      // creates the dashboard page
      Page page = userPortalConfigService.createPageTemplate(DASHBOARD_PAGE_TEMPLATE, userPageNavigation.getOwnerType(),
          userPageNavigation.getOwnerId());
      page.setTitle(DEFAULT_TAB_NAME);
      page.setName(DEFAULT_TAB_NAME);
      // creates the dashboard pageNode
      PageNode pageNode = new PageNode();
      pageNode.setName(DEFAULT_TAB_NAME);
      pageNode.setLabel(DEFAULT_TAB_LABEL);
      pageNode.setResolvedLabel(DEFAULT_TAB_LABEL);
      pageNode.setUri(DEFAULT_TAB_NAME);
      pageNode.setPageReference(page.getPageId());
      // add the pageNode to the pageNavogation
      userPageNavigation.addNode(pageNode);
      // create page and save pageNavigation
      dataStorageService.create(page);
      dataStorageService.save(userPageNavigation);
    } catch (Exception e) {
      if (logger.isDebugEnabled()) {
        logger.debug("Error while creating the user dashboard page for: " + userPageNavigation.getOwnerId(), e);
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
        // if(!gadget.isLocal()){
        // check if the gadget was saved elsewhere
        if (gadgetRegistryService.getGadget(gadget.getName()) == null) {
          gadgetRegistryService.saveGadget(gadget);
        }
        // }
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

}
