function MouseEventManager () {
} ;

MouseEventManager.prototype.docMouseDownEvt = function(evt) {
	alert(eXo.core.MouseEventManager.onMouseUpHandlers) ;
	eXo.core.MouseEventManager.onMouseUpHandlers(evt) ;
	document.onmousedown = null ;
} ;

MouseEventManager.prototype.addMouseDownHandler = function(method) {
	document.onmousedown = this.docMouseDownEvt ;
	this.onMouseDownHandlers = method ;
} ;

MouseEventManager.prototype.docMouseUpEvt = function() {
	var mouseUpHandlers = eXo.core.MouseEventManager.onMouseUpHandlers ;
	
} ;

MouseEventManager.prototype.addMouseUpHandler = function(method) {
	document.onmouseup = this.docMouseUpEvt ;
	this.onMouseUpHandlers = method ;
} ;

MouseEventManager.prototype.docMouseClickEvt = function(evt) {
	if(typeof(eXo.core.MouseEventManager.onMouseClickHandlers) == "string") eval(eXo.core.MouseEventManager.onMouseClickHandlers) ;
	else eXo.core.MouseEventManager.onMouseClickHandlers(evt) ;
	document.onclick = null ;
} ;

MouseEventManager.prototype.addMouseClickHandler = function(method) {
	document.onclick = this.docMouseClickEvt ;
	this.onMouseClickHandlers = method ;
} ;

eXo.core.MouseEventManager = new MouseEventManager() ;