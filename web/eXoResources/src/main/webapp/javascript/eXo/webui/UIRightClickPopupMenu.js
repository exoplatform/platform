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
	this.disableContextMenu(parentNode) ;
}

UIRightClickPopupMenu.prototype.hideContextMenu = function(contextId) {
	if (document.getElementById(contextId)) {
		document.getElementById(contextId).style.display = 'none' ;
		eXo.core.MouseEventManager.onMouseDownHandlers = null ;
	}
}

UIRightClickPopupMenu.prototype.disableContextMenu = function(comp) {
	if(typeof(comp) == "string") comp = document.getElementById(comp) ;
	comp.onmouseover = function() {
		document.oncontextmenu = function() {return false} ;
	}
	comp.onmouseout = function() {
		document.oncontextmenu = function() {return true} ;
	}
};

UIRightClickPopupMenu.prototype.prepareObjectId = function(evt, elemt) {
	var contextMenu = eXo.core.DOMUtil.findAncestorByClass(elemt, "UIRightClickPopupMenu") ;
	contextMenu.style.display = "none" ;
	var href = elemt.getAttribute('href') ;
	if(href.indexOf("javascript") == 0) {
		eval(unescape(href).replace('_objectid_', encodeURI(contextMenu.objId.replace(/'/g, "\\'")))) ; 
		eXo.core.MouseEventManager.docMouseDownEvt(evt) ;
		return false;
	}
	elemt.setAttribute('href', href.replace('_objectid_', encodeURI(contextMenu.objId.replace(/'/g, "\\'")))) ;
	return true;
}

UIRightClickPopupMenu.prototype.clickRightMouse = function(event, elemt, menuId, objId, params, opt) {
	if (!event) event = window.event;
	eXo.core.MouseEventManager.docMouseDownEvt(event) ;
	var contextMenu = document.getElementById(menuId) ;
	contextMenu.objId = objId ;
	if(!(((event.which) && (event.which == 2 || event.which == 3)) || ((event.button) && (event.button == 2))))	{
		contextMenu.style.display = 'none' ;
		return;
	}
	
	eXo.core.MouseEventManager.addMouseDownHandler("eXo.webui.UIRightClickPopupMenu.hideContextMenu('" + menuId + "');")

	if(params) {
		params = "," + params + "," ;
		var items = contextMenu.getElementsByTagName("a") ;
		for(var i = 0; i < items.length; i++) {
			if(params.indexOf(items[i].getAttribute("exo:attr")) > -1) {
				items[i].style.display = 'block' ;
			} else {
				items[i].style.display = 'none' ;
			}
		}
	}
	var customItem = eXo.core.DOMUtil.findFirstDescendantByClass(elemt, "div", "RightClickCustomItem") ;
	var tmpCustomItem = eXo.core.DOMUtil.findFirstDescendantByClass(contextMenu, "div", "RightClickCustomItem") ;
	if(tmpCustomItem) {
		if(customItem) {
			tmpCustomItem.innerHTML = customItem.innerHTML ;
			tmpCustomItem.style.display = "block" ;
		} else {
			tmpCustomItem.style.display = "none" ;
		}
	}
		/*
	 * fix bug right click in IE7.
	 */
	var fixWidthForIE7 = 0 ;
	var UIWorkingWorkspace = document.getElementById("UIWorkingWorkspace") ;
	if (eXo.core.Browser.isIE7() && document.getElementById("UIDockBar")) {
		  if (event.clientX > UIWorkingWorkspace.offsetLeft) fixWidthForIE7 = UIWorkingWorkspace.offsetLeft ;
	}
	
	eXo.core.Mouse.update(event);
	eXo.webui.UIPopup.show(contextMenu);
	
	var ctxMenuContainer = eXo.core.DOMUtil.findFirstChildByClass(contextMenu, "div", "UIContextMenuContainer") ;
	var intTop = eXo.core.Mouse.mouseyInPage - (eXo.core.Browser.findPosY(contextMenu) - contextMenu.offsetTop) ;
	var intLeft = eXo.core.Mouse.mousexInPage - (eXo.core.Browser.findPosX(contextMenu) - contextMenu.offsetLeft) + fixWidthForIE7 ;
	if(eXo.core.I18n.isRT()) {
		//scrollWidth is width of browser scrollbar
		var scrollWidth = 16 ;
		if(eXo.core.Browser.getBrowserType() == "mozilla") scrollWidth = 0 ;
		intLeft = contextMenu.offsetParent.offsetWidth - intLeft + fixWidthForIE7 + scrollWidth ;
		var clickCenter = eXo.core.DOMUtil.findFirstDescendantByClass(contextMenu, "div", "ClickCenterBottom") ;
		if(clickCenter) {
			var clickCenterWidth = clickCenter ? parseInt(eXo.core.DOMUtil.getStyle(clickCenter, "marginRight")) : 0 ;
			intLeft += (ctxMenuContainer.offsetWidth - 2*clickCenterWidth) ;
		}
	}

	switch (opt) {
		case 1:
			intTop -= ctxMenuContainer.offsetHeight ;
			break;
		case 2:
			break;
		case 3:
			break;
		case 4:
			break;
		default:
			if((eXo.core.Mouse.mouseyInClient + ctxMenuContainer.offsetHeight) > eXo.core.Browser.getBrowserHeight()) {
				intTop -= ctxMenuContainer.offsetHeight ;
			}
			break;
	}

	contextMenu.style.top = intTop + "px";
	if(eXo.core.I18n.isLT()) contextMenu.style.left = intLeft + "px";
	else contextMenu.style.right = intLeft + "px" ;
};

eXo.webui.UIRightClickPopupMenu = new UIRightClickPopupMenu() ;