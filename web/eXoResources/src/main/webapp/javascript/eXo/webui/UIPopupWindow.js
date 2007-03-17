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

	this.superClass.setAlign(popup) ;
	
	var popupBar = eXo.core.DOMUtil.findFirstDescendantByClass(popup, 'div' ,'PopupTitle') ;

	popupBar.onmousedown = this.superClass.initDND ;
	
	if(isShow == false) this.superClass.hide(popup) ; 
	var popupCloseButton = eXo.core.DOMUtil.findFirstDescendantByClass(popup, 'div' ,'CloseButton') ;
	popupCloseButton.onmouseup = function() {
		eXo.core.DOMUtil.findAncestorByClass(this, "UIDragObject").style.display = "none" ;
	}
	popup.style.visibility = "visible" ;
	if(isShow == true) this.superClass.show(popup);
//	alert(popupId +" : "+popup+" : "+isShow);
};

UIPopupWindow.prototype.show = function(popupId, isShow) {
	var popup = document.getElementById(popupId) ;
	popup.style.visibility = "hidden";
	this.superClass.show(popup) ;
	this.superClass.setAlign(popup)
	popup.style.visibility = "visible";
};

eXo.webui.UIPopupWindow = new UIPopupWindow();