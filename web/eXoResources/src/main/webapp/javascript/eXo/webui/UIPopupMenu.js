eXo.require('eXo.webui.UIPopup');

function UIPopupMenu() {
	// Used when timeout is finished and submenus must be hidden
	this.elementsToHide = new Array();
	this.currentVisibleContainers = new Array();
} ;

UIPopupMenu.prototype.init = function(popupMenu, container, x, y) {
	this.superClass = eXo.webui.UIPopup;
	this.superClass.init(popupMenu, container.id) ;
} ;

UIPopupMenu.prototype.setPosition = function(popupMenu, x, y) {
	this.superClass.setPosition(popupMenu, x, y) ;
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

UIPopupMenu.prototype.setCloseTimeout = function(time) {
	if (!time) time = 100;
	setTimeout("eXo.webui.UIPopupMenu.doOnMenuItemOut()", time) ;
};

/* The callback function called when timeout is finished
 * Hides the submenus that are no longer pointed
 */
UIPopupMenu.prototype.doOnMenuItemOut = function() {
	while (eXo.webui.UIPopupMenu.elementsToHide.length > 0) {
		var container = document.getElementById(eXo.webui.UIPopupMenu.elementsToHide.shift());
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
	object.style.visibility = "visible";
} ;

eXo.webui.UIPopupMenu = new UIPopupMenu() ;