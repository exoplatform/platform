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
 * Base class for sending a JSON request for people data.
 *
 * @param {Object} opt_params Key-value pairs of request parameters
 * @param {String} opt_path Path of data request URL relative to base
 *   path on server. Defaults to /socialdata
 * @param {Function} opt_callback Function to call when done.
 *   Will be called with the JSON data as the only parameter.
 * @constructor
 */
BatchRequest = function(path, jsonText, opt_callback, opt_params) {
  this.params_ = opt_params || {};
  this.params_['request'] = jsonText;

  this.path_ = path;
  this.callback_ = opt_callback;
};

BatchRequest.prototype.send = function() {
  // TODO: This will likely grow to be a lot more complicated
  var makeRequestParams = {
    "CONTENT_TYPE" : "JSON",
    "METHOD" : "POST",
    "AUTHORIZATION" : "SIGNED",
    "POST_DATA" : gadgets.io.encodeValues(this.params_)};

  gadgets.io.makeNonProxiedRequest(this.path_, this.callback_,
      makeRequestParams);
};