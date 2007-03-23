function UIExoStartMenu() {
	this.checkShow = false ;
	this.buttonClicked = false ;
};

UIExoStartMenu.prototype.init = function() {
	var uiExoStart = document.getElementById("UIExoStart") ;
	uiExoStart.onmousedown = function(event) {
		if(!event) event = window.event ;
		event.cancelBubble = true ;
	}
	var menuItems = eXo.core.DOMUtil.findDescendantsByClass(uiExoStart, "div", "MenuItem") ;
	for(var i = 0; i < menuItems.length; i++) {
		menuItems[i].onmouseover = eXo.portal.UIExoStartMenu.menuItemOver ;
	}
	
	this.exoStartButton = eXo.core.DOMUtil.findFirstDescendantByClass(uiExoStart, "div", "ExoStartButton") ;
	this.exoStartButton.onmouseover = eXo.portal.UIExoStartMenu.startButtonOver ;
	this.exoStartButton.onmouseout = eXo.portal.UIExoStartMenu.startButtonOut ;
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

UIExoStartMenu.prototype.showStartMenu = function() {
	var uiExoStart = document.getElementById("UIExoStart") ;
	var startBarContainer = eXo.core.DOMUtil.findFirstDescendantByClass(uiExoStart, "div", "StartBarContainer") ;
	var startBarContainerX = eXo.core.Browser.findPosX(startBarContainer) ;
	var startBarContainerY = eXo.core.Browser.findPosY(startBarContainer) ;
	var startBarContainerW = startBarContainer.offsetWidth ;
	
	eXo.portal.UIExoStartMenu.exoStartButton.className = "ExoStartButton ButtonClicked" ;
	eXo.portal.UIExoStartMenu.buttonClicked = true ;
	
	var startMenuContainer = document.getElementById("StartMenuContainer") ;
	if(startMenuContainer.style.display == "block") {
		startMenuContainer.style.display = "none" ;
		eXo.portal.UIExoStartMenu.exoStartButton.className = "ExoStartButton ButtonOver" ;
	} else {
		eXo.portal.UIExoStartMenu.checkShow = true ;
		
		startMenuContainer.style.position = "absolute" ;
		startMenuContainer.style.display = "block";
		
		var startMenuContainerH = startMenuContainer.offsetHeight ;
		var scrollTop = document.documentElement.scrollTop ;

/*		Comment here		*/
		var startMenuContainerTop = startBarContainerY - startMenuContainerH - scrollTop + 6 ;

		startMenuContainer.style.top = startMenuContainerTop + "px" ;
		startMenuContainer.style.left = (startBarContainerX + 1) + "px" ;
		startMenuContainer.style.width = (startBarContainerW - 10) + "px" ;
		//alert(startMenuContainer.style.width);
		startMenuContainer.style.height = startMenuContainerH + "px" ;
		var menuItemContainer = eXo.core.DOMUtil.findFirstDescendantByClass(startMenuContainer, "div", "MenuItemContainer") ;
				
		var blackArrowIcon = eXo.core.DOMUtil.findDescendantsByClass(menuItemContainer, "div", "BlackArrowIcon") ;
		for(var i = 0; i < blackArrowIcon.length; i++) {
			var menuItem = eXo.core.DOMUtil.findAncestorByClass(blackArrowIcon[i], "MenuItem") ;
			if(!eXo.core.DOMUtil.hasDescendantClass(menuItem, "MenuItemContainer")) {
				blackArrowIcon[i].style.background = "none" ;
			}
		}
		/* Debug On IE*/
		startMenuContainer.style.display = "block" ;
		eXo.portal.UIExoStartMenu.hideMenuItemContainer(menuItemContainer) ;
	}
} ;

UIExoStartMenu.prototype.hideMenuItemContainer = function(menuItemContainer) {
	var menuItemChildren = eXo.core.DOMUtil.findChildrenByClass(menuItemContainer, "div", "MenuItem");
	for(var i = 0 ; i < menuItemChildren.length; i++){
		var subFirstChild = eXo.core.DOMUtil.findFirstDescendantByClass(menuItemChildren[i], "div", "MenuItemContainer");
		subFirstChild.style.visibility = "hidden" ;
	}
} ;

UIExoStartMenu.prototype.showMenuItemContainer = function() {
	var startMenuContainer = document.getElementById("StartMenuContainer") ;
	var menuItemContainer = eXo.core.DOMUtil.findFirstDescendantByClass(startMenuContainer, "div", "MenuItemContainer") ;
	var menuItemChildren = eXo.core.DOMUtil.findChildrenByClass(menuItemContainer, "div", "MenuItem");
	for(var i = 0 ; i < menuItemChildren.length; i++){
		var subFirstChild = eXo.core.DOMUtil.findFirstDescendantByClass(menuItemChildren[i], "div", "MenuItemContainer");
		subFirstChild.style.visibility = "visible" ;
	}
} ;

UIExoStartMenu.prototype.renderMenuItem = function(rootItem) {
	var hasDescendant = eXo.core.DOMUtil.hasDescendantClass(rootItem, "MenuItemContainer") ;
	if(hasDescendant) {
		var DOMUtil = eXo.core.DOMUtil ;
		var startMenuBG = DOMUtil.findFirstDescendantByClass(rootItem, "div", "StartMenuBG") ;
		var decoratorBlock = DOMUtil.findFirstDescendantByClass(startMenuBG, "div", "DecoratorBlock") ;
		var children = null ;
		if(decoratorBlock != null) {
			children = DOMUtil.getChildrenByTagName(decoratorBlock, "div") ;
		}	else {
			children = DOMUtil.getChildrenByTagName(startMenuBG, "div") ;
		}
		
		if(children != null) {
			var maxWidth = children[0].offsetWidth;
			for(var i = 0; i < children.length; i++) {
				if(children[i].offsetWidth > maxWidth) maxWidth = children[i].offsetWidth ;
			}			
			if(decoratorBlock != null) {
				children[0].style.width = maxWidth - 10 + "px" ;
				for(var i = 1; i < children.length; i++) {
					children[i].style.width = maxWidth + "px" ;
				}
			} else {
				for(var i = 0; i < children.length; i++) {
					children[i].style.width = maxWidth + "px" ;
				}
			}
		}
	}
};

UIExoStartMenu.prototype.hideStartMenuContainer = function() {
	var startMenuContainer = document.getElementById("StartMenuContainer") ;
	startMenuContainer.style.display = "none" ;
};

UIExoStartMenu.prototype.menuItemOver = function() {

	eXo.portal.UIExoStartMenu.showMenuItemContainer() ;
	
	var selectedElement = this ;
	var menuItemSelected = eXo.core.DOMUtil.findFirstDescendantByClass(selectedElement, "div", "LabelItem") ;
	var menuItemContainer = eXo.core.DOMUtil.findFirstDescendantByClass(selectedElement, "div", "MenuItemContainer") ;
	
	var parentSelectedElement = selectedElement.parentNode ;
	var menuItem = eXo.core.DOMUtil.findChildrenByClass(parentSelectedElement, "div", "MenuItem") ;
	
	menuItemSelected.style.background = "#cbdaf4" ;
	if(menuItemContainer != null) {
		eXo.portal.UIExoStartMenu.showSubMenu(menuItemContainer, menuItemSelected) ;
	}
	
	for(var i = 0; i < menuItem.length; i++) {
		if(menuItem[i] != selectedElement) {
			var labelItem = eXo.core.DOMUtil.findFirstDescendantByClass(menuItem[i], "div", "LabelItem") ;
			var menuItemContainer = eXo.core.DOMUtil.findFirstDescendantByClass(menuItem[i], "div", "MenuItemContainer") ;
			
			labelItem.style.background = "none" ;
			
			if(menuItemContainer != null) {
				menuItemContainer.style.display = "none" ;
			}
		}	
	}
	//=============================== Bug Here ====================================
	eXo.portal.UIExoStartMenu.renderMenuItem(selectedElement);
};

UIExoStartMenu.prototype.showSubMenu = function(menuItemContainer, selectedElement) {
	var selectedElementX = eXo.core.Browser.findPosX(selectedElement) ;
	var selectedElementY = eXo.core.Browser.findPosY(selectedElement) ;
	var selectedElementW = selectedElement.offsetWidth ;
	var selectedElementH = selectedElement.offsetHeight ;
	var menuItemContainerAncestor = eXo.core.DOMUtil.findAncestorByClass(selectedElement, "MenuItemContainer") ;
	var browserHeight = eXo.core.Browser.getBrowserHeight() ;
		
	/*Process for the first MenuContainer*/
			
	menuItemContainer.style.display = "block" ;
	var menuItemContainerY = eXo.core.Browser.findPosY(menuItemContainerAncestor) ;
	var posYSelectedElementInContainer = Math.abs(selectedElementY - menuItemContainerY) ;
	
	var availableSpace = eXo.portal.UIExoStartMenu.checkAvailableSpace(menuItemContainer, selectedElementH, selectedElementY, browserHeight) ;
	
	menuItemContainer.style.position = "absolute" ;
	menuItemContainer.style.left = (selectedElementW + 0) + "px" ;
	
	if(availableSpace == "top") {
		menuItemContainer.style.top = (posYSelectedElementInContainer - menuItemContainer.offsetHeight + selectedElement.offsetHeight) + "px" ;
	} else if(availableSpace == "center") {
		var scrollTop = document.documentElement.scrollTop ;
		menuItemContainer.style.top = ((browserHeight - menuItemContainer.offsetHeight) / 2) - (menuItemContainerY - scrollTop) + "px" ;
	} else {
		menuItemContainer.style.top = posYSelectedElementInContainer + "px" ;
	}
	
	var minWidth = 120 ;
	
	if(menuItemContainer.offsetWidth < minWidth) {
		menuItemContainer.style.width = minWidth + "px" ;
	}
			
	/*Fix Bug on IE*/
//	var labelItem = eXo.core.DOMUtil.findDescendantsByClass(menuItemContainer, "div", "LabelItem") ;
//	var parentWidth = menuItemContainer.offsetWidth ;
//	for(var i = 0; i < labelItem.length; i++) {
//		var parentNode = labelItem[i].parentNode ;
//		labelItem[i].style.width = parentNode.offsetWidth + "px" ;
//		parentNode.style.border = "solid 1px red" ;
//		window.status = "Width: " + parentNode.offsetWidth ;
//	}
} ;

UIExoStartMenu.prototype.checkAvailableSpace = function(menuItemContainer, selectedElementH, selectedElementY, browserHeight) {
	var scrollTop = document.documentElement.scrollTop ;
	var availableSpace = "" ;
	var menuItemContainerHeight = menuItemContainer.offsetHeight ;
	var selectedElementXInBrowser = selectedElementY - scrollTop ;

	if(menuItemContainerHeight > (browserHeight - selectedElementXInBrowser)) {
		availableSpace = "top" ;
	}
	
	if((selectedElementXInBrowser + selectedElementH - menuItemContainerHeight) < 0) {
		availableSpace = "center" ;
	}
		
	return availableSpace ;
} ;

UIExoStartMenu.prototype.hideUIStartMenu = function() {
	if(eXo.portal.UIExoStartMenu.checkShow) {
		eXo.portal.UIExoStartMenu.hideStartMenuContainer() ;
	}
	/*Hide All MenuItemContainer*/
	var uiExoStart = document.getElementById("UIExoStart") ;
	var menuItemContainer = eXo.core.DOMUtil.findDescendantsByClass(uiExoStart, "div" ,"MenuItemContainer") ;
	eXo.portal.UIExoStartMenu.buttonClicked = false ;
	eXo.portal.UIExoStartMenu.exoStartButton.className = "ExoStartButton ButtonNormal" ;
		
	for(var i = 0; i < menuItemContainer.length; i++) {
		if((menuItemContainer[i] != null) && (menuItemContainer[i].style.display == "block")) {
			menuItemContainer[i].style.display = "none" ;
		}
	}

//	menuItemContainer[menuItemContainer.length - 1].style.border = "solid 1px red" ;
//	for(var i = (menuItemContainer.length - 1); i >= 0; i--) {
//		if((menuItemContainer[i] != null) && (menuItemContainer[i].style.display == "block")) {
//			menuItemContainer[i].style.display = "none" ;
//		}
//	}
	
	var uiPageDesktop = document.getElementById("UIPageDesktop") ;
	if(uiPageDesktop != null) uiPageDesktop.style.zIndex = "1" ;
};

UIExoStartMenu.prototype.findMenuItemInContainer = function(root, nodeType, itemClazz, containerClazz) {
	var DOMUtil = eXo.core.DOMUtil ;
	var itemList = new Array() ;
	var itemDescendants = DOMUtil.findDescendantsByClass(root, nodeType, itemClazz) ;
	for(var i = 0; i < itemDescendants.length; i++) {
		var parentNode = DOMUtil.findAncestorByClass(itemDescendants, containerClazz) ;
		if(parentNode == root) itemList.push(itemDescendants[i]) ;
	}
	
	return itemList ;
};

UIExoStartMenu.prototype.getTime = function() {
	return eXo.core.ExoDateTime.getTime() ;
};

UIExoStartMenu.prototype.onLoad = function() {
  eXo.portal.UIExoStartMenu.init() ;
  eXo.core.ExoDateTime.getTime() ;
  
  document.onmousedown = eXo.portal.UIExoStartMenu.hideUIStartMenu ;
}

eXo.portal.UIExoStartMenu = new UIExoStartMenu();
