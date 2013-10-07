/*
 * Copyright (C) 2003-2013 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU Affero General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.platform.common.rest.services.FeatureToggles;

import java.net.URI;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.exoplatform.commons.api.settings.ExoFeatureService;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.social.core.service.LinkProvider;

@Path("/homepage/intranet/features/")
public class FeatureTogglesRestService implements ResourceContainer {
  
  private static Log LOG = ExoLogger.getLogger(FeatureTogglesRestService.class);
  
  /**
   * Allows superusers to switch on or off a feature, then redirects them to the portal homepage.
   * 
   * @param featureName Name of the feature.
   * @param active Its value must be "yes" or "no" that switches on or off the feature respectively.
   * @authentication
   * @request
   * GET: http://localhost:8080/rest/homepage/intranet/features?name=notification&active=no
   * @return Redirects to the portal homepage.
   * @throws Exception
   */
  @GET
  public Response featureSwitch(@QueryParam("name") String featureName,
                                @QueryParam("active") String active) throws Exception {
    
    ExoContainer container = ExoContainerContext.getCurrentContainer();
    UserACL acl = (UserACL) container.getComponentInstanceOfType(UserACL.class);
    
    String currentUser = ConversationState.getCurrent().getIdentity().getUserId();
    
    //Check if the current user is a super user
    if (! acl.getSuperUser().equals(currentUser)) {
      LOG.warn("You don't have permission to switch a feature");
      throw new WebApplicationException(Response.Status.FORBIDDEN);
    }
    
    //
    boolean isActive = ("no".equals(active)) ? false : true;
    ExoFeatureService featureService = CommonsUtils.getService(ExoFeatureService.class);
    featureService.saveActiveFeature(featureName, isActive);
    
    //Redirect to the home page
    String domain = System.getProperty("gatein.email.domain.url", "http://localhost:8080");
    return Response.seeOther(URI.create(domain + LinkProvider.getRedirectUri(""))).build();
  }
  
}
