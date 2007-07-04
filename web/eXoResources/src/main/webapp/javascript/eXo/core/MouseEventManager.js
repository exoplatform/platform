function EventManager () {
	this.onMouseDownHandlers = new Array() ;
	this.onMouseUpHandlers = new Array() ;
	document.onmousedown = this.preOnMouseDown ;
	document.onmouseup = this.preOnMouseUp ;
} ;

MouseEventManager.prototype.preOnMouseDown = function(evt) {
	evt.cancelBubble = true ;
	var len = this.onMouseDownHandlers.length ;
	if (len <= 0) return ;
	for(var i = 0 ; i < len ; i++) {
		try {
			this.onMouseDownHandlers[i](evt) ;	
		} catch(e) {
			alert(e.message) ;
		}		
	}
} ;

MouseEventManager.prototype.postOnMouseDown = function() {
	
} ;

MouseEventManager.prototype.preOnMouseUp = function() {
	
} ;

MouseEventManager.prototype.postOnMouseUp = function() {
	
} ;

MouseEventManager.prototype.addMouseDownHandler = function(method) {
	this.onMouseDownHandlers.push(method) ;
} ;

MouseEventManager.prototype.addMouseUpHandler = function(method) {
	this.onMouseUpHandlers.push(method) ;
} ;

eXo.core.MouseEventManager = new MouseEventManager() ;