function UIPermissionSelectorTab() {
  
};

UIPermissionSelectorTab.prototype.init = function() {
  
};

UIPermissionSelectorTab.prototype.displayBlockContent = function(clickedEle) {
	var permissionTypeBar = eXo.core.DOMUtil.findAncestorByClass(clickedEle, "PermissionTypeBar") ;
	var permissionButton = eXo.core.DOMUtil.findChildrenByClass(permissionTypeBar, "div", "PermissionButton") ;
	var selectedPermissionInfo = eXo.core.DOMUtil.findChildrenByClass(permissionTypeBar.parentNode, "div", "SelectedPermissionInfo") ;

	for(var i = 0; i < permissionButton.length; i++) {
		if(permissionButton[i] == clickedEle) {
			permissionButton[i].style.fontWeight = "bold" ;
			selectedPermissionInfo[i].style.display = "block" ;
		} else {
			permissionButton[i].style.fontWeight = "100" ;
			selectedPermissionInfo[i].style.display = "none" ;
		}
	}
};

eXo.webui.UIPermissionSelectorTab = new UIPermissionSelectorTab() ;