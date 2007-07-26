function UIPortalControl() {
  this.scrollManagers = new Array();
  this.t = 0;
};

UIPortalControl.prototype.changeWindowState = function(id, state) {
  var params = [
    {name: "portletId", value: id},
    {name: "objectId", value: state}
  ] ;
	ajaxGet(eXo.env.server.createPortalURL("UIPortal", "ChangeWindowState", true, params));
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

/** Created: by Duy Tu - fixHeight function to UIControlWorkspace
 **/
UIPortalControl.prototype.fixHeight = function() {
	var objectParent = document.getElementById("UIControlWorkspace");
	if(objectParent) {
		var DOMUtil = eXo.core.DOMUtil;
		var uiControlWSWorkingArea = document.getElementById("UIControlWSWorkingArea");
		var uiWorkspaceContainer = DOMUtil.findFirstDescendantByClass(objectParent, "div", "UIWorkspaceContainer") ;
		if(uiWorkspaceContainer.style.display == "block") {
			var scrollArea = DOMUtil.findFirstDescendantByClass(objectParent, "div", "ScrollArea") ;
			if(scrollArea != null) {
				if(eXo.core.Browser.isIE6()) {
					var html = document.getElementsByTagName("html")[0];
				  var tmp = html.offsetHeight - 82;
				} else {
				  var tmp = objectParent.offsetHeight - 72;
				}
				/* 72 is total value (UserWorkspaceTitleHeight + UIExoStartHeight)
				 */
				var firstHeight = scrollArea.offsetHeight;
				scrollArea.style.height = "auto";
				scrollArea.style.width = "auto";
				var heightChild = scrollArea.offsetHeight;
				var maxHeight = 0;
				if(uiControlWSWorkingArea) {
				  maxHeight = uiControlWSWorkingArea.offsetHeight ;
				} 
				var deltaResize = maxHeight - tmp;
				if(deltaResize > 0 && (heightChild - deltaResize) > 0) {
					scrollArea.style.overflow = "auto";
					scrollArea.style.height = heightChild - deltaResize + "px";
					scrollArea.style.width = scrollArea.offsetWidth  + "px";
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
		if(!e) e = window.event;
		if(e.keyCode) code = e.keyCode;
		else if (e.which) code = e.which;
		if(code ==13) {
			if(this.t != 13) {
		    window.location.href = uiPortalLoginFormAction.href ;
			}
		  this.t = code;
		}
	}
};

/*********** Scroll Manager *************/
/**
 * This class adds a scroll functionnality to elements when there is not enough space to show them all
 * Use : create a manager with the function newScrollManager
 *     : create a load and an init function in your js file
 *     : the load function sets all the base attributes, the init function recalculates the visible elements
 *       (e.g. when the window is resized)
 *     : create a callback function if necessary, to add specific behavior to your scroll
 *       (e.g. if an element must be always visible)
 */
function ScrollManager() {
	this.id = null;
	this.elements = new Array();
	this.firstVisibleIndex = 0;
	this.lastVisibleIndex = -1;
	this.otherHiddenIndex = -1; // the index in elements of an element hidden because of a lack of space
	this.axis = 0; // horizontal scroll : 0 , vertical scroll : 1
	this.lastDirection = null; // left or up scroll : 0, right or down scroll : 1
	this.callback = null; // callback function when a scroll is done
	this.initFunction = null; // the init function in the files that use this class
	this.leftArrow = null;
	this.rightArrow = null;
	this.mainContainer = null; // The HTML DOM element that contains the tabs, the arrows, etc
	this.arrowsContainer = null // The HTML DOM element that contains the arrows
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
		if (!e) e = window.event;
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
	if(this.arrowsContainer)  {
		this.arrowsContainer.style.display = "none";
		this.arrowsContainer.space = null;
		this.mainContainer.space = null;
	}
};

ScrollManager.prototype.loadElements = function(elementClass, clean) {
	if (clean) this.cleanElements();
	this.elements.clear();
	this.elements.pushAll(eXo.core.DOMUtil.findDescendantsByClass(this.mainContainer, "div", elementClass));
};

ScrollManager.prototype.checkAvailableSpace = function(maxSpace) { // in pixels
 /*
  * Calculates the available space for the elements, and inits the elements array
  */
	if (!maxSpace) maxSpace = this.getElementSpace(this.mainContainer)-this.getElementSpace(this.arrowsContainer);
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
		indexStart = 0 ;
		indexEnd = this.elements.length-1 ;
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
			// decorator is another element that is linked to the current element (e.g. a separator bar)
			if (element.decorator) elementSpace += this.getElementSpace(element.decorator);
		} else if (this.axis == 1) { // vertical tabs
			elementSpace += element.offsetHeight;
			elementSpace += eXo.core.DOMUtil.getStyle(element, "marginTop", true);
			elementSpace += eXo.core.DOMUtil.getStyle(element, "marginBottom", true);
			if (element.decorator) elementSpace += this.getElementSpace(element.decorator);
		}
		if (wasHidden) element.style.display = "none";
	}
	// Store the calculated value for faster return on next calls. To recalculate, set element.space to null before.
	element.space = elementSpace;
	return elementSpace;
};

ScrollManager.prototype.cleanElements = function() {
	for (var i = 0; i < this.elements.length; i++) {
		this.elements[i].space = null;
		if (this.elements[i].decorator) this.elements[i].decorator.space = null;
	}
};

ScrollManager.prototype.scroll = function(e) {
	/*
	 * Function called when an arrow is clicked. Shows an additionnal element and calls the 
	 * appropriate scroll function (left or right)
	 */
	if (!e) e = window.event;
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
	/*
	 * Called by a scroll function. Renders the visible elements depending on the elements array
	 * If the new visible elements are too big, hides an additional element and keep its index in otherHiddenIndex
	 */
	/*
	 * Each time a scroll event occurs, at least one element is hidden, and one is shown. These elements can have
	 * a different width, hence the total width of the tabs changes. This is why we have to check if the
	 * new width is short enough so the arrows buttons are still well rendered. To do that, we add the elements
	 * width to each other, and we compare this width (elementsSpace) to the container width (maxSpace). If the
	 * total width is too large (with a 3 pixels range), we have to hide another tab.
	 * PS: for vertical tabs, replace width by height.
	 */
	var elementsSpace = 0;
	var maxSpace = this.getElementSpace(this.mainContainer)-this.getElementSpace(this.arrowsContainer);
	// Displays the elements
	for (var i = 0; i < this.elements.length; i++) {
		if (this.elements[i].isVisible) { // if the element should be rendered...
			elementsSpace += this.getElementSpace(this.elements[i]);
			if (maxSpace-elementsSpace <= 3) {
				/* 
				 * In certain browsers, a difference of 0 or 1 pixel between the container and the elements length
				 * is too big and the last element doesn't fit in the remaining space, hence we have to check
				 * for a bigger difference
				 */ 
				if (this.lastDirection == 1) {
					// If we are scrolling right or down, we hide the lefter or upper element 
					if (this.firstVisibleIndex < this.elements.length-1) {
						this.otherHiddenIndex = this.firstVisibleIndex;
						this.elements[this.firstVisibleIndex].isVisible = false;
						this.elements[this.firstVisibleIndex++].style.display = "none";
					}
				} else {
					// If we are scrolling left or up, we hide the righter or downer element
					if (this.lastVisibleIndex > 0) {
						this.otherHiddenIndex = this.lastVisibleIndex;
						this.elements[this.lastVisibleIndex].isVisible = false;
						this.elements[this.lastVisibleIndex--].style.display = "none";
					}
				}
				if (this.otherHiddenIndex != -1) elementsSpace -= this.getElementSpace(this.elements[this.otherHiddenIndex]);
			}
			this.elements[i].style.display = "block";
		} else { // if the element must not be rendered...
			this.elements[i].style.display = "none";
			this.arrowsContainer.style.display = "block";
		}
	}
	// Enables/Disables the arrow buttons depending on the elements to show
	if (this.firstVisibleIndex == 0) this.enableArrow(this.leftArrow, false);
	else this.enableArrow(this.leftArrow, true);
	
	if (this.lastVisibleIndex == this.elements.length-1) this.enableArrow(this.rightArrow, false);
	else this.enableArrow(this.rightArrow, true);
	
	if (typeof(this.callback) == "function") this.callback();
};

UIPortalControl.prototype.initAllManagers = function() {
	/*
	 * Called whenever the scroll managers present on the current page need to be re-calculated
	 * e.g. when the workspace control is opened/closed, when a popup window is resized, etc
	 * Inits only the scroll managers that manage tabs that appears on the current page
	 */
	console.trace();
	var managers = eXo.portal.UIPortalControl.scrollManagers;
	for (var i = 0; i < managers.length; i++) {
		var toInit = false;
		toInit = (document.getElementById(managers[i].id) !== null);
		toInit &= (typeof(managers[i].initFunction) == "function");
		if (toInit) managers[i].initFunction();
	}
};

UIPortalControl.prototype.newScrollManager = function(id_) {
	if (eXo.portal.UIPortalControl.scrollManagers.length == 0) {
		eXo.core.Browser.addOnResizeCallback("initAllManagers", eXo.portal.UIPortalControl.initAllManagers);
	}
	if (id_) {
		var tmpMgr = new ScrollManager();
		tmpMgr.id = id_;
		eXo.portal.UIPortalControl.scrollManagers.push(tmpMgr);
		return tmpMgr;
	} else {
		alert('id needed !!');
		return null;
	}
};
/*********** Scroll Manager *************/

eXo.portal.UIPortalControl = new UIPortalControl();