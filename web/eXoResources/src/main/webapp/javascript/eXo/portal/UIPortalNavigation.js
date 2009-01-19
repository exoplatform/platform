/**
 * Manages the main navigation menu on the portal
 */
function UIPortalNavigation() {
  this.currentOpenedMenu = null;
  this.scrollMgr = null;
  this.scrollManagerLoaded = false;
};
/**
 * Sets some parameters :
 *  . the superClass to eXo.webui.UIPopupMenu
 *  . the css style classes
 * and calls the buildMenu function
 */
UIPortalNavigation.prototype.init = function(popupMenu, container, x, y) {
  this.superClass = eXo.webui.UIPopupMenu;
  this.superClass.init(popupMenu, container, x, y) ;
  
  this.tabStyleClass = "MenuItem";
  this.itemStyleClass = "NormalItem";
  this.selectedItemStyleClass = "SelectedItem";
  this.itemOverStyleClass = "OverItem";
  this.containerStyleClass = "MenuItemContainer";
  
  this.buildMenu(popupMenu);
};
/**
 * Calls the init function when the page loads
 */
UIPortalNavigation.prototype.onLoad = function() {
  var uiWorkingWorkspace = document.getElementById("UIWorkingWorkspace");
  var uiNavPortlets = eXo.core.DOMUtil.findDescendantsByClass(uiWorkingWorkspace, "div", "UINavigationPortlet");
  if (uiNavPortlets.length) {
		var mainContainer = eXo.core.DOMUtil.findFirstDescendantByClass(uiNavPortlets[0], "div", "TabsContainer");
 		eXo.portal.UIPortalNavigation.init(uiNavPortlets[0], mainContainer, 0, 0);
		for (var i = 1; i < uiNavPortlets.length; ++i) {
				uiNavPortlets[i].style.display = "none";
		}
  }
};
/**
 * Builds the menu and the submenus
 * Configures each menu item :
 *  . sets onmouseover and onmouseout to call setTabStyle
 *  . sets the width of the item
 * Checks if a submenu exists, if yes, set some parameters :
 *  . sets onclick on the item to call toggleSubMenu
 *  . sets the width and min-width of the sub menu container
 * For each sub menu item :
 *  . set onmouseover to onMenuItemOver and onmouseout to onMenuItemOut
 *  . adds onclick event if the item contains a link, so a click on this item will call the link
 */
UIPortalNavigation.prototype.buildMenu = function(popupMenu) {
  var DOMUtil = eXo.core.DOMUtil;
  var topContainer = DOMUtil.findFirstDescendantByClass(popupMenu, "div", "TabsContainer");
  topContainer.id = "PortalNavigationTopContainer";
  // Top menu items
  var topItems = DOMUtil.findDescendantsByClass(topContainer, "div", "UITab");
  for (var i = 0; i < topItems.length; i++) {
    var item = topItems[i];
    item.onmouseover = eXo.portal.UIPortalNavigation.setTabStyleOnMouseOver ;
    item.onmouseout = eXo.portal.UIPortalNavigation.setTabStyleOnMouseOut ;
    if (!item.getAttribute('hidesubmenu')) {
      item.onmousemove = eXo.portal.UIPortalNavigation.tabOnMouseMove ;
    }
    item.style.width = item.offsetWidth + "px";
    /**
     * TODO: fix IE7;
     */
    var container = DOMUtil.findFirstDescendantByClass(item, "div", this.containerStyleClass);
    if (container) {
      if (eXo.core.Browser.browserType == "mozilla" || eXo.core.Browser.isIE7()) {
        container.style.minWidth = item.offsetWidth + "px";
      } else {
        container.style.width = item.offsetWidth + "px";
      }
    }
  }
  
  // Sub menus items
  var menuItems = DOMUtil.findDescendantsByClass(topContainer, "div", this.tabStyleClass);
  for(var i = 0; i < menuItems.length; i++) {
    var menuItem = menuItems[i];
    menuItem.onmouseover = eXo.portal.UIPortalNavigation.onMenuItemOver;
    menuItem.onmouseout = eXo.portal.UIPortalNavigation.onMenuItemOut;

    // Set an id to each container for future reference
    var cont = DOMUtil.findAncestorByClass(menuItem, this.containerStyleClass) ;
    if (!cont.id) cont.id = "PortalNavigationContainer-" + i + Math.random();
    cont.resized = false;
  }
};
/**
 * Sets the tab style on mouse over and mouse out
 * If the mouse goes out of the item but stays on its sub menu, the item remains highlighted
 */
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

UIPortalNavigation.prototype.setTabStyleOnMouseOver = function(e) {
  var tab = this ;
  if (eXo.portal.UIPortalNavigation.previousMenuItem != tab) {
    eXo.portal.UIPortalNavigation.hideMenu() ;
  }
	eXo.portal.UIPortalNavigation.setTabStyleOnMouseOut(e, tab) ;
  eXo.portal.UIPortalNavigation.previousMenuItem = tab ;
  if (!eXo.portal.UIPortalNavigation.menuVisible) {
    var menuItemContainer = eXo.core.DOMUtil.findFirstDescendantByClass(tab, "div", eXo.portal.UIPortalNavigation.containerStyleClass);
    var hideSubmenu = tab.getAttribute('hideSubmenu') ;
    if (menuItemContainer && !hideSubmenu) {
      eXo.portal.UIPortalNavigation.toggleSubMenu(e, tab, menuItemContainer) ;
    }
  }
  eXo.portal.UIPortalNavigation.menuVisible = true ;  
} ;

UIPortalNavigation.prototype.setTabStyleOnMouseOut = function(e, src) {
  var tab = src || this;
  var tabChildren = eXo.core.DOMUtil.getChildrenByTagName(tab, "div") ;
  if (tabChildren.length <= 0) {
    return ;
  }
  if (tabChildren[0].className != "HighlightNavigationTab") {
    // highlights the tab
    eXo.webui.UIHorizontalTabs.changeTabNavigationStyle(tab, true);
  } else {
    if(tabChildren.length <= 1 || tabChildren[1].id != eXo.portal.UIPortalNavigation.currentOpenedMenu) {
      // de-highlights the tab if it doesn't have a submenu (cond 1) or its submenu isn't visible (cond 2)
      eXo.webui.UIHorizontalTabs.changeTabNavigationStyle(tab, false);
    }
  }
  eXo.portal.UIPortalNavigation.hideMenuTimeout(500) ;
}

UIPortalNavigation.prototype.tabOnMouseMove = function() {
  eXo.portal.UIPortalNavigation.cancelHideMenuContainer() ;
} ;

/**
 * Shows or hides a submenu
 * Calls hideMenuContainer to hide a submenu.
 * Hides any other visible sub menu before showing the new one
 * Sets the width of the submenu (the first time it is shown) to fix a bug in IE
 * Sets the currentOpenedMenu to the menu being opened
 */
UIPortalNavigation.prototype.toggleSubMenu = function(e, tab, menuItemContainer) {
  if (!e) e = window.event;
  e.cancelBubble = true;
  var src = eXo.core.Browser.getEventSource(e);
  if (src.tagName.toLowerCase() == "a" && !menuItemContainer) {
    if (src.href.substr(0, 7) == "http://") {
      if (!src.target) {
        window.location.href = src.href
      } else {
        return true ;
      }
    } else eval(src.href);
    return false;
  }
  var item = tab;
  var DOMUtil = eXo.core.DOMUtil;
  if (menuItemContainer) {
    if (menuItemContainer.style.display == "none") {
      // shows the sub menu
      // hides a previously opened sub menu
      if (eXo.portal.UIPortalNavigation.currentOpenedMenu) eXo.portal.UIPortalNavigation.hideMenu();
      
      eXo.portal.UIPortalNavigation.superClass.pushVisibleContainer(menuItemContainer.id);
      var offParent = item.offsetParent ;
      var y = item.offsetHeight + item.offsetTop;
      var x = item.offsetLeft + 2;
      if(eXo.core.I18n.isRT()) {
      	x = eXo.core.Browser.findPosX(offParent) + offParent.offsetWidth - eXo.core.Browser.findPosX(item) - item.offsetWidth;
//      	if(eXo.core.Browser.isIE6()) x += parseInt(document.getElementById("UIWorkingWorkspace").style.marginRight) ;
      }
      eXo.portal.UIPortalNavigation.superClass.setPosition(menuItemContainer, x, y, eXo.core.I18n.isRT());
      eXo.portal.UIPortalNavigation.superClass.show(menuItemContainer);
      
      menuItemContainer.style.width = menuItemContainer.offsetWidth + 2 + "px";
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

UIPortalNavigation.prototype.cancelHideMenuContainer = function() {
  if (this.hideMenuTimeoutId) {
    window.clearTimeout(this.hideMenuTimeoutId) ;
  }
} ;

UIPortalNavigation.prototype.closeMenuTimeout = function() {
  eXo.portal.UIPortalNavigation.hideMenuTimeout(200) ;
} ;

UIPortalNavigation.prototype.hideMenuTimeout = function(time) {
  this.cancelHideMenuContainer() ;
  if (!time || time <= 0) {
    time = 200 ;
  }
  //this.hideMenuTimeoutId = window.setTimeout(this.hideMenu, time) ;
  this.hideMenuTimeoutId = window.setTimeout('eXo.portal.UIPortalNavigation.hideMenu() ;', time) ;
} ;

/**
 * Adds the currentOpenedMenu to the list of containers to hide
 * and sets a time out to close them effectively
 * Sets currentOpenedMenu to null (no menu is opened)
 * Uses the methods from the superClass (eXo.webui.UIPopupMenu) to perform these operations
 */
UIPortalNavigation.prototype.hideMenuContainer = function() {
  var menuItemContainer = document.getElementById(eXo.portal.UIPortalNavigation.currentOpenedMenu);
  if (menuItemContainer) {
    eXo.portal.UIPortalNavigation.superClass.pushHiddenContainer(menuItemContainer.id);
    eXo.portal.UIPortalNavigation.superClass.popVisibleContainer();
    eXo.portal.UIPortalNavigation.superClass.setCloseTimeout();
    eXo.portal.UIPortalNavigation.superClass.hide(menuItemContainer);
    eXo.portal.UIPortalNavigation.currentOpenedMenu = null;
  }
  this.previousMenuItem = false ;
  eXo.portal.UIPortalNavigation.menuVisible = false ;
};
/**
 * Changes the style of the parent button when a submenu has to be hidden
 */
UIPortalNavigation.prototype.hideMenu = function() {
  if (eXo.portal.UIPortalNavigation.currentOpenedMenu) {
    var currentItemContainer = document.getElementById(eXo.portal.UIPortalNavigation.currentOpenedMenu);
    var tab = eXo.core.DOMUtil.findAncestorByClass(currentItemContainer, "UITab");
    eXo.webui.UIHorizontalTabs.changeTabNavigationStyle(tab, false);
  }
  eXo.portal.UIPortalNavigation.hideMenuContainer();
};
/**
 * When the mouse goes over a menu item (in the main nav menu)
 * Check if this menu item has a sub menu, if yes, opens it
 * Changes the style of the button
 */
UIPortalNavigation.prototype.onMenuItemOver = function(e) {
  var menuItem = this;
  var DOMUtil = eXo.core.DOMUtil;
  var subContainer = DOMUtil.findFirstDescendantByClass(menuItem, "div", eXo.portal.UIPortalNavigation.containerStyleClass);
  if (subContainer) {
    eXo.portal.UIPortalNavigation.superClass.pushVisibleContainer(subContainer.id);
    eXo.portal.UIPortalNavigation.showMenuItemContainer(menuItem, subContainer) ;
    if (!subContainer.firstTime) {
        subContainer.style.width = subContainer.offsetWidth + 2 + "px";
        subContainer.firstTime = true;
    }
  }
  eXo.portal.UIPortalNavigation.cancelHideMenuContainer() ;
};
/**
 * Shows a sub menu, uses the methods from superClass (eXo.webui.UIPopupMenu)
 */
UIPortalNavigation.prototype.showMenuItemContainer = function(menuItem, menuItemContainer) {
  var x = menuItem.offsetWidth;
  var y = menuItem.offsetTop;
  this.superClass.show(menuItemContainer);
  var posRight = eXo.core.Browser.getBrowserWidth() - eXo.core.Browser.findPosX(menuItem) - menuItem.offsetWidth ; 
  var rootX = (eXo.core.I18n.isLT() ? eXo.core.Browser.findPosX(menuItem) : posRight) ;
	if (x + menuItemContainer.offsetWidth + rootX > eXo.core.Browser.getBrowserWidth()) {
  	x -= (menuItemContainer.offsetWidth + menuItem.offsetWidth) ;
  }
  this.superClass.setPosition(menuItemContainer, x, y, eXo.core.I18n.isRT());
};
/**
 * When the mouse goes out a menu item from the main nav menu
 * Checks if this item has a sub menu, if yes calls methods from superClass to hide it
 */
UIPortalNavigation.prototype.onMenuItemOut = function(e) {
  var menuItem = this;
  var subContainer = eXo.core.DOMUtil.findFirstDescendantByClass(menuItem, "div", eXo.portal.UIPortalNavigation.containerStyleClass);
  if (subContainer) {
    eXo.portal.UIPortalNavigation.superClass.pushHiddenContainer(subContainer.id);
    eXo.portal.UIPortalNavigation.superClass.popVisibleContainer();
    eXo.portal.UIPortalNavigation.superClass.setCloseTimeout(300);
  }
};

/***** Scroll Management *****/
/**
 * Function called to load the scroll manager that will manage the tabs in the main nav menu
 *  . Creates the scroll manager with id PortalNavigationTopContainer
 *  . Adds the tabs to the scroll manager
 *  . Configures the arrows
 *  . Calls the initScroll function
 */
UIPortalNavigation.prototype.loadScroll = function(e) {
  var uiNav = eXo.portal.UIPortalNavigation;
  var portalNav = document.getElementById("PortalNavigationTopContainer");
  if (portalNav) {
    // Creates new ScrollManager and initializes it
    uiNav.scrollMgr = eXo.portal.UIPortalControl.newScrollManager("PortalNavigationTopContainer");
    uiNav.scrollMgr.initFunction = uiNav.initScroll;
    // Adds the tab elements to the manager
    var tabs = eXo.core.DOMUtil.findAncestorByClass(portalNav, "UIHorizontalTabs");
    uiNav.scrollMgr.mainContainer = tabs;
    uiNav.scrollMgr.arrowsContainer = eXo.core.DOMUtil.findFirstDescendantByClass(tabs, "div", "ScrollButtons");
    uiNav.scrollMgr.loadElements("UITab");
    // Configures the arrow buttons
    var arrowButtons = eXo.core.DOMUtil.findDescendantsByTagName(uiNav.scrollMgr.arrowsContainer, "div");
    if (arrowButtons.length == 2) {
      uiNav.scrollMgr.initArrowButton(arrowButtons[0], "left", "ScrollLeftButton", "HighlightScrollLeftButton", "DisableScrollLeftButton");
      uiNav.scrollMgr.initArrowButton(arrowButtons[1], "right", "ScrollRightButton", "HighlightScrollRightButton", "DisableScrollRightButton");
    }
    // Finish initialization
    uiNav.scrollMgr.callback = uiNav.scrollCallback;
    uiNav.scrollManagerLoaded = true;
    uiNav.initScroll();
  }
};
/**
 * Init function for the scroll manager
 *  . Calls the init function of the scroll manager
 *  . Calculates the available space to render the tabs
 *  . Renders the tabs
 */
UIPortalNavigation.prototype.initScroll = function(e) {
  if (!eXo.portal.UIPortalNavigation.scrollManagerLoaded) eXo.portal.UIPortalNavigation.loadScroll();
  var scrollMgr = eXo.portal.UIPortalNavigation.scrollMgr;
  scrollMgr.init();
  // Gets the maximum width available for the tabs
  scrollMgr.checkAvailableSpace();
  scrollMgr.renderElements();
};
/**
 * A callback function to call after a scroll event occurs (and the elements are rendered)
 * Is empty so far.
 */
UIPortalNavigation.prototype.scrollCallback = function() {
};
/***** Scroll Management *****/
eXo.portal.UIPortalNavigation = new UIPortalNavigation() ;
