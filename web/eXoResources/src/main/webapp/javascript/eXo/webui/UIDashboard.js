eXo.webui.UIDashboard = {
		
	portletWindow: null,
	
	currCol : null,
	
	targetObj : null,
	
	currZIndex : null,
	
	init : function (dragItem, dragObj) {
		eXo.core.DragDrop2.init(dragItem, dragObj)	;

		dragObj.onDragStart = function(x, y, lastMouseX, lastMouseY, e){
			
			var uiWorkingWS = document.getElementById("UIWorkingWorkspace");
			var uiWindow = eXo.core.DOMUtil.findAncestorByClass(dragObj, "UIWindow");
			eXo.webui.UIDashboard.portletWindow = uiWindow;
			eXo.webui.UIDashboard.currZIndex = dragObj.style.zIndex;
			var dashboardContainer = eXo.core.DOMUtil.findFirstDescendantByClass(uiWindow, "div", "DashboardContainer");
			
			
			var ggwidth = dragObj.offsetWidth - parseInt(eXo.core.DOMUtil.getStyle(dragObj,"borderLeftWidth"))
											- parseInt(eXo.core.DOMUtil.getStyle(dragObj,"borderRightWidth"));
			var ggheight = dragObj.offsetHeight - parseInt(eXo.core.DOMUtil.getStyle(dragObj,"borderTopWidth"))
											- parseInt(eXo.core.DOMUtil.getStyle(dragObj,"borderBottomWidth"));
											
			//find position to put drag object in
			var mx = eXo.webui.UIDashboardUtil.findMouseRelativeX(uiWorkingWS, e);
			var ox = eXo.webui.UIDashboardUtil.findMouseRelativeX(dragObj, e);
			var x = mx-ox;
				
			var my = eXo.webui.UIDashboardUtil.findMouseRelativeY(uiWorkingWS, e);
			var oy = eXo.webui.UIDashboardUtil.findMouseRelativeY(dragObj, e);
			var y = my-oy;

			var temp = dragObj;
			while(temp.parentNode && eXo.core.DOMUtil.hasDescendant(uiWindow, temp)){
				if(temp.scrollLeft>0) 
					x -= temp.scrollLeft;
				if(temp.scrollTop>0)
					y -= temp.scrollTop;
				temp = temp.parentNode;
			}
			
			var slideBar = document.getElementById("ControlWorkspaceSlidebar");
			if(slideBar!=null && slideBar.style.display!="none" && eXo.core.Browser.getBrowserType()=="ie")
				x -= slideBar.offsetWidth;
				
			if(eXo.core.DOMUtil.hasClass(dragObj, "UIGadgetModule")){
				var uiTarget = eXo.webui.UIDashboard.createTarget(ggwidth, ggheight);
				dragObj.parentNode.insertBefore(uiTarget, dragObj.nextSibling);
				eXo.webui.UIDashboard.targetObj = uiTarget;
				eXo.webui.UIDashboard.currCol = uiTarget.parentNode;
			}else{
				var dragCopyObj = dragObj.cloneNode(true);
				eXo.core.DOMUtil.addClass(dragCopyObj, "CopyObj");
				dragObj.parentNode.insertBefore(dragCopyObj, dragObj);
				eXo.webui.UIDashboard.targetObj = null;
			}
			dragObj.style.width = ggwidth +"px";

			//increase speed of mouse when over iframe by create div layer above it
			var masks = eXo.core.DOMUtil.findDescendantsByClass(uiWindow, "div" , "UIMask");
			for(var i=0; i<masks.length; i++){
				var gadgetIframe = eXo.core.DOMUtil.findDescendantsByTagName(masks[i].parentNode, "iframe");
				if(gadgetIframe.length>0)
					masks[i].style.marginTop = - gadgetIframe[0].offsetHeight + "px";
				masks[i].style.height = ggheight - dragItem.offsetHeight + "px";
				masks[i].style.width = ggwidth + "px";
				masks[i].style.display = "block";
				masks[i].style.backgroundColor = "black";
				eXo.core.Browser.setOpacity(masks[i], 10);
			}
			
			if(!eXo.core.DOMUtil.hasClass(dragObj, "Dragging"))
				eXo.core.DOMUtil.addClass(dragObj, "Dragging");
				
			//set position of drag object
			dragObj.style.zIndex = "10000";
			dragObj.style.position = "absolute";
			eXo.webui.UIDashboardUtil.setPositionInContainer(uiWorkingWS, dragObj, x, y);
			
		}
		
		
		
		dragObj.onDrag = function(nx, ny, ex, ey, e){			
			var uiTarget = eXo.webui.UIDashboard.targetObj;
			var uiWindow = eXo.webui.UIDashboard.portletWindow;

			if(uiWindow == null) return;

			var dashboardCont = eXo.core.DOMUtil.findFirstDescendantByClass(uiWindow, "div", "DashboardContainer");
			var cols = null;

			if(eXo.webui.UIDashboardUtil.isIn(ex, ey, dashboardCont)){
				if(uiTarget == null){
					uiTarget = eXo.webui.UIDashboard.createTargetOfAnObject(dragObj);
					eXo.webui.UIDashboard.targetObj = uiTarget;
				}
				
				var uiCol = eXo.webui.UIDashboard.currCol ;
				
				if(uiCol == null){
					if(cols == null) cols = eXo.core.DOMUtil.findDescendantsByClass(dashboardCont, "div", "UIColumn");
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
					var gadgets = eXo.core.DOMUtil.findDescendantsByClass(uiCol, "div", "UIGadgetModule");
					
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
					if(cols == null) cols = eXo.core.DOMUtil.findDescendantsByClass(dashboardCont, "div", "UIColumn");
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
				if(uiTarget!=null && !eXo.core.DOMUtil.hasClass(dragObj, "UIGadgetModule")){
					uiTarget.parentNode.removeChild(uiTarget);					
					eXo.webui.UIDashboard.targetObj = uiTarget = null;
				}
			}
		}


	
		dragObj.onDragEnd = function(x, y, clientX, clientY){
			var uiWindow = eXo.webui.UIDashboard.portletWindow;
			if(uiWindow == null) return;
			
			var masks = eXo.core.DOMUtil.findDescendantsByClass(uiWindow, "div", "UIMask");
			for(var i=0; i<masks.length; i++){
				masks[i].style.display = "none";
//				masks[i].style.height = "0px";
			}
			
			var uiTarget = eXo.webui.UIDashboard.targetObj;
			dragObj.style.zIndex = eXo.webui.UIDashboard.currZIndex;
			eXo.webui.UIDashboard.currZIndex = null;
			dragObj.style.position = "static";
			if(eXo.core.DOMUtil.hasClass(dragObj, "Dragging"))
			eXo.core.DOMUtil.replaceClass(dragObj," Dragging","");

			var dragCopyObj = eXo.core.DOMUtil.findFirstDescendantByClass(uiWindow, "div", "CopyObj");
			if(dragCopyObj != null){
				dragCopyObj.parentNode.replaceChild(dragObj, dragCopyObj);
			}
			
			if(uiTarget!=null){	
				//if drag object is not gadget module, create an module
				if(!eXo.core.DOMUtil.hasClass(dragObj, "UIGadgetModule")){
					var innerHTML ="";
					innerHTML +=  '<div class="UIGadgetModule DragObject" id="'+dragObj.id+'-'+ new Date().getTime()+'" style="top:0px; left:0px;">';
					innerHTML +=		'<div class="GadgetMenuBar DragItem" id="">'+dragObj.id+'-'+ new Date().getTime()+'</div>';
					innerHTML +=		'<div class="GadgetContent">';
					
					innerHTML += 			'<div class="UIMask" style="display:none;"></div>'
					innerHTML += 		'</div>';
					innerHTML +=	'</div>';
					var uiGadget = eXo.core.DOMUtil.createElementNode(innerHTML, "div", "GadgetMenuBar");
					eXo.webui.UIDashboard.init(eXo.core.DOMUtil.findFirstDescendantByClass(uiGadget, "div", "GadgetMenuBar"), uiGadget);
					uiTarget.parentNode.replaceChild(uiGadget, uiTarget);
//					if(document.getElementById('UIMaskWorkspace')) ajaxGet(eXo.env.server.createPortalURL('UIPortal', 'ShowLoginForm', true)) ;
					return;
				} else {
					uiTarget.parentNode.replaceChild(dragObj, uiTarget);
				}
			}
			
			uiTarget = eXo.core.DOMUtil.findFirstDescendantByClass(uiWindow, "div", "UITarget");
			while(uiTarget!=null){
				eXo.core.DOMUtil.removeElement(uiTarget);
				uiTarget = eXo.core.DOMUtil.findFirstDescendantByClass(uiWindow, "div", "UITarget");
			}
			eXo.webui.UIDashboard.targetObj = null;
			eXo.webui.UIDashboard.currCol = null;
			eXo.webui.UIDashboard.portletWindow = null;
		}	
		
	},
	
	onLoad : function() {	
		
		var uiWorkingWS = document.getElementById("UIWorkingWorkspace");
		var dashboards = eXo.core.DOMUtil.findDescendantsByClass(uiWorkingWS, "div", "UIDashboardPortlet");

		if(dashboards.length<=0) return;

		for(var i=0; i < dashboards.length; i++){
			var dragItems = eXo.core.DOMUtil.findDescendantsByClass(dashboards[i], "div", "DragItem");
			for(var j=0; j<dragItems.length; j++) {
				eXo.webui.UIDashboard.init(dragItems[j], eXo.core.DOMUtil.findAncestorByClass(dragItems[j],"DragObject"));
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