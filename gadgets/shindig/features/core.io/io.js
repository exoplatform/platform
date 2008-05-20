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

var gadgets = gadgets || {};

/**
 * @fileoverview Provides remote content retrieval facilities.
 *     Available to every gadget.
 */

/**
 * @static
 * @class Provides remote content retrieval functions.
 * @name gadgets.io
 */

gadgets.io = function() {
  /**
   * Holds configuration-related data such as proxy urls.
   */
  var config = {};

  /**
   * Holds state for OAuth.
   */
  var oauthState;

  /**
   * Internal facility to create an xhr request.
   */
  function makeXhr() {
    if (window.XMLHttpRequest) {
      return new XMLHttpRequest();
    } else if (window.ActiveXObject) {
      var x = new ActiveXObject("Msxml2.XMLHTTP");
      if (!x) {
        x = new ActiveXObject("Microsoft.XMLHTTP");
      }
      return x;
    }
  }

  /**
   * Checks the xobj for errors, may call the callback with an error response
   * if the error is fatal.
   *
   * @param {Object} xobj The XHR object to check
   * @param {Function} callback The callback to call if the error is fatal
   * @return true if the xobj is not ready to be processed
   */
  function hadError(xobj, callback) {
    if (xobj.readyState !== 4) {
      return true;
    }
    if (xobj.status !== 200) {
      // TODO Need to work on standardizing errors
      callback({errors : ["Error " + xobj.status]});
      return true;
    }
    return false;
  }

  /**
   * Handles non-proxied XHR callback processing.
   *
   * @param {String} url
   * @param {Function} callback
   * @param {Object} params
   * @param {Object} xobj
   */
  function processNonProxiedResponse(url, callback, params, xobj) {
    if (hadError(xobj, callback)) {
      return;
    }
    var data = {
      body: xobj.responseText
    };
    callback(transformResponseData(params, data));
  }

  var UNPARSEABLE_CRUFT = "throw 1; < don't be evil' >";

  /**
   * Handles XHR callback processing.
   *
   * @param {String} url
   * @param {Function} callback
   * @param {Object} params
   * @param {Object} xobj
   */
  function processResponse(url, callback, params, xobj) {
    if (hadError(xobj, callback)) {
      return;
    }
    var txt = xobj.responseText;
    // remove unparseable cruft.
    // TODO: really remove this by eliminating it. It's not any real security
    //    to begin with, and we can solve this problem by using post requests
    //    and / or passing the url in the http headers.
    txt = txt.substr(UNPARSEABLE_CRUFT.length);
    // We are using eval directly here because the outer response comes from a
    // trusted source, and json parsing is slow in IE.
    var data = eval("(" + txt + ")");
    data = data[url];
    // Save off any transient OAuth state the server wants back later.
    if (data.oauthState) {
      oauthState = data.oauthState;
    }
    callback(transformResponseData(params, data));
  }

  function transformResponseData(params, data) {
    var resp = {
     text: data.body,
     approvalUrl: data.approvalUrl,
     errors: []
    };
    switch (params.CONTENT_TYPE) {
      case "JSON":
        // Same as before, but specific to JSON (not FEED)
        resp.data = gadgets.json.parse(resp.text);
        if (!resp.data) {
          resp.errors.push("failed to parse JSON");
          resp.data = null;
        }
        break;
      case "FEED":
        if (!data.body) {
          resp.errors.push("failed to parse JSON");
          resp.data = null;
        } else {
          resp.data = data.body;
        }
        break;
      case "DOM":
        var dom;
        if (window.ActiveXObject) {
          dom = new ActiveXObject("Microsoft.XMLDOM");
          dom.async = false;
          dom.validateOnParse = false;
          dom.resolveExternals = false;
          if (!dom.loadXML(resp.text)) {
            resp.errors.push("failed to parse XML");
          } else {
            resp.data = dom;
          }
        } else {
          var parser = new DOMParser();
          dom = parser.parseFromString(resp.text, "text/xml");
          if ("parsererror" === dom.documentElement.nodeName) {
            resp.errors.push("failed to parse XML");
          } else {
            resp.data = dom;
          }
        }
        break;
      default:
        resp.data = resp.text;
        break;
    }

    return resp;
  }

  /**
   * Sends an XHR post or get request
   *
   * @param realUrl The url to fetch data from that was requested by the gadget
   * @param proxyUrl The url to proxy through
   * @param callback The function to call once the data is fetched
   * @param postData The data to post to the proxyUrl
   * @param params The params to use when processing the response
   * @param processResponseFunction The function that should process the
   *     response from the sever before calling the callback
   */
  function makeXhrRequest(realUrl, proxyUrl, callback, paramData, method,
      params, processResponseFunction) {
    var xhr = makeXhr();

    xhr.open(method, proxyUrl, true);
    if (callback) {
      xhr.onreadystatechange = gadgets.util.makeClosure(
          null, processResponseFunction, realUrl, callback, params, xhr);
    }
    if (paramData != null) {
        xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
        xhr.send(paramData);
    } else {
        xhr.send(null);
    }
  }



  /**
   * Satisfy a request with data that is prefetched as per the gadget Preload
   * directive. The preloader will only satisfy a request for a specific piece
   * of content once.
   *
   * @param postData The definition of the request to be executed by the proxy
   * @param params The params to use when processing the response
   * @param callback The function to call once the data is fetched
   * @return true if the request can be satisfied by the preloaded content
   *         false otherwise
   */
  function respondWithPreload(postData, params, callback) {
    if (gadgets.io.preloaded_ && gadgets.io.preloaded_[postData.url]) {
      var preload = gadgets.io.preloaded_[postData.url];
      if (postData.httpMethod == "GET") {
        delete gadgets.io.preloaded_[postData.url];
        if (preload.rc !== 200) {
          callback({errors : ["Error " + preload.rc]});
        } else {
          callback(transformResponseData(params, { body: preload.body }));
        }
        return true;
      }
    }
    return false;
  }

  /**
   * @param {Object} configuration Configuration settings
   * @private
   */
  function init (configuration) {
    config = configuration["core.io"];
  }

  var requiredConfig = {
    proxyUrl: new gadgets.config.RegExValidator(/.*%url%.*/),
    jsonProxyUrl: gadgets.config.NonEmptyStringValidator
  };
  gadgets.config.register("core.io", requiredConfig, init);

  return /** @scope gadgets.io */ {
    /**
     * Fetches content from the provided URL and feeds that content into the
     * callback function.
     *
     * Example:
     * <pre>
     * gadgets.io.makeRequest(url, fn,
     *    {contentType: gadgets.io.ContentType.FEED});
     * </pre>
     *
     * @param {String} url The URL where the content is located
     * @param {Function} callback The function to call with the data from the
     *     URL once it is fetched
     * @param {Map.&lt;gadgets.io.RequestParameters, Object&gt;} opt_params
     *     Additional
     *     <a href="gadgets.io.RequestParameters.html">parameters</a>
     *     to pass to the request
     *
     * @member gadgets.io
     */
    makeRequest : function (url, callback, opt_params) {
      // TODO: This method also needs to respect all members of
      // gadgets.io.RequestParameters, and validate them.

      var params = opt_params || {};

      // Check if authorization is requested
      var auth, st;
      var reqState, oauthService, oauthToken;
      if (params.AUTHORIZATION && params.AUTHORIZATION !== "NONE") {
        auth = params.AUTHORIZATION.toLowerCase();
        st = gadgets.util.getUrlParameters().st;
        if (params.AUTHORIZATION === "AUTHENTICATED") {
          reqState = oauthState;
          oauthService = params.OAUTH_SERVICE;
          oauthToken = params.OAUTH_TOKEN;
        }
      } else {
        // Non auth'd & non post'd requests are cachable
        if (!params.REFRESH_INTERVAL && !params.POST_DATA) {
          params.REFRESH_INTERVAL = 3600;
         }
      }
      var signOwner = params.OWNER_SIGNED;
      var signViewer = params.VIEWER_SIGNED;

      var headers = params.HEADERS || {};
      if (params.METHOD === "POST" && !headers["Content-Type"]) {
        headers["Content-Type"] = "application/x-www-form-urlencoded";
      }

      var paramData = {
        url: url,
        httpMethod : params.METHOD || "GET",
        headers: gadgets.io.encodeValues(headers, false),
        postData : params.POST_DATA || "",
        authz : auth || "",
        st : st || "",
        oauthState : reqState || "",
        oauthService : oauthService || "",
        oauthToken : oauthToken || "",
        contentType : params.CONTENT_TYPE || "TEXT",
        numEntries : params.NUM_ENTRIES || "3",
        getSummaries : !!params.GET_SUMMARIES,
        signOwner : signOwner || "true",
        signViewer : signViewer || "true"
      };

      if (!respondWithPreload(paramData, params, callback, processResponse)) {
        var refreshInterval = params.REFRESH_INTERVAL || 0;

        if (refreshInterval > 0) {
          // this content should be cached
          // Add paramData to the URL
          var extraparams = "&refresh=" + refreshInterval + '&'
              + gadgets.io.encodeValues(paramData);

          makeXhrRequest(url, config.jsonProxyUrl + extraparams, callback,
              null, "GET", params, processResponse);

        } else {
          makeXhrRequest(url, config.jsonProxyUrl, callback,
              gadgets.io.encodeValues(paramData), "POST", params,
              processResponse);
        }
      }
    },

    /**
     * @private
     */
    makeNonProxiedRequest : function (relativeUrl, callback, opt_params) {
      var params = opt_params || {};
      makeXhrRequest(relativeUrl, relativeUrl, callback, params.POST_DATA,
          params.METHOD, params, processNonProxiedResponse);
    },

    /**
     * Converts an input object into a URL-encoded data string.
     * (key=value&amp;...)
     *
     * @param {Object} fields The post fields you wish to encode
     * @param {Boolean} opt_noEscaping An optional parameter specifying whether
     *     to turn off escaping of the parameters. Defaults to false.
     * @return {String} The processed post data in www-form-urlencoded format.
     *
     * @member gadgets.io
     */
    encodeValues : function (fields, opt_noEscaping) {
      var escape = !opt_noEscaping;

      var buf = [];
      var first = false;
      for (var i in fields) if (fields.hasOwnProperty(i)) {
        if (!first) {
          first = true;
        } else {
          buf.push("&");
        }
        buf.push(escape ? encodeURIComponent(i) : i);
        buf.push("=");
        buf.push(escape ? encodeURIComponent(fields[i]) : fields[i]);
      }
      return buf.join("");
    },

    /**
     * Gets the proxy version of the passed-in URL.
     *
     * @param {String} url The URL to get the proxy URL for
     * @return {String} The proxied version of the URL
     *
     * @member gadgets.io
     */
    getProxyUrl : function (url) {
      return config.proxyUrl.replace("%url%", encodeURIComponent(url));
    }
  };
}();

gadgets.io.RequestParameters = gadgets.util.makeEnum([
  "METHOD",
  "CONTENT_TYPE",
  "POST_DATA",
  "HEADERS",
  "AUTHORIZATION",
  "NUM_ENTRIES",
  "GET_SUMMARIES",
  "REFRESH_INTERVAL",
  "OAUTH_SERVICE",
  "OAUTH_TOKEN"
]);

// PUT, DELETE, and HEAD not supported currently.
gadgets.io.MethodType = gadgets.util.makeEnum([
  "GET", "POST", "PUT", "DELETE", "HEAD"
]);

gadgets.io.ContentType = gadgets.util.makeEnum([
  "TEXT", "DOM", "JSON", "FEED"
]);

gadgets.io.AuthorizationType = gadgets.util.makeEnum([
  "NONE", "SIGNED", "AUTHENTICATED"
]);
