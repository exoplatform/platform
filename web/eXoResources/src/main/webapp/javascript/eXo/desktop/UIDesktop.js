eXo.require('eXo.animation.ImplodeExplode');
eXo.require('eXo.desktop.UIDockbar');
eXo.require('eXo.core.UIMaskLayer');

function UIDesktop() {
};

UIDesktop.prototype.init = function() {
  var pageDesktop = document.getElementById("UIPageDesktop") ;

	eXo.desktop.UIDesktop.fixDesktop() ;
  var uiWindows = eXo.core.DOMUtil.findChildrenByClass(pageDesktop, "div", "UIWindow") ;
  for(var i = 0; i < uiWindows.length; i++) {
  	if(uiWindows[i].isFirstTime == false)	continue ;
  	eXo.desktop.UIDesktop.backupWindowProperties(uiWindows[i]);
  }
};

UIDesktop.prototype.fixDesktop = function() {
	var pageDesktop = document.getElementById("UIPageDesktop") ;
	var browserHeight = eXo.core.Browser.getBrowserHeight() ;
	pageDesktop.style.height = browserHeight + "px" ;
	window.scroll(0,0);
	
  eXo.desktop.UIDockbar.init() ;
};

UIDesktop.prototype.resetZIndex = function(windowObject) {
	var windowsInDesktop = eXo.core.DOMUtil.getChildrenByTagName(windowObject.parentNode, "div") ;
	var uiDockbar = document.getElementById("UIDockBar") ;
	
	var maxZIndex = windowsInDesktop[0].style.zIndex ;
	for(var i = 0; i < windowsInDesktop.length; i++) {
		if(parseInt(maxZIndex) < parseInt(windowsInDesktop[i].style.zIndex)) {
			maxZIndex = windowsInDesktop[i].style.zIndex ;
		}
		
		if(parseInt(windowsInDesktop[i].style.zIndex) > parseInt(windowObject.style.zIndex)) {
			windowsInDesktop[i].style.zIndex = parseInt(windowsInDesktop[i].style.zIndex) - 1 ;
		}
	}
	
	windowObject.style.zIndex = maxZIndex ;
	uiDockbar.style.zIndex = parseInt(maxZIndex) + 1 ;
	
	return maxZIndex ;
};

UIDesktop.prototype.showHideWindow = function(uiWindow, clickedElement) {
//	alert("Window: " + uiWindow.className + "\n Icon: " + clickedElement.className) ;
	if(typeof(uiWindow) == "string") this.object = document.getElementById(uiWindow) ;
	else this.object = uiWindow ;
//	alert("display: " + this.object.style.display + "\n visibility: " + this.object.style.visibility) ;
	this.object.maxIndex = eXo.desktop.UIDesktop.resetZIndex(this.object) ;
	var numberOfFrame = 10 ;
	if(this.object.style.display == "block") {
		eXo.animation.ImplodeExplode.implode(this.object, clickedElement, "UIPageDesktop", numberOfFrame, false) ;
	} else {
		var uiDockBar = document.getElementById("UIDockBar") ;
		eXo.desktop.UIDockbar.resetDesktopShowedStatus(uiPageDesktop, uiDockBar) ;
		eXo.animation.ImplodeExplode.explode(this.object, clickedElement, "UIPageDesktop", numberOfFrame, false) ;
	}
	eXo.desktop.UIDockbar.containerMouseOver() ;
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
	uiWindow.style.display = "none" ;
  uiWindow.isShowed = false ;
  uiWindow.isFirstTime = false ;
} ;

UIDesktop.prototype.createJSApplication = function(application, applicationId, instanceId, appLocation) {
	eXo.require(application, appLocation);
	var createApplication = application + '.initApplication(\''+applicationId+'\',\''+instanceId+'\');' ;
	eval(createApplication);
} ;

UIDesktop.prototype.saveJSApplication = function(application, applicationId, instanceId, appLocation) {
  var params = [
  	{name: "jsApplication", value : application},
  	{name: "jsApplicationId", value : applicationId},
  	{name: "jsInstanceId", value : instanceId},
  	{name: "jsApplicationLocation", value : appLocation}
  ] ;
	ajaxGet(eXo.env.server.createPortalURL("UIPortal", "AddJSApplicationToDesktop", true, params)) ;
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
	
	var separators = eXo.core.DOMUtil.findChildrenByClass(iconContainer, "img", "Separator") ;
	iconContainer.insertBefore(appIcon ,separators[1]) ;
	iconContainer.insertBefore(appTooltip ,separators[1]) ;
	
 	eXo.desktop.UIWindow.init(applicationNode, true, 20, 20, appDescriptor.application.minWidth) ;
  
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

eXo.desktop.UIDesktop = new UIDesktop() ;
