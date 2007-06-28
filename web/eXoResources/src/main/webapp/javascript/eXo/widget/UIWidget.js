function UIWidget() {	
	this.temp = 0;
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
	}
};

UIWidget.prototype.deleteWidget = function(selectedElement) {
	var DOMUtil = eXo.core.DOMUtil ;
	var uiWidgetContainer = DOMUtil.findAncestorByClass(selectedElement, "UIWidgetContainer") ;
	var uiPage = DOMUtil.findAncestorByClass(selectedElement, "UIPage") ;
	var uiWidget = DOMUtil.findAncestorByClass(selectedElement, "UIWidget") ;
	
	var containerBlockId ;
	if(uiPage) {
		var uiPageIdNode = DOMUtil.findFirstDescendantByClass(uiPage, "div", "id");
		containerBlockId = uiPageIdNode.innerHTML;
	}
	else {
		containerBlockId = uiWidgetContainer.id ;
	}
	
	var params = [
  	{name: "objectId", value : uiWidget.id}
  ] ;

	ajaxGet(eXo.env.server.createPortalURL(containerBlockId, "DeleteWidget", true, params)) ;
};

UIWidget.prototype.showWidgetControl = function() {
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

UIWidget.prototype.hideWidgetControl = function() {
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
  	var uiPage = DOMUtil.findAncestorByClass(dragObject, "UIPage") ;
  	var uiPageIdNode = DOMUtil.findFirstDescendantByClass(uiPage, "div", "id");
  	
		containerBlockId = uiPageIdNode.innerHTML;
  	var params = [
	  	{name: "objectId", value : dragObject.id},
	  	{name: "posX", value : dragObject.offsetLeft},
	  	{name: "posY", value : dragObject.offsetTop},
	  	{name: "zIndex", value : dragObject.style.zIndex}
	  ] ;
	  
  	ajaxAsyncGetRequest(eXo.env.server.createPortalURL(containerBlockId, "SaveWidgetProperties", true, params), false) ;
  }
  
  var clickBlock = this ;
  var dragBlock = eXo.core.DOMUtil.findAncestorByClass(this, "UIDragObject") ;
  DragDrop.init(null, clickBlock, dragBlock, e) ;
  
};
/*
 * Coder       : Dunghm
 * Date        : 30-05-2007
 * Description : resize workspace frame
 * */
UIWidget.prototype.resizeContainer = function(fixie) {
	var widgets  = document.getElementById("UIWidgets") ;
  var html = document.getElementsByTagName("html")[0]; 
	if(widgets == null) return ;	
	
	var DOMUtil = eXo.core.DOMUtil ;
	var extraHeight = 40 ;
	var workspacePanel = document.getElementById("UIWorkspacePanel") ;
	if(workspacePanel.style.display == "none") return;
	var uiControlWorkspace = document.getElementById("UIControlWorkspace") ;
	var uiWidgetContainer = DOMUtil.findFirstDescendantByClass(widgets, "div", "UIWidgetContainer");
	var widgetNavigator = DOMUtil.findFirstChildByClass(uiWidgetContainer, "div", "WidgetNavigator") ;	
	var widgetContainerScrollArea = DOMUtil.findFirstChildByClass(uiWidgetContainer, "div", "WidgetContainerScrollArea") ;
	var itemSelectorContainer = DOMUtil.findFirstChildByClass(widgets, "div", "ItemSelectorContainer") ;
	var maxHeight = (uiControlWorkspace.offsetHeight >= workspacePanel.offsetHeight)? workspacePanel.offsetHeight : (uiControlWorkspace.offsetHeight - 62);
	var availableHeight = maxHeight - (itemSelectorContainer.offsetHeight + widgetNavigator.offsetHeight + extraHeight) ;
	if(eXo.core.Browser.isIE6()) {
		if(this.temp == 0 && fixie == 0) {
			widgetContainerScrollArea.style.height = (html.offsetHeight - 262) + "px" ;
			++this.temp;
		} else {
			if(availableHeight < 0 || fixie == 0) return ;
			widgetContainerScrollArea.style.height = availableHeight + "px" ;
		}
		widgetContainerScrollArea.style.overflow = "hidden" ;
		return;
	} else {
		if(availableHeight < 0) return ;
		widgetContainerScrollArea.style.height = availableHeight + "px" ;
	  widgetContainerScrollArea.style.overflow = "hidden" ;
	}
} ;

eXo.widget.UIWidget = new UIWidget();