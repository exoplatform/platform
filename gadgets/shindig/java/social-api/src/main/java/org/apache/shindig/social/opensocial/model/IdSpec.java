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
package org.apache.shindig.social.opensocial.model;

import org.json.JSONException;
import org.json.JSONArray;

import java.util.List;
import java.util.ArrayList;

public class IdSpec {
  public enum Type {
    VIEWER, OWNER, VIEWER_FRIENDS, OWNER_FRIENDS, USER_IDS
  }

  String jsonSpec;
  Type type;

  public IdSpec(String jsonSpec, Type type) {
    this.jsonSpec = jsonSpec;
    this.type = type;
  }


  public static IdSpec fromJson(String jsonIdSpec) {
    Type idSpecEnum;
    try {
      idSpecEnum = Type.valueOf(jsonIdSpec);
    } catch (IllegalArgumentException e) {
      idSpecEnum = Type.USER_IDS;
    }

    return new IdSpec(jsonIdSpec, idSpecEnum);
  }

  /**
   * Only valid for IdSpecs of type USER_IDS
   * @return A list of the user ids in the id spec
   *
   * @throws JSONException If the id spec isn't a valid json String array
   */
  public List<String> fetchUserIds() throws JSONException {
    JSONArray userIdArray;
    try {
      userIdArray = new JSONArray(jsonSpec);
    } catch (JSONException e) {
      // If it isn't an array, treat it as a simple string
      // TODO: This will go away with rest so we can remove this hack
      List<String> list = new ArrayList<String>();
      list.add(jsonSpec);
      return list;
    }
    List<String> userIds = new ArrayList<String>(userIdArray.length());

    for (int i = 0; i < userIdArray.length(); i++) {
      userIds.add(userIdArray.getString(i));
    }
    return userIds;
  }

  public Type getType() {
    return type;
  }
}
