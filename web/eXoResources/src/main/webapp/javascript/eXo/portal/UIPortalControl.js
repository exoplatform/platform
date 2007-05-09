function UIPortalControl() {
  this.scrollManagers = new Array();
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
	var uiPortalLoginFormControl = document.getElementById("UIPortalLoginFormControl");
	if(uiPortalLoginFormControl) {
		uiPortalLoginFormControl.onkeypress = eXo.portal.UIPortalControl.onEnterPress ;
	}
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

/*********** Scroll Manager *************/
function ScrollManager() {
	this.elements = new Array();
	this.firstVisibleIndex = 0;
	this.lastVisibleIndex = -1;  
	this.axis = 0; // 0 : horizontal scroll, 1 : vertical scroll
	this.lastDirection = null; // 0 : left or up scroll, 1 : right or down scroll
	this.callback = null; // callback function when a scroll is done
	this.initFunction = null;
};

ScrollManager.prototype.init = function() {
	this.firstVisibleIndex = 0;
	this.lastVisibleIndex = -1;
};

ScrollManager.prototype.scrollLeft = function() { // Same for scrollUp
	if (this.scrollMgr && this.scrollMgr.firstVisibleIndex > 0) {
		this.scrollMgr.lastDirection = 0;
		// hides the last (right or down) element and moves lastVisibleIndex to the left
		this.scrollMgr.elements[this.scrollMgr.lastVisibleIndex--].isVisible = false;
		// moves firstVisibleIndex to the left and shows the first (left or up) element
		this.scrollMgr.elements[--this.scrollMgr.firstVisibleIndex].isVisible = true;
		this.scrollMgr.renderElements();
	}
};

ScrollManager.prototype.scrollUp = function() {
	if (this.scrollMgr) this.scrollMgr.scrollLeft();
};

ScrollManager.prototype.scrollRight = function() { // Same for scrollDown
	if (this.scrollMgr && this.scrollMgr.lastVisibleIndex < this.scrollMgr.elements.length-1) {/*Visibility*/
		this.scrollMgr.lastDirection = 1;
		// hides the first (left or up) element and moves firstVisibleIndex to the right
		this.scrollMgr.elements[this.scrollMgr.firstVisibleIndex++].isVisible = false;
		// moves lastVisibleIndex to the right and shows the last (right or down) element
		this.scrollMgr.elements[++this.scrollMgr.lastVisibleIndex].isVisible = true;
		this.scrollMgr.renderElements();
	}
};

ScrollManager.prototype.scrollDown = function() {
	if (this.scrollMgr) this.scrollMgr.scrollRight();
};

ScrollManager.prototype.renderElements = function() {
	for (var i = 0; i < this.elements.length; i++) {
		if (this.elements[i].isVisible) {
			this.elements[i].style.display = "block";
		} else {
			this.elements[i].style.display = "none";
		}
	}
	if (typeof(this.callback) == "function") this.callback();
};

UIPortalControl.prototype.initAllManagers = function() {
	var managers = eXo.portal.UIPortalControl.scrollManagers;
	for (var i = 0; i < managers.length; i++) {
		if (typeof(managers[i].initFunction) == "function") managers[i].initFunction();
	}
};

UIPortalControl.prototype.newScrollManager = function() {
	var tmpMgr = new ScrollManager();
	eXo.portal.UIPortalControl.scrollManagers.push(tmpMgr);
	return tmpMgr;
};
/*********** Scroll Manager *************/
eXo.portal.UIPortalControl = new UIPortalControl();
