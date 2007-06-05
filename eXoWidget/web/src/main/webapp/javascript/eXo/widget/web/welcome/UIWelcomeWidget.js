eXo.require('eXo.core.TemplateEngine') ;
eXo.require('eXo.application.ApplicationDescriptor') ;

function UIWelcomeWidget() {
	this.appCategory = "exo.widget.web" ;
	this.appName = "UIWelcomeWidget" ;
	this.appIcon = "/eXoResources/skin/portal/webui/component/view/UIPageDesktop/DefaultSkin/icons/80x80/Register.png" ;
	this.skin = {
	  Default: "/exo.widget.web/skin/welcome/DefaultStylesheet.css",
	  Mac:     "/exo.widget.web/skin/welcome/MacStylesheet.css",
	  Vista:   "/exo.widget.web/skin/welcome/VistaStylesheet.css"
	} ;
	this.width = "220px" ;
	this.height = "auto" ;
};

UIWelcomeWidget.prototype.createApplicationInstance = function(appDescriptor) {
	var DOMUtil = eXo.core.DOMUtil ;
	
	var app = document.getElementById("UIWelcomeWidget");

	appDescriptor.widget = {
		uiWelcomeWidget : {
			temporary : app,
			appId : app.getAttribute('applicationId'),
			positionX : app.getAttribute('posX'),
			positionY : app.getAttribute('posY'),
			userName : app.getAttribute('userName')
		}
	};
	
 	appDescriptor.widget.content = 
    eXo.core.TemplateEngine.merge("eXo/widget/web/welcome/UIWelcomeWidget.jstmpl", appDescriptor, "/exo.widget.web/javascript/") ;
 	appDescriptor.widget.removeApplication = 
 		"eXo.widget.web.UIWelcomeWidget.destroyInstance('" + appDescriptor.appId + "');" ;
 	
 	var innerHTML = eXo.core.TemplateEngine.merge("eXo/widget/UIWidget.jstmpl", appDescriptor) ;
 	var applicationNode = DOMUtil.createElementNode(innerHTML, "div");
 	applicationNode.applicationDescriptor = appDescriptor;
 	
 	return applicationNode ;
};

UIWelcomeWidget.prototype.initApplication = function(applicationId, instanceId) {
	var DOMUtil = eXo.core.DOMUtil ;
	var appDescriptor = 
	  new eXo.application.ApplicationDescriptor(instanceId, eXo.widget.web.welcome.UIWelcomeWidget) ;
	var appInstance = appDescriptor.createApplication();
	appInstance.id = appInstance.applicationDescriptor.widget.uiWelcomeWidget.appId ;
	var app = appInstance.applicationDescriptor.widget.uiWelcomeWidget.temporary ;
	
	var uiPageDesktop = DOMUtil.findAncestorByClass(app, "UIPageDesktop") ;
	if(uiPageDesktop == null) {
		eXo.widget.UIAddWidget.addWidget(appInstance) ;
		DOMUtil.removeTemporaryElement(app) ;
	} else {
		eXo.widget.UIAddWidget.addWidgetToDesktop(appInstance) ;
		DOMUtil.removeTemporaryElement(app) ;
	}	
} ;

UIWelcomeWidget.prototype.destroyApplicationInstance = function(appDescriptor) {
	var applicationNode = document.getElementById(appDescriptor.appId);
	
	return applicationNode ;
};

/*##############################################################################################*/
UIWelcomeWidget.prototype.destroyInstance = function(instanceId) {
	if(confirm("Are you sure you want to delete this application?")) {
    var appDescriptor = 
      new eXo.application.ApplicationDescriptor(instanceId, eXo.widget.web.welcome.UIWelcomeWidget);
    
    var removeAppInstance = appDescriptor.destroyApplication();
    eXo.desktop.UIDesktop.removeJSApplication(removeAppInstance);
  }	
};

if(eXo.widget.web == null) eXo.widget.web = {} ;
if(eXo.widget.web.welcome == null) eXo.widget.web.welcome = {};
eXo.widget.web.welcome.UIWelcomeWidget = new UIWelcomeWidget() ;