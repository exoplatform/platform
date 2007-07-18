eXo.require('eXo.core.DragDrop');
eXo.require('eXo.portal.UIPortal');

function PortalDragDrop() { 
	this.count = 0 ;
} ;

PortalDragDrop.prototype.onLoad = function(e) {
	// Sets the ajaxGet callback function to resizeRows
	var portalEditBar = document.getElementById("UIPortalManagementEditBar");
	var wizardEditBar = document.getElementById("WizardPageEditBar");
	// Check if an EditBar exists on the page
	var editBar = (portalEditBar != null) ? portalEditBar : ((wizardEditBar != null) ? wizardEditBar : null);
	//console.dir(editBar);
	if (editBar) {
		// editBar exists only in layout mode
	  var editButtons = new Array();
	  editButtons.pushAll(editBar.getElementsByTagName("a"));
	  var controlBar = document.getElementById("UIPortalManagementControlBar");
	  if (controlBar) editButtons.pushAll(controlBar.getElementsByTagName("a"));
	  for (var i = 0; i < editButtons.length; i++) {
	  	var url = editButtons[i].href;
	  	if (url && url.indexOf("resizeRows") == -1) {
	  		// if the callback function is not already set
	  		url = url.substr(0, url.length-1).concat(", eXo.portal.PortalDragDrop.resizeRows)");
	  		editButtons[i].href = url;
	  	}
	  }
	}
};

PortalDragDrop.prototype.init = function(e) {
	var DOMUtil = eXo.core.DOMUtil ;
	var Browser = eXo.core.Browser ;
  var DragDrop = eXo.core.DragDrop ;

  DragDrop.initCallback = function (dndEvent) {
  	var PortalDragDrop = eXo.portal.PortalDragDrop ;
    this.origDragObjectStyle = new eXo.core.HashMap() ;
    var dragObject = dndEvent.dragObject ;
    var properties = ["top", "left", "zIndex", "opacity", "filter", "position"] ;
    this.origDragObjectStyle.copyProperties(properties, dragObject.style) ;
   	
    PortalDragDrop.originalDragObjectTop = Browser.findPosY(dragObject) ;
    var originalDragObjectLeft = Browser.findPosX(dragObject) ;
    var originalMousePositionY = Browser.findMouseYInPage(e) ;
    var originalMousePositionX = Browser.findMouseXInPage(e) ;
    PortalDragDrop.deltaYDragObjectAndMouse = originalMousePositionY - PortalDragDrop.originalDragObjectTop ;
    PortalDragDrop.deltaXDragObjectAndMouse = originalMousePositionX - originalDragObjectLeft ;
    
    PortalDragDrop.parentDragObject = dragObject.parentNode ;
    PortalDragDrop.backupDragObjectWidth = dragObject.offsetWidth ;
        
    PortalDragDrop.backupTopPosition = PortalDragDrop.originalDragObjectTop ;
    PortalDragDrop.backupLeftPosition = originalDragObjectLeft ;
    PortalDragDrop.backupOffsetWidth = dragObject.offsetWidth ;
    PortalDragDrop.backupOffsetHeight = dragObject.offsetHeight ;
    
    /*Case: dragObject out of UIPortal*/
    if(DOMUtil.findFirstChildByClass(dragObject, "div", "CONTROL-BLOCK") == null) {
      var cloneObject = dragObject.cloneNode(true) ;
      dragObject.parentNode.insertBefore(cloneObject, dragObject) ;
      
      dndEvent.dragObject = cloneObject ;
      
      cloneObject.style.position = "absolute" ;
      cloneObject.style.left = (Browser.findMouseXInPage(e) - 
                               PortalDragDrop.deltaXDragObjectAndMouse) + "px" ;
            
      cloneObject.style.top = (Browser.findMouseYInPage(e) - 
                              PortalDragDrop.deltaYDragObjectAndMouse - document.documentElement.scrollTop) + "px" ;
      cloneObject.style.opacity = 0.5 ;
      cloneObject.style.filter = "alpha(opacity=50)" ;
      cloneObject.style.width = PortalDragDrop.backupDragObjectWidth + "px" ;
    }
  }
  
  DragDrop.dragCallback = function(dndEvent) {
    var dragObject = dndEvent.dragObject ;
    /* Control Scroll */
    eXo.portal.PortalDragDrop.scrollOnDrag(dragObject, dndEvent) ;
//    window.status = "foundTargetObject: " + dndEvent.foundTargetObject + "    lastFoundTargetObject: " + dndEvent.lastFoundTargetObject;
    if((dndEvent.foundTargetObject) && (dndEvent.lastFoundTargetObject)) {
      /*Check and asign UIPage to uiComponentLayout when DND on UIPage*/
      var uiComponentLayout ;
      if(dndEvent.foundTargetObject.className == "UIPage") {
        uiComponentLayout = DOMUtil.findFirstDescendantByClass(dndEvent.foundTargetObject, "div", "VIEW-PAGE") ;
      } else if(dndEvent.foundTargetObject.className == "UIPortal") {
        uiComponentLayout = DOMUtil.findFirstDescendantByClass(dndEvent.foundTargetObject, "div", "LAYOUT-PORTAL") ;
      } else {
        var foundUIComponent = new eXo.portal.UIPortalComponent(dndEvent.foundTargetObject) ;
        uiComponentLayout = foundUIComponent.getLayoutBlock() ;
      }
      
      /*Set Height is auto for the empty container layout*/
      
      if(uiComponentLayout.className == "LAYOUT-CONTAINER") uiComponentLayout.style.height = "auto" ;
      
      try {
	      if(eXo.portal.PortalDragDrop.backupLastFoundTarget) {
	      	var lastFoundUIComponent = new eXo.portal.UIPortalComponent(eXo.portal.PortalDragDrop.backupLastFoundTarget);
	      	
	      	var lastFoundComponentLayout = lastFoundUIComponent.getLayoutBlock();
		      if((lastFoundComponentLayout.className == "LAYOUT-CONTAINER") && (lastFoundComponentLayout.offsetHeight < 30)) {
		      	if (eXo.core.DOMUtil.findFirstDescendantByClass(lastFoundComponentLayout, "div", "UIContainer") == null) {
		      		lastFoundComponentLayout.style.height = "60px" ;
		      	}
		      }
	      }
      } catch(err) {
      	//window.status = err.toString() ;
      }
     
      /*################################################################################*/
      
      dndEvent.foundTargetObject.uiComponentLayoutType = uiComponentLayout ;
      
      var componentIdElement = DOMUtil.getChildrenByTagName(uiComponentLayout, "div")[0] ;
      var layoutTypeElement = DOMUtil.getChildrenByTagName(componentIdElement, "div")[0] ;
      eXo.portal.PortalDragDrop.layoutTypeElementNode = layoutTypeElement ;
      
      if(layoutTypeElement != null) {
        /* ===============================CASE ROW LAYOUT================================ */
        var rowContainer = DOMUtil.findFirstDescendantByClass(uiComponentLayout, "div", "UIRowContainer") ;
	      var childRowContainer = DOMUtil.getChildrenByTagName(rowContainer, "div") ;
        
        var listComponent = new Array() ;
        for(var i = 0; i < childRowContainer.length; i++) {
          if((childRowContainer[i].className != "DragAndDropPreview") && (childRowContainer[i] != dragObject)) {
            listComponent.push(childRowContainer[i]) ;
          }
        }
        
        dndEvent.foundTargetObject.listComponentInTarget = listComponent ;
        /*Set properties for drag object */
        eXo.portal.PortalDragDrop.setDragObjectProperties(dragObject, childRowContainer, "row", dndEvent.backupMouseEvent) ;
        
        var insertPosition = eXo.portal.PortalDragDrop.findInsertPosition(listComponent, dragObject, "row") ;
        				        
//        if(dndEvent.foundTargetObject == dndEvent.lastFoundTargetObject &&
//           dndEvent.lastFoundTargetObject.foundIndex == insertPosition) {
//            return ;
//        }
        dndEvent.foundTargetObject.foundIndex = insertPosition ;
        
        /*Undo preview */
        if(dndEvent.lastFoundTargetObject != null) {
          eXo.portal.PortalDragDrop.backupLastFoundTarget = dndEvent.lastFoundTargetObject ;
          eXo.portal.PortalDragDrop.undoPreview(dndEvent) ;
        }
        
        /* Insert preview block */
        if(insertPosition >= 0) {
          rowContainer.insertBefore(eXo.portal.PortalDragDrop.createPreview("row"), listComponent[insertPosition]) ;
        } else {
          rowContainer.appendChild(eXo.portal.PortalDragDrop.createPreview("row")) ;
        }

      } else {
        /* ===============================CASE COLUMN LAYOUT================================ */
        var columnContainer = DOMUtil.findFirstDescendantByClass(uiComponentLayout, "table", "UITableColumnContainer") ;
        var trContainer = DOMUtil.findFirstDescendantByClass(uiComponentLayout, "tr", "TRContainer") ;
        var tdElementList = DOMUtil.getChildrenByTagName(trContainer, "td") ;
        
        var listComponent = new Array() ;
        for(var i = 0; i < tdElementList.length; i++) {
          if(DOMUtil.hasDescendantClass(uiComponentLayout, "DragAndDropPreview")) {
            var previewBlock = DOMUtil.findFirstDescendantByClass(trContainer, "div", "DragAndDropPreview") ;
            if((tdElementList[i] != previewBlock.parentNode) && (tdElementList[i] != dragObject.parentNode)) {
              listComponent.push(tdElementList[i]) ;
            }
          } else {
            listComponent.push(tdElementList[i]) ;
          }          
        }
        
        dndEvent.foundTargetObject.listComponentInTarget = listComponent ;
        /*Find Insert Position */
        var insertPosition = eXo.portal.PortalDragDrop.findInsertPosition(listComponent, dragObject, "column") ;
        
        if(dndEvent.foundTargetObject == dndEvent.lastFoundTargetObject &&
          dndEvent.lastFoundTargetObject.foundIndex == insertPosition) {
            return ;
        }
        
        dndEvent.foundTargetObject.foundIndex = insertPosition ;
        /*Undo preview */
        if(dndEvent.lastFoundTargetObject != null) {
          eXo.portal.PortalDragDrop.undoPreview(dndEvent) ;
        }
        
        /* Insert preview block */
        eXo.portal.PortalDragDrop.tdInserted = document.createElement('td') ;
        eXo.portal.PortalDragDrop.tdInserted .appendChild(eXo.portal.PortalDragDrop.createPreview("column")) ;
        if(insertPosition >= 0) {
          trContainer.insertBefore(eXo.portal.PortalDragDrop.tdInserted, listComponent[insertPosition]) ;
        } else {
          trContainer.appendChild(eXo.portal.PortalDragDrop.tdInserted) ;
        }
        /*Set properties for drag object */
        eXo.portal.PortalDragDrop.setDragObjectProperties(dragObject, tdElementList, "column", dndEvent.backupMouseEvent) ;
      }      
    } 
  } ;

  DragDrop.dropCallback = function(dndEvent) {
  	this.origDragObjectStyle.setProperties(dndEvent.dragObject.style, false) ;
    if(dndEvent.foundTargetObject != null) {
      eXo.portal.PortalDragDrop.doDropCallback(dndEvent) ;
    } else {
      if(DOMUtil.findFirstChildByClass(dndEvent.dragObject, "div", "CONTROL-BLOCK") == null) {
        dndEvent.dragObject.parentNode.removeChild(dndEvent.dragObject) ;
      }
      dndEvent.foundTargetObject = eXo.portal.PortalDragDrop.backupLastFoundTarget ;
      eXo.portal.PortalDragDrop.doDropCallback(dndEvent) ;
    }
  }
  
  var clickObject = this ;
  var controlBlock = DOMUtil.findAncestorByClass(clickObject, "CONTROL-BLOCK") ;
  
  if(controlBlock != null) {
    var dragBlock = eXo.portal.UIPortal.findUIComponentOf(controlBlock) ;
    DragDrop.init(eXo.portal.PortalDragDrop.findDropableTargets(), clickObject, dragBlock, e) ;
  } else if(DOMUtil.findAncestorByClass(clickObject, "DragObjectPortlet")) {
  	var dragBlock = DOMUtil.findAncestorByClass(clickObject, "DragObjectPortlet") ;
  	DragDrop.init(eXo.portal.PortalDragDrop.findDropableTargets(), clickObject, dragBlock, e) ;
	} else {
    DragDrop.init(eXo.portal.PortalDragDrop.findDropableTargets(), clickObject, clickObject, e) ;
  }
};

PortalDragDrop.prototype.doDropCallback = function(dndEvent) {
	if(!dndEvent.lastFoundTargetObject) {
		dndEvent.lastFoundTargetObject = eXo.portal.PortalDragDrop.backupLastFoundTarget ;
	}

	eXo.portal.PortalDragDrop.undoPreview(dndEvent) ;
	
	var srcElement = dndEvent.dragObject ;
      
  var targetElement = dndEvent.foundTargetObject ;
  
  var newComponent = false;
  if(eXo.core.DOMUtil.hasDescendantClass(srcElement, "DragControlArea") && (targetElement.foundIndex != null)) {
//  	alert("My Test: " + eXo.portal.PortalDragDrop.layoutTypeElementNode);
    if(eXo.portal.PortalDragDrop.layoutTypeElementNode != null) {
      eXo.portal.PortalDragDrop.divRowContainerAddChild(srcElement, targetElement, targetElement.foundIndex) ;
    } else {
//    	alert("Table is OK");
      eXo.portal.PortalDragDrop.tableColumnContainerAddChild(srcElement, targetElement, targetElement.foundIndex) ;
    }
  }
  
  if(eXo.core.DOMUtil.findFirstChildByClass(dndEvent.dragObject, "div", "CONTROL-BLOCK") == null) {
    dndEvent.dragObject.parentNode.removeChild(dndEvent.dragObject) ;
    newComponent = true;
  }

  var params = [
    {name: "srcID", value: srcElement.id},
    {name: "targetID", value: targetElement.id},
    {name: "insertPosition", value: targetElement.foundIndex},
    {name: "newComponent", value: newComponent}
  ] ;
  
  try {
    dndEvent.lastFoundTargetObject.foundIndex = -1;
  } catch(err) {
  	
  }
	// Modified by Philippe : added callback function
  ajaxGet(eXo.env.server.createPortalURL("UIPortal", "MoveChild", true, params), eXo.portal.PortalDragDrop.resizeRows) ;
};

/* Find components in dropable target */
PortalDragDrop.prototype.findDropableTargets = function() {
  var dropableTargets = new Array() ;
  var uiWorkingWorkspace = document.getElementById("UIWorkingWorkspace") ;
  var uiPortal = eXo.core.DOMUtil.findFirstDescendantByClass(uiWorkingWorkspace, "div", "UIPortal") ;
  var uiPage = eXo.core.DOMUtil.findFirstDescendantByClass(uiWorkingWorkspace, "div", "UIPage") ;
  var viewPage = eXo.core.DOMUtil.findFirstDescendantByClass(uiPage, "div", "VIEW-PAGE") ;
  var uiContainers = eXo.core.DOMUtil.findDescendantsByClass(uiWorkingWorkspace, "div", "UIContainer") ;
  if(viewPage.style.display == "none") {
    dropableTargets.push(uiPortal) ;
  } else {
    dropableTargets.push(uiPage) ;
  }
  for(var i = 0; i < uiContainers.length; i++) {
    dropableTargets.push(uiContainers[i]) ;
  }
   return dropableTargets ;
};

PortalDragDrop.prototype.scrollOnDrag = function(dragObject, dndEvent) {
  var dragObjectTop = eXo.core.Browser.findPosY(dragObject) ;
  var browserHeight = eXo.core.Browser.getBrowserHeight() ;
  var mouseY = eXo.core.Browser.findMouseYInClient(dndEvent.backupMouseEvent) ;
  var deltaTopMouse = eXo.core.Browser.findMouseYInPage(dndEvent.backupMouseEvent) - mouseY ;
  var deltaTop = mouseY - (Math.round(browserHeight * 5/6)) ;
  var deltaBottom = mouseY - (Math.round(browserHeight/6)) ;
  if(deltaTop > 0) {
    document.documentElement.scrollTop += deltaTop - 5 ;
  }
  
  if(deltaBottom < 0 && document.documentElement.scrollTop > 0) {
    document.documentElement.scrollTop += deltaBottom ;
  }
};

PortalDragDrop.prototype.findInsertPosition = function(components, dragObject, layout) {
  var dragObjectX = eXo.core.Browser.findPosX(dragObject) ;
  
  if(layout == "row") {
    for(var i = 0; i < components.length; i++) {
      var componentTop = eXo.core.Browser.findPosY(components[i]) ;
      var dragObjectTop = eXo.core.Browser.findPosY(dragObject) ;
      var componentMiddle = componentTop + Math.round(components[i].offsetHeight / 2) ;
            
      if(dragObjectTop > componentMiddle) continue ;
      else return i;
    }
    return -1 ;
    
  } else {
    for(var i = 0; i < components.length; i++) {
      var componentInTD = eXo.core.DOMUtil.getChildrenByTagName(components[i] ,"div")[0] ;    	
      var componentX = eXo.core.Browser.findPosX(components[i]) ;
      
      if(dragObjectX > componentX) continue ;
      else return i ;
    }
    return -1 ;
  }  
};

PortalDragDrop.prototype.setDragObjectProperties = function(dragObject, listComponent, layout, e) {
  var uiControlWorkspace = document.getElementById("UIControlWorkspace") ;
  var uiPage = eXo.core.DOMUtil.findAncestorByClass(dragObject, "UIPage");
  var csWidth = uiControlWorkspace.offsetWidth ;
  
  /* IE's Bug: It always double when set position, margin-left for 
   * UIWorkingWorkspace is problem.
   * If WorkingWorkspace is setted a width, that bug disappear
   * but the layout on IE has breakdown!!!
   * */
  if(eXo.core.Browser.getBrowserType() == "ie" && (uiPage == null)) csWidth = csWidth * 2 ;

  dragObject.style.position = "absolute" ;
  if(eXo.core.DOMUtil.findFirstChildByClass(dragObject, "div", "CONTROL-BLOCK") == null) {
    dragObject.style.top = (eXo.core.Browser.findMouseYInPage(e) - 
                            eXo.portal.PortalDragDrop.deltaYDragObjectAndMouse - document.documentElement.scrollTop) + "px" ;
    dragObject.style.left = (eXo.core.Browser.findMouseXInPage(e) -
                              eXo.portal.PortalDragDrop.deltaXDragObjectAndMouse) + "px" ;
  } else {
    dragObject.style.top = (eXo.core.Browser.findMouseYInPage(e) - 
                            eXo.portal.PortalDragDrop.deltaYDragObjectAndMouse) + "px" ;
    dragObject.style.left = (eXo.core.Browser.findMouseXInPage(e) - csWidth -
                             eXo.portal.PortalDragDrop.deltaXDragObjectAndMouse) + "px" ;
  }
    
  if((listComponent.length > 0) && (eXo.core.DOMUtil.findFirstChildByClass(dragObject, "div", "CONTROL-BLOCK") != null)) {
    /*Set dragObject's width equal component in target */
    if(layout == "row") {
      for(var i = 0; i < listComponent.length; i++) {
        if(listComponent[i] != dragObject) {
          dragObject.style.width = listComponent[i].offsetWidth + "px" ;
        }
      }
    } else {
      if(listComponent.length > 0) {
        dragObject.style.width = eXo.portal.PortalDragDrop.widthComponentInTarget + "px" ;
        var parentNodeDragObject = eXo.core.DOMUtil.findAncestorByClass(dragObject, "TRContainer") ;
        if(parentNodeDragObject != null) {
          dragObject.style.width = eXo.portal.PortalDragDrop.backupDragObjectWidth + "px" ;
        }
      }
    }
  }
};

PortalDragDrop.prototype.createPreview = function(layoutType) {
  var previewBlock = document.createElement("div") ;
  var components = eXo.core.DragDrop.dndEvent.foundTargetObject.listComponentInTarget ;
  
  previewBlock.className = "DragAndDropPreview" ;
  previewBlock.id = "DragAndDropPreview" ;
  
  if((layoutType == "column") && (components.length > 0)) {
    var offsetWidthTR = components[0].parentNode.offsetWidth ;
    var widthComponent = 0 ;
    
    if(eXo.core.DOMUtil.findAncestorByClass(eXo.portal.PortalDragDrop.parentDragObject, "TRContainer") != null) {
      widthComponent = Math.round(offsetWidthTR / (components.length)) - 1 ;
    } else {
      widthComponent = Math.round(offsetWidthTR / (components.length + 1)) - 1 ;
    }
    
    eXo.portal.PortalDragDrop.tdInserted.style.width = widthComponent + "px" ;
    
    for(var i = 0 ; i < components.length; i++) {
      if(components[i] == eXo.portal.PortalDragDrop.parentDragObject) {
        components[i].style.width = "0px" ;
      } else {
        components[i].style.width = widthComponent + "px" ;
      }
    }
    
    eXo.portal.PortalDragDrop.widthComponentInTarget = widthComponent ;
  }
  return previewBlock ;
};

PortalDragDrop.prototype.undoPreview = function(dndEvent) {
	var DOMUtil = eXo.core.DOMUtil ;
  var uiComponentLayout ;
  try{
  if(dndEvent.lastFoundTargetObject.className == "UIPage") {
    uiComponentLayout = DOMUtil.findFirstDescendantByClass(dndEvent.lastFoundTargetObject, "div", "VIEW-PAGE") ;
  } else if(dndEvent.lastFoundTargetObject.className == "UIPortal") {
    uiComponentLayout = DOMUtil.findFirstDescendantByClass(dndEvent.lastFoundTargetObject, "div", "LAYOUT-PORTAL") ;
  } else {
    var foundUIComponent = new eXo.portal.UIPortalComponent(dndEvent.lastFoundTargetObject) ;
    uiComponentLayout = foundUIComponent.getLayoutBlock() ;
  }
  }catch(e) {}  
  var componentIdElement = DOMUtil.getChildrenByTagName(uiComponentLayout ,"div")[0] ;
  var layoutTypeElement = DOMUtil.getChildrenByTagName(componentIdElement ,"div")[0] ;
  
//  var uiComponent = new eXo.portal.UIPortalComponent(dndEvent.lastFoundTargetObject) ;
//  var uiComponentLayout = uiComponent.getLayoutBlock() ;

  var dropHere = document.getElementById("DragAndDropPreview") ;
  var dragObject = dndEvent.dragObject ;
	
  if(dropHere != null) {
    if(layoutTypeElement != null) {
      dropHere.parentNode.removeChild(dropHere) ;
    } else {
      var tableLayoutElement = DOMUtil.getChildrenByTagName(componentIdElement ,"table")[0] ;
      var trContainer = DOMUtil.findFirstDescendantByClass(tableLayoutElement, "tr", "TRContainer") ;
      
      trContainer.removeChild(dropHere.parentNode) ;
      childTRContainer = DOMUtil.getChildrenByTagName(trContainer, "td") ;
      
      for(var i = 0; i < childTRContainer.length; i++) {
        if(childTRContainer[i] == eXo.portal.PortalDragDrop.parentDragObject) {
          childTRContainer[i].style.width = "0px" ;
        }
      }
      
    }
  }
  dragObject.style.border = "none" ;
};

PortalDragDrop.prototype.divRowContainerAddChild = function(insertBlock, targetElement, insertPosition) {
  var listComponent = eXo.core.DragDrop.dndEvent.foundTargetObject.listComponentInTarget ;
  var uiRowContainer = eXo.core.DOMUtil.findFirstDescendantByClass(targetElement, "div", "UIRowContainer") ;
  insertBlock.style.width = "auto" ;
  
  if(insertPosition >= 0) {
    uiRowContainer.insertBefore(insertBlock, listComponent[insertPosition]) ;
  } else {
    uiRowContainer.appendChild(insertBlock) ;
  }
  
  if(eXo.portal.PortalDragDrop.parentDragObject.parentNode.className == "TRContainer") {
    eXo.portal.PortalDragDrop.parentDragObject.parentNode.removeChild(eXo.portal.PortalDragDrop.parentDragObject) ;
  }
};

PortalDragDrop.prototype.tableColumnContainerAddChild = function(insertBlock, targetElement, insertPosition) {
  var listComponent = eXo.core.DragDrop.dndEvent.foundTargetObject.listComponentInTarget ;
  var DOMUtil = eXo.core.DOMUtil ;
  var trContainer = DOMUtil.findFirstDescendantByClass(targetElement, "tr", "TRContainer") ;
  var tdInserted = document.createElement('td') ;
  
  var checkTRContainerInsertBlock = DOMUtil.findAncestorByClass(insertBlock, "TRContainer") ;
      
  tdInserted.style.width = eXo.portal.PortalDragDrop.widthComponentInTarget + "px" ;
  
  tdInserted.appendChild(insertBlock) ;
  if(insertPosition >= 0) {
    trContainer.insertBefore(tdInserted, listComponent[insertPosition]) ;
  } else {
    trContainer.appendChild(tdInserted) ;
  }

  insertBlock.style.width = "auto" ;
  
  if(checkTRContainerInsertBlock) {
    trContainer.removeChild(eXo.portal.PortalDragDrop.parentDragObject) ;
  }
};
// Function resizeRows added by Philippe
PortalDragDrop.prototype.resizeRows = function() {
	var uiWS = document.getElementById("UIWorkingWorkspace");
	if (uiWS) {
		var actionButtons = new Array();
		var uiContainers = eXo.core.DOMUtil.findDescendantsByClass(uiWS, "div", "UIContainer");
		for (var i = 0; i < uiContainers.length; i++) {
			var uiContainer = uiContainers[i];
			actionButtons.pushAll(uiContainer.getElementsByTagName("a"));
			var trContainer = eXo.core.DOMUtil.findFirstDescendantByClass(uiContainer, "tr", "TRContainer");
			if (trContainer) {
				var tdList = eXo.core.DOMUtil.getChildrenByTagName(trContainer, "td") ;
			  var offsetWidthTR = trContainer.offsetWidth ;
			  var tdWidth = offsetWidthTR / tdList.length;
			  for (var j = 0; j < tdList.length; j++) {
			  	var td = tdList[j];
					td.style.width = tdWidth + "px";
			  }
			}
			var layoutContainers = eXo.core.DOMUtil.findDescendantsByClass(uiContainer, "div", "LAYOUT-CONTAINER");
			for (var j = 0; j < layoutContainers.length; j++) {
				var layoutContainer = layoutContainers[j];
				layoutContainer.style.height = "auto";
				var portletChild = eXo.core.DOMUtil.findFirstDescendantByClass(layoutContainer, "div", "UIPortlet");
				var containerChild = eXo.core.DOMUtil.findFirstDescendantByClass(layoutContainer, "div", "UIContainer")
				 if (portletChild == null && containerChild == null) {
	      		layoutContainer.style.height = "60px" ;
	      	}
			}
		}
	  for (var k = 0; k < actionButtons.length; k++) {
	  	var url = actionButtons[k].href;
	  	if (url && url.indexOf("resizeRows") == -1) {
	  		url = url.substr(0, url.length-1).concat(", eXo.portal.PortalDragDrop.resizeRows)");
	  		actionButtons[k].href = url;
	  	}
	  }
	}
};

PortalDragDrop.prototype.fixCss =  function() {
	var DOMUtil = eXo.core.DOMUtil ;
 	uiPortal = document.getElementById("UIPortal-UIPortal") ;
 	if(uiPortal) {
	  parentByClass = DOMUtil.findAncestorByClass(uiPortal, "Vista") ;
	  if(parentByClass) {
	  	layoutPortal = DOMUtil.findFirstDescendantByClass(uiPortal, "div", "LAYOUT-PORTAL") ;
	  	viewPortal = DOMUtil.findFirstDescendantByClass(uiPortal, "div", "VIEW-PORTAL") ;
	  	uiRowContainer = DOMUtil.findFirstDescendantByClass(uiPortal, "div", "UIRowContainer") ;
	  	if(uiRowContainer != null) { 
	  		if(layoutPortal.style.display == "block" || viewPortal.style.display == "none") {
	  		  uiPortal.style.padding = "8px 0px 0px 0px" ;
	  		} else {
	  			uiPortal.style.padding = "0px" ;
	  		}
	  	} 
	  }
 	}
} ;

eXo.portal.PortalDragDrop = new PortalDragDrop() ;
