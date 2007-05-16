function UIBrowseContent() {
	this.bcManagers = new Array();
};

UIBrowseContent.prototype.waitForLoadComplete = function() {
	var styleSheet = document.getElementById("ecm_BrowseContentPortlet");
	if (styleSheet) eXo.portal.UIBrowseContent.loadScroll();
	else window.setTimeout("eXo.portal.UIBrowseContent.waitForLoadComplete()", 100);
};

UIBrowseContent.prototype.loadScroll = function() {
	var uiBC = eXo.portal.UIBrowseContent;
	var bcPortlet = document.getElementById("UIBrowseContainer");
	if (bcPortlet) {
		// Main Navigation Configuration
		var mainNav = eXo.core.DOMUtil.findFirstDescendantByClass(bcPortlet, "div", "UICBMainNavigation");
		var mainBarMgr = eXo.portal.UIPortalControl.newScrollManager();
		mainBarMgr.mainContainer = mainNav;
		mainBarMgr.arrowsContainer = eXo.core.DOMUtil.findFirstDescendantByClass(mainNav, "div", "NavigationButtonContainer");
		mainBarMgr.elements.pushAll(eXo.core.DOMUtil.findDescendantsByClass(mainNav, "div", "UITab"));
		var mainArrows = eXo.core.DOMUtil.findDescendantsByClass(mainBarMgr.arrowsContainer, "div", "NavigationIcon");
		if (mainArrows.length == 2) {
			mainBarMgr.initArrowButton(mainArrows[0], "left", "NavigationIcon ScrollBackArrow16x16Icon", "NavigationIcon DisableBackArrow16x16Icon", "NavigationIcon DisableBackArrow16x16Icon");
			mainBarMgr.initArrowButton(mainArrows[1], "right", "NavigationIcon ScrollNextArrow16x16Icon", "NavigationIcon DisableNextArrow16x16Icon", "NavigationIcon DisableNextArrow16x16Icon");
		}
		mainBarMgr.initFunction = uiBC.initScroll;
		mainBarMgr.callback = uiBC.mainMenuScrollCallback;
		uiBC.bcManagers.push(mainBarMgr);
		// Sub Navigation Configuration
		var subNav = eXo.core.DOMUtil.findFirstDescendantByClass(bcPortlet, "div", "UICBSubNavigation");
		var subBarMgr = eXo.portal.UIPortalControl.newScrollManager();
		subBarMgr.mainContainer = subNav;
		subBarMgr.arrowsContainer = eXo.core.DOMUtil.findFirstDescendantByClass(subNav, "div", "NavigationButtonContainer");
		subBarMgr.elements.pushAll(eXo.core.DOMUtil.findDescendantsByClass(subNav, "div", "ChildNodeItem"));
		for (var i = 0; i < subBarMgr.elements.length; i++) {
			subBarMgr.elements[i].decorator = eXo.core.DOMUtil.findNextElementByTagName(subBarMgr.elements[i], "div");
		}
		var subArrows = eXo.core.DOMUtil.findDescendantsByClass(subBarMgr.arrowsContainer, "div", "NavigationIcon");
		if (subArrows.length == 2) {
			subBarMgr.initArrowButton(subArrows[0], "left", "NavigationIcon ScrollBackArrow16x16Icon", "NavigationIcon DisableBackArrow16x16Icon", "NavigationIcon DisableBackArrow16x16Icon");
			subBarMgr.initArrowButton(subArrows[1], "right", "NavigationIcon ScrollNextArrow16x16Icon", "NavigationIcon DisableNextArrow16x16Icon", "NavigationIcon DisableNextArrow16x16Icon");
		}
		subBarMgr.callback = uiBC.subMenuScrollCallback;
		uiBC.bcManagers.push(subBarMgr);
		// Page Navigation Configuration
		var pageNav = eXo.core.DOMUtil.findFirstDescendantByClass(bcPortlet, "div", "UIEventViewer");
		var pageMenus = eXo.core.DOMUtil.findDescendantsByClass(pageNav, "div", "SubContentTitle");
		for (var i = 0; i < pageMenus.length; i++) {
			var currMgr = eXo.portal.UIPortalControl.newScrollManager();
			currMgr.mainContainer = eXo.core.DOMUtil.findFirstDescendantByClass(pageMenus[i], "div", "MiddleBar");
			currMgr.arrowsContainer = eXo.core.DOMUtil.findFirstDescendantByClass(pageMenus[i], "div", "NavigationButtonContainer");
			currMgr.elements.pushAll(eXo.core.DOMUtil.findDescendantsByClass(currMgr.mainContainer, "div", "ChildNode"));
			for (var j = 0; j < currMgr.elements.length; j++) {
				currMgr.elements[j].decorator = eXo.core.DOMUtil.findNextElementByTagName(currMgr.elements[j], "div");
			}
			var currArrows = eXo.core.DOMUtil.findDescendantsByClass(currMgr.arrowsContainer, "div", "NavigationIcon");
			if (currArrows.length == 2) {
				currMgr.initArrowButton(currArrows[0], "left", "NavigationIcon ScrollBackArrow16x16Icon", "NavigationIcon DisableBackArrow16x16Icon", "NavigationIcon DisableBackArrow16x16Icon");
				currMgr.initArrowButton(currArrows[1], "right", "NavigationIcon ScrollNextArrow16x16Icon", "NavigationIcon DisableNextArrow16x16Icon", "NavigationIcon DisableNextArrow16x16Icon");
			}
			currMgr.callback = uiBC.subMenuScrollCallback;
			uiBC.bcManagers.push(currMgr);
		}
		// End Configuration
		uiBC.scrollManagersLoaded = true;
		uiBC.initScroll();
	}
};

UIBrowseContent.prototype.initScroll = function() {
	var uiBC = eXo.portal.UIBrowseContent;
	if (!uiBC.scrollManagersLoaded) { uiBC.loadScroll(); return; }
	if (uiBC.bcManagers.length >= 1) {
		// Main Manager initialization
		var mainBarMgr = uiBC.bcManagers[0];
		mainBarMgr.init();
		if (eXo.core.Browser.getBrowserType() == "ie") {
			var maxSpace = mainBarMgr.getElementSpace(mainBarMgr.mainContainer)-mainBarMgr.getElementSpace(mainBarMgr.arrowsContainer)-100;
		} else {
			var homeButton = eXo.core.DOMUtil.findFirstDescendantByClass(mainBarMgr.mainContainer, "div", "HomeTab");
			var maxSpace = mainBarMgr.getElementSpace(mainBarMgr.mainContainer)-mainBarMgr.getElementSpace(mainBarMgr.arrowsContainer)-mainBarMgr.getElementSpace(homeButton);
		}
		mainBarMgr.checkAvailableSpace(maxSpace);
		mainBarMgr.renderElements();
		// Sub Manager initialization
		var subBarMgr = uiBC.bcManagers[1];
		subBarMgr.init();
		subBarMgr.checkAvailableSpace();
		subBarMgr.renderElements();
	}
	// Page Managers initialization
	if (uiBC.bcManagers.length >= 2) {
		for (var i = 2; i < uiBC.bcManagers.length; i++) {
			var currMgr = uiBC.bcManagers[i];
			currMgr.init();
			currMgr.checkAvailableSpace();
			currMgr.renderElements();
		}
	}
};

UIBrowseContent.prototype.mainMenuScrollCallback = function() {
	var uiBC = eXo.portal.UIBrowseContent;
	if (eXo.core.Browser.getBrowserType() == "ie") {
		var maxSpace = this.getElementSpace(this.mainContainer)-this.getElementSpace(this.arrowsContainer)-100;
	} else {
		var homeButton = eXo.core.DOMUtil.findFirstDescendantByClass(this.mainContainer, "div", "HomeTab");
		var maxSpace = this.getElementSpace(this.mainContainer)-this.getElementSpace(this.arrowsContainer)-this.getElementSpace(homeButton);
	}
	var elementsSpace = this.getElementsSpace(this.firstVisibleIndex, this.lastVisibleIndex);
	if (elementsSpace > maxSpace) {
		if (this.lastDirection == 1) {
			if (this.firstVisibleIndex >= 0 && this.firstVisibleIndex < this.elements.length-1) {
				this.otherHiddenIndex = this.firstVisibleIndex;
				this.elements[this.firstVisibleIndex].isVisible = false;
				this.elements[this.firstVisibleIndex++].style.display = "none";
			}
		} else {
			if (this.lastVisibleIndex > 0 && this.lastVisibleIndex < this.elements.length) {
				this.otherHiddenIndex = this.lastVisibleIndex;
				this.elements[this.lastVisibleIndex].isVisible = false;
				this.elements[this.lastVisibleIndex--].style.display = "none";
			}
		}
	}
};

UIBrowseContent.prototype.subMenuScrollCallback = function() {
	var uiBC = eXo.portal.UIBrowseContent;
	for (var i = 0; i < this.elements.length; i++) {
		if (!this.elements[i].isVisible) {
			this.elements[i].decorator.style.display = "none";
		} else {
			this.elements[i].decorator.style.display = "block";
		}
	}
};

eXo.portal.UIBrowseContent = new UIBrowseContent() ;