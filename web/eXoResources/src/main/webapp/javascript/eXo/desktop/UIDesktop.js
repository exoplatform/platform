function UIDesktop() {
};

UIDesktop.prototype.init = function() {
	var pageDesktop = document.getElementById("UIPageDesktop") ;
	if(pageDesktop) {
		eXo.desktop.UIDesktop.fixDesktop() ;
	  var uiWindows = eXo.core.DOMUtil.findChildrenByClass(pageDesktop, "div", "UIWindow") ;
	  for(var i = 0; i < uiWindows.length; i++) {
	  	if(uiWindows[i].isFirstTime == false)	continue ;
			//fix display scroll in first time
	  	var blockResizes = eXo.core.DOMUtil.findDescendantsByClass(uiWindows[i], "div", "UIResizableBlock");
			if (blockResizes.length > 1) blockResizes[0].style.overflow = "hidden" ;
			eXo.desktop.UIDesktop.backupWindowProperties(uiWindows[i]);
	  }
	}
};

UIDesktop.prototype.fixDesktop = function() {
  var pageDesktop = document.getElementById("UIPageDesktop") ;
  var browserHeight = eXo.core.Browser.getBrowserHeight() ;
  if(pageDesktop)pageDesktop.style.height = browserHeight + "px" ;
  window.scroll(0,0);
  
  eXo.desktop.UIDockbar.init() ;
};

//TODO DungHM
UIDesktop.prototype.resetZIndex = function(windowObject) {
  var windowsInDesktop = eXo.core.DOMUtil.getChildrenByTagName(windowObject.parentNode, "div") ;
  var uiDockbar = document.getElementById("UIDockBar") ;
  	
  var maxZIndex = windowObject.style.zIndex ;
 
  var uiPopupWindow = eXo.core.DOMUtil.findDescendantsByClass(windowObject.parentNode,'div','UIPopupWindow') ;
  for (var i = 0; i < uiPopupWindow.length; i ++) {
 		if (uiPopupWindow[i].style.display == "block") return ;
  }
  
  for(var i = 0; i < windowsInDesktop.length; i++) {
  	if((windowsInDesktop[i].className.indexOf("UIWindow") >= 0) || (windowsInDesktop[i].className.indexOf("UIWidget") >= 0)) {
  		
	    if(parseInt(maxZIndex) < parseInt(windowsInDesktop[i].style.zIndex)) {
	      maxZIndex = windowsInDesktop[i].style.zIndex ;
	    }
	    
	    if(parseInt(windowsInDesktop[i].style.zIndex) >= parseInt(windowObject.style.zIndex)) {
	      windowsInDesktop[i].style.zIndex = parseInt(windowsInDesktop[i].style.zIndex) - 1 ;
	      
	    }
  	}
  	if (windowsInDesktop[i].style.zIndex < 0) windowsInDesktop[i].style.zIndex = 1 ;
  }
	
  windowObject.style.zIndex = maxZIndex ;
  uiDockbar.style.zIndex = parseInt(maxZIndex) + 1 ;
};

UIDesktop.prototype.isMaxZIndex = function(object) {
	var isMax = false ;
	var DOMUtil = eXo.core.DOMUtil ;
	var uiPageDesktop = document.getElementById("UIPageDesktop") ;
	var uiDockbar = document.getElementById("UIDockBar") ;
	var desktopApps = DOMUtil.getChildrenByTagName(uiPageDesktop, "div") ;
	
	var maxZIndex = parseInt(object.style.zIndex) ;
	for(var i = 0; i < desktopApps.length; i++) {
		if((desktopApps[i].className.indexOf("UIWindow") >= 0) || (desktopApps[i].className.indexOf("UIWidget") >= 0)) {
			if(parseInt(desktopApps[i].style.zIndex) > maxZIndex) maxZIndex = desktopApps[i].style.zIndex ;
		}
	}
	
	if(object.style.zIndex == maxZIndex) isMax = true ;
	return isMax ;
};

UIDesktop.prototype.showHideWindow = function(uiWindow, clickedElement) {
  if(typeof(uiWindow) == "string") this.object = document.getElementById(uiWindow) ;
  else this.object = uiWindow ;
  this.object.maxIndex = eXo.desktop.UIDesktop.resetZIndex(this.object) ;
  var numberOfFrame = 10 ;
  if(this.object.style.display == "block") {
    eXo.animation.ImplodeExplode.implode(this.object, clickedElement, "UIPageDesktop", numberOfFrame, false) ;
    eXo.desktop.UIWindow.saveWindowProperties(this.object, "HIDE");
    this.object.isShowed = false ;
  } else {
  	this.object.isShowed = true ;
    var uiDockBar = document.getElementById("UIDockBar") ;
		var uiPageDesktop	= document.getElementById("UIPageDesktop") ;
    eXo.desktop.UIDockbar.resetDesktopShowedStatus(uiPageDesktop, uiDockBar) ;
    eXo.animation.ImplodeExplode.explode(this.object, clickedElement, "UIPageDesktop", numberOfFrame, false) ;
    eXo.desktop.UIWindow.saveWindowProperties(this.object, "SHOW");

  	//TODO MinhJS: fix bug for don't apply style css in IE6 in first time.
  	if(eXo.core.Browser.isIE6()) {
  		this.object.style.filter =  "" ;
  	}
		//fix display scroll in first time.
		var blockResizes = eXo.core.DOMUtil.findDescendantsByClass(this.object, "div", "UIResizableBlock");
		if (blockResizes.length > 1) blockResizes[0].style.overflow = "hidden" ;
  }
//  eXo.desktop.UIDockbar.containerMouseOver() ;
};

UIDesktop.prototype.findPosXInDesktop = function(object) {
  var uiPageDesktop = eXo.core.DOMUtil.findAncestorByClass(object, "UIPageDesktop") ;
  var posXUIPageDesktop = eXo.core.Browser.findPosX(uiPageDesktop) ;
  var posXObject = eXo.core.Browser.findPosX(object) ;
  return (posXObject - posXUIPageDesktop) ;
} ;

UIDesktop.prototype.findPosYInDesktop = function(object) {
  var uiPageDesktop = eXo.core.DOMUtil.findAncestorByClass(object, "UIPageDesktop") ;
  var posYUIPageDesktop = eXo.core.Browser.findPosY(uiPageDesktop) ;
  var posYObject = eXo.core.Browser.findPosY(object) ;
  return (posYObject - posYUIPageDesktop) ;
} ;

UIDesktop.prototype.backupWindowProperties = function(uiWindow) {
  uiWindow.originalX = eXo.desktop.UIDesktop.findPosYInDesktop(uiWindow) ;
  uiWindow.originalY = eXo.desktop.UIDesktop.findPosXInDesktop(uiWindow) ;
  uiWindow.originalW = uiWindow.offsetWidth ;
  uiWindow.originalH = uiWindow.offsetHeight ;
  uiWindow.style.visibility = "visible" ;
  if(uiWindow.style.display == "") uiWindow.style.display = "none" ;
  
  uiWindow.isShowed = false ;
  uiWindow.isFirstTime = false ;
} ;

UIDesktop.prototype.addApplicationToDesktop = function(application, appId, appLocation) {
  try {
    eXo.require(application, appLocation);
  } catch(err) {
    alert("Can Not Load Application!");
  }
  eval(application).appLocation = appLocation ;
  eval(application).initApplication(appId);
} ;

UIDesktop.prototype.addJSApplication = function(applicationNode) {
  var appDescriptor = applicationNode.applicationDescriptor;
  var DOMUtil = eXo.core.DOMUtil ;
  var uiPageDesktop = document.getElementById("UIPageDesktop");
  var iconContainer = document.getElementById("IconContainer");
  var windowsInDesktop = DOMUtil.findChildrenByClass(uiPageDesktop, "div", "UIWindow");
  var lastElement = null;
  if(windowsInDesktop[windowsInDesktop.length - 1] != undefined) {
     lastElement = DOMUtil.findNextElementByTagName(windowsInDesktop[windowsInDesktop.length - 1], "div");
  }  
  /*Insert ApplicationNode To UIPageDesktop*/
  if(lastElement == null) {
    uiPageDesktop.appendChild(applicationNode);
  } else  {
    uiPageDesktop.insertBefore(applicationNode, lastElement) ;
  }
  /*Get Application's Stylesheet*/
  var styleId = appDescriptor.appId + "Stylesheet" ;
  eXo.core.Skin.addSkin(styleId, appDescriptor.application.skin[eXo.env.client.skin]);
  
  /*Create Application Icon*/
  var iconUrl = appDescriptor.application.appIcon ;
  var iconId = appDescriptor.appId + "Icon" ;
  var appIcon = eXo.desktop.UIDockbar.createApplicationIcon(iconUrl, iconId) ;
  
  /*Create Application Tooltip*/
  var tooltip = appDescriptor.application.appName ;
  var appTooltip = eXo.desktop.UIDockbar.createApplicationTooltip(tooltip) ;
  
  /*Init a UIWindow Application*/
  var windowPosX = 20 ;
  if(applicationNode.style.left != "") {
  	windowPosX = (applicationNode.style.left).replace("px", "");
  }
  
  var windowPosY = 20 ;
  if(applicationNode.style.top != "") {
  	windowPosY = (applicationNode.style.top).replace("px", "") ;
  }
  
  eXo.desktop.UIWindow.init(applicationNode, true, windowPosX, windowPosY, appDescriptor.application.minWidth) ;
  
  eXo.desktop.UIDesktop.backupWindowProperties(applicationNode);
  
  appIcon.onclick = function() {
    eXo.desktop.UIDesktop.showHideWindow(applicationNode, this) ;
  }
  
  eXo.desktop.UIDockbar.resizeDockBar() ;
} ;


UIDesktop.prototype.removeJSApplication = function(applicationNode) {
  var uiPageDesktop = document.getElementById("UIPageDesktop") ;
  var iconContainer = document.getElementById("IconContainer") ;
  var appIcon = document.getElementById(applicationNode.id + "Icon");
  var appTooltip = eXo.core.DOMUtil.findNextElementByTagName(appIcon, "span") ;
  
  uiPageDesktop.removeChild(applicationNode) ;
  iconContainer.removeChild(appIcon) ;
  iconContainer.removeChild(appTooltip) ;

  eXo.desktop.UIDockbar.resizeDockBar() ;
  
  var params = [
    {name: "jsInstanceId", value : applicationNode.id}
  ] ;
  ajaxGet(eXo.env.server.createPortalURL("UIPortal", "RemoveJSApplicationToDesktop", true, params)) ;
} ;

UIDesktop.prototype.removeApp = function(uri) {
	var appId = uri.substr(uri.lastIndexOf("=") + 1) ;
	var result = ajaxAsyncGetRequest(uri, false) ;
	if(result == "OK") {
		eXo.desktop.UIDesktop.removeWindow("UIWindow-" + appId) ;
		eXo.desktop.UIDockbar.removeDockbarIcon("DockItem" + appId) ;
	}
};

UIDesktop.prototype.removeWindow = function (idWindow) {
	var uiWindow = document.getElementById("UIWindow-" + idWindow); 
	if(uiWindow) eXo.core.DOMUtil.removeElement(uiWindow);
};

eXo.desktop.UIDesktop = new UIDesktop() ;
