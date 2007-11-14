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
		var extraX = 0 ;
		var extraY = 0 ;
		if (UIContextMenu.menuElement.offsetParent) {
			extraX = eXo.core.Browser.findPosX(UIContextMenu.menuElement.offsetParent) ;
			extraY = eXo.core.Browser.findPosY(UIContextMenu.menuElement.offsetParent) ;
		}
		var top = eXo.core.Browser.findMouseYInPage(_e) - extraY ;
		var left = eXo.core.Browser.findMouseXInPage(_e) - extraX ;
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
		UIContextMenu.menuElement.onmouseover = UIContextMenu.autoHide ;
		UIContextMenu.menuElement.onmouseout = UIContextMenu.autoHide ;		
		if (!UIContextMenu.IE) {
//			var childNodes = eXo.core.DOMUtil.getChildrenByTagName(document.body, "div")  ;
//			var show = false ;
//			for(var i = 0 ; i < childNodes.length ; i ++) {
//				if  (childNodes[i].getAttribute("id") == menuElementId) {
//					show = true ;
//					break ;
//				}
//			}
//			if (!show) {				
				document.body.appendChild(UIContextMenu.menuElement) ;
			//}
		}
		return false ;
	}
	return UIContextMenu.getReturnValue(_e) ;
} ;

UIContextMenu.prototype.autoHide = function(evt) {
	var _e = window.event || evt ;
	var eventType = _e.type ;	
	var UIContextMenu = eXo.webui.UIContextMenu ;
	if (eventType == 'mouseout') {
		UIContextMenu.timeout = setTimeout("eXo.webui.UIContextMenu.menuElement.style.display='none'", 5000) ;		
	} else {
		if (UIContextMenu.timeout) clearTimeout(UIContextMenu.timeout) ;		
	}
} ;

UIContextMenu.prototype.replaceall = function(string, obj) {			
	var p = new Array() ;
	var i = 0 ;
	for(var reg in obj){
		p.push(new RegExp(reg)) ;
		string = string.replace(p[i], obj[reg]) ;
		i++ ;
	}
	if (!string) alert("Not match") ;
	return string ;
} ;

UIContextMenu.prototype.changeAction = function(obj, id) {
	var actions = eXo.core.DOMUtil.findDescendantsByTagName(obj, "a") ;
	var len = actions.length ;
	var href = "" ;
	if (typeof(id) == "string") {		
		var pattern = /objectId\s*=\s*[A-Za-z0-9_]*(?=&|'|")/ ;
		for(var i = 0 ; i < len ; i++) {
			href = String(actions[i].href) ;
			if (!pattern.test(href)) continue ;
			actions[i].href = href.replace(pattern,"objectId="+id) ;
		}
	} else if (typeof(id) == "object") {
		for(var i = 0 ; i < len ; i++) {
			href = String(actions[i].href) ;			
			actions[i].href = eXo.webui.UIContextMenu.replaceall(href, id) ;
		}
	} else {
		return  ;
	}
	
} ;

UIContextMenu.prototype.hide = function() {
	var ln = eXo.core.DOMUtil.hideElementList.length ;
	if (ln > 0) {
		for (var i = 0; i < ln; i++) {
			eXo.core.DOMUtil.hideElementList[i].style.display = "none" ;
		}
	}
} ;

UIContextMenu.prototype.showHide = function(obj) {
	if (obj.style.display != "block") {
		eXo.webui.UIContextMenu.hide() ;
		obj.style.display = "block" ;
		eXo.core.DOMUtil.listHideElements(obj) ;
	} else {
		obj.style.display = "none" ;
	}
} ;

UIContextMenu.prototype.swapMenu = function(oldmenu, clickobj) {
	var UIContextMenu = eXo.webui.UIContextMenu ;
	var Browser = eXo.core.Browser ;
	var menuX = Browser.findPosX(clickobj) ;
	var menuY = Browser.findPosY(clickobj) + clickobj.offsetHeight ;
	if (arguments.length > 2) { // Customize position of menu with an object that have 2 properties x, y 
		menuX = arguments[2].x ;
		menuY = arguments[2].y ;
	}	
	if(document.getElementById("tmpMenuElement")) document.getElementById("UIPortalApplication").removeChild(document.getElementById("tmpMenuElement")) ;
	var tmpMenuElement = oldmenu.cloneNode(true) ;
	tmpMenuElement.setAttribute("id","tmpMenuElement") ;
	UIContextMenu.menuElement = tmpMenuElement ;
	document.getElementById("UIPortalApplication").appendChild(tmpMenuElement) ;
	UIContextMenu.menuElement.style.top = menuY + "px" ;
	UIContextMenu.menuElement.style.left = menuX + "px" ;	
	UIContextMenu.showHide(UIContextMenu.menuElement) ;
} ;

eXo.webui.UIContextMenu = new UIContextMenu() ;