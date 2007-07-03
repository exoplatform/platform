eXo.require('eXo.webui.UIPopup');

function UIPopupWindow() {	
};

UIPopupWindow.prototype.init = function(popupId, isShow, isResizable, showCloseButton) {
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
	
	if(showCloseButton == true) {
		var popupCloseButton = DOMUtil.findFirstDescendantByClass(popup, 'div' ,'CloseButton') ;
		popupCloseButton.onmouseup = this.closePopupEvt ;
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

UIPopupWindow.prototype.closePopupEvt = function(e) {
	alert('torng') ;
	eXo.core.DOMUtil.findAncestorByClass(this, "UIDragObject").style.display = "none" ;
}

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
  	
  	var dragObjectX = dragObject.offsetLeft ;
  	var dragObjectY = dragObject.offsetTop ;
  	var uiWorkingWorkspace = document.getElementById("UIWorkingWorkspace") ;
		var uiWindow = DOMUtil.findAncestorByClass(dragObject,"UIWindow") ;
		var extraLeft = (uiWindow) ? uiWindow.offsetLeft : 0 ;
 		var extraHeight = (uiWindow) ? uiWindow.offsetTop : 0 ;  	
  	var uiPageDeskop = document.getElementById("UIPageDesktop") ;
		//alert("UIWindow OffsetLeft : " +  uiWindow.offsetLeft + " UIWindow OffsetTop : " +  uiWindow.offsetTop + " dragObjectX : " + dragObject.style.left + " dragObjectY : " + dragObject.style.top) ;
  	if (uiPageDeskop) {
  		if (dragObject.offsetLeft < (0 - extraLeft)) dragObject.style.left =(0 - extraLeft) +  "px" ;
	  	if (dragObject.offsetTop < (0 - extraHeight)) dragObject.style.top = (0 - extraHeight) + "px" ;  		
  	}
  	else {
  		if (dragObject.offsetLeft < 0) dragObject.style.left = "0px" ;
	  	if (dragObject.offsetTop < 0) dragObject.style.top = "0px" ;  
  	}
  	  	
  	if (uiWorkingWorkspace) {  		
			var offsetWidth = uiWorkingWorkspace.offsetWidth - dragObject.offsetWidth ;
			var offsetHeight = uiWorkingWorkspace.offsetHeight - dragObject.offsetHeight ;
			if (dragObjectX > (offsetWidth - extraLeft)) dragObject.style.left = (offsetWidth - extraLeft) + "px" ;
			if (dragObjectY > (offsetHeight - extraHeight)) dragObject.style.top = (offsetHeight - extraHeight) + "px" ;
  	}
  	//alert ("extraLeft : " + extraLeft + " extraHeight : " + extraHeight + " dragObject Left : " + dragObject.offsetLeft + " dragObject Top : " + dragObject.offsetTop) ;
  	//alert ("dragObject Left : " + dragObject.style.left + " dragObject Top : " + dragObject.style.top) ;
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

eXo.webui.UIPopupWindow = new UIPopupWindow();