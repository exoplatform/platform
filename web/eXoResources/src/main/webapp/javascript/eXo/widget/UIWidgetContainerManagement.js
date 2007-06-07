eXo.require('eXo.core.TemplateEngine');

function UIWidgetContainerManagement() {
	
}

UIWidgetContainerManagement.prototype.show = function() {
	var DOMUtil = eXo.core.DOMUtil ;
	
	var context = new Object();
	context.uiMaskWorkspace = {
		width: "700px"
	}
	
	context.uiMaskWorkspace.content = eXo.core.TemplateEngine.merge("eXo/widget/UIWidgetContainerManagement.jstmpl", context) ;
	
	var innerHTML = eXo.core.TemplateEngine.merge("eXo/portal/UIMaskWorkspace.jstmpl", context) ;
 	var uiMaskWorkspaceNode = DOMUtil.createElementNode(innerHTML, "div");
		
	eXo.core.UIMaskLayer.createMask("UIPortalApplication", uiMaskWorkspaceNode, 30) ;
	
	eXo.widget.UIWidgetContainerManagement.loadWidgetContainer(true);
};

UIWidgetContainerManagement.prototype.destroy = function() {
	var uiWidgetContainerManagement = document.getElementById("UIWidgetContainerManagement");
	var uiMaskWorkspace = eXo.core.DOMUtil.findAncestorByClass(uiWidgetContainerManagement, "UIMaskWorkspace");
	var uiMaskLayer = uiMaskWorkspace.previousSibling ;
	
	var parentNode = uiMaskWorkspace.parentNode ;
	parentNode.removeChild(uiMaskWorkspace);
	parentNode.removeChild(uiMaskLayer);
};

UIWidgetContainerManagement.prototype.loadWidgetContainer = function(refresh) {
	var DOMUtil = eXo.core.DOMUtil ;
	
	var uiWidgetContainerManagement = document.getElementById("UIWidgetContainerManagement");
	
	var url = eXo.env.server.context + "/command?";
	url += "type=org.exoplatform.portal.application.handler.GetWidgetContainerHandler";
	
	if(refresh == null || refresh == undefined) refresh = false;
		
	var containers = eXo.core.CacheJSonService.getData(url, refresh);
	
	if(containers == null || containers == undefined) return;
	
	var portalWidgetContainer = DOMUtil.findFirstDescendantByClass(uiWidgetContainerManagement, "div", "PortalWidgetContainer");
	var containerList = DOMUtil.findFirstDescendantByClass(uiWidgetContainerManagement, "div", "ContainerList");
	
	var itemList = '' ;
	
	for(container in containers.widgetContainer) {
		var containerName = containers.widgetContainer[container] ;
		itemList += '<a class="NormalItem" href="#">'+containerName+'</a>' ;
	}
	
	containerList.innerHTML = itemList ;

};

UIWidgetContainerManagement.prototype.addWidgetContainer = function() {
	var params = [
  	{name: "objectId", value : "Test"}
  ] ;
	ajaxGet(eXo.env.server.createPortalURL("UIWidgets", "AddWidgetContainer", true, params)) ;
};

eXo.widget.UIWidgetContainerManagement = new UIWidgetContainerManagement();