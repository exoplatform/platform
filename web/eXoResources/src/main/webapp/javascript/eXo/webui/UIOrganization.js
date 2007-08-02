function UIOrganization() {
	this.scrollMgr = null;
};

UIOrganization.prototype.loadScroll = function() {
	var org = eXo.webui.UIOrganization;
	var orgPortlet = document.getElementById("UIOrganizationPortlet");
	if (orgPortlet) {
		org.scrollMgr = eXo.portal.UIPortalControl.newScrollManager("UIOrganizationPortlet");
		org.scrollMgr.margin = 5;
		org.scrollMgr.mainContainer = eXo.core.DOMUtil.findFirstDescendantByClass(orgPortlet, "div", "ManagementIconContainer");
		org.scrollMgr.arrowsContainer = eXo.core.DOMUtil.findFirstDescendantByClass(orgPortlet, "div", "ScrollButtons");
		// loads the three elements
		var userBtn = eXo.core.DOMUtil.findFirstDescendantByClass(orgPortlet, "a", "UserButton");
		userBtn.decorator = eXo.core.DOMUtil.findNextElementByTagName(userBtn, "div");
		org.scrollMgr.elements.push(userBtn);
		var groupBtn = eXo.core.DOMUtil.findFirstDescendantByClass(orgPortlet, "a", "GroupButton");
		groupBtn.decorator = eXo.core.DOMUtil.findNextElementByTagName(groupBtn, "div");
		org.scrollMgr.elements.push(groupBtn);
		var mbrBtn = eXo.core.DOMUtil.findFirstDescendantByClass(orgPortlet, "a", "MembershipButton");
		mbrBtn.decorator = eXo.core.DOMUtil.findNextElementByTagName(mbrBtn, "div");
		org.scrollMgr.elements.push(mbrBtn);
		// init arrows
		var leftArrow = eXo.core.DOMUtil.findFirstDescendantByClass(org.scrollMgr.arrowsContainer, "div", "ScrollLeftButton");
		org.scrollMgr.initArrowButton(leftArrow, "left", "ScrollLeftButton", "HighlightScrollLeftButton", "DisableScrollLeftButton");
		var rightArrow = eXo.core.DOMUtil.findFirstDescendantByClass(org.scrollMgr.arrowsContainer, "div", "ScrollRightButton");
		org.scrollMgr.initArrowButton(rightArrow, "right", "ScrollRightButton", "HighlightScrollRightButton", "DisableScrollRightButton");
		// end creation
		org.scrollMgr.initFunction = org.initScroll;
		org.scrollMgr.callback = org.scrollCallback;
		org.initScroll();
	}
};

UIOrganization.prototype.initScroll = function() {
	var scrollMgr = eXo.webui.UIOrganization.scrollMgr;
	scrollMgr.init();
	// Gets the maximum width available for the tabs
	var maxSpace = scrollMgr.getElementSpace(scrollMgr.mainContainer)-scrollMgr.getElementSpace(scrollMgr.arrowsContainer)-scrollMgr.margin;
	if (maxSpace == 0) window.setTimeout(eXo.webui.UIOrganization.initScroll, 100);
	scrollMgr.checkAvailableSpace(maxSpace);
	scrollMgr.renderElements();
};

UIOrganization.prototype.scrollCallback = function() {
	var scrollMgr = eXo.webui.UIOrganization.scrollMgr;
	for (var i = 0; i < scrollMgr.elements.length; i++) {
		if (!scrollMgr.elements[i].isVisible) {
			scrollMgr.elements[i].decorator.style.display = "none";
		} else {
			scrollMgr.elements[i].decorator.style.display = "block";
		}
	}
};

eXo.webui.UIOrganization = new UIOrganization() ;