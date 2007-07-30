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
  if(!uiDockbar) return ;
  var imgObject = eXo.core.DOMUtil.findDescendantsByClass(uiDockbar, "img", "Icon") ;
  
  this.resetDefault = false ;
  this.onAnimation = false ;
  uiDockbar.defaultIconSize = 40 ;
  uiDockbar.originalBGDockbarHeight = 47 ;
  /*If this value is changed, need to synchronous with (.UIPageDesktop .UIDockBar .DockbarCenter) class*/
  
  if(imgObject.length > 0 && imgObject[0].onmousemove == undefined) this.isFirstTime = true ;
  
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
  uiDockbar.originalDockbarHeight = uiDockbar.offsetHeight ;
  
  var portletsViewer = document.getElementById("PortletsViewer") ;
  var widgetsViewer = document.getElementById("WidgetsViewer") ;
  portletsViewer.onclick = this.viewPortlets ;
  widgetsViewer.onclick = this.viewWidgets ;

} ;
/*
 * minh.js.exo
 */
UIDockbar.prototype.viewPortlets = function() {
	var uiPageDesktop = document.getElementById("UIPageDesktop") ;
  var children = eXo.core.DOMUtil.findDescendantsByClass(uiPageDesktop, "div", "UIWindow") ; 
	var srcMonitoringImage = "/eXoResources/skin/DefaultSkin/portal/webui/component/view/UIPageDesktop/icons/80x80/Hide"+this.id+".png" ;
  var srcPortletsViewerImage = "/eXoResources/skin/DefaultSkin/portal/webui/component/view/UIPageDesktop/icons/80x80/Show"+this.id+".png" ;
	eXo.desktop.UIDockbar.showDesktop = false ;
	for(var i = 0; i < children.length; i++) {
		if (children[i].style.display == "block" ) {
			children[i].style.display = "none" ;
			children[i].isShowed = true ;
			eXo.desktop.UIDockbar.showDesktop = false ;
		} else {
			if (children[i].isShowed)	{
				children[i].style.display = "block" ;
				eXo.desktop.UIDockbar.showDesktop = true ;
			}
		}
	}
	
	if (eXo.desktop.UIDockbar.showDesktop) {
		if (eXo.core.Browser.isIE6()) {
			this.runtimeStyle.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(src='" + srcMonitoringImage + "', sizingMethod='scale')" ;
		} else {
			this.src = srcMonitoringImage ;
		}
	} else {
		if (eXo.core.Browser.isIE6()) {
			this.runtimeStyle.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(src='" + srcPortletsViewerImage + "', sizingMethod='scale')" ;
		} else {
			this.src = srcPortletsViewerImage ;
		}
	}
	eXo.desktop.UIDockbar.containerMouseOver() ;
} ;


UIDockbar.prototype.viewWidgets = function() {
	var uiPageDesktop = document.getElementById("UIPageDesktop") ;
  var children = eXo.core.DOMUtil.findDescendantsByClass(uiPageDesktop, "div", "UIWidget") ; 
	for(var i = 0; i < children.length; i++) {
		if (children[i].style.display != "none" ) {
			children[i].style.display = "none" ;
		} else {
			children[i].style.display = "block" ;
		}
	}
	eXo.desktop.UIDockbar.containerMouseOver() ; 
} ;

//TODO : minh.js.exo
//UIDockbar.prototype.viewShowDesktop = function(portletsViewer) {
//  var uiPageDesktop = document.getElementById("UIPageDesktop") ;
//  var children = eXo.core.DOMUtil.getChildrenByTagName(uiPageDesktop, "div") ;
//  var blankImage = portletsViewer.src ;
//  var srcMonitoringImage = "/eXoResources/skin/portal/webui/component/view/UIPageDesktop/DefaultSkin/icons/80x80/Hide"+portletsViewer.id+".png" ;
//  var srcPortletsViewerImage = "/eXoResources/skin/portal/webui/component/view/UIPageDesktop/DefaultSkin/icons/80x80/Show"+portletsViewer.id+".png" ;
//  var uiWidget = eXo.core.DOMUtil.findDescendantsByClass(uiPageDesktop, "div", "UIWidget") ;
//  if(uiWidget && portletsViewer.id == "WidgetsViewer") {
//  	var temp = this.showDesktop ;
//  	this.showDesktop = true ;
//    for(var i = 0; i < uiWidget.length; ++i) {
//      if(uiWidget[i].style.display != "none") {
//        this.showDesktop = false ;
//        break ;
//      }
//    }
//  }
//  
//  if(this.showDesktop) {
//    for(var j = 0; j < children.length; j++) {
//      if(children[j].className != "UIDockBar") {
//        if(uiWidget && portletsViewer.id == "WidgetsViewer") {
//          if (String(children[j].className).indexOf("UIWidget") >= 0)
//          children[j].style.display = "block" ;
//          this.showDesktop = temp ;
//        } else {
//          if(children[j].isShowed) {
//            if (String(children[j].className).indexOf("UIWidget") >= 0) continue ;
//            children[j].style.display = "block" ;
//          }
//          this.showDesktop = false ;
//        }
//      }
//    }
//    if(eXo.core.Browser.isIE6()) {
//      portletsViewer.runtimeStyle.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(src='" + srcMonitoringImage + "', sizingMethod='scale')" ;
//      portletsViewer.src = blankImage ;
//    } else {
//      portletsViewer.src = srcMonitoringImage ;
//    }
//  } else {
//
//    for(var j = 0; j < children.length; j++) {
//      if(children[j].className != "UIDockBar") {
//        if(portletsViewer.id == "PortletsViewer") {
//          if(String(children[j].className).indexOf("UIWidget") >= 0) continue ;
//          children[j].style.display = "none" ;        
//          if(children[j].isShowed) {  
//            this.showDesktop = true ;
//          }
//        } else {
//          if(String(children[j].className).indexOf("UIWidget") >= 0) {
//            children[j].style.display = "none" ;  
//            this.showDesktop = temp ;
//          }
//        }
//      }
//    }
//    if(eXo.core.Browser.isIE6()) {
//      portletsViewer.src = blankImage ;
//      portletsViewer.runtimeStyle.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(src='" + srcPortletsViewerImage + "', sizingMethod='scale')" ;
//    } else {
//      portletsViewer.src = srcPortletsViewerImage ;
//    }
//  }
//  eXo.desktop.UIDockbar.containerMouseOver() ;
//} ;

UIDockbar.prototype.onMouseMoveIcon = function(e) {
	eXo.desktop.UIDockbar.animation(this, e) ;
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
} ;


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
  var distanceWeight =  delta /(2*curve*d) ;
  
  var selectedIconIndex = UIDockbar.findIndex(selectedIcon) ;
  var icons = eXo.core.DOMUtil.findChildrenByClass(selectedIcon.parentNode, "img", "Icon") ;
  var uiDockbar = document.getElementById("UIDockBar") ;
  var dockbarCenter = document.getElementById("DockbarCenter") ;
  
  fixBugImageElement.style.height = uiDockbar.defaultIconSize + (uiDockbar.defaultIconSize*(weight - 1)) + "px" ;
  
	uiDockbar.style.bottom = "0px" ;  
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
      } else {
        size = Math.round(uiDockbar.defaultIconSize + 
        uiDockbar.defaultIconSize * (weight - 1) * ((curve - deltaCurve) / curve + distanceWeight)) ;
      }
    }
        
    icons[i].style.width = size + "px" ;
    icons[i].style.height = size + "px" ;
  }
  
  UIDockbar.resizeDockBar() ;
} ;

UIDockbar.prototype.findIndex = function(object) {
  var icons = eXo.core.DOMUtil.findChildrenByClass(object.parentNode, "img", "Icon") ;
  for(var i = 0; i < icons.length; i++) {
    if(icons[i] == object) return i ;
  }
} ;

UIDockbar.prototype.containerMouseOver = function() {
  uiPageDesktop = document.getElementById("UIPageDesktop") ;
  uiPageDesktop.onmousemove = eXo.desktop.UIDockbar.mouseMoveOnDesktop ;    
} ;

UIDockbar.prototype.mouseMoveOnDesktop = function(e) {
  var uiPageDesktop = this ;
  var uiDockbar = document.getElementById("UIDockBar") ;
  var iconContainer = document.getElementById("IconContainer") ;
  var out = eXo.desktop.UIDockbar.isOut(iconContainer, uiPageDesktop, e) ;
  if(out) {
    eXo.desktop.UIDockbar.reset() ;
    uiPageDesktop.onmousemove = null ;
  }
} ;

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
} ;

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
} ;

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
  uiDockbar.style.height = "auto" ;
  
  uiDockbar.style.position = "absolute" ;
  if(eXo.desktop.UIDockbar.resetDefault) {
    uiDockbar.style.bottom =   "0px" ;
    eXo.desktop.UIDockbar.resetDefault = false ;
  } else if(eXo.desktop.UIDockbar.onAnimation) {
 	   eXo.desktop.UIDockbar.onAnimation = false ;
  } else {
      uiDockbar.style.bottom =   "0px" ;
  }
  uiDockbar.style.left = ((uiPageDesktop.offsetWidth - uiDockbar.offsetWidth) / 2) + "px" ;
} ;

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
    var srcMonitoringImage = "/eXoResources/skin/DefaultSkin/portal/webui/component/view/UIPageDesktop/icons/80x80/HidePortletsViewer.png" ;
    if(eXo.core.Browser.isIE6()) {
      portletsViewer.runtimeStyle.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(src='" + srcMonitoringImage + "', sizingMethod='scale')" ;
      portletsViewer.src = blankImage ;
    } else {
      portletsViewer.src = srcMonitoringImage ;
    }
    this.showDesktop = false ;
  }
} ;

UIDockbar.prototype.createApplicationIcon = function(iconUrl, iconId) {
  var appIcon = document.createElement("img") ;
  appIcon.className = "Icon" ;
  appIcon.alt = "/eXoResources/skin/sharedImages/Blank.gif" ;
  appIcon.src = iconUrl ;
  appIcon.id = iconId ;
  appIcon.style.marginRight = "4px" ;
  
  if(eXo.core.Browser.isIE6()) {
    appIcon.runtimeStyle.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+iconUrl+"', sizingMethod='scale')" ;
    appIcon.src = "/eXoResources/skin/sharedImages/Blank.gif" ;
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
};

eXo.desktop.UIDockbar = new UIDockbar() ;