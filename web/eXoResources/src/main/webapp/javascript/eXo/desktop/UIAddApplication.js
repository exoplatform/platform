eXo.require('eXo.core.TemplateEngine');
eXo.require('eXo.webui.UIHorizontalTabs');
eXo.require('eXo.core.CacheJSonService');

function UIAddApplication() {
  
};

UIAddApplication.prototype.init = function(containerId, context) {
	var DOMUtil = eXo.core.DOMUtil ;
	var container = document.getElementById(containerId);
	
	if(document.getElementById("UIMaskWorkspaceJSTemplate") == null) {
		var uiAddAppContainer = document.createElement('div') ;
		uiAddAppContainer.id = "UIAddApplicationContainer" ;
		uiAddAppContainer.style.display = "none" ;
		uiAddAppContainer.innerHTML = eXo.core.TemplateEngine.merge("eXo/desktop/UIMaskWorkspace.jstmpl", context) ;
		
		container.appendChild(uiAddAppContainer) ;
	}
	var uiAddApplicationContainer = document.getElementById("UIAddApplicationContainer");
	eXo.desktop.UIAddApplication.showAddApplication(uiAddApplicationContainer);
	this.loadPortlets(false);
};

UIAddApplication.prototype.loadPortlets = function(refresh) {
	var uiAddApplicationContainer = document.getElementById("UIAddApplicationContainer");
	var url = eXo.env.server.context + "/service?serviceName=portletRegistry";
	if(refresh == null || refresh == undefined) var refresh = false;
  var category = eXo.core.CacheJSonService.getData(url, refresh);
  window.status = "Onload1.3";
  if(category == null || category == undefined) return;
  var itemList = eXo.core.DOMUtil.findFirstDescendantByClass(uiAddApplicationContainer, "div", "ItemList") ;
  var itemDetailList = eXo.core.DOMUtil.findFirstDescendantByClass(uiAddApplicationContainer, "div", "ItemDetailList") ;

  
}

UIAddApplication.prototype.addPortlet = function(id) {
	var params = [{name: "portletId", value : id}] ;
	ajaxGet(eXo.env.server.createPortalURL("UIPortal", "AddPortletToDesktop", true, params)) ;
};

UIAddApplication.prototype.showAddApplication = function(object) {
	eXo.core.UIMaskLayer.createMask("UIPortalApplication", object, 30) ;
	eXo.desktop.UIDockbar.reset() ;
};

UIAddApplication.prototype.removeAddApplication = function() {
	var uiAddApplicationContainer = document.getElementById("UIAddApplicationContainer") ;
	var maskLayer = uiAddApplicationContainer.previousSibling ;
	eXo.core.UIMaskLayer.removeMask(maskLayer) ;
	
	uiAddApplicationContainer.parentNode.removeChild(uiAddApplicationContainer) ;
};

UIAddApplication.prototype.importJavascript = function(object) {
	eXo.require(object); 
};

eXo.desktop.UIAddApplication = new UIAddApplication() ;