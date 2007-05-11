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
	if(eXo.core.Browser.isIE6()) {
		var e = window.event ;
		if(e) e.cancelBubble = true ;
	}
	//alert("test");
	var objectParent = document.getElementById("UIControlWorkspace");
	if(objectParent) {
		var uiControlWSWorkingArea = document.getElementById("UIControlWSWorkingArea");
		var uiWorkspaceContainer = eXo.core.DOMUtil.findFirstDescendantByClass(objectParent, "div", "UIWorkspaceContainer") ;
		if(uiWorkspaceContainer.style.display == "block") {
			var scrollArea = eXo.core.DOMUtil.findFirstDescendantByClass(objectParent, "div", "ScrollArea") ;
			if(scrollArea != null) {
				uiControlWSWorkingArea.style.height = "auto";
				var jsContainer = eXo.core.DOMUtil.findFirstDescendantByClass(scrollArea, "div", "JSContainer") ;
				if(jsContainer){
					scrollArea.style.width = "210px";
					jsContainer.style.width = "208px";
				}
				if(uiControlWSWorkingArea) {
					uiControlWSWorkingArea.style.height = "auto";
					scrollArea.style.height = "auto";
				//alert(scrollArea.offsetHeight);
				  var maxHeight = uiControlWSWorkingArea.offsetHeight + 10 ;
				} 
				var deltaResize = maxHeight - (uiWorkspaceContainer.offsetHeight - 62);
				if(deltaResize > 0) {
					scrollArea.style.overflow = "auto";
					scrollArea.style.height = scrollArea.offsetHeight - deltaResize + "px";
					//uiControlWSWorkingArea.style.border = "1px solid red";
					//scrollArea.style.border = "1px solid red";
					if(jsContainer) {
						jsContainer.style.width = scrollArea.offsetWidth - 22 + "px";
					}
				}
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
	this.arrowButtons = new Array(); // 0 : left (up) arrow button, 1 : right (down) arrow button
};

ScrollManager.prototype.initArrowButtons = function(left, right) { // or (up, down)
	var arrowOver = function(e) {
		if (!e) var e = window.event;
		if (this == eXo.core.Browser.getEventSource(e)) this.className = this.overClass;
	};
	var arrowOut = function(e) {
		this.className = this.styleClass;
	};
	var arrowDisabled = function() {
		this.className = this.disabledClass;
		this.onclick = null; this.onmouseover = null; this.onmouseout = null;
	};
	var arrowEnabled = function() {
		this.className = this.styleClass;
		this.onclick = this.arrowClick; this.onmouseover = arrowOver; this.onmouseout = arrowOut;
	};
	var initArrow = function(arrow, overClass, disabledClass, mgr) {
		arrow.overClass = overClass;
		arrow.disabledClass = disabledClass;
		arrow.styleClass = arrow.className;
		arrow.scrollMgr = mgr;
		arrow.onmouseover = arrowOver;
		arrow.onmouseout = arrowOut;
		arrow.disable = arrowDisabled;
		arrow.enable = arrowEnabled;
		arrow.arrowClick = mgr.scroll;
		arrow.onclick = arrow.arrowClick;
	};
	left.direction = 0; // 0 for left or up arrow
	right.direction = 1; // 1 for right or down arrow
	initArrow(left, "HighlightScrollLeftButton", "DisableScrollLeftButton", this);
	initArrow(right, "HighlightScrollRightButton", "DisableScrollRightButton", this);
	
	this.arrowButtons.push(left, right); // or (up, down)
};

ScrollManager.prototype.init = function() {
	this.firstVisibleIndex = 0;
	this.lastVisibleIndex = -1;
};

ScrollManager.prototype.scroll = function(e) {
	if (!e) var e = window.event;
	e.cancelBubble = true;
	var src = eXo.core.Browser.getEventSource(e);
	if (src.scrollMgr) {
		if (src.direction == 0) src.scrollMgr.scrollLeft();
		else if (src.direction == 1) src.scrollMgr.scrollRight();
	}
	return false;
};

ScrollManager.prototype.scrollLeft = function() { // Same for scrollUp
	if (this.firstVisibleIndex > 0) {
		this.lastDirection = 0;
		// hides the last (right or down) element and moves lastVisibleIndex to the left
		this.elements[this.lastVisibleIndex--].isVisible = false;
		// moves firstVisibleIndex to the left and shows the first (left or up) element
		this.elements[--this.firstVisibleIndex].isVisible = true;
		this.renderElements();
	}
};

ScrollManager.prototype.scrollUp = function() {
	if (this.scrollMgr) this.scrollMgr.scrollLeft();
};

ScrollManager.prototype.scrollRight = function() { // Same for scrollDown
	if (this.lastVisibleIndex < this.elements.length-1) { /*this.scrollMgr && */
		this.lastDirection = 1;
		// hides the first (left or up) element and moves firstVisibleIndex to the right
		this.elements[this.firstVisibleIndex++].isVisible = false;
		// moves lastVisibleIndex to the right and shows the last (right or down) element
		this.elements[++this.lastVisibleIndex].isVisible = true;
		this.renderElements();
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
	if (this.firstVisibleIndex == 0) this.arrowButtons[0].disable();
	else this.arrowButtons[0].enable();
	
	if (this.lastVisibleIndex == this.elements.length-1) this.arrowButtons[1].disable();
	else this.arrowButtons[1].enable();
	
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
