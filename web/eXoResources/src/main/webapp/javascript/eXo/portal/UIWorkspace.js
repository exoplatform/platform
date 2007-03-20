function UIWorkspace(id) {	
  this.id = id ;
  this.showControlWorkspace = false ;
}

if(eXo.portal.Workspace == undefined){
  eXo.portal.Workspace = new UIWorkspace("UIWorkspace") ;
}
if(eXo.portal.UIControlWorkspace == undefined) {
  eXo.portal.UIControlWorkspace = new UIWorkspace("UIControlWorkspace") ;
}

eXo.portal.UIControlWorkspace.onResize = function(width, height) {
	this.width = width ;
	this.height = height ;
	var uiWorkspace = document.getElementById(this.id) ;
	var uiWorkspaceContainer = document.getElementById("UIWorkspaceContainer") ;
	this.uiWorkspaceControl = document.getElementById("UIWorkspaceControl") ;
	var uiWorkspacePanel = document.getElementById("UIWorkspacePanel") ;
	
	uiWorkspace.style.width = width + "px" ;
	uiWorkspace.style.height = height + "px" ;
	
	/*	In case uiWorkspaceContainer is setted display to none, uiWorkspaceControl.offsetHeight equal 0 
	 * 	22 is the height of User Workspace Title.
	 * */
	
	if(eXo.portal.UIControlWorkspace.showControlWorkspace == true) {
		uiWorkspaceContainer.style.display = "block" ;
		uiWorkspaceContainer.style.width = (eXo.portal.UIControlWorkspace.defaultWidth - 
																				eXo.portal.UIControlWorkspace.slidebarWidth) + "px" ;
	}
	uiWorkspacePanel.style.height = (height - this.uiWorkspaceControl.offsetHeight - 22) + "px" ;
	
	/*Fix Bug on IE*/
	eXo.portal.UIControlWorkspace.slidebar.style.height = height + "px" ;
	uiWorkspace.style.top = document.documentElement.scrollTop + "px" ;		
} ;

eXo.portal.UIControlWorkspace.onResizeDefault = function() {
	var cws = eXo.portal.UIControlWorkspace ;
	cws.defaultWidth = 250 ;
	
	cws.slidebar = document.getElementById("ControlWorkspaceSlidebar") ;
	cws.slidebarWidth = eXo.portal.UIControlWorkspace.slidebar.offsetWidth ;
	alert("WIDTH Test: " + cws.slidebarWidth);
	if(cws.showControlWorkspace) {
		cws.onResize(cws.defaultWidth, eXo.core.Browser.getBrowserHeight()) ;
	} else {
		cws.onResize(cws.slidebarWidth, eXo.core.Browser.getBrowserHeight()) ;
	}
	
//  var slidebarButton = document.getElementById("ControlWorkspaceSlidebarButton") ;
//	if(eXo.portal.UIControlWorkspace.showControlWorkspace){
//		slidebarButton.className = "SlidebarButtonHide" ;
//		cws.slidebar.style.display = "none" ;
//	} else {
//	  slidebarButton.className = "SlidebarButtonShow" ;
//	}
	
};
   	
eXo.portal.UIControlWorkspace.showWorkspace = function() {
	var uiWorkspace = document.getElementById(this.id) ;
//	var slidebarButton = document.getElementById("ControlWorkspaceSlidebarButton") ;
	var uiWorkspaceContainer = document.getElementById("UIWorkspaceContainer") ;
	var uiWorkspacePanel = document.getElementById("UIWorkspacePanel") ;

	if(eXo.portal.UIControlWorkspace.showControlWorkspace == false) {
		eXo.portal.UIControlWorkspace.slidebar.style.display = "none" ;
		uiWorkspaceContainer.style.display = "block" ;
		uiWorkspace.style.width = eXo.portal.UIControlWorkspace.defaultWidth + "px" ;
		uiWorkspaceContainer.style.width = (eXo.portal.UIControlWorkspace.defaultWidth - 
																				eXo.portal.UIControlWorkspace.slidebar.offsetWidth) + "px" ;
		
		uiWorkspacePanel.style.height = (eXo.portal.UIControlWorkspace.height - 
																		 eXo.portal.UIControlWorkspace.uiWorkspaceControl.offsetHeight - 22) + "px" ;
		/*22 is height of User Workspace Title*/																
		
		eXo.portal.UIControlWorkspace.width = eXo.portal.UIControlWorkspace.defaultWidth ;
		eXo.portal.UIWorkingWorkspace.onResize(null, null) ;
		this.showControlWorkspace = true ;
	} else {
		uiWorkspaceContainer.style.display = "none" ;
		eXo.portal.UIControlWorkspace.slidebar.style.display = "block" ;
		uiWorkspace.style.width = eXo.portal.UIControlWorkspace.slidebar.offsetWidth + "px" ;
//		slidebarButton.className = "SlidebarButtonShow" ;
		
		eXo.portal.UIControlWorkspace.width = eXo.portal.UIControlWorkspace.slidebar.offsetWidth ;
		eXo.portal.UIWorkingWorkspace.onResize(null, null) ;
		this.showControlWorkspace = false ;
	}
	
	/*Resize Dockbar*/
	var uiPageDesktop = document.getElementById("UIPageDesktop") ;
	if(uiPageDesktop != null) eXo.desktop.UIDockbar.resizeDockBar() ;
	
	/*Resize MaskLayer*/
//	var maskLayer = document.getElementById("MaskLayer");
//	if(maskLayer) {
//		var uiWorkingWorkspace = document.getElementById("UIWorkingWorkspace") ;
//		maskLayer.style.width = uiWorkingWorkspace.offsetWidth + "px" ;
//	}
};

/*#############################-Working Workspace-##############################*/
if(eXo.portal.UIWorkingWorkspace == undefined) {
   eXo.portal.UIWorkingWorkspace = new UIWorkspace("UIWorkingWorkspace") ;
}

eXo.portal.UIWorkingWorkspace.onResize = function(width, height) {
	var uiWorkspace = document.getElementById(this.id) ;
	var uiControlWorkspace = document.getElementById("UIControlWorkspace") ;
  var controlWorkspaceWidth = eXo.portal.UIControlWorkspace.width ;
  if(eXo.core.Browser.isIE6()) {
  	this.slidebar = document.getElementById("ControlWorkspaceSlidebar") ;
  	if(this.slidebar) {
  		uiWorkspace.style.width = eXo.core.Browser.getBrowserWidth() - controlWorkspaceWidth - this.slidebar.offsetWidth;
  	}
  }
  
  if(uiControlWorkspace) {
  	uiWorkspace.style.marginLeft = controlWorkspaceWidth + "px" ;
  } else {
  	uiWorkspace.style.marginLeft = "0px" ;
  }
};

eXo.portal.UIWorkingWorkspace.onResizeDefault = function(event) {
  eXo.portal.UIWorkingWorkspace.onResize(null, null) ;
}