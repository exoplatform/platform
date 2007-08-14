eXo.require('eXo.core.TemplateEngine');
eXo.require('eXo.application.ApplicationDescriptor');

function UIBrowserApplication() {
	this.appCategory = "web" ;
	this.appName = "browser" ;
	this.appIcon = "/eXoResources/skin/DefaultSkin/portal/webui/component/view/UIPageDesktop/icons/80x80/Register.png";
	this.skin = {
	  Default: "/eXoAppWeb/skin/browser/DefaultStylesheet.css",
	  Mac:     "/eXoAppWeb/skin/browser/MacStylesheet.css",
	  Vista:   "/eXoAppWeb/skin/browser/VistaStylesheet.css"
	} ;
};

UIBrowserApplication.prototype.createApplicationInstance = function(appDescriptor) {
	var DOMUtil = eXo.core.DOMUtil ;

	var webBrowserDetector = document.getElementById("WebBrowserApplicationDetector");
	var cssStyle = webBrowserDetector.getAttribute('cssStyle') ;
	
	appDescriptor.window = {
		cssElementStyle : cssStyle
	}
 	appDescriptor.window.content = eXo.core.TemplateEngine.merge("eXo/application/browser/UIBrowserApplication.jstmpl", appDescriptor, "/eXoAppWeb/javascript/") ;
 	appDescriptor.window.removeApplication = 
 		"eXo.application.browser.UIBrowserApplication.destroyBrowserInstance('" + appDescriptor.appId + "');";
 	
 	var innerHTML = eXo.core.TemplateEngine.merge("eXo/desktop/UIWindow.jstmpl", appDescriptor);
 	var applicationNode = DOMUtil.createElementNode(innerHTML, "div");
 	applicationNode.applicationDescriptor = appDescriptor;
 	 	
 	return applicationNode ;
};

UIBrowserApplication.prototype.destroyApplicationInstance = function(appDescriptor) {
	var applicationNode = document.getElementById(appDescriptor.appId);
	return applicationNode ;
};

/*##############################################################################################*/
// phuong thuc nay duoc trieu goi 2 lan
UIBrowserApplication.prototype.initApplication = function(applicationId, instanceId) {

	var appDescriptor = 
	  new eXo.application.ApplicationDescriptor(instanceId, eXo.application.browser.UIBrowserApplication);
	  
	var appInstance = appDescriptor.createApplication();
	eXo.desktop.UIDesktop.addJSApplication(appInstance);
	eXo.application.browser.UIBrowserApplication.init(instanceId);
};

UIBrowserApplication.prototype.destroyBrowserInstance = function(instanceId) {
	var appDescriptor = 
	  new eXo.application.ApplicationDescriptor(instanceId, eXo.application.browser.UIBrowserApplication);
	
	var removeAppInstance = appDescriptor.destroyApplication();
	if(confirm("Are you sure you want to delete this application?")) {
    eXo.desktop.UIDesktop.removeJSApplication(removeAppInstance);
  }	
};

UIBrowserApplication.prototype.init = function(instanceId) {
	var DOMUtil = eXo.core.DOMUtil ;
  this.NumberOfTab = 1 ;
  this.maxWidth = 150 ;
  var uiWindow = document.getElementById(instanceId) ; 
  var eXoBrowser = DOMUtil.findFirstDescendantByClass(uiWindow, "div", "eXoBrowser");
	/*##############-Register Event for ToolbarButton-###############*/
	
	var buttonContainer = DOMUtil.findFirstDescendantByClass(eXoBrowser, "div","ButtonContainer") ;
	var buttonBackground = DOMUtil.findChildrenByClass(buttonContainer, "div", "ToolbarButton") ;
	for(var i = 0; i < buttonBackground.length; i++) {
		var button = DOMUtil.getChildrenByTagName(buttonBackground[i], "div")[0] ;
		button.onmouseover = function() {
			eXo.application.browser.UIBrowserApplication.onMouseOver(this.parentNode, 'ToolbarButton', 'ToolbarButtonOver', true) ;
		}
		
		button.onmouseout = function() {			
			eXo.application.browser.UIBrowserApplication.onMouseOver(this.parentNode, 'ToolbarButton', 'ToolbarButtonOver', false) ;
		}
	}
	
	/*###############################################################*/
	var txtAddress = DOMUtil.findFirstDescendantByClass(eXoBrowser, "input", "txtAddress") ;
	txtAddress.onkeypress = eXo.application.browser.UIBrowserApplication.onKeyPress ;

	var firstCloseButton = DOMUtil.findFirstDescendantByClass(eXoBrowser, "div", "CloseButton") ;
	firstCloseButton.onclick = function() {
		eXo.application.browser.UIBrowserApplication.removeTabDetail(this) ;
	};
		
	var firstTabDetail = firstCloseButton.parentNode.parentNode ;
	firstTabDetail.index = 0 ;
	firstTabDetail.maxWidth = this.maxWidth ;
	firstTabDetail.onclick = function() {
		eXo.application.browser.UIBrowserApplication.activateTabDetail(this, eXoBrowser) ;
	};
	
	var iframe = DOMUtil.findFirstDescendantByClass(eXoBrowser, "iframe", "IFrame") ;
	iframe.style.display = "block" ;
} ;

UIBrowserApplication.prototype.onKeyPress = function(e) {
	var _e = null ;
	var srcElement = null ;
	if (window.event) {
		_e = window.event ;
		srcElement = _e.srcElement ;
	} else {
		_e = e ;
		srcElement = _e.target ;
	}	
	if(_e.keyCode == 13) {
		var addressBarContainer = eXo.core.DOMUtil.findAncestorByClass(srcElement, "AddressBarContainer") ;
		var obj = eXo.core.DOMUtil.findPreviousElementByTagName(addressBarContainer, "a") ;
		eXo.application.browser.UIBrowserApplication.getUrl(obj) ;
	}
} ;

UIBrowserApplication.prototype.convertURL = function(url) {
	var content = url.substr(7) ;
	if(content.indexOf("www") >= 0 || content.indexOf("WWW") >= 0) {
		content = content.substr(4) ;
		var dotIndex = content.indexOf(".") ;
		content = content.substr(0, dotIndex) ;
	} else {
		var dotIndex = content.indexOf(".") ;
		if(dotIndex > 0) content = content.substr(0, dotIndex) ;
		else content = content.substr(0, content.indexOf("/")) ;
	}
	return content ;
} ;

UIBrowserApplication.prototype.getUrl = function(obj) {
	var DOMUtil = eXo.core.DOMUtil ;
	var	addressBarContainer = DOMUtil.findNextElementByTagName(obj, "div") ; 
	var eXoBrowser = DOMUtil.findAncestorByClass(obj,"eXoBrowser") ;
	var txtAddress = DOMUtil.findFirstDescendantByClass(addressBarContainer, "input", "txtAddress") ;	
	var iframes = DOMUtil.findDescendantsByClass(eXoBrowser, "iframe", "IFrame") ;
	var tabContainer = DOMUtil.findFirstDescendantByClass(eXoBrowser, "div", "TabContainer") ;
	var tabLabels = DOMUtil.findDescendantsByClass(tabContainer, "div", "TabLabel") ;
	var src = txtAddress.value ;
  for(var i = 0; i < iframes.length; i++) {
  	if(iframes[i].style.display != "none") {
  		if (src.indexOf("http") < 0){
  			src = "http://"+ src ;
  			txtAddress.value = src ;
  		}
			iframes[i].src = src ;			
			tabLabels[i].innerHTML = this.convertURL(src) ;
		}
  }
  
} ;

UIBrowserApplication.prototype.onMouseOver = function(object, normalClass, activeClass, isOver) {
	if(isOver) {
		object.className = activeClass ;
	} else {
		object.className = normalClass ;
	}
};

UIBrowserApplication.prototype.createNewTab = function(clickedElement) {
	var DOMUtil = eXo.core.DOMUtil;
	var ancestorNode = DOMUtil.findAncestorByClass(clickedElement, "BrowserContent") ;
	var uiToolbar = DOMUtil.findPreviousElementByTagName(ancestorNode, "div") ;
  var txtAddress = DOMUtil.findFirstDescendantByClass(uiToolbar, 'input', "txtAddress") ;
  this.NumberOfTab++ ;
  txtAddress.value = "http://" ;
  
  
  var tabParent = clickedElement.parentNode ;
  
  var activeTabList = DOMUtil.findChildrenByClass(tabParent, "div", "ActiveTabDetailBackground") ;
  for(var i = 0; i < activeTabList.length; i++) {
  	activeTabList[i].className = "TabDetailBackground TabMenuItem" ;
  }
  var cloneActiveTab = activeTabList[i-1].cloneNode(true) ;
  cloneActiveTab.className = "ActiveTabDetailBackground TabMenuItem" ;
  cloneActiveTab.index = this.NumberOfTab - 1 ;
  cloneActiveTab.maxWidth = this.maxWidth ;
  var tabLabel = DOMUtil.findFirstDescendantByClass(cloneActiveTab, "div", "TabLabel") ;
  tabLabel.innerHTML = "(Untitled)" ;
  cloneActiveTab.onclick = function() {
  	eXo.application.browser.UIBrowserApplication.activateTabDetail(this, ancestorNode) ;
  } ;
  var closeButton = DOMUtil.findFirstDescendantByClass(cloneActiveTab, "div", "CloseButton") ;
  closeButton.onclick = function() {
  	eXo.application.browser.UIBrowserApplication.removeTabDetail(this) ;
  } ;
  clickedElement.parentNode.insertBefore(cloneActiveTab, clickedElement) ;
  
	var firstCloseButton = DOMUtil.findFirstDescendantByClass(tabParent, "div", "CloseButton") ;
  if(firstCloseButton.style.display == "none") {
	  var closeButton = DOMUtil.findDescendantsByClass(tabParent, "div", "CloseButton") ;
	  for(var i = 0; i < closeButton.length; i++) {
	  	if(closeButton[i].style.display == "none") closeButton[i].style.display = "block" ;
	  }
  }  

  var separator = DOMUtil.findFirstDescendantByClass(ancestorNode, "div", "Separator") ;
  var cloneSeparator = separator.cloneNode(true) ;
  clickedElement.parentNode.insertBefore(cloneSeparator, clickedElement) ;
  
  var iframes = DOMUtil.findDescendantsByClass(ancestorNode, "iframe", "IFrame") ;
  for(var j = 0; j < iframes.length; j++) {
  	iframes[j].style.display = "none" ;
  }
 	
 	var tabContent = DOMUtil.findFirstDescendantByClass(ancestorNode, "div", "TabContent") ;
  var newIFrame = document.createElement("iframe") ;
  newIFrame.className = "IFrame" ;
  newIFrame.style.display = "block" ;
  newIFrame.frameBorder = "0" ;
  tabContent.appendChild(newIFrame) ;
  
  this.resizeTabDetail(tabParent) ;
} ;

UIBrowserApplication.prototype.activateTabDetail = function(selectedElement, ancestor) {
	var DOMUtil = eXo.core.DOMUtil ;
	var tabContainer = DOMUtil.findFirstDescendantByClass(ancestor, "div", "TabContainer") ;
	var tabMenuItems = DOMUtil.findChildrenByClass(tabContainer, "div", "TabMenuItem") ;

	var activeTab = DOMUtil.findFirstDescendantByClass(tabContainer, "div", "ActiveTabDetailBackground") ;
	activeTab.className = "TabDetailBackground TabMenuItem" ;
	selectedElement.className = "ActiveTabDetailBackground TabMenuItem" ;
	var txtAddress = null ;

  var uiToolbar = DOMUtil.findPreviousElementByTagName(DOMUtil.findAncestorByClass(selectedElement, 'BrowserContent'), 'div') ;
	txtAddress = DOMUtil.findFirstDescendantByClass(uiToolbar, "input", "txtAddress") ;
	var iframes = DOMUtil.findDescendantsByClass(ancestor, "iframe", "IFrame") ;
	for(var j = 0; j < iframes.length; j++) {
		if (tabMenuItems[j] == selectedElement) {
			iframes[j].style.display = "block" ;
			txtAddress.value = (iframes[j].src !="")?iframes[j].src : "http://" ;
		} else {
	  	iframes[j].style.display = "none" ;
		}
  }
} ;

UIBrowserApplication.prototype.removeTabDetail = function(clickedElement) {
	var DOMUtil = eXo.core.DOMUtil ;
	if(this.NumberOfTab > 1) {
		this.NumberOfTab-- ;
		
		var eXoBrowser = DOMUtil.findAncestorByClass(clickedElement, "eXoBrowser") ;
		var txtAddress = DOMUtil.findFirstDescendantByClass(eXoBrowser, "input", "txtAddress") ;
		var tabDetail = (clickedElement.parentNode).parentNode ;
		var tabIndex ;
		tabIndex = tabDetail.index ;
		var tabContainer = tabDetail.parentNode ;
		var separator = DOMUtil.findDescendantsByClass(tabContainer, "div", "Separator") ;
		
		var tabContent = DOMUtil.findFirstDescendantByClass(eXoBrowser, "div", "TabContent") ;
		var iframes = DOMUtil.findDescendantsByClass(tabContent, "iframe", "IFrame") ;

		if(tabDetail.className == "ActiveTabDetailBackground") {
			var tabDetailList = DOMUtil.findDescendantsByClass(tabContainer, "div", "TabDetailBackground") ;
			var index ;
			if(tabIndex - 1 >= 0) {
				index = tabIndex - 1 ;
				iframes[index].style.display = "block" ;
				txtAddress.value = iframes[index].src ;
			} else {
				index = tabIndex ;
				iframes[index + 1].style.display = "block" ;
				txtAddress.value = iframes[index + 1].src ;
			}
			tabDetailList[index].className = "ActiveTabDetailBackground" ;
		}

		tabContainer.removeChild(separator[tabIndex]) ;
		tabContainer.removeChild(tabDetail) ;
		tabContent.removeChild(iframes[tabIndex]) ;
		
		this.resizeTabDetail(tabContainer) ;
		
		eXo.application.browser.UIBrowserApplication.resetIndex(eXoBrowser) ;
		if(this.NumberOfTab <= 1) {
			var firstCloseButton = DOMUtil.findFirstDescendantByClass(tabContainer, "div", "CloseButton") ;
			firstCloseButton.style.display = "none" ;
		}
	} else {
		clickedElement.style.display = "none" ;
	}
} ;

UIBrowserApplication.prototype.resetIndex = function(eXoBrowser) {
	var tabContainer = eXo.core.DOMUtil.findFirstDescendantByClass(eXoBrowser, "div", "TabContainer") ;
	var children = eXo.core.DOMUtil.getChildrenByTagName(tabContainer, "div") ;
	var index = 0 ;
	for(var i = 0; i < children.length; i++) {
		if((children[i].className != "Separator") && (children[i].className.indexOf("TabDetail") >= 0) && children[i]) {
			children[i].index = index ;
			index++ ;
		}
	}
} ;

UIBrowserApplication.prototype.refreshIFrame = function(obj) {
	var eXoBrowser = eXo.core.DOMUtil.findAncestorByClass(obj, "eXoBrowser");
	var iframes = eXo.core.DOMUtil.findDescendantsByClass(eXoBrowser, "iframe", "IFrame") ;
	
	for(var i = 0; i < iframes.length; i++) {
  	if(iframes[i].style.display == "block") {  		
			iframes[i].src = iframes[i].src ;
		}
  }
} ;
UIBrowserApplication.prototype.goBack = function(obj) {

	var eXoBrowser = eXo.core.DOMUtil.findAncestorByClass(obj, "eXoBrowser");
	var iframes = eXo.core.DOMUtil.findDescendantsByClass(eXoBrowser, "iframe", "IFrame") ;
	
	for(var i = 0; i < iframes.length; i++) {
  	if(iframes[i].style.display == "block") {
			iframes[i].contentWindow.history.back() ;
		}
  }
}

UIBrowserApplication.prototype.goForward = function(obj) {
	var eXoBrowser = eXo.core.DOMUtil.findAncestorByClass(obj, "eXoBrowser");
	var iframes = eXo.core.DOMUtil.findDescendantsByClass(eXoBrowser, "iframe", "IFrame") ;
	
	for(var i = 0; i < iframes.length; i++) {
  	if(iframes[i].style.display == "block") {
			iframes[i].contentWindow.history.forward() ;
		}
  }
}

UIBrowserApplication.prototype.resizeTabDetail = function(tabContainer) {
	var DOMUtil = eXo.core.DOMUtil ;
	var children = DOMUtil.getChildrenByTagName(tabContainer, "div") ;
	
	var sumOfWidth = tabContainer.offsetWidth ;
	var sumOfElementWidth = 0 ;
	var sumOfSeparatorWidth = 0 ;
	var sumOfTabDetail = 0 ;
	var newTabWidth = 0 ;
	var labelWidth = 0 ;
	
	for(var i = 0; i < children.length; i++) {
		if(children[i].className != "") {
			if(children[i].className == "Separator") {
				sumOfSeparatorWidth += children[i].offsetWidth ;
			}
			else if(children[i].className == "NewTabButtonOver" || children[i].className == "NewTabButton") {
				newTabWidth = children[i].offsetWidth ;
			}
			else {
				sumOfTabDetail++ ;
			}
			sumOfElementWidth += children[i].offsetWidth ;
		}
	}
	var tabDetailWidth = (sumOfWidth - sumOfSeparatorWidth - newTabWidth - 15)/sumOfTabDetail ;
	if(sumOfElementWidth <= sumOfWidth) {
		var computedWidth = (children[1].maxWidth > tabDetailWidth) ? tabDetailWidth : children[1].maxWidth ;
		labelWidth = computedWidth - 50 ;
		for(var i = 0; i < children.length; i++) {
			if(children[i].className != "Separator" && children[i].className != "" && children[i].className != "NewTabButton" && children[i].className != "NewTabButtonOver") {
				children[i].style.width = computedWidth + "px" ;
			}
		}
	}	else {
		labelWidth = tabDetailWidth - 50 ;
		for(var i = 0; i < children.length; i++) {
			if(children[i].className != "Separator" && children[i].className != "" && children[i].className != "NewTabButton" && children[i].className != "NewTabButtonOver") {
				children[i].style.width = tabDetailWidth + "px" ;
			}
		}
	}
	
	if(eXo.core.Browser.isIE6()) {
		var tabLabel = DOMUtil.findDescendantsByClass(tabContainer, "div", "TabLabel") ;
		for(var i = 0 ; i < tabLabel.length; i++) {
			tabLabel[i].style.width = labelWidth + "px" ;
		}
	}
} ;

UIBrowserApplication.prototype.stopLoad = function() {
	var detect = navigator.userAgent.toLowerCase() ;
	if (detect.indexOf("msie") > -1) {
		document.execCommand('Stop') ;
	} else {
		window.stop() ;
	}
} ;

eXo.application.browser.UIBrowserApplication = new UIBrowserApplication() ;
