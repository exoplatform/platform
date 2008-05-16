var DOMUtil = eXo.core.DOMUtil;
var DragDrop2 = eXo.core.DragDrop2;
eXo.webui.UIDashboard = {
	
	portletWindow: null,
	
	init : function (dragItem, dragObj) {
		DragDrop2.init(dragItem, dragObj)	;

		dragObj.onDragStart = function(x, y, lastMouseX, lastMouseY, e){
			
			var uiWorkingWS = document.getElementById("UIWorkingWorkspace");
			
			var dragCopyObj = dragObj.cloneNode(true);
			DOMUtil.addClass(dragCopyObj, "CopyObject");
			dragObj.parentNode.insertBefore(dragCopyObj,dragObj);
			var uiWindow = DOMUtil.findAncestorByClass(dragCopyObj, "UIWindow");
			eXo.webui.UIDashboard.portletWindow = uiWindow;
		
			//width and height of drag object
			var ggwidth = dragObj.offsetWidth - parseInt(DOMUtil.getStyle(dragObj,"borderLeftWidth"))
											- parseInt(DOMUtil.getStyle(dragObj,"borderRightWidth"));
			var ggheight = dragObj.offsetHeight - parseInt(DOMUtil.getStyle(dragObj,"borderTopWidth"))
											- parseInt(DOMUtil.getStyle(dragObj,"borderBottomWidth"));
			
			dragObj.style.position = "absolute";
			dragObj.style.width = ggwidth + "px";
			
			var mx = eXo.webui.UIDashboardUtil.findMouseRelativeX(uiWorkingWS, e);
			var ox = eXo.webui.UIDashboardUtil.findMouseRelativeX(dragCopyObj, e);
			var x = mx-ox;
				
			var my = eXo.webui.UIDashboardUtil.findMouseRelativeY(uiWorkingWS, e);
			var oy = eXo.webui.UIDashboardUtil.findMouseRelativeY(dragCopyObj, e);
			var y = my-oy;

			var temp = dragCopyObj;
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
				
			if(!DOMUtil.hasClass(dragObj, "Dragging"))
				DOMUtil.addClass(dragObj, "Dragging");
			
			eXo.webui.UIDashboardUtil.setPositionInContainer(uiWorkingWS, dragObj, x, y);
			
			var dashboardContainer = DOMUtil.findFirstDescendantByClass(uiWindow, "div", "DashboardContainer");
			if(eXo.webui.UIDashboardUtil.isIn(lastMouseX, lastMouseY, dashboardContainer)){
				var uiCol = DOMUtil.findAncestorByClass(dragCopyObj, "UIColumn");
				var uiTarget = document.createElement("div");
				uiTarget.id = "UITarget";
				uiTarget.className = "UITarget";
				uiTarget.style.width = ggwidth + "px";
				uiTarget.style.height = ggheight + "px";
				
				uiCol.replaceChild(uiTarget, dragCopyObj);
			}

		}
		
		
		
		dragObj.onDrag = function(nx, ny, ex, ey, e){			
			
			var uiWorkingWS = document.getElementById("UIWorkingWorkspace");
			var uiWindow = eXo.webui.UIDashboard.portletWindow;
			
			if(uiWindow == null) return;
			
			var dashboardContainer = DOMUtil.findFirstDescendantByClass(uiWindow, "div", "DashboardContainer");
			var uiTarget = DOMUtil.findFirstDescendantByClass(dashboardContainer, "div" , "UITarget");		
			
			
			if(eXo.webui.UIDashboardUtil.isIn(ex, ey, dashboardContainer)){
				var cols = DOMUtil.findDescendantsByClass(dashboardContainer, "div", "UIColumn");
				var uiCol = null;
				//search column which mouse drag in
				for(var i=0; i<cols.length; i++){
					var uiColLeft = eXo.webui.UIDashboardUtil.findPosX(cols[i]);
					if(uiColLeft<ex && ex<uiColLeft+cols[i].offsetWidth){
						uiCol = cols[i];
						break;
					}
				}
				
				if(uiCol==null) return;
				
				if(uiTarget == null){
					uiTarget = document.createElement("div");
					uiTarget.id = "UITarget";
					uiTarget.className = "UITarget";
					var ggheight = dragObj.offsetHeight - parseInt(DOMUtil.getStyle(dragObj,"borderTopWidth"))
										- parseInt(DOMUtil.getStyle(dragObj,"borderBottomWidth"));
					uiTarget.style.height = ggheight + "px";
				}
			
				var uiColLeft = eXo.webui.UIDashboardUtil.findPosX(uiCol);
				if(uiColLeft<ex  &&  ex<uiColLeft+uiCol.offsetWidth){
					var gadgets = DOMUtil.findDescendantsByClass(uiCol, "div", "UIGadget");
	
					//if column hasn't got a gadget, add target to column
					if(gadgets.length == 0){
						uiCol.appendChild(uiTarget);
						return;
					}
									
					//remove current gadget from gadgets array
					for(var i=0; i<gadgets.length; i++){
						if(gadgets[i].id==dragObj.id) {
							gadgets.splice(i,1);
							break;
						}
					}

					//add uiTarget in column
					for(var i=0; i<gadgets.length; i++){
						var oy = eXo.webui.UIDashboardUtil.findPosYInContainer(gadgets[i],uiWorkingWS) + gadgets[i].offsetHeight/3;
						if(ey<oy){
							uiCol.insertBefore(uiTarget, gadgets[i]);
							break;
						}
						if(i==gadgets.length-1 && ey>oy)
							uiCol.appendChild(uiTarget);
					}
					
				}else{
					
					// mouse over another column
					var cols = DOMUtil.findDescendantsByClass(gadgetContainer, "div", "UIColumn");
					
					for(var i=0; i<cols.length; i++){
						if(eXo.webui.UIDashboardUtil.isIn(ex, ey, cols[i])){
							DOMUtil.moveElemt(uiTarget, cols[i]);
							DOMUtil.moveElemt(dragObj, cols[i]);
							break;
						}
					}
					
				}
			}
			else{
				//prevent dragging gadget object out of DashboardContainer
				if(uiTarget!=null && !DOMUtil.hasClass(dragObj, "UIGadget"))
					DOMUtil.removeElement(uiTarget);
			}			
		}


	
		dragObj.onDragEnd = function(x, y, clientX, clientY){
			
			var uiWindow = eXo.webui.UIDashboard.portletWindow;
			if(uiWindow == null) return;
			
			var uiTarget = DOMUtil.findFirstDescendantByClass(uiWindow, "div", "UITarget");
			dragObj.style.position = "static";
			if(DOMUtil.hasClass(dragObj, "Dragging"))
			DOMUtil.replaceClass(dragObj," Dragging","");

			var dragCopyObj = DOMUtil.findFirstDescendantByClass(uiWindow, "div", "CopyObject");
			if(uiTarget == null){
				if(dragCopyObj != null)
					dragCopyObj.parentNode.replaceChild(dragObj, dragCopyObj);
			}else{
					
				if(!DOMUtil.hasClass(dragObj, "UIGadget")){
					var innerHTML = '<div class="UIGadget DragObject" id="'+dragObj.id+'-'+ new Date().getTime()+'" style="top:0px; left:0px;">';
					innerHTML +=	'<div class="GadgetMenuBar DragItem" id="">'+dragObj.id+'</div>';
					innerHTML +=	'<div class="GadgetContent">content</div>';
					innerHTML +=	'</div>';
					var uiGadget = DOMUtil.createElementNode(innerHTML, "div", "GadgetMenuBar");
					eXo.webui.UIDashboard.init(DOMUtil.findFirstDescendantByClass(uiGadget, "div", "GadgetMenuBar"), uiGadget);
					uiTarget.parentNode.replaceChild(uiGadget, uiTarget);
					DOMUtil.removeElement(dragObj);
					if(dragCopyObj != null)
						dragCopyObj.parentNode.replaceChild(dragObj, dragCopyObj);
					return;
				}
					
				uiTarget.parentNode.replaceChild(dragObj, uiTarget);
				DOMUtil.removeElement(dragCopyObj);
			}
		}
		
	},
	
	onLoad : function() {	
		
		var uiWorkingWS = document.getElementById("UIWorkingWorkspace");
		var dashboards = DOMUtil.findDescendantsByClass(uiWorkingWS, "div", "UIDashboardPortlet");

		for(var i=0; i < dashboards.length; i++){
			var dragItems = DOMUtil.findDescendantsByClass(dashboards[i],"div","DragItem");
			for(var j=0; j<dragItems.length; j++) {
				eXo.webui.UIDashboard.init(dragItems[j], DOMUtil.findAncestorByClass(dragItems[j],"DragObject"));
			}
		}

	}
	
}