function UIMaskWorkspace() {
	
};

UIMaskWorkspace.prototype.init = function(maskId, show) {
	var maskWorkpace = document.getElementById(maskId);
	if(show) {
		maskWorkpace.style.display = "block" ;
		maskLayer = eXo.core.UIMaskLayer.createMask("UIPortalApplication", maskWorkpace, 30) ;
		eXo.portal.UIMaskWorkspace.maskLayer = maskLayer;
	} else {
		maskWorkpace.style.display = "none" ;
		if(eXo.portal.UIMaskWorkspace.maskLayer == undefined)	return;
		eXo.core.UIMaskLayer.removeMask(eXo.portal.UIMaskWorkspace.maskLayer);
	}
	 var uiLoginForm = eXo.core.DOMUtil.findFirstDescendantByClass(maskWorkpace, "div", "UILoginForm");
	 if(uiLoginForm != null){
	 	 maskWorkpace.style.width = 630 + "px" ;
	 }
};

eXo.portal.UIMaskWorkspace = new UIMaskWorkspace() ;