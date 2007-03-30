eXo.require('eXo.webui.UIPopup');
function UIWindow() {

} ;

UIWindow.prototype.init = function(popup, isShow, posX, posY, minWidth) {
	var DOMUtil = eXo.core.DOMUtil ;
	this.superClass = eXo.webui.UIPopup ;
	var UIWindow = eXo.desktop.UIWindow ;
	popup.maximized = false;
	if(typeof(popup) == "string") popup = document.getElementById(popup) ;
		
	var uiPageDesktop = document.getElementById("UIPageDesktop") ;
	var uiApplication = DOMUtil.findFirstDescendantByClass(popup, "div", "UIApplication") ;
		
	var applicationMinWidth = DOMUtil.findFirstDescendantByClass(popup, "div", "ApplicationMinWidth") ;

  if(applicationMinWidth) {
  	if(minWidth) {
  		popup.style.width = (minWidth + 8) + "px" ;
	  	applicationMinWidth.style.width = minWidth + "px" ;
	  	popup.applicationOriginalWidth = minWidth ;
  	} else {
//  		applicationMinWidth.style.width = "720px" ;
  		popup.applicationOriginalWidth = 720 ;
  		popup.style.width = "720px" ;
  	}
  	
  	if(DOMUtil.hasDescendantClass(uiApplication, "UIResizableBlock")) {
  		applicationMinWidth.style.width = "auto" ;
		}
  }
  
  /*Fix Bug On IE6*/
	if(eXo.core.Browser.isIE6()) {
		uiApplication.style.width = (popup.offsetWidth - 8) + "px" ;
	}
	
	popup.style.zIndex = ++zIndex ;
	
	popup.onmousedown = function() {
		eXo.desktop.UIDesktop.resetZIndex(this) ;
	}

	var windowPortletInfo = DOMUtil.findFirstDescendantByClass(popup, "div", "WindowPortletInfo") ;
	this.superClass.setPosition(popup, posX, posY) ;
	try {
		windowPortletInfo.onmousedown = this.initDND ;
	} catch(err) {
		alert(err);
	}
	
	var windowPortletControl = DOMUtil.findFirstDescendantByClass(popup, "div", "WindowPortletControl");
	var minimizedIcon = DOMUtil.findFirstDescendantByClass(windowPortletControl, "div", "MinimizedIcon");
	this.maximizedIcon = DOMUtil.findFirstDescendantByClass(windowPortletControl, "div", "MaximizedIcon");
	
	minimizedIcon.onclick = function() {
		var index = 0 ;
		var windows = DOMUtil.getChildrenByTagName(popup.parentNode, "div") ;
		for(var j = 0; j < windows.length; j++) {
			if(popup == windows[j]) {
				index = j ;
				break ;
			}
		}
		
		var iconContainer = document.getElementById("IconContainer");
		var children = DOMUtil.findChildrenByClass(iconContainer, "img", "Icon");
		eXo.desktop.UIDesktop.showHideWindow(popup, children[index + 1]);
	}
	
	this.maximizedIcon.onclick = function() {
		UIWindow.maximizeWindow(popup, this) ;
	}
	
	var resizeArea = DOMUtil.findFirstDescendantByClass(popup, "div", "ResizeArea") ;
	
	resizeArea.onmousedown = function(e) {
		// Can only resize when the window is NOT maximized
		if (!popup.maximized) {
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
	    
	    UIWindow.originalMouseXInDesktop = eXo.core.Browser.findMouseRelativeX(uiPageDesktop, e) ;
	    UIWindow.originalMouseYInDesktop = eXo.core.Browser.findMouseRelativeY(uiPageDesktop, e) ;
	    
	    UIWindow.backupObjectProperties(popup, UIWindow.resizableObject) ;
	    
	    UIWindow.dragObject = this ;
	    uiPageDesktop.onmousemove = UIWindow.resizeWindow ;
		}
  } ;
  
  uiPageDesktop.onmouseup = function() {
    uiPageDesktop.onmousemove = null ;
  } ;
  
  UIWindow.windowMinHeight = popup.offsetHeight ;
} ;

UIWindow.prototype.maximizeWindow = function(windowObject, clickedElement) {
	var UIWindow = eXo.desktop.UIWindow ;
	var DOMUtil = eXo.core.DOMUtil ;
  var uiPageDesktop = document.getElementById("UIPageDesktop") ;
  var desktopWidth = uiPageDesktop.offsetWidth ;
  var desktopHeight = uiPageDesktop.offsetHeight ;
  var applicationMinWidth = DOMUtil.findFirstDescendantByClass(windowObject, "div", "ApplicationMinWidth") ;
  
  var resizableObject = new Array() ;
  var uiApplication = DOMUtil.findFirstDescendantByClass(windowObject, "div", "UIApplication") ;
  var uiResizableBlock = DOMUtil.findDescendantsByClass(windowObject, "div", "UIResizableBlock") ;
  
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
      if(resizableObject[i] == uiApplication) {
        resizableObject[i].style.height = (eXo.core.Browser.getBrowserHeight() - 50) + "px" ;
//        resizableObject[i].style.width = (desktopWidth - 20) + "px" ;
      } else {
        resizableObject[i].style.height = (eXo.core.Browser.getBrowserHeight() - 174) + "px" ;
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
    windowObject.oldY = UIWindow.posY;
    windowObject.style.left = UIWindow.posX + "px" ;
    windowObject.oldX = UIWindow.posX;
    windowObject.style.width = UIWindow.originalWidth + "px" ;
    windowObject.oldW = UIWindow.originalWidth;
    windowObject.maximized = false;
    for(var i = 0; i < resizableObject.length; i++) {
//      resizableObject[i].style.width = resizableObject[i].originalWidth + "px" ;
      resizableObject[i].style.height = resizableObject[i].originalHeight + "px" ;
    }
    clickedElement.className = "ControlIcon MaximizedIcon" ;
    if(eXo.core.Browser.isIE6()) {
    	uiApplication.style.width = windowObject.backupUIApplicationWidth + "px" ;
    }
    if(applicationMinWidth) {
    	applicationMinWidth.style.width = windowObject.backupApplicationMinWidth + "px" ;
    }
  }

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
  var DragDrop = eXo.core.DragDrop ;
  var clickBlock = this ;
  var dragBlock = eXo.core.DOMUtil.findAncestorByClass(this, "UIDragObject") ;
  if (!dragBlock.maximized) {
  	// Can drag n drop only when the window is NOT maximized
	  var uiPageDesktop = document.getElementById("UIPageDesktop") ;
	  var uiPageDesktopX = eXo.core.Browser.findPosX(uiPageDesktop) ;
	
	  DragDrop.initCallback = function (dndEvent) {
	  }
	
	  DragDrop.dragCallback = function (dndEvent) {
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
	    
	  }
	
	  DragDrop.dropCallback = function (dndEvent) {
	  }
	  
	  
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
//			if(hasResizableClass) {
//				uiApplication.style.clip = "auto" ;
//			} else {
//				uiApplication.style.overflow = null ;
//			}
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
	
//	if((UIWindow.originalHeight + deltaY) < UIWindow.windowMinHeight) {
//		uiWindow.style.height = UIWindow.windowMinHeight + "px" ;
//	} else {
//		uiWindow.style.height = (UIWindow.originalHeight + deltaY) + "px" ;
//	}
	
	for(var i = 0; i < UIWindow.resizableObject.length; i++) {
		if((UIWindow.resizableObject[i].originalHeight + deltaY) < UIWindow.resizableObject[i].minHeight) {
			return ;
//			eXo.webui.UIWindow.resizableObject[i].style.height = eXo.webui.UIWindow.resizableObject[i].minHeight + "px" ;
		} else {
			UIWindow.resizableObject[i].style.height = (UIWindow.resizableObject[i].originalHeight + deltaY) + "px" ;
		}		
	}
} ;

UIWindow.prototype.onControlOver = function(element, isOver) {
  var originalElementName = element.className ;
  if(isOver) {
    var overElementName = "ControlIcon Over" + originalElementName.substr(originalElementName.indexOf(" ") + 1, 30);
    element.className   = overElementName;
  } else {
    var over = originalElementName.indexOf("Over");
    if(over >= 0) {
      var overElementName = "ControlIcon " + originalElementName.substr(originalElementName.indexOf(" ") + 5, 30);
      element.className   = overElementName;
    }
  }
};

eXo.desktop.UIWindow = new UIWindow() ;
