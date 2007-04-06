eXo.require('eXo.webui.UIPopup');

function UIPopupMenu() {
	this.elementsToClose = new Array();
	this.currentVisibleContainers = new Array();
} ;

UIPopupMenu.prototype.init = function(popupMenu, container, x, y) {
	this.superClass = eXo.webui.UIPopup;
	this.superClass.init(popupMenu, container.id) ;
	this.superClass.setPosition(popupMenu, x, y) ;
		
	var menuItems = eXo.core.DOMUtil.findDescendantsByClass(popupMenu, "div", "MenuItemL") ;
	var menuContainer = eXo.core.DOMUtil.findFirstDescendantByClass(popupMenu, "div", "MenuItemContainer") ;
	var menuItemWidths = new Array();
	for(var i = 0; i<menuItems.length; i++) {
		menuItems[i].onmouseover = eXo.webui.UIPopupMenu.onMenuItemOver ;
		menuItems[i].onmouseout = eXo.webui.UIPopupMenu.onMenuItemOut ;
		menuItems[i].onclick = eXo.webui.UIPopupMenu.onClick;
		// Set width of each item depending on its container width
		var cont = eXo.core.DOMUtil.findAncestorByClass(menuItems[i], "MenuItemContainer") ;
		if (!cont.id) cont.id = "cont-"+i;
		if (!menuItemWidths[cont.id])
			menuItemWidths[cont.id] = cont.offsetWidth;
		menuItems[i].style.width = menuItemWidths[cont.id] + "px";
		// Set the right icon if necessary
		var childContainer = eXo.core.DOMUtil.findFirstDescendantByClass(menuItems[i], "div", "MenuItemContainer") ;
		if (childContainer) {
			//var itemRight = eXo.core.DOMUtil.findAncestorByClass(menuItems[i], "MenuItemR") ;
			var itemRight = eXo.core.DOMUtil.findFirstDescendantByClass(menuItems[i], "div", "MenuItemR") ;
			itemRight.className += " HasSubMenu";
		}
	}
} ;

UIPopupMenu.prototype.onClick = function(e) {
	var targ = eXo.core.DOMUtil.getEventSource(e);
	window.status = targ.innerHTML;
} ;


UIPopupMenu.prototype.onMenuItemOver = function(e) {
	var menuItem = this ;
	//eXo.webui.UIPopupMenu.count++ ;
	//if(!e) var e = window.event ;
	//e.cancelBubble = true ;
	//e.returnValue = false ;
	//if (e.stopPropagation) e.stopPropagation() ;
	//var menuItemL = eXo.core.DOMUtil.findAncestorByClass(menuItem, "MenuItemL") ;
	menuItem.className = "MenuItemL OnMouseOver" ;
	
	var menuItemContainer = eXo.core.DOMUtil.findFirstDescendantByClass(menuItem, "div", "MenuItemContainer") ;
	if(menuItemContainer) {
		menuItem.style.position = "relative" ;
		eXo.webui.UIPopupMenu.showMenuItemContainer(menuItem, menuItemContainer) ;
		eXo.webui.UIPopupMenu.currentVisibleContainers.push(menuItemContainer.id);
	} else {
		eXo.webui.UIPopupMenu.currentVisibleContainers.clear();
	}
	window.status = "";
	for (var i=0; i<eXo.webui.UIPopupMenu.currentVisibleContainers.length; i++)
		window.status += eXo.webui.UIPopupMenu.currentVisibleContainers[i]+", ";
} ;

UIPopupMenu.prototype.onMenuItemOut = function(e) {
	//if(!e) var e = window.event ;
	//e.cancelBubble = true ;
	//e.returnValue = false ;
	//if (e.stopPropagation) e.stopPropagation() ;
	var menuItem = this ;
	//var menuItemL = eXo.core.DOMUtil.findAncestorByClass(menuItem, "MenuItemL") ;
	menuItem.className = "MenuItemL" ;
	
	var container = eXo.core.DOMUtil.findFirstDescendantByClass(menuItem, "div", "MenuItemContainer") ;
	if(container) {
		eXo.webui.UIPopupMenu.menuItemcontainer = container ;
		//eXo.webui.UIPopupMenu.doOnMenuItemOut();
		eXo.webui.UIPopupMenu.elementsToClose.push(container.id);
		eXo.webui.UIPopupMenu.currentVisibleContainers.pop();
		setTimeout("eXo.webui.UIPopupMenu.doOnMenuItemOut()", 300) ;
	}
} ;

UIPopupMenu.prototype.doOnMenuItemOut = function() {
	while (eXo.webui.UIPopupMenu.elementsToClose.length > 0) {
		var container = document.getElementById(eXo.webui.UIPopupMenu.elementsToClose.shift());
		if (!eXo.webui.UIPopupMenu.currentVisibleContainers.contains(container.id))
			container.style.visibility = "hidden" ;
	}
} ;

UIPopupMenu.prototype.showMenuItemContainer = function(menuItem, menuItemContainer) {
	//var menuItemR = eXo.core.DOMUtil.findAncestorByClass(menuItem, "MenuItemR") ;
	this.superClass.setPosition(menuItemContainer, menuItem.offsetWidth, 0) ;
	menuItemContainer.style.visibility = "visible" ;
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