
function WebUI() {
	
};

WebUI.prototype.fixHeight = function(elemt, targetElemt) {
	if (elemt.offsetHeight == 0) return;
	if ( typeof targetElemt == "string" )	targetElemt = eXo.core.DOMUtil.findAncestorByClass(elemt, targetElemt);
	if ( targetElemt == null ) return;
	elemt.style.padding = "0px";
	elemt.style.overflow = "auto";
	targetElemt.style.overflow = "hidden"
	var targetHeight = targetElemt.offsetHeight ;
	var children = eXo.core.DOMUtil.getChildrenByTagName(targetElemt, "div") ;
	var childrenHeightTotal = 0;
	for (var i = 0; i < children.length; i++) {
		childrenHeightTotal += children[i].offsetHeight ;
	}
	var elemtHeight = ((targetHeight - childrenHeightTotal) + elemt.offsetHeight - 6) ;
	if ( elemtHeight > 0 ) {
		//children[0].style.width = children[0].offsetWidth ;
		elemt.style.height = elemtHeight + "px" ;
	}
};

eXo.webui.WebUI = new WebUI() ;