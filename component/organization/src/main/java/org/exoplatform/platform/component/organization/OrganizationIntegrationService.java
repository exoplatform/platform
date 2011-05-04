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
import org.exoplatform.services.organization.impl.GroupImpl;
import org.exoplatform.services.organization.impl.MembershipImpl;
import org.exoplatform.services.organization.impl.UserImpl;
import org.exoplatform.services.organization.impl.UserProfileImpl;
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
@RESTEndpoint(path = "orgsync")
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
      if (LOG.isDebugEnabled()) {
        LOG.debug("This component has already " + nbExternalComponentPlugins + " ExternalComponentPlugins");
      }
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

      // Search for Groups that aren't yet integrated
      syncAllGroups(EventType.ADDED.toString());

      // Search for Groups that are deleted from Organization Datasource
      syncAllGroups(EventType.DELETED.toString());
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
   * Add a listener instance to dedicated list of one organization element.
   * 
   * @param listener
   *          have to extends UserEventListener, GroupEventListener,
   *          MembershipEventListener or UserProfileEventListener.
   */
  public void addListenerPlugin(ComponentPlugin listener) {
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
  @ManagedDescription("invoke all organization model listeners. Becarefull, this could takes a lot of time.")
  @Impact(ImpactType.READ)
  public void syncAll() {
    if (LOG.isDebugEnabled()) {
      LOG.debug("All groups, users, profiles and memberships listeners invocation.");
    }

    startRequest();
    try {

      if (LOG.isDebugEnabled()) {
        LOG.debug(" Search for non integrated Groups.");
      }
      syncAllGroups(EventType.ADDED.toString());
      if (LOG.isDebugEnabled()) {
        LOG.debug(" Search for deleted Groups, but remain integrated.");
      }

      syncAllGroups(EventType.DELETED.toString());

      if (LOG.isDebugEnabled()) {
        LOG.debug(" Search for non integrated Users.");
      }
      syncAllUsers(EventType.ADDED.toString());
      if (LOG.isDebugEnabled()) {
        LOG.debug(" Search for deleted Users, but remain integrated.");
      }
      syncAllUsers(EventType.DELETED.toString());
    } catch (Exception e) {
      LOG.error(e);
    }
    endRequest();

    // TODO: delete this instruction when EXOGTN-347 will be fixed
    startRequest();
  }

  /**
   * Invoke Groups listeners to all Organization Model Elements
   * 
   * @param eventType
   *          ADDED/DELETED/UPDATED
   * @throws Exception
   *           JCR or IDM operation failure
   */
  @Managed
  @ManagedDescription("invoke all groups listeners")
  @Impact(ImpactType.READ)
  public void syncAllGroups(@ManagedDescription("Scan for added or deleted groups") @ManagedName("eventType") String eventType)
      throws Exception {
    if (LOG.isDebugEnabled()) {
      LOG.debug("All Groups listeners invocation, operation= " + eventType);
    }

    startRequest();

    List<Group> groups = new ArrayList<Group>(organizationService.getGroupHandler().getAllGroups());
    // Invoke listeners on groups, starting from parent groups to children
    Collections.sort(groups, GROUP_COMPARATOR);

    EventType event = EventType.valueOf(eventType);
    switch (event) {
      case DELETED: {
        // Invoke delete listeners on groups, starting from children groups
        // to parent
        Collections.reverse(groups);
        // Search for deleted groups, and invoke
        // GroupEventListener#preDelete and #postDelete
        Session session = null;
        try {
          session = repositoryService.getCurrentRepository().getSystemSession(Util.WORKSPACE);
          List<String> activatedGroups = Util.getActivatedGroups(session);
          for (Group group : groups) {
            activatedGroups.remove(group.getId());
          }
          for (String groupId : activatedGroups) {
            syncGroup(groupId, eventType);
          }
        } finally {
          if (session != null) {
            session.logout();
          }
        }
        break;
      }
      case UPDATED: {
        // Search for added groups that aren't yet integrated
        Session session = null;
        try {
          session = repositoryService.getCurrentRepository().getSystemSession(Util.WORKSPACE);
          List<String> activatedGroups = Util.getActivatedGroups(session);
          for (String groupId : activatedGroups) {
            syncGroup(groupId, eventType);
          }
        } finally {
          if (session != null) {
            session.logout();
          }
        }
        break;
      }
      case ADDED: {
        // Search for added groups that aren't yet integrated
        Session session = null;
        try {
          session = repositoryService.getCurrentRepository().getSystemSession(Util.WORKSPACE);
          List<String> activatedGroups = Util.getActivatedGroups(session);
          for (Group group : groups) {
            if (!activatedGroups.contains(group.getId())) {
              syncGroup(group.getId(), eventType);
            }
          }
        } finally {
          if (session != null) {
            session.logout();
          }
        }
        break;
      }
    }
    endRequest();

    // TODO: delete this instruction when EXOGTN-347 will be fixed
    startRequest();
  }

  /**
   * Apply OrganizationService listeners on a selected group.
   * 
   * @param groupId
   *          The group Identifier
   * @param eventType
   *          ADDED/UPDATED/DELETED
   */
  @Managed
  @ManagedDescription("invoke a group listeners")
  @Impact(ImpactType.WRITE)
  public void syncGroup(@ManagedDescription("Group Id") @ManagedName("groupId") String groupId,
      @ManagedDescription("Event type ADDED, UPDATED or DELETED") @ManagedName("eventType") String eventType) {

    if (LOG.isDebugEnabled()) {
      LOG.debug("\tGroup listeners invocation, operation= " + eventType + ", for group= " + groupId);
    }
    startRequest();
    EventType event = EventType.valueOf(eventType);
    switch (event) {
      case DELETED: {
        {
          Session session = null;
          try {
            session = repositoryService.getCurrentRepository().getSystemSession(Util.WORKSPACE);

            List<Group> groups = Util.getActivatedChildrenGroup(session, groupId);

            Collections.sort(groups, GROUP_COMPARATOR);
            Collections.reverse(groups);
            for (Group group : groups) {
              invokeDeleteGroupListeners(group.getId());
            }
          } catch (Exception e) {

          } finally {
            if (session != null) {
              session.logout();
            }
          }
          invokeDeleteGroupListeners(groupId);
        }
        break;
      }
      case ADDED:
      case UPDATED: {
        try {
          Group group = organizationService.getGroupHandler().findGroupById(groupId);
          if (group == null) {
            LOG.warn("\t\t" + groupId + " group wasn't found.");
            return;
          }
          invokeListenersToSavedGroup(group, event.equals(EventType.ADDED));
        } catch (Exception e) {
          LOG.error("\t\t" + "Error occured while invoking listeners of group: " + groupId, e);
        }
        break;
      }
    }
    endRequest();

    // TODO: delete this instruction when EXOGTN-347 will be fixed
    startRequest();
  }

  /**
   * Apply all users OrganizationService listeners
   * 
   * @param username
   *          The user name
   */
  @Managed
  @ManagedDescription("invoke all users listeners")
  @Impact(ImpactType.READ)
  public void syncAllUsers(@ManagedDescription("Event type: added/updated/deleted") @ManagedName("eventType") String eventType) {

    if (LOG.isDebugEnabled()) {
      LOG.debug("All users listeners invocation, eventType = " + eventType);
    }
    EventType event = EventType.valueOf(eventType);
    Session session = null;
    switch (event) {
      case DELETED: {
        if (LOG.isDebugEnabled()) {
          LOG.debug("\tSearch for deleted users and invoke related listeners.");
        }
        try {
          session = repositoryService.getCurrentRepository().getSystemSession(Util.WORKSPACE);
          List<String> activatedUsers = Util.getActivatedUsers(session);

          PageList<User> users = organizationService.getUserHandler().getUserPageList(USERS_PAGE_SIZE);
          int availablePages = users.getAvailablePage();

          for (int i = 1; i <= availablePages; i++) {
            List<User> tmpUsers = users.getPage(i);
            for (User user : tmpUsers) {
              activatedUsers.remove(user.getUserName());
            }
          }
          for (String username : activatedUsers) {
            syncUser(username, eventType);
          }
        } catch (Exception e) {
          LOG.error("\t\tUnknown error was occured while preparing to proceed users deletion", e);
        } finally {
          if (session != null) {
            session.logout();
          }
        }
        break;
      }
      case UPDATED: {
        if (LOG.isDebugEnabled()) {
          LOG.debug("\tAll users update invocation: Search for already existing users in Datasource and that are already integrated.");
        }
        try {
          session = repositoryService.getCurrentRepository().getSystemSession(Util.WORKSPACE);
          List<String> activatedUsers = Util.getActivatedUsers(session);
          for (String username : activatedUsers) {
            syncUser(username, eventType);
          }
        } catch (Exception e) {
          LOG.error("\tUnknown error was occured while preparing to proceed users update", e);
        } finally {
          if (session != null) {
            session.logout();
          }
        }
        break;
      }
      case ADDED: {
        try {
          session = repositoryService.getCurrentRepository().getSystemSession(Util.WORKSPACE);
          List<String> activatedUsers = Util.getActivatedUsers(session);

          if (LOG.isDebugEnabled()) {
            LOG.debug("\tAll new users intagration: Search for already existing users in Datasource but not integrated yet.");
          }
          PageList<User> users = organizationService.getUserHandler().getUserPageList(USERS_PAGE_SIZE);
          int availablePages = users.getAvailablePage();

          for (int i = 1; i <= availablePages; i++) {
            List<User> tmpUsers = users.getPage(i);
            for (User user : tmpUsers) {
              if (!activatedUsers.contains(user.getUserName())) {
                syncUser(user.getUserName(), eventType);
              }
            }
          }
        } catch (Exception e) {
          LOG.error("\tUnknown error was occured while preparing to proceed users update", e);
        } finally {
          if (session != null) {
            session.logout();
          }
        }
        break;
      }
    }
  }

  /**
   * Apply OrganizationService listeners on selected User
   * 
   * @param username
   *          The user name
   * @param eventType
   *          ADDED/UPDATED/DELETED
   */
  @Managed
  @ManagedDescription("invoke a user listeners")
  @Impact(ImpactType.READ)
  public void syncUser(@ManagedDescription("User name") @ManagedName("username") String username,
      @ManagedDescription("Event type") @ManagedName("eventType") String eventType) {
    if (LOG.isDebugEnabled()) {
      LOG.debug("\tUser listeners invocation, operation= " + eventType + ", for user= " + username);
    }
    EventType event = EventType.valueOf(eventType);
    startRequest();
    switch (event) {
      case DELETED: {
        User user = null;
        try {
          user = organizationService.getUserHandler().findUserByName(username);
        } catch (Exception e) {
          LOG.warn("\t\tError occured while verifying if user is present in Datasource or not."
              + " This may not cause a problem :" + e.getMessage());
        }
        if (user != null) {
          LOG.warn("\t\tUser exists: can't invoke delete listeners on the existant user : " + username);
          return;
        }
        invokeUserMembershipsListeners(username, event);
        invokeUserProfileListeners(username, event);
        Session session = null;
        try {
          session = repositoryService.getCurrentRepository().getSystemSession(Util.WORKSPACE);
          if (Util.hasUserFolder(session, username)) {
            LOG.info("Invoke user deletion: " + username);
            user = new UserImpl(username);
            Collection<UserEventListener> userDAOListeners = userDAOListeners_.values();
            for (UserEventListener userEventListener : userDAOListeners) {
              try {
                userEventListener.preDelete(user);
              } catch (Exception e) {
                LOG.error(
                    "\t\tFailed to call preDelete on " + username + " User with listener : " + userEventListener.getClass(), e);
              }
              try {
                startRequest();
                userEventListener.postDelete(user);
              } catch (Exception e) {
                LOG.error(
                    "\t\tFailed to call postDelete on " + username + " User with listener : " + userEventListener.getClass(), e);
              }
            }
          }
        } catch (Exception e) {
          LOG.error("\t\tFailed to initialize " + username + " User", e);
        } finally {
          if (session != null) {
            session.logout();
          }
        }
        endRequest();
        break;
      }
      case ADDED:
      case UPDATED: {
        Session session = null;
        try {
          boolean isNew = event.equals(EventType.ADDED);
          session = repositoryService.getCurrentRepository().getSystemSession(Util.WORKSPACE);
          if (!isNew || !Util.hasUserFolder(session, username)) {
            User user = organizationService.getUserHandler().findUserByName(username);
            if (user.getCreatedDate() == null) {
              user.setCreatedDate(new Date());
            }
            LOG.info("Invoke " + username + " user synchronization ");
            Collection<UserEventListener> userDAOListeners = userDAOListeners_.values();
            for (UserEventListener userEventListener : userDAOListeners) {
              try {
                userEventListener.preSave(user, isNew);
              } catch (Exception e) {
                LOG.warn("\t\tFailed to call preSave for " + username + " User with listener : " + userEventListener.getClass(),
                    e);
              }
              try {
                startRequest();
                userEventListener.postSave(user, isNew);
              } catch (Exception e) {
                LOG.warn("\t\tFailed to call postSave for " + username + " User with listener : " + userEventListener.getClass(),
                    e);
              }
            }
          }
        } catch (Exception e) {
          LOG.warn("\t\tFailed to call listeners for " + username + " User", e);
        } finally {
          if (session != null) {
            session.logout();
          }
        }
        endRequest();
        invokeUserProfileListeners(username, event);
        invokeUserMembershipsListeners(username, event);
        break;
      }
    }
    // TODO: delete this instruction when EXOGTN-347 will be fixed
    startRequest();
  }

  /**
   * trigger ADD,UPDATE or DELETE membership listeners. The membership is
   * identified by the username, groupId and membershipType.
   * 
   * @param username
   * @param groupId
   * @param eventType
   */
  @Managed
  @ManagedDescription("invoke a membership listeners")
  @Impact(ImpactType.READ)
  public void syncMembership(@ManagedDescription("User name") @ManagedName("username") String username,
      @ManagedDescription("group identifier") @ManagedName("groupId") String groupId,
      @ManagedDescription("event type") @ManagedName("eventType") String eventType) {

    if (LOG.isDebugEnabled()) {
      LOG.debug("Memberships listeners invocation, operation= " + eventType + ", for membership=" + username + ":" + groupId);
    }

    EventType event = EventType.valueOf(eventType);
    startRequest();
    switch (event) {
      case DELETED: {
        Session session = null;
        try {
          session = repositoryService.getCurrentRepository().getSystemSession(Util.WORKSPACE);
          List<Membership> activatedMemberships = Util.getActivatedMembershipsRelatedToUser(session, username);
          // Select memberships with given username and groupId
          int i = 0;
          while (i < activatedMemberships.size()) {
            Membership membership = activatedMemberships.get(i);
            if (membership.getGroupId().equals(groupId)) {
              activatedMemberships.remove(i);
            } else {
              i++;
            }
          }
          if (activatedMemberships.isEmpty()) {
            if (LOG.isDebugEnabled()) {
              LOG.debug("No integrated memberships was found for user: " + username + ", and group = " + groupId);
            }
            return;
          }
          List<Membership> memberships = null;
          try {
            memberships = (List<Membership>) organizationService.getMembershipHandler().findMembershipsByUserAndGroup(username,
                groupId);
          } catch (Exception e) {
            LOG.error("\t\tError occured while verifying if membership is present in Datasource or not. This may not cause a problem :"
                + e.getMessage());
          }
          if (memberships == null) {
            memberships = new ArrayList<Membership>();
          }

          for (Membership membership : activatedMemberships) {
            if (!contains(membership, memberships)) {
              invokeMembershipListeners(username, groupId, membership.getMembershipType(), event);
            }
          }
        } catch (Exception e) {
          LOG.error("\t\tUnknown error was occured while preparing to proceed membership deletion", e);
        } finally {
          if (session != null) {
            session.logout();
          }
        }
        break;
      }
      case ADDED:
      case UPDATED: {
        boolean isNew = EventType.ADDED.equals(eventType);
        Session session = null;
        try {
          List<Membership> memberships = null;
          try {
            memberships = (List<Membership>) organizationService.getMembershipHandler().findMembershipsByUserAndGroup(username,
                groupId);
          } catch (Exception e) {
            LOG.error("\t\tError occured while verifying if membership is present in Datasource or not. This may not cause a problem :"
                + e.getMessage());
          }
          if (memberships == null || memberships.isEmpty()) {
            if (LOG.isDebugEnabled()) {
              LOG.debug("No integrated memberships was found for user: " + username + ", and group = " + groupId);
            }
            return;
          }

          session = repositoryService.getCurrentRepository().getSystemSession(Util.WORKSPACE);
          List<Membership> activatedMemberships = Util.getActivatedMembershipsRelatedToUser(session, username);
          // Select memberships with given username and groupId
          int i = 0;
          while (i < activatedMemberships.size()) {
            Membership membership = activatedMemberships.get(i);
            if (membership.getGroupId().equals(groupId)) {
              activatedMemberships.remove(i);
            } else {
              i++;
            }
          }
          if (!isNew && activatedMemberships.isEmpty()) {
            if (LOG.isDebugEnabled()) {
              LOG.debug("No integrated memberships was found for user: " + username + ", abd group = " + groupId);
            }
            return;
          }

          for (Membership membership : memberships) {
            if (isNew) {
              if (!contains(membership, activatedMemberships)) {
                invokeMembershipListeners(username, groupId, membership.getMembershipType(), event);
              }
            } else {
              if (contains(membership, activatedMemberships)) {
                invokeMembershipListeners(username, groupId, membership.getMembershipType(), event);
              }
            }
          }
        } catch (Exception e) {
          LOG.error("\t\tUnknown error was occured while preparing to proceed membership deletion", e);
        } finally {
          if (session != null) {
            session.logout();
          }
        }
        break;
      }
    }
    endRequest();
  }

  private void invokeMembershipListeners(String username, String groupId, String membershipType, EventType eventType) {

    if (LOG.isDebugEnabled()) {
      LOG.debug("\tMembership listeners invocation, operation= " + eventType + ", for membership= " + membershipType + ":"
          + username + ":" + groupId);
    }

    startRequest();
    switch (eventType) {
      case DELETED: {
        try {
          Membership membership = null;
          try {
            membership = organizationService.getMembershipHandler().findMembershipByUserGroupAndType(username, groupId,
                membershipType);
          } catch (Exception e) {
            LOG.warn("\t\tError occured while verifying if membership is present in Datasource or not. This may not cause a problem :"
                + e.getMessage());
          }
          if (membership != null) {
            LOG.warn("\t\tMembership exists: can't invoke delete listeners on the existant membership : " + membership.getId());
            return;
          }
          {
            membership = new MembershipImpl();
            ((MembershipImpl) membership).setGroupId(groupId);
            ((MembershipImpl) membership).setUserName(username);
            ((MembershipImpl) membership).setMembershipType(membershipType);
            ((MembershipImpl) membership).setId(Util.computeId(membership));
          }
          try {
            LOG.info("Invoke " + membership.getId() + " Membership deletion listeners.");
            Collection<MembershipEventListener> membershipDAOListeners = membershipDAOListeners_.values();
            for (MembershipEventListener membershipEventListener : membershipDAOListeners) {
              try {
                membershipEventListener.preDelete(membership);
              } catch (Exception e) {
                LOG.error("\t\tFailed to call preDelete on " + username + " Membership (" + membership.getId() + ") listener = "
                    + membershipEventListener.getClass(), e);
              }
              try {
                membershipEventListener.postDelete(membership);
              } catch (Exception e) {
                LOG.error("\t\tFailed to call postDelete on " + username + " Membership (" + membership.getId() + ") listener = "
                    + membershipEventListener.getClass(), e);
              }
            }
          } catch (Exception e) {
            LOG.error("\t\tFailed to call listeners on Membership (" + membership.getId() + ")", e);
          }
        } catch (Exception e) {
          LOG.error("\t\tUnknown error was occured while preparing to proceed membership deletion", e);
        }
        break;
      }
      case ADDED:
      case UPDATED: {
        boolean isNew = EventType.ADDED.equals(eventType);
        Session session = null;
        try {
          session = repositoryService.getCurrentRepository().getSystemSession(Util.WORKSPACE);
          Membership membership = organizationService.getMembershipHandler().findMembershipByUserGroupAndType(username, groupId,
              membershipType);
          try {
            if (!Util.hasGroupFolder(session, groupId)) {
              syncGroup(groupId, EventType.ADDED.toString());
            }
            if (!Util.hasUserFolder(session, username)) {
              syncUser(username, EventType.ADDED.toString());
            }
            if (membership != null && (!isNew || !Util.hasMembershipFolder(session, membership))) {
              LOG.info("Invoke " + membership.getId() + " Membership synchronization.");
              Collection<MembershipEventListener> membershipDAOListeners = membershipDAOListeners_.values();
              for (MembershipEventListener membershipEventListener : membershipDAOListeners) {
                try {
                  membershipEventListener.preSave(membership, isNew);
                } catch (Exception e) {
                  LOG.error("\t\tFailed to call preSave on Membership (" + membership.getId() + ",isNew = " + isNew
                      + ") listener = " + membershipEventListener.getClass(), e);
                }
                try {
                  membershipEventListener.postSave(membership, isNew);
                } catch (Exception e) {
                  LOG.error("\t\tFailed to call postSave on Membership (" + membership.getId() + ") listener = "
                      + membershipEventListener.getClass(), e);
                }
              }
            }
          } catch (Exception e) {
            LOG.error("\t\tFailed to call listeners on Membership (" + membership.getId() + ")", e);
          }
        } catch (Exception e) {
          LOG.error("\t\tFailed to call listeners on " + username + " Memberships listeners", e);
        } finally {
          if (session != null) {
            session.logout();
          }
        }
        break;
      }
    }
    endRequest();
  }

  private void invokeDeleteGroupListeners(String groupId) {
    try {
      Group group = organizationService.getGroupHandler().findGroupById(groupId);
      if (group != null) {
        LOG.warn("\t\tGroup exists: can't invoke delete listeners on the existant group : " + groupId);
        return;
      }
    } catch (Exception exception) {
      LOG.error("\t\tException while trying to get a group instance with id : " + groupId
          + ", it may has been already synchronized:" + exception.getMessage());
    }

    Session session = null;
    try {
      session = repositoryService.getCurrentRepository().getSystemSession(Util.WORKSPACE);
      if (!Util.hasGroupFolder(session, groupId)) {
        if (LOG.isDebugEnabled()) {
          LOG.debug("\t\tGroup doesn't exist or has been already deleted.");
        }
        return;
      }
      List<Membership> memberships = Util.getActivatedMembershipsRelatedToGroup(session, groupId);
      for (Membership membership : memberships) {
        // Memberships could be managed internally in eXo Datasource,
        // so synchronize with the remote Datasources by removing all
        // memberships of deleted group
        Membership tmpMembership = organizationService.getMembershipHandler().removeMembership(membership.getId(), true);
        if (tmpMembership == null) { // if the Membership doesn't exist
                                     // in datasource, then it's not
                                     // deleted, and so listeners
                                     // aren't triggered, in this case
                                     // force the listeners execution
          invokeMembershipListeners(membership.getUserName(), membership.getGroupId(), membership.getMembershipType(),
              EventType.DELETED);
        }
      }

    } catch (Exception exception) {
      LOG.error("\t\tCouldn't process deletion of Memberships related to the group : " + groupId, exception);
    } finally {
      if (session != null) {
        session.logout();
      }
    }
    GroupImpl group = new GroupImpl(groupId);
    group.setId(groupId);
    Collection<GroupEventListener> groupDAOListeners = groupDAOListeners_.values();
    LOG.info("Invoke " + groupId + "Group deletion listeners.");
    for (GroupEventListener groupEventListener : groupDAOListeners) {
      try {
        groupEventListener.preDelete(group);
      } catch (Exception e) {
        LOG.warn("\t\tFailed to call preDelete on " + group.getId() + " Group, listener = " + groupEventListener.getClass(), e);
      }
      try {
        groupEventListener.postDelete(group);
      } catch (Exception e) {
        LOG.warn("\t\tFailed to call postDelete on " + group.getId() + " Group, listener = " + groupEventListener.getClass(), e);
      }
    }
  }

  private void invokeUserMembershipsListeners(String username, EventType eventType) {

    if (LOG.isDebugEnabled()) {
      LOG.debug("\t\tMemberships listeners invocation, operation= " + eventType + ", for user= " + username);
    }
    startRequest();
    switch (eventType) {
      case DELETED: {
        Session session = null;
        try {
          Collection userMemberships = null;
          try {
            userMemberships = organizationService.getMembershipHandler().removeMembershipByUser(username, true);
          } catch (Exception exception) {
            LOG.error("\t\t\tCouldn't process deletion of Memberships related to the user : " + username, exception);
          }
          if (userMemberships == null || userMemberships.isEmpty()) {
            session = repositoryService.getCurrentRepository().getSystemSession(Util.WORKSPACE);
            List<Membership> memberships = Util.getActivatedMembershipsRelatedToUser(session, username);
            for (Membership membership : memberships) {
              invokeMembershipListeners(username, membership.getGroupId(), membership.getMembershipType(), eventType);
            }
          } else { // Delete all related Memberships
            LOG.error("\t\t\tUser " + username + " was deleted, but some memberships are always existing : " + userMemberships);
          }
        } catch (Exception e) {
          LOG.error("\t\t\tUnknown error was occured while preparing to proceed membership deletion", e);
        } finally {
          if (session != null) {
            session.logout();
          }
        }
        break;
      }
      case ADDED:
      case UPDATED: {
        boolean isNew = EventType.ADDED.equals(eventType);
        Session session = null;
        Collection memberships = null;
        List<Membership> activatedMemberships = null;
        try {
          session = repositoryService.getCurrentRepository().getSystemSession(Util.WORKSPACE);
          memberships = organizationService.getMembershipHandler().findMembershipsByUser(username);
          activatedMemberships = Util.getActivatedMembershipsRelatedToUser(session, username);
        } catch (Exception e) {
          LOG.error("\t\t\tFailed to call Membership listeners for user : " + username, e);
          throw new IllegalStateException(e);
        } finally {
          if (session != null) {
            session.logout();
          }
        }
        for (Object membershipObject : memberships) {
          Membership membership = (Membership) membershipObject;
          boolean isAlreadyIntegrated = contains(membership, activatedMemberships);
          if (isNew) {
            if (!isAlreadyIntegrated) {
              invokeMembershipListeners(username, membership.getGroupId(), membership.getMembershipType(), eventType);
            } else {
              if (LOG.isDebugEnabled()) {
                LOG.debug("\t\t\t" + membership.getId() + " Membership is already integrated");
              }
            }
          } else {
            if (isAlreadyIntegrated) {
              invokeMembershipListeners(username, membership.getGroupId(), membership.getMembershipType(), eventType);
            } else {
              if (LOG.isDebugEnabled()) {
                LOG.debug("\t\t\t" + membership.getId()
                    + " Membership is not yet added, add invoke listeners with parameter isNew=true");
              }
              invokeMembershipListeners(username, membership.getGroupId(), membership.getMembershipType(), EventType.ADDED);
            }
          }
        }
        break;
      }
    }
    endRequest();
  }

  private boolean contains(Membership membership, List<Membership> activatedMemberships) {
    if (membership == null || activatedMemberships == null || activatedMemberships.size() == 0) {
      return false;
    }
    for (Membership tmpMembership : activatedMemberships) {
      if (tmpMembership.getId().equals(membership.getId())) {
        return true;
      }
    }
    return false;
  }

  private void invokeUserProfileListeners(String username, EventType eventType) {
    if (LOG.isDebugEnabled()) {
      LOG.debug("\t\tProfile listeners invocation, operation= " + eventType + ", for user= " + username);
    }
    startRequest();
    switch (eventType) {
      case ADDED:
      case UPDATED: {
        Session session = null;
        try {
          session = repositoryService.getCurrentRepository().getSystemSession(Util.WORKSPACE);
          boolean isNew = EventType.ADDED.equals(eventType);
          UserProfile userProfile = organizationService.getUserProfileHandler().findUserProfileByName(username);
          if (userProfile == null) {
            userProfile = organizationService.getUserProfileHandler().createUserProfileInstance(username);
            organizationService.getUserProfileHandler().saveUserProfile(userProfile, isNew);
            userProfile = organizationService.getUserProfileHandler().findUserProfileByName(username);
          }
          if (!isNew || !Util.hasProfileFolder(session, username)) {
            LOG.info("Invoke " + username + " user profile synchronization.");
            Collection<UserProfileEventListener> userProfileListeners = userProfileListeners_.values();
            for (UserProfileEventListener userProfileEventListener : userProfileListeners) {
              if (userProfile.getUserInfoMap() == null) {
                userProfile.setUserInfoMap(new HashMap<String, String>());
              }
              try {
                userProfileEventListener.preSave(userProfile, isNew);
              } catch (Exception e) {
                LOG.warn("\t\t\tFailed to call preSave on " + username + " User profile with listener : "
                    + userProfileEventListener.getClass(), e);
              }
              try {
                userProfileEventListener.postSave(userProfile, isNew);
              } catch (Exception e) {
                LOG.warn("\t\t\tFailed to call postSave on " + username + " User profile with listener : "
                    + userProfileEventListener.getClass(), e);
              }
            }
          }
        } catch (Exception e) {
          LOG.warn("\t\t\tFailed to call listeners on " + username + " User profile", e);
        } finally {
          if (session != null) {
            session.logout();
          }
        }
        break;
      }
      case DELETED: {
        Session session = null;
        try {
          session = repositoryService.getCurrentRepository().getSystemSession(Util.WORKSPACE);

          UserProfile userProfile = null;
          try {
            userProfile = organizationService.getUserProfileHandler().findUserProfileByName(username);
          } catch (Exception e) {
            LOG.warn("\t\t\tError occured while verifying if userProfile is present in Datasource or not. This may not cause a problem :"
                + e.getMessage());
          }
          if (userProfile != null) {
            organizationService.getUserProfileHandler().removeUserProfile(username, true);
          } else if (Util.hasProfileFolder(session, username)) {
            LOG.info("Invoke " + username + " user profile deletion listeners.");
            userProfile = new UserProfileImpl(username);
            userProfile.setUserInfoMap(new HashMap<String, String>());
            Collection<UserProfileEventListener> userProfileListeners = userProfileListeners_.values();
            for (UserProfileEventListener userProfileEventListener : userProfileListeners) {
              try {
                userProfileEventListener.preDelete(userProfile);
              } catch (Exception e) {
                LOG.warn("\t\t\tFailed to call preSave on " + username + " User profile with listener : "
                    + userProfileEventListener.getClass(), e);
              }
              try {
                userProfileEventListener.postDelete(userProfile);
              } catch (Exception e) {
                LOG.warn("\t\t\tFailed to call postSave on " + username + " User profile with listener : "
                    + userProfileEventListener.getClass(), e);
              }
            }
          }
        } catch (Exception e) {
          LOG.warn("\t\t\tFailed to call listeners on " + username + " User profile", e);
        } finally {
          if (session != null) {
            session.logout();
          }
        }
        break;
      }
    }
    endRequest();
  }

  private void invokeListenersToSavedGroup(Group group, boolean isNew) {
    Session session = null;
    try {
      session = repositoryService.getCurrentRepository().getSystemSession(Util.WORKSPACE);
      if (group.getParentId() != null && !group.getParentId().isEmpty()) {
        try {
          Group parentGroup = organizationService.getGroupHandler().findGroupById(group.getParentId());
          invokeListenersToSavedGroup(parentGroup, isNew);
        } catch (Exception e) {
          LOG.warn("\t\tError occured while attempting to get parent of " + group.getId()
              + " Group. Listeners will not be applied on parent " + group.getParentId(), e);
        }
      }
      if (!isNew || !Util.hasGroupFolder(session, group.getId())) {
        LOG.info("Invoke " + group.getId() + "Group deletion listeners.");
        Collection<GroupEventListener> groupDAOListeners = groupDAOListeners_.values();
        for (GroupEventListener groupEventListener : groupDAOListeners) {
          try {
            groupEventListener.preSave(group, isNew);
          } catch (Exception e) {
            LOG.warn("\t\t\tFailed to call preSave on " + group.getId() + " Group, listener = " + groupEventListener.getClass(),
                e);
          }
          try {
            groupEventListener.postSave(group, isNew);
          } catch (Exception e) {
            LOG.warn("\t\t\tFailed to call postSave on " + group.getId() + " Group, listener = " + groupEventListener.getClass(),
                e);
          }
        }
      }
    } catch (Exception e) {
      LOG.warn("\t\t\tFailed to call listeners for " + group.getId() + " Group.", e);
    } finally {
      if (session != null) {
        session.logout();
      }
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