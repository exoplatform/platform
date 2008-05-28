eXo.gadget.UIGadget = {
	
	limitX : 50,
	
	createGadget : function(url,id) {
	//eXo = eXo || {};
	eXo.gadgets = eXo.gadgets || {};
	//window.gadgets = eXo.gadget.Gadgets;
	
	if (!eXo.gadgets || !eXo.gadgets.rpc) {
		eXo.loadJS("/eXoGadgetServer/gadgets/js/rpc.js?c=1&debug=1&p=1");
	}
	eXo.require("eXo.gadgets.Gadgets", "/eXoGadgets/javascript/");
	eXo.require("eXo.gadgets.CookieBasedUserPrefStore", "/eXoGadgets/javascript/");
	window.gadgets = eXo.gadgets.Gadgets;
	var gadget = eXo.gadgets.Gadgets.container.createGadget({specUrl: url});
	
  eXo.gadgets.Gadgets.container.addGadget(gadget);
  var gadgetBlock = document.getElementById(id);
	gadgetBlock.innerHTML = "<div id='gadget_" + gadget.id + "'> </div>";
	eXo.gadgets.Gadgets.container.renderGadgets();
	
	var uiGadget = eXo.core.DOMUtil.findAncestorByClass(gadgetBlock, "UIGadget");
	var isDesktop = false;
	if(eXo.core.DOMUtil.findAncestorByClass(uiGadget, "UIPageDesktop"))	isDesktop = true;
	eXo.gadget.UIGadget.init(uiGadget, isDesktop);
	},
	
	init : function(uiGadget, inDesktop) {
	
		uiGadget.onmouseover = eXo.gadget.UIGadget.showGadgetControl ;
		uiGadget.onmouseout = eXo.gadget.UIGadget.hideGadgetControl ;
		
		if(inDesktop) {
			var dragHandleArea = eXo.core.DOMUtil.findFirstDescendantByClass(uiGadget, "div", "WidgetDragHandleArea");

			var posX = eXo.core.DOMUtil.getStyle(uiGadget, "left");
			var posY = eXo.core.DOMUtil.getStyle(uiGadget, "top");
			if(posX < 0) posX = 0 ;
			if(posY < 0) posY = 0 ;
			
			uiGadget.style.left = posX + "px" ;
			uiGadget.style.top = posY + "px" ;
			uiGadget.style.position = "absolute";
			eXo.core.DragDrop2.init(dragHandleArea, uiGadget);
		}
		
		uiGadget.onDragStart = function(x, y, lastMouseX, lastMouseY, e){
			var limitX = eXo.gadget.UIGadget.limitX;
			var dragPosX = uiGadget.offsetLeft;
			var dragPosY = uiGadget.offsetTop;
			
		}
		
		uiGadget.onDrag = function(nx, ny, ex, ey, e){
			
		}
		
		uiGadget.onDragEnd = function(x, y, clientX, clientY){
			
		}
		
	},
	
	showGadgetControl : function(e) {
		if (!e) e = window.event ;
	  e.cancelBubble = true ;
		var DOMUtil = eXo.core.DOMUtil;
		var uiGadget = this ;
		var gadgetControl = DOMUtil.findFirstDescendantByClass(uiGadget, "div", "WidgetControl");
		gadgetControl.style.display = "block" ;
	
		var uiPageDesktop = DOMUtil.findAncestorByClass(uiGadget, "UIPageDesktop");
		if(uiPageDesktop) {
			var dragHandleArea = DOMUtil.findFirstDescendantByClass(gadgetControl, "div", "WidgetDragHandleArea");
			dragHandleArea.title = "Drag this Widget";
		}
	},
	
	
	hideGadgetControl : function(e) {
		if (!e) e = window.event ;
	  e.cancelBubble = true ;
		var uiGadget = this ;
		var gadgetControl = eXo.core.DOMUtil.findFirstDescendantByClass(uiGadget, "div", "WidgetControl");
		gadgetControl.style.display = "none" ;
	},
	
	editGadget : function(selectedElement) {
		var DOMUtil = eXo.core.DOMUtil ;
		var uiGadget = DOMUtil.findAncestorByClass(selectedElement,"UIWidget") ;
		if (uiGadget && uiGadget.applicationDescriptor.application.editGadget) {
			uiGadget.applicationDescriptor.application.editGadget(uiGadget);
		}	else {
			var editMode = DOMUtil.findFirstDescendantByClass(uiGadget, "div", "EditMode") ;
			if (editMode) {		
				viewMode = DOMUtil.findNextElementByTagName(editMode, "div") ;
				if (editMode.style.display == "none") {
					editMode.style.position = "absolute" ;
					editMode.style.display = "block" ;
					editMode.style.left = viewMode.offsetWidth + "px" ;			
				} else {
					editMode.style.display = "none" ;
				}	
				
			}
		}
	}, 

	deleteGadget : function(selectedElement) {
		var DOMUtil = eXo.core.DOMUtil ;
		var uiGadgetContainer = DOMUtil.findAncestorByClass(selectedElement, "UIWidgetContainer") ;
		var uiPage = DOMUtil.findAncestorByClass(selectedElement, "UIPage") ;
		var uiGadget = DOMUtil.findAncestorByClass(selectedElement, "UIGadget") ;
		var containerBlockId ;
		var isInControlWorkspace = false ;
		if(uiPage) {
			var uiPageIdNode = DOMUtil.findFirstDescendantByClass(uiPage, "div", "id");
			containerBlockId = uiPageIdNode.innerHTML;
		}
		else {
			containerBlockId = uiGadgetContainer.id ;
			isInControlWorkspace = true ;
		}
		var gadgetApp = DOMUtil.findFirstDescendantByClass(uiGadget, "div", "WidgetApplication");
		var gadgetId = DOMUtil.getChildrenByTagName(gadgetApp,"div")[0].id;
		gadgetId = gadgetId.substring(gadgetId.lastIndexOf("/")+1, gadgetId.length);
		var params = [
	  	{name: "objectId", value : gadgetId}
	  ] ;
		if (confirm("Are you sure you want to delete this gadget ?")) {
			var result = ajaxAsyncGetRequest(eXo.env.server.createPortalURL(containerBlockId, "DeleteGadget", true, params), false) ;
			if(result == "OK") { 
				DOMUtil.removeElement(uiGadget) ;
				if(isInControlWorkspace) eXo.webui.UIVerticalScroller.refreshScroll(0) ;
			}
		}	
	},
	
	saveWindowProperties : function(object) {
		var DOMUtil = eXo.core.DOMUtil ;
		var uiPage = DOMUtil.findAncestorByClass(object, "UIPage") ;
  	var uiPageIdNode = DOMUtil.findFirstDescendantByClass(uiPage, "div", "id");
		containerBlockId = uiPageIdNode.innerHTML;
  	var params = [
	  	{name: "objectId", value : object.id},
	  	{name: "posX", value : object.offsetLeft},
	  	{name: "posY", value : object.offsetTop},
	  	{name: "zIndex", value : object.style.zIndex}
	  ] ;
	  
  	ajaxAsyncGetRequest(eXo.env.server.createPortalURL(containerBlockId, "SaveWidgetProperties", true, params), false) ;
	} ,
	
	resizeContainer : function() {
		var gadgets  = document.getElementById("UIGadgets") ;
		if(gadgets == null) return ;	
		
		var DOMUtil = eXo.core.DOMUtil ;
		var workspacePanel = document.getElementById("UIWorkspacePanel") ;
		if(workspacePanel.style.display == "none") return;
		var uiGadgetContainer = DOMUtil.findFirstDescendantByClass(gadgets, "div", "UIGadgetContainer");
		if(uiGadgetContainer == null) return ;
		var gadgetNavigator = DOMUtil.findFirstChildByClass(uiGadgetContainer, "div", "WidgetNavigator") ;	
		var gadgetContainerScrollArea = DOMUtil.findFirstChildByClass(uiGadgetContainer, "div", "WidgetContainerScrollArea") ;
		var itemSelectorContainer = DOMUtil.findFirstChildByClass(gadgets, "div", "ItemSelectorContainer") ;
		
		var availableHeight = workspacePanel.offsetHeight - (itemSelectorContainer.offsetHeight + gadgetNavigator.offsetHeight + 40) ;
		if(eXo.core.Browser.isIE6() || workspacePanel.offsetHeight < 1) {
			//var html = document.getElementsByTagName("html")[0];
			var uiControlWorkspace = document.getElementById("UIControlWorkspace") ;
			var fixHeight = uiControlWorkspace.offsetHeight - 153;
	    fixHeight = (fixHeight < 0) ? 0 : fixHeight ;
			/* 153 is total value (UserWorkspaceTitleHeight + UIExoStartHeight + WidgetNavigatorHeight + 40)
			 * 40 is distance between UIGadgets and UIExoStart 
			 * */
			if(gadgetContainerScrollArea.offsetHeight == fixHeight) return;
			gadgetContainerScrollArea.style.height = fixHeight + "px" ;
		} else {
			if(availableHeight < 0) return ;
			gadgetContainerScrollArea.style.height = availableHeight + "px" ;
		}
	  gadgetContainerScrollArea.style.overflow = "hidden" ;
	} 
}