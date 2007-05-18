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
	} else {
	var objectParent = document.getElementById("UIControlWorkspace");
	if(objectParent) {
		var uiControlWSWorkingArea = document.getElementById("UIControlWSWorkingArea");
		var uiWorkspaceContainer = eXo.core.DOMUtil.findFirstDescendantByClass(objectParent, "div", "UIWorkspaceContainer") ;
		if(uiWorkspaceContainer.style.display == "block") {
			var scrollArea = eXo.core.DOMUtil.findFirstDescendantByClass(objectParent, "div", "ScrollArea") ;
			var tmp = objectParent.offsetHeight - 72;
			if(scrollArea != null) {
				scrollArea.style.height = "auto";
				var heightChild = scrollArea.offsetHeight;
				var jsContainer = eXo.core.DOMUtil.findFirstDescendantByClass(scrollArea, "div", "JSContainer") ;
				if(jsContainer){
					scrollArea.style.width = "210px";
					jsContainer.style.width = "208px";
				}
				if(uiControlWSWorkingArea) {
				  var maxHeight = uiControlWSWorkingArea.offsetHeight ;
				} 
				var deltaResize = maxHeight - tmp;
				if(deltaResize > 0) {
					scrollArea.style.overflow = "auto";
					scrollArea.style.height = heightChild - deltaResize + "px";
					if(jsContainer) {
						jsContainer.style.width = scrollArea.offsetWidth - 22 + "px";
					}
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
	this.leftArrow = null;
	this.rightArrow = null;
	this.mainContainer = null; // The HTML DOM element that contains the tabs, the arrows, etc
	this.arrowsContainer = null // The HTML DOM element that contains the arrows
	this.otherHiddenIndex = -1;
};
ScrollManager.prototype.initArrowButton = function(arrow, dir, normalClass, overClass, disabledClass) {
	arrow.direction = dir; // "left" or "right" (up or down)
	arrow.overClass = overClass;
	arrow.disabledClass = disabledClass;
	arrow.styleClass = normalClass;
	arrow.scrollMgr = this;
	arrow.onmouseover = this.mouseOverArrow;
	arrow.onmouseout = this.mouseOutArrow;
	arrow.arrowClick = this.scroll;
	arrow.onclick = arrow.arrowClick;
	if (dir == "left") this.leftArrow = arrow;
	else if (dir == "right") this.rightArrow = arrow;
};

ScrollManager.prototype.enableArrow = function(arrow, enabled) {
	if (arrow && !enabled) { // disables the arrow
		arrow.className = arrow.disabledClass;
		arrow.onclick = null;
	} else if (arrow && enabled) { // enables the arrow
		arrow.className = arrow.styleClass;
		arrow.onclick = arrow.arrowClick;
	}
};

ScrollManager.prototype.mouseOverArrow = function(e) {
	var arrow = this;
	if (arrow.onclick && arrow.className == arrow.styleClass) {
		// mouse over
		if (!e) var e = window.event;
		if (arrow == eXo.core.Browser.getEventSource(e)) arrow.className = arrow.overClass;
	}
};

ScrollManager.prototype.mouseOutArrow = function(e) {
	var arrow = this;
	if (arrow.onclick && arrow.className == arrow.overClass) {
		// mouse out
		arrow.className = arrow.styleClass;
	}
};

ScrollManager.prototype.init = function() {
	this.firstVisibleIndex = 0;
	this.lastVisibleIndex = -1;
	// Hides the arrows by default
	this.arrowsContainer.style.display = "none";
	this.arrowsContainer.space = null;
	this.mainContainer.space = null;
};

ScrollManager.prototype.loadElements = function(elementClass, clean) {
	if (clean) this.cleanElements();
	this.elements.clear();
//	var tabs = eXo.core.DOMUtil.findDescendantsByClass(this.mainContainer, "div", elementClass);
//	for (var i = 0; i < tabs.length; i++) {
//		var tabLink = eXo.core.DOMUtil.findDescendantsByTagName(tabs[i], "a")[0];
//		if (this.initFunction && tabLink.href.indexOf("initAllManagers") == -1) {
//			tabLink.href = tabLink.href.substr(0, tabLink.href.length-1).concat(", eXo.portal.UIPortalControl.initAllManagers)");
//		}
//		this.elements.push(tabs[i]);
//	}
	this.elements.pushAll(eXo.core.DOMUtil.findDescendantsByClass(this.mainContainer, "div", elementClass));
};

ScrollManager.prototype.checkAvailableSpace = function(maxSpace) { // in pixels
	if (!maxSpace) var maxSpace = this.getElementSpace(this.mainContainer)-this.getElementSpace(this.arrowsContainer);
	var elementsSpace = 0;
	for (var i = 0; i < this.elements.length; i++) {
		elementsSpace += this.getElementSpace(this.elements[i]);
		if (elementsSpace <= maxSpace) { // If the tab fits in the available space
			this.elements[i].isVisible = true;
			this.lastVisibleIndex = i;
		} else { // If the available space is full
			this.elements[i].isVisible = false;
		}
	}
};

ScrollManager.prototype.getElementsSpace = function(indexStart, indexEnd) {
	if (indexStart == null && indexEnd == null) {
		var indexStart = 0;
		var indexEnd = this.elements.length-1;
	}
	var elementsSpace = 0;
	if (indexStart >= 0 && indexEnd <= this.elements.length-1) {
		for (var i = indexStart; i <= indexEnd; i++) {
			elementsSpace += this.getElementSpace(this.elements[i]);
		}
	}
	return elementsSpace;
};

ScrollManager.prototype.getElementSpace = function(element) {
	if (element && element.space) return element.space;
	var elementSpace = 0;
	var wasHidden = false;
	if (element) {
		if (element.style.display == "none") {
			element.style.display = "block";
			wasHidden = true;
		}
		if (this.axis == 0) { // horizontal tabs
			elementSpace += element.offsetWidth;
			elementSpace += eXo.core.DOMUtil.getStyle(element, "marginLeft", true);
			elementSpace += eXo.core.DOMUtil.getStyle(element, "marginRight", true);
			if (element.decorator) elementSpace += this.getElementSpace(element.decorator);
		} else if (this.axis == 1) { // vertical tabs
			elementSpace += element.offsetHeight;
			elementSpace += eXo.core.DOMUtil.getStyle(element, "marginTop", true);
			elementSpace += eXo.core.DOMUtil.getStyle(element, "marginBottom", true);
			if (element.decorator) elementSpace += this.getElementSpace(element.decorator);
		}
		if (wasHidden) element.style.display = "none";
	}
	element.space = elementSpace;
	return elementSpace;
};

ScrollManager.prototype.cleanElements = function() {
	for (var i = 0; i < this.elements.length; i++) {
		this.elements[i].space = null;
		if (this.elements[i].decorator) this.elements[i].decorator.space = null;
	}
}

ScrollManager.prototype.scroll = function(e) {
	if (!e) var e = window.event;
	e.cancelBubble = true;
	var src = eXo.core.Browser.getEventSource(e);
	if (src.scrollMgr) {
		if (src.scrollMgr.otherHiddenIndex != -1) {
			src.scrollMgr.elements[src.scrollMgr.otherHiddenIndex].isVisible = true;
			if (src.scrollMgr.lastDirection == 1) src.scrollMgr.firstVisibleIndex--;
			else if (src.scrollMgr.lastDirection == 0) src.scrollMgr.lastVisibleIndex++;
			src.scrollMgr.otherHiddenIndex = -1;
		}
		if (src.direction == "left") src.scrollMgr.scrollLeft();
		else if (src.direction == "right") src.scrollMgr.scrollRight();
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
	alert("test");
};

ScrollManager.prototype.scrollRight = function() { // Same for scrollDown
	if (this.lastVisibleIndex < this.elements.length-1) {
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
	var elementsSpace = 0;
	var maxSpace = this.getElementSpace(this.mainContainer)-this.getElementSpace(this.arrowsContainer);
	// Displays the elements
	for (var i = 0; i < this.elements.length; i++) {
		if (this.elements[i].isVisible) {
			elementsSpace += this.getElementSpace(this.elements[i]);
			this.elements[i].style.display = "block";
		} else {
			this.elements[i].style.display = "none";
			this.arrowsContainer.style.display = "block";
		}
	}
	// Checks that the available space is long enough, hides an element if not
	if (elementsSpace > maxSpace) {
		if (this.lastDirection == 1) {
			if (this.firstVisibleIndex >= 0 && this.firstVisibleIndex < this.elements.length-1) {
				this.otherHiddenIndex = this.firstVisibleIndex;
				this.elements[this.firstVisibleIndex].isVisible = false;
				this.elements[this.firstVisibleIndex++].style.display = "none";
			}
		} else {
			if (this.lastVisibleIndex > 0 && this.lastVisibleIndex < this.elements.length) {
				this.otherHiddenIndex = this.lastVisibleIndex;
				this.elements[this.lastVisibleIndex].isVisible = false;
				this.elements[this.lastVisibleIndex--].style.display = "none";
			}
		}
	}
	// Enables/Disables the arrow buttons
	if (this.firstVisibleIndex == 0) this.enableArrow(this.leftArrow, false);
	else this.enableArrow(this.leftArrow, true);
	
	if (this.lastVisibleIndex == this.elements.length-1) this.enableArrow(this.rightArrow, false);
	else this.enableArrow(this.rightArrow, true);
	
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



UIPortalControl.prototype.backgroundBody = function() {
	if(eXo.core.Browser.isIE6()) return;
	var mainPage = document.getElementById("UIPortalApplication");
	var backg = document.getElementById("Backgroud");
	mainPage.style.position = "absolute";
	mainPage.style.zIndex = 1;
	mainPage.style.top = "0px";
	mainPage.style.left = "0px";
	mainPage.style.right = "0px";
	mainPage.style.bottom = "0px";
	backg.style.top = document.documentElement.scrollTop + "px";
	backg.style.bottom = (-document.documentElement.scrollTop) + "px";
};

eXo.portal.UIPortalControl = new UIPortalControl();
