function UIVerticalSlideTabs() {
  
};

UIVerticalSlideTabs.prototype.init = function() {

};

UIVerticalSlideTabs.prototype.onTabClick = function(clickedElement, normalStyle, selectedStyle) {
  var uiClickedVTab = eXo.core.DOMUtil.findAncestorByClass(clickedElement, "UIVTab");
	var uiClickedVTabContent = eXo.core.DOMUtil.findFirstChildByClass(uiClickedVTab, "div", "UIVTabContent")
  var uiVerticalSlideTabs =  eXo.core.DOMUtil.findAncestorByClass(clickedElement, "UIVerticalSlideTabs");
  var uiVTab = eXo.core.DOMUtil.findChildrenByClass(uiVerticalSlideTabs, "div", "UIVTab") ;
	
	if ( eXo.core.DOMUtil.getChildrenByTagName(uiClickedVTab, "div")[0].className == normalStyle ) {
	  for(var i = 0; i < uiVTab.length; i++) {
	    eXo.core.DOMUtil.getChildrenByTagName(uiVTab[i], "div")[0].className = normalStyle;
	    eXo.core.DOMUtil.findFirstChildByClass(uiVTab[i], "div", "UIVTabContent").style.display = "none" ;
	    
	  }
		eXo.core.DOMUtil.getChildrenByTagName(uiClickedVTab, "div")[0].className = selectedStyle;
    uiClickedVTabContent.style.display = "block";
    //eXo.webui.WebUI.fixHeight(eXo.core.DOMUtil.findFirstDescendantByClass(uiClickedVTabContent, "div", "ScrollArea"), 'UIWorkspacePanel');
	} else {
		eXo.core.DOMUtil.getChildrenByTagName(uiClickedVTab, "div")[0].className = normalStyle;
    uiClickedVTabContent.style.display = "none";
	}
};

UIVerticalSlideTabs.prototype.onResize = function(uiVerticalSlideTabs, width, height) {
  var vTabHeight = 35;

  var uiVTabs = eXo.core.DOMUtil.findChildrenByClass(uiVerticalSlideTabs, "div", "UIVTab") ;
  var uiVTab = this.getSelectedUIVTab(uiVerticalSlideTabs, "div", "UIVTab") ;
  if(uiVTab == null) return ;

  if(height != null) {
    var totalTabHeight = (vTabHeight * uiVTabs.length) ;
    var controlArea = eXo.core.DOMUtil.findFirstDescendantByClass(uiVTab, "div", "ControlArea");
    var controlAreaHeight = 0 ;
    if(controlArea != null)  controlAreaHeight = controlArea.offsetHeight ;
    scrollArea = eXo.core.DOMUtil.findFirstDescendantByClass(uiVTab, "div", "ScrollArea");
    if(scrollArea != null) {
      scrollArea.style.height = (height - controlAreaHeight - totalTabHeight - 35) + "px" ;
    }
  }

  if(width != null) {
    scrollArea.style.width = width + "px";
  }
};

UIVerticalSlideTabs.prototype.isSelectedUIVTab = function(uiVtab) {
  var tabRight = eXo.core.DOMUtil.findFirstDescendantByClass(uiVtab, "div", "TabRight") ;
  var changeIcon = eXo.core.DOMUtil.getChildrenByTagName(tabRight, "div")[0];
  if(changeIcon.className == "ExpandButton") return true ;
  return false ;
};

UIVerticalSlideTabs.prototype.getSelectedUIVTab = function(uiVerticalSlideTabs) {
  var uiVTab = eXo.core.DOMUtil.findChildrenByClass(uiVerticalSlideTabs, "div", "UIVTab") ;
  for(var i = 0; i < uiVTab.length; i++) {
    if(this.isSelectedUIVTab(uiVTab[i]))  return uiVTab[i] ;
  }
  return null ;
};

UIVerticalSlideTabs.prototype.fitParentHeight = function(obj) {
  this.onResize(obj, null, obj.parentNode.offsetHeight);
}

eXo.webui.UIVerticalSlideTabs = new UIVerticalSlideTabs() ;


