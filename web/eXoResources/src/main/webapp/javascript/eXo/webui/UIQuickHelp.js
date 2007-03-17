function UIQuickHelp() {

};

UIQuickHelp.prototype.onResize = function(qhEle, width, height) {
	var scrollArea = eXo.core.DOMUtil.findFirstDescendantByClass(qhEle, "div", "ScrollArea") ;
  if(scrollArea == null) return ;
  if(height != null &&  height > 60) {
    scrollArea.style.height = (height - 68) + "px" ;
    scrollArea.style.overflow = "auto" ;
  }

  if(width != null) {
    scrollArea.style.width = width + "px" ;
  }
};

UIQuickHelp.prototype.fitParentHeight = function(qhEle) {
	//alert("ScrollArea height (QuickHelp.js): "+qhEle.parentNode.offsetHeight);
  this.onResize(qhEle, null, qhEle.parentNode.offsetHeight) ;
};

UIQuickHelp.prototype.switchPage = function(clickedElemt, blnOpt) {
	var quickHelpElemt = eXo.core.DOMUtil.findAncestorByClass(clickedElemt, 'UIQuickHelp');

	var pages = eXo.core.DOMUtil.findDescendantsByClass(quickHelpElemt, "div", "UIQuickHelpContentPage");
	for ( var i = 0 ; pages.length; i++ ) {
		if ( pages[i].style.display == "block") {
			if ( blnOpt ) {
				if ( (i+1) == pages.length ) return ;
				pages[i].style.display = "none" ;
				pages[i+1].style.display = "block" ;
				this.changeButtonStatus(quickHelpElemt, pages.length, i+1) ;
			} else {
				if ( i == 0 ) return;
				pages[i].style.display = "none";
				pages[i-1].style.display = "block";
				this.changeButtonStatus(quickHelpElemt, pages.length, i-1) ;
			}
			return ;
		}
	}
};

UIQuickHelp.prototype.changeQuickHelpPage = function(quickHelpElemt, selectedItem) {
	if ( quickHelpElemt == null ) return;
	if ( quickHelpElemt.className != "UIQuickHelp" ) quickHelpElemt = eXo.core.DOMUtil.findAncestorByClass(quickHelpElemt, 'UIQuickHelp');
	var pages = eXo.core.DOMUtil.findDescendantsByClass(quickHelpElemt, "div", "UIQuickHelpContentPage");

	for ( var i = 0 ; i < pages.length; i++ ) {
		if ( pages[i].id == selectedItem ) {
			for ( var j = 0; j < pages.length; j++ ) {
				pages[j].style.display = "none";				
			}
			pages[i].style.display = "block";
			this.changeButtonStatus(quickHelpElemt, pages.length, i);
			return;
		}
	}
};

UIQuickHelp.prototype.changeButtonStatus = function(quickHelpElemt, pageNumber, intSelectedPage) {
	var backButton = eXo.core.DOMUtil.findFirstDescendantByClass(quickHelpElemt, "div", "BackButton");
	var forwardButton = eXo.core.DOMUtil.findFirstDescendantByClass(quickHelpElemt, "div", "ForwardButton");
	if ( intSelectedPage > 0 )
		backButton.className = "BackButton BlueBackArrow16x16Icon";
	else
		backButton.className = "BackButton GrayBackArrow16x16Icon";

	if ( intSelectedPage < (pageNumber -1) ) {
		forwardButton.className = "ForwardButton BlueNextArrow16x16Icon";
	} else {
		forwardButton.className = "ForwardButton GrayNextArrow16x16Icon";
	}
}

eXo.webui.UIQuickHelp = new UIQuickHelp();
