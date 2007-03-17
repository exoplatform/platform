eXo.require('eXo.core.TemplateEngine');
eXo.require('eXo.webui.UIPopupMenu');
eXo.require('eXo.application.ApplicationDescriptor');

function UIForumApplication() {
	this.appCategory = "groupware";
	this.appName = "Forum";
	this.appIcon = "/eXoResources/skin/portal/webui/component/view/UIPageDesktop/DefaultSkin/icons/80x80/DefaultPortlet.png";
	this.skin = {
		Default: "/forum/skin/DefaultStylesheet.css",
	  Mac: "/forum/skin/MacStylesheet.css",
	  Vista: "/forum/skin/VistaStylesheet.css"	
	};
	this.minWidth = 900;
};	
	
UIForumApplication.prototype.createApplicationInstance = function(appDescriptor) {
	var DOMUtil = eXo.core.DOMUtil;
	appDescriptor.window = {
		
	}
	
	appDescriptor.window.content = eXo.core.TemplateEngine.merge("UIForumUserMode.jstmpl", appDescriptor, "/forum/javascript/eXo/application/forum/");
	appDescriptor.window.removeApplication = 
		"eXo.application.forum.UIForumApplication.destroyForumInstance('"+appDescriptor.appId+"');";
	var innerHTML = eXo.core.TemplateEngine.merge("eXo/desktop/UIWindow.jstmpl", appDescriptor);
	var applicationNode = DOMUtil.createElementNode(innerHTML, "div");
	applicationNode.applicationDescriptor = appDescriptor;
	return applicationNode ;
}	;

UIForumApplication.prototype.createForumInstance = function(applicationId, instanceId) {
	if(instanceId == null) {
		instanceId = eXo.core.DOMUtil.generateId(applicationId);
		var application = "eXo.application.forum.UIForumApplication.createForumInstance";
		eXo.desktop.UIDesktop.saveJSApplication(application, applicationId, instanceId);		
	}
	
	var appDescriptor = new eXo.application.ApplicationDescriptor(instanceId, eXo.application.forum.UIForumApplication);
	
	var appInstance = appDescriptor.createApplication();
	eXo.desktop.UIDesktop.addJSApplication(appInstance);		
};

UIForumApplication.prototype.destroyForumInstance = function(instanceId) {
	var appDescriptor = new eXo.application.ApplicationDescriptor(instanceId, eXo.application.calendar.UICalendarApplication);
	
	var removeAppInstance = appDescriptor.destroyApplication();
	if (confirm("Are you sure want to delete this application ?")) {
		eXo.desktop.UIDesktop.removeJSApplication(removeAppInstance);
	}
};

UIForumApplication.prototype.showPopupMenu = function(popup, direction) {	
  eXo.webui.UIMenu.buildSubmenu(popup, direction) ;      	
};

eXo.application.forum.UIForumApplication = new UIForumApplication() ;
