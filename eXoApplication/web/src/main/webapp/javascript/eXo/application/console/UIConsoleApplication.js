eXo.require('eXo.core.TemplateEngine');
eXo.require('eXo.application.ApplicationDescriptor');
eXo.require('eXo.application.console.Command', '/eXoAppWeb/javascript/');
eXo.require('eXo.application.console.EnvManager', '/eXoAppWeb/javascript/');
eXo.require('eXo.application.console.CommandManager', '/eXoAppWeb/javascript/');
eXo.require('eXo.core.OS');
eXo.require('eXo.core.HTMLUtil');
eXo.require('eXo.core.DefaultKeyboardListener');
eXo.require('eXo.core.SimpleNodeEditor');
eXo.require('eXo.core.Keyboard');
eXo.require('eXo.core.Editor');

function UIConsoleApplication() {
	this.appCategory = "web" ;
	this.appName = "console" ;
	this.appIcon = "/eXoResources/skin/portal/webui/component/view/UIPageDesktop/DefaultSkin/icons/80x80/Register.png";
	this.skin = {
	  Default: "/eXoAppWeb/skin/console/DefaultStylesheet.css",
	  Mac:     "/eXoAppWeb/skin/console/MacStylesheet.css",
	  Vista:   "/eXoAppWeb/skin/console/VistaStylesheet.css"
	} ;
};

UIConsoleApplication.prototype.createApplicationInstance = function(appDescriptor) {
	var DOMUtil = eXo.core.DOMUtil ;

	var webConsoleDetector = document.getElementById("WebConsoleApplicationDetector");
	var cssStyle = webConsoleDetector.getAttribute('cssStyle') ;
	
	appDescriptor.window = {
		cssElementStyle : cssStyle
	}
	
 	appDescriptor.window.content = 
    eXo.core.TemplateEngine.merge("eXo/application/console/UIConsoleApplication.jstmpl", appDescriptor, "/eXoAppWeb/javascript/") ;
 	appDescriptor.window.removeApplication = 
 		"eXo.application.console.UIConsoleApplication.destroyInstance('" + appDescriptor.appId + "');";
 	
 	var innerHTML = eXo.core.TemplateEngine.merge("eXo/desktop/UIWindow.jstmpl", appDescriptor);
 	var applicationNode = DOMUtil.createElementNode(innerHTML, "div");
 	applicationNode.applicationDescriptor = appDescriptor;
 	return applicationNode ;
};

UIConsoleApplication.prototype.initApplication = function(applicationId, instanceId) {
	var appDescriptor = 
	  new eXo.application.ApplicationDescriptor(instanceId, eXo.application.console.UIConsoleApplication);

	var appInstance = appDescriptor.createApplication();
	eXo.desktop.UIDesktop.addJSApplication(appInstance);
  eXo.core.Editor.registerEditors(appInstance) ;
};

UIConsoleApplication.prototype.destroyApplicationInstance = function(appDescriptor) {
	var applicationNode = document.getElementById(appDescriptor.appId);
	return applicationNode ;
};

/*##############################################################################################*/
UIConsoleApplication.prototype.destroyInstance = function(instanceId) {
	if(confirm("Are you sure you want to delete this application?")) {
    var appDescriptor = 
      new eXo.application.ApplicationDescriptor(instanceId, eXo.application.console.UIBConsoleApplication);
    
    var removeAppInstance = appDescriptor.destroyApplication();
    eXo.desktop.UIDesktop.removeJSApplication(removeAppInstance);
  }	
};

if(!eXo.application.console) {
  eXo.application.console = {} ;
}

eXo.application.console.UIConsoleApplication = new UIConsoleApplication() ;

UIConsoleApplication.prototype.getUIMaskWorkspace = function() {
  var uiConsoleApplicationNode = document.getElementById("uiConsoleApplication") ;
  var nodeLst = uiConsoleApplicationNode.getElementsByTagName('DIV') ;
  for (var node in nodeLst) {
    if (nodeLst[node].className == 'UIMaskWindowConsole') {
      return nodeLst[node] ;
    }
  }
  return false ;
}

UIConsoleApplication.prototype.showMaskWorkspace = function() {
  var uiMaskWorkspace = this.getUIMaskWorkspace() ;
  if (uiMaskWorkspace) {
    uiMaskWorkspace.style.display = 'block' ;
  }
};

UIConsoleApplication.prototype.hideMaskWorkspace = function() {
	var uiMaskWorkspace = this.getUIMaskWorkspace() ;
  if (uiMaskWorkspace) {
    uiMaskWorkspace.style.display = 'none' ;
  }
};
