function Resize() {} ;

Resize.prototype.init = function(o, oToResizeWidth, oToResizeHeight) {
	o.onmousedown = Resize.start ;
	o.oToResizeWidth = oToResizeWidth || o ;
	o.oToResizeHeight = oToResizeHeight || o.oToResizeWidth ;
	o.oToResizeWidth.style.width = o.oToResizeWidth.offsetWidth + "px" ;
	o.oToResizeHeight.style.height = o.oToResizeHeight.offsetHeight + "px" ;
} ;
	
Resize.prototype.start = function(e)	{
	Resize.obj = new Object();
	var o = Resize.obj.elemt = this;
	e = app.fixE(e);
	Resize.obj.initMouseX = browser.findMouseXInPage(e);
	Resize.obj.initMouseY = browser.findMouseYInPage(e);
	Resize.obj.initWidth = parseInt(o.oToResizeWidth.style.width) ;
	Resize.obj.initHeight = parseInt(o.oToResizeHeight.style.height);
//	window.status = Resize.obj.initWidth + " : " + Resize.obj.initHeight;
	document.onmousemove = Resize.drag;
	document.onmouseup = Resize.end;
	return false;
} ;
	
Resize.prototype.drag = function(e) {
	e = app.fixE(e);
	var o = Resize.obj.elemt;
	var nx = Resize.obj.initWidth + (browser.findMouseXInPage(e) - Resize.obj.initMouseX) ;
	var ny = Resize.obj.initHeight + (browser.findMouseYInPage(e) - Resize.obj.initMouseY) ;
	nx = Math.max(100, nx) ;
	ny = Math.max(100, ny) ;
	o.oToResizeHeight.style.height = ny + 'px';
	o.oToResizeWidth.style.width = nx + 'px' ;
	return false;
} ;
	
Resize.prototype.end = function(e) {
	e = app.fixE(e);
	document.onmousemove = null;
	document.onmouseup = null ;
	delete Resize.obj;
} ;

eXo.core.Resize = new Resize() ;