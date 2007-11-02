function UIDropDownControl() {} ;

UIDropDownControl.prototype.init = function(id) {
	//var popup = document.getElementById(id) ;
	//return popup;
};

// create by: Dang.Tung
UIDropDownControl.prototype.selectPageLayout = function(param,id) {
	var DOMUtil = eXo.core.DOMUtil ;
	var uiDropDownControl = document.getElementById(id);
	var itemSelectorAncestor = DOMUtil.findAncestorByClass(uiDropDownControl, "ItemSelectorAncestor") ;
	var itemList = DOMUtil.findDescendantsByClass(itemSelectorAncestor, "div", "ItemList") ;
	var itemSelectorLabel = DOMUtil.findDescendantsByClass(itemSelectorAncestor, "a", "OptionItem") ;
	var uiItemSelector = DOMUtil.findAncestorByClass(uiDropDownControl, "UIItemSelector");
	var itemDetailList = DOMUtil.findDescendantsByClass(uiItemSelector, "div", "ItemDetailList") ;
	if(itemList == null) return;
	for(i = 0; i < itemSelectorLabel.length; ++i) {
			if(i >= itemList.length) continue;
			if(param == itemSelectorLabel[i].innerHTML) {
				itemList[i].style.display = "block";
				if(itemDetailList.length < 1)  continue;
			  itemDetailList[i].style.display = "block";
				var selectedItem = DOMUtil.findFirstDescendantByClass(itemList[i], "div", "SelectedItem");
				if(selectedItem == null) continue;
				var setValue = DOMUtil.findDescendantById(selectedItem, "SetValue");
				if(setValue == null) continue;
				eval(setValue.innerHTML);
			} else {
				itemList[i].style.display = "none";
				if(itemDetailList.length > 0) itemDetailList[i].style.display = "none";
			}
		}
} ;

UIDropDownControl.prototype.selectItem = function(method, param,id) {
	if(method)	method(param,id) ;
} ;

/*.
 * minh.js.exo
 */

UIDropDownControl.prototype.show = function(obj, evt) {
	if(!evt) evt = window.event ;
	evt.cancelBubble = true ;
	
	var DOMUtil = eXo.core.DOMUtil ;
	var itemContainer = DOMUtil.findNextElementByTagName(obj, 'div') ;	
	if (itemContainer) {
		if (itemContainer.style.display == "none") itemContainer.style.display = "block" ;
		else itemContainer.style.display = "none" ;
	}
} ;

UIDropDownControl.prototype.hide = function(obj) {
	if (typeof(obj) == "string") obj = document.getElementById(obj) ;
	obj.style.display = "none" ;		
} ;

UIDropDownControl.prototype.onclickEvt = function(obj) {
	var DOMUtil = eXo.core.DOMUtil ;
	var uiDropDownAnchor = DOMUtil.findAncestorByClass(obj, 'UIDropDownAnchor') ;
	var uiDropDownTitle = DOMUtil.findPreviousElementByTagName(uiDropDownAnchor, 'div') ;
	var uiDropDownMiddleTitle = DOMUtil.findFirstDescendantByClass(uiDropDownTitle,'div','DropDownSelectLabel') ;
	uiDropDownMiddleTitle.innerHTML = obj.innerHTML ;
	uiDropDownAnchor.style.display = 'none' ;
} ;

eXo.webui.UIDropDownControl = new UIDropDownControl() ;
