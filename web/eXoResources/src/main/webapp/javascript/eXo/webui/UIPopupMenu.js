eXo.require('eXo.webui.UIPopup');

function UIPopupMenu() {
	this.count = 0 ;
} ;

UIPopupMenu.prototype.init = function(popupMenu, container, x, y) {
	this.superClass.init(popupMenu, container.id) ;
	this.setPosition(popupMenu, x, y) ;
		
	var menuItems = eXo.core.DOMUtil.findDescendantsByClass(popupMenu, "div", "MenuItem") ;
	for(var i = 0; i<menuItems.length; i++) {
		menuItems[i].onmouseover = eXo.webui.UIPopupMenu.onMenuItemOver ;
		menuItems[i].onmouseout = eXo.webui.UIPopupMenu.onMenuItemOut ;
		menuItems[i].onclick = eXo.webui.UIPopupMenu.onClick;
	}
} ;

UIPopupMenu.prototype.onClick = function(clickedButton) {	
} ;

UIPopupMenu.prototype.onMenuItemOver = function(e) {	
	if(eXo.webui.UIPopupMenu.timeout) {
		window.clearTimeout(eXo.webui.UIPopupMenu.timeout) ;
	}
	eXo.webui.UIPopupMenu.count++ ;
	if(!e) var e = window.event ;
	e.cancelBubble = true ;
	e.returnValue = false ;
//	if (e.stopPropagation) e.stopPropagation() ;
	var menuItem = this ;
	menuItem.className = "MenuItem OnMouseOver" ;
	
	var menuItemContainer = eXo.core.DOMUtil.findFirstDescendantByClass(menuItem, "div", "MenuItemContainer") ;
	window.status = "Test: -- "+ eXo.webui.UIPopupMenu.count + " --  " ;
	if(menuItemContainer) {
		menuItem.style.position = "relative" ;
		eXo.webui.UIPopupMenu.showMenuItemContainer(menuItem, menuItemContainer) ;
	}
} ;

UIPopupMenu.prototype.onMenuItemOut = function(e) {
	if(!e) var e = window.event ;
	e.cancelBubble = true ;
	e.returnValue = false ;
	
	var menuItem = this ;
	menuItem.className = "MenuItem" ;
	
	var container = eXo.core.DOMUtil.findFirstDescendantByClass(menuItem, "div", "MenuItemContainer") ;
//	window.status = "Test: -- "+ eXo.webui.UIPopupMenu.count + " --  " ;
	if(container) {
		eXo.webui.UIPopupMenu.menuItemcontainer = container ;
		setTimeout("eXo.webui.UIPopupMenu.doOnMenuItemOut()", 500) ;
	}
} ;

UIPopupMenu.prototype.doOnMenuItemOut = function() {	
	eXo.webui.UIPopupMenu.menuItemcontainer.style.display = "none" ;
	eXo.webui.UIPopupMenu.timeout = setTimeout("eXo.webui.UIPopupMenu.doOnMenuItemOut()", 500) ;
} ;

UIPopupMenu.prototype.showMenuItemContainer = function(menuItem, menuItemContainer) {
	this.setPosition(menuItemContainer, menuItem.offsetWidth, 0) ;
	menuItemContainer.style.display = "block" ;
} ;

UIPopupMenu.prototype.hide = function(object) {
	if(typeof(object) == "string") object = document.getElementById(object);
	object.style.display = "none" ;
	
} ;

eXo.webui.UIPopupMenu = new UIPopupMenu() ;