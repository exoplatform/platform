/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/**
 * Base interface for json based person objects.
 *
 * @private
 * @constructor
 */
JsonPerson = function(opt_params) {
  opt_params = opt_params || {};

  // TODO: doesn't handle drinker, smoker, or gender yet
  JsonPerson.constructObject(opt_params, "bodyType", opensocial.BodyType);
  JsonPerson.constructObject(opt_params, "currentLocation", opensocial.Address);
  JsonPerson.constructObject(opt_params, "dateOfBirth", Date);
  JsonPerson.constructObject(opt_params, "name", opensocial.Name);
  JsonPerson.constructObject(opt_params, "profileSong", opensocial.Url);
  JsonPerson.constructObject(opt_params, "profileVideo", opensocial.Url);

  JsonPerson.constructArrayObject(opt_params, "addresses", opensocial.Address);
  JsonPerson.constructArrayObject(opt_params, "emails", opensocial.Email);
  JsonPerson.constructArrayObject(opt_params, "jobs", opensocial.Organization);
  JsonPerson.constructArrayObject(opt_params, "phoneNumbers", opensocial.Phone);
  JsonPerson.constructArrayObject(opt_params, "schools",
      opensocial.Organization);
  JsonPerson.constructArrayObject(opt_params, "urls", opensocial.Url);

  JsonPerson.constructEnum(opt_params, "gender");
  JsonPerson.constructEnum(opt_params, "smoker");
  JsonPerson.constructEnum(opt_params, "drinker");

  opensocial.Person.call(this, opt_params, opt_params['isOwner'],
      opt_params['isViewer']);
};
JsonPerson.inherits(opensocial.Person);

// Converts the fieldName into an instance of a opensocial.Enum
JsonPerson.constructEnum = function(map, fieldName) {
  var fieldValue = map[fieldName];
  if (fieldValue) {
    map[fieldName] = new opensocial.Enum(fieldValue.key, fieldValue.displayValue);
  }
}

// Converts the fieldName into an instance of the specified object
JsonPerson.constructObject = function(map, fieldName, className) {
  var fieldValue = map[fieldName];
  if (fieldValue) {
    map[fieldName] = new className(fieldValue);
  }
}

JsonPerson.constructArrayObject = function(map, fieldName, className) {
  var fieldValue = map[fieldName];
  if (fieldValue) {
    for (var i = 0; i < fieldValue.length; i++) {
      fieldValue[i] = new className(fieldValue[i]);
    }
  }
}
