	function UIWidget() {
	
};

UIWidget.prototype.init = function(inDesktop) {
	var DOMUtil = eXo.core.DOMUtil ;
	var uiWidgetContainer ;
	if(!inDesktop) {
		var uiWidgets = document.getElementById("UIWidgets");
		uiWidgetContainer = DOMUtil.findFirstDescendantByClass(uiWidgets, "div", "UIWidgetContainer");
	} else {
		uiWidgetContainer = document.getElementById("UIPageDesktop") ;
	}
	
	var uiWidgets = DOMUtil.findDescendantsByClass(uiWidgetContainer, "div", "UIWidget");
	
	for(var i = 0; i < uiWidgets.length; i++) {
		uiWidgets[i].onmouseover = eXo.widget.UIWidget.showWidgetControl ;
		uiWidgets[i].onmouseout = eXo.widget.UIWidget.hideWidgetControl ;
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
  	//window.status = "MOUSE UP : " + uiPageDesktop.offsetHeight + "---" + dragObject.offsetTop + "---" + dragObject.offsetHeight ;
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
	var itemSelectorContainer = DOMUtil.findFirstChildByClass(widgets, "div", "ItemSelectorContainer") ;

	var height = workspacePanel.offsetHeight - (itemSelectorContainer.offsetHeight + extraHeight) ;
	uiWidgetContainer.style.height = height + "px" ;
	uiWidgetContainer.style.overflow = "auto" ;
	
	widgetNavigator.style.position = "absolute" ;
	widgetNavigator.style.bottom = "60px" ;
	widgetNavigator.style.left = "160px" ;
} ;

eXo.widget.UIWidget = new UIWidget();