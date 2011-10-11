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
package org.exoplatform.platform.organization.integration;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.Membership;
import org.exoplatform.services.organization.impl.GroupImpl;
import org.exoplatform.services.organization.impl.MembershipImpl;

public class Util {

  final public static String SPECIAL_CHARACTER_REPLACEMENT = "___";
  final public static String MEMBERSHIP_SEPARATOR = "---";
  final public static String ORGANIZATION_INITIALIZATIONS = "OrganizationIntegrationService";
  final public static String USERS_FOLDER = "users";
  final public static String GROUPS_FOLDER = "groups";
  final public static String MEMBERSHIPS_FOLDER = "memberships";
  final public static String PROFILES_FOLDER = "profiles";

  public static String WORKSPACE = "collaboration";
  public static String HOME_PATH = "/";

  public static void init(Session session) throws Exception {
    Node homePathNode = null;
    try {
      homePathNode = (Node) session.getItem(HOME_PATH);
    } catch (Exception e) {}
    if (homePathNode == null) {
      homePathNode = createFolder(session.getRootNode(), HOME_PATH);
    }
    Node organizationInitializersHomePathNode = null;
    if (!homePathNode.hasNode(ORGANIZATION_INITIALIZATIONS)) {
      organizationInitializersHomePathNode = createFolder(homePathNode, ORGANIZATION_INITIALIZATIONS);
    } else {
      organizationInitializersHomePathNode = homePathNode.getNode(ORGANIZATION_INITIALIZATIONS);
    }
    if (!organizationInitializersHomePathNode.hasNode(USERS_FOLDER)) {
      createFolder(organizationInitializersHomePathNode, USERS_FOLDER);
    }
    if (!organizationInitializersHomePathNode.hasNode(GROUPS_FOLDER)) {
      createFolder(organizationInitializersHomePathNode, GROUPS_FOLDER);
    }
    if (!organizationInitializersHomePathNode.hasNode(MEMBERSHIPS_FOLDER)) {
      createFolder(organizationInitializersHomePathNode, MEMBERSHIPS_FOLDER);
    }
    if (!organizationInitializersHomePathNode.hasNode(PROFILES_FOLDER)) {
      createFolder(organizationInitializersHomePathNode, PROFILES_FOLDER);
    }
    session.save();
  }

  public static Node getUsersFolder(Session session) throws Exception {
    Node organizationInitializersHomePathNode = null;
    try {
      Node homePathNode = (Node) session.getItem(HOME_PATH);
      organizationInitializersHomePathNode = homePathNode.getNode(ORGANIZATION_INITIALIZATIONS);
    } catch (Exception e) {
      init(session);
      Node homePathNode = (Node) session.getItem(HOME_PATH);
      organizationInitializersHomePathNode = homePathNode.getNode(ORGANIZATION_INITIALIZATIONS);
    }
    return organizationInitializersHomePathNode.getNode(USERS_FOLDER);
  }

  public static Node getGroupsFolder(Session session) throws Exception {
    Node organizationInitializersHomePathNode = null;
    try {
      Node homePathNode = (Node) session.getItem(HOME_PATH);
      organizationInitializersHomePathNode = homePathNode.getNode(ORGANIZATION_INITIALIZATIONS);
    } catch (Exception e) {
      init(session);
      Node homePathNode = (Node) session.getItem(HOME_PATH);
      organizationInitializersHomePathNode = homePathNode.getNode(ORGANIZATION_INITIALIZATIONS);
    }
    return organizationInitializersHomePathNode.getNode(GROUPS_FOLDER);
  }

  public static Node getMembershipsFolder(Session session) throws Exception {
    Node organizationInitializersHomePathNode = null;
    try {
      Node homePathNode = (Node) session.getItem(HOME_PATH);
      organizationInitializersHomePathNode = homePathNode.getNode(ORGANIZATION_INITIALIZATIONS);
    } catch (Exception e) {
      init(session);
      Node homePathNode = (Node) session.getItem(HOME_PATH);
      organizationInitializersHomePathNode = homePathNode.getNode(ORGANIZATION_INITIALIZATIONS);
    }
    return organizationInitializersHomePathNode.getNode(MEMBERSHIPS_FOLDER);
  }

  public static Node getProfilesFolder(Session session) throws Exception {
    Node organizationInitializersHomePathNode = null;
    try {
      Node homePathNode = (Node) session.getItem(HOME_PATH);
      organizationInitializersHomePathNode = homePathNode.getNode(ORGANIZATION_INITIALIZATIONS);
    } catch (Exception e) {
      init(session);
      Node homePathNode = (Node) session.getItem(HOME_PATH);
      organizationInitializersHomePathNode = homePathNode.getNode(ORGANIZATION_INITIALIZATIONS);
    }
    return organizationInitializersHomePathNode.getNode(PROFILES_FOLDER);
  }

  public static boolean hasUserFolder(Session session, String username) throws Exception {
    return getUsersFolder(session).hasNode(username);
  }

  public static boolean hasProfileFolder(Session session, String username) throws Exception {
    return getProfilesFolder(session).hasNode(username);
  }

  public static boolean hasMembershipFolder(Session session, Membership membership) throws Exception {
    boolean hasNode = Util.getMembershipsFolder(session).hasNode(
        membership.getGroupId().replace("/", Util.SPECIAL_CHARACTER_REPLACEMENT) + Util.MEMBERSHIP_SEPARATOR
            + membership.getMembershipType().replace("*", Util.SPECIAL_CHARACTER_REPLACEMENT)
            + Util.MEMBERSHIP_SEPARATOR + membership.getUserName());
    return hasNode;
  }

  public static boolean hasGroupFolder(Session session, String groupId) throws Exception {
    return getGroupsFolder(session).hasNode(groupId.replace("/", SPECIAL_CHARACTER_REPLACEMENT).trim());
  }

  public static void createUserFolder(Session session, String username) throws Exception {
    createFolder(getUsersFolder(session), username);
    session.save();
  }

  public static void createProfileFolder(Session session, String username) throws Exception {
    createFolder(getProfilesFolder(session), username);
    session.save();
  }

  public static void createMembershipFolder(Session session, Membership membership) throws Exception {
    createFolder(getMembershipsFolder(session), membership.getGroupId().replace("/", SPECIAL_CHARACTER_REPLACEMENT)
        + MEMBERSHIP_SEPARATOR + membership.getMembershipType().replace("*", SPECIAL_CHARACTER_REPLACEMENT)
        + MEMBERSHIP_SEPARATOR + membership.getUserName());
    createFolder(
        getGroupNode(session, membership.getGroupId()),
        membership.getMembershipType().replace("*", SPECIAL_CHARACTER_REPLACEMENT) + MEMBERSHIP_SEPARATOR
            + membership.getUserName());
    createFolder(getUserNode(session, membership.getUserName()),
        membership.getMembershipType().replace("*", SPECIAL_CHARACTER_REPLACEMENT) + MEMBERSHIP_SEPARATOR
            + membership.getGroupId().replace("/", SPECIAL_CHARACTER_REPLACEMENT));
    session.save();
  }

  public static Node getUserNode(Session session, String username) throws PathNotFoundException, RepositoryException, Exception {
    return getUsersFolder(session).getNode(username);
  }

  public static void createGroupFolder(Session session, String groupId) throws Exception {
    createFolder(getGroupsFolder(session), groupId.replace("/", SPECIAL_CHARACTER_REPLACEMENT).trim());
    session.save();
  }

  public static void deleteUserFolder(Session session, String username) throws Exception {
    getUsersFolder(session).getNode(username).remove();
    session.save();
  }

  public static void deleteProfileFolder(Session session, String username) throws Exception {
    getProfilesFolder(session).getNode(username).remove();
    session.save();
  }

  public static void deleteMembershipFolder(Session session, Membership membership) throws Exception {
    getMembershipsFolder(session).getNode(
        membership.getGroupId().replace("/", SPECIAL_CHARACTER_REPLACEMENT) + MEMBERSHIP_SEPARATOR
            + membership.getMembershipType().replace("*", SPECIAL_CHARACTER_REPLACEMENT) + MEMBERSHIP_SEPARATOR
            + membership.getUserName()).remove();

    String membershipGroupFolderName = membership.getMembershipType().replace("*", Util.SPECIAL_CHARACTER_REPLACEMENT)
        + Util.MEMBERSHIP_SEPARATOR + membership.getUserName();
    if (hasGroupFolder(session, membership.getGroupId())
        && getGroupNode(session, membership.getGroupId()).hasNode(membershipGroupFolderName)) {
      getGroupNode(session, membership.getGroupId()).getNode(membershipGroupFolderName).remove();
    }

    String membershipUserFolderName = membership.getMembershipType().replace("*", Util.SPECIAL_CHARACTER_REPLACEMENT)
        + Util.MEMBERSHIP_SEPARATOR + membership.getGroupId().replace("/", Util.SPECIAL_CHARACTER_REPLACEMENT);
    if (hasUserFolder(session, membership.getUserName())
        && getUserNode(session, membership.getUserName()).hasNode(membershipUserFolderName)) {

      getUserNode(session, membership.getUserName()).getNode(membershipUserFolderName).remove();
    }
    session.save();
  }

  public static void deleteGroupFolder(Session session, String groupId) throws Exception {
    getGroupNode(session, groupId).remove();
    session.save();
  }

  public static Node getGroupNode(Session session, String groupId) throws PathNotFoundException, RepositoryException, Exception {
    return getGroupsFolder(session).getNode(groupId.replace("/", SPECIAL_CHARACTER_REPLACEMENT));
  }

  private static Node createFolder(Node parentNode, String name) throws Exception {
    parentNode.addNode(name, "nt:folder");
    parentNode.getSession().save();
    return parentNode.getNode(name);
  }

  public static List<String> getActivatedUsers(Session session) throws Exception {
    List<String> activatedUsernames = new ArrayList<String>();
    NodeIterator usersFolderIterator = getUsersFolder(session).getNodes();
    while (usersFolderIterator.hasNext()) {
      Node userFolder = usersFolderIterator.nextNode();
      activatedUsernames.add(userFolder.getName());
    }
    return activatedUsernames;
  }

  public static List<String> getActivatedGroups(Session session) throws Exception {
    List<String> activatedGroups = new ArrayList<String>();
    NodeIterator groupsFolderIterator = getGroupsFolder(session).getNodes();
    while (groupsFolderIterator.hasNext()) {
      Node groupFolder = groupsFolderIterator.nextNode();
      String groupId = groupFolder.getName().replace(SPECIAL_CHARACTER_REPLACEMENT, "/");
      activatedGroups.add(groupId);
    }
    return activatedGroups;
  }

  public static List<Membership> getActivatedMembershipsRelatedToGroup(Session session, String groupId) throws Exception {
    List<Membership> activatedMemberships = new ArrayList<Membership>();
    NodeIterator membershipNodesIterator = getGroupNode(session, groupId).getNodes();
    while (membershipNodesIterator.hasNext()) {
      Node membershipNode = membershipNodesIterator.nextNode();
      String membershipId = membershipNode.getName();
      String[] membershipElements = membershipId.split(MEMBERSHIP_SEPARATOR);

      String membershipType = membershipElements[0].replace(SPECIAL_CHARACTER_REPLACEMENT, "*");
      String username = membershipElements[1];

      MembershipImpl membership = new MembershipImpl();
      membership.setGroupId(groupId);
      membership.setMembershipType(membershipType);
      membership.setUserName(username);
      membership.setId(computeId(membership));

      activatedMemberships.add(membership);
    }
    return activatedMemberships;
  }

  public static List<Group> getActivatedChildrenGroup(Session session, String parentGroupId) throws Exception {
    List<Group> activatedGroups = new ArrayList<Group>();
    NodeIterator groupsNodesIterator = getGroupsFolder(session).getNodes();
    while (groupsNodesIterator.hasNext()) {
      Node groupNode = groupsNodesIterator.nextNode();

      String groupId = groupNode.getName();
      groupId = groupId.replace(SPECIAL_CHARACTER_REPLACEMENT, "/");
      if (!groupId.contains(parentGroupId) || groupId.equals(parentGroupId)) {
        continue;
      }

      GroupImpl group = new GroupImpl();
      group.setId(groupId);
      group.setGroupName(groupId);

      activatedGroups.add(group);
    }
    return activatedGroups;
  }

  public static List<Membership> getActivatedMembershipsRelatedToUser(Session session, String username) throws Exception {
    List<Membership> activatedMemberships = new ArrayList<Membership>();
    NodeIterator membershipNodesIterator = getUserNode(session, username).getNodes();
    while (membershipNodesIterator.hasNext()) {
      Node membershipNode = membershipNodesIterator.nextNode();
      String membershipId = membershipNode.getName();
      String[] membershipElements = membershipId.split(MEMBERSHIP_SEPARATOR);

      String membershipType = membershipElements[0].replace(SPECIAL_CHARACTER_REPLACEMENT, "*");
      String groupId = membershipElements[1].replace(SPECIAL_CHARACTER_REPLACEMENT, "/");

      MembershipImpl membership = new MembershipImpl();
      membership.setGroupId(groupId);
      membership.setMembershipType(membershipType);
      membership.setUserName(username);
      membership.setId(computeId(membership));

      activatedMemberships.add(membership);
    }
    return activatedMemberships;
  }

  public static String computeId(Membership membership) {
    StringBuffer id = new StringBuffer();

    if (membership.getMembershipType() != null) {
      id.append(membership.getMembershipType());
    }
    id.append(":");
    if (membership.getUserName() != null) {
      id.append(membership.getUserName());
    }
    id.append(":");
    if (membership.getGroupId() != null) {
      id.append(membership.getGroupId());
    }
    return id.toString();
  }

}