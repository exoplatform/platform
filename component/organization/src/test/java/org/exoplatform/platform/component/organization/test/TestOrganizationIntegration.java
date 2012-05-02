package org.exoplatform.platform.component.organization.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.jcr.Session;

import org.exoplatform.commons.utils.ListAccess;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.ComponentRequestLifecycle;
import org.exoplatform.platform.organization.integration.EventType;
import org.exoplatform.platform.organization.integration.NewGroupListener;
import org.exoplatform.platform.organization.integration.NewMembershipListener;
import org.exoplatform.platform.organization.integration.NewProfileListener;
import org.exoplatform.platform.organization.integration.NewUserListener;
import org.exoplatform.platform.organization.integration.OrganizationIntegrationService;
import org.exoplatform.platform.organization.integration.Util;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.ext.distribution.DataDistributionManager;
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
    DataDistributionManager dataDistributionManager = null;

    @Override
    protected void setUp() throws Exception {
        container = PortalContainer.getInstance();
        repositoryService = (RepositoryService) container.getComponentInstanceOfType(RepositoryService.class);
        organizationService = (OrganizationService) container.getComponentInstanceOfType(OrganizationService.class);
        dataDistributionManager = (DataDistributionManager) container.getComponentInstanceOfType(DataDistributionManager.class);
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

            verifyMembershipFoldersCreation("demo", "/platform/guests", "member", false);
            organizationIntegrationService.syncMembership("demo", "/platform/guests",
                    EventType.ADDED.toString());
            verifyMembershipFoldersCreation("demo", "/platform/guests", "member", true);

            organizationIntegrationService.syncGroup("/organization/management/executive-board", EventType.ADDED.toString());
            organizationIntegrationService.syncGroup("/organization/management/executive-board", EventType.DELETED.toString());

            assertTrue(Util.hasGroupFolder(dataDistributionManager, session, "/organization"));
            assertTrue(Util.hasGroupFolder(dataDistributionManager, session, "/organization/management"));
            assertTrue(Util.hasGroupFolder(dataDistributionManager, session, "/organization/management/executive-board"));

            organizationIntegrationService.syncAllGroups(EventType.DELETED.toString());

            assertTrue(Util.hasGroupFolder(dataDistributionManager, session, "/organization"));
            assertTrue(Util.hasGroupFolder(dataDistributionManager, session, "/organization/management"));
            assertTrue(Util.hasGroupFolder(dataDistributionManager, session, "/organization/management/executive-board"));

            verifyUserFoldersCreation("root", false);

            organizationIntegrationService.syncUser("root", EventType.ADDED.toString());
            List<Membership> rootUserMemberships = Util.getActivatedMembershipsRelatedToUser(dataDistributionManager, session, "root");
            assertNotNull(rootUserMemberships);
            assertEquals(rootUserMemberships.size(), 3);

            verifyUserFoldersCreation("root", true);

            organizationIntegrationService.syncUser("root", EventType.DELETED.toString());

            verifyUserFoldersCreation("root", true);

            deleteMembership("member:root:/organization/management/executive-board");

            verifyMembershipFoldersCreation("root", "/organization/management/executive-board", "member", true);
            organizationIntegrationService.syncMembership("root", "/organization/management/executive-board",
                    EventType.DELETED.toString());
            verifyMembershipFoldersCreation("root", "/organization/management/executive-board", "member", false);
            rootUserMemberships = Util.getActivatedMembershipsRelatedToUser(dataDistributionManager, session, "root");
            assertNotNull(rootUserMemberships);
            assertEquals(rootUserMemberships.size(), 2);

            deleteUser("root");
            organizationIntegrationService.syncUser("root", EventType.DELETED.toString());

            verifyUserFoldersCreation("root", false);

            deleteGroup("/organization");
            organizationIntegrationService.syncGroup("/organization", EventType.DELETED.toString());

            assertFalse(Util.hasGroupFolder(dataDistributionManager, session, "/organization"));
            assertFalse(Util.hasGroupFolder(dataDistributionManager, session, "/organization/management"));
            assertFalse(Util.hasGroupFolder(dataDistributionManager, session, "/organization/management/executive-board"));

            MembershipImpl membership = new MembershipImpl();
            {
                membership.setMembershipType("manager");
                membership.setUserName("john");
                membership.setGroupId("/organization/management/executive-board");
                membership.setId(Util.computeMembershipId(membership));

                assertFalse(Util.hasMembershipFolder(dataDistributionManager, session, membership));
            }

            deleteUser("mary");
            deleteGroup("/platform/guests");

            organizationIntegrationService.syncAll();
            verifyFoldersCreation(true);

            deleteAllGroups();
            deleteAllUsers();

            organizationIntegrationService.syncAll();

            assertEquals(Util.getActivatedGroups(session).size(), 0);
            assertEquals(Util.getActivatedUsers(session).size(), 0);
        } finally {
            if (session != null) {
                session.logout();
            }
        }
    }

    private void deleteAllUsers() throws Exception {
        if (organizationService instanceof ComponentRequestLifecycle) {
            ((ComponentRequestLifecycle) organizationService).startRequest(container);
        }
        ListAccess<User> usersListAccess = organizationService.getUserHandler().findAllUsers();
        int i = 0;
        while (i <= usersListAccess.getSize()) {
            int length = i + 10 <= usersListAccess.getSize() ? 10 : usersListAccess.getSize() - i;
            User[] users = usersListAccess.load(i, length);
            for (User user : users) {
                organizationService.getUserHandler().removeUser(user.getUserName(), true);
            }
            i += 10;
        }
        if (organizationService instanceof ComponentRequestLifecycle) {
            ((ComponentRequestLifecycle) organizationService).endRequest(container);
        }
    }

    private void deleteAllGroups() throws Exception {
        if (organizationService instanceof ComponentRequestLifecycle) {
            ((ComponentRequestLifecycle) organizationService).startRequest(container);
        }
        @SuppressWarnings("unchecked")
        List<Group> groups = new ArrayList<Group>(organizationService.getGroupHandler().getAllGroups());
        Collections.sort(groups, OrganizationIntegrationService.GROUP_COMPARATOR);
        Collections.reverse(groups);

        for (Group group : groups) {
            organizationService.getGroupHandler().removeGroup(group, true);
        }
        if (organizationService instanceof ComponentRequestLifecycle) {
            ((ComponentRequestLifecycle) organizationService).endRequest(container);
        }
    }

    private void deleteMembership(String membershipId) throws Exception {
        if (organizationService instanceof ComponentRequestLifecycle) {
            ((ComponentRequestLifecycle) organizationService).startRequest(container);
        }
        organizationService.getMembershipHandler().removeMembership(membershipId, true);
        if (organizationService instanceof ComponentRequestLifecycle) {
            ((ComponentRequestLifecycle) organizationService).endRequest(container);
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
            assertEquals(creationAssertionValue, Util.hasUserFolder(dataDistributionManager, session, username));
            assertEquals(creationAssertionValue, Util.hasProfileFolder(dataDistributionManager, session, username));

            Collection<?> memberships = organizationService.getMembershipHandler().findMembershipsByUser(username);
            if (creationAssertionValue) {// Related groups has to be
                // integrated/added, but when deleting
                // user, the group could still exists

                for (Object objectMembership : memberships) {
                    assertEquals(creationAssertionValue, Util.hasMembershipFolder(dataDistributionManager, session, (Membership) objectMembership));
                }

                @SuppressWarnings("unchecked")
                List<Group> groups = new ArrayList<Group>(organizationService.getGroupHandler().findGroupsOfUser(username));
                Collections.sort(groups, OrganizationIntegrationService.GROUP_COMPARATOR);
                for (Group group : groups) {
                    assertEquals(creationAssertionValue, Util.hasGroupFolder(dataDistributionManager, session, group.getId()));
                }
            } else {
                for (Object objectMembership : memberships) {
                    assertEquals(creationAssertionValue, Util.hasMembershipFolder(dataDistributionManager, session, (Membership) objectMembership));
                }
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
            ListAccess<User> usersListAccess = organizationService.getUserHandler().findAllUsers();
            int i = 0;
            while (i <= usersListAccess.getSize()) {
                int length = i + 10 <= usersListAccess.getSize() ? 10 : usersListAccess.getSize() - i;
                User[] users = usersListAccess.load(i, length);
                for (User user : users) {
                    assertEquals(creationAssertionValue, Util.hasUserFolder(dataDistributionManager, session, user.getUserName()));
                    UserProfile profile = organizationService.getUserProfileHandler().findUserProfileByName(user.getUserName());
                    assertEquals(creationAssertionValue, Util.hasProfileFolder(dataDistributionManager, session, profile.getUserName()));
                    Collection<?> memberships = organizationService.getMembershipHandler().findMembershipsByUser(user.getUserName());
                    for (Object objectMembership : memberships) {
                        assertEquals(creationAssertionValue, Util.hasMembershipFolder(dataDistributionManager, session, (Membership) objectMembership));
                    }
                }
                i += 10;
            }
            @SuppressWarnings("unchecked")
            List<Group> groups = new ArrayList<Group>(organizationService.getGroupHandler().getAllGroups());
            Collections.sort(groups, OrganizationIntegrationService.GROUP_COMPARATOR);
            for (Group group : groups) {
                assertEquals(creationAssertionValue, Util.hasGroupFolder(dataDistributionManager, session, group.getId()));
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

    private void verifyMembershipFoldersCreation(String username, String groupId, String membershipType, boolean assertionCondition)
            throws Exception {
        Session session = null;
        try {
            session = repositoryService.getCurrentRepository().getSystemSession(Util.WORKSPACE);
            MembershipImpl membership = new MembershipImpl();
            {
                membership.setMembershipType(membershipType);
                membership.setUserName(username);
                membership.setGroupId(groupId);
                membership.setId(Util.computeMembershipId(membership));
            }
            assertEquals(assertionCondition, Util.hasMembershipFolder(dataDistributionManager, session, membership));
            session.save();
        } finally {
            if (session != null) {
                session.logout();
            }
        }
    }
}
