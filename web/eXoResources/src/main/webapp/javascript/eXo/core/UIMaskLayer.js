eXo.require('eXo.core.Browser') ;

function UIMaskLayer() {
// TODO: This variable seem to be used
//	this.count = 0 ;
} ;

UIMaskLayer.prototype.createMask = function(blockContainerId, object, opacity, position) {
	try {
	var Browser = eXo.core.Browser ;
	var blockContainer = document.getElementById(blockContainerId) ;
	var maskLayer = document.createElement("div") ;
	
	this.object = object ;
	this.blockContainer = blockContainer ;
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
	var maskLayerHeight = (document.body.offsetHeight > Browser.getBrowserHeight()) ? document.body.offsetHeight : eXo.core.Browser.getBrowserHeight() ;
	maskLayer.style.width = blockContainer.offsetWidth + "px" ;
	maskLayer.style.height = document.documentElement.scrollTop + maskLayerHeight + "px";


	}catch(err) {
		alert(err);
	}
	return maskLayer ;
} ;

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
		// for showMaskLayer() method, in file UIPortal.js
	  // left = blockContainer.offsetWidth - object.offsetWidth ;
	  // top = 0 ;
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
	var maskLayer = document.getElementById("MaskLayer") ;
	if (maskLayer) {
	  var UIInnerMaskLayer = eXo.core.DOMUtil.findAncestorByClass(maskLayer, "UIInnerMaskLayer") ;
	  if (UIInnerMaskLayer) return ;
	  else if (maskLayer.style.display == "block") {
    	var ojectHeight = document.getElementById("UIPortalApplication") ;
    	  maskLayer.style.width = "100%" ;
    	var maskLayerHeight = (document.body.offsetHeight > ojectHeight.offsetHeight) ? document.body.offsetHeight : ojectHeight.offsetHeight ;
  	    maskLayer.style.height = maskLayerHeight + "px" ;
	  }
	}
} ;

eXo.core.UIMaskLayer = new UIMaskLayer() ;
