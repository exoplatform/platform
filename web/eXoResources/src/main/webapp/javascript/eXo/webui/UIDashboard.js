eXo.webui.UIDashboard = {
	
	portletWindow: null,
	
	currCol : null,
	
	targetObj : null,
	
	init : function (dragItem, dragObj) {
		
		eXo.core.DragDrop2.init(dragItem, dragObj)	;

		dragObj.onDragStart = function(x, y, lastMouseX, lastMouseY, e){
			var DOMUtil = eXo.core.DOMUtil;
			var uiDashboard = eXo.webui.UIDashboard ;
			var uiWorkingWS = document.getElementById("UIWorkingWorkspace");
			var uiWindow = DOMUtil.findAncestorByClass(dragObj, "UIWindow");
			uiDashboard.portletWindow = uiWindow;
			var dashboardContainer = DOMUtil.findFirstDescendantByClass(uiWindow, "div", "DashboardContainer");
			var portletApp = DOMUtil.findAncestorByClass(dashboardContainer, "UIApplication");

			var ggwidth = dragObj.offsetWidth;
			var ggheight = dragObj.offsetHeight;
			
			//find position to put drag object in
			var mx = eXo.webui.UIDashboardUtil.findMouseRelativeX(uiWorkingWS, e);
			var ox = eXo.webui.UIDashboardUtil.findMouseRelativeX(dragObj, e);
			var x = mx-ox;
				
			var my = eXo.webui.UIDashboardUtil.findMouseRelativeY(uiWorkingWS, e);
			var oy = eXo.webui.UIDashboardUtil.findMouseRelativeY(dragObj, e);
			var y = my-oy;

			var temp = dragObj;
			while(temp.parentNode && DOMUtil.hasDescendant(uiWindow, temp)){
				if(temp.scrollLeft>0) 
					x -= temp.scrollLeft;
				if(temp.scrollTop>0)
					y -= temp.scrollTop;
				temp = temp.parentNode;
			}
			
			var slideBar = document.getElementById("ControlWorkspaceSlidebar");
			if(slideBar!=null && slideBar.style.display!="none" && eXo.core.Browser.getBrowserType()=="ie")
				x -= slideBar.offsetWidth;
			
			var uiTarget = null;
			if(!DOMUtil.hasClass(dragObj, "SelectItem")){
				uiTarget = uiDashboard.createTarget(ggwidth, 0);
				dragObj.parentNode.insertBefore(uiTarget, dragObj.nextSibling);
				uiDashboard.currCol = eXo.webui.UIDashboardUtil.findRowIndexInDashboard(dragObj);
			}else{
				var dragCopyObj = dragObj.cloneNode(true);
				DOMUtil.addClass(dragCopyObj, "CopyObj");
				dragObj.parentNode.insertBefore(dragCopyObj, dragObj);
				uiDashboard.targetObj = null;
			}
			dragObj.style.width = ggwidth +"px";

			//increase speed of mouse when over iframe by create div layer above it
			var uiGadgets = DOMUtil.findDescendantsByClass(dashboardContainer, "div", "UIGadget");
			
			for(var i=0; i<uiGadgets.length; i++){
				var uiMask = DOMUtil.findFirstDescendantByClass(uiGadgets[i], "div", "UIMask");
				if(uiMask!=null){
					var gadgetContent = DOMUtil.findFirstDescendantByClass(uiGadgets[i], "div", "gadgets-gadget-content");
					uiMask.style.marginTop = - gadgetContent.offsetHeight + "px";
					uiMask.style.height = gadgetContent.offsetHeight + "px";
					uiMask.style.width = gadgetContent.offsetWidth + "px";
					uiMask.style.display = "block";
					uiMask.style.backgroundColor = "white";
					eXo.core.Browser.setOpacity(uiMask, 3);
				}
			}
			
			if(!DOMUtil.hasClass(dragObj, "Dragging"))
				DOMUtil.addClass(dragObj, "Dragging");
				
			//set position of drag object
			dragObj.style.position = "absolute";
			eXo.webui.UIDashboardUtil.setPositionInContainer(uiWorkingWS, dragObj, x, y);
			if(uiTarget!=null){
				uiTarget.style.height = ggheight +"px";
				uiDashboard.targetObj = uiTarget;
			}
		}
		
		
		
		dragObj.onDrag = function(nx, ny, ex, ey, e){	
			var DOMUtil = eXo.core.DOMUtil;		
			var uiTarget = eXo.webui.UIDashboard.targetObj;
			var uiWindow = eXo.webui.UIDashboard.portletWindow;

			if(uiWindow == null) return;
			
			var dashboardCont = DOMUtil.findFirstDescendantByClass(uiWindow, "div", "DashboardContainer");
			var cols = null;

			if(eXo.webui.UIDashboardUtil.isIn(ex, ey, dashboardCont)){
				if(uiTarget == null){
					uiTarget = eXo.webui.UIDashboard.createTargetOfAnObject(dragObj);
					eXo.webui.UIDashboard.targetObj = uiTarget;
				}
				
				var uiCol = eXo.webui.UIDashboard.currCol ;
				
				if(uiCol == null){
					if(cols == null) cols = DOMUtil.findDescendantsByClass(dashboardCont, "div", "UIColumn");
					for(var i=0; i<cols.length; i++){
						var uiColLeft = eXo.webui.UIDashboardUtil.findPosX(cols[i]) - dashboardCont.scrollLeft;
						if(uiColLeft<ex  &&  ex<uiColLeft+cols[i].offsetWidth){
							uiCol = cols[i];
							eXo.webui.UIDashboard.currCol = uiCol;
							break;
						}
					}
					
				}
				
				if(uiCol==null) return;

				var uiColLeft = eXo.webui.UIDashboardUtil.findPosX(uiCol) - dashboardCont.scrollLeft;
				if(uiColLeft<ex  &&  ex<uiColLeft+uiCol.offsetWidth ){
					var gadgets = DOMUtil.findDescendantsByClass(uiCol, "div", "UIGadget");
					//remove drag object from dropable target
					for(var i=0; i<gadgets.length; i++){
						if(dragObj.id==gadgets[i].id){
							gadgets.splice(i,1);
							break;
						}
					}

					if(gadgets.length == 0){
						uiCol.appendChild(uiTarget);
						return;
					}

					//find position and add uiTarget into column				
					for(var i=0; i<gadgets.length; i++){
						var oy = eXo.webui.UIDashboardUtil.findPosY(gadgets[i]);
						
						if(ey<=oy){
							uiCol.insertBefore(uiTarget, gadgets[i]);
							break;
						}
						if(i==gadgets.length-1 && ey>oy)
							uiCol.appendChild(uiTarget);
						
					}
					
				}	else {

					//find column which draggin in					
					if(cols == null) cols = DOMUtil.findDescendantsByClass(dashboardCont, "div", "UIColumn");
					for(var i=0; i<cols.length; i++){
						var uiColLeft = eXo.webui.UIDashboardUtil.findPosX(cols[i]) - dashboardCont.scrollLeft;
						if(uiColLeft<ex  &&  ex<uiColLeft+cols[i].offsetWidth){
							eXo.webui.UIDashboard.currCol = cols[i];
							break;
						}
					}
				}
			} else {
				//prevent dragging gadget object out of DashboardContainer
				if(uiTarget!=null && DOMUtil.hasClass(dragObj, "SelectItem")){
					uiTarget.parentNode.removeChild(uiTarget);					
					eXo.webui.UIDashboard.targetObj = uiTarget = null;
				}
			}
		}


	
		dragObj.onDragEnd = function(x, y, clientX, clientY){
			var uiDashboard = eXo.webui.UIDashboard;
			var uiDashboardUtil = eXo.webui.UIDashboardUtil;
			var uiWindow = uiDashboard.portletWindow;
			
			if(uiWindow == null) return;
			
			var masks = eXo.core.DOMUtil.findDescendantsByClass(uiWindow, "div", "UIMask");
			for(var i=0; i<masks.length; i++){
				eXo.core.Browser.setOpacity(masks[i], 100);
				masks[i].style.display = "none";
			}
			
			var uiTarget = uiDashboard.targetObj;
			if(uiTarget != null && (uiTarget.parentNode == null || uiTarget.parentNode == "undefined")) { 
				uiTarget = null; 
			}
			dragObj.style.position = "static";
			if(eXo.core.DOMUtil.hasClass(dragObj, "Dragging")){
				eXo.core.DOMUtil.replaceClass(dragObj," Dragging","");
			}

			var dragCopyObj = eXo.core.DOMUtil.findFirstDescendantByClass(uiWindow, "div", "CopyObj");
			if(dragCopyObj != null){
				dragCopyObj.parentNode.replaceChild(dragObj, dragCopyObj);
				dragObj.style.width = "auto";
			}
			
			if(uiTarget!=null){	
				//if drag object is not gadget module, create an module
				var col = uiDashboardUtil.findColIndexInDashboard(uiTarget);
				var row = uiDashboardUtil.findRowIndexInDashboard(uiTarget);
				var compId = uiWindow.id.substring(uiWindow.id.lastIndexOf('-')+1, uiWindow.id.length);
				
				if(eXo.core.DOMUtil.hasClass(dragObj, "SelectItem")){
					var url = uiDashboardUtil.createRequest(compId, 'AddNewGadget', col, row, dragObj.id);
					ajaxGet(url);
				} else {
					//in case: drop to old position
					if(uiDashboardUtil.findColIndexInDashboard(dragObj) == col 
								&& uiDashboardUtil.findRowIndexInDashboard(dragObj) == (row-1)){
						uiTarget.parentNode.removeChild(uiTarget);
					} else {					
						uiTarget.parentNode.replaceChild(dragObj, uiTarget);
						gadgetId = dragObj.id;
						var url = uiDashboardUtil.createRequest(compId, 'MoveGadget', col, row, gadgetId);
						ajaxAsyncGetRequest(url);
					}
				}
			}

			uiTarget = eXo.core.DOMUtil.findFirstDescendantByClass(uiWindow, "div", "UITarget");
			while(uiTarget!=null){
				eXo.core.DOMUtil.removeElement(uiTarget);
				uiTarget = eXo.core.DOMUtil.findFirstDescendantByClass(uiWindow, "div", "UITarget");
			}
			uiDashboard.targetObj = uiDashboard.currCol = uiDashboard.compId = uiDashboard.portletWindow = null;
		}	
		
	},
	
	onLoad : function(windowId) {	
		var uiWindow = document.getElementById(windowId);
		if(uiWindow == null) return;

		var uiDashboard = eXo.core.DOMUtil.findFirstDescendantByClass(uiWindow, "div", "UIDashboardPortlet");
		if(uiDashboard == null) return;
		
		uiDashboard.style.overflow = "hidden";

		var uiContainer = eXo.core.DOMUtil.findFirstChildByClass(uiDashboard, "div", "UIDashboardContainer");

		var gadgetControls = eXo.core.DOMUtil.findDescendantsByClass(uiDashboard, "div", "GadgetTitle");
		for(var j=0; j<gadgetControls.length; j++) {
			eXo.webui.UIDashboard.init(gadgetControls[j], eXo.core.DOMUtil.findAncestorByClass(gadgetControls[j],"UIGadget"));
		}
		
		if(uiContainer == null) return;
		var dbContainer = eXo.core.DOMUtil.findFirstChildByClass(uiContainer, "div", "DashboardContainer");
		var colsContainer = eXo.core.DOMUtil.findFirstChildByClass(dbContainer, "div", "UIColumns");
		var columns = eXo.core.DOMUtil.findChildrenByClass(colsContainer, "div", "UIColumn");
		var colsSize = 0;
		for(var i=0; i<columns.length; i++){
			if(columns[i].style.display != "none") colsSize++;
		}
		colsContainer.style.width = colsSize*320 + 20 + "px";
		
		var uiSelectForm = eXo.core.DOMUtil.findFirstChildByClass(uiDashboard, "div", "UIDashboardSelectForm");
		if(uiSelectForm.style.display != "none") {
			var middleItemContainer = eXo.core.DOMUtil.findFirstDescendantByClass(uiSelectForm, "div", "MiddleItemContainer");
			middleItemContainer.style.height = uiWindow.offsetHeight - 66 + "px";
			uiContainer.style.marginLeft = "210px";
		} else {
			uiContainer.style.marginLeft = "0px";
		}
	},
	
	createTarget : function(width, height){
		var uiTarget = document.createElement("div");
		uiTarget.id = "UITarget";
		uiTarget.className = "UITarget";
		uiTarget.style.width = width + "px";
		uiTarget.style.height = height + "px";
		return uiTarget;
	},
	
	createTargetOfAnObject : function(obj){
		var ggwidth = obj.offsetWidth;
		var ggheight = obj.offsetHeight;
		var uiTarget = document.createElement("div");
		uiTarget.id = "UITarget";
		uiTarget.className = "UITarget";
		uiTarget.style.height = ggheight + "px";
		return uiTarget;
	},
	
	showHideSelectForm : function(comp){
		var DOMUtil = eXo.core.DOMUtil;
		var uiDashboardPortlet = DOMUtil.findAncestorByClass(comp, "UIDashboardPortlet");
		var uiWindow = uiDashboardPortlet.parentNode;
		var uiSelectForm = DOMUtil.findFirstChildByClass(uiDashboardPortlet, "div", "UIDashboardSelectForm");
		var uiContainer = DOMUtil.findFirstChildByClass(uiDashboardPortlet, "div", "UIDashboardContainer");
		
		var portletId = DOMUtil.findAncestorById(uiDashboardPortlet, "PORTLET-FRAGMENT").parentNode.id;
		
		var addButton = DOMUtil.findFirstDescendantByClass(uiContainer, "div", "ContainerControlBarL");
		
		var url = eXo.env.server.portalBaseURL + '?portal:componentId=' + portletId +
						'&portal:type=action&portal:isSecure=false&uicomponent=' + uiDashboardPortlet.id +
						'&op=SetShowSelectForm&ajaxRequest=true' ;
		
		if(uiSelectForm.style.display != "none"){
			uiSelectForm.style.display = "none";
			url += '&isShow=false';
			addButton.style.visibility = "visible";
			uiContainer.style.marginLeft = "0px";
		} else {
			uiSelectForm.style.display = "block";
			var middleItemContainer = DOMUtil.findFirstDescendantByClass(uiSelectForm, "div", "MiddleItemContainer");
			middleItemContainer.style.height = uiWindow.offsetHeight - 66 + "px";
			url += '&isShow=true';
			addButton.style.visibility = "hidden";
			uiContainer.style.marginLeft = "210px";
		}
		ajaxAsyncGetRequest(url, false);
	}, 
	
	onTabClick : function(clickElement, normalStyle, selectedType){
		var DOMUtil = eXo.core.DOMUtil;
		var category = DOMUtil.findAncestorByClass(clickElement, "GadgetCategory");
		var categoryContent = DOMUtil.findFirstChildByClass(category, "div", "ItemsContainer");
		var categoriesContainer = DOMUtil.findAncestorByClass(category, "GadgetItemsContainer");
		var categories = DOMUtil.findChildrenByClass(categoriesContainer, "div", "GadgetCategory");
		var gadgetTab = DOMUtil.findFirstChildByClass(category, "div", "GadgetTab");
		
		if(DOMUtil.hasClass(gadgetTab, normalStyle)) {
			for(var i=0; i<categories.length; i++){
				DOMUtil.findFirstChildByClass(categories[i], "div", "GadgetTab").className = "GadgetTab "+normalStyle;
				DOMUtil.findFirstChildByClass(categories[i], "div", "ItemsContainer").style.display = "none";
			}
			DOMUtil.findFirstChildByClass(category, "div", "GadgetTab").className = "GadgetTab "+selectedType;
			categoryContent.style.display = "block";
		} else {
			DOMUtil.findFirstChildByClass(category, "div", "GadgetTab").className = "GadgetTab "+normalStyle;
			categoryContent.style.display = "none";
		}
	},
	
	enableContainer : function(elemt){
		var DOMUtil = eXo.core.DOMUtil;
		if(DOMUtil.hasClass(elemt, "DisableContainer")){
			DOMUtil.replaceClass(elemt, " DisableContainer", "");
		}
	},
	
	disableContainer : function(elemt){
		var DOMUtil = eXo.core.DOMUtil;
		if(!DOMUtil.hasClass(elemt, "DisableContainer")){
			DOMUtil.addClass(elemt, "DisableContainer");
		}
	}
}
