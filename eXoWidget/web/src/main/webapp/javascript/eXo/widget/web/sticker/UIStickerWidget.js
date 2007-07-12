eXo.require('eXo.widget.UIExoWidget');

UIStickerWidget.prototype = eXo.widget.UIExoWidget;
UIStickerWidget.prototype.constructor = UIStickerWidget;

function UIStickerWidget() {
	this.init("UIStickerWidget", "sticker");
}

UIStickerWidget.prototype.sendContent = function(object) {	
	if (object.value == "")	return ;
	var DOMUtil = eXo.core.DOMUtil ;
	var uiWidgetContainer = DOMUtil.findAncestorByClass(object, "UIWidgetContainer") ;
	containerBlockId = uiWidgetContainer.id;

	var params = [
  	{name: "objectId", value : object.id} ,
  	{name: "stickerContent", value : object.value}
  ] ;
  
	ajaxAsyncGetRequest(eXo.env.server.createPortalURL(containerBlockId, "SaveContent", true, params), false) ;
} ;

if(eXo.widget.web == null) eXo.widget.web = {} ;
if(eXo.widget.web.sticker == null) eXo.widget.web.sticker = {};
eXo.widget.web.sticker.UIStickerWidget = new UIStickerWidget()  ;
