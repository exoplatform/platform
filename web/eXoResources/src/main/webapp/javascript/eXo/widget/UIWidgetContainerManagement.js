eXo.require('eXo.core.TemplateEngine');

function UIWidgetContainerManagement() {
	this.selectedContainer = null ;
	this.deletedContainers = new Array() ;
};

UIWidgetContainerManagement.prototype.show = function() {
	var DOMUtil = eXo.core.DOMUtil ;
	var context = new Object();
	context.uiMaskWorkspace = {
		width: "700px"
	}
	context.uiMaskWorkspace.content = eXo.core.TemplateEngine.merge("eXo/widget/UIWidgetContainerManagement.jstmpl", context) ;
	var innerHTML = eXo.core.TemplateEngine.merge("eXo/portal/UIMaskWorkspace.jstmpl", context) ;
 	var uiMaskWorkspaceNode = DOMUtil.createElementNode(innerHTML, "div");
	eXo.core.UIMaskLayer.createMask("UIPortalApplication", uiMaskWorkspaceNode, 30) ;
	eXo.widget.UIWidgetContainerManagement.loadWidgetContainer(true);
};

UIWidgetContainerManagement.prototype.destroy = function() {
	var uiWidgetContainerManagement = document.getElementById("UIWidgetContainerManagement");
	var uiMaskWorkspace = eXo.core.DOMUtil.findAncestorByClass(uiWidgetContainerManagement, "UIMaskWorkspace");
	
	var uiMaskLayer = uiMaskWorkspace.previousSibling ;	
	var parentNode = uiMaskWorkspace.parentNode ;
	parentNode.removeChild(uiMaskWorkspace);
	parentNode.removeChild(uiMaskLayer);
	eXo.widget.UIWidgetContainerManagement.clear() ;
};

UIWidgetContainerManagement.prototype.loadWidgetContainer = function(refresh) {
	var DOMUtil = eXo.core.DOMUtil ;
	var uiWidgetContainerManagement = document.getElementById("UIWidgetContainerManagement");
	
	var url = eXo.env.server.context + "/command?";
	url += "type=org.exoplatform.web.command.handler.GetWidgetContainerHandler";
	if(refresh == null || refresh == undefined) refresh = false;
	var containers = eXo.core.CacheJSonService.getData(url, refresh);
	if(containers == null || containers == undefined) return ;
	eXo.widget.UIWidgetContainerManagement.setup(uiWidgetContainerManagement, containers.widgetContainer) ;	
} ;

UIWidgetContainerManagement.prototype.setup = function(uiWidgetContainerManagement, containers) {
	var DOMUtil = eXo.core.DOMUtil ;
	var portalWidgetContainer = DOMUtil.findFirstDescendantByClass(uiWidgetContainerManagement, "div", "PortalWidgetContainer");
	var containerList = DOMUtil.findFirstDescendantByClass(uiWidgetContainerManagement, "div", "ContainerList");
	var htmlItem = "" ;
	
	if(containers == null || containers.length < 1) {
		var htmlItem = "<div class=\"EmptyListMessage\">There is no container in this category.</div>" ;
		eXo.widget.UIWidgetContainerManagement.selectedContainer = null;
	} else {
		var cssClass ;
		if(eXo.widget.UIWidgetContainerManagement.selectedContainer == null ) eXo.widget.UIWidgetContainerManagement.selectedContainer = containers[0] ;
		for(var i =0; i < containers.length; i++) {
			var container = containers[i] ;
			if(container.cName == eXo.widget.UIWidgetContainerManagement.selectedContainer.cName) {
				cssClass = "SelectedItem" ;
			} else cssClass = "NormalItem" ;
			
			htmlItem +=	'<div class="Item ' + cssClass + '" onclick="eXo.widget.UIWidgetContainerManagement.changeContainer(this);">'
							 +    '<input type="hidden" value="' + container.cName + '" name="name"/>'
							 +    '<input type="hidden" value="' + container.cDescription + '" name="description"/>'
							 +    '<div class="Label">' + container.cName + '</div>' 
				  		 +  '</div>';	
		}	
	}
	containerList.innerHTML = htmlItem ;
	eXo.widget.UIWidgetContainerManagement.bindContainerToDetail(eXo.widget.UIWidgetContainerManagement.selectedContainer) ;
} ;

UIWidgetContainerManagement.prototype.changeContainer = function(selectedElement) {
	if(eXo.widget.UIWidgetContainerManagement.isPopupExist()) return ;
	eXo.widget.UIWidgetContainerManagement.selectContainer(selectedElement) ;
} ;

UIWidgetContainerManagement.prototype.selectContainer = function(selectedElement) {
	var DOMUtil = eXo.core.DOMUtil ;
	var uiWidgetContainerManagement = document.getElementById("UIWidgetContainerManagement");
	var containerList = DOMUtil.findAncestorByClass(selectedElement, "ContainerList");
	var containers = DOMUtil.findChildrenByClass(containerList, "div", "Item");
	
	selectedElement.className = "Item SelectedItem" ;
	for(var i = 0; i < containers.length; i++) {
		if(containers[i] != selectedElement) {
			containers[i].className = "Item NormalItem" ;
		}
	}
	var container = eXo.widget.UIWidgetContainerManagement.bindElementToContainer(selectedElement) ;
	eXo.widget.UIWidgetContainerManagement.selectedContainer = container ;
	eXo.widget.UIWidgetContainerManagement.bindContainerToDetail(container) ;
} ;

UIWidgetContainerManagement.prototype.showAddForm = function() {
	if(eXo.widget.UIWidgetContainerManagement.isPopupExist()) return ;
	var DOMUtil = eXo.core.DOMUtil ;
	var popupCtx = new Object();
	popupCtx = {
		popup : {
			title : "Add New Widget Container",
			popupId : "UIWidgetContainerPopup",
			style : "NormalStyle",
			width : "400px",
			height : "180px",
			closeAction : "eXo.widget.UIWidgetContainerManagement.closePopup('UIWidgetContainerPopup');"
		}
	}
	
	var formCtx = {
		id : "UIWidgetContainerForm",
		container : {
			cName : "",
			cDescription : ""
		},
		action : {
			actionLabel : "Add",
			event : "eXo.widget.UIWidgetContainerManagement.addWidgetContainer();"
		}
	}
	eXo.widget.UIWidgetContainerManagement.showPopup(popupCtx, formCtx) ;
};

UIWidgetContainerManagement.prototype.addWidgetContainer = function() {
	var DOMUtil = eXo.core.DOMUtil ;
	var uiWidgetContainerManagement = document.getElementById("UIWidgetContainerManagement");
	var containerList = DOMUtil.findFirstDescendantByClass(uiWidgetContainerManagement, "div", "ContainerList");
	var uiForm = DOMUtil.findDescendantById(uiWidgetContainerManagement, "UIWidgetContainerForm");
	var fieldNameElement = uiForm.getElementsByTagName("input")[0] ;
	var newContainer = 	eXo.widget.UIWidgetContainerManagement.bindFormToContainer(uiForm) ;
	
	if(fieldNameElement.value == '' || fieldNameElement.value == null) {
		alert("The field 'Container Name' is required");
		fieldNameElement.focus();
		return ;
	}
	
	var containerElements = DOMUtil.findChildrenByClass(containerList, "div", "Item") ;
	var containers = [] ;
	for(var i = 0; i < containerElements.length; i++) {
		var container = eXo.widget.UIWidgetContainerManagement.bindElementToContainer(containerElements[i]) ;
		if(container.cName == fieldNameElement.value) {
			alert("This name is existing, please enter another one!") ;
			fieldNameElement.focus() ;
			return ;
		}
		containers.push(container) ;
	}
	containers.push(newContainer) ;
	eXo.widget.UIWidgetContainerManagement.selectedContainer = newContainer ;
	eXo.widget.UIWidgetContainerManagement.setup(uiWidgetContainerManagement, containers) ;
	eXo.widget.UIWidgetContainerManagement.closePopup('UIWidgetContainerPopup');
};

UIWidgetContainerManagement.prototype.showEditForm = function() {
	if(eXo.widget.UIWidgetContainerManagement.isPopupExist()) return ;
	var container = eXo.widget.UIWidgetContainerManagement.selectedContainer ;
	if(container == null) return ;
	var DOMUtil = eXo.core.DOMUtil ;
	var popupCtx = new Object();
	popupCtx = {
		popup : {
			title : "Edit Widget Container",
			popupId : "UIWidgetContainerPopup",
			style : "NormalStyle",
			width : "400px",
			height : "180px",
			closeAction : "eXo.widget.UIWidgetContainerManagement.closePopup('UIWidgetContainerPopup');"
		}
	}
	var formCtx = {
		id : "UIWidgetContainerForm",
		isEdit : true,
		container : {
			cName : container.cName,
			cDescription : container.cDescription
		},
		action : {
			actionLabel : "Save",
			event : "eXo.widget.UIWidgetContainerManagement.editContainer();"
		}
	}
	eXo.widget.UIWidgetContainerManagement.showPopup(popupCtx, formCtx) ;
} ;

UIWidgetContainerManagement.prototype.editContainer = function() {
	var DOMUtil = eXo.core.DOMUtil ;
	var uiWidgetContainerManagement = document.getElementById("UIWidgetContainerManagement") ;
	var containerList = DOMUtil.findFirstDescendantByClass(uiWidgetContainerManagement, "div", "ContainerList") ;
	var containers = DOMUtil.findChildrenByClass(containerList, "div", "Item") ;
	var uiForm = DOMUtil.findDescendantById(uiWidgetContainerManagement, "UIWidgetContainerForm") ;
	var editedContainer = eXo.widget.UIWidgetContainerManagement.bindFormToContainer(uiForm) ;
	
	for(var i = 0; i < containers.length; i++) {
		var container = eXo.widget.UIWidgetContainerManagement.bindElementToContainer(containers[i]) ;
		if(container.cName == editedContainer.cName) {
			eXo.widget.UIWidgetContainerManagement.bindContainerToElement(containers[i], editedContainer) ;
			eXo.widget.UIWidgetContainerManagement.selectContainer(containers[i]) ;
			break ;
		}
	}
	eXo.widget.UIWidgetContainerManagement.closePopup('UIWidgetContainerPopup');
} ;

UIWidgetContainerManagement.prototype.deleteContainer = function() {
	if(eXo.widget.UIWidgetContainerManagement.isPopupExist()) return ;
	var selectedContainer = eXo.widget.UIWidgetContainerManagement.selectedContainer ;
	if(selectedContainer == null) return ;
	if(!confirm("Are you sure you want to delete this container?")) return;
	var DOMUtil = eXo.core.DOMUtil ;
	var uiWidgetContainerManagement = document.getElementById("UIWidgetContainerManagement") ;
	var containerList = DOMUtil.findFirstDescendantByClass(uiWidgetContainerManagement, "div", "ContainerList") ;
	var containerElements = DOMUtil.findChildrenByClass(containerList, "div", "Item") ;
	var remainContainers = [] ;

	for(var i = 0; i < containerElements.length; i++) {
		var container = eXo.widget.UIWidgetContainerManagement.bindElementToContainer(containerElements[i]) ;
		if(container.cName != selectedContainer.cName) {
			remainContainers.push(container) ;
			continue ;
		}
		if(!eXo.widget.UIWidgetContainerManagement.deletedContainers.contains(container.cName)) {
			eXo.widget.UIWidgetContainerManagement.deletedContainers.push(container.cName) ;
		}
	}
	eXo.widget.UIWidgetContainerManagement.selectedContainer = null ;
	eXo.widget.UIWidgetContainerManagement.setup(uiWidgetContainerManagement, remainContainers) ;
};

UIWidgetContainerManagement.prototype.submit = function() {
	var DOMUtil = eXo.core.DOMUtil ;
	var uiWidgetContainerManagement = document.getElementById("UIWidgetContainerManagement") ;
	var portalWidgetContainer = DOMUtil.findFirstDescendantByClass(uiWidgetContainerManagement, "div", "PortalWidgetContainer");
	var containerList = DOMUtil.findFirstDescendantByClass(uiWidgetContainerManagement, "div", "ContainerList");
	var containers = DOMUtil.findChildrenByClass(containerList, "div", "Item") ;

	var params = "" ;
	for(var i = 0; i < containers.length; i++) {
		var container = eXo.widget.UIWidgetContainerManagement.bindElementToContainer(containers[i]) ;
		params += "&name=" + container.cName	+ "&desc=" + container.cDescription ;
	}
	
	var deletedParams = "";
	for(var j = 0; j < eXo.widget.UIWidgetContainerManagement.deletedContainers.length; j++) {
		deletedParams += ("&deleted=" + eXo.widget.UIWidgetContainerManagement.deletedContainers[j]) ;
	}
	
	var url = eXo.env.server.context
					+ "/command?type=org.exoplatform.web.command.handler.UpdateWidgetContainerHandler"
					+ params 
					+ deletedParams ;	
	ajaxAsyncGetRequest(url, true) ;
	eXo.widget.UIWidgetContainerManagement.destroy() ;
} ;

UIWidgetContainerManagement.prototype.showPopup = function(popupCtx, formCtx) {
	var DOMUtil = eXo.core.DOMUtil ;
	popupCtx.popup.content = eXo.core.TemplateEngine.merge("eXo/widget/UIWidgetContainerForm.jstmpl", formCtx);
	var uiWidgetContainerManagement = document.getElementById("UIWidgetContainerManagement");	
	var uiPopupWindowTemplate = eXo.core.TemplateEngine.merge('eXo/webui/UIPopupWindow.jstmpl', popupCtx);
 	var uiPopupWindowNode = DOMUtil.createElementNode(uiPopupWindowTemplate, "div");
 	uiPopupWindowNode.style.width = popupCtx.popup.width;
 	uiWidgetContainerManagement.appendChild(uiPopupWindowNode) ;
 	eXo.webui.UIPopupWindow.init(popupCtx.popup.popupId, false); 	
	eXo.webui.UIPopupWindow.show(popupCtx.popup.popupId);
} ;

UIWidgetContainerManagement.prototype.closePopup = function(popupId) {
	var popup = document.getElementById(popupId);
	var parentPopup = popup.parentNode ;
	parentPopup.removeChild(popup);
};

//---------------------------------------------------//

UIWidgetContainerManagement.prototype.bindContainerToElement = function(element, container) {
	var itemHTML = '<input type="hidden" value="' + container.cName + '" name="name"/>'
							 + '<input type="hidden" value="' + container.cDescription + '" name="description"/>'
							 + '<div class="Label">' + container.cName + '</div>' ;

	element.innerHTML = itemHTML ;
} ;

UIWidgetContainerManagement.prototype.bindElementToContainer = function(item) {
	var DOMUtil = eXo.core.DOMUtil ;
	var containerObject = new Object();
	var containerName = DOMUtil.findChildrenByAttribute(item, "input", "name", "name")[0] ;
	var containerDescription = DOMUtil.findChildrenByAttribute(item, "input", "name", "description")[0] ;
	containerObject.cName = containerName.value ;
	containerObject.cDescription = containerDescription.value ;

	return containerObject ;
} ;

UIWidgetContainerManagement.prototype.bindFormToContainer = function(uiForm) {
	var container = new Object() ;
	container.cName = uiForm.getElementsByTagName("input")[0].value ;
	container.cDescription = uiForm.getElementsByTagName("textarea")[0].value ;
	
	return  container ;
} ;

UIWidgetContainerManagement.prototype.bindContainerToDetail = function(container) {
	var DOMUtil = eXo.core.DOMUtil ;
	var uiWidgetContainerManagement = document.getElementById("UIWidgetContainerManagement") ;
	var containerDetail = eXo.core.TemplateEngine.merge("eXo/widget/UIWidgetContainerDetail.jstmpl", container) ;
	var widgetContainerDetailNode = DOMUtil.findFirstDescendantByClass(uiWidgetContainerManagement, "div", "WidgetContainerDetail") ;
	widgetContainerDetailNode.innerHTML = containerDetail ;	
} ;

UIWidgetContainerManagement.prototype.isPopupExist = function() {
	return (document.getElementById("UIWidgetContainerPopup") != null) ;
} ;

UIWidgetContainerManagement.prototype.clear = function() {
	this.selectedselectedContainer = null ;
	this.deletedContainers.clear() ;
} ;

eXo.widget.UIWidgetContainerManagement = new UIWidgetContainerManagement();