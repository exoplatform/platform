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

import org.exoplatform.commons.utils.ListAccess;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.social.core.space.SpaceFilter;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;
/**
 * @author <a href="kmenzli@exoplatform.com">kmenzli</a>
 */
@Path("/space")
public class SpaceRestServiceImpl implements ResourceContainer {

    private static final Log LOG = ExoLogger.getLogger(SpaceRestServiceImpl.class);

    private final SpaceService spaceService;

    private final CacheControl cacheControl;
    private static final int MAX_LOADED_SPACES_BY_REQUEST = 20;

    public SpaceRestServiceImpl(SpaceService spaceService) {
        this.spaceService = spaceService;
        cacheControl = new CacheControl();
        cacheControl.setNoCache(true);
        cacheControl.setNoStore(true);

    }

    @GET
    @Path("/user/searchSpace/")
    public Response searchSpaces(@QueryParam("keyword") String keyword, @QueryParam("fields") String fields, @Context SecurityContext sc) {
        List<Space> spaces = new ArrayList<Space>();
        try {

            List<Space> alphabeticallySort = new ArrayList<Space>();
            String userId = sc.getUserPrincipal().getName();
            if (userId == null) {
                return Response.status(500).cacheControl(cacheControl).build();
            }
            ListAccess<Space> listAccess = null;
            if ((keyword == null) || (keyword.equals(""))) {
                listAccess = spaceService.getMemberSpacesByFilter(userId, null);
            } else {
                listAccess = spaceService.getMemberSpacesByFilter(userId, new SpaceFilter(keyword));
            }
            //--- List of searchedSpaces
            List<Space> spacesSearched = new ArrayList(Arrays.asList(listAccess.load(0, MAX_LOADED_SPACES_BY_REQUEST)));
            //--- List of spaces sorted by access
            ListAccess<Space> allSpacesSorted = spaceService.getLastAccessedSpace(userId, null);
            //--- Convert user spaces to List collection
            spaces = Arrays.asList(allSpacesSorted.load(0,MAX_LOADED_SPACES_BY_REQUEST));

            List<Object> sortedSearchedSpaces = new ArrayList<Object>();

            for (Space space : spaces) {
                Space filteredSpace = filterSpace(space.getId(), spacesSearched);
                if (filteredSpace != null) {
                    spacesSearched.remove(filteredSpace);
                    addSpaceWithFieldsToList(space, fields, sortedSearchedSpaces);
                }
            }
            for (Space space : spacesSearched) {
                addSpaceWithFieldsToList(space, fields, sortedSearchedSpaces);
            }
            return Response.ok(sortedSearchedSpaces, "application/json").cacheControl(cacheControl).build();
        } catch (Exception ex) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("An exception happens when searchSpaces", ex);
            }
        }
        return Response.status(500).cacheControl(cacheControl).build();
    }

    private void addSpaceWithFieldsToList(Space space, String fields, List<Object> sortedSearchedSpaces) {
      String groupId = space.getGroupId();
      String permanentSpaceName = groupId.split("/")[2];

      StringBuffer baseSpaceURL = new StringBuffer();
      baseSpaceURL.append(PortalContainer.getCurrentPortalContainerName()+ "/g/:spaces:") ;
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
      sortedSearchedSpaces.add(extractObject(space, fields));
    }

    private static Space filterSpace(String spaceId, List<Space> spacesSearched) {
        for (Space space : spacesSearched) {
            if (space.getId().equalsIgnoreCase(spaceId)) {
                return space;
            }
        }
        return null;
    }

    private Object extractObject(Object from, String fields) {
      if (fields != null) {
        String[] f = fields.split(",");

        if (f.length > 0) {
          JSONObject obj = new JSONObject(from);
          Map<String, Object> map = new HashMap<String, Object>();

          for (String name : f) {
            if (obj.has(name)) {
              try {
                map.put(name, obj.get(name));
              } catch (JSONException e) {
                if (LOG.isWarnEnabled()) {
                  LOG.warn("The key does NOT exist", e);
                }
              }
            }
          }
          return map;
        }
      }
      return from;
    }
}