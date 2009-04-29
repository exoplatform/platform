function showPopupMenu(obj) {
	if(!obj) return;
	var uiNavi = document.getElementById('PortalNavigationTopContainer');
	if(eXo.core.Browser.browserType == 'ie' && uiNavi)   uiNavi.style.position = "static";
	if(obj.Timeout) clearTimeout(obj.Timeout);
	var DOMUtil = eXo.core.DOMUtil;
	var mnuItemContainer = DOMUtil.findNextElementByTagName(obj, "div");
	var objParent = DOMUtil.findAncestorByClass(obj, "TBItem");
	if(mnuItemContainer && mnuItemContainer.style.display != "block") {
		mnuItemContainer.style.display = 'block';
		mnuItemContainer.style.width = mnuItemContainer.offsetWidth - parseInt(DOMUtil.getStyle(mnuItemContainer, "borderLeftWidth")) - parseInt(DOMUtil.getStyle(mnuItemContainer, "borderRightWidth")) + 'px';
		objParent.className = 'TBItemHover';
		mnuItemContainer.onmouseout = function(){
			if(eXo.core.Browser.browserType == 'ie')   uiNavi.style.position = "relative";
			obj.Timeout = setTimeout(function() {
				mnuItemContainer.style.display = 'none';
				objParent.className = 'TBItem';
				mnuItemContainer.onmouseover = null;
				mnuItemContainer.onmouseout = null;
			},1*10);
		}

		mnuItemContainer.onmouseover = function() {
			objParent.className = 'TBItemHover';
			if(eXo.core.Browser.browserType == 'ie')  uiNavi.style.position = "static";
			if(obj.Timeout) clearTimeout(obj.Timeout);
			obj.Timeout = null;
		}
		obj.onmouseout = mnuItemContainer.onmouseout;	
	}
}		

function showPopupSubMenu(obj) {
	if(!obj) return;
	if(obj.Timeout) clearTimeout(obj.Timeout);	
	var DOMUtil = eXo.core.DOMUtil;
	var objParent = DOMUtil.findAncestorByClass(obj, "ArrowIcon");
	var subMenuItemContainer = false;
	if(objParent) subMenuItemContainer = DOMUtil.findNextElementByTagName(objParent, "div");
	if(subMenuItemContainer && subMenuItemContainer.style.display != "block") {
		subMenuItemContainer.style.display = 'block';
		objParent.className = 'MenuItemHover ArrowIcon';
		subMenuItemContainer.onmouseout = function() {
			objParent.Timeout = setTimeout(function() {
				subMenuItemContainer.style.display = 'none';
				objParent.className = 'MenuItem ArrowIcon';
				subMenuItemContainer.onmouseover = null;
				subMenuItemContainer.onmouseout = null;
			}, 1*10);
		}
		
		subMenuItemContainer.onmouseover = function() {
			objParent.className = 'MenuItemHover ArrowIcon';
			if(objParent.Timeout) clearTimeout(objParent.Timeout);
			objParent.Timeout =  null;
		}

		obj.onmouseout = subMenuItemContainer.onmouseout;
		subMenuItemContainer.style.width = subMenuItemContainer.offsetWidth - parseInt(DOMUtil.getStyle(subMenuItemContainer, "borderLeftWidth")) - parseInt(DOMUtil.getStyle(subMenuItemContainer, "borderRightWidth")) + 'px';
		subMenuItemContainer.style.left = objParent.offsetLeft + objParent.offsetWidth + 'px';
		subMenuItemContainer.style.top =  eXo.core.Browser.findPosYInContainer(objParent,subMenuItemContainer.offsetParent) + 'px';
	}
}