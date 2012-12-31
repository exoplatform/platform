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

import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.social.core.space.SpaceFilter;
import org.exoplatform.social.core.space.SpaceListAccess;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.*;
import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="kmenzli@exoplatform.com">kmenzli</a>
 * @date 01/12/12
 */
@Path("/space")
public class SpaceRestServiceImpl implements ResourceContainer {

    private static Log logger = ExoLogger.getLogger(SpaceRestServiceImpl.class);

    private final SpaceService spaceService;

    private final CacheControl cacheControl;
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

        try {

            String userId = sc.getUserPrincipal().getName();
            if (userId == null) {
                return Response.status(HTTPStatus.INTERNAL_ERROR).cacheControl(cacheControl).build();
            }
               if(keyword==null || keyword.equals("") )    {
                   listAccess = spaceService.getVisibleSpacesWithListAccess(userId, null);
               }else{
             listAccess = spaceService.getVisibleSpacesWithListAccess(userId, new SpaceFilter(keyword));
               }
            List<Space> spaces = Arrays.asList(listAccess.load(0, 10));

            for (Space space : spaces) {

                baseSpaceURL = new StringBuffer();
                //TODO Found solution to build spaces Link

                baseSpaceURL.append(PortalContainer.getCurrentPortalContainerName()+ "/g/:spaces:") ;
                String groupId = space.getGroupId();
                String permanentSpaceName = groupId.split("/")[2];

                if (permanentSpaceName.equals(space.getPrettyName())) {
                    //work-around for SOC-2366 when delete space after that create new space with the same name
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

            return Response.ok(spaces, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();
        } catch (Exception ex) {
            if (logger.isWarnEnabled()) {
                logger.warn("An exception happens when searchSpaces", ex);
            }
            return Response.status(HTTPStatus.INTERNAL_ERROR).cacheControl(cacheControl).build();
        }
    }
}
