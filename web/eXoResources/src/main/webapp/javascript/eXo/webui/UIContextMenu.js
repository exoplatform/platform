function UIContextMenu(){
	this.menus = new Array,
	this.attachedElement = null ;
	this.menuElement = null ;
	this.preventDefault = true ;
	this.preventForms = true ;
}
UIContextMenu.prototype.getCallback = function(menuId) {
	var menus = document.getElementById(menuId) ;
	var callback = menus.getAttribute("eXoCallback") ;
	return callback ;
} ;
UIContextMenu.prototype.init = function(conf) {
	var UIContextMenu = eXo.webui.UIContextMenu ;
	if ( document.all && document.getElementById && !window.opera ) {
		UIContextMenu.IE = true;
	}

	if ( !document.all && document.getElementById && !window.opera ) {
		UIContextMenu.FF = true;
	}

	if ( document.all && document.getElementById && window.opera ) {
		UIContextMenu.OP = true;
	}

	if ( UIContextMenu.IE || UIContextMenu.FF ) {

		if (conf && typeof(conf.preventDefault) != "undefined") {
			UIContextMenu.preventDefault = conf.preventDefault;
		}

		if (conf && typeof(conf.preventForms) != "undefined") {
			UIContextMenu.preventForms = conf.preventForms;
		}
		document.oncontextmenu = UIContextMenu.show;

	}
} ;
UIContextMenu.prototype.attach = function(classNames, menuId) {
	var UIContextMenu = eXo.webui.UIContextMenu ;
	if (typeof(classNames) == "string") {
		UIContextMenu.menus[classNames] = menuId;
	}

	if (typeof(classNames) == "object") {
		for (x = 0; x < classNames.length; x++) {
			UIContextMenu.menus[classNames[x]] = menuId ;
		}
	}
} ;

UIContextMenu.prototype.getMenuElementId = function(evt) {
	var _e = window.event || evt ;
	var UIContextMenu = eXo.webui.UIContextMenu ;
	if (UIContextMenu.IE) {
		UIContextMenu.attachedElement = _e.srcElement;
	} else {
		UIContextMenu.attachedElement = _e.target;
	}

	while(UIContextMenu.attachedElement != null) {
		var className = UIContextMenu.attachedElement.className;

		if (typeof(className) != "undefined") {
			className = className.replace(/^\s+/g, "").replace(/\s+$/g, "")
			var classArray = className.split(/[ ]+/g);

			for (i = 0; i < classArray.length; i++) {
				if (UIContextMenu.menus[classArray[i]]) {
					return UIContextMenu.menus[classArray[i]];
				}
			}
		}

		if (UIContextMenu.IE) {
			UIContextMenu.attachedElement = UIContextMenu.attachedElement.parentElement;
		} else {
			UIContextMenu.attachedElement = UIContextMenu.attachedElement.parentNode;
		}
	}

	return null;
} ;

UIContextMenu.prototype.getReturnValue = function(evt) {
	var returnValue = true;
	var _e = window.event || evt;

	if (evt.button != 1) {
		if (evt.target) {
			var el = _e.target;
		} else if (_e.srcElement) {
			var el = _e.srcElement;
		}

		var tname = el.tagName.toLowerCase();

		if ((tname == "input" || tname == "textarea")) {
			if (!UIContextMenu.preventForms) {
				returnValue = true;
			} else {
				returnValue = false;
			}
		} else {
			if (!UIContextMenu.preventDefault) {
				returnValue = true;
			} else {
				returnValue = false;
			}
		}
	}

	return returnValue;
} ;

UIContextMenu.prototype.show = function(evt) {
	var _e = window.event || evt
	var UIContextMenu = eXo.webui.UIContextMenu ;
	var menuElementId = UIContextMenu.getMenuElementId(_e) ;

	if (menuElementId) {
		UIContextMenu.menuElement = document.getElementById(menuElementId) ;
		var callback = UIContextMenu.getCallback(menuElementId) ;
		if(callback) {
			callback = callback + "(_e)" ;
			eval(callback) ;
		}		
		var top = eXo.core.Browser.findMouseYInPage(_e) ;
		var left = eXo.core.Browser.findMouseXInPage(_e) ;
		eXo.core.DOMUtil.listHideElements(UIContextMenu.menuElement) ;
		var ln = eXo.core.DOMUtil.hideElementList.length ;
		if (ln > 0) {
			for (var i = 0; i < ln; i++) {
				eXo.core.DOMUtil.hideElementList[i].style.display = "none" ;
			}
		}
		UIContextMenu.menuElement.style.left = left + "px" ;
		UIContextMenu.menuElement.style.top = top + "px" ;
		UIContextMenu.menuElement.style.display = 'block' ;
		return false ;
	}
	return UIContextMenu.getReturnValue(_e) ;
} ;
eXo.webui.UIContextMenu = new UIContextMenu() ;