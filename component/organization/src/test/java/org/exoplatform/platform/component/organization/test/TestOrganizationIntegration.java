package org.exoplatform.platform.component.organization.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.jcr.Session;

import org.exoplatform.commons.utils.PageList;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.ComponentRequestLifecycle;
import org.exoplatform.platform.component.organization.EventType;
import org.exoplatform.platform.component.organization.NewGroupListener;
import org.exoplatform.platform.component.organization.NewMembershipListener;
import org.exoplatform.platform.component.organization.NewProfileListener;
import org.exoplatform.platform.component.organization.NewUserListener;
import org.exoplatform.platform.component.organization.OrganizationIntegrationService;
import org.exoplatform.platform.component.organization.Util;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.Membership;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.UserProfile;
import org.exoplatform.services.organization.impl.MembershipImpl;
import org.exoplatform.test.BasicTestCase;

public class TestOrganizationIntegration extends BasicTestCase {
  PortalContainer container = null;
  RepositoryService repositoryService = null;
  OrganizationService organizationService = null;

  @Override
  protected void setUp() throws Exception {
    container = PortalContainer.getInstance();
    repositoryService = (RepositoryService) container.getComponentInstanceOfType(RepositoryService.class);
    organizationService = (OrganizationService) container.getComponentInstanceOfType(OrganizationService.class);
  }

  public void testIntegrationService() throws Exception {
    verifyFoldersCreation(false);

    OrganizationIntegrationService organizationIntegrationService = container
        .createComponent(OrganizationIntegrationService.class);
    container.registerComponentInstance(organizationIntegrationService);

    NewUserListener userListener = container.createComponent(NewUserListener.class);
    organizationIntegrationService.addListenerPlugin(userListener);
    NewProfileListener profileListener = container.createComponent(NewProfileListener.class);
    organizationIntegrationService.addListenerPlugin(profileListener);
    NewMembershipListener membershipListener = container.createComponent(NewMembershipListener.class);
    organizationIntegrationService.addListenerPlugin(membershipListener);
    NewGroupListener groupListener = container.createComponent(NewGroupListener.class);
    organizationIntegrationService.addListenerPlugin(groupListener);

    Session session = null;
    try {
      session = repositoryService.getCurrentRepository().getSystemSession(Util.WORKSPACE);
      organizationIntegrationService.invokeGroupListeners("/organization/management/executive-board", EventType.ADDED.toString());
      organizationIntegrationService.invokeGroupListeners("/organization/management/executive-board",
          EventType.DELETED.toString());

      assertTrue(Util.hasGroupFolder(session, "/organization"));
      assertTrue(Util.hasGroupFolder(session, "/organization/management"));
      assertTrue(Util.hasGroupFolder(session, "/organization/management/executive-board"));

      organizationIntegrationService.invokeGroupsListeners(EventType.DELETED.toString());

      assertTrue(Util.hasGroupFolder(session, "/organization"));
      assertTrue(Util.hasGroupFolder(session, "/organization/management"));
      assertTrue(Util.hasGroupFolder(session, "/organization/management/executive-board"));

      organizationIntegrationService.invokeUserListeners("root", EventType.ADDED.toString());

      verifyUserFoldersCreation("root", true);

      organizationIntegrationService.invokeUserListeners("root", EventType.DELETED.toString());

      verifyUserFoldersCreation("root", true);

      deleteUser("root");
      organizationIntegrationService.invokeUserListeners("root", EventType.DELETED.toString());

      verifyUserFoldersCreation("root", false);

      deleteGroup("/organization");
      organizationIntegrationService.invokeGroupListeners("/organization", EventType.DELETED.toString());

      assertFalse(Util.hasGroupFolder(session, "/organization"));
      assertFalse(Util.hasGroupFolder(session, "/organization/management"));
      assertFalse(Util.hasGroupFolder(session, "/organization/management/executive-board"));

      MembershipImpl membership = new MembershipImpl();
      {
        membership.setMembershipType("manager");
        membership.setUserName("john");
        membership.setGroupId("/organization/management/executive-board");
        membership.setId(Util.computeId(membership));

        assertFalse(Util.hasMembershipFolder(session, membership));
      }

      organizationIntegrationService.invokeAllListeners();
      verifyFoldersCreation(true);

      if (organizationService instanceof ComponentRequestLifecycle) {
        ((ComponentRequestLifecycle) organizationService).startRequest(container);
      }
      List<Group> groups = new ArrayList<Group>(organizationService.getGroupHandler().getAllGroups());
      Collections.sort(groups, OrganizationIntegrationService.GROUP_COMPARATOR);
      Collections.reverse(groups);

      for (Group group : groups) {
        organizationService.getGroupHandler().removeGroup(group, true);
      }

      List<User> users = new ArrayList<User>(organizationService.getUserHandler().getUserPageList(100).getPage(1));
      for (User user : users) {
        organizationService.getUserHandler().removeUser(user.getUserName(), true);
      }

      if (organizationService instanceof ComponentRequestLifecycle) {
        ((ComponentRequestLifecycle) organizationService).endRequest(container);
      }
      organizationIntegrationService.invokeAllListeners();

      assertFalse(Util.getGroupsFolder(session).hasNodes());
      assertFalse(Util.getMembershipsFolder(session).hasNodes());
      assertFalse(Util.getUsersFolder(session).hasNodes());
      assertFalse(Util.getProfilesFolder(session).hasNodes());
    } finally {
      if (session != null) {
        session.logout();
      }
    }
  }

  private void deleteUser(String username) throws Exception {
    if (organizationService instanceof ComponentRequestLifecycle) {
      ((ComponentRequestLifecycle) organizationService).startRequest(container);
    }
    organizationService.getUserHandler().removeUser(username, true);
    if (organizationService instanceof ComponentRequestLifecycle) {
      ((ComponentRequestLifecycle) organizationService).endRequest(container);
    }
  }

  private void deleteGroup(String groupId) throws Exception {
    if (organizationService instanceof ComponentRequestLifecycle) {
      ((ComponentRequestLifecycle) organizationService).startRequest(container);
    }
    organizationService.getGroupHandler().removeGroup(organizationService.getGroupHandler().findGroupById(groupId), true);
    if (organizationService instanceof ComponentRequestLifecycle) {
      ((ComponentRequestLifecycle) organizationService).endRequest(container);
    }
  }

  private void verifyUserFoldersCreation(String username, boolean creationAssertionValue) throws Exception {
    Session session = null;
    try {
      session = repositoryService.getCurrentRepository().getSystemSession(Util.WORKSPACE);
      if (organizationService instanceof ComponentRequestLifecycle) {
        ((ComponentRequestLifecycle) organizationService).startRequest(container);
      }
      assertEquals(creationAssertionValue, Util.hasUserFolder(session, username));
      assertEquals(creationAssertionValue, Util.hasProfileFolder(session, username));

      Collection memberships = organizationService.getMembershipHandler().findMembershipsByUser(username);
      if (creationAssertionValue) {// Related groups has to be
                                   // integrated/added, but when deleting
                                   // user, the group could still exists

        for (Object objectMembership : memberships) {
          assertEquals(creationAssertionValue, Util.hasMembershipFolder(session, (Membership) objectMembership));
        }

        List<Group> groups = new ArrayList<Group>(organizationService.getGroupHandler().findGroupsOfUser(username));
        Collections.sort(groups, OrganizationIntegrationService.GROUP_COMPARATOR);
        for (Group group : groups) {
          assertEquals(creationAssertionValue, Util.hasGroupFolder(session, group.getId()));
        }
      } else {
        assertTrue(memberships == null || memberships.isEmpty());
      }
      if (organizationService instanceof ComponentRequestLifecycle) {
        ((ComponentRequestLifecycle) organizationService).endRequest(container);
      }
    } finally {
      if (session != null) {
        session.logout();
      }
    }
  }

  private void verifyFoldersCreation(boolean creationAssertionValue) throws Exception {
    Session session = null;
    try {
      session = repositoryService.getCurrentRepository().getSystemSession(Util.WORKSPACE);
      if (organizationService instanceof ComponentRequestLifecycle) {
        ((ComponentRequestLifecycle) organizationService).startRequest(container);
      }
      PageList<User> users = organizationService.getUserHandler().getUserPageList(10);
      for (int i = 1; i <= users.getAvailablePage(); i++) {
        List<User> tmpUsers = users.getPage(i);
        for (User user : tmpUsers) {
          assertEquals(creationAssertionValue, Util.hasUserFolder(session, user.getUserName()));
          UserProfile profile = organizationService.getUserProfileHandler().findUserProfileByName(user.getUserName());
          assertEquals(creationAssertionValue, Util.hasProfileFolder(session, profile.getUserName()));
          Collection memberships = organizationService.getMembershipHandler().findMembershipsByUser(user.getUserName());
          for (Object objectMembership : memberships) {
            assertEquals(creationAssertionValue, Util.hasMembershipFolder(session, (Membership) objectMembership));
          }
        }
      }
      List<Group> groups = new ArrayList<Group>(organizationService.getGroupHandler().getAllGroups());
      Collections.sort(groups, OrganizationIntegrationService.GROUP_COMPARATOR);
      for (Group group : groups) {
        assertEquals(creationAssertionValue, Util.hasGroupFolder(session, group.getId()));
      }
      if (organizationService instanceof ComponentRequestLifecycle) {
        ((ComponentRequestLifecycle) organizationService).endRequest(container);
      }
    } finally {
      if (session != null) {
        session.logout();
      }
    }
  }
}
