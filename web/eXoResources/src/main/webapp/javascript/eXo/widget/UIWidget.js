function UIWidget() {	
	this.temp = 0;
	this.zIndex = 0;
};

UIWidget.prototype.init = function(uiWidget, inDesktop) {
	
	uiWidget.onmouseover = eXo.widget.UIWidget.showWidgetControl ;
	uiWidget.onmouseout = eXo.widget.UIWidget.hideWidgetControl ;
	
	if(inDesktop) {
		var appDescriptor = uiWidget.applicationDescriptor;
		uiWidget.style.width = appDescriptor.application.width ;
		uiWidget.style.height = appDescriptor.application.height ;
		uiWidget.style.position = "absolute" ;

		var posX = appDescriptor.widget.positionX ;
		var posY = appDescriptor.widget.positionY ;
		var zIndex = appDescriptor.widget.zIndex ;
		
		if(posX < 0) posX = 0 ;
		if(posY < 0) posY = 0 ;
		if(zIndex < 0) zIndex = 0 ;
		
		uiWidget.style.left = posX + "px" ;
		uiWidget.style.top = posY + "px" ;
		uiWidget.style.zIndex = zIndex ;
//		eXo.widget.UIWidget.zIndex = zIndex;
	}
};
UIWidget.prototype.editWidget = function(selectedElement) {
	var DOMUtil = eXo.core.DOMUtil ;
	var uiWidget = DOMUtil.findAncestorByClass(selectedElement,"UIWidget") ;
	var editMode = DOMUtil.findFirstDescendantByClass(uiWidget, "div", "EditMode") ;
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
} ;

UIWidget.prototype.deleteWidget = function(selectedElement) {
	var DOMUtil = eXo.core.DOMUtil ;
	var uiWidgetContainer = DOMUtil.findAncestorByClass(selectedElement, "UIWidgetContainer") ;
	var uiPage = DOMUtil.findAncestorByClass(selectedElement, "UIPage") ;
	var uiWidget = DOMUtil.findAncestorByClass(selectedElement, "UIWidget") ;
	var containerBlockId ;
	var isInControlWorkspace = false ;
	if(uiPage) {
		var uiPageIdNode = DOMUtil.findFirstDescendantByClass(uiPage, "div", "id");
		containerBlockId = uiPageIdNode.innerHTML;
	}
	else {
		containerBlockId = uiWidgetContainer.id ;
		isInControlWorkspace = true ;
	}
	var params = [
  	{name: "objectId", value : uiWidget.id}
  ] ;
	if (confirm("Are you sure you want to delete this widget ?")) {
		var result = ajaxAsyncGetRequest(eXo.env.server.createPortalURL(containerBlockId, "DeleteWidget", true, params), false) ;
		if(result == "OK") { 
			DOMUtil.removeElement(uiWidget) ;
			if(isInControlWorkspace) eXo.webui.UIVerticalScroller.refreshScroll(0) ;
		}
	}	
};
/*
	minh.js.exo
*/

UIWidget.prototype.showWidgetControl = function(e) {
	if (!e) e = window.event ;
  e.cancelBubble = true ;
	var DOMUtil = eXo.core.DOMUtil;
	var uiWidget = this ;
	var widgetControl = DOMUtil.findFirstDescendantByClass(uiWidget, "div", "WidgetControl");
	widgetControl.style.display = "block" ;

	var uiPageDesktop = DOMUtil.findAncestorByClass(uiWidget, "UIPageDesktop");
	if(uiPageDesktop) {
		var dragHandleArea = DOMUtil.findFirstDescendantByClass(widgetControl, "div", "WidgetDragHandleArea");
		dragHandleArea.title = "Drag this Widget";
		widgetControl.onmousedown = eXo.widget.UIWidget.initDND ;
	}
};


UIWidget.prototype.hideWidgetControl = function(e) {
	if (!e) e = window.event ;
  e.cancelBubble = true ;
	var uiWidget = this ;
	var widgetControl = eXo.core.DOMUtil.findFirstDescendantByClass(uiWidget, "div", "WidgetControl");
	widgetControl.style.display = "none" ;
};

UIWidget.prototype.initDND = function(e) {
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
		eXo.widget.UIWidget.saveWindowProperties(dragObject) ;
  }
  
  var clickBlock = this ;
  var dragBlock = eXo.core.DOMUtil.findAncestorByClass(this, "UIDragObject") ;
  DragDrop.init(null, clickBlock, dragBlock, e) ;
  
};

UIWidget.prototype.saveWindowProperties = function(object) {
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
UIWidget.prototype.resizeContainer = function() {
	var widgets  = document.getElementById("UIWidgets") ;
	if(widgets == null) return ;	
	
	var DOMUtil = eXo.core.DOMUtil ;
	var workspacePanel = document.getElementById("UIWorkspacePanel") ;
	if(workspacePanel.style.display == "none") return;
	var uiWidgetContainer = DOMUtil.findFirstDescendantByClass(widgets, "div", "UIWidgetContainer");
	if(uiWidgetContainer == null) return ;
	var widgetNavigator = DOMUtil.findFirstChildByClass(uiWidgetContainer, "div", "WidgetNavigator") ;	
	var widgetContainerScrollArea = DOMUtil.findFirstChildByClass(uiWidgetContainer, "div", "WidgetContainerScrollArea") ;
	var itemSelectorContainer = DOMUtil.findFirstChildByClass(widgets, "div", "ItemSelectorContainer") ;
	
	var availableHeight = workspacePanel.offsetHeight - (itemSelectorContainer.offsetHeight + widgetNavigator.offsetHeight + 40) ;
	if(eXo.core.Browser.isIE6() || workspacePanel.offsetHeight < 1) {
		//var html = document.getElementsByTagName("html")[0];
		var uiControlWorkspace = document.getElementById("UIControlWorkspace") ;
		var fixHeight = uiControlWorkspace.offsetHeight - 153;
    fixHeight = (fixHeight < 0) ? 0 : fixHeight ;
		/* 153 is total value (UserWorkspaceTitleHeight + UIExoStartHeight + WidgetNavigatorHeight + 40)
		 * 40 is distance between UIWidgets and UIExoStart 
		 * */
		if(widgetContainerScrollArea.offsetHeight == fixHeight) return;
		widgetContainerScrollArea.style.height = fixHeight + "px" ;
	} else {
		if(availableHeight < 0) return ;
		widgetContainerScrollArea.style.height = availableHeight + "px" ;
	}
  widgetContainerScrollArea.style.overflow = "hidden" ;
} ;

eXo.widget.UIWidget = new UIWidget();