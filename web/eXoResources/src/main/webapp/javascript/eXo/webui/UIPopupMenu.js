/**
 * Manages a popup menu
 */
function UIPopupMenu() {
	// Elements that must be hidden
	this.elementsToHide = new Array();
	// Elements that must be kept visible
	this.currentVisibleContainers = new Array();
	this.currentElement = null;
} ;

UIPopupMenu.prototype.init = function(popupMenu, container, x, y) {
	this.superClass = eXo.webui.UIPopup;
	this.superClass.init(popupMenu, container.id) ;
} ;

UIPopupMenu.prototype.setPosition = function(popupMenu, x, y, isRTL) {
	this.superClass.setPosition(popupMenu, x, y, isRTL) ;
};

UIPopupMenu.prototype.setSize = function(popup, w, h) {
	this.superClass.setSize(popupMenu, w, h) ;
} ;

UIPopupMenu.prototype.pushVisibleContainer = function(containerId) {
	eXo.webui.UIPopupMenu.currentVisibleContainers.push(containerId);
};

UIPopupMenu.prototype.popVisibleContainer = function() {
	eXo.webui.UIPopupMenu.currentVisibleContainers.pop();
};

UIPopupMenu.prototype.pushHiddenContainer = function(containerId) {
	eXo.webui.UIPopupMenu.elementsToHide.push(containerId);
};
/**
 * Function called when an element (or more) must be hidden
 * Sets a timeout to time (or 100ms by default) after which
 * the elements in elementsToHide will be hidden
 */
UIPopupMenu.prototype.setCloseTimeout = function(time) {
	if (!time) time = 100;
	setTimeout("eXo.webui.UIPopupMenu.doOnMenuItemOut()", time) ;
};
/**
 * Adds an onCLick event to link elements
 * If they are http links, changes the url in the browser
 * If they are javascript links, executes the javascript
 */
UIPopupMenu.prototype.createLink = function(menuItem, link) {
	if (link && link.href) {
		menuItem.onclick = function(e) {
			if (link.href.substr(0, 7) == "http://") window.location.href = link.href;
			else eval(link.href);
			if (!e) e = window.event;
			if (e.stopPropagation) e.stopPropagation();
			e.cancelBubble = true;
			return false;
		}
	}
};

/**
 * The callback function called when timeout is finished
 * Hides the submenus that are no longer pointed at
 */
UIPopupMenu.prototype.doOnMenuItemOut = function() {
	while (eXo.webui.UIPopupMenu.elementsToHide.length > 0) {
		var container = document.getElementById(eXo.webui.UIPopupMenu.elementsToHide.shift());
		if (container) {
			/* It can happen that a submenu appears in both the "to-hide" list and the "keep-visible" list
			 * This happens because when the mouse moves from the border of an item to the content of this item,
			 * a mouseOut Event is fired and the item submenu is added to the "to-hide" list while it remains in the 
			 * "keep-visible" list.
			 * Here, we check that the item submenu doesn't appear in the "keep-visible" list before we hide it
			 */
			if (!eXo.webui.UIPopupMenu.currentVisibleContainers.contains(container.id)) {
				eXo.webui.UIPopupMenu.hide(container);
			}
		}
	}
} ;

UIPopupMenu.prototype.showMenuItemContainer = function(menuItemContainer, x, y) {
	/*menuItemContainer.style.display = "block" ;
	var x = menuItem.offsetWidth + menuItem.offsetLeft;
	var y = menuItem.offsetTop;
	var rootX = eXo.core.Browser.findPosX(menuItem);
	var rootY = eXo.core.Browser.findPosY(menuItem);
	if (x + menuItemContainer.offsetWidth + rootX > eXo.core.Browser.getBrowserWidth()) {
		x -= (menuItemContainer.offsetWidth + menuItem.offsetWidth);
	}
	if (y + menuItemContainer.offsetHeight + rootY > eXo.core.Browser.getBrowserHeight()) {
		y -= (menuItemContainer.offsetHeight - menuItem.offsetHeight);
	}
	*/
	this.superClass.setPosition(menuItemContainer, x, y) ;
} ;

UIPopupMenu.prototype.hide = function(object) {
	if(typeof(object) == "string") object = document.getElementById(object);
	object.style.display = "none" ;
	object.style.visibility = "hidden";
} ;

UIPopupMenu.prototype.show = function(object) {
	if(typeof(object) == "string") object = document.getElementById(object);
	object.style.display = "block" ;
	object.style.visibility = "";
} ;

eXo.webui.UIPopupMenu = new UIPopupMenu() ;