package org.exoplatform.platform.portlet.juzu.gettingstarted.models;

import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.cms.link.LinkManager;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.ext.hierarchy.NodeHierarchyCreator;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.common.RealtimeListAccess;
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
import java.util.List;

/**
 * @author <a href="fbradai@exoplatform.com">Fbradai</a>
 * @date 12/26/12
 */
public class GettingStartedService {
    private static final Log log = ExoLogger.getLogger(GettingStartedService.class);

    public static Boolean  hasDocuments(Node node,String userId)  {
        SessionProvider sProvider = SessionProvider.createSystemProvider();
        NodeHierarchyCreator nodeHierarchyCreator_=(NodeHierarchyCreator)  ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(NodeHierarchyCreator.class);
        boolean docFound= false;
        try {
            Node userPrivateNode=node;
            if (userPrivateNode==null)
                userPrivateNode= nodeHierarchyCreator_.getUserNode(sProvider, userId).getNode("Private");

            LinkManager linkManager = (LinkManager)ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(LinkManager.class);
            String primaryType = userPrivateNode.getProperty("jcr:primaryType").getString();
            if (primaryType.contains("nt:file") ) {  //|| linkManager.isLink(userPrivateNode)
                return true;
            }
            else
            {
                if (userPrivateNode.hasNodes()){
                    NodeIterator childNodes = userPrivateNode.getNodes();
                    while ((childNodes.hasNext())&&(!docFound)) {
                        Node childNode = childNodes.nextNode();
                        docFound=hasDocuments(childNode, userId);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return  false;
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
    public static  boolean hasActivities(String userId) {
        try {
            IdentityManager identityManager = (IdentityManager) ExoContainerContext.getCurrentContainer()
                    .getComponentInstanceOfType(IdentityManager.class);
            Identity identity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME,
                    userId);
            ActivityManager activityService = (ActivityManager) ExoContainerContext.getCurrentContainer()
                    .getComponentInstanceOfType(ActivityManager.class);
            RealtimeListAccess activities = activityService.getActivitiesWithListAccess(identity);

            if (activities.getSize() != 0) {

                if ((hasAvatar(userId)) && (hasContacts(userId)) && (hasSpaces(userId)) && (activities.getSize() >= 5))
                    return true;
                else if ((hasAvatar(userId)) && (hasContacts(userId)) && (!hasSpaces(userId)) && (activities.getSize() >= 4))
                    return true;
                else if ((hasAvatar(userId)) && (!hasContacts(userId)) && (hasSpaces(userId)) && (activities.getSize() >= 3))
                    return true;

                else if ((!hasAvatar(userId)) && (hasContacts(userId)) && (hasSpaces(userId)) && (activities.getSize() >= 4))
                    return true;
                else if ((!hasAvatar(userId)) && (!hasContacts(userId)) && (hasSpaces(userId)) && (activities.getSize() >= 2))
                    return true;
                else if ((!hasAvatar(userId)) && (!hasContacts(userId)) && (!hasSpaces(userId)) && (activities.getSize() >= 1))
                    return true;
                else return false;
            }
            else return false;
        } catch (Exception e) {
            log.debug("Error in gettingStarted REST service: " + e.getMessage(), e);
            return false;
        }
    }

    @SuppressWarnings("deprecation")
    public static boolean  hasContacts(String userId) {
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
