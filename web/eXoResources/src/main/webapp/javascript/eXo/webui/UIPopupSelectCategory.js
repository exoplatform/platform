function UIPopupSelectCategory() {
} ;
UIPopupSelectCategory.prototype.hide = function() {
	var ln = eXo.core.DOMUtil.hideElementList.length ;
	if (ln > 0) {
		for (var i = 0 ; i < ln ; i++) {
			eXo.core.DOMUtil.hideElementList[i].style.display = "none" ;
		}
	}
} ;
UIPopupSelectCategory.prototype.show = function(obj, evt){
	if(!evt) evt = window.event ;
	evt.cancelBubble = true ;
	var DOMUtil = eXo.core.DOMUtil ;
	var uiPopupCategory = DOMUtil.findFirstDescendantByClass(obj, 'div', 'UIPopupCategory') ;	
	if (!uiPopupCategory) return ;	
	if(uiPopupCategory.style.display == "none") {
		eXo.webui.UIPopupSelectCategory.hide() ;
		uiPopupCategory.style.display = "block" ;
		eXo.core.DOMUtil.listHideElements(uiPopupCategory) ;	
	}	
	else uiPopupCategory.style.display = "none" ;
	
}


//
//UIPopupSelectCategory.prototype.show = function(selectedElement, width, e) {
//	if(!e) e = window.event ;
//	e.cancelBubble = true ;
//	if(e.stopPropagation) e.stopPropagation() ;
//	var DOMUtil = eXo.core.DOMUtil ;
//	var categoryDetectPosition = selectedElement.parentNode ;
//	var ancestorPopupCategory = DOMUtil.findAncestorByClass(selectedElement, "AncestorPopupCategory") ;
//  var uiPopupCategory = DOMUtil.findFirstDescendantByClass(ancestorPopupCategory, "div", "UIPopupCategory") ;
//	if(uiPopupCategory == null) return;
//	if(uiPopupCategory.style.display != "block") {
//		uiPopupCategory.style.position = "absolute" ;
//		uiPopupCategory.style.display = "block" ;
//		uiPopupCategory.style.width = width + "px" ;
//		var posLeft = eXo.core.Browser.findPosX(categoryDetectPosition) - width + selectedElement.offsetWidth + 28 ;
//		// 28 is distance between arrow and PopupCategoryRight
//		
//	  posLeft -= ancestorPopupCategory.offsetLeft ;
//		if(eXo.portal.UIControlWorkspace.showControlWorkspace) {
//			posLeft -= eXo.portal.UIControlWorkspace.defaultWidth ;
//		} else {
//			if(document.getElementById("UIControlWorkspace")) {
//				posLeft -= 5 ;
//			}
//		}
//		uiPopupCategory.style.left = posLeft + "px" ;
//	} else {
//		uiPopupCategory.style.display = "none" ;
//	}
//	
//	//Add uiPopupCategory to the list element will be display to "none" when click on document
//	eXo.core.DOMUtil.listHideElements(uiPopupCategory) ;
//} ;
//
//UIPopupSelectCategory.prototype.selectedCategoryIndex = function(selectedElement) {
//	var parentNode = selectedElement.parentNode ;
//	var categoryItems = eXo.core.DOMUtil.findChildrenByClass(parentNode, "div", "CategoryItem") ;
//	for (var i = 0; i < categoryItems.length; i++) {
//		if (categoryItems[i] == selectedElement) return i ;
//	}
//} ;
//
//UIPopupSelectCategory.prototype.selectdCategory = function(selectedElement) {
//	selectedElement.className = "CategoryItem" ;
//	var ancestorPopupCategory = eXo.core.DOMUtil.findAncestorByClass(selectedElement, "AncestorPopupCategory") ;
//	var categoryItems = eXo.core.DOMUtil.findDescendantsByClass(ancestorPopupCategory, "div", "CategoryContainer") ;
//	var selectedIndex = eXo.webui.UIPopupSelectCategory.selectedCategoryIndex(selectedElement) ;
//	
//	for(var i = 0 ;i < categoryItems.length; i++) {
//		if(i != selectedIndex ) categoryItems[i].style.display = "none" ;
//		else categoryItems[i].style.display = "block" ;
//	}
//	
//	var uiPopupCategory = eXo.core.DOMUtil.findAncestorByClass(selectedElement, "UIPopupCategory") ;
//	ancestorPopupCategory.style.position = "static" ;
//	uiPopupCategory.style.display = "none" ;
//
//} ;
//
////TODO : minh.js.exo
////UIPopupSelectCategory.prototype.onMouseOver = function(selectedElement, over) {
////	if(over) {
////		selectedElement.onclick = function() {
////			var uiPopupCategory = eXo.core.DOMUtil.findAncestorByClass(selectedElement, "UIPopupCategory") ;
////			uiPopupCategory.style.display = "none" ;
////		};
////		selectedElement.className = "CategoryItem OverCategoryItem" ;
////	} else {
////		selectedElement.className = "CategoryItem" ;
////	}
////} ;
//
//
//UIPopupSelectCategory.prototype.setDefault = function(selectedElement) {
//	var ancestorPopupCategory = eXo.core.DOMUtil.findAncestorByClass(selectedElement, "AncestorPopupCategory") ;
//	var uiPopupCategory = eXo.core.DOMUtil.findFirstDescendantByClass(ancestorPopupCategory, "div", "UIPopupCategory") ;
//
//	uiPopupCategory.style.top = "22px" ;		
//	ancestorPopupCategory.style.position = "absolute" ;
//};

eXo.webui.UIPopupSelectCategory = new UIPopupSelectCategory() ;
