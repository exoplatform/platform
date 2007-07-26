function MouseEventManager () {
	this.onMouseDownHandlers = new eXo.core.HashMap() ;
	this.onMouseUpHandlers = new eXo.core.HashMap() ;
	document.onmousedown = this.docMouseDownEvt ;
	document.onmouseup = this.docMouseUpEvt ;
} ;

MouseEventManager.prototype.docMouseDownEvt = function(evt) {
	if(!evt) evt = window.event ;
	eXo.core.MouseEventManager.preOnMouseDown(evt) ;
	eXo.core.MouseEventManager.postOnMouseDown(evt) ;
} ;

MouseEventManager.prototype.preOnMouseDown = function(evt) {
	evt.cancelBubble = true ;
	var mouseDownHandlers = eXo.core.MouseEventManager.onMouseDownHandlers ;
  for(var name in mouseDownHandlers.properties) {
    var method = mouseDownHandlers.get(name) ;
    method() ;
  }
} ;

MouseEventManager.prototype.postOnMouseDown = function(evt) {
	
} ;

MouseEventManager.prototype.docMouseUpEvt = function() {
	var mouseUpHandlers = eXo.core.MouseEventManager.onMouseUpHandlers ;
	
} ;

MouseEventManager.prototype.preOnMouseUp = function() {
	
} ;

MouseEventManager.prototype.postOnMouseUp = function() {
	
} ;

MouseEventManager.prototype.addMouseDownHandler = function(id, method) {
	this.onMouseDownHandlers.put(id, method) ;
} ;

MouseEventManager.prototype.addMouseUpHandler = function(id, method) {
	this.onMouseUpHandlers.put(id, method) ;
} ;

eXo.core.MouseEventManager = new MouseEventManager() ;