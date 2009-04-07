/**
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
 * @fileoverview Open Gadget Container
 */
var gadgets = gadgets || {};
gadgets.error = {};
gadgets.error.SUBCLASS_RESPONSIBILITY = 'subclass responsibility';
gadgets.error.TO_BE_DONE = 'to be done';

gadgets.log = function(message) {
  if (window.console && console.log) {
    console.log(message);
  } else {
    var logEntry = document.createElement('div');
    logEntry.className = 'gadgets-log-entry';
    logEntry.innerHTML = message;
    document.body.appendChild(logEntry);
  }
};

/**
 * Calls an array of asynchronous functions and calls the continuation
 * function when all are done.
 * @param {Array} functions Array of asynchronous functions, each taking
 *     one argument that is the continuation function that handles the result
 *     That is, each function is something like the following:
 *     function(continuation) {
 *       // compute result asynchronously
 *       continuation(result);
 *     }
 * @param {Function} continuation Function to call when all results are in.  It
 *     is pass an array of all results of all functions
 * @param {Object} opt_this Optional object used as "this" when calling each
 *     function
 */
gadgets.callAsyncAndJoin = function(functions, continuation, opt_this) {
  var pending = functions.length;
  var results = [];
  for (var i = 0; i < functions.length; i++) {
    // we need a wrapper here because i changes and we need one index
    // variable per closure
    var wrapper = function(index) {
      functions[index].call(opt_this, function(result) {
        results[index] = result;
        if (--pending === 0) {
          continuation(results);
        }
      });
    };
    wrapper(i);
  }
};


// ----------
// Extensible

gadgets.Extensible = function() {
};

/**
 * Sets the dependencies.
 * @param {Object} dependencies Object whose properties are set on this
 *     container as dependencies
 */
gadgets.Extensible.prototype.setDependencies = function(dependencies) {
  for (var p in dependencies) {
    this[p] = dependencies[p];
  }
};

/**
 * Returns a dependency given its name.
 * @param {String} name Name of dependency
 * @return {Object} Dependency with that name or undefined if not found
 */
gadgets.Extensible.prototype.getDependencies = function(name) {
  return this[name];
};



// -------------
// UserPrefStore

/**
 * User preference store interface.
 * @constructor
 */
gadgets.UserPrefStore = function() {
};

/**
 * Gets all user preferences of a gadget.
 * @param {Object} gadget Gadget object
 * @return {Object} All user preference of given gadget
 */
gadgets.UserPrefStore.prototype.getPrefs = function(gadget) {
  throw Error(gadgets.error.SUBCLASS_RESPONSIBILITY);
};

/**
 * Saves user preferences of a gadget in the store.
 * @param {Object} gadget Gadget object
 * @param {Object} prefs User preferences
 */
gadgets.UserPrefStore.prototype.savePrefs = function(gadget) {
  throw Error(gadgets.error.SUBCLASS_RESPONSIBILITY);
};


// -------------
// DefaultUserPrefStore

/**
 * User preference store implementation.
 * TODO: Turn this into a real implementation that is production safe
 * @constructor
 */
gadgets.DefaultUserPrefStore = function() {
  gadgets.UserPrefStore.call(this);
};
gadgets.DefaultUserPrefStore.inherits(gadgets.UserPrefStore);

gadgets.DefaultUserPrefStore.prototype.getPrefs = function(gadget) { };

gadgets.DefaultUserPrefStore.prototype.savePrefs = function(gadget) { };


// -------------
// GadgetService

/**
 * Interface of service provided to gadgets for resizing gadgets,
 * setting title, etc.
 * @constructor
 */
gadgets.GadgetService = function() {
};

gadgets.GadgetService.prototype.setHeight = function(elementId, height) {
  throw Error(gadgets.error.SUBCLASS_RESPONSIBILITY);
};

gadgets.GadgetService.prototype.setTitle = function(gadget, title) {
  throw Error(gadgets.error.SUBCLASS_RESPONSIBILITY);
};

gadgets.GadgetService.prototype.setUserPref = function(id) {
  throw Error(gadgets.error.SUBCLASS_RESPONSIBILITY);
};


// ----------------
// IfrGadgetService

/**
 * Base implementation of GadgetService.
 * @constructor
 */
gadgets.IfrGadgetService = function() {
  gadgets.GadgetService.call(this);
  gadgets.rpc.register('resize_iframe', this.setHeight);
  gadgets.rpc.register('set_pref', this.setUserPref);
  gadgets.rpc.register('set_title', this.setTitle);
  gadgets.rpc.register('requestNavigateTo', this.requestNavigateTo);
};

gadgets.IfrGadgetService.inherits(gadgets.GadgetService);

gadgets.IfrGadgetService.prototype.setHeight = function(height) {
  if (height > gadgets.container.maxheight_) {
    height = gadgets.container.maxheight_;
  }
  var element = document.getElementById(this.f);
  if(element.tagName.toLowerCase() == "iframe") {
	  if(height <= 0) element.height = "auto" ;
    else element.height = height ;
  } else {
  	if(height <= 0) element.style.height = "auto" ;
    else element.style.height = height + 'px';
  }
};

gadgets.IfrGadgetService.prototype.setTitle = function(title) {
  var element = document.getElementById(this.f);
  element = eXo.core.DOMUtil.findAncestorByClass(element, "UIGadget");
  element = eXo.core.DOMUtil.findFirstDescendantByClass(element, "div", "GadgetTitle");

  if (element) {
    element.innerHTML = title.replace(/&/g, '&amp;').replace(/</g, '&lt;');
  }
};

/**
 * Sets one or more user preferences
 * @param {String} editToken
 * @param {String} name Name of user preference
 * @param {String} value Value of user preference
 * More names and values may follow
 */
gadgets.IfrGadgetService.prototype.setUserPref = function(editToken, name,
    value) {
  var id = gadgets.container.gadgetService.getGadgetIdFromModuleId(this.f);
  var gadget = gadgets.container.getGadget(id);
  var prefs = gadget.getUserPrefs();
  for (var i = 1, j = arguments.length; i < j; i += 2) {
    prefs[arguments[i]] = arguments[i + 1];
  }
  gadget.setUserPrefs(prefs);
};

/**
 * Navigates the page to a new url based on a gadgets requested view and
 * parameters.
 */
gadgets.IfrGadgetService.prototype.requestNavigateTo = function(view,
    opt_params) {
  var id = this.getGadgetIdFromModuleId(this.f);
  var url = this.getUrlForView(view);

  if (opt_params) {
    var paramStr = JSON.stringify(opt_params);
    if (paramStr.length > 0) {
      url += '&appParams=' + encodeURIComponent(paramStr);
    }
  }

  if (url && document.location.href.indexOf(url) == -1) {
    document.location.href = url;
  }
};

/**
 * This is a silly implementation that will need to be overriden by almost all
 * real containers.
 * TODO: Find a better default for this function
 *
 * @param view The view name to get the url for
 */
gadgets.IfrGadgetService.prototype.getUrlForView = function(
    view) {
  if (view === 'canvas') {
    return '/canvas';
  } else if (view === 'profile') {
    return '/profile';
  } else {
    return null;
  }
}

gadgets.IfrGadgetService.prototype.getGadgetIdFromModuleId = function(
    moduleId) {
  // Quick hack to extract the gadget id from module id
  return parseInt(moduleId.match(/_([0-9]+)$/)[1], 10);
};


// -------------
// LayoutManager

/**
 * Layout manager interface.
 * @constructor
 */
gadgets.LayoutManager = function() {
};

/**
 * Gets the HTML element that is the chrome of a gadget into which the content
 * of the gadget can be rendered.
 * @param {Object} gadget Gadget instance
 * @return {Object} HTML element that is the chrome for the given gadget
 */
gadgets.LayoutManager.prototype.getGadgetChrome = function(gadget) {
  throw Error(gadgets.error.SUBCLASS_RESPONSIBILITY);
};

// -------------------
// StaticLayoutManager

/**
 * Static layout manager where gadget ids have a 1:1 mapping to chrome ids.
 * @constructor
 */
gadgets.StaticLayoutManager = function() {
  gadgets.LayoutManager.call(this);
};

gadgets.StaticLayoutManager.inherits(gadgets.LayoutManager);

/**
 * Sets chrome ids, whose indexes are gadget instance ids (starting from 0).
 * @param {Array} gadgetIdToChromeIdMap Gadget id to chrome id map
 */
gadgets.StaticLayoutManager.prototype.setGadgetChromeIds =
    function(gadgetChromeIds) {
  this.gadgetChromeIds_ = gadgetChromeIds;
};

gadgets.StaticLayoutManager.prototype.getGadgetChrome = function(gadget) {
  var chromeId = this.gadgetChromeIds_[gadget.id];
  return chromeId ? document.getElementById(chromeId) : null;
};


// ----------------------
// FloatLeftLayoutManager

/**
 * FloatLeft layout manager where gadget ids have a 1:1 mapping to chrome ids.
 * @constructor
 * @param {String} layoutRootId Id of the element that is the parent of all
 *     gadgets.
 */
gadgets.FloatLeftLayoutManager = function(layoutRootId) {
  gadgets.LayoutManager.call(this);
  this.layoutRootId_ = layoutRootId;
};

gadgets.FloatLeftLayoutManager.inherits(gadgets.LayoutManager);

gadgets.FloatLeftLayoutManager.prototype.getGadgetChrome =
    function(gadget) {
  var layoutRoot = document.getElementById(this.layoutRootId_);
  if (layoutRoot) {
    var chrome = document.createElement('div');
    chrome.className = 'gadgets-gadget-chrome';
    chrome.style.cssFloat = 'left'
    layoutRoot.appendChild(chrome);
    return chrome;
  } else {
    return null;
  }
};


// ------
// Gadget

/**
 * Creates a new instance of gadget.  Optional parameters are set as instance
 * variables.
 * @constructor
 * @param {Object} params Parameters to set on gadget.  Common parameters:
 *    "specUrl": URL to gadget specification
 *    "private": Whether gadget spec is accessible only privately, which means
 *        browser can load it but not gadget server
 *    "spec": Gadget Specification in XML
 *    "viewParams": a javascript object containing attribute value pairs
 *        for this gadgets
 *    "secureToken": an encoded token that is passed on the URL hash
 *    "hashData": Query-string like data that will be added to the
 *        hash portion of the URL.
 *    "specVersion": a hash value used to add a v= param to allow for better caching
 *    "title": the default title to use for the title bar.
 *    "height": height of the gadget
 *    "width": width of the gadget
 *    "debug": send debug=1 to the gadget server, gets us uncompressed
 *        javascript
 */
gadgets.Gadget = function(params) {
  this.userPrefs_ = {};

  if (params) {
    for (var name in params)  if (params.hasOwnProperty(name)) {
      this[name] = params[name];
    }
  }
  if (!this.secureToken) {
    // Assume that the default security token implementation is
    // in use on the server.
    this.secureToken = 'root:john:appid:cont:url:0';
  }
};



gadgets.Gadget.prototype.getUserPrefs = function() {
  return this.userPrefs_;
};

gadgets.Gadget.prototype.setUserPrefs = function(userPrefs) {
  this.userPrefs_ = userPrefs;
  gadgets.container.userPrefStore.savePrefs(this);
};

gadgets.Gadget.prototype.getUserPref = function(name) {
  return this.userPrefs_[name];
};

gadgets.Gadget.prototype.setUserPref = function(name, value) {
  this.userPrefs_[name] = value;
  gadgets.container.userPrefStore.savePrefs(this);
};

gadgets.Gadget.prototype.render = function(chrome) {
  if (chrome) {
    this.getContent(function(content) {
      chrome.innerHTML = content;
    });
  }
};

gadgets.Gadget.prototype.getContent = function(continuation) {
  gadgets.callAsyncAndJoin([
      this.getTitleBarContent, this.getUserPrefsDialogContent,
      this.getMainContent], function(results) {
        continuation(results.join(''));
      }, this);
};

/**
 * Gets title bar content asynchronously or synchronously.
 * @param {Function} continutation Function that handles title bar content as
 *     the one and only argument
 */
gadgets.Gadget.prototype.getTitleBarContent = function(continuation) {
  throw Error(gadgets.error.SUBCLASS_RESPONSIBILITY);
};

/**
 * Gets user preferences dialog content asynchronously or synchronously.
 * @param {Function} continutation Function that handles user preferences
 *     content as the one and only argument
 */
gadgets.Gadget.prototype.getUserPrefsDialogContent = function(continuation) {
  throw Error(gadgets.error.SUBCLASS_RESPONSIBILITY);
};

/**
 * Gets gadget content asynchronously or synchronously.
 * @param {Function} continutation Function that handles gadget content as
 *     the one and only argument
 */
gadgets.Gadget.prototype.getMainContent = function(continuation) {
  throw Error(gadgets.error.SUBCLASS_RESPONSIBILITY);
};

/*
 * Gets additional parameters to append to the iframe url
 * Override this method if you need any custom params.
 */
gadgets.Gadget.prototype.getAdditionalParams = function() {
  return '';
}


// ---------
// IfrGadget

gadgets.IfrGadget = function(opt_params) {
  gadgets.Gadget.call(this, opt_params);
  this.serverBase_ = '/eXoGadgetServer/gadgets/' // default gadget server
};

gadgets.IfrGadget.inherits(gadgets.Gadget);

gadgets.IfrGadget.prototype.GADGET_IFRAME_PREFIX_ = 'remote_iframe_';

gadgets.IfrGadget.prototype.CONTAINER = 'default';

gadgets.IfrGadget.prototype.cssClassGadget = 'gadgets-gadget';
gadgets.IfrGadget.prototype.cssClassTitleBar = 'gadgets-gadget-title-bar';
gadgets.IfrGadget.prototype.cssClassTitle = 'gadgets-gadget-title';
gadgets.IfrGadget.prototype.cssClassTitleButtonBar =
    'gadgets-gadget-title-button-bar';
gadgets.IfrGadget.prototype.cssClassGadgetUserPrefsDialog =
    'gadgets-gadget-user-prefs-dialog';
gadgets.IfrGadget.prototype.cssClassGadgetUserPrefsDialogActionBar =
    'gadgets-gadget-user-prefs-dialog-action-bar';
gadgets.IfrGadget.prototype.cssClassTitleButton = 'gadgets-gadget-title-button';
gadgets.IfrGadget.prototype.cssClassGadgetContent = 'gadgets-gadget-content';
gadgets.IfrGadget.prototype.rpcToken = (0x7FFFFFFF * Math.random()) | 0;
gadgets.IfrGadget.prototype.rpcRelay = 'files/container/rpc_relay.html';

gadgets.IfrGadget.prototype.getTitleBarContent = function(continuation) {
	continuation('');
};

gadgets.IfrGadget.prototype.getUserPrefsDialogContent = function(continuation) {
  continuation('<div id="' + this.getUserPrefsDialogId() + '" class="' +
      this.cssClassGadgetUserPrefsDialog + '"></div>');
};

gadgets.IfrGadget.prototype.setServerBase = function(url) {
  this.serverBase_ = url;
};

gadgets.IfrGadget.prototype.getServerBase = function() {
  return this.serverBase_;
};

gadgets.IfrGadget.prototype.getMainContent = function(continuation) {
  var iframeId = this.getIframeId();
  gadgets.rpc.setRelayUrl(iframeId, this.serverBase_ + this.rpcRelay);
  gadgets.rpc.setAuthToken(iframeId, this.rpcToken);
  
  /*
   * TODO: tan.pham: Fix bug WEBOS-273, width of iframe to large
   * When width of gadget didn't specify, set width of iframe to auto.
   * In control workspace, we set width of iframe by css.
   */
  continuation('<div class="' + this.cssClassGadgetContent + '"><iframe id="' +
      iframeId + '" name="' + iframeId + '" class="' + this.cssClassGadget +
      '" src="' + this.getIframeUrl() +
      '" frameborder="no" scrolling="no"' +
      (this.height ? ' height="' + this.height + '"' : '') +
      (this.width ? ' width="' + this.width + '"' : '') +
      '></iframe></div>');
};

gadgets.IfrGadget.prototype.getIframeId = function() {
  return this.GADGET_IFRAME_PREFIX_ + this.id;
};

gadgets.IfrGadget.prototype.getUserPrefsDialogId = function() {
  return this.getIframeId() + '_userPrefsDialog';
};

gadgets.IfrGadget.prototype.getIframeUrl = function() {
  return this.serverBase_ + 'ifr?' +
      'container=' + this.CONTAINER +
      '&mid=' +  this.id +
      '&nocache=' + this.nocache +
      '&country=' + gadgets.container.country_ +
      '&lang=' + gadgets.container.language_ +
      '&view=' + (this.view || gadgets.container.view_) +
      (this.specVersion ? '&v=' + this.specVersion : '') +
      (gadgets.container.parentUrl_ ? '&parent=' + encodeURIComponent(gadgets.container.parentUrl_) : '') +
      (this.debug ? '&debug=1' : '') +
      this.getAdditionalParams() +
      this.getUserPrefsParams() +
      (this.secureToken ? '&st=' + encodeURIComponent(this.secureToken) : '') +
      '&url=' + encodeURIComponent(this.specUrl) +
      '#rpctoken=' + this.rpcToken +
      (this.viewParams ?
          '&view-params=' +  encodeURIComponent(JSON.stringify(this.viewParams)) : '') +
      (this.hashData ? '&' + this.hashData : '');
};

gadgets.IfrGadget.prototype.getUserPrefsParams = function() {
  var params = '';
  if (this.getUserPrefs()) {
    for(var name in this.getUserPrefs()) {
      var value = this.getUserPref(name);
      params += '&up_' + encodeURIComponent(name) + '=' +
          encodeURIComponent(value);
    }
  }
  return params;
}

gadgets.IfrGadget.prototype.handleToggle = function() {
  var gadgetIframe = document.getElementById(this.getIframeId());
  if (gadgetIframe) {
    var gadgetContent = gadgetIframe.parentNode;
    var display = gadgetContent.style.display;
    gadgetContent.style.display = display ? '' : 'none';
  }
};

gadgets.IfrGadget.prototype.handleOpenUserPrefsDialog = function() {
  if (this.userPrefsDialogContentLoaded) {
    this.showUserPrefsDialog();
  } else {
    var gadget = this;
    var igCallbackName = 'ig_callback_' + this.id;
    //window[igCallbackName] = function(userPrefsDialogContent) {
      gadget.userPrefsDialogContentLoaded = true;
      this.generateForm(gadget, this.getUserPrefsParams());
      //gadget.buildUserPrefsDialog(userPrefsDialogContent);
      gadget.showUserPrefsDialog();
    //};

    /*var script = document.createElement('script');
    script.src = 'http://gmodules.com/ig/gadgetsettings?mid=' + this.id +
        '&output=js' + this.getUserPrefsParams() +  '&url=' + this.specUrl;
    document.body.appendChild(script); */
  }
};

gadgets.IfrGadget.prototype.generateForm = function(gadget) {
    var prefs = gadget.metadata.userPrefs;
    var userPrefs = gadget.userPrefs_;
    var gadgetId = gadget.id;
    var parentEl = document.getElementById(this.getUserPrefsDialogId());
    var formEl = document.createElement("form");
    var prefix = "m_" + gadgetId + "_up_";

    var j = 0;
    for (var att in prefs) {
	    	//TODO: dang.tung not append when using list
				type = prefs[att].type;
				if(type == "list"|| type == "hidden") continue;
				// end
        var attEl = document.createElement("div");
        var labelEl = document.createElement("span");

        var elID = "m_" + gadgetId + '_' + j;

        labelEl.innerHTML = prefs[att].displayName + ": ";
        attEl.appendChild(labelEl);
        if (type == "enum") {
            var el = document.createElement("select");
            el.name = prefix + att;
            var values = prefs[att].orderedEnumValues;
            var userValue = userPrefs[att];

            for (var i = 0; i < values.length; i++) {
                var value = values[i];
                var optEl = document.createElement("option");
                theText = document.createTextNode(value.displayValue);
                optEl.appendChild(theText);
                optEl.setAttribute("value", value.value);
                if(userValue && value.value == userValue)
                    optEl.setAttribute("selected", "selected");  
                el.appendChild(optEl);
            }
            el.id = elID;
            attEl.appendChild(el);
        }
        else if (type == "string" || type == "number") {
            var el = document.createElement("input");
            el.name = prefix + att;
            el.id = elID;
            if (userPrefs[att]) {
                el.value = userPrefs[att];
            }
            attEl.appendChild(el);
        }
        formEl.appendChild(attEl);
        j++;
    }

		//TODO: dang.tung remove save and cancel button when doesn't have any pref
    //if(formEl.innerHTML == "") return;
    // end
    
    var numFieldsEl = document.createElement("input");
    numFieldsEl.type = "hidden";
    numFieldsEl.value = j;
    numFieldsEl.id = "m_" + gadgetId + "_numfields";
    formEl.appendChild(numFieldsEl);

    parentEl.appendChild(formEl);

    var saveEl = document.createElement("div");
    saveEl.className = this.cssClassGadgetUserPrefsDialogActionBar;
    saveEl.innerHTML = '<input type="button" value="'+eXo.gadget.UIGadget.SaveTitle+'" onclick="gadgets.container.getGadget(' +
      this.id +').handleSaveUserPrefs()"> <input type="button" value="'+eXo.gadget.UIGadget.CancelTitle+'" onclick="gadgets.container.getGadget(' +
      this.id +').handleCancelUserPrefs()">';
    parentEl.appendChild(saveEl);
    if(gadget.isdev) {
      //Are we in a portlet ? if not, we don't had  this code because we can't save the value
      var gadgetEl = document.getElementById("gadget_" + gadget.id) ;
      var portletFragment = eXo.core.DOMUtil.findAncestorByClass(gadgetEl, "PORTLET-FRAGMENT");

      if (portletFragment) {
        var devEl = document.createElement("div");
        devEl.className = "devToolbar";
        devEl.innerHTML = '<table>' +
                          '<tr><td>'+eXo.gadget.UIGadget.Cache+'</td><td><input type="checkbox"' + (gadget.nocache ? ' checked=""' : "") + ' onclick="gadgets.container.getGadget(' + this.id + ').setNoCache(checked)"/></td></tr>' +
                          '<tr><td>'+eXo.gadget.UIGadget.Debug+'</td><td><input type="checkbox"' + (gadget.debug ? ' checked=""' : "") + ' onclick="gadgets.container.getGadget(' + this.id + ').setDebug(checked)"/></td></tr>' +
                          '</table>';
        parentEl.appendChild(devEl);
      }
    }


};

gadgets.IfrGadget.prototype.buildUserPrefsDialog = function(content) {
  var userPrefsDialog = document.getElementById(this.getUserPrefsDialogId());
  userPrefsDialog.innerHTML = content +
      '<div class="' + this.cssClassGadgetUserPrefsDialogActionBar +
      '"><input type="button" value="Save" onclick="gadgets.container.getGadget(' +
      this.id +').handleSaveUserPrefs()"> <input type="button" value="Cancel" onclick="gadgets.container.getGadget(' +
      this.id +').handleCancelUserPrefs()"></div>';
  userPrefsDialog.childNodes[0].style.display = '';
};

gadgets.IfrGadget.prototype.showUserPrefsDialog = function(opt_show) {
  var userPrefsDialog = document.getElementById(this.getUserPrefsDialogId());
  userPrefsDialog.style.display = (opt_show || opt_show == undefined)
      ? '' : 'none';
}

gadgets.IfrGadget.prototype.hideUserPrefsDialog = function() {
  this.showUserPrefsDialog(false);
};

gadgets.IfrGadget.prototype.handleSaveUserPrefs = function() {
  this.hideUserPrefsDialog();

  var prefs = {};
  var numFields = document.getElementById('m_' + this.id +
      '_numfields').value;
  for (var i = 0; i < numFields; i++) {
    var input = document.getElementById('m_' + this.id + '_' + i);
    if (input.type != 'hidden') {
      var userPrefNamePrefix = 'm_' + this.id + '_up_';
      var userPrefName = input.name.substring(userPrefNamePrefix.length);
      var userPrefValue = input.value;
      prefs[userPrefName] = userPrefValue;
    }
  }
  this.setUserPrefs(prefs);
  this.refresh();
};

gadgets.IfrGadget.prototype.handleCancelUserPrefs = function() {
  this.hideUserPrefsDialog();
};

gadgets.IfrGadget.prototype.refresh = function() {
  var iframeId = this.getIframeId();
  document.getElementById(iframeId).src = this.getIframeUrl();
};


gadgets.IfrGadget.prototype.sendServerRequest = function(op, key, value) {
  var DOMUtil = eXo.core.DOMUtil;
  var gadget = document.getElementById("gadget_" + this.id) ;
  if(gadget != null ) {                    
    var portletFragment = DOMUtil.findAncestorByClass(gadget, "PORTLET-FRAGMENT");
    var uiGadget = gadget.parentNode;
    if (portletFragment != null) {
      var compId = portletFragment.parentNode.id;
      var href = eXo.env.server.portalBaseURL + "?portal:componentId=" + compId;
      href += "&portal:type=action&uicomponent=" + uiGadget.id;
      href += "&op=" + op;
      href += "&" + key + "=" + value;
      ajaxAsyncGetRequest(href,true);
    } else {
      alert("not managed yet");
    }
  }
}

gadgets.IfrGadget.prototype.setDebug = function(value) {
  this.sendServerRequest("SetDebug", "debug", (value ? "1" : "0"));
};

gadgets.IfrGadget.prototype.setNoCache = function(value) {
  this.sendServerRequest("SetNoCache", "nocache", (value ? "1" : "0"));
};


// ---------
// Container

/**
 * Container interface.
 * @constructor
 */
gadgets.Container = function() {
  this.gadgets_ = {};
  this.parentUrl_ = 'http://' + document.location.host;
  this.country_ = 'ALL';
  this.language_ = 'ALL';
  this.view_ = 'default';
  this.nocache_ = 1;

  // signed max int
  this.maxheight_ = 0x7FFFFFFF;
};

gadgets.Container.inherits(gadgets.Extensible);

/**
 * Known dependencies:
 *     gadgetClass: constructor to create a new gadget instance
 *     userPrefStore: instance of a subclass of gadgets.UserPrefStore
 *     gadgetService: instance of a subclass of gadgets.GadgetService
 *     layoutManager: instance of a subclass of gadgets.LayoutManager
 */

gadgets.Container.prototype.gadgetClass = gadgets.Gadget;

gadgets.Container.prototype.userPrefStore = new gadgets.DefaultUserPrefStore();

gadgets.Container.prototype.gadgetService = new gadgets.GadgetService();

gadgets.Container.prototype.layoutManager =
    new gadgets.StaticLayoutManager();

gadgets.Container.prototype.setParentUrl = function(url) {
  this.parentUrl_ = url;
};

gadgets.Container.prototype.setCountry = function(country) {
  this.country_ = country;
};

gadgets.Container.prototype.setNoCache = function(nocache) {
  this.nocache_ = nocache;
};

gadgets.Container.prototype.setLanguage = function(language) {
  this.language_ = language;
};

gadgets.Container.prototype.setView = function(view) {
  this.view_ = view;
};

gadgets.Container.prototype.setMaxHeight = function(maxheight) {
  this.maxheight_ = maxheight;
};

gadgets.Container.prototype.getGadgetKey_ = function(instanceId) {
  return 'gadget_' + instanceId;
};

gadgets.Container.prototype.getGadget = function(instanceId) {
  return this.gadgets_[this.getGadgetKey_(instanceId)];
};

gadgets.Container.prototype.createGadget = function(opt_params) {
  return new this.gadgetClass(opt_params);
};

gadgets.Container.prototype.addGadget = function(gadget) {
  gadget.id = this.getNextGadgetInstanceId();
  gadget.setUserPrefs(this.userPrefStore.getPrefs(gadget));
  this.gadgets_[this.getGadgetKey_(gadget.id)] = gadget;
};

gadgets.Container.prototype.addGadgets = function(gadgets) {
  for (var i = 0; i < gadgets.length; i++) {
    this.addGadget(gadgets[i]);
  }
};

/**
 * Renders all gadgets in the container.
 */
gadgets.Container.prototype.renderGadgets = function() {
  for (var key in this.gadgets_) {
    this.renderGadget(this.gadgets_[key]);
  }
};

/**
 * Renders a gadget.  Gadgets are rendered inside their chrome element.
 * @param {Object} gadget Gadget object
 */
gadgets.Container.prototype.renderGadget = function(gadget) {
  throw Error(gadgets.error.SUBCLASS_RESPONSIBILITY);
};

gadgets.Container.prototype.nextGadgetInstanceId_ = 0;

gadgets.Container.prototype.getNextGadgetInstanceId = function() {
  return this.nextGadgetInstanceId_++;
};

/**
 * Refresh all the gadgets in the container.
 */
gadgets.Container.prototype.refreshGadgets = function() {
  for (var key in this.gadgets_) {
    this.gadgets_[key].refresh();
  }
};


// ------------
// IfrContainer

/**
 * Container that renders gadget using ifr.
 * @constructor
 */
gadgets.IfrContainer = function() {
  gadgets.Container.call(this);
};

gadgets.IfrContainer.inherits(gadgets.Container);

gadgets.IfrContainer.prototype.gadgetClass = gadgets.IfrGadget;

gadgets.IfrContainer.prototype.gadgetService = new gadgets.IfrGadgetService();

gadgets.IfrContainer.prototype.setParentUrl = function(url) {
  if (!url.match(/^http[s]?:\/\//)) {
    url = document.location.href.match(/^[^?#]+\//)[0] + url;
  }

  this.parentUrl_ = url;
};

/**
 * Renders a gadget using ifr.
 * @param {Object} gadget Gadget object
 */
gadgets.IfrContainer.prototype.renderGadget = function(gadget) {
  var chrome = document.getElementById("gadget_" + gadget.id);

  gadget.render(chrome);
};

/**
 * Default container.
 */
gadgets.container = new gadgets.IfrContainer();