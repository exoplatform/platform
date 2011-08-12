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
package org.exoplatform.platform.gadget.services.Online;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
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
import org.exoplatform.forum.service.ForumService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.impl.RuntimeDelegateImpl;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.model.Profile;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.ActivityManager;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.manager.RelationshipManager;
import org.exoplatform.social.core.relationship.model.Relationship;


/**
 * Created by The eXo Platform SARL Author : Tung Vu Minh tungvm@exoplatform.com
 * Jun 27, 2011 10:27:43 PM
 */

@Path("/online")
public class OnlineRestService implements ResourceContainer {
	private static final Log log = ExoLogger.getLogger(OnlineRestService.class);

	private static final CacheControl cacheControl;
	static {
		RuntimeDelegate.setInstance(new RuntimeDelegateImpl());
		cacheControl = new CacheControl();
		cacheControl.setNoCache(true);
		cacheControl.setNoStore(true);
	}

	/**
	 * List current user's contacts
	 */
	@GET
	@Path("my-contacts")
	@Produces("application/json")
	public Response myContacts(@Context SecurityContext sc, @Context UriInfo uriInfo) {
		try {	
			String viewerId = getUserId(sc, uriInfo);
			if(viewerId == null) {
				return Response.status(HTTPStatus.INTERNAL_ERROR).cacheControl(cacheControl).build();
			}

			IdentityManager identityManager = (IdentityManager)ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(IdentityManager.class);
			RelationshipManager relationshipManager = (RelationshipManager)ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(RelationshipManager.class);
			ActivityManager activityManager = (ActivityManager)ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(ActivityManager.class);
			
			Identity identity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, viewerId);
			Profile profile = identity.getProfile();
			List<Relationship> confirmedContacts = relationshipManager.getContacts(identity);

			List<Object> contacts = new ArrayList<Object>(confirmedContacts.size());

			for(Relationship contact : confirmedContacts){
				Identity contactIdentity = contact.getSender();
				if(viewerId.equals(contactIdentity.getRemoteId())) {
				   contactIdentity = contact.getReceiver();
				}              				
				profile = contactIdentity.getProfile();

				ContactBean contactBean = new ContactBean();
				contactBean.setId(contactIdentity.getRemoteId());
				contactBean.setFullName(profile.getFullName());
				contactBean.setAvatarUrl(profile.getAvatarImageSource());
				Object position = profile.getProperty("position");
				contactBean.setPosition(position == null ? null : position.toString());
				List<ExoSocialActivity> activities = activityManager.getActivities(contactIdentity, 0, 1);
				contactBean.setLatestActivity(activities.isEmpty() ? null : activities.get(0).getName());
				
				contacts.add(contactBean);
			}

			MessageBean data = new MessageBean();
			data.setData(contacts);
			return Response.ok(data, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();
		} catch(Exception e) {
			log.debug("Exception in my-contacts REST service: " + e.getMessage(), e);
			return Response.status(HTTPStatus.INTERNAL_ERROR).cacheControl(cacheControl).build();
		}
	}


	/**
	 * List online users
	 */
	@GET
	@Path("online-users")
	@Produces("application/json")
	public Response onlineUsers(@Context SecurityContext sc, @Context UriInfo uriInfo) {
		try {
			String viewerId = getUserId(sc, uriInfo);
			if(viewerId == null) {
				return Response.status(HTTPStatus.INTERNAL_ERROR).cacheControl(cacheControl).build();
			}
			
			ForumService forumService = (ForumService)ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(ForumService.class);
			List<String> users = forumService.getOnlineUsers();
			List<Object> profiles = new ArrayList<Object>(users.size());
			
			for(String userId : users) {
				if(userId.equals(viewerId)) {
					continue;
				}
				ContactBean contactBean = new ContactBean();
				contactBean.setId(userId);
				contactBean.setFullName(forumService.getUserInfo(userId).getFullName());
				
				profiles.add(contactBean);
			}
			MessageBean data = new MessageBean();
			data.setData(profiles);
			return Response.ok(data, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();
		} catch(Exception e) {
			log.debug("Exception in online-users REST service: " + e.getMessage(), e);
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


	public class ContactBean{
		private String id;
		private String fullName;
		private String avatarUrl;
		private String position;
		private String latestActivity;

		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getFullName() {
			return fullName;
		}
		public void setFullName(String fullName) {
			this.fullName = fullName;
		}
		public String getAvatarUrl() {
			return avatarUrl;
		}
		public void setAvatarUrl(String avatarUrl) {
			this.avatarUrl = avatarUrl;
		}
		public String getPosition() {
			return position;
		}
		public void setPosition(String position) {
			this.position = position;
		}
		public String getLatestActivity() {
			return latestActivity;
		}
		public void setLatestActivity(String latestActivity) {
			this.latestActivity = latestActivity;
		}
	}

	public class MessageBean {
		private List<Object> data;

		public void setData(List<Object> list) {
			this.data = list;
		}
		public List<Object> getData() {
			return data;
		}	
	}
	
}
