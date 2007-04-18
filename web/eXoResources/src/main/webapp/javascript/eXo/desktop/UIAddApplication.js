eXo.require('eXo.core.TemplateEngine');
eXo.require('eXo.webui.UIHorizontalTabs');
eXo.require('eXo.core.CacheJSonService');

function UIAddApplication() {
  
};

UIAddApplication.prototype.init = function(containerId, context) {
	var DOMUtil = eXo.core.DOMUtil ;
	var container = document.getElementById(containerId);
	
	if(document.getElementById("UIMaskWorkspaceJSTemplate") == null) {
		var uiAddAppContainer = document.createElement('div') ;
		uiAddAppContainer.id = "UIAddApplicationContainer" ;
		uiAddAppContainer.style.display = "none" ;
		uiAddAppContainer.innerHTML = eXo.core.TemplateEngine.merge("eXo/desktop/UIMaskWorkspace.jstmpl", context) ;
		
		container.appendChild(uiAddAppContainer) ;
	}
	var uiAddApplicationContainer = document.getElementById("UIAddApplicationContainer");
	eXo.desktop.UIAddApplication.showAddApplication(uiAddApplicationContainer);
	this.loadPortlets(false);
};

UIAddApplication.prototype.loadPortlets = function(refresh) {
	var uiAddApplicationContainer = document.getElementById("UIAddApplicationContainer");
	var url = eXo.env.server.context + "/service?serviceName=portletRegistry";
	if(refresh == null || refresh == undefined) var refresh = false;
  var category = eXo.core.CacheJSonService.getData(url, refresh);
  window.status = "Onload1.3";
  if(category == null || category == undefined) return;
  var itemList = eXo.core.DOMUtil.findFirstDescendantByClass(uiAddApplicationContainer, "div", "ItemList") ;
  var itemDetailList = eXo.core.DOMUtil.findFirstDescendantByClass(uiAddApplicationContainer, "div", "ItemDetailList") ;
  var items  = '';
  var itemDetails = ''; 
  var selected  = false;
  /*TODO: move HTML code to a .jstmpl file*/
  for(id in category.portletRegistry) {  	
		var cate = category.portletRegistry[id];
		if(!selected){
      items += '<div class="SelectedItem Item" onclick="eXo.webui.UIItemSelector.onClick(this)"';      
		} else {
			items += '<div class="Item" onclick="eXo.webui.UIItemSelector.onClick(this)"';
		}
	  items += 'onmouseover="eXo.webui.UIItemSelector.onOver(this, true)" onmouseout="eXo.webui.UIItemSelector.onOver(this, false)">' +
	           '  <div class="LeftItem">' +
	           '    <div class="RightItem"> ' + 
						 '		  	<div class="ItemTitle" id="'+id+'">' + 
						          cate["name"]+ 
						 '      </div>' +
						 '    </div>' + 
	           '  </div>' + 
	           '</div> ' ;
	  if(!selected) {
 	    itemDetails += '<div class="ItemDetail" style="display: block">';
	  } else {
	  	itemDetails += '<div class="ItemDetail" style="display: none">';
	  }
	   itemDetails += '<div class="ItemDetailTitle">' +
	          	    	'	 <div class="TitleIcon ViewListIcon"><span></span></div>' +
						  	    '	 <div class="Title">Applications List</div>' +
						  	    '	 <div style="clear: left;"><span></span></div>' +
	          	      '</div>';
	          	      
	  itemDetails += '  <div class="ApplicationListContainer">';
	  var portlets = cate["portlets"];
	    window.status = "Onload5";
	  for(id in portlets) {
	  	portlet = portlets[id];
	    itemDetails += '<div class="Application">' + 
					           '	<div class="TitleBarApplication">' + 
					           '		<div class="ApplicationItemIcon"><span></span></div>' + 
					           '		<div class="ApplicationLabel">'+portlet["title"]+'</div>' +
					           '    <div class="SelectButton"><span></span></div>' + 
					           ' 		<div class="AddButton" title="Add this application to the desktop page"' +
					           '         onclick="eXo.desktop.UIAddApplication.addPortlet(\''+id+'\')"><span></span></div>' + 
					           ' 		<div style="clear: both"></div>' + 
					           ' 	</div>' + 
					           '	<div class="ApplicationDescription">'+portlet["des"]+'</div>' + 
					           '</div>';
	  }
    itemDetails += '  </div>' +
									 '</div>';  
		if(!selected) selected = true;							 
  }
  itemList.innerHTML = items;  
  itemDetailList.innerHTML = itemDetails;
}

UIAddApplication.prototype.addPortlet = function(id) {
	var params = [{name: "portletId", value : id}] ;
	ajaxGet(eXo.env.server.createPortalURL("UIPortal", "AddPortletToDesktop", true, params)) ;
};

UIAddApplication.prototype.showAddApplication = function(object) {
	eXo.core.UIMaskLayer.createMask("UIPortalApplication", object, 30) ;
	eXo.desktop.UIDockbar.reset() ;
};

UIAddApplication.prototype.removeAddApplication = function() {
	var uiAddApplicationContainer = document.getElementById("UIAddApplicationContainer") ;
	var maskLayer = uiAddApplicationContainer.previousSibling ;
	eXo.core.UIMaskLayer.removeMask(maskLayer) ;
	
	uiAddApplicationContainer.parentNode.removeChild(uiAddApplicationContainer) ;
};

UIAddApplication.prototype.importJavascript = function(object) {
	eXo.require(object); 
};

eXo.desktop.UIAddApplication = new UIAddApplication() ;