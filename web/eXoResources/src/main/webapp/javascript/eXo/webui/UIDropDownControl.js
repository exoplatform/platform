function UIDropDownControl() {} ;

UIDropDownControl.prototype.init = function(id) {
//	alert("param : " + id) ;
};

UIDropDownControl.prototype.selectItem = function(clickedElemt, method, param) {
	if(method) method(param) ;
} ;

/*
 * minh.js.exo
 */

UIDropDownControl.prototype.show = function(obj, evt) {
	if(!evt) evt = window.event ;
	evt.cancelBubble = true ;
	
	var DOMUtil = eXo.core.DOMUtil ;
	var itemContainer = DOMUtil.findNextElementByTagName(obj, 'div') ;
	var editIcon = DOMUtil.findPreviousElementByTagName(itemContainer.parentNode, 'div');
	var uiPopupControl = DOMUtil.findFirstDescendantByClass(editIcon, 'div', 'UIPopupControl') ;
	if(uiPopupControl.style.display == "block") {
		uiPopupControl.style.display = "none" ;
	}	
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
	var uiDropDownMiddleTitle = DOMUtil.findFirstDescendantByClass(uiDropDownTitle,'div','UIDropDownMiddleTitle') ;
	uiDropDownMiddleTitle.firstChild.innerHTML = obj.innerHTML ;
	uiDropDownAnchor.style.display = 'none' ;
} ;

/* show PopupControl at right edit Icon
 * tungnd
 * */
UIDropDownControl.prototype.showPopupControl = function(obj, evt) {
	if(!evt) evt = window.event ;
	evt.cancelBubble = true ;
	var DOMUtil = eXo.core.DOMUtil ;
	var uiPopupControl = DOMUtil.findFirstDescendantByClass(obj, 'div', 'UIPopupControl') ;
	var tmpAnchor = DOMUtil.findNextElementByTagName(obj, 'div') ;
	var uiDropDownAnchor = DOMUtil.findFirstDescendantByClass(tmpAnchor, 'div', 'UIDropDownAnchor') ;
	if(uiDropDownAnchor.style.display == "block")
		uiDropDownAnchor.style.display = "none" ;
	if (!uiPopupControl) return ;	
	if(uiPopupControl.style.display == "none") {
		eXo.webui.UIDropDownControl.hide() ;
		uiPopupControl.style.display = "block" ;
		eXo.core.DOMUtil.listHideElements(uiPopupControl) ;	
	}	
	else uiPopupControl.style.display = "none" ;
} ;

/* hide element when onclick
 * tungnd
 * */
UIDropDownControl.prototype.hide = function() {
	var ln = eXo.core.DOMUtil.hideElementList.length ;
	if (ln > 0) {
		for (var i = 0 ; i < ln ; i++) {
			eXo.core.DOMUtil.hideElementList[i].style.display = "none" ;
		}
	}
} ;

eXo.webui.UIDropDownControl = new UIDropDownControl() ;