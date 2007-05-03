function Skin() {

};

Skin.prototype.addSkin = function(componentId, url) {
  var skin = document.getElementById(componentId);
  if(skin != null) return;
  var link = document.createElement('link') ;
  link.setAttribute('id', componentId) ;
  link.setAttribute('rel', 'stylesheet') ;
  link.setAttribute('type', 'text/css') ;
  link.setAttribute('href', url) ;
  var head = document.getElementsByTagName("head")[0];
  head.appendChild(link);  
};

Skin.prototype.addCoreSkin = function(component, url) {
  if(document.getElementById(component) == null) { 
    var coreSkin = document.getElementById("CoreSkin") ;
    var  head = coreSkin.parentNode ;
    var  link = document.createElement('link') ;
    link.setAttribute('id', component) ;
    link.setAttribute('rel', 'stylesheet') ;
    link.setAttribute('type', 'text/css') ;
    link.setAttribute('href', url) ;
    head.insertBefore(link,  coreSkin) ;
  }
};

Skin.prototype.addApplicationSkin = function(component, url) {
  if(document.getElementById(component) == null) { 
    var coreSkin = document.getElementById("PortalSkin") ;
    var  head = coreSkin.parentNode ;
    var  link = document.createElement('link') ;
    link.setAttribute('id', component) ;
    link.setAttribute('rel', 'stylesheet') ;
    link.setAttribute('type', 'text/css') ;
    link.setAttribute('href', url) ;
    head.insertBefore(link,  coreSkin) ;
  }
};
if(eXo.core.Skin == undefined){
  eXo.core.Skin = new Skin() ;
};