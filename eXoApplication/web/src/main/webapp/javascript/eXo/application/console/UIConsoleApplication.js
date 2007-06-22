eXo.require('eXo.core.TemplateEngine');
eXo.require('eXo.application.ApplicationDescriptor');
eXo.require('eXo.application.console.Editor', '/eXoAppWeb/javascript/');

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
//	if(instanceId == null) {
//	  instanceId = eXo.core.DOMUtil.generateId(applicationId);
//	  var application = "eXo.application.console.UIConsoleApplication";
//	  eXo.desktop.UIDesktop.saveJSApplication(application, applicationId, instanceId);
//  }

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


eXo.application.console  = {
  UIConsoleApplication : new UIConsoleApplication()
} ;

/********************************/
//create a Mask for window command Console.
//start: 4:39 20/6/2007
//reference class : eXo.core.UIMaskLayer;
//in file: UIMaskLayer.js
/*******************************/
UIConsoleApplication.prototype.showMaskWorkspace = function() {
	if ( !document.getElementById("UIMaskWindowConsole") ) {
	document.body.scroll = "no";
	var wsContent = eXo.core.TemplateEngine.merge("eXo/application/console/UIMaskConsoleSpace.jstmpl", null, "/eXoAppWeb/javascript/") ;
	var context = {
			uiMaskWorkspace : {
			width : "50%",
			content : wsContent
		}
	}
	var uiMaskWorkspace = eXo.core.TemplateEngine.merge("eXo/portal/UIMaskWorkspace.jstmpl", context) ;
	var uiMaskWorkspaceElement = eXo.core.DOMUtil.createElementNode(uiMaskWorkspace, "div") ;
			eXo.core.UIMaskLayer.createMask("UIConsoleDisplayArea", uiMaskWorkspaceElement, 64) ;
	var maskLayer = document.getElementById("MaskLayer") ;
			maskLayer.style.height =  "100%";
			maskLayer.style.width =  "100%";
			maskLayer.style.position = "absolute";
	var infoObj =  document.getElementById("UIMaskWindowConsole");
	//fix height.
	var fixObj =	document.getElementById("UIMaskWorkspaceJSTemplate");
	var maHeight = eXo.core.DOMUtil.findDescendantsByClass(fixObj, "div", "UIDescendantDetector");
	for (k=0; k < maHeight.length; k ++) {
		maHeight[k].style.height = "100%";
		maHeight[k].style.overflow =  "hidden";
	}
	//fix position.
	fixObj.style.position = "absolute";
	fixObj.style.height = "60%";
	fixObj.style.top =  "20%";
	fixObj.style.left =  "25%";	
	fixObj.style.overflow =  "hidden";
	//disable scroll for eXoConsoleResult
	if (document.getElementById("eXoConsoleResult").scroll) {alert("ol")};
	}
};

UIConsoleApplication.prototype.hiddenMaskWorkspace = function() {
	var maskLayer = document.getElementById("MaskLayer") ;
			eXo.core.UIMaskLayer.removeMask(maskLayer);
};
// end 10:31 22/6/2007
/*******************************/