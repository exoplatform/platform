function UIMaskWorkspace() {
};

UIMaskWorkspace.prototype.init = function(maskId, show, width, height) {
	var maskWorkpace = document.getElementById(maskId);	
	if(width > -1 && maskWorkpace != null) maskWorkpace.style.width = width+ "px";
	if(show) {
		if (eXo.portal.UIMaskWorkspace.maskLayer == null) {
			var	maskLayer = eXo.core.UIMaskLayer.createMask("UIPortalApplication", maskWorkpace, 30) ;
			eXo.portal.UIMaskWorkspace.maskLayer = maskLayer;
		}
		maskWorkpace.style.margin = "auto";
		maskWorkpace.style.display = "block";
	} else {
		if(eXo.portal.UIMaskWorkspace.maskLayer == undefined)	return;
		eXo.core.UIMaskLayer.removeMask(eXo.portal.UIMaskWorkspace.maskLayer);
		eXo.portal.UIMaskWorkspace.maskLayer = null;
		maskWorkpace.style.display = "none";
	}
	if(height < 0) return;
};

eXo.portal.UIMaskWorkspace = new UIMaskWorkspace() ;