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
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.exoplatform.services.jcr.ext.distribution.DataDistributionManager;
import org.exoplatform.services.jcr.ext.distribution.DataDistributionMode;
import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.Membership;
import org.exoplatform.services.organization.impl.GroupImpl;
import org.exoplatform.services.organization.impl.MembershipImpl;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XppDriver;

public class Util {

    final public static String MEMBERSHIPS_LIST_NODE_NAME = "list";
  final public static String SPECIAL_CHARACTER_REPLACEMENT = "___";
  final public static String MEMBERSHIP_SEPARATOR = "---";
    final public static String ORGANIZATION_INITIALIZATIONS = "integration_data";
    final public static String USERS_FOLDER = "usersData";
    final public static String GROUPS_FOLDER = "groupsData";
    final public static String MEMBERSHIPS_FOLDER = "membershipsData";
    final public static String PROFILES_FOLDER = "profilesData";

  public static String WORKSPACE = "collaboration";
  public static String HOME_PATH = "/";
    public static XStream xstreamList_ = null;
    static {
        xstreamList_ = new XStream(new XppDriver());
        xstreamList_.alias(MEMBERSHIPS_LIST_NODE_NAME, HashSet.class);
    }
  public static void init(Session session) throws Exception {
    Node homePathNode = null;
    try {
      homePathNode = (Node) session.getItem(HOME_PATH);
    } catch (PathNotFoundException e) {
      homePathNode = createFolder(session.getRootNode(), HOME_PATH);
    }
    Node organizationInitializersHomePathNode = null;
    if (!homePathNode.hasNode(ORGANIZATION_INITIALIZATIONS)) {
      organizationInitializersHomePathNode = createFolder(homePathNode, ORGANIZATION_INITIALIZATIONS);
    } else {
      organizationInitializersHomePathNode = homePathNode.getNode(ORGANIZATION_INITIALIZATIONS);
    }
    if (!organizationInitializersHomePathNode.hasNode(USERS_FOLDER)) {
        Node usersNode = createFolder(organizationInitializersHomePathNode, USERS_FOLDER);
        saveListActivation(usersNode, new HashSet<String>(), true);
    }
    if (!organizationInitializersHomePathNode.hasNode(GROUPS_FOLDER)) {
        Node groupsNode = createFolder(organizationInitializersHomePathNode, GROUPS_FOLDER);
        saveListActivation(groupsNode, new HashSet<String>(), true);
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

    public static boolean hasUserFolder(DataDistributionManager distributionManager, Session session, String username)
            throws RepositoryException, Exception {
        try {
            distributionManager.getDataDistributionType(DataDistributionMode.OPTIMIZED).getDataNode(getUsersFolder(session), username);
        } catch (PathNotFoundException exception) {
            return false;
        }
        return true;
  }

    public static boolean hasProfileFolder(DataDistributionManager distributionManager, Session session, String username)
            throws RepositoryException, Exception {
        try {
            distributionManager.getDataDistributionType(DataDistributionMode.OPTIMIZED).getDataNode(getProfilesFolder(session),
                    username);
        } catch (PathNotFoundException exception) {
            return false;
  }
        return true;
  }

    public static boolean hasMembershipFolder(DataDistributionManager distributionManager, Session session, Membership membership)
            throws RepositoryException, Exception {
        try {
            String dataId = getMembershipFolderName(membership);
            distributionManager.getDataDistributionType(DataDistributionMode.OPTIMIZED).getDataNode(getMembershipsFolder(session),
                    dataId);
        } catch (PathNotFoundException exception) {
            return false;
  }
        return true;
  }

    public static boolean hasGroupFolder(DataDistributionManager distributionManager, Session session, String groupId)
            throws RepositoryException, Exception {
        try {
            distributionManager.getDataDistributionType(DataDistributionMode.OPTIMIZED).getDataNode(getGroupsFolder(session),
                    getGroupFolderName(groupId));
        } catch (PathNotFoundException exception) {
            return false;
  }
        return true;
  }

    public static void createUserFolder(DataDistributionManager distributionManager, Session session, String username)
            throws Exception {
        Node usersNode = getUsersFolder(session);
        Set<String> users = getListActivation(usersNode);
        users.add(username);
        saveListActivation(usersNode, users, false);

        Node userNode = distributionManager.getDataDistributionType(DataDistributionMode.OPTIMIZED).getOrCreateDataNode(usersNode,
                username);
        saveListActivation(userNode, new HashSet<String>(), true);
    session.save();
  }

    public static void createProfileFolder(DataDistributionManager distributionManager, Session session, String username)
            throws Exception {
        distributionManager.getDataDistributionType(DataDistributionMode.OPTIMIZED).getOrCreateDataNode(getProfilesFolder(session),
                username);
  }

    public static void createMembershipFolder(DataDistributionManager distributionManager, Session session, Membership membership)
            throws Exception {
        String dataId = getMembershipFolderName(membership);
        distributionManager.getDataDistributionType(DataDistributionMode.OPTIMIZED).getOrCreateDataNode(
                getMembershipsFolder(session), dataId);

        String membershipId = computeMembershipId(membership);

        Node groupNode = getGroupNode(distributionManager, session, membership.getGroupId());
        Set<String> groupMembershipList = getListActivation(groupNode);
        groupMembershipList.add(membershipId);
        saveListActivation(groupNode, groupMembershipList, false);

        Node userNode = getUserNode(distributionManager, session, membership.getUserName());
        Set<String> userMembershipList = getListActivation(userNode);
        userMembershipList.add(membershipId);
        saveListActivation(userNode, userMembershipList, false);
    session.save();
  }

    public static void createGroupFolder(DataDistributionManager distributionManager, Session session, String groupId)
            throws Exception {
        Node groupsNode = getGroupsFolder(session);
        Set<String> groups = getListActivation(groupsNode);
        groups.add(groupId);
        saveListActivation(groupsNode, groups, false);

        Node groupNode = distributionManager.getDataDistributionType(DataDistributionMode.OPTIMIZED).getOrCreateDataNode(groupsNode,
                getGroupFolderName(groupId));
        saveListActivation(groupNode, new HashSet<String>(), true);
    session.save();
  }

    public static Node getUserNode(DataDistributionManager distributionManager, Session session, String username) throws Exception {
        return distributionManager.getDataDistributionType(DataDistributionMode.OPTIMIZED).getDataNode(getUsersFolder(session),
                username);
  }

    public static Node getGroupNode(DataDistributionManager distributionManager, Session session, String groupId) throws Exception {
        return distributionManager.getDataDistributionType(DataDistributionMode.OPTIMIZED).getDataNode(getGroupsFolder(session),
                getGroupFolderName(groupId));
    }
    public static void deleteUserFolder(DataDistributionManager distributionManager, Session session, String username)
            throws Exception {
        Node usersNode = getUsersFolder(session);
        Set<String> users = getListActivation(usersNode);
        users.remove(username);
        saveListActivation(usersNode, users, false);

        distributionManager.getDataDistributionType(DataDistributionMode.OPTIMIZED).removeDataNode(usersNode, username);
  }

    public static void deleteProfileFolder(DataDistributionManager distributionManager, Session session, String username)
            throws Exception {
        distributionManager.getDataDistributionType(DataDistributionMode.OPTIMIZED).removeDataNode(getProfilesFolder(session),
                username);
  }

    public static void deleteGroupFolder(DataDistributionManager distributionManager, Session session, String groupId)
            throws Exception {
        Node groupsNode = getGroupsFolder(session);
        Set<String> groups = getListActivation(groupsNode);
        groups.remove(groupId);
        saveListActivation(groupsNode, groups, false);

        distributionManager.getDataDistributionType(DataDistributionMode.OPTIMIZED).removeDataNode(groupsNode,
                getGroupFolderName(groupId));
  }

    public static void deleteMembershipFolder(DataDistributionManager distributionManager, Session session, Membership membership)
            throws Exception {
        String dataId = getMembershipFolderName(membership);
        distributionManager.getDataDistributionType(DataDistributionMode.OPTIMIZED).removeDataNode(getMembershipsFolder(session),
                dataId);

        String membershipId = computeMembershipId(membership);

        if (hasGroupFolder(distributionManager, session, membership.getGroupId())) {
            Node groupNode = getGroupNode(distributionManager, session, membership.getGroupId());
            Set<String> groupMembershipList = getListActivation(groupNode);
            groupMembershipList.remove(membershipId);
            saveListActivation(groupNode, groupMembershipList, false);
        }

        if (hasUserFolder(distributionManager, session, membership.getUserName())) {
            Node userNode = getUserNode(distributionManager, session, membership.getUserName());
            Set<String> userMembershipList = getListActivation(userNode);
            userMembershipList.remove(membershipId);
            saveListActivation(userNode, userMembershipList, false);
    }
        session.save();
  }

    public static Set<String> getActivatedUsers(Session session) throws Exception {
        return getListActivation(getUsersFolder(session));
    }

    public static Set<String> getActivatedGroups(Session session) throws Exception {
        return getListActivation(getGroupsFolder(session));
      }
    public static List<Group> getActivatedChildrenGroup(Session session, String parentGroupId) throws Exception {
        List<Group> activatedChildrenGroups = new ArrayList<Group>();
        Set<String> activatedGroups = getActivatedGroups(session);
        for (String groupId : activatedGroups) {
            if (groupId.startsWith(parentGroupId) && !groupId.equals(parentGroupId)) {
      GroupImpl group = new GroupImpl();
      group.setId(groupId);
      group.setGroupName(groupId);

                activatedChildrenGroups.add(group);
            }
    }
        return activatedChildrenGroups;
  }

    public static List<Membership> getActivatedMembershipsRelatedToGroup(DataDistributionManager distributionManager,
                                                                         Session session, String groupId) throws Exception {
        Set<String> memberships = getListActivation(getGroupNode(distributionManager, session, groupId));
    List<Membership> activatedMemberships = new ArrayList<Membership>();
        for (String membershipId : memberships) {
            activatedMemberships.add(computeMembershipFromId(membershipId));
        }
        return activatedMemberships;
    }

    public static List<Membership> getActivatedMembershipsRelatedToUser(DataDistributionManager distributionManager,
                                                                        Session session, String username) throws Exception {
        Set<String> memberships = getListActivation(getUserNode(distributionManager, session, username));
        List<Membership> activatedMemberships = new ArrayList<Membership>();
        for (String membershipId : memberships) {
            activatedMemberships.add(computeMembershipFromId(membershipId));
        }
        return activatedMemberships;
    }

    private static Membership computeMembershipFromId(String membershipId) {
        String[] membershipElements = membershipId.split(":");

        String membershipType = membershipElements[0];
        String username = membershipElements[1];
        String groupId = membershipElements[2];

      MembershipImpl membership = new MembershipImpl();
      membership.setGroupId(groupId);
      membership.setMembershipType(membershipType);
      membership.setUserName(username);
        membership.setId(membershipId);
        return membership;
  }

    public static String computeMembershipId(Membership membership) {
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
    private static Node createFolder(Node parentNode, String name) throws Exception {
        Node orgIntServNode = parentNode.addNode(name, "nt:unstructured");
        parentNode.save();
        if (orgIntServNode.canAddMixin("exo:hiddenable")) {
            orgIntServNode.addMixin("exo:hiddenable");
        }
        orgIntServNode.save();
        return orgIntServNode;
    }

    @SuppressWarnings("unchecked")
    private static Set<String> getListActivation(Node parentNode) throws Exception {
        String xml = parentNode.getProperty(MEMBERSHIPS_LIST_NODE_NAME + "/jcr:data").getString();
        return (Set<String>) xstreamList_.fromXML(xml);
    }

    private static synchronized void saveListActivation(Node parentNode, Set<String> list, boolean isNew) throws Exception {
        String content = xstreamList_.toXML(list);
        Node fileNode = null;
        if (isNew) {
            fileNode = parentNode.addNode(MEMBERSHIPS_LIST_NODE_NAME, "nt:resource");
        } else {
            fileNode = parentNode.getNode(MEMBERSHIPS_LIST_NODE_NAME);
        }
        fileNode.setProperty("jcr:data", content);
        fileNode.setProperty("jcr:mimeType", "text/xml");
        fileNode.setProperty("jcr:lastModified", Calendar.getInstance());
        parentNode.save();
    }

    private static String getMembershipFolderName(Membership membership) {
        String dataId = membership.getId();
        if (dataId == null || dataId.isEmpty()) {
            dataId = membership.getUserName() + membership.getGroupId() + membership.getMembershipType();
        }
        return "" + dataId.hashCode();
    }

    private static String getGroupFolderName(String groupId) {
        return "" + groupId.hashCode();
    }
}