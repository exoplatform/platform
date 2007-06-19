eXo.require('eXo.core.TemplateEngine');
eXo.require('eXo.application.ApplicationDescriptor');

function UIStickerWidget() {
	this.appCategory = "eXoWidgetWeb" ;
	this.appName = "UIStickerWidget" ;
	this.appIcon = "/eXoResources/skin/portal/webui/component/view/UIPageDesktop/DefaultSkin/icons/80x80/Register.png";
	this.skin = {
	  Default: "/eXoWidgetWeb/skin/sticker/DefaultStylesheet.css",
	  Mac:     "/eXoWidgetWeb/skin/sticker/MacStylesheet.css",
	  Vista:   "/eXoWidgetWeb/skin/sticker/VistaStylesheet.css"
	} ;
	this.width = "220px" ;
	this.height = "auto" ;
};

UIStickerWidget.prototype.createApplicationInstance = function(appDescriptor) {
	var DOMUtil = eXo.core.DOMUtil ;	
	var appElement = document.getElementById("UIStickerWidget");
	if(appElement == null) return;
	appDescriptor.widget = {
		positionX : appElement.getAttribute('posX'),
		positionY : appElement.getAttribute('posY'),
		zIndex : appElement.getAttribute('zIndex'),
		
		uiStickerWidget : {
			temporaty : appElement,
			appId : appElement.getAttribute('applicationId')
		}
	};
	
 	appDescriptor.widget.content = 
    eXo.core.TemplateEngine.merge("eXo/widget/web/sticker/UIStickerWidget.jstmpl", appDescriptor, "/eXoWidgetWeb/javascript/") ;
 	appDescriptor.widget.removeApplication = 
 		"eXo.widget.web.UIStickerWidget.destroyInstance('" + appDescriptor.appId + "');";
 	
 	var innerHTML = eXo.core.TemplateEngine.merge("eXo/widget/UIWidget.jstmpl", appDescriptor);
 	var applicationNode = DOMUtil.createElementNode(innerHTML, "div");
 	applicationNode.applicationDescriptor = appDescriptor;
 	
 	return applicationNode ;
};

UIStickerWidget.prototype.initApplication = function(applicationId, instanceId) {	
//	alert("INIT UISTICKER WIDGET");
	var DOMUtil = eXo.core.DOMUtil;	
	var appDescriptor = 
	  new eXo.application.ApplicationDescriptor(instanceId, eXo.widget.web.sticker.UIStickerWidget);
	var appInstance = appDescriptor.createApplication();
	if(appInstance == null) return;
	appInstance.id = appInstance.applicationDescriptor.widget.uiStickerWidget.appId;
	
	var appElement = appInstance.applicationDescriptor.widget.uiStickerWidget.temporaty;
	
	var uiPageDesktop = DOMUtil.findAncestorByClass(appElement, "UIPageDesktop") ;
	if(uiPageDesktop == null) {
		eXo.widget.UIAddWidget.addWidget(appInstance);
		DOMUtil.removeTemporaryElement(appElement);
	} else {
		eXo.widget.UIAddWidget.addWidgetToDesktop(appInstance) ;
		DOMUtil.removeTemporaryElement(appElement) ;
	}
};

UIStickerWidget.prototype.destroyApplicationInstance = function(appDescriptor) {
	var applicationNode = document.getElementById(appDescriptor.appId);
	return applicationNode ;
};

/*##############################################################################################*/
UIStickerWidget.prototype.destroyInstance = function(instanceId) {
	if(confirm("Are you sure you want to delete this application?")) {
    var appDescriptor = 
      new eXo.application.ApplicationDescriptor(instanceId, eXo.widget.web.Sticker.UIStickerWidget);
    
    var removeAppInstance = appDescriptor.destroyApplication();
    eXo.desktop.UIDesktop.removeJSApplication(removeAppInstance);
  }	
};

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
