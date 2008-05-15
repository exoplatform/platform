var DOMUtil = eXo.core.DOMUtil;
var DragDrop2 = eXo.core.DragDrop2;
eXo.widget.GadgetDragDrop = {
	
	portletWindow: null,
	
	init : function (dragItem, dragObj) {
		DragDrop2.init(dragItem, dragObj)	;

		dragObj.onDragStart = function(x, y, lastMouseX, lastMouseY, e){
			
			var uiWorkspace = document.getElementById("UIWorkingWorkspace");
			
			var dragCopyObj = dragObj.cloneNode(true);
			DOMUtil.addClass(dragCopyObj, "CopyObject");
			dragObj.parentNode.insertBefore(dragCopyObj,dragObj);
			var uiWindow = DOMUtil.findAncestorByClass(dragCopyObj, "UIWindow");
			eXo.widget.GadgetDragDrop.portletWindow = uiWindow;
		
			var ggwidth = dragObj.offsetWidth - parseInt(DOMUtil.getStyle(dragObj,"borderLeftWidth"))
											- parseInt(DOMUtil.getStyle(dragObj,"borderRightWidth"));
			var ggheight = dragObj.offsetHeight - parseInt(DOMUtil.getStyle(dragObj,"borderTopWidth"))
											- parseInt(DOMUtil.getStyle(dragObj,"borderBottomWidth"));
			
			dragObj.style.position = "absolute";
			dragObj.style.width = ggwidth + "px";
			
			var mx = eXo.widget.GadgetUtil.findMouseRelativeX(uiWorkspace, e);
			var ox = eXo.widget.GadgetUtil.findMouseRelativeX(dragCopyObj, e);
			var x = mx-ox;
				
			var my = eXo.widget.GadgetUtil.findMouseRelativeY(uiWorkspace, e);
			var oy = eXo.widget.GadgetUtil.findMouseRelativeY(dragCopyObj, e);
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
			
			eXo.widget.GadgetUtil.setPositionInContainer(uiWorkspace, dragObj, x, y);
			
			var gadgetContainer = DOMUtil.findFirstDescendantByClass(uiWindow, "div", "GadgetsContainer");
			if(eXo.widget.GadgetUtil.isIn(lastMouseX, lastMouseY, gadgetContainer)){
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
			var uiWorkspace = document.getElementById("UIWorkingWorkspace");
			var uiWindow = eXo.widget.GadgetDragDrop.portletWindow;
			if(uiWindow == null) return;
			
			var gadgetContainer = DOMUtil.findFirstDescendantByClass(uiWindow, "div", "GadgetsContainer");
			var uiTarget = DOMUtil.findFirstDescendantByClass(gadgetContainer, "div" , "UITarget");		
			
			
			if(eXo.widget.GadgetUtil.isIn(ex, ey, gadgetContainer)){
				var cols = DOMUtil.findDescendantsByClass(gadgetContainer, "div", "UIColumn");
				var uiCol = null;
				//search column which mouse drag on
				for(var i=0; i<cols.length; i++){
					if(eXo.widget.GadgetUtil.isIn(ex, ey, cols[i])){
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
			
				if(eXo.widget.GadgetUtil.isIn(ex, ey, uiCol)){
					var gadgets = DOMUtil.findDescendantsByClass(uiCol, "div", "UIGadget");
					
					//remove current gadget from gadgets array
					for(var i=0; i<gadgets.length; i++){
						if(gadgets[i].id==dragObj.id) {
							gadgets.splice(i,1);
							break;
						}
					}
					
					//add uiTarget in column
					for(var i=0; i<gadgets.length; i++){
						var oy = eXo.widget.GadgetUtil.findPosYInContainer(gadgets[i],uiWorkspace) + gadgets[i].offsetHeight/3;
						if(ey<oy){
							uiCol.insertBefore(uiTarget, gadgets[i]);
							break;
						}
						if(i==gadgets.length-1 && ey>oy)
							uiCol.appendChild(uiTarget);
					}
					
				}else{
					
					var cols = DOMUtil.findDescendantsByClass(gadgetContainer, "div", "UIColumn");
					
					for(var i=0; i<cols.length; i++){
						if(eXo.widget.GadgetUtil.isIn(ex, ey, cols[i])){
							DOMUtil.moveElemt(uiTarget, cols[i]);
							DOMUtil.moveElemt(dragObj, cols[i]);
							break;
						}
					}
					
				}
			}
			else{
				if(uiTarget!=null && !DOMUtil.hasClass(dragObj, "UIGadget"))
					DOMUtil.removeElement(uiTarget);
			}			
		}


	
		dragObj.onDragEnd = function(x, y, clientX, clientY){
			
			var uiWindow = eXo.widget.GadgetDragDrop.portletWindow;
			if(uiWindow == null) return;
			
			var uiTarget = DOMUtil.findFirstDescendantByClass(uiWindow, "div", "UITarget");
			dragObj.style.position = "static";
			dragObj.style.width = "auto";
			dragObj.style.height = "auto";
			if(DOMUtil.hasClass(dragObj, "Dragging"))
			DOMUtil.replaceClass(dragObj," Dragging","");
		
			if(uiTarget == null){
				var dragCopyObj = DOMUtil.findFirstDescendantByClass(uiWindow, "div", "CopyObject");
				dragCopyObj.parentNode.replaceChild(dragObj, dragCopyObj);
			}else{
					
				if(!DOMUtil.hasClass(dragObj, "UIGadget")){
					var innerHTML = '<div class="UIGadget DragObject" id="" style="top:0px; left:0px;">';
					innerHTML +=	'<div class="GadgetMenuBar DragItem" id="">menubar</div>';
					innerHTML +=	'<div class="GadgetContent">content</div>';
					innerHTML +=	'</div>';
					var uiGadget = DOMUtil.createElementNode(innerHTML, "div", "GadgetMenuBar");
					eXo.widget.GadgetDragDrop.init(DOMUtil.findFirstDescendantByClass(uiGadget, "div", "GadgetMenuBar"), uiGadget);
					uiTarget.parentNode.replaceChild(uiGadget, uiTarget);
					DOMUtil.removeElement(dragObj);
					return;
				}
					
				uiTarget.parentNode.replaceChild(dragObj, uiTarget);
				DOMUtil.removeElement(dragCopyObj);
			}
		}
		
	},
	
	onLoadGadgetContainer : function() {	
		
		var uiWorkspace = document.getElementById("UIWorkingWorkspace");
		var dashboards = DOMUtil.findDescendantsByClass(uiWorkspace, "div", "UIDashboardPortlet");

		for(var i=0; i < dashboards.length; i++){
			var dragItems = DOMUtil.findDescendantsByClass(dashboards[i],"div","DragItem");
			for(var j=0; j<dragItems.length; j++) {
				eXo.widget.GadgetDragDrop.init(dragItems[j], DOMUtil.findAncestorByClass(dragItems[j],"DragObject"));
			}
		}

	}
	
}