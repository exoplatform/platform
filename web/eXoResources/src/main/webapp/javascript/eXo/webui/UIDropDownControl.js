function UIDropDownControl() {
	
} ;

UIDropDownControl.prototype.init = function(id) {
//	var MEM = eXo.core.MouseEventManager ;
//	MEM.addMouseDownHandler(id,eXo.webui.UIDropDownControl.test) ;
};

UIDropDownControl.prototype.add = function() {
		
} ;

UIDropDownControl.prototype.remove = function() {
		
} ;

UIDropDownControl.prototype.show = function(obj, evt) {
	evt.cancelBubble = true ;
	var DOMUtil = eXo.core.DOMUtil ;
	var itemContainer = DOMUtil.findNextElementByTagName(obj, 'div') ;
	if (itemContainer) {
		if (itemContainer.style.display == "none") itemContainer.style.display = "block" ;
		else itemContainer.style.display = "none" ;
	}
	DOMUtil.listHideElements(itemContainer) ;
} ;

UIDropDownControl.prototype.hide = function(obj) {
	if (typeof(obj) == "string") obj = document.getElementById(obj) ;
	obj.style.display = "none" ;		
} ;

UIDropDownControl.prototype.selectItem = function(obj,callback) {
	var DOMUtil = eXo.core.DOMUtil ;
	var uiDropDownAnchor = DOMUtil.findAncestorByClass(obj, 'UIDropDownAnchor') ;
	var uiDropDownAnchor = DOMUtil.findAncestorByClass(obj, 'UIDropDownAnchor') ;
	var uiDropDownTitle = DOMUtil.findPreviousElementByTagName(uiDropDownAnchor, 'div') ;
	var uiDropDownMiddleTitle = DOMUtil.findFirstDescendantByClass(uiDropDownTitle,'div','UIDropDownMiddleTitle') ;
	uiDropDownMiddleTitle.firstChild.innerHTML = obj.innerHTML ;	
	if (callback){		
		eval(callback) ;
	}
	DOMUtil.listHideElements(uiDropDownAnchor) ;
} ;
UIDropDownControl.prototype.test = function() {
	
}

eXo.webui.UIDropDownControl = new UIDropDownControl() ;