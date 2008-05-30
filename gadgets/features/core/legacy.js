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

 // All functions in this file should be treated as deprecated legacy routines.
 // Gadget authors are explicitly discouraged from using any of them.

var JSON = gadgets.json;
var _IG_Prefs = gadgets.Prefs;

// Yes, these technically modifiy gadget.Prefs as well. Unfortunately,
// simply setting IG_Prefs.prototype to a new gadgets.Prefs object means
// that we'd have to duplicate the gadgets.Prefs constructor.
_IG_Prefs._parseURL = gadgets.Prefs.parseUrl;

function _IG_Fetch_wrapper(callback, obj) {
  callback(obj.data);
}

function _IG_FetchContent(url, callback, opt_params) {
  var params = opt_params || {};
  // this is really the only legacy parameter documented
  // at http://code.google.com/apis/gadgets/docs/remote-content.html#Params
  if (params.refreshInterval) {
    params['REFRESH_INTERVAL'] = params.refreshInterval;
  } else {
    params['REFRESH_INTERVAL'] = 3600;
  }
  var cb = gadgets.util.makeClosure(null, _IG_Fetch_wrapper, callback);
  gadgets.io.makeRequest(url, cb, params);
}

function _IG_FetchXmlContent(url, callback, opt_params) {
  var params = opt_params || {};
  if (params.refreshInterval) {
    params['REFRESH_INTERVAL'] = params.refreshInterval;
  } else {
    params['REFRESH_INTERVAL'] = 3600;
  }
  params.CONTENT_TYPE = "DOM";
  var cb = gadgets.util.makeClosure(null, _IG_Fetch_wrapper, callback);
  gadgets.io.makeRequest(url, cb, params);
}

// TODO: The server doesn't actually support FEED. Fix this!
function _IG_FetchFeedAsJSON(url, callback, numItems, getDescriptions,
                             opt_params) {
  var params = opt_params || {};
  params.CONTENT_TYPE = "FEED";
  params.NUM_ENTRIES = numItems;
  params.GET_SUMMARIES = getDescriptions;
  gadgets.io.makeRequest(url, callback, params);
}

function _IG_GetCachedUrl(url) {
  return gadgets.io.getProxyUrl(url);
}
function _IG_GetImageUrl(url) {
  return gadgets.io.getProxyUrl(url);
}

function _IG_RegisterOnloadHandler(callback) {
  gadgets.util.registerOnLoadHandler(callback);
}

// _IG_Callback takes the arguments in the scope the callback is executed and
// places them first in the argument array. MakeClosure takes the arguments
// from the scope at callback construction and pushes them first in the array
function _IG_Callback(handler_func, var_args) {
  var orig_args = arguments;
  return function() {
    var combined_args = Array.prototype.slice.call(arguments);
    // call the handler with all args combined
    handler_func.apply(null,
      combined_args.concat(Array.prototype.slice.call(orig_args, 1)));
  };
}

var _args = gadgets.util.getUrlParameters;

/**
 * Fetches an object by document id.
 *
 * @param {String | Object} el The element you wish to fetch. You may pass
 *     an object in which allows this to be called regardless of whether or
 *     not the type of the input is known.
 * @return {HTMLElement} The element, if it exists in the document, or null.
 */
function _gel(el) {
  return document.getElementById ? document.getElementById(el) : null;
}

/**
 * Fetches elements by tag name.
 * This is functionally identical to document.getElementsByTagName()
 *
 * @param {String} tag The tag to match elements against.
 * @return {Array.<HTMLElement>} All elements of this tag type.
 */
function _gelstn(tag) {
  if (tag === "*" && document.all) {
    return document.all;
  }
  return document.getElementsByTagName ?
         document.getElementsByTagName(tag) : [];
}

/**
 * Fetches elements with ids matching a given regular expression.
 *
 * @param {tagName} tag The tag to match elements against.
 * @param {RegEx} regex The expression to match.
 * @return {Array.<HTMLElement>} All elements of this tag type that match
 *     regex.
 */
function _gelsbyregex(tagName, regex) {
  var matchingTags = _gelstn(tagName);
  var matchingRegex = [];
  for (var i = 0, j = matchingTags.length; i < j; ++i) {
    if (regex.test(matchingTags[i].id)) {
      matchingRegex.push(matchingTags[i]);
    }
  }
  return matchingRegex;
}

/**
 * URI escapes the given string.
 * @param {String} str The string to escape.
 * @return {String} The escaped string.
 */
function _esc(str) {
  return window.encodeURIComponent ? encodeURIComponent(str) : escape(str);
}

/**
 * URI unescapes the given string.
 * @param {String} str The string to unescape.
 * @return {String} The unescaped string.
 */
function _unesc(str) {
  return window.decodeURIComponent ? decodeURIComponent(str) : unescape(str);
}

/**
 * Encodes HTML entities such as <, " and >.
 *
 * @param {String} str The string to escape.
 * @return The escaped string.
 */
function _hesc(str) {
  return gadgets.util.escapeString(str);
}

/**
 * Removes HTML tags from the given input string.
 *
 * @param {String} str The string to strip.
 * @return The stripped string.
 */
function _striptags(str) {
  return str.replace(/<\/?[^>]+>/g, "");
}

/**
 * Trims leading & trailing whitespace from the given string.
 *
 * @param {String} str The string to trim.
 * @return {String} The trimmed string.
 */
function _trim(str) {
  return str.replace(/^\s+|\s+$/g, "");
}

/**
 * Toggles the given element between being shown and block-style display.
 *
 * @param {String | HTMLElement} el The element to toggle.
 */
function _toggle(el) {
  el = _gel(el);
  if (el !== null) {
    if (el.style.display.length === 0 || el.style.display === "block") {
      el.style.display = "none";
    } else if (el.style.display === "none") {
      el.style.display = "block";
    }
  }
}

/**
 * {Number} A counter used by uniqueId().
 */
var _global_legacy_uidCounter = 0;

/**
 * @return a unique number.
 */
function _uid() {
  return _global_legacy_uidCounter++;
}

/**
 * @param {Number} a
 * @param {Number} b
 * @return The lesser of a or b.
 */
function _min(a, b) {
  return (a < b ? a : b);
}

/**
 * @param {Number} a
 * @param {Number} b
 * @return The greater of a or b.
 */
function _max(a, b) {
  return (a > b ? a : b);
}

/**
 * @param {String} name
 * @param {Array.<String | Object>} sym
 */
function _exportSymbols(name, sym) {
  var obj = {};

  for (var i = 0, j = sym.length; i < j; i += 2) {
    obj[sym[i]] = sym[i + 1];
  }
  var parts = name.split(".");
  var attach = window;
  for (var k = 0, l = parts.length - 1; k < l; ++k) {
    var tmp = {};
    attach[parts[k]] = tmp;
    attach = tmp;
  }
  attach[parts[parts.length - 1]] = obj;
}
