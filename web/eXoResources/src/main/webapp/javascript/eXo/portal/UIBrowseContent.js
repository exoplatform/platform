function UIBrowseContent() {
	this.bcManagers = new Array();
};
/**
 * A workaround function that waits for the page to be completely loaded to
 * effectively load the scroll managers
 * Compares the size of 2 elements that should be on the same line with different width
 * and that are on two different lines and have the same width before the page finishes loading
 */
UIBrowseContent.prototype.waitForLoadComplete = function() {
	var homeButton = null;
	var bcPortlet = document.getElementById("UIBrowseContainer");
	if (bcPortlet) homeButton = eXo.core.DOMUtil.findFirstDescendantByClass(bcPortlet, "div", "HomeTab");
	var tabs = eXo.core.DOMUtil.findFirstDescendantByClass(bcPortlet, "div", "UIHorizontalTabs");
	if (homeButton && tabs && homeButton.offsetWidth == tabs.offsetWidth) window.setTimeout(eXo.portal.UIBrowseContent.waitForLoadComplete, 100);
	else eXo.portal.UIBrowseContent.loadScroll();
};
/**
 * Creates the managers on the BrowseContent portlet
 * Adds the base parameters to them
 */
UIBrowseContent.prototype.loadScroll = function() {
	var uiBC = eXo.portal.UIBrowseContent;
	uiBC.bcManagers.clear();
	var bcPortlet = document.getElementById("UIBrowseContainer");
	if (bcPortlet) {
		// Main Navigation Configuration
		var mainNav = eXo.core.DOMUtil.findFirstDescendantByClass(bcPortlet, "div", "UICBMainNavigation");
		var mainBarMgr = eXo.portal.UIPortalControl.newScrollManager("UIBrowseContainer");
		//mainBarMgr.margin = 0;
		mainBarMgr.mainContainer = mainNav;
		mainBarMgr.arrowsContainer = eXo.core.DOMUtil.findFirstDescendantByClass(mainNav, "div", "NavigationButtonContainer");
		mainBarMgr.loadElements("UITab", true);
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
		var subBarMgr = eXo.portal.UIPortalControl.newScrollManager("UIBrowseContainer");
		subBarMgr.mainContainer = subNav;
		subBarMgr.arrowsContainer = eXo.core.DOMUtil.findFirstDescendantByClass(subNav, "div", "NavigationButtonContainer");
		subBarMgr.loadElements("ChildNodeItem", true);
		for (var i = 0; i < subBarMgr.elements.length; i++) {
			subBarMgr.elements[i].decorator = eXo.core.DOMUtil.findNextElementByTagName(subBarMgr.elements[i], "div");
		}
		var subArrows = eXo.core.DOMUtil.findDescendantsByClass(subBarMgr.arrowsContainer, "div", "NavigationIcon");
		if (subArrows.length == 2) {
			subBarMgr.initArrowButton(subArrows[0], "left", "NavigationIcon ScrollBackArrow16x16Icon", "NavigationIcon DisableBackArrow16x16Icon", "NavigationIcon DisableBackArrow16x16Icon");
			subBarMgr.initArrowButton(subArrows[1], "right", "NavigationIcon ScrollNextArrow16x16Icon", "NavigationIcon DisableNextArrow16x16Icon", "NavigationIcon DisableNextArrow16x16Icon");
		}
		subBarMgr.initFunction = uiBC.initScroll;
		subBarMgr.callback = uiBC.subMenuScrollCallback;
		uiBC.bcManagers.push(subBarMgr);
		// Page Navigation Configuration
		var pageNav = eXo.core.DOMUtil.findFirstDescendantByClass(bcPortlet, "div", "UIEventViewer");
		var pageMenus = eXo.core.DOMUtil.findDescendantsByClass(pageNav, "div", "SubContentTitle");
		for (var i = 0; i < pageMenus.length; i++) {
			var currMgr = eXo.portal.UIPortalControl.newScrollManager("UIBrowseContainer");
			currMgr.mainContainer = eXo.core.DOMUtil.findFirstDescendantByClass(pageMenus[i], "div", "MiddleBar");
			currMgr.arrowsContainer = eXo.core.DOMUtil.findFirstDescendantByClass(pageMenus[i], "div", "NavigationButtonContainer");
			currMgr.loadElements("ChildNode", true);
			for (var j = 0; j < currMgr.elements.length; j++) {
				currMgr.elements[j].decorator = eXo.core.DOMUtil.findNextElementByTagName(currMgr.elements[j], "div");
			}
			var currArrows = eXo.core.DOMUtil.findDescendantsByClass(currMgr.arrowsContainer, "div", "NavigationIcon");
			if (currArrows.length == 2) {
				currMgr.initArrowButton(currArrows[0], "left", "NavigationIcon ScrollBackArrow16x16Icon", "NavigationIcon DisableBackArrow16x16Icon", "NavigationIcon DisableBackArrow16x16Icon");
				currMgr.initArrowButton(currArrows[1], "right", "NavigationIcon ScrollNextArrow16x16Icon", "NavigationIcon DisableNextArrow16x16Icon", "NavigationIcon DisableNextArrow16x16Icon");
			}
			currMgr.initFunction = uiBC.initScroll;
			currMgr.callback = uiBC.subMenuScrollCallback;
			uiBC.bcManagers.push(currMgr);
		}
		// End Configuration
		uiBC.initScroll();
	}
};
/**
 * Inits (and re inits) the scroll managers, to hide or show tabs after the page changes,
 * the window is resized, etc
 * Basically, just calculates the free space again and hides the elements that should be
 * This portlet contains a lot (not a static number) of scroll managers, so this function
 * reloads the elements in each manager
 */
UIBrowseContent.prototype.initScroll = function() {
	var uiBC = eXo.portal.UIBrowseContent;
	var bcPortlet = document.getElementById("UIBrowseContainer");
	var mainNav = eXo.core.DOMUtil.findFirstDescendantByClass(bcPortlet, "div", "UICBMainNavigation");
	if (mainNav) {
		if (uiBC.bcManagers.length >= 1) {
			// Main Manager initialization
			var mainBarMgr = uiBC.bcManagers[0];
			mainBarMgr.init();
			mainBarMgr.loadElements("UITab", true);
			var homeButton = eXo.core.DOMUtil.findFirstDescendantByClass(mainBarMgr.mainContainer, "div", "HomeTab");
			var maxSpace = mainBarMgr.getElementSpace(mainBarMgr.mainContainer)-mainBarMgr.getElementSpace(mainBarMgr.arrowsContainer)-mainBarMgr.margin-100;
			if (homeButton) {
				maxSpace = maxSpace + 100-mainBarMgr.getElementSpace(homeButton);
			}
			mainBarMgr.checkAvailableSpace(maxSpace);
			mainBarMgr.renderElements();
			// Sub Manager initialization
			var subBarMgr = uiBC.bcManagers[1];
			subBarMgr.init();
			subBarMgr.loadElements("ChildNodeItem", true);
			subBarMgr.checkAvailableSpace();
			subBarMgr.renderElements();
		}
		// Page Managers initialization
		if (uiBC.bcManagers.length >= 2) {
			for (var i = 2; i < uiBC.bcManagers.length; i++) {
				var currMgr = uiBC.bcManagers[i];
				currMgr.init();
				currMgr.loadElements("ChildNode", true);
				currMgr.checkAvailableSpace();
				currMgr.renderElements();
			}
		}
	}
};
/**
 * The scroll back function of the main scroll manager
 * Makes sure the homeButton always appears and is removed from the maxSpace value
 * There was a bug in IE7, one element had a stored size of 0px which causes the calcul to be wrong
 * Here, the stored sizes (element.space) are cleaned (cleanElements) so they are calculated again
 */
UIBrowseContent.prototype.mainMenuScrollCallback = function() {
	var homeButton = eXo.core.DOMUtil.findFirstDescendantByClass(this.mainContainer, "div", "HomeTab");
	if (eXo.core.Browser.isIE7()) this.cleanElements();
	var maxSpace = this.getElementSpace(this.mainContainer)-this.getElementSpace(this.arrowsContainer)-this.margin-100;
	if (homeButton) {
		maxSpace = maxSpace + 100 - this.getElementSpace(homeButton);
	}
	var elementsSpace = this.getElementsSpace(this.firstVisibleIndex, this.lastVisibleIndex);
	var delta = maxSpace - elementsSpace;
	if (delta < 0) {
		this.hideElements(delta);
	}
//	while (this.elements.length > 0 && elementsSpace >= maxSpace) { //while
//		if (this.lastDirection == 1) {
//			if (this.firstVisibleIndex < this.elements.length-1) {
//				this.otherHiddenIndex = this.firstVisibleIndex;
//				this.elements[this.firstVisibleIndex].isVisible = false;
//				this.elements[this.firstVisibleIndex++].style.display = "none";
//			}
//		} else {
//			if (this.lastVisibleIndex > 0) {
//				this.otherHiddenIndex = this.lastVisibleIndex;
//				this.elements[this.lastVisibleIndex].isVisible = false;
//				this.elements[this.lastVisibleIndex--].style.display = "none";
//			}
//		}
//		elementsSpace -= this.getElementSpace(this.elements[this.otherHiddenIndex]);
//	}
};
/**
 * Sub menu scroll callback function
 * Hides the decorator (small vertical bar) for the elements that are hidden
 */
UIBrowseContent.prototype.subMenuScrollCallback = function() {
	for (var i = 0; i < this.elements.length; i++) {
		if (!this.elements[i].isVisible) {
			this.elements[i].decorator.style.display = "none";
		} else {
			this.elements[i].decorator.style.display = "block";
		}
	}
};

eXo.portal.UIBrowseContent = new UIBrowseContent() ;