eXo.require('eXo.webui.UIPopupMenu');

function UIExoStartMenu() {
	this.buttonClicked = false ;
} ;

UIExoStartMenu.prototype.init = function(popupMenu, container, x, y) {
	this.superClass = eXo.webui.UIPopupMenu;
	
	this.superClass.init(popupMenu, container.id, x, y) ;

	this.exoStartButton = eXo.core.DOMUtil.findFirstDescendantByClass(container, "div", "ExoStartButton") ;
	this.exoStartButton.onmouseover = eXo.portal.UIExoStartMenu.startButtonOver ;
	this.exoStartButton.onmouseout = eXo.portal.UIExoStartMenu.startButtonOut ;
} ;

UIExoStartMenu.prototype.onLoad = function() {
	var uiStartContainer = document.getElementById("StartMenuContainer") ;
	var uiExoStart = document.getElementById("UIExoStart") ;
	eXo.portal.UIExoStartMenu.init(uiStartContainer, uiExoStart, 0, 0);
	eXo.webui.UIPopupMenu.hide(uiStartContainer);
};

UIExoStartMenu.prototype.startButtonOver = function() {
	if(!eXo.portal.UIExoStartMenu.buttonClicked) {
		this.className = "ExoStartButton ButtonOver" ;
	}
};

UIExoStartMenu.prototype.startButtonOut = function() {
	if(!eXo.portal.UIExoStartMenu.buttonClicked) {
		this.className = "ExoStartButton ButtonNormal" ;
	}
}

UIExoStartMenu.prototype.showStartMenu = function(e) {
	if(!e) var e = window.event;
	e.cancelBubble = true;
	
	var uiStartContainer = document.getElementById("StartMenuContainer") ;
	eXo.portal.UIExoStartMenu.exoStartButton.className = "ExoStartButton ButtonClicked" ;
	if(uiStartContainer.style.display == "block") {
		eXo.portal.UIExoStartMenu.hideUIStartMenu();
	} else {
		eXo.portal.UIExoStartMenu.buttonClicked = true ;
		this.superClass.show(uiStartContainer);
		var menuY = eXo.core.Browser.findPosY(eXo.portal.UIExoStartMenu.exoStartButton);
		var y = menuY - uiStartContainer.offsetHeight;
		uiStartContainer.style.width = "238px";
		this.superClass.setPosition(uiStartContainer, 0, y) ;
	}
	
	/*Hide eXoStartMenu whenever click on the UIApplication*/
	var uiPortalApplication = document.getElementById("UIPortalApplication") ;
	uiPortalApplication.onclick = eXo.portal.UIExoStartMenu.hideUIStartMenu ;
	
//	eXo.core.DOMUtil.listHideElements(uiStartContainer);
};

UIExoStartMenu.prototype.hideUIStartMenu = function() {
	var uiStartContainer = document.getElementById("StartMenuContainer") ;
	eXo.webui.UIPopupMenu.hide(uiStartContainer);
	eXo.portal.UIExoStartMenu.buttonClicked = false ;
	eXo.portal.UIExoStartMenu.exoStartButton.className = "ExoStartButton ButtonNormal" ;
};

eXo.portal.UIExoStartMenu = new UIExoStartMenu() ;