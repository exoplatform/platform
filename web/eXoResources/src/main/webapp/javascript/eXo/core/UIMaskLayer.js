eXo.require('eXo.core.Browser') ;
/**
 * Manages the mask layer component
 */
function UIMaskLayer() {
// TODO: This variable seem to be used
//	this.count = 0 ;
} ;
/**
 * Creates and returns the dom element that contains the mask layer, with these parameters
 *  . the mask layer is a child of blockContainerId
 *  . object
 *  . the opacity in %
 *  . the position between : TOP-LEFT, TOP-RIGHT, BOTTOM-LEFT, BOTTOM-RIGHT, other value will position to center
 * The returned element has the following html attributes :
 *  . className = "MaskLayer" ;
 *	. id = "MaskLayer" ;
 *	.	style.display = "block" ;
 *	. maxZIndex = 2 ;
 *	.	style.zIndex = maskLayer.maxZIndex ;
 *	.	style.top = "0px" ;
 *	.	style.left = "0px" ;
 */
UIMaskLayer.prototype.createMask = function(blockContainerId, object, opacity, position) {
	try {
		var Browser = eXo.core.Browser ;
		var blockContainer = document.getElementById(blockContainerId) ;
		var maskLayer = document.createElement("div") ;
		
		this.object = object ;
		this.blockContainer = blockContainer ;
		this.maskLayer = maskLayer;
		
		this.position = position ;
		
		blockContainer.appendChild(maskLayer) ;
		
		maskLayer.className = "MaskLayer" ;
		maskLayer.id = "MaskLayer" ;
		maskLayer.style.display = "block" ;
		maskLayer.maxZIndex = 2 ;
		maskLayer.style.zIndex = maskLayer.maxZIndex ;
		maskLayer.style.top = "0px" ;
		maskLayer.style.left = "0px" ;
	//	maskLayer.style.right = "0px" ;
	
		if(opacity) {
	    Browser.setOpacity(maskLayer, opacity) ;
		}
		
		if(object != null){
			if(object.nextSibling) {
			  maskLayer.nextSiblingOfObject = object.nextSibling ;
			  maskLayer.parentOfObject = null ;
			} else {
			  maskLayer.nextSiblingOfObject = null ;
			  maskLayer.parentOfObject = object.parentNode ;
			}
			
			object.style.zIndex = maskLayer.maxZIndex + 1 ;
			object.style.display = "block" ;
			
			blockContainer.appendChild(object) ;
		
			eXo.core.UIMaskLayer.setPosition() ;
			
			if((blockContainer.offsetWidth > object.offsetLeft + object.offsetWidth) && (position == "TOP-RIGHT") || (position == "BOTTOM-RIGHT")) {
		    object.style.left = blockContainer.offsetWidth - object.offsetWidth + "px" ;
			}
	  }
		 //(document.body.offsetHeight > Browser.getBrowserHeight()) ? document.body.offsetHeight : eXo.core.Browser.getBrowserHeight() ;
		maskLayer.style.height = "100%"; //document.documentElement.scrollTop + maskLayerHeight + "px";
		maskLayer.style.width = "100%"; //blockContainer.offsetWidth + "px" ;

		eXo.core.UIMaskLayer.doScroll();

		}catch(err) {
			alert(err) ;
	}
	return maskLayer ;
};

/**
 * Moves the position of the mask layer to follow a scroll
 */
UIMaskLayer.prototype.doScroll = function() {
	if(document.getElementById("MaskLayer")) {
		var maskLayer =	this.maskLayer;
		maskLayer.style.top = document.documentElement.scrollTop + "px" ;
		setTimeout("eXo.core.UIMaskLayer.doScroll()", 1) ;
	}
};

/**
 * Set the position of the mask layer, depending on the position attribute of UIMaskLayer
 * position is between : TOP-LEFT, TOP-RIGHT, BOTTOM-LEFT, BOTTOM-RIGHT, other value will position to center
 */
UIMaskLayer.prototype.setPosition = function() {
	var UIMaskLayer = eXo.core.UIMaskLayer ;
	var Browser = eXo.core.Browser ;
	var object = UIMaskLayer.object ;
	var blockContainer = UIMaskLayer.blockContainer ;
	var position = UIMaskLayer.position ;
	object.style.position = "absolute" ;
	
	var left ;
	var top ;
	if (position == "TOP-LEFT") {
	  left = 0 ;
	  top = 0 ;
	} else if (position == "TOP-RIGHT") {
		return ;
	} else if (position == "BOTTOM-LEFT") {
	  left = 0 ;
	  top = Browser.getBrowserHeight() - object.offsetHeight + document.documentElement.scrollTop ;
	} else if (position == "BOTTOM-RIGHT") {
	  left = blockContainer.offsetWidth - object.offsetWidth ;
	  top = Browser.getBrowserHeight() - object.offsetHeight + document.documentElement.scrollTop ;
	} else {
	  left = (blockContainer.offsetWidth - object.offsetWidth) / 2 ;
	  top = (Browser.getBrowserHeight() - object.offsetHeight) / 2 +  document.documentElement.scrollTop ;
	}
	
	object.style.left = left + "px" ;
	object.style.top = top + "px" ;
} ;
/**
 * Removes the mask layer from the DOM
 */
UIMaskLayer.prototype.removeMask = function(maskLayer) {
	if (maskLayer) {
	  var parentNode = maskLayer.parentNode ;
	  maskLayer.nextSibling.style.display = "none" ;
  
	  if (maskLayer.nextSiblingOfObject) {
	  	maskLayer.nextSiblingOfObject.parentNode.insertBefore(maskLayer.nextSibling, maskLayer.nextSiblingOfObject) ;
	  	maskLayer.nextSiblingOfObject = null ;
	  } else {
	  	maskLayer.parentOfObject.appendChild(maskLayer.nextSibling) ;
	  	maskLayer.parentOfObject = null ;
	  }

  	parentNode.removeChild(maskLayer) ;
  	
	}
} ;


UIMaskLayer.prototype.resizeMaskLayer = function() {
	//TODO Lambkin: Don't need this method.
	return ;
} ;

eXo.core.UIMaskLayer = new UIMaskLayer() ;
