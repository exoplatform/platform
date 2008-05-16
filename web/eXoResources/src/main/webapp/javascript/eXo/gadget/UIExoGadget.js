eXo.require('eXo.gadget.UIAddGadget');
eXo.require('eXo.gadget.UIGadget') ;
function UIExoGadget() {
}
/*
* This method is used by any user gadget to init itself
*
* - The gadget name is is extracted from the full class name
* - A gadget can have an icon to be then used in several locations like the application registry
* - A gadget must have a width of maximum 220px to fit in the left column of eXo Portal
* - A gadget can provide different skins but only a default is mandatory
* 
*/
UIExoGadget.prototype.init = function(appName, appFolder, attrsGadget, webAppName) {
  this.appCategory = "eXoGadgetWeb" ;
	this.webAppName = webAppName || "eXoGadgetWeb";
	this.appName = appName ;
	this.attrsGadget = attrsGadget;
	this.appFolder = appFolder;
	var nameGadget = this.getNameGadget(appName);
	this.appIcon = "/eXoResources/skin/DefaultSkin/portal/webui/component/view/UIPageDesktop/icons/80x80/"+nameGadget+".png" ;
	this.skin = {
	  Default: "/eXoGadgetWeb/skin/"+appFolder+"/DefaultStylesheet.css",
	  Mac:     "/eXoGadgetWeb/skin/"+appFolder+"/MacStylesheet.css",
	  Vista:   "/eXoGadgetWeb/skin/"+appFolder+"/VistaStylesheet.css"
	} ;
	this.width = "220px" ;
	this.height = "auto" ;
};

UIExoGadget.prototype.createApplicationInstance = function(appDescriptor) {
	var instance = new Object();
	var DOMUtil = eXo.core.DOMUtil ;	
	var appElement = document.getElementById(this.appName);
	if(appElement == null) return;
	
	appDescriptor.gadget = {
		positionX : appElement.getAttribute('posX'),
		positionY : appElement.getAttribute('posY'),
		zIndex : appElement.getAttribute('zIndex'),
		
		uiGadget : {		
		}		
	};
	var setGadgetData = 'appDescriptor.gadget.uiGadget = { ' + 
												 'temporaty : appElement, ' +
												 'appId : appElement.getAttribute(\'applicationId\'), ' +
												 'url : appElement.getAttribute(\'url\') ' ;
	if(this.attrsGadget != null && this.attrsGadget.length > 0) {											 								 
  		for(var i = 0; i < this.attrsGadget.length; i++) {
  			var attrsGadget =   this.attrsGadget[i];
  			setGadgetData +=   ',' + attrsGadget + ': appElement.getAttribute(\''+attrsGadget+'\')';  	 
  		}												 
	}
	setGadgetData += '};';

  	eval(setGadgetData);
												
 	appDescriptor.gadget.content = 
    eXo.core.TemplateEngine.merge("eXo/gadgets/web/"+this.appFolder+"/"+this.appName+".jstmpl", appDescriptor, "/" + this.webAppName + "/javascript/") ;
    
 	appDescriptor.gadget.removeApplication = 
 		"eXo.gadgets.web."+this.appName+".destroyInstance('" + appDescriptor.appId + "');";
 	
 	var innerHTML = eXo.core.TemplateEngine.merge("eXo/gadget/UIGadget.jstmpl", appDescriptor);
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
UIExoGadget.prototype.initApplication = function(applicationId, instanceId) {	
	var DOMUtil = eXo.core.DOMUtil;	
	var appDescriptor =  new eXo.application.ApplicationDescriptor(instanceId, this);
	var appInstance = appDescriptor.createApplication();
	if(appInstance == null) return;
	
	appInstance.id = appInstance.applicationDescriptor.gadget.uiGadget.appId;
	var appElement = appInstance.applicationDescriptor.gadget.uiGadget.temporaty;
	
	var uiPageDesktop = DOMUtil.findAncestorByClass(appElement, "UIPageDesktop") ;
	if(uiPageDesktop == null) eXo.gadget.UIAddGadget.addGadget(appInstance);
	else	eXo.gadget.UIAddGadget.addGadgetToDesktop(appInstance) ;

	if (this.onLoad) {
		this.onLoad(appInstance.id,appInstance.applicationDescriptor.gadget.uiGadget.url);
	}
	DOMUtil.removeElement(appElement);
};


UIExoGadget.prototype.destroyApplicationInstance = function(appDescriptor) {
	var applicationNode = document.getElementById(appDescriptor.appId);
	return applicationNode ;
};

UIExoGadget.prototype.destroyInstance = function(instanceId) {
	if(!confirm("Are you sure you want to delete this application?")) return;
  var appDescriptor = new eXo.application.ApplicationDescriptor(instanceId, this);
  var removeAppInstance = appDescriptor.destroyApplication();
  eXo.desktop.UIDesktop.removeJSApplication(removeAppInstance);
};

/*
* This method allows to extract the gadget name from the full class name
*/
UIExoGadget.prototype.getNameGadget = function(nameGadget) {
	var strlabel = nameGadget.charAt(2); 
	for(var i = 3; i < nameGadget.length; i ++) {
		strlabel = strlabel + nameGadget.charAt(i);
	}
	return strlabel;
};

eXo.gadget.UIExoGadget = UIExoGadget;
