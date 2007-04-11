eXo.require('eXo.webui.UIPopup');

function UIPopupWindow() {	
};

UIPopupWindow.prototype.init = function(popupId, isShow, isResizable) {
	this.superClass = eXo.webui.UIPopup ;
	var popup = document.getElementById(popupId) ;
	var portalApp = document.getElementById("UIPortalApplication") ;
	if(popup == null) return;
	popup.style.visibility = "hidden" ;
	this.superClass.init(popup) ;
	
	var contentBlock = eXo.core.DOMUtil.findFirstDescendantByClass(popup, 'div' ,'Content');
	if((eXo.core.Browser.getBrowserHeight() - 100 ) < contentBlock.offsetHeight) {
		contentBlock.style.height = (eXo.core.Browser.getBrowserHeight() - 100) + "px";
	}

	var popupBar = eXo.core.DOMUtil.findFirstDescendantByClass(popup, 'div' ,'PopupTitle') ;

	popupBar.onmousedown = this.initDND ;
	
	if(isShow == false) this.superClass.hide(popup) ; 
	var popupCloseButton = eXo.core.DOMUtil.findFirstDescendantByClass(popup, 'div' ,'CloseButton') ;
	popupCloseButton.onmouseup = function() {
		eXo.core.DOMUtil.findAncestorByClass(this, "UIDragObject").style.display = "none" ;
	}
	
	if(isResizable) {
		var resizeBtn = eXo.core.DOMUtil.findFirstDescendantByClass(popup, "div", "ResizeButton");
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
	var contentWindow = eXo.core.DOMUtil.findFirstDescendantByClass(targetPopup, "div", "UIWindowContent");
	var content = eXo.core.DOMUtil.findFirstDescendantByClass(contentWindow, "div", "Content");
	var pointerX = eXo.core.Browser.findMouseRelativeX(targetPopup, e);
	var pointerY = eXo.core.Browser.findMouseRelativeY(targetPopup, e);
	var delta = eXo.core.Browser.findPosYInContainer(content,targetPopup) +
							content.style.borderWidth + content.style.padding + content.style.margin +
							contentWindow.style.borderWidth + contentWindow.style.padding + contentWindow.style.margin;
	contentWindow.style.height = (1*pointerY-delta)+"px";
	content.style.height = (1*pointerY-delta)+"px";
	targetPopup.style.height = "auto";
	targetPopup.style.width = (pointerX+5) + "px";
};

UIPopupWindow.prototype.show = function(popup) {
	if(typeof(popup) == "string") popup = document.getElementById(popup) ;
	var portalApp = document.getElementById("UIPortalApplication");
	var maskLayer = eXo.core.DOMUtil.findFirstDescendantByClass(portalApp, "div", "UIMaskWorkspace");
	zIndex = 0;
	var currZIndex = 0;
	if (maskLayer != null) {
		currZIndex = eXo.core.DOMUtil.getStyle(maskLayer, "zIndex");
		if (!isNaN(currZIndex) && currZIndex > zIndex) zIndex = currZIndex;
	}
	var popupWindows = eXo.core.DOMUtil.findDescendantsByClass(portalApp, "div", "UIPopupWindow");
	for (var i = 0; i<popupWindows.length; i++) {
		currZIndex = eXo.core.DOMUtil.getStyle(popupWindows[i], "zIndex");
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