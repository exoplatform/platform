eXo.require('eXo.webui.UIPopup');
function UIWindow() {
} ;
/*
 * minh.js.exo
 */

UIWindow.prototype.init = function(popup, isShow, posX, posY, minWidth) {
	this.superClass = eXo.webui.UIPopup ;
	if(typeof(popup) == "string") popup = document.getElementById(popup) ;
	if(popup == null) return ;

	var DOMUtil = eXo.core.DOMUtil ;
	var UIWindow = eXo.desktop.UIWindow ;
	var uiPageDesktop = document.getElementById("UIPageDesktop") ;
	var uiApplication = DOMUtil.findFirstDescendantByClass(popup, "div", "UIApplication") ;
	if(!uiApplication) return ;
	var applicationMinWidth = DOMUtil.findFirstDescendantByClass(popup, "div", "ApplicationMinWidth") ;
  if(applicationMinWidth) {
  	if(minWidth) {
  		popup.style.width = (minWidth + 8) + "px" ;
	  	applicationMinWidth.style.width = minWidth + "px" ;
	  	popup.applicationOriginalWidth = minWidth ;
  	} else {
  		popup.applicationOriginalWidth = 750 ;
  		if(popup.style.width == "") popup.style.width = "750px" ;
  	}
  	
  	if(DOMUtil.hasDescendantClass(uiApplication, "UIResizableBlock")) {
  		applicationMinWidth.style.width = "auto" ;
		}
  }

  /*Fix Bug On IE6*/
	if(eXo.core.Browser.isIE6()) {
		try {
			var appWidth = popup.offsetWidth - 8 ;
			if(appWidth > 0) uiApplication.style.width = appWidth + "px" ;
		} catch(e) {
			alert(e.message) ;
		}
	}
	
	if(popup.style.zIndex == "") popup.style.zIndex = ++eXo.webui.UIPopup.zIndex ;
	
	popup.onmousedown = this.mousedownOnPopup ;
	popup.onmouseup = this.mouseupOnPopup ; 

	var windowPortletInfo = DOMUtil.findFirstDescendantByClass(popup, "div", "WindowPortletInfo") ;
	this.superClass.setPosition(popup, posX, posY) ;
	try {
		windowPortletInfo.onmousedown = this.initDND ;
	} catch(err) {
		alert("Error In DND: " + err) ;
	}
	
	var windowPortletControl = DOMUtil.findFirstDescendantByClass(popup, "div", "WindowPortletControl") ;
	var minimizedIcon = DOMUtil.findFirstDescendantByClass(windowPortletControl, "div", "MinimizedIcon") ;
	var maximizedIcon = DOMUtil.findFirstDescendantByClass(windowPortletControl, "div", "MaximizedIcon") ;
	var resizeArea = DOMUtil.findFirstDescendantByClass(popup, "div", "ResizeArea") ;
	minimizedIcon.onmouseup = this.mouseupOnMinimizedIcon ; 
	maximizedIcon.onmouseup = this.mouseupOnMaximizedIcon ;
	resizeArea.onmousedown = this.mousedownOnResizeArea ;
	uiPageDesktop.onmouseup = this.mouseupOnUIPageDesktop ;
  UIWindow.windowMinHeight = popup.offsetHeight ;
} ;


UIWindow.prototype.mousedownOnPopup = function(evt) {
	var isMaxZIndex = eXo.desktop.UIDesktop.isMaxZIndex(this) ;
	if(!isMaxZIndex)	eXo.desktop.UIDesktop.resetZIndex(this) ;
} ;

UIWindow.prototype.mouseupOnPopup = function(evt) {
	eXo.desktop.UIWindow.saveWindowProperties(this) ;
} ;

UIWindow.prototype.mouseupOnMaximizedIcon =	function(evt) {
	var popup = eXo.core.DOMUtil.findAncestorByClass(this, "UIDragObject") ;
	eXo.desktop.UIWindow.maximizeWindow(popup, this) ;
} ;

UIWindow.prototype.mouseupOnMinimizedIcon =	function(evt) {
	var DOMUtil = eXo.core.DOMUtil ;
	var popup = DOMUtil.findAncestorByClass(this, "UIDragObject") ;
	var windows = DOMUtil.getChildrenByTagName(popup.parentNode, "div") ;
	var index = 0 ;
	for(var j = 0; j < windows.length; j++) {
		if(popup == windows[j]) {
			index = j ;
			break ;
		}
	}
	var iconContainer = document.getElementById("IconContainer") ;
	var children = DOMUtil.findChildrenByClass(iconContainer, "img", "Icon") ;
	eXo.desktop.UIDesktop.showHideWindow(popup, children[index + 1]) ;
} ;
	
UIWindow.prototype.mousedownOnResizeArea = function(evt) {
	var DOMUtil = eXo.core.DOMUtil ;
	var UIWindow = eXo.desktop.UIWindow ;
	var uiPageDesktop = document.getElementById("UIPageDesktop") ;
	var popup = eXo.core.DOMUtil.findAncestorByClass(this, "UIDragObject") ;
	popup.maximized = false ;
	var uiApplication = DOMUtil.findFirstDescendantByClass(popup, "div", "UIApplication") ;
//  Can only resize when the window is NOT maximized
	if(!popup.maximized) {
		if(eXo.core.Browser.isIE6()) {
			popup.originalUIApplicationWidth = uiApplication.offsetWidth ;
		}
		
		UIWindow.resizableObject = new Array() ;
		var uiResizableBlock = DOMUtil.findDescendantsByClass(popup, "div", "UIResizableBlock") ;
		if(uiApplication != null) UIWindow.resizableObject.push(uiApplication) ;
		if(uiResizableBlock.length > 0) {
		    for(var i = 0; i < uiResizableBlock.length; i++) {
		      eXo.desktop.UIWindow.resizableObject.push(uiResizableBlock[i]) ;
		    }
		  }
		  
		UIWindow.originalMouseXInDesktop = eXo.core.Browser.findMouseRelativeX(uiPageDesktop, evt) ;
		UIWindow.originalMouseYInDesktop = eXo.core.Browser.findMouseRelativeY(uiPageDesktop, evt) ;
		UIWindow.backupObjectProperties(popup, UIWindow.resizableObject) ;
		UIWindow.dragObject = this ;
		uiPageDesktop.onmousemove = UIWindow.resizeWindow ;
	}
} ;

 UIWindow.prototype.mouseupOnUIPageDesktop = function() {
	var uiPageDesktop = document.getElementById("UIPageDesktop") ;
  uiPageDesktop.onmousemove = null ;
} ;  


UIWindow.prototype.maximizeWindow = function(windowObject, clickedElement) {
	var DOMUtil = eXo.core.DOMUtil ;
	var UIWindow = eXo.desktop.UIWindow ;
  var uiPageDesktop = document.getElementById("UIPageDesktop") ;
  var desktopWidth = uiPageDesktop.offsetWidth  ;
  var desktopHeight = uiPageDesktop.offsetHeight  ;
  var applicationMinWidth = DOMUtil.findFirstDescendantByClass(windowObject, "div", "ApplicationMinWidth") ;
  var uiApplication = DOMUtil.findFirstDescendantByClass(windowObject, "div", "UIApplication") ;
  var uiResizableBlock = DOMUtil.findDescendantsByClass(windowObject, "div", "UIResizableBlock") ;

  var resizableObject = new Array() ;
  var tables = DOMUtil.findDescendantsByTag(windowObject, "table", resizableObject);
  if(uiApplication != null) resizableObject.push(uiApplication) ;
  if(uiResizableBlock != null) {
    for(var i = 0; i < uiResizableBlock.length; i++) {
      resizableObject.push(uiResizableBlock[i]) ;
    }
  }
  
  if(!windowObject.maximized) {
  	// Maximize...
  	if(applicationMinWidth) {
  		windowObject.backupApplicationMinWidth = applicationMinWidth.offsetWidth ;
    	applicationMinWidth.style.width = "auto" ;
  	}
  	
    UIWindow.backupObjectProperties(windowObject, resizableObject) ;
    windowObject.style.top = "0px" ;
    windowObject.oldY = 0;
    windowObject.style.left = "0px" ;
    windowObject.oldX = 0;
    windowObject.style.width = desktopWidth + "px" ;
    windowObject.oldW = desktopWidth;
    windowObject.style.height = "auto" ;
    
    for(var i = 0; i < resizableObject.length; i++) {
    	if (resizableObject[i].nodeName.toLowerCase() == "table") {
    		resizableObject[i].style.height = "auto" ;
    	} else {
    		resizableObject[i].style.height = (eXo.core.Browser.getBrowserHeight() - 50) + "px" ;
    	}
    }
   
    
    windowObject.maximized = true ;
    clickedElement.className = "ControlIcon RestoreIcon" ;
    
    if(eXo.core.Browser.isIE6()) {
    	windowObject.backupUIApplicationWidth = uiApplication.offsetWidth ;
    	uiApplication.style.width = "auto" ;
    }
		
  } else {
  	// Demaximize...
    windowObject.style.top = UIWindow.posY + "px" ;
    windowObject.oldY = UIWindow.posY ;
    windowObject.style.left = UIWindow.posX + "px" ;
    windowObject.oldX = UIWindow.posX ;
    windowObject.style.width = UIWindow.originalWidth + "px" ;
    windowObject.oldW = UIWindow.originalWidth ;
    windowObject.maximized = false ;
    for(var i = 0; i < resizableObject.length; i++) {
      resizableObject[i].style.height = resizableObject[i].originalHeight + "px" ;
    }
    clickedElement.className = "ControlIcon MaximizedIcon" ;
    if(eXo.core.Browser.isIE6()) {
    	uiApplication.style.width = windowObject.backupUIApplicationWidth + "px" ;
    }
    if(applicationMinWidth) {
    	applicationMinWidth.style.width = "auto" ;
    	applicationMinWidth.style.height = "auto" ;
    }
  }
	eXo.portal.UIPortalControl.initAllManagers() ;
} ;

UIWindow.prototype.backupObjectProperties = function(windowPortlet, resizableComponents) {
	var UIWindow = eXo.desktop.UIWindow ;
  for(var i = 0; i < resizableComponents.length; i++) {
    resizableComponents[i].originalWidth = resizableComponents[i].offsetWidth ;
    resizableComponents[i].originalHeight = resizableComponents[i].offsetHeight ;
  }
  
  UIWindow.posX = eXo.desktop.UIDesktop.findPosXInDesktop(windowPortlet) ;
  UIWindow.posY = eXo.desktop.UIDesktop.findPosYInDesktop(windowPortlet) ;
  UIWindow.originalWidth = windowPortlet.offsetWidth ;
  UIWindow.originalHeight = windowPortlet.offsetHeight ;
} ;

UIWindow.prototype.initDND = function(e) {
	var DOMUtil = eXo.core.DOMUtil ;
  var DragDrop = eXo.core.DragDrop ;
  var clickBlock = this ;
  var dragBlock = DOMUtil.findAncestorByClass(this, "UIDragObject") ;
  
  if(!dragBlock.maximized) {
  	// Can drag n drop only when the window is NOT maximized
	  var uiPageDesktop = document.getElementById("UIPageDesktop") ;
	  var uiPageDesktopX = eXo.core.Browser.findPosX(uiPageDesktop) ;
	  
	  /*Fix Bug On IE7, It's always double the value returned*/
	  if((eXo.core.Browser.getBrowserType() == "ie") && (!eXo.core.Browser.isIE6())) {
	  	uiPageDesktopX = uiPageDesktopX / 2 ;
	  }
	  
		var uiApplication = DOMUtil.findFirstDescendantByClass(dragBlock, "div", "UIApplication");
		var hiddenElements = new Array() ;
		
	  DragDrop.initCallback = function(dndEvent) {
	  	// A workaround to make the window go under the workspace panel during drag
	  	if (eXo.core.Browser.getBrowserType() == "mozilla" && DOMUtil.getStyle(uiApplication, "overflow") == "auto") {
	  		hiddenElements.push(uiApplication) ;
	  		uiApplication.style.overflow = "hidden" ;
	  	}
	  	uiAppDescendants = DOMUtil.findDescendantsByTagName(uiApplication, "div");
	  	for (var i=0; i<uiAppDescendants.length; i++) {
	  		if (DOMUtil.getStyle(uiAppDescendants[i], "overflow") == "auto") {
	  			hiddenElements.push(uiAppDescendants[i]) ;
	  			uiAppDescendants[i].style.overflow = "hidden" ;
	  		}
	  	}
	  } ;
	
	  DragDrop.dragCallback = function(dndEvent) {
	    var dragObject = dndEvent.dragObject ;
	    var dragObjectY = eXo.core.Browser.findPosY(dragObject) ;
	    var browserHeight = eXo.core.Browser.getBrowserHeight() ;
	    var browserWidth = eXo.core.Browser.getBrowserWidth() ;
	    var mouseX = eXo.core.Browser.findMouseXInPage(dndEvent.backupMouseEvent) ;
	    	    
	    if(dragObjectY < 0) {
	      dragObject.style.top = "0px" ;
	      document.onmousemove = DragDrop.onDrop ; /*Fix Bug On IE6*/
	    }
	    
	    if(dragObjectY > (browserHeight - 25)) {
	      dragObject.style.top = (browserHeight - 25) + "px" ;
	      document.onmousemove = DragDrop.onDrop ; /*Fix Bug On IE6*/
	    }
	    
	    if((mouseX < uiPageDesktopX) || (mouseX > browserWidth)) {
	      document.onmousemove = DragDrop.onDrop ;
	    }
	    
	  } ;
	
	  DragDrop.dropCallback = function(dndEvent) {
	  	var dragObject = dndEvent.dragObject ;
	  	// A workaround to make the window properly resizable after drop
	  	for (var i = 0; i < hiddenElements.length; i++) {
	  		hiddenElements[i].style.overflow = "auto" ;
	  	}
	  } ;
	  DragDrop.init(null, clickBlock, dragBlock, e) ;
	}
} ;

UIWindow.prototype.resizeWindow = function(e) {
	var UIWindow = eXo.desktop.UIWindow;
	var DOMUtil = eXo.core.DOMUtil ;
	var uiPageDesktop = document.getElementById("UIPageDesktop") ;
	var mouseXInDesktop = eXo.core.Browser.findMouseRelativeX(uiPageDesktop, e) ;
	var mouseYInDesktop = eXo.core.Browser.findMouseRelativeY(uiPageDesktop, e) ;
	var deltaX = mouseXInDesktop - UIWindow.originalMouseXInDesktop ;
	var deltaY = mouseYInDesktop - UIWindow.originalMouseYInDesktop ;
	var uiWindow = DOMUtil.findAncestorByClass(UIWindow.dragObject, "UIWindow") ;
	var uiApplication = DOMUtil.findFirstDescendantByClass(uiWindow, "div", "UIApplication") ;
	var applicationMinWidth = DOMUtil.findFirstDescendantByClass(uiWindow, "div", "ApplicationMinWidth") ;

	var hasResizableClass = DOMUtil.hasDescendantClass(uiApplication, "UIResizableBlock")	;
	if(hasResizableClass) {
		uiApplication.style.overflow = "hidden" ;
	}
	
	if(applicationMinWidth) {
		if(uiWindow.offsetWidth > uiWindow.applicationOriginalWidth) {
			applicationMinWidth.style.width = "auto" ;
		} else {
			applicationMinWidth.style.width = uiWindow.applicationOriginalWidth + "px" ;
		}
	}
		
	var windowMinWidth = 250 ;
	if((UIWindow.originalWidth + deltaX) < windowMinWidth) {
		uiWindow.style.width = windowMinWidth + "px" ;
	} else {
		uiWindow.style.width = (UIWindow.originalWidth + deltaX) + "px" ;
		/*Fix Bug On IE6*/
		if(eXo.core.Browser.isIE6()) {
			uiApplication.style.width = (uiWindow.originalUIApplicationWidth + deltaX) + "px" ;
		}
	}
	
	uiWindow.style.height = (UIWindow.originalHeight + deltaY) + "px" ;
	uiWindow.style.overflowY = "visible" ;
	for(var i = 0; i < UIWindow.resizableObject.length; i++) {
		if((UIWindow.resizableObject[i].originalHeight + deltaY) < UIWindow.resizableObject[i].minHeight) {
			return ;
		} else {
			UIWindow.resizableObject[i].style.height = (UIWindow.resizableObject[i].originalHeight + deltaY) + "px" ;
		}		
	}
	eXo.portal.UIPortalControl.initAllManagers() ;
} ;

UIWindow.prototype.onControlOver = function(element, isOver) {
  var originalElementName = element.className ;
  if(isOver) {
    var overElementName = "ControlIcon Over" + originalElementName.substr(originalElementName.indexOf(" ") + 1, 30) ;
    element.className   = overElementName;
    if(element.className == "ControlIcon OverRestoreIcon"){ element.title = "Restore Down" ;}
    if(element.className == "ControlIcon OverMaximizedIcon"){element.title = "Maximize" ;}
  } else {
    var over = originalElementName.indexOf("Over") ;
    if(over >= 0) {
      var overElementName = "ControlIcon " + originalElementName.substr(originalElementName.indexOf(" ") + 5, 30) ;
      element.className   = overElementName ;
    }
  }
} ;

UIWindow.prototype.saveWindowProperties = function(object, appStatus) {
	var DOMUtil = eXo.core.DOMUtil ;
	var uiPage = DOMUtil.findAncestorByClass(object, "UIPage") ;
	var uiPageIdNode = DOMUtil.findFirstDescendantByClass(uiPage, "div", "id") ;
	containerBlockId = uiPageIdNode.innerHTML ;
	
	var params ;
	if(!appStatus) {
	  params = [
	  	{name : "objectId", value : object.id},
	  	{name : "posX", value : object.offsetLeft},
	  	{name : "posY", value : object.offsetTop},
	  	{name : "zIndex", value : object.style.zIndex},
	  	{name : "windowWidth", value : object.offsetWidth},
		  {name : "windowHeight", value : object.offsetHeight}
	  ] ;
	} else {
		params = [
	  	{name : "objectId", value : object.id},
		  {name : "appStatus", value : appStatus}
	  ] ;
	}
	
	ajaxAsyncGetRequest(eXo.env.server.createPortalURL(containerBlockId, "SaveWindowProperties", true, params), false) ;
} ;

eXo.desktop.UIWindow = new UIWindow() ;
