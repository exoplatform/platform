eXo.require('eXo.webui.UIPopupSelectCategory');

function UIConfigOptions() {
	this.numberOfMessage = 0 ;
};

UIConfigOptions.prototype.init = function() {	
	var DOMUtil = eXo.core.DOMUtil ;
  var uiPortletOptions = DOMUtil.findFirstDescendantByClass(document.body, "div", "UIPortletOptions") ;
  var uiVerticalSlideTabs = DOMUtil.findDescendantsByClass(uiPortletOptions, "div", "UIVerticalSlideTabs") ;
  
  for(var i = 0; i < uiVerticalSlideTabs.length; ++i) {
	  var uiVTabs = DOMUtil.getChildrenByTagName(uiVerticalSlideTabs[i], "div") ;
	  for(var j = 0; j < uiVTabs.length; j++) {
	  	var moveArea = DOMUtil.findFirstDescendantByClass(uiVTabs[j], "div", "TabLabel") ;
      moveArea.style.cursor = "move" ;
      moveArea.onmousedown = eXo.portal.PortalDragDrop.init ;
	  }
  }

};

eXo.webui.UIConfigOptions = new UIConfigOptions();