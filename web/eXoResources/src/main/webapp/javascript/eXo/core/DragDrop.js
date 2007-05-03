function DragDropEvent(clickObject, dragObject) {
  this.clickObject = clickObject ;
  this.dragObject = dragObject && dragObject != null ? dragObject : clickObject ;
  this.foundTargetObject = null ;
  this.lastFoundTargetObject = null ;
  this.junkMove = false ;
  
  //alert(this.junkMove);
  
 	if(isNaN(parseInt(this.dragObject.style.left))) this.dragObject.style.left = "0px" ;
	if(isNaN(parseInt(this.dragObject.style.top))) this.dragObject.style.top = "0px" ;
} ;

DragDropEvent.prototype.update = function(foundTargetObject, junkMove) {
  this.lastFoundTargetObject = this.foundTargetObject ;
  this.foundTargetObject = foundTargetObject ;
  this.junkMove = junkMove ;
  //alert(this.foundTargetObject) ;
}

DragDropEvent.prototype.isJunkMove = function() {
  return this.junkMove ;
};

/*************************************************************************************/

var DragDrop = {
  dropableTargets : null ,
  dndEvent : null ,

  initCallback: null ,
  dragCallback: null ,
  dropCallback: null ,
  destroyCallback: null ,
  isJunkMoveCallback: null ,

	init : function(dropableTargets, clickObject, dragObject, e) {
    eXo.core.Mouse.init(e) ;
    DragDrop.dropableTargets = dropableTargets ;
    
    dndEvent = DragDrop.dndEvent = new DragDropEvent(clickObject, dragObject) ;
		document.onmousemove	= DragDrop.onMouseMove ;
		document.onmouseup		= DragDrop.onDrop ;
		
		// *************************************
		dragObject.onmouseup = null;
		// *************************************
		// 		
    if(this.initCallback != null) {
      this.initCallback(dndEvent) ;
    }
  },

  onMouseMove : function(e) {
    eXo.core.Mouse.update(e) ;
    DragDrop.dndEvent.backupMouseEvent = e ;
		var dndEvent = DragDrop.dndEvent ;
		var dragObject =  dndEvent.dragObject ;

		var y = parseInt(dragObject.style.top);
		var x = parseInt(dragObject.style.left);

		dragObject.style["left"] =  x + eXo.core.Mouse.deltax + "px" ;
		dragObject.style["top"]  =  y + eXo.core.Mouse.deltay + "px" ;
		
    if(DragDrop.dragCallback != null) {
      var foundTarget = DragDrop.findDropableTarget(dndEvent, DragDrop.dropableTargets, e) ;
      var junkMove =  DragDrop.isJunkMove(dragObject, foundTarget) ;
      dndEvent.update(foundTarget, junkMove) ;
      DragDrop.dragCallback(dndEvent) ;
    }
    
		return false;
	},

	onDrop : function(e) {
    /* should not remove this or move this line to  destroy since the onMouseMove method keep calling */
		document.onmousemove	= null ;
    if(DragDrop.dropCallback != null) {
      var dndEvent = DragDrop.dndEvent ;
      var dragObject = dndEvent.dragObject ;

      var foundTarget = DragDrop.findDropableTarget(dndEvent, DragDrop.dropableTargets, e) ;
      var junkMove =  DragDrop.isJunkMove(dragObject, foundTarget) ;
      //foundTarget.style.border = "solid 2px blue";

      dndEvent.update(foundTarget, junkMove) ;
      DragDrop.dropCallback (dndEvent) ;
    }
    DragDrop.destroy() ;
	},

	destroy : function(e) {
    if(this.destroyCallback != null) {
      this.destroyCallback(DragDrop.dndEvent) ;
    }
    //alert("Destroy");
    document.onmouseup = null ;

		//******************************
//    document.onmouseup= function(e) {
//			eXo.webui.UIPopup.closeAll() ;
//		} ;
		
//    DragDrop.dndEvent.dragObject.onmouseup = function(e) {
//			if(!e) e = window.event ;
//			e.cancelBubble = true ;
//		} ;
		//******************************


	  DragDrop.dndEvent = null ;
    DragDrop.dropableTargets = null ;

    DragDrop.initCallback = null ;
    DragDrop.dragCallback = null ;
    DragDrop.dropCallback = null ;
    DragDrop.destroyCallback = null ;
    DragDrop.isJunkMoveCallback = null ;
	},
  
  findDropableTarget : function(dndEvent, dropableTargets, mouseEvent) {
    if(dropableTargets == null) return null ;
    var mousexInPage = eXo.core.Browser.findMouseXInPage(mouseEvent) ;
    var mouseyInPage = eXo.core.Browser.findMouseYInPage(mouseEvent) ;
    
		var clickObject = dndEvent.clickObject ;
		var dragObject = dndEvent.dragObject ;
    var foundTarget = null ;
    for(var i = 0; i < dropableTargets.length; i++) {
      var ele =  dropableTargets[i] ;
      
//      window.status = "TEST: " + DragDrop.isIn(mousexInPage, mouseyInPage, ele) ;
      if(dragObject != ele && DragDrop.isIn(mousexInPage, mouseyInPage, ele)) {
        if(foundTarget == null) {
          foundTarget = ele ;
        } else {
          if(DragDrop.isAncestor(foundTarget, ele)) {
            foundTarget = ele ;
          }
        } 
      }
    }
   	
    return foundTarget ;
  } ,
  
  isAncestor : function(ancestor , child) {
  	var path = child.id ;
    var parent = child.parentNode ;
    while(parent != null) {
    	path = parent.className +   "/" + path ;
    	//window.status = path ;
      if(parent == ancestor) 	return true ;
      var tmp = parent.parentNode ;
      parent = tmp ;
    }
    return false ;
  },
  
  isIn : function(x, y, component) {
    var componentLeft = eXo.core.Browser.findPosX(component) ;
    var componentRight = componentLeft + component.offsetWidth ;
    var componentTop = eXo.core.Browser.findPosY(component) ;
    var componentBottom = componentTop + component.offsetHeight ;
    var isover = false ;
    
    //window.status = "BROWSER TYPE: " + eXo.core.Browser.getBrowserType();
    
    if(eXo.core.Browser.getBrowserType() == "ie") {
    	componentLeft = componentLeft / 2 ;
    }
    
//    window.status = "componentLeft: " + componentLeft + "  x: " + x ;
        
    if(componentLeft < x && x < componentRight) {
      if(componentTop < y && y < componentBottom) {
        isover = true ;
      }
    }
    return isover ;
  },
    
  isJunkMove : function(src, target) {
    if(DragDrop.isJunkMoveCallback != null) {
      return DragDrop.isJunkMoveCallback(src, target) ;
    }
    if(target == null) return true ;
    return false ;
  }
};

eXo.core.DragDrop = DragDrop ;