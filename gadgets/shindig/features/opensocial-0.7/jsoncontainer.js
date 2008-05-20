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
 * @fileoverview Json based opensocial container.
 */


JsonContainer = function(baseUrl, domain, supportedFieldsArray) {
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
};
JsonContainer.inherits(opensocial.Container);

JsonContainer.prototype.getEnvironment = function() {
  return this.environment_;
};

JsonContainer.prototype.requestCreateActivity = function(activity,
    priority, opt_callback) {
  opt_callback = opt_callback || {};

  var req = opensocial.newDataRequest();
  req.add(this.newCreateActivityRequest('VIEWER', activity), 'key');
  req.send(function(response) {
    opt_callback(response.get('key'));
  });
};

JsonContainer.prototype.createJson = function(requestObjects) {
  var jsonObjects = [];
  for (var i = 0; i < requestObjects.length; i++) {
    jsonObjects[i] = requestObjects[i].request.jsonParams;
  }
  return gadgets.json.stringify(jsonObjects);
};

JsonContainer.prototype.requestData = function(dataRequest, callback) {
  callback = callback || {};

  var requestObjects = dataRequest.getRequestObjects();
  if (requestObjects.length == 0) {
    callback(new opensocial.DataResponse({}, true));
    return;
  }

  var jsonText = this.createJson(requestObjects);

  var sendResponse = function(result) {
    result = result.data;

    if (!result || result['error']) {
      callback(new opensocial.DataResponse({}, true));
      return;
    }

    var responses = result['responses'] || [];
    var globalError = false;

    var responseMap = {};

    for (var i = 0; i < responses.length; i++) {
      var response = responses[i];
      var rawData = response['response'];
      var error = response['error'];
      var errorMessage = response['errorMessage'];

      var processedData = requestObjects[i].request.processResponse(
          requestObjects[i].request, rawData, error, errorMessage);
      globalError = globalError || processedData.hadError();
      responseMap[requestObjects[i].key] = processedData;
    }

    var dataResponse = new opensocial.DataResponse(responseMap, globalError);
    callback(dataResponse);
  };

  new BatchRequest(this.baseUrl_, jsonText, sendResponse,
      {'st' : this.securityToken_}).send();
};

JsonContainer.prototype.newFetchPersonRequest = function(id, opt_params) {
  var peopleRequest = this.newFetchPeopleRequest(id, opt_params);

  var me = this;
  return new RequestItem(peopleRequest.jsonParams,
      function(rawJson) {
        return me.createPersonFromJson(rawJson['items'][0]);
      });
};

JsonContainer.prototype.newFetchPeopleRequest = function(idSpec, opt_params) {
  var me = this;
  return new RequestItem(
      {'type' : 'FETCH_PEOPLE',
        'idSpec' : idSpec,
        'profileDetail': opt_params['profileDetail'],
        'sortOrder': opt_params['sortOrder'] || 'topFriends',
        'filter': opt_params['filter'] || 'all',
        'first': opt_params['first'] || 0,
        'max': opt_params['max'] || 20},
      function(rawJson) {
        var jsonPeople = rawJson['items'];
        var people = [];
        for (var i = 0; i < jsonPeople.length; i++) {
          people.push(me.createPersonFromJson(jsonPeople[i]));
        }
        return new opensocial.Collection(people, rawJson['offset'],
            rawJson['totalSize']);
      });
};

JsonContainer.prototype.createPersonFromJson = function(serverJson) {
  return new JsonPerson(serverJson);
}

JsonContainer.prototype.newFetchPersonAppDataRequest = function(idSpec, keys) {
  return new RequestItem({'type' : 'FETCH_PERSON_APP_DATA', 'idSpec' : idSpec,
      'keys' : keys},
      function (appData) {
        return gadgets.util.escape(appData, true);
      });
};

JsonContainer.prototype.newUpdatePersonAppDataRequest = function(id, key,
    value) {
  return new RequestItem({'type' : 'UPDATE_PERSON_APP_DATA', 'idSpec' : id,
    'key' : key, 'value' : value});
};

JsonContainer.prototype.newFetchActivitiesRequest = function(idSpec,
    opt_params) {
  return new RequestItem({'type' : 'FETCH_ACTIVITIES', 'idSpec' : idSpec},
      function(rawJson) {
        var activities = [];
        for (var i = 0; i < rawJson.length; i++) {
          activities.push(new JsonActivity(rawJson[i]));
        }
        return {'activities' : new opensocial.Collection(activities)};
      });
};

JsonContainer.prototype.newCreateActivityRequest = function(idSpec,
    activity) {
  return new RequestItem({'type' : 'CREATE_ACTIVITY', 'idSpec' : idSpec,
    'activity' : activity});
};

RequestItem = function(jsonParams, processData) {
  this.jsonParams = jsonParams;
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