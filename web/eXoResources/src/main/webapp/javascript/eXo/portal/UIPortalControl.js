function UIPortalControl() {
  
};

UIPortalControl.prototype.init = function(objectId) {  
  var uiPageDesktop = document.getElementById("UIPageDesktop");
  if(!uiPageDesktop) {
  	var uiWindow = document.getElementById(objectId);
  	var maxIcon = eXo.core.DOMUtil.findFirstDescendantByClass(uiWindow, "div", "MaximizedIcon");
		maxIcon.onclick = eXo.portal.UIPortalControl.maximizeWindow;
  	var miniIcon = eXo.core.DOMUtil.findFirstDescendantByClass(uiWindow, "div", "MinimizedIcon");
		miniIcon.onclick = eXo.portal.UIPortalControl.minimizeWindow;
  }
};

UIPortalControl.prototype.maximizeWindow = function() {
  var uiWindows = eXo.core.DOMUtil.findAncestorByClass(this, "UIWindow");
  var params = [{name: "portletId", value: uiWindows.id}] ;
	ajaxGet(eXo.env.server.createPortalURL("UIPortal", "Maximize", params,  true)) ;
};

UIPortalControl.prototype.minimizeWindow = function() {
	ajaxGet(eXo.env.server.createPortalURL("UIPortal", "Minimize",   true)) ;
};

/* Create Funtion by Duy Tu */
UIPortalControl.prototype.showHiddenContent = function(selectedElement) {
	var DOMUtil = eXo.core.DOMUtil ;
	var ancestorClass = DOMUtil.findAncestorByClass(selectedElement, "UILogged");
	var classNormal = DOMUtil.findAncestorByClass(selectedElement, "NormalBG");
	var classSelected = DOMUtil.findFirstDescendantByClass(ancestorClass, "div", "SelectedBG");
	var contentStyle = eXo.core.DOMUtil.findDescendantsByClass(ancestorClass, "div", "StyleContent") ;
	if(classNormal != null){
		classNormal.className = "SelectedBG";
		classSelected.className = "NormalBG";
		if(selectedElement.className == "UserIcon"){
		  contentStyle[0].style.display = "block";
			contentStyle[1].style.display = "none";
		} else {
		  contentStyle[0].style.display = "none";
		  contentStyle[1].style.display = "block";
		}
	} else {
		return;
	}

}

eXo.portal.UIPortalControl = new UIPortalControl();
