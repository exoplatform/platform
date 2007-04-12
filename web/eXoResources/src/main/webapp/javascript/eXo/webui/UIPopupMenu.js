eXo.require('eXo.webui.UIPopup');

function UIPopupMenu() {
	// Used when timeout is finished and submenus must be hidden
	this.elementsToHide = new Array();
	this.currentVisibleContainers = new Array();
	this.itemStyleClass = "MenuItem";
	this.itemOverStyleClass = "MenuItemOver";
	this.containerStyleClass = "MenuItemContainer";
} ;

UIPopupMenu.prototype.init = function(popupMenu, container, x, y) {
	this.superClass = eXo.webui.UIPopup;
	this.superClass.init(popupMenu, container.id) ;
	//this.superClass.setPosition(popupMenu, x, y) ;
} ;

UIPopupMenu.prototype.buildMenu = function(popupMenu, mouseOverListener, mouseOutListener) {
	var menuItems = eXo.core.DOMUtil.findDescendantsByClass(popupMenu, "div", this.itemStyleClass) ;
	for(var i = 0; i<menuItems.length; i++) {
		menuItems[i].onmouseover = mouseOverListener ;
		menuItems[i].onmouseout = mouseOutListener ;
		// Set an id to each container for future reference
		var cont = eXo.core.DOMUtil.findAncestorByClass(menuItems[i], this.containerStyleClass) ;
		if (!cont.id) cont.id = "cont-"+i;
		cont.resized = false;
	}
};

UIPopupMenu.prototype.setPosition = function(popupMenu, x, y) {
	this.superClass.setPosition(popupMenu, x, y) ;
};

UIPopupMenu.prototype.onMenuItemOver = function(e) {
	var menuItem = eXo.core.Browser.getEventSource(e);
	if (menuItem.className != this.itemStyleClass) menuItem = eXo.core.DOMUtil.findAncestorByClass(menuItem, this.itemStyleClass);
	var menuItemContainer = eXo.core.DOMUtil.findFirstDescendantByClass(menuItem, "div", this.containerStyleClass) ;
	// If the pointed menu item has a submenu
	if(menuItemContainer) {
		// Shows the submenu
		eXo.webui.UIPopupMenu.showMenuItemContainer(menuItem, menuItemContainer) ;
		/* Adds the submenu id to the list of visible submenus ("keep-visible" list)
		 * I use an array for "sub submenus" (not only one submenu to show)
		 */
		eXo.webui.UIPopupMenu.currentVisibleContainers.push(menuItemContainer.id);
	}
} ;

UIPopupMenu.prototype.onMenuItemOut = function(e) {
	var menuItem = eXo.core.Browser.getEventSource(e);
	if (menuItem.className != this.itemOverStyleClass) menuItem = eXo.core.DOMUtil.findAncestorByClass(menuItem, this.itemOverStyleClass);

	menuItem.className = this.itemStyleClass ;
	// If the menu we just left has (had) a submenu
	var container = eXo.core.DOMUtil.findFirstDescendantByClass(menuItem, "div", this.containerStyleClass) ;
	if(container) {
		eXo.webui.UIPopupMenu.menuItemcontainer = container ;
		/* Adds the submenu to the list of submenus to hide ("to-hide" list)
		 * This list will be parsed when timeout is finished
		 */
		eXo.webui.UIPopupMenu.elementsToHide.push(container.id);
		/* Removes the last submenu that was visible from the visible submenus list
		 * The last submenu that was visible is the one we just left
		 */
		eXo.webui.UIPopupMenu.currentVisibleContainers.pop();
		// Sets the timeout and the callback function
		setTimeout("eXo.webui.UIPopupMenu.doOnMenuItemOut()", 100) ;
	}
} ;

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
			container.style.display = "none" ;
		}
	}
} ;

UIPopupMenu.prototype.showMenuItemContainer = function(menuItem, menuItemContainer) {
	menuItemContainer.style.display = "block" ;
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
	
	this.superClass.setPosition(menuItemContainer, x, y) ;
} ;

UIPopupMenu.prototype.hide = function(object) {
	if(typeof(object) == "string") object = document.getElementById(object);
	object.style.display = "none" ;
} ;

UIPopupMenu.prototype.show = function(object) {
	if(typeof(object) == "string") object = document.getElementById(object);
	object.style.display = "block" ;
} ;

eXo.webui.UIPopupMenu = new UIPopupMenu() ;