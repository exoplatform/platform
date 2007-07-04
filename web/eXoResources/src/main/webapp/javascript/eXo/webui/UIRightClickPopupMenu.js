eXo.require('eXo.webui.UIPopup');

function UIRightClickPopupMenu() {};

UIRightClickPopupMenu.prototype.init = function(contextMenuId) {
	var contextMenu = document.getElementById(contextMenuId) ;
	// TODO: Fix temporary for the problem Minimize window in Page Mode
	if(!contextMenu) return;
	
	contextMenu.onmousedown = function(e) {
		if(!e) e = window.event ;
		e.cancelBubble = true ;
	}

	var parentNode = contextMenu.parentNode ;
	parentNode.contextMenu = contextMenu ;
	parentNode.onmousedown = function() {
		this.contextMenu.style.display = 'none' ;
	}
	
	var items = eXo.core.DOMUtil.findDescendantsByClass(contextMenu, "div", "MenuItem");
	for(var i = 0; i < items.length; i++) {
		items[i].onmouseout = new Function("this.className = 'MenuItem'") ;
		items[i].onmouseover = new Function("this.className = 'SelectedMenuItem'") ;
	}
	
	this.disableContextMenu(parentNode) ;
}

UIRightClickPopupMenu.prototype.disableContextMenu = function(comp) {
	if(typeof(comp) == "string") comp = document.getElementById(comp) ;
	comp.onmouseover = function() {
		document.body.oncontextmenu = new Function("return false;") ;
	}
	
	comp.onmouseout = function() {
		document.body.oncontextmenu = new Function("return true;") ;
	}
};

UIRightClickPopupMenu.prototype.prepareObjectId = function(elemt) {
	var aTag = elemt.getElementsByTagName('a') ;
	var str = aTag[0].getAttribute('href') ;
	var contextMenu = eXo.core.DOMUtil.findAncestorByClass(elemt, "UIRightClickPopupMenu");
	aTag[0].setAttribute('href',str.replace('_objectid_', contextMenu.objId.replace(/'/g, "\\'"))) ;
}

UIRightClickPopupMenu.prototype.clickRightMouse = function(event, elemt, menuId, objId, params) {
	event.cancelBubble = true;
	var contextMenu = document.getElementById(menuId) ;
	contextMenu.objId = objId ;
	if(!(((event.which) && (event.which == 2 || event.which == 3)) || ((event.button) && (event.button == 2))))	{
		contextMenu.style.display = 'none' ;
		return;
	}

	if(params) {
		params = "," + params + "," ;
		var items = eXo.core.DOMUtil.findDescendantsByClass(contextMenu, "div", "MenuItem");
		for(var i = 0; i < items.length; i++) {
			if(params.indexOf(items[i].getAttribute("exo:attr")) > -1) {
				items[i].style.display = 'block' ;
			} else {
				items[i].style.display = 'none' ;
			}
		}
	}
	var customItem = eXo.core.DOMUtil.findFirstChildByClass(elemt, "div", "RightClickCustomItem") ;
	var tmpCustomItem = eXo.core.DOMUtil.findFirstDescendantByClass(contextMenu, "div", "RightClickCustomItem") ;
	if(customItem) {
		tmpCustomItem.innerHTML = customItem.innerHTML ;
		tmpCustomItem.style.display = "block" ;
	} else {
		tmpCustomItem.style.display = "none" ;
	}
	eXo.core.Mouse.update(event) ;
	eXo.webui.UIPopup.show(contextMenu)
	
	var intTop = eXo.core.Mouse.mouseyInPage - (eXo.core.Browser.findPosY(contextMenu) - contextMenu.offsetTop);
	var intLeft = eXo.core.Mouse.mousexInPage - (eXo.core.Browser.findPosX(contextMenu) - contextMenu.offsetLeft);
	if((eXo.core.Mouse.mouseyInClient + contextMenu.offsetHeight) > eXo.core.Browser.getBrowserHeight()) {
		intTop -= contextMenu.offsetHeight ;
	}
	contextMenu.style.top = intTop + "px";
	contextMenu.style.left = intLeft + "px";
};

eXo.webui.UIRightClickPopupMenu = new UIRightClickPopupMenu() ;