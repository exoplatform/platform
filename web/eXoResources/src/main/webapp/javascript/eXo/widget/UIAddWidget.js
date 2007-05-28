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
	
	var uiWidgetContainer = document.getElementById("UIWidgetContainer");
	var firstChild = eXo.core.DOMUtil.getChildrenByTagName(uiWidgetContainer, "div")[0];
	uiWidgetContainer.insertBefore(widgetElement, firstChild) ;
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
	eXo.widget.UIWidget.init(widgetElement.factoryId);
};

eXo.widget.UIAddWidget = new UIAddWidget();