function UIVirtualList() {};

UIVirtualList.prototype.init = function(componentId) {
	var DOMUtil = eXo.core.DOMUtil;
	this.container = document.getElementById(componentId);
	this.scroller = DOMUtil.findDescendantById(this.container, "scroller");
	this.tmpScroller = DOMUtil.findDescendantById(this.container, "tmpScroller");
	this.scrollPosition = 0;
	this.containerHeight = this.container.offsetHeight;
	this.scrollerHeight = this.container.scrollHeight;
	this.finished = false;	
}

UIVirtualList.prototype.scrollMove = function() {
	if (this.finished) return;
	this.scrollPosition = this.container.scrollTop;
	this.scrollerHeight = this.container.scrollHeight;	
	var scrollable_gap = this.scrollerHeight - this.containerHeight;
	// if scrollbar reaches bottom
	if (scrollable_gap <= this.scrollPosition) {
		var DOMUtil = eXo.core.DOMUtil;
		var dataFeed = DOMUtil.findDescendantById(this.tmpScroller, "DataFeed");
		
		var appendHTML = dataFeed.innerHTML;
		var appendObj = DOMUtil.findDescendantById(this.scroller, "DataFeed");
		if (appendObj == null || appendObj == "undefined") {
			appendObj = this.scroller;
			appendHTML = dataFeed.parentNode.innerHTML;
		}		
		appendObj.innerHTML += appendHTML;
				
		ajaxGet(eXo.env.server.portalBaseURL + '?portal:componentId=' 
						+ this.container.id +'&portal:action=LoadNext&ajaxRequest=true');
	}
}

UIVirtualList.prototype.loadFinished = function() {
	this.finished = true;
}

eXo.webui.UIVirtualList = new UIVirtualList();