eXo.require('eXo.webui.UIPopupMenu');

function UIExoStartMenu() {
	this.buttonClicked = false ;
} ;

UIExoStartMenu.prototype.init = function(popupMenu, container, x, y) {
	var uiStart = eXo.portal.UIExoStartMenu;
	
	this.superClass = eXo.webui.UIPopupMenu;
	this.superClass.init(popupMenu, container.id, x, y) ;
	
	this.itemStyleClass = "MenuItem";
	this.itemOverStyleClass = "MenuItemOver";
	this.containerStyleClass = "MenuItemContainer";
	
	this.exoStartButton = eXo.core.DOMUtil.findFirstDescendantByClass(container, "div", "ExoStartButton") ;
	this.exoStartButton.onmouseover = uiStart.startButtonOver ;
	this.exoStartButton.onmouseout = uiStart.startButtonOut ;
	
	this.buildMenu(popupMenu);
} ;

UIExoStartMenu.prototype.onLoad = function() {
	var uiStartContainer = document.getElementById("StartMenuContainer") ;
	var uiExoStart = document.getElementById("UIExoStart") ;
	eXo.portal.UIExoStartMenu.init(uiStartContainer, uiExoStart, 0, 0);
	eXo.webui.UIPopupMenu.hide(uiStartContainer);
	eXo.core.ExoDateTime.getTime() ;
};

UIExoStartMenu.prototype.buildMenu = function(popupMenu) {
	var menuItems = eXo.core.DOMUtil.findDescendantsByClass(popupMenu, "div", this.itemStyleClass) ;
	for(var i = 0; i<menuItems.length; i++) {
		menuItems[i].onmouseover = eXo.portal.UIExoStartMenu.onMenuItemOver ;
		menuItems[i].onmouseout = eXo.portal.UIExoStartMenu.onMenuItemOut ;
		// Set an id to each container for future reference
		var cont = eXo.core.DOMUtil.findAncestorByClass(menuItems[i], this.containerStyleClass) ;
		if (!cont.id) cont.id = "StartMenuContainer-"+i;
		cont.resized = false;
	}
};

UIExoStartMenu.prototype.startButtonOver = function() {
	if(!eXo.portal.UIExoStartMenu.buttonClicked) {
		this.className = "ExoStartButton ButtonOver" ;
	}
};

UIExoStartMenu.prototype.startButtonOut = function() {
	if(!eXo.portal.UIExoStartMenu.buttonClicked) {
		this.className = "ExoStartButton ButtonNormal" ;
	}
};

UIExoStartMenu.prototype.showStartMenu = function(e) {
	if (!e) var e = window.event;
	e.cancelBubble = true;

	var uiStartContainer = document.getElementById("StartMenuContainer") ;
	eXo.portal.UIExoStartMenu.exoStartButton.className = "ExoStartButton ButtonClicked" ;
	if(uiStartContainer.style.display == "block") {
		eXo.portal.UIExoStartMenu.hideUIStartMenu();
	} else {
		eXo.portal.UIExoStartMenu.buttonClicked = true ;
		var menuY = eXo.core.Browser.findPosY(eXo.portal.UIExoStartMenu.exoStartButton);
		this.superClass.show(uiStartContainer);
		var y = menuY - uiStartContainer.offsetHeight;
		if (window.pageYOffset) y -= window.pageYOffset;
		else if (document.documentElement.scrollTop) y -= document.documentElement.scrollTop;
		else if (document.body.scrollTop) y -= document.body.scrollTop;
		this.superClass.setPosition(uiStartContainer, 0, y) ;
		eXo.portal.UIExoStartMenu.setSize(uiStartContainer, 238, 191);
	}
	/*Hide eXoStartMenu whenever click on the UIApplication*/
	var uiPortalApplication = document.getElementById("UIPortalApplication") ;
	uiPortalApplication.onclick = eXo.portal.UIExoStartMenu.hideUIStartMenu ;
};

UIExoStartMenu.prototype.hideUIStartMenu = function() {
	var uiStartContainer = document.getElementById("StartMenuContainer") ;
	eXo.webui.UIPopupMenu.hide(uiStartContainer);
	eXo.portal.UIExoStartMenu.buttonClicked = false ;
	eXo.portal.UIExoStartMenu.exoStartButton.className = "ExoStartButton ButtonNormal" ;
	eXo.portal.UIExoStartMenu.clearStartMenu();
};

UIExoStartMenu.prototype.clearStartMenu = function() {
	eXo.webui.UIPopupMenu.currentVisibleContainers.clear();
	eXo.webui.UIPopupMenu.setCloseTimeout();
};

UIExoStartMenu.prototype.onMenuItemOver = function(e) {
	var menuItem = this;
	menuItem.className = eXo.portal.UIExoStartMenu.itemOverStyleClass ;
	var labelItem = eXo.core.DOMUtil.findFirstDescendantByClass(menuItem, "div", "LabelItem") ;
	// If the pointed menu item contains a link, sets the item clickable
	var link = eXo.core.DOMUtil.findDescendantsByTagName(labelItem, "a")[0];
	if (link && link.href) {
		window.status = link.href;
		menuItem.onclick = function() {
			if (link.href.substr(0, 7) == "http://") window.location.href = link.href;
			else eval(link.href);
			return false;
		}
	}
	// If the pointed menu item contains a submenu, resizes it
	var menuItemContainer = eXo.core.DOMUtil.findFirstDescendantByClass(menuItem, "div", eXo.portal.UIExoStartMenu.containerStyleClass) ;
	if (menuItemContainer) {
		eXo.portal.UIExoStartMenu.showMenuItemContainer(menuItem, menuItemContainer) ;
		eXo.portal.UIExoStartMenu.superClass.pushVisibleContainer(menuItemContainer.id);
	}

	if (menuItemContainer && eXo.core.Browser.getBrowserType() == "ie" && !menuItemContainer.resized) {
		// Resizes the container only once, the first time. After, container.resized is true so the condition is false
		eXo.portal.UIExoStartMenu.setContainerSize(menuItemContainer);
	}
};

UIExoStartMenu.prototype.showMenuItemContainer = function(menuItem, menuItemContainer) {
	this.superClass.show(menuItemContainer);
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
	this.superClass.setPosition(menuItemContainer, x, y);
}

UIExoStartMenu.prototype.onMenuItemOut = function(e) {
	var menuItem = this;
	menuItem.className = eXo.portal.UIExoStartMenu.itemStyleClass ;
	var menuItemContainer = eXo.core.DOMUtil.findFirstDescendantByClass(menuItem, "div", eXo.portal.UIExoStartMenu.containerStyleClass) ;
	if (menuItemContainer) {
		eXo.portal.UIExoStartMenu.superClass.pushHiddenContainer(menuItemContainer.id);
		eXo.portal.UIExoStartMenu.superClass.popVisibleContainer();
		eXo.portal.UIExoStartMenu.superClass.setCloseTimeout();
	}
	window.status = "";
};

UIExoStartMenu.prototype.setContainerSize = function(menuItemContainer) {
	var menuCenter = eXo.core.DOMUtil.findFirstDescendantByClass(menuItemContainer, "div", "StartMenuML");
	var menuTop = eXo.core.DOMUtil.findFirstDescendantByClass(menuItemContainer, "div", "StartMenuTL");
	var decorator = eXo.core.DOMUtil.findFirstDescendantByClass(menuTop, "div", "StartMenuTR");
	var menuBottom = menuTop.nextSibling;
	while (menuBottom.className != "StartMenuBL") menuBottom = menuBottom.nextSibling;
	var w = menuCenter.offsetWidth - decorator.offsetLeft;
	menuTop.style.width = w;
	menuBottom.style.width = w;
	menuCenter.style.width = w;
	menuItemContainer.resized = true;
};

UIExoStartMenu.prototype.setSize = function(popup, w, h) {
	if (typeof(popup) == "string") popup = document.getElementById(popup);
	if (popup) {
		popup.style.width = w;
		popup.style.height = h;
	}
};

eXo.portal.UIExoStartMenu = new UIExoStartMenu() ;