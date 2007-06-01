function UIWidget() {
	
};

UIWidget.prototype.init = function(uiWidget, inDesktop) {
	
	uiWidget.onmouseover = eXo.widget.UIWidget.showWidgetControl ;
	uiWidget.onmouseout = eXo.widget.UIWidget.hideWidgetControl ;
	
	if(inDesktop) {
		var appDescriptor = uiWidget.applicationDescriptor;
		uiWidget.style.width = appDescriptor.application.width ;
		uiWidget.style.height = appDescriptor.application.height ;
		
		uiWidget.style.position = "absolute" ;		
		uiWidget.style.left = uiWidget.positionX + "px" ;
		uiWidget.style.top = uiWidget.positionY + "px" ;
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
	var uiWidget = this ;
	var widgetControl = eXo.core.DOMUtil.findFirstDescendantByClass(uiWidget, "div", "WidgetControl");
	widgetControl.style.display = "block" ;
	
	var uiPageDesktop = eXo.core.DOMUtil.findAncestorByClass(uiWidget, "UIPageDesktop");
	if(uiPageDesktop) widgetControl.onmousedown = eXo.widget.UIWidget.initDND ;
};

UIWidget.prototype.hideWidgetControl = function() {
	var uiWidget = this ;
	var widgetControl = eXo.core.DOMUtil.findFirstDescendantByClass(uiWidget, "div", "WidgetControl");
	widgetControl.style.display = "none" ;
};

UIWidget.prototype.initDND = function(e) {
  var DragDrop = eXo.core.DragDrop ;
  var DOMUtil = eXo.core.DOMUtil ;

	DragDrop.initCallback = function (dndEvent) {
  }

  DragDrop.dragCallback = function (dndEvent) {  	
  }

  DragDrop.dropCallback = function (dndEvent) {	  	
  	var dragObject = dndEvent.dragObject ;
  	var uiPageDesktop = document.getElementById("UIPageDesktop") ;
  	var offsetHeight = uiPageDesktop.offsetHeight - dragObject.offsetHeight ;
  	var offsetTop = dragObject.offsetTop ;
  	var offsetWidth = uiPageDesktop.offsetWidth - dragObject.offsetWidth ;
  	var offsetLeft = dragObject.offsetLeft ;
  	
  	if (dragObject.offsetLeft < 0) dragObject.style.left = "0px" ;
  	if (dragObject.offsetTop < 0) dragObject.style.top = "0px" ;
  	if (offsetTop > offsetHeight) dragObject.style.top = offsetHeight + "px" ;  	
  	if (offsetLeft > offsetWidth) dragObject.style.left = offsetWidth + "px" ;  	
  	
  	/*Save Position*/
  	var uiPage = DOMUtil.findAncestorByClass(dragObject, "UIPage") ;
  	var uiPageIdNode = DOMUtil.findFirstDescendantByClass(uiPage, "div", "id");
		containerBlockId = uiPageIdNode.innerHTML;
  	var params = [
	  	{name: "objectId", value : dragObject.id} ,
	  	{name: "posX", value : dragObject.offsetLeft},
	  	{name: "posY", value : dragObject.offsetTop}
	  ] ;
  	ajaxGet(eXo.env.server.createPortalURL(containerBlockId, "SaveProperties", true, params)) ;
  }
  
  var clickBlock = this ;
  var dragBlock = eXo.core.DOMUtil.findAncestorByClass(this, "UIDragObject") ;
  DragDrop.init(null, clickBlock, dragBlock, e) ;
};

/*
 * Coder      : Dunghm
 * Date       : 30-05-2007
 * Description: resize workspace frame
 * */
UIWidget.prototype.resizeContainer = function() {
	var widgets  = document.getElementById("UIWidgets") ;
	if(widgets == null) return ;	
	
	var DOMUtil = eXo.core.DOMUtil ;
	
	var extraHeight = 40 ;
	var uiWidgetContainer = DOMUtil.findFirstDescendantByClass(widgets, "div", "UIWidgetContainer");
	var workspacePanel = document.getElementById("UIWorkspacePanel") ;	
	var widgetNavigator = DOMUtil.findFirstChildByClass(uiWidgetContainer, "div", "WidgetNavigator") ;	
	var widgetContainerScrollArea = DOMUtil.findFirstChildByClass(uiWidgetContainer, "div", "WidgetContainerScrollArea") ;
	var itemSelectorContainer = DOMUtil.findFirstChildByClass(widgets, "div", "ItemSelectorContainer") ;

	var availableHeight = workspacePanel.offsetHeight - (itemSelectorContainer.offsetHeight + widgetNavigator.offsetHeight + extraHeight) ;
	widgetContainerScrollArea.style.height = availableHeight + "px" ;
} ;

eXo.widget.UIWidget = new UIWidget();