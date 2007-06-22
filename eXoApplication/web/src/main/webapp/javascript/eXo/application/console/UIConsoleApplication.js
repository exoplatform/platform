eXo.require('eXo.core.TemplateEngine');
eXo.require('eXo.application.ApplicationDescriptor');

function UIConsoleApplication() {
	this.appCategory = "web" ;
	this.appName = "console" ;
	this.appIcon = "/eXoResources/skin/portal/webui/component/view/UIPageDesktop/DefaultSkin/icons/80x80/Register.png";
	this.skin = {
	  Default: "/eXoAppWeb/skin/console/DefaultStylesheet.css",
	  Mac:     "/eXoAppWeb/skin/console/MacStylesheet.css",
	  Vista:   "/eXoAppWeb/skin/console/VistaStylesheet.css"
	} ;
};

UIConsoleApplication.prototype.createApplicationInstance = function(appDescriptor) {
	var DOMUtil = eXo.core.DOMUtil ;

	var webConsoleDetector = document.getElementById("WebConsoleApplicationDetector");
	var cssStyle = webConsoleDetector.getAttribute('cssStyle') ;
	
	appDescriptor.window = {
		cssElementStyle : cssStyle
	}
	
 	appDescriptor.window.content = 
    eXo.core.TemplateEngine.merge("eXo/application/console/UIConsoleApplication.jstmpl", appDescriptor, "/eXoAppWeb/javascript/") ;
 	appDescriptor.window.removeApplication = 
 		"eXo.application.console.UIConsoleApplication.destroyInstance('" + appDescriptor.appId + "');";
 	
 	var innerHTML = eXo.core.TemplateEngine.merge("eXo/desktop/UIWindow.jstmpl", appDescriptor);
 	var applicationNode = DOMUtil.createElementNode(innerHTML, "div");
 	applicationNode.applicationDescriptor = appDescriptor;
 	return applicationNode ;
};

UIConsoleApplication.prototype.initApplication = function(applicationId, instanceId) {
//	if(instanceId == null) {
//	  instanceId = eXo.core.DOMUtil.generateId(applicationId);
//	  var application = "eXo.application.console.UIConsoleApplication";
//	  eXo.desktop.UIDesktop.saveJSApplication(application, applicationId, instanceId);
//  }

	var appDescriptor = 
	  new eXo.application.ApplicationDescriptor(instanceId, eXo.application.console.UIConsoleApplication);
	  
	var appInstance = appDescriptor.createApplication();
	eXo.desktop.UIDesktop.addJSApplication(appInstance);
}

UIConsoleApplication.prototype.destroyApplicationInstance = function(appDescriptor) {
	var applicationNode = document.getElementById(appDescriptor.appId);
	
	return applicationNode ;
};

/*##############################################################################################*/
UIConsoleApplication.prototype.destroyInstance = function(instanceId) {
	if(confirm("Are you sure you want to delete this application?")) {
    var appDescriptor = 
      new eXo.application.ApplicationDescriptor(instanceId, eXo.application.console.UIBConsoleApplication);
    
    var removeAppInstance = appDescriptor.destroyApplication();
    eXo.desktop.UIDesktop.removeJSApplication(removeAppInstance);
  }	
};


eXo.application.console  = {
  UIConsoleApplication : new UIConsoleApplication()
}  ;
/********************************/
//create a Mask for window command Console.
//start: 4:39 20/6/2007
//reference class : eXo.core.UIMaskLayer;
//in file: UIMaskLayer.js
/*******************************/
UIConsoleApplication.prototype.showMaskWorkspace = function() {
	if ( !document.getElementById("UIMaskWindowConsole") ) {
	document.body.scroll = "no";
	var wsContent = eXo.core.TemplateEngine.merge("eXo/application/console/UIMaskConsoleSpace.jstmpl", null, "/eXoAppWeb/javascript/") ;
	var context = {
			uiMaskWorkspace : {
			width : "50%",
			content : wsContent
		}
	}
	var uiMaskWorkspace = eXo.core.TemplateEngine.merge("eXo/portal/UIMaskWorkspace.jstmpl", context) ;
	var uiMaskWorkspaceElement = eXo.core.DOMUtil.createElementNode(uiMaskWorkspace, "div") ;
			eXo.core.UIMaskLayer.createMask("UIConsoleDisplayArea", uiMaskWorkspaceElement, 64) ;
	var maskLayer = document.getElementById("MaskLayer") ;
			maskLayer.style.height =  "100%";
			maskLayer.style.width =  "100%";
			maskLayer.style.position = "absolute";
	var infoObj =  document.getElementById("UIMaskWindowConsole");
	//fix height.
	var fixObj =	document.getElementById("UIMaskWorkspaceJSTemplate");
	var maHeight = eXo.core.DOMUtil.findDescendantsByClass(fixObj, "div", "UIDescendantDetector");
	for (k=0; k < maHeight.length; k ++) {
		maHeight[k].style.height = "100%";
		maHeight[k].style.overflow =  "hidden";
	}
	//fix position.
	fixObj.style.position = "absolute";
	fixObj.style.height = "60%";
	fixObj.style.top =  "20%";
	fixObj.style.left =  "25%";	
	fixObj.style.overflow =  "hidden";
	//disable scroll for eXoConsoleResult
	if (document.getElementById("eXoConsoleResult").scroll) {alert("ol")};
	}
}

UIConsoleApplication.prototype.hiddenMaskWorkspace = function() {
	var maskLayer = document.getElementById("MaskLayer") ;
			eXo.core.UIMaskLayer.removeMask(maskLayer);
}
// end 10:31 22/6/2007
/*******************************/
/********************************************************************************************************/
function Editor() {
  this.beforeCursor = null ;
  this.cursor = "*|*" ;
  this.afterCursor = null ;
  this.currentNode = null ;

}

Editor.prototype.init = function(node) {
  this.onFinish() ;
  if(node == null) return ;
  var clickPosition =  window.getSelection().anchorOffset  ;
  
  var text = node.innerHTML ;
  if(clickPosition > 0) {
    this.beforeCursor = text.substring(0, clickPosition) ;
    this.afterCursor = text.substring(clickPosition, text.length) ;
  } else {
    this.beforeCursor = text ;
    this.afterCursor = '' ;
  }
  document.onkeypress =  this.onKeyPress ;
  node.innerHTML = this.beforeCursor + this.cursor + this.afterCursor ;
  node.style.border = "1px solid blue"
  this.currentNode = node ;
}

Editor.prototype.onFinish = function(node) {
  if(this.currentNode == null) return ;
  this.currentNode.innerHTML = this.beforeCursor +  this.afterCursor ;
  this.currentNode.style.border = null ;
  this.currentNode = null ;
  document.onkeypress = null ;
}

Editor.prototype.getTextClickPosition = function(node) {
  var sel = window.getSelection();
  var range = document.createRange();
  return sel.anchorOffset ;
}

Editor.prototype.registerKeyboardHandler = function() {
  var keyboard = eXo.core.Keyboard ;
  keyboard.clearRegisteredHandler() ;
  keyboard.register(keyboard.onBackspace, this.onBackspaceKey) ;
  keyboard.register(keyboard.onEnter, this.onEnterKey) ;
  keyboard.register(keyboard.onDefault, this.onDefaultKey) ;
}

Editor.prototype.onDefaultKey = function(keynum) {
  var keychar = String.fromCharCode(keynum) ;
  var editor = eXo.core.Editor ;
  editor.beforeCursor += c ;
  editor.currentNode.innerHTML = editor.beforeCursor + editor.cursor + editor.afterCursor ;
  return true ;
}

Editor.prototype.onBackspaceKey = function() {
  return true ;
}

Editor.prototype.onEnterKey = function() {
  return true ;
}

function Keyboard() {
  this.onAlphabet          = 0 ;
  this.onDigit             = 1 ;
  this.onPunctuation       = 2 ;
  this.onBackspace         = 3 ;
  this.onEnter             = 4 ;
  this.onDefault           = 5 ;
  
  this.clearRegisteredHandler() ;
  this.clearDefaultRegisteredHandler() ;
  document.onkeypress =  this.onKeyPress ;
}

Keyboard.prototype.register = function(eventCode, handler) {
  this.keyHandler[eventCode] = handler ;
}

Keyboard.prototype.clearRegisteredHandler = function() {
  this.keyHandler =  [null, null, null] ;
}

Keyboard.prototype.registerDefault = function(eventCode, handler) {
  this.defaultKeyHandler[eventCode] = handler ;
}

Keyboard.prototype.clearDefaultRegisteredHandler = function() {
  this.defaultKeyHandler = [null, null, null] ;
}

Keyboard.prototype.onKeyPress = function(event) {
  var keynum ;
  if(window.event) { /* IE */
    keynum = event.keyCode;
  } else if(event.which) { /* Netscape/Firefox/Opera */
    keynum = event.which ;
  }
  var eventCode = this.onDefault ;
  if(keynum >= 65 && keynum <= 90  || keynum >= 97 && keynum <= 122) eventCode = this.onAlphabet ;
  else if(keynum >= 48 && keynum <= 57) eventCode = this.onDigit ;
  
  var keychar = String.fromCharCode(keynum) ;
  
  var handler = this.keyHandler[eventCode] ;
  if(handler != null) return handler(keynum, keychar) ;
  handler = this.defaultKeyHandler[eventCode] ;
  if(handler != null) return handler(keynum, keychar) ;

  return false ;
}

eXo.core.Keyboard = new Keyboard() ;
eXo.core.Editor = new Editor() ;
