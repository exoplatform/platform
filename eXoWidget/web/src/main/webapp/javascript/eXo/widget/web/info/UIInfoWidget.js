eXo.require('eXo.core.TemplateEngine');
eXo.require('eXo.application.ApplicationDescriptor');

function UIInfoWidget() {
	this.appCategory = "exo.widget.web" ;
	this.appName = "InfoWidget" ;
	this.appIcon = "/eXoResources/skin/portal/webui/component/view/UIPageDesktop/DefaultSkin/icons/80x80/Register.png";
	this.skin = {
	  Default: "/exo.widget.web/skin/info/DefaultStylesheet.css",
	  Mac:     "/exo.widget.web/skin/info/MacStylesheet.css",
	  Vista:   "/exo.widget.web/skin/info/VistaStylesheet.css"
	} ;
	this.width = "220px" ;
	this.height = "auto" ;
};

UIInfoWidget.prototype.createApplicationInstance = function(appDescriptor) {
	var DOMUtil = eXo.core.DOMUtil ;

	appDescriptor.widget = {
	};
	
 	appDescriptor.widget.content = 
    eXo.core.TemplateEngine.merge("eXo/widget/web/info/UIInfoWidget.jstmpl", appDescriptor, "/exo.widget.web/javascript/") ;
 	appDescriptor.widget.removeApplication = 
 		"eXo.widget.web.UIInfoWidget.destroyInstance('" + appDescriptor.appId + "');";
 	
 	var innerHTML = eXo.core.TemplateEngine.merge("eXo/widget/UIWidget.jstmpl", appDescriptor);
 	var applicationNode = DOMUtil.createElementNode(innerHTML, "div");
 	applicationNode.applicationDescriptor = appDescriptor;
 	
 	return applicationNode ;
};

UIInfoWidget.prototype.initApplication = function(applicationId, instanceId) {
	var DOMUtil = eXo.core.DOMUtil;
	
	var appDescriptor = 
	  new eXo.application.ApplicationDescriptor(instanceId, eXo.widget.web.info.UIInfoWidget);
	  
	var appInstance = appDescriptor.createApplication();
	var appElement = document.getElementById("UIInfoWidget");
	appInstance.id = appElement.getAttribute('applicationId') ;
	
	var uiPageDesktop = DOMUtil.findAncestorByClass(appElement, "UIPageDesktop") ;
	if(uiPageDesktop == null) {
		eXo.widget.UIAddWidget.addWidget(appInstance);
		DOMUtil.removeTemporaryElement(appElement);
	} else {
		eXo.widget.UIAddWidget.addWidgetToDesktop(appInstance);
		DOMUtil.removeTemporaryElement(appElement);
	}	
};

UIInfoWidget.prototype.destroyApplicationInstance = function(appDescriptor) {
	var applicationNode = document.getElementById(appDescriptor.appId);
	
	return applicationNode ;
};

/*##############################################################################################*/
UIInfoWidget.prototype.destroyInstance = function(instanceId) {
	if(confirm("Are you sure you want to delete this application?")) {
    var appDescriptor = 
      new eXo.application.ApplicationDescriptor(instanceId, eXo.widget.web.info.UIInfoWidget);
    
    var removeAppInstance = appDescriptor.destroyApplication();
    eXo.desktop.UIDesktop.removeJSApplication(removeAppInstance);
  }	
};

if(eXo.widget.web == null) eXo.widget.web = {} ;
if(eXo.widget.web.info == null) eXo.widget.web.info = {};
eXo.widget.web.info.UIInfoWidget = new UIInfoWidget()  ;
