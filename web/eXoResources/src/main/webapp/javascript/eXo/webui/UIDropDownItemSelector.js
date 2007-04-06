function UIDropDownItemSelector() {
};

UIDropDownItemSelector.prototype.init = function() {
	this.itemSelectors = new Array();
	document.onclick = eXo.webui.UIDropDownItemSelector.hideLists;
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
	var DOMUtil = eXo.core.DOMUtil;
	var UISelector = eXo.webui.UIDropDownItemSelector;
	var itemSelector = DOMUtil.findAncestorByClass(itemBar, "UIDropDownItemSelector");
	var list = DOMUtil.findFirstDescendantByClass(itemSelector, "div", "UIItemList");
	if (itemSelector.open == null) UISelector.initSelector(itemSelector);
	if (!itemSelector.open) {
		itemSelector.open = true;
		list.style.position = "absolute";
		list.style.width = DOMUtil.getStyle(itemSelector, "width");
		list.style.display = "block";
	} else {
		UISelector.hideList(itemSelector);
	}
	if (!e) var e = window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
};

UIDropDownItemSelector.prototype.hideList = function(selector) {
	selector.open = false;
	var listContainer = eXo.core.DOMUtil.findFirstDescendantByClass(selector, "div", "UIItemList");
	listContainer.style.display = "none";
};

UIDropDownItemSelector.prototype.mouseOverItem = function(e) {
	var targ = eXo.core.DOMUtil.getEventSource(e);

	while (targ.className != "ItemSelector" && targ.className != "OverItemSelector") {
		targ = targ.parentNode;
	}
	targ.oldClassName = targ.className;
	targ.className = "OverItemSelector";
};

UIDropDownItemSelector.prototype.mouseOutItem = function(e) {
	var targ = eXo.core.DOMUtil.getEventSource(e);
	
	while (targ.className != "ItemSelector" && targ.className != "OverItemSelector")
		targ = targ.parentNode;
	targ.className = targ.oldClassName;
};

UIDropDownItemSelector.prototype.clickItem = function(e) {
	var DOMUtil = eXo.core.DOMUtil;
	var targ = DOMUtil.getEventSource(e);
	
	while (targ.className != "ItemSelector" && targ.className != "OverItemSelector")
		targ = targ.parentNode;
	var parentSelector = DOMUtil.findAncestorByClass(targ, "UIDropDownItemSelector");
	var selectedItemLabel = DOMUtil.findFirstDescendantByClass(parentSelector, "div", "SelectedItemLabel");
	var previousSelected = DOMUtil.findDescendantsByClass(parentSelector, "div", "OverItemSelector");
	for (var i = 0; i < previousSelected.length; i++) {
		 previousSelected[i].className = previousSelected[i].oldClassName = "ItemSelector";
	}
	var itemLabel = DOMUtil.findFirstDescendantByClass(targ, "a", "ItemSelectorLabel");
	selectedItemLabel.innerHTML = itemLabel.innerHTML;
	targ.className = targ.oldClassName = "OverItemSelector";
	eXo.webui.UIDropDownItemSelector.hideList(parentSelector);
};

UIDropDownItemSelector.prototype.hideLists = function() {
	var UISelector = eXo.webui.UIDropDownItemSelector;
	for (var i = 0; i < UISelector.itemSelectors.length; i++) {
		var selector = UISelector.itemSelectors[i];
		var list = eXo.core.DOMUtil.findFirstDescendantByClass(selector, "div", "UIItemList");
		eXo.core.Browser.listItem.push(list);
		selector.open = false;
	}
	eXo.core.Browser.hideElements();
};

eXo.webui.UIDropDownItemSelector = new UIDropDownItemSelector();