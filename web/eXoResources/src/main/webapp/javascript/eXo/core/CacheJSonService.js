function CacheJSonService() {	
	this.cacheData = new eXo.core.HashMap();
};

if(eXo.core.CacheJSonService == undefined){
  eXo.core.CacheJSonService = new CacheJSonService() ;
};

CacheJSonService.prototype.getData = function(url, invalidCache) {
  if(invalidCache){
  	this.cacheData.remove(url);	
  } else {
	  var value = this.cacheData.get(url);
		if(value != null && value != undefined)	return value;	
  }
	
	var responseText = ajaxSyncGet(url);
  
  var response;
  try {
  	if(request.responseText != '') {
  	  eval("response = "+responseText);
  	}
  } catch(err) {
  	alert(err + " : "+responseText);
    return  null;  
  }
  
  if(response == null || response == undefined) return null;
  this.cacheData.put(url, response);
  
  return response;
};