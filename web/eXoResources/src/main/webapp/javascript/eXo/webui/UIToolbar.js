function UIToolbar() {};

UIToolbar.prototype.displayBlockContent = function(clickedEle) {
	if(clickedEle == null) return;
  var uiToolbar = eXo.core.DOMUtil.findAncestorByClass(clickedEle, "UIToolbar");
  var parentUIToolbar = uiToolbar.parentNode;
  
  var buttons = eXo.core.DOMUtil.findDescendantsByClass(uiToolbar, "div", "Button");
  var buttonLabel = eXo.core.DOMUtil.findDescendantsByClass(uiToolbar, "div", "ButtonLabel");

  var uiToolbarContentContainer = eXo.core.DOMUtil.findFirstDescendantByClass(parentUIToolbar, "div", "UIToolbarContentContainer");
  var uiToolbarContentBlock = eXo.core.DOMUtil.getChildrenByTagName(uiToolbarContentContainer, "div");

  for(var i = 0; i < buttons.length; i++) {
    if(clickedEle == buttons[i]) {
      uiToolbarContentBlock[i].style.display = "block";
      buttonLabel[i].style.fontWeight = "bold";
    } else {
      uiToolbarContentBlock[i].style.display = "none";
      buttonLabel[i].style.fontWeight = "100";		
    }                                                                  
  }
  
};

eXo.webui.UIToolbar = new UIToolbar();
