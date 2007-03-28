eXo.require('eXo.webui.UIPopup');

function UIPopupWindow() {	
};

UIPopupWindow.prototype.init = function(popupId, isShow) {
	this.superClass = eXo.webui.UIPopup ;
	var popup = document.getElementById(popupId) ;
	if(popup == null) return;
	
	popup.style.visibility = "hidden" ;
	this.superClass.init(popup) ;
	
	var contentBlock = eXo.core.DOMUtil.findFirstDescendantByClass(popup, 'div' ,'Content');
	if((eXo.core.Browser.getBrowserHeight() - 100 ) < contentBlock.offsetHeight) {
		contentBlock.style.height = (eXo.core.Browser.getBrowserHeight() - 100) + "px";
	}

	var popupBar = eXo.core.DOMUtil.findFirstDescendantByClass(popup, 'div' ,'PopupTitle') ;

	popupBar.onmousedown = this.superClass.initDND ;
	
	if(isShow == false) this.superClass.hide(popup) ; 
	var popupCloseButton = eXo.core.DOMUtil.findFirstDescendantByClass(popup, 'div' ,'CloseButton') ;
	popupCloseButton.onmouseup = function() {
		eXo.core.DOMUtil.findAncestorByClass(this, "UIDragObject").style.display = "none" ;
	}
	popup.style.visibility = "visible" ;
	if(isShow == true) this.show(popup);
};

UIPopupWindow.prototype.show = function(popup) {
	if(typeof(popup) == "string") popup = document.getElementById(popup) ;
	popup.style.visibility = "hidden";
	this.superClass.show(popup) ;
	var offsetParent = popup.offsetParent ;
	if(offsetParent) {
		popup.style.top = ((offsetParent.offsetHeight - popup.offsetHeight) / 2) + "px" ;
		popup.style.left = ((offsetParent.offsetWidth - popup.offsetWidth) / 2) + "px" ;
	}
	popup.style.visibility = "visible";
};

eXo.webui.UIPopupWindow = new UIPopupWindow();