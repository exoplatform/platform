function EventManager () {
	this.onMouseDownHandlers = new eXo.core.HashMap() ;
	this.onMouseUpHandlers = new eXo.core.HashMap() ;
	document.onmousedown = this.preOnMouseDown ;
	document.onmouseup = this.preOnMouseUp ;
} ;

MouseEventManager.prototype.preOnMouseDown = function(evt) {
	evt.cancelBubble = true ;
	var mouseDownHandlers = this.onMouseDownHandlers ;
  for(var name in mouseDownHandlers.properties) {
    var method = mouseDownHandlers.get(name) ;
    method() ;
  }
} ;

MouseEventManager.prototype.postOnMouseDown = function() {
	
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