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
  
  /**Repair: by Vu Duy Tu **/
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
						  	    '	 <div class="Title">Select Portlets</div>' +
						  	    '	 <div style="clear: left;"><span></span></div>' +
	          	      '</div>';
	          	      
	  itemDetails += '  <div class="ApplicationListContainer">';
	  var portlets = cate["portlets"];
	    window.status = "Onload5";
	   var count=0;var i = 2;
	  for(id in portlets) {
	  	portlet = portlets[id];
	  	var cssFloat = "float:left";
	  	count = i%2;
      if(count == 1)cssFloat = "float:right";
      ++i; if(i==100)i=2;
      var srcBG = "/eXoResources/skin/portal/webui/component/view/UIPageDesktop/DefaultSkin/icons/80x80/" + portlet["title"]+".png";
      //var trurl = document.URL = "/eXoResources/skin/portal/webui/component/view/UIPageDesktop/DefaultSkin/icons/80x80/" + portlet["title"]+".png";
			//alert(trurl);
			if(0) {
				srcBG = "/eXoResources/skin/portal/webui/component/view/UIPageDesktop/DefaultSkin/icons/80x80/DefaultPortlet.png";
			}
	    itemDetails += '<div class="Application" style="'+cssFloat+';">' +
			               '		<div class="ApplicationDescription">' +
			               '			<div class="PortletIcon" title="'+portlet["title"]+'"' +
			               '        style="background: url(\''+srcBG+'\')no-repeat center;"' +
			               '        onclick="eXo.desktop.UIAddApplication.addPortlet(\''+id+'\',\'true\')">' +
			               '        <span></span></div>' +
			               '		  <div style="float: right;">' +
				             ' 			  <div class="SelectButton" onclick="eXo.desktop.UIAddApplication.addPortlet(\''+id+'\',\'false\')" ><span></span></div>' +
				             ' 			  <div class="AddButton" onclick="eXo.desktop.UIAddApplication.addPortlet(\''+id+'\',\'true\')"' +
				             '          title="Add this application to the desktop page">' +
						         ' 			    <span></span>' +
						      	 '		    </div>' +
					      		 '	    </div>' +
					      		 '	    <div style="clear: both"><span></span></div>' +
					      		 '	  </div>' +
			               '		<div class="TitleBarApplication">' +
			               '			<div class="ApplicationLabel">'+portlet["title"]+'</div>' +
			               '		</div>' +
		              	 '</div>';
	  }
    itemDetails += '  </div>' +
									 '</div>';  
		if(!selected) selected = true;							 
  }
  itemList.innerHTML = items;  
  itemDetailList.innerHTML = itemDetails;
}

UIAddApplication.prototype.addPortlet = function(id, save) {
	var params = [
		{name: "portletId", value : id},
		{name: "save", value : save}
	] ;
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