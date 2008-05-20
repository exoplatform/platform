/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.shindig.social.samplecontainer;

import org.apache.shindig.gadgets.GadgetToken;

import org.apache.shindig.social.ResponseItem;
import org.apache.shindig.social.ResponseError;
import org.apache.shindig.social.opensocial.ActivitiesService;
import org.apache.shindig.social.opensocial.model.Activity;
import org.json.JSONObject;

import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Date;

public class BasicActivitiesService implements ActivitiesService {
  private XmlStateFileFetcher fetcher;

  @Inject
  public BasicActivitiesService(XmlStateFileFetcher fetcher) {
    this.fetcher = fetcher;
  }

  public ResponseItem<List<Activity>> getActivities(List<String> ids,
      GadgetToken token) {
    Map<String, List<Activity>> allActivities = fetcher.getActivities();

    List<Activity> activities = new ArrayList<Activity>();

    for (String id : ids) {
      List<Activity> personActivities = allActivities.get(id);
      if (personActivities != null) {
        activities.addAll(personActivities);
      }
    }

    // TODO: Sort them
    return new ResponseItem<List<Activity>>(activities);
  }

  public ResponseItem<Activity> getActivity(String id, String activityId,
      GadgetToken token) {
    List<String> ids = new ArrayList<String>();
    ids.add(id);

    List<Activity> allActivities = getActivities(ids, token).getResponse();
    for (Activity activity : allActivities) {
      if (activity.getId().equals(activityId)) {
        return new ResponseItem<Activity>(activity);
      }
    }
    return new ResponseItem<Activity>(ResponseError.BAD_REQUEST,
        "Activity not found", null);
  }

  public ResponseItem createActivity(String personId, Activity activity,
      GadgetToken token) {
    // TODO: Validate the activity and do any template expanding
    activity.setUserId(personId);
    activity.setPostedTime(new Date().getTime());

    fetcher.createActivity(personId, activity);
    return new ResponseItem<JSONObject>(new JSONObject());
  }

}
