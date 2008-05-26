function UIAddGadget() {};
UIAddGadget.prototype.show = function() {
	var DOMUtil = eXo.core.DOMUtil ;
	
	var context = new Object();
	context.uiMaskWorkspace = {
		width: "700px"
	}
	
	context.uiMaskWorkspace.content = eXo.core.TemplateEngine.merge("eXo/gadget/UIAddGadget.jstmpl", context) ;
	
	var innerHTML = eXo.core.TemplateEngine.merge("eXo/portal/UIMaskWorkspace.jstmpl", context) ;
 	var uiMaskWorkspaceNode = DOMUtil.createElementNode(innerHTML, "div");
		
	eXo.core.UIMaskLayer.createMask("UIPortalApplication", uiMaskWorkspaceNode, 30) ;
};

UIAddGadget.prototype.addGadget = function(gadgetElement) {
	var appDescriptor = gadgetElement.applicationDescriptor;
	
	var uiGadgets = document.getElementById("UIWidgets");
	var gadgetContainerScrollArea = eXo.core.DOMUtil.findFirstDescendantByClass(uiGadgets, "div", "WidgetContainerScrollArea");
	gadgetContainerScrollArea.appendChild(gadgetElement);
	
	eXo.gadget.UIGadget.init(gadgetElement);
	
	/*Get Application's Stylesheet*/
	var styleId = appDescriptor.appId + "Stylesheet" ;
	eXo.core.Skin.addSkin(styleId, appDescriptor.application.skin[eXo.env.client.skin]);
		eXo.webui.UIVerticalScroller.init();
};

UIAddGadget.prototype.addGadgetToDesktop = function(gadgetElement) {
	var appDescriptor = gadgetElement.applicationDescriptor;
	var uiPageDesktop = document.getElementById("UIPageDesktop") ;
	if(uiPageDesktop == null) return ;
	uiPageDesktop.appendChild(gadgetElement);
	
	eXo.gadget.UIGadget.init(gadgetElement, true);
	/*Get Application's Stylesheet*/
	var styleId = appDescriptor.appId + "Stylesheet" ;
	eXo.core.Skin.addSkin(styleId, appDescriptor.application.skin[eXo.env.client.skin]);
};

eXo.gadget.UIAddGadget = new UIAddGadget();