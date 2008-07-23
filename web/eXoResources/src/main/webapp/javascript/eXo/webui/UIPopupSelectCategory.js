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

eXo.webui.UIPopupSelectCategory = new UIPopupSelectCategory() ;
