eXo.require('eXo.webui.UIPopupMenu');

function UIPortalNavigation() {
};

UIPortalNavigation.prototype.init = function(popupMenu, container, x, y) {
	var uiNav = eXo.portal.UIPortalNavigation;
	this.superClass = eXo.webui.UIPopupMenu;
	this.superClass.init(popupMenu, container, x, y) ;
	
	this.tabStyleClass = "MenuItem";
	this.itemStyleClass = "NormalItem";
	this.selectedItemStyleClass = "SelectedItem";
	this.itemOverStyleClass = "OverItem";
	this.containerStyleClass = "MenuItemContainer";
	
	this.buildMenu(popupMenu);
};

UIPortalNavigation.prototype.onLoad = function() {
	var uiNavPortlet = document.getElementById("UINavigationPortlet");
	var mainContainer = eXo.core.DOMUtil.findFirstDescendantByClass(uiNavPortlet, "div", "TabsContainer");
	eXo.portal.UIPortalNavigation.init(uiNavPortlet, mainContainer, 0, 0);
};

UIPortalNavigation.prototype.buildMenu = function(popupMenu) {
	var topContainer = eXo.core.DOMUtil.findFirstDescendantByClass(popupMenu, "div", "TabsContainer");
	topContainer.id = "PortalNavigationTopContainer";
	var menuItems = eXo.core.DOMUtil.findDescendantsByClass(topContainer, "div", this.tabStyleClass);
	for(var i = 0; i<menuItems.length; i++) {
		menuItems[i].onmouseover =  eXo.portal.UIPortalNavigation.onMenuItemOver;
		menuItems[i].onmouseout =  eXo.portal.UIPortalNavigation.onMenuItemOut;
		menuItems[i].id = "MenuItem-"+i;
		var link = eXo.core.DOMUtil.findDescendantsByTagName(menuItems[i], "a")[0];
		this.superClass.createLink(menuItems[i], link);
		// Set an id to each container for future reference
		var cont = eXo.core.DOMUtil.findAncestorByClass(menuItems[i], this.containerStyleClass) ;
		if (!cont.id) cont.id = "PortalNavigationContainer-"+i;
	}
};

UIPortalNavigation.prototype.onTopItemOver = function(item) {
	eXo.webui.UIHorizontalTabs.changeTabNavigationStyle(item, true);
	var menuItemContainer = eXo.core.DOMUtil.findFirstDescendantByClass(item, "div", eXo.portal.UIPortalNavigation.containerStyleClass);
	if (menuItemContainer) {
		eXo.portal.UIPortalNavigation.superClass.pushVisibleContainer(menuItemContainer.id);
		var x = item.offsetLeft;
		var y = item.offsetHeight + item.offsetTop;
		if (eXo.core.Browser.getBrowserType() == "ie") {
			// only for IE
			x = eXo.core.Browser.findPosX(item);
			y = /*eXo.core.Browser.findPosY(item) +*/ item.offsetHeight;
			if (eXo.portal.UIControlWorkspace.showControlWorkspace) {
				x -= eXo.portal.UIControlWorkspace.defaultWidth;
				if (!eXo.core.Browser.isIE6()) {
				  // only on IE7, mozilla, safari, opera 9
				  // we know that the browser is IE, hence, this is IE7
				  x -= eXo.portal.UIControlWorkspace.defaultWidth;
				}
			}
		}
		eXo.portal.UIPortalNavigation.superClass.setPosition(menuItemContainer, x, y);
		eXo.portal.UIPortalNavigation.superClass.show(menuItemContainer);
	}
};

UIPortalNavigation.prototype.onTopItemOut = function(item) {
	eXo.webui.UIHorizontalTabs.changeTabNavigationStyle(item, false);
	var menuItemContainer = eXo.core.DOMUtil.findFirstDescendantByClass(item, "div", eXo.portal.UIPortalNavigation.containerStyleClass);
	if (menuItemContainer) {
		eXo.portal.UIPortalNavigation.superClass.pushHiddenContainer(menuItemContainer.id);
		eXo.portal.UIPortalNavigation.superClass.popVisibleContainer();
		eXo.portal.UIPortalNavigation.superClass.setCloseTimeout();
		eXo.portal.UIPortalNavigation.superClass.hide(menuItemContainer);
	}
};

UIPortalNavigation.prototype.onMenuItemOver = function(e) {
	var menuItem = this;
	var item = eXo.core.DOMUtil.findFirstDescendantByClass(menuItem, "div", eXo.portal.UIPortalNavigation.itemStyleClass);
	if (!item) item = eXo.core.DOMUtil.findFirstDescendantByClass(menuItem, "div", eXo.portal.UIPortalNavigation.selectedItemStyleClass);
	menuItem.className = eXo.portal.UIPortalNavigation.itemOverStyleClass;
	var subContainer = eXo.core.DOMUtil.findFirstDescendantByClass(menuItem, "div", eXo.portal.UIPortalNavigation.containerStyleClass);
	if (subContainer) {
		eXo.portal.UIPortalNavigation.superClass.pushVisibleContainer(subContainer.id);
		eXo.portal.UIPortalNavigation.showMenuItemContainer(menuItem, subContainer) ;
	}
};

UIPortalNavigation.prototype.showMenuItemContainer = function(menuItem, menuItemContainer) {
	this.superClass.show(menuItem.parentNode);
	var x = menuItem.offsetLeft + menuItem.offsetWidth;
	var y = menuItem.offsetTop;
	
	this.superClass.setPosition(menuItemContainer, x, y);
	this.superClass.show(menuItemContainer);
};

UIPortalNavigation.prototype.onMenuItemOut = function(e) {
	var menuItem = this;
	var item = eXo.core.DOMUtil.findFirstDescendantByClass(menuItem, "div", eXo.portal.UIPortalNavigation.itemOverStyleClass);
	menuItem.className = eXo.portal.UIPortalNavigation.itemStyleClass;
	var subContainer = eXo.core.DOMUtil.findFirstDescendantByClass(menuItem, "div", eXo.portal.UIPortalNavigation.containerStyleClass);
	if (subContainer) {
		eXo.portal.UIPortalNavigation.superClass.pushHiddenContainer(subContainer.id);
		eXo.portal.UIPortalNavigation.superClass.popVisibleContainer();
		eXo.portal.UIPortalNavigation.superClass.setCloseTimeout(300);
	}
};

eXo.portal.UIPortalNavigation = new UIPortalNavigation() ;