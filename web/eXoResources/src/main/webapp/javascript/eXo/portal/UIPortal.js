function UIComponent(node) {
  this.node = node ;
  if(node) this.type = node.className ;
  componentBlock = eXo.core.DOMUtil.findFirstDescendantByClass(node, "div", "UIComponentBlock");
  var children =  eXo.core.DOMUtil.getChildrenByTagName(componentBlock, "div") ;
  if(children.length > 0) {
	  this.metaData =  children[0] ;
	  this.control = children[3] ; 
	  this.layout = children[1] ; 
	  this.view = children[2] ;
  } 
	
//	this.metaData = eXo.core.DOMUtil.findFirstChildByClass(componentBlock, "div", "META-DATA-BLOCK");
//	this.control = eXo.core.DOMUtil.findFirstChildByClass(componentBlock, "div", "CONTROL-BLOCK");
//	this.layout = eXo.core.DOMUtil.findFirstChildByClass(componentBlock, "div", "LAYOUT-BLOCK");
//	this.view = eXo.core.DOMUtil.findFirstChildByClass(componentBlock, "div", "VIEW-BLOCK");
	
  this.component = "";
  
  var div = eXo.core.DOMUtil.getChildrenByTagName(this.metaData, "div");
  if(div.length > 0) {
  	this.id = div[0].firstChild.nodeValue ;
  	this.title = div[1].firstChild.nodeValue ;
  }
	//minh.js.exo
 //bug PORTAL-1161.
	//this.description = div[2].firstChild.nodeValue ;
};
//UIComponent.prototype.getDescription = function() { return this.description ; };

UIComponent.prototype.getId = function() { return this.id ; };
UIComponent.prototype.getTitle = function() { return this.title ; };
UIComponent.prototype.getElement = function() { return this.node ; };
UIComponent.prototype.getUIComponentType = function() { return this.type ; };

UIComponent.prototype.getUIComponentBlock = function() { return this.node ; };
UIComponent.prototype.getControlBlock = function() { return this.control ; };
UIComponent.prototype.getLayoutBlock = function() { return this.layout ; };
UIComponent.prototype.getViewBlock = function() { return this.view ; };

/*******************************************************************************/

function UIPortal() {
  this.portalUIComponentDragDrop = false;
};

UIPortal.prototype.blockOnMouseOver = function(event, portlet, isOver) {
  var DOMUtil = eXo.core.DOMUtil;
  if(!eXo.env.isEditting) return;
	if(eXo.env.editType && DOMUtil.hasClass(portlet, "UIContainer")) return;
	else if(!eXo.env.editType && DOMUtil.hasClass(portlet, "UIPortlet")) return;
	
	if(!event) event = window.event;
	event.cancelBubble = true;
	
  var component = DOMUtil.findFirstDescendantByClass(portlet, "div", "UIComponentBlock");
  var children = DOMUtil.getChildrenByTagName(component, "div");
  var layoutBlock = children[1];
  var viewBlock = children[2];
  var editBlock = children[3];
  
  if(!editBlock) return;
	if(isOver) {
		var newLayer = DOMUtil.findFirstDescendantByClass(editBlock, "div", "NewLayer");
		if(newLayer) {
			var height = 0; var width = 0;
			if(layoutBlock && layoutBlock.style.display != "none") {
				height = layoutBlock.offsetHeight;
				width = layoutBlock.offsetWidth;
			} else if(viewBlock && viewBlock.style.display != "none") {
				height = viewBlock.offsetHeight;
        width = viewBlock.offsetWidth;
			}
			newLayer.style.width = width + "px";
			newLayer.style.height = height + "px";
			newLayer.parentNode.style.top = -height + "px";
		}
		editBlock.style.display = "block";
	}
	else editBlock.style.display = "none";
}

UIPortal.prototype.getUIPortlets = function() {
  var uiWorkingWorkspace = document.getElementById("UIWorkingWorkspace") ;
  var founds =  eXo.core.DOMUtil.findDescendantsByClass(uiWorkingWorkspace, "div", "UIPortlet") ;
  var components =  new Array() ;
  for(j = 0; j < founds.length; j++) {
    components[components.length] = new UIComponent(founds[j]) ;
  }
  return components ;
} ;

UIPortal.prototype.getUIPortletsInUIPortal = function() {
  var uiWorkingWorkspace = document.getElementById("UIWorkingWorkspace") ;
  var founds =  eXo.core.DOMUtil.findDescendantsByClass(uiWorkingWorkspace, "div", "UIPortlet") ;
  var components =  new Array() ;
  for(var j = 0; j < founds.length; j++) {
    if(eXo.core.DOMUtil.findAncestorByClass(founds[j], 'UIPage') == null) {
      components[components.length] = new UIComponent(founds[j]) ;
    }
  }
  return components ;
} ;

UIPortal.prototype.getUIPortletsInUIPage = function() {
  var uiPage = document.getElementById("UIPage") ;
  var founds =  eXo.core.DOMUtil.findDescendantsByClass(uiPage, "div", "UIPortlet");
  var components =  new Array() ;
  for(var j = 0; j < founds.length; j++) {
    components[components.length] = new UIComponent(founds[j]) ;
  }
  return components ;
} ;

UIPortal.prototype.getUIContainers = function() {
  var uiWorkingWorkspace = document.getElementById("UIWorkingWorkspace") ;
  var  founds = eXo.core.DOMUtil.findDescendantsByClass(uiWorkingWorkspace, "div", "UIContainer");
  var components =  new Array() ;
  for(var j = 0; j < founds.length; j++) {
    components[j] = new UIComponent(founds[j]) ;
  }
  return components ;
};

UIPortal.prototype.getUIPageBody = function() {
//  var uiPortal = document.getElementById("UIPortal") ;
//  return new UIComponent(eXo.core.DOMUtil.findFirstDescendantByClass(uiPortal, "div", "UIPage")) ;
	return new UIComponent(document.getElementById("UIPageBody")) ;
};

UIPortal.prototype.getUIPortal = function() {
  var uiWorkingWorkspace = document.getElementById("UIWorkingWorkspace") ;
  return new UIComponent(eXo.core.DOMUtil.findFirstDescendantByClass(uiWorkingWorkspace, "div", "UIPortal"));
};

UIPortal.prototype.switchViewModeToLayoutMode = function(uicomponent, swapContent) {
  var layoutBlock = uicomponent.getLayoutBlock() ;
  if(!layoutBlock || layoutBlock.style.display == 'block') return ;
  var viewBlock = uicomponent.getViewBlock() ;
  if(swapContent) {
    var contentNode = eXo.core.DOMUtil.findDescendantById(viewBlock, uicomponent.getId()) ;
    if(contentNode != null) {
      layoutBlock.appendChild(contentNode) ;
    }
  }
  
  try {
	  viewBlock.style.display = "none" ;
  	layoutBlock.style.display = "block" ;
  } catch (err) {
  	
  }
};

UIPortal.prototype.switchLayoutModeToViewMode = function(uicomponent, swapContent) {
  var viewBlock =  uicomponent.getViewBlock() ;
  if(!viewBlock || viewBlock.style.display == 'block') return ;
  var layoutBlock = uicomponent.getLayoutBlock() ;
  if(swapContent) {
    var contentNode = eXo.core.DOMUtil.findDescendantById(layoutBlock, uicomponent.getId()) ;
    if(contentNode != null) {
    	viewBlock.innerHTML = "";
      viewBlock.appendChild(contentNode) ;
    }
  }
  viewBlock.style.display = "block" ;
  layoutBlock.style.display = "none" ;
} ;

UIPortal.prototype.switchMode = function(elemtClicked) {
	if(elemtClicked.className == "Icon PreviewIcon") {
		elemtClicked.className = "Icon LayoutModeIcon" ;
		this.showViewMode() ;
		this.showMaskLayer() ;
//		eXo.core.Browser.onScrollCallback("", eXo.portal.UIPortal.showMaskLayer()) ;
	} else {
		this.hideMaskLayer() ;
		elemtClicked.className = "Icon PreviewIcon" ;
		this.showLayoutModeForPortal() ;
	}
	/*
	* minh.js.exo
	* fix bug portal 1757;
	*/
	//eXo.portal.PortalDragDrop.fixCss();
} ;

UIPortal.prototype.switchPortalMode = function(elemtClicked) {
	eXo.env.isBlockEditMode = !eXo.env.isBlockEditMode;
	if(!eXo.env.isBlockEditMode) {
		this.showViewMode() ;
	} else {
		this.showLayoutModeForPortal() ;
	}
	var url = eXo.env.server.createPortalURL(this.getUIPortal().id, "ChangePortalEditMode", true, [{name :"isBlockMode",value: eXo.env.isBlockEditMode}]);
	ajaxAsyncGetRequest(url);
};

UIPortal.prototype.switchModeForPage = function(elemtClicked) {
	var layoutMode  = this.showViewLayoutModeForPage();
	if(layoutMode == 1) {
		elemtClicked.className = "Icon PagePreviewIcon" ;
		this.hideMaskLayer() ;
	} else if(layoutMode == 0) {
		elemtClicked.className = "Icon PageLayoutModeIcon" ;
		this.showMaskLayer() ;
	}
} ;

UIPortal.prototype.showUIComponentControl = function(uicomponent, flag) {
  var controlBlock = uicomponent.getControlBlock() ;
  if(!controlBlock) return ;
  var clickObject = eXo.core.DOMUtil.findFirstDescendantByClass(controlBlock, "div", "DragControlArea") ;
  if(flag) {
    clickObject.onmousedown = eXo.portal.PortalDragDrop.init ;
//    controlBlock.style.display = 'block' ;
  } else {
    controlBlock.onmousedown = null ;
//    controlBlock.style.display = 'none' ;
  }
};

UIPortal.prototype.showViewLayoutModeForPage = function() {
	/*
	 * minh.js.exo;
	 */
  var layoutMode = -1;
  var container = this.getUIContainers() ;
  var portlet = this.getUIPortletsInUIPage() ;
  
  if(portlet.length > 0 && container.length >= 0) {
	
	  for(var i = 0; i < container.length; i++) {
	  	var viewBlock = container[i].getViewBlock() ;  
	    if(viewBlock.style.display == 'block') {
	    	this.switchViewModeToLayoutMode(container[i], true) ;
	      this.showUIComponentControl(container[i], this.component == 'UIContainer') ;
	    	if(layoutMode == -1) layoutMode = 1;
	    } else if(viewBlock.style.display == 'none') {
	    	this.switchLayoutModeToViewMode(container[i], true) ;
	    	this.showUIComponentControl(container[i], false) ;
	    	if(layoutMode == -1) layoutMode = 0;
	    }
	  }
	  for(var i = 0; i < portlet.length; i++) {
	  	var viewBlock = portlet[i].getViewBlock() ;
	    if(viewBlock.style.display == 'block') {
	    	this.switchViewModeToLayoutMode(portlet[i], false) ;
	    	this.showUIComponentControl(portlet[i], this.component == 'UIPortlet') ;
	    	if(layoutMode == -1) layoutMode = 1;
	    } else if(viewBlock.style.display == 'none') {
	    	this.switchLayoutModeToViewMode(portlet[i], false) ;
	    	this.showUIComponentControl(portlet[i], false) ;
	    	if(layoutMode == -1) layoutMode = 0;
	    }    
	  }
	
	} 
	
  return layoutMode;
};

 /**Repaired: by Vu Duy Tu 25/04/07**/
UIPortal.prototype.showLayoutModeForPage = function(control) {
	var uiPage = eXo.core.DOMUtil.findFirstDescendantByClass(document.body, "div", "UIPage") ;
	if(uiPage == null) return;
	var viewPage = eXo.core.DOMUtil.findFirstDescendantByClass(uiPage, "div", "VIEW-PAGE") ;
	var uiPageDesktop = document.getElementById("UIPageDesktop") ;
	var uiPortalApplication = document.getElementById("UIPortalApplication");
	if(uiPortalApplication.className != "Vista") {
	 viewPage.style.border = "solid 3px #dadada" ;
	}
	
	viewPage.style.paddingTop = "50px" ;
	viewPage.style.paddingRight = "0px";
	viewPage.style.paddingBottom = "50px";
	viewPage.style.paddingLeft = "0px";
		
	if(control) this.component = control ;
	var container = this.getUIContainers() ;
  for(var i = 0; i < container.length; i++) {
    this.switchViewModeToLayoutMode(container[i], true) ;
    this.showUIComponentControl(container[i], this.component == 'UIContainer') ;

	  var uiContainer = eXo.core.DOMUtil.findFirstDescendantByClass(viewPage, "div", "UIContainer") ;
	  if(uiContainer != null) {
	  	viewPage.style.border = "none" ;
	  	viewPage.style.paddingTop = "5px" ;
	  	viewPage.style.paddingRight = "5px";
	  	viewPage.style.paddingBottom = "5px";
	  	viewPage.style.paddingLeft = "5px";
	  }
  }
	
	var portlet = this.getUIPortletsInUIPage() ;
  for(var i = 0; i < portlet.length; i++) {
    this.switchViewModeToLayoutMode(portlet[i], false) ;
    this.showUIComponentControl(portlet[i], this.component == 'UIPortlet') ;
    
	  var uiPortlet = eXo.core.DOMUtil.findFirstDescendantByClass(viewPage, "div", "UIPortlet") ;
	  if(uiPortlet != null) {
	  	viewPage.style.border = "none" ;
	  	viewPage.style.paddingTop = "5px" ;
	  	viewPage.style.paddingRight = "5px" ;
			viewPage.style.paddingBottom = "5px" ;
			viewPage.style.paddingLeft = "5px" ;
	  }
  }
};

UIPortal.prototype.showViewMode = function() {
  var portal = this.getUIPortal() ;
  this.switchLayoutModeToViewMode(portal, true) ;
  this.showUIComponentControl(portal, false) ;
  
  var uiPageDesktop = document.getElementById("UIPageDesktop") ;
  if(!uiPageDesktop) {
  	var pageBody = this.getUIPageBody() ;
  	this.switchLayoutModeToViewMode(pageBody, true) ;
  	this.showUIComponentControl(pageBody, false) ;
  }

  var container = this.getUIContainers() ;
  for(var i = 0; i < container.length; i++) {
    this.switchLayoutModeToViewMode(container[i], true) ;
    this.showUIComponentControl(container[i], !eXo.env.editType) ;
  }

  var portlet  = this.getUIPortletsInUIPortal() ;
  for(var i = 0; i < portlet.length; i++) {
    this.switchLayoutModeToViewMode(portlet[i], false) ;
    this.showUIComponentControl(portlet[i], true) ;
//    var component = portlet[i].getUIComponentBlock();
//    var mask = eXo.core.DOMUtil.findFirstDescendantByClass(component, "div", "UIPortletMask");
//    if(eXo.env.isEditting && mask) {
//      mask.style.display = "block";
//      mask.style.height = component.offsetHeight + "px";
//      mask.style.width  = component.offsetWidth + "px";
//      mask.style.top = eXo.core.Browser.findPosY(component) + "px";
//      mask.style.left = eXo.core.Browser.findPosX(component) + "px";
//      mask.style.border = "1px solid red";
//    } else if(mask) {
//    	mask.style.display = "none";
//    }
  }
  
  //mask for pagebody
  if(!uiPageDesktop) {
  	var pageBodyBlock = pageBody.getUIComponentBlock();
  	var mask = eXo.core.DOMUtil.findFirstDescendantByClass(pageBodyBlock, "div", "UIPageBodyMask");
  	if(!eXo.env.isBlockEditMode) {
  		mask.style.height = pageBodyBlock.offsetHeight + "px";
  		mask.style.width = pageBodyBlock.offsetWidth + "px";
  		mask.style.top = eXo.core.Browser.findPosY(pageBodyBlock) + "px";
      mask.style.left = eXo.core.Browser.findPosX(pageBodyBlock) + "px";
  		mask.style.display = "block";
  	} else {
  		mask.style.display = "none";
  	}
  }
};

UIPortal.prototype.showLayoutModeForPortal = function(control) {
	if(control) this.component = control;
  var portal = this.getUIPortal() ;
  this.switchViewModeToLayoutMode(portal, true) ;
  this.showUIComponentControl(portal, this.component == 'UIPortal') ;
  
  var pageBody = this.getUIPageBody() ;
  this.switchViewModeToLayoutMode(pageBody, false) ;
  this.showUIComponentControl(pageBody, this.component == 'UIPageBody') ;
  var pageBodyBlock = pageBody.getUIComponentBlock();
  var mask = eXo.core.DOMUtil.findFirstDescendantByClass(pageBodyBlock, "div", "UIPageBodyMask");
  mask.style.display = "none";

  var container = this.getUIContainers() ;
  for(var i = 0; i < container.length; i++) {
    this.switchViewModeToLayoutMode(container[i], true) ;
    this.showUIComponentControl(container[i], this.component == 'UIContainer') ;
  }
    
	var portlet  = this.getUIPortletsInUIPortal() ;
  for(var i = 0; i < portlet.length; i++) {
    this.switchViewModeToLayoutMode(portlet[i], false) ;
    this.showUIComponentControl(portlet[i], this.component == 'UIPortlet') ;
//    var component = portlet[i].getUIComponentBlock();
//    var mask = eXo.core.DOMUtil.findFirstDescendantByClass(component, "div", "UIPortletMask");
//    if(eXo.env.isEditting && mask) {
//    	mask.style.display = "block";
//    	mask.style.height = component.offsetHeight + "px";
//    	mask.style.width  = component.offsetWidth + "px";
//      mask.style.top = eXo.core.Browser.findPosY(component) + "px";
//      mask.style.left = eXo.core.Browser.findPosX(component) + "px";
//      mask.style.border = "1px solid red";
//    } else {
//    	mask.style.display = "none";
//    }
  }  
} ;

UIPortal.prototype.findUIComponentOf = function(element) {
  var parent = element.parentNode ;
  while(parent != null) {
    var className = parent.className ;
    if(className == 'UIPortlet' || className == 'UIContainer' ||  
       className == 'UIPageBody' ||  className == 'UIPortal')  {
      return parent ;
    }
    parent = parent.parentNode ;
  }
  return null ;
};

UIPortal.prototype.showMaskLayer = function() {
	var uiPortalApplication = document.getElementById("UIPortalApplication") ;
	var object = document.createElement("div") ;
	object.className = "PreviewMode" ;
	object.style.display = "none" ;
	uiPortalApplication.appendChild(object) ;

	this.maskLayer = eXo.core.UIMaskLayer.createMask("UIPortalApplication", object, 30, "TOP-RIGHT") ;
	this.maskLayer.title = this.previewTitle ;
	this.maskLayer.style.cursor = "pointer" ;
	this.maskLayer.onclick = function() {
		var layoutModeIcon = eXo.core.DOMUtil.findFirstDescendantByClass(uiPortalApplication, "a", "LayoutModeIcon") ;
		var pageLayoutModeIcon = eXo.core.DOMUtil.findFirstDescendantByClass(uiPortalApplication, "a", "PageLayoutModeIcon") ;
		
		if(layoutModeIcon) {
			eXo.portal.UIPortal.switchMode(layoutModeIcon) ;
		}
		
		if(pageLayoutModeIcon) {
			eXo.portal.UIPortal.switchModeForPage(pageLayoutModeIcon) ;
		}
	}
	this.maskLayer.style.zIndex = parseInt(object.style.zIndex) + 1 ;
	eXo.core.Browser.addOnScrollCallback("3743892", eXo.core.UIMaskLayer.setPosition) ;
} ;

UIPortal.prototype.hideMaskLayer = function() {
	if(this.maskLayer) {
		var uiPortalApplication = document.getElementById("UIPortalApplication") ;
		eXo.core.UIMaskLayer.removeMask(this.maskLayer) ;
		this.maskLayer = null ;
		var maskObject = eXo.core.DOMUtil.findFirstDescendantByClass(uiPortalApplication, "div", "PreviewMode") ;
		uiPortalApplication.removeChild(maskObject) ;
	}
} ;

UIPortal.prototype.changeSkin = function(url) {
 var skin = '';
 if(eXo.webui.UIItemSelector.SelectedItem != undefined) {
   skin = eXo.webui.UIItemSelector.SelectedItem.option;
 }
 if(skin == undefined) skin = '';
  //ajaxAsyncGetRequest(url + '&skin='+skin, false);
  window.location = url + '&skin='+skin;
} ;

UIPortal.prototype.changeLanguage = function(url) {
	var language = '';
	if(eXo.webui.UIItemSelector.SelectedItem != undefined) {
  	language = eXo.webui.UIItemSelector.SelectedItem.option;
	}
	if(language == undefined) language = '';  
  //ajaxAsyncGetRequest(url + '&language='+language, false);
  window.location = url + '&language='+language;
} ;

UIPortal.prototype.changePortal = function(accessPath, portal) {
  window.location = eXo.env.server.context + "/" + accessPath + "/" + portal+"/";
} ;

/** Created: by Lxchiati **/
UIPortal.prototype.popupButton = function(url, action) {
	if(action == undefined) action = '';  
  window.location = url + '&action='+ action ;
} ;

/*
* This method will start the creation of a new javascript application such as a widget
*
* - The application parameter is the full javascript class for the application (for example "eXo.widget.web.info.UIInfoWidget")
* - The application id is an id shared among all the application instance
* - The instance id is unique among all the javascript application deployed in the layout
* - The appLocation is the root path location of the jstmpl file on the server (for example /eXoWidgetWeb/javascript/)
*
*  If the application class name is not null, the associated .js file on the server is loaded using the eXo.require() method
*
*  Once loaded the initApplication() method is called; in other words, the application is lazy instantiated and initialized
*  on the client browser
*/
//UIPortal.prototype.createJSApplication = function(application, applicationId, instanceId, appLocation) {
//	if(application) {
//	  eXo.require(application, appLocation);
//	  var createApplication = application + '.initApplication(\''+applicationId+'\',\''+instanceId+'\');' ;
//	  eval(createApplication);
//	}
//} ;

eXo.portal.UIPortalComponent = UIComponent.prototype.constructor ;
eXo.portal.UIPortal = new UIPortal() ;
eXo.portal.UIComponent = UIPortal.prototype.constructor ;
