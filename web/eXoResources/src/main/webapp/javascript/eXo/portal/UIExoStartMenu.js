eXo.require('eXo.webui.UIPopupMenu');
/**
 * Manages the Start Menu in the workspace area
 * Inherits from UIPopupMenu (superClass attribute)
 */
function UIExoStartMenu() {
  this.buttonClicked = false ;
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
  this.exoStartButton.onmouseover = uiStart.startButtonOver ;
  this.exoStartButton.onmouseout = uiStart.startButtonOut ;
  
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
    // If the pointed menu item contains a link, sets the item clickable
    var link = eXo.core.DOMUtil.findDescendantsByTagName(labelItem, "a")[0];
    this.superClass.createLink(menuItems[i], link);
//    var cont = eXo.core.DOMUtil.findAncestorByClass(menuItems[i], this.containerStyleClass) ;
//    if (!cont.id) cont.id = "StartMenuContainer-"+i;
//    cont.resized = false;
  }
  /*
   * minh.js.exo
   */
  var blockMenuItems = eXo.core.DOMUtil.findDescendantsByClass(popupMenu, "div", this.containerStyleClass) ;
  for (i=0; i < blockMenuItems.length; i++) {
    if (!blockMenuItems[i].id) blockMenuItems[i].id = "StartMenuContainer-"+i;
    blockMenuItems[i].resized = false;
  }
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
UIExoStartMenu.prototype.onMenuItemOver = function(e) {
  var menuItem = this;

  menuItem.className = eXo.portal.UIExoStartMenu.itemOverStyleClass ;
  // If the pointed menu item contains a submenu, resizes it
  var menuItemContainer = eXo.core.DOMUtil.findFirstDescendantByClass(menuItem, "div", eXo.portal.UIExoStartMenu.containerStyleClass) ;

  if (menuItemContainer) {
    eXo.portal.UIExoStartMenu.showMenuItemContainer(menuItem, menuItemContainer) ;
    eXo.portal.UIExoStartMenu.superClass.pushVisibleContainer(menuItemContainer.id);
    if (!menuItemContainer.resized && eXo.core.Browser.getBrowserType() == "ie") {
      // Resizes the container only once, the first time. After, container.resized is true so the condition is false
      eXo.portal.UIExoStartMenu.setContainerSize(menuItemContainer);
    }
  }
  
};
/**
 * Shows the submenu (menuItemContainer) of the pointed button (menuItem)
 * Sets the position of the submenu so it appears entirely on the screen
 * If the submenu is too on the right or on the bottom, its position moves to the left or up
 */
UIExoStartMenu.prototype.showMenuItemContainer = function(menuItem, menuItemContainer) {
  this.superClass.show(menuItemContainer);
  var x = menuItem.offsetWidth + menuItem.offsetLeft;
  var y = menuItem.offsetTop;
  var rootX = eXo.core.Browser.findPosX(menuItem);
  var rootY = eXo.core.Browser.findPosY(menuItem);
  if (x + menuItemContainer.offsetWidth + rootX > eXo.core.Browser.getBrowserWidth()) {
    x -= (menuItemContainer.offsetWidth + menuItem.offsetWidth);
  }
  if (y + menuItemContainer.offsetHeight + rootY > eXo.core.Browser.getBrowserHeight()) {
    y -= (menuItemContainer.offsetHeight - menuItem.offsetHeight);
  }
  if (eXo.core.Browser.getBrowserType() == "ie") x -= 10;
  this.superClass.setPosition(menuItemContainer, x, y);
};
/**
 * Called when the user leaves a button
 * If this button has a submenu, adds it to the elementsToHide array of UIPopupMenu, 
 * ad removes it from the currentVisibleContainers array.
 * See UIPopupMenu for more details about how the elements are hidden
 */
UIExoStartMenu.prototype.onMenuItemOut = function(e) {
  var menuItem = this;
  menuItem.className = eXo.portal.UIExoStartMenu.itemStyleClass ;
  var menuItemContainer = eXo.core.DOMUtil.findFirstDescendantByClass(menuItem, "div", eXo.portal.UIExoStartMenu.containerStyleClass) ;
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

// TODO : Don't need this method
//UIExoStartMenu.prototype.setSize = function(popup, w, h) {
//  if (typeof(popup) == "string") popup = document.getElementById(popup);
//  if (popup) {
//    popup.style.width = w + "px";
//    popup.style.height = h + "px";
//  }
//};

eXo.portal.UIExoStartMenu = new UIExoStartMenu() ;