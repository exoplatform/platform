eXo.require('eXo.webui.UIPopup');

function UIRightClickPopupMenu() {};

UIRightClickPopupMenu.prototype.init = function(contextMenuId) {
	var contextMenu = document.getElementById(contextMenuId) ;
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
}

UIRightClickPopupMenu.prototype.prepareObjectId = function(elemt) {
	var aTag = elemt.getElementsByTagName('a') ;
	var str = aTag[0].getAttribute('href') ;
	var contextMenu = eXo.core.DOMUtil.findAncestorByClass(elemt, "UIRightClickPopupMenu");
	aTag[0].setAttribute('href',str.replace('_objectid_', contextMenu.objId)) ;
}

UIRightClickPopupMenu.prototype.clickRightMouse = function(event, menuId, objId, params) {
	this.superClass = eXo.webui.UIPopup ;
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
	eXo.core.Mouse.update(event) ;
	this.superClass.show(contextMenu)
	
	var intTop = eXo.core.Mouse.mouseyInPage;
	var intLeft = eXo.core.Mouse.mousexInPage;
  popupWindow = eXo.core.DOMUtil.findAncestorByClass(contextMenu, "UIDragObject");
  if(popupWindow) {
    intTop = intTop - eXo.core.Browser.findPosY(popupWindow) ;
    intLeft = intLeft - eXo.core.Browser.findPosX(popupWindow) ;
  }
	contextMenu.style.top = intTop + "px";
	contextMenu.style.left = intLeft + "px";
	
	
}
eXo.webui.UIRightClickPopupMenu = new UIRightClickPopupMenu() ;