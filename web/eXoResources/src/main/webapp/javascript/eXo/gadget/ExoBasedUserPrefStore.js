

gadgets.ExoBasedUserPrefStore = function() {
  gadgets.UserPrefStore.call(this);
};

gadgets.ExoBasedUserPrefStore.inherits(gadgets.UserPrefStore);

gadgets.ExoBasedUserPrefStore.prototype.getPrefs = function(gadget) {
  return gadget.userPrefs_;
};

gadgets.ExoBasedUserPrefStore.prototype.savePrefs = function(gadget) {
  	//TODO: dang.tung - sent event to portal
  var prefs = eXo.core.JSON.stringify(gadget.userPrefs_) ;
  var DOMUtil = eXo.core.DOMUtil;
	var gadget = document.getElementById("gadget_" + gadget.id) ;
	if(gadget != null ) {
		var portletFragment = DOMUtil.findAncestorByClass(gadget, "PORTLET-FRAGMENT");
		var uiGadget = gadget.parentNode;
		if (portletFragment != null) {
			var compId = portletFragment.parentNode.id;
			var href = eXo.env.server.portalBaseURL + "?portal:componentId=" + compId;
			href += "&portal:type=action&uicomponent=" + uiGadget.id;
			href += "&op=SaveUserPref";
			href += "&userPref=" + prefs;
			ajaxAsyncGetRequest(href,true);
		} else {
			var params = [
			 {name : "userPref", value : prefs}
			] ;
			ajaxAsyncGetRequest(eXo.env.server.createPortalURL(uiGadget.id, "SaveUserPref", true, params),true) ;
		}
	}
};

gadgets.Container.prototype.userPrefStore =
    new gadgets.ExoBasedUserPrefStore();
//}