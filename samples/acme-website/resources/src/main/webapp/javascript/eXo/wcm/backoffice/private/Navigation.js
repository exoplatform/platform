function NavigationForm() {}

NavigationForm.prototype.enableCategoryParams = function() {
	var formObj = document.getElementById("UINavigationForm");
	var tdContextualFolder = eXo.core.DOMUtil.findDescendantsByClass(formObj, "td", "ClickableRadio")[0];

	var inputs = eXo.core.DOMUtil.getChildrenByTagName(tdContextualFolder, "input");
	var enableInput = inputs[0];
	var disableInput = inputs[1];
	
	var showInPage = document.getElementById("TargetListPageFormStringInput");
	var imgSelectTargetPage = eXo.core.DOMUtil.findNextElementByTagName(showInPage, "img");
	var onclick = imgSelectTargetPage.getAttribute("onclick");
	imgSelectTargetPage.setAttribute("onclick_", onclick);
	var imgRemoveTargetPage = eXo.core.DOMUtil.findNextElementByTagName(imgSelectTargetPage, "img");
	var onclickRemove = imgRemoveTargetPage.getAttribute("onclick");
	imgRemoveTargetPage.setAttribute("onclick_", onclickRemove);
	var paramName = document.getElementById("ListShowCLVByStringInput");

	enableInput.setAttribute("onmouseup", "eXo.ecm.Navigation.enableParams(this)");
	disableInput.setAttribute("onmouseup", "eXo.ecm.Navigation.disableParams(this)");
	if (enableInput.checked) {
		showInPage.removeAttribute('disabled');
		if(imgSelectTargetPage.getAttribute("onclick_") !=null){
			imgSelectTargetPage.setAttribute("onclick", onclick);
		}
		if(imgRemoveTargetPage.getAttribute("onclick_") !=null){
			imgRemoveTargetPage.setAttribute("onclick", onclickRemove);
		}
		paramName.removeAttribute('disabled');
	} else {
		showInPage.setAttribute('disabled', '');
		imgSelectTargetPage.setAttribute("onclick", function() { return false; });
		imgRemoveTargetPage.setAttribute("onclick", function() { return false; });
		paramName.setAttribute('disabled', '');
	}
};

NavigationForm.prototype.enableParams = function(obj){

	var showInPage = document.getElementById("TargetListPageFormStringInput");
	var imgSelectTargetPage = eXo.core.DOMUtil.findNextElementByTagName(showInPage, "img");
	var imgRemoveTargetPage = eXo.core.DOMUtil.findNextElementByTagName(imgSelectTargetPage, "img");
	var paramName = document.getElementById("ListShowCLVByStringInput");
	showInPage.removeAttribute('disabled');
	if(imgSelectTargetPage.getAttribute("onclick_") !=null){
		var onclick_ = imgSelectTargetPage.getAttribute("onclick_");
		imgSelectTargetPage.setAttribute("onclick", onclick_);
	}
	if(imgRemoveTargetPage.getAttribute("onclick_") !=null){
		var onclick_ = imgRemoveTargetPage.getAttribute("onclick_");
		imgRemoveTargetPage.setAttribute("onclick", onclick_);
	}
	paramName.removeAttribute('disabled');
};

NavigationForm.prototype.disableParams = function(obj){

	var showInPage = document.getElementById("TargetListPageFormStringInput");
	var imgSelectTargetPage = eXo.core.DOMUtil.findNextElementByTagName(showInPage, "img");
	var imgRemoveTargetPage = eXo.core.DOMUtil.findNextElementByTagName(imgSelectTargetPage, "img");
	var paramName = document.getElementById("ListShowCLVByStringInput");
	showInPage.setAttribute('disabled', '');
	imgSelectTargetPage.setAttribute("onclick", function() { return false; });
	imgRemoveTargetPage.setAttribute("onclick", function() { return false; });
	paramName.setAttribute('disabled', '');
};

eXo.ecm.Navigation = new NavigationForm();