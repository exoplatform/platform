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
 * @fileoverview Interface for containers of people functionality.
 */


/**
 * Base interface for all containers.
 *
 * @constructor
 * @private
 */
opensocial.Container = function() {};


/**
 * The container instance.
 *
 * @type Container
 * @private
 */
opensocial.Container.container_ = null;


/**
 * Set the current container object.
 *
 * @param {opensocial.Container} container The container
 * @private
 */
opensocial.Container.setContainer = function(container) {
  opensocial.Container.container_ = container;
};


/**
 * Get the current container object.
 *
 * @return {opensocial.Container} container The current container
 * @private
 */
opensocial.Container.get = function() {
  return opensocial.Container.container_;
};


/**
 * Gets the current environment for this gadget. You can use the environment to
 * query things like what profile fields and surfaces are supported by this
 * container, what parameters were passed to the current gadget and so forth.
 *
 * @return {opensocial.Environment} The current environment
 *
 * @private
 */
opensocial.Container.prototype.getEnvironment = function() {};

/**
 * Requests the container to send a specific message to the specified users. If
 * the container does not support this method the callback will be called with a
 * opensocial.ResponseItem. The response item will have its error code set to
 * NOT_IMPLEMENTED.
 *
 * @param {Array.&lt;String&gt; | String} recipients An ID, array of IDs, or a
 *     group reference; the supported keys are VIEWER, OWNER, VIEWER_FRIENDS,
 *    OWNER_FRIENDS, or a single ID within one of those groups
 * @param {opensocial.Message} message The message to send to the specified
 *     users.
 * @param {Function} opt_callback The function to call once the request has been
 *    processed; either this callback will be called or the gadget will be
 *    reloaded from scratch. This function will be passed one parameter, an
 *    opensocial.ResponseItem. The error code will be set to reflect whether
 *    there were any problems with the request. If there was no error, the
 *    message was sent. If there was an error, you can use the response item's
 *    getErrorCode method to determine how to proceed. The data on the response
 *    item will not be set.
 *
 * @member opensocial
 * @private
 */
opensocial.Container.prototype.requestSendMessage = function(recipients,
    message, opt_callback) {
  if (opt_callback) {
    opt_callback(new opensocial.ResponseItem(
        null, null, opensocial.ResponseItem.Error.NOT_IMPLEMENTED, null));
  }
};


/**
 * Requests the container to share this gadget with the specified users. If the
 * container does not support this method the callback will be called with a
 * opensocial.ResponseItem. The response item will have its error code set to
 * NOT_IMPLEMENTED.
 *
 * @param {Array.&lt;String&gt; | String} recipients An ID, array of IDs, or a
 *     group reference; the supported keys are VIEWER, OWNER, VIEWER_FRIENDS,
 *    OWNER_FRIENDS, or a single ID within one of those groups
 * @param {opensocial.Message} reason The reason the user wants the gadget to
 *     share itself. This reason can be used by the container when prompting the
 *     user for permission to share the app. It may also be ignored.
 * @param {Function} opt_callback The function to call once the request has been
 *    processed; either this callback will be called or the gadget will be
 *    reloaded from scratch. This function will be passed one parameter, an
 *    opensocial.ResponseItem. The error code will be set to reflect whether
 *    there were any problems with the request. If there was no error, the
 *    sharing request was sent. If there was an error, you can use the response
 *    item's getErrorCode method to determine how to proceed. The data on the
 *    response item will not be set.
 *
 * @member opensocial
 * @private
 */
opensocial.Container.prototype.requestShareApp = function(recipients, reason,
    opt_callback) {
  if (opt_callback) {
    opt_callback(new opensocial.ResponseItem(
        null, null, opensocial.ResponseItem.Error.NOT_IMPLEMENTED, null));
  }
};


/**
 * Request for the container to make the specified person not a friend.
 *
 * Note: If this is the first activity that has been created for the user and
 * the request is marked as HIGH priority then this call may open a user flow
 * and navigate away from your gadget.
 *
 * @param {Activity} activity The activity to create. The only required field is
 *     title.
 * @param {CreateActivityPriority} priority The priority for this request.
 * @param {Function} opt_callback Function to call once the request has been
 *    processed.
 * @private
 */
opensocial.Container.prototype.requestCreateActivity = function(activity,
    priority, opt_callback) {
  if (opt_callback) {
    opt_callback(new opensocial.ResponseItem(
        null, null, opensocial.ResponseItem.Error.NOT_IMPLEMENTED, null));
  }
};


/**
 * Returns whether the current gadget has access to the specified
 * permission.
 *
 * @param {opensocial.Permission | String} permission The permission
 * @return {Boolean} Whether the gadget has access for the permission.
 *
 * @private
 */
opensocial.Container.prototype.hasPermission = function(permission) {
  return false;
};


/**
 * Requests the user grants access to the specified permissions.
 *
 * @param {Array.<opensocial.Permission>} permissions The permissions to request
 *    access to from the viewer
 * @param {String} reason Will be displayed to the user as the reason why these
 *    permissions are needed.
 * @param {Function} opt_callback The function to call once the request has been
 *    processed. This callback will either be called or the gadget will be
 *    reloaded from scratch
 *
 * @private
 */
opensocial.Container.prototype.requestPermission = function(permissions, reason,
    opt_callback) {
  if (opt_callback) {
    opt_callback(new opensocial.ResponseItem(
        null, null, opensocial.ResponseItem.Error.NOT_IMPLEMENTED, null));
  }
};


/**
 * Calls the callback function with a dataResponse object containing the data
 * asked for in the dataRequest object.
 *
 * @param {opensocial.DataRequest} dataRequest Specifies which data to get from
 *    the server
 * @param {Function} callback Function to call after the data is fetched
 * @private
 */
opensocial.Container.prototype.requestData = function(dataRequest, callback) {};


/**
 * Request a profile for the specified person id.
 * When processed, returns a Person object.
 *
 * @param {String} id The id of the person to fetch. Can also be standard
 *    person IDs of VIEWER and OWNER.
 * @param {Map.<opensocial.DataRequest.PeopleRequestFields, Object>} opt_params
 *    Additional params to pass to the request. This request supports
 *    PROFILE_DETAILS.
 * @return {Object} a request object
 * @private
 */
opensocial.Container.prototype.newFetchPersonRequest = function(id,
    opt_params) {};


/**
 * Used to request friends from the server.
 * When processed, returns a Collection&lt;Person&gt; object.
 *
 * @param {Array.<String> | String} idSpec An id, array of ids, or a group
 *    reference used to specify which people to fetch
 * @param {Map.<opensocial.DataRequest.PeopleRequestFields, Object>} opt_params
 *    Additional params to pass to the request. This request supports
 *    PROFILE_DETAILS, SORT_ORDER, FILTER, FIRST, and MAX.
 * @return {Object} a request object
 * @private
 */
opensocial.Container.prototype.newFetchPeopleRequest = function(idSpec,
    opt_params) {};


/**
 * Used to request app data for the given people.
 * When processed, returns a Map&lt;person id, Map&lt;String, String&gt;&gt;
 * object.
 *
 * @param {Array.<String> | String} idSpec An ID, array of IDs, or a group
 *    reference; the supported keys are VIEWER, OWNER, VIEWER_FRIENDS,
 *    OWNER_FRIENDS, or a single ID within one of those groups
 * @param {Array.<String> | String} keys The keys you want data for. This
 *     can be an array of key names, a single key name, or "*" to mean
 *     "all keys".
 * @return {Object} a request object
 * @private
 */
opensocial.Container.prototype.newFetchPersonAppDataRequest = function(idSpec,
    keys) {};


/**
 * Used to request an update of an app field for the given person.
 * When processed, does not return any data.
 *
 * @param {String} id The id of the person to update. (Right now only the
 *    special VIEWER id is allowed.)
 * @param {String} key The name of the key
 * @param {String} value The value
 * @return {Object} a request object
 * @private
 */
opensocial.Container.prototype.newUpdatePersonAppDataRequest = function(id,
    key, value) {};


/**
 * Used to request an activity stream from the server.
 *
 * When processed, returns an object whose "activities" property is a
 * Collection&lt;Activity&gt; object.
 *
 * @param {Array.<String> | String} idSpec An ID, array of IDs, or a group
 *    reference used to specify which people's activities to fetch; the
 *    supported keys are VIEWER, OWNER, VIEWER_FRIENDS, OWNER_FRIENDS, or
 *    a single ID within one of those groups.
 * @param {Map.<opensocial.DataRequest.ActivityRequestFields, Object>} opt_params
 *    Additional params to pass to the request.
 * @return {Object} a request object
 * @private
 */
opensocial.Container.prototype.newFetchActivitiesRequest = function(idSpec,
    opt_params) {};


/**
 * Creates a new collection with caja support if enabled.
 * @return {opensocial.Collection} the collection object
 * @private
 */
opensocial.Container.prototype.newCollection = function(array, opt_offset,
    opt_totalSize) {
  return new opensocial.Collection(array, opt_offset, opt_totalSize);
};


/**
 * Creates a new person with caja support if enabled.
 * @return {opensocial.Person} the person object
 * @private
 */
opensocial.Container.prototype.newPerson = function(opt_params, opt_isOwner,
    opt_isViewer) {
  return new opensocial.Person(opt_params, opt_isOwner, opt_isViewer);
};


/**
 * Get an activity object used to create activities on the server
 *
 * @param {opensocial.Activity.Template || String} title The title of an
 *     activity, a template is reccommended, but this field can also be a
 *     string.
 * @param {Map.<opensocial.Activity.Field, Object>} opt_params Any other
 *    fields that should be set on the activity object. All of the defined
 *    Fields are supported.
 * @return {opensocial.Activity} the activity object
 * @private
 */
opensocial.Container.prototype.newActivity = function(opt_params) {
  return new opensocial.Activity(opt_params);
};


/**
 * A media item associated with an activity. Represents images, movies, and
 * audio. Used when creating activities on the server
 *
 * @param {String} mimeType of the media
 * @param {String} url where the media can be found
 * @param {Map.<opensocial.Activity.MediaItem.Field, Object>} opt_params
 *    Any other fields that should be set on the media item object.
 *    All of the defined Fields are supported.
 *
 * @return {opensocial.Activity.MediaItem} the media item object
 * @private
 */
opensocial.Container.prototype.newActivityMediaItem = function(mimeType, url,
    opt_params) {
  return new opensocial.Activity.MediaItem(mimeType, url, opt_params);
};


/**
 * Creates a media item associated with an activity.
 * Represents images, movies, and audio.
 * Used when creating activities on the server.
 *
 * @param {String} body The main text of the message.
 * @param {Map.&lt;opensocial.Message.Field, Object&gt;} opt_params
 *    Any other fields that should be set on the message object;
 *    all of the defined
 *    <a href="opensocial.Message.Field.html">Field</a>s
 *    are supported
 *
 * @return {opensocial.Message} The new
 *    <a href="opensocial.Message.html">message</a> object
 * @member opensocial
 */
opensocial.Container.prototype.newMessage = function(body, opt_params) {
  return new opensocial.Message(body, opt_params);
};


/**
 * Creates a new response item with caja support if enabled.
 * @return {opensocial.ResponseItem} the response item object
 * @private
 */
opensocial.Container.prototype.newResponseItem = function(originalDataRequest,
    data, opt_errorCode, opt_errorMessage) {
  return new opensocial.ResponseItem(originalDataRequest, data, opt_errorCode,
      opt_errorMessage);
};


/**
 * Creates a new data response with caja support if enabled.
 * @return {opensocial.DataResponse} the data response object
 * @private
 */
opensocial.Container.prototype.newDataResponse = function(responseItems,
    opt_globalError) {
  return new opensocial.DataResponse(responseItems, opt_globalError);
};


/**
 * Get a data request object to use for sending and fetching data from the
 * server.
 *
 * @return {opensocial.DataRequest} the request object
 * @private
 */
opensocial.Container.prototype.newDataRequest = function() {
  return new opensocial.DataRequest();
};


/**
 * Get a new environment object.
 *
 * @return {opensocial.Environment} the environment object
 * @private
 */
opensocial.Container.prototype.newEnvironment = function(domain,
    supportedFields) {
  return new opensocial.Environment(domain, supportedFields);
};


/**
 * Returns true if the specified value is an array
 * @param {Object} val Variable to test
 * @return {boolean} Whether variable is an array
 * @private
 */
opensocial.Container.isArray = function(val) {
  return val instanceof Array;
};


/**
 * Caja Support.  See features/caja/*.js
 */
var caja;
var ___;
var attachDocumentStub;
// See features/caja/domita.js for uriCallback's contract.
var uriCallback = {
  rewrite: function rewrite(uri, mimeTypes) {
    uri = String(uri);
    // By default, only allow references to anchors.
    if (/^#/.test(uri)) {
      return '#' + encodeURIComponent(decodeURIComponent(uri.substring(1)));
    }
    // This callback can be replaced with one that passes the URL through
    // a proxy that checks the mimetype.
    return null;
  }
};

/**
 * Enable Caja support
 *
 * @type Container
 * @private
 */

// TODO(doll): As caja evolves this method should get a lot smaller
opensocial.Container.prototype.enableCaja = function() {

  ___ = window["___"];
  caja = window["caja"];
  attachDocumentStub = window["attachDocumentStub"];

  var outers = caja.copy(___.sharedOuters);

  // TODO(doll): We need to add caja allows for the gadgets namespace so that
  // this works properly. It does not belong in gadgets.
  var igOnload = window["_IG_RegisterOnloadHandler"];
  if (igOnload) {
    outers._IG_RegisterOnloadHandler = ___.simpleFunc(igOnload);
  }

  attachDocumentStub('pre-', uriCallback, outers);

  // Temporarily adding some gadgets calls to the opensocial code.
  // This should move into the gadgets js code very soon.
  outers.gadgets = gadgets;

  // Adding all of the available opensocial calls as defined in the spec
  outers.opensocial = opensocial;
  ___.all2(
      ___.allowCall, outers.opensocial,
      ['requestSendMessage', 'requestShareApp', 'requestCreateActivity',
       'hasPermission', 'requestPermission', 'getEnvironment', 'newDataRequest',
       'newActivity', 'newActivityMediaItem', 'newMessage']);

  ___.all2(
      ___.allowCall, opensocial.Collection.prototype,
      ['getById', 'size', 'each', 'asArray', 'getTotalSize', 'getOffset']);

  ___.all2(
      ___.allowCall, opensocial.Person.prototype,
      ['getId', 'getDisplayName', 'getField', 'isViewer', 'isOwner']);

  ___.allowCall(opensocial.Address.prototype, 'getField');
  ___.allowCall(opensocial.BodyType.prototype, 'getField');
  ___.allowCall(opensocial.Email.prototype, 'getField');
  ___.allowCall(opensocial.Name.prototype, 'getField');
  ___.allowCall(opensocial.Organization.prototype, 'getField');
  ___.allowCall(opensocial.Phone.prototype, 'getField');
  ___.allowCall(opensocial.Url.prototype, 'getField');

  ___.allowCall(opensocial.Activity.prototype, 'getId');
  ___.allowCall(opensocial.Activity.prototype, 'getField');

  ___.allowCall(opensocial.Activity.MediaItem.prototype, 'getField');

  ___.allowCall(opensocial.ResponseItem.prototype, 'hadError');
  ___.allowCall(opensocial.ResponseItem.prototype, 'getError');
  ___.allowCall(opensocial.ResponseItem.prototype, 'getOriginalDataRequest');
  ___.allowCall(opensocial.ResponseItem.prototype, 'getData');

  ___.allowCall(opensocial.DataResponse.prototype, 'hadError');
  ___.allowCall(opensocial.DataResponse.prototype, 'get');

  ___.all2(
      ___.allowCall, opensocial.DataRequest.prototype,
      ['getRequestObjects', 'add', 'send', 'newFetchPersonRequest',
       'newFetchPeopleRequest', 'newFetchPersonAppDataRequest',
       'newUpdatePersonAppDataRequest', 'newFetchActivitiesRequest']);

  ___.allowCall(opensocial.Environment.prototype, 'getDomain');
  ___.allowCall(opensocial.Environment.prototype, 'supportsField');

  ___.allowCall(opensocial.Enum.prototype, 'getKey');
  ___.allowCall(opensocial.Enum.prototype, 'getDisplayValue');

  ___.allowCall(opensocial.Message.prototype, 'getField');
  ___.allowCall(opensocial.Message.prototype, 'setField');

  var moduleHandler = ___.freeze({
    getOuters: ___.simpleFunc(function() { return outers; }),
    handle: ___.simpleFunc(function(newModule) { newModule(outers); })
  });

  ___.setNewModuleHandler(moduleHandler);
};
