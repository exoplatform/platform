eXo.require('eXo.gadget.UIExoGadget');

eXo.gadgets = eXo.gadgets || {};
//if (!eXo.gadgets || !eXo.gadgets.rpc) {
	eXo.loadJS("/eXoGadgetServer/gadgets/js/rpc.js?c=1&debug=1&p=1");
	eXo.require("eXo.gadgets.Gadgets", "/eXoGadgetWeb/javascript/");
	eXo.require("eXo.gadgets.CookieBasedUserPrefStore", "/eXoGadgetWeb/javascript/");
	window.gadgets = eXo.gadgets.Gadgets;
//}

function Sample() {
	var attrs = new Array();
	//this.gadget = null;
	if (!this.gadgets) {
		this.gadgets = new HashMap();
	}
	this.init("Sample", "sample", attrs, "eXoGadgetWeb");
}

Sample.inherits(eXo.gadget.UIExoGadget);

Sample.prototype.onLoad = function(appId,url) {
	var gadget = eXo.gadgets.Gadgets.container.createGadget({specUrl: url});
	
    eXo.gadgets.Gadgets.container.addGadget(gadget);
	document.getElementById("gadgetBox").innerHTML = "<div id='gadget_" + gadget.id + "'> </div>";
	this.gadgets.put("" + appId, gadget);
//	debugger;
	eXo.gadgets.Gadgets.container.renderGadgets();
}

Sample.prototype.editGadget = function (uiGadget) {
//	debugger;
//	alert(uiWidget.id);
//	alert(this.gadgets.get(uiWidget.id).id);
	this.gadgets.get(uiGadget.id).handleOpenUserPrefsDialog();
}


if(eXo.gadgets.web == null) eXo.gadgets.web = {} ;
if(eXo.gadgets.web.sample == null) eXo.gadgets.web.sample = {};
eXo.gadgets.web.sample.Sample = new Sample();