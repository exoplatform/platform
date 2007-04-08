eXo.require('eXo.core.Browser') ;

function UIMaskLayer() {
	this.count = 0 ;
} ;

UIMaskLayer.prototype.createMask = function(blockContainerId, object, opacity, position) {
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
	maskLayer.maxZIndex = 100 ;
	maskLayer.style.zIndex = maskLayer.maxZIndex ;
	maskLayer.style.top = "0px" ;
	maskLayer.style.left = "0px" ;

	if(opacity) {
		Browser.setOpacity(maskLayer, opacity) ;
	}	
	
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

	//var scrollTop = document.documentElement.scrollTop ;
	var maskLayerHeight = (blockContainer.offsetHeight > Browser.getBrowserHeight()) ? blockContainer.offsetHeight : eXo.core.Browser.getBrowserHeight() ;
	maskLayer.style.width = blockContainer.offsetWidth + "px" ;
	//maskLayer.style.height = (maskLayerHeight + scrollTop) + "px" ;
	maskLayer.style.height = document.getElementById("UIPortalApplication").offsetHeight + "px";
	return maskLayer ;
} ;

UIMaskLayer.prototype.setPosition = function() {
	var UIMaskLayer = eXo.core.UIMaskLayer ;
	var Browser = eXo.core.Browser ;
	var object = UIMaskLayer.object ;
	var blockContainer = UIMaskLayer.blockContainer ;
	var position = UIMaskLayer.position ;
	object.style.position = "absolute" ;
	
	if(position == "TOP-LEFT") {
		var left = 0 ;
		var top = 0 ;
	} else if(position == "TOP-RIGHT") {
		var left = blockContainer.offsetWidth - object.offsetWidth ;
		var top = 0 ;
	} else if(position == "BOTTOM-LEFT") {
		var left = 0 ;
		var top = Browser.getBrowserHeight() - object.offsetHeight + document.documentElement.scrollTop;
	} else if(position == "BOTTOM-RIGHT") {
		var left = blockContainer.offsetWidth - object.offsetWidth ;
		var top = Browser.getBrowserHeight() - object.offsetHeight + document.documentElement.scrollTop ;
	} else {
		var left = (blockContainer.offsetWidth - object.offsetWidth) / 2 ;
		var top = (Browser.getBrowserHeight() - object.offsetHeight) / 2 +  document.documentElement.scrollTop;
	}
	
	object.style.left = left + "px" ;
	object.style.top = top + "px" ;
} ;

UIMaskLayer.prototype.removeMask = function(maskLayer) {
	var parentNode = maskLayer.parentNode ;
	maskLayer.nextSibling.style.display = "none" ;
	
	if(maskLayer.nextSiblingOfObject) {
		maskLayer.nextSiblingOfObject.parentNode.insertBefore(maskLayer.nextSibling, maskLayer.nextSiblingOfObject) ;
		maskLayer.nextSiblingOfObject = null ;
	} else {
		maskLayer.parentOfObject.appendChild(maskLayer.nextSibling) ;
		maskLayer.parentOfObject = null ;
	}
	
	parentNode.removeChild(maskLayer) ;
} ;

eXo.core.UIMaskLayer = new UIMaskLayer() ;
