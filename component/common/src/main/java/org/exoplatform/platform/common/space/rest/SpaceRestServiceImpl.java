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
package org.exoplatform.platform.common.space.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.social.core.space.SpaceFilter;
import org.exoplatform.social.core.space.SpaceListAccess;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;
/**
 * @author <a href="kmenzli@exoplatform.com">kmenzli</a>
 * @date 01/12/12
 */
@Path("/space")
public class SpaceRestServiceImpl implements ResourceContainer {

    private static Log logger = ExoLogger.getLogger(SpaceRestServiceImpl.class);

    private final SpaceService spaceService;

    private final CacheControl cacheControl;
    private static final int MAX_LOADED_SPACES_BY_REQUEST = 20;

    SpaceListAccess listAccess;

    public SpaceRestServiceImpl(SpaceService spaceService) {
        this.spaceService = spaceService;
        cacheControl = new CacheControl();
        cacheControl.setNoCache(true);
        cacheControl.setNoStore(true);

    }
    @GET
    @Path("/user/searchSpace/")
    public Response searchSpaces(@QueryParam("keyword") String keyword,@Context SecurityContext sc) {
        StringBuffer baseSpaceURL = null;
        try
        {
            String userId = sc.getUserPrincipal().getName();
            if (userId == null) {
                return Response.status(500).cacheControl(cacheControl).build();
            }
            if ((keyword == null) || (keyword.equals(""))) {
                listAccess = spaceService.getVisibleSpacesWithListAccess(userId, null);
            } else {
                listAccess = spaceService.getVisibleSpacesWithListAccess(userId, new SpaceFilter(keyword));
            }
            List<Space> spacesSearched = Arrays.asList(listAccess.load(0, MAX_LOADED_SPACES_BY_REQUEST));
            List<Space> spaces = spaceService.getLastAccessedSpace(userId, null, 0, MAX_LOADED_SPACES_BY_REQUEST);
            List<Space> removedSpaces = new ArrayList<Space>();
            for (Space space : spaces) {
                baseSpaceURL = new StringBuffer();

                baseSpaceURL.append(PortalContainer.getCurrentPortalContainerName()+ "/g/:spaces:") ;
                String groupId = space.getGroupId();
                String permanentSpaceName = groupId.split("/")[2];
                if ((filterSpace(space.getId(), spacesSearched)) && (permanentSpaceName.startsWith(keyword)))
                {
                    if (permanentSpaceName.equals(space.getPrettyName()))
                    {
                    baseSpaceURL.append(permanentSpaceName) ;
                    baseSpaceURL.append("/");
                    baseSpaceURL.append(permanentSpaceName) ;
                    }
                    else {
                    baseSpaceURL.append(space.getPrettyName()) ;
                    baseSpaceURL.append("/");
                    baseSpaceURL.append(space.getPrettyName()) ;
                }

                space.setUrl(baseSpaceURL.toString());
            }
                else {
                    removedSpaces.add(space);
                }

            }

            spaces.removeAll(removedSpaces);
            return Response.ok(spaces, "application/json").cacheControl(cacheControl).build();
        } catch (Exception ex) {
            if (logger.isWarnEnabled())
                logger.warn("An exception happens when searchSpaces", ex);
            }
        return Response.status(500).cacheControl(cacheControl).build();
    }
    private static boolean filterSpace(String spaceId, List<Space> spacesSearched) {
        for (Space space : spacesSearched) {
            if (space.getId().equals(spaceId)) {
                return true;
            }
        }
        return false;
    }


}
