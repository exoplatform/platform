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

	ajaxGet(eXo.env.server.createPortalURL(uiWidgetContainer.id, "DeleteWidget", true)) ;
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
  }
  
  var clickBlock = this ;
  var dragBlock = eXo.core.DOMUtil.findAncestorByClass(this, "UIDragObject") ;
  DragDrop.init(null, clickBlock, dragBlock, e) ;
};

eXo.widget.UIWidget = new UIWidget();