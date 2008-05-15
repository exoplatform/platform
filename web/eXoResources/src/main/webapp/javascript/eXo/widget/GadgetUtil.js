eXo.widget.GadgetUtil = {

	findPosX : function(obj) {
//		window.status ="";
	  var curleft = 0;
	  var uiWorkspaceContainer = document.getElementById("UIWorkspaceContainer");
	  var uiWorkingWorkspace = document.getElementById("UIWorkingWorkspace");
	  while (obj) {
	  	if(uiWorkspaceContainer!=null && uiWorkspaceContainer.style.display!="none"
	  					 && eXo.core.Browser.getBrowserType()=="ie"){
	  		var uiPageDesktop = document.getElementById("UIPageDesktop");
	  		if( (uiPageDesktop!=null && eXo.core.DOMUtil.hasClass(obj,"UIPageDesktop") && eXo.core.Browser.isIE7()) 
	  					|| (uiPageDesktop==null && eXo.core.DOMUtil.hasClass(obj,"UIWindow")) ){
	  			curleft += (obj.offsetLeft - uiWorkingWorkspace.offsetLeft);
//	  			window.status +=" "+obj.id+":"+obj.offsetLeft;
	  			obj = obj.offsetParent ;
	  			continue;
	  		}
	  	}
//	  	window.status +=" "+obj.id+":"+obj.offsetLeft;
  		curleft += obj.offsetLeft ;
	    obj = obj.offsetParent ;
	  }
//	  document.title = curleft;
	  return curleft ;
	} ,
	
	findPosY : function(obj) {
	  var curtop = 0 ;
	  while (obj) {
	    curtop += obj.offsetTop ;
	    obj = obj.offsetParent ;
	  }
	  return curtop ;
	} ,
	
	findMouseRelativeX : function (object, e){
		var posx = -1 ;
		var posXObject = eXo.widget.GadgetUtil.findPosX(object) ;
		if (!e) e = window.event ;
		if (e.pageX || e.pageY) {
		  posx = e.pageX - posXObject ;
		} else if (e.clientX || e.clientY) {
		  posx = e.clientX + document.body.scrollLeft - posXObject ;
		}
		return posx ;
	},
	
	findMouseRelativeY : function(object, e) {
	  var posy = -1 ;
	  var posYObject = eXo.widget.GadgetUtil.findPosY(object) ;
	  if (!e) e = window.event ;
	  if (e.pageY) {
	    posy = e.pageY - posYObject ;
	  } else if (e.clientX || e.clientY) {
	    //IE 6
	    if (document.documentElement && document.documentElement.scrollTop) {
	      posy = e.clientY + document.documentElement.scrollTop - posYObject ;
	    } else {
	      posy = e.clientY + document.body.scrollTop - posYObject ;
	    }
	  }
	  return  posy ;
	},
	
	findPosXInContainer : function(obj, container) {
  	var objX =  eXo.widget.GadgetUtil.findPosX(obj) ;
  	var containerX =  eXo.widget.GadgetUtil.findPosX(container) ;  
	  return (objX - containerX) ;
	},

	findPosYInContainer : function(obj, container) {
	  var objY = eXo.widget.GadgetUtil.findPosY(obj) ;
	  var containerY =  eXo.widget.GadgetUtil.findPosY(container) ;
	  return (objY - containerY) ;
	}, 
	
	setPositionInContainer : function(container, component, posX, posY) {
		var offsetX = component.offsetLeft ;
		var offsetY = component.offsetTop ;
	
		var posXInContainer = eXo.widget.GadgetUtil.findPosXInContainer(component, container) ;
		var posYInContainer = eXo.widget.GadgetUtil.findPosYInContainer(component, container) ;
	
		var deltaX = posX - (posXInContainer - offsetX) ;
		var deltaY = posY - (posYInContainer - offsetY) ;
	
		component.style.left = deltaX + "px" ;
		component.style.top = deltaY + "px" ;
	}, 
	
	isIn : function(x, y, component) {
	  var componentLeft = eXo.widget.GadgetUtil.findPosX(component);
	  var componentRight = componentLeft + component.offsetWidth ;
	  var componentTop = eXo.widget.GadgetUtil.findPosY(component) ;
	  var componentBottom = componentTop + component.offsetHeight ;
	  var isOver = false ;

	  if((componentLeft < x) && (x < componentRight)) {
	    if((componentTop < y) && (y < componentBottom)) {
	      isOver = true ;
	    }
	  }
	  
	  return isOver ;
	}
}