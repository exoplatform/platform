function BrowserApplication() {
};

BrowserApplication.prototype.init = function(instanceId) {
	var DOMUtil = eXo.core.DOMUtil ;
	this.NumberOfTab = 1 ;
	var eXoBrowser = document.getElementById(instanceId) ;
	
	var buttonContainer = DOMUtil.findFirstDescendantByClass(eXoBrowser, "div","ButtonContainer") ;
	var buttonBackground = DOMUtil.findChildrenByClass(buttonContainer, "div", "ToolbarButton") ;
	for(var i = 0; i < buttonBackground.length; i++) {
		var button = DOMUtil.getChildrenByTagName(buttonBackground[i], "div")[0] ;
		button.onmouseover = function() {
			eXo.application.browser.BrowserApplication.onMouseOver(this.parentNode, 'ToolbarButton', 'ToolbarButtonOver', true) ;
		}
		
		button.onmouseout = function() {			
			eXo.application.browser.BrowserApplication.onMouseOver(this.parentNode, 'ToolbarButton', 'ToolbarButtonOver', false) ;
		}
	}
	
	var txtAddress = DOMUtil.findFirstDescendantByClass(eXoBrowser, "input", "txtAddress") ;
	txtAddress.onkeypress = eXo.application.browser.BrowserApplication.onKeyPress ;

	var firstCloseButton = DOMUtil.findFirstDescendantByClass(eXoBrowser, "div", "CloseButton") ;
	firstCloseButton.onclick = function(event) {
		eXo.application.browser.BrowserApplication.removeTabDetail(this, event) ;
	};
		
	var firstTabDetail = firstCloseButton.parentNode.parentNode ;
	firstTabDetail.index = 0 ;
	firstTabDetail.onclick = function() {
		eXo.application.browser.BrowserApplication.activateTabDetail(this, eXoBrowser) ;
	};
	
	var iframe = DOMUtil.findFirstDescendantByClass(eXoBrowser, "iframe", "IFrame") ;
	iframe.style.display = "block" ;
  var hiddenAncestors = [] ;
  var prNode = iframe ;
	while(prNode && (prNode.nodeType == 1)) {
	  if(prNode.style.display == "none") {
			hiddenAncestors.push(prNode) ;
			prNode.style.display = "block" ;
		}
		prNode = prNode.parentNode ;
	}
	var parentIframe = iframe.parentNode;
	if (parentIframe.offsetHeight < 296) parentIframe.style.height = 296 + "px";
	var delta = eXoBrowser.parentNode.offsetHeight - eXoBrowser.offsetHeight;
	parentIframe.style.height = parentIframe.offsetHeight + delta + "px";
	this.storeURL(iframe, "javascript: voild(0);");
  for (var i = 0; i < hiddenAncestors.length; i++) {
 	 hiddenAncestors[i].style.display = "none" ;
  }
} ;

BrowserApplication.prototype.onKeyPress = function(e) {
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
		eXo.application.browser.BrowserApplication.getUrl(obj) ;
	}
} ;

BrowserApplication.prototype.convertURL = function(url) {
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

BrowserApplication.prototype.getUrl = function(obj) {
	var DOMUtil = eXo.core.DOMUtil ;
	var	addressBarContainer = DOMUtil.findNextElementByTagName(obj, "div") ; 
	var eXoBrowser = DOMUtil.findAncestorByClass(obj,"UIBrowserPortlet") ;
	var txtAddress = DOMUtil.findFirstDescendantByClass(addressBarContainer, "input", "txtAddress") ;	
	var iframes = DOMUtil.findDescendantsByClass(eXoBrowser, "iframe", "IFrame") ;
	var tabContainer = DOMUtil.findFirstDescendantByClass(eXoBrowser, "div", "TabContainer") ;
	var tabLabels = DOMUtil.findDescendantsByClass(tabContainer, "div", "TabLabel") ;
	var src = txtAddress.value ;
	for (var i = 0; i < iframes.length; i++) {
		if (iframes[i].style.display != "none") {
			if (src.indexOf("http") < 0) {
				src = "http://"+ src ;
				txtAddress.value = src ;
			}
			iframes[i].src = src ;
			this.storeURL(iframes[i], src);
			tabLabels[i].innerHTML = this.convertURL(src) ;
		}
	}
} ;

BrowserApplication.prototype.onMouseOver = function(object, normalClass, activeClass, isOver) {
	if(isOver) {
		object.className = activeClass ;
	} else {
		object.className = normalClass ;
	}
} ;

BrowserApplication.prototype.createNewTab = function(clickedElement) {
  var DOMUtil = eXo.core.DOMUtil ;
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
  	eXo.application.browser.BrowserApplication.activateTabDetail(this, ancestorNode) ;
  } ;
  var closeButton = DOMUtil.findFirstDescendantByClass(cloneActiveTab, "div", "CloseButton") ;
  closeButton.onclick = function(event) {
  	eXo.application.browser.BrowserApplication.removeTabDetail(this, event) ;
  } ;
  clickedElement.parentNode.insertBefore(cloneActiveTab, clickedElement) ;
  
  var firstCloseButton = DOMUtil.findFirstDescendantByClass(tabParent, "div", "CloseButton") ;
  if (firstCloseButton.style.display == "none") {
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
  this.storeURL(newIFrame, "javascript: voild(0);");
  this.resizeTabDetail(tabParent) ;
} ;

BrowserApplication.prototype.activateTabDetail = function(selectedElement, ancestor) {
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
BrowserApplication.prototype.removeTabDetail = function(clickedElement, event) {
	if(!event && window.event) event = window.event;
	event.cancelBubble = true;
	var DOMUtil = eXo.core.DOMUtil ;
	if (this.NumberOfTab > 1) {
		this.NumberOfTab-- ;
		
		var eXoBrowser = DOMUtil.findAncestorByClass(clickedElement, "UIBrowserPortlet") ;
		var txtAddress = DOMUtil.findFirstDescendantByClass(eXoBrowser, "input", "txtAddress") ;
		var tabDetail = (clickedElement.parentNode).parentNode ;
		var tabIndex ;
		tabIndex = tabDetail.index ;
		var tabContainer = tabDetail.parentNode ;
		var separator = DOMUtil.findDescendantsByClass(tabContainer, "div", "Separator") ;
		
		var tabContent = DOMUtil.findFirstDescendantByClass(eXoBrowser, "div", "TabContent") ;
		var iframes = DOMUtil.findDescendantsByClass(tabContent, "iframe", "IFrame") ;

		if (tabDetail.className == "ActiveTabDetailBackground TabMenuItem") {
			var tabDetailList = DOMUtil.findDescendantsByClass(tabContainer, "div", "TabDetailBackground") ;
			var index ;
			if (tabIndex - 1 >= 0) {
				index = tabIndex - 1 ;
				iframes[index].style.display = "block" ;
				txtAddress.value = iframes[index].src ;
			} else {
				index = tabIndex ;
				iframes[index + 1].style.display = "block" ;
				txtAddress.value = iframes[index + 1].src ;
			}
			tabDetailList[index].className = "ActiveTabDetailBackground TabMenuItem" ;
		}

		tabContainer.removeChild(separator[tabIndex]) ;
		tabContainer.removeChild(tabDetail) ;
		tabContent.removeChild(iframes[tabIndex]) ;
		
		this.resizeTabDetail(tabContainer) ;
		
		eXo.application.browser.BrowserApplication.resetIndex(eXoBrowser) ;
		if (this.NumberOfTab <= 1) {
			var firstCloseButton = DOMUtil.findFirstDescendantByClass(tabContainer, "div", "CloseButton") ;
			firstCloseButton.style.display = "none" ;
		}
	} else {
		clickedElement.style.display = "none" ;
	}
} ;

BrowserApplication.prototype.resetIndex = function(eXoBrowser) {
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

BrowserApplication.prototype.refreshIFrame = function(obj) {
	var eXoBrowser = eXo.core.DOMUtil.findAncestorByClass(obj, "UIBrowserPortlet");
	var iframes = eXo.core.DOMUtil.findDescendantsByClass(eXoBrowser, "iframe", "IFrame") ;
	for(var i = 0; i < iframes.length; i++) {
  	if(iframes[i].style.display != "none") {  		
			iframes[i].src = iframes[i].src ;
		}
  }
} ;
	
BrowserApplication.prototype.goBack = function(obj) {
	var eXoBrowser = eXo.core.DOMUtil.findAncestorByClass(obj, "UIBrowserPortlet");
	var iframes = eXo.core.DOMUtil.findDescendantsByClass(eXoBrowser, "iframe", "IFrame") ;
	for(var i = 0; i < iframes.length; i++) {
	  	if (iframes[i].style.display != "none") {
			var index = this.getIndexURL(iframes[i], iframes[i].src) ;
			if (--index) {
				iframes[i].src = this.getFullURL(iframes[i], index);
				var fieldAddress = eXo.core.DOMUtil.findFirstDescendantByClass(eXoBrowser, "input", "txtAddress") ;
				if (fieldAddress) fieldAddress.value = iframes[i].src ;
				var activeTab = eXo.core.DOMUtil.findFirstDescendantByClass(eXoBrowser, "div", "ActiveTabDetailBackground") ;
				var tabLabel = eXo.core.DOMUtil.findFirstDescendantByClass(activeTab, "div", "TabLabel") ;
				tabLabel.innerHTML = this.convertURL(iframes[i].src) ;
			}
		}
	}
} ;

BrowserApplication.prototype.goForward = function(obj) {
	var eXoBrowser = eXo.core.DOMUtil.findAncestorByClass(obj, "UIBrowserPortlet");
	var iframes = eXo.core.DOMUtil.findDescendantsByClass(eXoBrowser, "iframe", "IFrame") ;
	for (var i = 0; i < iframes.length; i++) {
		if (iframes[i].style.display != "none") {
			var index = this.getIndexURL(iframes[i], iframes[i].src);
			if (iframes[i].store[++index]) {
				iframes[i].src = this.getFullURL(iframes[i], index);
				var fieldAddress = eXo.core.DOMUtil.findFirstDescendantByClass(eXoBrowser, "input", "txtAddress") ;
				if (fieldAddress) fieldAddress.value = iframes[i].src ;
				var activeTab = eXo.core.DOMUtil.findFirstDescendantByClass(eXoBrowser, "div", "ActiveTabDetailBackground") ;
				var tabLabel = eXo.core.DOMUtil.findFirstDescendantByClass(activeTab, "div", "TabLabel") ;
				tabLabel.innerHTML = this.convertURL(iframes[i].src) ;
			}
		}
	}
} ;

BrowserApplication.prototype.resizeTabDetail = function(tabContainer) {
	var DOMUtil = eXo.core.DOMUtil ;
	var children = DOMUtil.getChildrenByTagName(tabContainer, "div") ;
	
	var sumOfWidth = tabContainer.offsetWidth ;
	var sumOfElementWidth = 0 ;
	var sumOfSeparatorWidth = 0 ;
	var sumOfTabDetail = 0 ;
	var newTabWidth = 0 ;
	var labelWidth = 0 ;
	var defaultSize = 150;
	
	for(var i = 0; i < children.length; i++) {
		if(children[i].className != "") {
			if(DOMUtil.hasClass(children[i], "ClearLeft") || DOMUtil.hasClass(children[i], "ClearRight") || DOMUtil.hasClass(children[i], "ClearBoth")) {
		    children.remove(children[i]) ;
		    i--;
		    continue;
		  }
			if(children[i].className == "Separator") {
				sumOfSeparatorWidth += children[i].offsetWidth ;
			} else if(children[i].className == "NewTabButtonOver" || children[i].className == "NewTabButton") {
				newTabWidth = children[i].offsetWidth ;
			} else {
				sumOfTabDetail++ ;
			}
			sumOfElementWidth += children[i].offsetWidth ;
		}
	}
	
	var tabDetailWidth = (sumOfWidth - sumOfSeparatorWidth - newTabWidth - 15) / sumOfTabDetail ;
	var computeSize = (defaultSize > tabDetailWidth) ? tabDetailWidth : defaultSize ;
	if(sumOfElementWidth <= sumOfWidth) {  
		if(this.NumberOfTab == 1 ) {
			tabDetailWidth = defaultSize ;
		}
		labelWidth = computeSize - 50 ;	 
		
		for(var i = 0; i < children.length; i++) {
			if(children[i].className != "Separator" && children[i].className != "" && children[i].className != "NewTabButton" && children[i].className != "NewTabButtonOver") {
				 children[i].style.width = computeSize + "px" ;
			}
		}
	}	else {
		labelWidth = computeSize - 50 ;
		for(var i = 0; i < children.length; i++) {
			if(children[i].className != "Separator" && children[i].className != "" && children[i].className != "NewTabButton" && children[i].className != "NewTabButtonOver") {
				children[i].style.width = computeSize + "px" ;
				children[i].setAttribute("width", tabDetailWidth) ;
			}
		}
	}
	
//	if(eXo.core.Browser.isIE6()) {
//		var tabLabel = DOMUtil.findDescendantsByClass(tabContainer, "div", "TabLabel") ;
//		for(var i = 0 ; i < tabLabel.length; i++) {
//			tabLabel[i].style.width = labelWidth + "px" ;
//		}
//		if(this.NumberOfTab == 1 ) {
//			tabLabel[0].style.width = 125 + "px" ;
//		}
//	}
	
} ;

BrowserApplication.prototype.stopLoad = function() {
	var detect = navigator.userAgent.toLowerCase() ;
	if (detect.indexOf("msie") > -1) {
		document.execCommand('Stop') ;
	} else {
		window.stop() ;
	}
} ;

BrowserApplication.prototype.storeURL = function (iframe, url) {
	if (!iframe.store) iframe.store = new Array();
	iframe.store.push(url) ;
} ;

BrowserApplication.prototype.getIndexURL = function (iframe, url) {
	if (!iframe || !iframe.store) return ;
	for (var i in iframe.store) {
		if (Array.prototype[i]) continue ;
		if (url.indexOf(iframe.store[i]) != -1) return i ;
	}
} ;

BrowserApplication.prototype.getFullURL = function (iframe, index) {
	if (!iframe || !iframe.store || !iframe.store[index]) return ;
	return iframe.store[index];
} ;

eXo.application.browser.BrowserApplication = new BrowserApplication() ;