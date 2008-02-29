eXo.require('eXo.widget.UIExoWidget');

UICometdWidget.prototype = eXo.widget.UIExoWidget;
UICometdWidget.prototype.constructor = UICometdWidget;

function UICometdWidget() {
	var attrs = new Array("userName", "token");
	this.init("UICometdWidget", "cometd", attrs);
	
	if(eXo.core.Cometd) {
		var el = document.getElementById("UICometdWidget");
		eXo.core.Cometd.exoId = el.attributes.userName.nodeValue;
		eXo.core.Cometd.exoToken = el.attributes.token.nodeValue;
	}
}

UICometdWidget.prototype.startCometdService = function() {
	eXo.core.Cometd.init();
	if(eXo.core.Topic) {
		eXo.core.Topic.initCometdBridge();
	}
}


if(eXo.widget.web == null) eXo.widget.web = {} ;
if(eXo.widget.web.cometd == null) eXo.widget.web.cometd = {};
eXo.widget.web.cometd.UICometdWidget = new UICometdWidget();

