/*### Created by: Duy Tu  ###*/
UIVerticalScroller = function () {} ;

UIVerticalScroller.prototype.init = function() {
	eXo.gadget.UIGadget.resizeContainer() ;
	eXo.webui.UIVerticalScroller.refreshScroll(0) ;
} ;

UIVerticalScroller.prototype.refreshScroll = function(id) {
  var DOMUtil =  eXo.core.DOMUtil ;
	var container = document.getElementById("UIWorkspaceContainer") ;
	if((container.style.display != "block") || !container) return ;
	var itemContainer = document.getElementById("UIWidgets") ;
	if(itemContainer == null) return;
	var items = DOMUtil.findDescendantsByClass(itemContainer, "div", "UIGadget") ;
	if(items == null || items.length < 1) return ;
	var scrollZone = DOMUtil.findFirstDescendantByClass(itemContainer, "div", "ScrollZone") ;
	var widgetNavigator = DOMUtil.findFirstDescendantByClass(container, "div", "WidgetNavigator") ;
	var iconButton = DOMUtil.findDescendantsByClass(widgetNavigator, "div", "Icon") ;
	var downButton = iconButton[1] ;
	var upButton = iconButton[2] ;
	var itemSize = items.length ;
	var index = 0 ;
	for(var i = 0 ; i < itemSize ; ++i) {
		if(items[i].style.display == "block") {
			index = i ;
			break;
		}
	}
	if(index-id > itemSize-1 || index == itemSize-1) downButton.className = "Icon DisableScrollDownButton" ;
	if(index == 0) {
		upButton.className = "Icon DisableScrollUpButton" ;
		if(id == 1) return ;
	}
	if(itemSize <= 0) return;
	for(var i = index; i < itemSize ; ++i) {
		items[i].style.display = "block" ;
	}
	var maxHeight = scrollZone.offsetHeight + 10 ;
	var itemsHeight = 0 ;
	var tmp = 0 ;
	var maxIndex = 0 ;
	var temp = items[index].offsetHeight ;
	if(id < 0) {
		items[index].style.display = "none" ;
	} else {
		items[index-id].style.display = "block" ;
	}
	if(index < 0) index = 0 ;
	for(var i = index - id ; i < itemSize ; ++i) {
		tmp = items[i].offsetHeight ;
		/* TODO: fix Height for Widgets when onload
		 * */
		 if(tmp > 0 && tmp < 209) {tmp = 229;}
		 if(DOMUtil.findFirstDescendantByClass(items[i], "div", "UIWelcomeWidget")) {
				tmp = 146;
		 }
		itemsHeight += tmp ;
		if(itemsHeight > maxHeight) {
			items[i].style.display = "none" ;
		} else {
			maxIndex = i ;
		}
	}
	if(maxIndex == (itemSize-1)) downButton.className = "Icon DisableScrollDownButton" ;
	else downButton.className = "Icon ScrollDownButton" ;
	if(maxHeight >= itemsHeight + temp) {
		if(id < 0) {
			items[index].style.display = "block" ;
			downButton.className = "Icon DisableScrollDownButton" ;
			if(index == 0) index = -1 ;
		}
	}
	if(id < 0) ++index ;
	else --index ;
	if(index > 0) upButton.className = "Icon ScrollUpButton" ;
	else upButton.className = "Icon DisableScrollUpButton" ;
} ;

eXo.webui.UIVerticalScroller = new UIVerticalScroller() ;