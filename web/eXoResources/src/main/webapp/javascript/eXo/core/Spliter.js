function Spliter() {	
} ;
Spliter.prototype.exeRowSplit = function(e , markerobj) {
	_e = (window.event) ? window.event : e ;
	this.posY = _e.clientY; 
	var marker = (typeof(markerobj) == "string")? document.getElementById(markerobj):markerobj ;
	this.beforeArea = eXo.core.DOMUtil.findPreviousElementByTagName(marker, "div") ;
	this.afterArea = eXo.core.DOMUtil.findNextElementByTagName(marker, "div") ;	
	this.beforeArea.style.height = this.beforeArea.offsetHeight + "px" ;
	this.afterArea.style.height = this.afterArea.offsetHeight + "px" ;	
	this.beforeY = this.beforeArea.offsetHeight ;
	this.afterY = this.afterArea.offsetHeight ;
	document.onmousemove = eXo.core.Spliter.adjustHeight ;	
	document.onmouseup = eXo.core.Spliter.clear ;
} ;
Spliter.prototype.adjustHeight = function(evt) {
	evt = (window.event) ? window.event : evt ;
	var Spliter = eXo.core.Spliter ;
	var delta = evt.clientY - Spliter.posY ;
	var afterHeight = (Spliter.afterY - delta) ;
	var beforeHeight = (Spliter.beforeY + delta) ;
	if (beforeHeight <= 0  || afterHeight <= 0) return ;
	Spliter.beforeArea.style.height =  beforeHeight + "px" ;
	Spliter.afterArea.style.height =  afterHeight + "px" ;	
} ;
Spliter.prototype.clear = function() {
	document.onmousemove = null ;
} ;
eXo.core.Spliter = new Spliter() ;