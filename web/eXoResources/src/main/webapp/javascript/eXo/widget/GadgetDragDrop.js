var DOMUtil = eXo.core.DOMUtil;
var Browser = eXo.core.Browser;
var DragDrop2 = eXo.core.DragDrop2;
eXo.widget.GadgetDragDrop = {
	init : function (gadgetMenubar, gadgetItem) {

		DragDrop2.init(gadgetMenubar, gadgetItem)	;

		gadgetItem.onDragStart = function(x, y, lastMouseX, lastMouseY, e){
			var uiCol = DOMUtil.findAncestorByClass(gadgetItem, "UIColumn");
	
			var ggwidth = gadgetItem.offsetWidth - parseInt(DOMUtil.getStyle(gadgetItem,"borderLeftWidth"))
											- parseInt(DOMUtil.getStyle(gadgetItem,"borderRightWidth"));
			var ggheight = gadgetItem.offsetHeight - parseInt(DOMUtil.getStyle(gadgetItem,"borderTopWidth"))
											- parseInt(DOMUtil.getStyle(gadgetItem,"borderBottomWidth"));
			
			var uiTarget = document.createElement("div");
			
			uiTarget.id = "UITarget";
			uiTarget.className = "UITarget";
			
			uiTarget.style.width = ggwidth + "px";
			uiTarget.style.height = ggheight + "px";
			
			DOMUtil.addClass(gadgetItem, "Dragging")
			gadgetItem.style.position = "absolute";
			gadgetItem.style.width = ggwidth + "px";
			
			uiCol.insertBefore(uiTarget,gadgetItem);
			
			var uiWindow = DOMUtil.findAncestorByClass(uiCol, "UIWindow");
			var uiWorkspace = DOMUtil.findAncestorByClass(uiCol, "UIWorkingWorkspace");

			var mx = Browser.findMouseRelativeX(uiWorkspace, e);
			var ox = Browser.findMouseRelativeX(uiTarget, e);
			var x = mx-ox;
			if(Browser.getBrowserType()=="ie" && DOMUtil.findAncestorByClass(uiWindow, "UIPageDesktop")==null)
				x -= (uiWorkspace.offsetLeft);
				
			var my = Browser.findMouseRelativeY(uiWorkspace, e);
			var oy = Browser.findMouseRelativeY(uiTarget, e);
			var y = my-oy;
			
			Browser.setPositionInContainer(uiWorkspace, gadgetItem, x, y);
		}
		
		gadgetItem.onDrag = function(nx, ny, ex, ey, e){			
			var uiCol = DOMUtil.findAncestorByClass(gadgetItem, "UIColumn");
			var uiTarget = DOMUtil.findFirstDescendantByClass(uiCol, "div", "UITarget");
			var uiWorkspace = DOMUtil.findAncestorByClass(uiCol, "UIWorkingWorkspace");
			
			if(DragDrop2.isIn(ex, ey, uiCol)){
				var gadgets = DOMUtil.findDescendantsByClass(uiCol, "div", "UIGadget");
				
				for(var i=0; i<gadgets.length; i++){
					if(gadgets[i].id==gadgetItem.id) continue;
					if(DragDrop2.isIn(ex, ey, gadgets[i])){
						var menuBar = DOMUtil.findFirstChildByClass(gadgets[i], "div", "GadgetMenuBar");
						if(DragDrop2.isIn(ex, ey, menuBar))
							uiCol.insertBefore(uiTarget,gadgets[i]);
						else
							uiCol.insertBefore(uiTarget,gadgets[i].nextSibling);
						break;
					}
				}
			}else{
				var colsContainer = DOMUtil.findAncestorByClass(uiCol, "UIColumns");
				var cols = DOMUtil.findDescendantsByClass(colsContainer, "div", "UIColumn");
				
				for(var i=0; i<cols.length; i++){
					if(DragDrop2.isIn(ex, ey, cols[i])){
						DOMUtil.moveElemt(uiTarget, cols[i]);
						DOMUtil.moveElemt(gadgetItem, cols[i]);
						break;
					}
				}
			}
		}
		
		gadgetItem.onDragEnd = function(x, y, clientX, clientY){
			var uiCol = DOMUtil.findAncestorByClass(gadgetItem, "UIColumn");
			var uiTarget = DOMUtil.findFirstDescendantByClass(uiCol, "div", "UITarget");
			
			gadgetItem.style.position = "static";
			gadgetItem.style.width = "auto";
			gadgetItem.style.height = "auto";
			DOMUtil.replaceClass(gadgetItem,"Dragging","")
			if(uiTarget != null){
				uiCol.replaceChild(gadgetItem,uiTarget);
			}
		}
		
	},
	
	copyStyle : function(srcElement, desElement, styles){
		for(var i=0; i<styles.length; i++){
			desElement.style[styles[i]] = DOMUtil.getStyle(srcElement, styles[i]);
		}		
	}
	
}

eXo.widget.GadgetDragDrop.onLoad = function() {	
	var gadgetsCont = document.getElementById("GadgetsContainer");
	var gadgetMenubars = DOMUtil.findDescendantsByClass(gadgetsCont,"div","GadgetMenuBar");
	
	for(var i=0; i<gadgetMenubars.length; i++) {
		eXo.widget.GadgetDragDrop.init(gadgetMenubars[i], gadgetMenubars[i].parentNode);
	}
}