eXo.require('eXo.webui.UIPopupMenu');
/**
 * Manages the Start Menu in the workspace area
 * Inherits from UIPopupMenu (superClass attribute)
 */
function UIExoStartMenu() {
  this.buttonClicked = false ;
  this.clipTop = 1;
	this.clipBottom = 1;
	this.stepScroll = 5;
} ;
/**
 * Init function called when the page loads
 * After the configuration of the parameters, call the buildMenu function that
 * adds the javascript events to the buttons in the menu
 */


UIExoStartMenu.prototype.init = function(popupMenu, container, x, y) {
  var uiStart = eXo.portal.UIExoStartMenu;
  
  this.superClass = eXo.webui.UIPopupMenu;
  this.superClass.init(popupMenu, container.id, x, y) ;
  
  this.itemStyleClass = "MenuItem";
  this.itemOverStyleClass = "MenuItemOver";
  this.containerStyleClass = "MenuItemContainer";
  
  this.exoStartButton = eXo.core.DOMUtil.findFirstDescendantByClass(container, "div", "ExoStartButton") ;
  this.exoStartButton.onmouseover = function() {
  	if(!eXo.portal.UIExoStartMenu.buttonClicked) this.className = "ExoStartButton ButtonOver" ;
  };
  this.exoStartButton.onmouseout = function() {
  	if(!eXo.portal.UIExoStartMenu.buttonClicked) this.className = "ExoStartButton ButtonNormal" ;
	};
  
  this.buildMenu(popupMenu);
} ;
/**
 * Configure some parameters on load, and inits the Start Menu
 */
UIExoStartMenu.prototype.onLoad = function() {
  var uiStartContainer = document.getElementById("StartMenuContainer") ;
  var uiExoStart = document.getElementById("UIExoStart") ;
  eXo.portal.UIExoStartMenu.init(uiStartContainer, uiExoStart, 0, 0);
  eXo.webui.UIPopupMenu.hide(uiStartContainer);
  eXo.core.ExoDateTime.getTime() ;
};
/**
 * Browse the different buttons in the menu, and adds
 *  . the onMouseOver listener
 *  . the onMouseOut listener
 *  . an onClick event for buttons that contain a link
 *  . an id
 */
UIExoStartMenu.prototype.buildMenu = function(popupMenu) {
  var menuItems = eXo.core.DOMUtil.findDescendantsByClass(popupMenu, "div", this.itemStyleClass) ;
  for(var i = 0; i< menuItems.length; i++) {
    menuItems[i].onmouseover = eXo.portal.UIExoStartMenu.onMenuItemOver ;
    menuItems[i].onmouseout = eXo.portal.UIExoStartMenu.onMenuItemOut ;
    
    var labelItem = eXo.core.DOMUtil.findFirstDescendantByClass(menuItems[i], "div", "LabelItem") ;
    var link = eXo.core.DOMUtil.findDescendantsByTagName(labelItem, "a")[0];
    this.superClass.createLink(menuItems[i], link);
  }
  var blockMenuItems = eXo.core.DOMUtil.findDescendantsByClass(popupMenu, "div", this.containerStyleClass) ;
  for (i=0; i < blockMenuItems.length; i++) {
    if (!blockMenuItems[i].id) blockMenuItems[i].id = "StartMenuContainer-"+i;
    blockMenuItems[i].resized = false;
  }
};

/**
 * Shows the start menu
 */
UIExoStartMenu.prototype.showStartMenu = function(evt) {
  if (!evt) evt = window.event ;
  evt.cancelBubble = true ;

  var uiStartContainer = document.getElementById("StartMenuContainer") ;
  eXo.portal.UIExoStartMenu.exoStartButton.className = "ExoStartButton ButtonClicked" ;
  if(uiStartContainer.style.display == "block") {
    eXo.portal.UIExoStartMenu.hideUIStartMenu() ;
  } else {
    eXo.portal.UIExoStartMenu.buttonClicked = true ;
    var menuY = eXo.core.Browser.findPosY(eXo.portal.UIExoStartMenu.exoStartButton) ;
    this.superClass.show(uiStartContainer) ;		
		var y = menuY - uiStartContainer.offsetHeight ;
		
    if(window.pageYOffset) y -= window.pageYOffset ;
    else if (document.documentElement.scrollTop) y -= document.documentElement.scrollTop ;
    else if (document.body.scrollTop) y -= document.body.scrollTop ;
		this.superClass.setPosition(uiStartContainer, 0, y) ;
		
    uiStartContainer.style.width = "238px";
    uiStartContainer.style.height = uiStartContainer.offsetHeight + "px";
  }
  /*Hide eXoStartMenu whenever click on the UIApplication*/
  var uiPortalApplication = document.getElementById("UIPortalApplication") ;
  uiPortalApplication.onclick = eXo.portal.UIExoStartMenu.hideUIStartMenu ;
};
/**
 * Hides the start menu when the user clicks anywhere on the page
 */
UIExoStartMenu.prototype.hideUIStartMenu = function() {
  var uiStartContainer = document.getElementById("StartMenuContainer") ;
  eXo.webui.UIPopupMenu.hide(uiStartContainer);
  eXo.portal.UIExoStartMenu.buttonClicked = false ;
  eXo.portal.UIExoStartMenu.exoStartButton.className = "ExoStartButton ButtonNormal" ;
  eXo.portal.UIExoStartMenu.clearStartMenu();
};

UIExoStartMenu.prototype.clearStartMenu = function() {
  eXo.webui.UIPopupMenu.currentVisibleContainers.clear();
  // Calls the function to hide the submenu, after a few milliseconds
  eXo.webui.UIPopupMenu.setCloseTimeout();
};
/**
 * Called when the user points at a button
 * If this button has a submenu, adds it to the currentVisibleContainers array of UIPopupMenu
 * See UIPopupMenu for more details about how the elements are shown
 */
UIExoStartMenu.prototype.onMenuItemOver = function(event) {
  var menuItem = this;

  menuItem.className = eXo.portal.UIExoStartMenu.itemOverStyleClass ;
  var menuItemContainer = eXo.core.DOMUtil.findFirstChildByClass(menuItem, "div", "MenuItemContainer") ;
  if (menuItemContainer) {
//  	menuItem.style.position = "relative" ;
    eXo.portal.UIExoStartMenu.showMenuItemContainer(event, menuItem, menuItemContainer) ;
    eXo.portal.UIExoStartMenu.superClass.pushVisibleContainer(menuItemContainer.id);
    if (!menuItemContainer.resized && eXo.core.Browser.getBrowserType() == "ie") {
      eXo.portal.UIExoStartMenu.setContainerSize(menuItemContainer);
    }
  }
};
/**
 * Shows the submenu (menuItemContainer) of the pointed button (menuItem)
 * Sets the position of the submenu so it appears entirely on the screen
 * If the submenu is too on the right or on the bottom, its position moves to the left or up
 */
 
 UIExoStartMenu.prototype.showMenuItemContainer = function(event, menuItem, menuItemContainer) {

			
	


 	var blockMenu = eXo.core.DOMUtil.findFirstDescendantByClass(menuItemContainer, "div", "blockMenu") ;
	var parentMenu = blockMenu.parentNode;
	var topElement = eXo.core.DOMUtil.findFirstDescendantByClass(parentMenu, "div", "topElement") ;
	var menuContainer = eXo.core.DOMUtil.findFirstDescendantByClass(blockMenu, "div", "menuContainer") ;
 	var bottomElement = eXo.core.DOMUtil.findDescendantsByClass(parentMenu, "div", "bottomElement") ;
	bottomElement = bottomElement[bottomElement.length - 1];
	
	if (!menuContainer.id) menuContainer.id = "eXo" + new Date().getTime() + Math.random().toString().substring(2);
	
	
	menuItemContainer.style.display = "block";
	var x = menuItem.offsetWidth + menuItem.offsetLeft;
  var rootX = eXo.core.Browser.findPosX(menuItem);
	if (x + menuItemContainer.offsetWidth + rootX > eXo.core.Browser.getBrowserWidth()) {
    	x -= (menuItemContainer.offsetWidth + menuItem.offsetWidth);
  }
  if (eXo.core.Browser.getBrowserType() == "ie") x -= 10;
 			menuItemContainer.style.left = x + "px" ;

	
	var browserHeight = eXo.core.Browser.getBrowserHeight();

  var Y = eXo.portal.UIExoStartMenu.getDimension(event, menuItem, menuContainer);

/*	
	tobe continue
	minh.js.exo
	
	if (Y.Pos) menuItemContainer.style.bottom = Y.Pos + "px" ;
	else menuItemContainer.style.bottom = Y + "px" ;
*/
  var y = menuItem.offsetTop;
  var rootY = eXo.core.Browser.findPosY(menuItem);
  if (y + menuItemContainer.offsetHeight + rootY > browserHeight) {
    	y -= (menuItemContainer.offsetHeight - menuItem.offsetHeight);
  }
	
	menuItemContainer.style.top = y + "px";
	if (menuContainer.offsetHeight + 149 > browserHeight) {
			menuItemContainer.style.top = y + "px";
			var curentHeight = browserHeight - 149;
			blockMenu.style.height = curentHeight + "px" ;
			blockMenu.style.width = blockMenu.offsetWidth + "px";

			topElement.style.display = "block" ;
			bottomElement.style.display = "block" ;
			
			if(!menuContainer.curentHeight || (menuContainer.curentHeight != curentHeight)) {
				eXo.portal.UIExoStartMenu.initSlide(menuContainer, curentHeight);
			}
			topElement.onmousedown = function() {
				eXo.portal.UIExoStartMenu.scrollDown(menuContainer.id, curentHeight);
			}
			topElement.onmouseoup = function() {
				if (menuContainer.repeat){
					clearTimeout(menuContainer.repeat);
					menuContainer.repeat = null;
				}
			};
			topElement.onclick = function(event) {
					clearTimeout(menuContainer.repeat);
					menuContainer.repeat = null;
				event = event || window.event;
				event.cancelBubble = true;
			}
			
			bottomElement.onmousedown = function() {
				eXo.portal.UIExoStartMenu.scrollUp(menuContainer.id, curentHeight);
			};
			bottomElement.onmouseoup = function() {
				if (menuContainer.repeat){
					clearTimeout(menuContainer.repeat);
					menuContainer.repeat = null;
				}
			};			
			bottomElement.onclick = function(event) {
				clearTimeout(menuContainer.repeat);
				menuContainer.repeat = null;
				event = event || window.event;
				event.cancelBubble = true;
			}
  	} else {
			blockMenu.style.height = menuContainer.offsetHeight + "px";
			menuContainer.style.clip = "rect(0px 1280px auto auto)";
			menuContainer.style.top = 0 + "px" ;
			topElement.style.display = "none" ;
			bottomElement.style.display = "none" ;
		  menuItemContainer.style.top = y + "px";
  	}
};

UIExoStartMenu.prototype.getDimension = function(event, menuItem, menuContainer) {
	var dimension = {};
	var Y = 0;
	dimension.total = eXo.core.Browser.getBrowserHeight();
	dimension.top = eXo.core.Browser.findMouseYInClient(event);
	dimension.top = dimension.top - (dimension.top % menuItem.offsetHeight);
	dimension.bottom = dimension.total - dimension.top;
	var blockMenu = eXo.core.DOMUtil.findAncestorByClass(menuItem, "blockMenu");
	if (blockMenu) {
			dimension.total = blockMenu.offsetHeight;
		 /*
		  * busy
		  * minh.js.exo
		  */
	}
	else {

		if (menuContainer.offsetHeight < dimension.top)	{
			Y = 0;
		} else if (menuContainer.offsetHeight < dimension.bottom){
			Y -= menuContainer.offsetHeight; 
		} else if (menuContainer.offsetHeight < dimension.total) {
			Y += menuItem.offsetHeight;
			Y -= dimension.bottom;
		} else {
			Y = {Pos: 0};
			Y.Pos += menuItem.offsetHeight;
			Y.Pos -= dimension.bottom;
		}
	}
	return Y;
}

UIExoStartMenu.prototype.initSlide = function(menuContainer, clipBottom) {
	menuContainer.curentHeight = clipBottom;
	menuContainer.style.position = "absolute";
	menuContainer.style.top = 0 + "px";
	menuContainer.style.clip = 'rect(0px, 1280px,'+clipBottom+'px, 0px)';	
}

 UIExoStartMenu.prototype.scrollUp = function(id, height) {
		var scrollObject = document.getElementById(id) ;
 		var menuHeight = scrollObject.offsetHeight - height - this.stepScroll;
 		if (eXo.portal.UIExoStartMenu.clipBottom < menuHeight) {
	 		eXo.portal.UIExoStartMenu.clipTop += this.stepScroll;
			eXo.portal.UIExoStartMenu.clipBottom += this.stepScroll;
			var clipTop = eXo.portal.UIExoStartMenu.clipTop;
			var	clipBottom = eXo.portal.UIExoStartMenu.clipBottom + height;
	
			scrollObject.style.clip = 'rect('+clipTop+'px, 1280px,'+clipBottom+'px, 0px)';		
			scrollObject.style.top = -clipTop + "px";
			if (scrollObject.repeat) {
				clearTimeout(scrollObject.repeat);
				scrollObject.repeat = null;
			}
			scrollObject.repeat = setTimeout("eXo.portal.UIExoStartMenu.scrollUp('"+id+"', "+height+")", 1)	;
		}		
};

 UIExoStartMenu.prototype.scrollDown = function(id, height) {
 		var scrollObject = document.getElementById(id) ;
 		if (eXo.portal.UIExoStartMenu.clipTop > this.stepScroll) {
	 		eXo.portal.UIExoStartMenu.clipTop -= this.stepScroll;
			eXo.portal.UIExoStartMenu.clipBottom -= this.stepScroll;
			var clipTop = eXo.portal.UIExoStartMenu.clipTop;
			var	clipBottom = eXo.portal.UIExoStartMenu.clipBottom + height;
		
			scrollObject.style.clip = 'rect('+clipTop+'px, 1280px,'+clipBottom+'px, 0px)';		
			scrollObject.style.top = -clipTop + "px";
			if (scrollObject.repeat) {
				clearTimeout(scrollObject.repeat);
				scrollObject.repeat = null;
			}
			scrollObject.repeat = setTimeout("eXo.portal.UIExoStartMenu.scrollDown('"+id+"', "+height+")", 1)	;
		}
	
};
	
				
/**
 * Called when the user leaves a button
 * If this button has a submenu, adds it to the elementsToHide array of UIPopupMenu, 
 * ad removes it from the currentVisibleContainers array.
 * See UIPopupMenu for more details about how the elements are hidden
 */
UIExoStartMenu.prototype.onMenuItemOut = function(event) {
  var menuItem = this;
  menuItem.className = eXo.portal.UIExoStartMenu.itemStyleClass ;
	var menuItemContainer = eXo.core.DOMUtil.findFirstChildByClass(menuItem, "div", "MenuItemContainer") ;
	
  if (menuItemContainer) {
    eXo.portal.UIExoStartMenu.superClass.pushHiddenContainer(menuItemContainer.id);
    eXo.portal.UIExoStartMenu.superClass.popVisibleContainer();
    // Calls the function to hide the submenu, after a few milliseconds
    eXo.portal.UIExoStartMenu.superClass.setCloseTimeout();
  }
  window.status = "";
};
/**
 * Called only once for each submenu (thanks to the boolean resized)
 * and only on IE browsers
 * Sets the width of the decorator parts to the width of the content part.
 */
UIExoStartMenu.prototype.setContainerSize = function(menuItemContainer) {
  var menuCenter = eXo.core.DOMUtil.findFirstDescendantByClass(menuItemContainer, "div", "StartMenuML");
  var menuTop = eXo.core.DOMUtil.findFirstDescendantByClass(menuItemContainer, "div", "StartMenuTL");
  var decorator = eXo.core.DOMUtil.findFirstDescendantByClass(menuTop, "div", "StartMenuTR");
  var menuBottom = menuTop.nextSibling;
  while (menuBottom.className != "StartMenuBL") menuBottom = menuBottom.nextSibling;
  var w = menuCenter.offsetWidth - decorator.offsetLeft;
  menuTop.style.width = w + "px";
  menuBottom.style.width = w + "px";
  menuCenter.style.width = w + "px";
  menuItemContainer.resized = true;
};

eXo.portal.UIExoStartMenu = new UIExoStartMenu() ;