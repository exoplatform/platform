eXo.require('eXo.webui.UIPopupSelectCategory');

function UIContainerConfigOptions() {
} ;

UIContainerConfigOptions.prototype.init = function() {
	var uiContainerTemplateOptions = eXo.core.DOMUtil.findFirstDescendantByClass(document.body, "div", "UIContainerConfigOptions") ;
	var categoryContainer = eXo.core.DOMUtil.findDescendantsByClass(uiContainerTemplateOptions, "div", "CategoryContainer") ;
	
	for(var i = 0; i < categoryContainer.length; i++) {
	  var dragObjects = eXo.core.DOMUtil.getChildrenByTagName(categoryContainer[i], "div") ;
	  for(var j = 0; j < dragObjects.length; j++) {
    	dragObjects[j].onmousedown = eXo.portal.PortalDragDrop.init ;
  	}
  }
};

eXo.webui.UIContainerConfigOptions = new UIContainerConfigOptions() ;