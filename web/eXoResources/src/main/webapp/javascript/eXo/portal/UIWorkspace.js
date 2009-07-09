function UIWorkspace(id) {
  this.id = id ;
  this.isFirstTime = true ;
};

eXo.portal.UIWorkspace = new UIWorkspace("UIWorkspace") ;

/*#############################-Working Workspace-##############################*/
if(eXo.portal.UIWorkingWorkspace == undefined) {
  eXo.portal.UIWorkingWorkspace = new UIWorkspace("UIWorkingWorkspace") ;
};

eXo.portal.UIWorkingWorkspace.onResize = function() {
	var uiWorkspace = document.getElementById(eXo.portal.UIWorkingWorkspace.id) ;
	if(eXo.core.Browser.isIE6()) {
		var tabs = eXo.core.DOMUtil.findFirstDescendantByClass(uiWorkspace, "div", "UIHorizontalTabs") ;
		if(tabs) tabs.style.left = 0;
	}
	if(eXo.core.I18n.isLT()) uiWorkspace.style.marginLeft = "0px" ;
	else uiWorkspace.style.marginRight = "0px" ;
};