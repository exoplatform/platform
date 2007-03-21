function UIPortalComponent() {
  
};

UIPortalComponent.prototype.init = function(objectId) {  
  var uiPageDesktop = document.getElementById("UIPageDesktop");
  if(!uiPageDesktop) {
  	var uiWindow = document.getElementById(objectId);
  	var maxIcon = eXo.core.DOMUtil.findFirstDescendantByClass(uiWindow, "div", "MaximizedIcon");
		maxIcon.onclick = eXo.portal.UIPortalComponent.maximizeWindow;
  	var miniIcon = eXo.core.DOMUtil.findFirstDescendantByClass(uiWindow, "div", "MinimizedIcon");
		miniIcon.onclick = eXo.portal.UIPortalComponent.minimizeWindow;
  }
};

//UIPortalComponent.prototype.showHidePortletControl = function(objClicked) {
//  if(objClicked.className == "ExpandButton") {
//    objClicked.className = "CollapseButton";
//  } else {
//    objClicked.className = "ExpandButton";
//  }
//
//  var objShowHide = document.getElementById("ShowHide");
//
//  if(objShowHide.className == "OnHide") {
//    objShowHide.className = "OnShow";
//  } else {
//    objShowHide.className = "OnHide";
//  }
//};

UIPortalComponent.prototype.maximizeWindow = function() {
  var uiWindows = eXo.core.DOMUtil.findAncestorByClass(this, "UIWindow");
  var params = [{name: "portletId", value: uiWindows.id}] ;
	ajaxGet(eXo.env.server.createPortalURL("UIPortal", "Maximize", params,  true)) ;
};

UIPortalComponent.prototype.minimizeWindow = function() {
	ajaxGet(eXo.env.server.createPortalURL("UIPortal", "Minimize",   true)) ;
};

/* Create Funtion by Duy Tu */
UIPortalComponent.prototype.showHiddenContent = function(selectedElement, ancestorByClass, hiParent, showParent, hiContent, ShowContent) {
	var DOMUtil = eXo.core.DOMUtil ;
	var ancestorClass = DOMUtil.findAncestorByClass(selectedElement, ancestorByClass);
	
	var hidParent = DOMUtil.findAncestorByClass(selectedElement, hiParent);
	var shParent = DOMUtil.findFirstDescendantByClass(ancestorClass, "div", showParent);
	var hiCont = DOMUtil.findFirstDescendantByClass(ancestorClass, "div", hiContent);
	var ShowCont = DOMUtil.findFirstDescendantByClass(ancestorClass, "div", ShowContent);

	hidParent.style.display = "none";
	hiCont.style.display = "none";
	
	shParent.style.display = "block";
	ShowCont.style.display = "block";
}

eXo.portal.UIPortalComponent = new UIPortalComponent();
