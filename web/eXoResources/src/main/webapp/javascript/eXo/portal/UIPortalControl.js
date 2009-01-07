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
  expandIcon.innerHTML = selectedElement.innerHTML ;
  parentNode.removeChild(childrenContainer);
  parentNode.insertBefore(expandIcon, selectedElement);
  parentNode.removeChild(selectedElement);
  eXo.portal.UIPortalControl.fixHeight();
};

/** Created: by Duy Tu - fixHeight function to UIControlWorkspace
 **/
UIPortalControl.prototype.fixHeight = function() {
	var objectParent = document.getElementById("UIControlWorkspace");
	if(!objectParent) return;
	clearTimeout(objectParent.recall);
	var DOMUtil = eXo.core.DOMUtil;
	var uiControlWSWorkingArea = document.getElementById("UIControlWSWorkingArea");
	var uiWorkspacePanel = document.getElementById("UIWorkspacePanel");
	var uiWorkspaceContainer = DOMUtil.findFirstDescendantByClass(objectParent, "div", "UIWorkspaceContainer") ;
	if (uiWorkspaceContainer.style.display == "block") {
  	var scrollArea = DOMUtil.findFirstDescendantByClass(objectParent, "div", "ScrollArea");
  	if (scrollArea != null) {
  		var baseHeight = parseInt(uiWorkspacePanel.style.height);
  		scrollArea.style.height = "auto";
  		var scrollAreaHeight = scrollArea.scrollHeight;
  		var contentHeight = 0;
  		if (uiControlWSWorkingArea) 
  			contentHeight = uiControlWSWorkingArea.offsetHeight;
  		var deltaResize = contentHeight - baseHeight;
  		if (deltaResize > 0 && (scrollAreaHeight > deltaResize)) {
  			scrollArea.style.height = scrollAreaHeight - deltaResize + "px";
  			scrollArea.style.overflowY = "auto";
  			scrollArea.style.overflowX = "hidden";
				var children = DOMUtil.getChildrenByTagName(scrollArea, "div");
				if(children.length > 0) {
					children[0].style.borderTop = "1px solid white";
					children[0].style.height = "auto";
					children[0].style.height = children[0].offsetHeight + "px";
				}
  		}
  	}
  }
} ;

UIPortalControl.prototype.onEnterPress = function(e) {
	var e = window.event || e;
	var uiPortalLoginFormAction = document.getElementById("UIPortalLoginFormAction");
	if(uiPortalLoginFormAction) {
		var code;
		if(!e) e = window.event;
		if(e.keyCode) code = e.keyCode;
		else if (e.which) code = e.which;
		if(code ==13) {
			if(this.t != 13) {
		    uiPortalLoginFormAction.onclick() ;
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
	this.elements = new Array(); // the array containing the elements
	this.firstVisibleIndex = 0; // the index in the array of the first visible element
	this.lastVisibleIndex = -1; // the index in the array of the last visible element
	this.otherHiddenElements = new Array(); // an array containing the elements hidden
	this.axis = 0; // horizontal scroll : 0 , vertical scroll : 1
	this.currDirection = null; // the direction of the current scroll; left or up scroll : 0, right or down scroll : 1
	this.callback = null; // callback function when a scroll is done
	this.initFunction = null; // the init function in the files that use this class
	this.leftArrow = null; // the left arrow dom node
	this.rightArrow = null; // the right arrow dom node
	this.mainContainer = null; // The HTML DOM element that contains the tabs, the arrows, etc
	this.arrowsContainer = null // The HTML DOM element that contains the arrows
	this.margin = 6.9;	//	a number of pixels to adapt to your tabs, used to calculate the max space available
};
/**
 * Initializes the arrows with :
 *  . mouse listeners
 *  . css class and other parameters
 */
ScrollManager.prototype.initArrowButton = function(arrow, dir, normalClass, overClass, disabledClass) {
	if (arrow) {
		arrow.direction = dir; // "left" or "right" (up or down)
		arrow.overClass = overClass; // the css class for mouse over event
		arrow.disabledClass = disabledClass; // the css class for a disabled arrow
		arrow.styleClass = normalClass; // the css class for an enabled arrow, in the normal state
		arrow.scrollMgr = this; // an easy access to the scroll manager
		arrow.onmouseover = this.mouseOverArrow;
		arrow.onmouseout = this.mouseOutArrow;
//		arrow.arrowClick = this.scroll;
//		arrow.onclick = arrow.arrowClick;
		arrow.onclick = this.scroll;
		if (dir == "left") this.leftArrow = arrow;
		else if (dir == "right") this.rightArrow = arrow;
	}
};
/**
 * Disables or enables the arrow
 */
ScrollManager.prototype.enableArrow = function(arrow, enabled) {
	if (arrow && !enabled) { // disables the arrow
		arrow.className = arrow.disabledClass;
		arrow.onclick = null;
	} else if (arrow && enabled) { // enables the arrow
		arrow.className = arrow.styleClass;
//		arrow.onclick = arrow.arrowClick;
		arrow.onclick = this.scroll;
	}
};
/**
 * Sets the mouse over css style of the arrow (this)
 * only if it is enabled
 */
ScrollManager.prototype.mouseOverArrow = function(e) {
	var arrow = this;
	if (arrow.onclick && arrow.className == arrow.styleClass) {
		// mouse over
		if (!e) e = window.event;
		if (arrow == eXo.core.Browser.getEventSource(e)) arrow.className = arrow.overClass;
	}
};
/**
 * Sets the mouse out css style of the arrow (this)
 * only if it is enabled
 */
ScrollManager.prototype.mouseOutArrow = function(e) {
	var arrow = this;
	if (arrow.onclick && arrow.className == arrow.overClass) {
		// mouse out
		arrow.className = arrow.styleClass;
	}
};
/**
 * Initializes the scroll manager, with some default parameters
 */
ScrollManager.prototype.init = function() {
	this.firstVisibleIndex = 0;
	this.lastVisibleIndex = -1;
	// Hides the arrows by default
	if(this.arrowsContainer)  {
		this.arrowsContainer.style.display = "none";
		this.arrowsContainer.space = null;
		this.mainContainer.space = null;
	}
	//fix bug display navigation PORTAL-1243
	var uiNavigationPortlet = eXo.core.DOMUtil.findAncestorByClass(this.mainContainer, "UINavigationPortlet");
	if (uiNavigationPortlet) {
  	var uiApplication = uiNavigationPortlet.parentNode;
 		uiApplication.style.display = "inline";
  }
};
/**
 * Loads the tabs in the scroll manager, depending on their css class
 * If clean is true, calls cleanElements to remove the space property of each element
 */
ScrollManager.prototype.loadElements = function(elementClass, clean) {
	if (clean) this.cleanElements();
	this.elements.clear();
	this.elements.pushAll(eXo.core.DOMUtil.findDescendantsByClass(this.mainContainer, "div", elementClass));
};
/**
 * Calculates the available space for the elements, and inits the elements array like this :
 *  . maxSpace = space of mainContainer - space of arrowsContainer - a margin
 *  . browses the elements and add their space to elementsSpace, for each element compares elementsSpace with maxSpace
 *  . if elementsSpace le maxSpace : the current element is set visible, and its index becomes the lastVisibleIndex
 *  . if elementsSpace gt maxSpace : the current element is set hidden (isVisible = false)
 * At the end, each visible element has an isVisible property set to true, the other elements are set to false,
 * the firstVisibleIndex is 0, the lastVisibleIndex is the last element with isVisible to true
 */
ScrollManager.prototype.checkAvailableSpace = function(maxSpace) { // in pixels
	if (!maxSpace) maxSpace = this.getElementSpace(this.mainContainer) - this.getElementSpace(this.arrowsContainer);
	var elementsSpace = 0;
	var margin = 0;
	var length =  this.elements.length;
	for (var i = 0; i < length; i++) {
		elementsSpace += this.getElementSpace(this.elements[i]);
		//dynamic margin;
		if (i+1 < length) margin = this.getElementSpace(this.elements[i+1]) / 3;
		else margin = this.margin;
		if (elementsSpace + margin < maxSpace) { // If the tab fits in the available space
			this.elements[i].isVisible = true;
			this.lastVisibleIndex = i;
		} else { // If the available space is full
			this.elements[i].isVisible = false;
		}
	}
};
/**
 * Calculates the space of the elements between indexStart and indexEnd
 * If these parameters are null, calculates the space for all the elements of the array
 * Uses the getElementSpace function
 */
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
/**
 * Calculates the space of the element passed in parameter
 * The calcul uses : (horizontal tabs | vertical tabs)
 *  . offsetWidth | offsetHeight
 *  . marginLeft and marginRight | marginTop and marginBottom
 *  . the space of the decorator associated with this element, if any
 * If the element is not rendered (display none), renders it, makes the calcul, and hides it again
 * The value of the space is stored in a property space of the element. In the function is called on
 * the same element again, this value is returned directly to avoid another calcul
 * To remove this value, use the cleanElements function, or set space to null manually
 */
ScrollManager.prototype.getElementSpace = function(element) {
	if (element && element.space) { return element.space; }
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
		// Store the calculated value for faster return on next calls. To recalculate, set element.space to null.
		element.space = elementSpace;
	}
	return elementSpace;
};
/**
 * Clean the elements of the array : set the space property to null
 */
ScrollManager.prototype.cleanElements = function() {
	for (var i = 0; i < this.elements.length; i++) {
		this.elements[i].space = null;
		if (this.elements[i].decorator) this.elements[i].decorator.space = null;
	}
};
/**
 * Function called when an arrow is clicked. Shows an additionnal element and calls the 
 * appropriate scroll function (left or right). Works like this :
 *  . shows the otherHiddenElements again
 *  . moves the firstVisibleIndex or lastVisibleIndex to the new index
 *  . clear the otherHiddenElements array
 *  . calls the appropriate scroll function (left or right)
 */
ScrollManager.prototype.scroll = function(e) {
	if (!e) e = window.event;
	e.cancelBubble = true;
	var src = eXo.core.Browser.getEventSource(e);
	if (src.scrollMgr && src.onclick) {
		if (src.scrollMgr.otherHiddenElements.length > 0) {
			for (var i = 0; i < src.scrollMgr.otherHiddenElements.length; i++) {
				src.scrollMgr.otherHiddenElements[i].isVisible = true;
				src.scrollMgr.otherHiddenElements[i].style.display = "block";
				if (src.scrollMgr.currDirection == 1) src.scrollMgr.firstVisibleIndex--;
				else if (src.scrollMgr.currDirection == 0) src.scrollMgr.lastVisibleIndex++;
			}
			src.scrollMgr.otherHiddenElements.clear();
		}
		if (src.direction == "left") src.scrollMgr.scrollLeft();
		else if (src.direction == "right") src.scrollMgr.scrollRight();
	}
	return false;
};
/**
 * Scrolls left (or up) :
 *  . sets the current last visible element hidden
 *  . decrements lastVisibleIndex
 *  . decrements firstVisibleIndex
 *  . set the new first visible element to visible
 * Simulates a move to the left of the tabs
 */
ScrollManager.prototype.scrollLeft = function() { // Same for scrollUp
	if (this.firstVisibleIndex > 0) {
		this.currDirection = 0;
		// hides the last (right or down) element and moves lastVisibleIndex to the left
		this.elements[this.lastVisibleIndex--].isVisible = false;
		// moves firstVisibleIndex to the left and shows the first (left or up) element
		this.getVisibleElements();
		//this.elements[--this.firstVisibleIndex].isVisible = true;
		this.renderElements();
	}
};

ScrollManager.prototype.scrollUp = function() {
	if (this.scrollMgr) this.scrollMgr.scrollLeft();
};
/**
 * Scrolls right (or down) :
 *  . sets the current first visible element hidden
 *  . increments firstVisibleIndex
 *  . increments lastVisibleIndex
 *  . set the new last visible element to visible
 * Simulates a move to the right of the tabs
 */
ScrollManager.prototype.scrollRight = function() { // Same for scrollDown
	if (this.lastVisibleIndex < this.elements.length-1) {
		this.currDirection = 1;
		// hides the first (left or up) element and moves firstVisibleIndex to the right
		this.elements[this.firstVisibleIndex++].isVisible = false;
		// moves lastVisibleIndex to the right and shows the last (right or down) element
		this.getVisibleElements();
		//this.elements[++this.lastVisibleIndex].isVisible = true;
		this.renderElements();
	}
};

ScrollManager.prototype.scrollDown = function() {
	if (this.scrollMgr) this.scrollMgr.scrollRight();
};


ScrollManager.prototype.getVisibleElements = function() {
	var availableSpace = this.getElementSpace(this.mainContainer) - this.getElementSpace(this.arrowsContainer);
	var refereceIndex = 0;
	var margin = 0;
	var elementsSpace = 0;
	
	if (this.currDirection) {
		var length = this.elements.length;
		for (var i = this.firstVisibleIndex; i < length ; i++) {
			elementsSpace += this.getElementSpace(this.elements[i]);
			//dynamic margin;
			if (i+1 < length) margin = this.getElementSpace(this.elements[i+1]) / 3;
			else margin = this.margin;
			if (elementsSpace + margin < availableSpace) {
				this.elements[i].isVisible = true;
				refereceIndex = i;
			} else this.elements[i].isVisible = false;
		}
		if (this.lastVisibleIndex == refereceIndex) this.scrollRight();
		else this.lastVisibleIndex = refereceIndex;
	} else {
		for (var i = this.lastVisibleIndex; i >= 0 ; i--) {
			elementsSpace += this.getElementSpace(this.elements[i]);
			//dynamic margin;
			margin = this.getElementSpace(this.elements[this.lastVisibleIndex]) / 3;
			if (elementsSpace + margin < availableSpace) {
				this.elements[i].isVisible = true;
				refereceIndex = i;
			} else this.elements[i].isVisible = false;
		}
		if (this.firstVisibleIndex == refereceIndex) this.scrollLeft();
		else this.firstVisibleIndex = refereceIndex;
	}
};
/**
 * Called by a scroll function. Renders the visible elements depending on the elements array
 * If the new visible element is too big, hides additional element(s) and keep its(their) index(es) in otherHiddenElements
 *
 * Each time a scroll event occurs, at least one element is hidden, and one is shown. These elements can have
 * a different width, hence the total width of the tabs changes. This is why we have to check if the
 * new width is short enough so the arrows buttons are still well rendered. To do that, we remove each element
 * width to the total width (delta). If the delta is negative, we have to hide the following visible tabs (hideElements).
 * PS: for vertical tabs, replace width by height above.
 */
ScrollManager.prototype.renderElements = function() {
//	var delta = this.getElementSpace(this.mainContainer)-this.getElementSpace(this.arrowsContainer)-this.margin;
	// Displays the elements
	for (var i = 0; i < this.elements.length; i++) {
		if (this.elements[i].isVisible) { // if the element should be rendered...
			this.elements[i].style.display = "block";
			//delta -= this.getElementSpace(this.elements[i]);
		} else { // if the element must not be rendered...
			this.elements[i].style.display = "none";
			this.arrowsContainer.style.display = "block";
		}
	}
//	if (delta < 0) { // if there are too many elements visible in the available space
//		this.hideElements(delta);
//	}
	if (this.arrowsContainer.style.display == "block") {
		this.renderArrows();
	}
	
	if (typeof(this.callback) == "function") this.callback();
};
/**
 * Called if the delta is negative, during a scroll event
 * Depending on the current scroll direction, hides the opposite visible elements so the other tabs
 * and the arrows are well rendered.
 * e.g. a scroll right event occurs :
 *  . a tab on the left is hidden, a tab on the right is show
 *  . the new tab is too large and doesn't fit in the available space (delta is negative)
 *  . elements (one or more) on the left are hidden until the new tab has enough space to be well rendered
 *  . these elements are stored in otherHiddenElements array to be shown again on the next scroll event
 */
//ScrollManager.prototype.hideElements = function(delta) {
//	// by default, we scroll left/up
//	var incr = -1;
//	var index = this.lastVisibleIndex;
//	if (this.currDirection == 1) { // if we scroll right/down
//		 incr = 1;
//		 index = this.firstVisibleIndex;
//	}
//	while (delta < 0 && index >= 0 && index < this.elements.length) {
//		delta += this.getElementSpace(this.elements[index]);
//		this.elements[index].isVisible = false;
//		this.elements[index].style.display = "none";
//		this.otherHiddenElements.push(this.elements[index]);
//		if (this.currDirection == 1) this.firstVisibleIndex++;
//		else this.lastVisibleIndex--;
//		index += incr;
//	}
//};
/**
 * Renders the arrows. If we reach the end of the tabs, this end arrow is disabled
 */
ScrollManager.prototype.renderArrows = function() {
	// Enables/Disables the arrow buttons depending on the elements to show
	if (this.firstVisibleIndex == 0) this.enableArrow(this.leftArrow, false);
	else this.enableArrow(this.leftArrow, true);
	
	if (this.lastVisibleIndex == this.elements.length-1) this.enableArrow(this.rightArrow, false);
	else this.enableArrow(this.rightArrow, true);
};
/**
 * Called whenever the scroll managers present on the current page need to be re-calculated
 * e.g. when the workspace control is opened/closed, when a popup window is resized, etc
 * Inits only the scroll managers that manage tabs that appears on the current page
 */
UIPortalControl.prototype.initAllManagers = function() {
	var managers = eXo.portal.UIPortalControl.scrollManagers;
	for (var i = 0; i < managers.length; i++) {
		var mgrContainer = document.getElementById(managers[i].id);
		var mgrParent = eXo.core.DOMUtil.findAncestorByClass(mgrContainer, "UIWindow");
		var toInit = (mgrContainer !== null) 														// if the tabs exist on the page
							// in desktop mode, checks that the UIWindow containing the tabs is visible (display block)
							&& (mgrParent === null || (mgrParent !== null && mgrParent.style.display == "block"))
							&& (typeof(managers[i].initFunction) == "function");  // if the initFunction is defined
		if (toInit) { managers[i].initFunction(); }
	}
};
/**
 * Creates a new scroll manager with the given id (id is mandatory)
 * At the creation of the first scroll manager, sets the initAllManagers to be called onResize
 * The id must be the one of the portlet, because it is used by initAllManagers to find the scroll
 * managers that exists on the page
 */
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
		alert('You must set an id to the new scroll manager !!');
		return null;
	}
};
/*********** Scroll Manager *************/

/*********** Vertical Scroll Manager ************/
function VerticalScrollManager() {
	repeat = null;
}

VerticalScrollManager.prototype.initScroll = function (clickedEle, isUp, step) {
	var DOMUtil = eXo.core.DOMUtil;
	var verticalScroll = eXo.portal.VerticalScrollManager;
	var container = DOMUtil.findAncestorByClass(clickedEle, "ItemContainer");
	var middleCont = DOMUtil.findFirstDescendantByClass(container, "div", "MiddleItemContainer");
	if(!middleCont.id) middleCont.id = "IC" + new Date().getTime() + Math.random().toString().substring(2);
	verticalScroll.scrollComponent(middleCont.id, isUp, step);
};
	
VerticalScrollManager.prototype.scrollComponent = function (id, isUp, step) {
	var verticalScroll = eXo.portal.VerticalScrollManager;
	var scrollComp = document.getElementById(id);
	if(isUp) {
		scrollComp.scrollTop -= step;
	} else {
		scrollComp.scrollTop += step;
	}
	if(verticalScroll.repeat) {
		verticalScroll.cancelScroll();
	}
	document.onmouseup = verticalScroll.cancelScroll;
	verticalScroll.repeat = setTimeout("eXo.portal.VerticalScrollManager.scrollComponent('" + id + "'," + isUp + "," + step + ")", 100);
};

VerticalScrollManager.prototype.cancelScroll = function () {
	clearTimeout(eXo.portal.VerticalScrollManager.repeat);
	eXo.portal.VerticalScrollManager.repeat = null;
};

eXo.portal.VerticalScrollManager = new VerticalScrollManager();

/*********** End Of Vertical Scroll Manager ************/

eXo.portal.UIPortalControl = new UIPortalControl();