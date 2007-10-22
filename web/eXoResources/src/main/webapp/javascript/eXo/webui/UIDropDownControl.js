function UIDropDownControl() {} ;

UIDropDownControl.prototype.init = function(id) {
	//alert("param : " + id) ;
};

UIDropDownControl.prototype.selectItem = function(clickedElemt, method, param) {
	if(method) method(param) ;
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
	var uiDropDownMiddleTitle = DOMUtil.findFirstDescendantByClass(uiDropDownTitle,'div','UIDropDownMiddleTitle') ;
	uiDropDownMiddleTitle.firstChild.innerHTML = obj.innerHTML ;
	uiDropDownAnchor.style.display = 'none' ;
} ;

eXo.webui.UIDropDownControl = new UIDropDownControl() ;
function abc(parm) {
	alert(parm) ;
}
