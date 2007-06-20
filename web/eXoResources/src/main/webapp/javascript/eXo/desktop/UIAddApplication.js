eXo.require('eXo.core.TemplateEngine');
eXo.require('eXo.webui.UIHorizontalTabs');
eXo.require('eXo.core.CacheJSonService');

function UIAddApplication() {};

UIAddApplication.prototype.init = function(parentId, containerId, applicationTypes) {
	var DOMUtil = eXo.core.DOMUtil ;
	var container = document.getElementById(containerId);
	var context = new Object();
	
	context.uiMaskWorkspace = { width: "700px" }
	
	if(document.getElementById("UIMaskWorkspaceJSTemplate") == null) {
		var uiAddAppContainer = document.createElement('div') ;
		uiAddAppContainer.id = "UIAddApplicationContainer" ;
		uiAddAppContainer.style.display = "none" ;
		
		context.uiMaskWorkspace.content = eXo.core.TemplateEngine.merge('eXo/desktop/UIAddApplication.jstmpl', context) ;
		uiAddAppContainer.innerHTML = eXo.core.TemplateEngine.merge("eXo/portal/UIMaskWorkspace.jstmpl", context) ;
		
		container.appendChild(uiAddAppContainer) ;
	}
	var uiAddApplicationContainer = document.getElementById("UIAddApplicationContainer");
	eXo.desktop.UIAddApplication.showAddApplication(uiAddApplicationContainer);
	this.loadApplications(true, applicationTypes, parentId);
};

/**Created: by Duy Tu**/
function getUrl(src) {
	var img = document.createElement('img');	
	img.src = src;
	return(img.src);
};

UIAddApplication.prototype.loadApplications = function(refresh, applicationTypes, parentId) {
	var uiAddApplicationContainer = document.getElementById("UIAddApplicationContainer");
	var url = eXo.env.server.context + "/command?";
	url += "type=org.exoplatform.web.command.handler.GetApplicationHandler";
	for(var i = 0; i < applicationTypes.length; i++) {
		url += "&applicationType="+applicationTypes[i];
	}
	if(refresh == null || refresh == undefined) refresh = false;
  var category = eXo.core.CacheJSonService.getData(url, refresh);
  if(category == null || category == undefined) return;
  var itemList = eXo.core.DOMUtil.findFirstDescendantByClass(uiAddApplicationContainer, "div", "ItemList") ;
  var itemDetailList = eXo.core.DOMUtil.findFirstDescendantByClass(uiAddApplicationContainer, "div", "ItemDetailList") ;
  var items  = '';
  var itemDetails = '';
  var checkSrc = ''; 
  var selected  = false;
  /**Repaired: by Vu Duy Tu **/ 
  itemDetails += '<div class="ItemDetailTitle">' +
        	    	 '	<div class="TitleIcon ViewListIcon"><span></span></div>' +
				  	     '	<div class="Title">Select Application</div>' +
				  	     '	<div style="clear: left;"><span></span></div>' +
        	       '</div>' +
        	       '<div class="ApplicationListContainer">';
  for(id in category.applicationRegistry) {  	
		var cate = category.applicationRegistry[id];
		var applications = cate["applications"];
		if(!selected){
      items += '<div class="SelectedItem Item" onclick="eXo.webui.UIItemSelector.onClick(this);"';
		} else {
			items += '<div class="Item" onclick="eXo.webui.UIItemSelector.onClick(this);"';
		}
	  items += 'onmouseover="eXo.webui.UIItemSelector.onOver(this, true);" onmouseout="eXo.webui.UIItemSelector.onOver(this, false);">' +
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
	  for(id in applications) {
	  	application = applications[id];  	 
	  	var created = application["owner"];
	  	if(created == "undefined" || created == null) created = "eXo Platform SAS."
      var srcBG = "/eXoResources/skin/portal/webui/component/view/UIPageDesktop/DefaultSkin/icons/80x80/" + application["title"]+".png";
      var srcNormalBG = "/eXoResources/skin/portal/webui/component/view/UIPageDesktop/DefaultSkin/icons/80x80/DefaultPortlet.png";
			srcBG = getUrl(srcBG);
			srcNormalBG = getUrl(srcNormalBG);
	    itemDetails += '<div class="Application">' +
				             '  <div class="ApplicationDescription">' +
				             '		<div class="PortletIcon" title="'+application["title"]+'"' +
			               '    	onclick="eXo.desktop.UIAddApplication.addApplication(\''+parentId+'\',\''+id+'\',\'true\');">' +
			               '    	<span>' +
			               '      	<img src="'+srcBG+'" onError="src=\''+srcNormalBG+'\'">' +
			               '      </span>' +
			               '    </div>' +
			               '	  <div class="ApplicationContent">' +
			               '	    <div class="TitleBarApplication">' +
			               '				<div class="Title">'+application["title"]+'</div>' +
			               '  			<div class="ApplicationButton">' +
				             ' 	  			<div class="SelectButton" onclick="eXo.desktop.UIAddApplication.addApplication(\''+parentId+'\',\''+id+'\',\'false\');"><span></span></div>' +
				             ' 					<div class="AddButton" onclick="eXo.desktop.UIAddApplication.addApplication(\''+parentId+'\',\''+id+'\',\'true\');"' +
				             '     			  title="Add this application to the desktop page">' +
						         ' 					  <span></span>' +
						      	 '					</div>' +
						      	 '					<div style="clear: right;"><span></span></div>' +				
						      	 '				</div>' +
			               '			</div>' +
			               '      <div class="ApplicationContentLabel">' +
			               '        <div class="ContentLabel">' +
			               '          <span class="LeftLabel">Type:</span>' +
			               '	        <span class="RightLabel">'+application["type"]+'</span>' +
			               '	      </div>' +
			               '	      <div class="ContentLabel">' +
			               '	        <span class="LeftLabel">Created by:</span>' +
			               '	        <span class="RightLabel">'+created+'</span>' +
			               '	      </div>' +
			               '	      <div class="ContentLabel">' +
                     '          <span class="LeftLabel">Description:</span>' +
                     '          <span class="RightLabel">'+application["title"]+' Description</span>' +
                     '        </div>' +
			               '	    </div>' +
			               '	  </div>' +
						      	 '	  <div style="clear: left"><span></span></div>' +
						      	 '	</div>' +
						      	 '	<div style="clear: right;"><span></span></div>' +
						      	 '</div>';
	  }
    itemDetails += '</div>';
		if(!selected) selected = true;
  }
  itemDetails += '</div>';
  itemList.innerHTML = items;
  itemDetailList.innerHTML = itemDetails;
};

UIAddApplication.prototype.addApplication = function(parentId, id, save) {
	var params = [
		{name: "applicationId", value : id},
		{name: "save", value : save},
	] ;
	
	ajaxGet(eXo.env.server.createPortalURL(parentId, "AddApplication", true, params)) ;
	eXo.widget.UIWidget.resizeContainer();
};

UIAddApplication.prototype.showAddApplication = function(object) {
	eXo.core.UIMaskLayer.createMask("UIPortalApplication", object, 30) ;
	var uiPageDesktop = document.getElementById("UIPageDesktop");
	if(uiPageDesktop) eXo.desktop.UIDockbar.reset() ;
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