/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.platform.component.organization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Session;

import org.exoplatform.commons.utils.PageList;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.ComponentPlugin;
import org.exoplatform.container.component.ComponentRequestLifecycle;
import org.exoplatform.container.configuration.ConfigurationManager;
import org.exoplatform.container.xml.Component;
import org.exoplatform.container.xml.ExternalComponentPlugins;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.management.annotations.Impact;
import org.exoplatform.management.annotations.ImpactType;
import org.exoplatform.management.annotations.Managed;
import org.exoplatform.management.annotations.ManagedDescription;
import org.exoplatform.management.annotations.ManagedName;
import org.exoplatform.management.jmx.annotations.NameTemplate;
import org.exoplatform.management.jmx.annotations.Property;
import org.exoplatform.management.rest.annotations.RESTEndpoint;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.GroupEventListener;
import org.exoplatform.services.organization.Membership;
import org.exoplatform.services.organization.MembershipEventListener;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.OrganizationServiceInitializer;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.UserEventListener;
import org.exoplatform.services.organization.UserProfile;
import org.exoplatform.services.organization.UserProfileEventListener;
import org.picocontainer.Startable;

/**
 * This Service create Organization Model profiles, for User & Groups not
 * created via eXo OrganizationService.
 * 
 * @author Boubaker KHANFIR
 */
@Managed
@ManagedDescription("Platform Organization Model Integration Service")
@NameTemplate({ @Property(key = "name", value = "OrganizationIntegrationService"),
    @Property(key = "service", value = "extensions"), @Property(key = "type", value = "platform") })
@RESTEndpoint(path = "OrganizationIntegrationService")
public class OrganizationIntegrationService implements Startable {

  private static final int USERS_PAGE_SIZE = 10;
  private static final Log LOG = ExoLogger.getLogger(OrganizationIntegrationService.class);
  private static final Comparator<org.exoplatform.container.xml.ComponentPlugin> COMPONENT_PLUGIN_COMPARATOR = new Comparator<org.exoplatform.container.xml.ComponentPlugin>() {
    public int compare(org.exoplatform.container.xml.ComponentPlugin o1, org.exoplatform.container.xml.ComponentPlugin o2) {
      return o1.getPriority() - o2.getPriority();
    }
  };
  public static final Comparator<Group> GROUP_COMPARATOR = new Comparator<Group>() {
    public int compare(Group o1, Group o2) {
      if (o1.getId().contains(o2.getId())) {
        return 1;
      }
      if (o2.getId().contains(o1.getId())) {
        return -1;
      }
      return o2.getId().compareTo(o1.getId());
    }
  };
  private Map<String, UserEventListener> userDAOListeners_;
  private Map<String, GroupEventListener> groupDAOListeners_;
  private Map<String, MembershipEventListener> membershipDAOListeners_;
  private Map<String, UserProfileEventListener> userProfileListeners_;
  private OrganizationService organizationService;
  private RepositoryService repositoryService;
  private PortalContainer container;
  private boolean requestStarted = false;

  public OrganizationIntegrationService(OrganizationService organizationService, RepositoryService repositoryService,
      ConfigurationManager manager, PortalContainer container, InitParams initParams) {
    this.organizationService = organizationService;
    this.repositoryService = repositoryService;
    this.container = container;
    userDAOListeners_ = new HashMap<String, UserEventListener>();
    groupDAOListeners_ = new HashMap<String, GroupEventListener>();
    membershipDAOListeners_ = new HashMap<String, MembershipEventListener>();
    userProfileListeners_ = new HashMap<String, UserProfileEventListener>();
    boolean hasExternalComponentPlugins = false;
    int nbExternalComponentPlugins = 0;
    try {
      ExternalComponentPlugins organizationServiceExternalComponentPlugins = manager.getConfiguration()
          .getExternalComponentPlugins(OrganizationIntegrationService.class.getName());

      if (organizationServiceExternalComponentPlugins != null
          && organizationServiceExternalComponentPlugins.getComponentPlugins() != null) {
        nbExternalComponentPlugins = organizationServiceExternalComponentPlugins.getComponentPlugins().size();
      }

      Component organizationServiceComponent = manager.getComponent(OrganizationIntegrationService.class);

      if (organizationServiceComponent != null && organizationServiceComponent.getComponentPlugins() != null) {
        nbExternalComponentPlugins += organizationServiceComponent.getComponentPlugins().size();
      }
      hasExternalComponentPlugins = (nbExternalComponentPlugins > 0);
    } catch (Exception e) {
      LOG.error("Test if this component has ExternalComponentPlugins generated an exception", e);
    }

    if (!hasExternalComponentPlugins) {
      try {
        ExternalComponentPlugins organizationServiceExternalComponentPlugins = manager.getConfiguration()
            .getExternalComponentPlugins(OrganizationService.class.getName());
        addComponentPlugin(organizationServiceExternalComponentPlugins.getComponentPlugins());

        Component organizationServiceComponent = manager.getComponent(OrganizationService.class);
        List<org.exoplatform.container.xml.ComponentPlugin> organizationServicePlugins = organizationServiceComponent
            .getComponentPlugins();
        if (organizationServicePlugins != null) {
          addComponentPlugin(organizationServicePlugins);
        }
      } catch (Exception e) {
        LOG.error("Failed to add OrganizationService plugins", e);
      }
    } else {
      LOG.info("This component has already " + nbExternalComponentPlugins + " ExternalComponentPlugins");
    }
    if (initParams != null) {
      if (initParams.containsKey("workspace")) {
        Util.WORKSPACE = initParams.getValueParam("workspace").getValue();
      } else {
        LOG.warn("'workspace' init param is empty, use default value: " + Util.WORKSPACE);
      }
      if (initParams.containsKey("homePath")) {
        Util.HOME_PATH = initParams.getValueParam("homePath").getValue();
      } else {
        LOG.warn("'homePath' init param is empty, use default value: " + Util.HOME_PATH);
      }
    } else {
      LOG.warn("init params not set, use default values for 'homePath'[=" + Util.HOME_PATH + "] and 'workspace[="
          + Util.WORKSPACE + "]'");
    }
  }

  public void start() {
    Session session = null;
    try {
      session = repositoryService.getCurrentRepository().getSystemSession(Util.WORKSPACE);
      Util.init(session);

      invokeAllGroupsListeners();
    } catch (Exception e) {
      LOG.error(e);
    } finally {
      if (session != null) {
        session.logout();
      }
    }
  }

  public void stop() {}

  /**
   * Add a list of OrganizationService listeners into
   * OrganizationIntegrationService
   * 
   * @param plugins
   *          List of OrganizationService ComponentPlugins
   */
  public void addComponentPlugin(List<org.exoplatform.container.xml.ComponentPlugin> plugins) {
    if (plugins == null)
      return;
    Collections.sort(plugins, COMPONENT_PLUGIN_COMPARATOR);
    for (org.exoplatform.container.xml.ComponentPlugin plugin : plugins) {

      try {
        Class<?> pluginClass = Class.forName(plugin.getType());
        ComponentPlugin cplugin = (ComponentPlugin) container.createComponent(pluginClass, plugin.getInitParams());
        cplugin.setName(plugin.getName());
        cplugin.setDescription(plugin.getDescription());

        this.addListenerPlugin(cplugin);
      } catch (Exception e) {
        LOG.error("Failed to instanciate component plugin " + plugin.getName() + ", type=" + plugin.getClass(), e);
      }
    }
  }

  /**
   * Add a selected OrganizationService listener to
   * OrganizationIntegrationService
   * 
   * @param listener
   */
  public synchronized void addListenerPlugin(ComponentPlugin listener) {
    if (listener instanceof OrganizationServiceInitializer) {
      return;
    } else if (listener instanceof UserEventListener) {
      userDAOListeners_.put(listener.getName(), (UserEventListener) listener);
    } else if (listener instanceof GroupEventListener) {
      groupDAOListeners_.put(listener.getName(), (GroupEventListener) listener);
    } else if (listener instanceof MembershipEventListener) {
      membershipDAOListeners_.put(listener.getName(), (MembershipEventListener) listener);
    } else if (listener instanceof UserProfileEventListener) {
      userProfileListeners_.put(listener.getName(), (UserProfileEventListener) listener);
    } else {
      LOG.warn("Unknown listener type : " + listener.getClass());
    }
  }

  /**
   * Apply OrganizationService listeners on all Groups
   */
  @Managed
  @ManagedDescription("invoke all organization model listeners")
  @Impact(ImpactType.WRITE)
  public void invokeAllListeners() {
    startRequest();
    try {
      PageList<User> users = organizationService.getUserHandler().getUserPageList(USERS_PAGE_SIZE);
      int availablePages = users.getAvailablePage();

      LOG.info("Apply listeners for all users");
      for (int i = 1; i <= availablePages; i++) {
        List<User> tmpUsers = users.getPage(i);
        for (User user : tmpUsers) {
          applyUserListeners(user.getUserName());
          startRequest();
        }
      }

      LOG.info("Apply listeners for all groups");
      List<Group> groups = new ArrayList<Group>(organizationService.getGroupHandler().getAllGroups());
      Collections.sort(groups, GROUP_COMPARATOR);
      for (Group group : groups) {
        applyGroupListeners(group);
      }
    } catch (Exception e) {
      LOG.error(e);
    }
    endRequest();

    // TODO: delete this instruction when EXOGTN-347 will be fixed
    startRequest();
  }

  /**
   * Apply OrganizationService listeners to all Organization Model Elements
   * (Users, Groups, Profile & Memberships)
   */
  @Managed
  @ManagedDescription("invoke all groups listeners")
  @Impact(ImpactType.WRITE)
  public void invokeAllGroupsListeners() {
    startRequest();
    try {
      LOG.info("Apply groups listeners");
      List<Group> groups = new ArrayList<Group>(organizationService.getGroupHandler().getAllGroups());
      Collections.sort(groups, GROUP_COMPARATOR);
      for (Group group : groups) {
        applyGroupListeners(group);
      }
    } catch (Exception e) {
      LOG.error("Error occured when invoking listeners of all groups.", e);
    }
    endRequest();

    // TODO: delete this instruction when EXOGTN-347 will be fixed
    startRequest();
  }

  /**
   * Apply OrganizationService listeners on selected groups
   * 
   * @param groupId
   *          The group Identifier
   */
  @Managed
  @ManagedDescription("invoke a group listeners")
  @Impact(ImpactType.WRITE)
  public void applyGroupListeners(@ManagedDescription("Group Id") @ManagedName("groupId") String groupId) {
    startRequest();
    try {
      Group group = organizationService.getGroupHandler().findGroupById(groupId);
      applyGroupListeners(group);
    } catch (Exception e) {
      LOG.error("Error occured when invoking listeners of group: " + groupId, e);
    }
    endRequest();

    // TODO: delete this instruction when EXOGTN-347 will be fixed
    startRequest();;
  }

  /**
   * Apply OrganizationService listeners on selected User
   * 
   * @param username
   *          The user name
   */
  @Managed
  @ManagedDescription("invoke a user listeners")
  @Impact(ImpactType.WRITE)
  public void applyUserListeners(@ManagedDescription("User name") @ManagedName("username") String username) {
    startRequest();
    try {
      User user = organizationService.getUserHandler().findUserByName(username);
      if (user.getCreatedDate() == null) {
        user.setCreatedDate(new Date());
      }
      if (!Util.hasUserFolder(repositoryService, user)) {
        LOG.info("\tApply listeners for user: " + username);
        Collection<UserEventListener> userDAOListeners = userDAOListeners_.values();
        for (UserEventListener userEventListener : userDAOListeners) {
          try {
            userEventListener.preSave(user, true);
            startRequest();
            userEventListener.postSave(user, true);
          } catch (Exception e) {
            LOG.warn("Failed to initialize " + username + " User with listener : " + userEventListener.getClass(), e);
          }
        }
      }
    } catch (Exception e) {
      LOG.warn("Failed to initialize " + username + " User", e);
    }
    endRequest();
    applyUserProfileListeners(username);
    applyMembershipsListeners(username);

    // TODO: delete this instruction when EXOGTN-347 will be fixed
    startRequest();
  }

  private void applyMembershipsListeners(String username) {
    startRequest();
    try {
      Collection memberships = organizationService.getMembershipHandler().findMembershipsByUser(username);
      for (Object objectMembership : memberships) {
        Membership membership = (Membership) objectMembership;
        Group group = organizationService.getGroupHandler().findGroupById(membership.getGroupId());
        applyGroupListeners(group);
        try {
          if (!Util.hasMembershipFolder(repositoryService, membership)) {
            LOG.info("\tApply listeners for membership : " + membership.getId());
            Collection<MembershipEventListener> membershipDAOListeners = membershipDAOListeners_.values();
            for (MembershipEventListener membershipEventListener : membershipDAOListeners) {
              try {
                membershipEventListener.preSave(membership, true);
                membershipEventListener.postSave(membership, true);
              } catch (Exception e) {
                LOG.warn("Failed to initialize " + username + " Membership (" + membership.getId() + ") listener = "
                    + membershipEventListener.getClass(), e);
              }
            }
          }
        } catch (Exception e) {
          LOG.warn("Failed to initialize " + username + " Membership (" + membership.getId() + ")", e);
        }
      }
    } catch (Exception e) {
      LOG.warn("Failed to initialize " + username + " Memberships listeners", e);
    }
    endRequest();
  }

  private void applyUserProfileListeners(String username) {
    startRequest();
    try {
      UserProfile userProfile = organizationService.getUserProfileHandler().findUserProfileByName(username);
      if (userProfile == null) {
        userProfile = organizationService.getUserProfileHandler().createUserProfileInstance(username);
        organizationService.getUserProfileHandler().saveUserProfile(userProfile, true);
        userProfile = organizationService.getUserProfileHandler().findUserProfileByName(username);
      }
      if (!Util.hasProfileFolder(repositoryService, userProfile)) {
        LOG.info("\tApply listeners for user prfile: " + username);
        Collection<UserProfileEventListener> userProfileListeners = userProfileListeners_.values();
        for (UserProfileEventListener userProfileEventListener : userProfileListeners) {
          try {
            if (userProfile.getUserInfoMap() == null) {
              userProfile.setUserInfoMap(new HashMap<String, String>());
            }
            userProfileEventListener.preSave(userProfile, true);
            userProfileEventListener.postSave(userProfile, true);
          } catch (Exception e) {
            LOG.warn("Failed to initialize " + username + " User profile with listener : " + userProfileEventListener.getClass(),
                e);
          }
        }
      }
    } catch (Exception e) {
      LOG.warn("Failed to initialize " + username + " User profile", e);
    }
    endRequest();
  }

  private void applyGroupListeners(Group group) {
    try {
      if (group.getParentId() != null && !group.getParentId().isEmpty()) {
        try {
          Group parentGroup = organizationService.getGroupHandler().findGroupById(group.getParentId());
          applyGroupListeners(parentGroup);
        } catch (Exception e) {
          LOG.warn("Error occured while attempting to get parent of " + group.getId()
              + " Group. Listeners will not be applied on parent " + group.getParentId(), e);
        }
      }
      if (!Util.hasGroupFolder(repositoryService, group)) {
        LOG.info("\tApply listeners for group: " + group.getId());
        Collection<GroupEventListener> groupDAOListeners = groupDAOListeners_.values();
        for (GroupEventListener groupEventListener : groupDAOListeners) {
          try {
            groupEventListener.preSave(group, true);
            groupEventListener.postSave(group, true);
          } catch (Exception e) {
            LOG.warn("Failed to initialize " + group.getId() + " Group, listener = " + groupEventListener.getClass(), e);
          }
        }
      }
    } catch (Exception e) {
      LOG.warn("Failed to initialize " + group.getId() + " Group listeners", e);
    }
  }

  private void endRequest() {
    if (requestStarted && organizationService instanceof ComponentRequestLifecycle) {
      try {
        // TODO: delete this instruction when EXOGTN-347 will be fixed
        ((ComponentRequestLifecycle) organizationService).startRequest(container);

        ((ComponentRequestLifecycle) organizationService).endRequest(container);
      } catch (Exception e) {
        LOG.warn(e);
      }
      requestStarted = false;
    }
  }

  private void startRequest() {
    if (organizationService instanceof ComponentRequestLifecycle) {
      ((ComponentRequestLifecycle) organizationService).startRequest(container);
      requestStarted = true;
    }
  }
}