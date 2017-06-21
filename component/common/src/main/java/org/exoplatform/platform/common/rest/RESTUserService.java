/*
 * Copyright (C) 2003-2013 eXo Platform SAS.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.exoplatform.platform.common.rest;

import org.apache.commons.lang.StringUtils;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.user.UserStateModel;
import org.exoplatform.services.user.UserStateService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.parsers.ParserConfigurationException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Path("/state/")
public class RESTUserService implements ResourceContainer {
  private final UserStateService userService;

  public RESTUserService(UserStateService userService) {
    this.userService = userService;
  }

  @GET
  @Path("/ping/")
  @RolesAllowed("users")
  public Response updateState() {
    CacheControl cacheControl = new CacheControl();
    cacheControl.setNoCache(true);
    String userId = ConversationState.getCurrent().getIdentity().getUserId();
    userService.ping(userId);
    return Response.ok().cacheControl(cacheControl).build();
  }

  @GET
  @Path("/status/")
  @RolesAllowed("users")
  public Response online() throws ParserConfigurationException, JSONException {
    List<UserStateModel> usersOnline = userService.online();
    if(usersOnline == null) return Response.ok().build();
    JSONArray json = new JSONArray();
    for (UserStateModel model : usersOnline) {
      //
      json.put(fillModelToJson(model));
    }
    return Response.ok(json.toString(), MediaType.APPLICATION_JSON).build();
  }
  
  private JSONObject fillModelToJson(UserStateModel model) throws JSONException {
    JSONObject object = new JSONObject();
    object.put("activity", "offline");
    object.put("userId", model.getUserId());
    Date date = new Date(model.getLastActivity());
    DateFormat ISO_8601_DATE_TIME = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    String lastActivityDate = ISO_8601_DATE_TIME.format(date);
    object.put("lastActivityDate", lastActivityDate);
    object.put("status", model.getStatus());
    //
    if (userService.isOnline(model.getUserId())) {
      object.put("activity", "online");
    }
    return object;
  }
  
  @GET
  @Path("/status/{userId}/")
  @RolesAllowed("users")
  public Response getStatus(@PathParam("userId") String userId) throws JSONException {
    UserStateModel model = userService.getUserState(userId);
    if(model == null) return Response.noContent().build();
    //
    JSONObject object = fillModelToJson(model);
    return Response.ok(object.toString(), MediaType.APPLICATION_JSON).build();
  }
  
  @PUT
  @Path("/status/{userId}/")
  @RolesAllowed("users")
  @Deprecated
  public Response setStatus(@PathParam("userId") String userId, @QueryParam("status") String status) throws JSONException {
    String authenticated = ConversationState.getCurrent().getIdentity().getUserId();
    if (!authenticated.equals(userId))
      return Response.status(Status.FORBIDDEN).build();
    UserStateModel model = userService.getUserState(userId);
    if(StringUtils.isNotEmpty(status)) {
      model.setStatus(status);
      userService.save(model);
      return Response.ok().build();
    }
    return Response.notModified().build();
  }

  @PUT
  @Path("/status")
  @RolesAllowed("users")
  public Response setStatus(@QueryParam("status") String status) throws JSONException {
    String authenticated = ConversationState.getCurrent().getIdentity().getUserId();
    UserStateModel model = userService.getUserState(authenticated);
    if(StringUtils.isNotEmpty(status)) {
      model.setStatus(status);
      userService.save(model); 
      return Response.ok().build();
    }
    return Response.notModified().build();
  }
}
