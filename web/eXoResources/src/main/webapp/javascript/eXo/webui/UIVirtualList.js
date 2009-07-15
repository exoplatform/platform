function UIVirtualList() {
	this.componentMark = "rel";
	this.finishLoadMark = "finish";
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
	var scrollable_gap = scrollerHeight - componentHeight;
	// if scrollbar reaches bottom
	if (scrollable_gap <= scrollPosition) {	
		var dataFeed = DOMUtil.findDescendantById(appendFragment, dataFeedId);		
		var appendHTML = dataFeed.innerHTML;
		var appendObj = DOMUtil.findDescendantById(storeFragment, dataFeedId);
		if (appendObj == null || appendObj == "undefined") {
			appendObj = storeFragment;
			appendHTML = dataFeed.parentNode.innerHTML;
		}		
		appendObj.innerHTML += appendHTML;
		dataFeed.innerHTML = "";
		
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

UIVirtualList.prototype.loadFinished = function(generateId) {
  var uicomponent = this.getUIComponent(generateId);
  if (uicomponent == null) return;
  uicomponent.setAttribute(this.finishLoadMark, "true");
}

eXo.webui.UIVirtualList = new UIVirtualList();