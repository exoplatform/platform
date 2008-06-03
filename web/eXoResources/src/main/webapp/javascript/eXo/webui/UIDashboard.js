eXo.webui.UIDashboard = {
	
	compId: null,
		
	portletWindow: null,
	
	currCol : null,
	
	targetObj : null,
	
	currZIndex : null,
	
	init : function (dragItem, dragObj) {
		var uiDashboard = eXo.webui.UIDashboard ;
		eXo.core.DragDrop2.init(dragItem, dragObj)	;

		dragObj.onDragStart = function(x, y, lastMouseX, lastMouseY, e){
			var DOMUtil = eXo.core.DOMUtil;
			var uiWorkingWS = document.getElementById("UIWorkingWorkspace");
			var uiWindow = DOMUtil.findAncestorByClass(dragObj, "UIWindow");
			uiDashboard.portletWindow = uiWindow;
			uiDashboard.currZIndex = dragObj.style.zIndex;
			var dashboardContainer = DOMUtil.findFirstDescendantByClass(uiWindow, "div", "DashboardContainer");
			var portletApp = DOMUtil.findAncestorByClass(dashboardContainer, "UIApplication");
			uiDashboard.compId = portletApp.parentNode.id;
			
			var ggwidth = dragObj.offsetWidth - parseInt(DOMUtil.getStyle(dragObj,"borderLeftWidth"))
											- parseInt(DOMUtil.getStyle(dragObj,"borderRightWidth"));
			var ggheight = dragObj.offsetHeight - parseInt(DOMUtil.getStyle(dragObj,"borderTopWidth"))
											- parseInt(DOMUtil.getStyle(dragObj,"borderBottomWidth"));
											
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
				uiDashboard.currCol = uiTarget.parentNode;
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
					uiMask.style.backgroundColor = "red";
					eXo.core.Browser.setOpacity(uiMask, 5);
					uiMask.style.display = "block";
				}
			}
			
			if(!DOMUtil.hasClass(dragObj, "Dragging"))
				eXo.core.DOMUtil.addClass(dragObj, "Dragging");
				
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
						var uiColLeft = eXo.webui.UIDashboardUtil.findPosX(cols[i]);
						if(uiColLeft<ex  &&  ex<uiColLeft+cols[i].offsetWidth){
							uiCol = cols[i];
							eXo.webui.UIDashboard.currCol = uiCol;
							break;
						}
					}
					
				}
				
				if(uiCol==null) return;

				var uiColLeft = eXo.webui.UIDashboardUtil.findPosX(uiCol);
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
						var oy = eXo.webui.UIDashboardUtil.findPosY(gadgets[i]) + gadgets[i].offsetHeight/2;
						
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
						var uiColLeft = eXo.webui.UIDashboardUtil.findPosX(cols[i]);
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
			dragObj.style.zIndex = uiDashboard.currZIndex;
			uiDashboard.currZIndex = null;
			dragObj.style.position = "static";
			if(eXo.core.DOMUtil.hasClass(dragObj, "Dragging")){
				eXo.core.DOMUtil.replaceClass(dragObj," Dragging","");
			}

			var dragCopyObj = eXo.core.DOMUtil.findFirstDescendantByClass(uiWindow, "div", "CopyObj");
			if(dragCopyObj != null){
				dragCopyObj.parentNode.replaceChild(dragObj, dragCopyObj);
			}
			
			if(uiTarget!=null){	
				//if drag object is not gadget module, create an module
				if(eXo.core.DOMUtil.hasClass(dragObj, "SelectItem")){
					uiDashboardUtil.createRequest(uiDashboard.compId, 'AddNewGadget',
									uiDashboardUtil.findPosXInDashboard(uiTarget), 
									uiDashboardUtil.findPosYInDashboard(uiTarget), dragObj.id);
				} else {
					uiTarget.parentNode.replaceChild(dragObj, uiTarget);
					gadgetId = dragObj.id;
					uiDashboardUtil.createRequest(uiDashboard.compId, 'MoveGadget',
									uiDashboardUtil.findPosXInDashboard(dragObj), 
									uiDashboardUtil.findPosYInDashboard(dragObj), gadgetId);
				}
			}
			
			uiTarget = eXo.core.DOMUtil.findFirstDescendantByClass(uiWindow, "div", "UITarget");
			while(uiTarget!=null){
				eXo.core.DOMUtil.removeElement(uiTarget);
				uiTarget = eXo.core.DOMUtil.findFirstDescendantByClass(uiWindow, "div", "UITarget");
			}
			uiDashboard.targetObj = null;
			uiDashboard.currCol = null;
			uiDashboard.portletWindow = null;
			uiDashboard.compId = null;
		}	
		
	},
	
	onLoad : function() {	
		
		var uiWorkingWS = document.getElementById("UIWorkingWorkspace");
		var dashboards = eXo.core.DOMUtil.findDescendantsByClass(uiWorkingWS, "div", "UIDashboardPortlet");

		if(dashboards.length<=0) return;

		for(var i=0; i < dashboards.length; i++){
			var gadgetControls = eXo.core.DOMUtil.findDescendantsByClass(dashboards[i], "div", "GadgetTitle");
			for(var j=0; j<gadgetControls.length; j++) {
				eXo.webui.UIDashboard.init(gadgetControls[j], eXo.core.DOMUtil.findAncestorByClass(gadgetControls[j],"UIGadget"));
			}
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
	}
}