eXo.require('eXo.core.TemplateEngine');
eXo.require('eXo.application.ApplicationDescriptor');
eXo.require('eXo.webui.UIPopupWindow');

function UICalendarApplication() {
	this.appCategory = "groupware" ;
	this.appName = "Calendar" ;
	this.appIcon = "/eXoResources/skin/portal/webui/component/view/UIPageDesktop/DefaultSkin/icons/80x80/eXoCalendar.png";
	this.skin = {
	  Default: "/calendar/skin/DefaultStylesheet.css",
	  Mac: "/calendar/skin/MacStylesheet.css",
	  Vista: "/calendar/skin/VistaStylesheet.css"
	} ;
	this.minWidth = 850 ;
};


UICalendarApplication.prototype.createApplicationInstance = function(appDescriptor) {
	var DOMUtil = eXo.core.DOMUtil ;
	
	appDescriptor.window = {
		popup: {
			title: "",
			popupId: "",
			content: "",
			style: ""
		}
	}
	
 	appDescriptor.window.content = eXo.core.TemplateEngine.merge("UICalendarApplication.jstmpl", appDescriptor, "/calendar/javascript/eXo/application/calendar/") ;
 	appDescriptor.window.removeApplication = 
 		"eXo.application.calendar.UICalendarApplication.destroyCalendarInstance('"+appDescriptor.appId+"');";
 	
	var innerHTML = eXo.core.TemplateEngine.merge("eXo/desktop/UIWindow.jstmpl", appDescriptor);
 	var applicationNode = DOMUtil.createElementNode(innerHTML, "div");
 	applicationNode.applicationDescriptor = appDescriptor;
 	return applicationNode ;
};

UICalendarApplication.prototype.destroyApplicationInstance = function(appDescriptor) {
	var applicationNode = document.getElementById(appDescriptor.appId);
	
	return applicationNode ;
};

/*##############################################################################################*/

UICalendarApplication.prototype.createCalendarInstance = function(applicationId, instanceId) {
	if(instanceId == null) {
	  instanceId = eXo.core.DOMUtil.generateId(applicationId);
	  var application = "eXo.application.calendar.UICalendarApplication.createCalendarInstance";
	  eXo.desktop.UIDesktop.saveJSApplication(application, applicationId, instanceId);
  }
	
	var appDescriptor = 
	  new eXo.application.ApplicationDescriptor(instanceId, eXo.application.calendar.UICalendarApplication);
	  
	var appInstance = appDescriptor.createApplication();
	eXo.desktop.UIDesktop.addJSApplication(appInstance);
};

UICalendarApplication.prototype.destroyCalendarInstance = function(instanceId) {
	var appDescriptor = 
	  new eXo.application.ApplicationDescriptor(instanceId, eXo.application.calendar.UICalendarApplication);
	
	var removeAppInstance = appDescriptor.destroyApplication();
	if(confirm("Are you sure you want to delete this application?")) {
    eXo.desktop.UIDesktop.removeJSApplication(removeAppInstance);
  }	
};

UICalendarApplication.prototype.showPopupWindow = function(selectedElement, title, popupId, content, width) {
	var DOMUtil = eXo.core.DOMUtil;
	
	var middleDecoratorCenter = DOMUtil.findAncestorByClass(selectedElement, "MiddleDecoratorCenter");
	var uiWindow = DOMUtil.findAncestorByClass(selectedElement, "UIWindow");
	var appDescriptor = uiWindow.applicationDescriptor ;
	
	appDescriptor.window.popup.title = title ;
	appDescriptor.window.popup.popupId = popupId ;
	appDescriptor.window.popup.content = eXo.core.TemplateEngine.merge(content, appDescriptor);
	appDescriptor.window.popup.style = "CaledarStyle";
	var uiPopupWindowTemplate = eXo.core.TemplateEngine.merge('eXo/webui/UIPopupWindow.jstmpl', appDescriptor);
 	var uiPopupWindowNode = DOMUtil.createElementNode(uiPopupWindowTemplate, "div");
 	uiPopupWindowNode.style.width = width;
 	var contentPoup = DOMUtil.findFirstDescendantByClass(uiPopupWindowNode, "div", "Content");
 	contentPoup.style.height = "255px";
 	middleDecoratorCenter.appendChild(uiPopupWindowNode);
 	eXo.webui.UIPopupWindow.init(appDescriptor.window.popup.popupId, false);
 	
	eXo.webui.UIPopupWindow.show(appDescriptor.window.popup.popupId);
}

UICalendarApplication.prototype.showPopUpWindow = function(selectedElement) {
	var content = "eXo/application/calendar/UICalendarCreatEvent.jstmpl";
	var title = "Creat An Event";
	var popupId = "UICalendarCreatEvent";
	var width = "500px";
	eXo.application.calendar.UICalendarApplication.showPopupWindow(selectedElement, title, popupId, content, width);	 	
};

UICalendarApplication.prototype.showPopUpActionMenu = function(selectedElement, nameClassPopUp) {
	var ancestorClass = eXo.core.DOMUtil.findAncestorByClass(selectedElement, "UICalendarApplication");
	var namePopUp = eXo.core.DOMUtil.findFirstDescendantByClass(ancestorClass, "div", nameClassPopUp);
	var popUpActionMenu = eXo.core.DOMUtil.findAncestorByClass(namePopUp, "PopUpActionMenu");
	var width;
	if(popUpActionMenu == null) {return;}	
	else {
		width = namePopUp.offsetWidth;
		selectedElement.style.position = "relative" ;
		var topElement = selectedElement.offsetTop;
		var leftElement = selectedElement.offsetLeft;
		
		if(popUpActionMenu.style.display == "none") {
			popUpActionMenu.style.position = "absolute";
			popUpActionMenu.style.top = topElement + 20 + "px";
			popUpActionMenu.style.left = leftElement + "px";
			popUpActionMenu.style.display = "block";
			width = namePopUp.offsetWidth;
		} else {
			ancestorClass.style.position = "static" ;
			popUpActionMenu.style.display = "none";
		}
	}	
	popUpActionMenu.style.width = width + "px";
}

UICalendarApplication.prototype.removeApplication = function(selectedElement,IdApplication) {
	var uiApplicationContainer = document.getElementById(IdApplication) ;
	uiApplicationContainer.parentNode.removeChild(uiApplicationContainer) ;
};

eXo.application.calendar.UICalendarApplication = new UICalendarApplication() ;