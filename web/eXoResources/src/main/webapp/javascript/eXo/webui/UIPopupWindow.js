eXo.require('eXo.webui.UIPopup') ;

function UIPopupWindow() {	
} ;

UIPopupWindow.prototype.init = function(popupId, isShow, isResizable, showCloseButton) {
	var DOMUtil = eXo.core.DOMUtil ;
	this.superClass = eXo.webui.UIPopup ;
	var popup = document.getElementById(popupId) ;
	var portalApp = document.getElementById("UIPortalApplication") ;
	if(popup == null) return;
	popup.style.visibility = "hidden" ;
	this.superClass.init(popup) ;
	var contentBlock = DOMUtil.findFirstDescendantByClass(popup, 'div' ,'PopupContent');
	if((eXo.core.Browser.getBrowserHeight() - 100 ) < contentBlock.offsetHeight) {
		contentBlock.style.height = (eXo.core.Browser.getBrowserHeight() - 100) + "px";
	}
	var popupBar = DOMUtil.findFirstDescendantByClass(popup, 'div' ,'PopupTitle') ;

	popupBar.onmousedown = this.initDND ;
	
	if(isShow == false) this.superClass.hide(popup) ; 
	
	if(showCloseButton == true) {
		var popupCloseButton = DOMUtil.findFirstDescendantByClass(popup, 'div' ,'CloseButton') ;
		popupCloseButton.onmouseup = this.closePopupEvt ;
	}
	
	if(isResizable) {
		var resizeBtn = DOMUtil.findFirstDescendantByClass(popup, "div", "ResizeButton");
		resizeBtn.style.display = 'block' ;
		resizeBtn.onmousedown = this.startResizeEvt ;
		portalApp.onmouseup = this.endResizeEvt ; 
	}
	
	popup.style.visibility = "visible" ;
	if(isShow == true) this.show(popup) ;
	
} ;
//TODO: manage zIndex properties
UIPopupWindow.prototype.show = function(popup) {
	var DOMUtil = eXo.core.DOMUtil ;
	if(typeof(popup) == "string") popup = document.getElementById(popup) ;
	var portalApp = document.getElementById("UIPortalApplication") ;
	
	var maskLayer = DOMUtil.findFirstDescendantByClass(portalApp, "div", "UIMaskWorkspace") ;
	var zIndex = 0 ;
	var currZIndex = 0 ;
	if (maskLayer != null) {
		currZIndex = DOMUtil.getStyle(maskLayer, "zIndex") ;
		if (!isNaN(currZIndex) && currZIndex > zIndex) zIndex = currZIndex ;
	}
	var popupWindows = DOMUtil.findDescendantsByClass(portalApp, "div", "UIPopupWindow") ;
	var len = popupWindows.length ;
	for (var i = 0 ; i < len ; i++) {
		currZIndex = DOMUtil.getStyle(popupWindows[i], "zIndex") ;
		if (!isNaN(currZIndex) && currZIndex > zIndex) zIndex = currZIndex ;
	}
  
	if (zIndex == 0) zIndex = 2000 ;
	// We don't increment zIndex here because it is done in the superClass.show function
	popup.style.visibility = "hidden" ;
	this.superClass.show(popup) ;
	var offsetParent = popup.offsetParent ;
	if(offsetParent) {
		popup.style.top = ((offsetParent.offsetHeight - popup.offsetHeight) / 2) + "px" ;
		popup.style.left = ((offsetParent.offsetWidth - popup.offsetWidth) / 2) + "px" ;
	}
	popup.style.visibility = "visible" ;
} ;

UIPopupWindow.prototype.closePopupEvt = function(evt) {
	eXo.core.DOMUtil.findAncestorByClass(this, "UIDragObject").style.display = "none" ;
}

UIPopupWindow.prototype.startResizeEvt = function(evt) {
	var portalApp = document.getElementById("UIPortalApplication") ;
	portalApp.setAttribute("popupId", popupId);
	portalApp.onmousemove = eXo.webui.UIPopupWindow.resize;
}

UIPopupWindow.prototype.endResizeEvt = function(evt) {
	document.getElementById("UIPortalApplication").onmousemove = null;
}

UIPopupWindow.prototype.initDND = function(evt) {
  var DragDrop = eXo.core.DragDrop ;
  var DOMUtil = eXo.core.DOMUtil ;

	DragDrop.initCallback = function (dndEvent) {
		var dragObject = dndEvent.dragObject ;
		dragObject.uiWindowContent = DOMUtil.findFirstDescendantByClass(dragObject, "div", "PopupContent") ;
		if(eXo.core.Browser.browserType == "mozilla") {
			dragObject.uiWindowContent.style.overflow = "hidden" ;
		}
  }

  DragDrop.dragCallback = function (dndEvent) {
  }

  DragDrop.dropCallback = function (dndEvent) {
  	var dragObject = dndEvent.dragObject ;
		if(eXo.core.Browser.browserType == "mozilla") {
  		dragObject.uiWindowContent.style.overflow = "auto" ;
		}
  	
  	var DOMUtil = eXo.core.DOMUtil ;
  	var dragObjectY = parseInt(dragObject.style.top) ;		
		try {
			var uiWindows = DOMUtil.findAncestorsByClass(dragObject, "UIWindow") ;
			var len = uiWindows.length ;
			var mLen = dragObject.childNodes.length ;
			var isMessage = false ;
			for(var i = 0 ; i < mLen ; i++) {
				var className = dragObject.childNodes[i].className ;
				if (className && (className.indexOf("Message") > -1)) {
					isMessage = true ;
					break ;
				}
			}
			if (len > 0 && !isMessage) {
				var offsetTop = (0 - uiWindows[0].offsetTop) ;
				if (dragObjectY < offsetTop)	dragObject.style.top = offsetTop + "px" ;
			} else {
				if (dragObjectY < 0) dragObject.style.top = "0px" ;
			}
		} catch(err) {
			alert(err.message) ;
		} 	
  }
  var clickBlock = this ;
  var dragBlock = eXo.core.DOMUtil.findAncestorByClass(this, "UIDragObject") ;
  DragDrop.init(null, clickBlock, dragBlock, evt) ;
} ;

UIPopupWindow.prototype.resize = function(evt) {
	var targetPopup = document.getElementById(this.getAttribute("popupId")) ;
	var content = eXo.core.DOMUtil.findFirstDescendantByClass(targetPopup, "div", "PopupContent") ;
	var pointerX = eXo.core.Browser.findMouseRelativeX(targetPopup, evt) ;
	var pointerY = eXo.core.Browser.findMouseRelativeY(targetPopup, evt) ;
	var delta = eXo.core.Browser.findPosYInContainer(content,targetPopup) +
							content.style.borderWidth + content.style.padding + content.style.margin ;
	if((1*pointerY-delta) > 0) content.style.height = (1*pointerY-delta)+"px" ;
	targetPopup.style.height = "auto";
	if(pointerX > 200) targetPopup.style.width = (pointerX+5) + "px" ;
} ;

eXo.webui.UIPopupWindow = new UIPopupWindow();