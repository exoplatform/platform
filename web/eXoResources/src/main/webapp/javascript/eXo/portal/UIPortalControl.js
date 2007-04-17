function UIPortalControl() {
  
};

UIPortalControl.prototype.changeWindowState = function(id, state) {
  var params = [
    {name: "portletId", value: id},
    {name: "objectId", value: state}
  ] ;
	ajaxGet(eXo.env.server.createPortalURL("UIPortal", "ChangeWindowState", true, params));
};

/* Created Function by Duy Tu */
UIPortalControl.prototype.showHiddenContent = function(selectedElement) {
	var DOMUtil = eXo.core.DOMUtil ;
	var ancestorClass = DOMUtil.findAncestorByClass(selectedElement, "UILoggedInfo");
	var classNormal = DOMUtil.findAncestorByClass(selectedElement, "NormalBG");
	var classSelected = DOMUtil.findFirstDescendantByClass(ancestorClass, "div", "SelectedBG");
	var contentStyle = DOMUtil.findDescendantsByClass(ancestorClass, "div", "StyleContent") ;
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
	} 
}

UIPortalControl.prototype.changeSkin = function (form, action) {
	if (eXo.webui.UIItemSelector.SelectedItem != null) {
		var component = eXo.webui.UIItemSelector.SelectedItem.component;
		var option = eXo.webui.UIItemSelector.SelectedItem.option;
		var params = [{name : component, value : option}];
	}
	ajaxGet(eXo.env.server.createPortalURL(form, action, true, params));
}

UIPortalControl.prototype.changeLanguage = function (form, action) {
	if (eXo.webui.UIItemSelector.SelectedItem != null) {
		var component = eXo.webui.UIItemSelector.SelectedItem.component;
		var option = eXo.webui.UIItemSelector.SelectedItem.option;
		var params = [{name : component, value : option}];
	}
	ajaxGet(eXo.env.server.createPortalURL(form, action, true, params));
}


eXo.portal.UIPortalControl = new UIPortalControl();
