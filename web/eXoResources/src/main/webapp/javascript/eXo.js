var eXo  = {
  animation : { },
  
  browser : { },
    
  desktop : { },
  
  core : { },

  env : { portal: {}, client: {}, server: {} },

  portal : { },
    
  util : { },
  
  webui : { },

  widget : { },
  
  gadget : { },
  
  application : { 
  	browser : { }
  },
  
  ecm : { },  
  
  calendar : { },
  
  contact : { },
  
  forum : { }, 
   
  mail : { },
  
  faq : { }
} ;

/*
* This method will : 
*   1) dynamically load a javascript module from the server (if no root location is set 
*      then use '/eXoResources/javascript/', aka files
*      located in the eXoResources WAR in the application server). 
*      The method used underneath is a XMLHttpRequest
*   2) Evaluate the returned script
*   3) Cache the script on the client
*
*/
eXo.require = function(module, jsLocation) {
  try {
    if(eval(module + ' != null'))  return ;
  } catch(err) {
    //alert(err + " : " + module);
  }
  window.status = "Loading Javascript Module " + module ;
  if(jsLocation == null) jsLocation = '/eXoResources/javascript/' ;
  var path = jsLocation  + module.replace(/\./g, '/')  + '.js' ;
  eXo.loadJS(path);
} ;

eXo.loadJS = function(path) {
  var request = eXo.core.Browser.createHttpRequest() ;
  request.open('GET', path, false) ;
  request.setRequestHeader("Cache-Control", "max-age=86400") ;

  request.send(null) ;
  
  try {
    eval(request.responseText) ;
  } catch(err) {
    alert(err + " : " + request.responseText) ;
  }
} ;

eXo.env.server.createPortalURL = function(targetComponentId, actionName, useAjax, params) {
  var href = eXo.env.server.portalBaseURL + "?portal:componentId=" + targetComponentId + "&portal:action=" + actionName ;

  if(params != null) {
  	var len = params.length ;
    for(var i = 0 ; i < len ; i++) {
      href += "&" +  params[i].name + "=" + params[i].value ;
    }
  }
  if(useAjax) href += "&ajaxRequest=true" ;
  return  href ;
} ;

eXo.portal.logout = function() {
	window.location = eXo.env.server.createPortalURL("UIPortal", "Logout", false) ;
}