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

import java.util.*;
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
import org.exoplatform.commons.utils.ListAccess;
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

    ListAccess<Space> listAccess;

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
        try {

            List<Space> alphabeticallySort = new ArrayList<Space>();
            String userId = sc.getUserPrincipal().getName();
            if (userId == null) {
                return Response.status(500).cacheControl(cacheControl).build();
            }
            if ((keyword == null) || (keyword.equals(""))) {
                listAccess = spaceService.getMemberSpacesByFilter(userId, null);
            } else {
                listAccess = spaceService.getMemberSpacesByFilter(userId, new SpaceFilter(keyword));
            }
            List<Space> spacesSearched = Arrays.asList(listAccess.load(0, MAX_LOADED_SPACES_BY_REQUEST));
            List<Space> spaces = spaceService.getLastAccessedSpace(userId, null, 0, MAX_LOADED_SPACES_BY_REQUEST);
            List<Space> removedSpaces = new ArrayList<Space>();
            for (Space space : spaces) {
                baseSpaceURL = new StringBuffer();

                baseSpaceURL.append(PortalContainer.getCurrentPortalContainerName()+ "/g/:spaces:") ;
                String groupId = space.getGroupId();
                String permanentSpaceName = groupId.split("/")[2];
                if ((filterSpace(space.getId(), spacesSearched))) {
                    if (permanentSpaceName.equals(space.getPrettyName())) {
                        baseSpaceURL.append(permanentSpaceName) ;
                        baseSpaceURL.append("/");
                        baseSpaceURL.append(permanentSpaceName) ;
                    } else {
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

            alphabeticallySort = alphabeticallySpaceSort (spaces,spacesSearched,spaceService);

            spaces.addAll(alphabeticallySort);

            return Response.ok(spaces, "application/json").cacheControl(cacheControl).build();

        } catch (Exception ex) {
            if (logger.isWarnEnabled()) {
                logger.warn("An exception happens when searchSpaces", ex);
            }
        }
        return Response.status(500).cacheControl(cacheControl).build();
    }
    private static boolean filterSpace(String spaceId, List<Space> spacesSearched) {
        for (Space space : spacesSearched) {
            if (space.getId().equalsIgnoreCase(spaceId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return space not yet visdited by alphabetically
     * @param renderedSpaces
     * @param searchedSpaces
     * @param spaceService
     * @return
     */
    private static List<Space> alphabeticallySpaceSort(List<Space> renderedSpaces, List<Space> searchedSpaces, SpaceService spaceService) {
        List<String> renderedSpacesId = new ArrayList<String>();
        List<String> searchedSpacesId = new ArrayList<String>();
        List<Space> alphabeticallySpaces = new ArrayList<Space>();
        StringBuffer baseSpaceURL = null;

        for (Space searchedSpace : searchedSpaces) {
            searchedSpacesId.add(searchedSpace.getId());
        }
        for (Space renderedSpace : renderedSpaces) {
            renderedSpacesId.add(renderedSpace.getId());
        }
        searchedSpacesId.removeAll(renderedSpacesId);

        for (String spacesId : searchedSpacesId) {
            alphabeticallySpaces.add(spaceService.getSpaceById(spacesId));
        }
        Collections.sort(alphabeticallySpaces,new Comparator<Space>()
        {
            public int compare(Space s1, Space f2)
            {
                return s1.getPrettyName().toString().compareTo(f2.getPrettyName().toString());
            }
        });

        /** Build the correct space URL when it is rendered on left navigation*/
        for (Space alphabeticallySpace : alphabeticallySpaces) {
            baseSpaceURL = new StringBuffer();
            baseSpaceURL.append(PortalContainer.getCurrentPortalContainerName()+ "/g/:spaces:") ;
            String groupId = alphabeticallySpace.getGroupId();
            String permanentSpaceName = groupId.split("/")[2];
            if (permanentSpaceName.equals(alphabeticallySpace.getPrettyName())) {
                baseSpaceURL.append(permanentSpaceName) ;
                baseSpaceURL.append("/");
                baseSpaceURL.append(permanentSpaceName) ;
            } else {
                baseSpaceURL.append(permanentSpaceName) ;
                baseSpaceURL.append("/");
                baseSpaceURL.append(alphabeticallySpace.getPrettyName()) ;
            }
            alphabeticallySpace.setUrl(baseSpaceURL.toString());

        }

        return alphabeticallySpaces;
    }



}
