package org.exoplatform.ks.test.mock;


import java.util.Date;

import junit.framework.TestCase;

import org.exoplatform.ks.test.AssertUtils;
import org.exoplatform.ks.test.mock.SimpleMockOrganizationService;
import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.GroupHandler;
import org.exoplatform.services.organization.Membership;
import org.exoplatform.services.organization.MembershipHandler;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.UserHandler;

public class TestSimpleMockOrganizationService extends TestCase {

	public TestSimpleMockOrganizationService() throws Exception {
		super();
	}
	SimpleMockOrganizationService a = new SimpleMockOrganizationService();
	
	public void setUp() {
		 a = new SimpleMockOrganizationService();
	}
	
	public void testSimpleGroup() {
		
		// test equals
		assertFalse(group("group1").equals(null));
		assertFalse(group("foo").equals(user("foo")));
		assertFalse(group("foo").equals(group("bar")));
		assertEquals(group("foo"),group("foo"));
		assertEquals(group("foo"),
				
		new org.exoplatform.services.organization.Group() {
			public String getDescription() {return null;}
			public String getGroupName() {return null;}
			public String getId() {return "foo";}
			public String getLabel() { return null;}
			public String getParentId() { return null;}
			public void setDescription(String desc) {}
			public void setGroupName(String name) {}
			public void setLabel(String name) {}
		});
		
		
		assertEquals("c", group("/a/b/c").getGroupName());
		assertEquals("/a/b", group("/a/b/c").getParentId());
		assertEquals("/a/b/c", group("/a/b/c").getId());
	}
	
	
	public void testGroupHandler() throws Exception {
		GroupHandler groupHandler = a.getGroupHandler();
		
		AssertUtils.assertEmpty(groupHandler.getAllGroups());
		assertNull(groupHandler.findGroupById("/foo"));
		AssertUtils.assertEmpty(groupHandler.findGroupByMembership("user", "type"));
		AssertUtils.assertEmpty(groupHandler.findGroups(group("/foo")));
		AssertUtils.assertEmpty(groupHandler.findGroupsOfUser("user"));
		
		a.addMemberships("user1", "member:/platform/users", "*:/platform/administrators");
		a.addMemberships("user2", "admin:/foo", "admin:/bar", "member:/baz");
		assertEquals(group("/platform/users"), groupHandler.findGroupById("/platform/users"));
		AssertUtils.assertContains(groupHandler.findGroupByMembership("user2", "admin"), groups("/foo", "/bar"));
		AssertUtils.assertNotContains(groupHandler.findGroupByMembership("user2", "admin"), groups("/baz"));
		AssertUtils.assertContains(groupHandler.findGroups(group("/platform")),  groups("/platform/users", "/platform/administrators"));
		AssertUtils.assertNotContains(groupHandler.findGroups(group("/platform")),  groups("/foo", "/bar", "/baz"));
		
		a.addMemberships("user1", "*:/foo", "admin:/bar", "member:/baz");
		AssertUtils.assertContains(groupHandler.findGroupsOfUser("user1"), groups("/foo", "/bar", "/baz", "/platform/users", "/platform/administrators"));
	}
	
	
	public void testSimpleUser() {
		
		// test equals
		assertFalse(user("foo").equals(null));
		assertFalse(user("foo").equals(user("bar")));
		assertFalse(user("foo").equals(group("foo")));
		assertEquals(user("foo"),user("foo"));
		assertEquals(user("foo"),
				
		new org.exoplatform.services.organization.User() {
			public Date getCreatedDate() {return null;}
			public String getEmail() {return null;}
			public String getFirstName() {return null;}
			public String getFullName() {return null;}
			public Date getLastLoginTime() {return null;}
			public String getLastName() {return null;}
			public String getOrganizationId() {return null;}
			public String getPassword() {return null;}
			public String getUserName() {return "foo";}
			public void setCreatedDate(Date t) {}
			public void setEmail(String s) {}
			public void setFirstName(String s) {}
			public void setFullName(String s) {}
			public void setLastLoginTime(Date t) {}
			public void setLastName(String s) {}
			public void setOrganizationId(String organizationId) {}
			public void setPassword(String s) {}
			public void setUserName(String s) {}
		});
		
		
		assertEquals("foo", user("foo").getUserName());
		assertEquals("foo", group("foo").getId());
	}
	
	
	public void testUserHandler() throws Exception {
		UserHandler userHandler = a.getUserHandler();
		AssertUtils.assertEmpty(userHandler.findUsersByGroup("foo").getAll());
		
		a.addMemberships("user1", "member:/platform/users", "*:/platform/administrators");
		a.addMemberships("user2", "admin:/foo", "admin:/bar", "member:/baz");
		a.addMemberships("user1", "*:/foo", "admin:/bar", "member:/baz");
		AssertUtils.assertContains(userHandler.findUsersByGroup("/foo").getAll(), user("user1"));
		AssertUtils.assertNotContains(userHandler.findUsersByGroup("/platform/administrators").getAll(), user("user2"));
	}
	
	public void testMembershipHandler() throws Exception {
		MembershipHandler membershipHandler = a.getMembershipHandler();
		AssertUtils.assertEmpty(membershipHandler.findMembershipsByUser("foo"));
		AssertUtils.assertEmpty(membershipHandler.findMembershipsByGroup(group("foo")));
		
		a.addMemberships("user1", "member:/platform/users", "*:/platform/administrators");
		a.addMemberships("user2", "admin:/foo", "admin:/bar", "member:/baz");
		a.addMemberships("user1", "*:/foo", "admin:/bar", "member:/baz");
		
		AssertUtils.assertContains(membershipHandler.findMembershipsByUser("user1"), memberships("user1@member:/platform/users", "user1@*:/platform/administrators", "user1@*:/foo", "user1@admin:/bar", "user1@member:/baz"));
		AssertUtils.assertNotContains(membershipHandler.findMembershipsByUser("user2"), memberships("user2@member:/platform/users", "user2@*:/platform/administrators", "user1@*:/foo"));

		AssertUtils.assertContains(membershipHandler.findMembershipsByGroup(group("/foo")), memberships("user1@*:/foo", "user2@admin:/foo"));
		AssertUtils.assertNotContains(membershipHandler.findMembershipsByUser("/bar"), memberships("user1@admin:/bar"));

	}
	
	
	private Membership membership(String id) {
		String[] parts = id.split("@");
		String[] mparts = parts[1].split(":");
		return new SimpleMockOrganizationService.SimpleMembership(parts[0], mparts[1], mparts[0]);
	}

	private Object[] memberships(String... ids) {
		SimpleMockOrganizationService.SimpleMembership[] result = new SimpleMockOrganizationService.SimpleMembership[ids.length];
		int i = 0;

		for (String id : ids) {
			result[i++] = (SimpleMockOrganizationService.SimpleMembership) membership(id);
		}
		return result;
	}
		
	
	
	private User user(String name) {
		return  new SimpleMockOrganizationService.SimpleUser(name);
	}
	
	private Object [] users(String... userNames) {
		
		SimpleMockOrganizationService.SimpleUser [] result = new SimpleMockOrganizationService.SimpleUser [userNames.length]; 
		int i = 0;
		
		for (String userName: userNames) {
			result[i++] = new SimpleMockOrganizationService.SimpleUser(userName);
		}
		return result;
	}	
	
	
	private Group group(String id) {
		return  new SimpleMockOrganizationService.SimpleGroup(id);
	}
	
	private Object [] groups(String... groupNames) {
		
		SimpleMockOrganizationService.SimpleGroup [] result = new SimpleMockOrganizationService.SimpleGroup [groupNames.length]; 
		int i = 0;
		
		for (String groupName: groupNames) {
			result[i++] = new SimpleMockOrganizationService.SimpleGroup(groupName);
		}
		return result;
	}
	
}
