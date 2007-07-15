function UIHorizontalTabs() {
  this.backupNavigationTabStyle;
  this.backupItemStyle ;
};

UIHorizontalTabs.prototype.init = function() {
} ;

UIHorizontalTabs.prototype.changeTabNavigationStyle = function(clickedEle, over) {
	if(clickedEle == null) return;
	if (clickedEle.className != "UITab") clickedEle = eXo.core.DOMUtil.findAncestorByClass(clickedEle, "UITab") ;
	//var uiMouseOverTab = eXo.core.DOMUtil.findAncestorByClass(clickedEle, "UITab") ;
	var tabStyle = eXo.core.DOMUtil.getChildrenByTagName(clickedEle, "div")[0] ;
	if(over) {
		this.backupNavigationTabStyle = tabStyle.className ;
		tabStyle.className = "HighlightNavigationTab" ;
	} else if (this.backupNavigationTabStyle){
		tabStyle.className = this.backupNavigationTabStyle ;
	}
}

UIHorizontalTabs.prototype.itemOver = function(selectedElement, over) {
	if(over) {
		this.backupItemStyle = selectedElement.className ;
		if(selectedElement.className == "MenuItem") {
			selectedElement.className = "MenuItemSelected" ;
		} else {
			selectedElement.className = "MenuItemSelected MenuItemExpand" ;
		}
	} else {
		selectedElement.className = this.backupItemStyle ;
	}
};

UIHorizontalTabs.prototype.displayTabContent = function(clickedEle) {
	var DOMUtil = eXo.core.DOMUtil;
  var uiSelectTab = DOMUtil.findAncestorByClass(clickedEle, "UITab") ;

  var uiHorizontalTabs = DOMUtil.findAncestorByClass(clickedEle, "UIHorizontalTabs") ;
  var uiTabs = eXo.core.DOMUtil.findDescendantsByClass(uiHorizontalTabs, "div", "UITab") ;
  var parentdHorizontalTab = uiHorizontalTabs.parentNode ;

  var contentTabContainer = DOMUtil.findFirstDescendantByClass(parentdHorizontalTab, "div", "UITabContentContainer") ;
  var uiTabContent = DOMUtil.getChildrenByTagName(contentTabContainer, "div") ;
  
  var index = 0 ;
  for(var i = 0; i < uiTabs.length; i++) {
    var styleTabDiv = DOMUtil.getChildrenByTagName(uiTabs[i], "div")[0] ;
    if(styleTabDiv.className == "DisabledTab") continue ;
    if(uiSelectTab == uiTabs[i]) {
      styleTabDiv.className = "SelectedTab" ;
    	index = i ; continue ;
    }
    styleTabDiv.className = "NormalTab" ;
    uiTabContent[i].style.display = "none" ;
  }
  uiTabContent[index].style.display = "block" ;
};

UIHorizontalTabs.prototype.changeTabForUIFormTabpane = function(clickedElemt, formId, hiddenValue) {
	this.displayTabContent(clickedElemt) ;
	eXo.webui.UIForm.setHiddenValue(formId, 'currentSelectedTab', hiddenValue) ;
};

eXo.webui.UIHorizontalTabs = new UIHorizontalTabs();
