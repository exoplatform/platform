eXo.require('eXo.core.TemplateEngine');
eXo.require('eXo.core.CacheJSonService');

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
	var url = eXo.env.server.context + "/command?";
	url += "type=org.exoplatform.web.command.handler.GetWidgetContainerHandler" ;
	if(refresh == null || refresh == undefined) refresh = false;
	var containers = eXo.core.CacheJSonService.getData(url, refresh);
	if(containers == null || containers == undefined) return ;
	eXo.widget.UIWidgetContainerManagement.renderContainer(containers.widgetContainers) ;
	eXo.widget.UIWidgetContainerManagement.bindContainerToDetail() ;
} ;

UIWidgetContainerManagement.prototype.renderContainer = function(containers) {
	var DOMUtil = eXo.core.DOMUtil ;
	var uiWidgetContainerManagement = document.getElementById("UIWidgetContainerManagement");
	var containerList = DOMUtil.findFirstDescendantByClass(uiWidgetContainerManagement, "div", "ContainerList");
	var htmlItem = "" ;
	
	if(containers == null || containers.length < 1) {
		var htmlItem = "<div class=\"EmptyListMessage\">There is no container.</div>" ;
	} else {
		var cssClass ;
		if(eXo.widget.UIWidgetContainerManagement.selectedContainer == null ) eXo.widget.UIWidgetContainerManagement.selectedContainer = containers[0] ;
		var selectedContainer = eXo.widget.UIWidgetContainerManagement.selectedContainer ;
		for(var i =0; i < containers.length; i++) {
			if(containers[i].cName == "" || containers[i].cName == "null") containers[i].cName = containers[i].cId ;
			if(containers[i].cDescription == "null") containers[i].cDescription = "" ;
			if(containers[i].cId  == selectedContainer.cId) {
				cssClass = "SelectedItem" ;
			} else cssClass = "NormalItem" ;
			
			htmlItem +=	'<div class="Item ' + cssClass + '" onclick="eXo.widget.UIWidgetContainerManagement.changeContainer(this);">'
							 +    '<input type="hidden" value="' + containers[i].cId + '" name="id"/>'
							 +    '<input type="hidden" value="' + containers[i].cName + '" name="name"/>'
							 +    '<input type="hidden" value="' + containers[i].cDescription + '" name="description"/>'
							 +    '<div class="Label">' + containers[i].cName + '</div>' 
				  		 +  '</div>';	
		}	
	}
	containerList.innerHTML = htmlItem ;
} ;

UIWidgetContainerManagement.prototype.changeContainer = function(selectedElement) {
	if(eXo.widget.UIWidgetContainerManagement.isPopupExist()) return ;
	eXo.widget.UIWidgetContainerManagement.selectContainer(selectedElement) ;
} ;

UIWidgetContainerManagement.prototype.selectContainer = function(selectedElement) {
	eXo.widget.UIWidgetContainerManagement.uncheckAll() ;
	selectedElement.className = "Item SelectedItem" ;
	var container = eXo.widget.UIWidgetContainerManagement.bindElementToContainer(selectedElement) ;
	eXo.widget.UIWidgetContainerManagement.selectedContainer = container ;
	eXo.widget.UIWidgetContainerManagement.bindContainerToDetail() ;
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
			height : "250px",
			closeAction : "eXo.widget.UIWidgetContainerManagement.closePopup('UIWidgetContainerPopup');"
		}
	}
	
	var formCtx = {
		id : "UIWidgetContainerForm",
		container : {
			cId : "",
			cName : "",
			cDescription : ""
		},
		action : [
		{
			actionLabel : "Add",
			event : "eXo.widget.UIWidgetContainerManagement.addWidgetContainer();"
		},
		{
			actionLabel : "Cancel",
			event : "eXo.widget.UIWidgetContainerManagement.closePopup('UIWidgetContainerPopup');"
		}		
		]
	}
	eXo.widget.UIWidgetContainerManagement.showPopup(popupCtx, formCtx) ;
};

UIWidgetContainerManagement.prototype.addWidgetContainer = function() {
	var DOMUtil = eXo.core.DOMUtil ;
	var uiWidgetContainerManagement = document.getElementById("UIWidgetContainerManagement");
	var uiForm = DOMUtil.findDescendantById(uiWidgetContainerManagement, "UIWidgetContainerForm");
	var idField = DOMUtil.findDescendantById(uiForm, "id") ;
	var newContainer = 	eXo.widget.UIWidgetContainerManagement.bindFormToContainer(uiForm) ;
	var containerList = DOMUtil.findFirstDescendantByClass(uiWidgetContainerManagement, "div", "ContainerList");
	if(idField.value == '' || idField.value == null) {
		alert("The field 'Container Id' is required");
		idField.focus();
		return ;
	}
	
	var containerElements = DOMUtil.findChildrenByClass(containerList, "div", "Item") ;
	var containers = [] ;
	for(var i = 0; i < containerElements.length; i++) {
		var container = eXo.widget.UIWidgetContainerManagement.bindElementToContainer(containerElements[i]) ;
		if(container.cId == idField.value) {
			alert("This id is existing, please enter another one!") ;
			idField.focus() ;
			return ;
		}
		containers.push(container) ;
	}
	containers.push(newContainer) ;
	eXo.widget.UIWidgetContainerManagement.uncheckAll() ;
	eXo.widget.UIWidgetContainerManagement.selectedContainer = newContainer ;
	eXo.widget.UIWidgetContainerManagement.renderContainer(containers) ;
	eXo.widget.UIWidgetContainerManagement.bindContainerToDetail() ;
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
			height : "250px",
			closeAction : "eXo.widget.UIWidgetContainerManagement.closePopup('UIWidgetContainerPopup');"
		}
	}
	var formCtx = {
		id : "UIWidgetContainerForm",
		isEdit : true,
		container : {
			cId : container.cId,
			cName : container.cName,
			cDescription : container.cDescription
		},
		action : [
		{
			actionLabel : "Save",
			event : "eXo.widget.UIWidgetContainerManagement.editContainer();"
		},
		{
			actionLabel : "Cancel",
			event : "eXo.widget.UIWidgetContainerManagement.closePopup('UIWidgetContainerPopup');"
		}		
		]
	}
	eXo.widget.UIWidgetContainerManagement.showPopup(popupCtx, formCtx) ;
} ;

UIWidgetContainerManagement.prototype.editContainer = function() {
	var DOMUtil = eXo.core.DOMUtil ;
	var uiWidgetContainerManagement = document.getElementById("UIWidgetContainerManagement") ;
	var uiForm = DOMUtil.findDescendantById(uiWidgetContainerManagement, "UIWidgetContainerForm") ;
	var editedContainer = eXo.widget.UIWidgetContainerManagement.bindFormToContainer(uiForm) ;
	if(editedContainer.cName == null || editedContainer.cName == "") editedContainer.cName = editedContainer.cId ;
	var containerList = DOMUtil.findFirstDescendantByClass(uiWidgetContainerManagement, "div", "ContainerList") ;
	var containers = DOMUtil.findChildrenByClass(containerList, "div", "Item") ;
	
	for(var i = 0; i < containers.length; i++) {
		var container = eXo.widget.UIWidgetContainerManagement.bindElementToContainer(containers[i]) ;
		if(container.cId == editedContainer.cId) {
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
		if(container.cId != selectedContainer.cId) {
			remainContainers.push(container) ;
			continue ;
		}
		if(!eXo.widget.UIWidgetContainerManagement.deletedContainers.contains(container.cId)) {
			eXo.widget.UIWidgetContainerManagement.deletedContainers.push(container.cId) ;
		}
	}
	
	eXo.widget.UIWidgetContainerManagement.selectedContainer = null ;
	eXo.widget.UIWidgetContainerManagement.renderContainer(remainContainers) ;
	eXo.widget.UIWidgetContainerManagement.bindContainerToDetail() ;
};

UIWidgetContainerManagement.prototype.submit = function() {
	var DOMUtil = eXo.core.DOMUtil ;
	var uiWidgetContainerManagement = document.getElementById("UIWidgetContainerManagement") ;
	var portalWidgetContainer = DOMUtil.findFirstDescendantByClass(uiWidgetContainerManagement, "div", "PortalWidgetContainer");
	var containerList = DOMUtil.findFirstDescendantByClass(uiWidgetContainerManagement, "div", "ContainerList");
	var params = [];
	
	var containers = DOMUtil.findChildrenByClass(containerList, "div", "Item") ;
	var updatedParams = "" ;
	for(var i = 0; i < containers.length; i++) {
		var container = eXo.widget.UIWidgetContainerManagement.bindElementToContainer(containers[i]) ;
		params.push({name : "id", value : container.cId}) ;
		params.push({name : "name", value : container.cName}) ;
		params.push({name : "desc", value : container.cDescription}) ;
	}
		
	var deletedParams = "";
	for(var j = 0; j < eXo.widget.UIWidgetContainerManagement.deletedContainers.length; j++) {
		var deletedContainer = eXo.widget.UIWidgetContainerManagement.deletedContainers[j] ;
		params.push({name : "deleted", value : deletedContainer}) ;
	}
	ajaxGet(eXo.env.server.createPortalURL("UIWidgetContainerManagement", "Save", true, params)) ;
	eXo.widget.UIWidgetContainerManagement.clear() ;

//	var DOMUtil = eXo.core.DOMUtil ;
//	var uiWidgetContainerManagement = document.getElementById("UIWidgetContainerManagement") ;
//	var portalWidgetContainer = DOMUtil.findFirstDescendantByClass(uiWidgetContainerManagement, "div", "PortalWidgetContainer");
//	var containerList = DOMUtil.findFirstDescendantByClass(uiWidgetContainerManagement, "div", "ContainerList");
//	
//	var containers = DOMUtil.findChildrenByClass(containerList, "div", "Item") ;
//	var updatedParams = "" ;
//	for(var i = 0; i < containers.length; i++) {
//		var container = eXo.widget.UIWidgetContainerManagement.bindElementToContainer(containers[i]) ;
//		updatedParams += "&id=" + container.cId + "&name=" + container.cName	+ "&desc=" + container.cDescription ;
//	}
//		
//	var deletedParams = "";
//	for(var j = 0; j < eXo.widget.UIWidgetContainerManagement.deletedContainers.length; j++) {
//		deletedParams += ("&deleted=" + eXo.widget.UIWidgetContainerManagement.deletedContainers[j]) ;
//	}
//		
//	var url = eXo.env.server.context
//					+ "/command?type=org.exoplatform.web.command.handler.UpdateWidgetContainerHandler"
//					+ updatedParams 
//					+ deletedParams ;	
//					
//	ajaxAsyncGetRequest(url, true) ;		
//
//	eXo.widget.UIWidgetContainerManagement.destroy() ;
} ;

UIWidgetContainerManagement.prototype.cancel = function() {
	eXo.widget.UIWidgetContainerManagement.clear() ;
	ajaxGet(eXo.env.server.createPortalURL("UIWidgetContainerManagement", "Close", true)) ;	
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

UIWidgetContainerManagement.prototype.uncheckAll = function() {
	var DOMUtil = eXo.core.DOMUtil ;
	var uiWidgetContainerManagement = document.getElementById("UIWidgetContainerManagement");
	var containerList = DOMUtil.findFirstDescendantByClass(uiWidgetContainerManagement, "div", "ContainerList");
	var containers = DOMUtil.findChildrenByClass(containerList, "div", "Item");
	
	for(var j = 0; j < containers.length; j++) {
		containers[j].className = "Item NormalItem" ;
	}		
} ;
//---------------------------------------------------//

UIWidgetContainerManagement.prototype.bindContainerToElement = function(element, container) {
	var itemHTML = '<input type="hidden" value="' + container.cId + '" name="id"/>'
							 + '<input type="hidden" value="' + container.cName + '" name="name"/>'
							 + '<input type="hidden" value="' + container.cDescription + '" name="description"/>'
							 + '<div class="Label">' + container.cName + '</div>' ;

	element.innerHTML = itemHTML ;
} ;

UIWidgetContainerManagement.prototype.bindElementToContainer = function(item) {
	var DOMUtil = eXo.core.DOMUtil ;
	var containerObject = new Object();
	
	containerObject.cId = DOMUtil.findChildrenByAttribute(item, "input", "name", "id")[0].value ;
	containerObject.cName = DOMUtil.findChildrenByAttribute(item, "input", "name", "name")[0].value ;
	containerObject.cDescription = DOMUtil.findChildrenByAttribute(item, "input", "name", "description")[0].value ;
	
	return containerObject ;
} ;

UIWidgetContainerManagement.prototype.bindFormToContainer = function(uiForm) {
	var DOMUtil = eXo.core.DOMUtil ;
	var container = new Object() ;
	container.cId = DOMUtil.findDescendantById(uiForm, "id").value ;
	container.cName = DOMUtil.findDescendantById(uiForm, "name").value ;
	container.cDescription = DOMUtil.findDescendantById(uiForm, "description").value ;
	
	return  container ;
} ;

UIWidgetContainerManagement.prototype.bindContainerToDetail = function() {
	var DOMUtil = eXo.core.DOMUtil ;
	var selectedContainer = eXo.widget.UIWidgetContainerManagement.selectedContainer ;
	var uiWidgetContainerManagement = document.getElementById("UIWidgetContainerManagement") ;
	var containerDetail = eXo.core.TemplateEngine.merge("eXo/widget/UIWidgetContainerDetail.jstmpl", selectedContainer) ;
	var widgetContainerDetailNode = DOMUtil.findFirstDescendantByClass(uiWidgetContainerManagement, "div", "WidgetContainerDetail") ;
	widgetContainerDetailNode.innerHTML = containerDetail ;	
} ;

UIWidgetContainerManagement.prototype.isPopupExist = function() {
	return (document.getElementById("UIWidgetContainerPopup") != null) ;
} ;

UIWidgetContainerManagement.prototype.clear = function() {
	this.selectedContainer = null ;
	this.deletedContainers.clear() ;
} ;

eXo.widget.UIWidgetContainerManagement = new UIWidgetContainerManagement();