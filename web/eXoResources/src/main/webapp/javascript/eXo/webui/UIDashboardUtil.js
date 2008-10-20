eXo.webui.UIDashboardUtil = {

	findPosX : function(obj) {
	  var curleft = 0;
	  var uiWorkspaceContainer = document.getElementById("UIWorkspaceContainer");
	  var uiWorkingWorkspace = document.getElementById("UIWorkingWorkspace");
	  while (obj) {
	  	if(uiWorkspaceContainer!=null && uiWorkspaceContainer.style.display!="none"
	  					 && eXo.core.Browser.getBrowserType()=="ie"){
	  		var uiPageDesktop = document.getElementById("UIPageDesktop");
	  		if( (uiPageDesktop!=null && eXo.core.DOMUtil.hasClass(obj,"UIPageDesktop") && eXo.core.Browser.isIE7()) 
	  					|| (uiPageDesktop==null && eXo.core.DOMUtil.hasClass(obj,"PORTLET-FRAGMENT")) ){
	  			curleft += (obj.offsetLeft - uiWorkingWorkspace.offsetLeft);
	  			obj = obj.offsetParent ;
	  			continue;
	  		}
	  	}
  		curleft += obj.offsetLeft ;
	    obj = obj.offsetParent ;
	  }
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
		var posXObject = eXo.webui.UIDashboardUtil.findPosX(object) ;
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
	  var posYObject = eXo.webui.UIDashboardUtil.findPosY(object) ;
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
  	var objX =  eXo.webui.UIDashboardUtil.findPosX(obj) ;
  	var containerX =  eXo.webui.UIDashboardUtil.findPosX(container) ;  
	  return (objX - containerX) ;
	},

	findPosYInContainer : function(obj, container) {
	  var objY = eXo.webui.UIDashboardUtil.findPosY(obj) ;
	  var containerY =  eXo.webui.UIDashboardUtil.findPosY(container) ;
	  return (objY - containerY) ;
	}, 
	
	setPositionInContainer : function(container, component, posX, posY) {
		var offsetX = component.offsetLeft ;
		var offsetY = component.offsetTop ;
	
		var posXInContainer = eXo.webui.UIDashboardUtil.findPosXInContainer(component, container) ;
		var posYInContainer = eXo.webui.UIDashboardUtil.findPosYInContainer(component, container) ;
	
		var deltaX = posX - (posXInContainer - offsetX) ;
		var deltaY = posY - (posYInContainer - offsetY) ;
	
		component.style.left = deltaX + "px" ;
		component.style.top = deltaY + "px" ;
	}, 
	
	isIn : function(x, y, component) {
	  var componentLeft = eXo.webui.UIDashboardUtil.findPosX(component);
	  var componentRight = componentLeft + component.offsetWidth ;
	  var componentTop = eXo.webui.UIDashboardUtil.findPosY(component) ;
	  var componentBottom = componentTop + component.offsetHeight ;
	  var isOver = false ;

	  if((componentLeft < x) && (x < componentRight)) {
	    if((componentTop < y) && (y < componentBottom)) {
	      isOver = true ;
	    }
	  }
	  
	  return isOver ;
	},
	
	findColIndexInDashboard : function(dragObj){
		var col = dragObj.parentNode;
		if(col==null) return null;
		var dashboardContainer = eXo.core.DOMUtil.findAncestorByClass(col, "DashboardContainer");
		var columns = eXo.core.DOMUtil.findDescendantsByClass(dashboardContainer, "div", "UIColumn");
		for(var i=0; i<columns.length; i++){
			if(col.id == columns[i].id){
				return i;
			}
		}
	},
	
	findRowIndexInDashboard : function(dragObj){
		var modules = eXo.core.DOMUtil.getChildrenByTagName(dragObj.parentNode, "div");
		for(var i=0; i<modules.length; i++){
			if(modules[i].id == dragObj.id) return i;
		}
	},
	
	createRequest : function(componentId, action, colIndex, rowIndex, objectId){
		var url = eXo.env.server.portalBaseURL;
		url += '?portal:componentId=' + componentId +
							'&portal:type=action&uicomponent=UIDashboard&op=' + action ;
		if(objectId!=null)					
			url += '&objectId=' + objectId ;
		url += '&colIndex=' + colIndex ;
		url += '&rowIndex=' + rowIndex ;
		url += '&ajaxRequest=true';
		return url;
	}
}