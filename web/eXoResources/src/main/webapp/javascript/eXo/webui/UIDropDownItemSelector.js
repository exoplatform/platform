function UIDropDownItemSelector() {
};

UIDropDownItemSelector.prototype.init = function() {
	this.itemSelectors = new Array();
	this.onload();
};

UIDropDownItemSelector.prototype.initSelector = function(selector) {
	var UISelector = eXo.webui.UIDropDownItemSelector;
	var items = eXo.core.DOMUtil.findDescendantsByClass(selector, "div", "ItemSelector");
	for (var j = 0; j < items.length; j++) {
		var item = items[j];
		item.onmouseover = UISelector.mouseOverItem;
		item.onmouseout = UISelector.mouseOutItem;
		item.onclick = UISelector.clickItem;
	}
	selector.open = false;
	UISelector.itemSelectors.push(selector);
};

UIDropDownItemSelector.prototype.showList = function(itemBar, e) {
	if (!e) e = window.event;
	e.cancelBubble = true;
	if(e.stopPropagation) e.stopPropagation();
	var DOMUtil = eXo.core.DOMUtil;
	var UISelector = eXo.webui.UIDropDownItemSelector;
	var itemSelector = DOMUtil.findAncestorByClass(itemBar, "UIDropDownItemSelector");
	var list = DOMUtil.findFirstDescendantByClass(itemSelector, "div", "UIItemList");
	if (itemSelector.open == null) UISelector.initSelector(itemSelector);
	if (list.style.display == "none") {
	  list.style.width = itemSelector.offsetWidth + "px";
		itemSelector.open = true;
		list.style.position = "absolute";
		list.style.display = "block";
	} else {
		UISelector.hideList(itemSelector);
	}
	/*#######################-Hide popup when click anywhere on document-#########################*/		
	eXo.core.DOMUtil.listHideElements(list);
};

UIDropDownItemSelector.prototype.hideList = function(selector) {
	selector.open = false;
	var listContainer = eXo.core.DOMUtil.findFirstDescendantByClass(selector, "div", "UIItemList");
	listContainer.style.display = "none";
};

UIDropDownItemSelector.prototype.mouseOverItem = function(e) {
	var targ = eXo.core.Browser.getEventSource(e);

	while (targ.className != "ItemSelector" && targ.className != "OverItemSelector") {
		targ = targ.parentNode;
	}
	targ.oldClassName = targ.className;
	targ.className = "OverItemSelector";
	var itemLabel = eXo.core.DOMUtil.findFirstDescendantByClass(targ, "div", "ItemSelectorLabel");
	targ.title = itemLabel.innerHTML;
};

UIDropDownItemSelector.prototype.mouseOutItem = function(e) {
	var targ = eXo.core.Browser.getEventSource(e);
	
	while (targ.className != "ItemSelector" && targ.className != "OverItemSelector") {
		targ = targ.parentNode;
	}
	targ.className = targ.oldClassName;
};

UIDropDownItemSelector.prototype.clickItem = function(e, targetComponentId, actionName) {
	var i;
	var DOMUtil = eXo.core.DOMUtil;
	var targ = eXo.core.Browser.getEventSource(e);
	
	while (targ.className != "ItemSelector" && targ.className != "OverItemSelector") {
		targ = targ.parentNode;
	}
	var parentSelector = DOMUtil.findAncestorByClass(targ, "UIDropDownItemSelector");
	var selectedItemLabel = DOMUtil.findFirstDescendantByClass(parentSelector, "div", "SelectedItemLabel");
	var previousSelected = DOMUtil.findDescendantsByClass(parentSelector, "div", "OverItemSelector");
	for (i = 0; i < previousSelected.length; i++) {
		previousSelected[i].className = previousSelected[i].oldClassName = "ItemSelector";
	}
	var itemLabel = DOMUtil.findFirstDescendantByClass(targ, "div", "ItemSelectorLabel");
	var strItemLabel = itemLabel.innerHTML;
	selectedItemLabel.innerHTML = strItemLabel;
	if(strItemLabel.length > 20) {
		eXo.webui.UIDropDownItemSelector.onload();
	} 
	targ.className = targ.oldClassName = "OverItemSelector";
	eXo.webui.UIDropDownItemSelector.hideList(parentSelector);
	
	if(this.getAttribute("onServer") == "true") {
	  var params = [
	  	{name: "objectId", value : this.getAttribute("itemId")}
	  ] ;
		ajaxGet(eXo.env.server.createPortalURL(this.getAttribute("targetParent"), this.getAttribute("action"), true, params)) ;
		return;
	}
	
	var itemSelectorAncestor = DOMUtil.findAncestorByClass(parentSelector, "ItemSelectorAncestor") ;
	var itemList = DOMUtil.findDescendantsByClass(itemSelectorAncestor, "div", "ItemList") ;
	var itemSelectorLabel = DOMUtil.findDescendantsByClass(itemSelectorAncestor, "div", "ItemSelectorLabel") ;
	
	if(itemList == null) return;
	for(i = 0; i < itemSelectorLabel.length; ++i) {
		if(i >= itemList.length) continue;
		if(itemLabel == itemSelectorLabel[i]) {
			itemList[i].style.display = "block";
			
		} else {
			itemList[i].style.display = "none";
		}
	}
};

/** Created: by Duy Tu **/
UIDropDownItemSelector.prototype.onload = function() {
	var DOMUtil = eXo.core.DOMUtil;
	var uiDropDownItemSelector = document.getElementById("UIDropDownItemSelector");
	var selectedItemLabel = DOMUtil.findFirstDescendantByClass(uiDropDownItemSelector, "div", "SelectedItemLabel");
	var strLabel = selectedItemLabel.innerHTML;
	//alert(strLabel + " :  " + strLabel.length);
	while(strLabel.charCodeAt(0) <= 32){
		var tmp = strLabel.charAt(1); 
		for(var i = 2; i < strLabel.length; ++i){
			tmp = tmp + strLabel.charAt(i);
		}
		strLabel = tmp;
	}
	while(strLabel.charCodeAt(strLabel.length - 1) <= 32){
		var tmp = strLabel.charAt(0); 
		for(var i = 1; i<strLabel.length - 1; ++i){
			tmp = tmp + strLabel.charAt(i);
		}
		strLabel = tmp;
	}

	if(strLabel.length > 20){
		var strlabel = strLabel.charAt(0); 
		for(var i = 1;i < 17; ++i){
	    strlabel = strlabel + strLabel.charAt(i);
		}
		selectedItemLabel.innerHTML = strlabel + "...";
	} else {
		selectedItemLabel.innerHTML = strLabel;
	}
};

eXo.webui.UIDropDownItemSelector = new UIDropDownItemSelector();