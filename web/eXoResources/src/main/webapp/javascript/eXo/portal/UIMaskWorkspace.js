function UIMaskWorkspace() {
};

UIMaskWorkspace.prototype.init = function(maskId, show, width, height) {
	var maskWorkpace = document.getElementById(maskId);	
	var contentContainer = eXo.core.DOMUtil.findFirstDescendantByClass(maskWorkpace, "div", "ContentContainer") ;
	contentContainer.style.height = 380 + "px"
	if(width > -1) maskWorkpace.style.width = width+'px';
	if(show) {
		if (eXo.portal.UIMaskWorkspace.maskLayer == null) {
			var	maskLayer = eXo.core.UIMaskLayer.createMask("UIPortalApplication", maskWorkpace, 30) ;
			eXo.portal.UIMaskWorkspace.maskLayer = maskLayer;			
		}
		maskWorkpace.style.margin = 'auto';
		maskWorkpace.style.display = 'block';
	} else {
		if(eXo.portal.UIMaskWorkspace.maskLayer == undefined)	return;
		eXo.core.UIMaskLayer.removeMask(eXo.portal.UIMaskWorkspace.maskLayer);
		eXo.portal.UIMaskWorkspace.maskLayer = null;
		maskWorkpace.style.display = 'none';
	}
	//contentContainer.style.width = (width - 30) + "px";
	contentContainer.style.overflow = "auto";
	//if(height > -1) maskWorkpace.style.height = height+'px';	
};

eXo.portal.UIMaskWorkspace = new UIMaskWorkspace() ;