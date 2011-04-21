package org.exoplatform.platform.component.organization.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.exoplatform.commons.utils.PageList;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.ComponentRequestLifecycle;
import org.exoplatform.platform.component.organization.NewGroupListener;
import org.exoplatform.platform.component.organization.NewMembershipListener;
import org.exoplatform.platform.component.organization.NewProfileListener;
import org.exoplatform.platform.component.organization.NewUserListener;
import org.exoplatform.platform.component.organization.OrganizationIntegrationREST;
import org.exoplatform.platform.component.organization.OrganizationIntegrationService;
import org.exoplatform.platform.component.organization.Util;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.Membership;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.UserProfile;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.MembershipEntry;
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

    OrganizationIntegrationREST organizationIntegrationREST = container.createComponent(OrganizationIntegrationREST.class);

    Response response = organizationIntegrationREST.invokeUserListeners("demo");
    assertEquals(Status.FORBIDDEN.getStatusCode(), response.getStatus());

    response = organizationIntegrationREST.invokeGroupListeners("users");
    assertEquals(Status.FORBIDDEN.getStatusCode(), response.getStatus());

    response = organizationIntegrationREST.invokeAllListeners();
    assertEquals(Status.FORBIDDEN.getStatusCode(), response.getStatus());

    List<MembershipEntry> memberships = new ArrayList<MembershipEntry>();
    memberships.add(new MembershipEntry("/users", "*"));

    List<String> roles = new ArrayList<String>();
    roles.add("users");

    Identity identity = new Identity("demo", memberships, roles);
    ConversationState.setCurrent(new ConversationState(identity));

    response = organizationIntegrationREST.invokeGroupListeners("users");
    assertEquals(Status.OK.getStatusCode(), response.getStatus());

    response = organizationIntegrationREST.invokeUserListeners("demo");
    assertEquals(Status.OK.getStatusCode(), response.getStatus());

    response = organizationIntegrationREST.invokeAllListeners();
    assertEquals(Status.OK.getStatusCode(), response.getStatus());

    verifyFoldersCreation(true);
  }

  private void verifyFoldersCreation(boolean creationAssertionValue) throws Exception {
    if (organizationService instanceof ComponentRequestLifecycle) {
      ((ComponentRequestLifecycle) organizationService).startRequest(container);
    }
    PageList<User> users = organizationService.getUserHandler().getUserPageList(10);
    for (int i = 1; i <= users.getAvailablePage(); i++) {
      List<User> tmpUsers = users.getPage(i);
      for (User user : tmpUsers) {
        assertEquals(creationAssertionValue, Util.hasUserFolder(repositoryService, user));
        UserProfile profile = organizationService.getUserProfileHandler().findUserProfileByName(user.getUserName());
        assertEquals(creationAssertionValue, Util.hasProfileFolder(repositoryService, profile));
        Collection memberships = organizationService.getMembershipHandler().findMembershipsByUser(user.getUserName());
        for (Object objectMembership : memberships) {
          assertEquals(creationAssertionValue, Util.hasMembershipFolder(repositoryService, (Membership) objectMembership));
        }
      }
    }
    List<Group> groups = new ArrayList<Group>(organizationService.getGroupHandler().getAllGroups());
    Collections.sort(groups, OrganizationIntegrationService.GROUP_COMPARATOR);
    for (Group group : groups) {
      assertEquals(creationAssertionValue, Util.hasGroupFolder(repositoryService, group));
    }
    if (organizationService instanceof ComponentRequestLifecycle) {
      ((ComponentRequestLifecycle) organizationService).endRequest(container);
    }
  }
}
