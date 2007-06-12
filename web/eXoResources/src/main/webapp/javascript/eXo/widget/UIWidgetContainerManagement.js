eXo.require('eXo.core.TemplateEngine');

function UIWidgetContainerManagement() {
	
}

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
};

UIWidgetContainerManagement.prototype.loadWidgetContainer = function(refresh) {
	var DOMUtil = eXo.core.DOMUtil ;
	
	var uiWidgetContainerManagement = document.getElementById("UIWidgetContainerManagement");
	
	var url = eXo.env.server.context + "/command?";
	url += "type=org.exoplatform.portal.application.handler.GetWidgetContainerHandler";
	
	if(refresh == null || refresh == undefined) refresh = false;
		
	var containers = eXo.core.CacheJSonService.getData(url, refresh);
	
	if(containers == null || containers == undefined) return;
	
	var portalWidgetContainer = DOMUtil.findFirstDescendantByClass(uiWidgetContainerManagement, "div", "PortalWidgetContainer");
	var containerList = DOMUtil.findFirstDescendantByClass(uiWidgetContainerManagement, "div", "ContainerList");
	
	var itemList = '' ;
	var formContainer = '' ;
	var isFirst = true ;
	var display = '' ;
	var containerClassName = '' ;
	
	var formDescriptor = {
		display : ''
	}
	
	for(container in containers.widgetContainer) {
		var containerObject = containers.widgetContainer[container] ;
		
		if(isFirst) { 
			formDescriptor.display = 'block' ;
			containerClassName = 'SelectedItem' ;
			isFirst = false ;
		} else {
			formDescriptor.display = 'none' ;
			containerClassName = 'NormalItem' ;
		}
		
		formDescriptor.cName = containerObject["name"] ;
		formDescriptor.cDescription = containerObject["description"] ;
				
		itemList += '<div class="'+containerClassName+'"  onclick="eXo.widget.UIWidgetContainerManagement.selectContainer(this);">'+containerObject["name"]+'</div>' ;
										 
		formContainer += eXo.core.TemplateEngine.merge("eXo/widget/UIWidgetContainerForm.jstmpl", formDescriptor) ;
	}
	
	containerList.innerHTML = itemList ;
	
	var widgetContainerForm = DOMUtil.findFirstDescendantByClass(uiWidgetContainerManagement, "div", "WidgetContainerForm");
	widgetContainerForm.innerHTML = formContainer ;
};

UIWidgetContainerManagement.prototype.selectContainer = function(selectedElement) {
	var DOMUtil = eXo.core.DOMUtil ;
	var uiWidgetContainerManagement = document.getElementById("UIWidgetContainerManagement");
	var containerList = DOMUtil.findAncestorByClass(selectedElement, "ContainerList");
	var containers = DOMUtil.getChildrenByTagName(containerList, "div");
	var widgetContainerForm = DOMUtil.findFirstDescendantByClass(uiWidgetContainerManagement, "div", "WidgetContainerForm");
	var childContainerForms = DOMUtil.getChildrenByTagName(widgetContainerForm, "div");
	
	selectedElement.className = "SelectedItem" ;
	for(var i = 0; i < containers.length; i++) {
		if(containers[i] != selectedElement) {
			containers[i].className = "NormalItem" ;
			childContainerForms[i].style.display = "none" ;
		} else {
			childContainerForms[i].style.display = "block" ;
		}
	}
};

UIWidgetContainerManagement.prototype.showAddPopup = function() {
	var DOMUtil = eXo.core.DOMUtil ;
	var context = new Object();
	context = {
		popup : {
			title : "Add New Widget Container",
			popupId : "UIAddWidgetContainerForm",
			width : "400px",
			height : "180px",
			closeAction : "eXo.widget.UIWidgetContainerManagement.closeAddPopup('UIAddWidgetContainerForm');"
		}
	}
	context.popup.content = eXo.core.TemplateEngine.merge("eXo/widget/UIAddWidgetContainerForm.jstmpl");
	
	var uiWidgetContainerManagement = document.getElementById("UIWidgetContainerManagement");
	
	var uiPopupWindowTemplate = eXo.core.TemplateEngine.merge('eXo/webui/UIPopupWindow.jstmpl', context);
 	var uiPopupWindowNode = DOMUtil.createElementNode(uiPopupWindowTemplate, "div");
 	uiPopupWindowNode.style.width = context.popup.width;
 	uiWidgetContainerManagement.appendChild(uiPopupWindowNode);
 	eXo.webui.UIPopupWindow.init(context.popup.popupId, false);
 	
	eXo.webui.UIPopupWindow.show(context.popup.popupId);
	
//	var params = [
//  	{name: "objectId", value : "Test"}
//  ] ;
//	ajaxGet(eXo.env.server.createPortalURL("UIWidgets", "AddWidgetContainer", true, params)) ;
};

UIWidgetContainerManagement.prototype.addWidgetContainer = function(selectedElement) {
	var DOMUtil = eXo.core.DOMUtil ;
	var uiForm = DOMUtil.findAncestorByClass(selectedElement, "UIForm");
	var fieldNameElement = uiForm.getElementsByTagName("input")[0] ;
	var textareaElement = uiForm.getElementsByTagName("textarea")[0] ;
	
	if(fieldNameElement.value == '' || fieldNameElement.value == null) {
		alert("The field 'Container Name' is required");
		fieldNameElement.focus();
		return ;
	}
		
	var uiWidgetContainerManagement = document.getElementById("UIWidgetContainerManagement");
	
	/*Insert To Container Name List*/
	var containerList = DOMUtil.findFirstDescendantByClass(uiWidgetContainerManagement, "div", "ContainerList");	
	var newContainer = '<div class="NormalItem" onclick="eXo.widget.UIWidgetContainerManagement.selectContainer(this);">'+fieldNameElement.value+'</div>'
	var newContainerElement = DOMUtil.createElementNode(newContainer, "div");
	
	var firstChildContainer = DOMUtil.getChildrenByTagName(containerList, "div")[0];
	if(firstChildContainer) {
		containerList.insertBefore(newContainerElement, firstChildContainer);
	} else {
		containerList.appendChild(newContainerElement);
	}
	
	/*Insert To Container Form List*/
	var widgetContainerForm = DOMUtil.findFirstDescendantByClass(uiWidgetContainerManagement, "div", "WidgetContainerForm");
	var containerFormDescriptor = {
		display : 'none',
		cName : fieldNameElement.value,
		cDescription : textareaElement.value
	}
	
	var containerFormHTML = eXo.core.TemplateEngine.merge("eXo/widget/UIWidgetContainerForm.jstmpl", containerFormDescriptor) ;
	var containerFormElement = DOMUtil.createElementNode(containerFormHTML, "div");
	
	var firstContainerForm = DOMUtil.findFirstChildByClass(widgetContainerForm, "div", "ContainerDetail");
	if(firstContainerForm) {
		widgetContainerForm.insertBefore(containerFormElement, firstContainerForm);
	} else {
		widgetContainerForm.appendChild(containerFormElement);
	}
	
	/*Close Popup*/
	eXo.widget.UIWidgetContainerManagement.closeAddPopup('UIAddWidgetContainerForm');
};

UIWidgetContainerManagement.prototype.deleteContainer = function(selectedElement) {
	var DOMUtil = eXo.core.DOMUtil ;
	var containerForm = DOMUtil.findAncestorByClass(selectedElement, "ContainerDetail");
	
	
//	alert(containerForm.className);
};

UIWidgetContainerManagement.prototype.closeAddPopup = function(popupId) {
	var popup = document.getElementById(popupId);
	var parentPopup = popup.parentNode ;
	parentPopup.removeChild(popup);
};

eXo.widget.UIWidgetContainerManagement = new UIWidgetContainerManagement();