function UIPopupSelectCategory() {
} ;

UIPopupSelectCategory.prototype.show = function(selectedElement, width) {
	selectedElement.style.position = "static" ;
	var ancestorPopupCategory = eXo.core.DOMUtil.findAncestorByClass(selectedElement, "AncestorPopupCategory") ;
	var categoryDetectPosition = eXo.core.DOMUtil.findAncestorByClass(selectedElement, "CategoryDetectPosition") ;
	var ControlCategory = eXo.core.DOMUtil.findFirstDescendantByClass(ancestorPopupCategory, "div", "ControlIcon") ;
  var uiPopupCategory = eXo.core.DOMUtil.findFirstDescendantByClass(ancestorPopupCategory, "div", "UIPopupCategory") ;
  
	if(uiPopupCategory == null) return;
	selectedElement.style.position = "relative" ;
			
	if(uiPopupCategory.style.display == "none") {
		ancestorPopupCategory.style.position = "relative" ;
		
		uiPopupCategory.style.position = "absolute" ;
		uiPopupCategory.style.display = "block" ;
		uiPopupCategory.style.top = "22px" ;
		uiPopupCategory.style.width = width + "px" ;
		if(ControlCategory != null) {
			var ancestorwidth = categoryDetectPosition.offsetWidth;
			uiPopupCategory.style.right = (ancestorwidth - 30) + "px";
		}
	} else {
		ancestorPopupCategory.style.position = "static" ;
		uiPopupCategory.style.display = "none" ;
	}
	 var uiWindow = eXo.core.DOMUtil.findAncestorByClass(selectedElement, "UIWindow") ;
	 var uiRowContainer = eXo.core.DOMUtil.findAncestorByClass(uiWindow, "UIRowContainer") ;
	 if(uiRowContainer != null) {
	 		uiRowContainer.style.height = uiWindow.offsetHeight + "px" ;
	 }
} ;

UIPopupSelectCategory.prototype.selectedCategoryIndex = function(selectedElement) {
	var parentNode = selectedElement.parentNode ;
	var categoryItems = eXo.core.DOMUtil.findChildrenByClass(parentNode, "div", "CategoryItem") ;
	for(var i =0; i < categoryItems.length; i++) {
		if(categoryItems[i] == selectedElement) return i ;
	}
} ;

UIPopupSelectCategory.prototype.selectdCategory = function(selectedElement) {
	selectedElement.className = "CategoryItem" ;
	var ancestorPopupCategory = eXo.core.DOMUtil.findAncestorByClass(selectedElement, "AncestorPopupCategory") ;
	var categoryItems = eXo.core.DOMUtil.findDescendantsByClass(ancestorPopupCategory, "div", "CategoryContainer") ;
	var selectedIndex = eXo.webui.UIPopupSelectCategory.selectedCategoryIndex(selectedElement);
	
	for(var i = 0 ;i < categoryItems.length; i++) {
		if(i != selectedIndex ) categoryItems[i].style.display = "none" ;
		else categoryItems[i].style.display = "block" ;
	}
	
	var uiPopupCategory = eXo.core.DOMUtil.findAncestorByClass(selectedElement, "UIPopupCategory") ;
	ancestorPopupCategory.style.position = "static" ;
	uiPopupCategory.style.display = "none" ;
} ;

UIPopupSelectCategory.prototype.onMouseOver = function(selectedElement, over) {
	if(over) {
		selectedElement.onmouseup = function() {
			var uiPopupCategory = eXo.core.DOMUtil.findAncestorByClass(selectedElement, "UIPopupCategory") ;
			uiPopupCategory.style.display = "none" ;
		};
		selectedElement.className = "CategoryItem OverCategoryItem" ;
	} else {
		selectedElement.className = "CategoryItem" ;
	}
} ;

UIPopupSelectCategory.prototype.setDefault = function(selectedElement) {
	var ancestorPopupCategory = eXo.core.DOMUtil.findAncestorByClass(selectedElement, "AncestorPopupCategory") ;
	var uiPopupCategory = eXo.core.DOMUtil.findFirstDescendantByClass(ancestorPopupCategory, "div", "UIPopupCategory") ;

	uiPopupCategory.style.top = "22px" ;		
	ancestorPopupCategory.style.position = "absolute" ;
};

UIPopupSelectCategory.prototype.onMouseUp = function(selectedElement) {
	var uiPopupCategory = eXo.core.DOMUtil.findAncestorByClass(selectedElement, "UIPopupCategory") ;
	uiPopupCategory.style.display = "none" ;
} ;

eXo.webui.UIPopupSelectCategory = new UIPopupSelectCategory() ;
