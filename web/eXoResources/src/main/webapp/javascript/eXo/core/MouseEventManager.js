function MouseEventManager () {} ;

MouseEventManager.prototype.addMouseDownHandler = function(method) {
	document.onmousedown = this.docMouseDownEvt ;
	this.onMouseDownHandlers = method ;
} ;

MouseEventManager.prototype.docMouseDownEvt = function(evt) {
	if(!evt) evt = window.event ;
	evt.cancelBubble = true ;

	if(eXo.core.MouseEventManager.onMouseDownHandlers == null) return;
	if(typeof(eXo.core.MouseEventManager.onMouseDownHandlers) == "string") eval(eXo.core.MouseEventManager.onMouseDownHandlers) ;
	else eXo.core.MouseEventManager.onMouseDownHandlers(evt) ;
	document.onmousedown = null ;
} ;

MouseEventManager.prototype.addMouseUpHandler = function(method) {
	document.onmouseup = this.docMouseUpEvt ;
	this.onMouseUpHandlers = method ;
} ;

MouseEventManager.prototype.docMouseUpEvt = function() {
	var mouseUpHandlers = eXo.core.MouseEventManager.onMouseUpHandlers ;
	
} ;

MouseEventManager.prototype.docMouseClickEvt = function(evt) {
	if(!evt) evt = window.event ;
	evt.cancelBubble = true ;
	
	if(typeof(eXo.core.MouseEventManager.onMouseClickHandlers) == "string") eval(eXo.core.MouseEventManager.onMouseClickHandlers) ;
	else eXo.core.MouseEventManager.onMouseClickHandlers(evt) ;
	document.onclick = null ;
} ;

MouseEventManager.prototype.addMouseClickHandler = function(method) {
	document.onclick = this.docMouseClickEvt ;
	this.onMouseClickHandlers = method ;
} ;

eXo.core.MouseEventManager = new MouseEventManager() ;