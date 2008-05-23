function UIGadget() {	
	this.temp = 0;
	this.zIndex = 0;
};

UIGadget.prototype.init = function(uiGadget, inDesktop) {
	
	uiGadget.onmouseover = eXo.gadget.UIGadget.showGadgetControl ;
	uiGadget.onmouseout = eXo.gadget.UIGadget.hideGadgetControl ;
	
	if(inDesktop) {
		var appDescriptor = uiGadget.applicationDescriptor;
		uiGadget.style.width = appDescriptor.application.width ;
		uiGadget.style.height = appDescriptor.application.height ;
		uiGadget.style.position = "absolute" ;

		var posX = appDescriptor.gadget.positionX ;
		var posY = appDescriptor.gadget.positionY ;
		var zIndex = appDescriptor.gadget.zIndex ;
		
		if(posX < 0) posX = 0 ;
		if(posY < 0) posY = 0 ;
		if(zIndex < 0) zIndex = 0 ;
		
		uiGadget.style.left = posX + "px" ;
		uiGadget.style.top = posY + "px" ;
		uiGadget.style.zIndex = zIndex ;
//		eXo.gadget.UIGadget.zIndex = zIndex;
	}
};
UIGadget.prototype.editGadget = function(selectedElement) {
	var DOMUtil = eXo.core.DOMUtil ;
	var uiGadget = DOMUtil.findAncestorByClass(selectedElement,"UIWidget") ;
	if (uiGadget && uiGadget.applicationDescriptor.application.editGadget) {
		uiGadget.applicationDescriptor.application.editGadget(uiGadget);
	}
	else {
	var editMode = DOMUtil.findFirstDescendantByClass(uiGadget, "div", "EditMode") ;
	if (editMode) {		
		viewMode = DOMUtil.findNextElementByTagName(editMode, "div") ;
		if (editMode.style.display == "none") {
			editMode.style.position = "absolute" ;
			editMode.style.display = "block" ;
			editMode.style.left = viewMode.offsetWidth + "px" ;			
		} else {
			editMode.style.display = "none" ;
		}	
		
	}
	}
} ;

UIGadget.prototype.deleteGadget = function(selectedElement) {
	var DOMUtil = eXo.core.DOMUtil ;
	var uiGadgetContainer = DOMUtil.findAncestorByClass(selectedElement, "UIGadgetContainer") ;
	var uiPage = DOMUtil.findAncestorByClass(selectedElement, "UIPage") ;
	var uiGadget = DOMUtil.findAncestorByClass(selectedElement, "UIWidget") ;
	var containerBlockId ;
	var isInControlWorkspace = false ;
	if(uiPage) {
		var uiPageIdNode = DOMUtil.findFirstDescendantByClass(uiPage, "div", "id");
		containerBlockId = uiPageIdNode.innerHTML;
	}
	else {
		containerBlockId = uiGadgetContainer.id ;
		isInControlWorkspace = true ;
	}
	var params = [
  	{name: "objectId", value : uiGadget.id}
  ] ;
	if (confirm("Are you sure you want to delete this gadget ?")) {
		var result = ajaxAsyncGetRequest(eXo.env.server.createPortalURL(containerBlockId, "DeleteWidget", true, params), false) ;
		if(result == "OK") { 
			DOMUtil.removeElement(uiGadget) ;
			if(isInControlWorkspace) eXo.webui.UIVerticalScroller.refreshScroll(0) ;
		}
	}	
};
/*
	minh.js.exo
*/

UIGadget.prototype.showGadgetControl = function(e) {
	if (!e) e = window.event ;
  e.cancelBubble = true ;
	var DOMUtil = eXo.core.DOMUtil;
	var uiGadget = this ;
	var gadgetControl = DOMUtil.findFirstDescendantByClass(uiGadget, "div", "WidgetControl");
	gadgetControl.style.display = "block" ;

	var uiPageDesktop = DOMUtil.findAncestorByClass(uiGadget, "UIPageDesktop");
	if(uiPageDesktop) {
		var dragHandleArea = DOMUtil.findFirstDescendantByClass(gadgetControl, "div", "WidgetDragHandleArea");
		dragHandleArea.title = "Drag this Widget";
		gadgetControl.onmousedown = eXo.gadget.UIGadget.initDND ;
	}
};


UIGadget.prototype.hideGadgetControl = function(e) {
	if (!e) e = window.event ;
  e.cancelBubble = true ;
	var uiGadget = this ;
	var gadgetControl = eXo.core.DOMUtil.findFirstDescendantByClass(uiGadget, "div", "WidgetControl");
	gadgetControl.style.display = "none" ;
};

UIGadget.prototype.initDND = function(e) {
  var DragDrop = eXo.core.DragDrop ;
  var DOMUtil = eXo.core.DOMUtil ;
  var uiPageDesktop = document.getElementById("UIPageDesktop") ;
  var limitX = 50 ;

	DragDrop.initCallback = function (dndEvent) {
		var dragObject = dndEvent.dragObject ;
		var dragObjectX = dragObject.offsetLeft ;
		var dragObjectY = dragObject.offsetTop ;
		
		if(dragObjectX > limitX )	dragObject.isIn = false ;
		else dragObject.isIn = true ;
		
		UIDesktop = eXo.desktop.UIDesktop ;
		UIDesktop.resetZIndex(dragObject) ;
		dragObject.onclick = function () {
		UIDesktop.resetZIndex(this) ;
		}
  }

  DragDrop.dragCallback = function (dndEvent) {
  	var dragObject = dndEvent.dragObject ;
  	var dragObjectX = dragObject.offsetLeft ;
  	var dragObjectY = dragObject.offsetTop ;
  	
   	if(dragObjectX < limitX && dragObject.isIn == false) {
  		dragObject.style.left = "0px" ;
  	} 	
  	
  	/*if(dragObjectY < limitX && dragObject.isIn == true) {
  		dragObject.style.top = "0px" ;
  	}
  	window.status = "uiPageDesktop : " + uiPageDesktop.offsetWidth ;*/
  }	
  DragDrop.dropCallback = function (dndEvent) {
  	var dragObject = dndEvent.dragObject ;
  	var dragObjectX = dragObject.offsetLeft ;
  	var dragObjectY = dragObject.offsetTop ;
  	
  	if(dragObjectX < limitX && dragObject.isIn == true) {
  		dragObject.style.left = "0px" ;
  	}
  	
  	if(dragObjectY < limitX ) {
  		dragObject.style.top = "0px" ;
  	}
  	  	
  	var offsetHeight = uiPageDesktop.offsetHeight - dragObject.offsetHeight  - limitX;
  	var offsetTop = dragObject.offsetTop ;
  	var offsetWidth = uiPageDesktop.offsetWidth - dragObject.offsetWidth - limitX ;
  	var offsetLeft = dragObject.offsetLeft ;
  	
  	if (dragObject.offsetLeft < 0) dragObject.style.left = "0px" ;
  	if (dragObject.offsetTop < 0) dragObject.style.top = "0px" ;
  	if (offsetTop > offsetHeight) dragObject.style.top = (offsetHeight + limitX) + "px" ;
  	if (offsetLeft > offsetWidth) dragObject.style.left = (offsetWidth + limitX) + "px" ;
  	
  	/*Save Position*/
		eXo.gadget.UIGadget.saveWindowProperties(dragObject) ;
  }
  
  var clickBlock = this ;
  var dragBlock = eXo.core.DOMUtil.findAncestorByClass(this, "UIDragObject") ;
  DragDrop.init(null, clickBlock, dragBlock, e) ;
  
};

UIGadget.prototype.saveWindowProperties = function(object) {
		var DOMUtil = eXo.core.DOMUtil ;
		var uiPage = DOMUtil.findAncestorByClass(object, "UIPage") ;
  	var uiPageIdNode = DOMUtil.findFirstDescendantByClass(uiPage, "div", "id");
		containerBlockId = uiPageIdNode.innerHTML;
  	var params = [
	  	{name: "objectId", value : object.id},
	  	{name: "posX", value : object.offsetLeft},
	  	{name: "posY", value : object.offsetTop},
	  	{name: "zIndex", value : object.style.zIndex}
	  ] ;
	  
  	ajaxAsyncGetRequest(eXo.env.server.createPortalURL(containerBlockId, "SaveWidgetProperties", true, params), false) ;
} ;


/** Created: by Duy Tu 
 *         duytucntt@gmail.com
 */
UIGadget.prototype.resizeContainer = function() {
	var gadgets  = document.getElementById("UIGadgets") ;
	if(gadgets == null) return ;	
	
	var DOMUtil = eXo.core.DOMUtil ;
	var workspacePanel = document.getElementById("UIWorkspacePanel") ;
	if(workspacePanel.style.display == "none") return;
	var uiGadgetContainer = DOMUtil.findFirstDescendantByClass(gadgets, "div", "UIGadgetContainer");
	if(uiGadgetContainer == null) return ;
	var gadgetNavigator = DOMUtil.findFirstChildByClass(uiGadgetContainer, "div", "WidgetNavigator") ;	
	var gadgetContainerScrollArea = DOMUtil.findFirstChildByClass(uiGadgetContainer, "div", "WidgetContainerScrollArea") ;
	var itemSelectorContainer = DOMUtil.findFirstChildByClass(gadgets, "div", "ItemSelectorContainer") ;
	
	var availableHeight = workspacePanel.offsetHeight - (itemSelectorContainer.offsetHeight + gadgetNavigator.offsetHeight + 40) ;
	if(eXo.core.Browser.isIE6() || workspacePanel.offsetHeight < 1) {
		//var html = document.getElementsByTagName("html")[0];
		var uiControlWorkspace = document.getElementById("UIControlWorkspace") ;
		var fixHeight = uiControlWorkspace.offsetHeight - 153;
    fixHeight = (fixHeight < 0) ? 0 : fixHeight ;
		/* 153 is total value (UserWorkspaceTitleHeight + UIExoStartHeight + WidgetNavigatorHeight + 40)
		 * 40 is distance between UIGadgets and UIExoStart 
		 * */
		if(gadgetContainerScrollArea.offsetHeight == fixHeight) return;
		gadgetContainerScrollArea.style.height = fixHeight + "px" ;
	} else {
		if(availableHeight < 0) return ;
		gadgetContainerScrollArea.style.height = availableHeight + "px" ;
	}
  gadgetContainerScrollArea.style.overflow = "hidden" ;
} ;

UIGadget.prototype.createGadget = function(url,id) {
	//eXo = eXo || {};
	eXo.gadgets = eXo.gadgets || {};
	//window.gadgets = eXo.gadget.Gadgets;
	
	if (!eXo.gadgets || !eXo.gadgets.rpc) {
		eXo.loadJS("/eXoGadgetServer/gadgets/js/rpc.js?c=1&debug=1&p=1");
	}
	eXo.require("eXo.gadgets.Gadgets", "/eXoGadgets/javascript/");
	eXo.require("eXo.gadgets.CookieBasedUserPrefStore", "/eXoGadgets/javascript/");
	window.gadgets = eXo.gadgets.Gadgets;
	var gadget = eXo.gadgets.Gadgets.container.createGadget({specUrl: url});
	
  eXo.gadgets.Gadgets.container.addGadget(gadget);
	document.getElementById(id).innerHTML = "<div id='gadget_" + gadget.id + "'> </div>";
	eXo.gadgets.Gadgets.container.renderGadgets();
};

eXo.gadget.UIGadget = new UIGadget();