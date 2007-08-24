/**
 * The mask layer, that appears when an ajax call waits for its result
 */
function UIMaskWorkspace() {
};
/**
 * Inits the mask workspace identified by maskId
 * if show is true
 *  . creates the mask with eXo.core.UIMaskLayer.createMask
 *  . set margin: auto and display: block
 * if show is false
 *  . removes the mask with eXo.core.UIMaskLayer.removeMask
 *  . set display: none
 * sets the size (width and height) of the mask
 */
UIMaskWorkspace.prototype.init = function(maskId, show, width, height) {
	var maskWorkpace = document.getElementById(maskId);
	this.maskWorkpace = maskWorkpace ;
	if(this.maskWorkpace) {
		if(width > -1) this.maskWorkpace.style.width = width+ "px";
		if(show) {
			if (eXo.portal.UIMaskWorkspace.maskLayer == null) {
				var	maskLayer = eXo.core.UIMaskLayer.createMask("UIPortalApplication", this.maskWorkpace, 30) ;
				eXo.portal.UIMaskWorkspace.maskLayer = maskLayer;
			}
			this.maskWorkpace.style.margin = "auto";
			this.maskWorkpace.style.display = "block";
		} else {
			if(eXo.portal.UIMaskWorkspace.maskLayer == undefined)	return;
			eXo.core.UIMaskLayer.removeMask(eXo.portal.UIMaskWorkspace.maskLayer);
			eXo.portal.UIMaskWorkspace.maskLayer = null;
			this.maskWorkpace.style.display = "none";
		}
		if(height < 0) return;
	}	
};
/**
 * Resets the position of the mask
 * calls eXo.core.UIMaskLayer.setPosition to perform this operation
 */
UIMaskWorkspace.prototype.resetPosition = function() {
	var maskWorkpace = eXo.portal.UIMaskWorkspace.maskWorkpace ;
	if (maskWorkpace && (maskWorkpace.style.display == "block")) {
		try{
			eXo.core.UIMaskLayer.blockContainer = document.getElementById("UIPortalApplication") ;
			eXo.core.UIMaskLayer.object =  maskWorkpace;
			eXo.core.UIMaskLayer.setPosition() ;
		} catch (e){}
	}
} ;

eXo.portal.UIMaskWorkspace = new UIMaskWorkspace() ;