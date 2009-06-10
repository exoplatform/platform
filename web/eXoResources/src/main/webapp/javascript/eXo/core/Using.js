function Using() {
  this.wait = 1;
  this.defaultAsyncWait = 250;
};

Using.prototype.init = function(scriptName, callback, context) {
	var a = -1;
  var scriptNames = new Array();
  if (typeof(scriptName) != "string" && scriptName.length) {
    var _scriptNames = scriptName;
    for (var s = 0; s < _scriptNames.length; s++) {
      if (this.registered[_scriptNames[s]] || durl(_scriptNames[s])) {
        scriptNames.push(_scriptNames[s]);
      }
    }
    scriptName = scriptNames[0];
    a = 1;
  } else {
    while (typeof(arguments[++a]) == "string") {
      if (this.registered[scriptName] || durl(scriptName)) {
        scriptNames.push(arguments[a]);
      }
    }
  }

  callback = arguments[a];
  context = arguments[++a];

  if (scriptNames.length > 1) {
    var cb = callback;
    callback = function() {
      eXo.core.Using.init(scriptNames, cb, context);
    }
  }

  var reg = this.registered[scriptName];
  if (!this.__durls) this.__durls = {};
  if (durl(scriptName) && scriptName.substring(0, 4) == "url(") {
    scriptName = scriptName.substring(4, scriptName.length - 1);
    if (!this.__durls[scriptName]) {
      scriptNames[0] = scriptName;
      this.register(scriptName, true, scriptName);
      reg = this.registered[scriptName];
      var callbackQueue = this.getCallbackQueue(scriptName);
      var cbitem = new this.CallbackItem(function() {
        eXo.core.Using.__durls[scriptName] = true;
      });
      callbackQueue.push(cbitem);
      callbackQueue.push(new this.CallbackItem(callback, context));
      callback = undefined;
      context = undefined;
    }
  }
  if (reg) {
    // load dependencies first
    for (var r = reg.requirements.length - 1; r >= 0; r--) {
      if (this.registered[reg.requirements[r].name]) {
        eXo.core.Using.init(reg.requirements[r].name, function() {eXo.core.Using.init(scriptName, callback, context);},context);
        return;
      }
    }

    // load each script URL
    for (var u = 0; u < reg.urls.length; u++) {
      if (u == reg.urls.length - 1) {
        if (callback) {
          this.load(reg.name, reg.urls[u], reg.remote, reg.asyncWait, new this.CallbackItem(callback, context));
        } else {
          this.load(reg.name, reg.urls[u], reg.remote, reg.asyncWait);
        }
      } else {
        this.load(reg.name, reg.urls[u], reg.remote, reg.asyncWait);
      }
    }

  } else {
    var cb = callback;
    if (cb) {
      cb.call(context);
    }
  }
};

Using.prototype.CallbackItem = function(_callback, _context) {
  this.callback = _callback;
  this.context = _context;
  this.invoke = function() {
    if (eXo.core.Using.context) this.call(eXo.core.Using.context);
    else this.callback();
  };
};

Using.prototype.Registration = function(_name, _version, _remote, _asyncWait, _urls) {
  this.name = _name;
  var a = 0;
  var arg = arguments[++a];
  var v = true;
  if (typeof(arg) == "string") {
    for (var c = 0; c < arg.length; c++) {
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
    for (a = a; a < arguments.length; a++) {
      if (arguments[a] && typeof(arguments[a]) == "string") {
        this.urls.push(arguments[a]);
      }
    }
  }
  this.requirements = new Array();
  this.requires = function(resourceName, minimumVersion) {
    if (!minimumVersion) minimumVersion = "1.0.0"; // not currently used
    eXo.core.Using.requirements.push({
      name: resourceName,
      minVersion: minimumVersion // not currently used
    });
    return this;
  }
  this.register = function(name, version, remote, asyncWait, urls) {
    return eXo.core.Using.register(name, version, remote, asyncWait, urls);
  }
  return this;
};

Using.prototype.register = function(name, version, remote, asyncWait, urls) {
  var reg;
  if (typeof(name) == "object") {
    reg = name;
    reg = new this.Registration(reg.name, reg.version, reg.remote, reg.asyncWait, urls);
  } else {
    reg = new this.Registration(name, version, remote, asyncWait, urls);
  }
  if (!this.registered) this.registered = {};
  if (this.registered[name] && window.console) {
    window.console.log("Warning: Resource named \"" + name + "\" was already registered with this.register(); overwritten.");
  }
  this.registered[name] = reg;
  return reg;
};

Using.prototype.getCallbackQueue = function(scriptUrl) {
  if (!this.__callbackQueue) {
    this.__callbackQueue = {};
  }
  var callbackQueue = this.__callbackQueue[scriptUrl];
  if (!callbackQueue) {
    callbackQueue = this.__callbackQueue[scriptUrl] = new Array();
  }
  return callbackQueue;
};

Using.prototype.load = function(scriptName, scriptUrl, remote, asyncWait, cb) {
  if (asyncWait == undefined) asyncWait = this.wait;
  if (remote && asyncWait == 0) asyncWait = this.defaultAsyncWait;

  if (!this.loadedScripts) this.loadedScripts = new Array();

  var callbackQueue = this.getCallbackQueue(scriptUrl);
  callbackQueue.push(new this.CallbackItem(function() {
	    eXo.core.Using.loadedScripts.push(eXo.core.Using.registered[scriptName]);
	    eXo.core.Using.registered[scriptName] = false;
	  },  null));
  if (cb) {
    callbackQueue.push(cb);
    if (callbackQueue.length > 2) return;
  }
  if (remote) {
    this.srcScript(scriptUrl, asyncWait, callbackQueue);
  } else {
    var request = eXo.core.Browser.createHttpRequest();
    request.open('GET', scriptUrl, false);
    request.send(null);

    this.injectScript(request.responseText, scriptName);
    if (callbackQueue) {
      for (var q = 0; q < callbackQueue.length; q++) {
        callbackQueue[q].invoke();
      }
    }
    this.__callbackQueue[scriptUrl] = undefined;
  }
};

Using.prototype.genScriptNode = function() {
  var scriptNode = document.createElement("script");
  scriptNode.setAttribute("type", "text/javascript");
  scriptNode.setAttribute("language", "JavaScript");
  return scriptNode;
};

Using.prototype.srcScript = function(scriptUrl, asyncWait, callbackQueue) {
  var scriptNode = this.genScriptNode();
  scriptNode.setAttribute("src", scriptUrl);
  if (callbackQueue) {
    var execQueue = function() {
      eXo.core.Using.__callbackQueue[scriptUrl] = undefined;
      for (var q = 0; q < callbackQueue.length; q++) {
        callbackQueue[q].invoke();
      }
      callbackQueue = new Array(); // reset
    }
    scriptNode.onload = scriptNode.onreadystatechange = function() {
      if ((!scriptNode.readyState) || scriptNode.readyState == "loaded" || scriptNode.readyState == "complete" || scriptNode.readyState == 4 && scriptNode.status == 200) {
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
};

Using.prototype.injectScript = function(scriptText, scriptName) {
  var scriptNode = this.genScriptNode();
  try {
    scriptNode.setAttribute("name", scriptName);
  } catch(err) {}
  scriptNode.text = scriptText;
  var headNode = document.getElementsByTagName("head")[0];
  headNode.appendChild(scriptNode);
};

function durl(sc) {
  var su = sc;
  if (sc && sc.substring(0, 4) == "url(") {
    su = sc.substring(4, sc.length - 1);
  }
  //console.log("this.registered", this.registered);
  var r = eXo.core.Using.registered[su];
  return (!r && (!this.__durls || !this.__durls[su]) && sc && sc.length > 4 && sc.substring(0, 4) == "url(");
};

eXo.core.Using = new Using();
window.using = eXo.core.Using.init ;