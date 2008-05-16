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

var gadgets = gadgets || {};

/**
 * IFrame pool
 */
gadgets.IFramePool_ = function() {
  this.pool_ = [];
};

/**
 * Returns a newly created IFRAME with the locked state as specified
 * @param {Boolean} locked whether the created IFRAME is locked by default
 * @returns {HTMLElement} the created IFRAME element
 * @private
 */
gadgets.IFramePool_.prototype.createIFrame_ = function(locked) {
  var div = document.createElement("DIV");

  // MSIE will reliably trigger an IFRAME onload event if the onload is defined
  // inlined but not if it is defined via JS with element.onload = func;
  // We create it within a DIV but eventually is moved directly into doc.body.
  div.innerHTML = "<iframe onload='this.pool_locked=false'></iframe>";

  var iframe = div.getElementsByTagName("IFRAME")[0];
  iframe.style.visibility = 'hidden';
  iframe.style.width = iframe.style.height = '0px';
  iframe.style.border = '0px';
  iframe.style.position = 'absolute';

  iframe.pool_locked = locked;
  this.pool_[this.pool_.length] = iframe;

  // The div was only used to create the iframe. Now we disown and remove it.
  div.removeChild(iframe);
  div = null;
  return iframe;
};

/**
 * Retrieves an available IFrame and sets the URL to 'url'
 * @param {String} url The URL the IFrame is pointed to
 */
gadgets.IFramePool_.prototype.iframe = function(url) {
  // Reject weird urls
  if (!url.match(/^http[s]?:\/\//)) {
    return;
  }
  // We wrap this code in a setTimeout call to avoid tying the UI up too much
  // with a series of repeated IFRAME creation calls.

  var ifp = this;
  window.setTimeout(function() {
    var iframe = null;

    // For MSIE, delete any iframes that are no longer being used. MSIE cannnot
    // re-use the IFRAME because it will 'click' when we set the SRC.
    // Other browsers scan the pool for a free iframe to re-use.
    for (var i = ifp.pool_.length - 1; i >= 0; i--) {
      var ifr = ifp.pool_[i];
      if (ifr && !ifr.pool_locked) {
        ifr.parentNode.removeChild(ifr);
        if (window.ActiveXObject) {  // MSIE
          ifr = null;
          ifp.pool_[i] = null;
          ifp.pool_.splice(i,1);  // Remove it from the array
        } else {
          ifr.pool_locked = true;
          iframe = ifr;
          break;
        }
      }
    }

    // If no iframe was found to re-use we create a new one
    iframe = iframe ? iframe : ifp.createIFrame_(true);
    iframe.src = url;

    // We append to the body after setting the src otherwise MSIE will 'click'
    document.body.appendChild(iframe);
  }, 0);
};

/**
 * Clears the pool and re-initializes it to empty
 */
gadgets.IFramePool_.prototype.clear = function() {
  for (var i = 0; i < this.pool_.length; i++) {
    this.pool_[i].onload = null;
    this.pool_[i] = null;
  }
  this.pool_.length = 0;
  this.pool_ = new Array();
};

/**
 * Inter-frame procedure call
 */
gadgets.ifpc_ = function() {

  var CALLBACK_ID_PREFIX_ = "cbid";
  var CALLBACK_SERVICE_NAME_ = "ifpc_callback";
  var iframePool_ = new gadgets.IFramePool_();
  var packetStore_ = {};
  var services_ = {};
  var callbacks_ = {};
  var callbackCounter_ = 0;
  var callCounter_ = 0;

  /**
   * Registers a new service and associates it with 'handler'
   * @param {String} name the id to used to identify this service when calling
   * @param {Function} handler function to handle incoming requests
   */
  function registerService(name, handler) {
    services_[name] = handler;
  }

  /**
   * Unregisters a registered service
   * @param {String} name the id used to identify the service when calling
   */
  function unregisterService(name) {
    delete services_[name];
  }

  /**
   * dispatches the call
   * @param {String} iframe_id iframe ID to use for this request
   * @param {String} service_name service name
   * @param {Array} args_list array of arguments expected by this service
   * @param {String} remote_relay_url remote relay URL of the relay HTML page
   * @param {Function} callback callback function if a response is expected
   *        (can be null if no callback expected)
   * @param {String} local_relay_url local relay URL of the relay HTML page
   *        (can be null if callback is also null)
   */
  function call(iframe_id,
      service_name,
      args_list,
      remote_relay_url,
      callback,
      local_relay_url,
      opt_shouldThrowError) {
    // We prepend some other arguments that the processRequest
    // method is expecting and will shift off in reverse order
    // once all the packets have been received
    // First make a local copy of args_list
    args_list = args_list.slice(0);
    args_list.unshift(registerCallback_(callback));
    args_list.unshift(local_relay_url);
    args_list.unshift(service_name);
    args_list.unshift(iframe_id);

    // Figure out how much URL space is available for actual data.
    // MSIE puts a limit of 4095 total chars including the # data.
    // Other browsers have limits at least as large as 4095.
    var max_data_len = 4095 - remote_relay_url.length;
    // Because we encodeArgs twice we need to leave room for escape chars
    max_data_len = parseInt(max_data_len / 3, 10);

    if (typeof opt_shouldThrowError == "undefined") {
      opt_shouldThrowError = true;
    }

    // Format of each packet is:
    // #iframe_id&callId&num_packets&packet_num&block_of_data
    var data = encodeArgs_(args_list);
    var num_packets = parseInt(data.length / max_data_len, 10);
    if (data.length % max_data_len > 0) {
      num_packets += 1;
    }
    for (var i = 0; i < num_packets; i++) {
      var data_slice = data.substr(i*max_data_len, max_data_len);
      var packet = [iframe_id, callCounter_, num_packets, i,
          data_slice, opt_shouldThrowError];
      iframePool_.iframe(
          remote_relay_url + "#" + encodeArgs_(packet));
    }
    callCounter_++;
  }

  /**
   * Clears internal state.
   * Should be called from an unload handler to avoid memory leaks.
   */
  function clear() {
    services_ = {};
    callbacks_ = {};
    iframePool_.clear();
  }

  /**
   * Relays a request either from container to gadget, from gadget to container,
   * or from gadget to gadget.
   * @param {String} argsString encoded parameters
   */
  function relayRequest(argsString) {
    // Extract the iframe-id.
    var iframeId = decodeArgs_(argsString)[0];
    // Need to find the destination window to pass the request on to.
    // We are in an IFPC relay iframe within the source window.
    var win = null;
    // If container-to-gadget communication, the window corresponding to
    // 'iframeId' will be our sibling, ie. a child of the container page,
    // and this child is the window we need.
    try {
      win = window.parent.frames[iframeId];
    } catch (e) {
      // Doesn't look like container-to-gadget communication.
      // Just leave 'win' unset.
    }
    // If gadget-to-gadget communication, the window corresponding to
    // 'iframeId' will be a sibling of our outer page, and this is the
    // window we need.
    try {
      if (!win && window.parent.parent.frames[iframeId] != window.parent) {
        win = window.parent.parent.frames[iframeId];
      }
    } catch (e) {
      // Doesn't look like gadget-to-gadget communication.
      // Just leave 'win' unset.
    }
    if (!win) {
      // Wasn't container-to-gadget nor gadget-to-gadget communication.
      // If gadget-to-container communication, 'iframeId' will be our grandparent.
      win = window.parent.parent;
    }
    // Now that 'win' is set appropriately, pass on the request.
    // Obscure Firefox bug sometimes causes an exception when xmlhttp is
    // utilized in an IFPC handler. Wrapping our handleRequest calls
    // with a setTimeout in the target window's scope prevents this
    // exception.
    // See this Mozilla bug for more info:
    // https://bugzilla.mozilla.org/show_bug.cgi?id=249843
    // Also see this blogged account of the bug:
    // http://the-stickman.com/web-development/javascript/iframes-xmlhttprequest-bug-in-firefox
    var fn = function() {
      win.gadgets.ifpc_.handleRequest(argsString);
    };

    if (window.ActiveXObject) { // MSIE
      // call the relay synchronously in IE
      // this is required because the iframe (and its relay closure)
      // may otherwise be deleted/invalidated before this call is made
      fn();
    } else {
      // all other browsers call with timeout, particularly FF. See
      // above comment regarding FF bug for why it's done this way
      win.setTimeout(fn, 0);
    }
  }

  /**
   * Internal function that processes the request
   * @param {String} packet encoded parameters
   */
  function handleRequest(packet) {
    packet = decodeArgs_(packet);

    var iframeId = packet.shift();
    var callId = packet.shift();
    var numPackets = packet.shift();
    var packetNum = packet.shift();
    var data = packet.shift();
    var shouldThrowError = packet.shift();
    // If you see fit to add a parameter here, don't.
    // If you must, be sure to add it to the END of the list!
    // If you don't, lots of problems will occur in situations where
    // IFPC versions mismatch, because ordered arguments will no longer
    // match up, causing all manner of breakages and odd behavior.

    // We store incoming packets in the packet_store object.
    // The key is the iframeId + the unique callId.
    // The value is an array to hold all the packets for the request.
    // The elements in the array are a 2-element array: packetNum and data.
    // When all packets are received, we sort based on the packetNum and then
    // re-create the original data block before passing to the Service Handler.
    var key = iframeId + "_" + callId;
    if (!packetStore_[key]) packetStore_[key] = [];
    packetStore_[key].push([packetNum, data]);

    if (packetStore_[key].length == numPackets) {
      // All packets have been received
      packetStore_[key].sort(function(a,b){
          return parseInt(a[0], 10) - parseInt(b[0], 10);
          });

      data = "";
      for (var i = 0; i < numPackets; i++) {
        data += packetStore_[key][i][1];
      }
      // Clear this entry from the packet_store
      packetStore_[key] = null;

      var args = decodeArgs_(data);

      iframeId = args.shift();
      var serviceName = args.shift();
      var remote_relay_url = args.shift();
      var callbackId = args.shift();

      var handler = getServiceHandler_(serviceName);
      if (handler) {
        var args_list_result = handler.apply(null, args);
        if (isCallbackIdWellFormed_(callbackId)) {
          args_list_result.unshift(callbackId);
          call(iframeId,
              CALLBACK_SERVICE_NAME_,
              args_list_result,
              remote_relay_url,
              null,   // no callback from the callback
              "");    // no callback, no relay needed
        }
      } else if (shouldThrowError) {
        throw new Error("Service " + serviceName + " not registered.");
      }
    }
  }

  /**
   * Returns the service handler given a specific service name
   * @param {String} name service name
   * @returns {Function} service
   * @private
   */
  function getServiceHandler_(name) {
    if(services_.hasOwnProperty(name)) {
      return services_[name];
    } else {
      return null;
    }
  }

  /**
   * Registers a new callback
   * @param {Function} callback callback function
   * @returns {String} a callback ID to use with call()
   * @private
   */
  function registerCallback_(callback) {
    var callbackId = "";
    if (callback && typeof callback == "function") {
      callbackId = getNewCallbackId_();
      callbacks_[callbackId] = callback;
    }
    return callbackId;
  }

  /**
   * Unregisters an existing callback
   * @param {String} callback_id callback ID
   * @private
   */
  function unregisterCallback_(callback_id) {
    if (callbacks_.hasOwnProperty(callback_id)) {
      callbacks_[callback_id] = null;
    }
  }

  /**
   * Returns the callback given a specific callback id
   * @param {String} callback_id callback ID
   * @returns {Function|null} callback function
   * @private
   */
  function getCallback_(callback_id) {
    if (callback_id &&
        callbacks_.hasOwnProperty(callback_id)) {
      return callbacks_[callback_id];
    }
    return null;
  }

  /**
   * Gets a new callback ID
   * @returns {String} a callback ID string
   * @private
   */
  function getNewCallbackId_() {
    return CALLBACK_ID_PREFIX_ + (callbackCounter_++);
  }

  /**
   * Return the decoded arguments a a list. First element is the service name.
   * @param {String} argsString Encoded argument string
   * @returns {Array} decoded argument list
   * @private
   */
  function decodeArgs_(argsString) {
    var args = argsString.split('&');
    for(var i = 0; i < args.length; i++) {
      var arg = decodeURIComponent(args[i]);
      try {
        arg = gadgets.json.parse(arg);
      } catch (e) {
        // unexpected, but ok - treat as a string
      }
      args[i] = arg;
    }
    return args;
  }

  /**
   * Determines whether a callbackId is well-formed.
   * @param {String} callbackId callback ID
   * @returns {Boolean} whether the callbackId is well-formed
   * @private
   */
  function isCallbackIdWellFormed_(callbackId) {
    return (callbackId+"").indexOf(CALLBACK_ID_PREFIX_) === 0;
  }

  /**
   * Private handler for the built-in callback service
   * @param {String} callbackId callback ID
   * @private
   */
  function callbackServiceHandler_(callbackId) {
    var callback = getCallback_(callbackId);
    if (callback) {
      var args = [];
      for (var i = 1; i < arguments.length; i++) {
        args[args.length] = arguments[i];  // append the extra arguments
      }
      callback.apply(null, args);

      // Once the callback is triggered, we remove it.
      unregisterCallback_(callbackId);
    } else {
      throw new Error("Invalid callbackId");
    }
  }

  /**
   * Return the encoded argument string.
   * @param {Array} args list of arguments to encode
   * @returns {String} encoded argument string
   * @private
   */
  function encodeArgs_(args) {
    var argsEscaped = [];
    for(var i = 0; i < args.length; i++) {
      var arg = gadgets.json.stringify(args[i]);
      argsEscaped.push(encodeURIComponent(arg));
    }
    return argsEscaped.join('&');
  }

  // Register the built-in callback handler
  registerService(CALLBACK_SERVICE_NAME_, callbackServiceHandler_);

  // Public methods
  return {
    registerService: registerService,
    unregisterService: unregisterService,
    call: call,
    clear: clear,
    relayRequest: relayRequest,
    processRequest: relayRequest,
    handleRequest: handleRequest
  };

}();

// Alias for legacy code
var _IFPC = gadgets.ifpc_;

