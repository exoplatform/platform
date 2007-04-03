function UIDropDownItemSelector() {
};

UIDropDownItemSelector.prototype.init = function() {
	var UISelector = eXo.webui.UIDropDownItemSelector;
	this.itemSelectors = eXo.core.DOMUtil.findDescendantsByClass(document, "div", "UIDropDownItemSelector");
	for (var i=0; i<this.itemSelectors.length; i++) {
		var itemSelector = this.itemSelectors[i];
		itemSelector.open = false;
		var menuIcon = eXo.core.DOMUtil.findFirstDescendantByClass(itemSelector, "div", "DropDownSelectIcon");
		menuIcon.onclick = UISelector.showList;
		var items = eXo.core.DOMUtil.findDescendantsByClass(itemSelector, "div", "ItemSelector");
		for (var j=0; j<items.length; j++) {
			var item = items[j];
			item.style.cursor = "pointer";
			item.onmouseover = function(e) {
				this.style.backgroundColor = "#ddd";
			};
			item.onmouseout = function(e) {
				this.style.backgroundColor = "transparent";
			}
			item.onclick = function(e) {
				var selectedItemLabel = eXo.core.DOMUtil.findFirstDescendantByClass(itemSelector, "div", "SelectedItemLabel");
				selectedItemLabel.innerHTML = this.innerHTML;
				UISelector.hideList(itemSelector);
			};
		}
	}
};

UIDropDownItemSelector.prototype.showList = function(e) {
	var targ;
	if (!e) var e = window.event;
	if (e.target) targ = e.target;
	else if (e.srcElement) targ = e.srcElement;
	if (targ.nodeType == 3) // defeat Safari bug
		targ = targ.parentNode;
		
	var UISelector = eXo.webui.UIDropDownItemSelector;
	var itemSelector = eXo.core.DOMUtil.findAncestorByClass(targ, "UIDropDownItemSelector");
	var listContainer = eXo.core.DOMUtil.findFirstDescendantByClass(itemSelector, "div", "UIItemList");
	if (!itemSelector.open) {
		itemSelector.open = true;
		listContainer.style.display = "block";
		listContainer.style.border = "solid red 1px";
		listContainer.style.position = "absolute";
	} else {
		itemSelector.open = false;
		listContainer.style.display = "none";
	}
};

UIDropDownItemSelector.prototype.hideList = function(element) {
	element.open = false;
	var listContainer = eXo.core.DOMUtil.findFirstDescendantByClass(element, "div", "UIItemList");
	listContainer.style.display = "none";
}

eXo.webui.UIDropDownItemSelector = new UIDropDownItemSelector();