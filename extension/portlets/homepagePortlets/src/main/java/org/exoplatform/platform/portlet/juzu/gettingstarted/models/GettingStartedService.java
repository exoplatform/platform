/**
 * Copyright ( C ) 2012 eXo Platform SAS.
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
package org.exoplatform.platform.portlet.juzu.gettingstarted.models;

import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.cms.link.LinkManager;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.ext.hierarchy.NodeHierarchyCreator;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.common.RealtimeListAccess;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.model.Profile;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.ActivityManager;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.manager.RelationshipManager;
import org.exoplatform.social.core.relationship.model.Relationship;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="fbradai@exoplatform.com">Fbradai</a>
 * @date 12/26/12
 */
public class GettingStartedService {
    private static final Log log = ExoLogger.getLogger(GettingStartedService.class);

    public static Boolean hasDocuments(Node node, String userId) {
        SessionProvider sProvider = null;
        boolean docFound = false;
        try {
            sProvider = SessionProvider.createSystemProvider();
            NodeHierarchyCreator nodeHierarchyCreator_ = (NodeHierarchyCreator) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(NodeHierarchyCreator.class);

            try {
            Node userPrivateNode = node;
            if (userPrivateNode == null)
                userPrivateNode = nodeHierarchyCreator_.getUserNode(sProvider, userId).getNode("Private");

            LinkManager linkManager = (LinkManager) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(LinkManager.class);
            String primaryType = userPrivateNode.getProperty("jcr:primaryType").getString();
            if (primaryType.contains("nt:file")) {
                return true;
            } else {
                if (userPrivateNode.hasNodes()) {
                    NodeIterator childNodes = userPrivateNode.getNodes();
                    while ((childNodes.hasNext()) && (!docFound)) {
                        Node childNode = childNodes.nextNode();
                        docFound = hasDocuments(childNode, userId);
                    }
                }
            }
        } catch (Exception e) {
                log.error("Error in gettingStarted REST service: " + e.getLocalizedMessage(), e);
            return false;
        }
        } catch (Exception E) {
            log.error("Getting started Service : cannot check uploaded documents " + E.getLocalizedMessage(), E);
            return false;

        } finally {
            if (sProvider !=null) {
                sProvider.close();
            }

        }
        return docFound;
    }

    @SuppressWarnings("deprecation")
    public static boolean hasAvatar(String userId) {
        try {
            IdentityManager identityManager = (IdentityManager) ExoContainerContext.getCurrentContainer()
                    .getComponentInstanceOfType(IdentityManager.class);
            Identity identity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME,
                    userId);
            Profile profile = identity.getProfile();

            if (profile.getAvatarUrl() != null)
                return true;
            else
                return false;
        } catch (Exception e) {
            log.debug("Error in gettingStarted REST service: " + e.getMessage(), e);
            return false;
        }
    }

    @SuppressWarnings("deprecation")
    public static boolean hasSpaces(String userId) {
        try {
            SpaceService spaceService = (SpaceService) ExoContainerContext.getCurrentContainer()
                    .getComponentInstanceOfType(SpaceService.class);
            List<Space> spaces = spaceService.getAccessibleSpaces(userId);

            if (spaces.size() != 0)
                return true;
            else
                return false;
        } catch (Exception e) {
            log.debug("Error in gettingStarted REST service: " + e.getMessage(), e);
            return false;
        }
    }

    @SuppressWarnings("deprecation")
    public static boolean hasActivities(String userId) {
        try {
            IdentityManager identityManager = (IdentityManager) ExoContainerContext.getCurrentContainer()
                    .getComponentInstanceOfType(IdentityManager.class);
            Identity identity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME,
                    userId);
            ActivityManager activityService = (ActivityManager) ExoContainerContext.getCurrentContainer()
                    .getComponentInstanceOfType(ActivityManager.class);
            RealtimeListAccess<ExoSocialActivity> activities = activityService.getActivitiesWithListAccess(identity);
            List listAct = activities.loadAsList(0, 20);
            Iterator iterator = null;
            if (listAct != null) iterator = listAct.iterator();
            if (iterator != null) {
                while (iterator.hasNext()) {
                    ExoSocialActivity activity = (ExoSocialActivity) iterator.next();
                    if (activity.getType().equals(GettingStartedUtils.DEFAULT_ACTIVITY)) {
                        return true;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            log.debug("Error in gettingStarted service: " + e.getMessage(), e);
            return false;
        }
    }

    @SuppressWarnings("deprecation")
    public static boolean hasContacts(String userId) {
        try {

            IdentityManager identityManager = (IdentityManager) ExoContainerContext.getCurrentContainer()
                    .getComponentInstanceOfType(IdentityManager.class);
            RelationshipManager relationshipManager = (RelationshipManager) ExoContainerContext.getCurrentContainer()
                    .getComponentInstanceOfType(RelationshipManager.class);
            Identity identity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME,
                    userId);
            List<Relationship> confirmedContacts = relationshipManager.getContacts(identity);

            if (confirmedContacts.size() != 0)
                return true;
            else
                return false;
        } catch (Exception e) {
            log.debug("Error in gettingStarted REST service: " + e.getMessage(), e);
            return false;
        }
    }
}
