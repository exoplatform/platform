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
};

/*For Navigation Tree*/
UIPortalControl.prototype.collapseTree = function(selectedElement ) {
  var DOMUtil = eXo.core.DOMUtil ;
  
  var parentNode = DOMUtil.findAncestorByClass(selectedElement, "Node");
  var childrenContainer = DOMUtil.findFirstDescendantByClass(parentNode, "div", "ChildrenContainer");
  var expandIcon = document.createElement('a');
  expandIcon.href = childrenContainer.getAttribute("actionLink") ;
  expandIcon.className = "ExpandIcon" ;
  expandIcon.innerHTML = "<span></span>" ;
  parentNode.removeChild(childrenContainer);
  parentNode.insertBefore(expandIcon, selectedElement);
  parentNode.removeChild(selectedElement);
  eXo.portal.UIPortalControl.fixHeight();
};

/** Created: by Duy Tu - fixHeight function to UIControlWorkspace**/
UIPortalControl.prototype.fixHeight = function() {
	var objectParent = document.getElementById("UIControlWorkspace");
	var uiWorkspaceContainer = eXo.core.DOMUtil.findFirstDescendantByClass(objectParent, "div", "UIWorkspaceContainer") ;
	if(uiWorkspaceContainer.style.display == "block") {
		var scrollArea = eXo.core.DOMUtil.findFirstDescendantByClass(objectParent, "div", "ScrollArea") ;
		if(scrollArea != null) {
			var jsContainer = eXo.core.DOMUtil.findFirstDescendantByClass(scrollArea, "div", "JSContainer") ;
			var maxHeight = objectParent.offsetHeight - 205 ;
			scrollArea.style.height = "auto";
			scrollArea.style.width = "210px";
			jsContainer.style.width = "208px";
			var heightChild = scrollArea.offsetHeight ;
			if(maxHeight > 0) {
				if(heightChild > maxHeight) {
					scrollArea.style.overflow = "auto";
					scrollArea.style.height = maxHeight + "px";
					jsContainer.style.width = scrollArea.offsetWidth - 22 + "px";
				}
			} else {
			  scrollArea.style.overflow = "hidden";
				scrollArea.style.height = "1px";
			}

		}
	}
} ;

UIPortalControl.prototype.onKeyPress = function() {
	document.body.onkeypress = eXo.portal.UIPortalControl.onEnterPress ;
};

UIPortalControl.prototype.onEnterPress = function(e) {
	var uiPortalLoginFormAction = document.getElementById("UIPortalLoginFormAction");
	
	if(uiPortalLoginFormAction) {
		var code;
		if(!e) var e = window.event;
		if(e.keyCode) code = e.keyCode;
		else if (e.which) code = e.which;
		
		if(code == 13) {
			window.location.href = uiPortalLoginFormAction.href ;
		}
	}
};

eXo.portal.UIPortalControl = new UIPortalControl();
