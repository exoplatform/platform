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
	// Top menu items
	var topItems = eXo.core.DOMUtil.findDescendantsByClass(topContainer, "div", "UITab");
	for (var i = 0; i<topItems.length; i++) {
		var item = topItems[i];
		item.onmouseover = eXo.portal.UIPortalNavigation.setTabStyle;
		item.onmouseout = eXo.portal.UIPortalNavigation.setTabStyle;
		item.style.width = item.offsetWidth + "px";
		var arrow = eXo.core.DOMUtil.findFirstDescendantByClass(item, "div", "DropDownArrowIcon");
		if (arrow) {
			item.onclick = eXo.portal.UIPortalNavigation.toggleSubMenu;
		}
		var container = eXo.core.DOMUtil.findFirstDescendantByClass(item, "div", this.containerStyleClass);
		if (container) {
			if (eXo.core.Browser.isIE6()) container.style.width = "100%";
			else container.style.minWidth = item.offsetWidth + "px";
		}
	}
	// Sub menus items
	var menuItems = eXo.core.DOMUtil.findDescendantsByClass(topContainer, "div", this.tabStyleClass);
	for(var i = 0; i<menuItems.length; i++) {
		var menuItem = menuItems[i];
		menuItem.onmouseover = eXo.portal.UIPortalNavigation.onMenuItemOver;
		menuItem.onmouseout = eXo.portal.UIPortalNavigation.onMenuItemOut;
		var link = eXo.core.DOMUtil.findDescendantsByTagName(menuItem, "a")[0];
		this.superClass.createLink(menuItem, link);
		// Set an id to each container for future reference
		var cont = eXo.core.DOMUtil.findAncestorByClass(menuItem, this.containerStyleClass) ;
		if (!cont.id) cont.id = "PortalNavigationContainer-"+i;
		cont.resized = false;
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
			// de-highlights the tab if it doesn't have a submenu (cond 1) or its submenu isn't visible (cond 2)
			eXo.webui.UIHorizontalTabs.changeTabNavigationStyle(tab, false);
		}
	}
}

UIPortalNavigation.prototype.toggleSubMenu = function(e) {
	if (!e) var e = window.event;
	e.cancelBubble = true;
	//var src = this;
	var src = eXo.core.Browser.getEventSource(e);
	if (src.tagName.toLowerCase() == "a") {
		if (src.href.substr(0, 7) == "http://") window.location.href = src.href;
		else eval(src.href);
		return false;
	}
	var item = this;
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
			
			if (!menuItemContainer.resized && eXo.core.Browser.getBrowserType() == "ie") {
				var w = menuItemContainer.offsetWidth;
				var menuItems = eXo.core.DOMUtil.findDescendantsByClass(menuItemContainer, "div", eXo.portal.UIPortalNavigation.tabStyleClass);
				for (var i = 0; i < menuItems.length; i++) {
					menuItems[i].style.width = w + "px";
				}
				menuItemContainer.resized = true;
			}
			eXo.portal.UIPortalNavigation.currentOpenedMenu = menuItemContainer.id;
			
			/*Hide eXoStartMenu whenever click on the UIApplication*/
			var uiPortalApplication = document.getElementById("UIPortalApplication") ;
			uiPortalApplication.onclick = eXo.portal.UIPortalNavigation.hideMenu ;
		} else {
			// hides the sub menu
			eXo.portal.UIPortalNavigation.hideMenuContainer();
		}
	}
};

UIPortalNavigation.prototype.hideMenuContainer = function() {
	var menuItemContainer = document.getElementById(eXo.portal.UIPortalNavigation.currentOpenedMenu);
	if (menuItemContainer) {
		eXo.portal.UIPortalNavigation.superClass.pushHiddenContainer(menuItemContainer.id);
		eXo.portal.UIPortalNavigation.superClass.popVisibleContainer();
		eXo.portal.UIPortalNavigation.superClass.setCloseTimeout();
		eXo.portal.UIPortalNavigation.superClass.hide(menuItemContainer);
		eXo.portal.UIPortalNavigation.currentOpenedMenu = null;
	}
};

UIPortalNavigation.prototype.hideMenu = function() {
	if (eXo.portal.UIPortalNavigation.currentOpenedMenu) {
		var currentItemContainer = document.getElementById(eXo.portal.UIPortalNavigation.currentOpenedMenu);
		var tab = eXo.core.DOMUtil.findAncestorByClass(currentItemContainer, "UITab");
		eXo.webui.UIHorizontalTabs.changeTabNavigationStyle(tab, false);
	}
	eXo.portal.UIPortalNavigation.hideMenuContainer();
};

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

/***** Scroll Management *****/
UIPortalNavigation.prototype.loadScroll = function(e) {
	var uiNav = eXo.portal.UIPortalNavigation;
	uiNav.scrollMgr = eXo.portal.UIPortalControl.newScrollManager();
	uiNav.scrollMgr.initFunction = uiNav.initScroll;
	var portalNav = document.getElementById("PortalNavigationTopContainer");
	uiNav.scrollMgr.elements.pushAll(eXo.core.DOMUtil.findDescendantsByClass(portalNav, "div", "UITab"));
	var leftButton = eXo.core.DOMUtil.findFirstDescendantByClass(portalNav, "div", "ScrollLeftButton");
	leftButton.scrollMgr = uiNav.scrollMgr;
	leftButton.onclick = uiNav.scrollMgr.scrollLeft;
	var rightButton = eXo.core.DOMUtil.findFirstDescendantByClass(portalNav, "div", "ScrollRightButton");
	rightButton.scrollMgr = uiNav.scrollMgr;
	rightButton.onclick = uiNav.scrollMgr.scrollRight;
	uiNav.scrollMgr.callback = uiNav.scrollCallback;
	uiNav.initScroll();
};

UIPortalNavigation.prototype.initScroll = function(e) {
	var scrollMgr = eXo.portal.UIPortalNavigation.scrollMgr;
	scrollMgr.init();
	var portalNav = document.getElementById("PortalNavigationTopContainer");
	var buttons = eXo.core.DOMUtil.findFirstDescendantByClass(portalNav, "div", "ScrollButtons");
	buttons.style.display = "none";
	var maxWidth = portalNav.offsetWidth;
	if (eXo.core.Browser.isIE6()) {
		var tabs = eXo.core.DOMUtil.findAncestorByClass(portalNav, "UIHorizontalTabs");
		maxWidth = tabs.offsetWidth;
	}
	var elementsWidth = 0;
	for (var i = 0; i < scrollMgr.elements.length; i++) {
		scrollMgr.elements[i].style.display = "block";
		elementsWidth += scrollMgr.elements[i].offsetWidth;
		if (elementsWidth <= maxWidth) {
			scrollMgr.elements[i].isVisible = true;
		} else {
			scrollMgr.elements[i].isVisible = false;
			if (scrollMgr.lastVisibleIndex == -1) {
				scrollMgr.lastVisibleIndex = i-1;
				buttons.style.display = "block";
			}
		}
	}
	scrollMgr.renderElements();
};

UIPortalNavigation.prototype.scrollCallback = function() {
	var scrollMgr = eXo.portal.UIPortalNavigation.scrollMgr;
	var portalNav = document.getElementById("PortalNavigationTopContainer");
	var buttons = eXo.core.DOMUtil.findFirstDescendantByClass(portalNav, "div", "ScrollButtons");
	var utilWidth = portalNav.offsetWidth - buttons.offsetWidth;
	var usedWidth = 0;
	for (var i = scrollMgr.firstVisibleIndex; i <= scrollMgr.lastVisibleIndex; i++) usedWidth += scrollMgr.elements[i].offsetWidth;
	if (usedWidth > utilWidth) {
		if (scrollMgr.lastDirection == 1) { // Hides the first (left or up) element
			scrollMgr.elements[scrollMgr.firstVisibleIndex].isVisible = false;
			scrollMgr.elements[scrollMgr.firstVisibleIndex++].style.display = "none";
		} else { // Hides the last (right or down) element
			scrollMgr.elements[scrollMgr.lastVisibleIndex].isVisible = false;
			scrollMgr.elements[scrollMgr.lastVisibleIndex--].style.display = "none";
		}
	}
};
/***** Scroll Management *****/
eXo.portal.UIPortalNavigation = new UIPortalNavigation() ;