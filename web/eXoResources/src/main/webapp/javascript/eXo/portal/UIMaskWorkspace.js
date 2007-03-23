function UIMaskWorkspace() {
	
};

UIMaskWorkspace.prototype.init = function(maskId, show) {
	var maskWorkpace = document.getElementById(maskId);
	if(show) {
		maskLayer = eXo.core.UIMaskLayer.createMask("UIPortalApplication", maskWorkpace, 30) ;
		eXo.portal.UIMaskWorkspace.maskLayer = maskLayer;
	} else {
		if(eXo.portal.UIMaskWorkspace.maskLayer == undefined)	return;
		eXo.core.UIMaskLayer.removeMask(eXo.portal.UIMaskWorkspace.maskLayer);
	}
};

eXo.portal.UIMaskWorkspace = new UIMaskWorkspace() ;