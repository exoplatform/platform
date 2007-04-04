eXo.require('eXo.core.DragDrop');
eXo.require('eXo.portal.UIPortal');

function PortalDragDrop() { 
	this.count = 0 ;
} ;

PortalDragDrop.prototype.init = function(e) {
	var DOMUtil = eXo.core.DOMUtil ;
  var DragDrop = eXo.core.DragDrop ;

  DragDrop.initCallback = function (dndEvent) {
  	window.status = "init DND!!!" ;
  	var PortalDragDrop = eXo.portal.PortalDragDrop ;
    this.origDragObjectStyle = new eXo.core.HashMap() ;
    var dragObject = dndEvent.dragObject ;
    var properties = ["top", "left", "zIndex", "opacity", "filter", "position"] ;
    this.origDragObjectStyle.copyProperties(properties, dragObject.style) ;
   	
    PortalDragDrop.originalDragObjectTop = eXo.core.Browser.findPosY(dragObject) ;
    var originalDragObjectLeft = eXo.core.Browser.findPosX(dragObject) ;
    var originalMousePositionY = eXo.core.Browser.findMouseYInPage(e) ;
    var originalMousePositionX = eXo.core.Browser.findMouseXInPage(e) ;
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
      cloneObject.style.left = (eXo.core.Browser.findMouseXInPage(e) - 
                               PortalDragDrop.deltaXDragObjectAndMouse) + "px" ;
            
      cloneObject.style.top = (eXo.core.Browser.findMouseYInPage(e) - 
                              PortalDragDrop.deltaYDragObjectAndMouse - document.documentElement.scrollTop) + "px" ;
      cloneObject.style.opacity = 0.5 ;
      cloneObject.style.filter = "alpha(opacity=50)" ;
      cloneObject.style.width = PortalDragDrop.backupDragObjectWidth + "px" ;
    }
  }
  
  DragDrop.dragCallback = function(dndEvent) {
//  	window.status = "dndEvent: " + dndEvent ;
    var dragObject = dndEvent.dragObject ;
    /* Control Scroll */
    eXo.portal.PortalDragDrop.scrollOnDrag(dragObject, dndEvent.backupMouseEvent) ;
    
//    window.status = "foundTargetObject: " + dndEvent.foundTargetObject + "    lastFoundTargetObject: " + dndEvent.lastFoundTargetObject;
    if((dndEvent.foundTargetObject != null) && (dndEvent.lastFoundTargetObject != null)) {
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
        				        
        if(dndEvent.foundTargetObject == dndEvent.lastFoundTargetObject &&
           dndEvent.lastFoundTargetObject.foundIndex == insertPosition) {
            return ;
        }
        
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
  	window.status = "DROP CALL BACK" ;
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
    if(eXo.portal.PortalDragDrop.layoutTypeElementNode != null) {
      eXo.portal.PortalDragDrop.divRowContainerAddChild(srcElement, targetElement, targetElement.foundIndex) ;
    } else {
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
  
  ajaxGet(eXo.env.server.createPortalURL("UIPortal", "MoveChild", true, params)) ;
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

PortalDragDrop.prototype.scrollOnDrag = function(dragObject, e) {
  var dragObjectTop = eXo.core.Browser.findPosY(dragObject) ;
  var browserHeight = eXo.core.Browser.getBrowserHeight() ;
  var mouseY = eXo.core.Browser.findMouseYInClient(e) ;
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
  var csWidth = uiControlWorkspace.offsetWidth ;
  if(eXo.core.Browser.getBrowserType() == "ie") csWidth = csWidth * 2 ;

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
  var components = dndEvent.foundTargetObject.listComponentInTarget ;
  
  previewBlock.className = "DragAndDropPreview" ;
  
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
  if(dndEvent.lastFoundTargetObject.className == "UIPage") {
    uiComponentLayout = DOMUtil.findFirstDescendantByClass(dndEvent.lastFoundTargetObject, "div", "VIEW-PAGE") ;
  } else if(dndEvent.lastFoundTargetObject.className == "UIPortal") {
    uiComponentLayout = DOMUtil.findFirstDescendantByClass(dndEvent.lastFoundTargetObject, "div", "LAYOUT-PORTAL") ;
  } else {
    var foundUIComponent = new eXo.portal.UIPortalComponent(dndEvent.lastFoundTargetObject) ;
    uiComponentLayout = foundUIComponent.getLayoutBlock() ;
  }
    
  var componentIdElement = DOMUtil.getChildrenByTagName(uiComponentLayout ,"div")[0] ;
  var layoutTypeElement = DOMUtil.getChildrenByTagName(componentIdElement ,"div")[0] ;
  
//  var uiComponent = new eXo.portal.UIPortalComponent(dndEvent.lastFoundTargetObject) ;
//  var uiComponentLayout = uiComponent.getLayoutBlock() ;

  var dropHere = DOMUtil.findFirstDescendantByClass(document.body, "div", "DragAndDropPreview") ;
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
//        childTRContainer[i].style.border = "solid 1px red" ;
        if(childTRContainer[i] == eXo.portal.PortalDragDrop.parentDragObject) {
//          childTRContainer[i].style.border = "1px solid blue" ;
          childTRContainer[i].style.width = "0px" ;
        }
      }
      
    }
  }
    
  dragObject.style.border = "none" ;
};

PortalDragDrop.prototype.divRowContainerAddChild = function(insertBlock, targetElement, insertPosition) {
  var listComponent = dndEvent.foundTargetObject.listComponentInTarget ;
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
  var listComponent = dndEvent.foundTargetObject.listComponentInTarget ;
  var DOMUtil = eXo.core.DOMUtil ;
  var trContainer = DOMUtil.findFirstDescendantByClass(targetElement, "tr", "TRContainer") ;
  var tdInserted = document.createElement('td') ;
  var tdList = DOMUtil.getChildrenByTagName(trContainer, "td") ;
  var offsetWidthTR = trContainer.offsetWidth ;
  
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

eXo.portal.PortalDragDrop = new PortalDragDrop() ;
