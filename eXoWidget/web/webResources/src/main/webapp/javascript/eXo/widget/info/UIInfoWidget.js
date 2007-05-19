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
};

UIInfoWidget.prototype.createApplicationInstance = function(appDescriptor) {
	var DOMUtil = eXo.core.DOMUtil ;

	appDescriptor.window = {
		
	}
	
 	appDescriptor.window.content = 
    eXo.core.TemplateEngine.merge("eXo/widget/info/UIInfoWidget.jstmpl", appDescriptor, "/exo.app.web/javascript/") ;
 	appDescriptor.window.removeApplication = 
 		"eXo.widget.web..UIInfoWidget.destroyInstance('" + appDescriptor.appId + "');";
 	
 	var innerHTML = eXo.core.TemplateEngine.merge("eXo/desktop/UIWindow.jstmpl", appDescriptor);
 	var applicationNode = DOMUtil.createElementNode(innerHTML, "div");
 	applicationNode.applicationDescriptor = appDescriptor;
 	return applicationNode ;
};

UIInfoWidget.prototype.initApplication = function(applicationId, instanceId) {
	if(instanceId == null) {
	  instanceId = eXo.core.DOMUtil.generateId(applicationId);
	  var application = "eXo.widget.web.info.UIInfoWidget";
	  eXo.desktop.UIDesktop.saveJSApplication(application, applicationId, instanceId);
  }

	var appDescriptor = 
	  new eXo.application.ApplicationDescriptor(instanceId, eXo.widget.web.UIInfoWidget);
	  
	var appInstance = appDescriptor.createApplication();
	eXo.desktop.UIDesktop.addJSApplication(appInstance);
}

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

if(exo.widget == null) exo.widget = {} ;
if(exo.widget.web == null) exo.widget.web = {} ;
eXo.widget.web.info.UIInfoWidget = new UIInfoWidget()  ;
