eXo.require('eXo.core.TemplateEngine') ;
eXo.require('eXo.application.ApplicationDescriptor') ;

function UIExoWidget() {
}

UIExoWidget.prototype.init = function(appName, appFolder) {
  this.appCategory = "eXoWidgetWeb" ;
	this.appName = appName ;
	this.appFolder = appFolder;
	var nameWidget = eXo.widget.UIExoWidget.getNameWidget(appName);
	this.appIcon = "/eXoResources/skin/portal/webui/component/view/UIPageDesktop/DefaultSkin/icons/80x80/"+nameWidget+".png" ;
	this.skin = {
	  Default: "/eXoWidgetWeb/skin/"+appFolder+"/DefaultStylesheet.css",
	  Mac:     "/eXoWidgetWeb/skin/"+appFolder+"/MacStylesheet.css",
	  Vista:   "/eXoWidgetWeb/skin/"+appFolder+"/VistaStylesheet.css"
	} ;
	this.width = "220px" ;
	this.height = "auto" ;
};

UIExoWidget.prototype.createApplicationInstance = function(appDescriptor) {
	var DOMUtil = eXo.core.DOMUtil ;	
	var appElement = document.getElementById(this.appName);
	if(appElement == null) return;
	this.createAppDescriptor(appDescriptor, appElement);
	
 	appDescriptor.widget.content = 
    eXo.core.TemplateEngine.merge("eXo/widget/web/"+this.appFolder+"/"+this.appName+".jstmpl", appDescriptor, "/eXoWidgetWeb/javascript/") ;
    
 	appDescriptor.widget.removeApplication = 
 		"eXo.widget.web."+this.appName+".destroyInstance('" + appDescriptor.appId + "');";
 	
 	var innerHTML = eXo.core.TemplateEngine.merge("eXo/widget/UIWidget.jstmpl", appDescriptor);
 	var applicationNode = DOMUtil.createElementNode(innerHTML, "div");
 	applicationNode.applicationDescriptor = appDescriptor;
 	return applicationNode ;
};

UIExoWidget.prototype.createAppDescriptor = function(appDescriptor, appElement) {
	appDescriptor.widget = {
		positionX : appElement.getAttribute('posX'),
		positionY : appElement.getAttribute('posY'),
		zIndex : appElement.getAttribute('zIndex'),
		
		uiWidget : {
			temporaty : appElement,
			appId : appElement.getAttribute('applicationId')
		}
	};
}

UIExoWidget.prototype.initApplication = function(applicationId, instanceId) {	
//	alert("INIT UISTICKER WIDGET");
	var DOMUtil = eXo.core.DOMUtil;	
	var appDescriptor =  new eXo.application.ApplicationDescriptor(instanceId, this);
	var appInstance = appDescriptor.createApplication();
	if(appInstance == null) return;
	
	appInstance.id = appInstance.applicationDescriptor.widget.uiWidget.appId;
	
	var appElement = appInstance.applicationDescriptor.widget.uiWidget.temporaty;
	
	var uiPageDesktop = DOMUtil.findAncestorByClass(appElement, "UIPageDesktop") ;
	if(uiPageDesktop == null) {
		eXo.widget.UIAddWidget.addWidget(appInstance);
		DOMUtil.removeTemporaryElement(appElement);
	} else {
		eXo.widget.UIAddWidget.addWidgetToDesktop(appInstance) ;
		DOMUtil.removeTemporaryElement(appElement) ;
	}
};


UIExoWidget.prototype.destroyApplicationInstance = function(appDescriptor) {
	var applicationNode = document.getElementById(appDescriptor.appId);
	return applicationNode ;
};

UIExoWidget.prototype.destroyInstance = function(instanceId) {
	if(!confirm("Are you sure you want to delete this application?")) return;
  var appDescriptor = new eXo.application.ApplicationDescriptor(instanceId, this);
  var removeAppInstance = appDescriptor.destroyApplication();
  eXo.desktop.UIDesktop.removeJSApplication(removeAppInstance);
};

UIExoWidget.prototype.getNameWidget = function(nameWidget) {
	var strlabel = nameWidget.charAt(2); 
	for(var i = 3; i < nameWidget.length; i ++) {
		strlabel = strlabel + nameWidget.charAt(i);
	}
	return strlabel;
};

eXo.widget.UIExoWidget = new UIExoWidget();
