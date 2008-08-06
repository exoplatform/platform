function UIAddWidget() {};

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
	var widgetContainerScrollArea = eXo.core.DOMUtil.findFirstDescendantByClass(uiWidgets, "div", "WidgetContainerScrollArea");
	
	widgetContainerScrollArea.appendChild(widgetElement);
	
	eXo.widget.UIWidget.init(widgetElement);
	
	/*Get Application's Stylesheet*/
	var styleId = appDescriptor.appId + "Stylesheet" ;
	eXo.core.Skin.addSkin(styleId, appDescriptor.application.skin[eXo.env.client.skin]);
		eXo.webui.UIVerticalScroller.init();
};

UIAddWidget.prototype.addWidgetToDesktop = function(widgetElement) {
	var appDescriptor = widgetElement.applicationDescriptor;
	var uiPageDesktop = document.getElementById("UIPageDesktop") ;
	if(uiPageDesktop == null) return ;
	uiPageDesktop.appendChild(widgetElement);
	
	eXo.widget.UIWidget.init(widgetElement, true);
	/*Get Application's Stylesheet*/
	var styleId = appDescriptor.appId + "Stylesheet" ;
	eXo.core.Skin.addSkin(styleId, appDescriptor.application.skin[eXo.env.client.skin]);
};

UIAddWidget.prototype.prepareShowNew = function(url) {
	alert(url);
	var subURL = url.substring(url.indexOf("componentId"));
	var componentId = subURL.substring(subURL.indexOf("=")+1, subURL.indexOf("&"));
	return url.replace(componentId, encodeURI(componentId));
};

eXo.widget.UIAddWidget = new UIAddWidget();