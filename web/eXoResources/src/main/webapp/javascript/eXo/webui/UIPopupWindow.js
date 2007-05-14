eXo.require('eXo.webui.UIPopup');

function UIPopupWindow() {	
};

UIPopupWindow.prototype.init = function(popupId, isShow, isResizable) {
	var DOMUtil = eXo.core.DOMUtil ;
	this.superClass = eXo.webui.UIPopup ;
	var popup = document.getElementById(popupId) ;
	var portalApp = document.getElementById("UIPortalApplication") ;
	if(popup == null) return;
	popup.style.visibility = "hidden" ;
	this.superClass.init(popup) ;
	
	var contentBlock = DOMUtil.findFirstDescendantByClass(popup, 'div' ,'Content');
	if((eXo.core.Browser.getBrowserHeight() - 100 ) < contentBlock.offsetHeight) {
		contentBlock.style.height = (eXo.core.Browser.getBrowserHeight() - 100) + "px";
	}

	var popupBar = DOMUtil.findFirstDescendantByClass(popup, 'div' ,'PopupTitle') ;

	popupBar.onmousedown = this.initDND ;
	
	if(isShow == false) this.superClass.hide(popup) ; 
	var popupCloseButton = DOMUtil.findFirstDescendantByClass(popup, 'div' ,'CloseButton') ;
	popupCloseButton.onmouseup = function() {
		DOMUtil.findAncestorByClass(this, "UIDragObject").style.display = "none" ;
	}
	
	if(isResizable) {
		var resizeBtn = DOMUtil.findFirstDescendantByClass(popup, "div", "ResizeButton");
		resizeBtn.style.display = 'block' ;
		resizeBtn.onmousedown = function(e) {
			portalApp.setAttribute("popupId", popupId);
			portalApp.onmousemove = eXo.webui.UIPopupWindow.resize;
		}
		portalApp.onmouseup = function(e) {
			portalApp.onmousemove = null;
		}
	}
	
	popup.style.visibility = "visible" ;
	if(isShow == true) this.show(popup);
	
};

UIPopupWindow.prototype.initDND = function(e) {
  var DragDrop = eXo.core.DragDrop ;
  var DOMUtil = eXo.core.DOMUtil ;
//  var overflowObjectList = new Array() ;

	DragDrop.initCallback = function (dndEvent) {
		var dragObject = dndEvent.dragObject ;
		dragObject.uiWindowContent = DOMUtil.findFirstDescendantByClass(dragObject, "div", "Content");
//		var elements = uiWindowContent.getElementsByTagName("div") ;
		dragObject.uiWindowContent.style.overflow = "hidden" ;
  }

  DragDrop.dragCallback = function (dndEvent) {
  }

  DragDrop.dropCallback = function (dndEvent) {
  	var dragObject = dndEvent.dragObject ;
  	dragObject.uiWindowContent.style.overflow = "auto" ;
  }
  
  var clickBlock = this ;
  var dragBlock = eXo.core.DOMUtil.findAncestorByClass(this, "UIDragObject") ;
  DragDrop.init(null, clickBlock, dragBlock, e) ;
};



UIPopupWindow.prototype.resize = function(e) {
	var targetPopup = document.getElementById(this.getAttribute("popupId"));
	var content = eXo.core.DOMUtil.findFirstDescendantByClass(targetPopup, "div", "Content");
	var pointerX = eXo.core.Browser.findMouseRelativeX(targetPopup, e);
	var pointerY = eXo.core.Browser.findMouseRelativeY(targetPopup, e);
	var delta = eXo.core.Browser.findPosYInContainer(content,targetPopup) +
							content.style.borderWidth + content.style.padding + content.style.margin ;
	if((1*pointerY-delta) > 0) content.style.height = (1*pointerY-delta)+"px";
	targetPopup.style.height = "auto";
	if(pointerX > 200) targetPopup.style.width = (pointerX+5) + "px";
};

UIPopupWindow.prototype.show = function(popup) {
	var DOMUtil = eXo.core.DOMUtil;
	if(typeof(popup) == "string") popup = document.getElementById(popup) ;
	var portalApp = document.getElementById("UIPortalApplication");
	
	var maskLayer = DOMUtil.findFirstDescendantByClass(portalApp, "div", "UIMaskWorkspace");
	zIndex = 0;
	var currZIndex = 0;
	if (maskLayer != null) {
		currZIndex = DOMUtil.getStyle(maskLayer, "zIndex");
		if (!isNaN(currZIndex) && currZIndex > zIndex) zIndex = currZIndex;
	}
	var popupWindows = DOMUtil.findDescendantsByClass(portalApp, "div", "UIPopupWindow");
	for (var i = 0; i<popupWindows.length; i++) {
		currZIndex = DOMUtil.getStyle(popupWindows[i], "zIndex");
		if (!isNaN(currZIndex) && currZIndex > zIndex) zIndex = currZIndex;
	}
  
	if (zIndex == 0) zIndex = 2000;
	// We don't increment zIndex here because it is done in the superClass.show function
	popup.style.visibility = "hidden";
	this.superClass.show(popup) ;
	var offsetParent = popup.offsetParent ;
	if(offsetParent) {
		popup.style.top = ((offsetParent.offsetHeight - popup.offsetHeight) / 2) + "px" ;
		popup.style.left = ((offsetParent.offsetWidth - popup.offsetWidth) / 2) + "px" ;
	}
	popup.style.visibility = "visible";
};

eXo.webui.UIPopupWindow = new UIPopupWindow();