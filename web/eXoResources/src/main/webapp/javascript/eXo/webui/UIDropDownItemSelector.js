function UIDropDownItemSelector() {
};

UIDropDownItemSelector.prototype.init = function() {
	this.itemSelectors = new Array();
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
	if (!e) var e = window.event;
	e.cancelBubble = true;
	if(e.stopPropagation) e.stopPropagation();
	var DOMUtil = eXo.core.DOMUtil;
	var UISelector = eXo.webui.UIDropDownItemSelector;
	var itemSelector = DOMUtil.findAncestorByClass(itemBar, "UIDropDownItemSelector");
	var list = DOMUtil.findFirstDescendantByClass(itemSelector, "div", "UIItemList");
	if (itemSelector.open == null) UISelector.initSelector(itemSelector);
	if (!itemSelector.open) {
		itemSelector.open = true;
		list.style.position = "absolute";
		list.style.width = itemSelector.offsetWidth;
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
	targ.title = itemLabel.innerHTML + " Portlet";
};

UIDropDownItemSelector.prototype.mouseOutItem = function(e) {
	var targ = eXo.core.Browser.getEventSource(e);
	
	while (targ.className != "ItemSelector" && targ.className != "OverItemSelector")
		targ = targ.parentNode;
	targ.className = targ.oldClassName;
};

UIDropDownItemSelector.prototype.clickItem = function(e) {
	var i;
	var DOMUtil = eXo.core.DOMUtil;
	var targ = eXo.core.Browser.getEventSource(e);
	
	while (targ.className != "ItemSelector" && targ.className != "OverItemSelector")
		targ = targ.parentNode;
	var parentSelector = DOMUtil.findAncestorByClass(targ, "UIDropDownItemSelector");
	var selectedItemLabel = DOMUtil.findFirstDescendantByClass(parentSelector, "div", "SelectedItemLabel");
	var previousSelected = DOMUtil.findDescendantsByClass(parentSelector, "div", "OverItemSelector");
	for (i = 0; i < previousSelected.length; i++) {
		 previousSelected[i].className = previousSelected[i].oldClassName = "ItemSelector";
	}
	var itemLabel = DOMUtil.findFirstDescendantByClass(targ, "div", "ItemSelectorLabel");
	selectedItemLabel.innerHTML = itemLabel.innerHTML;
	targ.className = targ.oldClassName = "OverItemSelector";
	eXo.webui.UIDropDownItemSelector.hideList(parentSelector);
	
	if(eXo.env.server.onServer == true) {
	  var params = [
	  	{name: "lable", value : itemLabel.id}
	  ] ;
		ajaxGet(eXo.env.server.createPortalURL("UIPortal", itemLabel.title, true, params)) ;
	} else {
		var itemSelectorAncestor = DOMUtil.findAncestorByClass(parentSelector, "ItemSelectorAncestor") ;
		var itemList = DOMUtil.findDescendantsByClass(itemSelectorAncestor, "div", "ItemList") ;
		var itemSelectorLabel = DOMUtil.findDescendantsByClass(itemSelectorAncestor, "div", "ItemSelectorLabel") ;
		if(itemList != null) {
			for(i = 0; i < itemSelectorLabel.length; ++i){
			if(i< itemList.length){
				if(itemLabel == itemSelectorLabel[i]) {
						itemList[i].style.display = "block";
					} else {
				    itemList[i].style.display = "none";
				  }
				}
			}
		}
	}
};

eXo.webui.UIDropDownItemSelector = new UIDropDownItemSelector();