/**
 * @author Nguyen Ba Uoc
 */
eXo.require('eXo.core.html.HTMLEntities');

function HTMLUtil() {
  this.entities = eXo.core.html.HTMLEntities ;
}

HTMLUtil.prototype.entitiesEncode = function(str) {
  for(var n in this.entities) {
    var entityChar = String.fromCharCode(this.entities[n]) ;
    if(entityChar == '&' || entityChar == ';') {
      entityChar = '\\' + entityChar ;
    }
    while(str.indexOf(entityChar) != -1) {
      str = str.replace(entityChar, '&' + n + ';') ;
    }
  }
  return str ;
}

HTMLUtil.prototype.entitiesDecode = function(str) {
  for(var n in this.entities) {
    var entityChar = String.fromCharCode(this.entities[n]) ;
    var htmlEntity = '&' + n + ';' ;
    while(str.indexOf(htmlEntity) != -1) {
      str = str.replace(htmlEntity, entityChar) ;
    }
  }
  return str ;
}

eXo.core.HTMLUtil = new HTMLUtil() ;