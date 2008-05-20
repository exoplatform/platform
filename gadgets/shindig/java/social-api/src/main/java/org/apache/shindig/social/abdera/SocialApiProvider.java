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

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.apache.abdera.protocol.server.TargetType;
import org.apache.abdera.protocol.server.impl.DefaultProvider;
import org.apache.abdera.protocol.server.impl.RouteManager;

public class SocialApiProvider extends DefaultProvider {
  //TODO why is this hardcoded here. can't this be from servletContext?
  private static final String BASE = "/social/rest/";

  private Provider<PeopleServiceAdapter> peopleAdapterProvider;
  private Provider<ActivitiesServiceAdapter> activitiesAdapterProvider;
  private Provider<FriendsServiceAdapter> friendsAdapterProvider;

  /**
   * The CollectionAdapter enum standardizes the names and descriptions of the
   * URL templates as defined in the RESTful API spec. Each unique template has
   * a corresponding CollectionAdapter. For example, "/people/{uid}/@all" is
   * roughly translated in English to read "Profiles of Connections of User" and
   * is the referred to in the code as PROFILES_OF_CONNECTIONS_OF_USER. The
   * descriptions can be customized by an implementer and used as the titles for
   * feeds.
   * TODO: Add ResourceBundle functions.
   */
  public enum CollectionAdapter {
    //People
    PROFILES_OF_CONNECTIONS_OF_USER("Profiles of Connections of User"),
    PROFILES_OF_FRIENDS_OF_USER("Profiles of Friends of User"),
    CONNECTIONS_OF_USER("Connections of User"),
    PROFILE_OF_CONNECTION_OF_USER("Profile of Connection of User"),
    PROFILE_OF_USER("Profile of User"),
    //Activities
    ACTIVITIES_OF_USER("Activities of User"),
    ACTIVITIES_OF_FRIENDS_OF_USER("Activities of Friends of User"),
    ACTIVITIES_OF_GROUP_OF_USER("Activities of Group of User"),
    ACTIVITY_OF_USER("Activity of User"),
    //AppData
    APPDATA_OF_APP_OF_USER("AppData of App of User"),
    APPDATA_OF_FRIENDS_OF_USER("AppData of Friends of User");

    private String description;

    private CollectionAdapter(String description) {
      this.description = description;
    }

    public String toString() {
      return description;
    }

    public static CollectionAdapter getValue(String value) {
      return valueOf(value.replaceAll(" ", "_").toUpperCase());
    }
  }

  @Inject
  public void setPeopleAdapter(Provider<PeopleServiceAdapter>
      peopleAdapterProvider) {
    this.peopleAdapterProvider = peopleAdapterProvider;
  }

  @Inject
  public void setFriendsAdapter(Provider<FriendsServiceAdapter>
      friendsAdapterProvider) {
    this.friendsAdapterProvider = friendsAdapterProvider;
  }

  @Inject
  public void setActivitiesAdapter(Provider<ActivitiesServiceAdapter>
      activitiesAdapterProvider) {
    this.activitiesAdapterProvider = activitiesAdapterProvider;
  }

  /**
   * CollectionAdapters are provided via Guice and the RouteManager wires
   * together the Routes, their TargetTypes and CollectionAdapters.
   *
   * TODO: Create one CollectionAdapter per URL. There is currently logic in the
   * People and Activities Adapters that allows them to be multi-purpose, but
   * this will need to change.
   *
   * TODO: Fully implement all routes in the 0.8 RESTful API spec.
   * They are specified here, but only some produce output from the server.
   * Currently implemented:
   * /people/{uid}/@all
   * /people/{uid}/@all/{pid}
   * /people/{uid}/@self
   * /activities/{uid}/@self/{aid}
   * /activities/{uid}/@self
   */
  public void initialize() {
    PeopleServiceAdapter peopleAdapter = peopleAdapterProvider.get();
    FriendsServiceAdapter friendsAdapter = friendsAdapterProvider.get();
    ActivitiesServiceAdapter activitiesAdapter
        = activitiesAdapterProvider.get();

    // Add the RouteManager that parses incoming and builds outgoing URLs
    // {uid} is assumed to be a deterministic GUID for the service
    routeManager = new RouteManager()
        // People

        // Collection of all people connected to user {uid}
        // /people/{uid}/@all
        // Currently, Shindig only has friends, so @all == @friends
        .addRoute(CollectionAdapter.CONNECTIONS_OF_USER.toString(),
            BASE + "people/:uid/@all",
            TargetType.TYPE_COLLECTION, friendsAdapter)

        // Collection of all friends of user {uid}; equal to @all
        // /people/{uid}/@friends
        .addRoute(CollectionAdapter.PROFILES_OF_FRIENDS_OF_USER.toString(),
            BASE + "people/:uid/@friends",
            TargetType.TYPE_COLLECTION, friendsAdapter)

        // Collection of all people connected to user {uid} in group {groupid}
        // /people/{uid}/{groupid}
        // TODO: Shindig does not support groups yet
        .addRoute(CollectionAdapter.PROFILES_OF_CONNECTIONS_OF_USER.toString(),
            BASE + "people/:uid/:groupid",
            TargetType.TYPE_COLLECTION, null)

        // Individual person record. /people/{uid}/@all/{pid}
        .addRoute(CollectionAdapter.PROFILE_OF_CONNECTION_OF_USER.toString(),
            BASE + "people/:uid/@all/:pid",
            TargetType.TYPE_ENTRY, friendsAdapter)

        // Self Profile record for user {uid} /people/{uid}/@self
        .addRoute(CollectionAdapter.PROFILE_OF_USER.toString(),
            BASE + "people/:uid/@self",
            TargetType.TYPE_ENTRY, peopleAdapter)


        // Activities

        // Collection of activities for given user /activities/{uid}/@self
        .addRoute(CollectionAdapter.ACTIVITIES_OF_USER.toString(),
            BASE + "activities/:uid/@self",
            TargetType.TYPE_COLLECTION, activitiesAdapter)

        // Collection of activities for friends of the given user {uid}
        // /activities/{uid}/@friends
        .addRoute(CollectionAdapter.ACTIVITIES_OF_FRIENDS_OF_USER.toString(),
            BASE + "activities/:uid/@friends",
            TargetType.TYPE_COLLECTION, activitiesAdapter)

        // Collection of activities for people in group {groupid}
        // belonging to given user {uid} -- /activities/{uid}/{groupid}
        .addRoute(CollectionAdapter.ACTIVITIES_OF_GROUP_OF_USER.toString(),
            BASE + "activities/:uid/:groupid",
            TargetType.TYPE_COLLECTION, activitiesAdapter)

        // Individual activity resource; usually discovered from collection
        // /activities/{uid}/@self/{aid}
        .addRoute(CollectionAdapter.ACTIVITY_OF_USER.toString(),
            BASE + "activities/:uid/@self/:aid",
            TargetType.TYPE_ENTRY, activitiesAdapter)


        // AppData

        // Individual App Data record for a given user+app, consisting primarily
        // of a bag of key/value pairs. -- /appdata/{uid}/self/{aid}
        .addRoute(CollectionAdapter.APPDATA_OF_APP_OF_USER.toString(),
            BASE + "appdata/:uid/self/:aid",
            TargetType.TYPE_ENTRY, null)

        // Collection of App Data records for friends of {uid}
        // /appdata/{uid}/friends/{aid}
        .addRoute(CollectionAdapter.APPDATA_OF_FRIENDS_OF_USER.toString(),
            BASE + "appdata/:uid/friends/:aid",
            TargetType.TYPE_COLLECTION, null)

        ;

    targetBuilder = routeManager;
    targetResolver = routeManager;
  }
}
