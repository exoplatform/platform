function UIPortalComponent() {
  
};

UIPortalComponent.prototype.init = function() {
  var uiPageDesktop = document.getElementById("UIPageDesktop");
  if(!uiPageDesktop) {
  	var uiPage = document.getElementById("UIPage"); 
  	var uiWindows = eXo.core.DOMUtil.findDescendantsByClass(uiPage, "div", "UIWindow");
  	for(var i = 0; i < uiWindows.length; i++) {
  		var maxIcon = eXo.core.DOMUtil.findFirstDescendantByClass(uiWindows[i], "div", "MaximizedIcon");
			maxIcon.onclick = eXo.portal.UIPortalComponent.maximizeWindow;
  		var miniIcon = eXo.core.DOMUtil.findFirstDescendantByClass(uiWindows[i], "div", "MinimizedIcon");
			miniIcon.onclick = eXo.portal.UIPortalComponent.minimizeWindow;
  	}
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
  alert("max");
};


UIPortalComponent.prototype.minimizeWindow = function() {
  alert("mini");
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
