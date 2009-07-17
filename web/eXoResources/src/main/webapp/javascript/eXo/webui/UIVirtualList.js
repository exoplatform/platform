function UIVirtualList() {
	this.componentMark = "rel";
	this.finishLoadMark = "finish";
	this.storeMark = "store";
}

UIVirtualList.prototype.init = function(generateId) {
  var uicomponent = this.getUIComponent(generateId);
  if (uicomponent == null) return;
  var children = eXo.core.DOMUtil.getChildrenByTagName(uicomponent,"div");
  var appendFragment = children[1];
  var initHeight = appendFragment.offsetHeight - 100;
  uicomponent.style.height = initHeight + "px";  
}

UIVirtualList.prototype.scrollMove = function(uicomponent, url) {
	var DOMUtil = eXo.core.DOMUtil;	
	var finished = uicomponent.getAttribute(this.finishLoadMark);	
	if (finished == "true") return;
	
	var children = DOMUtil.getChildrenByTagName(uicomponent,"div");
	var storeFragment = children[0]; // store fragment
  var appendFragment = children[1]; // append fragment
  
	var componentHeight = uicomponent.offsetHeight;	
	var dataFeedId = uicomponent.getAttribute(this.componentMark);	
	var scrollPosition = uicomponent.scrollTop;
	var scrollerHeight = uicomponent.scrollHeight;	
	var scrollable_gap = scrollerHeight - (scrollPosition + componentHeight);	
	// if scrollbar reaches bottom	
	if (scrollable_gap <= 1) {
	  //alert(scrollerHeight + " - " + scrollPosition + " - " + scrollable_gap);
		var dataFeed = DOMUtil.findDescendantById(appendFragment, dataFeedId);
		var appendHTML = dataFeed.innerHTML;
		storeFragment.setAttribute(this.storeMark, appendHTML);
		
		ajaxGet(url);
	}
}

UIVirtualList.prototype.getUIComponent = function(generateId) {
  var dataFeed = document.getElementById(generateId);
  if (dataFeed == null || dataFeed == "undefined") return null;
  var parent = dataFeed.parentNode ;
  while (parent != null) {
    var relValue = parent.getAttribute(this.componentMark);
    if (generateId == relValue) return parent;    
    parent = parent.parentNode ;
  }
  return null;
}

UIVirtualList.prototype.updateList = function(generateId) {
  var DOMUtil = eXo.core.DOMUtil;
  var uicomponent = this.getUIComponent(generateId);
  if (uicomponent == null) return;
  var children = DOMUtil.getChildrenByTagName(uicomponent,"div");
  var storeFragment = children[0]; // store fragment
  var appendFragment = children[1]; // append fragment
  var dataFeedId = uicomponent.getAttribute(this.componentMark);
  var dataFeed = DOMUtil.findDescendantById(appendFragment, dataFeedId);
  dataFeed.innerHTML = storeFragment.getAttribute(this.storeMark) + dataFeed.innerHTML;  
}

UIVirtualList.prototype.loadFinished = function(generateId) {  
  var uicomponent = this.getUIComponent(generateId);
  if (uicomponent == null) return;
  uicomponent.setAttribute(this.finishLoadMark, "true");
}

eXo.webui.UIVirtualList = new UIVirtualList();