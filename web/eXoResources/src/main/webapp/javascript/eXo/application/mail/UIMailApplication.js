eXo.require('eXo.core.TemplateEngine');
eXo.require('eXo.application.ApplicationDescriptor');
eXo.require('eXo.webui.UIVerticalSlideTabs');
eXo.require('eXo.webui.UIPopupWindow');

function UIMailApplication() {
	this.appCategory = "groupware" ;
	this.appName = "Mail" ;
	this.appIcon = "/eXoResources/skin/portal/webui/component/view/UIPageDesktop/DefaultSkin/icons/80x80/eXoMail.png";
	this.skin = {
	  Default: "/mail/skin/DefaultStylesheet.css",
	  Mac: "/mail/skin/MacStylesheet.css",
	  Vista: "/mail/skin/VistaStylesheet.css"
	} ;
	this.minWidth = 850 ;
};

UIMailApplication.prototype.createApplicationInstance = function(appDescriptor) {
	var DOMUtil = eXo.core.DOMUtil ;
	
	appDescriptor.window = {
		popup: {
			title: "",
			popupId: "",
			content: "" 
		}
	}
	 	 	
 	appDescriptor.window.content = eXo.core.TemplateEngine.merge("UIMailApplication.jstmpl", appDescriptor, "/mail/javascript/eXo/application/mail/") ;
 	appDescriptor.window.removeApplication = 
 		"eXo.application.mail.UIMailApplication.destroyMailInstance('"+appDescriptor.appId+"');";
 	
 	var innerHTML = eXo.core.TemplateEngine.merge("eXo/desktop/UIWindow.jstmpl", appDescriptor);
 	var applicationNode = DOMUtil.createElementNode(innerHTML, "div");
 	applicationNode.applicationDescriptor = appDescriptor;
 	return applicationNode ;
};

UIMailApplication.prototype.destroyApplicationInstance = function(appDescriptor) {
	var applicationNode = document.getElementById(appDescriptor.appId);
	
	return applicationNode ;
};


/*##############################################################################################*/

UIMailApplication.prototype.initApplication = function(applicationId, instanceId) {
	if(instanceId == null) {
	  instanceId = eXo.core.DOMUtil.generateId(applicationId);
	  var application = "eXo.application.mail.UIMailApplication";
	  eXo.desktop.UIDesktop.saveJSApplication(application, applicationId, instanceId);
  }
  
	var appDescriptor = 
	  new eXo.application.ApplicationDescriptor(instanceId, eXo.application.mail.UIMailApplication);
	
	var appInstance = appDescriptor.createApplication();
	eXo.desktop.UIDesktop.addJSApplication(appInstance);
	eXo.webui.UIPopupWindow.init('UIAddAccountMail', false) ;
};

UIMailApplication.prototype.destroyMailInstance = function(instanceId) {
	var appDescriptor = 
	  new eXo.application.ApplicationDescriptor(instanceId, eXo.application.mail.UIMailApplication);
	
	var removeAppInstance = appDescriptor.destroyApplication();
	if(confirm("Are you sure you want to delete this application?")) {
    eXo.desktop.UIDesktop.removeJSApplication(removeAppInstance);
  }	
};

UIMailApplication.prototype.refresh = function(selectedElement) {
	var uiWindow = eXo.core.DOMUtil.findAncestorByClass(selectedElement, "UIWindow");
	var contentContainer = eXo.core.DOMUtil.findFirstDescendantByClass(uiWindow, "div", "MiddleDecoratorCenter");
	contentContainer.innerHTML = eXo.core.TemplateEngine.merge("eXo/application/mail/UIMailApplication.jstmpl");
};

UIMailApplication.prototype.showPopupWindow = function(selectedElement, title, popupId, content, width) {
	var DOMUtil = eXo.core.DOMUtil;
	
	var middleDecoratorCenter = DOMUtil.findAncestorByClass(selectedElement, "MiddleDecoratorCenter");
	var uiWindow = DOMUtil.findAncestorByClass(selectedElement, "UIWindow");
	var appDescriptor = uiWindow.applicationDescriptor ;
	
	appDescriptor.window.popup.title = title ;
	appDescriptor.window.popup.popupId = popupId ;
	appDescriptor.window.popup.content = eXo.core.TemplateEngine.merge(content, appDescriptor);
	var uiPopupWindowTemplate = eXo.core.TemplateEngine.merge('eXo/webui/UIPopupWindow.jstmpl', appDescriptor);
 	var uiPopupWindowNode = DOMUtil.createElementNode(uiPopupWindowTemplate, "div");
 	uiPopupWindowNode.style.width = width;
 	middleDecoratorCenter.appendChild(uiPopupWindowNode);
 	eXo.webui.UIPopupWindow.init(appDescriptor.window.popup.popupId, false);
 	
	eXo.webui.UIPopupWindow.show(appDescriptor.window.popup.popupId);
}

UIMailApplication.prototype.addNewAccount = function(selectedElement) {
	var content = "eXo/application/mail/UIAddAccount.jstmpl";
	var title = "Add New Account";
	var popupId = "UIAddAccountMail";
	var width = "650px";
	eXo.application.mail.UIMailApplication.showPopupWindow(selectedElement, title, popupId, content, width); 	
};

UIMailApplication.prototype.showUIReadMail = function(selectedElement) {
	var content = "eXo/application/mail/UIReadMessage.jstmpl";
	var title = "CheckMail";
	var popupId = "UIReadMail";
	var width = "740px";
	eXo.application.mail.UIMailApplication.showPopupWindow(selectedElement, title, popupId, content, width);	 	
};

UIMailApplication.prototype.showUIComposeForm = function(selectedElement) {
	var content = "eXo/application/mail/UIComposeForm.jstmpl";
	var title = "Send Mail";
	var popupId = "UIComposeForm";
	var width = "740px";
	eXo.application.mail.UIMailApplication.showPopupWindow(selectedElement, title, popupId, content, width);	 	
};


UIMailApplication.prototype.removeApplication = function(selectedElement) {
	var uiApplicationContainer = document.getElementById("UIAddAccountMail") ;	
	uiApplicationContainer.parentNode.removeChild(uiApplicationContainer) ;
};

UIMailApplication.prototype.expandAndCollapse = function(selectedElement, elementByClass, ancestorByClass, classExpandOrCollapse) {
	var ancestorClass = eXo.core.DOMUtil.findAncestorByClass(selectedElement, ancestorByClass);
	var classExpOrCol = eXo.core.DOMUtil.findFirstDescendantByClass(ancestorClass, "div", classExpandOrCollapse);
	selectedElement.style.display = "none";
	var nextElement = eXo.core.DOMUtil.findNextElementByTagName(selectedElement, "div");
	if(nextElement.className == elementByClass){
		classExpOrCol.style.display = "block";
		nextElement.style.display = "block";
	} else {
		classExpOrCol.style.display = "none";
		var elementClass = eXo.core.DOMUtil.findFirstDescendantByClass(ancestorClass, "div", elementByClass);
		elementClass.style.display = "block";
	}
}

UIMailApplication.prototype.expandAndCollapseMail = function(selectedElement) {
	var middleRightSideBoxDecorator = eXo.core.DOMUtil.findAncestorByClass(selectedElement, "MiddleRightSideBoxDecorator");
	var boxDecoratorContainer = eXo.core.DOMUtil.findAncestorByClass(selectedElement, "BoxDecoratorContainer");
	var boxDecoratorContent = eXo.core.DOMUtil.findAncestorByClass(selectedElement, "BoxDecoratorContent");
	if(boxDecoratorContent == null){
	  boxDecoratorContainer.style.display = "none";
	  boxDecoratorContent = eXo.core.DOMUtil.findNextElementByTagName(boxDecoratorContainer, "div");
	  boxDecoratorContent.style.display = "block";
	} else {
	  boxDecoratorContent.style.display = "none";
	  boxDecoratorContainer = eXo.core.DOMUtil.findFirstDescendantByClass(middleRightSideBoxDecorator, "div", "BoxDecoratorContainer");
	  boxDecoratorContainer.style.display = "block";
	}
}

UIMailApplication.prototype.showPopUpActionMenu = function(selectedElement, nameClassPopUp) {
	var ancestorClass = eXo.core.DOMUtil.findAncestorByClass(selectedElement, "UIMailApplication");
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
			popUpActionMenu.style.top = topElement + 30 + "px";
			popUpActionMenu.style.left = leftElement + "px";
			popUpActionMenu.style.display = "block";
			width = namePopUp.offsetWidth;
		} else {
			ancestorClass.style.position = "static" ;
			popUpActionMenu.style.display = "none";
		}
	}	
	popUpActionMenu.style.width = width + 20 + "px";
}

eXo.application.mail.UIMailApplication = new UIMailApplication() ;