/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

/**
 * @fileoverview This library provides functions for navigating to and dealing
 *     with views of the current gadget.
 */

var gadgets = gadgets || {};

/**
 * Implements the gadgets.views API spec. See
 * http://code.google.com/apis/gadgets/docs/reference/gadgets.views.html
 */
gadgets.views = function() {

  /**
   * Reference to the current view object.
   */
  var currentView = null;

  /**
   * Map of all supported views for this container.
   */
  var supportedViews = {};

  /**
   * Map of parameters passed to the current request.
   */
  var params = {};

  /**
   * Initializes views. Assumes that the current view is the "view"
   * url parameter (or default if "view" isn't supported), and that
   * all view parameters are in the form view-<name>
   * TODO: Use unified configuration when it becomes available.
   *
   */
  function init(config) {
    var supported = config["views"];

    for (var s in supported) if (supported.hasOwnProperty(s)) {
      var obj = supported[s];
      if (!obj) {
        continue;
      }
      supportedViews[s] = new gadgets.views.View(s, obj.isOnlyVisible);
      var aliases = obj.aliases || [];
      for (var i = 0, alias; alias = aliases[i]; ++i) {
        supportedViews[alias] = new gadgets.views.View(s, obj.isOnlyVisible);
      }
    }

    var urlParams = gadgets.util.getUrlParameters();
    // View parameters are passed as a single parameter.
    if (urlParams["view-params"]) {
      var tmpParams = gadgets.json.parse(
          decodeURIComponent(urlParams["view-params"]));
      if (tmpParams) {
        params = tmpParams;
        for (var p in params) if (params.hasOwnProperty(p)) {
          params[p] = gadgets.util.escapeString(params[p]);
        }
      }
    }
    currentView = supportedViews[urlParams.view] || supportedViews["default"];
  }

  gadgets.config.register("views", null, init);

  return {
    /**
     * Attempts to navigate to this gadget in a different view. If the container
     * supports parameters will pass the optional parameters along to the gadget
     * in the new view.
     *
     * @param {gadgets.views.View} view The view to navigate to
     * @param {Map.&lt;String, String&gt;} opt_params Parameters to pass to the
     *     gadget after it has been navigated to on the surface
     */
    requestNavigateTo : function(view, opt_params) {
      gadgets.rpc.call(
          null, "requestNavigateTo", null, view.getName(), opt_params);
    },

    /**
     * Returns the current view.
     *
     * @return {gadgets.views.View} The current view
     */
    getCurrentView : function() {
      return currentView;
    },

    /**
     * Returns a map of all the supported views. Keys each gadgets.view.View by
     * its name.
     *
     * @return {Map&lt;gadgets.views.ViewType | String, gadgets.views.View&gt;}
     *   All supported views, keyed by their name attribute.
     */
    getSupportedViews : function() {
      return supportedViews;
    },

    /**
     * Returns the parameters passed into this gadget for this view. Does not
     * include all url parameters, only the ones passed into
     * gadgets.views.requestNavigateTo
     *
     * @return {Map.&lt;String, String&gt;} The parameter map
     */
    getParams : function() {
      return params;
    }
  };
}();

gadgets.views.View = function(name, opt_isOnlyVisible) {
  this.name_ = name;
  this.isOnlyVisible_ = !!opt_isOnlyVisible;
};

/**
 * @return {String} The view name.
 */
gadgets.views.View.prototype.getName = function() {
  return this.name_;
};

/**
 * @return {Boolean} True if this is the only visible gadget on the page.
 */
gadgets.views.View.prototype.isOnlyVisibleGadget = function() {
  return this.isOnlyVisible_;
};

gadgets.views.ViewType = gadgets.util.makeEnum([
  "FULL_PAGE", "DASHBOARD", "POPUP"
]);
