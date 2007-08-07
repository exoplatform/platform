eXo.require('eXo.widget.UIExoWidget');

UIStickerWidget.prototype = eXo.widget.UIExoWidget;
UIStickerWidget.prototype.constructor = UIStickerWidget;

function UIStickerWidget() {
	this.init("UIStickerWidget", "sticker");
}


UIStickerWidget.prototype.sendContent = function(object) {	
	if (object.value == "")	return ;
	var DOMUtil = eXo.core.DOMUtil ;
	var uiWidgetContainer = DOMUtil.findAncestorByClass(object, "WidgetApplication") ;
	var uiWidget = DOMUtil.findAncestorByClass(object, "UIWidget" );
	containerBlockId = uiWidgetContainer.id;
	var parent = uiWidgetContainer.parentNode ;
	var params = [
  	{name: "objectId", value : uiWidget.id} ,
  	{name: "content", value : object.value}
  ] ;
  alert("---------1-------" + uiWidget.id);
  var url = eXo.env.server.context + "/command?" ;
  url += "type=org.exoplatform.web.command.handler.StickerWidgetHandler&action=saveContent&objectId="+uiWidget.id+"&content="+object.value ;
  ajaxAsyncGetRequest(url, false);
} ;

UIStickerWidget.prototype.createAppDescriptor = function(appDescriptor, appElement) {
    appDescriptor.widget = {
        positionX : appElement.getAttribute('posX'),
        positionY : appElement.getAttribute('posY'),
        zIndex : appElement.getAttribute('zIndex'),
        
        uiWidget : {
            temporaty : appElement,
            appId : appElement.getAttribute('applicationId'),
            content : appElement.getAttribute('content')
        }
    };
}

if(eXo.widget.web == null) eXo.widget.web = {} ;
if(eXo.widget.web.sticker == null) eXo.widget.web.sticker = {};
eXo.widget.web.sticker.UIStickerWidget = new UIStickerWidget()  ;
