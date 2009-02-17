function MouseObject() {
  this.init(null) ;
} ;

MouseObject.prototype.init = function(mouseEvent) {
  this.mousexInPage = null ;
  this.mouseyInPage = null ;

  this.lastMousexInPage = null ;
  this.lastMouseyInPage = null ;

  this.mousexInClient = null ;
  this.mouseyInClient = null ;

  this.lastMousexInClient = null ;
  this.lastMouseyInClient = null ;

  this.deltax = null ;
  this.deltay = null ;
  if(mouseEvent != null) this.update(mouseEvent) ;
} ;

MouseObject.prototype.update = function(mouseEvent) {
  var  x = eXo.core.Browser.findMouseXInPage(mouseEvent) ;
  var  y = eXo.core.Browser.findMouseYInPage(mouseEvent) ;

  this.lastMousexInPage =  this.mousexInPage != null ? this.mousexInPage : x ;
  this.lastMouseyInPage =  this.mouseyInPage != null ? this.mouseyInPage : y ;

  this.mousexInPage = x ;
  this.mouseyInPage = y ;

  x  =  eXo.core.Browser.findMouseXInClient(mouseEvent) ;
  y  =  eXo.core.Browser.findMouseYInClient(mouseEvent) ;

  this.lastMousexInClient =  this.mousexInClient != null ? this.mousexInClient : x ;
  this.lastMouseyInClient =  this.mouseyInClient != null ? this.mouseyInClient : y ;

  this.mousexInClient = x ;
  this.mouseyInClient = y ;

  this.deltax = this.mousexInClient - this.lastMousexInClient ;
  this.deltay = this.mouseyInClient - this.lastMouseyInClient ;
} ;

/************************************************************************************/
/**
* This function aims is to configure the javascript environment according to the browser in use
*
* Common configuration are made first, then we detect the current browser and according to the one 
* in use, we call delegated methods such as this.initIE() which will add some new configuration or
* overide the existing ones
*
* In all cases the method createHttpRequest is acting as the usual XMLHttpRequest in use in all AJAX
* calls
*/
function Browser() {
  this.onLoadCallback = new eXo.core.HashMap() ;
  this.onResizeCallback = new eXo.core.HashMap() ;
  this.onScrollCallback = new eXo.core.HashMap() ;
  
  this.breakStream;
  window.onresize =  this.managerResize ;
  window.onscroll =  this.onScroll ;
  
  this.initCommon() ;
  this.detectBrowser();

  if(this.opera)  this.initOpera() ;
  else if(this.ie) this.initIE() ;
  else if(this.webkit) this.initSafari() ;
  else this.initMozilla() ;
} ;

/**
  Copyright (c) 2008, Yahoo! Inc. All rights reserved.
  Code licensed under the BSD License:
  http://developer.yahoo.net/yui/license.txt
  version: 2.5.2
 */
Browser.prototype.detectBrowser = function() {
	
    /**
     * Internet Explorer version number or 0.  Example: 6
     * @property ie
     * @type float
     */
    this.ie = 0;

    /**
     * Opera version number or 0.  Example: 9.2
     * @property opera
     * @type float
     */
    this.opera = 0;

    /**
     * Gecko engine revision number.  Will evaluate to 1 if Gecko 
     * is detected but the revision could not be found. Other browsers
     * will be 0.  Example: 1.8
     * <pre>
     * Firefox 1.0.0.4: 1.7.8   <-- Reports 1.7
     * Firefox 1.5.0.9: 1.8.0.9 <-- Reports 1.8
     * Firefox 2.0.0.3: 1.8.1.3 <-- Reports 1.8
     * Firefox 3 alpha: 1.9a4   <-- Reports 1.9
     * </pre>
     * @property gecko
     * @type float
     */
    this.gecko = 0;

    /**
     * AppleWebKit version.  KHTML browsers that are not WebKit browsers 
     * will evaluate to 1, other browsers 0.  Example: 418.9.1
     * <pre>
     * Safari 1.3.2 (312.6): 312.8.1 <-- Reports 312.8 -- currently the 
     *                                   latest available for Mac OSX 10.3.
     * Safari 2.0.2:         416     <-- hasOwnProperty introduced
     * Safari 2.0.4:         418     <-- preventDefault fixed
     * Safari 2.0.4 (419.3): 418.9.1 <-- One version of Safari may run
     *                                   different versions of webkit
     * Safari 2.0.4 (419.3): 419     <-- Tiger installations that have been
     *                                   updated, but not updated
     *                                   to the latest patch.
     * Webkit 212 nightly:   522+    <-- Safari 3.0 precursor (with native SVG
     *                                   and many major issues fixed).  
     * 3.x yahoo.com, flickr:422     <-- Safari 3.x hacks the user agent
     *                                   string when hitting yahoo.com and 
     *                                   flickr.com.
     * Safari 3.0.4 (523.12):523.12  <-- First Tiger release - automatic update
     *                                   from 2.x via the 10.4.11 OS patch
     * Webkit nightly 1/2008:525+    <-- Supports DOMContentLoaded event.
     *                                   yahoo.com user agent hack removed.
     *                                   
     * </pre>
     * http://developer.apple.com/internet/safari/uamatrix.html
     * @property webkit
     * @type float
     */
    this.webkit = 0;

    /**
     * The mobile property will be set to a string containing any relevant
     * user agent information when a modern mobile browser is detected.
     * Currently limited to Safari on the iPhone/iPod Touch, Nokia N-series
     * devices with the WebKit-based browser, and Opera Mini.  
     * @property mobile 
     * @type string
     */
    this.mobile = null;

    /**
     * Adobe AIR version number or 0.  Only populated if webkit is detected.
     * Example: 1.0
     * @property air
     * @type float
     */
    this.air = 0;
	
	
    var ua=navigator.userAgent, m;

    // Modern KHTML browsers should qualify as Safari X-Grade
    if ((/KHTML/).test(ua)) {
        this.webkit=1;
    }
    // Modern WebKit browsers are at least X-Grade
    m=ua.match(/AppleWebKit\/([^\s]*)/);
    if (m&&m[1]) {
        this.webkit=parseFloat(m[1]);

        // Mobile browser check
        if (/ Mobile\//.test(ua)) {
            this.mobile = "Apple"; // iPhone or iPod Touch
        } else {
            m=ua.match(/NokiaN[^\/]*/);
            if (m) {
                this.mobile = m[0]; // Nokia N-series, ex: NokiaN95
            }
        }

        m=ua.match(/AdobeAIR\/([^\s]*)/);
        if (m) {
            this.air = m[0]; // Adobe AIR 1.0 or better
        }

    }

    if (!this.webkit) { // not webkit
        // @todo check Opera/8.01 (J2ME/MIDP; Opera Mini/2.0.4509/1316; fi; U; ssr)
        m=ua.match(/Opera[\s\/]([^\s]*)/);
        if (m&&m[1]) {
            this.opera=parseFloat(m[1]);
            m=ua.match(/Opera Mini[^;]*/);
            if (m) {
                this.mobile = m[0]; // ex: Opera Mini/2.0.4509/1316
            }
        } else { // not opera or webkit
            m=ua.match(/MSIE\s([^;]*)/);
            if (m&&m[1]) {
                this.ie=parseFloat(m[1]);
            } else { // not opera, webkit, or ie
                m=ua.match(/Gecko\/([^\s]*)/);
                if (m) {
                    this.gecko=1; // Gecko detected, look for revision
                    m=ua.match(/rv:([^\s\)]*)/);
                    if (m&&m[1]) {
                        this.gecko=parseFloat(m[1]);
                    }
                }
            }
        }
    }
}

Browser.prototype.managerResize = function() {
	if(eXo.core.Browser.currheight != document.documentElement.clientHeight) {
 		clearTimeout(eXo.core.Browser.breakStream) ;
 		eXo.core.Browser.breakStream = setTimeout(eXo.core.Browser.onResize, 100) ;
 	}
 	eXo.core.Browser.currheight = document.documentElement.clientHeight;
}

Browser.prototype.initCommon = function() {
  this.getBrowserHeight = function() { return document.documentElement.clientHeight ; }
  this.getBrowserWidth = function() { return document.documentElement.clientWidth ; }
  this.createHttpRequest = function() { return new XMLHttpRequest() ; }
} ;

Browser.prototype.initIE = function() {
  this.browserType = "ie" ;
  this.createHttpRequest = function() {
  	 return new ActiveXObject("Msxml2.XMLHTTP") ; 
  }
  this.eventListener = function(object, event, operation) {
    event = "on" + event ;
    object.attachEvent(event, operation) ;
  }
  this.setOpacity = function(component, value) {component.style.filter = "alpha(opacity=" + value + ")" ;}
  this.getEventSource = function(e) { return window.event.srcElement ; }
} ;

Browser.prototype.initMozilla = function() {
  this.browserType = "mozilla" ;
  this.eventListener = function(object, event, operation) { object.addEventListener(event, operation, false) ; }
  this.setOpacity = function(component, value) { component.style.opacity = value/100 ; }
  this.getEventSource = function(e) { return e.target ; }
} ;

Browser.prototype.initSafari = function() {
  this.browserType = "safari" ;
  this.getBrowserHeight = function() { return self.innerHeight ; } ;
  this.getBrowserWidth = function() { return self.innerWidth ; } ;
  this.eventListener = function(object, event, operation) { object.addEventListener(event, operation, false) ; }
  this.setOpacity = function(component, value) { component.style.opacity = value/100 ; }
  this.getEventSource = function(e) {
  	var targ = e.target ;
  	if (targ.nodeType == 3) targ = targ.parentNode ;
  	return targ ;
  }
} ;

Browser.prototype.initOpera = function() {
  this.browserType = "opera" ;
  this.getBrowserHeight = function() {
    return document.body.clientHeight ;
  }
  this.getBrowserWidth = function() {
    return document.body.clientWidth ;
  }
} ;

Browser.prototype.isIE6 = function() {
  var agent = navigator.userAgent ;
  return (agent.indexOf("MSIE 6") >=0);
} ;

Browser.prototype.isIE7 = function() {
  var agent = navigator.userAgent ;
  return (agent.indexOf("MSIE 7") >=0);
} ;

Browser.prototype.isFF = function() {
  return this.gecko;
} ;

Browser.prototype.isFF2 = function() {
  return (navigator.userAgent.indexOf("Firefox/2") >= 0);
} ;

Browser.prototype.isFF3 = function() {
  return (navigator.userAgent.indexOf("Firefox/3") >= 0);
} ;

Browser.prototype.findMouseXInClient = function(e) {
  if (!e) e = window.event ;
  return e.clientX ;
} ;

Browser.prototype.findMouseYInClient = function(e) {
  if (!e) e = window.event ;
  return e.clientY ;
} ;
/**
 * Adds a function to the list of functions to call on load
 */
Browser.prototype.addOnLoadCallback = function(id, method) {
  this.onLoadCallback.put(id, method) ;
} ;
/**
 * Calls the functions in the onLoadCallback array, if they exist
 * and clean the array
 */
Browser.prototype.onLoad = function() {
	try {
  	var callback = eXo.core.Browser.onLoadCallback ;
	  for(var name in callback.properties) {
	    var method = callback.get(name) ;
	    if (typeof(method) == "function") method() ;
	  }
	} catch(e) {}
  this.onLoadCallback = new eXo.core.HashMap();
} ;
/**
 * Adds a function to the list of functions to call when the window is resized
 */
Browser.prototype.addOnResizeCallback = function(id, method) {
  this.onResizeCallback.put(id, method) ;
} ;
/**
 * Calls the functions in the onResizeCallback array, if they exist
 */

Browser.prototype.onResize = function(event) {
	var callback = eXo.core.Browser.onResizeCallback ;
 for(var name in callback.properties) {
   var method = callback.get(name) ;
   if (typeof(method) == "function") method(event) ;
 }
} ;
/**
 * Adds a function to the list of functions to call when the user scrolls
 */
Browser.prototype.addOnScrollCallback = function(id, method) {
  this.onScrollCallback.put(id, method) ;
} ;
/**
 * Calls the functions in the onScrollCallback array, if they exist
 */
Browser.prototype.onScroll = function(event) {
  var callback = eXo.core.Browser.onScrollCallback ;
	for(var name in callback.properties) {
    var method = callback.get(name) ;
    try {
    	if (typeof(method) == "function") method(event) ;
    }catch(err){}
	}
} ;
/************************************TO BROWSER PAGE CLASS************************************************/
Browser.prototype.getBrowserType = function() {  
  return this.browserType ;
} ;
/**
 * Returns the horizontal position of an object relative to the window
 */
Browser.prototype.findPosX = function(obj, isRTL) {
  var curleft = 0;
  var tmpObj = obj ;
  while (tmpObj) {
    curleft += tmpObj.offsetLeft ;
    tmpObj = tmpObj.offsetParent ;
  }
  // if RTL return right position of obj
  if(isRTL) return curleft + obj.offsetWidth ;
  return curleft ;
} ;
/**
 * Returns the vertical position of an object relative to the window
 */
Browser.prototype.findPosY = function(obj) {
  var curtop = 0 ;
  while (obj) {
    curtop += obj.offsetTop ;
    obj = obj.offsetParent ;
  }
  return curtop ;
} ;
/**
 * Returns the horizontal position of an object relative to its container
 */
Browser.prototype.findPosXInContainer = function(obj, container, isRTL) {
  var objX = eXo.core.Browser.findPosX(obj, isRTL) ;
  var containerX = eXo.core.Browser.findPosX(container, isRTL) ;
  if(isRTL) return -(objX - containerX) ;
  return (objX - containerX) ;
} ;
/**
 * Returns the vertical position of an object relative to its container
 */
Browser.prototype.findPosYInContainer = function(obj, container) {
  var objY = eXo.core.Browser.findPosY(obj) ;
  var containerY = eXo.core.Browser.findPosY(container) ;
  return (objY - containerY) ;
} ;

/**
 * find the x position of the mouse in the page
 */
Browser.prototype.findMouseXInPage = function(e) {
  var posx = -1 ;
  if (!e) e = window.event ;
  if (e.pageX || e.pageY) {
    posx = e.pageX ;
  } else if (e.clientX || e.clientY) {
    posx = e.clientX + document.body.scrollLeft ;
  }
  return posx ;
} ;
/**
 * find the y position of the mouse in the page
 */
Browser.prototype.findMouseYInPage = function(e) {
  var posy = -1 ;
  if (!e) e = window.event ;
  if (e.pageY) {
    posy = e.pageY ;
  } else if (e.clientX || e.clientY) {
    //IE 6
    if (document.documentElement && document.documentElement.scrollTop) {
      posy = e.clientY + document.documentElement.scrollTop ;
    } else {
      posy = e.clientY + document.body.scrollTop ;
    }
  }
  return  posy ;
} ;
/**
 * find the x position of the mouse relative to object
 */
Browser.prototype.findMouseRelativeX = function(object, e) {
  var posx = -1 ;
  var posXObject = eXo.core.Browser.findPosX(object) ;
  
  /*
   * posXObject is added more 3px on IE6
   * posXObject is double on IE7
   * */
  
  if(eXo.core.Browser.isIE7()) {
  	posXObject = posXObject / 2 ;
  }
  
  if (!e) e = window.event ;
  if (e.pageX || e.pageY) {
    posx = e.pageX - posXObject ;
  } else if (e.clientX || e.clientY) {
    posx = e.clientX + document.body.scrollLeft - posXObject ;
  }
  return posx ;
} ;
/**
 * find the y position of the mouse relative to object
 */
Browser.prototype.findMouseRelativeY = function(object, e) {
  var posy = -1 ;
  var posYObject = eXo.core.Browser.findPosY(object) ;
  if (!e) e = window.event ;
  if (e.pageY) {
    posy = e.pageY - posYObject ;
  } else if (e.clientX || e.clientY) {
    //IE 6
    if (document.documentElement && document.documentElement.scrollTop) {
      posy = e.clientY + document.documentElement.scrollTop - posYObject ;
    } else {
      posy = e.clientY + document.body.scrollTop - posYObject ;
    }
  }
  return  posy ;
} ;

/* 
 * Set Position for a Component in a container
 */
Browser.prototype.setPositionInContainer = function(container, component, posX, posY) {
	var offsetX = component.offsetLeft ;
	var offsetY = component.offsetTop ;

	var posXInContainer = eXo.core.Browser.findPosXInContainer(component, container) ;
	var posYInContainer = eXo.core.Browser.findPosYInContainer(component, container) ;

	var deltaX = posX - (posXInContainer - offsetX) ;
	var deltaY = posY - (posYInContainer - offsetY) ;

	component.style.left = deltaX + "px" ;
	component.style.top = deltaY + "px" ;
} ;
/* 
 * Set Cookie
 */
Browser.prototype.setCookie = function(name,value,expiredays) {
	var exdate = new Date() ;
	exdate.setDate(exdate.getDate() + expiredays) ;
	document.cookie = name + "=" + escape(value) + ((expiredays==null) ? "" : ";expires="+exdate.toGMTString()) ;
} ;
/* 
 * Get Cookie
 */
Browser.prototype.getCookie = function(name) {
	if (document.cookie.length > 0) {
		var start = document.cookie.indexOf(name + "=")
		if (start != -1) {
			start = start + name.length + 1 ;
	    var end = document.cookie.indexOf(";",start) ;
	    if (end == -1) end = document.cookie.length ;
	    	return unescape(document.cookie.substring(start,end)) ;
	  } 
	}
	return "" ;
} ;

Browser.prototype.isDesktop = function() {
	if(document.getElementById("UIPageDesktop")) return true ;
	return false ;
}
/************************************************************************************/
eXo.core.Browser = new Browser() ;
eXo.core.Mouse = new MouseObject() ;