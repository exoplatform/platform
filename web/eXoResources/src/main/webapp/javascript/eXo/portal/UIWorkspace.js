function UIWorkspace(id) {
  this.id = id ;
  this.showControlWorkspace = false ;
  this.isFirstTime = true ;
};

eXo.portal.UIWorkspace = new UIWorkspace("UIWorkspace") ;

/*#############################-Working Workspace-##############################*/
if(eXo.portal.UIWorkingWorkspace == undefined) {
  eXo.portal.UIWorkingWorkspace = new UIWorkspace("UIWorkingWorkspace") ;
};

eXo.portal.UIWorkingWorkspace.onResize = function() {
	var uiWorkspace = document.getElementById(eXo.portal.UIWorkingWorkspace.id) ;
	var uiControlWorkspace = document.getElementById("UIControlWorkspace") ;
  var controlWorkspaceWidth = eXo.portal.UIControlWorkspace.width ;
	if(eXo.core.Browser.isIE6()) {
		var tabs = eXo.core.DOMUtil.findFirstDescendantByClass(uiWorkspace, "div", "UIHorizontalTabs") ;
		if(tabs) tabs.style.left = 0;
	}
  if(uiControlWorkspace) {
  	if(eXo.core.I18n.isLT()) uiWorkspace.style.marginLeft = controlWorkspaceWidth + "px" ;
  	else uiWorkspace.style.marginRight = controlWorkspaceWidth + "px" ;
  } else {
  	if(eXo.core.I18n.isLT()) uiWorkspace.style.marginLeft = "0px" ;
  	else uiWorkspace.style.marginRight = "0px" ;
  }
};

eXo.portal.UIWorkingWorkspace.resizeWorkspacePanel = function(h) {
  var workspacePanel = document.getElementById("UIWorkspacePanel");
  workspacePanel.style.height = h + "px";
};

eXo.portal.UIWorkingWorkspace.reorganizeWindows = function(showControlWorkspace) {
	var uiDesk = document.getElementById("UIPageDesktop");
	if (uiDesk) {
		var uiCtrl = document.getElementById("UIControlWorkspace");
		var uiWindows = eXo.core.DOMUtil.findDescendantsByClass(uiDesk, "div", "UIWindow");
		for (var k = 0; k < uiWindows.length; k++) {
			if (uiWindows[k].style.display != "none") {
				// We reorganize the opened windows (display != none) only
				var uiWindow = uiWindows[k];
				if (showControlWorkspace) {
					// When the ControlWorkspace is shown
					uiWindow.oldW = uiWindow.offsetWidth;
					if ((uiWindow.offsetLeft + uiWindow.offsetWidth) > uiDesk.offsetWidth) {
						/*
						 * If the window is too large to fit in the screen after the control panel is shown
						 * we remove the control panel width to the window width
						 */ 
						uiWindow.style.width = 
						(uiWindow.offsetWidth - eXo.portal.UIControlWorkspace.defaultWidth + eXo.portal.UIControlWorkspace.slidebarDefaultWidth) + "px";
						
						if ((eXo.desktop.UIWindow.originalWidth + uiWindow.offsetLeft) > uiDesk.offsetWidth && uiWindow.maximized) {
							/* if the maximized window original size is too large
							 * we remove the control panel width to the original width
							 * when the window is demaximized but the control panel is still there, the window
							 * will not come out of the screen
							 */
							eXo.desktop.UIWindow.originalWidth -= eXo.portal.UIControlWorkspace.defaultWidth;
						}
					}
				} else {
					// When the ControlWorkspace is hidden
					if (uiWindow.maximized) {
						// If the window is maximized, we set the size to its maximum : the desktop size
						uiWindow.style.width = uiDesk.offsetWidth + "px";
					} else {
						uiWindow.style.width = uiWindow.oldW + "px";
					}
				}
			}
		}
	}
};