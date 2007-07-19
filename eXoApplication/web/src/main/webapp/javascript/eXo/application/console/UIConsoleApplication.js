eXo.require('eXo.core.TemplateEngine');
eXo.require('eXo.application.ApplicationDescriptor');
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

//double load initApplication
//minh.js.exo
UIConsoleApplication.prototype.initApplication = function(applicationId, instanceId) {
	var appDescriptor = 
	  new eXo.application.ApplicationDescriptor(instanceId, eXo.application.console.UIConsoleApplication);

	var appInstance = appDescriptor.createApplication();
	eXo.desktop.UIDesktop.addJSApplication(appInstance);
  eXo.core.Editor.registerEditors(appInstance) ;  
};



UIConsoleApplication.prototype.destroyApplicationInstance = function(appDescriptor) {
	var applicationNode = document.getElementById(appDescriptor.appId);
// note this
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

/********************************/
//minh.js.exo
//create a Mask for window command Console.
//reference class : eXo.core.UIMaskLayer;
//in file: UIMaskLayer.js
/*******************************/
UIConsoleApplication.prototype.showMaskWorkspace = function() {
	if ( !document.getElementById("UIMaskWindowConsole") ) {
  	var wsContent = eXo.core.TemplateEngine.merge("eXo/application/console/UIMaskConsoleSpace.jstmpl", null, "/eXoAppWeb/javascript/") ;
  	
    var uiConsoleDisplayArea = document.getElementById("UIConsoleDisplayArea") ;
    var wsNode = eXo.core.DOMUtil.createElementNode(wsContent, "div");
    uiConsoleDisplayArea.appendChild(wsNode) ;
	}
};

UIConsoleApplication.prototype.hideMaskWorkspace = function() {
	var uiMaskWindowConsole = document.getElementById("UIMaskWindowConsole") ;
  if(uiMaskWindowConsole) {
		document.getElementById("UIConsoleDisplayArea").removeChild(uiMaskWindowConsole) ;
  }
};
// end 10:31 22/6/2007
/*******************************/

/**
 * Nguyen Ba Uoc
 * Toggle show/hide MaskWorkspace
 * Date: 12/07/2007
 */
UIConsoleApplication.prototype.toggleMaskWorkspace = function() {
	var maskLayer = document.getElementById("UIMaskWindowConsole") ;
  if(maskLayer) {
    this.hideMaskWorkspace() ;
  } else {
    this.showMaskWorkspace() ;
  }
};