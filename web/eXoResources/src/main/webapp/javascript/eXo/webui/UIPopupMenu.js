eXo.require('eXo.webui.UIPopup');

function UIPopupMenu() {
	this.count = 0 ;
} ;

UIPopupMenu.prototype.init = function(popupMenu, container, x, y) {
	this.superClass = eXo.webui.UIPopup;
	this.superClass.init(popupMenu, container.id) ;
	this.superClass.setPosition(popupMenu, x, y) ;
		
	var menuItems = eXo.core.DOMUtil.findDescendantsByClass(popupMenu, "div", "MenuItem") ;
	var menuContainer = eXo.core.DOMUtil.findFirstDescendantByClass(popupMenu, "div", "MenuItemContainer") ;
	var menuItems2 = eXo.core.DOMUtil.findChildrenByClass(menuContainer, "div", "MenuItem") ;
	for(var i = 0; i<menuItems.length; i++) {
		menuItems[i].onmouseover = eXo.webui.UIPopupMenu.onMenuItemOver ;
		menuItems[i].onmouseout = eXo.webui.UIPopupMenu.onMenuItemOut ;
		menuItems[i].onclick = eXo.webui.UIPopupMenu.onClick;
	}
	window.status = "L: "+menuItems.length+", "+menuItems2.length;
} ;



UIPopupMenu.prototype.onClick = function(e) {	
	var targ;
	if (!e) var e = window.event;
	if (e.target) targ = e.target;
	else if (e.srcElement) targ = e.srcElement;
	if (targ.nodeType == 3) // defeat Safari bug
		targ = targ.parentNode;
		
	window.status = targ.innerHTML;
} ;

UIPopupMenu.prototype.onMenuItemOver = function(e) {	
	//if(eXo.webui.UIPopupMenu.timeout) {
		//window.clearTimeout(eXo.webui.UIPopupMenu.timeout) ;
	//}
	//eXo.webui.UIPopupMenu.count++ ;
	//if(!e) var e = window.event ;
	//e.cancelBubble = true ;
	//e.returnValue = false ;
	//if (e.stopPropagation) e.stopPropagation() ;
	var menuItem = this ;
	menuItem.className = "MenuItem OnMouseOver" ;
	
	var menuItemContainer = eXo.core.DOMUtil.findFirstDescendantByClass(menuItem, "div", "MenuItemContainer") ;
	//window.status = "Test: -- "+ eXo.webui.UIPopupMenu.count + " --  " ;
	if(menuItemContainer) {
		menuItem.style.position = "relative" ;
		eXo.webui.UIPopupMenu.showMenuItemContainer(menuItem, menuItemContainer) ;
	}
} ;

UIPopupMenu.prototype.onMenuItemOut = function(e) {
	//if(!e) var e = window.event ;
	//e.cancelBubble = true ;
	//e.returnValue = false ;
	//if (e.stopPropagation) e.stopPropagation() ;
	var menuItem = this ;
	menuItem.className = "MenuItem" ;
	
	var container = eXo.core.DOMUtil.findFirstDescendantByClass(menuItem, "div", "MenuItemContainer") ;
//	window.status = "Test: -- "+ eXo.webui.UIPopupMenu.count + " --  " ;
	if(container) {
		eXo.webui.UIPopupMenu.menuItemcontainer = container ;
		eXo.webui.UIPopupMenu.doOnMenuItemOut();
		//setTimeout("eXo.webui.UIPopupMenu.doOnMenuItemOut()", 500) ;
	}
} ;

UIPopupMenu.prototype.doOnMenuItemOut = function() {	
	eXo.webui.UIPopupMenu.menuItemcontainer.style.display = "none" ;
	//eXo.webui.UIPopupMenu.timeout = setTimeout("eXo.webui.UIPopupMenu.doOnMenuItemOut()", 500) ;
} ;

UIPopupMenu.prototype.showMenuItemContainer = function(menuItem, menuItemContainer) {
	this.superClass.setPosition(menuItemContainer, menuItem.offsetWidth-2, 0) ;
	menuItemContainer.style.display = "block" ;
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