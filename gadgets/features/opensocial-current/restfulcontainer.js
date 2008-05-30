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

/**
 * @fileoverview RESTful based opensocial container.
 */

RestfulContainer = function(baseUrl, domain, supportedFieldsArray) {
  opensocial.Container.call(this);

  var supportedFieldsMap = {};
  for (var objectType in supportedFieldsArray) {
    if (supportedFieldsArray.hasOwnProperty(objectType)) {
      supportedFieldsMap[objectType] = {};
      for (var i = 0; i < supportedFieldsArray[objectType].length; i++) {
        var supportedField = supportedFieldsArray[objectType][i];
        supportedFieldsMap[objectType][supportedField] = true;
      }
    }
  }

  this.environment_ = new opensocial.Environment(domain, supportedFieldsMap);
  this.baseUrl_ = baseUrl;

  this.securityToken_ = gadgets.util.getUrlParameters().st;
  this.parseSecurityToken();
};
RestfulContainer.inherits(opensocial.Container);

// Hopefully we can get rid of these with special @viewer and @owner tokens
// in the restful spec.
RestfulContainer.prototype.parseSecurityToken = function() {
  var parts = this.securityToken_.split(":");
  this.ownerId_ = parts[0];
  this.viewerId_ = parts[1];
  this.appId_ = parts[2];
};

RestfulContainer.prototype.getEnvironment = function() {
  return this.environment_;
};

RestfulContainer.prototype.requestCreateActivity = function(activity,
    priority, opt_callback) {
  opt_callback = opt_callback || {};
  // TODO: Implement this
};

RestfulContainer.prototype.requestData = function(dataRequest, callback) {
  callback = callback || {};

  var requestObjects = dataRequest.getRequestObjects();
  var totalRequests = requestObjects.length;

  if (totalRequests == 0) {
    callback(new opensocial.DataResponse({}, true));
    return;
  }

  var responseMap = {};
  var globalError = false;
  var responsesReceived = 0;

  var checkIfFinished = function() {
    responsesReceived++;
    if (responsesReceived == totalRequests) {
      var dataResponse = new opensocial.DataResponse(responseMap, globalError);
      callback(dataResponse);
    }
  }

  var makeProxiedRequest = function(requestObject, baseUrl, st) {
    var makeRequestParams = {
      "CONTENT_TYPE" : "JSON",
      "METHOD" : requestObject.request.method
      // TODO: Handle post data
    };

    // TODO: Use batching instead of doing this one by one
    gadgets.io.makeNonProxiedRequest(
        baseUrl + requestObject.request.url + "&st=" + st,
        function(result) {
          result = result.data;

          // TODO: handle errors
          var processedData = requestObject.request.processResponse(
              requestObject.request, result, null, null);
          globalError = globalError || processedData.hadError();
          responseMap[requestObject.key] = processedData;

          checkIfFinished();
        },
        makeRequestParams);
  }

  // may need multiple urls for one response but lets ignore that for now
  for (var i = 0; i < totalRequests; i++) {
    makeProxiedRequest(requestObjects[i], this.baseUrl_, this.securityToken_);
  }

};

RestfulContainer.prototype.translateIdSpec = function(idSpec) {
  // This will get cleaner in 0.8 because idSpec will be an object

  // TODO: Some of these rest urls return "feeds" and some return "entries"..
  // this is going to get complicated unless we can get them all in the same
  // format
  if (idSpec == "VIEWER") {
    return this.viewerId_ + "/@self";
  } else if (idSpec == "VIEWER_FRIENDS") {
    return this.viewerId_ + "/@friends";
  } else if (idSpec == "OWNER") {
    return this.ownerId_ + "/@self";
  } else if (idSpec == "OWNER_FRIENDS") {
    return this.ownerId_ + "/@friends";
  } else if (opensocial.Container.isArray(idSpec)) {
    for (var i = 0; i < idSpec.length; i++) {
      // TODO: We will need multiple urls here....don't want to think about
      // that yet
    }
  } else {
    return idSpec + "/@self";
  }
};

RestfulContainer.prototype.newFetchPersonRequest = function(id, opt_params) {
  var peopleRequest = this.newFetchPeopleRequest(id, opt_params);

  var me = this;
  return new RestfulRequestItem(peopleRequest.url, peopleRequest.method, null,
      function(rawJson) {
        return me.createPersonFromJson(rawJson);
      });
};

RestfulContainer.prototype.newFetchPeopleRequest = function(idSpec,
    opt_params) {
  var url = "/people/" + this.translateIdSpec(idSpec);

  // TODO: Add sortOrder, filter
  //    'sortOrder': opt_params['sortOrder'] || 'topFriends',
  //    'filter': opt_params['filter'] || 'all',

  url += "?fields=" + (opt_params['profileDetail'].join(','));
  url += "&startPage=" + (opt_params['first'] || 0);
  url += "&count=" + (opt_params['max'] || 20);

  var me = this;
  return new RestfulRequestItem(url, "GET", null,
      function(rawJson) {
        var jsonPeople = rawJson['entry'];
        var people = [];
        for (var i = 0; i < jsonPeople.length; i++) {
          people.push(me.createPersonFromJson(jsonPeople[i]));
        }
        return new opensocial.Collection(people,
            rawJson['startIndex'], rawJson['totalResults']);
      });
};

RestfulContainer.prototype.createPersonFromJson = function(serverJson) {
  return new JsonPerson(serverJson);
}

RestfulContainer.prototype.newFetchPersonAppDataRequest = function(idSpec,
    keys) {
   var url = "/appdata/" + this.translateIdSpec(idSpec) + "/" + this.appId_
       + "?fields=" + keys.join(',');
  return new RestfulRequestItem(url, "GET", null,
      function (appData) {
        return gadgets.util.escape(appData['entry'], true);
      });
};

RestfulContainer.prototype.newUpdatePersonAppDataRequest = function(id, key,
    value) {
  var url = "/appdata/" + this.translateIdSpec(id) + "/" + this.appId_
       + "?fields=" + key;
  // TODO: Or should we use POST?
  return new RestfulRequestItem(url, "PUT", {key: value});
};

RestfulContainer.prototype.newFetchActivitiesRequest = function(idSpec,
    opt_params) {
  var url = "/activities/" + this.translateIdSpec(idSpec)
      + "?app=" + this.appId_;
  return new RestfulRequestItem(url, "GET", null,
      function(rawJson) {
        rawJson = rawJson['entry'];
        var activities = [];
        for (var i = 0; i < rawJson.length; i++) {
          activities.push(new JsonActivity(rawJson[i]));
        }
        return {'activities' : new opensocial.Collection(activities)};
      });
};

RestfulContainer.prototype.newCreateActivityRequest = function(idSpec,
    activity) {
   // TODO: no idea how to do this yet
  return new RestfulRequestItem("TODO", "POST", {});
};

RestfulRequestItem = function(url, method, postData, processData) {
  this.url = url;
  this.method = method;
  this.postData = postData;
  this.processData = processData ||
    function (rawJson) {
      return rawJson;
    };

  this.processResponse = function(originalDataRequest, rawJson, error,
      errorMessage) {

    return new opensocial.ResponseItem(originalDataRequest,
        error ? null : this.processData(rawJson), error, errorMessage);
  }
};