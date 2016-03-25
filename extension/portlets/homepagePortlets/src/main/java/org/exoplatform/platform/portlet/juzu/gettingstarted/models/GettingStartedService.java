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

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;

import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.commons.utils.ListAccess;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.impl.core.query.QueryImpl;
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
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;

/**
 * @author <a href="fbradai@exoplatform.com">Fbradai</a>
 */
public class GettingStartedService {
    @Inject
    private SpaceService spaceService;
    
    @Inject
    private IdentityManager identityManager;
    
    @Inject 
    private RelationshipManager relManager;
    
    @Inject
    private ActivityManager activityManager;
    
    private static final Log LOG = ExoLogger.getLogger(GettingStartedService.class);

    public Boolean hasDocuments(Node node, String userId) {
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
    public boolean hasAvatar(String userId) {
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

    public boolean hasSpaces(String userId) {
      try {
        ListAccess<Space> spaces = spaceService.getAccessibleSpacesWithListAccess(userId);
        if (spaces != null) {
          return spaces.getSize() > 0;    
        }
      } catch (Exception ex) {
        LOG.error("Can't get space number. Error during get accessible spaces", ex);
      }
      return false;
    }

    public boolean hasActivities(String userId) {
      Identity identity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, userId, false);
      
      if (identity != null) {
        RealtimeListAccess<ExoSocialActivity> activities = activityManager.getActivitiesWithListAccess(identity);
        if (activities != null) {
          return activities.getSize() > 0;          
        }
      }
      
      return false;
    }

    public boolean hasContacts(String userId) {
      Identity identity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, userId, false);
      
      if (identity != null) {
        try {
          ListAccess<Identity> identities = relManager.getConnections(identity); 
          if (identities != null) {
            return identities.getSize() > 0;            
          }
        } catch (Exception ex) {
          LOG.error("Error during get connections", ex);
        }
      }

      return false;
    }
}
