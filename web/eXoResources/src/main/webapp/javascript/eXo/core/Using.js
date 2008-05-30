/**
 * @author Jon Davis <jon@jondavis.net>
 * @version 1.3.1
 */
var using = window.using = function( scriptName, callback, context ) {
    function durl(sc) {
        var su = sc;
        if (sc && sc.substring(0, 4) == "url(") {
            su = sc.substring(4, sc.length - 1);
        }
        //console.log("using.registered", using.registered);
        var r = using.registered[su];
        return (!r && (!using.__durls || !using.__durls[su]) &&
                sc && sc.length > 4 && sc.substring(0, 4) == "url(");
    }
    var a=-1;
    var scriptNames = new Array();
    if (typeof(scriptName) != "string" && scriptName.length) {
        var _scriptNames = scriptName;
        for (var s=0;s<_scriptNames.length; s++) {
            if (using.registered[_scriptNames[s]] || durl(_scriptNames[s])) {
                scriptNames.push(_scriptNames[s]);
            }
        }
        scriptName = scriptNames[0];
        a=1;
    } else {
        while (typeof(arguments[++a]) == "string") {
            if (using.registered[scriptName] || durl(scriptName)) {
                scriptNames.push(arguments[a]);
            }
        }
    }

    callback = arguments[a];
    context = arguments[++a];

    if (scriptNames.length > 1) {
        var cb = callback;
        callback = function() {
            using(scriptNames, cb, context);
        }
    }

    var reg = using.registered[scriptName];
    if (!using.__durls) using.__durls = {};
    if (durl(scriptName) && scriptName.substring(0, 4) == "url(") {
        scriptName = scriptName.substring(4, scriptName.length - 1);
        if (!using.__durls[scriptName]) {
            scriptNames[0] = scriptName;
            using.register(scriptName, true,  scriptName);
            reg = using.registered[scriptName];
            var callbackQueue = using.prototype.getCallbackQueue(scriptName);
            var cbitem = new using.prototype.CallbackItem(function() {
                using.__durls[scriptName] = true;
            });
            callbackQueue.push(cbitem);
            callbackQueue.push(new using.prototype.CallbackItem(callback, context));
            callback = undefined;
            context = undefined;
        }
    }
    if (reg) {

        // load dependencies first
        for (var r=reg.requirements.length-1; r>=0; r--) {
            if (using.registered[reg.requirements[r].name]) {
                using(reg.requirements[r].name, function() {
                    using(scriptName, callback, context);
                }, context);
                return;
            }
        }

        // load each script URL
        for (var u=0; u<reg.urls.length; u++) {
            if (u == reg.urls.length - 1) {
                if (callback) {
                    using.load(reg.name, reg.urls[u], reg.remote, reg.asyncWait,
                        new using.prototype.CallbackItem(callback, context));
                } else {
                    using.load(reg.name, reg.urls[u], reg.remote, reg.asyncWait);
                }
            } else {
                using.load(reg.name, reg.urls[u], reg.remote, reg.asyncWait);
            }
        }

    } else {
        var cb = callback;
        if (cb) {
            cb.call(context);
        }
    }
}

using.prototype = {

    CallbackItem : function(_callback, _context) {
        this.callback = _callback;
        this.context = _context;
        this.invoke = function() {
            if (this.context) this.callback.call(this.context);
            else this.callback();
        };
    },

	Registration : function(_name, _version, _remote, _asyncWait, _urls) {
	    this.name = _name;
	    var a=0;
	    var arg = arguments[++a];
	    var v=true;
	    if (typeof(arg) == "string") {
	        for (var c=0; c<arg.length; c++) {
	            if ("1234567890.".indexOf(arg.substring(c)) == -1) {
	                v = false;
	                break;
	            }
	        }
	        if (v) {
	            this.version = arg; // not currently used
	            arg = arguments[++a];
	        } else {
	            this.version = "1.0.0"; // not currently used
	        }
	    }
	    if (arg && typeof(arg) == "boolean") {
	        this.remote = arg;
	        arg = arguments[++a];
	    } else {
	        this.remote = false;
	    }
	    if (arg && typeof(arg) == "number") {
	        this.asyncWait = _asyncWait;
        } else {
            this.asyncWait = 0;
        }
	    this.urls = new Array();
	    if (arg && arg.length && typeof(arg) != "string") {
	        this.urls = arg;
	    } else {
	        for (a=a; a<arguments.length; a++) {
	            if (arguments[a] && typeof(arguments[a]) == "string") {
	                this.urls.push(arguments[a]);
	            }
	        }
	    }
	    this.requirements = new Array();
	    this.requires = function(resourceName, minimumVersion) {
	        if (!minimumVersion) minimumVersion = "1.0.0"; // not currently used
	        this.requirements.push({
	            name: resourceName,
	            minVersion: minimumVersion // not currently used
	            });
	        return this;
	    }
	    this.register = function(name, version, remote, asyncWait, urls) {
	        return using.register(name, version, remote, asyncWait, urls);
	    }
	    return this;
	},

    register : function(name, version, remote, asyncWait, urls) {
        var reg;
        if (typeof(name) == "object") {
            reg = name;
            reg = new using.prototype.Registration(reg.name, reg.version, reg.remote, reg.asyncWait, urls);
        } else {
            reg = new using.prototype.Registration(name, version, remote, asyncWait, urls);
        }
        if (!using.registered) using.registered = { };
        if (using.registered[name] && window.console) {
            window.console.log("Warning: Resource named \"" + name + "\" was already registered with using.register(); overwritten.");
        }
        using.registered[name] = reg;
        return reg;
    },

	wait: 1,

	defaultAsyncWait: 250,

	getCallbackQueue: function(scriptUrl) {
		if (!using.__callbackQueue) {
			using.__callbackQueue = {};
		}
 		var callbackQueue = using.__callbackQueue[scriptUrl];
 		if (!callbackQueue) {
 		    callbackQueue = using.__callbackQueue[scriptUrl] = new Array();
 		}
 		return callbackQueue;
	},

	load: function(scriptName, scriptUrl, remote, asyncWait, cb) {
		if (asyncWait == undefined) asyncWait = using.wait;
		if (remote && asyncWait == 0) asyncWait = using.defaultAsyncWait;

		if (!using.loadedScripts) using.loadedScripts = new Array();

 		var callbackQueue = using.prototype.getCallbackQueue(scriptUrl);
 		callbackQueue.push(new using.prototype.CallbackItem( function() {
 		    using.loadedScripts.push(using.registered[scriptName]);
 		    using.registered[scriptName] = false;
 		}, null));
 		if (cb) {
 		    callbackQueue.push(cb);
 		    if (callbackQueue.length > 2) return;
 		}
 		if (remote) {
 		    using.srcScript(scriptUrl, asyncWait, callbackQueue);
 		} else {
       var request = eXo.core.Browser.createHttpRequest() ;
       request.open('GET', scriptUrl, false) ;
       request.send(null);

        using.injectScript(request.responseText, scriptName);
        if (callbackQueue) {
            for (var q=0; q<callbackQueue.length; q++) {
                callbackQueue[q].invoke();
            }
        }
        using.__callbackQueue[scriptUrl] = undefined;
 		}
	},

	genScriptNode : function() {
		var scriptNode = document.createElement("script");
		scriptNode.setAttribute("type", "text/javascript");
		scriptNode.setAttribute("language", "JavaScript");
		return scriptNode;
	},
	srcScript : function(scriptUrl, asyncWait, callbackQueue) {
		var scriptNode = using.prototype.genScriptNode();
		scriptNode.setAttribute("src", scriptUrl);
		if (callbackQueue) {
		    var execQueue = function() {
				using.__callbackQueue[scriptUrl] = undefined;
			    for (var q=0; q<callbackQueue.length; q++) {
			        callbackQueue[q].invoke();
			    }
			    callbackQueue = new Array(); // reset
		    }
			scriptNode.onload = scriptNode.onreadystatechange = function() {
				if ((!scriptNode.readyState) || scriptNode.readyState == "loaded" || scriptNode.readyState == "complete" ||
					scriptNode.readyState == 4 && scriptNode.status == 200) {
					if (asyncWait > 0) {
						setTimeout(execQueue, asyncWait);
					}
					else {
						execQueue();
					}
				}
			};
		}
		var headNode = document.getElementsByTagName("head")[0];
		headNode.appendChild(scriptNode);
	},
	injectScript : function(scriptText, scriptName) {
		var scriptNode = using.prototype.genScriptNode();
		try {
		    scriptNode.setAttribute("name", scriptName);
		} catch (err) { }
		scriptNode.text = scriptText;
		var headNode = document.getElementsByTagName("head")[0];
		headNode.appendChild(scriptNode);
	}
};
using.register = using.prototype.register;
using.load = using.prototype.load;
using.wait = using.prototype.wait;
using.defaultAsyncWait = using.prototype.defaultAsyncWait;
using.srcScript = using.prototype.srcScript;
using.injectScript = using.prototype.injectScript;

eXo.core.Using = using;