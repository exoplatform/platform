function UIPortlet() {
	this.maxIndex = 0;
} ;

UIPortlet.prototype.onControlOver = function(element, isOver) {
  var originalElementName = element.className ;
  if(isOver) {
    var overElementName = "ControlIcon Over" + originalElementName.substr(originalElementName.indexOf(" ") + 1, 30) ;
    element.className   = overElementName;
   	if(element.className == "ControlIcon OverRestoreIcon"){ 
   		var hiddenAttribute = eval('(' + eXo.core.DOMUtil.findFirstChildByClass(element, "div", "").innerHTML + ')');
   		element.title = hiddenAttribute.modeTitle ;
   	}
    if(element.className == "ControlIcon OverMaximizedIcon"){ 
    	var hiddenAttribute = eval('(' + eXo.core.DOMUtil.findFirstChildByClass(element, "div", "").innerHTML + ')');
    	element.title = hiddenAttribute.normalTitle ;
    }
  } else {
    var over = originalElementName.indexOf("Over") ;
    if(over >= 0) {
      var overElementName = "ControlIcon " + originalElementName.substr(originalElementName.indexOf(" ") + 5, 30) ;
      element.className   = overElementName ;
    }
  }
} ;

eXo.webui.UIPortlet = new UIPortlet() ;
