function UIExoWidget() {
}

/*
* This method is used by any user widget to init itself
*
* - The widget name is is extracted from the full class name
* - A widget can have an icon to be then used in several locations like the application registry
* - A widget must have a width of maximum 220px to fit in the left column of eXo Portal
* - A widget can provide different skins but only a default is mandatory
* 
*/
UIExoWidget.prototype.init = function(appName, appFolder, attrsWidget) {
  this.appCategory = "eXoWidgetWeb" ;
	this.appName = appName ;
	this.attrsWidget = attrsWidget;
	this.appFolder = appFolder;
	var nameWidget = eXo.widget.UIExoWidget.getNameWidget(appName);
	this.appIcon = "/eXoResources/skin/DefaultSkin/portal/webui/component/view/UIPageDesktop/icons/80x80/"+nameWidget+".png" ;
	this.skin = {
	  Default: "/eXoWidgetWeb/skin/"+appFolder+"/DefaultStylesheet.css",
	  Mac:     "/eXoWidgetWeb/skin/"+appFolder+"/MacStylesheet.css",
	  Vista:   "/eXoWidgetWeb/skin/"+appFolder+"/VistaStylesheet.css"
	} ;
	this.width = "220px" ;
	this.height = "auto" ;
};

UIExoWidget.prototype.createApplicationInstance = function(appDescriptor) {
	var instance = new Object();
	var DOMUtil = eXo.core.DOMUtil ;	
	var appElement = document.getElementById(this.appName);
	if(appElement == null) return;
	
	appDescriptor.widget = {
		positionX : appElement.getAttribute('posX'),
		positionY : appElement.getAttribute('posY'),
		zIndex : appElement.getAttribute('zIndex'),
		
		uiWidget : {		
		}		
	};
	
	var setWidgetData = 'appDescriptor.widget.uiWidget = { ' + 
												 'temporaty : appElement, ' +
												 'appId : appElement.getAttribute(\'applicationId\') ' ;
	if(this.attrsWidget != null && this.attrsWidget.length > 0) {											 								 
  		for(var i = 0; i < this.attrsWidget.length; i++) {
  			var attrWidget =   this.attrsWidget[i];
  			setWidgetData +=   ',' + attrWidget + ': appElement.getAttribute(\''+attrWidget+'\')';  	 
  		}												 
	}
	setWidgetData += '};';

  eval(setWidgetData);
												
 	appDescriptor.widget.content = 
    eXo.core.TemplateEngine.merge("eXo/widget/web/"+this.appFolder+"/"+this.appName+".jstmpl", appDescriptor, "/eXoWidgetWeb/javascript/") ;
    
 	appDescriptor.widget.removeApplication = 
 		"eXo.widget.web."+this.appName+".destroyInstance('" + appDescriptor.appId + "');";
 	
 	var innerHTML = eXo.core.TemplateEngine.merge("eXo/widget/UIWidget.jstmpl", appDescriptor);
 	var applicationNode = DOMUtil.createElementNode(innerHTML, "div");
 	applicationNode.applicationDescriptor = appDescriptor;
 	return applicationNode ;
};


/*
* 
*
*
*
*/
UIExoWidget.prototype.initApplication = function(applicationId, instanceId) {	
	var DOMUtil = eXo.core.DOMUtil;	
	var appDescriptor =  new eXo.application.ApplicationDescriptor(instanceId, this);
	var appInstance = appDescriptor.createApplication();
	if(appInstance == null) return;
	
	appInstance.id = appInstance.applicationDescriptor.widget.uiWidget.appId;
	
	var appElement = appInstance.applicationDescriptor.widget.uiWidget.temporaty;
	
	var uiPageDesktop = DOMUtil.findAncestorByClass(appElement, "UIPageDesktop") ;
	if(uiPageDesktop == null) eXo.widget.UIAddWidget.addWidget(appInstance);
	else	eXo.widget.UIAddWidget.addWidgetToDesktop(appInstance) ;
	DOMUtil.removeElement(appElement);
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

/*
* This method allows to extract the widget name from the full class name
*/
UIExoWidget.prototype.getNameWidget = function(nameWidget) {
	var strlabel = nameWidget.charAt(2); 
	for(var i = 3; i < nameWidget.length; i ++) {
		strlabel = strlabel + nameWidget.charAt(i);
	}
	return strlabel;
};

eXo.widget.UIExoWidget = new UIExoWidget();
