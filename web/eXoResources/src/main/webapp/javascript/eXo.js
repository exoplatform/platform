var eXo  = {
  animation : { },
  
  browser : { },
    
  desktop : { },
  
  core : { },
  
  ecm : { },

  env : { client: {}, server: {} },

  portal : { },
    
  util : { },
  
  webui : { },

  widget : { },
  
  application : { 
  	browser : { }
  },
  
  forum : { },
  
  mail : { }  
} ;

eXo.require = function(module, jsLocation) {
  try {
    if(eval(module + ' != null'))  return ;
  } catch(err) {
    //alert(err + " : " + module);
  }
  window.status = "Loading Javascript Module " + module ;
  if(jsLocation == null) jsLocation = '/eXoResources/javascript/' ;
  var path = jsLocation  + module.replace(/\./g, '/')  + '.js' ;
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

