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

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;

import org.chromattic.api.ChromatticSession;
import org.exoplatform.commons.chromattic.ChromatticManager;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.impl.core.query.QueryImpl;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.common.lifecycle.SocialChromatticLifeCycle;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.model.Profile;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;

/**
 * @author <a href="fbradai@exoplatform.com">Fbradai</a>
 * @date 12/26/12
 */
public class GettingStartedService {
    private static final Log LOG = ExoLogger.getLogger(GettingStartedService.class);
    
    private static final String ORGANIZATION_PREFIXE_PATH = "/production/soc:providers/soc:organization/soc:%s";
    private static final String ACTIVITIES_NODE_TYPE = "soc:activities";
    private static final String RELATIONSHIP_NODE_TYPE = "soc:relationship";
    private static final String SPACE_MEMBER_NODE_TYPE = "soc:spacemember";
    private static final String NUMBER_ACTIVITIES_PROPERTY = "soc:number";

    public static Boolean hasDocuments(Node node, String userId) {
        SessionProvider sProvider = null;
        try {
          sProvider = SessionProvider.createSystemProvider();
          String pathCondition = node == null ? "" : 
                                 new StringBuilder(" AND jcr:path like ").append(node.getPath()).append("/%").toString();
          String fileQueryStatement = new StringBuilder("SELECT * FROM nt:file WHERE exo:owner='").
                              append(userId).append("'").append(pathCondition).toString();
          String ws = CommonsUtils.getRepository().getConfiguration().getDefaultWorkspaceName();
          QueryImpl query = (QueryImpl)sProvider.getSession(ws, CommonsUtils.getRepository()).
                    getWorkspace().getQueryManager().createQuery(fileQueryStatement, Query.SQL);
          query.setLimit(1);
          return (query.execute().getNodes().hasNext());
        } catch (RepositoryException e) {
            LOG.error("Getting started Service : cannot check uploaded documents " + e.getLocalizedMessage(), e);
            return false;
        } finally {
            if (sProvider !=null) {
                sProvider.close();
            }
        }
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
            LOG.debug("Error in gettingStarted REST service: " + e.getMessage(), e);
            return false;
        }
    }

    @SuppressWarnings("deprecation")
    public static boolean hasSpaces(String userId) {
      return hasChildren(userId, SPACE_MEMBER_NODE_TYPE);
    }

    @SuppressWarnings("deprecation")
    public static boolean hasActivities(String userId) {
      return hasChildren(userId, ACTIVITIES_NODE_TYPE);
    }

    @SuppressWarnings("deprecation")
    public static boolean hasContacts(String userId) {
      return hasChildren(userId, RELATIONSHIP_NODE_TYPE);
    }
    
    private static boolean hasChildren(String userId, String nodeType) {
      String userPath = String.format(ORGANIZATION_PREFIXE_PATH, userId);
      ChromatticSession session = lifecycleLookup().getSession();
      try {
        Node node = (Node) session.getJCRSession().getItem(userPath + "/" + nodeType);
        if (! nodeType.equals(ACTIVITIES_NODE_TYPE)) {
          return node.hasNodes();
        }
        if (node.hasProperty(NUMBER_ACTIVITIES_PROPERTY)) {
          return node.getProperty(NUMBER_ACTIVITIES_PROPERTY).getLong() > 0;
        }
      } catch (Exception e) {
        LOG.debug("Failed to get user node " + e.getMessage(), e);
      }
      return false;
    }
    
    public static SocialChromatticLifeCycle lifecycleLookup() {

      PortalContainer container = PortalContainer.getInstance();
      ChromatticManager manager = (ChromatticManager) container.getComponentInstanceOfType(ChromatticManager.class);
      return (SocialChromatticLifeCycle) manager.getLifeCycle(SocialChromatticLifeCycle.SOCIAL_LIFECYCLE_NAME);

    }
}
