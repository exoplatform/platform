/* TODO: need to manage zIndex for all popup */
//var zIndex = 2 ;

//var popupArr = new Array() ;
/**
 * Main class to manage popups
 */
function UIPopup() {
	this.zIndex = 3 ;
} ;
/**
 * Inits the popup
 *  . calls changezIndex when the users presses the popup
 */
UIPopup.prototype.init = function(popup, containerId) {
	if(typeof(popup) == "string") popup = document.getElementById(popup) ;
	if(containerId) popup.containerId = containerId ;
	popup.onmousedown = this.changezIndex ;
} ;
/**
 * Increments the current zIndex value and sets this popup's zIndex property to this value
 */
UIPopup.prototype.changezIndex = function() {
	this.style.zIndex = ++eXo.webui.UIPopup.zIndex;
} ;
/**
 * Creates and returns a div element with the following style properties
 *  . position: relative
 *  . display: none
 */
UIPopup.prototype.create = function() {	
	var popup = document.createElement("div") ;
	with(popup.style) {
		position = "relative" ;
		display = "none" ;
	}
	return popup ;
} ;
/**
 * Sets the size of the popup with the given width and height parameters
 */
UIPopup.prototype.setSize = function(popup, w, h) {
	popup.style.width = w + "px" ;
	popup.style.height = h + "px" ;
} ;
/**
 * Shows (display: block) the popup
 */
UIPopup.prototype.show = function(popup) {
	if(typeof(popup) == "string") {	
		popup = document.getElementById(popup) ;
	}
	
	var uiMaskWS = document.getElementById("UIMaskWorkspace");
	if(uiMaskWS) {
		uiMaskWSzIndex = eXo.core.DOMUtil.getStyle(uiMaskWS, "zIndex");
		if(uiMaskWSzIndex && (uiMaskWSzIndex > eXo.webui.UIPopup.zIndex))	{
			eXo.webui.UIPopup.zIndex = uiMaskWSzIndex;
		}
	}
	
	popup.style.zIndex = ++eXo.webui.UIPopup.zIndex ;
	popup.style.display = "block" ;
} ;
/**
 * Shows (display: none) the popup
 */
UIPopup.prototype.hide = function(popup) {
	if(typeof(popup) == "string") {
		popup = document.getElementById(popup) ;
	}
	
	popup.style.display = "none" ;
} ;
/**
 * Sets the position of the popup to x and y values
 * changes the style properties :
 *  . position: absolute
 *  . top and left to y and x respectively
 * if the popup has a container, set its position: relative too
 */
UIPopup.prototype.setPosition = function(popup, x, y, isRTL) {
	if(popup.containerId) {
		var container = document.getElementById(popup.containerId) ;
		container.style.position = "relative" ;
	}	
	popup.style.position = "absolute" ;
	popup.style.top = y + "px" ;
	if(isRTL) {
		popup.style.right = x + "px" ;
		popup.style.left = "" ;
	}
	else {
		popup.style.left = x + "px" ;
		popup.style.right = "" ;
	}
} ;
/**
 * Aligns the popup according to the following values :
 *  1 : top left
 *  2 : top right
 *  3 : bottom left
 *  4 : bottom right
 *  other : center
 */
UIPopup.prototype.setAlign = function(popup, pos) {
	if ( typeof(popup) == 'string') popup = document.getElementById(popup) ;
	var intTop = 0 ;
	var intLeft = 0 ;
	switch (pos) {
		case 1:							// Top Left
		  intTop = 0 ;
		  intLeft = 0 ;
			break ;
		case 2:							// Top Right
  		intTop = 0 ;  		
		  intLeft = (eXo.core.Browser.getBrowserWidth() - popup.offsetWidth) ;
			break ;
		case 3:							// Bottom Left
		  intTop = (eXo.core.Browser.getBrowserHeight() - popup.offsetHeight) ;
		  intLeft = 0 ;				
			break ;
		case 4:							// Bottom Right
  		intTop = (eXo.core.Browser.getBrowserHeight() - popup.offsetHeight) ;
		  intLeft = (eXo.core.Browser.getBrowserWidth() - popup.offsetWidth) ;			
			break ;
		default:
		  intTop = (eXo.core.Browser.getBrowserHeight() - popup.offsetHeight) / 2 ;
		  intLeft = (eXo.core.Browser.getBrowserWidth() - popup.offsetWidth) / 2 ;	
			break ;
	}
	
	this.setPosition(popup, intLeft, intTop) ;
} ;
/**
 * Inits the DragDrop class with empty values
 */
UIPopup.prototype.initDND = function(evt) {
  var DragDrop = eXo.core.DragDrop ;

	DragDrop.initCallback = null ;

  DragDrop.dragCallback = null ;

  DragDrop.dropCallback = null ;
  
  var clickBlock = this ;
  var dragBlock = eXo.core.DOMUtil.findAncestorByClass(this, "UIDragObject") ;
  DragDrop.init(null, clickBlock, dragBlock, evt) ;
} ;

/**
 * Browses the popups on the page and closes them all
 * Clears the popupArr array (that contains the popups dom objects)
 */
//UIPopup.prototype.closeAll = function() {
//	var len = popupArr.length ;
//	for(var i = 0 ; i < len ; i++) {
//		popupArr[i].style.display = "none" ;
//	}
//	popupArr.clear() ;
//} ;

eXo.webui.UIPopup = new UIPopup() ;