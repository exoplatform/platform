function UIPopupSelectCategory() {
} ;

UIPopupSelectCategory.prototype.show = function(selectedElement, width, e) {
	if(!e) var e = window.event;
	e.cancelBubble = true;
	if(e.stopPropagation) e.stopPropagation();
	
	var ancestorPopupCategory = eXo.core.DOMUtil.findAncestorByClass(selectedElement, "AncestorPopupCategory") ;
	var categoryDetectPosition = eXo.core.DOMUtil.findAncestorByClass(selectedElement, "CategoryDetectPosition") ;
	var ControlCategory = eXo.core.DOMUtil.findFirstDescendantByClass(ancestorPopupCategory, "div", "ControlIcon") ;
  var uiPopupCategory = eXo.core.DOMUtil.findFirstDescendantByClass(ancestorPopupCategory, "div", "UIPopupCategory") ;
  var uiDesktop = eXo.core.DOMUtil.findAncestorByClass(ancestorPopupCategory, "UIPageDesktop");
  
	if(uiPopupCategory == null) return;

	if(uiPopupCategory.style.display == "none") {
		uiPopupCategory.style.position = "absolute" ;
		uiPopupCategory.style.display = "block" ;
		var posTop = eXo.core.Browser.findPosY(ancestorPopupCategory) + 22;
		if (uiDesktop != null) posTop -= ancestorPopupCategory.offsetTop;
		uiPopupCategory.style.top = posTop + "px" ;
		uiPopupCategory.style.width = width + "px" ;

		if(ControlCategory != null) {
			var posLeft = eXo.core.Browser.findPosX(categoryDetectPosition) - width + 35;
			if (uiDesktop != null) posLeft -= ancestorPopupCategory.offsetLeft;
			if (eXo.portal.UIControlWorkspace.showControlWorkspace) posLeft -= eXo.portal.UIControlWorkspace.defaultWidth;
			//if (eXo.core.Browser.isIE() && !eXo.core.Browser.isIE6()) posLeft -= eXo.portal.UIControlWorkspace.defaultWidth;
			uiPopupCategory.style.left = posLeft + "px";
		}
	} else {
		uiPopupCategory.style.display = "none" ;
	}
	
	var uiWindow = eXo.core.DOMUtil.findAncestorByClass(selectedElement, "UIWindow") ;
	if(uiWindow != null) {
		var uiRowContainer = eXo.core.DOMUtil.findAncestorByClass(uiWindow, "UIRowContainer") ;
		if(uiRowContainer != null) {
				uiRowContainer.style.height = uiWindow.offsetHeight + "px" ;
			}
		} 
	/*Add uiPopupCategory to the list element will be display to "none" when click on document*/
	eXo.core.DOMUtil.listHideElements(uiPopupCategory);
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
