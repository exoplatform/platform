/***************************************************************************
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 ***************************************************************************/
package org.exoplatform.platform.gadget.services.Bookmark;

import java.net.URI;

import javax.jcr.Node;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.RuntimeDelegate;

import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.ext.hierarchy.NodeHierarchyCreator;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.impl.RuntimeDelegateImpl;
import org.exoplatform.services.rest.resource.ResourceContainer;

@Path("bookmarks/")
@Produces(MediaType.APPLICATION_JSON)
public class BookmarkRestService implements ResourceContainer {
	private static final Log log = ExoLogger.getLogger(BookmarkRestService.class);

	private static final CacheControl cacheControl;
	static {
		RuntimeDelegate.setInstance(new RuntimeDelegateImpl());
		cacheControl = new CacheControl();
		cacheControl.setNoCache(true);
		cacheControl.setNoStore(true);
	}
			
	/**
	 * Get user's bookmarks
	 * 
	 * REST service URL: /bookmarks/get
	 * 
	 * @return: user's bookmarks
	 */		
	@GET
	@Path("get")
	public Response get(@Context SecurityContext sc, @Context UriInfo uriInfo) throws Exception {
		try {
			String viewerId = getUserId(sc, uriInfo);
			if(viewerId == null) {
				return Response.status(HTTPStatus.INTERNAL_ERROR).cacheControl(cacheControl).build();
			}
			
			NodeHierarchyCreator nodeCreator = (NodeHierarchyCreator) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(NodeHierarchyCreator.class);
			SessionProvider sProvider = SessionProvider.createSystemProvider();
			Node userPrivateNode = nodeCreator.getUserNode(sProvider, viewerId).getNode("Private");
			if(!userPrivateNode.hasNode("Bookmarks")){
				Node bookmarksNode = userPrivateNode.addNode("Bookmarks");
				userPrivateNode.save();
				String default_bookmarks="[{\"name\":\"Discussions\", \"link\":\"/portal/intranet/forum\"},{\"name\":\"Wiki\", \"link\":\"/portal/intranet/wiki\"},{\"name\":\"Documents\", \"link\":\"/portal/intranet/documents\"},{\"name\":\"Agenda\", \"link\":\"/portal/intranet/calendar\"}]";
				bookmarksNode.setProperty("exo:bookmarkService_bookmarks", default_bookmarks);
				bookmarksNode.save();
			}
			String bookmarks = userPrivateNode.getNode("Bookmarks").getProperty("exo:bookmarkService_bookmarks").getString();
			
			return Response.ok(bookmarks, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();
		}
		catch (Exception e) {
			log.debug("Error in get bookmarks REST service: " + e.getMessage(), e);
			return Response.status(HTTPStatus.INTERNAL_ERROR).cacheControl(cacheControl).build();
		}
	}
	 

	/**
	 * Set user's bookmarks
	 * 
	 * REST service URL: /bookmarks/set/{bookmarks}
	 * 
	 */		
	@GET
	@Path("set/{bookmarks}")
	public Response set(@PathParam("bookmarks") String bookmarks, @Context SecurityContext sc, @Context UriInfo uriInfo) throws Exception {
		try {
			String viewerId = getUserId(sc, uriInfo);
			if(viewerId == null) {
				return Response.status(HTTPStatus.INTERNAL_ERROR).cacheControl(cacheControl).build();
			}

			NodeHierarchyCreator nodeCreator = (NodeHierarchyCreator) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(NodeHierarchyCreator.class);
			SessionProvider sProvider = SessionProvider.createSystemProvider();
			Node userPrivateNode = nodeCreator.getUserNode(sProvider, viewerId).getNode("Private");
			if(!userPrivateNode.hasNode("Bookmarks")){
				userPrivateNode.addNode("Bookmarks");
				userPrivateNode.save();
			}
			Node bookmarksNode = userPrivateNode.getNode("Bookmarks");
			bookmarksNode.setProperty("exo:bookmarkService_bookmarks", bookmarks);
			bookmarksNode.save();
			
			return Response.ok("{\"status\":\"successed\"}", MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();
		}
		catch (Exception e) {
			log.debug("Error in set bookmarks REST service: " + e.getMessage(), e);
			return Response.status(HTTPStatus.INTERNAL_ERROR).cacheControl(cacheControl).build();
		}
	}

	
	private String getUserId(SecurityContext sc, UriInfo uriInfo) {
		try {
			return sc.getUserPrincipal().getName();
		} catch (NullPointerException e) {
			return getViewerId(uriInfo);
		} catch (Exception e) {
			return null;
		}
	}

	private String getViewerId(UriInfo uriInfo) {
		URI uri = uriInfo.getRequestUri();
		String requestString = uri.getQuery();
		if (requestString == null) return null;
		String[] queryParts = requestString.split("&");
		for (String queryPart : queryParts) {
			if (queryPart.startsWith("opensocial_viewer_id")) {
				return queryPart.substring(queryPart.indexOf("=") + 1, queryPart.length());
			}
		}
		return null;
	}
}

