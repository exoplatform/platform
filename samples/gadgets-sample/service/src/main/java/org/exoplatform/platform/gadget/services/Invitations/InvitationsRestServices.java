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
package org.exoplatform.platform.gadget.services.Invitations;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.RuntimeDelegate;

import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.rest.impl.RuntimeDelegateImpl;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.model.Profile;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.manager.RelationshipManager;
import org.exoplatform.social.core.relationship.model.Relationship;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;

@Path("/gadgets/social-inbox/v1")
@Produces("application/json")
public class InvitationsRestServices implements ResourceContainer {

	private static final CacheControl cacheControl;
	static {
		RuntimeDelegate.setInstance(new RuntimeDelegateImpl());
		cacheControl = new CacheControl();
		cacheControl.setNoCache(true);
		cacheControl.setNoStore(true);
	}

	@GET
	@Path("contacts/{identityId}/pending")
	public Response contactsPendingRequests(@PathParam("identityId") String identityId) {
		IdentityManager identityManager = (IdentityManager) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(IdentityManager.class);

		Identity identity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, identityId);
		if (identity == null){
			return Response.ok("identity null for "+identityId, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();
		}

		RelationshipManager relationshipManager = (RelationshipManager) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(RelationshipManager.class);
		List<Relationship> relations = relationshipManager.getIncoming(identity);

		ArrayList<Object> liste = new ArrayList<Object>();
		for (Relationship relation : relations) {
			Relation rel = new Relation();
			
			Identity contactIdentity = relation.getSender();
			Profile profile = contactIdentity.getProfile();
			
			rel.setRelationshipId(contactIdentity.getRemoteId());
			rel.setRequesterName(profile.getFullName());
			rel.setPosition(profile.getPosition());
			rel.setAvatarUrl(profile.getAvatarUrl());
			liste.add(rel);
		}

		return renderJSON(liste);
	}

	@GET
	@Path("spaces/{userId}/pending")
	public Response invited(@PathParam("userId") String userId){
		SpaceService spaceService = (SpaceService) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(SpaceService.class);
		List<Space> spaces =  new ArrayList<Space>();
		try{
			spaces = spaceService.getInvitedSpaces(userId);
		}catch (Exception e) {
			// TODO: handle exception
		}
		List<Object> listData=  new ArrayList<Object>();
		for (Space space : spaces) {
			listData.add(space);
		}
		return renderJSON(listData);
	}

	/**
	 * Render the response with JSON format
	 */
	private Response renderJSON(List<Object> liste) {
		CacheControl cacheControl = new CacheControl();
		cacheControl.setNoCache(true);
		cacheControl.setNoStore(true);
		MessageBean data = new MessageBean();
		data.setData(liste);
		return Response.ok(data, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();
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


