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

import org.apache.shindig.social.opensocial.ActivitiesService;
import org.apache.shindig.social.opensocial.model.Activity;

import com.google.inject.Inject;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * All "activity" requests are processed here.
 *
 */
@SuppressWarnings("unchecked")
public class ActivitiesServiceAdapter extends RestServerCollectionAdapter {
  private ActivitiesService handler;

  // TODO get these from the config files like in feedserver
  private static final String TITLE = "Acitivity Collection title";
  private static final String AUTHOR = "TODO";

  @Inject
  public ActivitiesServiceAdapter(ActivitiesService handler) {
    this.handler = handler;
  }

  /**
   * Handles the following URL
   *    /activities/{uid}/@self
   */
  public ResponseContext getFeed(RequestContext request) {
    String uid = request.getTarget().getParameter("uid");

    List<String> ids = new ArrayList<String>();
    ids.add(uid);
    // TODO: Use a real gadget token
    List<Activity> listOfObj = handler.getActivities(ids, null).getResponse();
    return returnFeed(request, TITLE, AUTHOR, (List)listOfObj);
  }

  /**
   *  Handles the following URL
   *    /activities/{uid}/@self/{activityId}
   */
  public ResponseContext getEntry(RequestContext request) {
    String uid = request.getTarget().getParameter("uid");
    String aid = request.getTarget().getParameter("aid");

    // TODO: Use a real gadget token
    Activity obj = handler.getActivity(uid, aid, null).getResponse();

    // TODO: how is entry id determined. check.
    String entryId = request.getUri().toString();
    Date updated = (obj != null) ? obj.getUpdated() : null;
    return returnEntry(request, obj, entryId, updated);
  }
}
