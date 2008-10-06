function UIDropDownControl() {} ;

UIDropDownControl.prototype.init = function(id) {
	//var popup = document.getElementById(id) ;
	//return popup;
};

UIDropDownControl.prototype.selectItem = function(method, id, selectedIndex) {
	if(method)	method(id, selectedIndex) ;
} ;

/*.
 * minh.js.exo
 */

UIDropDownControl.prototype.show = function(obj, evt) {
	if(!evt) evt = window.event ;
	evt.cancelBubble = true ;
	
	var DOMUtil = eXo.core.DOMUtil ;
	var Browser = eXo.core.Browser ;
	var dropDownAnchor = DOMUtil.findNextElementByTagName(obj, 'div') ;	
	if (dropDownAnchor) {
		if (dropDownAnchor.style.display == "none") {
			dropDownAnchor.style.display = "block" ;
			var middleCont = DOMUtil.findFirstDescendantByClass(dropDownAnchor, "div", "MiddleItemContainer") ;
			var topCont = DOMUtil.findPreviousElementByTagName(middleCont, "div") ;
			var bottomCont = DOMUtil.findNextElementByTagName(middleCont, "div") ;
			middleCont.style.height = "auto";
			var visibleHeight = Browser.getBrowserHeight() - Browser.findPosY(middleCont) - 45 ;
			var scrollHeight = middleCont.scrollHeight ;
			if(scrollHeight > visibleHeight) {
				middleCont.style.height = visibleHeight + "px" ;
				topCont.style.display = "block" ;
				bottomCont.style.display = "block" ;
			} else {
				topCont.style.display = "none" ;
				bottomCont.style.display = "none" ;
			}
		}
		else dropDownAnchor.style.display = "none" ;
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
