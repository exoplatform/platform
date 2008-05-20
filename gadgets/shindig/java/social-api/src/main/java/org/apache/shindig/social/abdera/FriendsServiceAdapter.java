/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  The ASF licenses this file to You
* under the Apache License, Version 2.0 (the "License"); you may not
* use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.  For additional information regarding
* copyright in this work, please see the NOTICE file in the top level
* directory of this distribution.
*/
package org.apache.shindig.social.abdera;

import org.apache.shindig.gadgets.GadgetToken;
import org.apache.shindig.social.opensocial.PeopleService;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.model.IdSpec;
import org.apache.shindig.social.opensocial.model.ApiCollection;
import org.apache.shindig.social.ResponseItem;

import com.google.inject.Inject;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.json.JSONException;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * All friends related requests are processed here.
 */
@SuppressWarnings("unchecked")
public class FriendsServiceAdapter extends RestServerCollectionAdapter {
  private static Logger logger =
      Logger.getLogger(FriendsServiceAdapter.class.getName());
  private PeopleService handler;

  // TODO get these from the config files like in feedserver
  private static final String TITLE = "People Collection title";
  private static final String AUTHOR = "TODO";

  @Inject
  public FriendsServiceAdapter(PeopleService handler) {
    this.handler = handler;
  }

  /**
   * Handles the following URLs
   *       /people/{uid}/@all
   *       /people/{uid}/@friends
   */
  public ResponseContext getFeed(RequestContext request) {
    String uid = request.getTarget().getParameter("uid");
    ResponseItem<ApiCollection<Person>> friends = getFriends(request, uid);

    return returnFeed(request, TITLE, AUTHOR,
        (List)friends.getResponse().getItems());
  }

  /**
   * Handles the following URLs
   *       /people/{uid}/@all/{pid}
   *       /people/{uid}/@friends/{pid}
   */
  public ResponseContext getEntry(RequestContext request) {
    String uid = request.getTarget().getParameter("uid");
    String pid = request.getTarget().getParameter("pid");
    ResponseItem<ApiCollection<Person>> allFriends = getFriends(request, uid);

    // TODO: Improve the service apis so we can get rid of code like this
    Person person = null;
    for (Person friend : allFriends.getResponse().getItems()) {
      if (friend.getId().equals(pid)) {
        person = friend;
        break;
      }
    }

    // TODO: how is entry id determined. check.
    // TODO: Pull this kind of code into a utility class
    String entryId = request.getUri().toString();
    Date updated = (person != null) ? person.getUpdated() : null;
    logger.fine("updated = " + updated);
    return returnEntry(request, person, entryId, updated);
  }

  private ResponseItem<ApiCollection<Person>> getFriends(RequestContext request,
      String uid) {
    GadgetToken token = getGadgetToken(request, uid);

    IdSpec idSpec = new IdSpec(null, IdSpec.Type.VIEWER_FRIENDS);
    List<String> viewerFriendIds = null;
    try {
      viewerFriendIds = handler.getIds(idSpec, token);
    } catch (JSONException e) {
      // TODO: Ignoring this for now. Eventually we can make the service apis
      // fit the restful model better. For now, it is worth some hackiness to
      // keep the interfaces stable.
    }

    // TODO: Should have a real concept of first, max sort etc with defaults
    return handler.getPeople(viewerFriendIds, PeopleService.SortOrder.name,
        null, 0, 100, null, token);
  }
}
