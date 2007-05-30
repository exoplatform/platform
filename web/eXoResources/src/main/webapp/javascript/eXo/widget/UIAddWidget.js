function UIAddWidget() {
	
}

UIAddWidget.prototype.show = function() {
	var DOMUtil = eXo.core.DOMUtil ;
	
	var context = new Object();
	context.uiMaskWorkspace = {
		width: "700px"
	}
	
	context.uiMaskWorkspace.content = eXo.core.TemplateEngine.merge("eXo/widget/UIAddWidget.jstmpl", context) ;
	
	var innerHTML = eXo.core.TemplateEngine.merge("eXo/portal/UIMaskWorkspace.jstmpl", context) ;
 	var uiMaskWorkspaceNode = DOMUtil.createElementNode(innerHTML, "div");
		
	eXo.core.UIMaskLayer.createMask("UIPortalApplication", uiMaskWorkspaceNode, 30) ;
};

UIAddWidget.prototype.addWidget = function(widgetElement) {
	
	var appDescriptor = widgetElement.applicationDescriptor;
	var uiWidgets = document.getElementById("UIWidgets");
	var uiWidgetContainer = eXo.core.DOMUtil.findFirstDescendantByClass(uiWidgets, "div", "UIWidgetContainer");
	var widgetNavigator = eXo.core.DOMUtil.findFirstChildByClass(uiWidgetContainer, "div", "WidgetNavigator");
	
	uiWidgetContainer.insertBefore(widgetElement, widgetNavigator) ;
	
	eXo.widget.UIWidget.init();
	
	/*Get Application's Stylesheet*/
	var styleId = appDescriptor.appId + "Stylesheet" ;
	eXo.core.Skin.addSkin(styleId, appDescriptor.application.skin[eXo.env.client.skin]);
};

UIAddWidget.prototype.addWidgetToDesktop = function(widgetElement) {
	var appDescriptor = widgetElement.applicationDescriptor;
	var uiPageDesktop = document.getElementById("UIPageDesktop") ;
	if(uiPageDesktop == null) return ;
	uiPageDesktop.appendChild(widgetElement);
	
	widgetElement.style.width = appDescriptor.application.width ;
	widgetElement.style.height = appDescriptor.application.height ;
	widgetElement.style.position = "absolute" ;
	widgetElement.style.top = "40px" ;
	widgetElement.style.left = "20px" ;
	
	eXo.widget.UIWidget.init(true);
	/*Get Application's Stylesheet*/
	var styleId = appDescriptor.appId + "Stylesheet" ;
	eXo.core.Skin.addSkin(styleId, appDescriptor.application.skin[eXo.env.client.skin]);
};

eXo.widget.UIAddWidget = new UIAddWidget();