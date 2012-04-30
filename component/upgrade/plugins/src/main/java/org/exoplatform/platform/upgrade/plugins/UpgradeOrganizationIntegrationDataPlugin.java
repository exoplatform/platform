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

package org.exoplatform.platform.upgrade.plugins;

import org.exoplatform.commons.upgrade.UpgradeProductPlugin;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.platform.organization.integration.OrganizationIntegrationService;
import org.exoplatform.platform.organization.integration.Util;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.ext.distribution.DataDistributionManager;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.idm.MembershipImpl;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: kmenzli
 * Date: 30/04/12
 * Time: 12:45
 * To change this template use File | Settings | File Templates.
 */
public class UpgradeOrganizationIntegrationDataPlugin extends UpgradeProductPlugin {

    private static final Log LOG = ExoLogger.getLogger(UpgradeOrganizationIntegrationDataPlugin.class);
    private static final String SPECIAL_CHARACTER_REPLACEMENT = "___";
    private static final String MEMBERSHIP_SEPARATOR = "---";
    private static final String ORGANIZATION_INITIALIZATIONS = "OrganizationIntegrationService";
    private static final String USERS_FOLDER = "users";
    private static final String GROUPS_FOLDER = "groups";
    private static final String MEMBERSHIPS_FOLDER = "memberships";
    private static final String PROFILES_FOLDER = "profiles";

    private RepositoryService repositoryService;
    private DataDistributionManager dataDistributionManager;
    private OrganizationIntegrationService organizationIntegrationService;

    public UpgradeOrganizationIntegrationDataPlugin(OrganizationIntegrationService organizationIntegrationService,
                                                    DataDistributionManager dataDistributionManager, RepositoryService repositoryService, InitParams initParams) {
        super(initParams);
        this.repositoryService = repositoryService;
        this.dataDistributionManager = dataDistributionManager;
        this.organizationIntegrationService = organizationIntegrationService;
    }

    @Override
    public boolean shouldProceedToUpgrade(String previousVersion, String newVersion) {
        if (organizationIntegrationService.isSynchronizeGroups()) {
            LOG.warn("Caution: synchronization of groups is activated for OrganizationIntegrationService. It shouldn't be enabled when upgrading!");
        }
        Session session = null;
        try {
            session = repositoryService.getCurrentRepository().getSystemSession(Util.WORKSPACE);
            boolean isUpgrade = false;
            isUpgrade = session.itemExists(Util.HOME_PATH);
            if (isUpgrade) {
                Node homeNode = (Node) session.getItem(Util.HOME_PATH);
                isUpgrade = homeNode.hasNode(ORGANIZATION_INITIALIZATIONS);
                if (isUpgrade) {
                    Node orgIntegrationParentNode = homeNode.getNode(ORGANIZATION_INITIALIZATIONS);
                    isUpgrade = orgIntegrationParentNode.hasNode(GROUPS_FOLDER);
                }
            }
            return isUpgrade;
        } catch (RepositoryException exception) {
            throw new RuntimeException(exception);
        } finally {
            if (session != null) {
                session.logout();
                session = null;
            }
        }
    }

    @Override
    public void processUpgrade(String oldVersion, String newVersion) {
        Session session = null;
        try {
            session = repositoryService.getCurrentRepository().getSystemSession(Util.WORKSPACE);
            Util.init(session);

            Node homeNode = (Node) session.getItem(Util.HOME_PATH);
            Node orgIntegrationParentNode = homeNode.getNode(ORGANIZATION_INITIALIZATIONS);
            migrateUsers(session, orgIntegrationParentNode);
            migrateProfiles(session, orgIntegrationParentNode);
            migrateGroups(session, orgIntegrationParentNode);
            migrateMemberships(session, orgIntegrationParentNode);
            orgIntegrationParentNode.remove();
            session.save();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        } finally {
            if (session != null) {
                session.logout();
                session = null;
            }
        }
    }

    private void migrateUsers(Session session, Node orgIntegrationParentNode) throws Exception {
        Node usersNode = orgIntegrationParentNode.getNode(USERS_FOLDER);
        Map<String, String> userMap = getNodeList(usersNode);
        for (String username : userMap.keySet()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Begin migration for [USER]: " + username);
            }
            if (!Util.hasUserFolder(dataDistributionManager, session, username)) {
                Util.createUserFolder(dataDistributionManager, session, username);
            }
            Node node = (Node) session.getItem(userMap.get(username));
            node.remove();
            session.save();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Migration successfully finished for [USER]: " + username);
            }
        }
        if (usersNode.hasNodes()) {
            throw new IllegalStateException(usersNode.getPath() + " : users parent node should be empty after migration.");
        }
        usersNode.remove();
        session.save();
    }

    private void migrateProfiles(Session session, Node orgIntegrationParentNode) throws Exception {
        Node profilesNode = orgIntegrationParentNode.getNode(PROFILES_FOLDER);
        Map<String, String> profileMap = getNodeList(profilesNode);
        for (String username : profileMap.keySet()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Begin migration for [PROFILE]: " + username);
            }
            if (!Util.hasProfileFolder(dataDistributionManager, session, username)) {
                Util.createProfileFolder(dataDistributionManager, session, username);
            }
            Node node = (Node) session.getItem(profileMap.get(username));
            node.remove();
            session.save();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Migration successfully finished for [PROFILE]: " + username);
            }
        }
        if (profilesNode.hasNodes()) {
            throw new IllegalStateException(profilesNode.getPath() + " : profiles parent node should be empty after migration.");
        }
        profilesNode.remove();
        session.save();
    }

    private void migrateGroups(Session session, Node orgIntegrationParentNode) throws Exception {
        Node groupsNode = orgIntegrationParentNode.getNode(GROUPS_FOLDER);
        Map<String, String> groupMap = getNodeList(groupsNode);
        for (String groupId : groupMap.keySet()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Begin migration for [GROUP]: " + groupId);
            }
            String originalGroupId = groupId.replace(SPECIAL_CHARACTER_REPLACEMENT, "/");
            if (!Util.hasGroupFolder(dataDistributionManager, session, originalGroupId)) {
                Util.createGroupFolder(dataDistributionManager, session, originalGroupId);
            }
            Node node = (Node) session.getItem(groupMap.get(groupId));
            node.remove();
            session.save();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Migration successfully finished for [GROUP]: " + originalGroupId);
            }
        }
        if (groupsNode.hasNodes()) {
            throw new IllegalStateException(groupsNode.getPath() + " : groups parent node should be empty after migration.");
        }
        groupsNode.remove();
        session.save();
    }

    private void migrateMemberships(Session session, Node orgIntegrationParentNode) throws Exception {
        Node membershipsNode = orgIntegrationParentNode.getNode(MEMBERSHIPS_FOLDER);
        Map<String, String> membershipMap = getNodeList(membershipsNode);
        for (String membershipFolderName : membershipMap.keySet()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Begin migration for [MEMBERSHIP]: " + membershipFolderName);
            }
            String[] membershipIdElements = membershipFolderName.split(MEMBERSHIP_SEPARATOR);
            MembershipImpl membership = new MembershipImpl();
            membership.setGroupId(membershipIdElements[0].replace(SPECIAL_CHARACTER_REPLACEMENT, "/"));
            membership.setMembershipType(membershipIdElements[1].replace(SPECIAL_CHARACTER_REPLACEMENT, "*"));
            membership.setUserName(membershipIdElements[2]);
            if (!Util.hasMembershipFolder(dataDistributionManager, session, membership)) {
                Util.createMembershipFolder(dataDistributionManager, session, membership);
            }
            Node node = (Node) session.getItem(membershipMap.get(membershipFolderName));
            node.remove();
            session.save();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Migration successfully finished for [MEMBERSHIP]: " + membership.getId());
            }
        }
        if (membershipsNode.hasNodes()) {
            throw new IllegalStateException(membershipsNode.getPath() + " : memberships parent node should be empty after migration.");
        }
        membershipsNode.remove();
        session.save();
    }

    private Map<String, String> getNodeList(Node parentNode) throws Exception {
        NodeIterator nodeIterator = parentNode.getNodes();
        Map<String, String> nodeNameList = new HashMap<String, String>();
        while (nodeIterator.hasNext()) {
            Node node = nodeIterator.nextNode();
            nodeNameList.put(node.getName(), node.getPath());
        }
        return nodeNameList;
    }
}
