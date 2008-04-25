var count = 1 ;
eXo.core.DragDrop2 = {
	obj : null,
	
	init : function(o, oRoot) {
		o.onmousedown = Drag.start;
		
		o.root = oRoot && oRoot != null ? oRoot : o ;
		o.root.onmousedown = function() {
			this.style.zIndex = ++count ;
		}
		
		o.root.onDragStart = new Function();
		o.root.onDragEnd = new Function();
		o.root.onDrag = new Function();
	},
	
	start : function(e)	{
		var o = Drag.obj = this;
		e = Drag.fixE(e);
		var y = parseInt(o.root.style.top);
		var x = parseInt(o.root.style.left);
		o.lastMouseX = 		app.Browser.findMouseXInPage(e);
		o.lastMouseY = 		app.Browser.findMouseYInPage(e);
		o.root.onDragStart(x, y, o.lastMouseX, o.lastMouseY);
		document.onmousemove = Drag.drag;
		document.onmouseup = Drag.end;
		return false;
	},
	
	drag : function(e) {
		e = Drag.fixE(e);
		var o = Drag.obj;
		var ey = app.Browser.findMouseYInPage(e);
		var ex = app.Browser.findMouseXInPage(e);
		var y = parseInt(o.root.style.top);
		var x = parseInt(o.root.style.left);
		var nx, ny;

		nx = x + (ex - o.lastMouseX);
		ny = y + (ey - o.lastMouseY);
		
		Drag.obj.root.style["left"] = nx + "px";
		Drag.obj.root.style["top"] = ny + "px";
		Drag.obj.lastMouseX = ex;
		Drag.obj.lastMouseY = ey;
		
		Drag.obj.root.onDrag(nx, ny, ex, ey);
		return false;
	},
	
	end : function(e) {
		e = Drag.fixE(e);
		document.onmousemove = null;
		document.onmouseup = null;
		Drag.obj.root.onDragEnd( parseInt(Drag.obj.root.style[Drag.obj.hmode ? "left" : "right"]), 
		parseInt(Drag.obj.root.style[Drag.obj.vmode ? "top" : "bottom"]), e.clientX, e.clientY);
		Drag.obj = null;
	},
	
	fixE : function(e) {
		if (typeof e == 'undefined') e = window.event;
		if (typeof e.layerX == 'undefined') e.layerX = e.offsetX;
		if (typeof e.layerY == 'undefined') e.layerY = e.offsetY;
		return e;
	}
};