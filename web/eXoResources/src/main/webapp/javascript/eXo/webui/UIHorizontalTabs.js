/**
 * A class to manage horizontal tabs
 * TODO : could be a good thing to implement a scroll manager directly in this class
 */
function UIHorizontalTabs() {
  this.backupNavigationTabStyle;
  this.backupItemStyle ;
};

UIHorizontalTabs.prototype.init = function() {
} ;
/**
 * Changes the style of a tab, depending on the over value (true or false)
 * Gives the defaut css style class names
 *  . UITab when tab is NOT highlighted
 *  . HighlightNavigationTab when tab is highlighted
 */
UIHorizontalTabs.prototype.changeTabNavigationStyle = function(clickedEle, over) {
	if (clickedEle == null) return;
	if (!eXo.core.DOMUtil.hasClass(clickedEle, "UITab")) clickedEle = eXo.core.DOMUtil.findAncestorByClass(clickedEle, "UITab") ;
	
	var tabStyle = eXo.core.DOMUtil.getChildrenByTagName(clickedEle, "div")[0] ;
	if(over) {
		this.backupNavigationTabStyle = tabStyle.className ;
		tabStyle.className = "HighlightNavigationTab" ;
	} else if (this.backupNavigationTabStyle){
		tabStyle.className = this.backupNavigationTabStyle ;
	}
}
/**
 * Changes the css style of an item on mouse over / out with the values :
 *  . MenuItem when item is NOT highlighted
 *  . MenuItemSelected when item is highlighted
 */
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
/**
 * Calls changeTabForUITabPane to display tab content
 */
UIHorizontalTabs.prototype.displayTabContent = function(clickedEle) {
	this.changeTabForUITabPane(clickedEle, null, null) ;
};
/**
 * Gets the tab element and the tab content associated and displays them
 *  . changes the style of the tab
 *  . displays the tab content of the selected tab (display: block)
 * if tabId are provided, can get the tab content by Ajax
 */
UIHorizontalTabs.prototype.changeTabForUITabPane = function(clickedEle, tabId, url) {
  var DOMUtil = eXo.core.DOMUtil;
  var uiSelectTab = DOMUtil.findAncestorByClass(clickedEle, "UITab") ;

  var uiHorizontalTabs = DOMUtil.findAncestorByClass(clickedEle, "UIHorizontalTabs") ;
  var uiTabs = eXo.core.DOMUtil.findDescendantsByClass(uiHorizontalTabs, "div", "UITab") ;
  var parentdHorizontalTab = uiHorizontalTabs.parentNode ;
  var contentTabContainer = DOMUtil.findFirstDescendantByClass(parentdHorizontalTab, "div", "UITabContentContainer") ;
  var uiTabContents = DOMUtil.findChildrenByClass(contentTabContainer, "div", "UITabContent") ;
 	var form = DOMUtil.getChildrenByTagName(contentTabContainer, "form") ;
 	if(form.length > 0) {
 		var tmp = DOMUtil.findChildrenByClass(form[0], "div", "UITabContent") ;
  	for(var i = 0; i < tmp.length; i++) {
  		uiTabContents.push(tmp[i]) ;
  	}
 	}
  var index = 0 ;
  for(var i = 0; i < uiTabs.length; i++) {
    var styleTabDiv = DOMUtil.getChildrenByTagName(uiTabs[i], "div")[0] ;
    if(styleTabDiv.className == "DisabledTab") continue ;
    if(uiSelectTab == uiTabs[i]) {
      styleTabDiv.className = "SelectedTab" ;
      index = i ;
			continue ;
    }
    styleTabDiv.className = "NormalTab" ;
    uiTabContents[i].style.display = "none" ;
  }
  uiTabContents[index].style.display = "block" ;
	if (eXo.ecm.UIJCRExplorer) {
		try {
				eXo.ecm.UIJCRExplorer.initViewNodeScroll();
		} catch(e) {void(0);}
	}
//  if(tabId !=null){
//  	//TODO: modify: dang.tung
//    url = url+"&objectId="+tabId ;
//    ajaxAsyncGetRequest(url, false) ;
//  }

};

UIHorizontalTabs.prototype.checkContentAvailable = function(id) {
	var tabContent = document.getElementById(id);
	var textTrimmed = tabContent.innerHTML.replace(/\n/g, '')
	if (textTrimmed == '') {
		return false;
	}
	tabContent.style.display = 'block';
	return true;
};
	
/**
 * 
 */
UIHorizontalTabs.prototype.changeTabForUIFormTabpane = function(clickedElemt, formId, hiddenValue) {
	this.displayTabContent(clickedElemt, formId, hiddenValue) ;
	eXo.webui.UIForm.setHiddenValue(formId, 'currentSelectedTab', hiddenValue) ;
};

eXo.webui.UIHorizontalTabs = new UIHorizontalTabs();
