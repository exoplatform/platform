function UIPopupSelectCategory() {
} ;

/*
 * minh.js.exo
 */
UIPopupSelectCategory.prototype.show = function(selectedElement, width, e) {
	if(!e) e = window.event ;
	e.cancelBubble = true ;
	if(e.stopPropagation) e.stopPropagation() ;
	var DOMUtil = eXo.core.DOMUtil ;
	var categoryDetectPosition = selectedElement.parentNode ;
	var ancestorPopupCategory = DOMUtil.findAncestorByClass(selectedElement, "AncestorPopupCategory") ;
  var uiPopupCategory = DOMUtil.findFirstDescendantByClass(ancestorPopupCategory, "div", "UIPopupCategory") ;
	if(uiPopupCategory == null) return ;
	if(uiPopupCategory.style.display != "block") {
		uiPopupCategory.style.position = "absolute" ;
		uiPopupCategory.style.display = "block" ;
		uiPopupCategory.style.width = width + "px" ;
	var posLeft = -categoryDetectPosition.clientWidth - width + 38 ;
	uiPopupCategory.style.left = posLeft + "px" ;
	uiPopupCategory.style.top = -5 + "px" ;
	} else {
		uiPopupCategory.style.display = "none" ;
	}
	eXo.core.DOMUtil.listHideElements(uiPopupCategory) ;
} ;

UIPopupSelectCategory.prototype.selectedCategoryIndex = function(selectedElement) {
	var parentNode = selectedElement.parentNode ;
	var categoryItems = eXo.core.DOMUtil.findChildrenByClass(parentNode, "div", "CategoryItem") ;
	for (var i = 0; i < categoryItems.length; i++) {
		if (categoryItems[i] == selectedElement) return i ;
	}
} ;

UIPopupSelectCategory.prototype.selectdCategory = function(selectedElement) {
	selectedElement.className = "CategoryItem" ;
	var ancestorPopupCategory = eXo.core.DOMUtil.findAncestorByClass(selectedElement, "AncestorPopupCategory") ;
	var categoryItems = eXo.core.DOMUtil.findDescendantsByClass(ancestorPopupCategory, "div", "CategoryContainer") ;
	var selectedIndex = eXo.webui.UIPopupSelectCategory.selectedCategoryIndex(selectedElement) ;
	
	for(var i = 0 ;i < categoryItems.length; i++) {
		if(i != selectedIndex ) categoryItems[i].style.display = "none" ;
		else categoryItems[i].style.display = "block" ;
	}
	
	var uiPopupCategory = eXo.core.DOMUtil.findAncestorByClass(selectedElement, "UIPopupCategory") ;
	ancestorPopupCategory.style.position = "static" ;
	uiPopupCategory.style.display = "none" ;

} ;

UIPopupSelectCategory.prototype.setDefault = function(selectedElement) {
	var ancestorPopupCategory = eXo.core.DOMUtil.findAncestorByClass(selectedElement, "AncestorPopupCategory") ;
	var uiPopupCategory = eXo.core.DOMUtil.findFirstDescendantByClass(ancestorPopupCategory, "div", "UIPopupCategory") ;

	uiPopupCategory.style.top = "22px" ;		
	ancestorPopupCategory.style.position = "absolute" ;
};

eXo.webui.UIPopupSelectCategory = new UIPopupSelectCategory() ;
