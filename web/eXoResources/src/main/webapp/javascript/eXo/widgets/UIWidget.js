function UIWidget() {
	
};

UIWidget.prototype.init = function() {
	var DOMUtil = eXo.core.DOMUtil ;
	var uiWidgetContainer = document.getElementById("UIWidgetContainer");
	
	var uiWidgets = DOMUtil.findDescendantsByClass(uiWidgetContainer, "div", "UIWidget");
	
	for(var i = 0; i < uiWidgets.length; i++) {
		uiWidgets[i].onmouseover = eXo.widgets.UIWidget.showWidgetControl ;
		uiWidgets[i].onmouseout = eXo.widgets.UIWidget.hideWidgetControl ;
	}
	
};

UIWidget.prototype.showWidgetControl = function() {
	var uiWidget = this ;
	var widgetControl = eXo.core.DOMUtil.findFirstDescendantByClass(uiWidget, "div", "WidgetControl");
	widgetControl.style.display = "block" ;
};

UIWidget.prototype.hideWidgetControl = function() {
	var uiWidget = this ;
	var widgetControl = eXo.core.DOMUtil.findFirstDescendantByClass(uiWidget, "div", "WidgetControl");
	widgetControl.style.display = "none" ;
};

eXo.widgets.UIWidget = new UIWidget();