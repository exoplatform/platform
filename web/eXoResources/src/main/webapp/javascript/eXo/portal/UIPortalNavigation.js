eXo.require('eXo.webui.UIPopupMenu');

function UIPortalNavigation() {
	this.currentOpenedMenu = null;
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
	var topItems = eXo.core.DOMUtil.findDescendantsByClass(topContainer, "div", "UITab");
	for (var i = 0; i<topItems.length; i++) {
		var item = topItems[i];
		item.onmouseover = eXo.portal.UIPortalNavigation.setTabStyle;
		item.onmouseout = eXo.portal.UIPortalNavigation.setTabStyle;
		var arrow = eXo.core.DOMUtil.findFirstDescendantByClass(item, "div", "DropDownArrowIcon");
		if (arrow) {
			arrow.onmouseover = function() { this.style.backgroundColor = "white"; };
			arrow.onmouseout = function() { this.style.backgroundColor = "transparent"; };
			arrow.onclick = eXo.portal.UIPortalNavigation.toggleSubMenu;
			arrow.parentTab = item;
		}
	}
	
	var menuItems = eXo.core.DOMUtil.findDescendantsByClass(topContainer, "div", this.tabStyleClass);
	for(var i = 0; i<menuItems.length; i++) {
		menuItems[i].onmouseover = eXo.portal.UIPortalNavigation.onMenuItemOver;
		menuItems[i].onmouseout = eXo.portal.UIPortalNavigation.onMenuItemOut;
		var link = eXo.core.DOMUtil.findDescendantsByTagName(menuItems[i], "a")[0];
		this.superClass.createLink(menuItems[i], link);
		// Set an id to each container for future reference
		var cont = eXo.core.DOMUtil.findAncestorByClass(menuItems[i], this.containerStyleClass) ;
		if (!cont.id) cont.id = "PortalNavigationContainer-"+i;
	}
};

UIPortalNavigation.prototype.setTabStyle = function() {
	var tab = this;
	var tabChildren = eXo.core.DOMUtil.getChildrenByTagName(tab, "div") ;
	if (tabChildren[0].className != "HighlightNavigationTab") {
		// highlights the tab
		eXo.webui.UIHorizontalTabs.changeTabNavigationStyle(tab, true);
	} else {
		if(tabChildren.length <= 1 || tabChildren[1].id != eXo.portal.UIPortalNavigation.currentOpenedMenu) {
			// de-highlights the tab if its submenu isn't visible
			eXo.webui.UIHorizontalTabs.changeTabNavigationStyle(tab, false);
		}
	}
}

UIPortalNavigation.prototype.toggleSubMenu = function(e) {
	if (!e) var e = window.event;
	e.cancelBubble = true;
	
	var src = eXo.core.Browser.getEventSource(e);
	var item = src.parentTab;
	var menuItemContainer = eXo.core.DOMUtil.findFirstDescendantByClass(item, "div", eXo.portal.UIPortalNavigation.containerStyleClass);
	if (menuItemContainer) {
		if (menuItemContainer.style.display == "none") {
			// shows the sub menu
			// hides a previously opened sub menu
			if (eXo.portal.UIPortalNavigation.currentOpenedMenu) eXo.portal.UIPortalNavigation.hideMenu();
			
			eXo.portal.UIPortalNavigation.superClass.pushVisibleContainer(menuItemContainer.id);
			var x = item.offsetLeft;
			var y = item.offsetHeight + item.offsetTop;
			
			eXo.portal.UIPortalNavigation.superClass.setPosition(menuItemContainer, x, y);
			eXo.portal.UIPortalNavigation.superClass.show(menuItemContainer);
			
			eXo.portal.UIPortalNavigation.currentOpenedMenu = menuItemContainer.id;
			
			/*Hide eXoStartMenu whenever click on the UIApplication*/
			var uiPortalApplication = document.getElementById("UIPortalApplication") ;
			uiPortalApplication.onclick = eXo.portal.UIPortalNavigation.hideMenu ;
		} else {
			// hides the sub menu
			eXo.portal.UIPortalNavigation.hideMenuContainer();
		}
	}
}

UIPortalNavigation.prototype.hideMenuContainer = function() {
	var menuItemContainer = document.getElementById(eXo.portal.UIPortalNavigation.currentOpenedMenu);
	if (menuItemContainer) {
		eXo.portal.UIPortalNavigation.superClass.pushHiddenContainer(menuItemContainer.id);
		eXo.portal.UIPortalNavigation.superClass.popVisibleContainer();
		eXo.portal.UIPortalNavigation.superClass.setCloseTimeout();
		eXo.portal.UIPortalNavigation.superClass.hide(menuItemContainer);
		eXo.portal.UIPortalNavigation.currentOpenedMenu = null;
	}
}

UIPortalNavigation.prototype.hideMenu = function() {
	if (eXo.portal.UIPortalNavigation.currentOpenedMenu) {
		var currentItemContainer = document.getElementById(eXo.portal.UIPortalNavigation.currentOpenedMenu);
		var tab = eXo.core.DOMUtil.findAncestorByClass(currentItemContainer, "UITab");
		eXo.webui.UIHorizontalTabs.changeTabNavigationStyle(tab, false);
	}
	eXo.portal.UIPortalNavigation.hideMenuContainer();
}

UIPortalNavigation.prototype.onMenuItemOver = function(e) {
	var menuItem = this;
	var item = eXo.core.DOMUtil.findFirstDescendantByClass(menuItem, "div", eXo.portal.UIPortalNavigation.itemStyleClass);
	if (!item) item = eXo.core.DOMUtil.findFirstDescendantByClass(menuItem, "div", eXo.portal.UIPortalNavigation.selectedItemStyleClass);
	item.oldClassName = item.className;
	item.className = eXo.portal.UIPortalNavigation.itemOverStyleClass;
	var subContainer = eXo.core.DOMUtil.findFirstDescendantByClass(menuItem, "div", eXo.portal.UIPortalNavigation.containerStyleClass);
	if (subContainer) {
		eXo.portal.UIPortalNavigation.superClass.pushVisibleContainer(subContainer.id);
		eXo.portal.UIPortalNavigation.showMenuItemContainer(menuItem, subContainer) ;
	}
};

UIPortalNavigation.prototype.showMenuItemContainer = function(menuItem, menuItemContainer) {
	var x = menuItem.offsetWidth;
	var y = menuItem.offsetTop;
	this.superClass.setPosition(menuItemContainer, x, y);
	this.superClass.show(menuItemContainer);
};

UIPortalNavigation.prototype.onMenuItemOut = function(e) {
	var menuItem = this;
	var item = eXo.core.DOMUtil.findFirstDescendantByClass(menuItem, "div", eXo.portal.UIPortalNavigation.itemOverStyleClass);
	item.className = item.oldClassName;
	var subContainer = eXo.core.DOMUtil.findFirstDescendantByClass(menuItem, "div", eXo.portal.UIPortalNavigation.containerStyleClass);
	if (subContainer) {
		eXo.portal.UIPortalNavigation.superClass.pushHiddenContainer(subContainer.id);
		eXo.portal.UIPortalNavigation.superClass.popVisibleContainer();
		eXo.portal.UIPortalNavigation.superClass.setCloseTimeout(300);
	}
};

eXo.portal.UIPortalNavigation = new UIPortalNavigation() ;