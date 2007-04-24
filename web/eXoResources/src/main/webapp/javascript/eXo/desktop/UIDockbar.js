function UIDockbar() {
	this.curve = 3 ;
	this.weight = 2.3 ;
	this.isFirstTime = true;
	this.displayTooltip = true ;
	this.showDesktop = false ;
};

UIDockbar.prototype.init = function() {
	var UIDockbar = eXo.desktop.UIDockbar ;
	var uiDockbar = document.getElementById("UIDockBar") ;
	var imgObject = eXo.core.DOMUtil.findDescendantsByClass(uiDockbar, "img", "Icon") ;
	
	this.resetDefault = false ;
	this.onAnimation = false ;
	uiDockbar.defaultIconSize = 40 ;
	uiDockbar.originalBGDockbarHeight = 47 ;
	/*If this value is changed, need to synchronous with (.UIPageDesktop .UIDockBar .DockbarCenter) class*/
	
	if(imgObject.length > 0 && imgObject[0].onmousemove == undefined) this.isFirstTime = true;
	
	if(this.isFirstTime == true) {
		for(var i = 0; i < imgObject.length; i++) {
			imgObject[i].onmousemove = UIDockbar.onMouseMoveIcon ;
			imgObject[i].onmouseover = UIDockbar.showTooltip ;
			imgObject[i].onmouseout = UIDockbar.showTooltip ;
			
			srcImage = imgObject[i].src ;
			blankImage = imgObject[i].alt ;
			if(eXo.core.Browser.isIE6() && (imgObject[i].alt != "")) {
				imgObject[i].runtimeStyle.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+srcImage+"', sizingMethod='scale')" ;
				imgObject[i].src = blankImage ;
				imgObject[i].alt = "" ;
			}
		}
		this.isFirstTime = false ;
	}
	
	UIDockbar.resizeDockBar() ;
	uiDockbar.originalY = eXo.desktop.UIDesktop.findPosYInDesktop(uiDockbar) ;
	uiDockbar.originalDockbarHeight = uiDockbar.offsetHeight ;
	
	var portletsViewer = document.getElementById("PortletsViewer") ;
	
	portletsViewer.onclick = function() {
		UIDockbar.viewShowDesktop(portletsViewer) ;
	};
};

UIDockbar.prototype.viewShowDesktop = function(portletsViewer) {
	var uiPageDesktop = document.getElementById("UIPageDesktop");
  var children = eXo.core.DOMUtil.getChildrenByTagName(uiPageDesktop, "div");
  
  var blankImage = portletsViewer.src ;
  var srcMonitoringImage = "/eXoResources/skin/portal/webui/component/view/UIPageDesktop/DefaultSkin/icons/80x80/HideDesktop.png" ;
	var srcPortletsViewerImage = "/eXoResources/skin/portal/webui/component/view/UIPageDesktop/DefaultSkin/icons/80x80/PortletsViewer.png" ;

	if(this.showDesktop) {
		for(var j = 0; j < children.length; j++) {
			if(children[j].isShowed==true && children[j].className!="UIDockBar") {
				children[j].style.display = "block" ;
			}
	  }
	  if(eXo.core.Browser.isIE6()) {
	  	portletsViewer.runtimeStyle.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(src='" + srcMonitoringImage + "', sizingMethod='scale')" ;
	  	portletsViewer.src = blankImage ;
		} else {
			portletsViewer.src = srcMonitoringImage ;
		}
		this.showDesktop = false ;
	} else {
		for(var j = 0; j < children.length; j++) {
			if(children[j].className!="UIDockBar") {
				children[j].style.display = "none" ;
				if(children[j].isShowed) {
					this.showDesktop = true ;
				}
			}
	  }
	  if(this.showDesktop) {
  		if(eXo.core.Browser.isIE6()) {
				portletsViewer.src = blankImage ;
				portletsViewer.runtimeStyle.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(src='" + srcPortletsViewerImage + "', sizingMethod='scale')" ;
			} else {
				portletsViewer.src = srcPortletsViewerImage ;
			}
	  }
	}
	
  eXo.desktop.UIDockbar.containerMouseOver() ;
} ;

UIDockbar.prototype.onMouseMoveIcon = function(e) {
	var selectedIcon = this ;
	eXo.desktop.UIDockbar.animation(selectedIcon, e) ;
};

UIDockbar.prototype.showTooltip = function(e) {
	var UIDockbar = eXo.desktop.UIDockbar ;
	var object = this ;
	var uiDockbar = document.getElementById("UIDockBar") ;
	var objectXInDockbar = eXo.core.Browser.findPosXInContainer(object, uiDockbar) ;
	var iconContainer = document.getElementById("IconContainer") ;
	var tooltipObjects = eXo.core.DOMUtil.findChildrenByClass(iconContainer, "span", "Tooltip") ;
	var selectedIconIndex = UIDockbar.findIndex(object) ;
	
	if(UIDockbar.displayTooltip) {
		tooltipObjects[selectedIconIndex].style.display = "block" ;
		tooltipObjects[selectedIconIndex].style.top = (-tooltipObjects[selectedIconIndex].offsetHeight) + "px" ;
		tooltipObjects[selectedIconIndex].style.left = objectXInDockbar + "px" ;
		UIDockbar.displayTooltip = false ;
	} else {
		tooltipObjects[selectedIconIndex].style.display = "none" ;
		UIDockbar.displayTooltip = true ;
	}
};

UIDockbar.prototype.animation = function(selectedIcon, e) {	 
	var UIDockbar = eXo.desktop.UIDockbar ;
	eXo.desktop.UIDockbar.onAnimation = true ;
	var curve = UIDockbar.curve ;
	var weight = UIDockbar.weight ;
	
	var fixBugImageElement = document.getElementById("FixBug") ;
	
	var uiPageDesktop = document.getElementById("UIPageDesktop") ;
	var selectedIconX = eXo.desktop.UIDesktop.findPosXInDesktop(selectedIcon) ;
	var middleIcon = selectedIconX + (selectedIcon.offsetWidth / 2) ;
	var mouseX = eXo.core.Browser.findMouseRelativeX(uiPageDesktop, e) ;
	
	var d = middleIcon - selectedIconX ;
	var delta = middleIcon - mouseX ;
	var distanceWeight = (0.5 / curve) * (delta / d) ;
//	console.debug(selectedIconX) ;
	/*Mouse X Is Problem on IE7*/
	var selectedIconIndex = UIDockbar.findIndex(selectedIcon) ;
	var icons = eXo.core.DOMUtil.findChildrenByClass(selectedIcon.parentNode, "img", "Icon") ;
	var uiDockbar = document.getElementById("UIDockBar") ;
	var dockbarCenter = document.getElementById("DockbarCenter") ;
	
	uiDockbar.style.top = uiDockbar.originalY - (uiDockbar.defaultIconSize*(weight - 1)) + "px" ;
	
	fixBugImageElement.style.height = uiDockbar.defaultIconSize + (uiDockbar.defaultIconSize*(weight - 1)) + "px" ;
	
	uiDockbar.style.height = "auto" ;
	dockbarCenter.style.height = uiDockbar.originalBGDockbarHeight + (uiDockbar.defaultIconSize*(weight - 1)) + "px" ;
	
	for(var i = 0; i < icons.length; i++) {
		var deltaCurve = Math.abs(selectedIconIndex - i) ;
		var size = uiDockbar.defaultIconSize ;
		if(deltaCurve < curve) {
			if(i == selectedIconIndex) {
				size = Math.round(uiDockbar.defaultIconSize + 
							 uiDockbar.defaultIconSize * (weight - 1) * ((curve - deltaCurve) / curve - Math.abs(distanceWeight))) ;
				distanceWeight *= -1 ;
//  			window.status = "size : " + size ;
			} else {
				size = Math.round(uiDockbar.defaultIconSize + 
				uiDockbar.defaultIconSize * (weight - 1) * ((curve - deltaCurve) / curve + distanceWeight)) ;
			}
		}
		
//		window.status = "SIZE: " + size ;
//		console.warn(size);
		
		icons[i].style.width = size + "px" ;
		icons[i].style.height = size + "px" ;
	}
	
	UIDockbar.resizeDockBar() ;
};

UIDockbar.prototype.findIndex = function(object) {
	var icons = eXo.core.DOMUtil.findChildrenByClass(object.parentNode, "img", "Icon") ;
	for(var i = 0; i < icons.length; i++) {
		if(icons[i] == object) return i ;
	}
};

UIDockbar.prototype.containerMouseOver = function() {
	uiPageDesktop = document.getElementById("UIPageDesktop") ;
	uiPageDesktop.onmousemove = eXo.desktop.UIDockbar.mouseMoveOnDesktop ;		
};

UIDockbar.prototype.mouseMoveOnDesktop = function(e) {
	var uiPageDesktop = this ;
	var uiDockbar = document.getElementById("UIDockBar") ;
	var iconContainer = document.getElementById("IconContainer") ;
	var out = eXo.desktop.UIDockbar.isOut(iconContainer, uiPageDesktop, e) ;
	if(out) {
		eXo.desktop.UIDockbar.reset() ;
		uiPageDesktop.onmousemove = null ;
	}
};

UIDockbar.prototype.isOut = function(object, container, e) {
	var mouseXInContainer = eXo.core.Browser.findMouseRelativeX(container, e) ;
	var mouseYInContainer = eXo.core.Browser.findMouseRelativeY(container, e) ;
	
	var objectX  = eXo.desktop.UIDesktop.findPosXInDesktop(object) ;
	var objectY  = eXo.desktop.UIDesktop.findPosYInDesktop(object) ;
	
	var objectPosRight = objectX + object.offsetWidth ;
	var objectPosBottom = objectY + object.offsetHeight ;
	
	if((mouseXInContainer < objectX) || (mouseXInContainer > objectPosRight) ||
	   (mouseYInContainer < objectY) || (mouseYInContainer > objectPosBottom)) {
		return true ;
	} else {
		return false ;
	}	
};

UIDockbar.prototype.reset = function() {
	var UIDockbar = eXo.desktop.UIDockbar ;
	eXo.desktop.UIDockbar.resetDefault = true ;
	var uiDockbar = document.getElementById("UIDockBar") ;
	var dockbarCenter = document.getElementById("DockbarCenter") ;
	uiDockbar.style.height = uiDockbar.originalDockbarHeight + "px" ;
	dockbarCenter.style.height = uiDockbar.originalBGDockbarHeight + "px" ;
	
	var iconContainer = document.getElementById("IconContainer") ;
	var icons = eXo.core.DOMUtil.findChildrenByClass(iconContainer, "img", "Icon") ;
	for(var i = 0; i < icons.length; i++) {
		icons[i].style.width = uiDockbar.defaultIconSize + "px" ;
		icons[i].style.height = uiDockbar.defaultIconSize + "px" ;
	}
	var fixBugImageElement = document.getElementById("FixBug") ;
	fixBugImageElement.style.height = uiDockbar.defaultIconSize + "px" ;
	
	UIDockbar.resizeDockBar() ;
};

UIDockbar.prototype.resizeDockBar = function() {
	var DOMUtil =  eXo.core.DOMUtil ;
	var uiPageDesktop = document.getElementById("UIPageDesktop") ;
	var uiDockbar = document.getElementById("UIDockBar") ;
	var iconContainer = document.getElementById("IconContainer") ;
	
	var icons = DOMUtil.findChildrenByClass(iconContainer, "img", "Icon") ;
	var widthItemControl = 0 ;
	for(var i = 0; i < icons.length; i++) {
		widthItemControl = (widthItemControl + icons[i].offsetWidth + 5) ;
	}
		
	var separators = DOMUtil.findChildrenByClass(iconContainer, "img", "Separator") ;
	var totalWidthSeparators = 0 ;
	for(var i = 0; i < separators.length; i++) {
		totalWidthSeparators = totalWidthSeparators + separators[i].offsetWidth + 10 ;
		/* 10 is the total of margin left and right of each separator*/
	}
	
	iconContainer.style.width = (widthItemControl + totalWidthSeparators + 10) + "px" ;
	uiDockbar.style.width = (iconContainer.offsetWidth + 10) + "px" ;
	
	uiDockbar.style.position = "absolute" ;
	if(eXo.desktop.UIDockbar.resetDefault) {
		uiDockbar.style.top = uiDockbar.originalY + "px" ;
		eXo.desktop.UIDockbar.resetDefault = false ;
	} else if(eXo.desktop.UIDockbar.onAnimation) {
		/*Top of UIDockbar was seted on function animation*/
		eXo.desktop.UIDockbar.onAnimation = false ;
	} else {
//		uiDockbar.style.top = (uiPageDesktop.offsetHeight - uiDockbar.offsetHeight) + "px" ;
		uiDockbar.style.top = (uiPageDesktop.offsetHeight - uiDockbar.originalBGDockbarHeight) + "px" ;
	}
	
	uiDockbar.style.left = ((uiPageDesktop.offsetWidth - uiDockbar.offsetWidth) / 2) + "px" ;
};

UIDockbar.prototype.resetDesktopShowedStatus = function(uiPageDesktop, uiDockBar) {
	var uiPageDesktopChildren = eXo.core.DOMUtil.getChildrenByTagName(uiPageDesktop, "div") ;
	for(var i = 0; i < uiPageDesktopChildren.length; i++) {
		if(uiPageDesktopChildren[i].isShowed == true && uiPageDesktopChildren[i].style.display == "none") {
			uiPageDesktopChildren[i].isShowed = false ;
		}
	}
	if(this.showDesktop) {
		var portletsViewer = eXo.core.DOMUtil.findDescendantById(uiDockBar, "PortletsViewer") ;
		var blankImage = portletsViewer.src ;
		var srcMonitoringImage = "/eXoResources/skin/portal/webui/component/view/UIPageDesktop/DefaultSkin/icons/80x80/HideDesktop.png" ;
		if(eXo.core.Browser.isIE6()) {
	  	portletsViewer.runtimeStyle.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(src='" + srcMonitoringImage + "', sizingMethod='scale')" ;
	  	portletsViewer.src = blankImage ;
		} else {
			portletsViewer.src = srcMonitoringImage ;
		}
		this.showDesktop = false ;
	}
};

UIDockbar.prototype.createApplicationIcon = function(iconUrl, iconId) {
	var appIcon = document.createElement("img") ;
	appIcon.className = "Icon" ;
	appIcon.alt = "/eXoResources/background/DefaultSkin/Blank.gif" ;
	appIcon.src = iconUrl ;
	appIcon.id = iconId ;
	appIcon.style.marginRight = "4px" ;
	
	if(eXo.core.Browser.isIE6()) {
		appIcon.runtimeStyle.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+iconUrl+"', sizingMethod='scale')" ;
		appIcon.src = "/eXoResources/background/DefaultSkin/Blank.gif" ;
		appIcon.alt = "" ;
	}
	
	appIcon.onmousemove = eXo.desktop.UIDockbar.onMouseMoveIcon ;
	appIcon.onmouseover = eXo.desktop.UIDockbar.showTooltip ;
	appIcon.onmouseout = eXo.desktop.UIDockbar.showTooltip ;
	
	return appIcon ;
} ;

UIDockbar.prototype.createApplicationTooltip = function(tooltip) {
	var appTooltip = document.createElement("span") ;
	appTooltip.className = "Tooltip" ;
	appTooltip.style.display = "none" ;
	appTooltip.innerHTML = tooltip ;
	
	return appTooltip ;
}

eXo.desktop.UIDockbar = new UIDockbar() ;